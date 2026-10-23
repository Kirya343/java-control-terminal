package org.kirya343.terminal.components;

import java.util.List;
import java.util.function.Supplier;

import org.kirya343.terminal.components.commands.DiagnosticCommand;
import org.kirya343.terminal.components.commands.HelpCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class LocalDiagnosticCommandProvider
        implements DiagnosticCommandProvider {

    private final Supplier<List<DiagnosticCommand>> commands;

    public List<DiagnosticCommand> getCommands() {
        return List.of(
            new HelpCommand(commands, getComponents())
        );
    }

    public List<DiagnosticComponent> getComponents() {
        return List.of();
    }
}
