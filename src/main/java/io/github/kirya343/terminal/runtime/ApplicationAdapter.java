package io.github.kirya343.terminal.runtime;

import java.util.function.Consumer;

import io.github.kirya343.terminal.api.provider.DiagnosticCommandProvider;

@FunctionalInterface
public interface ApplicationAdapter {

    void launch(
        Class<?> applicationClass,
        String[] args,
        Consumer<DiagnosticCommandProvider> registerProvider,
        ApplicationLifecycle lifecycle
    ) throws Exception;
}
