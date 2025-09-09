package backtracking.hard;

/**
 * LeetCode 10: Regular Expression Matching
 * https://leetcode.com/problems/regular-expression-matching/
 *
 * Description: Given an input string s and a pattern p, implement regular
 * expression matching with support for '.' and '*'.
 * '.' Matches any single character.
 * '*' Matches zero or more of the preceding element.
 * 
 * Constraints:
 * - 1 <= s.length <= 20
 * - 1 <= p.length <= 30
 * - s contains only lowercase English letters
 * - p contains only lowercase English letters, '.', and '*'
 *
 * Follow-up:
 * - Can you solve it using dynamic programming?
 * 
 * Time Complexity: O(m * n)
 * Space Complexity: O(m * n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class RegularExpressionMatching {

    public boolean isMatch(String s, String p) {
        return backtrack(s, p, 0, 0);
    }

    private boolean backtrack(String s, String p, int i, int j) {
        if (j == p.length())
            return i == s.length();

        boolean firstMatch = i < s.length() && (p.charAt(j) == s.charAt(i) || p.charAt(j) == '.');

        if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
            return backtrack(s, p, i, j + 2) || (firstMatch && backtrack(s, p, i + 1, j));
        } else {
            return firstMatch && backtrack(s, p, i + 1, j + 1);
        }
    }

    // Alternative solution - Dynamic Programming
    public boolean isMatchDP(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];

        dp[0][0] = true;

        // Handle patterns with '*' that can match empty string
        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char sc = s.charAt(i - 1);
                char pc = p.charAt(j - 1);

                if (pc == '*') {
                    dp[i][j] = dp[i][j - 2];
                    if (matches(sc, p.charAt(j - 2))) {
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                } else {
                    dp[i][j] = dp[i - 1][j - 1] && matches(sc, pc);
                }
            }
        }

        return dp[m][n];
    }

    private boolean matches(char s, char p) {
        return p == '.' || s == p;
    }

    public static void main(String[] args) {
        RegularExpressionMatching solution = new RegularExpressionMatching();

        System.out.println(solution.isMatch("aa", "a")); // Expected: false
        System.out.println(solution.isMatch("aa", "a*")); // Expected: true
        System.out.println(solution.isMatch("ab", ".*")); // Expected: true
        System.out.println(solution.isMatch("aab", "c*a*b")); // Expected: true
        System.out.println(solution.isMatch("mississippi", "mis*is*p*.")); // Expected: false
    }
}
