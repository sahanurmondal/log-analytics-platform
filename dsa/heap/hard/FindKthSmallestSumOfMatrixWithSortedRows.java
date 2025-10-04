package heap.hard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * LeetCode 1439: Find the Kth Smallest Sum of a Matrix With Sorted Rows
 * https://leetcode.com/problems/find-the-kth-smallest-sum-of-a-matrix-with-sorted-rows/
 *
 * Description:
 * Given a m x n matrix with sorted rows, return the kth smallest sum of a row
 * selection.
 *
 * Constraints:
 * - 1 <= m, n <= 40
 * - 1 <= k <= min(200, n^m)
 * - 1 <= mat[i][j] <= 5000
 *
 * Follow-up:
 * - Can you solve it in O(k*m*n log k) time?
 */
public class FindKthSmallestSumOfMatrixWithSortedRows {
    /**
     * Finds the kth smallest sum of a matrix with sorted rows.
     * This method iteratively merges the rows, keeping only the k smallest sums at
     * each step.
     *
     * @param mat The m x n matrix with sorted rows.
     * @param k   The value of k.
     * @return The kth smallest sum.
     */
    public int kthSmallest(int[][] mat, int k) {
        List<Integer> sums = new ArrayList<>();
        sums.add(0);

        for (int[] row : mat) {
            sums = merge(sums, row, k);
        }

        return sums.get(k - 1);
    }

    /**
     * Merges a list of sums with a new row, returning the k smallest new sums.
     *
     * @param list1 The current list of sums.
     * @param list2 The new row to merge.
     * @param k     The number of smallest sums to keep.
     * @return A sorted list of the k smallest sums.
     */
    private List<Integer> merge(List<Integer> list1, int[] list2, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        for (int s1 : list1) {
            for (int s2 : list2) {
                int sum = s1 + s2;
                if (maxHeap.size() < k) {
                    maxHeap.offer(sum);
                } else if (sum < maxHeap.peek()) {
                    maxHeap.poll();
                    maxHeap.offer(sum);
                } else {
                    break; // Optimization since list2 is sorted
                }
            }
        }
        List<Integer> result = new ArrayList<>(maxHeap);
        Collections.sort(result);
        return result;
    }

    public static void main(String[] args) {
        FindKthSmallestSumOfMatrixWithSortedRows solution = new FindKthSmallestSumOfMatrixWithSortedRows();
        // Edge Case 1: Normal case
        int[][] mat1 = { { 1, 3, 11 }, { 2, 4, 6 } };
        System.out.println(solution.kthSmallest(mat1, 5)); // 7
        // Edge Case 2: Single row
        int[][] mat2 = { { 1, 2, 3 } };
        System.out.println(solution.kthSmallest(mat2, 2)); // 2
        // Edge Case 3: Large k
        int[][] mat3 = { { 1, 2 }, { 3, 4 } };
        System.out.println(solution.kthSmallest(mat3, 4)); // 6
        // Edge Case 4: All same
        int[][] mat4 = { { 2, 2 }, { 2, 2 } };
        System.out.println(solution.kthSmallest(mat4, 3)); // 4
        // Edge Case 5: Single element
        int[][] mat5 = { { 42 } };
        System.out.println(solution.kthSmallest(mat5, 1)); // 42
        // Edge Case 6: Large matrix
        int[][] mat6 = new int[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                mat6[i][j] = i + j;
        System.out.println(solution.kthSmallest(mat6, 50)); // Should be small
    }
}
