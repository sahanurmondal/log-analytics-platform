package arrays.medium;

/**
 * LeetCode 11: Container With Most Water
 * https://leetcode.com/problems/container-with-most-water/
 *
 * Description:
 * You are given an integer array height of length n. There are n vertical lines
 * drawn
 * such that the two endpoints of the ith line are (i, 0) and (i, height[i]).
 * Find two lines that together with the x-axis form a container that contains
 * the most water.
 *
 * Constraints:
 * - n == height.length
 * - 2 <= n <= 10^5
 * - 0 <= height[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class ContainerMostWater {

    // Main solution - Two pointers
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int maxArea = 0;

        while (left < right) {
            int area = Math.min(height[left], height[right]) * (right - left);
            maxArea = Math.max(maxArea, area);

            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxArea;
    }

    // Alternative solution - Brute force (for comparison)
    public int maxAreaBruteForce(int[] height) {
        int maxArea = 0;

        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                int area = Math.min(height[i], height[j]) * (j - i);
                maxArea = Math.max(maxArea, area);
            }
        }

        return maxArea;
    }

    // Follow-up solution - Optimized two pointers with early termination
    public int maxAreaOptimized(int[] height) {
        int left = 0, right = height.length - 1;
        int maxArea = 0;
        int maxHeight = 0;

        // Find maximum height for early termination
        for (int h : height) {
            maxHeight = Math.max(maxHeight, h);
        }

        while (left < right) {
            int area = Math.min(height[left], height[right]) * (right - left);
            maxArea = Math.max(maxArea, area);

            // Early termination if we can't get better area
            if (maxArea >= maxHeight * (right - left)) {
                break;
            }

            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxArea;
    }

    public static void main(String[] args) {
        ContainerMostWater solution = new ContainerMostWater();

        // Test Case 1: Normal case
        System.out.println(solution.maxArea(new int[] { 1, 8, 6, 2, 5, 4, 8, 3, 7 })); // Expected: 49

        // Test Case 2: Edge case - minimum input
        System.out.println(solution.maxArea(new int[] { 1, 1 })); // Expected: 1

        // Test Case 3: Corner case - increasing heights
        System.out.println(solution.maxArea(new int[] { 1, 2, 3, 4, 5 })); // Expected: 6

        // Test Case 4: Large input - same heights
        System.out.println(solution.maxArea(new int[] { 5, 5, 5, 5 })); // Expected: 15

        // Test Case 5: Peak at ends
        System.out.println(solution.maxArea(new int[] { 9, 1, 1, 1, 9 })); // Expected: 36

        // Test Case 6: Special case - zero heights
        System.out.println(solution.maxArea(new int[] { 0, 5, 0 })); // Expected: 0

        // Test Case 7: Valley pattern
        System.out.println(solution.maxArea(new int[] { 8, 1, 2, 1, 8 })); // Expected: 32

        // Test Case 8: Decreasing heights
        System.out.println(solution.maxArea(new int[] { 5, 4, 3, 2, 1 })); // Expected: 6

        // Test Case 9: Single high wall
        System.out.println(solution.maxArea(new int[] { 1, 10000, 1 })); // Expected: 2

        // Test Case 10: Random pattern
        System.out.println(solution.maxArea(new int[] { 2, 3, 4, 5, 18, 17, 6 })); // Expected: 17
    }
}
