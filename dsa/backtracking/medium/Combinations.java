package backtracking.medium;

import java.util.*;

/**
 * LeetCode 77: Combinations
 * https://leetcode.com/problems/combinations/
 *
 * Description: Given two integers n and k, return all possible combinations of
 * k numbers out of the range [1, n].
 * You may return the answer in any order.
 * 
 * Constraints:
 * - 1 <= n <= 20
 * - 1 <= k <= n
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * - What about using Pascal's triangle approach?
 * 
 * Time Complexity: O(C(n,k) * k) = O(n! / (k!(n-k)!) * k)
 * Space Complexity: O(k)
 * 
 * Algorithm:
 * 1. Backtracking: Choose k numbers from 1 to n
 * 2. Iterative: Build combinations level by level
 * 3. Lexicographic: Generate in lexicographic order
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class Combinations {

    // Main optimized solution - Backtracking
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(n, k, 1, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int n, int k, int start, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        // Pruning: if remaining numbers can't fill the combination
        int remaining = k - current.size();
        for (int i = start; i <= n - remaining + 1; i++) {
            current.add(i);
            backtrack(n, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Alternative solution - Iterative
    public List<List<Integer>> combineIterative(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (k == 0) {
            result.add(new ArrayList<>());
            return result;
        }

        result.add(new ArrayList<>());

        for (int i = 1; i <= n; i++) {
            List<List<Integer>> newResult = new ArrayList<>();

            for (List<Integer> combination : result) {
                if (combination.size() < k) {
                    List<Integer> newCombination = new ArrayList<>(combination);
                    newCombination.add(i);
                    newResult.add(newCombination);
                }

                if (combination.size() < k && n - i >= k - combination.size() - 1) {
                    newResult.add(new ArrayList<>(combination));
                }
            }

            result = newResult;
        }

        return result.stream().filter(list -> list.size() == k).collect(ArrayList::new, List::add, List::addAll);
    }

    // Follow-up optimization - Lexicographic generation
    public List<List<Integer>> combineLexicographic(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();

        // Initialize first combination [1, 2, ..., k]
        List<Integer> current = new ArrayList<>();
        for (int i = 1; i <= k; i++) {
            current.add(i);
        }

        while (true) {
            result.add(new ArrayList<>(current));

            // Find the rightmost element that can be incremented
            int i = k - 1;
            while (i >= 0 && current.get(i) == n - k + i + 1) {
                i--;
            }

            if (i < 0)
                break; // No more combinations

            // Increment current[i] and reset all elements to its right
            current.set(i, current.get(i) + 1);
            for (int j = i + 1; j < k; j++) {
                current.set(j, current.get(i) + j - i);
            }
        }

        return result;
    }

    // Alternative approach - Using bit manipulation concept
    public List<List<Integer>> combineBitMask(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();

        // Generate all possible bitmasks with k bits set
        generateCombinations(n, k, 0, 0, new ArrayList<>(), result);
        return result;
    }

    private void generateCombinations(int n, int k, int pos, int count, List<Integer> current,
            List<List<Integer>> result) {
        if (count == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (pos > n)
            return;

        // Include current position
        current.add(pos + 1);
        generateCombinations(n, k, pos + 1, count + 1, current, result);
        current.remove(current.size() - 1);

        // Exclude current position (with pruning)
        if (n - pos > k - count) {
            generateCombinations(n, k, pos + 1, count, current, result);
        }
    }

    public static void main(String[] args) {
        Combinations solution = new Combinations();

        // Test Case 1: Normal case
        System.out.println(solution.combine(4, 2)); // Expected: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]

        // Test Case 2: k = 1
        System.out.println(solution.combine(3, 1)); // Expected: [[1],[2],[3]]

        // Test Case 3: k = n
        System.out.println(solution.combine(3, 3)); // Expected: [[1,2,3]]

        // Test Case 4: Small case
        System.out.println(solution.combine(2, 1)); // Expected: [[1],[2]]

        // Test Case 5: Edge case
        System.out.println(solution.combine(1, 1)); // Expected: [[1]]

        // Test Case 6: Larger case
        System.out.println(solution.combine(5, 3).size()); // Expected: 10 (C(5,3))

        // Test Case 7: Test lexicographic approach
        System.out.println(solution.combineLexicographic(4, 2).size()); // Expected: 6

        // Test Case 8: Medium case
        System.out.println(solution.combine(6, 2).size()); // Expected: 15 (C(6,2))

        // Test Case 9: Half selection
        System.out.println(solution.combine(6, 3).size()); // Expected: 20 (C(6,3))

        // Test Case 10: Large case
        System.out.println(solution.combine(10, 5).size()); // Expected: 252 (C(10,5))
    }
}
