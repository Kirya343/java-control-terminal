package io.github.kirya343.terminal.command.execution;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.github.kirya343.terminal.command.definition.CommandDefinition;
import io.github.kirya343.terminal.command.routing.CommandRoute;
import io.github.kirya343.terminal.command.routing.CommandRouter;
import io.github.kirya343.terminal.console.DiagnosticScreen;
import io.github.kirya343.terminal.result.StreamingResult;
import io.github.kirya343.terminal.result.TextResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class CommandExecutor {

    private final CommandRouter commandRouter;
    private final DiagnosticScreen screen;

    private Thread streamingThread;

    public void execute(String input) {

        List<String> tokens = Arrays.asList(input.trim().split("\\s+"));

        if (tokens.isEmpty() || tokens.size() == 0) {
            return;
        }

        Optional<CommandRoute> route = commandRouter.route(tokens);

        if (route.isEmpty()) {
            // Команда не найдена
            return;
        }

        execute(route.get());
    }
    
    /**
     * Выполняет диагностическую команду.
     */
    public void execute(CommandRoute route) {
        stopStreaming();
        screen.clearContent();

        try {

            CommandDefinition command = route.command();

            Method method = command.getMethod();

            long time = System.nanoTime();

            Object result = method.invoke(command.getInstance());

            long resultTime = System.nanoTime() - time;

            if (method.getReturnType() == void.class) {
                screen.setContent(
                    List.of("Ok, response: " + resultTime / 1_000_000 + "ms")
                );

                return;
            }

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
