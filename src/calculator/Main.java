package calculator;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Smart calculator. Type /help for instructions.");

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
                            "Invalid sequences like '+ +' or '+ -' are rejected.");
                    continue;
                } else {
                    System.out.println("Unknown command");
                    continue;
                }
            }

            // Remove all spaces
            String normalized = input.replaceAll("\\s*(?=[+-])|(?<=[+-])\\s*", "");

            // Only digits and + - allowed
            if (!normalized.matches("[0-9+-]+")) {
                System.out.println("Invalid input");
                continue;
            }
            if (normalized.matches(".*[+-]$")) {
                System.out.println("Invalid input");
                continue;
            }

            normalized = normalized.replaceAll("--", "+");

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
        }
    }
}
