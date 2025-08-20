package dp.easy;

import java.util.*;

/**
 * LeetCode 119: Pascal's Triangle II
 * https://leetcode.com/problems/pascals-triangle-ii/
 *
 * Description:
 * Given an integer rowIndex, return the rowIndexth (0-indexed) row of the
 * Pascal's triangle.
 * In Pascal's triangle, each number is the sum of the two numbers directly
 * above it.
 *
 * Constraints:
 * - 0 <= rowIndex <= 33
 *
 * Follow-up: Could you optimize your algorithm to use only O(rowIndex) extra
 * space?
 *
 * Company Tags: Google, Amazon
 * Difficulty: Easy
 */
public class PascalsTriangleII {

    // Approach 1: Space Optimized DP - O(rowIndex^2) time, O(rowIndex) space
    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        row.add(1);

        for (int i = 1; i <= rowIndex; i++) {
            for (int j = i - 1; j >= 1; j--) {
                row.set(j, row.get(j) + row.get(j - 1));
            }
            row.add(1);
        }

        return row;
    }

    // Approach 2: Mathematical Formula - O(rowIndex) time, O(rowIndex) space
    public List<Integer> getRowMath(int rowIndex) {
        List<Integer> row = new ArrayList<>();

        for (int i = 0; i <= rowIndex; i++) {
            row.add(combination(rowIndex, i));
        }

        return row;
    }

    private int combination(int n, int k) {
        long result = 1;

        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }

        return (int) result;
    }

    public static void main(String[] args) {
        PascalsTriangleII solution = new PascalsTriangleII();

        System.out.println("=== Pascal's Triangle II Test Cases ===");

        for (int rowIndex = 0; rowIndex <= 5; rowIndex++) {
            System.out.println("Row " + rowIndex + ": " + solution.getRow(rowIndex));
        }
    }
}
