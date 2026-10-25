package io.github.kirya343.terminal.models;

import io.github.kirya343.terminal.core.StreamingDataProvider;

public record StreamingResult(
    StreamingDataProvider provider,
    StreamMode mode
) implements CommandResult {
}