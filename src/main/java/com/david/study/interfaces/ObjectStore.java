package com.david.study.interfaces;

import com.david.study.model.TreeEntry;

import java.io.IOException;
import java.util.List;

public interface ObjectStore {
    String createBlobObject(String fileName, boolean write) throws IOException;
    String writeTreeObject(byte[] content) throws IOException;
    String createCommitObject(String treeHash, String parentHash, String message) throws IOException;
    String readObject(String hash) throws IOException;
    List<TreeEntry> readTreeEntries(String hash) throws IOException;
    void writeObject(String hash, byte[] content) throws IOException;
}
