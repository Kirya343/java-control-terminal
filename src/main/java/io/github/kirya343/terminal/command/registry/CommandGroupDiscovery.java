package io.github.kirya343.terminal.command.registry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import io.github.kirya343.terminal.api.annotations.CommandGroup;
import io.github.kirya343.terminal.command.definition.CommandGroupDefinition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CommandGroupDiscovery {

    private final CommandScanner scanner;

    public CommandGroupDiscovery(CommandScanner scanner) {
        this.scanner = scanner;
    }

    public List<CommandGroupDefinition> discover(String basePackage) {
        ClassLoader classLoader =
            Thread.currentThread().getContextClassLoader();

        List<CommandGroupDefinition> definitions = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph()
            .enableClassLoaders(classLoader)
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(basePackage)
            .scan()) {

            List<ClassInfo> commandGroups =
                scanResult.getClassesWithAnnotation(
                    CommandGroup.class.getName()
                );

            for (ClassInfo classInfo : commandGroups) {
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

                    if (cls.isMemberClass()
                        && !Modifier.isStatic(cls.getModifiers())) {
                        log.debug(
                            "[Discovery] Skipped non-static nested class: {}",
                            className
                        );
                        continue;
                    }

                    Constructor<?> constructor;

                    try {
                        constructor = cls.getConstructor();
                    } catch (NoSuchMethodException e) {
                        log.debug(
                            "[Discovery] Skipped (no public no-arg constructor): {}",
                            className
                        );
                        continue;
                    }

                    Object instance = constructor.newInstance();

                    CommandGroupDefinition definition =
                        scanner.scan(instance);

                    definitions.add(definition);

                    log.debug("[Discovery] Discovered: {}", className);

                } catch (ReflectiveOperationException e) {
                    log.error(
                        "[Discovery] Failed to instantiate: {}",
                        className,
                        e
                    );
                }
            }

        } catch (Exception e) {
            log.error("[Discovery] Scan failed", e);
        }

        return definitions;
    }
}
