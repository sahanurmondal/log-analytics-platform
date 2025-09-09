package bitmanipulation.easy;

/**
 * LeetCode 231: Power of Two
 * https://leetcode.com/problems/power-of-two/
 *
 * Description: Given an integer n, return true if it is a power of two.
 * Otherwise, return false.
 * An integer n is a power of two, if there exists an integer x such that n ==
 * 2^x.
 * 
 * Constraints:
 * - -2^31 <= n <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it without loops/recursion?
 * - What about using bit manipulation tricks?
 * 
 * Time Complexity: O(1)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Bit manipulation: n > 0 && (n & (n-1)) == 0
 * 2. Count bits: Power of 2 has exactly one bit set
 * 3. Mathematical: Use logarithms
 * 
 * Company Tags: Google, Facebook, Amazon, Apple
 */
public class PowerOfTwo {

    // Main optimized solution - Bit manipulation
    public boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    // Alternative solution - Count bits
    public boolean isPowerOfTwoBitCount(int n) {
        return n > 0 && Integer.bitCount(n) == 1;
    }

    // Alternative solution - Iterative division
    public boolean isPowerOfTwoIterative(int n) {
        if (n <= 0)
            return false;

        while (n % 2 == 0) {
            n /= 2;
        }

        return n == 1;
    }

    // Alternative solution - Recursive
    public boolean isPowerOfTwoRecursive(int n) {
        if (n == 1)
            return true;
        if (n <= 0 || n % 2 != 0)
            return false;

        return isPowerOfTwoRecursive(n / 2);
    }

    // Alternative solution - Mathematical
    public boolean isPowerOfTwoMath(int n) {
        if (n <= 0)
            return false;
        return (Math.log(n) / Math.log(2)) % 1 == 0;
    }

    // Alternative solution - Using largest power of 2
    public boolean isPowerOfTwoLargest(int n) {
        return n > 0 && (1073741824 % n) == 0; // 2^30 is largest power of 2 in int range
    }

    public static void main(String[] args) {
        PowerOfTwo solution = new PowerOfTwo();

        // Test Case 1: Power of 2
        System.out.println(solution.isPowerOfTwo(1)); // Expected: true (2^0)

        // Test Case 2: Power of 2
        System.out.println(solution.isPowerOfTwo(16)); // Expected: true (2^4)

        // Test Case 3: Not power of 2
        System.out.println(solution.isPowerOfTwo(3)); // Expected: false

        // Test Case 4: Negative number
        System.out.println(solution.isPowerOfTwo(-16)); // Expected: false

        // Test Case 5: Zero
        System.out.println(solution.isPowerOfTwo(0)); // Expected: false

        // Test Case 6: Large power of 2
        System.out.println(solution.isPowerOfTwo(1024)); // Expected: true (2^10)

        // Test Case 7: Test bit count approach
        System.out.println(solution.isPowerOfTwoBitCount(8)); // Expected: true

        // Test Case 8: Test iterative approach
        System.out.println(solution.isPowerOfTwoIterative(32)); // Expected: true

        // Test Case 9: Test recursive approach
        System.out.println(solution.isPowerOfTwoRecursive(64)); // Expected: true

        // Test Case 10: Not power of 2 but close
        System.out.println(solution.isPowerOfTwo(6)); // Expected: false
    }
}
