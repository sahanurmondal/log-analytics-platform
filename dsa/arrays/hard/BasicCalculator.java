package arrays.hard;

import java.util.*;

/**
 * LeetCode 224: Basic Calculator
 * https://leetcode.com/problems/basic-calculator/
 *
 * Description:
 * Given a string s representing a valid expression, implement a basic
 * calculator to evaluate it,
 * and return the result of the evaluation.
 *
 * Constraints:
 * - 1 <= s.length <= 3 * 10^5
 * - s consists of digits, '+', '-', '(', ')', and ' '
 * - s represents a valid expression
 * - '+' is not used as a unary operation
 * - '-' could be used as a unary operation and in this case, it will not be
 * used directly after '+'
 *
 * Follow-up:
 * - Can you solve it without using built-in library functions?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Use stack to handle parentheses and track previous results
 * 2. Process numbers and operators sequentially
 * 3. Handle nested expressions with stack for signs and results
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
                number = 10 * number + (c - '0');
            } else if (c == '+') {
                result += sign * number;
                number = 0;
                sign = 1;
            } else if (c == '-') {
                result += sign * number;
                number = 0;
                sign = -1;
            } else if (c == '(') {
                stack.push(result);
                stack.push(sign);
                sign = 1;
                result = 0;
            } else if (c == ')') {
                result += sign * number;
                number = 0;
                result *= stack.pop(); // sign
                result += stack.pop(); // previous result
            }
        }

        if (number != 0)
            result += sign * number;
        return result;
    }

    public static void main(String[] args) {
        BasicCalculator solution = new BasicCalculator();

        // Test Case 1: Normal case
        System.out.println(solution.calculate("1 + 1")); // Expected: 2

        // Test Case 2: Edge case - with parentheses
        System.out.println(solution.calculate(" 2-1 + 2 ")); // Expected: 3

        // Test Case 3: Corner case - nested parentheses
        System.out.println(solution.calculate("(1+(4+5+2)-3)+(6+8)")); // Expected: 23

        // Test Case 4: Large input - complex expression
        System.out.println(solution.calculate("1-(     -2)")); // Expected: 3

        // Test Case 5: Minimum input - single number
        System.out.println(solution.calculate("42")); // Expected: 42

        // Test Case 6: Special case - negative numbers
        System.out.println(solution.calculate("-2+ 1")); // Expected: -1

        // Test Case 7: Boundary case - multiple parentheses
        System.out.println(solution.calculate("((1))")); // Expected: 1

        // Test Case 8: Spaces everywhere
        System.out.println(solution.calculate("  1   +   1  ")); // Expected: 2

        // Test Case 9: Complex nested
        System.out.println(solution.calculate("2-(5-6)")); // Expected: 3

        // Test Case 10: Zero result
        System.out.println(solution.calculate("1-1")); // Expected: 0
    }
}
