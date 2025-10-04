package searching.medium;

/**
 * LeetCode 34: Find First and Last Position of Element in Sorted Array
 * https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
 *
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given an array of integers nums sorted in ascending order, find
 * the starting and ending position of a given target value.
 *
 * Constraints:
 * - 0 <= nums.length <= 10^5
 * - -10^9 <= nums[i], target <= 10^9
 *
 * Follow-ups:
 * 1. Can you count the number of occurrences?
 * 2. Can you handle unsorted arrays?
 * 3. Can you find the closest position if not found?
 */
public class FindFirstAndLastPosition {
    public int[] searchRange(int[] nums, int target) {
        int first = findFirst(nums, target);
        int last = findLast(nums, target);
        return new int[] { first, last };
    }

    private int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1, res = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] >= target) {
                if (nums[mid] == target)
                    res = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return res;
    }

    private int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1, res = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) {
                if (nums[mid] == target)
                    res = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return res;
    }

    // Follow-up 1: Count occurrences
    public int countOccurrences(int[] nums, int target) {
        int[] range = searchRange(nums, target);
        return (range[0] == -1) ? 0 : range[1] - range[0] + 1;
    }

    // Follow-up 2: Unsorted array (linear scan)
    public int[] searchRangeUnsorted(int[] nums, int target) {
        int first = -1, last = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                if (first == -1)
                    first = i;
                last = i;
            }
        }
        return new int[] { first, last };
    }

    // Follow-up 3: Closest position if not found
    public int closestPosition(int[] nums, int target) {
        int left = 0, right = nums.length - 1, closest = -1, minDiff = Integer.MAX_VALUE;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int diff = Math.abs(nums[mid] - target);
            if (diff < minDiff) {
                minDiff = diff;
                closest = mid;
            }
            if (nums[mid] < target)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return closest;
    }

    public static void main(String[] args) {
        FindFirstAndLastPosition solution = new FindFirstAndLastPosition();
        // Basic case
        int[] nums1 = { 5, 7, 7, 8, 8, 10 };
        System.out.println("Basic: " + java.util.Arrays.toString(solution.searchRange(nums1, 8))); // [3,4]
        // Not found
        System.out.println("Not found: " + java.util.Arrays.toString(solution.searchRange(nums1, 6))); // [-1,-1]
        // Edge: Single element
        int[] nums2 = { 1 };
        System.out.println("Single element: " + java.util.Arrays.toString(solution.searchRange(nums2, 1))); // [0,0]
        // Edge: Empty array
        int[] nums3 = {};
        System.out.println("Empty array: " + java.util.Arrays.toString(solution.searchRange(nums3, 1))); // [-1,-1]
        // Follow-up 1: Count occurrences
        System.out.println("Count occurrences: " + solution.countOccurrences(nums1, 7)); // 2
        // Follow-up 2: Unsorted array
        int[] nums4 = { 8, 5, 8, 7, 8, 10, 8 };
        System.out.println("Unsorted: " + java.util.Arrays.toString(solution.searchRangeUnsorted(nums4, 8))); // [0,6]
        // Follow-up 3: Closest position
        System.out.println("Closest to 9: " + solution.closestPosition(nums1, 9)); // 4
    }
}
