package binarysearch.medium;

/**
 * LeetCode 33: Search in Rotated Sorted Array
 * URL: <a href="https://leetcode.com/problems/search-in-rotated-sorted-array/">https://leetcode.com/problems/search-in-rotated-sorted-array/</a>
 * Company Tags: Amazon, Facebook, Microsoft, Google, Bloomberg, Adobe
 * Frequency: Very High
 *
 * Problem:
 * There is an integer array nums sorted in ascending order (with distinct values).
 * Prior to being passed to your function, nums is possibly rotated at an unknown pivot index k (1 <= k < nums.length)
 * such that the resulting array is [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]] (0-indexed).
 * For example, [0,1,2,4,5,6,7] might be rotated at pivot index 3 and become [4,5,6,7,0,1,2].
 * Given the array nums after the possible rotation and an integer target, return the index of target if it is in nums, or -1 if it is not in nums.
 * You must write an algorithm with O(log n) runtime complexity.
 *
 * Example 1:
 * Input: nums = [4,5,6,7,0,1,2], target = 0
 * Output: 4
 *
 * Example 2:
 * Input: nums = [4,5,6,7,0,1,2], target = 3
 * Output: -1
 *
 * Example 3:
 * Input: nums = [1], target = 0
 * Output: -1
 *
 * Constraints:
 * 1 <= nums.length <= 5000
 * -10^4 <= nums[i] <= 10^4
 * All values of nums are unique.
 * nums is an ascending array that is possibly rotated.
 * -10^4 <= target <= 10^4
 *
 * Follow-up:
 * What if duplicates are allowed in the array?
 * This corresponds to LeetCode 81: Search in Rotated Sorted Array II.
 * How would the presence of duplicates affect the time complexity?
 */
public class SearchInRotatedSortedArray {

    /**
     * Solution Approach: Modified Binary Search
     *
     * Algorithm:
     * The key idea is that in a rotated sorted array, when you split it at the midpoint, at least one of the two halves must be sorted.
     * 1. Initialize `left = 0` and `right = n-1`.
     * 2. Loop while `left <= right`:
     *    a. Calculate `mid = left + (right - left) / 2`.
     *    b. If `nums[mid] == target`, return `mid`.
     *    c. Check if the left half (`nums[left]` to `nums[mid]`) is sorted. This is true if `nums[left] <= nums[mid]`.
     *       i. If it is sorted, check if the target lies within this sorted range (`target >= nums[left]` and `target < nums[mid]`).
     *          - If yes, the target is in the left half. Set `right = mid - 1`.
     *          - If no, the target must be in the right half. Set `left = mid + 1`.
     *    d. If the left half is not sorted, it means the right half (`nums[mid]` to `nums[right]`) must be sorted.
     *       i. Check if the target lies within this sorted right half (`target > nums[mid]` and `target <= nums[right]`).
     *          - If yes, the target is in the right half. Set `left = mid + 1`.
     *          - If no, the target must be in the left half. Set `right = mid - 1`.
     * 3. If the loop ends, the target was not found. Return -1.
     *
     * Time Complexity: O(log n), as we discard half of the search space in each iteration.
     * Space Complexity: O(1), using only a few variables.
     */
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            }

            // Check if the left half is sorted
            if (nums[left] <= nums[mid]) {
                // Check if target is in the sorted left half
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            // Otherwise, the right half must be sorted
            else {
                // Check if target is in the sorted right half
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return -1;
    }

    /**
     * Follow-up Solution: Search in Rotated Sorted Array with Duplicates
     *
     * Algorithm:
     * The logic is similar to the original problem, but we have to handle the case where `nums[left] == nums[mid] == nums[right]`.
     * In this specific case, we cannot determine which half is sorted. For example, in `[3, 1, 2, 3, 3, 3, 3]`, `mid` is 3, `nums[left]=3`, `nums[mid]=3`, `nums[right]=3`.
     * We don't know if the pivot is in the left or right half.
     * The safe bet is to shrink the search space from both ends by doing `left++` and `right--`.
     *
     * The rest of the logic remains the same:
     * 1. If `nums[left] <= nums[mid]`, the left half is sorted.
     * 2. Otherwise, the right half is sorted.
     *
     * Time Complexity: O(log n) on average, but O(n) in the worst case. The worst case occurs when all elements are the same (e.g., `[1,1,1,1,1]`) and the target is different. In each step, we might only be able to shrink the search space by one element from each side.
     * Space Complexity: O(1).
     */
    public boolean searchWithDuplicates(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return true;
            }

            // The case that causes ambiguity
            if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
                left++;
                right--;
                continue;
            }

            // Check if the left half is sorted
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            // Otherwise, the right half must be sorted
            else {
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SearchInRotatedSortedArray solution = new SearchInRotatedSortedArray();

        // Example 1
        int[] nums1 = {4, 5, 6, 7, 0, 1, 2};
        int target1 = 0;
        System.out.println("Index of " + target1 + ": " + solution.search(nums1, target1)); // Expected: 4

        // Example 2
        int target2 = 3;
        System.out.println("Index of " + target2 + ": " + solution.search(nums1, target2)); // Expected: -1

        // Edge Cases
        // 1. Target is the pivot element
        System.out.println("Target is pivot: " + solution.search(new int[]{3, 1}, 1)); // Expected: 1
        // 2. Target is at the start
        System.out.println("Target at start: " + solution.search(nums1, 4)); // Expected: 0
        // 3. Target is at the end
        System.out.println("Target at end: " + solution.search(nums1, 2)); // Expected: 6
        // 4. Array not rotated
        System.out.println("Not rotated: " + solution.search(new int[]{1, 2, 3, 4, 5}, 4)); // Expected: 3
        // 5. Single element array, target present
        System.out.println("Single element, present: " + solution.search(new int[]{1}, 1)); // Expected: 0
        // 6. Single element array, target not present
        System.out.println("Single element, not present: " + solution.search(new int[]{1}, 0)); // Expected: -1

        System.out.println("\n--- Follow-up: Search with Duplicates ---");
        int[] nums2 = {2, 5, 6, 0, 0, 1, 2};
        System.out.println("Search for 0 in " + java.util.Arrays.toString(nums2) + ": " + solution.searchWithDuplicates(nums2, 0)); // Expected: true
        System.out.println("Search for 3 in " + java.util.Arrays.toString(nums2) + ": " + solution.searchWithDuplicates(nums2, 3)); // Expected: false
        int[] nums3 = {1, 0, 1, 1, 1};
        System.out.println("Search for 0 in " + java.util.Arrays.toString(nums3) + ": " + solution.searchWithDuplicates(nums3, 0)); // Expected: true
    }
}
