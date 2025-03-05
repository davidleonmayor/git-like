package com.david.study.impl;

import java.io.IOException;

import com.david.study.GitRepository;
import com.david.study.interfaces.Command;

public class InitCommand implements Command {
    private final GitRepository repository;

    public InitCommand(GitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(String[] args) throws IOException {
        repository.init();
        System.out.println("Initialized git directory");
    }
}
