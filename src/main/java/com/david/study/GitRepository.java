package com.david.study;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class GitRepository {
    private static final String GIT_DIR = ".gitlike";
    private static final String OBJECTS_DIR = GIT_DIR + "/objects";
    private static final String REFS_DIR = GIT_DIR + "/refs";

    // Inicia el repositorio Git-like (SRP)
    public void init() throws IOException {
        Files.createDirectories(Paths.get(OBJECTS_DIR));
        Files.createDirectories(Paths.get(REFS_DIR));
        Files.write(Paths.get(GIT_DIR, "HEAD"),
                "ref: refs/heads/main\n".getBytes(StandardCharsets.UTF_8));
    }

    // Crea un commit integrando el estado del árbol y actualiza la referencia HEAD (SRP)
    public String commitTree(String message) throws IOException {
        Path cwd = Paths.get(".").toRealPath();
        String treeHash = writeTreeRecursive(cwd);
        String parentHash = getCurrentHead();
        String commitHash = createCommitObject(treeHash, parentHash, message);
        Path refsHeadsMain = Paths.get(REFS_DIR, "heads", "main");
        Files.createDirectories(refsHeadsMain.getParent());
        Files.write(refsHeadsMain, commitHash.getBytes(StandardCharsets.UTF_8));
        return commitHash;
    }

    // Muestra el log de commits recorriendo el historial (SRP)
    public void showCommitLog() throws IOException {
        String commitHash = getCurrentHead();
        while (commitHash != null && !commitHash.isEmpty()) {
            printCommit(commitHash);
            commitHash = getParentCommit(commitHash);
        }
    }

    // Realiza el checkout de un commit específico reconstruyendo el árbol de archivos (SRP)
    public void checkout(String commitHash) throws IOException {
        String commit = readCommitObject(commitHash);
        String treeHash = null;
        for (String line : commit.split("\n")) {
            if (line.startsWith("tree ")) {
                treeHash = line.substring(5).trim();
                break;
            }
        }
        if (treeHash == null) {
            System.out.println("No tree found in commit.");
            return;
        }
        String checkoutDirName = "checkout-" + commitHash;
        Path checkoutDir = Paths.get(checkoutDirName);
        Files.createDirectories(checkoutDir);
        checkoutTree(treeHash, checkoutDir);
        System.out.println("Checked out commit " + commitHash + " into directory " + checkoutDirName);
    }

    // Métodos privados de ayuda

    private void printCommit(String commitHash) throws IOException {
        String commit = readCommitObject(commitHash);
        String[] lines = commit.split("\n");
        StringBuilder message = new StringBuilder();
        String author = "";
        boolean inMessage = false;
        for (String line : lines) {
            if (inMessage) {
                message.append(line).append("\n");
            } else if (line.isEmpty()) {
                inMessage = true;
            } else if (line.startsWith("author ")) {
                author = line.substring(7);
            }
        }
        System.out.println("\u001B[32m" + "commit " + commitHash + "\u001B[0m");
        System.out.println("Author: " + author);
        System.out.println("Date:   " + author.substring(author.lastIndexOf(' ') + 1));
        System.out.println();
        System.out.println("    " + message.toString().trim());
        System.out.println();
    }

    private void checkoutTree(String treeHash, Path dest) throws IOException {
        List<TreeEntry> entries = readTreeEntries(treeHash);
        for (TreeEntry entry : entries) {
            Path entryPath = dest.resolve(entry.name);
            if (entry.mode.equals("40000")) { // Directorio
                Files.createDirectories(entryPath);
                checkoutTree(entry.sha, entryPath);
            } else if (entry.mode.equals("100644")) { // Archivo
                String blobContent = readObject(entry.sha);
                Files.write(entryPath, blobContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private String readCommitObject(String hash) throws IOException {
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

    private String readObject(String hash) throws IOException {
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

    private String getParentCommit(String commitHash) throws IOException {
        String commit = readCommitObject(commitHash);
        for (String line : commit.split("\n")) {
            if (line.startsWith("parent ")) {
                return line.substring(7).trim();
            }
        }
        return "";
    }

    private String getCurrentHead() throws IOException {
        Path headPath = Paths.get(GIT_DIR, "HEAD");
        if (!Files.exists(headPath)) {
            return "";
        }
        String headContent = new String(Files.readAllBytes(headPath), StandardCharsets.UTF_8).trim();
        if (headContent.startsWith("ref: ")) {
            String refPath = headContent.substring(5).trim();
            Path fullRefPath = Paths.get(GIT_DIR, refPath);
            if (Files.exists(fullRefPath)) {
                return new String(Files.readAllBytes(fullRefPath), StandardCharsets.UTF_8).trim();
            }
        }
        return "";
    }

    private String createCommitObject(String treeHash, String parentHash, String message) throws IOException {
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
        String commitHash = sha1Hex(fullContent);
        writeObject(commitHash, fullContent);
        return commitHash;
    }

    private String createBlobObject(String fileName, boolean write) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(fileName));
        String header = "blob " + content.length + "\0";
        byte[] fullContent = concatenateArrays(header.getBytes(StandardCharsets.UTF_8), content);
        String sha1Hash = sha1Hex(fullContent);
        if (write) {
            writeObject(sha1Hash, fullContent);
        }
        return sha1Hash;
    }

    private byte[] concatenateArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private String sha1Hex(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1Bytes = md.digest(input);
            return bytesToHex(sha1Bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String shaToPath(String sha) {
        return OBJECTS_DIR + "/" + sha.substring(0, 2) + "/" + sha.substring(2);
    }

    private String writeTreeRecursive(Path dir) throws IOException {
        ByteArrayOutputStream treeContent = new ByteArrayOutputStream();
        try (Stream<Path> paths = Files.list(dir).sorted()) {
            paths.forEach(path -> writeTreeEntry(treeContent, dir, path));
        }
        byte[] content = treeContent.toByteArray();
        String header = "tree " + content.length + "\0";
        byte[] fullContent = concatenateArrays(header.getBytes(StandardCharsets.UTF_8), content);
        String treeHash = sha1Hex(fullContent);
        writeObject(treeHash, fullContent);
        return treeHash;
    }

    private void writeTreeEntry(ByteArrayOutputStream output, Path baseDir, Path path) {
        try {
            if (path.getFileName().toString().equals(GIT_DIR)) {
                return;
            }
            String relativePath = baseDir.relativize(path).toString();
            String mode;
            String hash;
            if (Files.isDirectory(path)) {
                mode = "40000";
                hash = writeTreeRecursive(path);
            } else {
                mode = "100644";
                hash = createBlobObject(path.toString(), true);
            }
            output.write((mode + " " + relativePath).getBytes(StandardCharsets.UTF_8));
            output.write(0);
            byte[] binaryHash = new byte[20];
            for (int i = 0; i < 40; i += 2) {
                binaryHash[i / 2] = (byte) Integer.parseInt(hash.substring(i, i + 2), 16);
            }
            output.write(binaryHash);
        } catch (IOException e) {
            throw new RuntimeException("Error processing path: " + path, e);
        }
    }

    private List<TreeEntry> readTreeEntries(String hash) throws IOException {
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
                String sha = bytesToHex(shaBytes);
                entries.add(new TreeEntry(mode, name, sha));
            }
        }
        return entries;
    }

    private void writeObject(String hash, byte[] content) throws IOException {
        Path objectPath = Paths.get(shaToPath(hash));
        Files.createDirectories(objectPath.getParent());
        try (OutputStream fileOut = Files.newOutputStream(objectPath);
             DeflaterOutputStream deflater = new DeflaterOutputStream(fileOut)) {
            deflater.write(content);
        }
    }

    // Clase interna para representar entradas del árbol
    private static class TreeEntry {
        String mode;
        String name;
        String sha;

        TreeEntry(String mode, String name, String sha) {
            this.mode = mode;
            this.name = name;
            this.sha = sha;
        }
    }
}
