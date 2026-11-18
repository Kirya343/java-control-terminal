package io.github.kirya343.terminal.command.registry;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.kirya343.terminal.api.annotations.Command;
import io.github.kirya343.terminal.api.annotations.CommandGroup;
import io.github.kirya343.terminal.api.annotations.CommandParam;
import io.github.kirya343.terminal.api.annotations.CommandPathParam;
import io.github.kirya343.terminal.command.definition.CommandDefinition;
import io.github.kirya343.terminal.command.definition.CommandGroupDefinition;
import io.github.kirya343.terminal.command.definition.CommandParameterDefinition;

public final class CommandScanner {

    public CommandGroupDefinition scan(Object rootInstance) {
        Objects.requireNonNull(rootInstance, "rootInstance");

        Class<?> rootClass = rootInstance.getClass();

        if (!rootClass.isAnnotationPresent(CommandGroup.class)) {
            throw new IllegalArgumentException(
                rootClass.getName() + " is missing @CommandGroup"
            );
        }

        return scanGroup(rootInstance);
    }

    private CommandGroupDefinition scanGroup(Object instance) {
        Class<?> groupClass = instance.getClass();
        CommandGroup groupAnnotation = groupClass.getAnnotation(CommandGroup.class);

        List<CommandDefinition> commands = scanCommands(instance);

        return new CommandGroupDefinition(
            groupAnnotation.value(),
            groupAnnotation.description(),
            commands,
            List.of()
        );
    }

    private List<CommandDefinition> scanCommands(Object instance) {
        List<CommandDefinition> commands = new ArrayList<>();

        for (Method method : instance.getClass().getDeclaredMethods()) {
            Command annotation = method.getAnnotation(Command.class);

            if (annotation == null) {
                continue;
            }

            method.setAccessible(true);

            commands.add(new CommandDefinition(
                annotation.value(),
                annotation.description(),
                method,
                instance,
                scanParameters(method)
            ));
        }

        return commands;
    }

    private List<CommandParameterDefinition> scanParameters(Method method) {
        List<CommandParameterDefinition> parameters = new ArrayList<>();

        Parameter[] methodParameters = method.getParameters();

        for (int i = 0; i < methodParameters.length; i++) {
            Parameter parameter = methodParameters[i];

            CommandParam commandParam =
                parameter.getAnnotation(CommandParam.class);

            CommandPathParam pathParam =
                parameter.getAnnotation(CommandPathParam.class);

            if (commandParam != null && pathParam != null) {
                throw new IllegalArgumentException(
                    "Parameter cannot have both @CommandParam and " +
                    "@CommandPathParam: " + method.getName()
                );
            }

            if (commandParam == null && pathParam == null) {
                throw new IllegalArgumentException(
                    "Missing command parameter annotation: " +
                    method.getName() + ", parameter " + i
                );
            }

            if (commandParam != null) {
                parameters.add(new CommandParameterDefinition(
                    commandParam.value(),
                    parameter.getType(),
                    commandParam.optional(),
                    false,
                    commandParam.description(),
                    commandParam.example(),
                    i
                ));

                continue;
            }

            parameters.add(new CommandParameterDefinition(
                pathParam.value(),
                parameter.getType(),
                false,
                true,
                pathParam.description(),
                "",
                i
            ));
        }

        return parameters;
    }
}