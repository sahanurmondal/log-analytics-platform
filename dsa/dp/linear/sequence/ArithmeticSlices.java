package dp.linear.sequence;

/**
 * LeetCode 413: Arithmetic Slices
 * https://leetcode.com/problems/arithmetic-slices/
 *
 * Description:
 * An integer array is called arithmetic if it consists of at least three
 * elements and if the difference between any two consecutive elements is the
 * same.
 * Given an integer array nums, return the number of arithmetic subarrays of
 * nums.
 * A subarray is a contiguous subsequence of the array.
 *
 * Constraints:
 * - 1 <= nums.length <= 5000
 * - -1000 <= nums[i] <= 1000
 *
 * Company Tags: Google, Amazon
 * Difficulty: Easy
 */
public class ArithmeticSlices {

    // Approach 1: DP - O(n) time, O(1) space
    public int numberOfArithmeticSlices(int[] nums) {
        if (nums.length < 3)
            return 0;

        int count = 0;
        int current = 0;

        for (int i = 2; i < nums.length; i++) {
            if (nums[i] - nums[i - 1] == nums[i - 1] - nums[i - 2]) {
                current++;
                count += current;
            } else {
                current = 0;
            }
        }

        return count;
    }

    // Approach 2: DP Array - O(n) time, O(n) space
    public int numberOfArithmeticSlicesDP(int[] nums) {
        if (nums.length < 3)
            return 0;

        int[] dp = new int[nums.length];
        int count = 0;

        for (int i = 2; i < nums.length; i++) {
            if (nums[i] - nums[i - 1] == nums[i - 1] - nums[i - 2]) {
                dp[i] = dp[i - 1] + 1;
                count += dp[i];
            }
        }

        return count;
    }

    public static void main(String[] args) {
        ArithmeticSlices solution = new ArithmeticSlices();

        System.out.println("=== Arithmetic Slices Test Cases ===");

        int[] nums1 = { 1, 2, 3, 4 };
        System.out.println("Array: " + java.util.Arrays.toString(nums1));
        System.out.println("Arithmetic Slices: " + solution.numberOfArithmeticSlices(nums1));
        System.out.println("Expected: 3\n");

        int[] nums2 = { 1, 3, 5, 7, 9 };
        System.out.println("Array: " + java.util.Arrays.toString(nums2));
        System.out.println("Arithmetic Slices: " + solution.numberOfArithmeticSlices(nums2));
        System.out.println("Expected: 6\n");
    }
}
