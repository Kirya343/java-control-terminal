package io.github.kirya343.terminal;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import io.github.kirya343.terminal.components.DiagnosticComponentScanner;
import io.github.kirya343.terminal.console.DiagnosticContentLayout;
import io.github.kirya343.terminal.console.DiagnosticScreen;
import io.github.kirya343.terminal.console.DiagnosticScreenRenderer;
import io.github.kirya343.terminal.console.DiagnosticTerminal;
import io.github.kirya343.terminal.core.DiagnosticManager;
import io.github.kirya343.terminal.core.TerminalCommandExecutor;
import io.github.kirya343.terminal.logging.DiagnosticLoggingConfigurer;
import io.github.kirya343.terminal.logging.LogBuffer;
import io.github.kirya343.terminal.logging.LogCollector;
import io.github.kirya343.terminal.runtime.TerminalInstance;

public final class TerminalApplication {

    public static TerminalInstance create(String applicationName) throws Exception {
        LogBuffer logBuffer = new LogBuffer(1000);
        LogCollector logCollector = new LogCollector(logBuffer);

        DiagnosticLoggingConfigurer loggingConfigurer =
            new DiagnosticLoggingConfigurer(logCollector);

        loggingConfigurer.configure();

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

        TerminalCommandExecutor commandExecutor =
            new TerminalCommandExecutor(manager, screen);

        return new TerminalInstance(
            new DiagnosticTerminal(
                terminal,
                screen,
                commandExecutor
            ),
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