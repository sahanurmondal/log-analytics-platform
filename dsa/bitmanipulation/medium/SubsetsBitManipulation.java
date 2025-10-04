package bitmanipulation.medium;

import java.util.*;

/**
 * LeetCode 78: Subsets (Bit Manipulation Approach)
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
 * - Can you solve it using bit manipulation?
 * - What about the relationship with binary numbers?
 * 
 * Time Complexity: O(N * 2^N)
 * Space Complexity: O(N * 2^N)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class SubsetsBitManipulation {

    // Main optimized solution - Bit manipulation
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;

        // Generate all possible subsets using bit manipulation
        for (int i = 0; i < (1 << n); i++) {
            List<Integer> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                // Check if j-th bit is set in i
                if ((i & (1 << j)) != 0) {
                    subset.add(nums[j]);
                }
            }
            result.add(subset);
        }

        return result;
    }

    // Alternative solution - Cascading approach
    public List<List<Integer>> subsetsCascading(int[] nums) {
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

    public static void main(String[] args) {
        SubsetsBitManipulation solution = new SubsetsBitManipulation();

        System.out.println(solution.subsets(new int[] { 1, 2, 3 })); // Expected:
                                                                     // [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
        System.out.println(solution.subsets(new int[] { 0 })); // Expected: [[],[0]]
        System.out.println(solution.subsetsCascading(new int[] { 1, 2 })); // Expected: [[],[1],[2],[1,2]]
    }
}
