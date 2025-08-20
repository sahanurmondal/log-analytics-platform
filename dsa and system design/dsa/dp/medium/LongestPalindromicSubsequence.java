package dp.medium;

/**
 * LeetCode 516: Longest Palindromic Subsequence
 * https://leetcode.com/problems/longest-palindromic-subsequence/
 *
 * Description:
 * Given a string s, find the longest palindromic subsequence's length in s.
 * A subsequence is a sequence that can be derived from another sequence by
 * deleting some or no elements
 * without changing the order of the remaining elements.
 *
 * Constraints:
 * - 1 <= s.length <= 1000
 * - s consists only of lowercase English letters.
 *
 * Follow-up:
 * - Could you solve it in O(n) space?
 * - Can you find the actual palindromic subsequence?
 *
 * Company Tags: Google, Microsoft, Amazon, Facebook, Apple, Bloomberg
 * Difficulty: Medium
 */
public class LongestPalindromicSubsequence {

    // Approach 1: Recursive (Brute Force) - O(2^n) time, O(n) space
    public int longestPalindromeSubseqRecursive(String s) {
        return longestPalindromeHelper(s, 0, s.length() - 1);
    }

    private int longestPalindromeHelper(String s, int left, int right) {
        if (left > right)
            return 0;
        if (left == right)
            return 1;

        if (s.charAt(left) == s.charAt(right)) {
            return 2 + longestPalindromeHelper(s, left + 1, right - 1);
        } else {
            return Math.max(longestPalindromeHelper(s, left + 1, right),
                    longestPalindromeHelper(s, left, right - 1));
        }
    }

    // Approach 2: Memoization (Top-down DP) - O(n^2) time, O(n^2) space
    public int longestPalindromeSubseqMemo(String s) {
        int n = s.length();
        Integer[][] memo = new Integer[n][n];
        return longestPalindromeMemoHelper(s, 0, n - 1, memo);
    }

    private int longestPalindromeMemoHelper(String s, int left, int right, Integer[][] memo) {
        if (left > right)
            return 0;
        if (left == right)
            return 1;

        if (memo[left][right] != null)
            return memo[left][right];

        if (s.charAt(left) == s.charAt(right)) {
            memo[left][right] = 2 + longestPalindromeMemoHelper(s, left + 1, right - 1, memo);
        } else {
            memo[left][right] = Math.max(longestPalindromeMemoHelper(s, left + 1, right, memo),
                    longestPalindromeMemoHelper(s, left, right - 1, memo));
        }

        return memo[left][right];
    }

    // Approach 3: Tabulation (Bottom-up DP) - O(n^2) time, O(n^2) space
    public int longestPalindromeSubseqDP(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];

        // Every single character is a palindrome of length 1
        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
        }

        // Fill for lengths 2 to n
        for (int length = 2; length <= n; length++) {
            for (int i = 0; i <= n - length; i++) {
                int j = i + length - 1;

                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 2 + dp[i + 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[0][n - 1];
    }

    // Approach 4: Space Optimized - O(n^2) time, O(n) space
    public int longestPalindromeSubseqOptimized(String s) {
        int n = s.length();
        int[] prev = new int[n];
        int[] curr = new int[n];

        // Initialize for length 1
        for (int i = 0; i < n; i++) {
            prev[i] = 1;
        }

        for (int length = 2; length <= n; length++) {
            for (int i = 0; i <= n - length; i++) {
                int j = i + length - 1;

                if (s.charAt(i) == s.charAt(j)) {
                    curr[i] = 2 + (length == 2 ? 0 : prev[i + 1]);
                } else {
                    curr[i] = Math.max(prev[i], curr[i + 1]);
                }
            }
            prev = curr.clone();
        }

        return prev[0];
    }

    // Approach 5: LCS with Reverse String - O(n^2) time, O(n^2) space
    public int longestPalindromeSubseqLCS(String s) {
        String reversed = new StringBuilder(s).reverse().toString();
        return longestCommonSubsequence(s, reversed);
    }

    private int longestCommonSubsequence(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    // Bonus: Get the actual palindromic subsequence
    public String getLongestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];

        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
        }

        for (int length = 2; length <= n; length++) {
            for (int i = 0; i <= n - length; i++) {
                int j = i + length - 1;

                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 2 + dp[i + 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }

        return constructPalindrome(s, dp, 0, n - 1);
    }

    private String constructPalindrome(String s, int[][] dp, int left, int right) {
        if (left > right)
            return "";
        if (left == right)
            return String.valueOf(s.charAt(left));

        if (s.charAt(left) == s.charAt(right)) {
            return s.charAt(left) + constructPalindrome(s, dp, left + 1, right - 1) + s.charAt(right);
        } else if (dp[left + 1][right] > dp[left][right - 1]) {
            return constructPalindrome(s, dp, left + 1, right);
        } else {
            return constructPalindrome(s, dp, left, right - 1);
        }
    }

    public static void main(String[] args) {
        LongestPalindromicSubsequence solution = new LongestPalindromicSubsequence();

        System.out.println("=== Longest Palindromic Subsequence Test Cases ===");

        // Test Case 1: Example from problem
        String s1 = "bbbab";
        System.out.println("Test 1 - String: " + s1);
        System.out.println("Recursive: " + solution.longestPalindromeSubseqRecursive(s1));
        System.out.println("Memoization: " + solution.longestPalindromeSubseqMemo(s1));
        System.out.println("DP: " + solution.longestPalindromeSubseqDP(s1));
        System.out.println("Optimized: " + solution.longestPalindromeSubseqOptimized(s1));
        System.out.println("LCS: " + solution.longestPalindromeSubseqLCS(s1));
        System.out.println("Actual subsequence: " + solution.getLongestPalindromeSubseq(s1));
        System.out.println("Expected: 4\n");

        // Test Case 2: Another example
        String s2 = "cbbd";
        System.out.println("Test 2 - String: " + s2);
        System.out.println("DP: " + solution.longestPalindromeSubseqDP(s2));
        System.out.println("Actual subsequence: " + solution.getLongestPalindromeSubseq(s2));
        System.out.println("Expected: 2\n");

        // Test Case 3: Single character
        String s3 = "a";
        System.out.println("Test 3 - String: " + s3);
        System.out.println("DP: " + solution.longestPalindromeSubseqDP(s3));
        System.out.println("Expected: 1\n");

        // Test Case 4: All same characters
        String s4 = "aaaa";
        System.out.println("Test 4 - String: " + s4);
        System.out.println("DP: " + solution.longestPalindromeSubseqDP(s4));
        System.out.println("Expected: 4\n");

        performanceTest();
    }

    private static void performanceTest() {
        LongestPalindromicSubsequence solution = new LongestPalindromicSubsequence();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append((char) ('a' + (int) (Math.random() * 26)));
        }
        String testString = sb.toString();

        System.out.println("=== Performance Test (String length: " + testString.length() + ") ===");

        long start = System.nanoTime();
        int result1 = solution.longestPalindromeSubseqMemo(testString);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.longestPalindromeSubseqDP(testString);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.longestPalindromeSubseqLCS(testString);
        end = System.nanoTime();
        System.out.println("LCS: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
