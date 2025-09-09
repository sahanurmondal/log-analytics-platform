package arrays.medium;

/**
 * LeetCode 80: Remove Duplicates from Sorted Array II
 * https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/
 * 
 * Description:
 * Given an integer array nums sorted in non-decreasing order, remove some
 * duplicates in-place such that each unique element appears at most twice.
 *
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - -10^4 <= nums[i] <= 10^4
 * - nums is sorted in non-decreasing order
 *
 * Follow-up:
 * - Can you solve it in O(1) extra space?
 * - Can you generalize to at most k duplicates?
 * - Can you make it work for unsorted arrays?
 */
public class RemoveDuplicatesFromSortedArrayII {
    public int removeDuplicates(int[] nums) {
        if (nums.length <= 2)
            return nums.length;

        int writeIndex = 2;

        for (int i = 2; i < nums.length; i++) {
            if (nums[i] != nums[writeIndex - 2]) {
                nums[writeIndex] = nums[i];
                writeIndex++;
            }
        }

        return writeIndex;
    }

    // Alternative solution - General approach for at most k duplicates
    public int removeDuplicatesK(int[] nums, int k) {
        if (nums.length <= k)
            return nums.length;

        int writeIndex = k;

        for (int i = k; i < nums.length; i++) {
            if (nums[i] != nums[writeIndex - k]) {
                nums[writeIndex] = nums[i];
                writeIndex++;
            }
        }

        return writeIndex;
    }

    public static void main(String[] args) {
        RemoveDuplicatesFromSortedArrayII solution = new RemoveDuplicatesFromSortedArrayII();
        // Edge Case 1: Normal case
        int[] arr1 = { 1, 1, 1, 2, 2, 3 };
        System.out.println(solution.removeDuplicates(arr1)); // 5
        // Edge Case 2: No duplicates
        int[] arr2 = { 1, 2, 3, 4, 5 };
        System.out.println(solution.removeDuplicates(arr2)); // 5
        // Edge Case 3: All same
        int[] arr3 = { 2, 2, 2, 2, 2 };
        System.out.println(solution.removeDuplicates(arr3)); // 2
        // Edge Case 4: Duplicates at end
        int[] arr4 = { 1, 2, 2, 2, 2 };
        System.out.println(solution.removeDuplicates(arr4)); // 3
        // Edge Case 5: Single element
        int[] arr5 = { 7 };
        System.out.println(solution.removeDuplicates(arr5)); // 1
        // Edge Case 6: Two elements, same
        int[] arr6 = { 8, 8 };
        System.out.println(solution.removeDuplicates(arr6)); // 2
        // Edge Case 7: Two elements, different
        int[] arr7 = { 8, 9 };
        System.out.println(solution.removeDuplicates(arr7)); // 2
        // Edge Case 8: Large input
        int[] arr8 = new int[30000];
        for (int i = 0; i < 30000; i++)
            arr8[i] = i / 3;
        System.out.println(solution.removeDuplicates(arr8)); // 20000
    }
}