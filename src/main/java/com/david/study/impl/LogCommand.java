// File: LogCommand.java
package com.david.study.impl;

import java.io.IOException;

import com.david.study.GitRepository;
import com.david.study.interfaces.Command;

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
