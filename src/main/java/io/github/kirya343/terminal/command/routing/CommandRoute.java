package io.github.kirya343.terminal.command.routing;

import java.util.Map;

import io.github.kirya343.terminal.command.definition.CommandDefinition;

public record CommandRoute(
    CommandDefinition command,
    Map<String, String> pathParameters
) {
    public CommandRoute {
        pathParameters = Map.copyOf(pathParameters);
    }
}