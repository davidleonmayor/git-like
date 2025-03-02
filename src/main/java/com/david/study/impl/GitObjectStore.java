package com.david.study.impl;

import com.david.study.interfaces.HashGenerator;
import com.david.study.interfaces.ObjectStore;
import com.david.study.model.TreeEntry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class GitObjectStore implements ObjectStore {
    private static final String OBJECTS_DIR = ".gitlike/objects";
    private final HashGenerator hashGenerator;

    public GitObjectStore(HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
    }

    @Override
    public String createBlobObject(String fileName, boolean write) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(fileName));
        String header = "blob " + content.length + "\0";
        byte[] fullContent = concatenateArrays(header.getBytes(StandardCharsets.UTF_8), content);
        String sha1Hash = hashGenerator.generateHash(fullContent);
        if (write) {
            writeObject(sha1Hash, fullContent);
        }
        return sha1Hash;
    }

    @Override
    public String writeTreeObject(byte[] content) throws IOException {
        String header = "tree " + content.length + "\0";
        byte[] fullContent = concatenateArrays(header.getBytes(StandardCharsets.UTF_8), content);
        String treeHash = hashGenerator.generateHash(fullContent);
        writeObject(treeHash, fullContent);
        return treeHash;
    }

    @Override
    public String createCommitObject(String treeHash, String parentHash, String message) throws IOException {
        String timestamp = Instant.now().toString();
        String author = "David leon <jd.leon@unimayor.edu.co>";
        String committer = author;
        StringBuilder commitContent = new StringBuilder()
                .append("tree ").append(treeHash).append('\n')
                .append("parent ").append(parentHash).append('\n')
                .append("author ").append(author).append(' ').append(timestamp).append('\n')
                .append("committer ").append(committer).append(' ').append(timestamp).append('\n')
                .append('\n').append(message).append('\n');
        byte[] content = commitContent.toString().getBytes(StandardCharsets.UTF_8);
        String header = "commit " + content.length + "\0";
        byte[] fullContent = concatenateArrays(header.getBytes(StandardCharsets.UTF_8), content);
        String commitHash = hashGenerator.generateHash(fullContent);
        writeObject(commitHash, fullContent);
        return commitHash;
    }

    @Override
    public String readObject(String hash) throws IOException {
        Path objectPath = Paths.get(shaToPath(hash));
        try (InputStream fileIn = Files.newInputStream(objectPath);
             InflaterInputStream inflater = new InflaterInputStream(fileIn);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inflater.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
            String content = new String(output.toByteArray(), StandardCharsets.UTF_8);
            int nullIndex = content.indexOf('\0');
            if (nullIndex != -1) {
                content = content.substring(nullIndex + 1);
            }
            return content;
        }
    }

    @Override
    public List<TreeEntry> readTreeEntries(String hash) throws IOException {
        List<TreeEntry> entries = new ArrayList<>();
        Path objectPath = Paths.get(shaToPath(hash));
        try (InputStream fileIn = Files.newInputStream(objectPath);
             InflaterInputStream inflater = new InflaterInputStream(fileIn);
             DataInputStream dataIn = new DataInputStream(inflater)) {
            while (dataIn.readByte() != 0) {
                // Saltar header
            }
            while (dataIn.available() > 0) {
                StringBuilder modeBuilder = new StringBuilder();
                byte b;
                while ((b = dataIn.readByte()) != ' ') {
                    modeBuilder.append((char) b);
                }
                String mode = modeBuilder.toString();
                StringBuilder nameBuilder = new StringBuilder();
                while ((b = dataIn.readByte()) != 0) {
                    nameBuilder.append((char) b);
                }
                String name = nameBuilder.toString();
                byte[] shaBytes = new byte[20];
                dataIn.readFully(shaBytes);
                String sha = hashGenerator.bytesToHex(shaBytes);
                entries.add(new TreeEntry(mode, name, sha));
            }
        }
        return entries;
    }

    @Override
    public void writeObject(String hash, byte[] content) throws IOException {
        Path objectPath = Paths.get(shaToPath(hash));
        Files.createDirectories(objectPath.getParent());
        try (OutputStream fileOut = Files.newOutputStream(objectPath);
             DeflaterOutputStream deflater = new DeflaterOutputStream(fileOut)) {
            deflater.write(content);
        }
    }

    private String shaToPath(String sha) {
        return OBJECTS_DIR + "/" + sha.substring(0, 2) + "/" + sha.substring(2);
    }

    private byte[] concatenateArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
