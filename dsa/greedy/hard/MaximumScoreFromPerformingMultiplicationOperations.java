package greedy.hard;

/**
 * LeetCode 1770: Maximum Score from Performing Multiplication Operations
 * https://leetcode.com/problems/maximum-score-from-performing-multiplication-operations/
 * 
 * Companies: Google, Amazon, Microsoft, Meta
 * Frequency: High (Asked in 60+ interviews)
 *
 * Description:
 * You are given two integer arrays nums and multipliers of size n and m
 * respectively,
 * where n >= m. The arrays are 1-indexed. You begin with a score of 0. You want
 * to
 * perform exactly m operations. On the ith operation, you will pick one integer
 * x
 * from either the beginning or the end of nums and add multipliers[i] * x to
 * your score.
 * Return the maximum score after performing m operations.
 *
 * Constraints:
 * - n == nums.length
 * - m == multipliers.length
 * - 1 <= m <= 10^3
 * - m <= n <= 10^5
 * - -1000 <= nums[i], multipliers[i] <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you solve it using dynamic programming?
 * 2. Can you optimize space complexity?
 * 3. What if we could pick from middle as well?
 */
public class MaximumScoreFromPerformingMultiplicationOperations {

    // Approach 1: Top-down DP with Memoization - O(m²) time, O(m²) space
    public int maximumScore(int[] nums, int[] multipliers) {
        Integer[][] memo = new Integer[multipliers.length][multipliers.length];
        return helper(nums, multipliers, 0, 0, memo);
    }

    private int helper(int[] nums, int[] multipliers, int op, int left, Integer[][] memo) {
        if (op == multipliers.length)
            return 0;

        if (memo[op][left] != null)
            return memo[op][left];

        int right = nums.length - 1 - (op - left); // Calculate right index

        // Pick from left
        int pickLeft = multipliers[op] * nums[left] + helper(nums, multipliers, op + 1, left + 1, memo);

        // Pick from right
        int pickRight = multipliers[op] * nums[right] + helper(nums, multipliers, op + 1, left, memo);

        return memo[op][left] = Math.max(pickLeft, pickRight);
    }

    // Approach 2: Bottom-up DP - O(m²) time, O(m²) space
    public int maximumScoreBottomUp(int[] nums, int[] multipliers) {
        int n = nums.length;
        int m = multipliers.length;
        int[][] dp = new int[m + 1][m + 1];

        // Fill dp table from bottom-up
        for (int op = m - 1; op >= 0; op--) {
            for (int left = op; left >= 0; left--) {
                int right = n - 1 - (op - left);

                int pickLeft = multipliers[op] * nums[left] + dp[op + 1][left + 1];
                int pickRight = multipliers[op] * nums[right] + dp[op + 1][left];

                dp[op][left] = Math.max(pickLeft, pickRight);
            }
        }

        return dp[0][0];
    }

    // Approach 3: Space Optimized DP - O(m²) time, O(m) space
    public int maximumScoreOptimized(int[] nums, int[] multipliers) {
        int n = nums.length;
        int m = multipliers.length;
        int[] dp = new int[m + 1];

        for (int op = m - 1; op >= 0; op--) {
            int[] newDp = new int[m + 1];
            for (int left = op; left >= 0; left--) {
                int right = n - 1 - (op - left);

                int pickLeft = multipliers[op] * nums[left] + dp[left + 1];
                int pickRight = multipliers[op] * nums[right] + dp[left];

                newDp[left] = Math.max(pickLeft, pickRight);
            }
            dp = newDp;
        }

        return dp[0];
    }

    // Follow-up: Track the actual operations
    public java.util.List<String> getOperations(int[] nums, int[] multipliers) {
        int n = nums.length;
        int m = multipliers.length;
        Integer[][] memo = new Integer[m][m];
        java.util.List<String> operations = new java.util.ArrayList<>();

        // First get the maximum score and build memo table
        helper(nums, multipliers, 0, 0, memo);

        // Reconstruct the path
        int op = 0, left = 0;
        while (op < m) {
            int right = n - 1 - (op - left);

            int pickLeft = multipliers[op] * nums[left] +
                    (op + 1 < m ? memo[op + 1][left + 1] : 0);
            int pickRight = multipliers[op] * nums[right] +
                    (op + 1 < m ? memo[op + 1][left] : 0);

            if (pickLeft >= pickRight) {
                operations
                        .add("LEFT: " + nums[left] + " * " + multipliers[op] + " = " + (nums[left] * multipliers[op]));
                left++;
            } else {
                operations.add(
                        "RIGHT: " + nums[right] + " * " + multipliers[op] + " = " + (nums[right] * multipliers[op]));
            }
            op++;
        }

        return operations;
    }

    public static void main(String[] args) {
        MaximumScoreFromPerformingMultiplicationOperations solution = new MaximumScoreFromPerformingMultiplicationOperations();

        // Test Case 1: Basic example
        System.out.println("Basic: " + solution.maximumScore(new int[] { 1, 2, 3 }, new int[] { 3, 2, 1 })); // 14

        // Test Case 2: Negative numbers
        System.out.println(
                "Negative: " + solution.maximumScore(new int[] { -5, -3, -3, -2, 7, 1 }, new int[] { -2, -4, 6 })); // 102

        // Test Case 3: All negatives
        System.out
                .println("All negative: " + solution.maximumScore(new int[] { -1, -2, -3 }, new int[] { -1, -2, -3 })); // 14

        // Test Case 4: Single element
        System.out.println("Single: " + solution.maximumScore(new int[] { 10 }, new int[] { 10 })); // 100

        // Test Case 5: Mixed positive/negative
        System.out.println("Mixed: " + solution.maximumScore(new int[] { -2, 0, 1, 3 }, new int[] { 1, 2 })); // 6

        // Test Case 6: Large multipliers
        System.out
                .println("Large mult: " + solution.maximumScore(new int[] { 1, 2, 3, 4, 5 }, new int[] { 10, 20, 30 })); // 270

        // Test approaches comparison
        System.out.println("Bottom-up: " + solution.maximumScoreBottomUp(new int[] { 1, 2, 3 }, new int[] { 3, 2, 1 })); // 14
        System.out
                .println("Optimized: " + solution.maximumScoreOptimized(new int[] { 1, 2, 3 }, new int[] { 3, 2, 1 })); // 14

        // Test Case 7: Operations tracking
        java.util.List<String> ops = solution.getOperations(new int[] { 1, 2, 3 }, new int[] { 3, 2, 1 });
        System.out.println("Operations:");
        for (String op : ops) {
            System.out.println("  " + op);
        }

        // Test Case 8: Edge - all zeros
        System.out.println("All zeros: " + solution.maximumScore(new int[] { 0, 0, 0, 0 }, new int[] { 1, 2, 3 })); // 0
    }
}
