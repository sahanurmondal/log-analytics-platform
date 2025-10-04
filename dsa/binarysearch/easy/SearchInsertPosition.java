package binarysearch.easy;

/**
 * LeetCode 35: Search Insert Position
 * https://leetcode.com/problems/search-insert-position/
 *
 * Description:
 * Given a sorted array of distinct integers and a target value, return the
 * index if the target is found.
 * If not, return the index where it would be if it were inserted in order.
 * You must write an algorithm with O(log n) runtime complexity.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Bloomberg,
 * Adobe
 * Difficulty: Easy
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 * - nums contains distinct values sorted in ascending order
 * - -10^4 <= target <= 10^4
 *
 * Follow-ups:
 * - Can you solve this with no extra space?
 * - What if array has duplicates? Find leftmost position?
 * - How would you handle a very large sorted file?
 */
public class SearchInsertPosition {

    // Binary Search - O(log n) time, O(1) space
    public int searchInsert(int[] nums, int target) {
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

        // If not found, left will be the insertion position
        return left;
    }

    // Linear Search - O(n) time (for comparison)
    public int searchInsertLinear(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] >= target) {
                return i;
            }
        }
        return nums.length; // Insert at the end
    }

    // Binary Search with template pattern (alternative approach)
    public int searchInsertTemplate(int[] nums, int target) {
        int left = 0;
        int right = nums.length; // Note: right = nums.length, not nums.length - 1

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

    // Recursive Binary Search
    public int searchInsertRecursive(int[] nums, int target) {
        return searchInsertRecursiveHelper(nums, target, 0, nums.length - 1);
    }

    private int searchInsertRecursiveHelper(int[] nums, int target, int left, int right) {
        if (left > right) {
            return left;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            return mid;
        } else if (nums[mid] < target) {
            return searchInsertRecursiveHelper(nums, target, mid + 1, right);
        } else {
            return searchInsertRecursiveHelper(nums, target, left, mid - 1);
        }
    }

    public static void main(String[] args) {
        SearchInsertPosition solution = new SearchInsertPosition();

        // Test Case 1: Target found
        int[] nums1 = { 1, 3, 5, 6 };
        System.out.println(solution.searchInsert(nums1, 5)); // Expected: 2

        // Test Case 2: Insert at beginning
        System.out.println(solution.searchInsert(nums1, 0)); // Expected: 0

        // Test Case 3: Insert in middle
        System.out.println(solution.searchInsert(nums1, 4)); // Expected: 2

        // Test Case 4: Insert at end
        System.out.println(solution.searchInsert(nums1, 7)); // Expected: 4

        // Test Case 5: Single element - found
        int[] nums2 = { 1 };
        System.out.println(solution.searchInsert(nums2, 1)); // Expected: 0

        // Test Case 6: Single element - not found (insert before)
        System.out.println(solution.searchInsert(nums2, 0)); // Expected: 0

        // Test Case 7: Single element - not found (insert after)
        System.out.println(solution.searchInsert(nums2, 2)); // Expected: 1

        // Test Case 8: Empty array (edge case - won't occur per constraints)
        // int[] nums3 = {};
        // System.out.println(solution.searchInsert(nums3, 1)); // Expected: 0

        // Test recursive version
        System.out.println("Recursive: " + solution.searchInsertRecursive(nums1, 4)); // Expected: 2

        // Test template version
        System.out.println("Template: " + solution.searchInsertTemplate(nums1, 4)); // Expected: 2
    }
}
