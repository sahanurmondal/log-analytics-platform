package bitmanipulation.medium;

/**
 * LeetCode 1835: Bitwise XOR of Numbers Range
 * https://leetcode.com/problems/find-xor-of-numbers-range/
 * Difficulty: Medium
 *
 * Description:
 * Given two integers left and right, return the bitwise XOR of all numbers in
 * this
 * range [left, right], inclusive.
 * 
 * Example:
 * Input: left = 5, right = 7
 * Output: 7
 * Explanation:
 * 5 XOR 6 XOR 7 = (101) XOR (110) XOR (111) = 111 = 7
 *
 * Constraints:
 * - 0 <= left <= right <= 2^31 - 1
 * 
 * Follow-up:
 * 1. Can you solve it without iteration?
 * 2. Can you find a pattern for XOR from 1 to n?
 * 3. How would you handle very large ranges efficiently?
 */
public class FindBitwiseXOROfNumbersRange {

    // Approach 1: Pattern observation - O(1) time, O(1) space
    public int rangeBitwiseXor(int left, int right) {
        return computeXor(right) ^ computeXor(left - 1);
    }

    // Helper method to compute XOR from 1 to n
    private int computeXor(int n) {
        if (n < 0)
            return 0;
        switch (n % 4) {
            case 0:
                return n;
            case 1:
                return 1;
            case 2:
                return n + 1;
            case 3:
                return 0;
        }
        return 0; // Unreachable
    }

    // Approach 2: Common prefix - O(log n) time, O(1) space
    public int rangeBitwiseXorPrefix(int left, int right) {
        if (left == right)
            return left;

        int xor = 0;
        while (left != right) {
            xor ^= left ^ right;
            left++;
            right--;
            if (left > right)
                break;
        }
        return xor ^ (left == right ? left : 0);
    }

    // Approach 3: Bit manipulation - O(32) time, O(1) space
    public int rangeBitwiseXorBitwise(int left, int right) {
        int xor = 0;
        for (int bit = 30; bit >= 0; bit--) {
            int mask = 1 << bit;
            if ((right & mask) > (left & mask)) {
                int count = (right - left + 1) >> bit;
                if (count % 2 == 1)
                    xor |= mask;
            } else if ((right & mask) == (left & mask)) {
                xor |= right & mask;
            }
        }
        return xor;
    }

    // Approach 4: Using Gray Code properties - O(1) time, O(1) space
    public int rangeBitwiseXorGrayCode(int left, int right) {
        return grayCode(right) ^ grayCode(left - 1);
    }

    private int grayCode(int n) {
        if (n < 0)
            return 0;
        return n ^ (n >> 1);
    }

    public static void main(String[] args) {
        FindBitwiseXOROfNumbersRange solution = new FindBitwiseXOROfNumbersRange();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.rangeBitwiseXor(5, 7)); // 7

        // Test Case 2: Same numbers
        System.out.println("Test 2: " + solution.rangeBitwiseXor(8, 8)); // 8

        // Test Case 3: Power of 2 range
        System.out.println("Test 3: " + solution.rangeBitwiseXor(16, 19));

        // Test Case 4: Large range
        System.out.println("Test 4: " + solution.rangeBitwiseXor(1000000000, 2147483646));

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
            int result1 = solution.rangeBitwiseXor(test[0], test[1]);
            int result2 = solution.rangeBitwiseXorPrefix(test[0], test[1]);
            int result3 = solution.rangeBitwiseXorBitwise(test[0], test[1]);
            int result4 = solution.rangeBitwiseXorGrayCode(test[0], test[1]);

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
            solution.rangeBitwiseXor(600000000, 2147483645);
        }
        System.out.println("Pattern: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseXorPrefix(600000000, 2147483645);
        }
        System.out.println("Prefix: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseXorBitwise(600000000, 2147483645);
        }
        System.out.println("Bitwise: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.rangeBitwiseXorGrayCode(600000000, 2147483645);
        }
        System.out.println("Gray Code: " + (System.currentTimeMillis() - start) + "ms");

        // Verify special properties
        System.out.println("\nSpecial cases:");
        // XOR of all numbers up to power of 2
        System.out.println("Power of 2 boundary: " + solution.rangeBitwiseXor(0, 16));
        // XOR of numbers with same number of bits
        System.out.println("Same bit length: " + solution.rangeBitwiseXor(8, 15));
        // XOR of alternating sequence
        System.out.println("Alternating sequence: " + solution.rangeBitwiseXor(5, 8));
    }
}
