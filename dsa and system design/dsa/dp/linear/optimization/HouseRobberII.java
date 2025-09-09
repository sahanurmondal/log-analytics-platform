package dp.linear.optimization;

import java.util.Arrays;

/**
 * LeetCode 213: House Robber II
 * https://leetcode.com/problems/house-robber-ii/
 *
 * Description:
 * You are a professional robber planning to rob houses along a street. Each
 * house has a certain amount of money stashed.
 * All houses at this place are arranged in a circle. That means the first house
 * is the neighbor of the last one.
 * Meanwhile, adjacent houses have security systems connected and it will
 * automatically contact the police if two adjacent houses were broken into on
 * the same night.
 * Given an integer array nums representing the amount of money of each house,
 * return the maximum amount of money you can rob tonight without alerting the
 * police.
 *
 * Constraints:
 * - 1 <= nums.length <= 100
 * - 0 <= nums[i] <= 1000
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * - What if there are k constraints (can't rob k consecutive houses)?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class HouseRobberII {

    // Approach 1: Two Linear Robberies - O(n) time, O(1) space
    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1)
            return nums[0];
        if (n == 2)
            return Math.max(nums[0], nums[1]);

        // Case 1: Rob houses 0 to n-2 (exclude last house)
        int robFirst = robLinear(nums, 0, n - 2);

        // Case 2: Rob houses 1 to n-1 (exclude first house)
        int robLast = robLinear(nums, 1, n - 1);

        return Math.max(robFirst, robLast);
    }

    private int robLinear(int[] nums, int start, int end) {
        int prev2 = 0, prev1 = 0;

        for (int i = start; i <= end; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    // Approach 2: DP with Arrays - O(n) time, O(n) space
    public int robDP(int[] nums) {
        int n = nums.length;
        if (n == 1)
            return nums[0];
        if (n == 2)
            return Math.max(nums[0], nums[1]);

        // DP for houses 0 to n-2
        int[] dp1 = new int[n - 1];
        dp1[0] = nums[0];
        dp1[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n - 1; i++) {
            dp1[i] = Math.max(dp1[i - 1], dp1[i - 2] + nums[i]);
        }

        // DP for houses 1 to n-1
        int[] dp2 = new int[n - 1];
        dp2[0] = nums[1];
        if (n > 2)
            dp2[1] = Math.max(nums[1], nums[2]);

        for (int i = 2; i < n - 1; i++) {
            dp2[i] = Math.max(dp2[i - 1], dp2[i - 2] + nums[i + 1]);
        }

        return Math.max(dp1[n - 2], dp2[n - 2]);
    }

    // Approach 3: Memoization - O(n) time, O(n) space
    public int robMemo(int[] nums) {
        int n = nums.length;
        if (n == 1)
            return nums[0];

        Integer[][] memo = new Integer[n][2];
        return Math.max(
                robMemoHelper(nums, 0, 0, memo), // Don't rob first house
                robMemoHelper(nums, 0, 1, memo) // Rob first house
        );
    }

    private int robMemoHelper(int[] nums, int index, int robbedFirst, Integer[][] memo) {
        int n = nums.length;

        if (index >= n)
            return 0;
        if (index == n - 1 && robbedFirst == 1)
            return 0; // Can't rob last if robbed first

        if (memo[index][robbedFirst] != null)
            return memo[index][robbedFirst];

        // Don't rob current house
        int dontRob = robMemoHelper(nums, index + 1, robbedFirst, memo);

        // Rob current house
        int robCurrent = nums[index] + robMemoHelper(nums, index + 2, robbedFirst, memo);

        memo[index][robbedFirst] = Math.max(dontRob, robCurrent);
        return memo[index][robbedFirst];
    }

    // Approach 4: State Machine DP - O(n) time, O(1) space
    public int robStateMachine(int[] nums) {
        int n = nums.length;
        if (n == 1)
            return nums[0];
        if (n == 2)
            return Math.max(nums[0], nums[1]);

        // State: [robbed_first][current_position_robbed]
        int[][] dp = new int[2][2];

        // Initialize: first house
        dp[1][1] = nums[0]; // Rob first house
        dp[0][0] = 0; // Don't rob first house

        for (int i = 1; i < n; i++) {
            int[][] newDp = new int[2][2];

            // If we robbed the first house
            if (i == n - 1) {
                // Last house - can't rob if we robbed first
                newDp[1][0] = Math.max(dp[1][0], dp[1][1]);
                newDp[1][1] = 0; // Invalid state
            } else {
                newDp[1][0] = Math.max(dp[1][0], dp[1][1]); // Don't rob current
                newDp[1][1] = dp[1][0] + nums[i]; // Rob current
            }

            // If we didn't rob the first house
            newDp[0][0] = Math.max(dp[0][0], dp[0][1]); // Don't rob current
            newDp[0][1] = dp[0][0] + nums[i]; // Rob current

            dp = newDp;
        }

        return Math.max(Math.max(dp[0][0], dp[0][1]), Math.max(dp[1][0], dp[1][1]));
    }

    // Approach 5: Get Optimal Robbing Plan - O(n) time, O(n) space
    public boolean[] getOptimalPlan(int[] nums) {
        int n = nums.length;
        if (n == 1)
            return new boolean[] { true };

        // Try both scenarios and pick the better one
        boolean[] plan1 = getLinearPlan(nums, 0, n - 2);
        boolean[] plan2 = getLinearPlan(nums, 1, n - 1);

        int sum1 = 0, sum2 = 0;
        for (int i = 0; i < plan1.length; i++) {
            if (plan1[i])
                sum1 += nums[i];
        }
        for (int i = 0; i < plan2.length; i++) {
            if (plan2[i])
                sum2 += nums[i];
        }

        boolean[] result = new boolean[n];
        if (sum1 >= sum2) {
            System.arraycopy(plan1, 0, result, 0, n - 1);
        } else {
            System.arraycopy(plan2, 1, result, 1, n - 1);
        }

        return result;
    }

    private boolean[] getLinearPlan(int[] nums, int start, int end) {
        int len = end - start + 1;
        if (len == 0)
            return new boolean[0];
        if (len == 1)
            return new boolean[] { true };

        int[] dp = new int[len];
        boolean[] plan = new boolean[len];

        dp[0] = nums[start];
        plan[0] = true;

        dp[1] = Math.max(nums[start], nums[start + 1]);
        plan[1] = nums[start + 1] > nums[start];

        for (int i = 2; i < len; i++) {
            if (dp[i - 1] > dp[i - 2] + nums[start + i]) {
                dp[i] = dp[i - 1];
                plan[i] = false;
            } else {
                dp[i] = dp[i - 2] + nums[start + i];
                plan[i] = true;
            }
        }

        return plan;
    }

    public static void main(String[] args) {
        HouseRobberII solution = new HouseRobberII();

        System.out.println("=== House Robber II Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 2, 3, 2 };
        System.out.println("Test 1 - Houses: " + Arrays.toString(nums1));
        System.out.println("Two Linear: " + solution.rob(nums1));
        System.out.println("DP Arrays: " + solution.robDP(nums1));
        System.out.println("Memoization: " + solution.robMemo(nums1));
        System.out.println("State Machine: " + solution.robStateMachine(nums1));
        System.out.println("Optimal Plan: " + Arrays.toString(solution.getOptimalPlan(nums1)));
        System.out.println("Expected: 3\n");

        // Test Case 2: Another example
        int[] nums2 = { 1, 2, 3, 1 };
        System.out.println("Test 2 - Houses: " + Arrays.toString(nums2));
        System.out.println("Two Linear: " + solution.rob(nums2));
        System.out.println("Expected: 4\n");

        // Test Case 3: Single house
        int[] nums3 = { 5 };
        System.out.println("Test 3 - Houses: " + Arrays.toString(nums3));
        System.out.println("Two Linear: " + solution.rob(nums3));
        System.out.println("Expected: 5\n");

        performanceTest();
    }

    private static void performanceTest() {
        HouseRobberII solution = new HouseRobberII();

        int[] largeArray = new int[100];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 1000);
        }

        System.out.println("=== Performance Test (Houses: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.rob(largeArray);
        long end = System.nanoTime();
        System.out.println("Two Linear: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.robDP(largeArray);
        end = System.nanoTime();
        System.out.println("DP Arrays: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.robMemo(largeArray);
        end = System.nanoTime();
        System.out.println("Memoization: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
