package io.github.kirya343.terminal.runtime;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.kirya343.terminal.console.DiagnosticTerminal;
import lombok.Getter;

public class TerminalConfig {

    private List<Consumer<DiagnosticTerminal>> startupTasks = new ArrayList<>();

    @Getter 
    private Path logFile;

    public void addStartupTask(Consumer<DiagnosticTerminal> task) {
        startupTasks.add(task);
    }

    public List<Consumer<DiagnosticTerminal>> getStartupTasks() {
        return List.copyOf(startupTasks);
    }

    public static TerminalConfig defaultConfig() {
        TerminalConfig config = new TerminalConfig();

        config.addStartupTask(t ->
            t.getCommandExecutor().execute("logs -f 100")
        );

        return config;
    }

    public TerminalConfig logFile(Path path) {
        this.logFile = path;
        return this;
    }
}
