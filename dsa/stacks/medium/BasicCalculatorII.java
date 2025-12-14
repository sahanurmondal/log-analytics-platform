package stacks.medium;

import java.util.Stack;

/**
 * LeetCode 227: Basic Calculator II
 *
 * Given a string s which represents an expression, evaluate this expression and return its value.
 * The integer division should truncate towards zero.
 * You may assume that the given expression is always valid. All intermediate results will be
 * in the range of [-2^31, 2^31 - 1].
 *
 * Note: You cannot use any built-in function which evaluates strings as mathematical expressions,
 * such as eval().
 *
 * Example 1:
 * Input: s = "3+2*2"
 * Output: 7
 *
 * Example 2:
 * Input: s = " 6/2 *3 "
 * Output: 9
 *
 * Example 3:
 * Input: s = "2-3/-4"
 * Output: 1 (Note: 2 - (-0.75) = 2.75, truncates to 2)
 *
 * Operators: +, -, *, /
 * Must handle operator precedence: * and / have higher precedence than + and -
 */
public class BasicCalculatorII {

    /**
     * Solution: Stack-based with operator precedence
     * Time: O(n), Space: O(n)
     *
     * Algorithm:
     * 1. Use a stack to handle numbers
     * 2. Track the last operator
     * 3. When we encounter:
     *    - '+' or '-': push the number to stack (or pop and push if operator)
     *    - '*' or '/': immediately apply to last number on stack
     * 4. Sum all numbers in stack
     */
    public int calculate(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        Stack<Integer> stack = new Stack<>();
        int num = 0;
        char operator = '+'; // Initialize with '+'

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // Build the number
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }

            // Process operator or end of string
            // Process if: not a digit, not a space, or last character
            if (!Character.isDigit(c) && c != ' ' || i == s.length() - 1) {
                switch (operator) {
                    case '+':
                        stack.push(num);
                        break;
                    case '-':
                        stack.push(-num);
                        break;
                    case '*':
                        stack.push(stack.pop() * num);
                        break;
                    case '/':
                        stack.push(stack.pop() / num);
                        break;
                }

                // Update operator for next iteration
                if (c == '+' || c == '-' || c == '*' || c == '/') {
                    operator = c;
                }

                // Reset num for next number
                num = 0;
            }
        }

        // Sum all numbers in stack
        int result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }

        return result;
    }

    /**
     * Alternative: More explicit version
     * Time: O(n), Space: O(n)
     */
    public int calculateV2(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        Stack<Integer> stack = new Stack<>();
        int num = 0;
        char sign = '+';

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            // Is it a digit?
            if (ch >= '0' && ch <= '9') {
                num = num * 10 + (ch - '0');
            }

            // Is it an operator or end of string?
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || i == s.length() - 1) {
                if (sign == '+') {
                    stack.push(num);
                } else if (sign == '-') {
                    stack.push(-num);
                } else if (sign == '*') {
                    stack.push(stack.pop() * num);
                } else if (sign == '/') {
                    stack.push(stack.pop() / num);
                }

                if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                    sign = ch;
                }
                num = 0;
            }
        }

        int result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }

        return result;
    }

    /**
     * Alternative: Tracking last number separately
     * Time: O(n), Space: O(n)
     */
    public int calculateV3(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        int num = 0;
        int lastNum = 0;
        int result = 0;
        char operator = '+';

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }

            if (c != ' ' && !Character.isDigit(c) || i == s.length() - 1) {
                switch (operator) {
                    case '+':
                        result += lastNum;
                        lastNum = num;
                        break;
                    case '-':
                        result += lastNum;
                        lastNum = -num;
                        break;
                    case '*':
                        lastNum *= num;
                        break;
                    case '/':
                        lastNum /= num;
                        break;
                }

                if (!Character.isDigit(c) && c != ' ') {
                    operator = c;
                }
                num = 0;
            }
        }

        result += lastNum;
        return result;
    }

    public static void main(String[] args) {
        BasicCalculatorII solution = new BasicCalculatorII();

        // Test case 1
        System.out.println("Input: \"3+2*2\"");
        System.out.println("Output: " + solution.calculate("3+2*2")); // 7

        // Test case 2
        System.out.println("\nInput: \" 6/2 *3 \"");
        System.out.println("Output: " + solution.calculate(" 6/2 *3 ")); // 9

        // Test case 3
        System.out.println("\nInput: \"2-3/-4\"");
        System.out.println("Output: " + solution.calculate("2-3/-4")); // 1

        // Test case 4
        System.out.println("\nInput: \"100\"");
        System.out.println("Output: " + solution.calculate("100")); // 100

        // Test case 5
        System.out.println("\nInput: \"2*3+4\"");
        System.out.println("Output: " + solution.calculate("2*3+4")); // 10

        // Test case 6
        System.out.println("\nInput: \"1-2*3\"");
        System.out.println("Output: " + solution.calculate("1-2*3")); // -5
    }
}

