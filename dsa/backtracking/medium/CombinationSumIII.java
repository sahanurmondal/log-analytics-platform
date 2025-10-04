package backtracking.medium;

import java.util.*;

/**
 * LeetCode 216: Combination Sum III
 * https://leetcode.com/problems/combination-sum-iii/
 *
 * Description: Find all valid combinations of k numbers that sum up to n such
 * that:
 * - Only numbers 1 through 9 are used
 * - Each number is used at most once
 * 
 * Constraints:
 * - 2 <= k <= 9
 * - 1 <= n <= 60
 *
 * Follow-up:
 * - Can you optimize with early pruning?
 * - What if we allow larger numbers?
 * 
 * Time Complexity: O(C(9,k))
 * Space Complexity: O(k)
 * 
 * Algorithm:
 * 1. Backtracking: Choose k numbers from 1-9
 * 2. Pruning: Early termination for impossible cases
 * 3. Constraint checking: Sum equals n, exactly k numbers
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class CombinationSumIII {

    // Main optimized solution - Backtracking with pruning
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(k, n, 1, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int k, int target, int start, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) {
            if (target == 0) {
                result.add(new ArrayList<>(current));
            }
            return;
        }

        // Pruning optimizations
        int remaining = k - current.size();
        if (target < 0)
            return;
        if (remaining > 9 - start + 1)
            return; // Not enough numbers left
        if (target > remaining * 9)
            return; // Target too large
        if (target < remaining * start)
            return; // Target too small

        for (int i = start; i <= 9; i++) {
            current.add(i);
            backtrack(k, target - i, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Alternative solution - Without early pruning (simpler)
    public List<List<Integer>> combinationSum3Simple(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSimple(k, n, 1, new ArrayList<>(), result);
        return result;
    }

    private void backtrackSimple(int k, int target, int start, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (current.size() >= k || target <= 0)
            return;

        for (int i = start; i <= 9; i++) {
            current.add(i);
            backtrackSimple(k, target - i, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Follow-up optimization - Iterative approach
    public List<List<Integer>> combinationSum3Iterative(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();

        // Use bit manipulation to generate all k-combinations
        for (int mask = 0; mask < (1 << 9); mask++) {
            if (Integer.bitCount(mask) == k) {
                List<Integer> combination = new ArrayList<>();
                int sum = 0;

                for (int i = 0; i < 9; i++) {
                    if ((mask & (1 << i)) != 0) {
                        combination.add(i + 1);
                        sum += i + 1;
                    }
                }

                if (sum == n) {
                    result.add(combination);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        CombinationSumIII solution = new CombinationSumIII();

        // Test Case 1: Normal case
        System.out.println(solution.combinationSum3(3, 7)); // Expected: [[1,2,4]]

        // Test Case 2: Multiple solutions
        System.out.println(solution.combinationSum3(3, 9)); // Expected: [[1,2,6],[1,3,5],[2,3,4]]

        // Test Case 3: No solution
        System.out.println(solution.combinationSum3(4, 1)); // Expected: []

        // Test Case 4: Minimum k
        System.out.println(solution.combinationSum3(2, 3)); // Expected: [[1,2]]

        // Test Case 5: Large target
        System.out.println(solution.combinationSum3(9, 45)); // Expected: [[1,2,3,4,5,6,7,8,9]]

        // Test Case 6: Impossible case - target too large
        System.out.println(solution.combinationSum3(3, 28)); // Expected: []

        // Test Case 7: Impossible case - target too small
        System.out.println(solution.combinationSum3(5, 5)); // Expected: []

        // Test Case 8: Test simple approach
        System.out.println(solution.combinationSum3Simple(3, 9).size()); // Expected: 3

        // Test Case 9: Test iterative approach
        System.out.println(solution.combinationSum3Iterative(3, 7)); // Expected: [[1,2,4]]

        // Test Case 10: Edge case
        System.out.println(solution.combinationSum3(2, 18)); // Expected: [[9,9]] - not valid since no duplicates
    }
}
