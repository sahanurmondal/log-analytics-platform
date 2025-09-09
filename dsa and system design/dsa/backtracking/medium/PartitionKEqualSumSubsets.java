package backtracking.medium;

import java.util.*;

/**
 * LeetCode 698: Partition to K Equal Sum Subsets
 * https://leetcode.com/problems/partition-to-k-equal-sum-subsets/
 *
 * Description: Given an integer array nums and an integer k, return true if it
 * is possible
 * to partition this array into k non-empty subsets whose sums are all equal.
 * 
 * Constraints:
 * - 1 <= k <= nums.length <= 16
 * - 1 <= nums[i] <= 10^4
 * - The frequency of each element is in the range [1, 4]
 *
 * Follow-up:
 * - Can you optimize with memoization?
 * 
 * Time Complexity: O(k * 2^n)
 * Space Complexity: O(2^n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class PartitionKEqualSumSubsets {

    public boolean canPartitionKSubsets(int[] nums, int k) {
        int sum = Arrays.stream(nums).sum();
        if (sum % k != 0)
            return false;

        int targetSum = sum / k;
        Arrays.sort(nums);

        // Optimization: if largest element > target, impossible
        if (nums[nums.length - 1] > targetSum)
            return false;

        return backtrack(nums, new int[k], nums.length - 1, targetSum);
    }

    private boolean backtrack(int[] nums, int[] subsets, int index, int targetSum) {
        if (index < 0) {
            // Check if all subsets have target sum
            for (int subset : subsets) {
                if (subset != targetSum)
                    return false;
            }
            return true;
        }

        int num = nums[index];
        for (int i = 0; i < subsets.length; i++) {
            if (subsets[i] + num <= targetSum) {
                subsets[i] += num;
                if (backtrack(nums, subsets, index - 1, targetSum)) {
                    return true;
                }
                subsets[i] -= num;
            }

            // Optimization: if current subset is empty, no need to try other empty subsets
            if (subsets[i] == 0)
                break;
        }

        return false;
    }

    // Alternative solution - Bit manipulation with memoization
    public boolean canPartitionKSubsetsMemo(int[] nums, int k) {
        int sum = Arrays.stream(nums).sum();
        if (sum % k != 0)
            return false;

        int targetSum = sum / k;
        return backtrackMemo(nums, 0, 0, 0, targetSum, k, new HashMap<>());
    }

    private boolean backtrackMemo(int[] nums, int mask, int currentSum, int subsetsFormed,
            int targetSum, int k, Map<Integer, Boolean> memo) {
        if (subsetsFormed == k - 1)
            return true;

        if (memo.containsKey(mask))
            return memo.get(mask);

        if (currentSum == targetSum) {
            boolean result = backtrackMemo(nums, mask, 0, subsetsFormed + 1, targetSum, k, memo);
            memo.put(mask, result);
            return result;
        }

        for (int i = 0; i < nums.length; i++) {
            if ((mask & (1 << i)) == 0 && currentSum + nums[i] <= targetSum) {
                if (backtrackMemo(nums, mask | (1 << i), currentSum + nums[i], subsetsFormed, targetSum, k, memo)) {
                    memo.put(mask, true);
                    return true;
                }
            }
        }

        memo.put(mask, false);
        return false;
    }

    public static void main(String[] args) {
        PartitionKEqualSumSubsets solution = new PartitionKEqualSumSubsets();

        System.out.println(solution.canPartitionKSubsets(new int[] { 4, 3, 2, 3, 5, 2, 1 }, 4)); // Expected: true
        System.out.println(solution.canPartitionKSubsets(new int[] { 1, 2, 3, 4 }, 3)); // Expected: false
        System.out.println(solution.canPartitionKSubsets(new int[] { 2, 2, 2, 2, 3, 4, 5 }, 4)); // Expected: false
    }
}
