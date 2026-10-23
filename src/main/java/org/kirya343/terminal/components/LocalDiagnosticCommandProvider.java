package org.kirya343.terminal.components;

import java.util.List;

import org.kirya343.terminal.components.commands.DiagnosticCommand;
import org.kirya343.terminal.components.commands.HelpCommand;

public class LocalDiagnosticCommandProvider
        implements DiagnosticCommandProvider {

    public List<DiagnosticCommand> getCommands() {
        return List.of(
            new HelpCommand(null, getComponents())
        );
    }

    public List<DiagnosticComponent> getComponents() {
        return List.of();
    }
}
