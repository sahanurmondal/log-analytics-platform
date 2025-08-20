package heap.hard;

/**
 * LeetCode 668: Kth Smallest Number in Multiplication Table
 * https://leetcode.com/problems/kth-smallest-number-in-multiplication-table/
 * 
 * Companies: Google, Amazon
 * Frequency: Hard
 *
 * Description:
 * Nearly everyone has used the Multiplication Table. The multiplication table
 * of size `m x n` is an integer matrix `mat` where `mat[i][j] == i * j`
 * (1-indexed).
 * Given three integers `m`, `n`, and `k`, return the `k`-th smallest element in
 * the `m x n` multiplication table.
 *
 * Constraints:
 * - 1 <= m, n <= 3 * 10^4
 * - 1 <= k <= m * n
 * 
 * Follow-up Questions:
 * 1. Why is a heap-based approach too slow here? (m*n can be large)
 * 2. Explain the binary search on the answer approach.
 * 3. How do you efficiently count numbers less than or equal to `x` in the
 * table?
 */
public class FindKthSmallestNumberInMultiplicationTable {

    // Approach 1: Binary Search on the Answer - O(m * log(m*n)) time, O(1) space
    public int findKthNumber(int m, int n, int k) {
        int low = 1;
        int high = m * n;

        while (low < high) {
            int mid = low + (high - low) / 2;

            // Count how many numbers in the table are <= mid
            int count = 0;
            for (int i = 1; i <= m; i++) {
                // In row `i`, the elements are i, 2i, 3i, ...
                // The number of elements <= mid is min(mid / i, n)
                count += Math.min(mid / i, n);
            }

            if (count >= k) {
                // mid is a potential answer, try for a smaller one
                high = mid;
            } else {
                // mid is too small
                low = mid + 1;
            }
        }

        return low;
    }

    public static void main(String[] args) {
        FindKthSmallestNumberInMultiplicationTable solution = new FindKthSmallestNumberInMultiplicationTable();

        // Test case 1
        int m1 = 3, n1 = 3, k1 = 5;
        System.out.println("Kth smallest 1: " + solution.findKthNumber(m1, n1, k1)); // 3

        // Test case 2
        int m2 = 2, n2 = 3, k2 = 6;
        System.out.println("Kth smallest 2: " + solution.findKthNumber(m2, n2, k2)); // 6

        // Test case 3
        int m3 = 9, n3 = 9, k3 = 81;
        System.out.println("Kth smallest 3: " + solution.findKthNumber(m3, n3, k3)); // 81

        // Test case 4
        int m4 = 9, n4 = 9, k4 = 1;
        System.out.println("Kth smallest 4: " + solution.findKthNumber(m4, n4, k4)); // 1
    }
}
