package bitmanipulation.medium;

/**
 * LeetCode 201: Bitwise AND of Numbers Range
 * https://leetcode.com/problems/bitwise-and-of-numbers-range/
 *
 * Description:
 * Given two integers left and right, return the bitwise AND of all numbers in
 * the range [left, right] inclusive.
 * 
 * Example:
 * Input: left = 5, right = 7
 * Output: 4
 * Explanation:
 * 5 -> 101
 * 6 -> 110
 * 7 -> 111
 * AND = 100 (4)
 *
 * Constraints:
 * - 0 <= left <= right <= 2^31 - 1
 * 
 * Follow-up:
 * 1. Can you solve it without loops?
 * 2. Can you find the pattern in the common prefix?
 * 3. What happens when the range is very large?
 */
public class FindBitwiseANDOfNumbersRange {

    // Approach 1: Common prefix approach - O(1) time, O(1) space
    public int rangeBitwiseAnd(int left, int right) {
        int shift = 0;

        // Find common prefix
        while (left < right) {
            left >>= 1;
            right >>= 1;
            shift++;
        }

        return left << shift;
    }

    // Approach 2: Brian Kernighan's algorithm - O(1) time, O(1) space
    public int rangeBitwiseAndKernighan(int left, int right) {
        while (right > left) {
            right &= (right - 1);
        }
        return right;
    }

    // Approach 3: Mask approach - O(1) time, O(1) space
    public int rangeBitwiseAndMask(int left, int right) {
        int mask = Integer.MAX_VALUE;
        while ((left & mask) != (right & mask)) {
            mask <<= 1;
        }
        return right & mask;
    }

    // Approach 4: Using Integer.highestOneBit - O(1) time, O(1) space
    public int rangeBitwiseAndHighestBit(int left, int right) {
        if (left == 0)
            return 0;

        int highestBitLeft = Integer.highestOneBit(left);
        int highestBitRight = Integer.highestOneBit(right);

        if (highestBitLeft != highestBitRight)
            return 0;

        int result = highestBitLeft;
        left -= highestBitLeft;
        right -= highestBitLeft;

        return result | rangeBitwiseAnd(left, right);
    }

    public static void main(String[] args) {
        FindBitwiseANDOfNumbersRange solution = new FindBitwiseANDOfNumbersRange();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.rangeBitwiseAnd(5, 7)); // 4

        // Test Case 2: Same numbers
        System.out.println("Test 2: " + solution.rangeBitwiseAnd(8, 8)); // 8

        // Test Case 3: Range with zero
        System.out.println("Test 3: " + solution.rangeBitwiseAnd(0, 1)); // 0

        // Test Case 4: Power of 2 range
        System.out.println("Test 4: " + solution.rangeBitwiseAnd(16, 19)); // 16

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
            int result1 = solution.rangeBitwiseAnd(test[0], test[1]);
            int result2 = solution.rangeBitwiseAndKernighan(test[0], test[1]);
            int result3 = solution.rangeBitwiseAndMask(test[0], test[1]);
            int result4 = solution.rangeBitwiseAndHighestBit(test[0], test[1]);

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
            solution.rangeBitwiseAnd(600000000, 2147483645);
        }
        System.out.println("Common prefix: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseAndKernighan(600000000, 2147483645);
        }
        System.out.println("Kernighan: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseAndMask(600000000, 2147483645);
        }
        System.out.println("Mask: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseAndHighestBit(600000000, 2147483645);
        }
        System.out.println("Highest bit: " + (System.currentTimeMillis() - start) + "ms");
    }
}
