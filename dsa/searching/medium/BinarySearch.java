package searching.medium;

/**
 * LeetCode 704: Binary Search
 * https://leetcode.com/problems/binary-search/
 * 
 * Companies: Google, Amazon, Facebook, Microsoft
 * Frequency: High
 *
 * Description: Given a sorted array of integers nums and an integer target,
 * write a function to search target in nums. If target exists, then return its index.
 * Otherwise, return -1.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^4 < nums[i], target < 10^4
 * - All the integers in nums are unique.
 * - nums is sorted in ascending order.
 * 
 * Follow-up Questions:
 * 1. Can you handle duplicates (e.g., find first/last occurrence)?
 * 2. What about searching in a rotated sorted array?
 * 3. Can you find the insertion position for a target?
 */
public class BinarySearch {

    // Approach 1: Standard Iterative Binary Search - O(log n) time, O(1) space
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2; // Avoids overflow
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

    // Follow-up 1: Find first occurrence of target in an array with duplicates
    public int findFirstOccurrence(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] >= target) {
                if (nums[mid] == target) result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return result;
    }

    // Follow-up 1: Find last occurrence of target in an array with duplicates
    public int findLastOccurrence(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) {
                if (nums[mid] == target) result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    // Follow-up 2: Search in a rotated sorted array (LeetCode 33)
    public int searchInRotatedArray(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;

            if (nums[left] <= nums[mid]) { // Left half is sorted
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else { // Right half is sorted
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return -1;
    }

    // Follow-up 3: Find insertion position (LeetCode 35)
    public int searchInsertPosition(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left; // `left` is the insertion point
    }

    public static void main(String[] args) {
        BinarySearch solution = new BinarySearch();

        // Test case 1: Target exists
        int[] nums1 = {-1, 0, 3, 5, 9, 12};
        System.out.println("Test 1: Target 9 in [-1,0,3,5,9,12]");
        System.out.println("Expected: 4, Got: " + solution.search(nums1, 9));

        // Test case 2: Target does not exist
        System.out.println("\nTest 2: Target 2 in [-1,0,3,5,9,12]");
        System.out.println("Expected: -1, Got: " + solution.search(nums1, 2));

        // Edge case: Target is the first element
        System.out.println("\nEdge case: Target -1 (first element)");
        System.out.println("Expected: 0, Got: " + solution.search(nums1, -1));

        // Edge case: Target is the last element
        System.out.println("\nEdge case: Target 12 (last element)");
        System.out.println("Expected: 5, Got: " + solution.search(nums1, 12));

        // Follow-up 1: Duplicates
        int[] numsWithDups = {5, 7, 7, 8, 8, 10};
        System.out.println("\nFollow-up 1: Duplicates in [5,7,7,8,8,10], target 8");
        System.out.println("First occurrence: " + solution.findFirstOccurrence(numsWithDups, 8));
        System.out.println("Last occurrence: " + solution.findLastOccurrence(numsWithDups, 8));

        // Follow-up 2: Rotated Array
        int[] rotatedNums = {4, 5, 6, 7, 0, 1, 2};
        System.out.println("\nFollow-up 2: Rotated array [4,5,6,7,0,1,2], target 0");
        System.out.println("Expected: 4, Got: " + solution.searchInRotatedArray(rotatedNums, 0));
        System.out.println("Rotated array, target 3 (not found): " + solution.searchInRotatedArray(rotatedNums, 3));

        // Follow-up 3: Insertion Position
        int[] insertNums = {1, 3, 5, 6};
        System.out.println("\nFollow-up 3: Insertion position in [1,3,5,6]");
        System.out.println("Target 5: " + solution.searchInsertPosition(insertNums, 5));
        System.out.println("Target 2: " + solution.searchInsertPosition(insertNums, 2));
        System.out.println("Target 7: " + solution.searchInsertPosition(insertNums, 7));
        System.out.println("Target 0: " + solution.searchInsertPosition(insertNums, 0));
    }
}
