package binarysearch.easy;

import java.util.Arrays;

/**
 * LeetCode 704: Binary Search
 * URL: <a href="https://leetcode.com/problems/binary-search/">https://leetcode.com/problems/binary-search/</a>
 * Company Tags: Adobe, Amazon, Apple, Facebook, Google, Microsoft
 * Frequency: High
 *
 * Problem:
 * Given an array of integers nums which is sorted in ascending order, and an integer target,
 * write a function to search target in nums. If target exists, then return its index.
 * Otherwise, return -1.
 * You must write an algorithm with O(log n) runtime complexity.
 *
 * Example 1:
 * Input: nums = [-1,0,3,5,9,12], target = 9
 * Output: 4
 * Explanation: 9 exists in nums and its index is 4
 *
 * Example 2:
 * Input: nums = [-1,0,3,5,9,12], target = 2
 * Output: -1
 * Explanation: 2 does not exist in nums so return -1
 *
 * Constraints:
 * 1 <= nums.length <= 10^4
 * -10^4 < nums[i], target < 10^4
 * All the integers in nums are unique.
 * nums is sorted in ascending order.
 *
 * Follow-up:
 * What if the array contains duplicates? Find the first and last position of a given target value.
 * If the target is not found in the array, return [-1, -1].
 * This corresponds to LeetCode 34: Find First and Last Position of Element in Sorted Array.
 */
public class BinarySearch {

    /**
     * Solution Approach: Iterative Binary Search
     *
     * Algorithm:
     * 1. Initialize two pointers, `left` at the start of the array (index 0) and `right` at the end (index n-1).
     * 2. While `left` is less than or equal to `right`:
     *    a. Calculate the middle index: `mid = left + (right - left) / 2`. This avoids potential overflow compared to `(left + right) / 2`.
     *    b. If `nums[mid]` is the target, we've found it, return `mid`.
     *    c. If `nums[mid]` is less than the target, it means the target must be in the right half of the current search space. So, we move our `left` pointer to `mid + 1`.
     *    d. If `nums[mid]` is greater than the target, the target must be in the left half. So, we move our `right` pointer to `mid - 1`.
     * 3. If the loop finishes without finding the target, it means the target is not in the array. Return -1.
     *
     * Time Complexity: O(log n), where n is the number of elements in the array. With each step, we reduce the search space by half.
     * Space Complexity: O(1), as we only use a few variables to store pointers, regardless of the input size.
     */
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

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
     * Follow-up Solution: Find First and Last Position of Element in Sorted Array
     *
     * Algorithm:
     * To find the range, we can perform two modified binary searches.
     * 1. One search to find the leftmost (first) occurrence of the target.
     * 2. Another search to find the rightmost (last) occurrence of the target.
     *
     * To find the first occurrence:
     * - Perform a binary search.
     * - If `nums[mid] == target`, we might have found the first one, but there could be more on the left.
     *   So, we store this index and continue searching in the left half (`right = mid - 1`).
     * - If `nums[mid] < target`, search right (`left = mid + 1`).
     * - If `nums[mid] > target`, search left (`right = mid - 1`).
     *
     * To find the last occurrence:
     * - Perform a binary search.
     * - If `nums[mid] == target`, we might have found the last one, but there could be more on the right.
     *   So, we store this index and continue searching in the right half (`left = mid + 1`).
     * - The rest of the logic is similar.
     *
     * Time Complexity: O(log n) for each binary search, so the total is O(log n).
     * Space Complexity: O(1).
     */
    public int[] searchRange(int[] nums, int target) {
        int[] result = new int[]{-1, -1};
        result[0] = findFirst(nums, target);
        result[1] = findLast(nums, target);
        return result;
    }

    private int findFirst(int[] nums, int target) {
        int index = -1;
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] >= target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
            if (nums[mid] == target) {
                index = mid;
            }
        }
        return index;
    }

    private int findLast(int[] nums, int target) {
        int index = -1;
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
            if (nums[mid] == target) {
                index = mid;
            }
        }
        return index;
    }


    public static void main(String[] args) {
        BinarySearch solution = new BinarySearch();

        // Example 1
        int[] nums1 = {-1, 0, 3, 5, 9, 12};
        int target1 = 9;
        System.out.println("Index of " + target1 + ": " + solution.search(nums1, target1)); // Expected: 4

        // Example 2
        int[] nums2 = {-1, 0, 3, 5, 9, 12};
        int target2 = 2;
        System.out.println("Index of " + target2 + ": " + solution.search(nums2, target2)); // Expected: -1

        // Edge Cases
        // 1. Target is the first element
        System.out.println("Target is first element: " + solution.search(nums1, -1)); // Expected: 0
        // 2. Target is the last element
        System.out.println("Target is last element: " + solution.search(nums1, 12)); // Expected: 5
        // 3. Array with one element, target present
        System.out.println("Single element, target present: " + solution.search(new int[]{5}, 5)); // Expected: 0
        // 4. Array with one element, target not present
        System.out.println("Single element, target not present: " + solution.search(new int[]{5}, 3)); // Expected: -1
        // 5. Target smaller than all elements
        System.out.println("Target smaller than all: " + solution.search(nums1, -5)); // Expected: -1
        // 6. Target larger than all elements
        System.out.println("Target larger than all: " + solution.search(nums1, 20)); // Expected: -1

        System.out.println("\n--- Follow-up: Search Range ---");
        int[] nums3 = {5, 7, 7, 8, 8, 10};
        int target3 = 8;
        System.out.println("Search range for " + target3 + ": " + Arrays.toString(solution.searchRange(nums3, target3))); // Expected: [3, 4]

        int target4 = 6;
        System.out.println("Search range for " + target4 + ": " + Arrays.toString(solution.searchRange(nums3, target4))); // Expected: [-1, -1]

        int[] nums4 = {2, 2};
        int target5 = 2;
        System.out.println("Search range for " + target5 + ": " + Arrays.toString(solution.searchRange(nums4, target5))); // Expected: [0, 1]
    }
}
