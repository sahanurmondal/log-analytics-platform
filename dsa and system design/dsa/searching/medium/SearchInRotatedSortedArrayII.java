package searching.medium;

/**
 * LeetCode 81: Search in Rotated Sorted Array II
 * https://leetcode.com/problems/search-in-rotated-sorted-array-ii/
 *
 * Description:
 * There is an integer array nums sorted in non-decreasing order (not
 * necessarily with distinct values).
 * Given the array nums after the rotation and an integer target, return true if
 * target is in nums, or false if it is not in nums.
 *
 * Constraints:
 * - 1 <= nums.length <= 5000
 * - -10^4 <= nums[i] <= 10^4
 * - nums is guaranteed to be rotated at some pivot
 * - -10^4 <= target <= 10^4
 *
 * Follow-ups:
 * 1. Can you find the rotation index?
 * 2. Can you search for the closest value?
 * 3. Can you count occurrences?
 */
public class SearchInRotatedSortedArrayII {
    public boolean search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return true;
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
        return false;
    }

    // Follow-up 1: Rotation index
    public int findRotationIndex(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) left = mid + 1;
            else if (nums[mid] < nums[right]) right = mid;
            else right--;
        }
        return left;
    }

    // Follow-up 2: Closest value
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
        return closest;
    }

    // Follow-up 3: Count occurrences
    public int countOccurrences(int[] nums, int target) {
        int count = 0;
        for (int num : nums) if (num == target) count++;
        return count;
    }

    public static void main(String[] args) {
        SearchInRotatedSortedArrayII solution = new SearchInRotatedSortedArrayII();
        int[] nums1 = {2,5,6,0,0,1,2};
        System.out.println("Basic: " + solution.search(nums1, 0)); // true
        System.out.println("Not found: " + solution.search(nums1, 3)); // false
        int[] nums2 = {1};
        System.out.println("Single element: " + solution.search(nums2, 1)); // true
        System.out.println("Rotation index: " + solution.findRotationIndex(nums1)); // 3
        System.out.println("Closest to 3: " + solution.searchClosest(nums1, 3)); // 2
        System.out.println("Count occurrences of 0: " + solution.countOccurrences(nums1, 0)); // 2
    }
}
