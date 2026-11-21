package io.github.kirya343.terminal.command.registry;

import java.util.Collection;
import java.util.Optional;

import io.github.kirya343.terminal.command.definition.CommandGroupDefinition;

public interface CommandRegistry {

    void register(CommandGroupDefinition definition);

    Optional<CommandGroupDefinition> findGroup(String name);

    Collection<CommandGroupDefinition> getGroups();

    void clear();
}