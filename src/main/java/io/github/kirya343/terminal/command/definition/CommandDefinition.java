package io.github.kirya343.terminal.command.definition;

import java.lang.reflect.Method;
import java.util.List;

public record CommandDefinition(
    String name,
    String description,
    Method method,
    Object instance,
    List<CommandParameterDefinition> parameters
) {
    public CommandDefinition {
        parameters = List.copyOf(parameters);
    }
}