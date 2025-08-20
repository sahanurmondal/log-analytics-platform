package arrays.easy;

import java.util.*;

/**
 * LeetCode 26: Remove Duplicates from Sorted Array
 * https://leetcode.com/problems/remove-duplicates-from-sorted-array/
 *
 * Description:
 * Given an integer array nums sorted in non-decreasing order, remove the
 * duplicates in-place
 * such that each unique element appears only once. The relative order of the
 * elements should be kept the same.
 *
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - -100 <= nums[i] <= 100
 * - nums is sorted in non-decreasing order
 *
 * Follow-up:
 * - Can you solve it in O(1) extra space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class RemoveDuplicatesFromSortedArray {

    // Main solution - Two pointers
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0)
            return 0;

        int writeIndex = 1;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                nums[writeIndex] = nums[i];
                writeIndex++;
            }
        }

        return writeIndex;
    }

    // Alternative solution - Slow/Fast pointers
    public int removeDuplicatesSlowFast(int[] nums) {
        int slow = 0;

        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];
            }
        }

        return slow + 1;
    }

    public static void main(String[] args) {
        RemoveDuplicatesFromSortedArray solution = new RemoveDuplicatesFromSortedArray();

        // Test Case 1: Normal case
        int[] nums1 = { 1, 1, 2 };
        int len1 = solution.removeDuplicates(nums1);
        System.out.println("Length: " + len1 + ", Array: " + Arrays.toString(Arrays.copyOf(nums1, len1))); // Expected:
                                                                                                           // Length: 2,
                                                                                                           // Array:
                                                                                                           // [1,2]

        // Test Case 2: Edge case - multiple duplicates
        int[] nums2 = { 0, 0, 1, 1, 1, 2, 2, 3, 3, 4 };
        int len2 = solution.removeDuplicates(nums2);
        System.out.println("Length: " + len2 + ", Array: " + Arrays.toString(Arrays.copyOf(nums2, len2))); // Expected:
                                                                                                           // Length: 5,
                                                                                                           // Array:
                                                                                                           // [0,1,2,3,4]

        // Test Case 3: Corner case - single element
        int[] nums3 = { 1 };
        int len3 = solution.removeDuplicates(nums3);
        System.out.println("Length: " + len3 + ", Array: " + Arrays.toString(Arrays.copyOf(nums3, len3))); // Expected:
                                                                                                           // Length: 1,
                                                                                                           // Array: [1]

        // Test Case 4: All same elements
        int[] nums4 = { 2, 2, 2, 2 };
        int len4 = solution.removeDuplicates(nums4);
        System.out.println("Length: " + len4 + ", Array: " + Arrays.toString(Arrays.copyOf(nums4, len4))); // Expected:
                                                                                                           // Length: 1,
                                                                                                           // Array: [2]

        // Test Case 5: No duplicates
        int[] nums5 = { 1, 2, 3, 4, 5 };
        int len5 = solution.removeDuplicates(nums5);
        System.out.println("Length: " + len5 + ", Array: " + Arrays.toString(Arrays.copyOf(nums5, len5))); // Expected:
                                                                                                           // Length: 5,
                                                                                                           // Array:
                                                                                                           // [1,2,3,4,5]

        // Test Case 6: Negative numbers
        int[] nums6 = { -3, -1, -1, 0, 0, 0, 1, 1 };
        int len6 = solution.removeDuplicates(nums6);
        System.out.println("Length: " + len6 + ", Array: " + Arrays.toString(Arrays.copyOf(nums6, len6))); // Expected:
                                                                                                           // Length: 4,
                                                                                                           // Array:
                                                                                                           // [-3,-1,0,1]

        // Test Case 7: Two elements - same
        int[] nums7 = { 1, 1 };
        int len7 = solution.removeDuplicates(nums7);
        System.out.println("Length: " + len7 + ", Array: " + Arrays.toString(Arrays.copyOf(nums7, len7))); // Expected:
                                                                                                           // Length: 1,
                                                                                                           // Array: [1]

        // Test Case 8: Two elements - different
        int[] nums8 = { 1, 2 };
        int len8 = solution.removeDuplicates(nums8);
        System.out.println("Length: " + len8 + ", Array: " + Arrays.toString(Arrays.copyOf(nums8, len8))); // Expected:
                                                                                                           // Length: 2,
                                                                                                           // Array:
                                                                                                           // [1,2]

        // Test Case 9: Large duplicates at start
        int[] nums9 = { 1, 1, 1, 1, 2, 3 };
        int len9 = solution.removeDuplicates(nums9);
        System.out.println("Length: " + len9 + ", Array: " + Arrays.toString(Arrays.copyOf(nums9, len9))); // Expected:
                                                                                                           // Length: 3,
                                                                                                           // Array:
                                                                                                           // [1,2,3]

        // Test Case 10: Boundary values
        int[] nums10 = { -100, -50, 0, 50, 100 };
        int len10 = solution.removeDuplicates(nums10);
        System.out.println("Length: " + len10 + ", Array: " + Arrays.toString(Arrays.copyOf(nums10, len10))); // Expected:
                                                                                                              // Length:
                                                                                                              // 5,
                                                                                                              // Array:
                                                                                                              // [-100,-50,0,50,100]
    }
}
