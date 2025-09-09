package miscellaneous.recent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Recent Problem: Maximum Subarray Sum with At Most K Distinct Elements
 * 
 * Description:
 * Given an array of integers and an integer k, find the maximum sum of a
 * subarray
 * that contains at most k distinct elements.
 * 
 * Companies: Google, Facebook, Amazon
 * Difficulty: Medium
 * Asked: 2023-2024
 */
public class MaximumSubarraySum {

    public long maximumSubarraySum(int[] nums, int k) {
        int n = nums.length;
        long maxSum = 0;

        for (int i = 0; i < n; i++) {
            Set<Integer> distinct = new HashSet<>();
            long currentSum = 0;

            for (int j = i; j < n; j++) {
                distinct.add(nums[j]);
                currentSum += nums[j];

                if (distinct.size() <= k) {
                    maxSum = Math.max(maxSum, currentSum);
                } else {
                    break;
                }
            }
        }

        return maxSum;
    }

    // Optimized sliding window approach
    public long maximumSubarraySumOptimized(int[] nums, int k) {
        Map<Integer, Integer> count = new HashMap<>();
        long currentSum = 0;
        long maxSum = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            count.put(nums[right], count.getOrDefault(nums[right], 0) + 1);
            currentSum += nums[right];

            while (count.size() > k) {
                count.put(nums[left], count.get(nums[left]) - 1);
                if (count.get(nums[left]) == 0) {
                    count.remove(nums[left]);
                }
                currentSum -= nums[left];
                left++;
            }

            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        MaximumSubarraySum solution = new MaximumSubarraySum();

        int[] nums = { 1, 2, 1, 2, 6, 7, 5, 1 };
        int k = 3;

        System.out.println(solution.maximumSubarraySum(nums, k)); // Expected output depends on implementation
        System.out.println(solution.maximumSubarraySumOptimized(nums, k));
    }
}
