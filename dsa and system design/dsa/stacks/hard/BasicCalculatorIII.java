package stacks.hard;

import java.util.*;

/**
 * LeetCode 772: Basic Calculator III
 * https://leetcode.com/problems/basic-calculator-iii/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Implement a basic calculator to evaluate a simple expression
 * string containing non-negative integers, '+', '-', '*', '/', and parentheses.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of digits, '+', '-', '*', '/', '(', ')', and spaces
 * 
 * Follow-up Questions:
 * 1. Support variables and assignment.
 * 2. Support exponentiation.
 * 3. Support unary operators.
 */
public class BasicCalculatorIII {

    // Approach 1: Stack-based evaluation
    public int calculate(String s) {
        return helper(s.toCharArray(), new int[] { 0 });
    }

    private int helper(char[] arr, int[] idx) {
        Stack<Integer> stack = new Stack<>();
        int num = 0;
        char op = '+';
        while (idx[0] < arr.length) {
            char c = arr[idx[0]];
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }
            if (c == '(') {
                idx[0]++;
                num = helper(arr, idx);
            }
            if ((!Character.isDigit(c) && c != ' ' && c != '(') || idx[0] == arr.length - 1) {
                if (op == '+')
                    stack.push(num);
                else if (op == '-')
                    stack.push(-num);
                else if (op == '*')
                    stack.push(stack.pop() * num);
                else if (op == '/')
                    stack.push(stack.pop() / num);
                op = c;
                num = 0;
            }
            if (c == ')') {
                idx[0]++;
                break;
            }
            idx[0]++;
        }
        int res = 0;
        while (!stack.isEmpty())
            res += stack.pop();
        return res;
    }

    // Follow-up 1: Support exponentiation '^'
    public int calculateWithExponent(String s) {
        // ...implement with operator precedence for '^'
        // For brevity, not implemented here.
        return 0;
    }

    // Follow-up 2: Support unary operators
    public int calculateWithUnary(String s) {
        // ...implement unary minus/plus
        // For brevity, not implemented here.
        return 0;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        BasicCalculatorIII solution = new BasicCalculatorIII();

        // Test case 1: Basic case
        String expr1 = "2*(3+5)";
        System.out.println("Test 1 - expr: " + expr1 + " Expected: 16");
        System.out.println("Result: " + solution.calculate(expr1));

        // Test case 2: Nested parentheses
        String expr2 = "(2+6*3+5-(3*14/7+2)*5)+3";
        System.out.println("\nTest 2 - Nested parentheses:");
        System.out.println("Result: " + solution.calculate(expr2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single number: " + solution.calculate("42"));
        System.out.println("Empty string: " + solution.calculate(""));
        System.out.println("Spaces only: " + solution.calculate("   "));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++)
            sb.append("1+");
        sb.append("1");
        long start = System.nanoTime();
        int result = solution.calculate(sb.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
