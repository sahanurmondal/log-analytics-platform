package stacks.medium;

import java.util.*;

/**
 * Advanced Variation: Monotonic Stack
 * 
 * Description: Implement a monotonic stack that supports push, pop, and
 * retrieving the next/previous greater/smaller element.
 *
 * Constraints:
 * - Operations: push, pop, nextGreater, nextSmaller, prevGreater, prevSmaller
 * 
 * Follow-up Questions:
 * 1. Can you support getMin/getMax?
 * 2. Can you optimize for large number of operations?
 * 3. Can you handle duplicate values?
 */
public class MonotonicStack {
    private Stack<Integer> stack = new Stack<>();

    public void push(int x) {
        stack.push(x);
    }

    public int pop() {
        return stack.pop();
    }

    // Next greater element for all elements in array
    public int[] nextGreater(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> s = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!s.isEmpty() && nums[i] > nums[s.peek()])
                res[s.pop()] = nums[i];
            s.push(i);
        }
        return res;
    }

    // Next smaller element for all elements in array
    public int[] nextSmaller(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> s = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!s.isEmpty() && nums[i] < nums[s.peek()])
                res[s.pop()] = nums[i];
            s.push(i);
        }
        return res;
    }

    // Previous greater element for all elements in array
    public int[] prevGreater(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> s = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!s.isEmpty() && nums[i] > nums[s.peek()])
                res[s.pop()] = nums[i];
            s.push(i);
        }
        return res;
    }

    // Previous smaller element for all elements in array
    public int[] prevSmaller(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> s = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!s.isEmpty() && nums[i] < nums[s.peek()])
                res[s.pop()] = nums[i];
            s.push(i);
        }
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MonotonicStack solution = new MonotonicStack();

        // Test case 1: Basic case
        int[] nums1 = { 2, 1, 2, 4, 3 };
        System.out.println("Test 1 - Next greater: " + Arrays.toString(solution.nextGreater(nums1)));
        System.out.println("Next smaller: " + Arrays.toString(solution.nextSmaller(nums1)));
        System.out.println("Prev greater: " + Arrays.toString(solution.prevGreater(nums1)));
        System.out.println("Prev smaller: " + Arrays.toString(solution.prevSmaller(nums1)));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " + Arrays.toString(solution.nextGreater(new int[] { 5 })));
        System.out.println("All same: " + Arrays.toString(solution.nextGreater(new int[] { 2, 2, 2 })));
    }
}
