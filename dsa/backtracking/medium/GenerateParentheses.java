package backtracking.medium;

import java.util.*;

/**
 * LeetCode 22: Generate Parentheses
 * https://leetcode.com/problems/generate-parentheses/
 *
 * Description:
 * Given n pairs of parentheses, write a function to generate all combinations
 * of well-formed parentheses.
 *
 * Constraints:
 * - 1 <= n <= 8
 *
 * Follow-up:
 * - Can you solve it recursively?
 */
public class GenerateParentheses {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, "", 0, 0, n);
        return result;
    }

    private void backtrack(List<String> result, String current, int open, int close, int max) {
        if (current.length() == max * 2) {
            result.add(current);
            return;
        }

        if (open < max) {
            backtrack(result, current + "(", open + 1, close, max);
        }

        if (close < open) {
            backtrack(result, current + ")", open, close + 1, max);
        }
    }

    public static void main(String[] args) {
        GenerateParentheses solution = new GenerateParentheses();
        System.out.println(solution.generateParenthesis(3)); // ["((()))","(()())","(())()","()(())","()()()"]
        System.out.println(solution.generateParenthesis(1)); // ["()"]
        System.out.println(solution.generateParenthesis(2)); // ["(())","()()"]
        // Edge Case: n = 0
        System.out.println(solution.generateParenthesis(0)); // []
        // Edge Case: Large n
        System.out.println(solution.generateParenthesis(8)); // [....]
    }
}
