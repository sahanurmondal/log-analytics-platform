package strings.hard;

import java.util.*;

/**
 * LeetCode 115: Distinct Subsequences
 * https://leetcode.com/problems/distinct-subsequences/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given two strings s and t, return the number of distinct
 * subsequences of s which equals t.
 *
 * Constraints:
 * - 1 <= s.length, t.length <= 1000
 * - s and t consist of English letters
 * 
 * Follow-up Questions:
 * 1. Can you handle case-insensitive matching?
 * 2. Can you count distinct subsequences with wildcards?
 * 3. Can you optimize for large strings?
 */
public class DistinctSubsequences {

    // Approach 1: Dynamic Programming - O(m*n) time, O(m*n) space
    public int numDistinct(String s, String t) {
        int m = s.length(), n = t.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++)
            dp[i][0] = 1;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = dp[i - 1][j];
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] += dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    // Approach 2: Space optimized DP - O(m*n) time, O(n) space
    public int numDistinctOptimized(String s, String t) {
        int m = s.length(), n = t.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        for (int i = 1; i <= m; i++) {
            for (int j = Math.min(i, n); j >= 1; j--) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[j] += dp[j - 1];
                }
            }
        }
        return dp[n];
    }

    // Follow-up 1: Case-insensitive matching
    public int numDistinctIgnoreCase(String s, String t) {
        return numDistinct(s.toLowerCase(), t.toLowerCase());
    }

    // Follow-up 2: With wildcards ('?' matches any character)
    public int numDistinctWithWildcard(String s, String t) {
        int m = s.length(), n = t.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++)
            dp[i][0] = 1;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = dp[i - 1][j];
                if (s.charAt(i - 1) == t.charAt(j - 1) ||
                        s.charAt(i - 1) == '?' || t.charAt(j - 1) == '?') {
                    dp[i][j] += dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        DistinctSubsequences solution = new DistinctSubsequences();

        // Test case 1: Basic case
        String s1 = "rabbbit", t1 = "rabbit";
        System.out.println("Test 1 - s: " + s1 + ", t: " + t1 + " Expected: 3");
        System.out.println("Result: " + solution.numDistinct(s1, t1));
        System.out.println("Optimized: " + solution.numDistinctOptimized(s1, t1));

        // Test case 2: No match
        String s2 = "abc", t2 = "def";
        System.out.println("\nTest 2 - No match:");
        System.out.println("Result: " + solution.numDistinct(s2, t2));

        // Test case 3: Case-insensitive
        String s3 = "RaBbBiT", t3 = "rabbit";
        System.out.println("\nTest 3 - Case-insensitive:");
        System.out.println("Result: " + solution.numDistinctIgnoreCase(s3, t3));

        // Test case 4: With wildcards
        String s4 = "ra?b?it", t4 = "rabbit";
        System.out.println("\nTest 4 - With wildcards:");
        System.out.println("Result: " + solution.numDistinctWithWildcard(s4, t4));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty t: " + solution.numDistinct("abc", ""));
        System.out.println("Empty s: " + solution.numDistinct("", "abc"));
        System.out.println("Single char match: " + solution.numDistinct("a", "a"));
        System.out.println("Repeated chars: " + solution.numDistinct("aaa", "a"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sbS = new StringBuilder();
        StringBuilder sbT = new StringBuilder();
        for (int i = 0; i < 500; i++)
            sbS.append("ab");
        for (int i = 0; i < 250; i++)
            sbT.append("ab");
        long start = System.nanoTime();
        int result = solution.numDistinctOptimized(sbS.toString(), sbT.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
