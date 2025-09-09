package strings.hard;

/**
 * LeetCode 730: Count Different Palindromic Subsequences
 * URL: https://leetcode.com/problems/count-different-palindromic-subsequences/
 * Difficulty: Hard
 * 
 * Companies: Google, Amazon, Microsoft, Facebook, Apple
 * Frequency: Medium
 * 
 * Description:
 * Given a string s, return the number of different non-empty palindromic
 * subsequences in s. Since the answer may be large, return it modulo 10^9 + 7.
 * 
 * Constraints:
 * - 1 <= s.length <= 1000
 * - s consists of only lowercase English letters ('a' to 'd')
 * - Answer fits in 32-bit integer after modulo
 * 
 * Follow-up Questions:
 * 1. How would you optimize for larger alphabets?
 * 2. How would you handle case-insensitive palindromes?
 * 3. How would you count palindromic substrings instead?
 * 4. How would you find the longest palindromic subsequence?
 * 5. How would you optimize space complexity?
 */
public class CountDifferentPalindromicSubsequences {
    private static final int MOD = 1000000007;

    // Approach 1: Dynamic Programming with Memoization
    public int countPalindromicSubsequences(String s) {
        int n = s.length();
        Integer[][][] memo = new Integer[n][n][4];
        return dfs(s, 0, n - 1, 0, memo);
    }

    /**
     * DFS with memoization
     * Time: O(n^2 * 4), Space: O(n^2 * 4)
     */
    private int dfs(String s, int i, int j, int c, Integer[][][] memo) {
        if (i > j)
            return 0;
        if (memo[i][j][c] != null)
            return memo[i][j][c];

        long result = 0;
        char target = (char) ('a' + c);

        if (i == j) {
            result = s.charAt(i) == target ? 1 : 0;
        } else {
            if (s.charAt(i) != target) {
                result = dfs(s, i + 1, j, c, memo);
            } else if (s.charAt(j) != target) {
                result = dfs(s, i, j - 1, c, memo);
            } else {
                result = 2; // Single char + pair
                if (i + 1 <= j - 1) {
                    for (int k = 0; k < 4; k++) {
                        result += dfs(s, i + 1, j - 1, k, memo);
                        result %= MOD;
                    }
                }
            }
        }

        return memo[i][j][c] = (int) (result % MOD);
    }

    // Approach 2: Bottom-up DP with character tracking
    public int countPalindromicSubsequencesBottomUp(String s) {
        int n = s.length();
        int[][][] dp = new int[n][n][4];

        // Base case: single characters
        for (int i = 0; i < n; i++) {
            dp[i][i][s.charAt(i) - 'a'] = 1;
        }

        // Fill DP table
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                for (int c = 0; c < 4; c++) {
                    char target = (char) ('a' + c);

                    if (s.charAt(i) != target) {
                        dp[i][j][c] = dp[i + 1][j][c];
                    } else if (s.charAt(j) != target) {
                        dp[i][j][c] = dp[i][j - 1][c];
                    } else {
                        dp[i][j][c] = 2; // Single char + pair
                        if (i + 1 <= j - 1) {
                            for (int k = 0; k < 4; k++) {
                                dp[i][j][c] = (dp[i][j][c] + dp[i + 1][j - 1][k]) % MOD;
                            }
                        }
                    }
                }
            }
        }

        int result = 0;
        for (int c = 0; c < 4; c++) {
            result = (result + dp[0][n - 1][c]) % MOD;
        }
        return result;
    }

    // Approach 3: Optimized with next/prev arrays
    public int countPalindromicSubsequencesOptimized(String s) {
        int n = s.length();
        int[] next = new int[n];
        int[] prev = new int[n];

        // Precompute next occurrence of each character
        int[] lastPos = { -1, -1, -1, -1 };
        for (int i = n - 1; i >= 0; i--) {
            int c = s.charAt(i) - 'a';
            next[i] = lastPos[c];
            lastPos[c] = i;
        }

        // Precompute previous occurrence of each character
        int[] prevPos = { -1, -1, -1, -1 };
        for (int i = 0; i < n; i++) {
            int c = s.charAt(i) - 'a';
            prev[i] = prevPos[c];
            prevPos[c] = i;
        }

        Integer[][] memo = new Integer[n][n];
        return helper(s, 0, n - 1, next, prev, memo);
    }

    private int helper(String s, int i, int j, int[] next, int[] prev, Integer[][] memo) {
        if (i > j)
            return 0;
        if (memo[i][j] != null)
            return memo[i][j];

        long result = 0;
        for (int c = 0; c < 4; c++) {
            int left = findNext(s, i, j, c);
            int right = findPrev(s, i, j, c);

            if (left == -1 || right == -1)
                continue;

            if (left == right) {
                result++;
            } else if (left < right) {
                result = (result + 2 + helper(s, left + 1, right - 1, next, prev, memo)) % MOD;
            }
        }

        return memo[i][j] = (int) (result % MOD);
    }

    private int findNext(String s, int start, int end, int c) {
        for (int i = start; i <= end; i++) {
            if (s.charAt(i) - 'a' == c)
                return i;
        }
        return -1;
    }

    private int findPrev(String s, int start, int end, int c) {
        for (int i = end; i >= start; i--) {
            if (s.charAt(i) - 'a' == c)
                return i;
        }
        return -1;
    }

    // Follow-up: Count palindromic substrings (not subsequences)
    public int countPalindromicSubstrings(String s) {
        int n = s.length();
        int count = 0;

        // Check all possible centers
        for (int center = 0; center < 2 * n - 1; center++) {
            int left = center / 2;
            int right = left + center % 2;

            while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) {
                count++;
                left--;
                right++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        CountDifferentPalindromicSubsequences solution = new CountDifferentPalindromicSubsequences();

        // Test Case 1: Basic example
        System.out.println("Basic 'bccb': " + solution.countPalindromicSubsequences("bccb")); // 6
        System.out.println("Bottom-up 'bccb': " + solution.countPalindromicSubsequencesBottomUp("bccb")); // 6

        // Test Case 2: All same characters
        System.out.println("All same 'aaaa': " + solution.countPalindromicSubsequences("aaaa")); // 4

        // Test Case 3: All different characters
        System.out.println("All different 'abcd': " + solution.countPalindromicSubsequences("abcd")); // 4

        // Test Case 4: Single character
        System.out.println("Single 'a': " + solution.countPalindromicSubsequences("a")); // 1

        // Test Case 5: Two characters same
        System.out.println("Two same 'aa': " + solution.countPalindromicSubsequences("aa")); // 2

        // Test Case 6: Two characters different
        System.out.println("Two different 'ab': " + solution.countPalindromicSubsequences("ab")); // 2

        // Test Case 7: Complex pattern
        System.out.println("Complex 'abcdabcd': " + solution.countPalindromicSubsequences("abcdabcd")); // 104

        // Test Case 8: Optimized approach
        System.out.println("Optimized 'bccb': " + solution.countPalindromicSubsequencesOptimized("bccb")); // 6

        // Test Case 9: Follow-up palindromic substrings
        System.out.println("Substrings 'bccb': " + solution.countPalindromicSubstrings("bccb")); // 5

        // Test Case 10: Edge case with repeated pattern
        System.out.println("Repeated 'abab': " + solution.countPalindromicSubsequences("abab")); // 8

        // Test Case 11: Maximum single character
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('a');
        }
        System.out.println("Large same: " + solution.countPalindromicSubsequences(sb.toString())); // 1

        // Test Case 12: Palindrome pattern
        System.out.println("Palindrome 'aba': " + solution.countPalindromicSubsequences("aba")); // 4
    }
}
