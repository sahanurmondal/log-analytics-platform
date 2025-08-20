package dp.hard;

import java.util.Arrays;

/**
 * LeetCode 132: Palindrome Partitioning II
 * https://leetcode.com/problems/palindrome-partitioning-ii/
 *
 * Description:
 * Given a string s, partition s such that every substring of the partition is a
 * palindrome.
 * Return the minimum cuts needed for a palindrome partitioning of s.
 *
 * Constraints:
 * - 1 <= s.length <= 2000
 * - s consists of lowercase English letters only.
 *
 * Follow-up:
 * - Can you solve it in O(n) space?
 * - What if we need to find the actual partitioning?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class PalindromePartitioningII {

    // Approach 1: DP with Palindrome Precomputation - O(n^2) time, O(n^2) space
    public int minCutDP(String s) {
        int n = s.length();
        boolean[][] isPalindrome = new boolean[n][n];

        // Precompute palindromes
        for (int i = 0; i < n; i++) {
            isPalindrome[i][i] = true;
        }

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                if (s.charAt(i) == s.charAt(j)) {
                    isPalindrome[i][j] = (len == 2) || isPalindrome[i + 1][j - 1];
                }
            }
        }

        // DP for minimum cuts
        int[] dp = new int[n];
        for (int i = 0; i < n; i++) {
            if (isPalindrome[0][i]) {
                dp[i] = 0;
            } else {
                dp[i] = i; // worst case: i cuts
                for (int j = 1; j <= i; j++) {
                    if (isPalindrome[j][i]) {
                        dp[i] = Math.min(dp[i], dp[j - 1] + 1);
                    }
                }
            }
        }

        return dp[n - 1];
    }

    // Approach 2: Expand Around Centers - O(n^2) time, O(n) space
    public int minCutExpandCenter(String s) {
        int n = s.length();
        int[] dp = new int[n];

        for (int i = 0; i < n; i++) {
            dp[i] = i; // worst case
        }

        for (int center = 0; center < n; center++) {
            // Odd length palindromes
            expandAndUpdate(s, center, center, dp);

            // Even length palindromes
            if (center + 1 < n) {
                expandAndUpdate(s, center, center + 1, dp);
            }
        }

        return dp[n - 1];
    }

    private void expandAndUpdate(String s, int left, int right, int[] dp) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            if (left == 0) {
                dp[right] = 0;
            } else {
                dp[right] = Math.min(dp[right], dp[left - 1] + 1);
            }
            left--;
            right++;
        }
    }

    // Approach 3: Memoization - O(n^2) time, O(n^2) space
    public int minCutMemo(String s) {
        int n = s.length();
        Integer[] memo = new Integer[n];
        Boolean[][] isPalindromeMemo = new Boolean[n][n];
        return minCutMemoHelper(s, 0, memo, isPalindromeMemo);
    }

    private int minCutMemoHelper(String s, int start, Integer[] memo, Boolean[][] isPalindromeMemo) {
        if (start >= s.length())
            return -1; // No cuts needed for empty string

        if (memo[start] != null)
            return memo[start];

        int minCuts = Integer.MAX_VALUE;

        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end, isPalindromeMemo)) {
                int remainingCuts = minCutMemoHelper(s, end + 1, memo, isPalindromeMemo);
                minCuts = Math.min(minCuts, (remainingCuts == -1 ? 0 : remainingCuts + 1));
            }
        }

        memo[start] = minCuts;
        return minCuts;
    }

    private boolean isPalindrome(String s, int left, int right, Boolean[][] memo) {
        if (left >= right)
            return true;

        if (memo[left][right] != null)
            return memo[left][right];

        memo[left][right] = (s.charAt(left) == s.charAt(right)) &&
                isPalindrome(s, left + 1, right - 1, memo);

        return memo[left][right];
    }

    // Approach 4: Optimized DP with Single Pass - O(n^2) time, O(n) space
    public int minCutOptimized(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];

        // Initialize: dp[i] represents min cuts for s[0...i-1]
        for (int i = 0; i <= n; i++) {
            dp[i] = i - 1;
        }

        for (int center = 0; center < n; center++) {
            // Odd length palindromes
            for (int i = 0; center - i >= 0 && center + i < n &&
                    s.charAt(center - i) == s.charAt(center + i); i++) {
                dp[center + i + 1] = Math.min(dp[center + i + 1], dp[center - i] + 1);
            }

            // Even length palindromes
            for (int i = 0; center - i >= 0 && center + i + 1 < n &&
                    s.charAt(center - i) == s.charAt(center + i + 1); i++) {
                dp[center + i + 2] = Math.min(dp[center + i + 2], dp[center - i] + 1);
            }
        }

        return dp[n];
    }

    // Approach 5: Manacher's Algorithm + DP - O(n) average time, O(n) space
    public int minCutManacher(String s) {
        if (s == null || s.length() <= 1)
            return 0;

        // Transform string for Manacher's algorithm
        StringBuilder sb = new StringBuilder("^");
        for (char c : s.toCharArray()) {
            sb.append("#").append(c);
        }
        sb.append("#$");

        String transformed = sb.toString();
        int n = transformed.length();
        int[] P = new int[n]; // P[i] = radius of palindrome centered at i
        int center = 0, right = 0;

        // Manacher's algorithm
        for (int i = 1; i < n - 1; i++) {
            int mirror = 2 * center - i;

            if (i < right) {
                P[i] = Math.min(right - i, P[mirror]);
            }

            // Try to expand palindrome centered at i
            while (transformed.charAt(i + 1 + P[i]) == transformed.charAt(i - 1 - P[i])) {
                P[i]++;
            }

            // If palindrome centered at i extends past right, adjust center and right
            if (i + P[i] > right) {
                center = i;
                right = i + P[i];
            }
        }

        // Convert back to original string and compute min cuts
        int originalLen = s.length();
        int[] dp = new int[originalLen + 1];

        for (int i = 0; i <= originalLen; i++) {
            dp[i] = i - 1;
        }

        for (int i = 1; i < n - 1; i++) {
            int radius = P[i];
            int start = (i - radius) / 2;
            int end = (i + radius) / 2;

            if (radius > 0) {
                dp[end + 1] = Math.min(dp[end + 1], dp[start] + 1);
            }
        }

        return dp[originalLen];
    }

    public static void main(String[] args) {
        PalindromePartitioningII solution = new PalindromePartitioningII();

        System.out.println("=== Palindrome Partitioning II Test Cases ===");

        // Test Case 1: Example from problem
        String s1 = "aab";
        System.out.println("Test 1 - String: \"" + s1 + "\"");
        System.out.println("DP: " + solution.minCutDP(s1));
        System.out.println("Expand Center: " + solution.minCutExpandCenter(s1));
        System.out.println("Memoization: " + solution.minCutMemo(s1));
        System.out.println("Optimized: " + solution.minCutOptimized(s1));
        System.out.println("Expected: 1\n");

        // Test Case 2: All same characters
        String s2 = "aba";
        System.out.println("Test 2 - String: \"" + s2 + "\"");
        System.out.println("DP: " + solution.minCutDP(s2));
        System.out.println("Expected: 0\n");

        // Test Case 3: No palindromes
        String s3 = "abcde";
        System.out.println("Test 3 - String: \"" + s3 + "\"");
        System.out.println("DP: " + solution.minCutDP(s3));
        System.out.println("Expected: 4\n");

        // Test Case 4: Complex case
        String s4 = "abccba";
        System.out.println("Test 4 - String: \"" + s4 + "\"");
        System.out.println("DP: " + solution.minCutDP(s4));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void performanceTest() {
        PalindromePartitioningII solution = new PalindromePartitioningII();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append((char) ('a' + (i % 3)));
        }
        String testString = sb.toString();

        System.out.println("=== Performance Test (String length: " + testString.length() + ") ===");

        long start = System.nanoTime();
        int result1 = solution.minCutDP(testString);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.minCutExpandCenter(testString);
        end = System.nanoTime();
        System.out.println("Expand Center: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minCutOptimized(testString);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
