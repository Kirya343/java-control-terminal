package org.kirya343.terminal.core;

import java.util.List;

import org.kirya343.terminal.commands.DiagnosticCommand;

public interface DiagnosticComponent {

    String getComponentId();

    List<DiagnosticCommand> getCommands();
}