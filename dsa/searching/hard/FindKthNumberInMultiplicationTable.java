package searching.hard;

/**
 * LeetCode 668: Kth Smallest Number in Multiplication Table
 * https://leetcode.com/problems/kth-smallest-number-in-multiplication-table/
 * 
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description: Find the kth smallest number in an m x n multiplication table.
 *
 * Constraints:
 * - 1 <= m, n <= 3 * 10^4
 * - 1 <= k <= m * n
 * 
 * Follow-up Questions:
 * 1. Can you find all numbers less than or equal to k-th number?
 * 2. What if we want the k-th largest instead?
 * 3. Can you handle sparse tables efficiently?
 */
public class FindKthNumberInMultiplicationTable {

    // Approach 1: Binary search on answer - O(m * log(m*n)) time, O(1) space
    public int findKthNumber(int m, int n, int k) {
        int left = 1, right = m * n;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (countLessEqual(m, n, mid) < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private int countLessEqual(int m, int n, int val) {
        int count = 0;
        for (int i = 1; i <= m; i++) {
            count += Math.min(val / i, n);
        }
        return count;
    }

    // Approach 2: Optimized count function - O(m * log(m*n)) time, O(1) space
    public int findKthNumberOptimized(int m, int n, int k) {
        int left = 1, right = m * n;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (countLessEqualOptimized(m, n, mid) < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private int countLessEqualOptimized(int m, int n, int val) {
        int count = 0;
        int row = m, col = 1;

        while (row >= 1 && col <= n) {
            if (row * col <= val) {
                count += row;
                col++;
            } else {
                row--;
            }
        }
        return count;
    }

    // Follow-up 1: Find all numbers less than or equal to k-th number
    public java.util.List<Integer> findAllLessEqual(int m, int n, int k) {
        int kthNumber = findKthNumber(m, n, k);
        java.util.Set<Integer> unique = new java.util.HashSet<>();

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int product = i * j;
                if (product <= kthNumber) {
                    unique.add(product);
                }
            }
        }

        java.util.List<Integer> result = new java.util.ArrayList<>(unique);
        java.util.Collections.sort(result);
        return result;
    }

    // Follow-up 2: Find k-th largest
    public int findKthLargest(int m, int n, int k) {
        return findKthNumber(m, n, m * n - k + 1);
    }

    // Follow-up 3: Handle sparse tables (skip zeros)
    public int findKthNumberSparse(int[][] table, int k) {
        java.util.List<Integer> nonZero = new java.util.ArrayList<>();

        for (int[] row : table) {
            for (int val : row) {
                if (val != 0) {
                    nonZero.add(val);
                }
            }
        }

        if (k > nonZero.size())
            return -1;

        java.util.Collections.sort(nonZero);
        return nonZero.get(k - 1);
    }

    public static void main(String[] args) {
        FindKthNumberInMultiplicationTable solution = new FindKthNumberInMultiplicationTable();

        // Test case 1: Basic case
        System.out.println("Test 1 - Basic case (m=3, n=3, k=5):");
        System.out.println("Expected: 3, Got: " + solution.findKthNumber(3, 3, 5));
        System.out.println("Optimized: " + solution.findKthNumberOptimized(3, 3, 5));

        // Test case 2: Small table
        System.out.println("\nTest 2 - Small table (m=2, n=3, k=6):");
        System.out.println("Expected: 6, Got: " + solution.findKthNumber(2, 3, 6));

        // Test case 3: k = 1 (smallest)
        System.out.println("\nTest 3 - k=1 (smallest):");
        System.out.println("Expected: 1, Got: " + solution.findKthNumber(5, 5, 1));

        // Test case 4: k = m*n (largest)
        System.out.println("\nTest 4 - k=m*n (largest):");
        System.out.println("Expected: 25, Got: " + solution.findKthNumber(5, 5, 25));

        // Test case 5: Rectangle table
        System.out.println("\nTest 5 - Rectangle table (m=2, n=5, k=7):");
        System.out.println("Expected: 6, Got: " + solution.findKthNumber(2, 5, 7));

        // Edge case: 1x1 table
        System.out.println("\nEdge case - 1x1 table:");
        System.out.println("Expected: 1, Got: " + solution.findKthNumber(1, 1, 1));

        // Edge case: Large numbers
        System.out.println("\nEdge case - Large numbers (m=1000, n=1000, k=500000):");
        long startTime = System.currentTimeMillis();
        int result = solution.findKthNumber(1000, 1000, 500000);
        long endTime = System.currentTimeMillis();
        System.out.println("Result: " + result + " in " + (endTime - startTime) + "ms");

        // Follow-up 1: All numbers less than or equal to k-th
        System.out.println("\nFollow-up 1 - All numbers <= k-th (m=3, n=3, k=5):");
        System.out.println("Numbers: " + solution.findAllLessEqual(3, 3, 5));

        // Follow-up 2: K-th largest
        System.out.println("\nFollow-up 2 - K-th largest (m=3, n=3, k=5):");
        System.out.println("Expected: 6, Got: " + solution.findKthLargest(3, 3, 5));

        // Follow-up 3: Sparse table
        int[][] sparseTable = { { 0, 2, 0 }, { 3, 0, 6 }, { 0, 8, 9 } };
        System.out.println("\nFollow-up 3 - Sparse table (k=3):");
        System.out.println("Expected: 6, Got: " + solution.findKthNumberSparse(sparseTable, 3));

        // Verification: Print actual multiplication table for small case
        System.out.println("\nVerification - 3x3 multiplication table:");
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                System.out.print(i * j + " ");
            }
            System.out.println();
        }
        System.out.println("Sorted: [1, 2, 2, 3, 3, 4, 4, 6, 9]");
        System.out.println("5th element: 3");
    }
}
