package io.github.kirya343.terminal.runtime;

import java.util.function.Consumer;

import io.github.kirya343.terminal.TerminalApplication;
import io.github.kirya343.terminal.command.builtin.HelpCommands;
import io.github.kirya343.terminal.command.builtin.LogsCommands;
import io.github.kirya343.terminal.command.registration.CommandInstanceProvider;
import io.github.kirya343.terminal.command.registration.LocalCommandInstanceProvider;
import io.github.kirya343.terminal.console.DiagnosticTerminal;
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

            HelpCommands helpCommands = new HelpCommands(terminalInstance.registry());

            LocalCommandInstanceProvider instanceProvider = new LocalCommandInstanceProvider();

            instanceProvider.register(HelpCommands.class, helpCommands);
            instanceProvider.register(LogsCommands.class, new LogsCommands());

            instanceProvider.getInstances().stream()
                .map(terminalInstance.scanner()::scan)
                .forEach(terminalInstance.registry()::register);

            Consumer<CommandInstanceProvider> applicationInstanceProvider =
                provider -> provider.getInstances().stream()
                    .map(terminalInstance.scanner()::scan)
                    .forEach(terminalInstance.registry()::register);

            DiagnosticTerminal terminal = terminalInstance.terminal();

            ApplicationLifecycle lifecycle = new ApplicationLifecycle() {

                @Override
                public void onStarted() {
                    terminalInstance.terminal().getScreen().setStatus("ONLINE");
                    log.info(" === Application started === ");
                }

                @Override
                public void onStopped() {
                    terminalInstance.terminal().getScreen().setStatus("OFFLINE");
                    log.info(" === Application stopped === ");
                }

                @Override
                public void onFailed(Throwable exception) {
                    terminalInstance.terminal().getScreen().setStatus("OFFLINE");
                    log.error(" === Application failed === ", exception);
                }
            };

            Thread applicationThread = new Thread(
                () -> {
                    try {
                        ApplicationLauncher.launch(
                            applicationClass,
                            args,
                            applicationInstanceProvider,
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