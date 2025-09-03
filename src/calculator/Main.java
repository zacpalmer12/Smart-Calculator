package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Smart calculator. Type /help for instructions.");

        // Move HashMap outside the loop so it persists between inputs
        Map<String, Integer> variables = new HashMap<>();

        outerLoop:
        while (true) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            // Commands
            if (input.startsWith("/")) {
                if (input.equals("/exit")) {
                    System.out.println("Bye!");
                    break;
                } else if (input.equals("/help")) {
                    System.out.println("The program calculates expressions with + and - operators.\n" +
                            "Supports multiple consecutive operators (e.g., '--' → '+').\n" +
                            "Supports variable assignment (e.g., 'a = 5' or 'b = a').\n" +
                            "Invalid sequences like '+ +' or '+ -' are rejected.");
                    continue;
                } else {
                    System.out.println("Unknown command");
                    continue;
                }
            }

            // Handle variable assignment: var = value or var = otherVar
            if (input.matches("^[a-zA-Z]+\\s*=\\s*(\\d+|[a-zA-Z][a-zA-Z0-9]*)$")) {
                String[] parts = input.split("=");
                String key = parts[0].trim();
                String valueStr = parts[1].trim();

                // Check if valueStr is a number
                if (valueStr.matches("\\d+")) {
                    int value = Integer.parseInt(valueStr);
                    variables.put(key, value);
//                    System.out.println(key + " = " + value);
                } else {
                    // It's another variable name
                    if (variables.containsKey(valueStr)) {
                        int value = variables.get(valueStr);
                        variables.put(key, value);
//                        System.out.println(key + " = " + valueStr + " (" + value + ")");
                    } else {
                        System.out.println("Unknown variable: " + valueStr);
                    }
                }
                continue;
            }

            // Handle variable lookup (single variable name)
            if (input.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
                if (variables.containsKey(input)) {
                    System.out.println(variables.get(input));
                } else {
                    System.out.println("Unknown variable");
                }
                continue;
            }

            // Handle arithmetic expressions (may contain variables)
            try {
                // First, replace variable names with their values
                String expressionWithValues = input;
                for (Map.Entry<String, Integer> entry : variables.entrySet()) {
                    expressionWithValues = expressionWithValues.replaceAll("\\b" + entry.getKey() + "\\b",
                            entry.getValue().toString());
                }

                // Remove all spaces and normalize operators
                String normalized = expressionWithValues.replaceAll("\\s+", "")
                        .replaceAll("--", "+")
                        .replaceAll("\\+\\+", "+")
                        .replaceAll("-\\+", "-")
                        .replaceAll("\\+-", "-");

                // Validate the expression
                if (!normalized.matches("^([+-]?\\d+[+-])*[+-]?\\d+$")) {
                    System.out.println("Invalid expression");
                    continue;
                }

                // Extract numbers with sign
                Pattern tokenPattern = Pattern.compile("[+-]?\\d+");
                Matcher tokenMatcher = tokenPattern.matcher(normalized);

                int total = 0;
                boolean hasToken = false;
                while (tokenMatcher.find()) {
                    hasToken = true;
                    total += Integer.parseInt(tokenMatcher.group());
                }

                if (!hasToken) {
                    System.out.println("Invalid input");
                    continue;
                }

                System.out.println(total);

            } catch (Exception e) {
                System.out.println("Invalid expression");
            }
        }
        scanner.close();
    }
}