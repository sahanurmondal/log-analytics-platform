package arrays.hard;

import java.util.*;

/**
 * LeetCode 301: Remove Invalid Parentheses
 * https://leetcode.com/problems/remove-invalid-parentheses/
 *
 * Description:
 * Given a string s that contains parentheses and letters, remove the minimum
 * number of invalid parentheses
 * to make the input string valid. Return all possible results.
 *
 * Constraints:
 * - 1 <= s.length <= 25
 * - s consists of lowercase English letters and parentheses '(' and ')'
 *
 * Follow-up:
 * - Can you solve it using BFS?
 * 
 * Time Complexity: O(2^n)
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Calculate minimum removals needed for left and right parentheses
 * 2. Use backtracking to generate all valid combinations
 * 3. Prune invalid paths early to optimize
 */
public class RemoveInvalidParentheses {
    public List<String> removeInvalidParentheses(String s) {
        List<String> result = new ArrayList<>();
        int[] toRemove = calculateRemovals(s);

        dfs(s, 0, 0, 0, toRemove[0], toRemove[1], new StringBuilder(), result);
        return result;
    }

    private int[] calculateRemovals(String s) {
        int left = 0, right = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                left++;
            } else if (c == ')') {
                if (left > 0)
                    left--;
                else
                    right++;
            }
        }
        return new int[] { left, right };
    }

    private void dfs(String s, int index, int leftCount, int rightCount,
            int leftRem, int rightRem, StringBuilder path, List<String> result) {
        if (index == s.length()) {
            if (leftRem == 0 && rightRem == 0) {
                result.add(path.toString());
            }
            return;
        }

        char c = s.charAt(index);

        // Option 1: Remove current character
        if ((c == '(' && leftRem > 0) || (c == ')' && rightRem > 0)) {
            dfs(s, index + 1, leftCount, rightCount,
                    leftRem - (c == '(' ? 1 : 0), rightRem - (c == ')' ? 1 : 0), path, result);
        }

        // Option 2: Keep current character
        path.append(c);
        if (c != '(' && c != ')') {
            dfs(s, index + 1, leftCount, rightCount, leftRem, rightRem, path, result);
        } else if (c == '(') {
            dfs(s, index + 1, leftCount + 1, rightCount, leftRem, rightRem, path, result);
        } else if (c == ')' && leftCount > rightCount) {
            dfs(s, index + 1, leftCount, rightCount + 1, leftRem, rightRem, path, result);
        }
        path.deleteCharAt(path.length() - 1);
    }

    public static void main(String[] args) {
        RemoveInvalidParentheses solution = new RemoveInvalidParentheses();

        // Test Case 1: Normal case
        System.out.println(solution.removeInvalidParentheses("()())")); // Expected: ["(())","()()"]

        // Test Case 2: Edge case - multiple invalid
        System.out.println(solution.removeInvalidParentheses("(((")); // Expected: [""]

        // Test Case 3: Corner case - valid string
        System.out.println(solution.removeInvalidParentheses("()")); // Expected: ["()"]

        // Test Case 4: Large input - with letters
        System.out.println(solution.removeInvalidParentheses("(v)())")); // Expected: ["(v())","(v)()"]

        // Test Case 5: Minimum input - single char
        System.out.println(solution.removeInvalidParentheses("(")); // Expected: [""]

        // Test Case 6: Special case - only letters
        System.out.println(solution.removeInvalidParentheses("abc")); // Expected: ["abc"]

        // Test Case 7: Boundary case - empty result
        System.out.println(solution.removeInvalidParentheses("))(")); // Expected: [""]

        // Test Case 8: Complex pattern
        System.out.println(solution.removeInvalidParentheses("(a)())")); // Expected: ["(a())","(a)()"]

        // Test Case 9: Multiple solutions
        System.out.println(solution.removeInvalidParentheses("(a))")); // Expected: ["(a)"]

        // Test Case 10: Nested with letters
        System.out.println(solution.removeInvalidParentheses("((a)")); // Expected: ["(a)"]
    }
}
