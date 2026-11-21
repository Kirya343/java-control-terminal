package io.github.kirya343.terminal.runtime;

import io.github.kirya343.terminal.command.registry.CommandRegistry;
import io.github.kirya343.terminal.command.registry.CommandScanner;
import io.github.kirya343.terminal.console.DiagnosticTerminal;

public record TerminalInstance(
    DiagnosticTerminal terminal,
    CommandRegistry registry,
    CommandScanner scanner
) {
}