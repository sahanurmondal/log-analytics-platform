package arrays.hard;

/**
 * LeetCode 42: Trapping Rain Water (Medium Variant)
 * https://leetcode.com/problems/trapping-rain-water/
 *
 * Description:
 * Given n non-negative integers representing an elevation map where the width
 * of each bar is 1,
 * compute how much water it can trap after raining.
 *
 * Image (original):
 * https://assets.leetcode.com/uploads/2018/10/22/rainwatertrap.png
 * Text-style diagram:
 * Example: height = [0,1,0,2,1,0,1,3,2,1,2,1]
 *
 * 3| |
 * 2| | | | | | | | |
 * 1| | | | | | | | | | | | | | |
 * 0|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|
 * 0 1 2 3 4 5 6 7 8 9 10 11
 * Water trapped is shown by spaces between bars.
 *
 * Constraints:
 * - 1 <= height.length <= 2 * 10^4
 * - 0 <= height[i] <= 10^5
 *
 * Follow-up:
 * - Can you solve it in O(n) time and O(1) space?
 * - How would you handle if heights are in a linked list?
 */
public class TrappingRainWater {

    // Main solution - Two pointers
    public int trap(int[] height) {
        if (height == null || height.length < 3)
            return 0;

        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }

        return water;
    }

    // Alternative solution - Dynamic programming
    public int trapDP(int[] height) {
        if (height.length < 3)
            return 0;

        int n = height.length;
        int[] leftMax = new int[n];
        int[] rightMax = new int[n];

        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        }

        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }

        int water = 0;
        for (int i = 0; i < n; i++) {
            water += Math.min(leftMax[i], rightMax[i]) - height[i];
        }

        return water;
    }

    // Alternative solution - Stack based
    public int trapStack(int[] height) {
        java.util.Stack<Integer> stack = new java.util.Stack<>();
        int water = 0;

        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int bottom = stack.pop();
                if (stack.isEmpty())
                    break;

                int distance = i - stack.peek() - 1;
                int boundedHeight = Math.min(height[i], height[stack.peek()]) - height[bottom];
                water += distance * boundedHeight;
            }
            stack.push(i);
        }

        return water;
    }

    public static void main(String[] args) {
        TrappingRainWater solution = new TrappingRainWater();

        // Test Case 1: Normal case
        System.out.println(solution.trap(new int[] { 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 })); // Expected: 6

        // Test Case 2: Edge case - no water
        System.out.println(solution.trap(new int[] { 3, 0, 2, 0, 4 })); // Expected: 5

        // Test Case 3: Corner case - increasing heights
        System.out.println(solution.trap(new int[] { 1, 2, 3, 4, 5 })); // Expected: 0

        // Test Case 4: Decreasing heights
        System.out.println(solution.trap(new int[] { 5, 4, 3, 2, 1 })); // Expected: 0

        // Test Case 5: Minimum input
        System.out.println(solution.trap(new int[] { 3, 0, 2 })); // Expected: 2

        // Test Case 6: Valley pattern
        System.out.println(solution.trap(new int[] { 4, 2, 0, 3, 2, 5 })); // Expected: 9

        // Test Case 7: All zeros
        System.out.println(solution.trap(new int[] { 0, 0, 0, 0 })); // Expected: 0

        // Test Case 8: Single peak
        System.out.println(solution.trap(new int[] { 2, 0, 5, 0, 2 })); // Expected: 6

        // Test Case 9: Two peaks
        System.out.println(solution.trap(new int[] { 3, 2, 0, 4 })); // Expected: 5

        // Test Case 10: Complex pattern
        System.out.println(solution.trap(new int[] { 5, 2, 7, 2, 6, 1, 5, 1, 2, 3, 4 })); // Expected: 14
    }
}
