package io.github.kirya343.terminal.command;

import java.util.List;

import io.github.kirya343.terminal.api.provider.DiagnosticCommandProvider;
import io.github.kirya343.terminal.registry.DiagnosticManager;

public final class DiagnosticCommandRegistrar {

    public void register(
            DiagnosticManager manager,
            List<DiagnosticCommandProvider> providers
    ) {
        providers.stream().forEach(provider -> register(manager, provider));
    }

    public void register(
            DiagnosticManager manager,
            DiagnosticCommandProvider provider
    ) {
        provider.getCommands().stream().forEach(manager::registerCommand);
        provider.getComponents().stream().forEach(manager::registerComponent);
    }
}