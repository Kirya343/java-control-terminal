package io.github.kirya343.terminal.components.builtin;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import io.github.kirya343.terminal.api.command.DiagnosticCommand;
import io.github.kirya343.terminal.api.command.TerminalCommand;
import io.github.kirya343.terminal.api.component.DiagnosticComponent;
import io.github.kirya343.terminal.formatting.DiagnosticTextBuilder;
import io.github.kirya343.terminal.result.CommandResult;
import io.github.kirya343.terminal.result.TextResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
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

        log.debug("commands: {}", commands.get().stream().map(DiagnosticCommand::getName).toList());
        log.debug("components: {}", components.stream().map(DiagnosticComponent::getComponentId).toList());

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
