package org.kirya343.terminal.components;

import java.util.List;

import org.kirya343.terminal.core.DiagnosticManager;

public final class DiagnosticCommandRegistrar {

    public void register(
            DiagnosticManager manager,
            List<DiagnosticCommandProvider> providers
    ) {
        providers.stream()
            .forEach(provider -> {
                provider.getCommands().stream().forEach(manager::registerCommand);
                provider.getComponents().stream().forEach(manager::registerComponent);
            });
    }
}