package arrays.hard;

/**
 * LeetCode 72: Edit Distance
 * https://leetcode.com/problems/edit-distance/
 *
 * Description:
 * Given two strings word1 and word2, return the minimum number of operations
 * required to convert word1 to word2.
 * You have the following three operations permitted on a word:
 * Insert a character, Delete a character, Replace a character.
 *
 * Constraints:
 * - 0 <= word1.length, word2.length <= 500
 * - word1 and word2 consist of lowercase English letters
 *
 * Follow-up:
 * - Can you solve it in O(min(m,n)) space?
 * 
 * Time Complexity: O(m * n)
 * Space Complexity: O(m * n)
 * 
 * Algorithm:
 * 1. Use dynamic programming with 2D table
 * 2. Consider three operations: insert, delete, replace
 * 3. Take minimum cost among all possible operations
 */
public class EditDistance {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Initialize base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // Delete all characters from word1
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // Insert all characters to match word2
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No operation needed
                } else {
                    dp[i][j] = 1 + Math.min(
                            Math.min(dp[i - 1][j], dp[i][j - 1]), // Delete or Insert
                            dp[i - 1][j - 1] // Replace
                    );
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        EditDistance solution = new EditDistance();

        // Test Case 1: Normal case
        System.out.println(solution.minDistance("horse", "ros")); // Expected: 3

        // Test Case 2: Edge case - one empty string
        System.out.println(solution.minDistance("intention", "execution")); // Expected: 5

        // Test Case 3: Corner case - both empty
        System.out.println(solution.minDistance("", "")); // Expected: 0

        // Test Case 4: Large input - same strings
        System.out.println(solution.minDistance("abc", "abc")); // Expected: 0

        // Test Case 5: Minimum input - single char
        System.out.println(solution.minDistance("a", "b")); // Expected: 1

        // Test Case 6: Special case - one char to empty
        System.out.println(solution.minDistance("a", "")); // Expected: 1

        // Test Case 7: Boundary case - insert all
        System.out.println(solution.minDistance("", "abc")); // Expected: 3

        // Test Case 8: Completely different
        System.out.println(solution.minDistance("abc", "def")); // Expected: 3

        // Test Case 9: Substring relationship
        System.out.println(solution.minDistance("abc", "ab")); // Expected: 1

        // Test Case 10: Reverse strings
        System.out.println(solution.minDistance("abc", "cba")); // Expected: 2
    }
}
