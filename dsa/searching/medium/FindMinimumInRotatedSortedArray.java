package searching.medium;

/**
 * LeetCode 153: Find Minimum in Rotated Sorted Array
 * https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/
 *
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Find the minimum element in a rotated sorted array.
 *
 * Constraints:
 * - 1 <= nums.length <= 5000
 * - -5000 <= nums[i] <= 5000
 *
 * Follow-ups:
 * 1. Can you find the index of the minimum?
 * 2. Can you handle duplicates?
 * 3. Can you find the rotation index?
 */
public class FindMinimumInRotatedSortedArray {
    public int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right])
                left = mid + 1;
            else
                right = mid;
        }
        return nums[left];
    }

    // Follow-up 1: Find index of minimum
    public int findMinIndex(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right])
                left = mid + 1;
            else
                right = mid;
        }
        return left;
    }

    // Follow-up 2: Find rotation index
    public int findRotationIndex(int[] nums) {
        return findMinIndex(nums);
    }

    public static void main(String[] args) {
        FindMinimumInRotatedSortedArray solution = new FindMinimumInRotatedSortedArray();
        // Basic case
        int[] nums1 = { 3, 4, 5, 1, 2 };
        System.out.println("Basic: " + solution.findMin(nums1)); // 1
        // Edge: No rotation
        int[] nums2 = { 1, 2, 3, 4, 5 };
        System.out.println("No rotation: " + solution.findMin(nums2)); // 1
        // Edge: Single element
        int[] nums3 = { 10 };
        System.out.println("Single element: " + solution.findMin(nums3)); // 10
        // Edge: Two elements
        int[] nums4 = { 2, 1 };
        System.out.println("Two elements: " + solution.findMin(nums4)); // 1
        // Follow-up 1: Index of minimum
        System.out.println("Index of min: " + solution.findMinIndex(nums1)); // 3
        // Follow-up 2: Rotation index
        System.out.println("Rotation index: " + solution.findRotationIndex(nums1)); // 3
    }
}
