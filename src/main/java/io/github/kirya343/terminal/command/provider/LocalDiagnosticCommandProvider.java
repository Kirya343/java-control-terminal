package io.github.kirya343.terminal.command.provider;

import java.util.List;
import java.util.function.Supplier;

import io.github.kirya343.terminal.api.command.DiagnosticCommand;
import io.github.kirya343.terminal.api.component.DiagnosticComponent;
import io.github.kirya343.terminal.api.provider.DiagnosticCommandProvider;
import io.github.kirya343.terminal.command.builtin.HelpCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class LocalDiagnosticCommandProvider
        implements DiagnosticCommandProvider {

    private final Supplier<List<DiagnosticCommand>> commands;
    private final List<DiagnosticComponent> components;

    public List<DiagnosticCommand> getCommands() {
        return List.of(
            new HelpCommand(commands, getComponents())
        );
    }

    public List<DiagnosticComponent> getComponents() {
        return components;
    }
}
