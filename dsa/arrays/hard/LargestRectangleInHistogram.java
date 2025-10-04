package arrays.hard;

import java.util.*;

/**
 * LeetCode 84: Largest Rectangle in Histogram
 * https://leetcode.com/problems/largest-rectangle-in-histogram/
 *
 * Description:
 * Given an array of integers heights representing the histogram's bar height
 * where the width of each bar is 1,
 * return the area of the largest rectangle in the histogram.
 *
 * Constraints:
 * - 1 <= heights.length <= 10^5
 * - 0 <= heights[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it using divide and conquer?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Use stack to maintain indices of increasing heights
 * 2. When current height < stack top, calculate area with stack top as smallest
 * bar
 * 3. Width is distance between current index and element before stack top
 */
public class LargestRectangleInHistogram {
    public int largestRectangleArea(int[] heights) {
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;

        for (int i = 0; i <= heights.length; i++) {
            int currentHeight = (i == heights.length) ? 0 : heights[i];

            while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }

            stack.push(i);
        }

        return maxArea;
    }

    public static void main(String[] args) {
        LargestRectangleInHistogram solution = new LargestRectangleInHistogram();

        // Test Case 1: Normal case
        System.out.println(solution.largestRectangleArea(new int[] { 2, 1, 5, 6, 2, 3 })); // Expected: 10

        // Test Case 2: Edge case - increasing heights
        System.out.println(solution.largestRectangleArea(new int[] { 2, 4 })); // Expected: 4

        // Test Case 3: Corner case - single bar
        System.out.println(solution.largestRectangleArea(new int[] { 5 })); // Expected: 5

        // Test Case 4: Large input - all same height
        System.out.println(solution.largestRectangleArea(new int[] { 3, 3, 3, 3 })); // Expected: 12

        // Test Case 5: Minimum input - decreasing heights
        System.out.println(solution.largestRectangleArea(new int[] { 6, 2, 1 })); // Expected: 6

        // Test Case 6: Special case - valley pattern
        System.out.println(solution.largestRectangleArea(new int[] { 5, 1, 5 })); // Expected: 5

        // Test Case 7: Boundary case - zeros included
        System.out.println(solution.largestRectangleArea(new int[] { 0, 2, 0 })); // Expected: 2

        // Test Case 8: Mountain pattern
        System.out.println(solution.largestRectangleArea(new int[] { 1, 2, 3, 2, 1 })); // Expected: 6

        // Test Case 9: All zeros
        System.out.println(solution.largestRectangleArea(new int[] { 0, 0, 0 })); // Expected: 0

        // Test Case 10: Large rectangle at end
        System.out.println(solution.largestRectangleArea(new int[] { 1, 1, 1, 10, 10 })); // Expected: 20
    }
}
