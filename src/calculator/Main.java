package calculator;

import java.math.BigInteger;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Smart calculator. Type /help for instructions.");

        Map<String, BigInteger> variables = new HashMap<>();

        outerLoop:
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            // Commands
            if (input.startsWith("/")) {
                switch (input) {
                    case "/exit":
                        System.out.println("Bye!");
                        break outerLoop;
                    case "/help":
                        System.out.println("Supports +, -, *, /, parentheses, multi-digit numbers, and variables.");
                        continue;
                    default:
                        System.out.println("Unknown command");
                        continue;
                }
            }

            // Variable assignment
            if (input.matches("^[a-zA-Z]+\\s*=\\s*(-?\\d+|[a-zA-Z][a-zA-Z0-9]*)$")) {
                String[] parts = input.split("=", 2);
                String key = parts[0].trim();
                String valueStr = parts[1].trim();

                if (valueStr.matches("-?\\d+")) {
                    variables.put(key, new BigInteger(valueStr));
                } else if (variables.containsKey(valueStr)) {
                    variables.put(key, variables.get(valueStr));
                } else {
                    System.out.println("Unknown variable");
                }
                continue;
            }

            // Single variable lookup
            if (input.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
                if (variables.containsKey(input)) {
                    System.out.println(variables.get(input));
                } else {
                    System.out.println("Unknown variable");
                }
                continue;
            }

            // Handle arithmetic expressions
            try {
                // Replace variables with values
                String expression = input;
                for (Map.Entry<String, BigInteger> entry : variables.entrySet()) {
                    expression = expression.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue().toString());
                }

                expression = expression.replaceAll("\\s+", ""); // remove spaces

                // Normalize + and -
                StringBuilder normalized = new StringBuilder();
                int i = 0;
                while (i < expression.length()) {
                    char c = expression.charAt(i);
                    if (c == '+' || c == '-') {
                        int minusCount = 0;
                        while (i < expression.length() && (expression.charAt(i) == '+' || expression.charAt(i) == '-')) {
                            if (expression.charAt(i) == '-') minusCount++;
                            i++;
                        }
                        normalized.append((minusCount % 2 == 0) ? '+' : '-');
                    } else {
                        normalized.append(c);
                        i++;
                    }
                }

                expression = normalized.toString();

                // Convert to postfix
                String postfix = infixToPostfix(expression);

                // Evaluate postfix
                String[] tokens = postfix.trim().split("\\s+");
                Stack<BigInteger> stack = new Stack<>();
                for (String token : tokens) {
                    if (token.matches("-?\\d+")) {
                        stack.push(new BigInteger(token));
                    } else {
                        BigInteger val1 = stack.pop();
                        BigInteger val2 = stack.pop();
                        switch (token) {
                            case "+": stack.push(val2.add(val1)); break;
                            case "-": stack.push(val2.subtract(val1)); break;
                            case "*": stack.push(val2.multiply(val1)); break;
                            case "/": stack.push(val2.divide(val1)); break;
                        }
                    }
                }
                System.out.println(stack.pop());

            } catch (Exception e) {
                System.out.println("Invalid expression");
            }
        }
        scanner.close();
    }

    // Infix to Postfix
    public static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stk = new Stack<>();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isDigit(c)) {
                while (i < infix.length() && Character.isDigit(infix.charAt(i))) {
                    postfix.append(infix.charAt(i));
                    i++;
                }
                postfix.append(' ');
                i--;
            } else if (c == '(') {
                stk.push(c);
            } else if (c == ')') {
                while (!stk.isEmpty() && stk.peek() != '(') {
                    postfix.append(stk.pop()).append(' ');
                }
                stk.pop();
            } else { // operator
                while (!stk.isEmpty() && precedence(stk.peek()) >= precedence(c)) {
                    postfix.append(stk.pop()).append(' ');
                }
                stk.push(c);
            }
        }

        while (!stk.isEmpty()) {
            postfix.append(stk.pop()).append(' ');
        }

        return postfix.toString();
    }

    public static int precedence(char op) {
        switch (op) {
            case '+': case '-': return 1;
            case '*': case '/': return 2;
            default: return 0;
        }
    }
}
