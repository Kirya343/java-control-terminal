package org.kirya343.terminal.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.RequiredArgsConstructor;

import org.kirya343.terminal.logging.LogCollector;
import org.kirya343.terminal.logging.LogEntry;

import java.time.Instant;

@RequiredArgsConstructor 
public class DiagnosticLogAppender
        extends AppenderBase<ILoggingEvent> {

    private final LogCollector collector;
    
    @Override
    protected void append(ILoggingEvent event) {
        LogEntry entry = new LogEntry(
            Instant.ofEpochMilli(event.getTimeStamp()),
            event.getLevel().toString(),
            event.getLoggerName(),
            event.getThreadName(),
            event.getFormattedMessage(),
            null
        );

        collector.collect(entry);
    }
}