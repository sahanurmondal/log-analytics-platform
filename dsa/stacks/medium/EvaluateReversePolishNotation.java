package stacks.medium;

import java.util.Stack;

/**
 * LeetCode 150: Evaluate Reverse Polish Notation
 *
 * Evaluate the value of an arithmetic expression in Reverse Polish Notation.
 * Valid operators are +, -, *, and /. Each operand may be an integer or another expression.
 * Division between two integers should truncate towards zero.
 *
 * Example 1:
 * Input: tokens = ["2","1","+","3","*"]
 * Output: 9
 * Explanation: ((2 + 1) * 3) = 9
 *
 * Example 2:
 * Input: tokens = ["4","13","5","/","+"]
 * Output: 6
 * Explanation: (4 + (13 / 5)) = (4 + 2) = 6
 *
 * Reverse Polish Notation (RPN) is a mathematical notation in which operators follow operands.
 * So "3 4 +" means "3 + 4" in infix notation.
 */
public class EvaluateReversePolishNotation {

    /**
     * Solution: Stack-based evaluation
     * Time: O(n), Space: O(n)
     *
     * Algorithm:
     * 1. Scan tokens from left to right
     * 2. If token is a number, push to stack
     * 3. If token is an operator:
     *    - Pop two operands
     *    - Apply operator (second operand is the first popped element)
     *    - Push result back
     * 4. Final result is the only element in stack
     */
    public int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                // Pop two operands (order matters!)
                int num2 = stack.pop();  // Second operand
                int num1 = stack.pop();  // First operand

                int result = applyOperation(token, num1, num2);
                stack.push(result);
            } else {
                // It's a number
                stack.push(Integer.parseInt(token));
            }
        }

        return stack.pop();
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") ||
               token.equals("*") || token.equals("/");
    }

    private int applyOperation(String operator, int num1, int num2) {
        switch (operator) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                // Division truncates towards zero
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    /**
     * Alternative: Using single character operator detection
     * Time: O(n), Space: O(n)
     */
    public int evalRPNV2(String[] tokens) {
        Stack<Integer> stack = new Stack<>();

        for (String token : tokens) {
            if (token.length() > 1 || Character.isDigit(token.charAt(0))) {
                // It's a number
                stack.push(Integer.parseInt(token));
            } else {
                // It's an operator
                int b = stack.pop();
                int a = stack.pop();

                int result;
                switch (token) {
                    case "+":
                        result = a + b;
                        break;
                    case "-":
                        result = a - b;
                        break;
                    case "*":
                        result = a * b;
                        break;
                    case "/":
                        result = a / b;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operator");
                }

                stack.push(result);
            }
        }

        return stack.peek();
    }

    /**
     * Alternative: Using Lambda for operation
     * Time: O(n), Space: O(n)
     */
    public int evalRPNV3(String[] tokens) {
        Stack<Integer> stack = new Stack<>();

        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") ||
                token.equals("*") || token.equals("/")) {

                int b = stack.pop();
                int a = stack.pop();

                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            } else {
                stack.push(Integer.parseInt(token));
            }
        }

        return stack.pop();
    }

    public static void main(String[] args) {
        EvaluateReversePolishNotation solution = new EvaluateReversePolishNotation();

        // Test case 1: ["2","1","+","3","*"] = ((2+1)*3) = 9
        String[] tokens1 = {"2", "1", "+", "3", "*"};
        System.out.println("Input: [\"2\",\"1\",\"+\",\"3\",\"*\"]");
        System.out.println("Output: " + solution.evalRPN(tokens1)); // 9

        // Test case 2: ["4","13","5","/","+"] = (4+(13/5)) = 6
        String[] tokens2 = {"4", "13", "5", "/", "+"};
        System.out.println("\nInput: [\"4\",\"13\",\"5\",\"/\",\"+\"]");
        System.out.println("Output: " + solution.evalRPN(tokens2)); // 6

        // Test case 3: ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
        String[] tokens3 = {"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"};
        System.out.println("\nComplex expression:");
        System.out.println("Output: " + solution.evalRPN(tokens3)); // 22

        // Test case 4: Single number
        String[] tokens4 = {"42"};
        System.out.println("\nInput: [\"42\"]");
        System.out.println("Output: " + solution.evalRPN(tokens4)); // 42

        // Test case 5: Division truncates towards zero
        String[] tokens5 = {"-12", "3", "/"};
        System.out.println("\nInput: [\"-12\", \"3\", \"/\"]");
        System.out.println("Output: " + solution.evalRPN(tokens5)); // -4
    }
}

