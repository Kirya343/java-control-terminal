package org.kirya343.terminal.models;

import org.kirya343.terminal.core.StreamingDataProvider;

public record StreamingResult(
    StreamingDataProvider provider,
    StreamMode mode
) implements CommandResult {
}