package io.github.kirya343.terminal.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.kirya343.terminal.console.DiagnosticTerminal;

public class TerminalConfig {

    private List<Consumer<DiagnosticTerminal>> startupTasks = new ArrayList<>();

    public void addStartupTask(Consumer<DiagnosticTerminal> task) {
        startupTasks.add(task);
    }

    public List<Consumer<DiagnosticTerminal>> getStartupTasks() {
        return List.copyOf(startupTasks);
    }

    public static TerminalConfig defaultConfig() {
        TerminalConfig config = new TerminalConfig();

        config.addStartupTask(t ->
            t.getCommandExecutor().executeCommand("logs -f 100")
        );

        return config;
    }
}
