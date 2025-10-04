package bitmanipulation.easy;

/**
 * LeetCode 461: Hamming Distance
 * https://leetcode.com/problems/hamming-distance/
 *
 * Description: The Hamming distance between two integers is the number of
 * positions at which the corresponding bits are different.
 * Given two integers x and y, return the Hamming distance between them.
 * 
 * Constraints:
 * - 0 <= x, y <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it using XOR?
 * - What about Brian Kernighan's algorithm?
 * 
 * Time Complexity: O(k) where k is number of different bits
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. XOR + Brian Kernighan: XOR to find different bits, then count 1s
 * 2. XOR + Built-in: Use Integer.bitCount()
 * 3. Bit by bit comparison: Check each bit position
 * 
 * Company Tags: Google, Facebook, Amazon, Apple
 */
public class HammingDistance {

    // Main optimized solution - XOR + Brian Kernighan
    public int hammingDistance(int x, int y) {
        int xor = x ^ y;
        int count = 0;

        while (xor != 0) {
            xor &= (xor - 1); // Remove rightmost 1 bit
            count++;
        }

        return count;
    }

    // Alternative solution - XOR + Built-in bit count
    public int hammingDistanceBuiltIn(int x, int y) {
        return Integer.bitCount(x ^ y);
    }

    // Alternative solution - Bit by bit comparison
    public int hammingDistanceBitByBit(int x, int y) {
        int count = 0;

        for (int i = 0; i < 32; i++) {
            if (((x >> i) & 1) != ((y >> i) & 1)) {
                count++;
            }
        }

        return count;
    }

    // Alternative solution - XOR + Right shift
    public int hammingDistanceShift(int x, int y) {
        int xor = x ^ y;
        int count = 0;

        while (xor != 0) {
            count += xor & 1;
            xor >>= 1;
        }

        return count;
    }

    public static void main(String[] args) {
        HammingDistance solution = new HammingDistance();

        // Test Case 1: Normal case
        System.out.println(solution.hammingDistance(1, 4)); // Expected: 2 (001 vs 100)

        // Test Case 2: Same numbers
        System.out.println(solution.hammingDistance(5, 5)); // Expected: 0

        // Test Case 3: One bit difference
        System.out.println(solution.hammingDistance(0, 1)); // Expected: 1

        // Test Case 4: All bits different
        System.out.println(solution.hammingDistance(0, 15)); // Expected: 4 (0000 vs 1111)

        // Test Case 5: Large numbers
        System.out.println(solution.hammingDistance(1000, 2000)); // Expected: calculated difference

        // Test Case 6: Test built-in approach
        System.out.println(solution.hammingDistanceBuiltIn(1, 4)); // Expected: 2

        // Test Case 7: Test bit by bit approach
        System.out.println(solution.hammingDistanceBitByBit(1, 4)); // Expected: 2

        // Test Case 8: Test shift approach
        System.out.println(solution.hammingDistanceShift(1, 4)); // Expected: 2

        // Test Case 9: Powers of 2
        System.out.println(solution.hammingDistance(8, 16)); // Expected: 2

        // Test Case 10: Maximum values
        System.out.println(solution.hammingDistance(Integer.MAX_VALUE, 0)); // Expected: 31
    }
}
