package io.github.kirya343.terminal.command.definition;

import java.util.List;
import java.util.Optional;

public final class CommandGroupDefinition {

    private final String name;
    private final String description;
    private final List<CommandDefinition> commands;
    private final List<CommandGroupDefinition> groups;

    public CommandGroupDefinition(
        String name,
        String description,
        List<CommandDefinition> commands,
        List<CommandGroupDefinition> groups
    ) {
        this.name = name;
        this.description = description;
        this.commands = List.copyOf(commands);
        this.groups = List.copyOf(groups);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<CommandDefinition> getCommands() {
        return commands;
    }

    public List<CommandGroupDefinition> getGroups() {
        return groups;
    }

    public Optional<CommandDefinition> findCommand(String name) {
        return commands.stream()
            .filter(command -> command.getName().equals(name))
            .findFirst();
    }

    public Optional<CommandGroupDefinition> findGroup(String name) {
        return groups.stream()
            .filter(group -> group.getName().equals(name))
            .findFirst();
    }
}