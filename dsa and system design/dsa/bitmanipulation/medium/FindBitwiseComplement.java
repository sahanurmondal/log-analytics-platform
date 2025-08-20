package bitmanipulation.medium;

/**
 * LeetCode 1009: Complement of Base 10 Integer
 * https://leetcode.com/problems/complement-of-base-10-integer/
 *
 * Description:
 * Given a positive integer n, return its bitwise complement.
 * The bitwise complement of a number is the result of flipping every bit in its
 * binary representation.
 * 
 * Example:
 * Input: n = 5 (binary: 101)
 * Output: 2 (binary: 010)
 * 
 * Constraints:
 * - 0 <= n <= 10^9
 * 
 * Follow-up:
 * 1. Can you solve it without using String operations?
 * 2. Can you solve it with minimal bit manipulations?
 * 3. What's the difference between this and BitwiseComplement?
 */
public class FindBitwiseComplement {

    // Approach 1: Using bit mask - O(log n) time, O(1) space
    public int bitwiseComplement(int n) {
        if (n == 0)
            return 1;

        // Create mask with all 1s in significant bits
        int mask = n;
        mask |= mask >> 1;
        mask |= mask >> 2;
        mask |= mask >> 4;
        mask |= mask >> 8;
        mask |= mask >> 16;

        return n ^ mask;
    }

    // Approach 2: Using highest one bit - O(1) time, O(1) space
    public int bitwiseComplementHighestBit(int n) {
        if (n == 0)
            return 1;
        int highestBit = Integer.highestOneBit(n);
        int mask = (highestBit << 1) - 1;
        return n ^ mask;
    }

    // Approach 3: Using binary string - O(log n) time, O(log n) space
    public int bitwiseComplementString(int n) {
        if (n == 0)
            return 1;
        String binary = Integer.toBinaryString(n);
        StringBuilder complement = new StringBuilder();
        for (char c : binary.toCharArray()) {
            complement.append(c == '0' ? '1' : '0');
        }
        return Integer.parseInt(complement.toString(), 2);
    }

    // Approach 4: Using logarithm - O(1) time, O(1) space
    public int bitwiseComplementLog(int n) {
        if (n == 0)
            return 1;
        int bits = (int) (Math.log(n) / Math.log(2)) + 1;
        int mask = (1 << bits) - 1;
        return n ^ mask;
    }

    public static void main(String[] args) {
        FindBitwiseComplement solution = new FindBitwiseComplement();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.bitwiseComplement(5)); // 2

        // Test Case 2: Zero
        System.out.println("Test 2: " + solution.bitwiseComplement(0)); // 1

        // Test Case 3: Power of 2
        System.out.println("Test 3: " + solution.bitwiseComplement(8)); // 7

        // Test Case 4: Large number
        System.out.println("Test 4: " + solution.bitwiseComplement(1000000000));

        // Test all approaches
        int[] testCases = { 0, 1, 5, 7, 8, 15, 1000000000 };
        System.out.println("\nTesting all approaches:");
        for (int test : testCases) {
            int result1 = solution.bitwiseComplement(test);
            int result2 = solution.bitwiseComplementHighestBit(test);
            int result3 = solution.bitwiseComplementString(test);
            int result4 = solution.bitwiseComplementLog(test);

            boolean consistent = result1 == result2 && result2 == result3 && result3 == result4;
            System.out.printf("Number %d: %d (consistent: %b)%n", test, result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int iterations = 1000000;
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.bitwiseComplement(1000000000);
        }
        System.out.println("Bit mask: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.bitwiseComplementHighestBit(1000000000);
        }
        System.out.println("Highest bit: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.bitwiseComplementString(1000000000);
        }
        System.out.println("String: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.bitwiseComplementLog(1000000000);
        }
        System.out.println("Logarithm: " + (System.currentTimeMillis() - start) + "ms");

        // Verify handling of edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Max int: " + solution.bitwiseComplement(Integer.MAX_VALUE));
        System.out.println("Powers of 2: " + solution.bitwiseComplement(1024));
        System.out.println("All ones: " + solution.bitwiseComplement(255));
    }
}
