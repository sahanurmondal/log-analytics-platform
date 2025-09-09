package arrays.medium;

/**
 * LeetCode 33: Search in Rotated Sorted Array
 * https://leetcode.com/problems/search-in-rotated-sorted-array/
 *
 * Description:
 * There is an integer array nums sorted in ascending order (with distinct
 * values).
 * Prior to being passed to your function, nums is possibly rotated at an
 * unknown pivot index.
 * Given the array nums after the possible rotation and an integer target,
 * return the index of target if it is in nums, or -1 if it is not in nums.
 *
 * Constraints:
 * - 1 <= nums.length <= 5000
 * - -10^4 <= nums[i] <= 10^4
 * - All values of nums are unique
 * - nums is an ascending array that is possibly rotated
 * - -10^4 <= target <= 10^4
 *
 * Follow-up:
 * - Can you achieve this in O(log n) time complexity?
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 */
public class SearchRotatedArray {

    // Main solution - Binary search
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            }

            // Check which half is sorted
            if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return -1;
    }

    // Alternative solution - Find pivot first
    public int searchWithPivot(int[] nums, int target) {
        int pivot = findPivot(nums);

        // Array is not rotated
        if (pivot == 0) {
            return binarySearch(nums, 0, nums.length - 1, target);
        }

        // Decide which half to search
        if (target >= nums[0]) {
            return binarySearch(nums, 0, pivot - 1, target);
        } else {
            return binarySearch(nums, pivot, nums.length - 1, target);
        }
    }

    private int findPivot(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private int binarySearch(int[] nums, int left, int right, int target) {
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

    public static void main(String[] args) {
        SearchRotatedArray solution = new SearchRotatedArray();

        // Test Case 1: Normal case
        System.out.println(solution.search(new int[] { 4, 5, 6, 7, 0, 1, 2 }, 0)); // Expected: 4

        // Test Case 2: Edge case - target not found
        System.out.println(solution.search(new int[] { 4, 5, 6, 7, 0, 1, 2 }, 3)); // Expected: -1

        // Test Case 3: Corner case - single element
        System.out.println(solution.search(new int[] { 1 }, 0)); // Expected: -1

        // Test Case 4: No rotation
        System.out.println(solution.search(new int[] { 1, 2, 3, 4, 5 }, 3)); // Expected: 2

        // Test Case 5: Target at pivot
        System.out.println(solution.search(new int[] { 4, 5, 6, 7, 0, 1, 2 }, 4)); // Expected: 0

        // Test Case 6: Special case - two elements
        System.out.println(solution.search(new int[] { 1, 3 }, 3)); // Expected: 1

        // Test Case 7: Target at end
        System.out.println(solution.search(new int[] { 4, 5, 6, 7, 0, 1, 2 }, 2)); // Expected: 6

        // Test Case 8: Rotated at index 1
        System.out.println(solution.search(new int[] { 3, 1 }, 1)); // Expected: 1

        // Test Case 9: Large rotation
        System.out.println(solution.search(new int[] { 8, 9, 2, 3, 4 }, 9)); // Expected: 1

        // Test Case 10: Target at beginning
        System.out.println(solution.search(new int[] { 6, 7, 0, 1, 2, 4, 5 }, 6)); // Expected: 0
    }
}
