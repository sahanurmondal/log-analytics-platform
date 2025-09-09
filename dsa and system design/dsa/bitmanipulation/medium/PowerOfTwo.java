package bitmanipulation.medium;

/**
 * LeetCode 231: Power of Two
 * https://leetcode.com/problems/power-of-two/
 *
 * Description:
 * Given an integer, return true if it is a power of two.
 *
 * Constraints:
 * - -2^31 <= n <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you solve it without loops/recursion?
 * 2. Can you solve it using bit manipulation?
 * 3. Can you generalize for any power k?
 */
public class PowerOfTwo {
    /**
     * Main solution: Bit manipulation, no loops/recursion
     */
    public boolean isPowerOfTwo(int n) {
        // n > 0 and only one bit set
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * Follow-up 1: Loop/recursion approach
     */
    public boolean isPowerOfTwoLoop(int n) {
        if (n < 1)
            return false;
        while (n % 2 == 0)
            n /= 2;
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
        PowerOfTwo solution = new PowerOfTwo();
        // Edge Case 1: Normal case
        System.out.println(solution.isPowerOfTwo(16)); // true
        // Edge Case 2: Zero
        System.out.println(solution.isPowerOfTwo(0)); // false
        // Edge Case 3: Negative number
        System.out.println(solution.isPowerOfTwo(-2)); // false
        // Edge Case 4: Large power
        System.out.println(solution.isPowerOfTwo(1073741824)); // true

        // Follow-up 1: Loop/recursion
        System.out.println(solution.isPowerOfTwoLoop(16)); // true
        System.out.println(solution.isPowerOfTwoLoop(0)); // false
        System.out.println(solution.isPowerOfTwoLoop(-2)); // false
        System.out.println(solution.isPowerOfTwoLoop(1073741824)); // true

        // Follow-up 2: Generalize for any power k
        System.out.println(solution.isPowerOfK(27, 3)); // true
        System.out.println(solution.isPowerOfK(16, 2)); // true
        System.out.println(solution.isPowerOfK(81, 9)); // true
        System.out.println(solution.isPowerOfK(20, 4)); // false
    }
}
