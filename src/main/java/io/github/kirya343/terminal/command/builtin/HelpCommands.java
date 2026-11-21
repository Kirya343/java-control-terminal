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
            .sorted(Comparator.comparing(CommandGroupDefinition::name))
            .forEach(group -> addGroup(builder, group, ""));

        return new TextResult(builder.getContent());
    }

    private void addGroup(
        DiagnosticTextBuilder builder,
        CommandGroupDefinition group,
        String parentPath
    ) {
        String groupPath = parentPath.isBlank()
            ? group.name()
            : parentPath + " " + group.name();

        DiagnosticTextBuilder section = new DiagnosticTextBuilder(
            groupPath + " commands"
        );

        if (!group.description().isBlank()) {
            section.addRow(group.description());
        }

        section.addRow("Usage: " + groupPath + " <command> [arguments]");
        section.addRow("");

        group.commands().stream()
            .sorted(Comparator.comparing(CommandDefinition::name))
            .forEach(command -> addCommand(section, command, groupPath));

        builder.addSection(section);

        group.groups().stream()
            .sorted(Comparator.comparing(CommandGroupDefinition::name))
            .forEach(nested -> addGroup(builder, nested, groupPath));
    }

    private void addCommand(
        DiagnosticTextBuilder builder,
        CommandDefinition command,
        String groupPath
    ) {
        String commandPath = groupPath;

        if (!command.name().isBlank()) {
            commandPath += " " + command.name();
        }

        String usage = command.parameters().stream()
            .map(this::formatParameter)
            .collect(Collectors.joining(" "));

        if (!usage.isBlank()) {
            commandPath += " " + usage;
        }

        builder.addRow(commandPath);

        if (!command.description().isBlank()) {
            builder.addRow("    " + command.description());
        }

        command.parameters().forEach(parameter -> {
            String description = parameter.description();

            if (parameter.pathParameter()) {
                description = "[path] " + description;
            }

            if (parameter.optional()) {
                description = "[optional] " + description;
            }

            if (parameter.example() != null
                && !parameter.example().isBlank()) {
                description += " (example: " + parameter.example() + ")";
            }

            String name = parameter.pathParameter()
                ? parameter.name()
                : "--" + parameter.name();

            if (!parameter.aliases().isEmpty()) {
                name += ", " + parameter.aliases().stream()
                    .map(alias -> "-" + alias)
                    .collect(Collectors.joining(", "));
            }

            builder.addRow(String.format(
                "    %-20s %-10s %s",
                name,
                parameter.type().getSimpleName(),
                description
            ));
        });

        builder.addRow("");
    }

    private String formatParameter(CommandParameterDefinition parameter) {
        if (parameter.pathParameter()) {
            return parameter.optional()
                ? "[" + parameter.name() + "]"
                : "<" + parameter.name() + ">";
        }

        String name = "--" + parameter.name();

        return parameter.optional()
            ? "[" + name + "]"
            : name;
    }
}
