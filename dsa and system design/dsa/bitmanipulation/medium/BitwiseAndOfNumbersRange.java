package bitmanipulation.medium;

/**
 * LeetCode 201: Bitwise AND of Numbers Range
 * https://leetcode.com/problems/bitwise-and-of-numbers-range/
 *
 * Description: Given two integers left and right that represent the range
 * [left, right], return the bitwise AND of all numbers in this range,
 * inclusive.
 * 
 * Constraints:
 * - 0 <= left <= right <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it without iterating through all numbers?
 * - What about using bit shift?
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class BitwiseAndOfNumbersRange {

    // Main optimized solution - Find common prefix
    public int rangeBitwiseAnd(int left, int right) {
        int shift = 0;

        // Find common prefix of left and right
        while (left != right) {
            left >>= 1;
            right >>= 1;
            shift++;
        }

        return left << shift;
    }

    // Alternative solution - Brian Kernighan's algorithm
    public int rangeBitwiseAndKernighan(int left, int right) {
        while (left < right) {
            // Remove rightmost 1 bit from right
            right &= (right - 1);
        }
        return right;
    }

    // Alternative solution - Bit manipulation
    public int rangeBitwiseAndBitManip(int left, int right) {
        int result = 0;

        for (int i = 31; i >= 0; i--) {
            int leftBit = (left >> i) & 1;
            int rightBit = (right >> i) & 1;

            if (leftBit == rightBit) {
                result |= (leftBit << i);
            } else {
                break; // Once bits differ, all lower bits will be 0
            }
        }

        return result;
    }

    public static void main(String[] args) {
        BitwiseAndOfNumbersRange solution = new BitwiseAndOfNumbersRange();

        System.out.println(solution.rangeBitwiseAnd(5, 7)); // Expected: 4
        System.out.println(solution.rangeBitwiseAnd(0, 0)); // Expected: 0
        System.out.println(solution.rangeBitwiseAnd(1, 2147483647)); // Expected: 0
        System.out.println(solution.rangeBitwiseAndKernighan(5, 7)); // Expected: 4
        System.out.println(solution.rangeBitwiseAndBitManip(5, 7)); // Expected: 4
    }
}
