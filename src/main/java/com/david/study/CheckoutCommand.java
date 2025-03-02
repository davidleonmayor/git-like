package com.david.study;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckoutCommand implements Command {
    private final GitRepository repository;

    public CheckoutCommand(GitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: checkout <commit-hash>");
            return;
        }
        String commitHash = args[1];
        repository.checkout(commitHash);
    }
}
