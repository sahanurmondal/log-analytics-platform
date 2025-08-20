package heap.medium;

import java.util.*;

/**
 * LeetCode 378: Kth Smallest Element in a Sorted Matrix
 * URL: https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/
 * Difficulty: Medium
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 120+ interviews)
 *
 * Description:
 * Given an n x n matrix where each of the rows and columns is sorted in
 * ascending order,
 * return the kth smallest element in the matrix.
 * Note that it is the kth smallest element in the sorted order, not the kth
 * distinct element.
 *
 * Example:
 * Input: matrix = [[1,5,9],[10,11,13],[12,13,15]], k = 8
 * Output: 13
 * Explanation: The elements in the matrix are [1,5,9,10,11,12,13,13,15], and
 * the 8th smallest number is 13
 *
 * Constraints:
 * - n == matrix.length == matrix[i].length
 * - 1 <= n <= 300
 * - -10^9 <= matrix[i][j] <= 10^9
 * - All rows and columns are sorted in non-decreasing order
 * - 1 <= k <= n^2
 * 
 * Follow-up Questions:
 * 1. Can you solve it in O(n log n) time?
 * 2. What if the matrix is very large but sparse?
 * 3. Can you solve without using extra space?
 * 4. How would you handle streaming data?
 */
public class KthSmallestElementInSortedMatrix {

    // Approach 1: Min Heap - O(min(k,n) + k*log(min(k,n))) time, O(min(k,n)) space
    public int kthSmallest(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || k <= 0) {
            return -1;
        }

        int n = matrix.length;
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        // Add first element of each row
        for (int i = 0; i < Math.min(n, k); i++) {
            minHeap.offer(new int[] { matrix[i][0], i, 0 });
        }

        // Extract k-1 elements
        for (int i = 0; i < k - 1; i++) {
            int[] curr = minHeap.poll();
            int row = curr[1], col = curr[2];

            // Add next element in the same row if exists
            if (col + 1 < n) {
                minHeap.offer(new int[] { matrix[row][col + 1], row, col + 1 });
            }
        }

        return minHeap.peek()[0];
    }

    // Approach 2: Binary Search - O(n * log(max-min)) time, O(1) space
    public int kthSmallestBinarySearch(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || k <= 0) {
            return -1;
        }

        int n = matrix.length;
        int low = matrix[0][0];
        int high = matrix[n - 1][n - 1];

        while (low < high) {
            int mid = low + (high - low) / 2;
            int count = countLessEqual(matrix, mid);

            if (count < k) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return low;
    }

    private int countLessEqual(int[][] matrix, int target) {
        int n = matrix.length;
        int count = 0;
        int row = n - 1, col = 0;

        while (row >= 0 && col < n) {
            if (matrix[row][col] <= target) {
                count += row + 1;
                col++;
            } else {
                row--;
            }
        }

        return count;
    }

    // Approach 3: Max Heap (for small k) - O(n^2 * log k) time, O(k) space
    public int kthSmallestMaxHeap(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || k <= 0) {
            return -1;
        }

        int n = matrix.length;
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                maxHeap.offer(matrix[i][j]);
                if (maxHeap.size() > k) {
                    maxHeap.poll();
                }
            }
        }

        return maxHeap.peek();
    }

    // Approach 4: Optimized Min Heap - O(k * log n) time, O(n) space
    public int kthSmallestOptimizedHeap(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || k <= 0) {
            return -1;
        }

        int n = matrix.length;
        PriorityQueue<Element> minHeap = new PriorityQueue<>();
        boolean[][] visited = new boolean[n][n];

        minHeap.offer(new Element(matrix[0][0], 0, 0));
        visited[0][0] = true;

        for (int i = 0; i < k - 1; i++) {
            Element curr = minHeap.poll();
            int row = curr.row, col = curr.col;

            // Add right neighbor
            if (col + 1 < n && !visited[row][col + 1]) {
                minHeap.offer(new Element(matrix[row][col + 1], row, col + 1));
                visited[row][col + 1] = true;
            }

            // Add bottom neighbor
            if (row + 1 < n && !visited[row + 1][col]) {
                minHeap.offer(new Element(matrix[row + 1][col], row + 1, col));
                visited[row + 1][col] = true;
            }
        }

        return minHeap.peek().val;
    }

    private static class Element implements Comparable<Element> {
        int val, row, col;

        Element(int val, int row, int col) {
            this.val = val;
            this.row = row;
            this.col = col;
        }

        @Override
        public int compareTo(Element other) {
            return Integer.compare(this.val, other.val);
        }
    }

    public static void main(String[] args) {
        KthSmallestElementInSortedMatrix solution = new KthSmallestElementInSortedMatrix();

        // Test Case 1: Normal case
        System.out.println("=== Test Case 1: Normal Case ===");
        int[][] matrix1 = { { 1, 5, 9 }, { 10, 11, 13 }, { 12, 13, 15 } };
        System.out.println("Expected: 13, Got: " + solution.kthSmallest(matrix1, 8)); // 13

        // Test Case 2: k = 1 (smallest)
        System.out.println("\n=== Test Case 2: k = 1 ===");
        int[][] matrix2 = { { 1, 2 }, { 1, 3 } };
        System.out.println("Expected: 1, Got: " + solution.kthSmallest(matrix2, 1)); // 1

        // Test Case 3: k = n^2 (largest)
        System.out.println("\n=== Test Case 3: k = n^2 ===");
        int[][] matrix3 = { { 1, 2 }, { 1, 3 } };
        System.out.println("Expected: 3, Got: " + solution.kthSmallest(matrix3, 4)); // 3

        // Test Case 4: Single element
        System.out.println("\n=== Test Case 4: Single Element ===");
        int[][] matrix4 = { { 1 } };
        System.out.println("Expected: 1, Got: " + solution.kthSmallest(matrix4, 1)); // 1

        // Test Case 5: Duplicates
        System.out.println("\n=== Test Case 5: With Duplicates ===");
        int[][] matrix5 = { { 1, 2, 2 }, { 2, 3, 3 }, { 3, 3, 4 } };
        System.out.println("Expected: 3, Got: " + solution.kthSmallest(matrix5, 7)); // 3

        // Test Case 6: Negative numbers
        System.out.println("\n=== Test Case 6: Negative Numbers ===");
        int[][] matrix6 = { { -5, -4 }, { -3, -1 } };
        System.out.println("Expected: -3, Got: " + solution.kthSmallest(matrix6, 3)); // -3

        // Test Case 7: Compare approaches
        System.out.println("\n=== Test Case 7: Approach Comparison ===");
        compareApproaches(solution, matrix1, 8);

        // Test Case 8: Large matrix
        System.out.println("\n=== Test Case 8: Large Matrix ===");
        int[][] largeMatrix = generateMatrix(10, 1, 100);
        System.out.println("Large matrix kth smallest: " + solution.kthSmallest(largeMatrix, 50));

        // Test Case 9: Performance test
        System.out.println("\n=== Test Case 9: Performance Test ===");
        performanceTest(solution);

        // Test Case 10: Edge case - identical elements
        System.out.println("\n=== Test Case 10: Identical Elements ===");
        int[][] matrix10 = { { 5, 5, 5 }, { 5, 5, 5 }, { 5, 5, 5 } };
        System.out.println("Expected: 5, Got: " + solution.kthSmallest(matrix10, 5)); // 5

        // Test Case 11: Sequential matrix
        System.out.println("\n=== Test Case 11: Sequential Matrix ===");
        int[][] matrix11 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        System.out.println("Expected: 5, Got: " + solution.kthSmallest(matrix11, 5)); // 5

        // Test Case 12: Validation test
        System.out.println("\n=== Test Case 12: Validation Test ===");
        validateAllApproaches(solution);

        // Test Case 13: Stress test
        System.out.println("\n=== Test Case 13: Stress Test ===");
        stressTest(solution);

        // Test Case 14: Binary search specific
        System.out.println("\n=== Test Case 14: Binary Search Test ===");
        System.out.println("Binary search result: " + solution.kthSmallestBinarySearch(matrix1, 8)); // 13

        // Test Case 15: Large k value
        System.out.println("\n=== Test Case 15: Large k ===");
        int[][] matrix15 = generateMatrix(5, 1, 25);
        System.out.println("Large k result: " + solution.kthSmallest(matrix15, 20));
    }

    private static void compareApproaches(KthSmallestElementInSortedMatrix solution, int[][] matrix, int k) {
        int result1 = solution.kthSmallest(matrix, k);
        int result2 = solution.kthSmallestBinarySearch(matrix, k);
        int result3 = solution.kthSmallestMaxHeap(matrix, k);
        int result4 = solution.kthSmallestOptimizedHeap(matrix, k);

        System.out.println("Min Heap: " + result1);
        System.out.println("Binary Search: " + result2);
        System.out.println("Max Heap: " + result3);
        System.out.println("Optimized Heap: " + result4);
        System.out.println("All consistent: " +
                (result1 == result2 && result2 == result3 && result3 == result4));
    }

    private static void performanceTest(KthSmallestElementInSortedMatrix solution) {
        int[][] perfMatrix = generateMatrix(50, 1, 2500);

        long start = System.currentTimeMillis();
        int result = solution.kthSmallest(perfMatrix, 1000);
        long end = System.currentTimeMillis();

        System.out.println("Performance test result: " + result +
                " found in " + (end - start) + "ms");
    }

    private static void validateAllApproaches(KthSmallestElementInSortedMatrix solution) {
        int[][] testMatrix = { { 1, 3, 5 }, { 6, 7, 12 }, { 11, 14, 14 } };
        int k = 6;

        int result1 = solution.kthSmallest(testMatrix, k);
        int result2 = solution.kthSmallestBinarySearch(testMatrix, k);
        int result3 = solution.kthSmallestMaxHeap(testMatrix, k);
        int result4 = solution.kthSmallestOptimizedHeap(testMatrix, k);

        boolean allConsistent = result1 == result2 && result2 == result3 && result3 == result4;
        System.out.println("Validation result: " + result1 + ", All consistent: " + allConsistent);
    }

    private static void stressTest(KthSmallestElementInSortedMatrix solution) {
        int[][] stressMatrix = generateMatrix(20, -100, 100);

        for (int k = 1; k <= Math.min(100, stressMatrix.length * stressMatrix.length); k += 50) {
            int result = solution.kthSmallest(stressMatrix, k);
            System.out.println("Stress test k=" + k + ", result=" + result);
        }
    }

    private static int[][] generateMatrix(int n, int minVal, int maxVal) {
        int[][] matrix = new int[n][n];
        int val = minVal;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = val;
                val = Math.min(val + (int) (Math.random() * 3) + 1, maxVal);
            }
        }

        return matrix;
    }
}
