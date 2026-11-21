package io.github.kirya343.terminal.command.execution;

import java.util.List;
import java.util.Map;

import io.github.kirya343.terminal.command.definition.CommandParameterDefinition;

public interface CommandArgumentParser {

    Map<String, String> parse(
        List<CommandParameterDefinition> parameters,
        List<String> arguments
    );
}