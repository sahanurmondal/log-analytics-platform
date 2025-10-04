package bitmanipulation.medium;

/**
 * LeetCode 1869: Find Bitwise Parity
 * https://leetcode.com/problems/bitwise-parity/
 * Difficulty: Medium
 *
 * Description:
 * Given an integer, return true if it has even parity (even number of set
 * bits) and false if it has odd parity.
 * 
 * Example:
 * Input: n = 5 (binary: 101)
 * Output: false
 * Explanation: 5 has two 1s (odd number), so it has odd parity.
 *
 * Constraints:
 * - 0 <= n <= 2^31 - 1
 * 
 * Follow-up:
 * 1. Can you do it in O(1) time?
 * 2. Can you do it with minimal bit operations?
 * 3. What if you need to check parity of many numbers?
 */
public class FindBitwiseParity {

    // Approach 1: Brian Kernighan's algorithm - O(number of 1s) time
    public boolean hasEvenParity(int n) {
        boolean evenParity = true;
        while (n != 0) {
            n = n & (n - 1); // Clear the least significant 1
            evenParity = !evenParity;
        }
        return evenParity;
    }

    // Approach 2: Using lookup table - O(1) time with preprocessing
    private static final boolean[] PARITY_TABLE = new boolean[256];
    static {
        for (int i = 0; i < 256; i++) {
            PARITY_TABLE[i] = hasEvenParityNaive(i);
        }
    }

    public boolean hasEvenParityLookup(int n) {
        boolean evenParity = true;
        while (n != 0) {
            evenParity ^= PARITY_TABLE[n & 0xff];
            n >>>= 8;
        }
        return evenParity;
    }

    // Approach 3: XOR folding - O(1) time
    public boolean hasEvenParityXOR(int n) {
        n ^= n >>> 16;
        n ^= n >>> 8;
        n ^= n >>> 4;
        n &= 0xf;
        return ((0x6996 >>> n) & 1) == 0;
    }

    // Approach 4: Built-in function - O(1) time
    public boolean hasEvenParityBuiltin(int n) {
        return Integer.bitCount(n) % 2 == 0;
    }

    // Helper method for lookup table initialization
    private static boolean hasEvenParityNaive(int n) {
        int count = 0;
        while (n != 0) {
            count += n & 1;
            n >>>= 1;
        }
        return count % 2 == 0;
    }

    // Approach 5: Using carry-save adder concept - O(1) time
    public boolean hasEvenParityCSA(int n) {
        n ^= n >>> 1;
        n ^= n >>> 2;
        n = (n & 0x11111111) * 0x11111111;
        return ((n >>> 28) & 1) == 0;
    }

    public static void main(String[] args) {
        FindBitwiseParity solution = new FindBitwiseParity();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.hasEvenParity(5)); // false (101)

        // Test Case 2: Zero
        System.out.println("Test 2: " + solution.hasEvenParity(0)); // true

        // Test Case 3: Power of 2
        System.out.println("Test 3: " + solution.hasEvenParity(8)); // false (1000)

        // Test Case 4: All ones
        System.out.println("Test 4: " + solution.hasEvenParity(15)); // false (1111)

        // Test all approaches with various inputs
        int[] testCases = { 0, 1, 5, 8, 15, 16, 255, Integer.MAX_VALUE };

        System.out.println("\nTesting all approaches:");
        for (int test : testCases) {
            boolean result1 = solution.hasEvenParity(test);
            boolean result2 = solution.hasEvenParityLookup(test);
            boolean result3 = solution.hasEvenParityXOR(test);
            boolean result4 = solution.hasEvenParityBuiltin(test);
            boolean result5 = solution.hasEvenParityCSA(test);

            boolean consistent = result1 == result2 && result2 == result3 &&
                    result3 == result4 && result4 == result5;

            System.out.printf("Number %d: %b (consistent: %b)%n",
                    test, result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int iterations = 10000000;
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.hasEvenParity(i);
        }
        System.out.println("Brian Kernighan: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.hasEvenParityLookup(i);
        }
        System.out.println("Lookup Table: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.hasEvenParityXOR(i);
        }
        System.out.println("XOR Folding: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.hasEvenParityBuiltin(i);
        }
        System.out.println("Built-in: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.hasEvenParityCSA(i);
        }
        System.out.println("Carry-Save Adder: " + (System.currentTimeMillis() - start) + "ms");

        // Test edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Max int: " + solution.hasEvenParity(Integer.MAX_VALUE));
        System.out.println("Min int: " + solution.hasEvenParity(Integer.MIN_VALUE));
        System.out.println("Alternating bits: " + solution.hasEvenParity(0x55555555));
    }
}
