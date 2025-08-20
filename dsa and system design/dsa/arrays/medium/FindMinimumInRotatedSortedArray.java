package arrays.medium;

/**
 * LeetCode 153: Find Minimum in Rotated Sorted Array
 * https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/
 *
 * Description:
 * Suppose an array of length n sorted in ascending order is rotated between 1
 * and n times.
 * Given the sorted rotated array nums of unique elements, return the minimum
 * element of this array.
 * You must write an algorithm that runs in O(log n) time.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 5000
 * - -5000 <= nums[i] <= 5000
 * - All integers of nums are unique
 *
 * Follow-up:
 * - Can you solve it in O(log n) time?
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Use binary search to find the rotation point
 * 2. Compare middle element with rightmost element
 * 3. If mid > right, minimum is in right half; otherwise in left half
 */
public class FindMinimumInRotatedSortedArray {
    public int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                // Minimum is in right half
                left = mid + 1;
            } else {
                // Minimum is in left half (including mid)
                right = mid;
            }
        }

        return nums[left];
    }

    public static void main(String[] args) {
        FindMinimumInRotatedSortedArray solution = new FindMinimumInRotatedSortedArray();

        // Test Case 1: Normal case - rotated
        System.out.println(solution.findMin(new int[] { 3, 4, 5, 1, 2 })); // Expected: 1

        // Test Case 2: Edge case - not rotated
        System.out.println(solution.findMin(new int[] { 1, 2, 3, 4, 5 })); // Expected: 1

        // Test Case 3: Corner case - rotated once
        System.out.println(solution.findMin(new int[] { 2, 1 })); // Expected: 1

        // Test Case 4: Large input
        System.out.println(solution.findMin(new int[] { 4, 5, 6, 7, 0, 1, 2 })); // Expected: 0

        // Test Case 5: Minimum input - single element
        System.out.println(solution.findMin(new int[] { 1 })); // Expected: 1

        // Test Case 6: Special case - minimum at end
        System.out.println(solution.findMin(new int[] { 2, 3, 4, 5, 1 })); // Expected: 1

        // Test Case 7: Boundary case - two elements
        System.out.println(solution.findMin(new int[] { 1, 2 })); // Expected: 1

        // Test Case 8: Negative numbers
        System.out.println(solution.findMin(new int[] { 0, 1, 2, -3, -2, -1 })); // Expected: -3

        // Test Case 9: All negative
        System.out.println(solution.findMin(new int[] { -2, -1, -3 })); // Expected: -3

        // Test Case 10: Minimum at beginning
        System.out.println(solution.findMin(new int[] { 1, 2, 3 })); // Expected: 1
    }
}
