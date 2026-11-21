package io.github.kirya343.terminal.command.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.kirya343.terminal.command.definition.CommandParameterDefinition;

public final class DefaultCommandArgumentParser
    implements CommandArgumentParser {

    @Override
    public Map<String, String> parse(
        List<CommandParameterDefinition> parameters,
        List<String> arguments
    ) {
        Map<String, CommandParameterDefinition> options = new HashMap<>();

        for (CommandParameterDefinition parameter : parameters) {
            if (parameter.pathParameter()) {
                continue;
            }

            options.put("--" + parameter.name(), parameter);

            for (String alias : parameter.aliases()) {
                options.put("-" + alias, parameter);
            }
        }

        Map<String, String> values = new HashMap<>();

        boolean optionsEnabled = true;

        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);

            if (optionsEnabled && argument.equals("--")) {
                optionsEnabled = false;
                continue;
            }

            if (!optionsEnabled) {
                throw new IllegalArgumentException(
                    "Unexpected positional argument: " + argument
                );
            }

            if (!argument.startsWith("-") || argument.equals("-")) {
                throw new IllegalArgumentException(
                    "Unexpected argument: " + argument
                );
            }

            int separatorIndex = argument.indexOf('=');

            String optionName = separatorIndex >= 0
                ? argument.substring(0, separatorIndex)
                : argument;

            String explicitValue = separatorIndex >= 0
                ? argument.substring(separatorIndex + 1)
                : null;

            CommandParameterDefinition parameter =
                options.get(optionName);

            if (parameter == null) {
                throw new IllegalArgumentException(
                    "Unknown command parameter: " + optionName
                );
            }

            String name = parameter.name();

            if (values.containsKey(name)) {
                throw new IllegalArgumentException(
                    "Duplicate command parameter: " + optionName
                );
            }

            String value;

            if (isBoolean(parameter.type())) {
                if (explicitValue != null) {
                    value = parseBoolean(explicitValue, optionName);
                } else if (i + 1 < arguments.size()
                    && isBooleanLiteral(arguments.get(i + 1))) {
                    value = arguments.get(++i);
                } else {
                    value = "true";
                }
            } else {
                if (explicitValue != null) {
                    if (explicitValue.isEmpty()) {
                        throw new IllegalArgumentException(
                            "Missing value for parameter: " + optionName
                        );
                    }

                    value = explicitValue;
                } else {
                    String next = arguments.get(i + 1);

                    if (options.containsKey(next) || next.equals("--")) {
                        throw new IllegalArgumentException(
                            "Missing value for parameter: " + optionName
                        );
                    }

                    value = arguments.get(++i);
                }
            }

            values.put(name, value);
        }

        return values;
    }

    private boolean isBoolean(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

    private boolean isBooleanLiteral(String value) {
        return value.equalsIgnoreCase("true")
            || value.equalsIgnoreCase("false");
    }

    private String parseBoolean(String value, String optionName) {
        return Boolean.toString(Boolean.parseBoolean(value));
    }
}