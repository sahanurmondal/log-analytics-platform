package stacks.hard;

import java.util.*;

/**
 * LeetCode 2104: Sum of Subarray Ranges
 * https://leetcode.com/problems/sum-of-subarray-ranges/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given an integer array nums, return the sum of ranges (max -
 * min) for all subarrays.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * 
 * Follow-up Questions:
 * 1. Can you find the average range?
 * 2. Can you count subarrays with range at most k?
 * 3. Can you optimize for large arrays?
 */
public class SumOfSubarrayRanges {

    // Approach 1: Monotonic stack for min/max contributions
    public long subArrayRanges(int[] nums) {
        int n = nums.length;
        long res = 0;
        res += getSum(nums, true);
        res -= getSum(nums, false);
        return res;
    }

    private long getSum(int[] nums, boolean isMax) {
        int n = nums.length;
        long sum = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i <= n; i++) {
            int val = i < n ? nums[i] : (isMax ? Integer.MAX_VALUE : Integer.MIN_VALUE);
            while (!stack.isEmpty() && (isMax ? nums[stack.peek()] < val : nums[stack.peek()] > val)) {
                int j = stack.pop();
                int k = stack.isEmpty() ? -1 : stack.peek();
                sum += (long) nums[j] * (i - j) * (j - k);
            }
            stack.push(i);
        }
        return sum;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        SumOfSubarrayRanges solution = new SumOfSubarrayRanges();

        // Test case 1: Basic case
        int[] nums1 = { 1, 2, 3 };
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + " Expected: 4");
        System.out.println("Result: " + solution.subArrayRanges(nums1));

        // Test case 2: Repeated elements
        int[] nums2 = { 3, 3, 3 };
        System.out.println("Test 2 - nums: " + Arrays.toString(nums2) + " Expected: 0");
        System.out.println("Result: " + solution.subArrayRanges(nums2));

        // Test case 3: Negative and positive mix
        int[] nums3 = { -1, 2, -3, 4 };
        System.out.println("Test 3 - nums: " + Arrays.toString(nums3) + " Expected: 14");
        System.out.println("Result: " + solution.subArrayRanges(nums3));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " + solution.subArrayRanges(new int[] { 5 }));
        System.out.println("All same: " + solution.subArrayRanges(new int[] { 2, 2, 2 }));
    }
}
