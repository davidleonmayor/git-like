package com.david.study.model;

import java.time.LocalDate;

import com.david.study.util.HashGenerator;

public class Repository {
    private String name;
    private String mainBranch;

    public Repository(String name, String mainBranch) {
        this.name = name;
        this.mainBranch = mainBranch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainBranch() {
        return mainBranch;
    }

    public void setMainBranch(String mainBranch) {
        this.mainBranch = mainBranch;
    }

    // Métodos de dominio (SRP): cada método realiza una única acción.
    public void fusionar() {
        System.out.println("Fusionando ramas en el repositorio " + name);
    }

    public void revertir(String branch) {
        System.out.println("Revirtiendo cambios de la rama " + branch + " en el repositorio " + name);
    }

    public Commit commit(String mensaje) {
        String id = HashGenerator.generateRandomHash(7);
        Commit commit = new Commit(LocalDate.now(), id, mensaje);
        System.out.println("Commit creado: " + commit.getId() + " - " + commit.getMessage());
        return commit;
    }
}
