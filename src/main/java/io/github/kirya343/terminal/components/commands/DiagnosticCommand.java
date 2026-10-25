package io.github.kirya343.terminal.components.commands;

import io.github.kirya343.terminal.models.CommandResult;

public interface DiagnosticCommand {

    String getName();

    String getDescription();

    CommandResult execute(String[] args);
}