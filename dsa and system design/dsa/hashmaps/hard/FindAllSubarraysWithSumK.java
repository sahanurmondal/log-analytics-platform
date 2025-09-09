package hashmaps.hard;

import java.util.*;

/**
 * LeetCode 560: Subarray Sum Equals K
 * https://leetcode.com/problems/subarray-sum-equals-k/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Very High (Asked in 20+ interviews)
 *
 * Description: Given an array of integers `nums` and an integer `k`, return all
 * continuous subarrays whose sum equals to `k`.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - -1000 <= nums[i] <= 1000
 * - -10^7 <= k <= 10^7
 * 
 * Follow-up Questions:
 * 1. Can you just count the number of such subarrays? (This is the original
 * LeetCode problem)
 * 2. What if the array contains only positive numbers? (Sliding Window)
 * 3. How would you handle this for a 2D matrix?
 */
public class FindAllSubarraysWithSumK {

    // Approach 1: Prefix Sum with HashMap - O(n) time, O(n) space
    public List<List<Integer>> findAllSubarraysWithSumK(int[] nums, int k) {
        List<List<Integer>> result = new ArrayList<>();
        Map<Integer, List<Integer>> prefixSumMap = new HashMap<>();
        prefixSumMap.computeIfAbsent(0, key -> new ArrayList<>()).add(-1);
        int currentSum = 0;

        for (int i = 0; i < nums.length; i++) {
            currentSum += nums[i];
            int complement = currentSum - k;

            if (prefixSumMap.containsKey(complement)) {
                for (int start : prefixSumMap.get(complement)) {
                    result.add(Arrays.asList(start + 1, i));
                }
            }

            prefixSumMap.computeIfAbsent(currentSum, key -> new ArrayList<>()).add(i);
        }

        return result;
    }

    public static void main(String[] args) {
        FindAllSubarraysWithSumK solution = new FindAllSubarraysWithSumK();

        // Test case 1
        int[] nums1 = { 1, 1, 1 };
        int k1 = 2;
        System.out.println("Subarrays 1: " + solution.findAllSubarraysWithSumK(nums1, k1)); // [[0, 1], [1, 2]]

        // Test case 2
        int[] nums2 = { 3, 4, 7, 2, -3, 1, 4, 2 };
        int k2 = 7;
        System.out.println("Subarrays 2: " + solution.findAllSubarraysWithSumK(nums2, k2));
    }
}
