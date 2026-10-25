package io.github.kirya343.terminal.core;

import java.util.Collection;
import java.util.Optional;

import io.github.kirya343.terminal.components.DiagnosticComponent;

public interface DiagnosticRegistry {

    void register(DiagnosticComponent component);

    Optional<DiagnosticComponent> find(String diagnosticId);

    Collection<DiagnosticComponent> findAll();
}
