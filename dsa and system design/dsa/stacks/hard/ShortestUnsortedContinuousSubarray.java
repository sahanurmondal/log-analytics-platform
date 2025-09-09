package stacks.hard;

import java.util.*;

/**
 * LeetCode 581: Shortest Unsorted Continuous Subarray
 * https://leetcode.com/problems/shortest-unsorted-continuous-subarray/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given an integer array, return the shortest subarray that if
 * sorted, results in the whole array being sorted.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^5 <= nums[i] <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you find the minimum number of swaps to sort?
 * 2. Can you handle duplicate values?
 * 3. Can you optimize for large arrays?
 */
public class ShortestUnsortedContinuousSubarray {

    // Approach 1: Stack-based boundaries
    public int findUnsortedSubarray(int[] nums) {
        int n = nums.length, left = n, right = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] > nums[i])
                left = Math.min(left, stack.pop());
            stack.push(i);
        }
        stack.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i])
                right = Math.max(right, stack.pop());
            stack.push(i);
        }
        return right > left ? right - left + 1 : 0;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ShortestUnsortedContinuousSubarray solution = new ShortestUnsortedContinuousSubarray();

        // Test case 1: Basic case
        int[] nums1 = { 2, 6, 4, 8, 10, 9, 15 };
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + " Expected: 5");
        System.out.println("Result: " + solution.findUnsortedSubarray(nums1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Already sorted: " + solution.findUnsortedSubarray(new int[] { 1, 2, 3, 4, 5 }));
        System.out.println("Single element: " + solution.findUnsortedSubarray(new int[] { 1 }));
        // Edge Case: Reverse sorted
        System.out.println("Reverse sorted: " + solution.findUnsortedSubarray(new int[] { 5, 4, 3, 2, 1 })); // 5
        // Edge Case: Two elements out of order
        System.out.println("Two elements out of order: " + solution.findUnsortedSubarray(new int[] { 2, 1 })); // 2
    }
}
