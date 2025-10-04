package bitmanipulation.easy;

/**
 * LeetCode 342: Power of Four
 * https://leetcode.com/problems/power-of-four/
 *
 * Description: Given an integer n, return true if it is a power of four.
 * Otherwise, return false.
 * An integer n is a power of four, if there exists an integer x such that n ==
 * 4^x.
 * 
 * Constraints:
 * - -2^31 <= n <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it without loops/recursion?
 * - What's the difference from power of two?
 * 
 * Time Complexity: O(1)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Bit manipulation: Check if power of 2 and bit is at even position
 * 2. Mathematical: Use logarithms base 4
 * 3. Modulo: Powers of 4 have specific modulo properties
 * 
 * Company Tags: Google, Facebook
 */
public class PowerOfFour {

    // Main optimized solution - Bit manipulation
    public boolean isPowerOfFour(int n) {
        // Check if power of 2 and the single bit is at even position (0, 2, 4, 6, ...)
        return n > 0 && (n & (n - 1)) == 0 && (n & 0x55555555) != 0;
    }

    // Alternative solution - Mathematical
    public boolean isPowerOfFourMath(int n) {
        if (n <= 0)
            return false;
        double log4 = Math.log(n) / Math.log(4);
        return log4 == Math.floor(log4);
    }

    // Alternative solution - Modulo property
    public boolean isPowerOfFourModulo(int n) {
        // Powers of 4: 1, 4, 16, 64, 256... all have remainder 1 when divided by 3
        return n > 0 && (n & (n - 1)) == 0 && n % 3 == 1;
    }

    // Alternative solution - Iterative
    public boolean isPowerOfFourIterative(int n) {
        if (n <= 0)
            return false;

        while (n % 4 == 0) {
            n /= 4;
        }

        return n == 1;
    }

    // Alternative solution - String approach
    public boolean isPowerOfFourString(int n) {
        if (n <= 0)
            return false;

        String binary = Integer.toBinaryString(n);
        if (binary.chars().sum() - '0' * binary.length() != 1)
            return false; // Not power of 2

        return (binary.length() - 1) % 2 == 0; // Bit position should be even
    }

    public static void main(String[] args) {
        PowerOfFour solution = new PowerOfFour();

        // Test Case 1: Power of 4
        System.out.println(solution.isPowerOfFour(1)); // Expected: true (4^0)

        // Test Case 2: Power of 4
        System.out.println(solution.isPowerOfFour(16)); // Expected: true (4^2)

        // Test Case 3: Power of 2 but not 4
        System.out.println(solution.isPowerOfFour(2)); // Expected: false

        // Test Case 4: Not power of anything
        System.out.println(solution.isPowerOfFour(5)); // Expected: false

        // Test Case 5: Negative number
        System.out.println(solution.isPowerOfFour(-4)); // Expected: false

        // Test Case 6: Zero
        System.out.println(solution.isPowerOfFour(0)); // Expected: false

        // Test Case 7: Large power of 4
        System.out.println(solution.isPowerOfFour(1024)); // Expected: true (4^5)

        // Test Case 8: Test mathematical approach
        System.out.println(solution.isPowerOfFourMath(64)); // Expected: true

        // Test Case 9: Test modulo approach
        System.out.println(solution.isPowerOfFourModulo(256)); // Expected: true

        // Test Case 10: Edge case - power of 2 but not 4
        System.out.println(solution.isPowerOfFour(8)); // Expected: false
    }
}
