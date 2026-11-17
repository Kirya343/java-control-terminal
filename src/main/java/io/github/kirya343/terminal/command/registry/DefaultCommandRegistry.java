package io.github.kirya343.terminal.command.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.kirya343.terminal.command.definition.CommandGroupDefinition;

public final class DefaultCommandRegistry implements CommandRegistry {

    private final Map<String, CommandGroupDefinition> groups = new LinkedHashMap<>();

    @Override
    public void register(CommandGroupDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Command group definition cannot be null");
        }

        String name = definition.getName();

        if (groups.containsKey(name)) {
            throw new IllegalStateException(
                "Command group already registered: " + name
            );
        }

        groups.put(name, definition);
    }

    @Override
    public Optional<CommandGroupDefinition> findGroup(String name) {
        return Optional.ofNullable(groups.get(name));
    }

    @Override
    public Collection<CommandGroupDefinition> getGroups() {
        return List.copyOf(groups.values());
    }

    @Override
    public void clear() {
        groups.clear();
    }
}
