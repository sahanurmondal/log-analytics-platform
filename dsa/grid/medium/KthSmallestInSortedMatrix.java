package grid.medium;

import java.util.*;

/**
 * LeetCode 378: Kth Smallest Element in a Sorted Matrix
 * https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/
 *
 * Description:
 * Given an n x n matrix where each of the rows and columns is sorted in
 * ascending order,
 * return the kth smallest element in the matrix.
 * Note that it is the kth smallest element in the sorted order, not the kth
 * distinct element.
 * You must find a solution with a memory complexity better than O(n^2).
 *
 * Constraints:
 * - n == matrix.length == matrix[i].length
 * - 1 <= n <= 300
 * - -10^9 <= matrix[i][j] <= 10^9
 * - All the rows and columns of matrix are guaranteed to be sorted in
 * non-decreasing order.
 * - 1 <= k <= n^2
 */
public class KthSmallestInSortedMatrix {

    public int kthSmallest(int[][] matrix, int k) {
        int n = matrix.length;
        int left = matrix[0][0];
        int right = matrix[n - 1][n - 1];

        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = countLessEqual(matrix, mid);

            if (count < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private int countLessEqual(int[][] matrix, int val) {
        int n = matrix.length;
        int count = 0;
        int row = n - 1;
        int col = 0;

        while (row >= 0 && col < n) {
            if (matrix[row][col] <= val) {
                count += row + 1;
                col++;
            } else {
                row--;
            }
        }

        return count;
    }

    // Alternative solution using min heap
    public int kthSmallestHeap(int[][] matrix, int k) {
        int n = matrix.length;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));

        // Add first element of each row
        for (int i = 0; i < n; i++) {
            pq.offer(new int[] { matrix[i][0], i, 0 });
        }

        for (int i = 0; i < k - 1; i++) {
            int[] curr = pq.poll();
            int row = curr[1];
            int col = curr[2];

            if (col + 1 < n) {
                pq.offer(new int[] { matrix[row][col + 1], row, col + 1 });
            }
        }

        return pq.poll()[0];
    }

    public static void main(String[] args) {
        KthSmallestInSortedMatrix solution = new KthSmallestInSortedMatrix();

        int[][] matrix = { { 1, 5, 9 }, { 10, 11, 13 }, { 12, 13, 15 } };
        System.out.println(solution.kthSmallest(matrix, 8)); // 13
        System.out.println(solution.kthSmallestHeap(matrix, 8)); // 13
    }
}
