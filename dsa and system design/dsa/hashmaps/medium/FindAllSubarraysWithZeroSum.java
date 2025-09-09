package hashmaps.medium;

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

    public static void main(String[] args) {
        FindAllSubarraysWithZeroSum solution = new FindAllSubarraysWithZeroSum();
        System.out.println(solution.findZeroSumSubarrays(new int[] { 6, 3, -1, -3, 4, -2, 2, 4, 6, -12, -7 })); // [[2,5],[0,5],[5,6],[6,9],[0,9]]
        // Edge Case: No zero sum
        System.out.println(solution.findZeroSumSubarrays(new int[] { 1, 2, 3, 4 })); // []
        // Edge Case: All zeros
        System.out.println(solution.findZeroSumSubarrays(new int[] { 0, 0, 0 })); // [[0,0],[0,1],[0,2],[1,1],[1,2],[2,2]]
        // Edge Case: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = i % 2 == 0 ? 1 : -1;
        System.out.println(solution.findZeroSumSubarrays(large)); // Should be large
    }
}
