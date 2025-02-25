package com.david.study;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String nombreRep = "Git-like";
        String ramaPrincipalRep = "main";
        Repositorio repositorio = new Repositorio(nombreRep, ramaPrincipalRep);

        do {
            System.out.println("\033[1;34m" + ">>>");
            // Read command
            String req = scanner.nextLine();
            String[] parts = req.split("\\s+", 4); // Limitamos el split para mantener el mensaje del commit junto

            if ("git-like".equals(parts[0])) {
                // Command cases
                switch (parts[1]) {
                    case "status":
                        System.out.println("\033[0m"+ "Working in branch " + repositorio.getRamaPrincipal());
                    break;

                    case "commit":
                        if (parts.length >= 4 && "-m".equals(parts[2])) {
                            repositorio.commit(parts[3]);  // Agregar commit
                        } else {
                            System.out.println("\033[1;34m" + "Wrong format. Use: git-like commit -m \"message\"");
                        }
                    break;

                    case "merge":
                        if (parts.length >= 4) {
                            repositorio.fusionar();
                        } else {
                            System.out.println("\033[1;34m" + "Wrong format. Use: git-like commit -m \"message\"");
                        }
                    break;

                    case "revert":
                            repositorio.revertir(repositorio.getRamaPrincipal());  // Agregar commit
                    break;

                    case "help":
                        System.out.println(
                                "\033[1;34m" +
                                "\n  .:Commands:. " +
                                "\n git-like status" +
                                "\n git-like commit -m <message>" +
                                "\n git-like merge <branch1> <branch2>" +
                                "\n git-like revert "
                        );
                    break;

                    default:
                        System.out.println("\033[1;31m" + "Check the command, has a wrong format...");
                }
            } else if ("exit".equals(parts[0])) {
                System.out.println("\033[1;34m" + "End Program...");
                break;
            } else {
                System.out.println("\033[1;31m" + "Invalid command");
            }
        }
        while (true);
    }
}
