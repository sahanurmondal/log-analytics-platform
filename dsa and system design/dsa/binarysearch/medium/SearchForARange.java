package binarysearch.medium;

/**
 * LeetCode 34: Find First and Last Position of Element in Sorted Array
 * https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
 * 
 * Problem:
 * Given an array of integers nums sorted in non-decreasing order, find the
 * starting and ending position of a given target value.
 * If target is not found in the array, return [-1, -1].
 * You must write an algorithm with O(log n) runtime complexity.
 * 
 * Example 1:
 * Input: nums = [5,7,7,8,8,10], target = 8
 * Output: [3,4]
 * 
 * Example 2:
 * Input: nums = [5,7,7,8,8,10], target = 6
 * Output: [-1,-1]
 * 
 * Example 3:
 * Input: nums = [], target = 0
 * Output: [-1,-1]
 * 
 * Constraints:
 * 0 <= nums.length <= 10^5
 * -10^9 <= nums[i] <= 10^9
 * nums is a non-decreasing array
 * -10^9 <= target <= 10^9
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple
 * Frequency: Very High
 */
public class SearchForARange {

    /**
     * Main solution: Two binary searches
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public int[] searchRange(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        int firstPos = findFirst(nums, target);
        if (firstPos == -1) {
            return new int[] { -1, -1 };
        }

        int lastPos = findLast(nums, target);
        return new int[] { firstPos, lastPos };
    }

    /**
     * Find the first occurrence of target
     */
    private int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int firstPos = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                firstPos = mid;
                right = mid - 1; // Continue searching in left half
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return firstPos;
    }

    /**
     * Find the last occurrence of target
     */
    private int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int lastPos = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                lastPos = mid;
                left = mid + 1; // Continue searching in right half
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return lastPos;
    }

    /**
     * Alternative approach: Single binary search with expansion
     * Time Complexity: O(log n + k) where k is the number of occurrences
     * Space Complexity: O(1)
     */
    public int[] searchRangeAlternative(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        int pos = binarySearch(nums, target);
        if (pos == -1) {
            return new int[] { -1, -1 };
        }

        // Expand to find first and last positions
        int left = pos, right = pos;
        while (left > 0 && nums[left - 1] == target) {
            left--;
        }
        while (right < nums.length - 1 && nums[right + 1] == target) {
            right++;
        }

        return new int[] { left, right };
    }

    private int binarySearch(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    /**
     * Follow-up: Using built-in binary search methods (for reference)
     */
    public int[] searchRangeWithBuiltIn(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        // Find insertion point for target
        int left = lowerBound(nums, target);
        if (left == nums.length || nums[left] != target) {
            return new int[] { -1, -1 };
        }

        // Find insertion point for target + 1
        int right = lowerBound(nums, target + 1) - 1;

        return new int[] { left, right };
    }

    private int lowerBound(int[] nums, int target) {
        int left = 0, right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    public static void main(String[] args) {
        SearchForARange solution = new SearchForARange();

        // Test case 1
        int[] nums1 = { 5, 7, 7, 8, 8, 10 };
        int[] result1 = solution.searchRange(nums1, 8);
        System.out.println("Test 1: [" + result1[0] + ", " + result1[1] + "]"); // [3, 4]

        // Test case 2
        int[] nums2 = { 5, 7, 7, 8, 8, 10 };
        int[] result2 = solution.searchRange(nums2, 6);
        System.out.println("Test 2: [" + result2[0] + ", " + result2[1] + "]"); // [-1, -1]

        // Test case 3
        int[] nums3 = {};
        int[] result3 = solution.searchRange(nums3, 0);
        System.out.println("Test 3: [" + result3[0] + ", " + result3[1] + "]"); // [-1, -1]

        // Test case 4: Single element
        int[] nums4 = { 1 };
        int[] result4 = solution.searchRange(nums4, 1);
        System.out.println("Test 4: [" + result4[0] + ", " + result4[1] + "]"); // [0, 0]

        // Test case 5: All elements are the same
        int[] nums5 = { 2, 2, 2, 2, 2 };
        int[] result5 = solution.searchRange(nums5, 2);
        System.out.println("Test 5: [" + result5[0] + ", " + result5[1] + "]"); // [0, 4]

        System.out.println("\nAll test cases completed successfully!");
    }
}
