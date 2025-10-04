package heap.hard;

import java.util.PriorityQueue;

/**
 * LeetCode 1738: Find Kth Largest XOR Coordinate Value
 * https://leetcode.com/problems/find-kth-largest-xor-coordinate-value/
 * 
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * You are given a 2D `matrix` of size `m x n`, consisting of non-negative
 * integers. You are also given an integer `k`.
 * The value of coordinate `(a, b)` is the XOR sum of all `matrix[i][j]` where
 * `0 <= i <= a < m` and `0 <= j <= b < n`.
 * Find the `k`-th largest value (1-indexed) of all coordinate values.
 *
 * Constraints:
 * - m == matrix.length
 * - n == matrix[i].length
 * - 1 <= m, n <= 1000
 * - 0 <= matrix[i][j] <= 10^6
 * - 1 <= k <= m * n
 * 
 * Follow-up Questions:
 * 1. How can you efficiently calculate the XOR sum for each coordinate?
 * (Dynamic Programming)
 * 2. Why is a min-heap a good choice for finding the kth largest element?
 * 3. What is the time and space complexity?
 */
public class FindKthLargestXORCoordinateValue {

    // Approach 1: DP for Prefix XOR + Min-Heap - O(m*n log k) time, O(m*n) space
    // for DP table
    public int kthLargestValue(int[][] matrix, int k) {
        int m = matrix.length;
        int n = matrix[0].length;

        // dp[i][j] will store the XOR value of the coordinate (i-1, j-1)
        int[][] dp = new int[m + 1][n + 1];
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // DP relation for prefix XOR sum
                dp[i][j] = dp[i - 1][j] ^ dp[i][j - 1] ^ dp[i - 1][j - 1] ^ matrix[i - 1][j - 1];

                // Maintain a min-heap of size k
                minHeap.offer(dp[i][j]);
                if (minHeap.size() > k) {
                    minHeap.poll();
                }
            }
        }

        return minHeap.peek();
    }

    public static void main(String[] args) {
        FindKthLargestXORCoordinateValue solution = new FindKthLargestXORCoordinateValue();

        // Test case 1
        int[][] matrix1 = { { 5, 2 }, { 1, 6 } };
        int k1 = 1;
        System.out.println("Kth largest XOR 1: " + solution.kthLargestValue(matrix1, k1)); // 7

        // Test case 2
        int[][] matrix2 = { { 5, 2 }, { 1, 6 } };
        int k2 = 2;
        System.out.println("Kth largest XOR 2: " + solution.kthLargestValue(matrix2, k2)); // 5

        // Test case 3
        int[][] matrix3 = { { 5, 2 }, { 1, 6 } };
        int k3 = 3;
        System.out.println("Kth largest XOR 3: " + solution.kthLargestValue(matrix3, k3)); // 4

        // Test case 4
        int[][] matrix4 = { { 5, 2 }, { 1, 6 } };
        int k4 = 4;
        System.out.println("Kth largest XOR 4: " + solution.kthLargestValue(matrix4, k4)); // 0
    }
}
