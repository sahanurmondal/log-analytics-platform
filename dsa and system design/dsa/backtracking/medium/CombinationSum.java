package backtracking.medium;

import java.util.*;

/**
 * LeetCode 39: Combination Sum
 * https://leetcode.com/problems/combination-sum/
 *
 * Description: Given an array of distinct integers candidates and a target
 * integer target,
 * return a list of all unique combinations of candidates where the chosen
 * numbers sum to target.
 * 
 * Constraints:
 * - 1 <= candidates.length <= 30
 * - 1 <= candidates[i] <= 200
 * - All elements of candidates are distinct
 * - 1 <= target <= 500
 *
 * Follow-up:
 * - What if candidates have duplicates?
 * - Can you optimize by sorting first?
 * 
 * Time Complexity: O(N^(T/M)) where N = candidates.length, T = target, M =
 * minimal value
 * Space Complexity: O(T/M)
 * 
 * Algorithm:
 * 1. Backtracking: Try each candidate, allow reuse
 * 2. Optimized: Sort first and prune early
 * 3. Iterative: Use stack to simulate recursion
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class CombinationSum {

    // Main optimized solution - Backtracking
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates); // Sort for optimization
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
                break; // Pruning

            current.add(candidates[i]);
            backtrack(candidates, target - candidates[i], i, current, result); // i not i+1 because reuse allowed
            current.remove(current.size() - 1);
        }
    }

    // Alternative solution - Without sorting
    public List<List<Integer>> combinationSumUnsorted(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackUnsorted(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrackUnsorted(int[] candidates, int target, int start, List<Integer> current,
            List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (target < 0)
            return;

        for (int i = start; i < candidates.length; i++) {
            current.add(candidates[i]);
            backtrackUnsorted(candidates, target - candidates[i], i, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Follow-up optimization - Iterative approach
    public List<List<Integer>> combinationSumIterative(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Stack<State> stack = new Stack<>();
        Arrays.sort(candidates);

        stack.push(new State(0, target, new ArrayList<>()));

        while (!stack.isEmpty()) {
            State state = stack.pop();

            if (state.target == 0) {
                result.add(new ArrayList<>(state.current));
                continue;
            }

            for (int i = state.start; i < candidates.length; i++) {
                if (candidates[i] > state.target)
                    break;

                List<Integer> newCurrent = new ArrayList<>(state.current);
                newCurrent.add(candidates[i]);
                stack.push(new State(i, state.target - candidates[i], newCurrent));
            }
        }

        return result;
    }

    static class State {
        int start, target;
        List<Integer> current;

        State(int start, int target, List<Integer> current) {
            this.start = start;
            this.target = target;
            this.current = current;
        }
    }

    public static void main(String[] args) {
        CombinationSum solution = new CombinationSum();

        // Test Case 1: Normal case
        System.out.println(solution.combinationSum(new int[] { 2, 3, 6, 7 }, 7)); // Expected: [[2,2,3],[7]]

        // Test Case 2: Multiple solutions
        System.out.println(solution.combinationSum(new int[] { 2, 3, 5 }, 8)); // Expected: [[2,2,2,2],[2,3,3],[3,5]]

        // Test Case 3: No solution
        System.out.println(solution.combinationSum(new int[] { 2 }, 1)); // Expected: []

        // Test Case 4: Single element solution
        System.out.println(solution.combinationSum(new int[] { 1 }, 1)); // Expected: [[1]]

        // Test Case 5: Large numbers
        System.out.println(solution.combinationSum(new int[] { 1 }, 2)); // Expected: [[1,1]]

        // Test Case 6: Multiple same elements needed
        System.out.println(solution.combinationSum(new int[] { 2, 3, 6, 7 }, 8)); // Expected: [[2,2,2,2],[2,3,3],[2,6]]

        // Test Case 7: Edge case - exact match
        System.out.println(solution.combinationSum(new int[] { 5 }, 5)); // Expected: [[5]]

        // Test Case 8: Complex case
        System.out.println(solution.combinationSum(new int[] { 1, 2, 3 }, 4)); // Expected:
                                                                               // [[1,1,1,1],[1,1,2],[1,3],[2,2]]

        // Test Case 9: Large target
        System.out.println(solution.combinationSum(new int[] { 1, 2 }, 4).size()); // Expected: 3

        // Test Case 10: Test iterative approach
        System.out.println(solution.combinationSumIterative(new int[] { 2, 3, 6, 7 }, 7).size()); // Expected: 2
    }
}
