package backtracking.medium;

import java.util.*;

/**
 * LeetCode 46: Permutations
 * https://leetcode.com/problems/permutations/
 *
 * Description: Given an array nums of distinct integers, return all the
 * possible permutations.
 * You can return the answer in any order.
 * 
 * Constraints:
 * - 1 <= nums.length <= 6
 * - -10 <= nums[i] <= 10
 * - All the integers of nums are unique
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * - What if there are duplicates?
 * - How to generate next permutation?
 * 
 * Time Complexity: O(n! * n)
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Backtracking: Choose, explore, unchoose
 * 2. Iterative: Build permutations level by level
 * 3. Heap's algorithm: Generate all permutations efficiently
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class Permutations {

    // Main optimized solution - Backtracking
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int num : nums) {
            if (current.contains(num))
                continue; // Skip used numbers

            current.add(num); // Choose
            backtrack(nums, current, result); // Explore
            current.remove(current.size() - 1); // Unchoose
        }
    }

    // Alternative solution - Using boolean array for visited
    public List<List<Integer>> permuteOptimized(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrackOptimized(nums, new ArrayList<>(), used, result);
        return result;
    }

    private void backtrackOptimized(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i])
                continue;

            used[i] = true;
            current.add(nums[i]);
            backtrackOptimized(nums, current, used, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    // Follow-up optimization - Iterative approach
    public List<List<Integer>> permuteIterative(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        for (int num : nums) {
            List<List<Integer>> newResult = new ArrayList<>();

            for (List<Integer> perm : result) {
                for (int i = 0; i <= perm.size(); i++) {
                    List<Integer> newPerm = new ArrayList<>(perm);
                    newPerm.add(i, num);
                    newResult.add(newPerm);
                }
            }

            result = newResult;
        }

        return result;
    }

    // Follow-up - Heap's Algorithm
    public List<List<Integer>> permuteHeaps(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        heapsAlgorithm(nums, nums.length, result);
        return result;
    }

    private void heapsAlgorithm(int[] nums, int k, List<List<Integer>> result) {
        if (k == 1) {
            result.add(Arrays.stream(nums).boxed().collect(ArrayList::new, List::add, List::addAll));
            return;
        }

        for (int i = 0; i < k; i++) {
            heapsAlgorithm(nums, k - 1, result);

            if (k % 2 == 1) {
                swap(nums, 0, k - 1);
            } else {
                swap(nums, i, k - 1);
            }
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        Permutations solution = new Permutations();

        // Test Case 1: Normal case
        System.out.println(solution.permute(new int[] { 1, 2, 3 })); // Expected:
                                                                     // [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]

        // Test Case 2: Two elements
        System.out.println(solution.permute(new int[] { 0, 1 })); // Expected: [[0,1],[1,0]]

        // Test Case 3: Single element
        System.out.println(solution.permute(new int[] { 1 })); // Expected: [[1]]

        // Test Case 4: Negative numbers
        System.out.println(solution.permute(new int[] { -1, 1, 0 })); // Expected: all permutations

        // Test Case 5: Four elements
        System.out.println(solution.permute(new int[] { 1, 2, 3, 4 }).size()); // Expected: 24

        // Test Case 6: Larger case
        System.out.println(solution.permute(new int[] { 1, 2, 3, 4, 5 }).size()); // Expected: 120

        // Test Case 7: Edge values
        System.out.println(solution.permute(new int[] { -10, 10 })); // Expected: [[-10,10],[10,-10]]

        // Test Case 8: Zero included
        System.out.println(solution.permute(new int[] { 0, 1, 2 })); // Expected: all permutations

        // Test Case 9: Test iterative approach
        System.out.println(solution.permuteIterative(new int[] { 1, 2 }).equals(solution.permute(new int[] { 1, 2 }))); // Expected:
                                                                                                                        // true
                                                                                                                        // (same
                                                                                                                        // result)

        // Test Case 10: Maximum constraint
        System.out.println(solution.permute(new int[] { 1, 2, 3, 4, 5, 6 }).size()); // Expected: 720
    }
}
