package io.github.kirya343.terminal.api.provider;

import java.util.List;

import io.github.kirya343.terminal.api.command.DiagnosticCommand;
import io.github.kirya343.terminal.api.component.DiagnosticComponent;

public interface DiagnosticCommandProvider {

    List<DiagnosticCommand> getCommands();
    List<DiagnosticComponent> getComponents();
}
