package bitmanipulation.medium;

/**
 * LeetCode 191: Number of 1 Bits
 * https://leetcode.com/problems/number-of-1-bits/
 *
 * package bitmanipulation.medium;
 * 
 * /**
 * LeetCode 191: Number of 1 Bits
 * https://leetcode.com/problems/number-of-1-bits/
 *
 * Description:
 * Write a function that takes an unsigned integer and returns the number of '1'
 * bits it has.
 *
 * Constraints:
 * - The input must be a 32-bit unsigned integer.
 *
 * Follow-ups:
 * 1. Can you solve it in O(1) time? (Using bit tricks or lookup tables)
 * 2. Can you count bits for all numbers 0..n efficiently? (See LeetCode 338)
 */
public class NumberOf1Bits {
    /**
     * Main solution: Count bits by shifting
     */
    public int hammingWeight(int n) {
        int count = 0;
        for (int i = 0; i < 32; i++) {
            count += (n >>> i) & 1;
        }
        return count;
    }

    /**
     * Follow-up 1: O(1) time using bit tricks (Brian Kernighan's algorithm)
     */
    public int hammingWeightFast(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1);
            count++;
        }
        return count;
    }

    /**
     * Follow-up 2: Count bits for all numbers 0..n efficiently
     * Returns an array of bit counts for 0..n
     */
    public int[] countBitsUpToN(int n) {
        int[] res = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            res[i] = res[i >> 1] + (i & 1);
        }
        return res;
    }

    public static void main(String[] args) {
        NumberOf1Bits solution = new NumberOf1Bits();
        // Edge Case 1: Normal case
        System.out.println(solution.hammingWeight(11)); // 3
        // Edge Case 2: All bits set
        System.out.println(solution.hammingWeight(-1)); // 32
        // Edge Case 3: Zero
        System.out.println(solution.hammingWeight(0)); // 0

        // Follow-up 1: O(1) time (bit tricks)
        System.out.println(solution.hammingWeightFast(11)); // 3
        System.out.println(solution.hammingWeightFast(-1)); // 32
        System.out.println(solution.hammingWeightFast(0)); // 0

        // Follow-up 2: Count bits for all numbers 0..5
        int[] bits = solution.countBitsUpToN(5);
        System.out.println(java.util.Arrays.toString(bits)); // [0, 1, 1, 2, 1, 2]
    }
}
