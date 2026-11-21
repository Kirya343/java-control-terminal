package io.github.kirya343.terminal.command.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.kirya343.terminal.command.definition.CommandDefinition;
import io.github.kirya343.terminal.command.definition.CommandParameterDefinition;
import io.github.kirya343.terminal.command.routing.CommandRoute;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public final class DefaultCommandArgumentResolver implements CommandArgumentResolver {

    private final CommandArgumentParser parser;

    @Override 
    public Object[] resolve(CommandRoute route) {
        CommandDefinition command = route.command();

        List<CommandParameterDefinition> parameters =
            command.parameters();

        Map<String, String> values = new HashMap<>(
            route.pathParameters()
        );

        values.putAll(parser.parse(parameters, route.arguments()));

        Object[] resolved = new Object[parameters.size()];

        for (CommandParameterDefinition parameter : parameters) {
            String value = values.get(parameter.name());

            if (value == null) {
                if (parameter.optional()) {
                    resolved[parameter.index()] =
                        defaultValue(parameter.type());
                    continue;
                }

                throw new IllegalArgumentException(
                    "Missing required parameter: " + parameter.name()
                );
            }

            resolved[parameter.index()] = convert(
                value,
                parameter.type()
            );
        }

        return resolved;
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