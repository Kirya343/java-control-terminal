package org.kirya343.terminal.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import org.kirya343.terminal.components.DiagnosticCommandProvider;

public final class ApplicationLauncher {

    private ApplicationLauncher() {
    }

    public static void launch(
            Class<?> applicationClass,
            String[] args,
            Consumer<DiagnosticCommandProvider> registerProvider
    ) {
        try {
            Method main = applicationClass.getMethod(
                "main",
                String[].class
            );

            if (!Modifier.isStatic(
                    main.getModifiers()
            )) {
                throw new IllegalArgumentException(
                    "Application main method must be static"
                );
            }

            main.invoke(null, (Object) args);

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
        }
    }
}