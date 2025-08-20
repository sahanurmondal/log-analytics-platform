package arrays.easy;

import java.util.*;

/**
 * LeetCode 283: Move Zeroes
 * https://leetcode.com/problems/move-zeroes/
 *
 * Description:
 * Given an integer array nums, move all 0's to the end of it while maintaining
 * the relative order of the non-zero elements.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -2^31 <= nums[i] <= 2^31 - 1
 *
 * Follow-up:
 * - Could you minimize the total number of operations done?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class MoveZeroes {

    // Main solution - Two pointers
    public void moveZeroes(int[] nums) {
        int left = 0;

        // Move all non-zero elements to the front
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] != 0) {
                nums[left] = nums[right];
                left++;
            }
        }

        // Fill remaining positions with zeros
        while (left < nums.length) {
            nums[left] = 0;
            left++;
        }
    }

    // Alternative solution - Swap approach
    public void moveZeroesSwap(int[] nums) {
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] != 0) {
                // Swap only if positions are different
                if (left != right) {
                    int temp = nums[left];
                    nums[left] = nums[right];
                    nums[right] = temp;
                }
                left++;
            }
        }
    }

    // Follow-up solution - Minimize operations
    public void moveZeroesOptimized(int[] nums) {
        int insertPos = 0;

        // First pass: move all non-zero elements
        for (int num : nums) {
            if (num != 0) {
                nums[insertPos++] = num;
            }
        }

        // Second pass: fill zeros (only if needed)
        while (insertPos < nums.length) {
            nums[insertPos++] = 0;
        }
    }

    public static void main(String[] args) {
        MoveZeroes solution = new MoveZeroes();

        // Test Case 1: Normal case
        int[] nums1 = { 0, 1, 0, 3, 12 };
        solution.moveZeroes(nums1);
        System.out.println(Arrays.toString(nums1)); // Expected: [1,3,12,0,0]

        // Test Case 2: Edge case - all zeros
        int[] nums2 = { 0, 0, 0 };
        solution.moveZeroes(nums2);
        System.out.println(Arrays.toString(nums2)); // Expected: [0,0,0]

        // Test Case 3: Corner case - no zeros
        int[] nums3 = { 1, 2, 3, 4, 5 };
        solution.moveZeroes(nums3);
        System.out.println(Arrays.toString(nums3)); // Expected: [1,2,3,4,5]

        // Test Case 4: Single element - zero
        int[] nums4 = { 0 };
        solution.moveZeroes(nums4);
        System.out.println(Arrays.toString(nums4)); // Expected: [0]

        // Test Case 5: Single element - non-zero
        int[] nums5 = { 1 };
        solution.moveZeroes(nums5);
        System.out.println(Arrays.toString(nums5)); // Expected: [1]

        // Test Case 6: Special case - zeros at end
        int[] nums6 = { 1, 2, 3, 0, 0 };
        solution.moveZeroes(nums6);
        System.out.println(Arrays.toString(nums6)); // Expected: [1,2,3,0,0]

        // Test Case 7: Zeros at beginning
        int[] nums7 = { 0, 0, 1, 2, 3 };
        solution.moveZeroes(nums7);
        System.out.println(Arrays.toString(nums7)); // Expected: [1,2,3,0,0]

        // Test Case 8: Alternating pattern
        int[] nums8 = { 1, 0, 2, 0, 3, 0 };
        solution.moveZeroes(nums8);
        System.out.println(Arrays.toString(nums8)); // Expected: [1,2,3,0,0,0]

        // Test Case 9: Negative numbers
        int[] nums9 = { -1, 0, -2, 0, 3 };
        solution.moveZeroes(nums9);
        System.out.println(Arrays.toString(nums9)); // Expected: [-1,-2,3,0,0]

        // Test Case 10: Two elements
        int[] nums10 = { 0, 1 };
        solution.moveZeroes(nums10);
        System.out.println(Arrays.toString(nums10)); // Expected: [1,0]
    }
}
