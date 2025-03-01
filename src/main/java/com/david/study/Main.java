package com.david.study;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Main {
    private static final String GIT_DIR = ".gitlike";
    private static final String OBJECTS_DIR = GIT_DIR + "/objects";
    private static final String REFS_DIR = GIT_DIR + "/refs";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a command");
            return;
        }

        try {
            executeCommand(args);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeCommand(String[] args) throws IOException {
        String command = args[0];
        switch (command) {
            case "init" -> initRepository();
//            case "cat-file" -> catFile(args);
//            case "hash-object" -> hashObject(args);
//            case "ls-tree" -> lsTree(args);
//            case "write-tree" -> writeTree();
            case "commit-tree" -> commitTree(args);
            case "log" -> showCommitLog(args);
            case "checkout" -> checkoutCommit(args);
            // TODO: implements if we got time
//            case "clone" -> cloneRepository(args);
            default -> System.out.println("Unknown command: " + command);
        }
    }


    // ---------------------- Init Command ----------------------
    private static void initRepository() throws IOException {
        Files.createDirectories(Paths.get(OBJECTS_DIR));
        Files.createDirectories(Paths.get(REFS_DIR));
        Files.write(Paths.get(GIT_DIR, "HEAD"), "ref: refs/heads/main\n".getBytes(StandardCharsets.UTF_8));
        System.out.println("Initialized git directory");
    }

    // ---------------------- Log Command ----------------------
    private static void showCommitLog(String[] args) throws IOException {
        // Get current HEAD commit
        String commitHash = getCurrentHead();

        // Print commit history
        while (commitHash != null && !commitHash.isEmpty()) {
            printCommit(commitHash);
            commitHash = getParentCommit(commitHash);
        }
    }
    private static void printCommit(String commitHash) throws IOException {
        String commit = readCommitObject(commitHash);

        // Parse commit fields
        String[] lines = commit.split("\n");
        String tree = "";
        String parent = "";
        String author = "";
        String committer = "";
        StringBuilder message = new StringBuilder();

        int i = 0;
        boolean inMessage = false;

        for (String line : lines) {
            if (inMessage) {
                message.append(line).append("\n");
            } else if (line.isEmpty()) {
                inMessage = true;
            } else if (line.startsWith("tree ")) {
                tree = line.substring(5);
            } else if (line.startsWith("parent ")) {
                parent = line.substring(7);
            } else if (line.startsWith("author ")) {
                author = line.substring(7);
            } else if (line.startsWith("committer ")) {
                committer = line.substring(10);
            }
        }

        // Print commit info
        System.out.println("commit " + commitHash);
        System.out.println("Author: " + author);
        System.out.println("Date:   " + author.substring(author.lastIndexOf(' ') + 1));
        System.out.println();
        System.out.println("    " + message.toString().trim());
        System.out.println();
    }
    private static String readCommitObject(String hash) throws IOException {
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

            // Skip the header (e.g., "commit 231\0")
            int nullIndex = content.indexOf('\0');
            if (nullIndex != -1) {
                content = content.substring(nullIndex + 1);
            }

            return content;
        }
    }
    private static String getParentCommit(String commitHash) throws IOException {
        String commit = readCommitObject(commitHash);
        String[] lines = commit.split("\n");

        for (String line : lines) {
            if (line.startsWith("parent ")) {
                return line.substring(7);
            }
        }

        return "";
    }



    //
    private static String sha1Hex(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1Bytes = md.digest(input);
            return bytesToHex(sha1Bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static void writeObject(String hash, byte[] content) throws IOException {
        Path objectPath = Paths.get(shaToPath(hash));
        Files.createDirectories(objectPath.getParent());
        try (OutputStream fileOut = Files.newOutputStream(objectPath);
             DeflaterOutputStream deflater = new DeflaterOutputStream(fileOut)) {
            deflater.write(content);
        }
    }
    private static String shaToPath(String sha) {
        return OBJECTS_DIR + "/" + sha.substring(0, 2) + "/" + sha.substring(2);
    }

    //
    private static String createBlobObject(String fileName, boolean write) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(fileName));
        String header = "blob " + content.length + "\0";
        byte[] fullContent = concatenateArrays(header.getBytes(StandardCharsets.UTF_8), content);
        String sha1Hash = sha1Hex(fullContent);
        if (write) {
            writeObject(sha1Hash, fullContent);
        }
        return sha1Hash;
    }
    private static byte[] concatenateArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static String readObject(String hash) throws IOException {
        Path objectPath = Paths.get(shaToPath(hash));
        try (InputStream fileIn = Files.newInputStream(objectPath);
             InflaterInputStream inflater = new InflaterInputStream(fileIn);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inflater))) {
            String header = reader.readLine();
            int nullIndex = header.indexOf('\0');
            if (nullIndex != -1) {
                header = header.substring(nullIndex + 1);
            }
            return header + reader.lines().collect(Collectors.joining("\n"));
        }
    }


    //  Tree operations
    private static String writeTreeRecursive(Path dir) throws IOException {
        ByteArrayOutputStream treeContent = new ByteArrayOutputStream();
        Files.list(dir)
                .sorted()
                .forEach(path -> writeTreeEntry(treeContent, dir, path));
        byte[] content = treeContent.toByteArray();
        String header = "tree " + content.length + "\0";
        byte[] fullContent = concatenateArrays(header.getBytes(StandardCharsets.UTF_8), content);
        String treeHash = sha1Hex(fullContent);
        writeObject(treeHash, fullContent);
        return treeHash;
    }
    private static void writeTreeEntry(ByteArrayOutputStream output, Path baseDir, Path path) {
        try {
            String relativePath = baseDir.relativize(path).toString();
            String mode;
            String hash;

            if (Files.isDirectory(path)) {
                // Skip .git directory
                if (path.getFileName().toString().equals(".git")) {
                    return;
                }
                // Directory mode: 40000
                mode = "40000";
                // Create tree for directory
                hash = writeTreeRecursive(path);
            } else {
                // Regular file mode: 100644
                mode = "100644";
                // Create blob for file
                hash = createBlobObject(path.toString(), true);
            }

            // Write entry to tree content
            output.write((mode + " " + relativePath).getBytes(StandardCharsets.UTF_8));
            output.write(0); // NULL byte separator

            // Convert hexadecimal hash to binary
            byte[] binaryHash = new byte[20];
            for (int i = 0; i < 40; i += 2) {
                binaryHash[i / 2] = (byte) Integer.parseInt(hash.substring(i, i + 2), 16);
            }
            output.write(binaryHash);
        } catch (IOException e) {
            throw new RuntimeException("Error processing path: " + path, e);
        }
    }
    private static List<String> readTreeObject(String hash) throws IOException {
        List<String> entries = new ArrayList<>();
        Path objectPath = Paths.get(shaToPath(hash));
        try (InputStream fileIn = Files.newInputStream(objectPath);
             InflaterInputStream inflater = new InflaterInputStream(fileIn);
             DataInputStream dataIn = new DataInputStream(inflater)) {
// Skip the header
            while (dataIn.readByte() != 0) {}
            while (dataIn.available() > 0) {
                StringBuilder entry = new StringBuilder();
// Read mode
                String mode = "";
                byte b;
                while ((b = dataIn.readByte()) != ' ') {
                    mode += (char) b;
                }
                entry.append(mode).append(' ');
// Read name
                String name = "";
                while ((b = dataIn.readByte()) != 0) {
                    name += (char) b;
                }
                entry.append(name);
// Read SHA-1 (20 bytes)
                byte[] sha = new byte[20];
                dataIn.readFully(sha);
                entries.add(entry.toString());
            }
        }
        return entries;
    }


    // Commit creation
    private static void commitTree(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: commit-tree <tree-hash> -m <message>");
            return;
        }

        String treeHash = args[1];
        String message = args[3]; // Assuming format is: commit-tree <hash> -m <message>

        // Get parent hash from HEAD
        String parentHash = getCurrentHead();

        // Create commit object
        String commitHash = createCommitObject(treeHash, parentHash, message);

        // Update refs/heads/main with new commit hash
        Path refsHeadsMain = Paths.get(REFS_DIR, "heads", "main");
        Files.createDirectories(refsHeadsMain.getParent());
        Files.write(refsHeadsMain, commitHash.getBytes(StandardCharsets.UTF_8));

        System.out.println("Created commit: " + commitHash);
    }
    private static String getCurrentHead() throws IOException {
        String headContent = new String(Files.readAllBytes(Paths.get(GIT_DIR, "HEAD")), StandardCharsets.UTF_8).trim();
        if (headContent.startsWith("ref: ")) {
            String refPath = headContent.substring(5).trim();
            // Construir la ruta correcta dentro de .git
            Path fullRefPath = Paths.get(GIT_DIR, refPath);
            if (Files.exists(fullRefPath)) {
                return new String(Files.readAllBytes(fullRefPath), StandardCharsets.UTF_8).trim();
            }
        }
        return "";
    }
    private static String createCommitObject(String treeHash, String parentHash, String message) throws IOException {
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

    // ---------------------- Checkout Command ----------------------
    // Helper class for representing tree entries
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

    private static void checkoutCommit(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: checkout <commit-hash>");
            return;
        }
        String commitHash = args[1];
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
        // Crear un directorio para el checkout, por ejemplo: checkout-<commitHash>
        String checkoutDirName = "checkout-" + commitHash;
        Path checkoutDir = Paths.get(checkoutDirName);
        Files.createDirectories(checkoutDir);
        checkoutTree(treeHash, checkoutDir);
        System.out.println("Checked out commit " + commitHash + " into directory " + checkoutDirName);
    }
    // Recorre el árbol y escribe archivos/directorios en 'dest'
    private static void checkoutTree(String treeHash, Path dest) throws IOException {
        List<TreeEntry> entries = readTreeEntries(treeHash);
        for (TreeEntry entry : entries) {
            Path entryPath = dest.resolve(entry.name);
            if (entry.mode.equals("40000")) { // Directorio
                Files.createDirectories(entryPath);
                checkoutTree(entry.sha, entryPath);
            } else if (entry.mode.equals("100644")) { // Archivo
                // Leer blob y escribirlo en el archivo
                String blobContent = readObject(entry.sha);
                Files.write(entryPath, blobContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
    private static List<TreeEntry> readTreeEntries(String hash) throws IOException {
        List<TreeEntry> entries = new ArrayList<>();
        Path objectPath = Paths.get(shaToPath(hash));
        try (InputStream fileIn = Files.newInputStream(objectPath);
             InflaterInputStream inflater = new InflaterInputStream(fileIn);
             DataInputStream dataIn = new DataInputStream(inflater)) {
            // Saltar el header
            while (dataIn.readByte() != 0) {}
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
                TreeEntry entry = new TreeEntry(mode, name, sha);
                entries.add(entry);
            }
        }
        return entries;
    }

}
