package org.kirya343.terminal.logging;

public class LogCollector {

    private final LogBuffer logBuffer;

    public LogCollector(LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    public void collect(LogEntry entry) {
        logBuffer.add(entry);
    }
}
