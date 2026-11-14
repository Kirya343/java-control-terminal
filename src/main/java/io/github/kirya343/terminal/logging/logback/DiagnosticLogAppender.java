package io.github.kirya343.terminal.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.github.kirya343.terminal.logging.LogCollector;
import io.github.kirya343.terminal.logging.LogEntry;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor 
public class DiagnosticLogAppender extends AppenderBase<ILoggingEvent> {

    private final LogCollector globalCollector;
    private final Map<String, LogCollector> collectors = new ConcurrentHashMap<>();

    public void register(String loggerName, LogCollector collector) {
        collectors.put(loggerName, collector);
    }

    public void unregister(String loggerName) {
        collectors.remove(loggerName);
    }

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

        globalCollector.collect(entry);

        LogCollector collector = collectors.get(event.getLoggerName());

        if (collector != null) {
            collector.collect(entry);
        }
    }
}