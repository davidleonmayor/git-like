package com.david.study;

import java.util.Scanner;

public class Main {
    private static  Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String nombreRep = "Git-like";
        String ramaPrincipalRep = "main";
        Repositorio repositorio = new Repositorio(nombreRep, ramaPrincipalRep);

        do {
            // basic cases
            String req = scanner.nextLine();
            // System.out.println(req);
            String[] parts = req.split("\\s+");
            //for (String part : parts) {
            //    System.out.println(part);
            //}

            if ("git-like".equals(parts[0])) {
                // commands cases
                switch (parts[1]) {
                    case "status" :
                        System.out.println("Working in branch " + repositorio.getRamaPrincipal());
                        break;

                    case "commit":
                        System.out.println("Add message");
                        break;

                    case "merge":
                        repositorio.fusionar();
                        break;

                    case "reverse":
                        repositorio.revertir();
                        break;

                    default:
                        System.out.println("\033[1;34m" + "Wrong format...");
                }
            }
            else if ("exit".equals(parts[0]))  {
                System.out.println("\033[1;34m" + "End Program...");
                break;
            }
            else {
                System.out.println("\033[1;34m" + "Invalid command");
            }
        }
        while (true);
    }
}