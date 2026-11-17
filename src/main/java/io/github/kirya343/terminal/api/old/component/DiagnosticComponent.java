package io.github.kirya343.terminal.api.component;

import java.util.List;

import io.github.kirya343.terminal.api.command.DiagnosticCommand;

public interface DiagnosticComponent {

    String getComponentId();

    List<DiagnosticCommand> getCommands();
}