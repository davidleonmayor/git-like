package com.david.study;

import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String nombreRep = "Git-like";
        String ramaPrincipalRep = "Git-like";
        Repositorio repositorio = new Repositorio(nombreRep, ramaPrincipalRep);
        System.out.println(repositorio.getNombre());

    }
}