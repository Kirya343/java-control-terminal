package io.github.kirya343.terminal.command.definition;

import java.lang.reflect.Method;
import java.util.List;

public final class CommandDefinition {

    private final String name;
    private final String description;
    private final Method method;
    private final Object instance;
    private final List<CommandParameterDefinition> parameters;

    public CommandDefinition(
        String name,
        String description,
        Method method,
        Object instance,
        List<CommandParameterDefinition> parameters
    ) {
        this.name = name;
        this.description = description;
        this.method = method;
        this.instance = instance;
        this.parameters = List.copyOf(parameters);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    public List<CommandParameterDefinition> getParameters() {
        return parameters;
    }
}