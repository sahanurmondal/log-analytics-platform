package backtracking.hard;

import java.util.*;

/**
 * Variation: Combination Sum (Hard Variant)
 * URL: https://leetcode.com/problems/combination-sum-iv/ (Modified)
 * Difficulty: Hard
 * Companies: Google, Facebook, Amazon, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Given an array of positive integers nums and a target, return all unique
 * combinations where the chosen numbers sum to target. Each number may be used
 * at most k times. Also includes various hard variants.
 *
 * Constraints:
 * - 1 <= nums.length <= 20
 * - 1 <= nums[i] <= 30
 * - 1 <= target <= 100
 * - 1 <= k <= 10
 *
 * Follow-up Questions:
 * 1. Can you solve it recursively?
 * 2. Can you generalize for at most k times?
 * 3. Can you optimize with DP?
 * 4. What about finding combinations with minimum elements?
 */
public class CombinationSumHard {

    // Approach 1: Backtracking with k constraint - O(k^n)
    public List<List<Integer>> combinationSumK(int[] nums, int target, int k) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        Map<Integer, Integer> frequency = new HashMap<>();
        for (int num : nums) {
            frequency.put(num, k);
        }
        backtrackK(nums, target, 0, frequency, new ArrayList<>(), result);
        return result;
    }

    private void backtrackK(int[] nums, int target, int start, Map<Integer, Integer> frequency,
            List<Integer> current, List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1])
                continue; // Skip duplicates
            if (nums[i] > target)
                break; // Pruning
            if (frequency.get(nums[i]) > 0) {
                current.add(nums[i]);
                frequency.put(nums[i], frequency.get(nums[i]) - 1);
                backtrackK(nums, target - nums[i], i, frequency, current, result);
                frequency.put(nums[i], frequency.get(nums[i]) + 1);
                current.remove(current.size() - 1);
            }
        }
    }

    // Approach 2: Combination Sum with minimum elements - O(2^n)
    public List<Integer> combinationSumMinElements(int[] nums, int target) {
        Arrays.sort(nums);
        List<Integer> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        if (findMinCombination(nums, target, 0, current, result)) {
            return result;
        }
        return new ArrayList<>();
    }

    private boolean findMinCombination(int[] nums, int target, int start,
            List<Integer> current, List<Integer> result) {
        if (target == 0) {
            if (result.isEmpty() || current.size() < result.size()) {
                result.clear();
                result.addAll(current);
                return current.size() == 1; // Early termination if size 1
            }
            return false;
        }

        for (int i = start; i < nums.length; i++) {
            if (nums[i] > target)
                break;
            if (!result.isEmpty() && current.size() >= result.size())
                continue; // Pruning

            current.add(nums[i]);
            if (findMinCombination(nums, target - nums[i], i, current, result)) {
                return true;
            }
            current.remove(current.size() - 1);
        }
        return false;
    }

    // Approach 3: Count combinations with DP - O(target * n)
    public int combinationSumCount(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1;

        for (int num : nums) {
            for (int i = num; i <= target; i++) {
                dp[i] += dp[i - num];
            }
        }

        return dp[target];
    }

    // Approach 4: Combination sum with exact k elements - O(C(n,k))
    public List<List<Integer>> combinationSumExactK(int[] nums, int target, int k) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        backtrackExactK(nums, target, k, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrackExactK(int[] nums, int target, int k, int start,
            List<Integer> current, List<List<Integer>> result) {
        if (k == 0) {
            if (target == 0) {
                result.add(new ArrayList<>(current));
            }
            return;
        }

        if (target <= 0)
            return;

        for (int i = start; i < nums.length; i++) {
            if (nums[i] > target)
                break;

            current.add(nums[i]);
            backtrackExactK(nums, target - nums[i], k - 1, i, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Approach 5: Combination sum with distinct elements only
    public List<List<Integer>> combinationSumDistinct(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        backtrackDistinct(nums, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrackDistinct(int[] nums, int target, int start,
            List<Integer> current, List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < nums.length; i++) {
            if (nums[i] > target)
                break;

            current.add(nums[i]);
            backtrackDistinct(nums, target - nums[i], i + 1, current, result); // i+1 for distinct
            current.remove(current.size() - 1);
        }
    }

    // Helper method to validate combination
    public boolean isValidCombination(List<Integer> combination, int target) {
        return combination.stream().mapToInt(Integer::intValue).sum() == target;
    }

    public static void main(String[] args) {
        CombinationSumHard solution = new CombinationSumHard();

        // Test Case 1: Basic k constraint
        System.out.println("Test 1: " + solution.combinationSumK(new int[] { 2, 3, 6, 7 }, 7, 2));
        // Expected: combinations using each number at most 2 times

        // Test Case 2: Minimum elements
        System.out.println("Test 2: " + solution.combinationSumMinElements(new int[] { 2, 3, 5 }, 8));
        // Expected: [3, 5] (minimum elements)

        // Test Case 3: Count combinations
        System.out.println("Test 3: " + solution.combinationSumCount(new int[] { 1, 2, 3 }, 4));
        // Expected: 7 (1+1+1+1, 1+1+2, 1+2+1, 2+1+1, 2+2, 1+3, 3+1)

        // Test Case 4: Exact k elements
        System.out.println("Test 4: " + solution.combinationSumExactK(new int[] { 2, 3, 6, 7 }, 7, 1));
        // Expected: [[7]]

        // Test Case 5: Distinct elements only
        System.out.println("Test 5: " + solution.combinationSumDistinct(new int[] { 2, 3, 6, 7 }, 7));
        // Expected: [[7]]

        // Test Case 6: No solution
        System.out.println("Test 6: " + solution.combinationSumK(new int[] { 3, 5 }, 1, 1));
        // Expected: []

        // Test Case 7: Single element
        System.out.println("Test 7: " + solution.combinationSumK(new int[] { 2 }, 6, 3));
        // Expected: [[2,2,2]]

        // Test Case 8: Validate combination
        System.out.println("Test 8: " + solution.isValidCombination(Arrays.asList(2, 3, 2), 7));
        // Expected: true

        // Test Case 9: Large k constraint
        System.out.println("Test 9: " + solution.combinationSumK(new int[] { 1, 2 }, 3, 5).size());
        // Expected: number of valid combinations

        // Test Case 10: Count with larger array
        System.out.println("Test 10: " + solution.combinationSumCount(new int[] { 1, 2, 5 }, 11));
        // Expected: count of combinations

        // Test Case 11: Exact 2 elements
        System.out.println("Test 11: " + solution.combinationSumExactK(new int[] { 1, 2, 3 }, 5, 2));
        // Expected: [[2,3]]

        // Test Case 12: Distinct with duplicates in array
        System.out.println("Test 12: " + solution.combinationSumDistinct(new int[] { 2, 2, 3 }, 7));
        // Expected: combinations without reusing same index

        // Test Case 13: Minimum elements edge case
        System.out.println("Test 13: " + solution.combinationSumMinElements(new int[] { 1 }, 5));
        // Expected: [1,1,1,1,1]

        // Test Case 14: K constraint boundary
        System.out.println("Test 14: " + solution.combinationSumK(new int[] { 2 }, 8, 4));
        // Expected: [[2,2,2,2]]

        // Test Case 15: Complex case
        System.out.println("Test 15: " + solution.combinationSumK(new int[] { 1, 2, 3, 4 }, 10, 3).size());
        // Expected: count of valid combinations with k=3 constraint
    }
}
