package io.github.kirya343.terminal.api.command;

import io.github.kirya343.terminal.result.CommandResult;

public interface DiagnosticCommand {

    String getName();

    String getDescription();

    CommandResult execute(String[] args);
}