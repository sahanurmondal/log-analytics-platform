package backtracking.medium;

import java.util.*;

/**
 * LeetCode 47: Permutations II
 * https://leetcode.com/problems/permutations-ii/
 *
 * Description: Given a collection of numbers, nums, that might contain
 * duplicates,
 * return all possible unique permutations in any order.
 * 
 * Constraints:
 * - 1 <= nums.length <= 8
 * - -10 <= nums[i] <= 10
 *
 * Follow-up:
 * - How to handle duplicates efficiently?
 * - Can you solve without sorting?
 * 
 * Time Complexity: O(N! * N)
 * Space Complexity: O(N)
 * 
 * Algorithm:
 * 1. Sort array to group duplicates
 * 2. Use visited array to track usage
 * 3. Skip duplicates when previous same element not used
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class PermutationsII {

    // Main optimized solution - Backtracking with duplicate handling
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        boolean[] used = new boolean[nums.length];
        backtrack(nums, new ArrayList<>(), used, result);
        return result;
    }

    private void backtrack(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i])
                continue;

            // Skip duplicates: if current element equals previous and previous not used
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1])
                continue;

            used[i] = true;
            current.add(nums[i]);
            backtrack(nums, current, used, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    // Alternative solution - Using frequency map
    public List<List<Integer>> permuteUniqueFrequency(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Map<Integer, Integer> frequency = new HashMap<>();

        for (int num : nums) {
            frequency.put(num, frequency.getOrDefault(num, 0) + 1);
        }

        backtrackFrequency(frequency, new ArrayList<>(), nums.length, result);
        return result;
    }

    private void backtrackFrequency(Map<Integer, Integer> frequency, List<Integer> current,
            int totalLength, List<List<Integer>> result) {
        if (current.size() == totalLength) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            int num = entry.getKey();
            int count = entry.getValue();

            if (count > 0) {
                current.add(num);
                frequency.put(num, count - 1);
                backtrackFrequency(frequency, current, totalLength, result);
                current.remove(current.size() - 1);
                frequency.put(num, count);
            }
        }
    }

    // Follow-up optimization - Using Set (less efficient but simpler)
    public List<List<Integer>> permuteUniqueSet(int[] nums) {
        Set<List<Integer>> uniquePerms = new HashSet<>();
        boolean[] used = new boolean[nums.length];
        backtrackSet(nums, new ArrayList<>(), used, uniquePerms);
        return new ArrayList<>(uniquePerms);
    }

    private void backtrackSet(int[] nums, List<Integer> current, boolean[] used, Set<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i])
                continue;

            used[i] = true;
            current.add(nums[i]);
            backtrackSet(nums, current, used, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    public static void main(String[] args) {
        PermutationsII solution = new PermutationsII();

        // Test Case 1: Normal case with duplicates
        System.out.println(solution.permuteUnique(new int[] { 1, 1, 2 })); // Expected: [[1,1,2],[1,2,1],[2,1,1]]

        // Test Case 2: All same elements
        System.out.println(solution.permuteUnique(new int[] { 1, 1, 1 })); // Expected: [[1,1,1]]

        // Test Case 3: No duplicates
        System.out.println(solution.permuteUnique(new int[] { 1, 2, 3 })); // Expected: 6 permutations

        // Test Case 4: Two pairs of duplicates
        System.out.println(solution.permuteUnique(new int[] { 1, 1, 2, 2 })); // Expected: 6 unique permutations

        // Test Case 5: Single element
        System.out.println(solution.permuteUnique(new int[] { 1 })); // Expected: [[1]]

        // Test Case 6: Negative numbers
        System.out.println(solution.permuteUnique(new int[] { -1, 0, -1 })); // Expected: unique permutations

        // Test Case 7: Test frequency approach
        System.out.println(solution.permuteUniqueFrequency(new int[] { 1, 1, 2 }).size()); // Expected: 3

        // Test Case 8: Many duplicates
        System.out.println(solution.permuteUnique(new int[] { 2, 2, 1, 1, 1 })); // Expected: calculated unique count

        // Test Case 9: Edge values
        System.out.println(solution.permuteUnique(new int[] { -10, 10, -10 })); // Expected: unique permutations

        // Test Case 10: Maximum constraint
        System.out.println(solution.permuteUnique(new int[] { 1, 1, 2, 2, 3, 3, 4, 4 }).size()); // Expected: calculated
                                                                                                 // count
    }
}
