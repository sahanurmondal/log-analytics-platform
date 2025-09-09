package bitmanipulation.easy;

/**
 * LeetCode 693: Binary Number with Alternating Bits
 * https://leetcode.com/problems/binary-number-with-alternating-bits/
 *
 * Description: Given a positive integer, check whether it has alternating bits:
 * namely, if two adjacent bits will always have different values.
 * 
 * Constraints:
 * - 1 <= n <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it using bit manipulation tricks?
 * - What about mathematical approach?
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Right shift: Compare current bit with next bit
 * 2. XOR trick: n ^ (n >> 1) should give all 1s if alternating
 * 3. String approach: Convert to binary string and check pattern
 * 
 * Company Tags: Google
 */
public class BinaryNumberWithAlternatingBits {

    // Main optimized solution - XOR trick
    public boolean hasAlternatingBits(int n) {
        // If n has alternating bits, n ^ (n >> 1) will have all 1s
        int xor = n ^ (n >> 1);

        // Check if xor has all 1s: (xor + 1) & xor should be 0
        return (xor & (xor + 1)) == 0;
    }

    // Alternative solution - Right shift comparison
    public boolean hasAlternatingBitsShift(int n) {
        int lastBit = n & 1;
        n >>= 1;

        while (n > 0) {
            int currentBit = n & 1;
            if (currentBit == lastBit) {
                return false;
            }
            lastBit = currentBit;
            n >>= 1;
        }

        return true;
    }

    // Alternative solution - String approach
    public boolean hasAlternatingBitsString(int n) {
        String binary = Integer.toBinaryString(n);

        for (int i = 1; i < binary.length(); i++) {
            if (binary.charAt(i) == binary.charAt(i - 1)) {
                return false;
            }
        }

        return true;
    }

    // Alternative solution - Pattern matching
    public boolean hasAlternatingBitsPattern(int n) {
        // Check if n matches pattern 101010... or 010101...
        return isPattern(n, 0b101010101010101010101010101010101L) ||
                isPattern(n, 0b010101010101010101010101010101010L);
    }

    private boolean isPattern(int n, long pattern) {
        while (n > 0 && pattern > 0) {
            if ((n & 1) != (pattern & 1)) {
                return false;
            }
            n >>= 1;
            pattern >>= 1;
        }
        return n == 0;
    }

    public static void main(String[] args) {
        BinaryNumberWithAlternatingBits solution = new BinaryNumberWithAlternatingBits();

        // Test Case 1: Alternating bits
        System.out.println(solution.hasAlternatingBits(5)); // Expected: true (101)

        // Test Case 2: Alternating bits
        System.out.println(solution.hasAlternatingBits(10)); // Expected: true (1010)

        // Test Case 3: Not alternating
        System.out.println(solution.hasAlternatingBits(7)); // Expected: false (111)

        // Test Case 4: Not alternating
        System.out.println(solution.hasAlternatingBits(11)); // Expected: false (1011)

        // Test Case 5: Single bit
        System.out.println(solution.hasAlternatingBits(1)); // Expected: true (1)

        // Test Case 6: Two bits alternating
        System.out.println(solution.hasAlternatingBits(2)); // Expected: true (10)

        // Test Case 7: Test shift approach
        System.out.println(solution.hasAlternatingBitsShift(5)); // Expected: true

        // Test Case 8: Test string approach
        System.out.println(solution.hasAlternatingBitsString(10)); // Expected: true

        // Test Case 9: Large alternating number
        System.out.println(solution.hasAlternatingBits(85)); // Expected: true (1010101)

        // Test Case 10: Large non-alternating number
        System.out.println(solution.hasAlternatingBits(4)); // Expected: false (100)
    }
}
