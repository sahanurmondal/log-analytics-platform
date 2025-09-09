package backtracking.medium;

import java.util.*;

/**
 * LeetCode 90: Subsets II
 * https://leetcode.com/problems/subsets-ii/
 *
 * Description: Given an integer array nums that may contain duplicates, return
 * all possible subsets (the power set).
 * The solution set must not contain duplicate subsets. Return the solution in
 * any order.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10
 * - -10 <= nums[i] <= 10
 *
 * Follow-up:
 * - How does this differ from Subsets I?
 * - Can you solve using bit manipulation?
 * 
 * Time Complexity: O(N * 2^N)
 * Space Complexity: O(N * 2^N)
 * 
 * Algorithm:
 * 1. Sort array to handle duplicates
 * 2. Skip duplicates at same recursion level
 * 3. Generate all unique subsets
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class SubsetsII {

    // Main optimized solution - Backtracking with duplicate handling
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            // Skip duplicates at same level
            if (i > start && nums[i] == nums[i - 1])
                continue;

            current.add(nums[i]);
            backtrack(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Alternative solution - Using Set to avoid duplicates
    public List<List<Integer>> subsetsWithDupSet(int[] nums) {
        Set<List<Integer>> uniqueSubsets = new HashSet<>();
        Arrays.sort(nums);
        backtrackSet(nums, 0, new ArrayList<>(), uniqueSubsets);
        return new ArrayList<>(uniqueSubsets);
    }

    private void backtrackSet(int[] nums, int start, List<Integer> current, Set<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrackSet(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Follow-up optimization - Iterative approach
    public List<List<Integer>> subsetsWithDupIterative(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        result.add(new ArrayList<>());

        int start = 0;
        for (int i = 0; i < nums.length; i++) {
            int size = result.size();

            // If current element is duplicate, only add to recently added subsets
            if (i > 0 && nums[i] == nums[i - 1]) {
                start = size / 2;
            } else {
                start = 0;
            }

            for (int j = start; j < size; j++) {
                List<Integer> subset = new ArrayList<>(result.get(j));
                subset.add(nums[i]);
                result.add(subset);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SubsetsII solution = new SubsetsII();

        // Test Case 1: Normal case with duplicates
        System.out.println(solution.subsetsWithDup(new int[] { 1, 2, 2 })); // Expected:
                                                                            // [[],[1],[1,2],[1,2,2],[2],[2,2]]

        // Test Case 2: All same elements
        System.out.println(solution.subsetsWithDup(new int[] { 1, 1, 1 })); // Expected: [[],[1],[1,1],[1,1,1]]

        // Test Case 3: No duplicates
        System.out.println(solution.subsetsWithDup(new int[] { 1, 2, 3 })); // Expected:
                                                                            // [[],[1],[1,2],[1,2,3],[1,3],[2],[2,3],[3]]

        // Test Case 4: Single element
        System.out.println(solution.subsetsWithDup(new int[] { 0 })); // Expected: [[],[0]]

        // Test Case 5: Multiple duplicates
        System.out.println(solution.subsetsWithDup(new int[] { 4, 4, 4, 1, 4 })); // Expected: unique subsets only

        // Test Case 6: Negative numbers
        System.out.println(solution.subsetsWithDup(new int[] { -1, -1, 0 })); // Expected: unique subsets

        // Test Case 7: Test iterative approach
        System.out.println(solution.subsetsWithDupIterative(new int[] { 1, 2, 2 }).size()); // Expected: 6

        // Test Case 8: Complex duplicates
        System.out.println(solution.subsetsWithDup(new int[] { 1, 2, 2, 3, 3, 3 })); // Expected: multiple unique
                                                                                     // subsets

        // Test Case 9: Edge values
        System.out.println(solution.subsetsWithDup(new int[] { -10, 10, -10 })); // Expected: unique subsets

        // Test Case 10: Large input
        System.out.println(solution.subsetsWithDup(new int[] { 1, 1, 2, 2, 3, 3, 4, 4 }).size()); // Expected:
                                                                                                  // calculated unique
                                                                                                  // count
    }
}
