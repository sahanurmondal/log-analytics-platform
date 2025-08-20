package dp.hard;

import java.util.*;

/**
 * LeetCode 664: Strange Printer
 * https://leetcode.com/problems/strange-printer/
 *
 * Description:
 * There is a strange printer with the following two special properties:
 * - The printer can only print a sequence of the same character each time.
 * - At each turn, the printer can print new characters starting from and ending
 * at any place and will cover the original existing characters.
 * Given a string s, return the minimum number of turns the printer needed to
 * print it.
 *
 * Constraints:
 * - 1 <= s.length <= 100
 * - s consists of lowercase English letters.
 *
 * Follow-up:
 * - Can you find the optimal printing sequence?
 * - What if we have multiple printers?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class StrangePrinter {

    // Approach 1: Interval DP - O(n^3) time, O(n^2) space
    public int strangePrinter(String s) {
        if (s.length() == 0)
            return 0;

        int n = s.length();
        int[][] dp = new int[n][n];

        // Base case: single character needs 1 turn
        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
        }

        // Fill for lengths 2 to n
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = len; // Worst case: print each character separately

                // Try all possible split points
                for (int k = i; k < j; k++) {
                    int total = dp[i][k] + dp[k + 1][j];

                    // If characters at i and j are same, we can save one turn
                    if (s.charAt(i) == s.charAt(j)) {
                        total--;
                    }

                    dp[i][j] = Math.min(dp[i][j], total);
                }
            }
        }

        return dp[0][n - 1];
    }

    // Approach 2: Memoization - O(n^3) time, O(n^2) space
    public int strangePrinterMemo(String s) {
        if (s.length() == 0)
            return 0;

        Integer[][] memo = new Integer[s.length()][s.length()];
        return strangePrinterMemoHelper(s, 0, s.length() - 1, memo);
    }

    private int strangePrinterMemoHelper(String s, int i, int j, Integer[][] memo) {
        if (i > j)
            return 0;
        if (i == j)
            return 1;

        if (memo[i][j] != null)
            return memo[i][j];

        int result = Integer.MAX_VALUE;

        // Try all possible split points
        for (int k = i; k < j; k++) {
            int left = strangePrinterMemoHelper(s, i, k, memo);
            int right = strangePrinterMemoHelper(s, k + 1, j, memo);

            int total = left + right;

            // If first and last characters are same, we can merge them
            if (s.charAt(i) == s.charAt(j)) {
                total--;
            }

            result = Math.min(result, total);
        }

        memo[i][j] = result;
        return result;
    }

    // Approach 3: Optimized with Character Removal - O(n^3) time, O(n^2) space
    public int strangePrinterOptimized(String s) {
        if (s.length() == 0)
            return 0;

        // Remove consecutive duplicate characters first
        StringBuilder sb = new StringBuilder();
        char prev = s.charAt(0);
        sb.append(prev);

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != prev) {
                sb.append(s.charAt(i));
                prev = s.charAt(i);
            }
        }

        String cleaned = sb.toString();
        int n = cleaned.length();
        int[][] dp = new int[n][n];

        // Base case
        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
        }

        // Fill DP table
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;

                if (cleaned.charAt(i) == cleaned.charAt(j)) {
                    dp[i][j] = dp[i][j - 1];
                } else {
                    dp[i][j] = Integer.MAX_VALUE;
                    for (int k = i; k < j; k++) {
                        dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k + 1][j]);
                    }
                }
            }
        }

        return dp[0][n - 1];
    }

    // Approach 4: Get Printing Sequence - O(n^3) time, O(n^2) space
    public List<String> getPrintingSequence(String s) {
        if (s.length() == 0)
            return new ArrayList<>();

        int n = s.length();
        int[][] dp = new int[n][n];
        int[][] split = new int[n][n];

        // Base case
        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
            split[i][i] = i;
        }

        // Fill DP table with split tracking
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = len;
                split[i][j] = i;

                for (int k = i; k < j; k++) {
                    int total = dp[i][k] + dp[k + 1][j];
                    if (s.charAt(i) == s.charAt(j)) {
                        total--;
                    }

                    if (total < dp[i][j]) {
                        dp[i][j] = total;
                        split[i][j] = k;
                    }
                }
            }
        }

        List<String> sequence = new ArrayList<>();
        reconstructSequence(s, 0, n - 1, split, sequence);
        return sequence;
    }

    private void reconstructSequence(String s, int i, int j, int[][] split, List<String> sequence) {
        if (i > j)
            return;

        if (i == j) {
            sequence.add("Print '" + s.charAt(i) + "' at position " + i);
            return;
        }

        int k = split[i][j];

        if (s.charAt(i) == s.charAt(j)) {
            sequence.add("Print '" + s.charAt(i) + "' from position " + i + " to " + j);
            reconstructSequence(s, i + 1, j - 1, split, sequence);
        } else {
            reconstructSequence(s, i, k, split, sequence);
            reconstructSequence(s, k + 1, j, split, sequence);
        }
    }

    // Approach 5: Greedy with Backtracking - O(2^n) time, O(n) space
    public int strangePrinterGreedy(String s) {
        if (s.length() == 0)
            return 0;

        return greedyHelper(s, new ArrayList<>(), 0);
    }

    private int greedyHelper(String target, List<String> current, int minTurns) {
        if (Arrays.equals(target.toCharArray(), getCurrentState(current, target.length()).toCharArray())) {
            return current.size();
        }

        if (current.size() >= minTurns)
            return Integer.MAX_VALUE;

        int result = Integer.MAX_VALUE;

        // Try all possible printing operations
        for (char c = 'a'; c <= 'z'; c++) {
            for (int start = 0; start < target.length(); start++) {
                for (int end = start; end < target.length(); end++) {
                    String operation = c + ":" + start + "-" + end;
                    current.add(operation);

                    int turns = greedyHelper(target, current, Math.min(minTurns, result));
                    result = Math.min(result, turns);

                    current.remove(current.size() - 1);
                }
            }
        }

        return result;
    }

    private String getCurrentState(List<String> operations, int length) {
        char[] state = new char[length];
        Arrays.fill(state, ' ');

        for (String op : operations) {
            String[] parts = op.split(":");
            char c = parts[0].charAt(0);
            String[] range = parts[1].split("-");
            int start = Integer.parseInt(range[0]);
            int end = Integer.parseInt(range[1]);

            for (int i = start; i <= end; i++) {
                state[i] = c;
            }
        }

        return new String(state);
    }

    public static void main(String[] args) {
        StrangePrinter solution = new StrangePrinter();

        System.out.println("=== Strange Printer Test Cases ===");

        // Test Case 1: Example from problem
        String s1 = "aaabbb";
        System.out.println("Test 1 - String: \"" + s1 + "\"");
        System.out.println("Interval DP: " + solution.strangePrinter(s1));
        System.out.println("Memoization: " + solution.strangePrinterMemo(s1));
        System.out.println("Optimized: " + solution.strangePrinterOptimized(s1));

        List<String> sequence1 = solution.getPrintingSequence(s1);
        System.out.println("Printing sequence:");
        for (String step : sequence1) {
            System.out.println("  " + step);
        }
        System.out.println("Expected: 2\n");

        // Test Case 2: Complex pattern
        String s2 = "aba";
        System.out.println("Test 2 - String: \"" + s2 + "\"");
        System.out.println("Interval DP: " + solution.strangePrinter(s2));
        System.out.println("Expected: 2\n");

        // Test Case 3: All same characters
        String s3 = "aaaa";
        System.out.println("Test 3 - String: \"" + s3 + "\"");
        System.out.println("Optimized: " + solution.strangePrinterOptimized(s3));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        StrangePrinter solution = new StrangePrinter();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append((char) ('a' + (i % 3)));
        }
        String testString = sb.toString();

        System.out.println("=== Performance Test (String length: " + testString.length() + ") ===");

        long start = System.nanoTime();
        int result1 = solution.strangePrinter(testString);
        long end = System.nanoTime();
        System.out.println("Interval DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.strangePrinterMemo(testString);
        end = System.nanoTime();
        System.out.println("Memoization: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.strangePrinterOptimized(testString);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
