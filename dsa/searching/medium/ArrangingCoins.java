package searching.medium;

/**
 * LeetCode 441: Arranging Coins
 * https://leetcode.com/problems/arranging-coins/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: You have n coins and you want to build a staircase with these
 * coins.
 * The staircase consists of k rows where the ith row has exactly i coins.
 * Given n, return the number of complete rows of the staircase you will build.
 *
 * Constraints:
 * - 1 <= n <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. Can you find the number of remaining coins?
 * 2. What if we want to count incomplete rows as well?
 * 3. Can you handle different staircase patterns (e.g., rows with 1, 3, 5, ...
 * coins)?
 */
public class ArrangingCoins {

    // Approach 1: Binary Search - O(log n) time, O(1) space
    public int arrangeCoins(int n) {
        long left = 0, right = n;
        long result = 0;

        while (left <= right) {
            long mid = left + (right - left) / 2;
            long coinsNeeded = mid * (mid + 1) / 2;

            if (coinsNeeded <= n) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return (int) result;
    }

    // Approach 2: Mathematical Formula - O(1) time, O(1) space
    public int arrangeCoinsFormula(int n) {
        // The number of coins for k rows is k*(k+1)/2. We need to solve k*(k+1)/2 <= n.
        // This is a quadratic equation: k^2 + k - 2n <= 0.
        // The positive root is (-1 + sqrt(1 + 8n)) / 2.
        return (int) ((-1 + Math.sqrt(1 + 8.0 * n)) / 2);
    }

    // Follow-up 1: Return number of complete rows and remaining coins
    public int[] getRowsAndRemaining(int n) {
        int rows = arrangeCoins(n);
        long coinsUsed = (long) rows * (rows + 1) / 2;
        int remaining = (int) (n - coinsUsed);
        return new int[] { rows, remaining };
    }

    // Follow-up 2: Count total rows including the incomplete one
    public int countTotalRows(int n) {
        int completeRows = arrangeCoins(n);
        long coinsUsed = (long) completeRows * (completeRows + 1) / 2;
        return (n > coinsUsed) ? completeRows + 1 : completeRows;
    }

    // Follow-up 3: Handle different staircase patterns (e.g., arithmetic
    // progression)
    public int arrangeCoinsCustomPattern(int n, int start, int increment) {
        long left = 0, right = n;
        long result = 0;

        while (left <= right) {
            long mid = left + (right - left) / 2;
            if (mid == 0) {
                left = 1;
                continue;
            }
            // Sum of arithmetic series: k/2 * (2a + (k-1)d)
            long coinsNeeded = mid * (2L * start + (mid - 1) * increment) / 2;

            if (coinsNeeded <= n) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return (int) result;
    }

    public static void main(String[] args) {
        ArrangingCoins solution = new ArrangingCoins();

        // Test case 1: Basic case
        System.out.println("Test 1: n = 5");
        System.out.println("Expected: 2, Got (Binary Search): " + solution.arrangeCoins(5));
        System.out.println("Expected: 2, Got (Formula): " + solution.arrangeCoinsFormula(5));

        // Test case 2: Perfect staircase
        System.out.println("\nTest 2: n = 6 (Perfect staircase)");
        System.out.println("Expected: 3, Got: " + solution.arrangeCoins(6));

        // Test case 3: Large number
        System.out.println("\nTest 3: n = 1804289383");
        System.out.println("Expected: 60071, Got: " + solution.arrangeCoins(1804289383));

        // Edge case: n = 1
        System.out.println("\nEdge case: n = 1");
        System.out.println("Expected: 1, Got: " + solution.arrangeCoins(1));

        // Edge case: n = Integer.MAX_VALUE
        System.out.println("\nEdge case: n = Integer.MAX_VALUE");
        System.out.println("Expected: 65535, Got: " + solution.arrangeCoins(Integer.MAX_VALUE));

        // Follow-up 1: Rows and remaining coins
        System.out.println("\nFollow-up 1: Rows and Remaining for n=8");
        int[] result = solution.getRowsAndRemaining(8);
        System.out.println("Expected: [3, 2], Got: " + java.util.Arrays.toString(result));

        // Follow-up 2: Total rows including incomplete
        System.out.println("\nFollow-up 2: Total rows for n=8");
        System.out.println("Expected: 4, Got: " + solution.countTotalRows(8));
        System.out.println("Total rows for n=6: " + solution.countTotalRows(6));

        // Follow-up 3: Custom pattern (1, 3, 5, ...)
        System.out.println("\nFollow-up 3: Custom pattern (1,3,5...) for n=9");
        System.out.println("Expected: 3, Got: " + solution.arrangeCoinsCustomPattern(9, 1, 2));
    }
}
