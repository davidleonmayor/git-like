package com.david.study;

public class Repositorio {
    private String nombre;
    private String ramaPrincipal = "main";

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

    // fun
    public void fusionar() {
        System.out.println("Fusionar");
    }

    public void revertir() {
        System.out.println("Revertir");
    }
}
