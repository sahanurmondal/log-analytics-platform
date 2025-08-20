package binarysearch.easy;

/**
 * LeetCode 441: Arranging Coins
 * https://leetcode.com/problems/arranging-coins/
 *
 * Description:
 * You have n coins and you want to build a staircase with these coins.
 * The staircase consists of k rows where the ith row has exactly i coins.
 * The last row of the staircase may be incomplete.
 * Given the integer n, return the number of complete rows of the staircase you
 * will build.
 *
 * Companies: Google, Microsoft, Amazon, Apple, Adobe, Uber
 * Difficulty: Easy
 * Asked: 2023-2024 (Medium Frequency)
 *
 * Constraints:
 * - 1 <= n <= 2^31 - 1
 *
 * Follow-ups:
 * - Can you solve this without using floating-point arithmetic?
 * - How would you optimize for very large values of n?
 * - What if we want to find the exact number of coins remaining?
 */
public class ArrangingCoins {

    // Binary Search solution - O(log n) time, O(1) space
    public int arrangeCoins(int n) {
        long left = 0;
        long right = n;

        while (left <= right) {
            long mid = left + (right - left) / 2;
            long coinsNeeded = mid * (mid + 1) / 2;

            if (coinsNeeded == n) {
                return (int) mid;
            } else if (coinsNeeded < n) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return (int) right;
    }

    // Mathematical solution using quadratic formula - O(1) time
    public int arrangeCoinsMath(int n) {
        // k(k+1)/2 <= n
        // k^2 + k - 2n <= 0
        // Using quadratic formula: k = (-1 + sqrt(1 + 8n)) / 2
        return (int) ((-1 + Math.sqrt(1 + 8.0 * n)) / 2);
    }

    // Newton's method - O(log log n) time
    public int arrangeCoinsNewton(int n) {
        if (n == 0)
            return 0;

        long x = n;
        while (true) {
            long nx = (x + n / x) / 2;
            if (Math.abs(nx - x) < 1)
                break;
            x = nx;
        }

        // Now we have approximate sqrt(2n), need to find k
        long k = (long) ((-1 + Math.sqrt(1 + 8.0 * n)) / 2);

        // Verify and adjust if necessary
        if (k * (k + 1) / 2 <= n && (k + 1) * (k + 2) / 2 > n) {
            return (int) k;
        } else if (k * (k + 1) / 2 > n) {
            return (int) (k - 1);
        } else {
            return (int) (k + 1);
        }
    }

    // Brute force solution - O(sqrt(n)) time
    public int arrangeCoinsBruteForce(int n) {
        int k = 0;
        long sum = 0;

        while (sum + k + 1 <= n) {
            k++;
            sum += k;
        }

        return k;
    }

    // Binary search with template pattern
    public int arrangeCoinsTemplate(int n) {
        long left = 0;
        long right = (long) n + 1; // Upper bound

        // Find the largest k such that k*(k+1)/2 <= n
        while (left < right) {
            long mid = left + (right - left) / 2;
            long coinsNeeded = mid * (mid + 1) / 2;

            if (coinsNeeded <= n) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return (int) left - 1;
    }

    // Solution that also returns remaining coins
    public int[] arrangeCoinsWithRemaining(int n) {
        int completeRows = arrangeCoins(n);
        long usedCoins = (long) completeRows * (completeRows + 1) / 2;
        int remaining = (int) (n - usedCoins);

        return new int[] { completeRows, remaining };
    }

    public static void main(String[] args) {
        ArrangingCoins solution = new ArrangingCoins();

        // Test Case 1: n = 5 (1+2 = 3 <= 5, 1+2+3 = 6 > 5)
        System.out.println(solution.arrangeCoins(5)); // Expected: 2

        // Test Case 2: n = 8 (1+2+3 = 6 <= 8, 1+2+3+4 = 10 > 8)
        System.out.println(solution.arrangeCoins(8)); // Expected: 3

        // Test Case 3: n = 1
        System.out.println(solution.arrangeCoins(1)); // Expected: 1

        // Test Case 4: n = 3 (perfect triangle number)
        System.out.println(solution.arrangeCoins(3)); // Expected: 2

        // Test Case 5: n = 6 (perfect triangle number)
        System.out.println(solution.arrangeCoins(6)); // Expected: 3

        // Test Case 6: n = 10 (perfect triangle number)
        System.out.println(solution.arrangeCoins(10)); // Expected: 4

        // Test mathematical solution
        System.out.println("Math: " + solution.arrangeCoinsMath(8)); // Expected: 3

        // Test Newton's method
        System.out.println("Newton: " + solution.arrangeCoinsNewton(8)); // Expected: 3

        // Test brute force
        System.out.println("Brute Force: " + solution.arrangeCoinsBruteForce(8)); // Expected: 3

        // Test template pattern
        System.out.println("Template: " + solution.arrangeCoinsTemplate(8)); // Expected: 3

        // Test with remaining coins
        int[] result = solution.arrangeCoinsWithRemaining(8);
        System.out.println("Complete rows: " + result[0] + ", Remaining coins: " + result[1]);
        // Expected: Complete rows: 3, Remaining coins: 2

        // Large test case
        System.out.println("Large test: " + solution.arrangeCoins(1804289383)); // Large number test

        // Edge cases
        System.out.println("Edge case n=0: " + solution.arrangeCoins(0)); // Expected: 0 (but constraint says n >= 1)
        System.out.println("Edge case n=2^31-1: " + solution.arrangeCoins(Integer.MAX_VALUE)); // Large test
    }
}
