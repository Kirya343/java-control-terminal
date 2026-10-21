package org.kirya343.terminal.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.RequiredArgsConstructor;
import org.kirya343.terminal.logging.logback.DiagnosticLogAppender;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class DiagnosticLoggingConfigurer {

    private final LogCollector collector;

    private DiagnosticLogAppender appender;
    private Logger rootLogger;

    public void configure() {
        if (appender != null) {
            return;
        }

        if (!(LoggerFactory.getILoggerFactory()
                instanceof LoggerContext context)) {
            return;
        }

        rootLogger = context.getLogger(
            Logger.ROOT_LOGGER_NAME
        );

        appender = new DiagnosticLogAppender(collector);
        appender.setContext(context);
        appender.setName("DIAGNOSTICS");
        appender.start();

        rootLogger.addAppender(appender);
    }

    public void stop() {
        if (appender == null) {
            return;
        }

        rootLogger.detachAppender(appender);
        appender.stop();

        appender = null;
        rootLogger = null;
    }
}