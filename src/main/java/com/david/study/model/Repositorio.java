package com.david.study.model;

import java.time.LocalDate;

import com.david.study.util.HashGenerator;

public class Repositorio {
    private String nombre;
    private String ramaPrincipal;

    public Repositorio(String nombre, String ramaPrincipal) {
        this.nombre = nombre;
        this.ramaPrincipal = ramaPrincipal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRamaPrincipal() {
        return ramaPrincipal;
    }

    public void setRamaPrincipal(String ramaPrincipal) {
        this.ramaPrincipal = ramaPrincipal;
    }

    // Métodos de dominio (SRP): cada método realiza una única acción.
    public void fusionar() {
        System.out.println("Fusionando ramas en el repositorio " + nombre);
    }

    public void revertir(String branch) {
        System.out.println("Revirtiendo cambios de la rama " + branch + " en el repositorio " + nombre);
    }

    public Commit commit(String mensaje) {
        String id = HashGenerator.generateRandomHash(7);
        Commit commit = new Commit(LocalDate.now(), id, mensaje);
        System.out.println("Commit creado: " + commit.getId() + " - " + commit.getMensaje());
        return commit;
    }
}
