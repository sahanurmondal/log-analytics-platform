package bitmanipulation.easy;

/**
 * LeetCode 338: Counting Bits
 * https://leetcode.com/problems/counting-bits/
 *
 * Description: Given an integer n, return an array ans of length n + 1 such
 * that for each i (0 <= i <= n),
 * ans[i] is the number of 1's in the binary representation of i.
 * 
 * Constraints:
 * - 0 <= n <= 10^5
 *
 * Follow-up:
 * - Can you do it in O(n) time and O(1) space (excluding output array)?
 * - Can you solve it using DP?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1) excluding output
 * 
 * Algorithm:
 * 1. DP with right shift: dp[i] = dp[i >> 1] + (i & 1)
 * 2. DP with Brian Kernighan: dp[i] = dp[i & (i-1)] + 1
 * 3. Brute force: Count bits for each number
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class CountingBits {

    // Main optimized solution - DP with right shift
    public int[] countBits(int n) {
        int[] result = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            // Number of 1s in i = Number of 1s in i/2 + (i is odd ? 1 : 0)
            result[i] = result[i >> 1] + (i & 1);
        }

        return result;
    }

    // Alternative solution - DP with Brian Kernighan
    public int[] countBitsKernighan(int n) {
        int[] result = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            // i & (i-1) removes the rightmost 1 bit
            result[i] = result[i & (i - 1)] + 1;
        }

        return result;
    }

    // Alternative solution - DP with offset
    public int[] countBitsOffset(int n) {
        int[] result = new int[n + 1];
        int offset = 1;

        for (int i = 1; i <= n; i++) {
            if (offset * 2 == i) {
                offset = i;
            }
            result[i] = result[i - offset] + 1;
        }

        return result;
    }

    // Brute force solution - For comparison
    public int[] countBitsBruteForce(int n) {
        int[] result = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            result[i] = Integer.bitCount(i);
        }

        return result;
    }

    public static void main(String[] args) {
        CountingBits solution = new CountingBits();

        // Test Case 1: Small case
        System.out.println(java.util.Arrays.toString(solution.countBits(2))); // Expected: [0,1,1]

        // Test Case 2: Power of 2
        System.out.println(java.util.Arrays.toString(solution.countBits(5))); // Expected: [0,1,1,2,1,2]

        // Test Case 3: Single element
        System.out.println(java.util.Arrays.toString(solution.countBits(0))); // Expected: [0]

        // Test Case 4: Larger case
        System.out.println(java.util.Arrays.toString(solution.countBits(8))); // Expected: [0,1,1,2,1,2,2,3,1]

        // Test Case 5: Test Kernighan approach
        System.out.println(java.util.Arrays.toString(solution.countBitsKernighan(5))); // Expected: [0,1,1,2,1,2]

        // Test Case 6: Test offset approach
        System.out.println(java.util.Arrays.toString(solution.countBitsOffset(5))); // Expected: [0,1,1,2,1,2]

        // Test Case 7: Test brute force
        System.out.println(java.util.Arrays.toString(solution.countBitsBruteForce(5))); // Expected: [0,1,1,2,1,2]

        // Test Case 8: Perfect powers
        System.out.println(java.util.Arrays.toString(solution.countBits(16)));

        // Test Case 9: Edge case
        System.out.println(java.util.Arrays.toString(solution.countBits(1))); // Expected: [0,1]

        // Test Case 10: Complex pattern
        System.out.println(java.util.Arrays.toString(solution.countBits(15)));
    }
}
