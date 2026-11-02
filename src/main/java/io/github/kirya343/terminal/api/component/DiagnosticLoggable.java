package io.github.kirya343.terminal.api.component;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.github.kirya343.terminal.formatting.DiagnosticTextBuilder;

@Slf4j 
public abstract class DiagnosticLoggable {

    private static final int MAX_LOGS = 50;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<String> logs = new ArrayList<>();

    public void addLog(String format, Object... args) {
        String newLog = String.format(format, args);

        log.debug(newLog);

        String timestamp = LocalDateTime.now().format(FORMATTER);

        logs.add(String.format("%s > %s", timestamp, newLog));

        if (logs.size() > MAX_LOGS) {
            logs.removeFirst();
        }
    }

    public DiagnosticTextBuilder getLogs() {
        DiagnosticTextBuilder builder = new DiagnosticTextBuilder("Logs: ");

        logs.forEach(l -> builder.addRow(l));

        return builder;
    }
}