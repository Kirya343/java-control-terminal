package org.kirya343.terminal.logging;

import java.time.Instant;

public record LogEntry(
    Instant timestamp,
    String level,
    String loggerName,
    String threadName,
    String message,
    Throwable throwable
) {
}