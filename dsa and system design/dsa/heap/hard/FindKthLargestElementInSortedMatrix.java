package heap.hard;

import java.util.Collections;
import java.util.PriorityQueue;

/**
 * LeetCode 378 (variation): Kth Smallest Element in a Sorted Matrix
 * https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description:
 * Given a n x n matrix where each row and column is sorted in ascending order,
 * return the kth largest element in the matrix. Note that it is the kth largest
 * element in the sorted order, not the kth distinct element.
 *
 * Constraints:
 * - n == matrix.length == matrix[i].length
 * - 1 <= n <= 300
 * - -10^9 <= matrix[i][j] <= 10^9
 * - All the rows and columns of matrix are guaranteed to be sorted in
 * non-decreasing order.
 * - 1 <= k <= n^2
 * 
 * Follow-up Questions:
 * 1. Can you solve it with a memory complexity better than O(n^2)?
 * 2. Can you solve it with a time complexity better than O(n^2)?
 * 3. How would you solve for the kth smallest element?
 */
public class FindKthLargestElementInSortedMatrix {

    // Approach 1: Max-Heap - O(k log n) time, O(n) space
    public int kthLargest(int[][] matrix, int k) {
        int n = matrix.length;
        // Max-heap to store tuples of (value, row, col)
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);

        // Initialize the heap with the last element of each row
        for (int i = 0; i < n; i++) {
            if (matrix[i].length > 0) {
                maxHeap.offer(new int[] { matrix[i][n - 1], i, n - 1 });
            }
        }

        int result = -1;
        // Extract the max element k times
        for (int i = 0; i < k; i++) {
            int[] top = maxHeap.poll();
            result = top[0];
            int r = top[1];
            int c = top[2];

            // If there's a previous element in the same row, add it to the heap
            if (c > 0) {
                maxHeap.offer(new int[] { matrix[r][c - 1], r, c - 1 });
            }
        }

        return result;
    }

    // Approach 2: Binary Search on the Answer's Value - O(n * log(max-min)) time,
    // O(1) space
    public int kthLargestBinarySearch(int[][] matrix, int k) {
        int n = matrix.length;
        int low = matrix[0][0];
        int high = matrix[n - 1][n - 1];

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int count = countGreaterOrEqual(matrix, mid);

            if (count >= k) {
                // `mid` could be the answer, try for a larger value
                low = mid + 1;
            } else {
                // `mid` is too large, need to search in the lower half
                high = mid - 1;
            }
        }
        // `high` will hold the smallest value `x` such that there are at least `k`
        // elements >= `x`
        return high;
    }

    // Helper to count elements >= value in O(n)
    private int countGreaterOrEqual(int[][] matrix, int value) {
        int n = matrix.length;
        int count = 0;
        int row = 0;
        int col = n - 1;

        while (row < n && col >= 0) {
            if (matrix[row][col] >= value) {
                // All elements in this column from this row downwards are >= value
                count += (n - row);
                col--; // Move to the previous column
            } else {
                row++; // Move to the next row
            }
        }
        return count;
    }

    // Follow-up 3: Find Kth Smallest
    public int kthSmallest(int[][] matrix, int k) {
        int n = matrix.length;
        // To find the kth largest, we can find the (n*n - k + 1)th smallest.
        // So, to find the kth smallest, we can use the same logic but with a min-heap.
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        for (int i = 0; i < Math.min(n, k); i++) {
            minHeap.offer(new int[] { matrix[i][0], i, 0 });
        }

        int result = -1;
        for (int i = 0; i < k; i++) {
            int[] top = minHeap.poll();
            result = top[0];
            int r = top[1];
            int c = top[2];
            if (c + 1 < n) {
                minHeap.offer(new int[] { matrix[r][c + 1], r, c + 1 });
            }
        }
        return result;
    }

    public static void main(String[] args) {
        FindKthLargestElementInSortedMatrix solution = new FindKthLargestElementInSortedMatrix();
        int[][] matrix1 = { { 1, 5, 9 }, { 10, 11, 13 }, { 12, 13, 15 } };
        System.out.println("kthLargest(1): " + solution.kthLargest(matrix1, 1)); // 15
        System.out.println("kthLargest(8): " + solution.kthLargest(matrix1, 8)); // 5
        System.out.println("kthLargestBS(8): " + solution.kthLargestBinarySearch(matrix1, 8)); // 5
        System.out.println("kthSmallest(8): " + solution.kthSmallest(matrix1, 8)); // 13

        // Edge Case 1: All same
        int[][] matrix2 = { { 2, 2 }, { 2, 2 } };
        System.out.println("kthLargest(3): " + solution.kthLargest(matrix2, 3)); // 2

        // Edge Case 2: Matrix with negative numbers
        int[][] matrix3 = { { -5, -3 }, { -2, 0 } };
        System.out.println("kthLargest(2): " + solution.kthLargest(matrix3, 2)); // -2

        // Edge Case 3: Matrix with single element
        int[][] matrix4 = { { 42 } };
        System.out.println("kthLargest(1): " + solution.kthLargest(matrix4, 1)); // 42

        // Edge Case 4: Non-square matrix (not per constraints, but good to test)
        // Note: The provided solution assumes a square matrix as per constraints.
        // int[][] matrix5 = { { 1, 2 } };
        // System.out.println(solution.kthLargest(matrix5, 2)); // 1
    }
}
