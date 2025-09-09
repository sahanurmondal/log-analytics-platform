package dp.string.subsequence;

/**
 * LeetCode 392: Is Subsequence
 * https://leetcode.com/problems/is-subsequence/
 *
 * Description:
 * Given two strings s and t, return true if s is a subsequence of t, or false
 * otherwise.
 * A subsequence of a string is a new string that is formed from the original
 * string by deleting
 * some (can be none) of the characters without disturbing the relative
 * positions of the remaining characters.
 *
 * Constraints:
 * - 0 <= s.length <= 100
 * - 0 <= t.length <= 10^4
 * - s and t consist only of lowercase English letters.
 *
 * Company Tags: Google, Amazon, Microsoft
 * Difficulty: Easy
 */
public class IsSubsequence {

    // Approach 1: Two Pointers - O(n) time, O(1) space
    public boolean isSubsequence(String s, String t) {
        int i = 0, j = 0;

        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) {
                i++;
            }
            j++;
        }

        return i == s.length();
    }

    // Approach 2: DP - O(m*n) time, O(m*n) space
    public boolean isSubsequenceDP(String s, String t) {
        int m = s.length(), n = t.length();
        boolean[][] dp = new boolean[m + 1][n + 1];

        // Base case: empty string is subsequence of any string
        for (int j = 0; j <= n; j++) {
            dp[0][j] = true;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = dp[i][j - 1];
                }
            }
        }

        return dp[m][n];
    }

    // Approach 3: Recursive - O(2^min(m,n)) time, O(min(m,n)) space
    public boolean isSubsequenceRecursive(String s, String t) {
        return helper(s, t, 0, 0);
    }

    private boolean helper(String s, String t, int i, int j) {
        if (i == s.length())
            return true;
        if (j == t.length())
            return false;

        if (s.charAt(i) == t.charAt(j)) {
            return helper(s, t, i + 1, j + 1);
        } else {
            return helper(s, t, i, j + 1);
        }
    }

    public static void main(String[] args) {
        IsSubsequence solution = new IsSubsequence();

        System.out.println("=== Is Subsequence Test Cases ===");

        String s1 = "abc", t1 = "aebdc";
        System.out.println("s = \"" + s1 + "\", t = \"" + t1 + "\"");
        System.out.println("Two Pointers: " + solution.isSubsequence(s1, t1));
        System.out.println("DP: " + solution.isSubsequenceDP(s1, t1));
        System.out.println("Expected: true\n");

        String s2 = "axc", t2 = "ahbgdc";
        System.out.println("s = \"" + s2 + "\", t = \"" + t2 + "\"");
        System.out.println("Two Pointers: " + solution.isSubsequence(s2, t2));
        System.out.println("Expected: false\n");
    }
}
