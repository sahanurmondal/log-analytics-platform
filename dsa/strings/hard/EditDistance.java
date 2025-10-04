package strings.hard;

import java.util.*;

/**
 * LeetCode 72: Edit Distance
 * https://leetcode.com/problems/edit-distance/
 * 
 * Companies: Google, Amazon, Facebook, Microsoft
 * Frequency: Very High
 *
 * Description: Given two strings word1 and word2, return the minimum number of
 * operations required to convert word1 to word2.
 *
 * Constraints:
 * - 0 <= word1.length, word2.length <= 500
 * - word1 and word2 consist of lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you return the actual edit operations?
 * 2. Can you handle different operation costs?
 * 3. Can you optimize for large strings?
 */
public class EditDistance {

    // Approach 1: Dynamic Programming - O(m*n) time, O(m*n) space
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++)
            dp[i][0] = i;
        for (int j = 0; j <= n; j++)
            dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    // Approach 2: Space optimized - O(m*n) time, O(min(m,n)) space
    public int minDistanceOptimized(String word1, String word2) {
        if (word1.length() < word2.length())
            return minDistanceOptimized(word2, word1);
        int m = word1.length(), n = word2.length();
        int[] prev = new int[n + 1], curr = new int[n + 1];
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
        return prev[n];
    }

    // Follow-up 1: Return actual edit operations
    public List<String> getEditOperations(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++)
            dp[i][0] = i;
        for (int j = 0; j <= n; j++)
            dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }

        List<String> operations = new ArrayList<>();
        int i = m, j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && word1.charAt(i - 1) == word2.charAt(j - 1)) {
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                operations.add("Replace " + word1.charAt(i - 1) + " with " + word2.charAt(j - 1));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                operations.add("Delete " + word1.charAt(i - 1));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                operations.add("Insert " + word2.charAt(j - 1));
                j--;
            }
        }
        Collections.reverse(operations);
        return operations;
    }

    // Follow-up 2: Different operation costs
    public int minDistanceWithCosts(String word1, String word2, int insertCost, int deleteCost, int replaceCost) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++)
            dp[i][0] = i * deleteCost;
        for (int j = 0; j <= n; j++)
            dp[0][j] = j * insertCost;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(
                            dp[i - 1][j] + deleteCost,
                            dp[i][j - 1] + insertCost),
                            dp[i - 1][j - 1] + replaceCost);
                }
            }
        }
        return dp[m][n];
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        EditDistance solution = new EditDistance();

        // Test case 1: Basic case
        String word1 = "horse", word2 = "ros";
        System.out.println("Test 1 - word1: " + word1 + ", word2: " + word2 + " Expected: 3");
        System.out.println("Result: " + solution.minDistance(word1, word2));
        System.out.println("Optimized: " + solution.minDistanceOptimized(word1, word2));

        // Test case 2: Edit operations
        System.out.println("\nTest 2 - Edit operations:");
        List<String> ops = solution.getEditOperations(word1, word2);
        for (String op : ops)
            System.out.println(op);

        // Test case 3: Different costs
        System.out.println("\nTest 3 - Different costs (insert:1, delete:2, replace:3):");
        System.out.println("Result: " + solution.minDistanceWithCosts(word1, word2, 1, 2, 3));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Both empty: " + solution.minDistance("", ""));
        System.out.println("One empty: " + solution.minDistance("abc", ""));
        System.out.println("Same strings: " + solution.minDistance("hello", "hello"));
        System.out.println("Single char diff: " + solution.minDistance("a", "b"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < 250; i++) {
            sb1.append((char) ('a' + (i % 26)));
            sb2.append((char) ('a' + ((i + 1) % 26)));
        }
        long start = System.nanoTime();
        int result = solution.minDistanceOptimized(sb1.toString(), sb2.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
