package org.kirya343.terminal.runtime;

import java.util.function.Consumer;

import org.kirya343.terminal.TerminalApplication;
import org.kirya343.terminal.components.DiagnosticCommandProvider;
import org.kirya343.terminal.components.DiagnosticCommandRegistrar;
import org.kirya343.terminal.components.DiagnosticComponentScanner;
import org.kirya343.terminal.components.LocalDiagnosticCommandProvider;
import org.kirya343.terminal.console.DiagnosticTerminal;
import org.kirya343.terminal.core.DiagnosticManager;

public final class TerminalRuntime {

    public static void launch(
            Class<?> applicationClass,
            String[] args
    ) {

        String currentLibBasePackage = TerminalApplication.class
            .getPackageName();
        
        DiagnosticManager manager = new DiagnosticManager(
            DiagnosticComponentScanner.scanCommands(currentLibBasePackage), 
            DiagnosticComponentScanner.scanComponents(currentLibBasePackage)
        );

        DiagnosticCommandRegistrar registrar =
            new DiagnosticCommandRegistrar();

        registrar.register(
            manager,
            new LocalDiagnosticCommandProvider(() -> manager.getCommands())
        );

        Consumer<DiagnosticCommandProvider> registerProvider =
            provider -> registrar.register(
                manager,
                provider
            );

        try {
            DiagnosticTerminal terminal =
                TerminalApplication.create(manager);

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