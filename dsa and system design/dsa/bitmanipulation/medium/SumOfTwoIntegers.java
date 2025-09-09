package bitmanipulation.medium;

/**
 * LeetCode 371: Sum of Two Integers
 * https://leetcode.com/problems/sum-of-two-integers/
 *
 * Description: Given two integers a and b, return the sum of the two integers
 * without using the operators + and -.
 * 
 * Constraints:
 * - -1000 <= a, b <= 1000
 *
 * Follow-up:
 * - Can you solve it using bit manipulation only?
 * - What about handling negative numbers?
 * 
 * Time Complexity: O(1) - at most 32 iterations
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook, Amazon, Apple
 */
public class SumOfTwoIntegers {

    // Main optimized solution - XOR and AND with carry
    public int getSum(int a, int b) {
        while (b != 0) {
            int carry = a & b; // Calculate carry
            a = a ^ b; // Sum without carry
            b = carry << 1; // Shift carry left
        }
        return a;
    }

    // Alternative solution - Recursive approach
    public int getSumRecursive(int a, int b) {
        if (b == 0)
            return a;

        int sum = a ^ b; // Sum without carry
        int carry = (a & b) << 1; // Carry shifted left

        return getSumRecursive(sum, carry);
    }

    // Alternative solution - Using subtraction (for negative numbers)
    public int getSumSubtraction(int a, int b) {
        if (b == 0)
            return a;
        if (b > 0) {
            return getSumSubtraction(a ^ b, (a & b) << 1);
        } else {
            return getSumSubtraction(a ^ b, (a & b) << 1);
        }
    }

    public static void main(String[] args) {
        SumOfTwoIntegers solution = new SumOfTwoIntegers();

        System.out.println(solution.getSum(1, 2)); // Expected: 3
        System.out.println(solution.getSum(2, 3)); // Expected: 5
        System.out.println(solution.getSum(-2, 3)); // Expected: 1
        System.out.println(solution.getSum(0, 0)); // Expected: 0
        System.out.println(solution.getSumRecursive(1, 2)); // Expected: 3
        System.out.println(solution.getSum(-1, 1)); // Expected: 0
        System.out.println(solution.getSum(20, -30)); // Expected: -10
    }
}
