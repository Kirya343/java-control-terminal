package org.kirya343.terminal.components;

import java.util.List;

import org.kirya343.terminal.components.commands.DiagnosticCommand;

public interface DiagnosticCommandProvider {

    List<DiagnosticCommand> getCommands();
    List<DiagnosticComponent> getComponents();
}
