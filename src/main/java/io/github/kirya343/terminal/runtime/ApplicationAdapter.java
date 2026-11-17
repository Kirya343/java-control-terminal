package io.github.kirya343.terminal.runtime;

import java.util.function.Consumer;

import io.github.kirya343.terminal.command.registration.CommandInstanceProvider;

@FunctionalInterface
public interface ApplicationAdapter {

    void launch(
        Class<?> applicationClass,
        String[] args,
        Consumer<CommandInstanceProvider> commandInstanceProvider,
        ApplicationLifecycle lifecycle
    ) throws Exception;
}
