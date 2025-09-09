package stacks.medium;

import java.util.*;

/**
 * LeetCode 503: Next Greater Element II
 * https://leetcode.com/problems/next-greater-element-ii/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given a circular array, return the next greater element for each
 * element.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^9 <= nums[i] <= 10^9
 * 
 * Follow-up Questions:
 * 1. Can you find next smaller element?
 * 2. Can you optimize for large arrays?
 * 3. Can you handle duplicate values?
 */
public class NextGreaterElementII {

    // Approach 1: Monotonic stack + circular array
    public int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 2 * n; i++) {
            int num = nums[i % n];
            while (!stack.isEmpty() && nums[stack.peek()] < num)
                res[stack.pop()] = num;
            if (i < n)
                stack.push(i);
        }
        return res;
    }

    // Follow-up 1: Next smaller element
    public int[] nextSmallerElements(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 2 * n; i++) {
            int num = nums[i % n];
            while (!stack.isEmpty() && nums[stack.peek()] > num)
                res[stack.pop()] = num;
            if (i < n)
                stack.push(i);
        }
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        NextGreaterElementII solution = new NextGreaterElementII();

        // Test case 1: Basic case
        int[] nums1 = { 1, 2, 1 };
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1));
        System.out.println("Result: " + Arrays.toString(solution.nextGreaterElements(nums1)));

        // Test case 2: Next smaller
        System.out.println("\nTest 2 - Next smaller:");
        System.out.println(Arrays.toString(solution.nextSmallerElements(nums1)));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " + Arrays.toString(solution.nextGreaterElements(new int[] { 5 })));
        System.out.println("All same: " + Arrays.toString(solution.nextGreaterElements(new int[] { 2, 2, 2 })));
    }
}
