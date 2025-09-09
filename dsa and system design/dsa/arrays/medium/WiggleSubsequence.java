package arrays.medium;

/**
 * LeetCode 376: Wiggle Subsequence
 * https://leetcode.com/problems/wiggle-subsequence/
 *
 * Description:
 * Given an integer array nums, return the length of the longest wiggle
 * subsequence.
 * A wiggle sequence is one where the differences between successive numbers
 * strictly alternate between positive and negative.
 *
 * Constraints:
 * - 1 <= nums.length <= 1000
 * - 0 <= nums[i] <= 1000
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 */
public class WiggleSubsequence {
    public int wiggleMaxLength(int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;
        if (nums.length == 1)
            return 1;

        int maxLength = 1;
        int prevDiff = 0, currDiff = 0;

        for (int i = 1; i < nums.length; i++) {
            currDiff = nums[i] - nums[i - 1];

            if (currDiff > 0 && prevDiff <= 0) {
                maxLength++;
            } else if (currDiff < 0 && prevDiff >= 0) {
                maxLength++;
            }

            prevDiff = currDiff;
        }

        return maxLength;
    }

    public static void main(String[] args) {
        WiggleSubsequence solution = new WiggleSubsequence();
        int[] nums = { 1, 7, 4, 9, 2, 5 };
        System.out.println("The length of the longest wiggle subsequence is: " + solution.wiggleMaxLength(nums));
    }
}