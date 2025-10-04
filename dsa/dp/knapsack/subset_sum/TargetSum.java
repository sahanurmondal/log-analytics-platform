package dp.knapsack.subset_sum;

import java.util.*;

/**
 * LeetCode 494: Target Sum
 * https://leetcode.com/problems/target-sum/
 *
 * Description:
 * You are given an integer array nums and an integer target.
 * You want to build an expression out of nums by adding one of the symbols '+'
 * and '-' before each integer in nums and then concatenate all the integers.
 * Return the number of different expressions that you can build, which
 * evaluates to target.
 *
 * Constraints:
 * - 1 <= nums.length <= 20
 * - 0 <= nums[i] <= 1000
 * - 0 <= sum(nums[i]) <= 1000
 * - -1000 <= target <= 1000
 *
 * Follow-up:
 * - Can you solve it in O(n * sum) time?
 * - What if we need to find all possible expressions?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class TargetSum {

    // Approach 1: Brute Force Recursion - O(2^n) time, O(n) space
    public int findTargetSumWays(int[] nums, int target) {
        return findTargetSumWaysHelper(nums, 0, 0, target);
    }

    private int findTargetSumWaysHelper(int[] nums, int index, int currentSum, int target) {
        if (index == nums.length) {
            return currentSum == target ? 1 : 0;
        }

        // Try adding positive and negative
        int positive = findTargetSumWaysHelper(nums, index + 1, currentSum + nums[index], target);
        int negative = findTargetSumWaysHelper(nums, index + 1, currentSum - nums[index], target);

        return positive + negative;
    }

    // Approach 2: Memoization - O(n * sum) time, O(n * sum) space
    public int findTargetSumWaysMemo(int[] nums, int target) {
        Map<String, Integer> memo = new HashMap<>();
        return findTargetSumWaysMemoHelper(nums, 0, 0, target, memo);
    }

    private int findTargetSumWaysMemoHelper(int[] nums, int index, int currentSum, int target,
            Map<String, Integer> memo) {
        if (index == nums.length) {
            return currentSum == target ? 1 : 0;
        }

        String key = index + "," + currentSum;
        if (memo.containsKey(key))
            return memo.get(key);

        int positive = findTargetSumWaysMemoHelper(nums, index + 1, currentSum + nums[index], target, memo);
        int negative = findTargetSumWaysMemoHelper(nums, index + 1, currentSum - nums[index], target, memo);

        int result = positive + negative;
        memo.put(key, result);
        return result;
    }

    // Approach 3: DP Transformation to Subset Sum - O(n * sum) time, O(sum) space
    public int findTargetSumWaysDP(int[] nums, int target) {
        int sum = Arrays.stream(nums).sum();

        // Check if transformation is possible
        if (target > sum || target < -sum || (sum + target) % 2 == 1) {
            return 0;
        }

        // Transform problem: find subsets with sum = (sum + target) / 2
        int subsetSum = (sum + target) / 2;
        return countSubsetsWithSum(nums, subsetSum);
    }

    private int countSubsetsWithSum(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1; // One way to make sum 0: empty subset

        for (int num : nums) {
            // Process from right to left to avoid using updated values
            for (int j = target; j >= num; j--) {
                dp[j] += dp[j - num];
            }
        }

        return dp[target];
    }

    // Approach 4: 2D DP - O(n * sum) time, O(n * sum) space
    public int findTargetSumWays2D(int[] nums, int target) {
        int sum = Arrays.stream(nums).sum();
        if (target > sum || target < -sum)
            return 0;

        int n = nums.length;
        int offset = sum; // To handle negative indices
        int[][] dp = new int[n + 1][2 * sum + 1];

        dp[0][offset] = 1; // Base case: sum 0 at index 0

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= 2 * sum; j++) {
                // Add current number
                if (j + nums[i - 1] <= 2 * sum) {
                    dp[i][j] += dp[i - 1][j + nums[i - 1]];
                }

                // Subtract current number
                if (j - nums[i - 1] >= 0) {
                    dp[i][j] += dp[i - 1][j - nums[i - 1]];
                }
            }
        }

        return dp[n][target + offset];
    }

    // Approach 5: Alternative Subset Sum Implementation - O(n * sum) time, O(sum)
    // space
    public int findTargetSumWaysSubsetSum(int[] nums, int target) {
        return findTargetSumWaysDP(nums, target); // Use the same logic as DP approach
    }

    // Approach 6: Space Optimized Version - O(n * sum) time, O(sum) space
    public int findTargetSumWaysSpaceOptimized(int[] nums, int target) {
        int sum = Arrays.stream(nums).sum();
        if (target > sum || target < -sum || (sum + target) % 2 == 1) {
            return 0;
        }

        return countSubsetsWithSum(nums, (sum + target) / 2);
    }

    // Approach 7: Get All Target Sum Expressions - O(2^n) time, O(2^n) space
    public List<String> getAllTargetSumExpressions(int[] nums, int target) {
        List<String> result = new ArrayList<>();
        List<Character> current = new ArrayList<>();
        getAllExpressionsHelper(nums, 0, 0, target, current, result);
        return result;
    }

    private void getAllExpressionsHelper(int[] nums, int index, int currentSum, int target,
            List<Character> current, List<String> result) {
        if (index == nums.length) {
            if (currentSum == target) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < nums.length; i++) {
                    sb.append(current.get(i)).append(nums[i]);
                    if (i < nums.length - 1)
                        sb.append(" ");
                }
                result.add(sb.toString());
            }
            return;
        }

        // Try positive
        current.add('+');
        getAllExpressionsHelper(nums, index + 1, currentSum + nums[index], target, current, result);
        current.remove(current.size() - 1);

        // Try negative
        current.add('-');
        getAllExpressionsHelper(nums, index + 1, currentSum - nums[index], target, current, result);
        current.remove(current.size() - 1);
    }

    public static void main(String[] args) {
        TargetSum solution = new TargetSum();

        System.out.println("=== Target Sum Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 1, 1, 1, 1, 1 };
        int target1 = 3;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", target: " + target1);
        System.out.println("Brute Force: " + solution.findTargetSumWays(nums1, target1));
        System.out.println("Memoization: " + solution.findTargetSumWaysMemo(nums1, target1));
        System.out.println("DP Transformation: " + solution.findTargetSumWaysDP(nums1, target1));
        System.out.println("2D DP: " + solution.findTargetSumWays2D(nums1, target1));

        List<String> expressions1 = solution.getAllTargetSumExpressions(nums1, target1);
        System.out.println("All expressions (" + expressions1.size() + " total):");
        for (String expr : expressions1) {
            System.out.println("  " + expr);
        }
        System.out.println("Expected: 5\n");

        // Test Case 2: Single element
        int[] nums2 = { 1 };
        int target2 = 1;
        System.out.println("Test 2 - nums: " + Arrays.toString(nums2) + ", target: " + target2);
        System.out.println("DP Transformation: " + solution.findTargetSumWaysDP(nums2, target2));
        System.out.println("Expected: 1\n");

        // Test Case 3: Impossible target
        int[] nums3 = { 1, 2 };
        int target3 = 4;
        System.out.println("Test 3 - nums: " + Arrays.toString(nums3) + ", target: " + target3);
        System.out.println("DP Transformation: " + solution.findTargetSumWaysDP(nums3, target3));
        System.out.println("Expected: 0\n");

        // Test case 4: [1, 0], target = 1
        int[] nums4 = { 1, 0 };
        int target4 = 1;
        System.out.println("Test 4 - nums: " + Arrays.toString(nums4) + ", target: " + target4);
        System.out.println("Brute Force: " + solution.findTargetSumWays(nums4, target4)); // Expected: 2
        System.out.println("Memoization: " + solution.findTargetSumWaysMemo(nums4, target4)); // Expected: 2
        System.out.println("Expected: 2\n");

        // Test case 5: [100], target = -200 (impossible)
        int[] nums5 = { 100 };
        int target5 = -200;
        System.out.println("Test 5 - nums: " + Arrays.toString(nums5) + ", target: " + target5);
        System.out.println("Brute Force: " + solution.findTargetSumWays(nums5, target5)); // Expected: 0
        System.out.println("Subset Sum DP: " + solution.findTargetSumWaysSubsetSum(nums5, target5)); // Expected: 0
        System.out.println("Expected: 0\n");

        // Test case 6: Complex case
        int[] nums6 = { 1, 2, 3, 4, 5 };
        int target6 = 3;
        System.out.println("Test 6 - nums: " + Arrays.toString(nums6) + ", target: " + target6);
        System.out.println("Brute Force: " + solution.findTargetSumWays(nums6, target6)); // Expected: 5
        System.out.println("Subset Sum DP: " + solution.findTargetSumWaysSubsetSum(nums6, target6)); // Expected: 5
        System.out.println("Expected: 5\n");

        // Test case 7: All zeros
        int[] nums7 = { 0, 0, 0 };
        int target7 = 0;
        System.out.println("Test 7 - nums: " + Arrays.toString(nums7) + ", target: " + target7);
        System.out.println("Brute Force: " + solution.findTargetSumWays(nums7, target7)); // Expected: 8 (2^3)
        System.out.println("Subset Sum DP: " + solution.findTargetSumWaysSubsetSum(nums7, target7)); // Expected: 8
        System.out.println("Expected: 8\n");

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("=== Performance Comparison ===");
        TargetSum solution = new TargetSum();

        // Create test case
        int[] nums = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        int target = 5;

        long startTime, endTime;

        // Test Brute Force approach (comment out for large inputs due to exponential
        // time)
        startTime = System.nanoTime();
        int result1 = solution.findTargetSumWays(nums, target);
        endTime = System.nanoTime();
        System.out.println("Brute Force: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Memoization approach
        startTime = System.nanoTime();
        int result2 = solution.findTargetSumWaysMemo(nums, target);
        endTime = System.nanoTime();
        System.out.println("Memoization: " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Subset Sum DP approach
        startTime = System.nanoTime();
        int result3 = solution.findTargetSumWaysSubsetSum(nums, target);
        endTime = System.nanoTime();
        System.out.println("Subset Sum DP: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test 2D DP approach
        startTime = System.nanoTime();
        int result4 = solution.findTargetSumWays2D(nums, target);
        endTime = System.nanoTime();
        System.out.println("2D DP: " + result4 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Space Optimized approach
        startTime = System.nanoTime();
        int result5 = solution.findTargetSumWaysSpaceOptimized(nums, target);
        endTime = System.nanoTime();
        System.out.println("Space Optimized: " + result5 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        System.out.println("All approaches should return the same result: " +
                (result1 == result2 && result2 == result3 &&
                        result3 == result4 && result4 == result5));
    }
}
