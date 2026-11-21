package io.github.kirya343.terminal.command.definition;

import java.util.List;
import java.util.Optional;

public record CommandGroupDefinition(
    String name,
    String description,
    List<CommandDefinition> commands,
    List<CommandGroupDefinition> groups
) {

    public CommandGroupDefinition {
        commands = List.copyOf(commands);
        groups = List.copyOf(groups);
    }

    public Optional<CommandDefinition> findCommand(String name) {
        return commands.stream()
            .filter(command -> command.name().equals(name))
            .findFirst();
    }

    public Optional<CommandGroupDefinition> findGroup(String name) {
        return groups.stream()
            .filter(group -> group.name().equals(name))
            .findFirst();
    }
}