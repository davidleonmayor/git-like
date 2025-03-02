package com.david.study;

import java.io.IOException;

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
