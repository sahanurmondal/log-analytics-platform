package arrays.medium;

/**
 * LeetCode 41: First Missing Positive
 * https://leetcode.com/problems/first-missing-positive/
 *
 * Description:
 * Given an unsorted integer array nums, return the smallest missing positive
 * integer.
 * You must implement an algorithm that runs in O(n) time and uses constant
 * extra space.
 *
 * Constraints:
 * - 1 <= nums.length <= 5 * 10^5
 * - -2^31 <= nums[i] <= 2^31 - 1
 *
 * Follow-up:
 * - Can you implement this in O(n) time and O(1) space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Use array indices to mark presence of positive numbers
 * 2. Place each positive number at its corresponding index (num-1)
 * 3. Find first index where number doesn't match position+1
 */
public class FirstMissingPositive {
    public int firstMissingPositive(int[] nums) {
        int n = nums.length;

        // Step 1: Replace numbers <= 0 or > n with n+1
        for (int i = 0; i < n; i++) {
            if (nums[i] <= 0 || nums[i] > n) {
                nums[i] = n + 1;
            }
        }

        // Step 2: Use indices to mark presence
        for (int i = 0; i < n; i++) {
            int num = Math.abs(nums[i]);
            if (num <= n) {
                nums[num - 1] = -Math.abs(nums[num - 1]);
            }
        }

        // Step 3: Find first positive number
        for (int i = 0; i < n; i++) {
            if (nums[i] > 0) {
                return i + 1;
            }
        }

        return n + 1;
    }

    public static void main(String[] args) {
        FirstMissingPositive solution = new FirstMissingPositive();

        // Test Case 1: Normal case
        System.out.println(solution.firstMissingPositive(new int[] { 1, 2, 0 })); // Expected: 3

        // Test Case 2: Edge case - negative numbers
        System.out.println(solution.firstMissingPositive(new int[] { 3, 4, -1, 1 })); // Expected: 2

        // Test Case 3: Corner case - all negative
        System.out.println(solution.firstMissingPositive(new int[] { -1, -2, -3 })); // Expected: 1

        // Test Case 4: Large input - consecutive
        System.out.println(solution.firstMissingPositive(new int[] { 1, 2, 3, 4, 5 })); // Expected: 6

        // Test Case 5: Minimum input
        System.out.println(solution.firstMissingPositive(new int[] { 1 })); // Expected: 2

        // Test Case 6: Special case - missing 1
        System.out.println(solution.firstMissingPositive(new int[] { 2, 3, 4 })); // Expected: 1

        // Test Case 7: Boundary case - duplicates
        System.out.println(solution.firstMissingPositive(new int[] { 1, 1, 1, 1 })); // Expected: 2

        // Test Case 8: Large numbers
        System.out.println(solution.firstMissingPositive(new int[] { 1000, 1001, 1002 })); // Expected: 1

        // Test Case 9: Mixed positive/negative
        System.out.println(solution.firstMissingPositive(new int[] { 1, -1, 3, 4 })); // Expected: 2

        // Test Case 10: Single element - not 1
        System.out.println(solution.firstMissingPositive(new int[] { 2 })); // Expected: 1
    }
}
