package io.github.kirya343.terminal.components;

import java.util.List;
import java.util.function.Supplier;

import io.github.kirya343.terminal.components.commands.DiagnosticCommand;
import io.github.kirya343.terminal.components.commands.HelpCommand;
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
