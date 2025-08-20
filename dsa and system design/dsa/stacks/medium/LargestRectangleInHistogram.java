package stacks.medium;

import java.util.*;

/**
 * LeetCode 84: Largest Rectangle in Histogram
 * https://leetcode.com/problems/largest-rectangle-in-histogram/
 *
 * Description:
 * Given an array of integers heights representing the histogram's bar height
 * where the width of each bar is 1, return the area of the largest rectangle in
 * the histogram.
 *
 * Constraints:
 * - 1 <= heights.length <= 10^5
 * - 0 <= heights[i] <= 10^4
 *
 * ASCII Art:
 * heights = [2,1,5,6,2,3]
 * 
 * 6 ┌─┐
 * 5 │ ├─┐
 * 4 │ │ │
 * 3 │ │ │ ┌─┐
 * 2 ├─┤ │ ┌─┼─┤
 * 1 │ ├─┤ │ │ │
 * 0 └─┴─┴─┴─┴─┘
 * 2 1 5 6 2 3
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * - Can you extend to 2D rectangles?
 */
public class LargestRectangleInHistogram {

    // Approach 1: Monotonic stack
    public int largestRectangleArea(int[] heights) {
        int n = heights.length, maxArea = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i <= n; i++) {
            int h = i == n ? 0 : heights[i];
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }

    // Follow-up 1: Return coordinates of rectangle (not implemented for brevity)
    // Comprehensive test cases
    public static void main(String[] args) {
        LargestRectangleInHistogram solution = new LargestRectangleInHistogram();

        // Test case 1: Basic case
        int[] heights1 = {2,1,5,6,2,3};
        System.out.println("Test 1 - heights: " + Arrays.toString(heights1) + " Expected: 10");
        System.out.println("Result: " + solution.largestRectangleArea(heights1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single bar: " + solution.largestRectangleArea(new int[]{5}));
        System.out.println("All same: " + solution.largestRectangleArea(new int[]{2,2,2}));
    }
}
