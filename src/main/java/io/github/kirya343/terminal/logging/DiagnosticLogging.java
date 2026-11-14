package io.github.kirya343.terminal.logging;

public final class DiagnosticLogging {

    private static DiagnosticLoggingConfigurer configurer;

    private DiagnosticLogging() {}

    public static void initialize(DiagnosticLoggingConfigurer configurer) {
        DiagnosticLogging.configurer = configurer;
    }

    public static void register(Class<?> type, LogCollector collector) {
        configurer.register(type, collector);
    }

    public static void unregister(Class<?> type) {
        configurer.unregister(type);
    }

    public static DiagnosticLoggingConfigurer getConfigurer() {
        if (configurer == null) {
            throw new IllegalStateException(
                "Diagnostic logging is not initialized"
            );
        }

        return configurer;
    }
}