package bitmanipulation.medium;

import java.util.*;

/**
 * LeetCode 89: Gray Code
 * https://leetcode.com/problems/gray-code/
 *
 * Description: An n-bit gray code sequence is a sequence of 2^n integers where:
 * - Every integer is in the inclusive range [0, 2^n - 1]
 * - The first integer is 0
 * - An integer appears no more than once in the sequence
 * - The binary representation of every pair of adjacent integers differs by
 * exactly one bit
 * - The binary representation of the first and last integers differs by exactly
 * one bit
 * 
 * Constraints:
 * - 1 <= n <= 16
 *
 * Follow-up:
 * - Can you solve it recursively?
 * - What about using XOR?
 * 
 * Time Complexity: O(2^n)
 * Space Complexity: O(2^n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class GrayCode {

    // Main optimized solution - Recursive approach
    public List<Integer> grayCode(int n) {
        if (n == 0)
            return Arrays.asList(0);

        List<Integer> result = grayCode(n - 1);
        int leadingBit = 1 << (n - 1);

        // Add reversed list with leading bit set
        for (int i = result.size() - 1; i >= 0; i--) {
            result.add(leadingBit | result.get(i));
        }

        return result;
    }

    // Alternative solution - Iterative approach
    public List<Integer> grayCodeIterative(int n) {
        List<Integer> result = new ArrayList<>();
        result.add(0);

        for (int i = 0; i < n; i++) {
            int size = result.size();
            for (int j = size - 1; j >= 0; j--) {
                result.add(result.get(j) | (1 << i));
            }
        }

        return result;
    }

    // Alternative solution - Mathematical formula
    public List<Integer> grayCodeFormula(int n) {
        List<Integer> result = new ArrayList<>();
        int totalNumbers = 1 << n;

        for (int i = 0; i < totalNumbers; i++) {
            result.add(i ^ (i >> 1));
        }

        return result;
    }

    public static void main(String[] args) {
        GrayCode solution = new GrayCode();

        System.out.println(solution.grayCode(2)); // Expected: [0,1,3,2]
        System.out.println(solution.grayCode(1)); // Expected: [0,1]
        System.out.println(solution.grayCodeIterative(3)); // Expected: [0,1,3,2,6,7,5,4]
        System.out.println(solution.grayCodeFormula(2)); // Expected: [0,1,3,2]
    }
}
