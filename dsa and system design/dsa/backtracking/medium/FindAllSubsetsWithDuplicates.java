package backtracking.medium;

import java.util.*;

/**
 * LeetCode 90: Subsets II
 * https://leetcode.com/problems/subsets-ii/
 *
 * Description:
 * Given an integer array nums that may contain duplicates, return all possible
 * subsets (the power set).
 *
 * Constraints:
 * - 1 <= nums.length <= 10
 * - -10 <= nums[i] <= 10
 *
 * Follow-up:
 * - Can you solve it recursively?
 * - Can you generate subsets in lexicographical order?
 * - Can you solve it iteratively with O(n*2^n) space?
 */
public class FindAllSubsetsWithDuplicates {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // Sort to handle duplicates
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            // Skip duplicates
            if (i > start && nums[i] == nums[i - 1])
                continue;

            current.add(nums[i]);
            backtrack(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        FindAllSubsetsWithDuplicates solution = new FindAllSubsetsWithDuplicates();
        System.out.println(solution.subsetsWithDup(new int[] { 1, 2, 2 })); // [[],[1],[1,2],[1,2,2],[2],[2,2]]
        System.out.println(solution.subsetsWithDup(new int[] { 0 })); // [[],[0]]
        System.out.println(solution.subsetsWithDup(new int[] {})); // [[]]
        // Edge Case: All duplicates
        System.out.println(solution.subsetsWithDup(new int[] { 2, 2, 2 })); // [[...]]
        // Edge Case: Negative numbers
        System.out.println(solution.subsetsWithDup(new int[] { -1, 0, 1 })); // [[...]]
        // Edge Case: Large input
        int[] large = new int[10];
        for (int i = 0; i < 10; i++)
            large[i] = i % 2;
        System.out.println(solution.subsetsWithDup(large)); // [[...]]
    }
}
