package stacks.medium;

import java.util.*;

/**
 * LeetCode 907: Sum of Subarray Minimums
 * https://leetcode.com/problems/sum-of-subarray-minimums/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given an array of integers, return the sum of the minimum value
 * of every subarray.
 *
 * Constraints:
 * - 1 <= arr.length <= 3 * 10^4
 * - 1 <= arr[i] <= 3 * 10^4
 * 
 * Follow-up Questions:
 * 1. Can you find the average minimum?
 * 2. Can you count subarrays with minimum at most k?
 * 3. Can you optimize for large arrays?
 */
public class SumOfSubarrayMinimums {

    // Approach 1: Monotonic stack for min contributions
    public int sumSubarrayMins(int[] arr) {
        int n = arr.length, mod = (int) 1e9 + 7;
        Stack<Integer> stack = new Stack<>();
        int[] left = new int[n], right = new int[n];
        for (int i = 0; i < n; i++) {
            left[i] = i + 1;
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i])
                stack.pop();
            left[i] = stack.isEmpty() ? i + 1 : i - stack.peek();
            stack.push(i);
        }
        stack.clear();
        for (int i = n - 1; i >= 0; i--) {
            right[i] = n - i;
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i])
                stack.pop();
            right[i] = stack.isEmpty() ? n - i : stack.peek() - i;
            stack.push(i);
        }
        long res = 0;
        for (int i = 0; i < n; i++) {
            res = (res + (long) arr[i] * left[i] * right[i]) % mod;
        }
        return (int) res;
    }

    // Follow-up 1: Average minimum
    public double averageSubarrayMin(int[] arr) {
        int sum = sumSubarrayMins(arr);
        int n = arr.length;
        return (double) sum / (n * (n + 1) / 2);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        SumOfSubarrayMinimums solution = new SumOfSubarrayMinimums();

        // Test case 1: Basic case
        int[] arr1 = { 3, 1, 2, 4 };
        System.out.println("Test 1 - arr: " + Arrays.toString(arr1) + " Expected: 17");
        System.out.println("Result: " + solution.sumSubarrayMins(arr1));

        // Test case 2: Average minimum
        System.out.println("\nTest 2 - Average minimum:");
        System.out.println(solution.averageSubarrayMin(arr1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " + solution.sumSubarrayMins(new int[] { 5 }));
        System.out.println("All same: " + solution.sumSubarrayMins(new int[] { 2, 2, 2 }));
    }
}
