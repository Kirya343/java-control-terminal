package io.github.kirya343.terminal.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.kirya343.terminal.components.DiagnosticComponent;
import io.github.kirya343.terminal.components.commands.DiagnosticCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class DiagnosticManager {

    private final Map<String, DiagnosticCommand> commandMap =
        new ConcurrentHashMap<>();

    @Getter 
    private final List<DiagnosticCommand> commands = new ArrayList<>();
    @Getter
    private final List<DiagnosticComponent> components = new ArrayList<>();

    public DiagnosticManager(
        List<DiagnosticCommand> commands, 
        List<DiagnosticComponent> components
    ) {
        commands.forEach(this::registerCommand);
        components.forEach(this::registerComponent);
    }

    public void registerCommand(DiagnosticCommand command) {

        commands.add(command);

        log.debug("Register command: {}", command.getName());
        registerCommand(command, null);
    }

    public void registerComponent(DiagnosticComponent component) {

        components.add(component);

        log.debug("Registed component: {}", component.getComponentId());
        component.getCommands().forEach(cmd -> 
            registerCommand(cmd, component.getComponentId()));
    }

    public void registerCommand(DiagnosticCommand command, String componentId) {

        String commandName = command.getName();

        if (componentId != null) {
            commandName = componentId + " " + commandName;
        }
        
        DiagnosticCommand existing = commandMap.putIfAbsent(commandName, command);

        if (existing != null) {
            throw new IllegalArgumentException(
                "Command already registered: " + command.getName()
            );
        }

        log.debug("Registed command: {}", commandName);
    }

    public Map.Entry<String, DiagnosticCommand> findCommand(
        String input
    ) {
        return commandMap.entrySet().stream()
            .filter(entry ->
                input.equals(entry.getKey())
                    || input.startsWith(entry.getKey() + " ")
            )
            .max(Map.Entry.comparingByKey(
                Comparator.comparingInt(String::length)
            ))
            .orElseThrow(() ->
                new IllegalArgumentException("Unknown command: " + input)
            );
    }
}