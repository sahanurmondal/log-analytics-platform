package matrix.medium;

import java.util.*;

/**
 * LeetCode 1277: Count Square Submatrices with All Ones
 * https://leetcode.com/problems/count-square-submatrices-with-all-ones/
 *
 * Description:
 * Given a m * n matrix of ones and zeros, return how many square submatrices
 * have all ones.
 *
 * Constraints:
 * - 1 <= arr.length <= 300
 * - 1 <= arr[0].length <= 300
 * - 0 <= arr[i][j] <= 1
 */
public class CountSquareSubmatrices {
    public int countSquares(int[][] matrix) {
        // DP solution
        int m = matrix.length, n = matrix[0].length, count = 0;
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1;
                    } else {
                        dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                    }
                    count += dp[i][j];
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        CountSquareSubmatrices solution = new CountSquareSubmatrices();
        System.out.println(solution.countSquares(new int[][] { { 0, 1, 1, 1 }, { 1, 1, 1, 1 }, { 0, 1, 1, 1 } })); // 15
        System.out.println(solution.countSquares(new int[][] { { 1, 0, 1 }, { 1, 1, 0 }, { 1, 1, 0 } })); // 7
        // Edge Case: All zeros
        System.out.println(solution.countSquares(new int[][] { { 0, 0 }, { 0, 0 } })); // 0
        // Edge Case: All ones
        System.out.println(solution.countSquares(new int[][] { { 1, 1 }, { 1, 1 } })); // 6
    }
}
