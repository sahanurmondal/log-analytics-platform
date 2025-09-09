package arrays.hard;

/**
 * LeetCode 44: Wildcard Matching
 * https://leetcode.com/problems/wildcard-matching/
 *
 * Description:
 * Given an input string (s) and a pattern (p), implement wildcard pattern
 * matching with support for '?' and '*'.
 * '?' Matches any single character.
 * '*' Matches any sequence of characters (including the empty sequence).
 *
 * Constraints:
 * - 0 <= s.length, p.length <= 2000
 * - s contains only lowercase English letters
 * - p contains only lowercase English letters, '?' or '*'
 *
 * Follow-up:
 * - Can you solve it using O(1) space?
 * 
 * Time Complexity: O(m * n)
 * Space Complexity: O(m * n)
 * 
 * Algorithm:
 * 1. Use dynamic programming with 2D table
 * 2. Handle '*' by considering empty sequence or any character
 * 3. Handle '?' as single character wildcard
 */
public class WildcardMatching {
    public boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];

        dp[0][0] = true;

        // Handle patterns with '*' at the beginning
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 1];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);

                if (pc == '*') {
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else if (pc == '?' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        WildcardMatching solution = new WildcardMatching();

        // Test Case 1: Normal case
        System.out.println(solution.isMatch("aa", "a")); // Expected: false

        // Test Case 2: Edge case - with '*'
        System.out.println(solution.isMatch("aa", "*")); // Expected: true

        // Test Case 3: Corner case - with '?'
        System.out.println(solution.isMatch("cb", "?a")); // Expected: false

        // Test Case 4: Large input - complex pattern
        System.out.println(solution.isMatch("adceb", "*a*b*")); // Expected: true

        // Test Case 5: Minimum input - empty strings
        System.out.println(solution.isMatch("", "")); // Expected: true

        // Test Case 6: Special case - no match
        System.out.println(solution.isMatch("acdcb", "a*c?b")); // Expected: false

        // Test Case 7: Boundary case - '*' at end
        System.out.println(solution.isMatch("abc", "abc*")); // Expected: true

        // Test Case 8: Multiple wildcards
        System.out.println(solution.isMatch("abcdef", "a?c*f")); // Expected: true

        // Test Case 9: Pattern longer than string
        System.out.println(solution.isMatch("a", "a*a")); // Expected: true

        // Test Case 10: Only wildcards
        System.out.println(solution.isMatch("abc", "***")); // Expected: true
    }
}
