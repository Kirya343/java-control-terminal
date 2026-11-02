package io.github.kirya343.terminal.runtime;

import java.util.function.Consumer;

import io.github.kirya343.terminal.TerminalApplication;
import io.github.kirya343.terminal.api.provider.DiagnosticCommandProvider;
import io.github.kirya343.terminal.command.DiagnosticCommandRegistrar;
import io.github.kirya343.terminal.command.provider.LocalDiagnosticCommandProvider;
import io.github.kirya343.terminal.component.DiagnosticComponentScanner;
import io.github.kirya343.terminal.console.DiagnosticTerminal;
import io.github.kirya343.terminal.registry.DiagnosticManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public final class TerminalRuntime {

    public static void launch(
            Class<?> applicationClass,
            String applicationName,
            String[] args,
            ApplicationAdapter adapter
    ) {
        launch(
            applicationClass,
            applicationName,
            args,
            adapter,
            TerminalConfig.defaultConfig()
        );
    }

    public static void launch(
            Class<?> applicationClass,
            String applicationName,
            String[] args,
            ApplicationAdapter adapter,
            TerminalConfig config
    ) {
        try {

            TerminalInstance terminalInstance = TerminalApplication.create(applicationName, config);

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

            ApplicationLifecycle lifecycle = new ApplicationLifecycle() {

                @Override
                public void onStarted() {
                    terminalInstance.terminal().getScreen().setStatus("ONLINE");

                    String appPackage = applicationClass.getPackageName();

                    DiagnosticComponentScanner.scanCommands(appPackage)
                        .forEach(manager::registerCommand);

                    DiagnosticComponentScanner.scanComponents(appPackage)
                        .forEach(manager::registerComponent);
                }

                @Override
                public void onStopped() {
                    terminalInstance.terminal().getScreen().setStatus("OFFLINE");
                    log.info("Application stopped");
                }

                @Override
                public void onFailed(Throwable exception) {
                    terminalInstance.terminal().getScreen().setStatus("OFFLINE");
                    log.error("Application failed", exception);
                }
            };

            Thread applicationThread = new Thread(
                () -> {
                    try {
                        ApplicationLauncher.launch(
                            applicationClass,
                            args,
                            registerProvider,
                            adapter,
                            lifecycle
                        );
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