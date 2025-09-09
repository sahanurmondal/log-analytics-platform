package binarysearchtree.medium;

/**
 * LeetCode 96: Unique Binary Search Trees
 * https://leetcode.com/problems/unique-binary-search-trees/
 *
 * Description: Given an integer n, return the number of structurally unique
 * BST's
 * which has exactly n nodes of unique values from 1 to n.
 * 
 * Constraints:
 * - 1 <= n <= 19
 *
 * Follow-up:
 * - Can you solve it using Catalan numbers formula?
 * 
 * Time Complexity: O(n^2)
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class UniqueBinarySearchTrees {

    // Main optimized solution - Dynamic Programming
    public int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                dp[i] += dp[j - 1] * dp[i - j];
            }
        }

        return dp[n];
    }

    // Alternative solution - Catalan number formula
    public int numTreesCatalan(int n) {
        long result = 1;
        for (int i = 0; i < n; i++) {
            result = result * (n + i + 1) / (i + 1);
        }
        return (int) (result / (n + 1));
    }

    // Alternative solution - Memoization
    public int numTreesMemo(int n) {
        Integer[] memo = new Integer[n + 1];
        return helper(n, memo);
    }

    private int helper(int n, Integer[] memo) {
        if (n <= 1)
            return 1;
        if (memo[n] != null)
            return memo[n];

        int result = 0;
        for (int i = 1; i <= n; i++) {
            result += helper(i - 1, memo) * helper(n - i, memo);
        }

        memo[n] = result;
        return result;
    }

    public static void main(String[] args) {
        UniqueBinarySearchTrees solution = new UniqueBinarySearchTrees();

        System.out.println(solution.numTrees(3)); // Expected: 5
        System.out.println(solution.numTrees(1)); // Expected: 1
        System.out.println(solution.numTreesCatalan(3)); // Expected: 5
        System.out.println(solution.numTreesMemo(4)); // Expected: 14
    }
}
