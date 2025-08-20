package arrays.medium;

/**
 * LeetCode 33: Search in Rotated Sorted Array
 * https://leetcode.com/problems/search-in-rotated-sorted-array/
 */
public class SearchInRotatedSortedArray {
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

        if (pivot == 0) {
            return binarySearch(nums, 0, nums.length - 1, target);
        }

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
            if (nums[mid] == target)
                return mid;
            else if (nums[mid] < target)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        SearchInRotatedSortedArray solution = new SearchInRotatedSortedArray();
        // Edge Case 1: Rotated array, target present
        System.out.println(solution.search(new int[] { 4, 5, 6, 7, 0, 1, 2 }, 0)); // 4
        // Edge Case 2: Rotated array, target absent
        System.out.println(solution.search(new int[] { 4, 5, 6, 7, 0, 1, 2 }, 3)); // -1
        // Edge Case 3: Not rotated, target present
        System.out.println(solution.search(new int[] { 1, 2, 3, 4, 5 }, 3)); // 2
        // Edge Case 4: Not rotated, target absent
        System.out.println(solution.search(new int[] { 1, 2, 3, 4, 5 }, 6)); // -1
        // Edge Case 5: Single element, target present
        System.out.println(solution.search(new int[] { 10 }, 10)); // 0
        // Edge Case 6: Single element, target absent
        System.out.println(solution.search(new int[] { 10 }, 5)); // -1
        // Edge Case 7: Two elements, rotated, target present
        System.out.println(solution.search(new int[] { 2, 1 }, 1)); // 1
        // Edge Case 8: Large input, rotated at end
        int[] large = new int[5000];
        for (int i = 0; i < 4999; i++)
            large[i] = i + 2;
        large[4999] = 1;
        System.out.println(solution.search(large, 1)); // 4999
    }
}