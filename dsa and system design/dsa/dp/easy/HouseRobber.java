package dp.easy;

/**
 * LeetCode 198: House Robber
 * https://leetcode.com/problems/house-robber/
 *
 * Description:
 * You are a professional robber planning to rob houses along a street.
 * Each house has a certain amount of money stashed, the only constraint
 * stopping you from robbing each of them
 * is that adjacent houses have security systems connected and it will
 * automatically contact the police
 * if two adjacent houses were broken into on the same night.
 * Given an integer array nums representing the amount of money of each house,
 * return the maximum amount of money you can rob tonight without alerting the
 * police.
 *
 * Constraints:
 * - 1 <= nums.length <= 100
 * - 0 <= nums[i] <= 400
 *
 * Company Tags: Google, Amazon, Microsoft, Apple
 * Difficulty: Easy
 */
public class HouseRobber {

    // Approach 1: DP - O(n) time, O(n) space
    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1)
            return nums[0];

        int[] dp = new int[n];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }

        return dp[n - 1];
    }

    // Approach 2: Space Optimized - O(n) time, O(1) space
    public int robOptimized(int[] nums) {
        int prev2 = 0, prev1 = 0;

        for (int num : nums) {
            int current = Math.max(prev1, prev2 + num);
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    public static void main(String[] args) {
        HouseRobber solution = new HouseRobber();

        System.out.println("=== House Robber Test Cases ===");

        int[] nums1 = { 1, 2, 3, 1 };
        System.out.println("Houses: " + java.util.Arrays.toString(nums1));
        System.out.println("Max Rob: " + solution.rob(nums1));
        System.out.println("Expected: 4\n");

        int[] nums2 = { 2, 7, 9, 3, 1 };
        System.out.println("Houses: " + java.util.Arrays.toString(nums2));
        System.out.println("Max Rob: " + solution.rob(nums2));
        System.out.println("Expected: 12\n");
    }
}
