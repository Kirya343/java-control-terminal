package org.kirya343.terminal;

import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.kirya343.terminal.core.TerminalCommandExecutor;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor 
public class DiagnosticTerminal {

    private final Terminal terminal;
    private final DiagnosticScreen screen;
    private final TerminalCommandExecutor commandExecutor;

    private final List<String> commandHistory = new ArrayList<>();

    private int historyIndex = 0;
    private String savedInput = "";

    public void start() throws IOException {
        StringBuilder input = new StringBuilder();
        Attributes originalAttributes = terminal.enterRawMode();

        try {
            terminal.puts(InfoCmp.Capability.enter_ca_mode);
            terminal.writer().print("\033[?25h");

            terminal.flush();

            //screen.render();

            while (true) {
                int key = terminal.reader().read(100);

                // EOF или Ctrl+C.
                if (key == -1 || key == 3) {
                    break;
                }

                // Тайм-аут чтения.
                if (key == -2) {
                    continue;
                }

                // ANSI escape-последовательности.
                if (key == 27) {
                    handleEscapeSequence(input);
                    continue;
                }

                // Enter.
                if (key == '\r' || key == '\n') {
                    String command = input.toString().trim();
                    input.setLength(0);

                    if (command.equalsIgnoreCase("exit")) {
                        break;
                    }

                    if (!command.isEmpty()) {
                        // Не сохраняем подряд одинаковые команды.
                        if (commandHistory.isEmpty()
                            || !commandHistory.get(
                                commandHistory.size() - 1
                            ).equals(command)) {
                            commandHistory.add(command);
                        }

                        historyIndex = commandHistory.size();
                        savedInput = "";

                        screen.clearContent();
                        commandExecutor.executeCommand(command);
                    }

                    screen.setInput("");
                    continue;
                }

                int cursorPosition = screen.getCursorPosition();

                // Backspace.
                
                if (key == 127 || key == 8) {
                    // Backspace: удалить символ слева от курсора.
                    if (cursorPosition > 0) {
                        input.deleteCharAt(cursorPosition - 1);
                        screen.moveCursor(-1);
                    }
                } else if (key >= 32 && key <= 126) {
                    // Вставить символ в позицию курсора.
                    input.insert(cursorPosition, (char) key);
                    screen.moveCursor(1);
                }

                screen.setInput(input.toString());
            }
        } finally {
            terminal.puts(InfoCmp.Capability.exit_ca_mode);
            terminal.flush();
            terminal.setAttributes(originalAttributes);
            terminal.flush();
        }
    }

    /**
     * Читает ANSI escape-последовательность и передаёт её
     * обработчику клавиш.
     */
    private void handleEscapeSequence(StringBuilder input)
        throws IOException {

        int next = terminal.reader().read(100);

        // Одиночный Escape или неполная последовательность.
        if (next < 0 || next != '[') {
            return;
        }

        StringBuilder sequence = new StringBuilder("\033[");

        while (true) {
            int key = terminal.reader().read(100);

            if (key < 0) {
                return;
            }

            sequence.append((char) key);

            // Конечный символ ANSI-последовательности.
            if (key >= 0x40 && key <= 0x7E) {
                break;
            }
        }

        handleKeySequence(sequence.toString(), input);
    }

    /**
     * Обрабатывает историю команд и прокрутку содержимого.
     */
    private void handleKeySequence(
        String sequence,
        StringBuilder input
    ) {
        switch (sequence) {
            // Стрелка вверх: предыдущая команда.
            case "\033[A" -> {
                if (commandHistory.isEmpty()) {
                    return;
                }

                // Сохраняем ввод, существовавший до просмотра истории.
                if (historyIndex == commandHistory.size()) {
                    savedInput = input.toString();
                }

                if (historyIndex > 0) {
                    historyIndex--;

                    replaceInput(
                        input,
                        commandHistory.get(historyIndex)
                    );
                }
            }

            // Стрелка вниз: следующая команда.
            case "\033[B" -> {
                if (historyIndex < commandHistory.size() - 1) {
                    historyIndex++;

                    replaceInput(
                        input,
                        commandHistory.get(historyIndex)
                    );
                } else if (historyIndex < commandHistory.size()) {
                    // Выходим из истории и восстанавливаем ввод.
                    historyIndex = commandHistory.size();

                    replaceInput(input, savedInput);
                }
            }

            case "\033[D" -> {
                screen.moveCursor(-1);
            }

            // Стрелка вправо.
            case "\033[C" -> {
                screen.moveCursor(1);
            }

            // Page Up: прокрутка вверх на несколько строчек.
            case "\033[5~" ->
                screen.scrollUp(3);

            // Page Down: прокрутка вниз на несколько строчек.
            case "\033[6~" ->
                screen.scrollDown(3);

            default -> {
                // Неизвестные последовательности игнорируются.
            }
        }
    }

    /**
     * Заменяет одновременно буфер ввода и отображаемый текст.
     */
    private void replaceInput(
        StringBuilder input,
        String value
    ) {
        input.setLength(0);
        input.append(value);

        screen.setInput(value);
    }
}