package io.github.kirya343.terminal.runtime;

import io.github.kirya343.terminal.console.DiagnosticTerminal;
import io.github.kirya343.terminal.core.DiagnosticManager;

public record TerminalInstance(
    DiagnosticTerminal terminal,
    DiagnosticManager manager
) {
}