package com.david.study;

import java.util.Random;

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
        System.out.println("Mixing branches");
    }

    public void revertir(String branch) {
        System.out.println("Reversing branch changes " + branch);
    }

    public void commit(String msg) {
        System.out.println("\033[1;32m " + generateRandomHash(7) + msg);
    }

    public static String generateRandomHash(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Caracteres hexadecimales
        String hexChars = "0123456789abcdef";

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(hexChars.length());
            sb.append(hexChars.charAt(randomIndex));
        }

        return sb.toString();
    }
}
