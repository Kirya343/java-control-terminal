package io.github.kirya343.terminal.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import io.github.kirya343.terminal.api.command.DiagnosticCommand;
import io.github.kirya343.terminal.api.component.DiagnosticComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public final class DiagnosticComponentScanner {

    private DiagnosticComponentScanner() {
    }

    public static List<DiagnosticComponent> scanComponents(String basePackage) {

        return scan(basePackage, DiagnosticComponent.class);
    }

    public static List<DiagnosticCommand> scanCommands(String basePackage) {

        return scan(basePackage, DiagnosticCommand.class);
    }

    private static <T> List<T> scan(
            String basePackage,
            Class<T> componentType
    ) {
        ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();

        List<T> components = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph()
                .enableClassLoaders(classLoader)
                .enableClassInfo()
                .acceptPackages(basePackage)
                .scan()) {

            List<ClassInfo> implementations =
                    scanResult.getAllClassesImplementing(componentType.getName());

            for (ClassInfo classInfo : implementations) {
                String className = classInfo.getName();

                try {
                    Class<?> cls = Class.forName(
                            className,
                            false,
                            classLoader
                    );

                    if (cls.isInterface()
                            || Modifier.isAbstract(cls.getModifiers())) {
                        continue;
                    }

                    Constructor<?> constructor;

                    try {
                        constructor = cls.getConstructor();
                    } catch (NoSuchMethodException e) {
                        log.debug(
                                "[Scanner] Skipped (no public no-arg constructor): "
                                        + className
                        );
                        continue;
                    }

                    T component = componentType.cast(
                            constructor.newInstance()
                    );

                    components.add(component);

                    log.debug(
                            "[Scanner] Registered: " + className
                    );

                } catch (ReflectiveOperationException e) {
                    log.error(
                        "[Scanner] Failed to instantiate: " + className
                    );
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.err.println("[Scanner] Scan failed");
            e.printStackTrace();
        }

        return components;
    }
}
