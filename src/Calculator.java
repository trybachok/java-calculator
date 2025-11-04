import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Calculator {

    private double result = 0.0;
    private boolean hasResult = false;
    private boolean isRunning = true;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final int MAX_INPUT_LENGTH = 100;

    public void run() {
        System.out.println("Калькулятор запущен.");
        System.out.println("Введите число, операцию (+, -, *, /), 'C' для сброса, 'S' для выхода.");

        while (isRunning) {
            if (!hasResult) {
                Double first = readOperand("Введите первое число: ");
                if (!isRunning) break;
                if (first == null) continue;
                result = first;
                hasResult = true;
            } else {
                System.out.println("Текущий результат: " + result);
            }

            String operation = readOperation("Введите операцию (+, -, *, /) или команду (C, S): ");
            if (!isRunning) break;
            if (operation == null) continue;

            Double second = readOperand("Введите второе число: ");
            if (!isRunning) break;
            if (second == null) continue;

            performOperation(operation, second);
            System.out.println("Результат: " + result);
        }

        System.out.println("Калькулятор завершил работу.");
    }

    private String readLineWithPrompt(String prompt) {
        try {
            System.out.print(prompt);
            String line = reader.readLine();
            if (line == null) {
                exitCalculator();
                return null;
            }
            String trimmed = line.trim();

            if (trimmed.isEmpty() || trimmed.length() > MAX_INPUT_LENGTH) {
                System.out.println("Ошибка: введена слишком длинная или пустая строка.");
                drainBufferedInput();
                return "";
            }

            return trimmed;
        } catch (IOException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
            exitCalculator();
            return null;
        }
    }

    private Double readOperand(String prompt) {
        while (isRunning) {
            String input = readLineWithPrompt(prompt);
            if (!isRunning) return null;
            if (input == null || input.isEmpty()) continue;

            if (processCommand(input)) continue;

            if (!isAllowedInput(input, false)) {
                System.out.println("Ошибка: недопустимый ввод числа.");
                drainBufferedInput();
                continue;
            }

            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число или команду (C/S).");
                drainBufferedInput();
            }
        }
        return null;
    }

    private String readOperation(String prompt) {
        while (isRunning) {
            String input = readLineWithPrompt(prompt);
            if (!isRunning) return null;
            if (input == null || input.isEmpty()) continue;

            if (processCommand(input)) return null;

            // Проверка на латинские буквы для команд и допустимые символы операций
            if (!input.matches("[a-zA-Z]|[+\\-*/]")) {
                System.out.println("Ошибка: допускаются только латинские буквы для команд и символы операций (+, -, *, /).");
                continue;
            }

            if (!isAllowedInput(input, true)) {
                System.out.println("Ошибка: введена некорректная операция.");
                drainBufferedInput();
                continue;
            }

            return input;
        }
        return null;
    }

    private boolean processCommand(String input) {
        if (input.equalsIgnoreCase("C")) {
            resetCalculator();
            return true;
        }
        if (input.equalsIgnoreCase("S")) {
            exitCalculator();
            return true;
        }
        return false;
    }

    private void performOperation(String operation, double operand) {
        switch (operation) {
            case "+" -> result = add(result, operand);
            case "-" -> result = subtract(result, operand);
            case "*" -> result = multiply(result, operand);
            case "/" -> {
                if (operand == 0.0) {
                    System.out.println("Ошибка: деление на ноль невозможно.");
                } else {
                    result = divide(result, operand);
                }
            }
            default -> System.out.println("Ошибка: неподдерживаемая операция.");
        }
    }

    private double add(double a, double b) { return a + b; }
    private double subtract(double a, double b) { return a - b; }
    private double multiply(double a, double b) { return a * b; }
    private double divide(double a, double b) { return a / b; }

    private void resetCalculator() {
        result = 0.0;
        hasResult = false;
        System.out.println("Сброс: калькулятор возвращён в исходное состояние.");
    }

    private void exitCalculator() {
        System.out.println("Выход из калькулятора...");
        isRunning = false;
    }

    private boolean isAllowedInput(String input, boolean isOperation) {
        if (input == null || input.isBlank() || input.length() > MAX_INPUT_LENGTH) return false;
        input = input.trim();
        if (isOperation) {
            return input.matches("[+\\-*/]");
        } else {
            return input.matches("[+-]?\\d*(\\.\\d+)?");
        }
    }

    private void drainBufferedInput() {
        try {
            while (reader.ready()) {
                reader.readLine();
            }
        } catch (IOException ignored) { }
    }

    public static void main(String[] args) {
        new Calculator().run();
    }
}
