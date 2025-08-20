package dp.medium;

/**
 * LeetCode 357: Count Numbers with Unique Digits
 * https://leetcode.com/problems/count-numbers-with-unique-digits/
 *
 * Description:
 * Given an integer n, return the count of all numbers with unique digits, x,
 * where 0 <= x < 10^n.
 *
 * Constraints:
 * - 0 <= n <= 8
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * 
 * Company Tags: Google, Microsoft
 * Difficulty: Medium
 */
public class CountNumbersWithUniqueDigits {

    // Approach 1: Mathematical Permutation - O(n) time, O(1) space
    public int countNumbersWithUniqueDigits(int n) {
        if (n == 0)
            return 1;
        if (n == 1)
            return 10;

        int result = 10; // Count for n = 1
        int uniqueDigits = 9; // First digit can't be 0
        int availableDigits = 9; // Remaining digits for subsequent positions

        for (int i = 2; i <= n; i++) {
            uniqueDigits *= availableDigits;
            result += uniqueDigits;
            availableDigits--;
        }

        return result;
    }

    // Approach 2: DP - O(n) time, O(n) space
    public int countNumbersWithUniqueDigitsDP(int n) {
        if (n == 0)
            return 1;

        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 10;

        for (int i = 2; i <= n; i++) {
            int count = 9; // First digit can't be 0
            for (int j = 0; j < i - 1; j++) {
                count *= (9 - j); // Remaining available digits
            }
            dp[i] = dp[i - 1] + count;
        }

        return dp[n];
    }

    // Approach 3: Recursive with Memoization - O(n) time, O(n) space
    public int countNumbersWithUniqueDigitsMemo(int n) {
        Integer[] memo = new Integer[n + 1];
        return countHelper(n, memo);
    }

    private int countHelper(int n, Integer[] memo) {
        if (n == 0)
            return 1;
        if (n == 1)
            return 10;

        if (memo[n] != null)
            return memo[n];

        // Count for length n
        int count = 9;
        for (int i = 0; i < n - 1; i++) {
            count *= (9 - i);
        }

        memo[n] = countHelper(n - 1, memo) + count;
        return memo[n];
    }

    public static void main(String[] args) {
        CountNumbersWithUniqueDigits solution = new CountNumbersWithUniqueDigits();

        System.out.println("=== Count Numbers with Unique Digits Test Cases ===");

        // Test all approaches for each case
        for (int n = 0; n <= 8; n++) {
            System.out.println("n = " + n + ":");
            System.out.println("  Mathematical: " + solution.countNumbersWithUniqueDigits(n));
            System.out.println("  DP: " + solution.countNumbersWithUniqueDigitsDP(n));
            System.out.println("  Memoization: " + solution.countNumbersWithUniqueDigitsMemo(n));
        }

        // Expected results
        System.out.println("\nExpected results:");
        System.out.println("n=0: 1, n=1: 10, n=2: 91, n=3: 739");
        System.out.println("n=4: 5275, n=5: 32491, n=6: 168571");
        System.out.println("n=7: 712891, n=8: 2345851");
    }
}
