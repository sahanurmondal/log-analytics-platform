package backtracking.easy;

import java.util.*;

/**
 * LeetCode 22: Generate Parentheses
 * https://leetcode.com/problems/generate-parentheses/
 *
 * Description: Given n pairs of parentheses, write a function to generate all
 * combinations of well-formed parentheses.
 * 
 * Constraints:
 * - 1 <= n <= 8
 *
 * Follow-up:
 * - Can you solve it using dynamic programming?
 * - What about generating the kth valid combination?
 * 
 * Time Complexity: O(4^n / sqrt(n)) - Catalan number
 * Space Complexity: O(4^n / sqrt(n))
 * 
 * Algorithm:
 * 1. Backtracking: Add '(' when possible, add ')' when valid
 * 2. Dynamic Programming: Build from smaller solutions
 * 3. BFS: Generate level by level with validation
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class GenerateParentheses {

    // Main optimized solution - Backtracking
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private void backtrack(List<String> result, StringBuilder current, int open, int close, int n) {
        if (current.length() == 2 * n) {
            result.add(current.toString());
            return;
        }

        if (open < n) {
            current.append('(');
            backtrack(result, current, open + 1, close, n);
            current.deleteCharAt(current.length() - 1);
        }

        if (close < open) {
            current.append(')');
            backtrack(result, current, open, close + 1, n);
            current.deleteCharAt(current.length() - 1);
        }
    }

    // Alternative solution - Dynamic Programming
    public List<String> generateParenthesisDP(int n) {
        List<List<String>> dp = new ArrayList<>();
        dp.add(Arrays.asList(""));

        for (int i = 1; i <= n; i++) {
            List<String> current = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                List<String> left = dp.get(j);
                List<String> right = dp.get(i - 1 - j);

                for (String l : left) {
                    for (String r : right) {
                        current.add("(" + l + ")" + r);
                    }
                }
            }
            dp.add(current);
        }

        return dp.get(n);
    }

    // Follow-up optimization - BFS approach
    public List<String> generateParenthesisBFS(int n) {
        List<String> result = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node("", 0, 0));

        while (!queue.isEmpty()) {
            Node node = queue.poll();

            if (node.str.length() == 2 * n) {
                result.add(node.str);
                continue;
            }

            if (node.open < n) {
                queue.offer(new Node(node.str + "(", node.open + 1, node.close));
            }

            if (node.close < node.open) {
                queue.offer(new Node(node.str + ")", node.open, node.close + 1));
            }
        }

        return result;
    }

    static class Node {
        String str;
        int open, close;

        Node(String str, int open, int close) {
            this.str = str;
            this.open = open;
            this.close = close;
        }
    }

    public static void main(String[] args) {
        GenerateParentheses solution = new GenerateParentheses();

        // Test Case 1: n = 3
        System.out.println(solution.generateParenthesis(3)); // Expected: ["((()))","(()())","(())()","()(())","()()()"]

        // Test Case 2: n = 1
        System.out.println(solution.generateParenthesis(1)); // Expected: ["()"]

        // Test Case 3: n = 2
        System.out.println(solution.generateParenthesis(2)); // Expected: ["(())","()()"]

        // Test Case 4: n = 4
        System.out.println(solution.generateParenthesis(4).size()); // Expected: 14

        // Test Case 5: n = 5
        System.out.println(solution.generateParenthesis(5).size()); // Expected: 42

        // Test Case 6: Test DP approach
        System.out.println(solution.generateParenthesisDP(3).size()); // Expected: 5

        // Test Case 7: Test BFS approach
        System.out.println(solution.generateParenthesisBFS(2)); // Expected: ["(())","()()"]

        // Test Case 8: Maximum constraint
        System.out.println(solution.generateParenthesis(8).size()); // Expected: 1430 (8th Catalan number)

        // Test Case 9: Compare approaches
        System.out.println(solution.generateParenthesis(3).equals(solution.generateParenthesisDP(3))); // Should be same
                                                                                                       // content

        // Test Case 10: Edge case
        System.out.println(solution.generateParenthesis(6).size()); // Expected: 132
    }
}
