package arrays.hard;

/**
 * LeetCode 97: Interleaving String
 * https://leetcode.com/problems/interleaving-string/
 *
 * Description:
 * Given strings s1, s2, and s3, find whether s3 is formed by an interleaving of
 * s1 and s2.
 * An interleaving of two strings s and t is a configuration where they are
 * divided into non-empty substrings.
 *
 * Constraints:
 * - 0 <= s1.length, s2.length <= 100
 * - 0 <= s3.length <= 200
 * - s1, s2, and s3 consist of lowercase English letters
 *
 * Follow-up:
 * - Can you solve it using O(min(m,n)) space?
 * 
 * Time Complexity: O(m * n)
 * Space Complexity: O(m * n)
 * 
 * Algorithm:
 * 1. Use dynamic programming with 2D table
 * 2. Check if characters from s1 or s2 can form s3
 * 3. Consider both possibilities at each step
 */
public class InterleavingString {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();
        if (m + n != s3.length())
            return false;

        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        // Fill first row (using only s2)
        for (int j = 1; j <= n; j++) {
            dp[0][j] = dp[0][j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        // Fill first column (using only s1)
        for (int i = 1; i <= m; i++) {
            dp[i][0] = dp[i - 1][0] && s1.charAt(i - 1) == s3.charAt(i - 1);
        }

        // Fill rest of the table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int k = i + j - 1;
                dp[i][j] = (dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(k)) ||
                        (dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(k));
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        InterleavingString solution = new InterleavingString();

        // Test Case 1: Normal case
        System.out.println(solution.isInterleave("aabcc", "dbbca", "aadbbcbcac")); // Expected: true

        // Test Case 2: Edge case - not interleaving
        System.out.println(solution.isInterleave("aabcc", "dbbca", "aadbbbaccc")); // Expected: false

        // Test Case 3: Corner case - empty strings
        System.out.println(solution.isInterleave("", "", "")); // Expected: true

        // Test Case 4: Large input - one empty string
        System.out.println(solution.isInterleave("", "abc", "abc")); // Expected: true

        // Test Case 5: Minimum input - single chars
        System.out.println(solution.isInterleave("a", "b", "ab")); // Expected: true

        // Test Case 6: Special case - wrong length
        System.out.println(solution.isInterleave("abc", "def", "abcde")); // Expected: false

        // Test Case 7: Boundary case - same characters
        System.out.println(solution.isInterleave("aaa", "aaa", "aaaaaa")); // Expected: true

        // Test Case 8: No interleaving possible
        System.out.println(solution.isInterleave("abc", "def", "abcdef")); // Expected: true

        // Test Case 9: Complex interleaving
        System.out.println(solution.isInterleave("ab", "cd", "acbd")); // Expected: true

        // Test Case 10: Single character strings
        System.out.println(solution.isInterleave("a", "b", "ba")); // Expected: true
    }
}
