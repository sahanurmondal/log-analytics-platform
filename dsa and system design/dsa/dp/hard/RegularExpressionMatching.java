package dp.hard;

import java.util.*;

/**
 * LeetCode 10: Regular Expression Matching
 * https://leetcode.com/problems/regular-expression-matching/
 *
 * Description:
 * Given an input string s and a pattern p, implement regular expression
 * matching with support for '.' and '*' where:
 * - '.' Matches any single character.
 * - '*' Matches zero or more of the preceding element.
 * The matching should cover the entire input string (not partial).
 *
 * Constraints:
 * - 1 <= s.length <= 20
 * - 1 <= p.length <= 30
 * - s contains only lowercase English letters.
 * - p contains only lowercase English letters, '.', and '*'.
 * - It is guaranteed that for each appearance of the character '*', there will
 * be a previous valid character to match.
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * - What if we need to find all matching substrings?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg
 * Difficulty: Hard
 */
public class RegularExpressionMatching {

    // Approach 1: Recursive with Memoization - O(m*n) time, O(m*n) space
    public boolean isMatch(String s, String p) {
        Boolean[][] memo = new Boolean[s.length() + 1][p.length() + 1];
        return isMatchHelper(s, p, 0, 0, memo);
    }

    private boolean isMatchHelper(String s, String p, int i, int j, Boolean[][] memo) {
        if (memo[i][j] != null)
            return memo[i][j];

        if (j == p.length()) {
            memo[i][j] = (i == s.length());
            return memo[i][j];
        }

        boolean firstMatch = (i < s.length()) &&
                (p.charAt(j) == s.charAt(i) || p.charAt(j) == '.');

        boolean result;
        if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
            // Two choices: use * for zero matches, or use * for one+ matches
            result = isMatchHelper(s, p, i, j + 2, memo) ||
                    (firstMatch && isMatchHelper(s, p, i + 1, j, memo));
        } else {
            result = firstMatch && isMatchHelper(s, p, i + 1, j + 1, memo);
        }

        memo[i][j] = result;
        return result;
    }

    // Approach 2: Bottom-up DP - O(m*n) time, O(m*n) space
    public boolean isMatchDP(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];

        dp[m][n] = true;

        // Fill last row (empty string cases)
        for (int j = n - 1; j >= 0; j--) {
            if (j + 1 < n && p.charAt(j + 1) == '*') {
                dp[m][j] = dp[m][j + 2];
            }
        }

        // Fill the DP table from bottom-right to top-left
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                boolean firstMatch = (p.charAt(j) == s.charAt(i) || p.charAt(j) == '.');

                if (j + 1 < n && p.charAt(j + 1) == '*') {
                    dp[i][j] = dp[i][j + 2] || (firstMatch && dp[i + 1][j]);
                } else {
                    dp[i][j] = firstMatch && dp[i + 1][j + 1];
                }
            }
        }

        return dp[0][0];
    }

    // Approach 3: Space Optimized DP - O(m*n) time, O(n) space
    public boolean isMatchOptimized(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[] prev = new boolean[n + 1];
        boolean[] curr = new boolean[n + 1];

        prev[n] = true;

        // Fill last row
        for (int j = n - 1; j >= 0; j--) {
            if (j + 1 < n && p.charAt(j + 1) == '*') {
                prev[j] = prev[j + 2];
            }
        }

        for (int i = m - 1; i >= 0; i--) {
            curr[n] = false;

            for (int j = n - 1; j >= 0; j--) {
                boolean firstMatch = (p.charAt(j) == s.charAt(i) || p.charAt(j) == '.');

                if (j + 1 < n && p.charAt(j + 1) == '*') {
                    curr[j] = curr[j + 2] || (firstMatch && prev[j]);
                } else {
                    curr[j] = firstMatch && prev[j + 1];
                }
            }

            boolean[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[0];
    }

    // Approach 4: NFA-based Approach - O(m*n) time, O(n) space
    public boolean isMatchNFA(String s, String p) {
        // Build NFA states
        List<Set<Integer>> states = buildNFA(p);
        Set<Integer> currentStates = new HashSet<>();
        currentStates.add(0);
        currentStates = epsilonClosure(currentStates, states);

        for (char c : s.toCharArray()) {
            currentStates = step(currentStates, c, p, states);
            if (currentStates.isEmpty())
                return false;
        }

        return currentStates.contains(p.length());
    }

    private List<Set<Integer>> buildNFA(String p) {
        int n = p.length();
        List<Set<Integer>> states = new ArrayList<>();

        for (int i = 0; i <= n; i++) {
            states.add(new HashSet<>());
        }

        for (int i = 0; i < n; i++) {
            if (i + 1 < n && p.charAt(i + 1) == '*') {
                states.get(i).add(i + 2); // Skip pattern
                states.get(i + 2).add(i); // Repeat pattern
            }
        }

        return states;
    }

    private Set<Integer> epsilonClosure(Set<Integer> states, List<Set<Integer>> nfa) {
        Set<Integer> closure = new HashSet<>(states);
        Stack<Integer> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            int state = stack.pop();
            for (int nextState : nfa.get(state)) {
                if (!closure.contains(nextState)) {
                    closure.add(nextState);
                    stack.push(nextState);
                }
            }
        }

        return closure;
    }

    private Set<Integer> step(Set<Integer> states, char c, String p, List<Set<Integer>> nfa) {
        Set<Integer> nextStates = new HashSet<>();

        for (int state : states) {
            if (state < p.length()) {
                char pc = p.charAt(state);
                if (pc == c || pc == '.') {
                    nextStates.add(state + 1);
                } else if (state + 1 < p.length() && p.charAt(state + 1) == '*') {
                    if (pc == c || pc == '.') {
                        nextStates.add(state); // Stay in same state
                    }
                }
            }
        }

        return epsilonClosure(nextStates, nfa);
    }

    // Approach 5: Get All Matching Positions - O(m*n*k) time, O(m*n*k) space
    public List<int[]> getAllMatchingPositions(String s, String p) {
        List<int[]> matches = new ArrayList<>();

        for (int start = 0; start < s.length(); start++) {
            for (int end = start; end <= s.length(); end++) {
                String substring = s.substring(start, end);
                if (isMatch(substring, p)) {
                    matches.add(new int[] { start, end - 1 });
                }
            }
        }

        return matches;
    }

    public static void main(String[] args) {
        RegularExpressionMatching solution = new RegularExpressionMatching();

        System.out.println("=== Regular Expression Matching Test Cases ===");

        // Test Case 1: Basic example
        String s1 = "aa", p1 = "a";
        System.out.println("Test 1 - s: \"" + s1 + "\", p: \"" + p1 + "\"");
        System.out.println("Recursive: " + solution.isMatch(s1, p1));
        System.out.println("DP: " + solution.isMatchDP(s1, p1));
        System.out.println("Optimized: " + solution.isMatchOptimized(s1, p1));
        System.out.println("NFA: " + solution.isMatchNFA(s1, p1));
        System.out.println("Expected: false\n");

        // Test Case 2: With star
        String s2 = "aa", p2 = "a*";
        System.out.println("Test 2 - s: \"" + s2 + "\", p: \"" + p2 + "\"");
        System.out.println("Recursive: " + solution.isMatch(s2, p2));
        System.out.println("Expected: true\n");

        // Test Case 3: Complex pattern
        String s3 = "ab", p3 = ".*";
        System.out.println("Test 3 - s: \"" + s3 + "\", p: \"" + p3 + "\"");
        System.out.println("Recursive: " + solution.isMatch(s3, p3));
        System.out.println("Expected: true\n");

        // Test Case 4: More complex
        String s4 = "aab", p4 = "c*a*b";
        System.out.println("Test 4 - s: \"" + s4 + "\", p: \"" + p4 + "\"");
        System.out.println("Recursive: " + solution.isMatch(s4, p4));

        List<int[]> positions = solution.getAllMatchingPositions(s4, p4);
        System.out.println("Matching positions:");
        for (int[] pos : positions) {
            System.out.println("  [" + pos[0] + ", " + pos[1] + "]");
        }
        System.out.println("Expected: true\n");

        performanceTest();
    }

    private static void performanceTest() {
        RegularExpressionMatching solution = new RegularExpressionMatching();

        String s = "a".repeat(15);
        String p = "a*".repeat(10);

        System.out.println("=== Performance Test (s length: " + s.length() + ", p length: " + p.length() + ") ===");

        long start = System.nanoTime();
        boolean result1 = solution.isMatch(s, p);
        long end = System.nanoTime();
        System.out.println("Recursive: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result2 = solution.isMatchDP(s, p);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result3 = solution.isMatchOptimized(s, p);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
