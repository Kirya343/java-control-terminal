package io.github.kirya343.terminal.command.builtin;

import java.util.Comparator;
import java.util.stream.Collectors;

import io.github.kirya343.terminal.api.annotations.Command;
import io.github.kirya343.terminal.api.annotations.CommandGroup;
import io.github.kirya343.terminal.command.definition.CommandDefinition;
import io.github.kirya343.terminal.command.definition.CommandGroupDefinition;
import io.github.kirya343.terminal.command.definition.CommandParameterDefinition;
import io.github.kirya343.terminal.command.registry.CommandRegistry;
import io.github.kirya343.terminal.formatting.DiagnosticTextBuilder;
import io.github.kirya343.terminal.result.CommandResult;
import io.github.kirya343.terminal.result.TextResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RequiredArgsConstructor 
@CommandGroup("help")
public class HelpCommands {

    private final CommandRegistry commandRegistry;

    @Command(description = "Shows all coommands")
    public CommandResult getHelp() {
        DiagnosticTextBuilder builder =
            new DiagnosticTextBuilder("Application commands:");

        commandRegistry.getGroups().stream()
            .sorted(Comparator.comparing(CommandGroupDefinition::getName))
            .forEach(group -> addGroup(builder, group, ""));

        return new TextResult(builder.getContent());
    }

    private void addGroup(
        DiagnosticTextBuilder builder,
        CommandGroupDefinition group,
        String parentPath
    ) {
        String groupPath = parentPath.isBlank()
            ? group.getName()
            : parentPath + " " + group.getName();

        DiagnosticTextBuilder section = new DiagnosticTextBuilder(
            groupPath + " commands"
        );

        if (!group.getDescription().isBlank()) {
            section.addRow(group.getDescription());
        }

        section.addRow("Usage: " + groupPath + " <command> [arguments]");
        section.addRow("");

        group.getCommands().stream()
            .sorted(Comparator.comparing(CommandDefinition::getName))
            .forEach(command -> addCommand(section, command, groupPath));

        builder.addSection(section);

        group.getGroups().stream()
            .sorted(Comparator.comparing(CommandGroupDefinition::getName))
            .forEach(nested -> addGroup(builder, nested, groupPath));
    }

    private void addCommand(
        DiagnosticTextBuilder builder,
        CommandDefinition command,
        String groupPath
    ) {
        String commandPath = groupPath;

        if (!command.getName().isBlank()) {
            commandPath += " " + command.getName();
        }

        String usage = command.getParameters().stream()
            .map(this::formatParameter)
            .collect(Collectors.joining(" "));

        if (!usage.isBlank()) {
            commandPath += " " + usage;
        }

        builder.addRow(commandPath);

        if (!command.getDescription().isBlank()) {
            builder.addRow("    " + command.getDescription());
        }

        command.getParameters().forEach(parameter -> {
            String description = parameter.getDescription();

            if (parameter.isPathParameter()) {
                description = "[path] " + description;
            }

            if (parameter.isOptional()) {
                description = "[optional] " + description;
            }

            if (parameter.getExample() != null
                && !parameter.getExample().isBlank()) {
                description += " (example: " + parameter.getExample() + ")";
            }

            builder.addRow(String.format(
                "    %-15s %-10s %s",
                parameter.getName(),
                parameter.getType().getSimpleName(),
                description
            ));
        });

        builder.addRow("");
    }

    private String formatParameter(CommandParameterDefinition parameter) {
        String name = parameter.isPathParameter()
            ? "{" + parameter.getName() + "}"
            : parameter.getName();

        return parameter.isOptional()
            ? "[" + name + "]"
            : "<" + name + ">";
    }
}
