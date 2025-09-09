package stacks.medium;

import java.util.*;

/**
 * LeetCode 150: Evaluate Reverse Polish Notation
 * https://leetcode.com/problems/evaluate-reverse-polish-notation/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Evaluate the value of an arithmetic expression in Reverse Polish Notation.
 *
 * Constraints:
 * - 1 <= tokens.length <= 10^4
 * - tokens[i] is an integer or "+", "-", "*", "/"
 * 
 * Follow-up Questions:
 * 1. Can you support variables?
 * 2. Can you optimize for large expressions?
 * 3. Can you handle invalid tokens?
 */
public class EvaluateReversePolishNotation {

    // Approach 1: Stack evaluation
    public int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        for (String token : tokens) {
            if ("+-*/".contains(token)) {
                int b = stack.pop(), a = stack.pop();
                if (token.equals("+")) stack.push(a + b);
                else if (token.equals("-")) stack.push(a - b);
                else if (token.equals("*")) stack.push(a * b);
                else stack.push(a / b);
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.pop();
    }

    // Follow-up 1: Support variables (not implemented for brevity)
    // Comprehensive test cases
    public static void main(String[] args) {
        EvaluateReversePolishNotation solution = new EvaluateReversePolishNotation();

        // Test case 1: Basic case
        String[] tokens1 = {"2","1","+","3","*"};
        System.out.println("Test 1 - tokens: " + Arrays.toString(tokens1) + " Expected: 9");
        System.out.println("Result: " + solution.evalRPN(tokens1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single number: " + solution.evalRPN(new String[]{"42"}));
        System.out.println("Negative result: " + solution.evalRPN(new String[]{"4","13","5","/","+"}));
    }
}
