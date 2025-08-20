package bitmanipulation.easy;

/**
 * LeetCode 476: Number Complement
 * https://leetcode.com/problems/number-complement/
 *
 * Description: The complement of an integer is the integer you get when you
 * flip all the 0's to 1's and all the 1's to 0's in its binary representation.
 * Given an integer num, return its complement.
 * 
 * Constraints:
 * - 1 <= num < 2^31
 *
 * Follow-up:
 * - Can you solve it using bit manipulation only?
 * - What about using bit mask?
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Bit mask: Create mask with all 1s in significant positions, then XOR
 * 2. Find highest bit: Determine number of significant bits
 * 3. Mathematical: Use formula (1 << bits) - 1 - num
 * 
 * Company Tags: Google, Apple
 */
public class NumberComplement {

    // Main optimized solution - Bit mask
    public int findComplement(int num) {
        // Find the number of bits in num
        int bitLength = Integer.toBinaryString(num).length();

        // Create a mask with all 1s for the significant bits
        int mask = (1 << bitLength) - 1;

        // XOR with mask to flip all significant bits
        return num ^ mask;
    }

    // Alternative solution - Find highest bit
    public int findComplementHighestBit(int num) {
        int mask = 1;
        int temp = num;

        // Find the position of the highest bit
        while (temp > 0) {
            mask <<= 1;
            temp >>= 1;
        }

        // mask now has 1 followed by zeros at positions we want to flip
        return (mask - 1) ^ num;
    }

    // Alternative solution - Bit by bit flip
    public int findComplementBitByBit(int num) {
        int result = 0;
        int bitPosition = 0;

        while (num > 0) {
            // If current bit is 0, set corresponding bit in result to 1
            if ((num & 1) == 0) {
                result |= (1 << bitPosition);
            }
            num >>= 1;
            bitPosition++;
        }

        return result;
    }

    // Alternative solution - Using Integer.highestOneBit
    public int findComplementBuiltIn(int num) {
        int mask = Integer.highestOneBit(num);
        mask = (mask << 1) - 1;
        return num ^ mask;
    }

    public static void main(String[] args) {
        NumberComplement solution = new NumberComplement();

        // Test Case 1: Normal case
        System.out.println(solution.findComplement(5)); // Expected: 2 (101 -> 010)

        // Test Case 2: Single bit
        System.out.println(solution.findComplement(1)); // Expected: 0 (1 -> 0)

        // Test Case 3: All bits set in range
        System.out.println(solution.findComplement(7)); // Expected: 0 (111 -> 000)

        // Test Case 4: Power of 2
        System.out.println(solution.findComplement(8)); // Expected: 7 (1000 -> 0111)

        // Test Case 5: Large number
        System.out.println(solution.findComplement(1000)); // Expected: calculated complement

        // Test Case 6: Test highest bit approach
        System.out.println(solution.findComplementHighestBit(5)); // Expected: 2

        // Test Case 7: Test bit by bit approach
        System.out.println(solution.findComplementBitByBit(5)); // Expected: 2

        // Test Case 8: Test built-in approach
        System.out.println(solution.findComplementBuiltIn(5)); // Expected: 2

        // Test Case 9: Two bits
        System.out.println(solution.findComplement(2)); // Expected: 1 (10 -> 01)

        // Test Case 10: Maximum constraint
        System.out.println(solution.findComplement(2147483647)); // Expected: 0
    }
}
