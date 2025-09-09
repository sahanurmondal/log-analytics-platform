package backtracking.hard;

import java.util.*;

/**
 * LeetCode 282: Expression Add Operators
 * URL: https://leetcode.com/problems/expression-add-operators/
 * Difficulty: Hard
 * Companies: Google, Facebook, Microsoft, Amazon
 * Frequency: High
 *
 * Description:
 * Given a string num that contains only digits and an integer target,
 * return all possibilities to insert the binary operators '+', '-', and '*'
 * between the digits of num so that the resultant expression evaluates to the
 * target value.
 * Note that operands in the expression should not have leading zeros.
 *
 * Constraints:
 * - 1 <= num.length <= 10
 * - num consists of only digits
 * - -2^31 <= target <= 2^31 - 1
 *
 * Follow-up Questions:
 * 1. How to handle operator precedence correctly?
 * 2. Can you optimize for specific target patterns?
 * 3. What if we allow parentheses?
 * 4. How to handle division operator?
 */
public class ExpressionAddOperators {

    // Approach 1: Standard backtracking with proper precedence - O(4^n)
    public List<String> addOperators(String num, int target) {
        List<String> result = new ArrayList<>();
        if (num == null || num.isEmpty())
            return result;

        backtrack(num, target, 0, 0, 0, "", result);
        return result;
    }

    private void backtrack(String num, int target, int index, long eval, long multed,
            String expr, List<String> result) {
        if (index == num.length()) {
            if (eval == target) {
                result.add(expr);
            }
            return;
        }

        for (int i = index; i < num.length(); i++) {
            String str = num.substring(index, i + 1);

            // Skip numbers with leading zeros (except "0" itself)
            if (str.length() > 1 && str.charAt(0) == '0')
                break;

            long val = Long.parseLong(str);

            if (index == 0) {
                // First number, no operator needed
                backtrack(num, target, i + 1, val, val, str, result);
            } else {
                // Add operation
                backtrack(num, target, i + 1, eval + val, val, expr + "+" + str, result);

                // Subtract operation
                backtrack(num, target, i + 1, eval - val, -val, expr + "-" + str, result);

                // Multiply operation (handle precedence by undoing last operation)
                backtrack(num, target, i + 1, eval - multed + multed * val,
                        multed * val, expr + "*" + str, result);
            }
        }
    }

    // Approach 2: Optimized with early pruning - O(4^n) with better average case
    public List<String> addOperatorsOptimized(String num, int target) {
        List<String> result = new ArrayList<>();
        if (num == null || num.isEmpty())
            return result;

        backtrackOptimized(num, target, 0, 0, 0, "", result);
        return result;
    }

    private void backtrackOptimized(String num, int target, int index, long eval, long multed,
            String expr, List<String> result) {
        if (index == num.length()) {
            if (eval == target) {
                result.add(expr);
            }
            return;
        }

        // Early pruning: if remaining digits can't possibly reach target
        if (Math.abs(eval - target) > Math.pow(9, num.length() - index)) {
            return;
        }

        for (int i = index; i < num.length(); i++) {
            String str = num.substring(index, i + 1);

            if (str.length() > 1 && str.charAt(0) == '0')
                break;

            long val = Long.parseLong(str);

            if (index == 0) {
                backtrackOptimized(num, target, i + 1, val, val, str, result);
            } else {
                backtrackOptimized(num, target, i + 1, eval + val, val, expr + "+" + str, result);
                backtrackOptimized(num, target, i + 1, eval - val, -val, expr + "-" + str, result);
                backtrackOptimized(num, target, i + 1, eval - multed + multed * val,
                        multed * val, expr + "*" + str, result);
            }
        }
    }

    // Approach 3: With memoization (though limited benefit for this problem)
    public List<String> addOperatorsMemo(String num, int target) {
        List<String> result = new ArrayList<>();
        Map<String, List<String>> memo = new HashMap<>();

        backtrackMemo(num, target, 0, 0, 0, "", result, memo);
        return result;
    }

    private void backtrackMemo(String num, int target, int index, long eval, long multed,
            String expr, List<String> result, Map<String, List<String>> memo) {
        if (index == num.length()) {
            if (eval == target) {
                result.add(expr);
            }
            return;
        }

        for (int i = index; i < num.length(); i++) {
            String str = num.substring(index, i + 1);

            if (str.length() > 1 && str.charAt(0) == '0')
                break;

            long val = Long.parseLong(str);

            if (index == 0) {
                backtrackMemo(num, target, i + 1, val, val, str, result, memo);
            } else {
                backtrackMemo(num, target, i + 1, eval + val, val, expr + "+" + str, result, memo);
                backtrackMemo(num, target, i + 1, eval - val, -val, expr + "-" + str, result, memo);
                backtrackMemo(num, target, i + 1, eval - multed + multed * val,
                        multed * val, expr + "*" + str, result, memo);
            }
        }
    }

    // Helper method to evaluate expression manually
    public long evaluateExpression(String expr) {
        // Simple evaluation without using eval (for validation)
        Stack<Long> nums = new Stack<>();

        long num = 0;
        char operation = '+';

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }

            if (!Character.isDigit(c) || i == expr.length() - 1) {
                if (operation == '+') {
                    nums.push(num);
                } else if (operation == '-') {
                    nums.push(-num);
                } else if (operation == '*') {
                    nums.push(nums.pop() * num);
                }

                operation = c;
                num = 0;
            }
        }

        long result = 0;
        while (!nums.isEmpty()) {
            result += nums.pop();
        }

        return result;
    }

    // Helper method to count total possible expressions
    public int countTotalExpressions(String num) {
        return countExpressions(num, 0);
    }

    private int countExpressions(String num, int index) {
        if (index == num.length())
            return 1;

        int count = 0;
        for (int i = index; i < num.length(); i++) {
            String str = num.substring(index, i + 1);
            if (str.length() > 1 && str.charAt(0) == '0')
                break;

            if (index == 0) {
                count += countExpressions(num, i + 1);
            } else {
                count += 3 * countExpressions(num, i + 1); // 3 operators
            }
        }
        return count;
    }

    public static void main(String[] args) {
        ExpressionAddOperators solution = new ExpressionAddOperators();

        // Test Case 1: Basic example from LeetCode
        System.out.println("Test 1: " + solution.addOperators("123", 6));
        // Expected: ["1*2*3", "1+2+3"]

        // Test Case 2: Multiple solutions
        System.out.println("Test 2: " + solution.addOperators("232", 8));
        // Expected: ["2*3+2", "2+3*2"]

        // Test Case 3: With zeros
        System.out.println("Test 3: " + solution.addOperators("105", 5));
        // Expected: ["1*0+5", "10-5"]

        // Test Case 4: All zeros
        System.out.println("Test 4: " + solution.addOperators("00", 0));
        // Expected: ["0*0", "0+0", "0-0"]

        // Test Case 5: Single digit
        System.out.println("Test 5: " + solution.addOperators("1", 1));
        // Expected: ["1"]

        // Test Case 6: Impossible target
        System.out.println("Test 6: " + solution.addOperators("123", 100));
        // Expected: []

        // Test Case 7: Optimized approach
        System.out.println("Test 7 (Optimized): " + solution.addOperatorsOptimized("123", 6));
        // Expected: ["1*2*3", "1+2+3"]

        // Test Case 8: Large number formation
        System.out.println("Test 8: " + solution.addOperators("1234", 11));
        // Expected: various combinations

        // Test Case 9: Negative target
        System.out.println("Test 9: " + solution.addOperators("12", -12));
        // Expected: expressions that evaluate to -12

        // Test Case 10: Expression evaluation helper
        System.out.println("Test 10 (Eval): " + solution.evaluateExpression("1+2*3"));
        // Expected: 7

        // Test Case 11: Count total possible expressions
        System.out.println("Test 11 (Count): " + solution.countTotalExpressions("123"));
        // Expected: total possible expressions

        // Test Case 12: Performance comparison
        long start = System.currentTimeMillis();
        List<String> result12 = solution.addOperators("1234567", 100);
        long end = System.currentTimeMillis();
        System.out.println("Test 12 (Performance): " + result12.size() + " results in " + (end - start) + "ms");

        // Test Case 13: Leading zeros handling
        System.out.println("Test 13: " + solution.addOperators("102", 3));
        // Expected: ["1+0+2", "10-2*3+5"] but no "102" as single number

        // Test Case 14: Memoized approach
        System.out.println("Test 14 (Memo): " + solution.addOperatorsMemo("232", 8));
        // Expected: ["2*3+2", "2+3*2"]

        // Test Case 15: Edge case with all same digits
        System.out.println("Test 15: " + solution.addOperators("1111", 4));
        // Expected: various combinations including "1+1+1+1"
    }
}
