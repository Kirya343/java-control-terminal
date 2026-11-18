package io.github.kirya343.terminal.command.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.kirya343.terminal.command.definition.CommandDefinition;
import io.github.kirya343.terminal.command.definition.CommandParameterDefinition;
import io.github.kirya343.terminal.command.routing.CommandRoute;

public final class DefaultCommandArgumentResolver implements CommandArgumentResolver {

    @Override 
    public Object[] resolve(CommandRoute route) {
        CommandDefinition command = route.command();
        List<CommandParameterDefinition> parameters = command.getParameters();

        Map<String, String> values = new HashMap<>(route.pathParameters());
        List<String> arguments = route.arguments();

        for (int i = 0; i < arguments.size(); i++) {
            values.put(String.valueOf(i), arguments.get(i));
        }

        Object[] resolved = new Object[parameters.size()];

        int argumentIndex = 0;

        for (CommandParameterDefinition parameter : parameters) {
            String value;

            if (parameter.isPathParameter()) {
                value = route.pathParameters().get(parameter.getName());
            } else {
                value = resolveArgument(parameter, arguments, argumentIndex);
                if (value != null) {
                    argumentIndex++;
                }
            }

            if (value == null) {
                if (parameter.isOptional()) {
                    resolved[parameter.getIndex()] = defaultValue(parameter.getType());
                    continue;
                }

                throw new IllegalArgumentException(
                    "Missing required parameter: " + parameter.getName()
                );
            }

            resolved[parameter.getIndex()] = convert(value, parameter.getType());
        }

        return resolved;
    }

    private String resolveArgument(
        CommandParameterDefinition parameter,
        List<String> arguments,
        int index
    ) {
        if (index >= arguments.size()) {
            return null;
        }

        String argument = arguments.get(index);

        if (parameter.getType() == boolean.class
            || parameter.getType() == Boolean.class) {

            if (argument.equalsIgnoreCase(parameter.getName())) {
                return "true";
            }

            if (argument.startsWith("-")) {
                return null;
            }
        }

        return argument;
    }

    private Object convert(String value, Class<?> type) {
        if (type == String.class) return value;
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }
        if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }

        throw new IllegalArgumentException(
            "Unsupported parameter type: " + type.getName()
        );
    }

    private Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;

        throw new IllegalArgumentException(
            "Unsupported primitive type: " + type.getName()
        );
    }
}