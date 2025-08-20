package dp.hard;

import java.util.*;

/**
 * LeetCode 1312: Minimum Insertion Steps to Make a String Palindrome
 * https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/
 *
 * Description:
 * Given a string s. In one step you can insert any character at any index of
 * the string.
 * Return the minimum number of steps to make s palindrome.
 * A Palindrome String is one that reads the same backward as well as forward.
 *
 * Constraints:
 * - 1 <= s.length <= 500
 * - s consists of lowercase English letters.
 *
 * Follow-up:
 * - Can you find the actual insertions needed?
 * - What if we can also delete characters?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class MinimumInsertionStepsToMakeStringPalindrome {

    // Approach 1: LCS-based DP - O(n^2) time, O(n^2) space
    public int minInsertions(String s) {
        String reversed = new StringBuilder(s).reverse().toString();
        int lcs = longestCommonSubsequence(s, reversed);
        return s.length() - lcs;
    }

    private int longestCommonSubsequence(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    // Approach 2: Interval DP - O(n^2) time, O(n^2) space
    public int minInsertionsInterval(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];

        // For length 2 strings
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) != s.charAt(i + 1)) {
                dp[i][i + 1] = 1;
            }
        }

        // For length 3 and more
        for (int len = 3; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;

                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = dp[i + 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i + 1][j], dp[i][j - 1]) + 1;
                }
            }
        }

        return dp[0][n - 1];
    }

    // Approach 3: Memoization - O(n^2) time, O(n^2) space
    public int minInsertionsMemo(String s) {
        Integer[][] memo = new Integer[s.length()][s.length()];
        return minInsertionsMemoHelper(s, 0, s.length() - 1, memo);
    }

    private int minInsertionsMemoHelper(String s, int i, int j, Integer[][] memo) {
        if (i >= j)
            return 0;

        if (memo[i][j] != null)
            return memo[i][j];

        if (s.charAt(i) == s.charAt(j)) {
            memo[i][j] = minInsertionsMemoHelper(s, i + 1, j - 1, memo);
        } else {
            int insertLeft = 1 + minInsertionsMemoHelper(s, i, j - 1, memo);
            int insertRight = 1 + minInsertionsMemoHelper(s, i + 1, j, memo);
            memo[i][j] = Math.min(insertLeft, insertRight);
        }

        return memo[i][j];
    }

    // Approach 4: Space Optimized DP - O(n^2) time, O(n) space
    public int minInsertionsOptimized(String s) {
        int n = s.length();
        int[] prev = new int[n];
        int[] curr = new int[n];

        for (int len = 2; len <= n; len++) {
            Arrays.fill(curr, 0);

            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;

                if (len == 2) {
                    curr[i] = (s.charAt(i) != s.charAt(j)) ? 1 : 0;
                } else {
                    if (s.charAt(i) == s.charAt(j)) {
                        curr[i] = prev[i + 1];
                    } else {
                        curr[i] = Math.min(prev[i + 1], prev[i]) + 1;
                    }
                }
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return n == 1 ? 0 : prev[0];
    }

    // Approach 5: Get Actual Insertions - O(n^2) time, O(n^2) space
    public String getResultingPalindrome(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];

        // Fill DP table
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) != s.charAt(i + 1)) {
                dp[i][i + 1] = 1;
            }
        }

        for (int len = 3; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;

                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = dp[i + 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i + 1][j], dp[i][j - 1]) + 1;
                }
            }
        }

        // Reconstruct palindrome
        return reconstructPalindrome(s, 0, n - 1, dp);
    }

    private String reconstructPalindrome(String s, int i, int j, int[][] dp) {
        if (i > j)
            return "";
        if (i == j)
            return String.valueOf(s.charAt(i));

        if (s.charAt(i) == s.charAt(j)) {
            return s.charAt(i) + reconstructPalindrome(s, i + 1, j - 1, dp) + s.charAt(j);
        } else if (dp[i + 1][j] < dp[i][j - 1]) {
            // Insert s[i] at the end
            return s.charAt(i) + reconstructPalindrome(s, i + 1, j, dp) + s.charAt(i);
        } else {
            // Insert s[j] at the beginning
            return s.charAt(j) + reconstructPalindrome(s, i, j - 1, dp) + s.charAt(j);
        }
    }

    public static void main(String[] args) {
        MinimumInsertionStepsToMakeStringPalindrome solution = new MinimumInsertionStepsToMakeStringPalindrome();

        System.out.println("=== Minimum Insertion Steps to Make String Palindrome Test Cases ===");

        // Test Case 1: Example from problem
        String s1 = "zzazz";
        System.out.println("Test 1 - String: \"" + s1 + "\"");
        System.out.println("LCS-based: " + solution.minInsertions(s1));
        System.out.println("Interval DP: " + solution.minInsertionsInterval(s1));
        System.out.println("Memoization: " + solution.minInsertionsMemo(s1));
        System.out.println("Optimized: " + solution.minInsertionsOptimized(s1));
        System.out.println("Resulting palindrome: \"" + solution.getResultingPalindrome(s1) + "\"");
        System.out.println("Expected: 0\n");

        // Test Case 2: More complex
        String s2 = "mbadm";
        System.out.println("Test 2 - String: \"" + s2 + "\"");
        System.out.println("Interval DP: " + solution.minInsertionsInterval(s2));
        System.out.println("Resulting palindrome: \"" + solution.getResultingPalindrome(s2) + "\"");
        System.out.println("Expected: 2\n");

        // Test Case 3: Single character
        String s3 = "a";
        System.out.println("Test 3 - String: \"" + s3 + "\"");
        System.out.println("Interval DP: " + solution.minInsertionsInterval(s3));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void performanceTest() {
        MinimumInsertionStepsToMakeStringPalindrome solution = new MinimumInsertionStepsToMakeStringPalindrome();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append((char) ('a' + (int) (Math.random() * 26)));
        }
        String testString = sb.toString();

        System.out.println("=== Performance Test (String length: " + testString.length() + ") ===");

        long start = System.nanoTime();
        int result1 = solution.minInsertions(testString);
        long end = System.nanoTime();
        System.out.println("LCS-based: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.minInsertionsInterval(testString);
        end = System.nanoTime();
        System.out.println("Interval DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minInsertionsOptimized(testString);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
