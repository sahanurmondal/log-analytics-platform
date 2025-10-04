package searching.medium;

/**
 * LeetCode 33: Search in Rotated Sorted Array
 * https://leetcode.com/problems/search-in-rotated-sorted-array/
 *
 * Description:
 * There is an integer array nums sorted in ascending order (with distinct
 * values).
 * Given the array nums after the rotation and an integer target, return the
 * index of target if it is in nums, or -1 if it is not in nums.
 *
 * Constraints:
 * - 1 <= nums.length <= 5000
 * - -10^4 <= nums[i] <= 10^4
 * - All values of nums are unique
 * - nums is guaranteed to be rotated at some pivot
 * - -10^4 <= target <= 10^4
 *
 * ASCII Art:
 * Original: [1,2,3,4,5,6,7]
 * Rotated: [4,5,6,7,1,2,3] (rotated at index 4)
 * ↑pivot
 *
 * Follow-ups:
 * 1. Can you find the rotation index?
 * 2. Can you search for the closest value?
 * 3. Can you handle duplicates?
 */
public class SearchInRotatedSortedArray {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {
                if (target > nums[mid] && target <= nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return -1;
    }

    // Follow-up 1: Find rotation index
    public int findRotationIndex(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    // Follow-up 2: Search for closest value
    public int searchClosest(int[] nums, int target) {
        int left = 0, right = nums.length - 1, closest = -1, minDiff = Integer.MAX_VALUE;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int diff = Math.abs(nums[mid] - target);
            if (diff < minDiff) {
                minDiff = diff;
                closest = mid;
            }
            if (nums[mid] == target) break;
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {
                if (target > nums[mid] && target <= nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return closest;
    }

    // Follow-up 3: Handle duplicates (LeetCode 81)
    public int searchWithDuplicates(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
                left++; right--;
            } else if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {
                if (target > nums[mid] && target <= nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SearchInRotatedSortedArray solution = new SearchInRotatedSortedArray();
        int[] nums1 = {4,5,6,7,0,1,2};
        System.out.println("Basic: " + solution.search(nums1, 0)); // 4
        System.out.println("Not found: " + solution.search(nums1, 3)); // -1
        int[] nums2 = {1};
        System.out.println("Single element: " + solution.search(nums2, 1)); // 0
        System.out.println("Rotation index: " + solution.findRotationIndex(nums1)); // 4
        System.out.println("Closest to 3: " + solution.searchClosest(nums1, 3)); // 6
        int[] nums3 = {2,5,6,0,0,1,2};
        System.out.println("With duplicates: " + solution.searchWithDuplicates(nums3, 0)); // 3 or 4
    }
}
