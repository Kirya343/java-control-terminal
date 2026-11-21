package io.github.kirya343.terminal.command.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.kirya343.terminal.command.definition.CommandGroupDefinition;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public final class DefaultCommandRegistry implements CommandRegistry {

    private final Map<String, CommandGroupDefinition> groups = new LinkedHashMap<>();

    @Override
    public void register(CommandGroupDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException(
                "Command group definition cannot be null"
            );
        }

        String name = definition.name();

        if (groups.containsKey(name)) {
            throw new IllegalStateException(
                "Command group already registered: " + name
            );
        }

        groups.put(name, definition);

        log.info("Registered command group:\n{}", formatGroup(definition, ""));
    }

    private String formatGroup(
        CommandGroupDefinition group,
        String parentPath
    ) {
        String groupPath = parentPath.isBlank()
            ? group.name()
            : parentPath + " " + group.name();

        StringBuilder builder = new StringBuilder()
            .append(groupPath);

        if (!group.description().isBlank()) {
            builder.append(" - ").append(group.description());
        }

        for (var command : group.commands()) {
            builder.append("\n  ").append(groupPath);

            if (!command.name().isBlank()) {
                builder.append(" ").append(command.name());
            }

            for (var parameter : command.parameters()) {
                builder.append(" ");

                if (parameter.pathParameter()) {
                    builder.append("{").append(parameter.name()).append("}");
                } else {
                    builder.append("--").append(parameter.name());

                    if (!parameter.aliases().isEmpty()) {
                        builder.append(" (-")
                            .append(String.join(", -", parameter.aliases()))
                            .append(")");
                    }
                }
            }
        }

        for (var nested : group.groups()) {
            builder.append("\n")
                .append(formatGroup(nested, groupPath));
        }

        return builder.toString();
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
