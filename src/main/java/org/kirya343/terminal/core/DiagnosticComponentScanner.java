package org.kirya343.terminal.core;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.kirya343.terminal.commands.DiagnosticCommand;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public final class DiagnosticComponentScanner {

    private DiagnosticComponentScanner() {
    }

    public static List<DiagnosticCommand> scanCommands(String basePackage) {
        return getInstances(basePackage, DiagnosticCommand.class);
    }

    public static List<DiagnosticComponent> scanComponents(String basePackage) {
        return getInstances(basePackage, DiagnosticComponent.class);
    }

    public static DiagnosticManager scan(String basePackage) {
        List<DiagnosticCommand> commands = scanCommands(basePackage);

        List<DiagnosticComponent> components = scanComponents(basePackage);

        System.out.println("Loaded diagnostic commands: " + commands.size());

        for (DiagnosticCommand command : commands) {
            System.out.println(" - " + command.getClass().getName());
        }

        System.out.println("Loaded diagnostic components: " + components.size());

        for (DiagnosticComponent component : components) {
            System.out.println(" - " + component.getClass().getName());
        }

        return new DiagnosticManager(commands, components);
    }

    public static <T> List<T> getInstances(
            String basePackage,
            Class<T> targetType
    ) {
        List<T> instances = new ArrayList<>();

        try (ScanResult result = new ClassGraph()
                .acceptPackages(basePackage)
                .enableClassInfo()
                .verbose()
                .scan()) {

            System.out.println("Base package: " + basePackage);
            System.out.println("Target type: " + targetType.getName());
            System.out.println("Found classes: " + result.getAllClasses().size());

            ClassInfo targetInfo = result.getClassInfo(targetType.getName());

            if (targetInfo == null) {
                return instances;
            }

            for (ClassInfo classInfo : result.getAllClasses()
                    .getAssignableTo(targetInfo)) {

                System.out.println(classInfo.getName());

                if (classInfo.isInterface()
                        || Modifier.isAbstract(classInfo.getModifiers())) {
                    continue;
                }

                try {
                    Class<?> loadedClass = Class.forName(
                        classInfo.getName(),
                        true,
                        Thread.currentThread().getContextClassLoader()
                    );

                    T instance = loadedClass
                        .asSubclass(targetType)
                        .getDeclaredConstructor()
                        .newInstance();

                    instances.add(instance);

                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(
                        "Failed to instantiate: " + classInfo.getName(),
                        e
                    );
                }
            }
        }

        return instances;
    }
}
