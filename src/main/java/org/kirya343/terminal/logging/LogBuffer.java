package org.kirya343.terminal.logging;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class LogBuffer {

    private final int capacity;
    private final ArrayDeque<LogEntry> entries = new ArrayDeque<>();

    public LogBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Capacity must be greater than zero"
            );
        }

        this.capacity = capacity;
    }

    public synchronized void add(LogEntry entry) {
        if (entries.size() >= capacity) {
            entries.removeFirst();
        }

        entries.addLast(entry);
    }

    public synchronized List<LogEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public synchronized List<LogEntry> getEntries(int limit) {
        if (limit <= 0) {
            return List.of();
        }

        int skip = Math.max(0, entries.size() - limit);
        List<LogEntry> result = new ArrayList<>(Math.min(limit, entries.size()));

        int index = 0;

        for (LogEntry entry : entries) {
            if (index++ >= skip) {
                result.add(entry);
            }
        }

        return result;
    }

    public synchronized int size() {
        return entries.size();
    }

    public synchronized void clear() {
        entries.clear();
    }
}