package io.github.kirya343.terminal.command.execution;

import io.github.kirya343.terminal.command.routing.CommandRoute;

public interface CommandArgumentResolver {

    Object[] resolve(CommandRoute route);
}