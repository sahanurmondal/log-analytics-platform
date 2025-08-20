package dp.easy;

import java.util.*;

/**
 * LeetCode 740: Delete and Earn
 * https://leetcode.com/problems/delete-and-earn/
 *
 * Description:
 * You are given an integer array nums. You want to maximize the number of
 * points you get by performing the following operation any number of times:
 * Pick any nums[i] and delete it to earn nums[i] points. Afterwards, you must
 * delete every element equal to nums[i] - 1 and every element equal to nums[i]
 * + 1.
 * Return the maximum number of points you can earn by applying the above
 * operation some number of times.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - 1 <= nums[i] <= 10^4
 *
 * Company Tags: Google, Amazon
 * Difficulty: Easy
 */
public class DeleteAndEarn {

    // Approach 1: Transform to House Robber - O(n + k) time, O(k) space
    public int deleteAndEarn(int[] nums) {
        int maxNum = Arrays.stream(nums).max().orElse(0);
        int[] counts = new int[maxNum + 1];

        for (int num : nums) {
            counts[num] += num;
        }

        return rob(counts);
    }

    private int rob(int[] nums) {
        int prev2 = 0, prev1 = 0;

        for (int num : nums) {
            int current = Math.max(prev1, prev2 + num);
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    public static void main(String[] args) {
        DeleteAndEarn solution = new DeleteAndEarn();

        System.out.println("=== Delete and Earn Test Cases ===");

        int[] nums1 = { 3, 4, 2 };
        System.out.println("Array: " + Arrays.toString(nums1));
        System.out.println("Max Points: " + solution.deleteAndEarn(nums1));
        System.out.println("Expected: 6\n");
    }
}
