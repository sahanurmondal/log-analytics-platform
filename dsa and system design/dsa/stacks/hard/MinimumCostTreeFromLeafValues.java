package stacks.hard;

import java.util.*;

/**
 * LeetCode 1130: Minimum Cost Tree From Leaf Values
 * https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given an array arr of positive integers, build a binary tree
 * from arr and return the minimum possible sum of the values of each non-leaf
 * node. Each non-leaf node's value is the product of the largest leaf values in
 * its left and right subtree.
 *
 * Constraints:
 * - 1 <= arr.length <= 40
 * - 1 <= arr[i] <= 15
 * 
 * Follow-up Questions:
 * 1. Can you reconstruct the tree?
 * 2. Can you optimize for large arrays?
 * 3. Can you handle updates to the array?
 */
public class MinimumCostTreeFromLeafValues {

    // Approach 1: Monotonic stack (O(n) time)
    public int mctFromLeafValues(int[] arr) {
        int res = 0;
        Stack<Integer> stack = new Stack<>();
        stack.push(Integer.MAX_VALUE);
        for (int a : arr) {
            while (stack.peek() <= a) {
                int mid = stack.pop();
                res += mid * Math.min(stack.peek(), a);
            }
            stack.push(a);
        }
        while (stack.size() > 2) {
            res += stack.pop() * stack.peek();
        }
        return res;
    }

    // Follow-up 1: Reconstruct tree (not implemented for brevity)
    // Comprehensive test cases
    public static void main(String[] args) {
        MinimumCostTreeFromLeafValues solution = new MinimumCostTreeFromLeafValues();

        // Test case 1: Basic case
        int[] arr1 = { 6, 2, 4 };
        System.out.println("Test 1 - arr: " + Arrays.toString(arr1) + " Expected: 32");
        System.out.println("Result: " + solution.mctFromLeafValues(arr1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " + solution.mctFromLeafValues(new int[] { 7 }));
        System.out.println("Two elements: " + solution.mctFromLeafValues(new int[] { 2, 3 }));
    }
}
