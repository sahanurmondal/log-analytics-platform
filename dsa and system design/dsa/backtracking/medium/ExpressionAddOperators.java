package backtracking.medium;

import java.util.*;

/**
 * LeetCode 282: Expression Add Operators
 * https://leetcode.com/problems/expression-add-operators/
 *
 * Description:
 * Given a string num and a target integer, add binary operators (+, -, *)
 * between the digits so that the expression evaluates to the target value.
 *
 * Constraints:
 * - 1 <= num.length <= 10
 * - num consists of only digits.
 * - -2^31 <= target <= 2^31-1
 *
 * Follow-up:
 * - Can you solve it recursively?
 */
public class ExpressionAddOperators {
    public List<String> addOperators(String num, int target) {
        List<String> result = new ArrayList<>();
        if (num == null || num.length() == 0)
            return result;

        backtrack(num, target, 0, 0, 0, "", result);
        return result;
    }

    private void backtrack(String num, int target, int index, long eval, long multed, String expr,
            List<String> result) {
        if (index == num.length()) {
            if (target == eval) {
                result.add(expr);
            }
            return;
        }

        for (int i = index; i < num.length(); i++) {
            String part = num.substring(index, i + 1);
            if (part.length() > 1 && part.charAt(0) == '0')
                break; // No leading zeros

            long cur = Long.parseLong(part);

            if (index == 0) {
                backtrack(num, target, i + 1, cur, cur, part, result);
            } else {
                // Addition
                backtrack(num, target, i + 1, eval + cur, cur, expr + "+" + part, result);

                // Subtraction
                backtrack(num, target, i + 1, eval - cur, -cur, expr + "-" + part, result);

                // Multiplication
                backtrack(num, target, i + 1, eval - multed + multed * cur, multed * cur, expr + "*" + part, result);
            }
        }
    }

    public static void main(String[] args) {
        ExpressionAddOperators solution = new ExpressionAddOperators();
        System.out.println(solution.addOperators("123", 6)); // ["1+2+3","1*2*3"]
        System.out.println(solution.addOperators("232", 8)); // ["2*3+2","2+3*2"]
        System.out.println(solution.addOperators("105", 5)); // ["1*0+5","10-5"]
        System.out.println(solution.addOperators("00", 0)); // ["0+0","0-0","0*0"]
        System.out.println(solution.addOperators("3456237490", 9191)); // []
        // Edge Case: Empty num
        System.out.println(solution.addOperators("", 0)); // []
        // Edge Case: num with leading zeros
        System.out.println(solution.addOperators("010", 10)); // ["0+10","0-10","0*10"]
        // Edge Case: Large num
        System.out.println(solution.addOperators("123456789", 45)); // [....]
    }
}
