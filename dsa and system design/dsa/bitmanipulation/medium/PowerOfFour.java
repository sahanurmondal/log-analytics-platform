package bitmanipulation.medium;

/**
 * LeetCode 342: Power of Four
 * https://leetcode.com/problems/power-of-four/
 *
 * Description:
 * Given an integer, return true if it is a power of four.
 *
 * Constraints:
 * - -2^31 <= n <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you solve it without loops/recursion?
 * 2. Can you solve it using bit manipulation?
 * 3. Can you generalize for any power k?
 */
public class PowerOfFour {
    /**
     * Main solution: Bit manipulation, no loops/recursion
     */
    public boolean isPowerOfFour(int n) {
        // n > 0, n is power of 2, and only the single 1 bit is at an even position
        return n > 0 && (n & (n - 1)) == 0 && (n & 0x55555555) != 0;
    }

    /**
     * Follow-up 1: Loop/recursion approach
     */
    public boolean isPowerOfFourLoop(int n) {
        if (n < 1)
            return false;
        while (n % 4 == 0)
            n /= 4;
        return n == 1;
    }

    /**
     * Follow-up 2: Generalize for any power k
     */
    public boolean isPowerOfK(int n, int k) {
        if (n < 1 || k < 2)
            return false;
        while (n % k == 0)
            n /= k;
        return n == 1;
    }

    public static void main(String[] args) {
        PowerOfFour solution = new PowerOfFour();
        // Edge Case 1: Normal case
        System.out.println(solution.isPowerOfFour(16)); // true
        // Edge Case 2: Zero
        System.out.println(solution.isPowerOfFour(0)); // false
        // Edge Case 3: Negative number
        System.out.println(solution.isPowerOfFour(-4)); // false
        // Edge Case 4: Large power
        System.out.println(solution.isPowerOfFour(1073741824)); // false
        // Edge Case 5: Power of four
        System.out.println(solution.isPowerOfFour(64)); // true

        // Follow-up 1: Loop/recursion
        System.out.println(solution.isPowerOfFourLoop(16)); // true
        System.out.println(solution.isPowerOfFourLoop(0)); // false
        System.out.println(solution.isPowerOfFourLoop(-4)); // false
        System.out.println(solution.isPowerOfFourLoop(64)); // true

        // Follow-up 2: Generalize for any power k
        System.out.println(solution.isPowerOfK(27, 3)); // true
        System.out.println(solution.isPowerOfK(16, 2)); // true
        System.out.println(solution.isPowerOfK(81, 9)); // true
        System.out.println(solution.isPowerOfK(20, 4)); // false
    }
}
