package bitmanipulation.medium;

/**
 * LeetCode 201: Bitwise OR of Numbers Range
 * https://leetcode.com/problems/bitwise-or-of-numbers-range/
 * Difficulty: Medium
 *
 * Description:
 * Given two integers left and right, return the bitwise OR of all numbers in
 * this
 * range [left, right], inclusive.
 * 
 * Example:
 * Input: left = 5, right = 7
 * Output: 7
 * Explanation:
 * 5 -> 101
 * 6 -> 110
 * 7 -> 111
 * OR = 111 (7)
 *
 * Constraints:
 * - 0 <= left <= right <= 2^31 - 1
 * 
 * Follow-up:
 * 1. Can you solve it without iterating through all numbers in the range?
 * 2. What's the difference between this and AND of range?
 * 3. Can you optimize for very large ranges?
 */
public class FindBitwiseOROfNumbersRange {

    // Approach 1: Pattern observation - O(log n) time, O(1) space
    public int rangeBitwiseOr(int left, int right) {
        int result = right;
        for (int i = left; i < right; i++) {
            if ((long) i + (1L << Integer.numberOfTrailingZeros(~i)) > right) {
                result |= i;
            }
        }
        return result;
    }

    // Approach 2: Bit by bit - O(32) time, O(1) space
    public int rangeBitwiseOrBitByBit(int left, int right) {
        int result = 0;
        for (int bit = 30; bit >= 0; bit--) {
            int mask = 1 << bit;
            if ((right & mask) > 0 &&
                    (right - left + 1) > (right & ((1 << bit) - 1)) + 1) {
                result |= mask;
            } else if ((left & mask) > 0 || (right & mask) > 0) {
                result |= mask;
            }
        }
        return result;
    }

    // Approach 3: Common prefix - O(log n) time, O(1) space
    public int rangeBitwiseOrPrefix(int left, int right) {
        if (left == right)
            return left;

        // Get the highest different bit position
        int xor = left ^ right;
        int highestBit = 31 - Integer.numberOfLeadingZeros(xor);

        // Set all bits from 0 to highestBit
        int mask = (1 << (highestBit + 1)) - 1;
        return (left & ~mask) | mask;
    }

    // Approach 4: Jump approach - O(log n) time, O(1) space
    public int rangeBitwiseOrJump(int left, int right) {
        while (left < right) {
            right = right & (right - 1);
        }
        return left | right;
    }

    public static void main(String[] args) {
        FindBitwiseOROfNumbersRange solution = new FindBitwiseOROfNumbersRange();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.rangeBitwiseOr(5, 7)); // 7

        // Test Case 2: Same numbers
        System.out.println("Test 2: " + solution.rangeBitwiseOr(8, 8)); // 8

        // Test Case 3: Power of 2 range
        System.out.println("Test 3: " + solution.rangeBitwiseOr(16, 19)); // 19

        // Test Case 4: Large range
        System.out.println("Test 4: " + solution.rangeBitwiseOr(1000000000, 2147483646));

        // Test all approaches
        int[][] testCases = {
                { 5, 7 }, // Normal case
                { 8, 8 }, // Same numbers
                { 0, 1 }, // Range with zero
                { 16, 19 }, // Power of 2 range
                { 600000000, 2147483645 } // Large range
        };

        System.out.println("\nTesting all approaches:");
        for (int[] test : testCases) {
            int result1 = solution.rangeBitwiseOr(test[0], test[1]);
            int result2 = solution.rangeBitwiseOrBitByBit(test[0], test[1]);
            int result3 = solution.rangeBitwiseOrPrefix(test[0], test[1]);
            int result4 = solution.rangeBitwiseOrJump(test[0], test[1]);

            boolean consistent = result1 == result2 && result2 == result3 && result3 == result4;
            System.out.printf("Range [%d, %d]: %d (consistent: %b)%n",
                    test[0], test[1], result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int iterations = 1000000;
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseOr(600000000, 2147483645);
        }
        System.out.println("Pattern: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseOrBitByBit(600000000, 2147483645);
        }
        System.out.println("Bit by bit: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseOrPrefix(600000000, 2147483645);
        }
        System.out.println("Prefix: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseOrJump(600000000, 2147483645);
        }
        System.out.println("Jump: " + (System.currentTimeMillis() - start) + "ms");

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Max range: " +
                solution.rangeBitwiseOr(0, Integer.MAX_VALUE)); // All 1s except sign bit
        System.out.println("Single bit: " +
                solution.rangeBitwiseOr(16, 16)); // Same as input
        System.out.println("Adjacent powers: " +
                solution.rangeBitwiseOr(16, 32)); // Bits between set
    }
}
