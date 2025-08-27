package calculator;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
     public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Smart calculator. Type /help for instructions.");

        while (true) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue; // skip empty lines
            if (input.equals("/exit")) {
                System.out.println("Bye!");
                break;
            }
            if (input.equals("/help")) {
                System.out.println("The program calculates expressions with + and - operators. " +
                        "It supports multiple consecutive operators (e.g., '++', '--', '---').");
                continue;
            }

            try {
                int result = calculateExpression(input);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("Invalid expression");
            }
        }
    }

    private static int calculateExpression(String input) {
        // Step 1: Normalize operators
        String normalized = normalizeOperators(input);

        // Step 2: Split into numbers and operators
        String[] tokens = normalized.split(" ");

        int total = 0;
        String operator = "+"; // default operator for first number

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (token.equals("+") || token.equals("-")) {
                operator = token; // update current operator
            } else { // must be a number
                int number = Integer.parseInt(token);
                total = operator.equals("+") ? total + number : total - number;
            }
        }
        return total;
    }

    private static String normalizeOperators(String input) {
        // Replace multiple + with single +
        String normalized = input.replaceAll("\\++", "+");

        // Replace sequences of - with + or - depending on even/odd count
        Pattern pattern = Pattern.compile("-+");
        Matcher matcher = pattern.matcher(normalized);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String replacement = (matcher.group().length() % 2 == 0) ? "+" : "-";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        normalized = sb.toString();

        // Add spaces around operators for splitting
        normalized = normalized.replaceAll("([+-])", " $1 ");
        normalized = normalized.replaceAll("\\s+", " ").trim();

        return normalized;
    }

}
