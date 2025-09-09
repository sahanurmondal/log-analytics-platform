package searching.medium;

/**
 * LeetCode 154: Find Minimum in Rotated Sorted Array II
 * https://leetcode.com/problems/find-minimum-in-rotated-sorted-array-ii/
 *
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Find the minimum element in a rotated sorted array that may
 * contain duplicates.
 *
 * Constraints:
 * - 1 <= nums.length <= 5000
 * - -5000 <= nums[i] <= 5000
 *
 * Follow-ups:
 * 1. Can you find the index of the minimum?
 * 2. Can you find the rotation index?
 * 3. Can you handle all duplicates?
 */
public class FindMinimumInRotatedSortedArrayII {
    public int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right])
                left = mid + 1;
            else if (nums[mid] < nums[right])
                right = mid;
            else
                right--;
        }
        return nums[left];
    }

    // Follow-up 1: Index of minimum
    public int findMinIndex(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right])
                left = mid + 1;
            else if (nums[mid] < nums[right])
                right = mid;
            else
                right--;
        }
        return left;
    }

    // Follow-up 2: Rotation index
    public int findRotationIndex(int[] nums) {
        return findMinIndex(nums);
    }

    // Follow-up 3: All duplicates (return first index)
    public int findFirstIndex(int[] nums) {
        int min = findMin(nums);
        for (int i = 0; i < nums.length; i++)
            if (nums[i] == min)
                return i;
        return -1;
    }

    public static void main(String[] args) {
        FindMinimumInRotatedSortedArrayII solution = new FindMinimumInRotatedSortedArrayII();
        // Basic case
        int[] nums1 = { 2, 2, 2, 0, 1 };
        System.out.println("Basic: " + solution.findMin(nums1)); // 0
        // Edge: All duplicates
        int[] nums2 = { 1, 1, 1, 1, 1 };
        System.out.println("All duplicates: " + solution.findMin(nums2)); // 1
        // Edge: No rotation
        int[] nums3 = { 1, 2, 3, 4, 5 };
        System.out.println("No rotation: " + solution.findMin(nums3)); // 1
        // Edge: Single element
        int[] nums4 = { 10 };
        System.out.println("Single element: " + solution.findMin(nums4)); // 10
        // Follow-up 1: Index of minimum
        System.out.println("Index of min: " + solution.findMinIndex(nums1)); // 3
        // Follow-up 2: Rotation index
        System.out.println("Rotation index: " + solution.findRotationIndex(nums1)); // 3
        // Follow-up 3: First index of min
        System.out.println("First index of min: " + solution.findFirstIndex(nums1)); // 3
    }
}
