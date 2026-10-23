package org.kirya343.terminal.components;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.kirya343.terminal.components.commands.DiagnosticCommand;
import org.kirya343.terminal.components.commands.HelpCommand;
import org.kirya343.terminal.components.commands.TerminalCommand;
import org.kirya343.terminal.core.DiagnosticManager;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public final class DiagnosticComponentScanner {

    private DiagnosticComponentScanner() {
    }

    public static DiagnosticManager scan(String basePackage) {
        List<Class<? extends DiagnosticCommand>> commandClasses =
                findCommandClasses(basePackage);

        List<Class<? extends DiagnosticComponent>> componentClasses =
                findComponentClasses(basePackage);

        List<DiagnosticCommand> commands = new ArrayList<>();
        List<DiagnosticComponent> components = new ArrayList<>();

        for (Class<? extends DiagnosticCommand> clazz : commandClasses) {
            if (clazz.equals(HelpCommand.class)) {
                continue;
            }

            commands.add(createInstance(clazz));
        }

        for (Class<? extends DiagnosticComponent> clazz : componentClasses) {
            components.add(createInstance(clazz));
        }

        commands.add(new HelpCommand(() -> commands, components));

        return new DiagnosticManager(commands, components);
    }

    private static List<Class<? extends DiagnosticCommand>> findCommandClasses(
            String basePackage
    ) {
        List<Class<? extends DiagnosticCommand>> classes = new ArrayList<>();

        try (ScanResult result = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackage)
                .scan()) {

            for (ClassInfo info : result.getClassesWithAnnotation(
                    TerminalCommand.class.getName())) {

                if (!info.implementsInterface(DiagnosticCommand.class.getName())) {
                    continue;
                }

                if (info.isAbstract() || info.isInterface()) {
                    continue;
                }

                try {
                    classes.add(Class.forName(
                            info.getName(),
                            false,
                            Thread.currentThread().getContextClassLoader()
                    ).asSubclass(DiagnosticCommand.class));
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(
                            "Cannot load command: " + info.getName(), e
                    );
                }
            }
        }

        return classes;
    }

    private static List<Class<? extends DiagnosticComponent>> findComponentClasses(
            String basePackage
    ) {
        List<Class<? extends DiagnosticComponent>> classes = new ArrayList<>();

        try (ScanResult result = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackage)
                .scan()) {

            for (ClassInfo info : result.getClassesWithAnnotation(
                    TerminalComponent.class.getName())) {

                if (!info.implementsInterface(DiagnosticComponent.class.getName())) {
                    continue;
                }

                if (info.isAbstract() || info.isInterface()) {
                    continue;
                }

                try {
                    classes.add(Class.forName(
                            info.getName(),
                            false,
                            Thread.currentThread().getContextClassLoader()
                    ).asSubclass(DiagnosticComponent.class));
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(
                            "Cannot load command: " + info.getName(), e
                    );
                }
            }
        }

        return classes;
    }

    private static <T> T createInstance(Class<? extends T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot create instance: " + clazz.getName(), e
            );
        }
    }
}
