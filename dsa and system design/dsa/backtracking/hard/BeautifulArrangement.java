package backtracking.hard;

import java.util.*;

/**
 * LeetCode 526: Beautiful Arrangement
 * URL: https://leetcode.com/problems/beautiful-arrangement/
 * Difficulty: Hard
 * Companies: Google, Facebook, Microsoft, Apple
 * Frequency: Medium
 *
 * Description:
 * Given n, count the number of beautiful arrangements that you can construct.
 * A beautiful arrangement is an array nums of length n where:
 * - nums[i] is divisible by i, or
 * - i is divisible by nums[i]
 * where i is the position (1-indexed).
 *
 * Constraints:
 * - 1 <= n <= 15
 *
 * Follow-up Questions:
 * 1. Can you optimize using memoization?
 * 2. Can you solve it iteratively?
 * 3. What if we need to return all arrangements?
 * 4. How to handle larger constraints?
 */
public class BeautifulArrangement {

    // Approach 1: Backtracking - O(n!)
    public int countArrangement(int n) {
        boolean[] used = new boolean[n + 1];
        return backtrack(n, 1, used);
    }

    private int backtrack(int n, int position, boolean[] used) {
        if (position > n) {
            return 1; // Found a valid arrangement
        }

        int count = 0;
        for (int num = 1; num <= n; num++) {
            if (!used[num] && (num % position == 0 || position % num == 0)) {
                used[num] = true;
                count += backtrack(n, position + 1, used);
                used[num] = false;
            }
        }

        return count;
    }

    // Approach 2: Backtracking with memoization - O(2^n * n)
    public int countArrangementMemo(int n) {
        return backtrackMemo(n, 1, 0, new Integer[1 << n]);
    }

    private int backtrackMemo(int n, int position, int mask, Integer[] memo) {
        if (position > n) {
            return 1;
        }

        if (memo[mask] != null) {
            return memo[mask];
        }

        int count = 0;
        for (int num = 1; num <= n; num++) {
            if ((mask & (1 << (num - 1))) == 0 && (num % position == 0 || position % num == 0)) {
                count += backtrackMemo(n, position + 1, mask | (1 << (num - 1)), memo);
            }
        }

        return memo[mask] = count;
    }

    // Approach 3: Bottom-up from end - O(n!)
    public int countArrangementBottomUp(int n) {
        boolean[] used = new boolean[n + 1];
        return backtrackFromEnd(n, n, used);
    }

    private int backtrackFromEnd(int n, int position, boolean[] used) {
        if (position == 0) {
            return 1;
        }

        int count = 0;
        for (int num = 1; num <= n; num++) {
            if (!used[num] && (num % position == 0 || position % num == 0)) {
                used[num] = true;
                count += backtrackFromEnd(n, position - 1, used);
                used[num] = false;
            }
        }

        return count;
    }

    // Follow-up 3: Get all arrangements
    public List<List<Integer>> getAllArrangements(int n) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[n + 1];
        getAllArrangementsHelper(n, 1, used, new ArrayList<>(), result);
        return result;
    }

    private void getAllArrangementsHelper(int n, int position, boolean[] used,
            List<Integer> current, List<List<Integer>> result) {
        if (position > n) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int num = 1; num <= n; num++) {
            if (!used[num] && (num % position == 0 || position % num == 0)) {
                used[num] = true;
                current.add(num);
                getAllArrangementsHelper(n, position + 1, used, current, result);
                current.remove(current.size() - 1);
                used[num] = false;
            }
        }
    }

    // Helper method to check if arrangement is beautiful
    public boolean isBeautifulArrangement(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int position = i + 1;
            int num = nums[i];
            if (num % position != 0 && position % num != 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        BeautifulArrangement solution = new BeautifulArrangement();

        // Test Case 1: Basic example
        System.out.println("Test 1: " + solution.countArrangement(2)); // Expected: 2

        // Test Case 2: Single element
        System.out.println("Test 2: " + solution.countArrangement(1)); // Expected: 1

        // Test Case 3: Larger example
        System.out.println("Test 3: " + solution.countArrangement(3)); // Expected: 3

        // Test Case 4: Medium size
        System.out.println("Test 4: " + solution.countArrangement(4)); // Expected: 8

        // Test Case 5: Memoization approach
        System.out.println("Test 5 (Memo): " + solution.countArrangementMemo(4)); // Expected: 8

        // Test Case 6: Bottom-up approach
        System.out.println("Test 6 (Bottom-up): " + solution.countArrangementBottomUp(4)); // Expected: 8

        // Test Case 7: Get all arrangements
        System.out.println("Test 7 (All): " + solution.getAllArrangements(3));
        // Expected: [[2,1,3], [3,2,1], [2,4,1]]

        // Test Case 8: Check if arrangement is beautiful
        System.out.println("Test 8 (Check): " + solution.isBeautifulArrangement(new int[] { 2, 1, 3 })); // Expected:
                                                                                                         // true

        // Test Case 9: Invalid arrangement
        System.out.println("Test 9 (Invalid): " + solution.isBeautifulArrangement(new int[] { 1, 2, 3 })); // Expected:
                                                                                                           // false

        // Test Case 10: Large constraint
        System.out.println("Test 10: " + solution.countArrangement(5)); // Expected: 10

        // Test Case 11: Performance test with memoization
        long start = System.currentTimeMillis();
        int result11 = solution.countArrangementMemo(10);
        long end = System.currentTimeMillis();
        System.out.println("Test 11 (Performance): " + result11 + " in " + (end - start) + "ms");

        // Test Case 12: Edge case validation
        System.out.println("Test 12: " + solution.countArrangement(6)); // Expected: 36

        // Test Case 13: Verify bottom-up consistency
        System.out.println("Test 13 (Consistency): " +
                (solution.countArrangement(5) == solution.countArrangementBottomUp(5))); // Expected: true

        // Test Case 14: Maximum constraint
        System.out.println("Test 14: " + solution.countArrangement(8)); // Expected: large number

        // Test Case 15: All arrangements for small n
        System.out.println("Test 15: " + solution.getAllArrangements(2).size()); // Expected: 2
    }
}
