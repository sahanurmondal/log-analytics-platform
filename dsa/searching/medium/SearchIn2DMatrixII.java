package searching.medium;

import java.util.*;

/**
 * LeetCode 240: Search a 2D Matrix II
 * https://leetcode.com/problems/search-a-2d-matrix-ii/
 * 
 * Companies: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg
 * Frequency: Very High (Asked in 600+ interviews)
 *
 * Description:
 * Write an efficient algorithm that searches for a value target in an m x n
 * integer matrix.
 * This matrix has the following properties:
 * - Integers in each row are sorted in ascending from left to right.
 * - Integers in each column are sorted in ascending from top to bottom.
 * 
 * Constraints:
 * - m == matrix.length
 * - n == matrix[i].length
 * - 1 <= n, m <= 300
 * - -10^9 <= matrix[i][j] <= 10^9
 * - All the integers in each row are sorted in ascending order.
 * - All the integers in each column are sorted in ascending order.
 * - -10^9 <= target <= 10^9
 * 
 * Follow-up Questions:
 * 1. Can you find all occurrences of target?
 * 2. How would you find the kth smallest element?
 * 3. Can you count elements smaller than target?
 * 4. What about finding the closest element to target?
 * 5. How to optimize for multiple queries?
 * 6. Can you implement parallel search?
 */
public class SearchIn2DMatrixII {

    // Approach 1: Start from top-right corner - O(m + n) time, O(1) space
    public static boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        int row = 0;
        int col = matrix[0].length - 1;

        while (row < matrix.length && col >= 0) {
            if (matrix[row][col] == target) {
                return true;
            } else if (matrix[row][col] > target) {
                col--; // Move left
            } else {
                row++; // Move down
            }
        }

        return false;
    }

    // Approach 2: Start from bottom-left corner - O(m + n) time, O(1) space
    public static boolean searchMatrixBottomLeft(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        int row = matrix.length - 1;
        int col = 0;

        while (row >= 0 && col < matrix[0].length) {
            if (matrix[row][col] == target) {
                return true;
            } else if (matrix[row][col] > target) {
                row--; // Move up
            } else {
                col++; // Move right
            }
        }

        return false;
    }

    // Approach 3: Binary search on each row - O(m log n) time, O(1) space
    public static boolean searchMatrixBinarySearch(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        for (int[] row : matrix) {
            if (binarySearch(row, target)) {
                return true;
            }
        }

        return false;
    }

    private static boolean binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == target) {
                return true;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

    // Approach 4: Divide and conquer - O(n^log₄3) ≈ O(n^1.58) time
    public static boolean searchMatrixDivideConquer(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        return divideConquer(matrix, target, 0, 0, matrix.length - 1, matrix[0].length - 1);
    }

    private static boolean divideConquer(int[][] matrix, int target,
            int row1, int col1, int row2, int col2) {
        if (row1 > row2 || col1 > col2)
            return false;

        if (row1 == row2 && col1 == col2) {
            return matrix[row1][col1] == target;
        }

        int midRow = row1 + (row2 - row1) / 2;
        int midCol = col1 + (col2 - col1) / 2;

        if (matrix[midRow][midCol] == target) {
            return true;
        } else if (matrix[midRow][midCol] > target) {
            // Search in top-left, top-right, bottom-left quadrants
            return divideConquer(matrix, target, row1, col1, midRow, midCol) ||
                    divideConquer(matrix, target, row1, midCol + 1, midRow, col2) ||
                    divideConquer(matrix, target, midRow + 1, col1, row2, midCol);
        } else {
            // Search in top-right, bottom-left, bottom-right quadrants
            return divideConquer(matrix, target, row1, midCol + 1, midRow, col2) ||
                    divideConquer(matrix, target, midRow + 1, col1, row2, midCol) ||
                    divideConquer(matrix, target, midRow + 1, midCol + 1, row2, col2);
        }
    }

    // Follow-up 1: Find all occurrences of target
    public static class FindAllOccurrences {

        public static List<int[]> findAllOccurrences(int[][] matrix, int target) {
            List<int[]> result = new ArrayList<>();

            if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
                return result;
            }

            int row = 0;
            int col = matrix[0].length - 1;

            while (row < matrix.length && col >= 0) {
                if (matrix[row][col] == target) {
                    result.add(new int[] { row, col });

                    // Check for more occurrences in the current row
                    int tempCol = col - 1;
                    while (tempCol >= 0 && matrix[row][tempCol] == target) {
                        result.add(new int[] { row, tempCol });
                        tempCol--;
                    }

                    row++; // Move to next row
                } else if (matrix[row][col] > target) {
                    col--; // Move left
                } else {
                    row++; // Move down
                }
            }

            return result;
        }

        // Find all occurrences using divide and conquer
        public static List<int[]> findAllOccurrencesDC(int[][] matrix, int target) {
            List<int[]> result = new ArrayList<>();

            if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
                return result;
            }

            findAllHelper(matrix, target, 0, 0, matrix.length - 1, matrix[0].length - 1, result);
            return result;
        }

        private static void findAllHelper(int[][] matrix, int target,
                int row1, int col1, int row2, int col2,
                List<int[]> result) {
            if (row1 > row2 || col1 > col2)
                return;

            if (row1 == row2 && col1 == col2) {
                if (matrix[row1][col1] == target) {
                    result.add(new int[] { row1, col1 });
                }
                return;
            }

            int midRow = row1 + (row2 - row1) / 2;
            int midCol = col1 + (col2 - col1) / 2;

            if (matrix[midRow][midCol] == target) {
                result.add(new int[] { midRow, midCol });
            }

            if (matrix[midRow][midCol] >= target) {
                // Search in top-left, top-right, bottom-left quadrants
                findAllHelper(matrix, target, row1, col1, midRow, midCol, result);
                findAllHelper(matrix, target, row1, midCol + 1, midRow, col2, result);
                findAllHelper(matrix, target, midRow + 1, col1, row2, midCol, result);
            }

            if (matrix[midRow][midCol] <= target) {
                // Search in top-right, bottom-left, bottom-right quadrants
                findAllHelper(matrix, target, row1, midCol + 1, midRow, col2, result);
                findAllHelper(matrix, target, midRow + 1, col1, row2, midCol, result);
                findAllHelper(matrix, target, midRow + 1, midCol + 1, row2, col2, result);
            }
        }
    }

    // Follow-up 2: Find kth smallest element
    public static class KthSmallestElement {

        public static int kthSmallest(int[][] matrix, int k) {
            int n = matrix.length;
            PriorityQueue<int[]> minHeap = new PriorityQueue<>(
                    (a, b) -> Integer.compare(matrix[a[0]][a[1]], matrix[b[0]][b[1]]));

            // Add first element of each row
            for (int i = 0; i < n; i++) {
                minHeap.offer(new int[] { i, 0 });
            }

            for (int i = 0; i < k - 1; i++) {
                int[] current = minHeap.poll();
                int row = current[0];
                int col = current[1];

                if (col + 1 < matrix[row].length) {
                    minHeap.offer(new int[] { row, col + 1 });
                }
            }

            int[] result = minHeap.poll();
            return matrix[result[0]][result[1]];
        }

        // Binary search approach
        public static int kthSmallestBinarySearch(int[][] matrix, int k) {
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

        private static int countLessEqual(int[][] matrix, int target) {
            int count = 0;
            int n = matrix.length;
            int row = n - 1;
            int col = 0;

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
    }

    // Follow-up 3: Count elements smaller than target
    public static class CountElements {

        public static int countSmallerThan(int[][] matrix, int target) {
            if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
                return 0;
            }

            int count = 0;
            int row = matrix.length - 1;
            int col = 0;

            while (row >= 0 && col < matrix[0].length) {
                if (matrix[row][col] < target) {
                    count += row + 1;
                    col++;
                } else {
                    row--;
                }
            }

            return count;
        }

        public static int countSmallerEqual(int[][] matrix, int target) {
            if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
                return 0;
            }

            int count = 0;
            int row = matrix.length - 1;
            int col = 0;

            while (row >= 0 && col < matrix[0].length) {
                if (matrix[row][col] <= target) {
                    count += row + 1;
                    col++;
                } else {
                    row--;
                }
            }

            return count;
        }

        public static int countInRange(int[][] matrix, int lower, int upper) {
            return countSmallerEqual(matrix, upper) - countSmallerThan(matrix, lower);
        }
    }

    // Follow-up 4: Find closest element to target
    public static class FindClosest {

        public static int findClosestElement(int[][] matrix, int target) {
            if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
                throw new IllegalArgumentException("Matrix is empty");
            }

            int closest = matrix[0][0];
            int minDiff = Math.abs(matrix[0][0] - target);

            int row = 0;
            int col = matrix[0].length - 1;

            while (row < matrix.length && col >= 0) {
                int current = matrix[row][col];
                int diff = Math.abs(current - target);

                if (diff < minDiff) {
                    minDiff = diff;
                    closest = current;
                }

                if (current == target) {
                    return current;
                } else if (current > target) {
                    col--; // Move left
                } else {
                    row++; // Move down
                }
            }

            return closest;
        }

        public static List<Integer> findKClosestElements(int[][] matrix, int target, int k) {
            PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
                    (a, b) -> Integer.compare(Math.abs(b - target), Math.abs(a - target)));

            for (int[] row : matrix) {
                for (int val : row) {
                    maxHeap.offer(val);
                    if (maxHeap.size() > k) {
                        maxHeap.poll();
                    }
                }
            }

            return new ArrayList<>(maxHeap);
        }
    }

    // Follow-up 5: Multiple queries optimization
    public static class MultipleQueriesOptimizer {
        private int[][] matrix;
        private List<Integer> flattenedSorted;

        public MultipleQueriesOptimizer(int[][] matrix) {
            this.matrix = matrix;
            this.flattenedSorted = new ArrayList<>();

            // Flatten and sort the matrix
            for (int[] row : matrix) {
                for (int val : row) {
                    flattenedSorted.add(val);
                }
            }
            Collections.sort(flattenedSorted);
        }

        public boolean search(int target) {
            return Collections.binarySearch(flattenedSorted, target) >= 0;
        }

        public int kthSmallest(int k) {
            return flattenedSorted.get(k - 1);
        }

        public int countSmallerThan(int target) {
            int index = Collections.binarySearch(flattenedSorted, target);
            if (index < 0) {
                return -(index + 1);
            } else {
                // Find first occurrence
                while (index > 0 && flattenedSorted.get(index - 1) == target) {
                    index--;
                }
                return index;
            }
        }

        public List<Integer> getRange(int lower, int upper) {
            int lowerIndex = Collections.binarySearch(flattenedSorted, lower);
            int upperIndex = Collections.binarySearch(flattenedSorted, upper);

            if (lowerIndex < 0)
                lowerIndex = -(lowerIndex + 1);
            if (upperIndex < 0)
                upperIndex = -(upperIndex + 1) - 1;
            else {
                // Include all occurrences of upper bound
                while (upperIndex < flattenedSorted.size() - 1 &&
                        flattenedSorted.get(upperIndex + 1) == upper) {
                    upperIndex++;
                }
            }

            return flattenedSorted.subList(lowerIndex, upperIndex + 1);
        }
    }

    // Advanced: Parallel search
    public static class ParallelSearch {

        public static boolean searchParallel(int[][] matrix, int target) {
            if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
                return false;
            }

            return Arrays.stream(matrix)
                    .parallel()
                    .anyMatch(row -> binarySearch(row, target));
        }

        // Parallel search with ForkJoin
        public static boolean searchForkJoin(int[][] matrix, int target) {
            if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
                return false;
            }

            java.util.concurrent.ForkJoinPool pool = java.util.concurrent.ForkJoinPool.commonPool();
            return pool.invoke(new SearchTask(matrix, target, 0, matrix.length - 1));
        }

        private static class SearchTask extends java.util.concurrent.RecursiveTask<Boolean> {
            private int[][] matrix;
            private int target;
            private int startRow, endRow;
            private static final int THRESHOLD = 10;

            public SearchTask(int[][] matrix, int target, int startRow, int endRow) {
                this.matrix = matrix;
                this.target = target;
                this.startRow = startRow;
                this.endRow = endRow;
            }

            @Override
            protected Boolean compute() {
                if (endRow - startRow <= THRESHOLD) {
                    // Sequential search for small range
                    for (int i = startRow; i <= endRow; i++) {
                        if (binarySearch(matrix[i], target)) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    // Split task
                    int mid = startRow + (endRow - startRow) / 2;
                    SearchTask leftTask = new SearchTask(matrix, target, startRow, mid);
                    SearchTask rightTask = new SearchTask(matrix, target, mid + 1, endRow);

                    leftTask.fork();
                    Boolean rightResult = rightTask.compute();
                    Boolean leftResult = leftTask.join();

                    return leftResult || rightResult;
                }
            }
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(int[][] matrix, int target, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Matrix size: " + matrix.length + "x" + matrix[0].length +
                    ", Target: " + target + ", Iterations: " + iterations);

            // Top-right approach
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                searchMatrix(matrix, target);
            }
            long topRightTime = System.nanoTime() - start;

            // Bottom-left approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                searchMatrixBottomLeft(matrix, target);
            }
            long bottomLeftTime = System.nanoTime() - start;

            // Binary search approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                searchMatrixBinarySearch(matrix, target);
            }
            long binarySearchTime = System.nanoTime() - start;

            // Divide and conquer
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                searchMatrixDivideConquer(matrix, target);
            }
            long divideConquerTime = System.nanoTime() - start;

            System.out.println("Top-right corner: " + topRightTime / 1_000_000 + " ms");
            System.out.println("Bottom-left corner: " + bottomLeftTime / 1_000_000 + " ms");
            System.out.println("Binary search: " + binarySearchTime / 1_000_000 + " ms");
            System.out.println("Divide & conquer: " + divideConquerTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[][] matrix1 = {
                { 1, 4, 7, 11, 15 },
                { 2, 5, 8, 12, 19 },
                { 3, 6, 9, 16, 22 },
                { 10, 13, 14, 17, 24 },
                { 18, 21, 23, 26, 30 }
        };

        int target1 = 5;
        System.out.println("Target: " + target1);
        System.out.println("Top-right: " + searchMatrix(matrix1, target1));
        System.out.println("Bottom-left: " + searchMatrixBottomLeft(matrix1, target1));
        System.out.println("Binary search: " + searchMatrixBinarySearch(matrix1, target1));
        System.out.println("Divide & conquer: " + searchMatrixDivideConquer(matrix1, target1));

        // Test Case 2: Target not found
        System.out.println("\n=== Test Case 2: Target Not Found ===");

        int target2 = 20;
        System.out.println("Target: " + target2);
        System.out.println("Found: " + searchMatrix(matrix1, target2));

        // Test Case 3: Edge cases
        System.out.println("\n=== Test Case 3: Edge Cases ===");

        int[][] singleElement = { { 1 } };
        int[][] singleRow = { { 1, 3, 5 } };
        int[][] singleCol = { { 1 }, { 3 }, { 5 } };

        System.out.println("Single element (target 1): " + searchMatrix(singleElement, 1));
        System.out.println("Single element (target 2): " + searchMatrix(singleElement, 2));
        System.out.println("Single row (target 3): " + searchMatrix(singleRow, 3));
        System.out.println("Single column (target 5): " + searchMatrix(singleCol, 5));

        // Test Case 4: Corner elements
        System.out.println("\n=== Test Case 4: Corner Elements ===");

        System.out.println("Top-left corner (1): " + searchMatrix(matrix1, 1));
        System.out.println("Top-right corner (15): " + searchMatrix(matrix1, 15));
        System.out.println("Bottom-left corner (18): " + searchMatrix(matrix1, 18));
        System.out.println("Bottom-right corner (30): " + searchMatrix(matrix1, 30));

        // Test Case 5: Find all occurrences
        System.out.println("\n=== Test Case 5: Find All Occurrences ===");

        int[][] matrixWithDuplicates = {
                { 1, 2, 3 },
                { 2, 3, 4 },
                { 3, 4, 5 }
        };

        int targetDup = 3;
        System.out.println("Matrix with duplicates, target: " + targetDup);

        List<int[]> occurrences = FindAllOccurrences.findAllOccurrences(matrixWithDuplicates, targetDup);
        System.out.print("Occurrences: ");
        for (int[] pos : occurrences) {
            System.out.print("(" + pos[0] + "," + pos[1] + ") ");
        }
        System.out.println();

        // Test Case 6: Kth smallest element
        System.out.println("\n=== Test Case 6: Kth Smallest Element ===");

        int[][] matrix6 = {
                { 1, 5, 9 },
                { 10, 11, 13 },
                { 12, 13, 15 }
        };

        for (int k = 1; k <= 3; k++) {
            System.out.println("k=" + k + ": " + KthSmallestElement.kthSmallest(matrix6, k));
        }

        System.out.println("5th smallest (binary search): " +
                KthSmallestElement.kthSmallestBinarySearch(matrix6, 5));

        // Test Case 7: Count elements
        System.out.println("\n=== Test Case 7: Count Elements ===");

        int countTarget = 10;
        System.out.println("Elements < " + countTarget + ": " +
                CountElements.countSmallerThan(matrix1, countTarget));
        System.out.println("Elements <= " + countTarget + ": " +
                CountElements.countSmallerEqual(matrix1, countTarget));
        System.out.println("Elements in range [5, 15]: " +
                CountElements.countInRange(matrix1, 5, 15));

        // Test Case 8: Find closest element
        System.out.println("\n=== Test Case 8: Find Closest Element ===");

        int targetClosest = 20;
        System.out.println("Target: " + targetClosest);
        System.out.println("Closest element: " +
                FindClosest.findClosestElement(matrix1, targetClosest));

        List<Integer> closestK = FindClosest.findKClosestElements(matrix1, targetClosest, 3);
        System.out.println("3 closest elements: " + closestK);

        // Test Case 9: Multiple queries optimization
        System.out.println("\n=== Test Case 9: Multiple Queries ===");

        MultipleQueriesOptimizer optimizer = new MultipleQueriesOptimizer(matrix1);

        System.out.println("Search 5: " + optimizer.search(5));
        System.out.println("Search 20: " + optimizer.search(20));
        System.out.println("3rd smallest: " + optimizer.kthSmallest(3));
        System.out.println("Elements < 10: " + optimizer.countSmallerThan(10));
        System.out.println("Range [5, 15]: " + optimizer.getRange(5, 15));

        // Test Case 10: Large matrix performance
        System.out.println("\n=== Test Case 10: Large Matrix Performance ===");

        int size = 100;
        int[][] largeMatrix = new int[size][size];

        // Fill matrix with sorted values
        int value = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                largeMatrix[i][j] = value;
                value += (i * j % 3) + 1; // Some variation in increment
            }
        }

        int largeTarget = largeMatrix[size / 2][size / 2];

        long start = System.currentTimeMillis();
        boolean found = searchMatrix(largeMatrix, largeTarget);
        long end = System.currentTimeMillis();

        System.out.println("Large matrix (" + size + "x" + size + ")");
        System.out.println("Found target: " + found);
        System.out.println("Time taken: " + (end - start) + " ms");

        // Test Case 11: Parallel search
        System.out.println("\n=== Test Case 11: Parallel Search ===");

        start = System.currentTimeMillis();
        boolean sequentialResult = searchMatrixBinarySearch(largeMatrix, largeTarget);
        long sequentialTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        boolean parallelResult = ParallelSearch.searchParallel(largeMatrix, largeTarget);
        long parallelTime = System.currentTimeMillis() - start;

        System.out.println("Sequential result: " + sequentialResult + " (Time: " + sequentialTime + " ms)");
        System.out.println("Parallel result: " + parallelResult + " (Time: " + parallelTime + " ms)");
        System.out.println("Results match: " + (sequentialResult == parallelResult));

        // Test Case 12: Stress test
        System.out.println("\n=== Test Case 12: Stress Test ===");

        Random random = new Random(42);
        int testCases = 100;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int rows = random.nextInt(10) + 1;
            int cols = random.nextInt(10) + 1;
            int[][] testMatrix = new int[rows][cols];

            // Fill with sorted values
            int val = 1;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    testMatrix[i][j] = val;
                    val += random.nextInt(3) + 1;
                }
            }

            int testTarget = random.nextBoolean() ? testMatrix[random.nextInt(rows)][random.nextInt(cols)]
                    : random.nextInt(1000) + 1;

            boolean expected = searchMatrixBinarySearch(testMatrix, testTarget);
            boolean actual = searchMatrix(testMatrix, testTarget);

            if (expected == actual) {
                passed++;
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Test Case 13: Negative numbers
        System.out.println("\n=== Test Case 13: Negative Numbers ===");

        int[][] negativeMatrix = {
                { -9, -7, -4, -2 },
                { -8, -6, -3, -1 },
                { -5, -3, -1, 1 },
                { -2, 0, 2, 4 }
        };

        System.out.println("Search -3: " + searchMatrix(negativeMatrix, -3));
        System.out.println("Search 0: " + searchMatrix(negativeMatrix, 0));
        System.out.println("Search 3: " + searchMatrix(negativeMatrix, 3));

        // Performance comparison
        PerformanceComparison.compareApproaches(matrix1, 5, 10000);

        System.out.println("\nSearch in 2D Matrix II testing completed successfully!");
    }
}
