package heap.hard;

import java.util.PriorityQueue;

/**
 * Variation: Find Kth Largest Sum in Matrix
 *
 * Description:
 * Given a matrix, return the kth largest sum of submatrices.
 *
 * Constraints:
 * - 1 <= matrix.length, matrix[0].length <= 1000
 * - 1 <= k <= matrix.length * matrix[0].length
 * - -10^6 <= matrix[i][j] <= 10^6
 */
public class FindKthLargestSumInMatrix {
    /**
     * Finds the kth largest sum of submatrices.
     * This method uses a min-heap to keep track of the k largest sums found so far.
     *
     * @param matrix The input matrix.
     * @param k      The value of k.
     * @return The kth largest sum.
     */
    public int kthLargestSum(int[][] matrix, int k) {
        int m = matrix.length;
        int n = matrix[0].length;
        int[][] prefixSum = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefixSum[i][j] = matrix[i - 1][j - 1] + prefixSum[i - 1][j] + prefixSum[i][j - 1]
                        - prefixSum[i - 1][j - 1];
            }
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

        for (int r1 = 1; r1 <= m; r1++) {
            for (int c1 = 1; c1 <= n; c1++) {
                for (int r2 = r1; r2 <= m; r2++) {
                    for (int c2 = c1; c2 <= n; c2++) {
                        int sum = prefixSum[r2][c2] - prefixSum[r1 - 1][c2] - prefixSum[r2][c1 - 1]
                                + prefixSum[r1 - 1][c1 - 1];
                        if (minHeap.size() < k) {
                            minHeap.offer(sum);
                        } else if (sum > minHeap.peek()) {
                            minHeap.poll();
                            minHeap.offer(sum);
                        }
                    }
                }
            }
        }
        return minHeap.peek();
    }

    public static void main(String[] args) {
        FindKthLargestSumInMatrix solution = new FindKthLargestSumInMatrix();
        int[][] matrix1 = { { 1, 2 }, { 3, 4 } };
        System.out.println(solution.kthLargestSum(matrix1, 1)); // 10
        System.out.println(solution.kthLargestSum(matrix1, 2)); // 7
        System.out.println(solution.kthLargestSum(matrix1, 4)); // 1
        // Edge Case 1: All zeros
        int[][] matrix2 = { { 0, 0 }, { 0, 0 } };
        System.out.println(solution.kthLargestSum(matrix2, 1)); // 0
        // Edge Case 2: Large matrix
        int[][] matrix3 = new int[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                matrix3[i][j] = i + j;
        System.out.println(solution.kthLargestSum(matrix3, 50)); // Should be small
    }
}
