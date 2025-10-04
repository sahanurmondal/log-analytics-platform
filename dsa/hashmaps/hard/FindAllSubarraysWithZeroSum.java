package hashmaps.hard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 560 (variation): Subarray Sum Equals K (where K=0)
 * https://leetcode.com/problems/subarray-sum-equals-k/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description:
 * Given an array of integers, return all subarrays whose sum is zero.
 * A subarray is a contiguous part of an array.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * 
 * Follow-up Questions:
 * 1. Can you just count the number of such subarrays? (This is LeetCode 560)
 * 2. How would you handle large numbers that might overflow standard integer
 * types?
 * 3. What if you need to find subarrays with a sum equal to K?
 */
public class FindAllSubarraysWithZeroSum {

    // Approach 1: Prefix Sum with HashMap - O(n) time, O(n) space
    public List<List<Integer>> findZeroSumSubarrays(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        // Map to store prefix sums and the indices where they occur
        Map<Long, List<Integer>> prefixSumMap = new HashMap<>();
        long currentSum = 0;

        // Add a base case for sums starting from index 0
        prefixSumMap.computeIfAbsent(0L, k -> new ArrayList<>()).add(-1);

        for (int i = 0; i < nums.length; i++) {
            currentSum += nums[i];

            // If the currentSum has been seen before, it means the subarray(s)
            // between the previous occurrences and the current index sum to zero.
            if (prefixSumMap.containsKey(currentSum)) {
                for (int start : prefixSumMap.get(currentSum)) {
                    // The subarray is from start + 1 to i
                    result.add(List.of(start + 1, i));
                }
            }

            // Add the current index to the list for the currentSum
            prefixSumMap.computeIfAbsent(currentSum, k -> new ArrayList<>()).add(i);
        }

        return result;
    }

    // Approach 2: Brute Force - O(n^2) time, O(1) space (excluding result list)
    public List<List<Integer>> findZeroSumSubarraysBruteForce(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            long currentSum = 0;
            for (int j = i; j < nums.length; j++) {
                currentSum += nums[j];
                if (currentSum == 0) {
                    result.add(List.of(i, j));
                }
            }
        }
        return result;
    }

    // Follow-up 1: Count the number of subarrays with zero sum
    public int countZeroSumSubarrays(int[] nums) {
        Map<Long, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0L, 1);
        long currentSum = 0;
        int count = 0;

        for (int num : nums) {
            currentSum += num;
            count += prefixSumCount.getOrDefault(currentSum, 0);
            prefixSumCount.put(currentSum, prefixSumCount.getOrDefault(currentSum, 0) + 1);
        }

        return count;
    }

    public static void main(String[] args) {
        FindAllSubarraysWithZeroSum solution = new FindAllSubarraysWithZeroSum();

        // Test case 1: Standard case
        int[] nums1 = { 6, 3, -1, -3, 4, -2, 2, 4, 6, -12, -7 };
        System.out.println("Subarrays for nums1: " + solution.findZeroSumSubarrays(nums1));
        // Expected: [[2, 4], [2, 5], [5, 6], [6, 9], [0, 9]] (order may vary)

        // Test case 2: No zero-sum subarrays
        int[] nums2 = { 1, 2, 3, 4 };
        System.out.println("Subarrays for nums2: " + solution.findZeroSumSubarrays(nums2));
        // Expected: []

        // Test case 3: Array with all zeros
        int[] nums3 = { 0, 0, 0 };
        System.out.println("Subarrays for nums3: " + solution.findZeroSumSubarrays(nums3));
        // Expected: [[0, 0], [1, 1], [2, 2], [0, 1], [1, 2], [0, 2]]

        // Test case 4: Array with single zero
        int[] nums4 = { 1, 0, -1 };
        System.out.println("Subarrays for nums4: " + solution.findZeroSumSubarrays(nums4));
        // Expected: [[1, 1], [0, 2]]

        // Test case 5: Empty array
        int[] nums5 = {};
        System.out.println("Subarrays for nums5: " + solution.findZeroSumSubarrays(nums5));
        // Expected: []

        // Test case 6: Large input
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++)
            large[i] = (i % 2 == 0) ? 1 : -1;
        System.out.println("Count for large input: " + solution.countZeroSumSubarrays(large));
        // Expected: 250000
    }
}
