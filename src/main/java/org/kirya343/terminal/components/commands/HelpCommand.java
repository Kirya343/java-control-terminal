package org.kirya343.terminal.components.commands;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import org.kirya343.terminal.components.DiagnosticComponent;
import org.kirya343.terminal.core.DiagnosticTextBuilder;
import org.kirya343.terminal.models.CommandResult;
import org.kirya343.terminal.models.TextResult;

@TerminalCommand
public class HelpCommand implements DiagnosticCommand {

    private final Supplier<List<DiagnosticCommand>> commands;
    private final List<DiagnosticComponent> components;

    public HelpCommand(
            Supplier<List<DiagnosticCommand>> commands,
            List<DiagnosticComponent> components
    ) {
        this.commands = commands;
        this.components = components;
    }
    
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows all coommands";
    }

    @Override
    public CommandResult execute(String[] args) {
        DiagnosticTextBuilder builder = new DiagnosticTextBuilder("All application commands:");

        builder.addRow("");
        addCommands(builder, commands.get());

        components.stream()
            .sorted(Comparator.comparing(DiagnosticComponent::getComponentId))
            .forEach(component -> {

                DiagnosticTextBuilder componentSection = new DiagnosticTextBuilder(component.getClass().getSimpleName() + " commands: ");

                componentSection.addRow("usage: " + component.getComponentId() + " <command> [-args]");
                componentSection.addRow("");

                addCommands(componentSection, component.getCommands());

                builder.addSection(componentSection);
            });

        return new TextResult(builder.getContent());
    }

    private void addCommands(DiagnosticTextBuilder builder, List<DiagnosticCommand> cmds) {
        cmds.stream()
            .sorted(Comparator.comparing(DiagnosticCommand::getName))
            .forEach(command -> {
                String cmdInfo = String.format(
                    "%-15s %s",
                    command.getName(),
                    command.getDescription()
                );

                builder.addRow(cmdInfo);
            });
    }
}
