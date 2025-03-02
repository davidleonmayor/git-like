package com.david.study.interfaces;

import java.io.IOException;
import java.nio.file.Path;

public interface RepositoryManager {
    void init() throws IOException;
    String commitTree(String message) throws IOException;
    void showCommitLog() throws IOException;
    void checkout(String commitHash) throws IOException;
    String writeTreeRecursive(Path dir) throws IOException;
    String getCurrentHead() throws IOException;
}
