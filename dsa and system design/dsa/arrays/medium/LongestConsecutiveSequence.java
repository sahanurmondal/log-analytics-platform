package arrays.medium;

import java.util.*;

/**
 * LeetCode 128: Longest Consecutive Sequence
 * https://leetcode.com/problems/longest-consecutive-sequence/
 *
 * Description:
 * Given an unsorted array of integers nums, return the length of the longest
 * consecutive elements sequence.
 * You must write an algorithm that runs in O(n) time.
 *
 * Constraints:
 * - 0 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Add all numbers to a HashSet for O(1) lookup
 * 2. For each number, check if it's the start of a sequence (no left neighbor)
 * 3. Count consecutive numbers from each sequence start
 */

// this will throw Time limit exception
public class LongestConsecutiveSequence {
    public int longestConsecutive(int[] nums) {
        if (nums.length == 0)
            return 0;

        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }

        int maxLength = 0;

        for (int num : numSet) {
            // Check if this is the start of a sequence
            if (!numSet.contains(num - 1)) {
                int currentNum = num;
                int currentLength = 1;

                // Count consecutive numbers
                while (numSet.contains(currentNum + 1)) {
                    currentNum++;
                    currentLength++;
                }

                maxLength = Math.max(maxLength, currentLength);
            }
        }

        return maxLength;
    }

    public int longestConsecutiveOptimized(int[] nums) {
        Set<Integer> set = new HashSet<>();
        if (nums.length == 0)
            return 0;
        for (int n : nums) {
            set.add(n);
        }

        int max = Integer.MIN_VALUE;
        for (int n : nums) {
            if (!set.contains(n - 1)) {
                int count = 0;
                // int val = n;
                while (set.remove(n++)) {
                    count++;
                }
                max = Math.max(max, count);
            }
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }

    public static void main(String[] args) {
        LongestConsecutiveSequence solution = new LongestConsecutiveSequence();

        // Test Case 1: Normal case
        System.out.println(solution.longestConsecutive(new int[] { 100, 4, 200, 1, 3, 2 })); // Expected: 4

        // Test Case 2: Edge case - duplicates
        System.out.println(solution.longestConsecutive(new int[] { 0, 3, 7, 2, 5, 8, 4, 6, 0, 1 })); // Expected: 9

        // Test Case 3: Corner case - empty array
        System.out.println(solution.longestConsecutive(new int[] {})); // Expected: 0

        // Test Case 4: Large input - all consecutive
        System.out.println(solution.longestConsecutive(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 })); // Expected: 10

        // Test Case 5: Minimum input - single element
        System.out.println(solution.longestConsecutive(new int[] { 1 })); // Expected: 1

        // Test Case 6: Special case - no consecutive
        System.out.println(solution.longestConsecutive(new int[] { 1, 3, 5, 7, 9 })); // Expected: 1

        // Test Case 7: Boundary case - two elements consecutive
        System.out.println(solution.longestConsecutive(new int[] { 1, 2 })); // Expected: 2

        // Test Case 8: Negative numbers
        System.out.println(solution.longestConsecutive(new int[] { -1, -2, -3, 1, 2, 3 })); // Expected: 3

        // Test Case 9: Duplicates
        System.out.println(solution.longestConsecutive(new int[] { 1, 1, 1, 1 })); // Expected: 1

        // Test Case 10: Mixed positive/negative
        System.out.println(solution.longestConsecutive(new int[] { -1, 0, 1, 2 })); // Expected: 4
    }
}
