package bitmanipulation.easy;

/**
 * LeetCode 191: Number of 1 Bits
 * https://leetcode.com/problems/number-of-1-bits/
 *
 * Description: Write a function that takes an unsigned integer and returns the
 * number of '1' bits it has (also known as the Hamming weight).
 * 
 * Constraints:
 * - The input must be a binary string of length 32
 *
 * Follow-up:
 * - Can you solve it using Brian Kernighan's algorithm?
 * - What about using built-in functions?
 * 
 * Time Complexity: O(k) where k is number of 1 bits
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Brian Kernighan's: n & (n-1) removes rightmost 1 bit
 * 2. Bit shifting: Check each bit position
 * 3. Built-in: Integer.bitCount()
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft, Apple
 */
public class NumberOf1Bits {

    // Main optimized solution - Brian Kernighan's algorithm
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1); // Remove rightmost 1 bit
            count++;
        }
        return count;
    }

    // Alternative solution - Bit shifting
    public int hammingWeightShift(int n) {
        int count = 0;
        for (int i = 0; i < 32; i++) {
            if ((n & (1 << i)) != 0) {
                count++;
            }
        }
        return count;
    }

    // Alternative solution - Right shift approach
    public int hammingWeightRightShift(int n) {
        int count = 0;
        while (n != 0) {
            count += n & 1;
            n >>>= 1; // Unsigned right shift
        }
        return count;
    }

    // Built-in solution
    public int hammingWeightBuiltIn(int n) {
        return Integer.bitCount(n);
    }

    public static void main(String[] args) {
        NumberOf1Bits solution = new NumberOf1Bits();

        // Test Case 1: Normal case
        System.out.println(solution.hammingWeight(0b00000000000000000000000000001011)); // Expected: 3

        // Test Case 2: All bits set
        System.out.println(solution.hammingWeight(0b11111111111111111111111111111101)); // Expected: 31

        // Test Case 3: Single bit
        System.out.println(solution.hammingWeight(1)); // Expected: 1

        // Test Case 4: Zero
        System.out.println(solution.hammingWeight(0)); // Expected: 0

        // Test Case 5: Maximum unsigned int
        System.out.println(solution.hammingWeight(-1)); // Expected: 32 (all bits set)

        // Test Case 6: Power of 2
        System.out.println(solution.hammingWeight(8)); // Expected: 1

        // Test Case 7: Test shift approach
        System.out.println(solution.hammingWeightShift(0b1011)); // Expected: 3

        // Test Case 8: Test right shift approach
        System.out.println(solution.hammingWeightRightShift(0b1011)); // Expected: 3

        // Test Case 9: Test built-in
        System.out.println(solution.hammingWeightBuiltIn(0b1011)); // Expected: 3

        // Test Case 10: Large number
        System.out.println(solution.hammingWeight(0b11110000111100001111000011110000)); // Expected: 16
    }
}
