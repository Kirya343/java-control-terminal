package io.github.kirya343.terminal.command.registration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocalCommandInstanceProvider
        implements CommandInstanceProvider {

    private final Map<Class<?>, Object> instances = new HashMap<>();

    public <T> void register(Class<T> commandClass, T instance) {
        instances.put(commandClass, commandClass.cast(instance));
    }

    @Override
    public <T> T getInstance(Class<T> commandClass) {
        Object instance = instances.get(commandClass);

        if (instance == null) {
            throw new IllegalStateException(
                "No instance registered for: " + commandClass.getName()
            );
        }

        return commandClass.cast(instance);
    }

    @Override
    public Collection<Object> getInstances() {
        return List.copyOf(instances.values());
    }
}