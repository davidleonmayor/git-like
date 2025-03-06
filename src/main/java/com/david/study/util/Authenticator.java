package com.david.study.util;

import java.io.*;
import java.util.Scanner;

public class Authenticator {
    private static final String SESSION_FILE = "session.txt";
    private static boolean isAuthenticated = false;

    public static void authenticate(AuthManager authManager) {
        // Verificar si ya hay una sesión activa
        if (isSessionActive()) {
            isAuthenticated = true;
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Registrar usuario");
        System.out.println("2. Iniciar sesión");
        System.out.print("Seleccione una opción: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // Consumir salto de línea

        if (option == 1) {
            System.out.print("Ingrese un nombre de usuario: ");
            String username = scanner.nextLine();
            System.out.print("Ingrese una contraseña: ");
            String password = scanner.nextLine();
            authManager.registerUser(username, password);
            System.out.println("Usuario registrado. Reinicie el programa para iniciar sesión.");
            System.exit(0);
        } else if (option == 2) {
            System.out.print("Usuario: ");
            String username = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();
            if (!authManager.authenticate(username, password)) {
                System.out.println("Autenticación fallida. Cerrando programa.");
                System.exit(0);
            }
            isAuthenticated = true;
            saveSession(username); // Guardar la sesión
        } else {
            System.out.println("Opción inválida. Cerrando programa.");
            System.exit(0);
        }
    }

    private static void saveSession(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            writer.write(username);
        } catch (IOException e) {
            System.out.println("Error al guardar la sesión: " + e.getMessage());
        }
    }

    private static boolean isSessionActive() {
        File file = new File(SESSION_FILE);
        return file.exists();
    }

    public static void logout() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            file.delete();
        }
        isAuthenticated = false;
        System.out.println("Sesión cerrada.");
    }
}
