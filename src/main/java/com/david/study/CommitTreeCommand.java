package com.david.study;

import java.io.IOException;

public class CommitTreeCommand implements Command {
    private final GitRepository repository;

    public CommitTreeCommand(GitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (args.length < 3 || !args[1].equals("-m")) {
            System.out.println("Usage: commit-tree -m <message>");
            return;
        }
        String message = args[2];
        String commitHash = repository.commitTree(message);
        System.out.println("Created commit: " + commitHash);
    }
}
