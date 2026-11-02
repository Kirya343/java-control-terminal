package io.github.kirya343.terminal.runtime;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import io.github.kirya343.terminal.api.provider.DiagnosticCommandProvider;

public final class ApplicationLauncher {

    private ApplicationLauncher() {
    }

    public static void launch(
            Class<?> applicationClass,
            String[] args,
            Consumer<DiagnosticCommandProvider> registerProvider,
            ApplicationAdapter adapter,
            ApplicationLifecycle lifecycle
    ) {

        try {

            adapter.launch(
                applicationClass,
                args,
                registerProvider,
                lifecycle
            );

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "Application must declare public static void main(String[] args): "
                    + applicationClass.getName(),
                e
            );
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            throw new RuntimeException(
                "Application failed to start: "
                    + applicationClass.getName(),
                cause
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                "Failed to launch application: "
                    + applicationClass.getName(),
                e
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to launch application: "
                    + applicationClass.getName(),
                e
            );
        }
    }
}