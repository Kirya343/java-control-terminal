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

    public static DiagnosticManager scan(String basePackage) {
        List<DiagnosticCommand> commands = getInstances(basePackage, DiagnosticCommand.class);
        List<DiagnosticComponent> components = getInstances(basePackage, DiagnosticComponent.class);

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
                .scan()) {

            ClassInfo targetInfo = result.getClassInfo(targetType.getName());

            if (targetInfo == null) {
                return instances;
            }

            for (ClassInfo classInfo : result.getAllClasses()
                    .getAssignableTo(targetInfo)) {

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
