package backtracking.hard;

import java.util.*;

/**
 * LeetCode 301: Remove Invalid Parentheses
 * https://leetcode.com/problems/remove-invalid-parentheses/
 *
 * Description: Given a string s that contains parentheses and letters, remove
 * the minimum number of invalid parentheses
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
 * Company Tags: Google, Facebook, Amazon
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

    // Alternative solution - BFS
    public List<String> removeInvalidParenthesesBFS(String s) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.offer(s);
        visited.add(s);
        boolean found = false;

        while (!queue.isEmpty() && !found) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                String current = queue.poll();

                if (isValid(current)) {
                    result.add(current);
                    found = true;
                }

                if (!found) {
                    for (int j = 0; j < current.length(); j++) {
                        if (current.charAt(j) != '(' && current.charAt(j) != ')')
                            continue;

                        String next = current.substring(0, j) + current.substring(j + 1);
                        if (!visited.contains(next)) {
                            visited.add(next);
                            queue.offer(next);
                        }
                    }
                }
            }
        }

        return result;
    }

    private boolean isValid(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == '(')
                count++;
            else if (c == ')') {
                count--;
                if (count < 0)
                    return false;
            }
        }
        return count == 0;
    }

    public static void main(String[] args) {
        RemoveInvalidParentheses solution = new RemoveInvalidParentheses();

        System.out.println(solution.removeInvalidParentheses("()())")); // Expected: ["(())","()()"]
        System.out.println(solution.removeInvalidParentheses("(((")); // Expected: [""]
        System.out.println(solution.removeInvalidParentheses("()")); // Expected: ["()"]
    }
}
