package com.david.study;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.david.study.impl.CheckoutCommand;
import com.david.study.impl.CommitTreeCommand;
import com.david.study.impl.InitCommand;
import com.david.study.impl.LogCommand;
import com.david.study.interfaces.Command;

public class Main {
    private static final Map<String, Command> commands = new HashMap<>();

    public static void main(String[] args) {
        // Inyección de dependencia (DIP): se crea el GitRepository y se lo pasa a cada comando.
        GitRepository repository = new GitRepository();

        // Registro de comandos (OCP y LSP)
        commands.put("init", new InitCommand(repository));
        commands.put("commit-tree", new CommitTreeCommand(repository));
        commands.put("log", new LogCommand(repository));
        commands.put("checkout", new CheckoutCommand(repository));

        if (args.length == 0) {
            System.out.println("Please provide a command");
            return;
        }
        String commandKey = args[0];
        Command command = commands.get(commandKey);
        if (command != null) {
            try {
                command.execute(args);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Unknown command: " + commandKey);
        }
    }
}
