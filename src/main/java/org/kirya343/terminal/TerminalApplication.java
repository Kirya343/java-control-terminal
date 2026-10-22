package org.kirya343.terminal;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.kirya343.terminal.console.DiagnosticContentLayout;
import org.kirya343.terminal.console.DiagnosticScreenRenderer;
import org.kirya343.terminal.core.DiagnosticManager;
import org.kirya343.terminal.core.TerminalCommandExecutor;
import org.kirya343.terminal.logging.DiagnosticLoggingConfigurer;
import org.kirya343.terminal.logging.LogBuffer;
import org.kirya343.terminal.logging.LogCollector;

public final class TerminalApplication {

    public static DiagnosticTerminal create(
            DiagnosticManager manager
    ) throws Exception {
        LogBuffer logBuffer = new LogBuffer(1000);
        LogCollector logCollector = new LogCollector(logBuffer);

        DiagnosticLoggingConfigurer loggingConfigurer =
            new DiagnosticLoggingConfigurer(logCollector);

        loggingConfigurer.configure();

        Terminal terminal = TerminalBuilder.builder()
            .system(true)
            .build();

        DiagnosticScreenRenderer renderer =
            new DiagnosticScreenRenderer(terminal);

        DiagnosticScreen screen = new DiagnosticScreen(
            terminal,
            renderer,
            new DiagnosticContentLayout()
        );

        TerminalCommandExecutor commandExecutor =
            new TerminalCommandExecutor(manager, screen);

        return new DiagnosticTerminal(
            terminal,
            screen,
            commandExecutor
        );
    }
}