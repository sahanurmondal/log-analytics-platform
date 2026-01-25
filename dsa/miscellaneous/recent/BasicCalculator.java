package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Basic Calculator
 * 
 * Description:
 * Implement a basic calculator to evaluate a simple expression string.
 * The expression string may contain +, -, (, ), and non-negative integers.
 * 
 * Companies: Google, Facebook, Amazon
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class BasicCalculator {

    public int calculate(String s) {
        Stack<Integer> stack = new Stack<>();
        int result = 0;
        int number = 0;
        int sign = 1;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                number = number * 10 + (c - '0');
            }

            if (c == '+' || c == '-' || c == ')' || i == s.length() - 1) {
                result += sign * number;
                number = 0;
                sign = (c == '+') ? 1 : -1;
            }

            if (c == '(') {
                stack.push(result);
                stack.push(sign);
                result = 0;
                sign = 1;
            }

            if (c == ')') {
                result = result * stack.pop() + stack.pop();
            }
        }

        return result;
    }

    // Stack-based with proper operator precedence and parentheses
    public static int calculateAdvanced(String s) {
        s = s.replaceAll(" ", "");
        Stack<Integer> stack = new Stack<>();
        int num = 0;
        char prevOp = '+';

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }

            // Handle opening parenthesis - evaluate sub-expression recursively
            if (c == '(') {
                // Find matching closing parenthesis
                int count = 1, j = i + 1;
                while (j < s.length() && count > 0) {
                    if (s.charAt(j) == '(') count++;
                    if (s.charAt(j) == ')') count--;
                    j++;
                }
                // Recursively evaluate expression inside parentheses
                num = calculateAdvanced(s.substring(i + 1, j - 1));
                i = j - 1; // Skip to after closing parenthesis
            }

            // Process operator or end of string
            if (!Character.isDigit(c) && c != '(' || i == s.length() - 1) {
                // Apply previous operator to num (respecting precedence)
                if (prevOp == '+') stack.push(num);
                else if (prevOp == '-') stack.push(-num);
                else if (prevOp == '*') stack.push(stack.pop() * num);
                else if (prevOp == '/') stack.push(stack.pop() / num);

                // Set next operator
                if (i < s.length() && (c == '+' || c == '-' || c == '*' || c == '/')) {
                    prevOp = c;
                }
                num = 0;
            }
        }

        // Sum all values in stack
        int result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }
        return result;
    }

    public static void main(String[] args) {
        BasicCalculator solution = new BasicCalculator();

        System.out.println("=== Basic Calculator Tests ===\n");

        System.out.println("--- Basic addition and subtraction ---");
        System.out.println("\"1 + 1\" = " + solution.calculate("1 + 1") + " (expected: 2)");
        System.out.println("\" 2-1 + 2 \" = " + solution.calculate(" 2-1 + 2 ") + " (expected: 3)");

        System.out.println("\n--- Advanced with *, /, and parentheses ---");
        System.out.println("\"(1+(4+5+2)-3)+(6+8)\" = " + solution.calculateAdvanced("(1+(4+5+2)-3)+(6+8)") + " (expected: 23)");
        System.out.println("\"3+2*2\" = " + solution.calculateAdvanced("3+2*2") + " (expected: 7)");
        System.out.println("\"(1+2)*3\" = " + solution.calculateAdvanced("(1+2)*3") + " (expected: 9)");
        System.out.println("\"9/3 + (1+2)\" = " + solution.calculateAdvanced("9/3 + (1+2)") + " (expected: 6)");
        System.out.println("\" 3/2 \" = " + solution.calculateAdvanced(" 3/2 ") + " (expected: 1)");
        System.out.println("\" 3+5 / 2 \" = " + solution.calculateAdvanced(" 3+5 / 2 ") + " (expected: 5)");
        System.out.println("\"2*(3+4)\" = " + solution.calculateAdvanced("2*(3+4)") + " (expected: 14)");
        System.out.println("\"(10-5)*2\" = " + solution.calculateAdvanced("(10-5)*2") + " (expected: 10)");
        System.out.println("\"3*(2+5)/7\" = " + solution.calculateAdvanced("3*(2+5)/7") + " (expected: 3)");
    }
}
