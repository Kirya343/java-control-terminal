package io.github.kirya343.terminal.logging;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record LogEntry(
    Instant timestamp,
    String level,
    String loggerName,
    String threadName,
    String message,
    Throwable throwable
) {
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");

    public String format() {
        return "%s [%s] %-5s %s - %s".formatted(
            FORMATTER.format(timestamp.atZone(ZoneId.systemDefault())),
            threadName,
            level,
            loggerName.length() > 36
                ? loggerName.substring(loggerName.length() - 36)
                : loggerName,
            message
        );
    }
}