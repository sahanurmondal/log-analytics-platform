package dp.medium;

/**
 * LeetCode 198: House Robber
 * https://leetcode.com/problems/house-robber/
 *
 * Description:
 * You are a professional robber planning to rob houses along a street. Each
 * house has a certain amount of money stashed.
 * Return the maximum amount of money you can rob tonight without alerting the
 * police (cannot rob adjacent houses).
 *
 * Constraints:
 * - 1 <= nums.length <= 100
 * - 0 <= nums[i] <= 40000
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * 
 * Company Tags: Amazon, Microsoft, Google, Apple, Adobe
 * Difficulty: Medium
 */
public class HouseRobber {

    // Approach 1: Dynamic Programming - O(n) time, O(n) space
    public int rob(int[] nums) {
        if (nums.length == 0)
            return 0;
        if (nums.length == 1)
            return nums[0];

        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }

        return dp[nums.length - 1];
    }

    // Approach 2: Space Optimized DP - O(n) time, O(1) space
    public int robOptimized(int[] nums) {
        if (nums.length == 0)
            return 0;
        if (nums.length == 1)
            return nums[0];

        int prev2 = nums[0];
        int prev1 = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    // Approach 3: Even More Optimized - O(n) time, O(1) space
    public int robSimple(int[] nums) {
        int robInclude = 0; // Maximum money including previous house
        int robExclude = 0; // Maximum money excluding previous house

        for (int num : nums) {
            int newExclude = Math.max(robInclude, robExclude);
            robInclude = robExclude + num;
            robExclude = newExclude;
        }

        return Math.max(robInclude, robExclude);
    }

    // Approach 4: Recursive with Memoization - O(n) time, O(n) space
    public int robMemo(int[] nums) {
        Integer[] memo = new Integer[nums.length];
        return robRecursive(nums, 0, memo);
    }

    private int robRecursive(int[] nums, int index, Integer[] memo) {
        if (index >= nums.length)
            return 0;
        if (memo[index] != null)
            return memo[index];

        // Choice: rob current house or skip it
        int robCurrent = nums[index] + robRecursive(nums, index + 2, memo);
        int skipCurrent = robRecursive(nums, index + 1, memo);

        memo[index] = Math.max(robCurrent, skipCurrent);
        return memo[index];
    }

    // Approach 5: Bottom-up with clearer logic - O(n) time, O(1) space
    public int robClear(int[] nums) {
        if (nums.length == 0)
            return 0;

        int rob = 0; // Maximum money if we rob current house
        int notRob = 0; // Maximum money if we don't rob current house

        for (int num : nums) {
            int currentRob = notRob + num; // Rob current house + max from houses before previous
            notRob = Math.max(rob, notRob); // Don't rob current, take max of previous states
            rob = currentRob;
        }

        return Math.max(rob, notRob);
    }

    public static void main(String[] args) {
        HouseRobber solution = new HouseRobber();

        System.out.println("=== House Robber Test Cases ===");

        // Test case 1: [1,2,3,1]
        int[] nums1 = { 1, 2, 3, 1 };
        System.out.println("Array: [1,2,3,1]");
        System.out.println("DP: " + solution.rob(nums1)); // Expected: 4
        System.out.println("Optimized: " + solution.robOptimized(nums1)); // Expected: 4
        System.out.println("Simple: " + solution.robSimple(nums1)); // Expected: 4
        System.out.println("Memoization: " + solution.robMemo(nums1)); // Expected: 4

        // Test case 2: [2,7,9,3,1]
        int[] nums2 = { 2, 7, 9, 3, 1 };
        System.out.println("\nArray: [2,7,9,3,1]");
        System.out.println("DP: " + solution.rob(nums2)); // Expected: 12
        System.out.println("Optimized: " + solution.robOptimized(nums2)); // Expected: 12
        System.out.println("Clear: " + solution.robClear(nums2)); // Expected: 12

        // Test case 3: [2,1,1,2]
        int[] nums3 = { 2, 1, 1, 2 };
        System.out.println("\nArray: [2,1,1,2]");
        System.out.println("DP: " + solution.rob(nums3)); // Expected: 4
        System.out.println("Simple: " + solution.robSimple(nums3)); // Expected: 4

        // Test case 4: Single house
        int[] nums4 = { 5 };
        System.out.println("\nArray: [5]");
        System.out.println("DP: " + solution.rob(nums4)); // Expected: 5

        // Test case 5: Two houses
        int[] nums5 = { 1, 2 };
        System.out.println("\nArray: [1,2]");
        System.out.println("DP: " + solution.rob(nums5)); // Expected: 2

        // Test case 6: All same values
        int[] nums6 = { 5, 5, 5, 5, 5 };
        System.out.println("\nArray: [5,5,5,5,5]");
        System.out.println("DP: " + solution.rob(nums6)); // Expected: 15

        // Test case 7: Increasing values
        int[] nums7 = { 1, 2, 3, 4, 5 };
        System.out.println("\nArray: [1,2,3,4,5]");
        System.out.println("DP: " + solution.rob(nums7)); // Expected: 9

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Comparison ===");
        HouseRobber solution = new HouseRobber();

        // Create large array for testing
        int[] largeNums = new int[10000];
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = (int) (Math.random() * 1000) + 1;
        }

        long startTime, endTime;

        // Test DP approach
        startTime = System.nanoTime();
        int result1 = solution.rob(largeNums.clone());
        endTime = System.nanoTime();
        System.out.println("DP: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test optimized approach
        startTime = System.nanoTime();
        int result2 = solution.robOptimized(largeNums.clone());
        endTime = System.nanoTime();
        System.out.println("Optimized: " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test simple approach
        startTime = System.nanoTime();
        int result3 = solution.robSimple(largeNums.clone());
        endTime = System.nanoTime();
        System.out.println("Simple: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test clear approach
        startTime = System.nanoTime();
        int result4 = solution.robClear(largeNums.clone());
        endTime = System.nanoTime();
        System.out.println("Clear: " + result4 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }
}
