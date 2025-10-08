package arrays.medium;

import java.util.*;

/**
 * LeetCode 560: Subarray Sum Equals K
 * https://leetcode.com/problems/subarray-sum-equals-k/
 *
 * Description:
 * Given an array of integers nums and an integer k, return the total number of
 * continuous subarrays whose sum equals to k.
 *
 * Input:
 * - nums: int[] (array of integers)
 * - k: int (target sum)
 *
 * Output:
 * - int (total number of continuous subarrays whose sum equals k)
 *
 * Example 1:
 * Input: nums = [1,1,1], k = 2
 * Output: 2
 *
 * Example 2:
 * Input: nums = [1,2,3], k = 3
 * Output: 2
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - -1000 <= nums[i] <= 1000
 * - -10^7 <= k <= 10^7
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * - How would you handle very large arrays?
 *
 * Mathematical Proof & Explanation:
 * ------------------------------------------------------------
 * Let prefixSum[i] be the sum of nums[0] to nums[i].
 * For any subarray nums[i..j], its sum is:
 *   sum(nums[i..j]) = prefixSum[j] - prefixSum[i-1]
 * We want sum(nums[i..j]) == k, so:
 *   prefixSum[j] - prefixSum[i-1] == k
 *   => prefixSum[i-1] == prefixSum[j] - k
 * Thus, for each position j, if we have seen prefixSum[j] - k before,
 * it means there exists an i such that nums[i..j] sums to k.
 * By counting the number of times prefixSum[j] - k has occurred,
 * we count all valid subarrays ending at j.
 * This is why we use a hashmap to store frequencies of prefix sums.
 * ------------------------------------------------------------
 */
public class SubarraySumEqualsK {
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0, 1);

        int count = 0;
        int prefixSum = 0;

        for (int num : nums) {
            prefixSum += num;
            if (prefixSumCount.containsKey(prefixSum - k)) {
                count += prefixSumCount.get(prefixSum - k);
            }
            prefixSumCount.put(prefixSum, prefixSumCount.getOrDefault(prefixSum, 0) + 1);
        }

        return count;
    }

    // Alternative solution - Brute force
    public int subarraySumBruteForce(int[] nums, int k) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum == k)
                    count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        SubarraySumEqualsK solution = new SubarraySumEqualsK();
        // Edge Case 1: Normal case
        System.out.println(solution.subarraySum(new int[] { 1, 1, 1 }, 2)); // 2
        // Edge Case 2: Negative numbers
        System.out.println(solution.subarraySum(new int[] { -1, -1, 1 }, 0)); // 1
        // Edge Case 3: All zeros
        System.out.println(solution.subarraySum(new int[] { 0, 0, 0, 0 }, 0)); // 10
        // Edge Case 4: Large input
        int[] large = new int[20000];
        for (int i = 0; i < 20000; i++)
            large[i] = 1;
        System.out.println(solution.subarraySum(large, 20000)); // 1
        // Edge Case 5: Single element
        System.out.println(solution.subarraySum(new int[] { 5 }, 5)); // 1
        // Edge Case 6: No subarray matches
        System.out.println(solution.subarraySum(new int[] { 1, 2, 3 }, 7)); // 0
        // Edge Case 7: Negative k
        System.out.println(solution.subarraySum(new int[] { -1, -1, -1 }, -2)); // 2
    }
}
