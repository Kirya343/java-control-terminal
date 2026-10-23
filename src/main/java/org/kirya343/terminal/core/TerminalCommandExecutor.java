package org.kirya343.terminal.core;

import java.util.List;

import org.kirya343.terminal.components.commands.DiagnosticCommand;
import org.kirya343.terminal.console.DiagnosticScreen;
import org.kirya343.terminal.models.CommandResult;
import org.kirya343.terminal.models.StreamingResult;
import org.kirya343.terminal.models.TextResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class TerminalCommandExecutor {

    private final DiagnosticManager diagnosticManager;
    private final DiagnosticScreen screen;

    private Thread streamingThread;
    
    /**
     * Выполняет диагностическую команду.
     */
    public void executeCommand(String input) {
        stopStreaming();
        screen.clearContent();

        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            return;
        }

        try {

            var entry = diagnosticManager.findCommand(trimmedInput);

            String commandName = entry.getKey();
            DiagnosticCommand command = entry.getValue();

            String argumentsString = trimmedInput
                .substring(commandName.length())
                .trim();

            String[] commandArgs = argumentsString.isEmpty()
                ? new String[0]
              : argumentsString.split("\\s+");

            CommandResult result = command.execute(commandArgs);

            if (result instanceof StreamingResult streamingResult) {
                streamingThread = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        if (streamingThread == null) {
                            return;
                        }

                        String content = streamingResult.provider().getData();

                        switch (streamingResult.mode()) {
                            case APPEND -> {
                                if (content == null || content.isEmpty()) {
                                    break;
                                }

                                screen.addLines(
                                    List.of(content.split("\\R"))
                                );
                            }
                            case REPLACE -> screen.setContent(
                                List.of(content.split("\\R"))
                            );
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });

                streamingThread.setDaemon(true);
                streamingThread.start();

                return;
            }

            if (result instanceof TextResult textResult) {
                screen.setContent(
                    List.of(textResult.content().split("\\R"))
                );

                return;
            }

            screen.setContent(
                List.of("Unsupported diagnostic result: " + result)
            );
        } catch (Exception exception) {
            screen.setContent(
                List.of("Diagnostic error: " + exception.getMessage())
            );
        }
    }

    private void stopStreaming() {
        if (streamingThread != null && streamingThread.isAlive()) {
            streamingThread.interrupt();
            streamingThread = null;
        }
    }
}
