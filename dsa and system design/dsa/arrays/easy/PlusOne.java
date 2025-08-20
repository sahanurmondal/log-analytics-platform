package arrays.easy;

import java.util.*;

/**
 * LeetCode 66: Plus One
 * https://leetcode.com/problems/plus-one/
 *
 * Description:
 * You are given a large integer represented as an integer array digits, where
 * each digits[i] is the ith digit of the integer.
 * The digits are ordered from most significant to least significant in
 * left-to-right order.
 * The large integer does not contain any leading zeros. Increment the large
 * integer by one and return the resulting array of digits.
 *
 * Constraints:
 * - 1 <= digits.length <= 100
 * - 0 <= digits[i] <= 9
 * - digits does not contain any leading zeros except for the number 0 itself
 *
 * Follow-up:
 * - Can you solve it without converting to integer?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1) or O(n) if new array needed
 */
public class PlusOne {

    // Main solution - Handle carry
    public int[] plusOne(int[] digits) {
        for (int i = digits.length - 1; i >= 0; i--) {
            if (digits[i] < 9) {
                digits[i]++;
                return digits;
            }
            digits[i] = 0;
        }

        // All digits were 9, need new array
        int[] result = new int[digits.length + 1];
        result[0] = 1;
        return result;
    }

    // Alternative solution - Using carry flag
    public int[] plusOneWithCarry(int[] digits) {
        int carry = 1;

        for (int i = digits.length - 1; i >= 0 && carry > 0; i--) {
            int sum = digits[i] + carry;
            digits[i] = sum % 10;
            carry = sum / 10;
        }

        if (carry > 0) {
            int[] result = new int[digits.length + 1];
            result[0] = carry;
            System.arraycopy(digits, 0, result, 1, digits.length);
            return result;
        }

        return digits;
    }

    public static void main(String[] args) {
        PlusOne solution = new PlusOne();

        // Test Case 1: Normal case
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 1, 2, 3 }))); // Expected: [1,2,4]

        // Test Case 2: Edge case - carry needed
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 4, 3, 2, 1 }))); // Expected: [4,3,2,2]

        // Test Case 3: Corner case - all 9s
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 9 }))); // Expected: [1,0]

        // Test Case 4: Large input - multiple 9s
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 1, 9, 9 }))); // Expected: [2,0,0]

        // Test Case 5: Single digit
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 5 }))); // Expected: [6]

        // Test Case 6: Special case - zero
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 0 }))); // Expected: [1]

        // Test Case 7: Multiple 9s at end
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 1, 2, 9, 9 }))); // Expected: [1,3,0,0]

        // Test Case 8: All 9s multiple digits
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 9, 9, 9 }))); // Expected: [1,0,0,0]

        // Test Case 9: No carry needed
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 1, 2, 3, 4 }))); // Expected: [1,2,3,5]

        // Test Case 10: Large number
        System.out.println(Arrays.toString(solution.plusOne(new int[] { 8, 9, 9, 9 }))); // Expected: [9,0,0,0]
    }
}
