package io.github.kirya343.terminal.command.routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.kirya343.terminal.command.definition.CommandDefinition;
import io.github.kirya343.terminal.command.definition.CommandGroupDefinition;
import io.github.kirya343.terminal.command.registry.CommandRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public final class CommandRouter {

    private final CommandRegistry registry;

    public Optional<CommandRoute> route(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return Optional.empty();
        }

        CommandGroupDefinition rootGroup =
            registry.findGroup(tokens.get(0)).orElse(null);

        if (rootGroup == null) {
            return Optional.empty();
        }

        return findRoute(
            rootGroup,
            tokens,
            1,
            new HashMap<>()
        );
    }

    private Optional<CommandRoute> findRoute(
        CommandGroupDefinition group,
        List<String> tokens,
        int index,
        Map<String, String> pathParameters
    ) {
        // 1. Если токенов больше нет — ищем default-команду.
        if (index >= tokens.size()) {
            return group.findCommand("")
                .map(command -> new CommandRoute(
                    command,
                    pathParameters,
                    List.of()
                ));
        }

        String token = tokens.get(index);

        // 2. Ищем именованную команду.
        Optional<CommandDefinition> command = group.findCommand(token);

        if (command.isPresent()) {
            return Optional.of(new CommandRoute(
                command.get(),
                pathParameters,
                tokens.subList(index + 1, tokens.size())
            ));
        }

        // 3. Сначала статические группы.
        for (CommandGroupDefinition child : group.getGroups()) {
            if (isDynamic(child.getName())) {
                continue;
            }

            if (child.getName().equals(token)) {
                Optional<CommandRoute> result = findRoute(
                    child,
                    tokens,
                    index + 1,
                    pathParameters
                );

                if (result.isPresent()) {
                    return result;
                }
            }
        }

        // 4. Затем динамические группы.
        for (CommandGroupDefinition child : group.getGroups()) {
            if (!isDynamic(child.getName())) {
                continue;
            }

            String parameterName = extractParameterName(child.getName());

            Map<String, String> parameters = new HashMap<>(pathParameters);
            parameters.put(parameterName, token);

            Optional<CommandRoute> result = findRoute(
                child,
                tokens,
                index + 1,
                parameters
            );

            if (result.isPresent()) {
                return result;
            }
        }

        // 5. Если группа не подошла — пробуем её default-команду.
        return group.findCommand("")
            .map(defaultCommand -> new CommandRoute(
                defaultCommand,
                pathParameters,
                tokens.subList(index, tokens.size())
            ));
    }

    private boolean isDynamic(String name) {
        return name.startsWith("{") && name.endsWith("}");
    }

    private String extractParameterName(String name) {
        return name.substring(1, name.length() - 1);
    }
}