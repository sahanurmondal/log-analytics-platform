package stacks.medium;

import java.util.*;

/**
 * Advanced Variation: Next Smaller Element
 * 
 * Description: Given an array, return the next smaller element for each
 * element.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you find previous smaller element?
 * 2. Can you optimize for large arrays?
 * 3. Can you handle duplicate values?
 */
public class NextSmallerElement {

    // Approach 1: Monotonic stack
    public int[] nextSmaller(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] > nums[i])
                res[stack.pop()] = nums[i];
            stack.push(i);
        }
        return res;
    }

    // Follow-up 1: Previous smaller element
    public int[] prevSmaller(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> stack = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && nums[stack.peek()] > nums[i])
                res[stack.pop()] = nums[i];
            stack.push(i);
        }
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        NextSmallerElement solution = new NextSmallerElement();

        // Test case 1: Basic case
        int[] nums1 = { 4, 5, 2, 10, 8 };
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1));
        System.out.println("Next smaller: " + Arrays.toString(solution.nextSmaller(nums1)));

        // Test case 2: Previous smaller
        System.out.println("\nTest 2 - Previous smaller:");
        System.out.println(Arrays.toString(solution.prevSmaller(nums1)));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " + Arrays.toString(solution.nextSmaller(new int[] { 5 })));
        System.out.println("All same: " + Arrays.toString(solution.nextSmaller(new int[] { 2, 2, 2 })));
    }
}
