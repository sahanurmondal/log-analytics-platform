package dp.easy;

import java.util.*;

/**
 * LeetCode 118: Pascal's Triangle
 * https://leetcode.com/problems/pascals-triangle/
 *
 * Description:
 * Given an integer numRows, return the first numRows of Pascal's triangle.
 * In Pascal's triangle, each number is the sum of the two numbers directly
 * above it.
 *
 * Constraints:
 * - 1 <= numRows <= 30
 *
 * Company Tags: Google, Amazon, Microsoft
 * Difficulty: Easy
 */
public class PascalsTriangle {

    // Approach 1: DP - O(numRows^2) time, O(numRows^2) space
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> triangle = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>();

            for (int j = 0; j <= i; j++) {
                if (j == 0 || j == i) {
                    row.add(1);
                } else {
                    int val = triangle.get(i - 1).get(j - 1) + triangle.get(i - 1).get(j);
                    row.add(val);
                }
            }

            triangle.add(row);
        }

        return triangle;
    }

    // Approach 2: Mathematical Formula - O(numRows^2) time, O(numRows^2) space
    public List<List<Integer>> generateMath(int numRows) {
        List<List<Integer>> triangle = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>();

            for (int j = 0; j <= i; j++) {
                row.add(combination(i, j));
            }

            triangle.add(row);
        }

        return triangle;
    }

    private int combination(int n, int k) {
        long result = 1;

        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }

        return (int) result;
    }

    // Approach 3: Space Optimized - O(numRows^2) time, O(numRows) space
    public List<List<Integer>> generateOptimized(int numRows) {
        List<List<Integer>> triangle = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>();
            row.add(1);

            for (int j = 1; j < i; j++) {
                int val = triangle.get(i - 1).get(j - 1) + triangle.get(i - 1).get(j);
                row.add(val);
            }

            if (i > 0)
                row.add(1);
            triangle.add(row);
        }

        return triangle;
    }

    public static void main(String[] args) {
        PascalsTriangle solution = new PascalsTriangle();

        System.out.println("=== Pascal's Triangle Test Cases ===");

        int numRows = 5;
        System.out.println("numRows = " + numRows);

        List<List<Integer>> triangle = solution.generate(numRows);
        for (List<Integer> row : triangle) {
            System.out.println(row);
        }
    }
}
