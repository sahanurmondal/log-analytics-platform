package bitmanipulation.easy;

/**
 * LeetCode 190: Reverse Bits
 * https://leetcode.com/problems/reverse-bits/
 *
 * Description: Reverse bits of a given 32 bits unsigned integer.
 * 
 * Constraints:
 * - The input must be a binary string of length 32
 *
 * Follow-up:
 * - Can you do it with divide and conquer?
 * - What if this function is called many times?
 * 
 * Time Complexity: O(1) - fixed 32 bits
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Bit by bit: Extract each bit and build result
 * 2. Divide and conquer: Swap pairs, then 4s, then 8s, etc.
 * 3. Lookup table: For frequent calls
 * 
 * Company Tags: Google, Facebook, Amazon, Apple
 */
public class ReverseBits {

    // Main optimized solution - Bit by bit
    public int reverseBits(int n) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (n & 1);
            n >>= 1;
        }
        return result;
    }

    // Alternative solution - Divide and conquer
    public int reverseBitsDivideConquer(int n) {
        // Swap pairs of bits
        n = ((n & 0xAAAAAAAA) >>> 1) | ((n & 0x55555555) << 1);
        // Swap pairs of pairs
        n = ((n & 0xCCCCCCCC) >>> 2) | ((n & 0x33333333) << 2);
        // Swap pairs of pairs of pairs
        n = ((n & 0xF0F0F0F0) >>> 4) | ((n & 0x0F0F0F0F) << 4);
        // Swap bytes
        n = ((n & 0xFF00FF00) >>> 8) | ((n & 0x00FF00FF) << 8);
        // Swap pairs of bytes
        n = (n >>> 16) | (n << 16);
        return n;
    }

    // Alternative solution - Using StringBuilder
    public int reverseBitsString(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(n & 1);
            n >>= 1;
        }
        return Integer.parseUnsignedInt(sb.toString(), 2);
    }

    // Follow-up: Lookup table for frequent calls
    private java.util.Map<Integer, Integer> cache = new java.util.HashMap<>();

    public int reverseBitsWithCache(int n) {
        if (cache.containsKey(n)) {
            return cache.get(n);
        }

        int result = reverseBits(n);
        cache.put(n, result);
        return result;
    }

    public static void main(String[] args) {
        ReverseBits solution = new ReverseBits();

        // Test Case 1: Normal case
        System.out.println(Integer.toBinaryString(solution.reverseBits(0b00000010100101000001111010011100)));
        // Expected: 964176192 (00111001011110000010100101000000)

        // Test Case 2: All 1s
        System.out.println(Integer.toUnsignedString(solution.reverseBits(-1))); // Expected: 4294967295

        // Test Case 3: Single bit set
        System.out.println(Integer.toUnsignedString(solution.reverseBits(1))); // Expected: 2147483648

        // Test Case 4: Zero
        System.out.println(solution.reverseBits(0)); // Expected: 0

        // Test Case 5: Test divide and conquer
        System.out.println(solution.reverseBitsDivideConquer(0b00000010100101000001111010011100));

        // Test Case 6: Alternating bits
        System.out.println(Integer.toUnsignedString(solution.reverseBits(0b10101010101010101010101010101010)));

        // Test Case 7: Test with cache
        System.out.println(solution.reverseBitsWithCache(43261596));

        // Test Case 8: Power of 2
        System.out.println(solution.reverseBits(8)); // Expected: 268435456

        // Test Case 9: Maximum value
        System.out.println(solution.reverseBits(Integer.MAX_VALUE));

        // Test Case 10: Test string approach
        System.out.println(solution.reverseBitsString(43261596));
    }
}
