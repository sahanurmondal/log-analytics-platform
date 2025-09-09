package dp.advanced;

import java.util.*;

/**
 * LeetCode 115: Distinct Subsequences
 * https://leetcode.com/problems/distinct-subsequences/
 *
 * Description:
 * Given two strings s and t, return the number of distinct subsequences of s
 * which equals t.
 * A string's subsequence is a new string formed from the original string by
 * deleting some (can be none) of the characters
 * without disturbing the relative positions of the remaining characters.
 *
 * Constraints:
 * - 1 <= s.length, t.length <= 1000
 * - s and t consist of English letters.
 *
 * Follow-up:
 * - Can you solve it in O(t.length) space?
 * - What if we need to find all distinct subsequences?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class DistinctSubsequences {

    // Approach 1: 2D DP - O(m*n) time, O(m*n) space
    public int numDistinct(String s, String t) {
        int m = s.length(), n = t.length();
        long[][] dp = new long[m + 1][n + 1];

        // Base case: empty t can be formed by any s in 1 way
        for (int i = 0; i <= m; i++) {
            dp[i][0] = 1;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Don't use current character from s
                dp[i][j] = dp[i - 1][j];

                // Use current character from s if it matches
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] += dp[i - 1][j - 1];
                }
            }
        }

        return (int) dp[m][n];
    }

    // Approach 2: Space Optimized DP - O(m*n) time, O(n) space
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

    // Approach 3: 1D DP with Reverse Iteration - O(m*n) time, O(n) space
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

    // Approach 4: Memoization - O(m*n) time, O(m*n) space
    public int numDistinctMemo(String s, String t) {
        Long[][] memo = new Long[s.length()][t.length()];
        return (int) numDistinctMemoHelper(s, t, 0, 0, memo);
    }

    private long numDistinctMemoHelper(String s, String t, int i, int j, Long[][] memo) {
        if (j == t.length())
            return 1; // Found complete subsequence
        if (i == s.length())
            return 0; // Ran out of characters in s

        if (memo[i][j] != null)
            return memo[i][j];

        long result = numDistinctMemoHelper(s, t, i + 1, j, memo); // Skip current char in s

        if (s.charAt(i) == t.charAt(j)) {
            result += numDistinctMemoHelper(s, t, i + 1, j + 1, memo); // Use current char
        }

        memo[i][j] = result;
        return result;
    }

    // Approach 5: Get All Distinct Subsequences - O(2^m) time, O(2^m) space
    public List<String> getAllDistinctSubsequences(String s, String t) {
        Set<String> result = new HashSet<>();
        getAllSubsequencesHelper(s, t, 0, 0, new StringBuilder(), result);
        return new ArrayList<>(result);
    }

    private void getAllSubsequencesHelper(String s, String t, int sIndex, int tIndex,
            StringBuilder current, Set<String> result) {
        if (tIndex == t.length()) {
            result.add(current.toString());
            return;
        }

        if (sIndex >= s.length())
            return;

        // Try all remaining characters in s
        for (int i = sIndex; i < s.length(); i++) {
            if (s.charAt(i) == t.charAt(tIndex)) {
                current.append(s.charAt(i));
                getAllSubsequencesHelper(s, t, i + 1, tIndex + 1, current, result);
                current.deleteCharAt(current.length() - 1);
            }
        }
    }

    public static void main(String[] args) {
        DistinctSubsequences solution = new DistinctSubsequences();

        System.out.println("=== Distinct Subsequences Test Cases ===");

        // Test Case 1: Example from problem
        String s1 = "rabbbit", t1 = "rabbit";
        System.out.println("Test 1 - s: \"" + s1 + "\", t: \"" + t1 + "\"");
        System.out.println("2D DP: " + solution.numDistinct(s1, t1));
        System.out.println("Optimized: " + solution.numDistinctOptimized(s1, t1));
        System.out.println("1D DP: " + solution.numDistinct1D(s1, t1));
        System.out.println("Memoization: " + solution.numDistinctMemo(s1, t1));

        List<String> subsequences1 = solution.getAllDistinctSubsequences(s1, t1);
        System.out.println("All subsequences (" + subsequences1.size() + " total):");
        for (String subseq : subsequences1) {
            System.out.println("  \"" + subseq + "\"");
        }
        System.out.println("Expected: 3\n");

        // Test Case 2: Another example
        String s2 = "babgbag", t2 = "bag";
        System.out.println("Test 2 - s: \"" + s2 + "\", t: \"" + t2 + "\"");
        System.out.println("1D DP: " + solution.numDistinct1D(s2, t2));
        System.out.println("Expected: 5\n");

        performanceTest();
    }

    private static void performanceTest() {
        DistinctSubsequences solution = new DistinctSubsequences();

        String s = "a".repeat(500) + "b".repeat(500);
        String t = "a".repeat(50) + "b".repeat(50);

        System.out.println("=== Performance Test (s length: " + s.length() + ", t length: " + t.length() + ") ===");

        long start = System.nanoTime();
        int result1 = solution.numDistinct(s, t);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.numDistinctOptimized(s, t);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.numDistinct1D(s, t);
        end = System.nanoTime();
        System.out.println("1D DP: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
