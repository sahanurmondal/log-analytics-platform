package dp.string.matching;

import java.util.Arrays;

/**
 * LeetCode 72: Edit Distance
 * https://leetcode.com/problems/edit-distance/
 *
 * Description:
 * Given two strings word1 and word2, return the minimum number of operations
 * required to convert word1 to word2.
 * You have the following three operations permitted on a word:
 * - Insert a character
 * - Delete a character
 * - Replace a character
 *
 * Constraints:
 * - 0 <= word1.length, word2.length <= 500
 * - word1 and word2 consist of lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it in O(min(m,n)) space?
 * - What if we need to track the actual operations?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg
 * Difficulty: Hard
 */
public class EditDistance {

    // Approach 1: 2D DP - O(m*n) time, O(m*n) space
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // Delete all characters from word1
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // Insert all characters of word2
        }

        // Fill DP table
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

    // Approach 2: Space Optimized DP - O(m*n) time, O(min(m,n)) space
    public int minDistanceOptimized(String word1, String word2) {
        // Ensure word2 is the shorter string for space optimization
        if (word1.length() < word2.length()) {
            return minDistanceOptimized(word2, word1);
        }

        int m = word1.length(), n = word2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        // Initialize base case
        for (int j = 0; j <= n; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= m; i++) {
            curr[0] = i;

            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(
                            Math.min(prev[j], curr[j - 1]),
                            prev[j - 1]);
                }
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }

    // Approach 3: Memoization - O(m*n) time, O(m*n) space
    public int minDistanceMemo(String word1, String word2) {
        Integer[][] memo = new Integer[word1.length()][word2.length()];
        return minDistanceMemoHelper(word1, word2, 0, 0, memo);
    }

    private int minDistanceMemoHelper(String word1, String word2, int i, int j, Integer[][] memo) {
        if (i == word1.length())
            return word2.length() - j;
        if (j == word2.length())
            return word1.length() - i;

        if (memo[i][j] != null)
            return memo[i][j];

        int result;
        if (word1.charAt(i) == word2.charAt(j)) {
            result = minDistanceMemoHelper(word1, word2, i + 1, j + 1, memo);
        } else {
            int insert = minDistanceMemoHelper(word1, word2, i, j + 1, memo);
            int delete = minDistanceMemoHelper(word1, word2, i + 1, j, memo);
            int replace = minDistanceMemoHelper(word1, word2, i + 1, j + 1, memo);

            result = 1 + Math.min(Math.min(insert, delete), replace);
        }

        memo[i][j] = result;
        return result;
    }

    // Approach 4: Get Edit Operations - O(m*n) time, O(m*n) space
    public java.util.List<String> getEditOperations(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Fill DP table
        for (int i = 0; i <= m; i++)
            dp[i][0] = i;
        for (int j = 0; j <= n; j++)
            dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            Math.min(dp[i - 1][j], dp[i][j - 1]),
                            dp[i - 1][j - 1]);
                }
            }
        }

        // Reconstruct operations
        java.util.List<String> operations = new java.util.ArrayList<>();
        int i = m, j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && word1.charAt(i - 1) == word2.charAt(j - 1)) {
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                operations.add("Replace '" + word1.charAt(i - 1) + "' with '" + word2.charAt(j - 1) + "' at position "
                        + (i - 1));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                operations.add("Delete '" + word1.charAt(i - 1) + "' at position " + (i - 1));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                operations.add("Insert '" + word2.charAt(j - 1) + "' at position " + i);
                j--;
            }
        }

        java.util.Collections.reverse(operations);
        return operations;
    }

    // Approach 5: Hirschberg's Algorithm - O(m*n) time, O(min(m,n)) space
    public int minDistanceHirschberg(String word1, String word2) {
        return hirschberg(word1, word2, 0, word1.length(), 0, word2.length());
    }

    private int hirschberg(String word1, String word2, int start1, int end1, int start2, int end2) {
        int m = end1 - start1;
        int n = end2 - start2;

        if (m == 0)
            return n;
        if (n == 0)
            return m;
        if (m == 1) {
            return editDistanceOneLine(word1.substring(start1, end1), word2.substring(start2, end2));
        }

        int mid = start1 + m / 2;

        // Compute edit distance from start to mid
        int[] left = editDistanceArray(word1.substring(start1, mid), word2.substring(start2, end2));

        // Compute edit distance from mid to end (reversed)
        String rev1 = new StringBuilder(word1.substring(mid, end1)).reverse().toString();
        String rev2 = new StringBuilder(word2.substring(start2, end2)).reverse().toString();
        int[] right = editDistanceArray(rev1, rev2);

        // Find optimal split point
        int minCost = Integer.MAX_VALUE;
        int splitPoint = start2;

        for (int k = 0; k <= n; k++) {
            int cost = left[k] + right[n - k];
            if (cost < minCost) {
                minCost = cost;
                splitPoint = start2 + k;
            }
        }

        return hirschberg(word1, word2, start1, mid, start2, splitPoint) +
                hirschberg(word1, word2, mid, end1, splitPoint, end2);
    }

    private int[] editDistanceArray(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int j = 0; j <= n; j++)
            prev[j] = j;

        for (int i = 1; i <= m; i++) {
            curr[0] = i;
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(Math.min(prev[j], curr[j - 1]), prev[j - 1]);
                }
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev;
    }

    private int editDistanceOneLine(String word1, String word2) {
        int n = word2.length();
        int[] dp = new int[n + 1];

        for (int j = 0; j <= n; j++)
            dp[j] = j;

        for (int i = 1; i <= word1.length(); i++) {
            int prev = dp[0];
            dp[0] = i;

            for (int j = 1; j <= n; j++) {
                int temp = dp[j];
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[j] = prev;
                } else {
                    dp[j] = 1 + Math.min(Math.min(dp[j], dp[j - 1]), prev);
                }
                prev = temp;
            }
        }

        return dp[n];
    }

    public static void main(String[] args) {
        EditDistance solution = new EditDistance();

        System.out.println("=== Edit Distance Test Cases ===");

        // Test Case 1: Example from problem
        String word1_1 = "horse", word2_1 = "ros";
        System.out.println("Test 1 - word1: \"" + word1_1 + "\", word2: \"" + word2_1 + "\"");
        System.out.println("2D DP: " + solution.minDistance(word1_1, word2_1));
        System.out.println("Optimized: " + solution.minDistanceOptimized(word1_1, word2_1));
        System.out.println("Memoization: " + solution.minDistanceMemo(word1_1, word2_1));
        System.out.println("Hirschberg: " + solution.minDistanceHirschberg(word1_1, word2_1));
        System.out.println("Operations: " + solution.getEditOperations(word1_1, word2_1));
        System.out.println("Expected: 3\n");

        // Test Case 2: Another example
        String word1_2 = "intention", word2_2 = "execution";
        System.out.println("Test 2 - word1: \"" + word1_2 + "\", word2: \"" + word2_2 + "\"");
        System.out.println("2D DP: " + solution.minDistance(word1_2, word2_2));
        System.out.println("Expected: 5\n");

        // Test Case 3: Empty strings
        String word1_3 = "", word2_3 = "abc";
        System.out.println("Test 3 - word1: \"" + word1_3 + "\", word2: \"" + word2_3 + "\"");
        System.out.println("2D DP: " + solution.minDistance(word1_3, word2_3));
        System.out.println("Expected: 3\n");

        performanceTest();
    }

    private static void performanceTest() {
        EditDistance solution = new EditDistance();

        String word1 = "a".repeat(250);
        String word2 = "b".repeat(250);

        System.out.println("=== Performance Test (String lengths: " + word1.length() + ", " + word2.length() + ") ===");

        long start = System.nanoTime();
        int result1 = solution.minDistance(word1, word2);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.minDistanceOptimized(word1, word2);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minDistanceHirschberg(word1, word2);
        end = System.nanoTime();
        System.out.println("Hirschberg: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
