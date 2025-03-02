package com.david.study.impl;

import com.david.study.interfaces.HashGenerator;
import com.david.study.interfaces.ObjectStore;
import com.david.study.interfaces.RepositoryManager;
import com.david.study.model.TreeEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class GitRepositoryManager implements RepositoryManager {
    private static final String GIT_DIR = ".gitlike";
    private static final String OBJECTS_DIR = GIT_DIR + "/objects";
    private static final String REFS_DIR = GIT_DIR + "/refs";
    
    private final ObjectStore objectStore;
    private final HashGenerator hashGenerator;

    public GitRepositoryManager(ObjectStore objectStore, HashGenerator hashGenerator) {
        this.objectStore = objectStore;
        this.hashGenerator = hashGenerator;
    }

    @Override
    public void init() throws IOException {
        Files.createDirectories(Paths.get(OBJECTS_DIR));
        Files.createDirectories(Paths.get(REFS_DIR));
        Files.write(Paths.get(GIT_DIR, "HEAD"),
                "ref: refs/heads/main\n".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String commitTree(String message) throws IOException {
        Path cwd = Paths.get(".").toRealPath();
        String treeHash = writeTreeRecursive(cwd);
        String parentHash = getCurrentHead();
        String commitHash = objectStore.createCommitObject(treeHash, parentHash, message);
        Path refsHeadsMain = Paths.get(REFS_DIR, "heads", "main");
        Files.createDirectories(refsHeadsMain.getParent());
        Files.write(refsHeadsMain, commitHash.getBytes(StandardCharsets.UTF_8));
        return commitHash;
    }

    @Override
    public void showCommitLog() throws IOException {
        String commitHash = getCurrentHead();
        while (commitHash != null && !commitHash.isEmpty()) {
            printCommit(commitHash);
            commitHash = getParentCommit(commitHash);
        }
    }

    @Override
    public void checkout(String commitHash) throws IOException {
        String commit = objectStore.readObject(commitHash);
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

    @Override
    public String writeTreeRecursive(Path dir) throws IOException {
        ByteArrayOutputStream treeContent = new ByteArrayOutputStream();
        try (Stream<Path> paths = Files.list(dir).sorted()) {
            paths.forEach(path -> writeTreeEntry(treeContent, dir, path));
        }
        return objectStore.writeTreeObject(treeContent.toByteArray());
    }

    @Override
    public String getCurrentHead() throws IOException {
        Path headPath = Paths.get(GIT_DIR, "HEAD");
        if (!Files.exists(headPath)) {
            return "";
        }
        String headContent = new String(Files.readAllBytes(headPath), StandardCharsets.UTF_8).trim();
        if (headContent.startsWith("ref: ")) {
            String refPath = headContent.substring(5).trim();
            Path fullRefPath = Paths.get(refPath);
            if (!Files.exists(fullRefPath)) {
                fullRefPath = Paths.get(GIT_DIR, refPath);
            }
            if (Files.exists(fullRefPath)) {
                return new String(Files.readAllBytes(fullRefPath), StandardCharsets.UTF_8).trim();
            }
        }
        return "";
    }

    private void printCommit(String commitHash) throws IOException {
        String commit = objectStore.readObject(commitHash);
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

    private String getParentCommit(String commitHash) throws IOException {
        String commit = objectStore.readObject(commitHash);
        for (String line : commit.split("\n")) {
            if (line.startsWith("parent ")) {
                return line.substring(7).trim();
            }
        }
        return "";
    }

    private void checkoutTree(String treeHash, Path dest) throws IOException {
        List<TreeEntry> entries = objectStore.readTreeEntries(treeHash);
        for (TreeEntry entry : entries) {
            Path entryPath = dest.resolve(entry.getName());
            if (entry.getMode().equals("40000")) { // Directorio
                Files.createDirectories(entryPath);
                checkoutTree(entry.getSha(), entryPath);
            } else if (entry.getMode().equals("100644")) { // Archivo
                String blobContent = objectStore.readObject(entry.getSha());
                Files.write(entryPath, blobContent.getBytes(StandardCharsets.UTF_8));
            }
        }
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
                hash = objectStore.createBlobObject(path.toString(), true);
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
}
