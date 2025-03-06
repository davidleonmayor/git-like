package com.david.study.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthManager {
    private static final String FILE_NAME = "users.txt";
    private final Map<String, String> users = new HashMap<>();

    public AuthManager() {
        loadUsers();
    }

    public void registerUser(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("El usuario ya existe.");
        } else {
            users.put(username, password);
            saveUsers(); 
            System.out.println("Usuario registrado con éxito.");
        }
    }

    public boolean authenticate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    private void loadUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; 

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar usuarios: " + e.getMessage());
        }
    }
}
