package org.kirya343.terminal.runtime;

public final class TerminalRuntime {

    public static void launch(
            Class<?> applicationClass,
            String[] args
    ) {
        if (!applicationClass.isAnnotationPresent(
                TerminalManaged.class
        )) {
            throw new IllegalArgumentException(
                "Application must have @TerminalManaged"
            );
        }

        ApplicationLauncher.launch(applicationClass, args);
    }
}