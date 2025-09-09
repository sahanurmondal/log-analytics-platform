package arrays.hard;

/**
 * LeetCode 10: Regular Expression Matching
 * https://leetcode.com/problems/regular-expression-matching/
 *
 * Description:
 * Given an input string s and a pattern p, implement regular expression
 * matching with support for '.' and '*'.
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
 * - Can you solve it using recursion with memoization?
 * 
 * Time Complexity: O(m * n)
 * Space Complexity: O(m * n)
 * 
 * Algorithm:
 * 1. Use dynamic programming with 2D table
 * 2. Handle '*' by considering zero or more occurrences
 * 3. Handle '.' as wildcard character
 */
public class RegularExpressionMatching {
    public boolean isMatch(String s, String p) {
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
                    // '*' matches zero occurrences
                    dp[i][j] = dp[i][j - 2];

                    // '*' matches one or more occurrences
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

        // Test Case 1: Normal case
        System.out.println(solution.isMatch("aa", "a")); // Expected: false

        // Test Case 2: Edge case - with '*'
        System.out.println(solution.isMatch("aa", "a*")); // Expected: true

        // Test Case 3: Corner case - with '.'
        System.out.println(solution.isMatch("ab", ".*")); // Expected: true

        // Test Case 4: Large input - complex pattern
        System.out.println(solution.isMatch("aab", "c*a*b")); // Expected: true

        // Test Case 5: Minimum input - single char
        System.out.println(solution.isMatch("a", "a")); // Expected: true

        // Test Case 6: Special case - empty pattern
        System.out.println(solution.isMatch("mississippi", "mis*is*p*.")); // Expected: false

        // Test Case 7: Boundary case - pattern longer
        System.out.println(solution.isMatch("a", "ab*")); // Expected: true

        // Test Case 8: Multiple '*'
        System.out.println(solution.isMatch("ab", ".*c")); // Expected: false

        // Test Case 9: Exact match
        System.out.println(solution.isMatch("abc", "abc")); // Expected: true

        // Test Case 10: No match
        System.out.println(solution.isMatch("abc", "def")); // Expected: false
    }
}
