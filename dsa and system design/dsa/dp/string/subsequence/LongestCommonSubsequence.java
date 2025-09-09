package dp.string.subsequence;

/**
 * LeetCode 1143: Longest Common Subsequence
 * https://leetcode.com/problems/longest-common-subsequence/
 *
 * Description:
 * Given two strings text1 and text2, return the length of their longest common
 * subsequence.
 *
 * Constraints:
 * - 1 <= text1.length, text2.length <= 1000
 * - text1 and text2 consist of only lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it in O(n*m) time?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class LongestCommonSubsequence {

    // Approach 1: 2D DP - O(m*n) time, O(m*n) space
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();

        // dp[i][j] = LCS length of text1[0..i-1] and text2[0..j-1]
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    // Approach 2: Space Optimized DP - O(m*n) time, O(min(m,n)) space
    public int longestCommonSubsequenceOptimized(String text1, String text2) {
        // Make text1 the shorter string to optimize space
        if (text1.length() > text2.length()) {
            return longestCommonSubsequenceOptimized(text2, text1);
        }

        int m = text1.length();
        int n = text2.length();

        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];

        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    curr[i] = prev[i - 1] + 1;
                } else {
                    curr[i] = Math.max(prev[i], curr[i - 1]);
                }
            }
            // Swap arrays
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[m];
    }

    // Approach 3: Recursive with Memoization - O(m*n) time, O(m*n) space
    public int longestCommonSubsequenceMemo(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        Integer[][] memo = new Integer[m][n];

        return lcsRecursive(text1, text2, 0, 0, memo);
    }

    private int lcsRecursive(String text1, String text2, int i, int j, Integer[][] memo) {
        if (i == text1.length() || j == text2.length()) {
            return 0;
        }

        if (memo[i][j] != null) {
            return memo[i][j];
        }

        if (text1.charAt(i) == text2.charAt(j)) {
            memo[i][j] = 1 + lcsRecursive(text1, text2, i + 1, j + 1, memo);
        } else {
            memo[i][j] = Math.max(
                    lcsRecursive(text1, text2, i + 1, j, memo),
                    lcsRecursive(text1, text2, i, j + 1, memo));
        }

        return memo[i][j];
    }

    // Approach 4: Print the actual LCS - O(m*n) time, O(m*n) space
    public String printLCS(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Build DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        // Backtrack to build LCS
        StringBuilder lcs = new StringBuilder();
        int i = m, j = n;

        while (i > 0 && j > 0) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                lcs.append(text1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return lcs.reverse().toString();
    }

    // Approach 5: Rolling Array Optimization - O(m*n) time, O(2*min(m,n)) space
    public int longestCommonSubsequenceRolling(String text1, String text2) {
        if (text1.length() > text2.length()) {
            return longestCommonSubsequenceRolling(text2, text1);
        }

        int m = text1.length();
        int n = text2.length();

        // Use only two rows
        int[][] dp = new int[2][m + 1];

        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[j % 2][i] = dp[(j - 1) % 2][i - 1] + 1;
                } else {
                    dp[j % 2][i] = Math.max(dp[(j - 1) % 2][i], dp[j % 2][i - 1]);
                }
            }
        }

        return dp[n % 2][m];
    }

    public static void main(String[] args) {
        LongestCommonSubsequence solution = new LongestCommonSubsequence();

        System.out.println("=== Longest Common Subsequence Test Cases ===");

        // Test case 1: "abcde" and "ace"
        String text1 = "abcde", text2 = "ace";
        System.out.println("Text1: \"" + text1 + "\", Text2: \"" + text2 + "\"");
        System.out.println("2D DP: " + solution.longestCommonSubsequence(text1, text2)); // Expected: 3
        System.out.println("Optimized: " + solution.longestCommonSubsequenceOptimized(text1, text2)); // Expected: 3
        System.out.println("Memoization: " + solution.longestCommonSubsequenceMemo(text1, text2)); // Expected: 3
        System.out.println("LCS String: \"" + solution.printLCS(text1, text2) + "\""); // Expected: "ace"

        // Test case 2: "abc" and "abc"
        text1 = "abc";
        text2 = "abc";
        System.out.println("\nText1: \"" + text1 + "\", Text2: \"" + text2 + "\"");
        System.out.println("2D DP: " + solution.longestCommonSubsequence(text1, text2)); // Expected: 3
        System.out.println("LCS String: \"" + solution.printLCS(text1, text2) + "\""); // Expected: "abc"

        // Test case 3: "abc" and "def"
        text1 = "abc";
        text2 = "def";
        System.out.println("\nText1: \"" + text1 + "\", Text2: \"" + text2 + "\"");
        System.out.println("2D DP: " + solution.longestCommonSubsequence(text1, text2)); // Expected: 0
        System.out.println("LCS String: \"" + solution.printLCS(text1, text2) + "\""); // Expected: ""

        // Test case 4: "bl" and "yby"
        text1 = "bl";
        text2 = "yby";
        System.out.println("\nText1: \"" + text1 + "\", Text2: \"" + text2 + "\"");
        System.out.println("2D DP: " + solution.longestCommonSubsequence(text1, text2)); // Expected: 1
        System.out.println("LCS String: \"" + solution.printLCS(text1, text2) + "\""); // Expected: "b"

        // Test case 5: Complex case
        text1 = "bsbininm";
        text2 = "jmjkbkjkv";
        System.out.println("\nText1: \"" + text1 + "\", Text2: \"" + text2 + "\"");
        System.out.println("2D DP: " + solution.longestCommonSubsequence(text1, text2)); // Expected: 1
        System.out.println("Rolling: " + solution.longestCommonSubsequenceRolling(text1, text2)); // Expected: 1

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Comparison ===");
        LongestCommonSubsequence solution = new LongestCommonSubsequence();

        // Create large strings for testing
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        for (int i = 0; i < 500; i++) {
            sb1.append((char) ('a' + (i % 26)));
            sb2.append((char) ('a' + ((i + 5) % 26)));
        }

        String largeText1 = sb1.toString();
        String largeText2 = sb2.toString();

        long startTime, endTime;

        // Test 2D DP approach
        startTime = System.nanoTime();
        int result1 = solution.longestCommonSubsequence(largeText1, largeText2);
        endTime = System.nanoTime();
        System.out.println("2D DP: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test optimized approach
        startTime = System.nanoTime();
        int result2 = solution.longestCommonSubsequenceOptimized(largeText1, largeText2);
        endTime = System.nanoTime();
        System.out.println("Optimized: " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test memoization approach
        startTime = System.nanoTime();
        int result3 = solution.longestCommonSubsequenceMemo(largeText1, largeText2);
        endTime = System.nanoTime();
        System.out.println("Memoization: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test rolling array approach
        startTime = System.nanoTime();
        int result4 = solution.longestCommonSubsequenceRolling(largeText1, largeText2);
        endTime = System.nanoTime();
        System.out.println("Rolling: " + result4 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }
}
