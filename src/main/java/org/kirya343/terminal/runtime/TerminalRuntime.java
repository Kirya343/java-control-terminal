package org.kirya343.terminal.runtime;

import org.kirya343.terminal.DiagnosticTerminal;
import org.kirya343.terminal.TerminalApplication;
import org.kirya343.terminal.core.DiagnosticComponentScanner;
import org.kirya343.terminal.core.DiagnosticManager;

public final class TerminalRuntime {

    private TerminalRuntime() {
    }

    public static void launch(
            Class<?> applicationClass,
            String[] args
    ) {
        DiagnosticManager manager = DiagnosticComponentScanner.scan("org.kirya343.terminal");

        try {
            DiagnosticTerminal terminal =
                TerminalApplication.create(manager);

            Thread applicationThread = new Thread(
                () -> {
                    try {
                        ApplicationLauncher.launch(
                            applicationClass,
                            args
                        );
                    } catch (Throwable e) {
                        // Передать ошибку в диагностическую систему.
                        e.printStackTrace();
                    }
                },
                "managed-application"
            );

            applicationThread.start();

            terminal.start();
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to start terminal runtime",
                e
            );
        }
    }
}