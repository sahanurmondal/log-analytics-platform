package dp.easy;

import java.util.Arrays;

/**
 * LeetCode 338: Counting Bits
 * https://leetcode.com/problems/counting-bits/
 *
 * Description:
 * Given an integer n, return an array ans of length n + 1 such that for each i
 * (0 <= i <= n),
 * ans[i] is the number of 1's in the binary representation of i.
 *
 * Constraints:
 * - 0 <= n <= 10^5
 *
 * Follow-up:
 * - Can you do it in O(n) time and O(1) space?
 *
 * Company Tags: Google, Amazon, Microsoft, Apple
 * Difficulty: Easy
 */
public class CountingBits {

    // Approach 1: DP with Bit Manipulation - O(n) time, O(1) space
    public int[] countBits(int n) {
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i >> 1] + (i & 1);
        }

        return dp;
    }

    // Approach 2: DP with Power of 2 - O(n) time, O(1) space
    public int[] countBitsPowerOf2(int n) {
        int[] dp = new int[n + 1];
        int powerOf2 = 1;

        for (int i = 1; i <= n; i++) {
            if (i == powerOf2 * 2) {
                powerOf2 *= 2;
            }
            dp[i] = dp[i - powerOf2] + 1;
        }

        return dp;
    }

    // Approach 3: Brian Kernighan's Algorithm - O(n log n) time, O(1) space
    public int[] countBitsBK(int n) {
        int[] result = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            result[i] = hammingWeight(i);
        }

        return result;
    }

    private int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1); // Clear the lowest set bit
            count++;
        }
        return count;
    }

    public static void main(String[] args) {
        CountingBits solution = new CountingBits();

        System.out.println("=== Counting Bits Test Cases ===");

        int n1 = 5;
        System.out.println("n = " + n1);
        System.out.println("DP: " + Arrays.toString(solution.countBits(n1)));
        System.out.println("Power of 2: " + Arrays.toString(solution.countBitsPowerOf2(n1)));
        System.out.println("Expected: [0, 1, 1, 2, 1, 2]\n");
    }
}
