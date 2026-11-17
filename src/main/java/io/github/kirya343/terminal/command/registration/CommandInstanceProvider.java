package io.github.kirya343.terminal.command.registration;

import java.util.Collection;

public interface CommandInstanceProvider {

    <T> T getInstance(Class<T> commandClass);

    Collection<Object> getInstances();
}