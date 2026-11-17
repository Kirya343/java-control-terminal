package io.github.kirya343.terminal.command.definition;

public final class CommandParameterDefinition {

    private final String name;
    private final Class<?> type;
    private final boolean optional;
    private final boolean pathParameter;
    private final String description;
    private final String example;
    private final int index;

    public CommandParameterDefinition(
        String name,
        Class<?> type,
        boolean optional,
        boolean pathParameter,
        String description,
        String example,
        int index
    ) {
        this.name = name;
        this.type = type;
        this.optional = optional;
        this.pathParameter = pathParameter;
        this.description = description;
        this.example = example;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isPathParameter() {
        return pathParameter;
    }

    public String getDescription() {
        return description;
    }

    public String getExample() {
        return example;
    }

    public int getIndex() {
        return index;
    }
}