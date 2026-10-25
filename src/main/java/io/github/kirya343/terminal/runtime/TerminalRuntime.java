package io.github.kirya343.terminal.runtime;

import java.util.function.Consumer;

import io.github.kirya343.terminal.TerminalApplication;
import io.github.kirya343.terminal.components.DiagnosticCommandProvider;
import io.github.kirya343.terminal.components.DiagnosticCommandRegistrar;
import io.github.kirya343.terminal.components.DiagnosticComponentScanner;
import io.github.kirya343.terminal.components.LocalDiagnosticCommandProvider;
import io.github.kirya343.terminal.console.DiagnosticTerminal;
import io.github.kirya343.terminal.core.DiagnosticManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public final class TerminalRuntime {

    public static void launch(
            Class<?> applicationClass,
            String[] args
    ) {
        try {
            TerminalInstance terminalInstance = TerminalApplication.create();

            DiagnosticManager manager = terminalInstance.manager();

            DiagnosticCommandRegistrar registrar =
                new DiagnosticCommandRegistrar();

            registrar.register(
                manager,
                new LocalDiagnosticCommandProvider(() -> manager.getCommands(), manager.getComponents())
            );

            Consumer<DiagnosticCommandProvider> registerProvider =
                provider -> registrar.register(
                    manager,
                    provider
                );

            DiagnosticTerminal terminal = terminalInstance.terminal();

            Thread applicationThread = new Thread(
                () -> {
                    try {
                        ApplicationLauncher.launch(
                            applicationClass,
                            args,
                            registerProvider
                        );

                        String launchingAppPackage = applicationClass.getPackageName();

                        DiagnosticComponentScanner.scanCommands(launchingAppPackage).forEach(manager::registerCommand);
                        DiagnosticComponentScanner.scanComponents(launchingAppPackage).forEach(manager::registerComponent);
                    } catch (Throwable e) {
                        // Передать ошибку в диагностическую систему.
                        log.debug("Application launch failed: {}", e);
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