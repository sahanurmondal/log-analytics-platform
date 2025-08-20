package backtracking.medium;

import java.util.*;

/**
 * LeetCode 40: Combination Sum II
 * https://leetcode.com/problems/combination-sum-ii/
 *
 * Description: Given a collection of candidate numbers (candidates) and a
 * target number (target),
 * find all unique combinations in candidates where the candidate numbers sum to
 * target.
 * Each number in candidates may only be used once in the combination.
 * 
 * Constraints:
 * - 1 <= candidates.length <= 100
 * - 1 <= candidates[i] <= 50
 * - 1 <= target <= 30
 *
 * Follow-up:
 * - How does this differ from Combination Sum I?
 * - Can you optimize duplicate handling?
 * 
 * Time Complexity: O(2^n)
 * Space Complexity: O(target)
 * 
 * Algorithm:
 * 1. Sort array to handle duplicates
 * 2. Skip duplicates at same recursion level
 * 3. Each element used at most once
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class CombinationSumII {

    // Main optimized solution - Backtracking with duplicate handling
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int target, int start, List<Integer> current, List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > target)
                break;

            // Skip duplicates at same level
            if (i > start && candidates[i] == candidates[i - 1])
                continue;

            current.add(candidates[i]);
            backtrack(candidates, target - candidates[i], i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Alternative solution - Using Set to avoid duplicates
    public List<List<Integer>> combinationSum2Set(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        Set<List<Integer>> uniqueCombinations = new HashSet<>();
        backtrackSet(candidates, target, 0, new ArrayList<>(), uniqueCombinations);
        result.addAll(uniqueCombinations);
        return result;
    }

    private void backtrackSet(int[] candidates, int target, int start, List<Integer> current,
            Set<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > target)
                break;

            current.add(candidates[i]);
            backtrackSet(candidates, target - candidates[i], i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        CombinationSumII solution = new CombinationSumII();

        // Test Case 1: Normal case with duplicates
        System.out.println(solution.combinationSum2(new int[] { 10, 1, 2, 7, 6, 1, 5 }, 8)); // Expected:
                                                                                             // [[1,1,6],[1,2,5],[1,7],[2,6]]

        // Test Case 2: Multiple duplicates
        System.out.println(solution.combinationSum2(new int[] { 2, 5, 2, 1, 2 }, 5)); // Expected: [[1,2,2],[5]]

        // Test Case 3: No solution
        System.out.println(solution.combinationSum2(new int[] { 1, 2 }, 4)); // Expected: []

        // Test Case 4: Single element
        System.out.println(solution.combinationSum2(new int[] { 1 }, 1)); // Expected: [[1]]

        // Test Case 5: All same elements
        System.out.println(solution.combinationSum2(new int[] { 1, 1, 1, 1 }, 2)); // Expected: [[1,1]]

        // Test Case 6: Large target
        System.out.println(solution.combinationSum2(new int[] { 1, 2, 3, 4, 5 }, 5)); // Expected: [[1,4],[2,3],[5]]

        // Test Case 7: Complex duplicates
        System.out.println(solution.combinationSum2(new int[] { 1, 1, 2, 2, 3, 3 }, 6)); // Expected: multiple
                                                                                         // combinations

        // Test Case 8: Target equals single element
        System.out.println(solution.combinationSum2(new int[] { 5, 10, 15 }, 15)); // Expected: [[15]]

        // Test Case 9: Many small numbers
        System.out.println(solution.combinationSum2(new int[] { 1, 1, 1, 1, 1 }, 3)); // Expected: [[1,1,1]]

        // Test Case 10: Mixed values
        System.out.println(solution.combinationSum2(new int[] { 3, 1, 3, 5, 1, 1 }, 8)); // Expected: multiple
                                                                                         // combinations
    }
}
