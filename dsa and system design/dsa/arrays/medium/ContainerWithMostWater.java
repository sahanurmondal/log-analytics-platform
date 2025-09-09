package arrays.medium;

/**
 * LeetCode 11: Container With Most Water
 * https://leetcode.com/problems/container-with-most-water/
 *
 * Description:
 * You are given an integer array height of length n. There are n vertical lines
 * drawn such that the two endpoints of the ith line are (i, 0) and (i,
 * height[i]).
 * Find two lines that together with the x-axis form a container that can hold
 * the most water.
 *
 * Constraints:
 * - n == height.length
 * - 2 <= n <= 10^5
 * - 0 <= height[i] <= 10^4
 *
 * ASCII Art:
 * height = [1,8,6,2,5,4,8,3,7]
 * 
 * | | | |
 * | | | | | | |
 * | | | | | | | | | |
 * | | | | | | | | | | | | | | |
 * +---+---+---+---+---+---+---+
 * 0 1 2 3 4 5 6 7 8 (indices)
 * 
 * Max area = min(height[1], height[8]) * (8-1) = 7 * 7 = 49
 *
 * Follow-up:
 * - Can you solve it in O(n) time using two pointers?
 * - Can you prove why the two-pointer approach works?
 * - Can you extend to 3D containers?
 */
public class ContainerWithMostWater {
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

    // Alternative solution - Brute force
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

    public static void main(String[] args) {
        ContainerWithMostWater solution = new ContainerWithMostWater();
        // Edge Case 1: Normal case
        System.out.println(solution.maxArea(new int[] { 1, 8, 6, 2, 5, 4, 8, 3, 7 })); // 49
        // Edge Case 2: All heights same
        System.out.println(solution.maxArea(new int[] { 5, 5, 5, 5, 5 })); // 20
        // Edge Case 3: Only two heights
        System.out.println(solution.maxArea(new int[] { 1, 2 })); // 1
        // Edge Case 4: Heights with zero
        System.out.println(solution.maxArea(new int[] { 0, 0, 0, 0, 0 })); // 0
        // Edge Case 5: Increasing heights
        System.out.println(solution.maxArea(new int[] { 1, 2, 3, 4, 5 })); // 6
        // Edge Case 6: Decreasing heights
        System.out.println(solution.maxArea(new int[] { 5, 4, 3, 2, 1 })); // 6
        // Edge Case 7: Large input
        int[] large = new int[100000];
        for (int i = 0; i < 100000; i++)
            large[i] = i % 100;
        System.out.println(solution.maxArea(large)); // Should be large
    }
}