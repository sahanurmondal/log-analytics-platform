package bitmanipulation.medium;

/**
 * Problem: Find Number of Bits to Flip
 * LeetCode 461: Hamming Distance (Modified)
 * https://leetcode.com/problems/hamming-distance/
 *
 * Description:
 * Given two integers m and n, return the number of bits that need to be flipped
 * to convert m to n.
 * 
 * Example:
 * Input: m = 29 (11101), n = 15 (01111)
 * Output: 2
 * Explanation: Need to flip 2 bits (positions 0 and 4)
 *
 * Constraints:
 * - 0 <= m, n <= 2^31 - 1
 * 
 * Follow-up:
 * 1. Can you do it with minimal bit operations?
 * 2. What if you need to do this for many pairs?
 * 3. Can you use lookup tables to optimize?
 */
public class FindNumberOfBitsToFlip {

    // Approach 1: Using XOR and bit counting - O(log n) time, O(1) space
    public int countBitsToFlip(int m, int n) {
        return Integer.bitCount(m ^ n);
    }

    // Approach 2: Brian Kernighan's algorithm - O(number of 1s) time, O(1) space
    public int countBitsToFlipKernighan(int m, int n) {
        int xor = m ^ n;
        int count = 0;

        // Count set bits in xor
        while (xor != 0) {
            xor &= (xor - 1); // Clear least significant set bit
            count++;
        }

        return count;
    }

    // Approach 3: Using lookup table - O(1) time with preprocessing, O(256) space
    private static final byte[] BIT_COUNT_TABLE = new byte[256];
    static {
        for (int i = 0; i < 256; i++) {
            BIT_COUNT_TABLE[i] = (byte) Integer.bitCount(i);
        }
    }

    public int countBitsToFlipLookup(int m, int n) {
        int xor = m ^ n;
        return BIT_COUNT_TABLE[xor & 0xff] +
                BIT_COUNT_TABLE[(xor >>> 8) & 0xff] +
                BIT_COUNT_TABLE[(xor >>> 16) & 0xff] +
                BIT_COUNT_TABLE[(xor >>> 24) & 0xff];
    }

    // Approach 4: Bit manipulation without built-in function - O(32) time, O(1)
    // space
    public int countBitsToFlipManual(int m, int n) {
        int xor = m ^ n;
        int count = 0;

        for (int i = 0; i < 32; i++) {
            count += (xor >> i) & 1;
        }

        return count;
    }

    // Approach 5: Parallel counting - O(1) time, O(1) space
    public int countBitsToFlipParallel(int m, int n) {
        int xor = m ^ n;

        // Parallel bit counting using magic numbers
        xor = ((xor & 0xAAAAAAAA) >>> 1) + (xor & 0x55555555);
        xor = ((xor & 0xCCCCCCCC) >>> 2) + (xor & 0x33333333);
        xor = ((xor & 0xF0F0F0F0) >>> 4) + (xor & 0x0F0F0F0F);
        xor = ((xor & 0xFF00FF00) >>> 8) + (xor & 0x00FF00FF);
        xor = ((xor & 0xFFFF0000) >>> 16) + (xor & 0x0000FFFF);

        return xor;
    }

    public static void main(String[] args) {
        FindNumberOfBitsToFlip solution = new FindNumberOfBitsToFlip();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.countBitsToFlip(29, 15)); // 2

        // Test Case 2: Same numbers
        System.out.println("Test 2: " + solution.countBitsToFlip(8, 8)); // 0

        // Test Case 3: Zero and one
        System.out.println("Test 3: " + solution.countBitsToFlip(0, 1)); // 1

        // Test Case 4: Powers of 2
        System.out.println("Test 4: " + solution.countBitsToFlip(16, 24)); // 3

        // Test all approaches
        int[][] testCases = {
                { 29, 15 }, // Normal case
                { 8, 8 }, // Same numbers
                { 0, 1 }, // Minimal difference
                { 16, 24 }, // Powers of 2
                { 0, Integer.MAX_VALUE } // Maximum difference
        };

        System.out.println("\nTesting all approaches:");
        for (int[] test : testCases) {
            int result1 = solution.countBitsToFlip(test[0], test[1]);
            int result2 = solution.countBitsToFlipKernighan(test[0], test[1]);
            int result3 = solution.countBitsToFlipLookup(test[0], test[1]);
            int result4 = solution.countBitsToFlipManual(test[0], test[1]);
            int result5 = solution.countBitsToFlipParallel(test[0], test[1]);

            boolean consistent = result1 == result2 && result2 == result3 &&
                    result3 == result4 && result4 == result5;
            System.out.printf("Numbers %d, %d: %d (consistent: %b)%n",
                    test[0], test[1], result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int iterations = 10000000;
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.countBitsToFlip(600000000, 2147483645);
        }
        System.out.println("Built-in: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.countBitsToFlipKernighan(600000000, 2147483645);
        }
        System.out.println("Kernighan: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.countBitsToFlipLookup(600000000, 2147483645);
        }
        System.out.println("Lookup: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.countBitsToFlipManual(600000000, 2147483645);
        }
        System.out.println("Manual: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.countBitsToFlipParallel(600000000, 2147483645);
        }
        System.out.println("Parallel: " + (System.currentTimeMillis() - start) + "ms");

        // Special patterns
        System.out.println("\nSpecial patterns:");
        System.out.println("Alternating bits: " +
                solution.countBitsToFlip(0x55555555, 0xAAAAAAAA)); // 32
        System.out.println("Sparse to dense: " +
                solution.countBitsToFlip(0x11111111, 0xFFFFFFFF)); // 24
        System.out.println("Lower to upper half: " +
                solution.countBitsToFlip(0x0000FFFF, 0xFFFF0000)); // 32
    }
}
