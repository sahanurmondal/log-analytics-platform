package arrays.medium;

import java.util.*;

/**
 * LeetCode 300: Longest Increasing Subsequence
 * https://leetcode.com/problems/longest-increasing-subsequence/
 *
 * Description:
 * Given an integer array nums, return the length of the longest strictly
 * increasing subsequence.
 * A subsequence is a sequence that can be derived from the array by deleting
 * some or no elements
 * without changing the order of the remaining elements.
 *
 * Constraints:
 * - 1 <= nums.length <= 2500
 * - -10^4 <= nums[i] <= 10^4
 *
 * Follow-up:
 * - Can you come up with O(n log n) solution?
 * 
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Use binary search with patience sorting approach
 * 2. Maintain array of smallest tail elements for each length
 * 3. For each element, find position using binary search and update
 */
public class LongestIncreasingSubsequence {
    public int lengthOfLIS(int[] nums) {
        List<Integer> tails = new ArrayList<>();

        for (int num : nums) {
            int pos = binarySearch(tails, num);
            if (pos == tails.size()) {
                tails.add(num);
            } else {
                tails.set(pos, num);
            }
        }

        return tails.size();
    }

    private int binarySearch(List<Integer> tails, int target) {
        int left = 0, right = tails.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (tails.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        LongestIncreasingSubsequence solution = new LongestIncreasingSubsequence();

        // Test Case 1: Normal case
        System.out.println(solution.lengthOfLIS(new int[] { 10, 9, 2, 5, 3, 7, 101, 18 })); // Expected: 4

        // Test Case 2: Edge case - all decreasing
        System.out.println(solution.lengthOfLIS(new int[] { 7, 7, 7, 7, 7, 7, 7 })); // Expected: 1

        // Test Case 3: Corner case - all increasing
        System.out.println(solution.lengthOfLIS(new int[] { 1, 2, 3, 4, 5 })); // Expected: 5

        // Test Case 4: Large input - random order
        System.out.println(solution.lengthOfLIS(new int[] { 0, 1, 0, 3, 2, 3 })); // Expected: 4

        // Test Case 5: Minimum input - single element
        System.out.println(solution.lengthOfLIS(new int[] { 1 })); // Expected: 1

        // Test Case 6: Special case - two elements increasing
        System.out.println(solution.lengthOfLIS(new int[] { 1, 3 })); // Expected: 2

        // Test Case 7: Boundary case - two elements decreasing
        System.out.println(solution.lengthOfLIS(new int[] { 3, 1 })); // Expected: 1

        // Test Case 8: Negative numbers
        System.out.println(solution.lengthOfLIS(new int[] { -1, -2, 0, 1 })); // Expected: 3

        // Test Case 9: Duplicates with increasing
        System.out.println(solution.lengthOfLIS(new int[] { 1, 3, 6, 7, 9, 4, 10, 5, 6 })); // Expected: 6

        // Test Case 10: All same elements
        System.out.println(solution.lengthOfLIS(new int[] { 5, 5, 5, 5 })); // Expected: 1
    }
}
