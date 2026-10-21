package org.kirya343.terminal.core;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kirya343.terminal.commands.DiagnosticCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class DiagnosticManager {

    private final Map<String, DiagnosticCommand> commands =
        new ConcurrentHashMap<>();

    public DiagnosticManager(
        List<DiagnosticCommand> commands, 
        List<DiagnosticComponent> components
    ) {
        commands.forEach(this::registerCommand);
        
        components.forEach(c -> 
            c.getCommands().forEach(cm -> 
                registerCommand(cm, c.getComponentId())));
    }

    public void registerCommand(DiagnosticCommand command) {
        registerCommand(command, null);
    }

    public void registerCommand(DiagnosticCommand command, String componentId) {

        String commandName = command.getName();

        if (componentId != null) {
            commandName = componentId + " " + commandName;
        }

        log.debug("Register command: {}", commandName);
        
        DiagnosticCommand existing = commands.putIfAbsent(commandName, command);

        if (existing != null) {
            throw new IllegalArgumentException(
                "Command already registered: " + command.getName()
            );
        }
    }

    public Map.Entry<String, DiagnosticCommand> findCommand(
        String input
    ) {
        return commands.entrySet().stream()
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