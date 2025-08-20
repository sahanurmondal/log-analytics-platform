package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 1425: Constrained Subsequence Sum
 * https://leetcode.com/problems/constrained-subsequence-sum/
 *
 * Description:
 * Given an integer array nums and an integer k, return the maximum sum of a
 * non-empty subsequence of that array such that for every two consecutive
 * integers in the subsequence, nums[i] and nums[j], where i < j, the condition
 * j - i <= k is satisfied.
 *
 * Constraints:
 * - 1 <= k <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it using sliding window maximum with DP?
 * - Can you optimize space complexity?
 * - Can you handle negative constraints on k?
 */
public class ConstrainedSubsequenceSum {
    public int constrainedSubsetSum(int[] nums, int k) {
        if (nums == null || nums.length == 0)
            return 0;

        int n = nums.length;
        int[] dp = new int[n];
        Deque<Integer> deque = new ArrayDeque<>();

        dp[0] = nums[0];
        deque.offer(0);
        int result = dp[0];

        for (int i = 1; i < n; i++) {
            // Remove elements that are out of range
            while (!deque.isEmpty() && deque.peekFirst() < i - k) {
                deque.pollFirst();
            }

            // Calculate dp[i]
            int maxPrev = deque.isEmpty() ? 0 : dp[deque.peekFirst()];
            dp[i] = Math.max(nums[i], nums[i] + maxPrev);
            result = Math.max(result, dp[i]);

            // Maintain decreasing order in deque
            while (!deque.isEmpty() && dp[deque.peekLast()] <= dp[i]) {
                deque.pollLast();
            }

            if (dp[i] > 0) {
                deque.offer(i);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        ConstrainedSubsequenceSum solution = new ConstrainedSubsequenceSum();
        System.out.println(solution.constrainedSubsetSum(new int[] { 10, 2, -10, 5, 20 }, 2)); // 37
        System.out.println(solution.constrainedSubsetSum(new int[] { -1, -2, -3 }, 1)); // -1
        System.out.println(solution.constrainedSubsetSum(new int[] { 10, -2, -10, -5, 20 }, 2)); // 23
        // Edge Case: k = 1
        System.out.println(solution.constrainedSubsetSum(new int[] { 1, 2, 3, 4 }, 1)); // 10
        // Edge Case: All negative
        System.out.println(solution.constrainedSubsetSum(new int[] { -5, -3, -1, -4 }, 2)); // -1
        // Edge Case: k >= array length
        System.out.println(solution.constrainedSubsetSum(new int[] { 1, 2, 3 }, 5)); // 6
    }
}
