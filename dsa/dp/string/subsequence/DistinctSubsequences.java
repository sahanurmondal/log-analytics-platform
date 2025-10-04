package dp.string.subsequence;

import java.util.Arrays;

/**
 * LeetCode 115: Distinct Subsequences
 * https://leetcode.com/problems/distinct-subsequences/
 *
 * Description:
 * Given two strings s and t, return the number of distinct subsequences of s
 * which equals t.
 * A string's subsequence is a new string formed from the original string by
 * deleting some (can be none)
 * of the characters without disturbing the relative positions of the remaining
 * characters.
 *
 * Constraints:
 * - 1 <= s.length, t.length <= 1000
 * - s and t consist of English letters.
 *
 * Follow-up:
 * - Can you solve it in O(n) space?
 * - What if we need to handle very large numbers?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard (but in medium folder per prompt)
 */
public class DistinctSubsequences {

    // Approach 1: Recursive (Brute Force) - O(2^n) time, O(n) space
    public int numDistinctRecursive(String s, String t) {
        return numDistinctHelper(s, t, 0, 0);
    }

    private int numDistinctHelper(String s, String t, int i, int j) {
        // Base case: t is fully matched
        if (j == t.length())
            return 1;

        // Base case: s is exhausted but t is not
        if (i == s.length())
            return 0;

        int result = 0;

        // Always have option to skip current character in s
        result += numDistinctHelper(s, t, i + 1, j);

        // If characters match, we can use current character
        if (s.charAt(i) == t.charAt(j)) {
            result += numDistinctHelper(s, t, i + 1, j + 1);
        }

        return result;
    }

    // Approach 2: Memoization (Top-down DP) - O(m*n) time, O(m*n) space
    public int numDistinctMemo(String s, String t) {
        int m = s.length(), n = t.length();
        Integer[][] memo = new Integer[m][n];
        return numDistinctMemoHelper(s, t, 0, 0, memo);
    }

    private int numDistinctMemoHelper(String s, String t, int i, int j, Integer[][] memo) {
        if (j == t.length())
            return 1;
        if (i == s.length())
            return 0;

        if (memo[i][j] != null)
            return memo[i][j];

        int result = numDistinctMemoHelper(s, t, i + 1, j, memo);

        if (s.charAt(i) == t.charAt(j)) {
            result += numDistinctMemoHelper(s, t, i + 1, j + 1, memo);
        }

        memo[i][j] = result;
        return result;
    }

    // Approach 3: Tabulation (Bottom-up DP) - O(m*n) time, O(m*n) space
    public int numDistinctDP(String s, String t) {
        int m = s.length(), n = t.length();
        long[][] dp = new long[m + 1][n + 1];

        // Base case: empty t can be formed in 1 way
        for (int i = 0; i <= m; i++) {
            dp[i][0] = 1;
        }

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Always can skip current character in s
                dp[i][j] = dp[i - 1][j];

                // If characters match, add ways using current character
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] += dp[i - 1][j - 1];
                }
            }
        }

        return (int) dp[m][n];
    }

    // Approach 4: Space Optimized - O(m*n) time, O(n) space
    public int numDistinctOptimized(String s, String t) {
        int m = s.length(), n = t.length();
        long[] prev = new long[n + 1];
        long[] curr = new long[n + 1];

        prev[0] = 1;

        for (int i = 1; i <= m; i++) {
            curr[0] = 1;

            for (int j = 1; j <= n; j++) {
                curr[j] = prev[j];

                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    curr[j] += prev[j - 1];
                }
            }

            long[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return (int) prev[n];
    }

    // Approach 5: 1D Space Optimized - O(m*n) time, O(n) space
    public int numDistinct1D(String s, String t) {
        int m = s.length(), n = t.length();
        long[] dp = new long[n + 1];

        dp[0] = 1;

        for (int i = 1; i <= m; i++) {
            // Process from right to left to avoid overwriting needed values
            for (int j = n; j >= 1; j--) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[j] += dp[j - 1];
                }
            }
        }

        return (int) dp[n];
    }

    public static void main(String[] args) {
        DistinctSubsequences solution = new DistinctSubsequences();

        System.out.println("=== Distinct Subsequences Test Cases ===");

        // Test Case 1: Example from problem
        String s1 = "rabbbit", t1 = "rabbit";
        System.out.println("Test 1 - s: \"" + s1 + "\", t: \"" + t1 + "\"");
        System.out.println("Recursive: " + solution.numDistinctRecursive(s1, t1));
        System.out.println("Memoization: " + solution.numDistinctMemo(s1, t1));
        System.out.println("DP: " + solution.numDistinctDP(s1, t1));
        System.out.println("Optimized: " + solution.numDistinctOptimized(s1, t1));
        System.out.println("1D: " + solution.numDistinct1D(s1, t1));
        System.out.println("Expected: 3\n");

        // Test Case 2: Another example
        String s2 = "babgbag", t2 = "bag";
        System.out.println("Test 2 - s: \"" + s2 + "\", t: \"" + t2 + "\"");
        System.out.println("DP: " + solution.numDistinctDP(s2, t2));
        System.out.println("Expected: 5\n");

        // Test Case 3: No match
        String s3 = "abc", t3 = "def";
        System.out.println("Test 3 - s: \"" + s3 + "\", t: \"" + t3 + "\"");
        System.out.println("DP: " + solution.numDistinctDP(s3, t3));
        System.out.println("Expected: 0\n");

        // Test Case 4: Same strings
        String s4 = "abc", t4 = "abc";
        System.out.println("Test 4 - s: \"" + s4 + "\", t: \"" + t4 + "\"");
        System.out.println("DP: " + solution.numDistinctDP(s4, t4));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        DistinctSubsequences solution = new DistinctSubsequences();

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        for (int i = 0; i < 200; i++) {
            sb1.append((char) ('a' + (i % 3)));
        }

        for (int i = 0; i < 50; i++) {
            sb2.append((char) ('a' + (i % 3)));
        }

        String testS = sb1.toString();
        String testT = sb2.toString();

        System.out.println("=== Performance Test ===");
        System.out.println("s length: " + testS.length() + ", t length: " + testT.length());

        long start = System.nanoTime();
        int result1 = solution.numDistinctMemo(testS, testT);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.numDistinctDP(testS, testT);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.numDistinct1D(testS, testT);
        end = System.nanoTime();
        System.out.println("1D Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
