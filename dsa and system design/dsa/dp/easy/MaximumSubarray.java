package dp.easy;

/**
 * LeetCode 53: Maximum Subarray
 * https://leetcode.com/problems/maximum-subarray/
 *
 * Description:
 * Given an integer array nums, find the contiguous subarray (containing at
 * least one number)
 * which has the largest sum and return its sum.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 *
 * Company Tags: Google, Amazon, Microsoft, Apple, Bloomberg
 * Difficulty: Easy
 */
public class MaximumSubarray {

    // Approach 1: Kadane's Algorithm - O(n) time, O(1) space
    public int maxSubArray(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];

        for (int i = 1; i < nums.length; i++) {
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }

        return maxSoFar;
    }

    // Approach 2: DP - O(n) time, O(n) space
    public int maxSubArrayDP(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = nums[0];
        int maxSum = dp[0];

        for (int i = 1; i < n; i++) {
            dp[i] = Math.max(nums[i], dp[i - 1] + nums[i]);
            maxSum = Math.max(maxSum, dp[i]);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        MaximumSubarray solution = new MaximumSubarray();

        System.out.println("=== Maximum Subarray Test Cases ===");

        int[] nums1 = { -2, 1, -3, 4, -1, 2, 1, -5, 4 };
        System.out.println("Array: " + java.util.Arrays.toString(nums1));
        System.out.println("Max Subarray Sum: " + solution.maxSubArray(nums1));
        System.out.println("Expected: 6\n");
    }
}
