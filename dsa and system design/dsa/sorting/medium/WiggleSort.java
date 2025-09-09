package sorting.medium;

import java.util.*;

/**
 * LeetCode 280: Wiggle Sort
 * URL: https://leetcode.com/problems/wiggle-sort/
 * Difficulty: Medium
 * Companies: Google, Facebook, Microsoft
 * Frequency: Medium
 */
public class WiggleSort {
    // One pass approach - O(n) time
    public void wiggleSort(int[] nums) {
        for (int i = 0; i < nums.length - 1; i++) {
            if ((i % 2 == 0 && nums[i] > nums[i + 1]) ||
                    (i % 2 == 1 && nums[i] < nums[i + 1])) {
                swap(nums, i, i + 1);
            }
        }
    }

    // Sorting approach - O(n log n) time
    public void wiggleSortWithSorting(int[] nums) {
        Arrays.sort(nums);

        for (int i = 1; i < nums.length - 1; i += 2) {
            swap(nums, i, i + 1);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        WiggleSort solution = new WiggleSort();

        int[] test1 = { 3, 5, 2, 1, 6, 4 };
        solution.wiggleSort(test1);
        System.out.println(Arrays.toString(test1)); // [3,5,1,6,2,4]

        int[] test2 = { 6, 6, 5, 6, 3, 8 };
        solution.wiggleSort(test2);
        System.out.println(Arrays.toString(test2)); // [6,6,5,8,3,6]

        int[] test3 = { 1, 2, 3, 4, 5 };
        solution.wiggleSort(test3);
        System.out.println(Arrays.toString(test3)); // [1,3,2,5,4]

        int[] test4 = { 1, 1, 1, 1, 1 };
        solution.wiggleSort(test4);
        System.out.println(Arrays.toString(test4)); // [1,1,1,1,1]

        int[] test5 = { 2, 1 };
        solution.wiggleSort(test5);
        System.out.println(Arrays.toString(test5)); // [1,2]
    }
}
