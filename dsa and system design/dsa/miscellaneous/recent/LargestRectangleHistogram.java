package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Largest Rectangle in Histogram with Updates
 * 
 * Description:
 * Given histogram heights, find largest rectangle area. Support dynamic
 * updates.
 * 
 * Companies: Google, Amazon, Microsoft
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class LargestRectangleHistogram {

    public int largestRectangleArea(int[] heights) {
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;

        for (int i = 0; i <= heights.length; i++) {
            int h = (i == heights.length) ? 0 : heights[i];

            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }

        return maxArea;
    }

    public static void main(String[] args) {
        LargestRectangleHistogram solution = new LargestRectangleHistogram();
        int[] heights = { 2, 1, 5, 6, 2, 3 };
        System.out.println(solution.largestRectangleArea(heights)); // 10
    }
}
