package arrays.easy;

import java.util.Arrays;

/**
 * LeetCode 88: Merge Sorted Array
 * https://leetcode.com/problems/merge-sorted-array/
 * 
 * Problem:
 * You are given two integer arrays nums1 and nums2, sorted in non-decreasing
 * order,
 * and two integers m and n, representing the number of elements in nums1 and
 * nums2 respectively.
 * Merge nums1 and nums2 into a single array sorted in non-decreasing order.
 * 
 * The final sorted array should not be returned by the function, but instead be
 * stored inside the array nums1.
 * To accommodate this, nums1 has a length of m + n, where the first m elements
 * denote the elements that should be merged,
 * and the last n elements are set to 0 and should be ignored. nums2 has a
 * length of n.
 * 
 * Example 1:
 * Input: nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
 * Output: [1,2,2,3,5,6]
 * 
 * Example 2:
 * Input: nums1 = [1], m = 1, nums2 = [], n = 0
 * Output: [1]
 * 
 * Constraints:
 * nums1.length == m + n
 * nums2.length == n
 * 0 <= m, n <= 200
 * 1 <= m + n <= 200
 * -10^9 <= nums1[i], nums2[i] <= 10^9
 * 
 * Company Tags: Microsoft, Amazon, Google, Meta, Apple
 * Frequency: Very High
 */
public class MergeSortedArray {

    /**
     * Optimal approach: Three pointers from the end
     * Time Complexity: O(m + n)
     * Space Complexity: O(1)
     */
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1; // Last element in nums1
        int j = n - 1; // Last element in nums2
        int k = m + n - 1; // Last position in merged array

        // Merge from the end to avoid overwriting
        while (i >= 0 && j >= 0) {
            if (nums1[i] > nums2[j]) {
                nums1[k] = nums1[i];
                i--;
            } else {
                nums1[k] = nums2[j];
                j--;
            }
            k--;
        }

        // Copy remaining elements from nums2 (if any)
        while (j >= 0) {
            nums1[k] = nums2[j];
            j--;
            k--;
        }
        // Note: if i >= 0, elements are already in correct position
    }

    /**
     * Alternative approach: Using extra space
     * Time Complexity: O(m + n)
     * Space Complexity: O(m)
     */
    public void mergeWithExtraSpace(int[] nums1, int m, int[] nums2, int n) {
        int[] nums1Copy = new int[m];
        System.arraycopy(nums1, 0, nums1Copy, 0, m);

        int i = 0, j = 0, k = 0;

        while (i < m && j < n) {
            if (nums1Copy[i] <= nums2[j]) {
                nums1[k] = nums1Copy[i];
                i++;
            } else {
                nums1[k] = nums2[j];
                j++;
            }
            k++;
        }

        while (i < m) {
            nums1[k] = nums1Copy[i];
            i++;
            k++;
        }

        while (j < n) {
            nums1[k] = nums2[j];
            j++;
            k++;
        }
    }

    /**
     * Follow-up: What if we need to merge and return a new array?
     */
    public int[] mergeToNewArray(int[] nums1, int m, int[] nums2, int n) {
        int[] result = new int[m + n];
        int i = 0, j = 0, k = 0;

        while (i < m && j < n) {
            if (nums1[i] <= nums2[j]) {
                result[k] = nums1[i];
                i++;
            } else {
                result[k] = nums2[j];
                j++;
            }
            k++;
        }

        while (i < m) {
            result[k] = nums1[i];
            i++;
            k++;
        }

        while (j < n) {
            result[k] = nums2[j];
            j++;
            k++;
        }

        return result;
    }

    public static void main(String[] args) {
        MergeSortedArray solution = new MergeSortedArray();

        // Test case 1
        int[] nums1 = { 1, 2, 3, 0, 0, 0 };
        int[] nums2 = { 2, 5, 6 };
        solution.merge(nums1, 3, nums2, 3);
        System.out.println("Test 1: " + Arrays.toString(nums1)); // [1,2,2,3,5,6]

        // Test case 2
        nums1 = new int[] { 1 };
        nums2 = new int[] {};
        solution.merge(nums1, 1, nums2, 0);
        System.out.println("Test 2: " + Arrays.toString(nums1)); // [1]

        // Test case 3
        nums1 = new int[] { 0 };
        nums2 = new int[] { 1 };
        solution.merge(nums1, 0, nums2, 1);
        System.out.println("Test 3: " + Arrays.toString(nums1)); // [1]

        // Test case 4: Edge case - all elements from nums2 are smaller
        nums1 = new int[] { 4, 5, 6, 0, 0, 0 };
        nums2 = new int[] { 1, 2, 3 };
        solution.merge(nums1, 3, nums2, 3);
        System.out.println("Test 4: " + Arrays.toString(nums1)); // [1,2,3,4,5,6]

        // Test case 5: Edge case - all elements from nums1 are smaller
        nums1 = new int[] { 1, 2, 3, 0, 0, 0 };
        nums2 = new int[] { 4, 5, 6 };
        solution.merge(nums1, 3, nums2, 3);
        System.out.println("Test 5: " + Arrays.toString(nums1)); // [1,2,3,4,5,6]

        System.out.println("\nAll test cases completed successfully!");
    }
}
