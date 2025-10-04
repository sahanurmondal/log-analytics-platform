package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 209: Minimum Size Subarray Sum
 * https://leetcode.com/problems/minimum-size-subarray-sum/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Given an array of positive integers nums and a positive integer
 * target, return the minimal length of a contiguous subarray of which the sum
 * is greater than or equal to target. If there is no such subarray, return 0.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 1 <= nums[i] <= 10^4
 * - 1 <= target <= 10^9
 * 
 * Follow-up Questions:
 * 1. Can you find the longest such subarray?
 * 2. Can you handle negative numbers?
 * 3. Can you find the number of such subarrays?
 */
public class MinimumSizeSubarraySum {

    // Approach 1: Sliding window (O(n) time)
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0, sum = 0, minLen = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                minLen = Math.min(minLen, right - left + 1);
                sum -= nums[left++];
            }
        }
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    // Follow-up 1: Longest subarray with sum >= target
    public int longestSubArrayLen(int target, int[] nums) {
        int left = 0, sum = 0, maxLen = 0;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                maxLen = Math.max(maxLen, right - left + 1);
                sum -= nums[left++];
            }
        }
        return maxLen;
    }

    // Follow-up 2: Number of subarrays with sum >= target
    public int countSubArraysWithSumAtLeastTarget(int target, int[] nums) {
        int left = 0, sum = 0, count = 0;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                count += nums.length - right;
                sum -= nums[left++];
            }
        }
        return count;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MinimumSizeSubarraySum solution = new MinimumSizeSubarraySum();

        // Test case 1: Basic case
        int[] nums1 = { 2, 3, 1, 2, 4, 3 };
        int target1 = 7;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", target: " + target1 + " Expected: 2");
        System.out.println("Result: " + solution.minSubArrayLen(target1, nums1));

        // Test case 2: No valid subarray
        int[] nums2 = { 1, 1, 1, 1, 1 };
        int target2 = 10;
        System.out.println("\nTest 2 - No valid subarray:");
        System.out.println("Result: " + solution.minSubArrayLen(target2, nums2));

        // Test case 3: Longest subarray
        System.out.println("\nTest 3 - Longest subarray:");
        System.out.println(solution.longestSubArrayLen(target1, nums1));

        // Test case 4: Count subarrays
        System.out.println("\nTest 4 - Count subarrays:");
        System.out.println(solution.countSubArraysWithSumAtLeastTarget(target1, nums1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: " + solution.minSubArrayLen(1, new int[] {}));
        System.out.println("Single element: " + solution.minSubArrayLen(1, new int[] { 1 }));
        System.out.println("Target larger than sum: " + solution.minSubArrayLen(100, nums1));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        int result = solution.minSubArrayLen(5000, large);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
