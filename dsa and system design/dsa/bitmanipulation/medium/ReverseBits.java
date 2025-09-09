package bitmanipulation.medium;

/**
 * LeetCode 190: Reverse Bits
 * https://leetcode.com/problems/reverse-bits/
 *
 * Description:
 * Reverse bits of a given 32 bits unsigned integer.
 *
 * Constraints:
 * - The input must be a 32-bit unsigned integer.
 *
 * Follow-ups:
 * 1. Can you optimize to O(1) time using bit tricks or lookup tables?
 * 2. Can you reverse bits for arbitrary bit-widths?
 */
public class ReverseBits {
    /**
     * Main solution: Reverse bits by shifting
     */
    public int reverseBits(int n) {
        int res = 0;
        for (int i = 0; i < 32; i++) {
            res <<= 1;
            res |= (n & 1);
            n >>>= 1;
        }
        return res;
    }

    /**
     * Follow-up 1: O(1) time using bit tricks (swap halves, quarters, etc.)
     */
    public int reverseBitsFast(int n) {
        n = ((n >>> 16) | (n << 16));
        n = (((n & 0xff00ff00) >>> 8) | ((n & 0x00ff00ff) << 8));
        n = (((n & 0xf0f0f0f0) >>> 4) | ((n & 0x0f0f0f0f) << 4));
        n = (((n & 0xcccccccc) >>> 2) | ((n & 0x33333333) << 2));
        n = (((n & 0xaaaaaaaa) >>> 1) | ((n & 0x55555555) << 1));
        return n;
    }

    /**
     * Follow-up 2: Reverse bits for arbitrary bit-widths
     */
    public int reverseBitsWidth(int n, int width) {
        int res = 0;
        for (int i = 0; i < width; i++) {
            res <<= 1;
            res |= (n & 1);
            n >>>= 1;
        }
        return res;
    }

    public static void main(String[] args) {
        ReverseBits solution = new ReverseBits();
        // Edge Case 1: Normal case
        System.out.println(solution.reverseBits(43261596)); // 964176192
        // Edge Case 2: All bits set
        System.out.println(solution.reverseBits(-1)); // -1
        // Edge Case 3: Zero
        System.out.println(solution.reverseBits(0)); // 0

        // Follow-up 1: O(1) time (bit tricks)
        System.out.println(solution.reverseBitsFast(43261596)); // 964176192
        System.out.println(solution.reverseBitsFast(-1)); // -1
        System.out.println(solution.reverseBitsFast(0)); // 0

        // Follow-up 2: Arbitrary bit-width
        System.out.println(solution.reverseBitsWidth(5, 3)); // 5 -> 101 -> 101 (5)
        System.out.println(solution.reverseBitsWidth(5, 4)); // 5 -> 0101 -> 1010 (10)
    }
}
