package dp.medium;
import java.util.*;
/**
 * LeetCode 1671: Minimum Number of Removals to Make Mountain Array
 * https://leetcode.com/problems/minimum-number-of-removals-to-make-mountain-array/
 *
 * Description:
 * Given an array nums, return the minimum number of elements to remove to make
 * it a mountain array.
 *
 * Constraints:
 * - 3 <= nums.length <= 1000
 * - 1 <= nums[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it in O(n^2) time?
 */
public class MinimumNumberOfRemovalsToMakeMountainArray {
    // Approach 1: LIS from both sides - O(n^2) time, O(n) space
    public int minimumMountainRemovals(int[] nums) {
        int n = nums.length;

        int[] left = new int[n]; // LIS ending at i
        int[] right = new int[n]; // LIS starting at i (reversed)

        Arrays.fill(left, 1);
        Arrays.fill(right, 1);

        // Calculate LIS from left
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    left[i] = Math.max(left[i], left[j] + 1);
                }
            }
        }

        // Calculate LIS from right
        for (int i = n - 2; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] > nums[j]) {
                    right[i] = Math.max(right[i], right[j] + 1);
                }
            }
        }

        int maxMountain = 0;
        for (int i = 1; i < n - 1; i++) {
            if (left[i] > 1 && right[i] > 1) {
                maxMountain = Math.max(maxMountain, left[i] + right[i] - 1);
            }
        }

        return n - maxMountain;
    }

    public static void main(String[] args) {
        MinimumNumberOfRemovalsToMakeMountainArray solution = new MinimumNumberOfRemovalsToMakeMountainArray();
        // Edge Case 1: Normal case
        System.out.println(solution.minimumMountainRemovals(new int[] { 2, 1, 1, 5, 6, 2, 3, 1 })); // 3
        // Edge Case 2: Already mountain
        System.out.println(solution.minimumMountainRemovals(new int[] { 1, 2, 3, 2, 1 })); // 0
        // Edge Case 3: All increasing
        System.out.println(solution.minimumMountainRemovals(new int[] { 1, 2, 3, 4, 5 })); // 2
        // Edge Case 4: All decreasing
        System.out.println(solution.minimumMountainRemovals(new int[] { 5, 4, 3, 2, 1 })); // 2
        // Edge Case 5: Large input
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++)
            large[i] = i;
        System.out.println(solution.minimumMountainRemovals(large)); // 998
    }
}
