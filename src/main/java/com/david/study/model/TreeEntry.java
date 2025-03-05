package com.david.study.model;

public class TreeEntry {
    private String mode;
    private String name;
    private String sha;

    public TreeEntry(String mode, String name, String sha) {
        this.mode = mode;
        this.name = name;
        this.sha = sha;
    }

    public String getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    public String getSha() {
        return sha;
    }
}
