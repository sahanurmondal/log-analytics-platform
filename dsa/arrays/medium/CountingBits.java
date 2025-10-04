package arrays.medium;

/**
 * LeetCode 338: Counting Bits
 * https://leetcode.com/problems/counting-bits/
 *
 * Description:
 * Given an integer n, return an array ans of length n + 1 such that for each i
 * (0 <= i <= n),
 * ans[i] is the number of 1's in the binary representation of i.
 *
 * Constraints:
 * - 0 <= n <= 10^5
 *
 * Follow-up:
 * - Can you do it in O(n) time and O(1) space (excluding the output array)?
 * - Can you do it without using any built-in function?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1) - excluding output array
 * 
 * Algorithm:
 * 1. Use dynamic programming with bit manipulation
 * 2. For each number i, count = count[i/2] + (i%2)
 * 3. Right shift removes one bit, LSB tells if odd
 */
public class CountingBits {
    public int[] countBits(int n) {
        int[] result = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            result[i] = result[i >> 1] + (i & 1);
        }

        return result;
    }

    public static void main(String[] args) {
        CountingBits solution = new CountingBits();

        // Test Case 1: Normal case
        System.out.println(java.util.Arrays.toString(solution.countBits(2))); // Expected: [0,1,1]

        // Test Case 2: Edge case - power of 2
        System.out.println(java.util.Arrays.toString(solution.countBits(5))); // Expected: [0,1,1,2,1,2]

        // Test Case 3: Corner case - zero
        System.out.println(java.util.Arrays.toString(solution.countBits(0))); // Expected: [0]

        // Test Case 4: Large input
        System.out.println(java.util.Arrays.toString(solution.countBits(8))); // Expected: [0,1,1,2,1,2,2,3,1]

        // Test Case 5: Small input
        System.out.println(java.util.Arrays.toString(solution.countBits(1))); // Expected: [0,1]

        // Test Case 6: Special case - 15 (all 1s in 4 bits)
        System.out.println(java.util.Arrays.toString(solution.countBits(15))); // Expected:
                                                                               // [0,1,1,2,1,2,2,3,1,2,2,3,2,3,3,4]

        // Test Case 7: Boundary case - 3
        System.out.println(java.util.Arrays.toString(solution.countBits(3))); // Expected: [0,1,1,2]

        // Test Case 8: Power of 2 - 1
        System.out.println(java.util.Arrays.toString(solution.countBits(7))); // Expected: [0,1,1,2,1,2,2,3]

        // Test Case 9: Even number
        System.out.println(java.util.Arrays.toString(solution.countBits(4))); // Expected: [0,1,1,2,1]

        // Test Case 10: Odd number
        System.out.println(java.util.Arrays.toString(solution.countBits(9))); // Expected: [0,1,1,2,1,2,2,3,1,2]
    }
}
