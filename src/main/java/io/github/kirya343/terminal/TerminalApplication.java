package io.github.kirya343.terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import io.github.kirya343.terminal.command.DiagnosticCommandExecutor;
import io.github.kirya343.terminal.component.DiagnosticComponentScanner;
import io.github.kirya343.terminal.console.DiagnosticContentLayout;
import io.github.kirya343.terminal.console.DiagnosticScreen;
import io.github.kirya343.terminal.console.DiagnosticScreenRenderer;
import io.github.kirya343.terminal.console.DiagnosticTerminal;
import io.github.kirya343.terminal.logging.DiagnosticLogging;
import io.github.kirya343.terminal.logging.DiagnosticLoggingConfigurer;
import io.github.kirya343.terminal.logging.LogBuffer;
import io.github.kirya343.terminal.logging.LogCollector;
import io.github.kirya343.terminal.registry.DiagnosticManager;
import io.github.kirya343.terminal.runtime.TerminalConfig;
import io.github.kirya343.terminal.runtime.TerminalInstance;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public final class TerminalApplication {

    public static TerminalInstance create(
        String applicationName, 
        TerminalConfig config
    ) throws Exception {

        if (config.getLogFile() != null) {
            try {
                Files.writeString(config.getLogFile(), "");
                log.info("Log file cleared: {}", config.getLogFile().toAbsolutePath());
            } catch (NoSuchFileException e) {
                log.warn("Log not found: ", e);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to clear log file", e);
            }
        }

        LogBuffer logBuffer = new LogBuffer(1000);
        LogCollector logCollector = new LogCollector(logBuffer);

        DiagnosticLoggingConfigurer loggingConfigurer =
            new DiagnosticLoggingConfigurer(logCollector);

        loggingConfigurer.configure();

        DiagnosticLogging.initialize(loggingConfigurer);

        Terminal terminal = TerminalBuilder.builder()
            .system(true)
            .build();

        DiagnosticScreenRenderer renderer =
            new DiagnosticScreenRenderer(applicationName, terminal);

        DiagnosticScreen screen = new DiagnosticScreen(
            terminal,
            renderer,
            new DiagnosticContentLayout()
        );

        DiagnosticManager manager = createManager();

        DiagnosticCommandExecutor commandExecutor =
            new DiagnosticCommandExecutor(manager, screen);

        DiagnosticTerminal diagnosticTerminal = new DiagnosticTerminal(
            terminal,
            screen,
            commandExecutor
        );

        config.getStartupTasks().forEach(task -> {
            try {
                task.accept(diagnosticTerminal);
            } catch (Exception e) {
                log.error("Terminal startup task failed", e);
            }
        });

        return new TerminalInstance(
            diagnosticTerminal,
            manager
        );
    }

    private static DiagnosticManager createManager() {
        String currentLibBasePackage = TerminalApplication.class
            .getPackageName();
        
        return new DiagnosticManager(
            DiagnosticComponentScanner.scanCommands(currentLibBasePackage), 
            DiagnosticComponentScanner.scanComponents(currentLibBasePackage)
        );
    }
}