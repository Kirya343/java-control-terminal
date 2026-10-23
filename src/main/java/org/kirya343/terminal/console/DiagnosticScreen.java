package org.kirya343.terminal.console;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.jline.terminal.Terminal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DiagnosticScreen {

    private static final int MAX_CONTENT_ENTRIES = 1000;
    private static final int MIN_TERMINAL_WIDTH = 10;

    private final Terminal terminal;
    private final DiagnosticScreenRenderer renderer;
    private final DiagnosticContentLayout contentLayout;

    private final Deque<String> content = new ArrayDeque<>();

    private int scrollOffset = 0;

    private String input = "";

    @Getter 
    private int cursorPosition = 0;
    private String status = "ONLINE";

    private int contentHeight() {
        return Math.max(1, terminal.getSize().getRows() - 7);
    }

    private int terminalWidth() {
        return Math.max(MIN_TERMINAL_WIDTH, terminal.getSize().getColumns());
    }

    public synchronized void render() {
        List<String> allContent = getWrappedContent();

        int height = contentHeight();

        clampScrollOffset(allContent.size(), height);

        int end = allContent.size() - scrollOffset;
        int start = Math.max(0, end - height);

        List<String> visibleContent = new ArrayList<>(
            allContent.subList(start, end)
        );

        DiagnosticScreenFrame frame = new DiagnosticScreenFrame(
            status,
            input,
            visibleContent,
            height
        );

        renderer.render(frame);
        
        terminal.writer().print(
            "\033[" + (terminal.getSize().getRows() - 2) + ";" + (cursorPosition + 5) + "H"
        );

        terminal.flush();
    }

    public synchronized void addLines(List<String> newLines) {
        if (newLines == null || newLines.isEmpty()) {
            return;
        }

        boolean followOutput = scrollOffset == 0;

        // Считаем количество визуальных строк новых записей.
        int addedVisualLines = contentLayout
            .wrap(newLines, terminalWidth() - 3)
            .size();

        for (String line : newLines) {
            content.addLast(line == null ? "null" : line);
        }

        while (content.size() > MAX_CONTENT_ENTRIES) {
            content.removeFirst();
        }

        if (followOutput) {
            scrollOffset = 0;
        } else {
            // Сохраняем положение просмотра при поступлении новых строк.
            scrollOffset += addedVisualLines;
        }

        render();
    }

    public synchronized void moveCursor(int move) {
        int newPos = cursorPosition + move;

        if (newPos < 0) newPos = 0;
        if (newPos > input.length()) newPos = input.length();

        cursorPosition = newPos;

        render();
    }

    public synchronized void setContent(List<String> newContent) {
        content.clear();

        if (newContent != null) {
            int start = Math.max(
                0,
                newContent.size() - MAX_CONTENT_ENTRIES
            );

            for (int i = start; i < newContent.size(); i++) {
                String line = newContent.get(i);

                content.addLast(line == null ? "null" : line);
            }
        }

        render();
    }

    public synchronized void clearContent() {
        content.clear();
        scrollOffset = 0;

        render();
    }

    public synchronized void setInput(String input) {

        // reset input and positions
        if (input == null) {
            this.input = "";
            this.cursorPosition = 0;
        } else {
            if (cursorPosition == this.input.length()) {
                cursorPosition = input.length();
            }

            this.input = input;
        }
        this.input = input == null ? "" : input;

        render();
    }

    public synchronized void setStatus(String status) {
        this.status = status == null ? "ONLINE" : status;

        render();
    }

    public synchronized void scrollUp(int amount) {
        if (amount <= 0) {
            return;
        }

        int maxOffset = getMaxScrollOffset();

        scrollOffset = (int) Math.min(
            maxOffset,
            (long) scrollOffset + amount
        );

        render();
    }

    public synchronized void scrollDown(int amount) {
        if (amount <= 0) {
            return;
        }

        scrollOffset = Math.max(0, scrollOffset - amount);

        render();
    }

    private List<String> getWrappedContent() {
        return contentLayout.wrap(
            new ArrayList<>(content),
            terminalWidth() - 3
        );
    }

    private int getMaxScrollOffset() {
        return Math.max(
            0,
            getWrappedContent().size() - contentHeight()
        );
    }

    private void clampScrollOffset(int totalLines, int height) {
        scrollOffset = Math.max(
            0,
            Math.min(scrollOffset, totalLines - height)
        );
    }
}