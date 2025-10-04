package backtracking.medium;

import java.util.*;

/**
 * LeetCode 78: Subsets
 * https://leetcode.com/problems/subsets/
 *
 * Description: Given an integer array nums of unique elements, return all
 * possible subsets (the power set).
 * The solution set must not contain duplicate subsets. Return the solution in
 * any order.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10
 * - -10 <= nums[i] <= 10
 * - All the numbers of nums are unique
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * - What about using bit manipulation?
 * 
 * Time Complexity: O(N * 2^N)
 * Space Complexity: O(N * 2^N)
 * 
 * Algorithm:
 * 1. Backtracking: Include/exclude each element
 * 2. Iterative: Build subsets incrementally
 * 3. Bit Manipulation: Use binary representation
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class Subsets {

    // Main optimized solution - Backtracking
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrack(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Alternative solution - Iterative
    public List<List<Integer>> subsetsIterative(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        for (int num : nums) {
            int size = result.size();
            for (int i = 0; i < size; i++) {
                List<Integer> subset = new ArrayList<>(result.get(i));
                subset.add(num);
                result.add(subset);
            }
        }

        return result;
    }

    // Follow-up optimization - Bit Manipulation
    public List<List<Integer>> subsetsBitManipulation(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;

        for (int i = 0; i < (1 << n); i++) {
            List<Integer> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) != 0) {
                    subset.add(nums[j]);
                }
            }
            result.add(subset);
        }

        return result;
    }

    // Alternative approach - Lexicographic order
    public List<List<Integer>> subsetsLexicographic(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        generateLexicographic(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void generateLexicographic(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            generateLexicographic(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        Subsets solution = new Subsets();

        // Test Case 1: Normal case
        System.out.println(solution.subsets(new int[] { 1, 2, 3 })); // Expected:
                                                                     // [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]

        // Test Case 2: Single element
        System.out.println(solution.subsets(new int[] { 0 })); // Expected: [[],[0]]

        // Test Case 3: Two elements
        System.out.println(solution.subsets(new int[] { 1, 2 })); // Expected: [[],[1],[2],[1,2]]

        // Test Case 4: Negative numbers
        System.out.println(solution.subsets(new int[] { -1, 0, 1 })); // Expected: 8 subsets

        // Test Case 5: Test iterative approach
        System.out.println(solution.subsetsIterative(new int[] { 1, 2 }).size()); // Expected: 4

        // Test Case 6: Test bit manipulation
        System.out.println(solution.subsetsBitManipulation(new int[] { 1, 2, 3 }).size()); // Expected: 8

        // Test Case 7: Four elements
        System.out.println(solution.subsets(new int[] { 1, 2, 3, 4 }).size()); // Expected: 16

        // Test Case 8: Edge values
        System.out.println(solution.subsets(new int[] { -10, 10 })); // Expected: [[],[-10],[10],[-10,10]]

        // Test Case 9: Large input
        System.out.println(solution.subsets(new int[] { 1, 2, 3, 4, 5 }).size()); // Expected: 32

        // Test Case 10: Maximum constraint
        System.out.println(solution.subsets(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).size()); // Expected: 1024
    }
}
