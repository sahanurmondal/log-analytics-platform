package bitmanipulation.medium;

/**
 * LeetCode 1009: Complement of Base 10 Integer
 * https://leetcode.com/problems/complement-of-base-10-integer/
 *
 * Description:
 * Given a positive integer n, return its complement.
 * The complement of a binary number is the number formed by flipping all bits
 * 0s to 1s and 1s to 0s.
 *
 * Example:
 * Input: n = 5 (101 in binary)
 * Output: 2 (010 in binary)
 *
 * Constraints:
 * - 0 <= n <= 10^9
 * 
 * Follow-up:
 * 1. Can you do it without converting to string?
 * 2. Can you do it with minimal bit operations?
 * 3. How would you handle negative numbers?
 */
public class BitwiseComplement {

    // Approach 1: Using XOR with mask - O(log n) time, O(1) space
    public int bitwiseComplement(int n) {
        if (n == 0)
            return 1;

        // Find the number of bits needed
        int mask = n;
        mask |= mask >> 1;
        mask |= mask >> 2;
        mask |= mask >> 4;
        mask |= mask >> 8;
        mask |= mask >> 16;

        return n ^ mask;
    }

    // Approach 2: Using Integer.highestOneBit - O(1) time, O(1) space
    public int bitwiseComplementOptimal(int n) {
        if (n == 0)
            return 1;
        int highestBit = Integer.highestOneBit(n);
        int mask = (highestBit << 1) - 1;
        return n ^ mask;
    }

    // Approach 3: Using Log base 2 - O(1) time, O(1) space
    public int bitwiseComplementLog(int n) {
        if (n == 0)
            return 1;
        int bitsNeeded = (int) (Math.log(n) / Math.log(2)) + 1;
        int mask = (1 << bitsNeeded) - 1;
        return n ^ mask;
    }

    // Follow-up: Handle negative numbers
    public int bitwiseComplementWithNegatives(int n) {
        if (n == 0)
            return 1;
        if (n < 0) {
            // For negative numbers, complement only the bits after sign bit
            int mask = Integer.highestOneBit(n) - 1;
            return n ^ mask;
        }
        return bitwiseComplement(n);
    }

    public static void main(String[] args) {
        BitwiseComplement solution = new BitwiseComplement();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.bitwiseComplement(5)); // 2

        // Test Case 2: Zero
        System.out.println("Test 2: " + solution.bitwiseComplement(0)); // 1

        // Test Case 3: Powers of 2
        System.out.println("Test 3: " + solution.bitwiseComplement(8)); // 7

        // Test Case 4: All 1s
        System.out.println("Test 4: " + solution.bitwiseComplement(7)); // 0

        // Test optimal approach
        System.out.println("Optimal 5: " + solution.bitwiseComplementOptimal(5)); // 2

        // Test log approach
        System.out.println("Log 5: " + solution.bitwiseComplementLog(5)); // 2

        // Test negative numbers (follow-up)
        System.out.println("Negative -5: " + solution.bitwiseComplementWithNegatives(-5));

        // Test large numbers
        System.out.println("Large: " + solution.bitwiseComplement(1000000000));

        // Performance test
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            solution.bitwiseComplement(i);
        }
        System.out.println("Performance test: " + (System.currentTimeMillis() - start) + "ms");

        // Verify all approaches give same results
        int[] testCases = { 0, 1, 5, 7, 8, 15, 1000 };
        boolean allMatch = true;
        for (int test : testCases) {
            int result1 = solution.bitwiseComplement(test);
            int result2 = solution.bitwiseComplementOptimal(test);
            int result3 = solution.bitwiseComplementLog(test);
            if (result1 != result2 || result2 != result3) {
                allMatch = false;
                System.out.println("Mismatch for " + test + ": " +
                        result1 + ", " + result2 + ", " + result3);
            }
        }
        System.out.println("All approaches consistent: " + allMatch);
    }
}
