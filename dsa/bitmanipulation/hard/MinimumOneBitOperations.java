package bitmanipulation.hard;

/**
 * LeetCode 1611: Minimum One Bit Operations to Make Integers Zero
 * https://leetcode.com/problems/minimum-one-bit-operations-to-make-integers-zero/
 *
 * Description: Given an integer n, you must transform it into 0 using the
 * following operations any number of times:
 * - Change the rightmost (0th) bit in the binary representation of n.
 * - Change the ith bit in the binary representation of n if the (i-1)th bit is
 * 1 and the (i-2)th through 0th bits are 0.
 * Return the minimum number of operations to transform n into 0.
 * 
 * Constraints:
 * - 0 <= n <= 10^9
 *
 * Follow-up:
 * - Can you find the mathematical pattern?
 * - What about using Gray code relationship?
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google
 */
public class MinimumOneBitOperations {

    // Main optimized solution - Mathematical pattern (Gray code inverse)
    public int minimumOneBitOperations(int n) {
        if (n == 0)
            return 0;

        int result = 0;
        while (n > 0) {
            result ^= n;
            n >>= 1;
        }

        return result;
    }

    // Alternative solution - Recursive approach
    public int minimumOneBitOperationsRecursive(int n) {
        if (n <= 1)
            return n;

        // Find the position of the highest bit
        int k = 0;
        while ((1 << (k + 1)) <= n) {
            k++;
        }

        // Operations needed: 2^(k+1) - 1 - minimumOneBitOperations(n ^ (1 << k))
        return (1 << (k + 1)) - 1 - minimumOneBitOperations(n ^ (1 << k));
    }

    // Alternative solution - Using the pattern directly
    public int minimumOneBitOperationsPattern(int n) {
        int result = n;
        result ^= result >> 16;
        result ^= result >> 8;
        result ^= result >> 4;
        result ^= result >> 2;
        result ^= result >> 1;
        return result;
    }

    public static void main(String[] args) {
        MinimumOneBitOperations solution = new MinimumOneBitOperations();

        System.out.println(solution.minimumOneBitOperations(0)); // Expected: 0
        System.out.println(solution.minimumOneBitOperations(3)); // Expected: 2
        System.out.println(solution.minimumOneBitOperations(6)); // Expected: 4
        System.out.println(solution.minimumOneBitOperationsRecursive(3)); // Expected: 2
        System.out.println(solution.minimumOneBitOperationsPattern(6)); // Expected: 4
    }
}
