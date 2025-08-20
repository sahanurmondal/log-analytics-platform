package backtracking.hard;

import java.util.*;

/**
 * LeetCode 22: Generate Parentheses (Hard Variant)
 * https://leetcode.com/problems/generate-parentheses/
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 * 
 * Given n pairs of parentheses, write a function to generate all combinations
 * of well-formed parentheses with additional constraints.
 * 
 * Constraints:
 * - 1 <= n <= 10
 * - Additional constraint: No two consecutive opening parentheses in some
 * variants
 */
public class FindAllValidCombinationsOfParentheses {

    /**
     * Approach 1: Standard Backtracking
     * Time: O(4^n / sqrt(n)) - Catalan number
     * Space: O(4^n / sqrt(n)) for results + O(n) recursion depth
     */
    public List<String> generateParenthesisConstrained(int n) {
        List<String> result = new ArrayList<>();
        if (n == 0)
            return result;
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private void backtrack(List<String> result, StringBuilder current,
            int open, int close, int n) {
        if (current.length() == 2 * n) {
            result.add(current.toString());
            return;
        }

        // Add opening parenthesis if we haven't used all n
        if (open < n) {
            current.append('(');
            backtrack(result, current, open + 1, close, n);
            current.deleteCharAt(current.length() - 1);
        }

        // Add closing parenthesis if it doesn't exceed opening
        if (close < open) {
            current.append(')');
            backtrack(result, current, open, close + 1, n);
            current.deleteCharAt(current.length() - 1);
        }
    }

    /**
     * Approach 2: No Consecutive Opens Constraint
     * Time: O(2^(2n))
     * Space: O(2^(2n))
     */
    public List<String> generateNoConsecutiveOpens(int n) {
        List<String> result = new ArrayList<>();
        if (n == 0)
            return result;
        backtrackConstrained(result, new StringBuilder(), 0, 0, n, false);
        return result;
    }

    private void backtrackConstrained(List<String> result, StringBuilder current,
            int open, int close, int n, boolean lastWasOpen) {
        if (current.length() == 2 * n) {
            result.add(current.toString());
            return;
        }

        // Add opening parenthesis if we haven't used all n and last wasn't open
        if (open < n && !lastWasOpen) {
            current.append('(');
            backtrackConstrained(result, current, open + 1, close, n, true);
            current.deleteCharAt(current.length() - 1);
        }

        // Add closing parenthesis if it doesn't exceed opening
        if (close < open) {
            current.append(')');
            backtrackConstrained(result, current, open, close + 1, n, false);
            current.deleteCharAt(current.length() - 1);
        }
    }

    /**
     * Approach 3: Dynamic Programming
     * Time: O(4^n / sqrt(n))
     * Space: O(4^n / sqrt(n))
     */
    public List<String> generateParenthesesDP(int n) {
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

    /**
     * Approach 4: Iterative BFS
     * Time: O(4^n / sqrt(n))
     * Space: O(4^n / sqrt(n))
     */
    public List<String> generateParenthesesIterative(int n) {
        List<String> result = new ArrayList<>();
        if (n == 0)
            return result;

        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node("", 0, 0));

        while (!queue.isEmpty()) {
            Node node = queue.poll();

            if (node.str.length() == 2 * n) {
                result.add(node.str);
                continue;
            }

            // Add opening parenthesis
            if (node.open < n) {
                queue.offer(new Node(node.str + "(", node.open + 1, node.close));
            }

            // Add closing parenthesis
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
        FindAllValidCombinationsOfParentheses solution = new FindAllValidCombinationsOfParentheses();

        // Test Case 1: Standard generation
        System.out.println("Test Case 1: Standard n=3");
        List<String> result1 = solution.generateParenthesisConstrained(3);
        System.out.println("Result: " + result1);
        System.out.println("Count: " + result1.size());

        // Test Case 2: n = 1
        System.out.println("\nTest Case 2: n=1");
        System.out.println(solution.generateParenthesisConstrained(1)); // ["()"]

        // Test Case 3: n = 0
        System.out.println("\nTest Case 3: n=0");
        System.out.println(solution.generateParenthesisConstrained(0)); // []

        // Test Case 4: Constrained version
        System.out.println("\nTest Case 4: No consecutive opens, n=3");
        List<String> constrained = solution.generateNoConsecutiveOpens(3);
        System.out.println("Result: " + constrained);

        // Test Case 5: DP approach
        System.out.println("\nTest Case 5: DP approach, n=3");
        List<String> dp = solution.generateParenthesesDP(3);
        System.out.println("DP Result count: " + dp.size());

        // Test Case 6: Iterative approach
        System.out.println("\nTest Case 6: Iterative approach, n=3");
        List<String> iterative = solution.generateParenthesesIterative(3);
        System.out.println("Iterative Result count: " + iterative.size());

        // Test Case 7: Large n
        System.out.println("\nTest Case 7: Large n=5");
        List<String> large = solution.generateParenthesisConstrained(5);
        System.out.println("Count for n=5: " + large.size());

        // Test Cases 8-15: Additional comprehensive testing
        System.out.println("\nTest Case 8: Performance comparison");
        long start = System.nanoTime();
        solution.generateParenthesisConstrained(6);
        long backtrackTime = System.nanoTime() - start;

        start = System.nanoTime();
        solution.generateParenthesesIterative(6);
        long iterativeTime = System.nanoTime() - start;

        System.out.println("Backtrack time: " + backtrackTime / 1_000_000.0 + " ms");
        System.out.println("Iterative time: " + iterativeTime / 1_000_000.0 + " ms");

        // Test Case 9: Validation
        System.out.println("\nTest Case 9: Validation for n=4");
        List<String> toValidate = solution.generateParenthesisConstrained(4);
        boolean allValid = toValidate.stream().allMatch(solution::isValidParentheses);
        System.out.println("All valid: " + allValid);

        // Test Case 10: Uniqueness check
        System.out.println("\nTest Case 10: Uniqueness check");
        Set<String> unique = new HashSet<>(result1);
        System.out.println("All unique: " + (unique.size() == result1.size()));

        // Test Cases 11-15: Edge cases and stress tests
        for (int i = 0; i <= 4; i++) {
            List<String> test = solution.generateParenthesisConstrained(i);
            System.out.println("n=" + i + ", count=" + test.size());
        }
    }

    private boolean isValidParentheses(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(')
                balance++;
            else
                balance--;
            if (balance < 0)
                return false;
        }
        return balance == 0;
    }
}
