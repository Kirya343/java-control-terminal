package io.github.kirya343.terminal.api.component;

import lombok.extern.slf4j.Slf4j;
import java.util.List;

import io.github.kirya343.terminal.formatting.DiagnosticTextBuilder;
import io.github.kirya343.terminal.logging.DiagnosticLogging;
import io.github.kirya343.terminal.logging.LogBuffer;
import io.github.kirya343.terminal.logging.LogCollector;
import io.github.kirya343.terminal.logging.LogEntry;

@Slf4j 
public abstract class DiagnosticLoggable {

    private final LogBuffer logBuffer = new LogBuffer(50);
    private final LogCollector collector = new LogCollector(logBuffer);

    protected DiagnosticLoggable() {
        DiagnosticLogging.register(getClass(), collector);
    }

    public List<LogEntry> getLogs() {
        return logBuffer.getEntries();
    }

    public DiagnosticTextBuilder getFormattedLogs() {
        DiagnosticTextBuilder builder = new DiagnosticTextBuilder("Logs: ");

        logBuffer.getEntries().forEach(l -> builder.addRow(l.format()));

        return builder;
    }
}