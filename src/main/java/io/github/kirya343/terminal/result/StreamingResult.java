package io.github.kirya343.terminal.result;

import io.github.kirya343.terminal.api.provider.StreamingDataProvider;

public record StreamingResult(
    StreamingDataProvider provider,
    StreamMode mode
) implements CommandResult {
}