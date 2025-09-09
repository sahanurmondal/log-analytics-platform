package arrays.hard;

import java.util.*;

/**
 * LeetCode 32: Longest Valid Parentheses
 * https://leetcode.com/problems/longest-valid-parentheses/
 *
 * Description:
 * Given a string containing just the characters '(' and ')', find the length of
 * the longest valid parentheses substring.
 *
 * Constraints:
 * - 0 <= s.length <= 3 * 10^4
 * - s[i] is '(', or ')'
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Use stack to track indices of unmatched parentheses
 * 2. Push -1 initially as base for length calculation
 * 3. Calculate length between current index and stack top
 */
public class LongestValidParentheses {
    public int longestValidParentheses(String s) {
        Stack<Integer> stack = new Stack<>();
        stack.push(-1);
        int maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }

        return maxLen;
    }

    public static void main(String[] args) {
        LongestValidParentheses solution = new LongestValidParentheses();

        // Test Case 1: Normal case
        System.out.println(solution.longestValidParentheses("(()")); // Expected: 2

        // Test Case 2: Edge case - all valid
        System.out.println(solution.longestValidParentheses(")()())")); // Expected: 4

        // Test Case 3: Corner case - empty string
        System.out.println(solution.longestValidParentheses("")); // Expected: 0

        // Test Case 4: Large input - all valid pairs
        System.out.println(solution.longestValidParentheses("()(())")); // Expected: 6

        // Test Case 5: Minimum input - single char
        System.out.println(solution.longestValidParentheses("(")); // Expected: 0

        // Test Case 6: Special case - all invalid
        System.out.println(solution.longestValidParentheses("(((")); // Expected: 0

        // Test Case 7: Boundary case - nested valid
        System.out.println(solution.longestValidParentheses("((()))")); // Expected: 6

        // Test Case 8: Mixed valid/invalid
        System.out.println(solution.longestValidParentheses("()(()")); // Expected: 2

        // Test Case 9: Multiple valid segments
        System.out.println(solution.longestValidParentheses("()()")); // Expected: 4

        // Test Case 10: Complex pattern
        System.out.println(solution.longestValidParentheses("()()())")); // Expected: 6
    }
}
