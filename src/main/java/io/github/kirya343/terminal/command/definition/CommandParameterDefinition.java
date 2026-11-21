package io.github.kirya343.terminal.command.definition;

import java.util.List;

public record CommandParameterDefinition(
    String name,
    Class<?> type,
    List<String> aliases,
    boolean optional,
    boolean pathParameter,
    String description,
    String example,
    int index
) {
    public CommandParameterDefinition {
        aliases = List.copyOf(aliases);
    }
}