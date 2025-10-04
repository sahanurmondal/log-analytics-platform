package hashmaps.medium;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 560: Subarray Sum Equals K
 * https://leetcode.com/problems/subarray-sum-equals-k/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Very High (Asked in 20+ interviews)
 *
 * Description: Given an array of integers `nums` and an integer `k`, return the
 * total number of continuous subarrays whose sum equals to `k`.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - -1000 <= nums[i] <= 1000
 * - -10^7 <= k <= 10^7
 * 
 * Follow-up Questions:
 * 1. Can you find the actual subarrays, not just the count?
 * 2. What if the array contains only positive numbers? (Sliding Window)
 * 3. How would you handle this for a 2D matrix?
 */
public class SubarraySumEqualsK {

    // Approach 1: Prefix Sum with HashMap - O(n) time, O(n) space
    public int subarraySum(int[] nums, int k) {
        int count = 0;
        int currentSum = 0;
        Map<Integer, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0, 1); // Base case for subarrays starting at index 0

        for (int num : nums) {
            currentSum += num;
            // If (currentSum - k) exists in the map, it means there is a subarray
            // ending at the current index with a sum of k.
            if (prefixSumCount.containsKey(currentSum - k)) {
                count += prefixSumCount.get(currentSum - k);
            }

            // Add the current prefix sum to the map
            prefixSumCount.put(currentSum, prefixSumCount.getOrDefault(currentSum, 0) + 1);
        }

        return count;
    }

    // Approach 2: Brute Force - O(n^2) time, O(1) space
    public int subarraySumBruteForce(int[] nums, int k) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum == k) {
                    count++;
                }
            }
        }
        return count;
    }

    // Follow-up 2: Array with only positive numbers (Sliding Window) - O(n) time,
    // O(1) space
    public int subarraySumPositive(int[] nums, int k) {
        int count = 0;
        int left = 0;
        int currentSum = 0;

        for (int right = 0; right < nums.length; right++) {
            currentSum += nums[right];

            while (currentSum > k && left <= right) {
                currentSum -= nums[left];
                left++;
            }

            if (currentSum == k) {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        SubarraySumEqualsK solution = new SubarraySumEqualsK();

        // Test case 1
        int[] nums1 = { 1, 1, 1 };
        int k1 = 2;
        System.out.println("Count 1: " + solution.subarraySum(nums1, k1)); // 2

        // Test case 2
        int[] nums2 = { 1, 2, 3 };
        int k2 = 3;
        System.out.println("Count 2: " + solution.subarraySum(nums2, k2)); // 2

        // Test case 3: Negative numbers
        int[] nums3 = { 1, -1, 1, -1, 1 };
        int k3 = 0;
        System.out.println("Count 3: " + solution.subarraySum(nums3, k3)); // 6

        // Test case 4: Zeros
        int[] nums4 = { 0, 0, 0 };
        int k4 = 0;
        System.out.println("Count 4: " + solution.subarraySum(nums4, k4)); // 6

        // Test case 5: Positive numbers with sliding window
        int[] nums5 = { 1, 2, 1, 2, 1 };
        int k5 = 3;
        System.out.println("Count 5 (Positive): " + solution.subarraySumPositive(nums5, k5)); // 3
    }
}
