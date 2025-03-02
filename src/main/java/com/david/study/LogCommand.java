// File: LogCommand.java
package com.david.study;

import java.io.IOException;

public class LogCommand implements Command {
    private final GitRepository repository;

    public LogCommand(GitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(String[] args) throws IOException {
        repository.showCommitLog();
    }
}
