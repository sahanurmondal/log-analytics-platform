package backtracking.hard;

import java.util.*;

/**
 * LeetCode 2597: The Number of Beautiful Subsets
 * URL: https://leetcode.com/problems/the-number-of-beautiful-subsets/
 * Difficulty: Hard
 * Companies: Amazon, Google, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Given an array nums and an integer k, return the number of non-empty
 * beautiful subsets of nums. A subset is beautiful if no two elements
 * in the subset have an absolute difference equal to k.
 *
 * Constraints:
 * - 1 <= nums.length <= 20
 * - 1 <= nums[i], k <= 1000
 *
 * Follow-up Questions:
 * 1. Can you solve it recursively?
 * 2. Can you optimize with bitmask DP?
 * 3. What if we need to return all beautiful subsets?
 * 4. How to handle larger constraints?
 */
public class CountNumberOfBeautifulSubsets {

    // Approach 1: Backtracking - O(2^n)
    public int beautifulSubsets(int[] nums, int k) {
        return backtrack(nums, k, 0, new ArrayList<>()) - 1; // -1 to exclude empty subset
    }

    private int backtrack(int[] nums, int k, int index, List<Integer> current) {
        if (index == nums.length) {
            return 1; // Count this subset (including empty)
        }

        // Don't include current element
        int count = backtrack(nums, k, index + 1, current);

        // Check if we can include current element
        boolean canInclude = true;
        for (int num : current) {
            if (Math.abs(num - nums[index]) == k) {
                canInclude = false;
                break;
            }
        }

        if (canInclude) {
            current.add(nums[index]);
            count += backtrack(nums, k, index + 1, current);
            current.remove(current.size() - 1);
        }

        return count;
    }

    // Approach 2: Optimized with frequency map - O(2^n)
    public int beautifulSubsetsOptimized(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        return backtrackOptimized(nums, k, 0, freq) - 1;
    }

    private int backtrackOptimized(int[] nums, int k, int index, Map<Integer, Integer> freq) {
        if (index == nums.length) {
            return 1;
        }

        // Don't include current element
        int count = backtrackOptimized(nums, k, index + 1, freq);

        // Check if we can include current element
        int current = nums[index];
        if (!freq.containsKey(current - k) && !freq.containsKey(current + k)) {
            freq.put(current, freq.getOrDefault(current, 0) + 1);
            count += backtrackOptimized(nums, k, index + 1, freq);

            if (freq.get(current) == 1) {
                freq.remove(current);
            } else {
                freq.put(current, freq.get(current) - 1);
            }
        }

        return count;
    }

    // Approach 3: Bitmask DP - O(n * 2^n)
    public int beautifulSubsetsBitmask(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[1 << n];
        dp[0] = 1; // Empty subset

        for (int mask = 0; mask < (1 << n); mask++) {
            if (dp[mask] == 0)
                continue;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0)
                    continue; // Already included

                boolean canInclude = true;
                for (int j = 0; j < n; j++) {
                    if ((mask & (1 << j)) != 0 && Math.abs(nums[i] - nums[j]) == k) {
                        canInclude = false;
                        break;
                    }
                }

                if (canInclude) {
                    dp[mask | (1 << i)] += dp[mask];
                }
            }
        }

        int result = 0;
        for (int mask = 1; mask < (1 << n); mask++) { // Exclude empty subset
            result += dp[mask];
        }

        return result;
    }

    // Follow-up 3: Get all beautiful subsets
    public List<List<Integer>> getAllBeautifulSubsets(int[] nums, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackAll(nums, k, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrackAll(int[] nums, int k, int index, List<Integer> current,
            List<List<Integer>> result) {
        if (index == nums.length) {
            if (!current.isEmpty()) {
                result.add(new ArrayList<>(current));
            }
            return;
        }

        // Don't include current element
        backtrackAll(nums, k, index + 1, current, result);

        // Check if we can include current element
        boolean canInclude = true;
        for (int num : current) {
            if (Math.abs(num - nums[index]) == k) {
                canInclude = false;
                break;
            }
        }

        if (canInclude) {
            current.add(nums[index]);
            backtrackAll(nums, k, index + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Helper method to check if subset is beautiful
    public boolean isBeautifulSubset(List<Integer> subset, int k) {
        for (int i = 0; i < subset.size(); i++) {
            for (int j = i + 1; j < subset.size(); j++) {
                if (Math.abs(subset.get(i) - subset.get(j)) == k) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        CountNumberOfBeautifulSubsets solution = new CountNumberOfBeautifulSubsets();

        // Test Case 1: Basic example
        System.out.println("Test 1: " + solution.beautifulSubsets(new int[] { 2, 4, 6 }, 2));
        // Expected: 4 (subsets: [2], [4], [6], [2,6])

        // Test Case 2: Single element
        System.out.println("Test 2: " + solution.beautifulSubsets(new int[] { 1 }, 1));
        // Expected: 1

        // Test Case 3: All elements conflict
        System.out.println("Test 3: " + solution.beautifulSubsets(new int[] { 4, 2, 5, 9, 10, 3 }, 1));
        // Expected: count of beautiful subsets

        // Test Case 4: Optimized approach
        System.out.println("Test 4 (Optimized): " + solution.beautifulSubsetsOptimized(new int[] { 2, 4, 6 }, 2));
        // Expected: 4

        // Test Case 5: Bitmask DP approach
        System.out.println("Test 5 (Bitmask): " + solution.beautifulSubsetsBitmask(new int[] { 2, 4, 6 }, 2));
        // Expected: 4

        // Test Case 6: No conflicts
        System.out.println("Test 6: " + solution.beautifulSubsets(new int[] { 1, 3, 5, 7 }, 3));
        // Expected: 2^4 - 1 = 15 (all non-empty subsets)

        // Test Case 7: All beautiful subsets
        System.out.println("Test 7 (All): " + solution.getAllBeautifulSubsets(new int[] { 2, 4, 6 }, 2));
        // Expected: [[2], [4], [6], [2,6]]

        // Test Case 8: Check if subset is beautiful
        System.out.println("Test 8 (Check): " + solution.isBeautifulSubset(Arrays.asList(2, 6), 2));
        // Expected: true

        // Test Case 9: Large k value
        System.out.println("Test 9: " + solution.beautifulSubsets(new int[] { 1, 2, 3, 4 }, 10));
        // Expected: 15 (no conflicts)

        // Test Case 10: Duplicate elements
        System.out.println("Test 10: " + solution.beautifulSubsets(new int[] { 2, 2, 4 }, 2));
        // Expected: count considering duplicates

        // Test Case 11: Small k
        System.out.println("Test 11: " + solution.beautifulSubsets(new int[] { 1, 2, 3 }, 1));
        // Expected: 4 ([1], [2], [3], [1,3])

        // Test Case 12: Performance comparison
        long start = System.currentTimeMillis();
        int result12 = solution.beautifulSubsetsOptimized(new int[] { 1, 2, 3, 4, 5, 6 }, 2);
        long end = System.currentTimeMillis();
        System.out.println("Test 12 (Performance): " + result12 + " in " + (end - start) + "ms");

        // Test Case 13: Edge case - k equals max difference
        System.out.println("Test 13: " + solution.beautifulSubsets(new int[] { 1, 5 }, 4));
        // Expected: 3 ([1], [5], [1,5])

        // Test Case 14: Verify consistency across approaches
        int[] test14 = { 2, 4, 6, 8 };
        int k14 = 2;
        System.out.println("Test 14 (Consistency): " +
                (solution.beautifulSubsets(test14, k14) == solution.beautifulSubsetsOptimized(test14, k14)));
        // Expected: true

        // Test Case 15: Maximum constraint
        System.out.println("Test 15: " + solution.beautifulSubsets(new int[] { 10, 20, 30, 40 }, 10));
        // Expected: count with k=10
    }
}
