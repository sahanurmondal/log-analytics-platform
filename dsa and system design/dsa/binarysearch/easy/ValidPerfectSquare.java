package binarysearch.easy;

/**
 * LeetCode 367: Valid Perfect Square
 * https://leetcode.com/problems/valid-perfect-square/
 *
 * Description:
 * Given a positive integer num, write a function which returns True if num is a
 * perfect square else False.
 * Follow up: Do not use any built-in library function such as sqrt.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn
 * Difficulty: Easy
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= num <= 2^31 - 1
 *
 * Follow-ups:
 * - Can you solve it in O(log n) time?
 * - What about using Newton's method?
 * - How would you handle overflow?
 */
public class ValidPerfectSquare {

    // Binary Search approach - O(log n)
    public boolean isPerfectSquare(int num) {
        if (num < 2)
            return true;

        long left = 2, right = num / 2;

        while (left <= right) {
            long mid = left + (right - left) / 2;
            long square = mid * mid;

            if (square == num) {
                return true;
            } else if (square < num) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

    // Newton's Method approach - Follow-up
    public boolean isPerfectSquareNewton(int num) {
        if (num < 2)
            return true;

        long x = num;
        while (x * x > num) {
            x = (x + num / x) / 2;
        }

        return x * x == num;
    }

    // Mathematical approach using 1+3+5+7+...+(2n-1) = n^2
    public boolean isPerfectSquareMath(int num) {
        int i = 1;
        while (num > 0) {
            num -= i;
            i += 2;
        }
        return num == 0;
    }

    public static void main(String[] args) {
        ValidPerfectSquare solution = new ValidPerfectSquare();

        // Test Case 1: Perfect square
        System.out.println(solution.isPerfectSquare(16)); // Expected: true

        // Test Case 2: Not a perfect square
        System.out.println(solution.isPerfectSquare(14)); // Expected: false

        // Test Case 3: Edge case - 1
        System.out.println(solution.isPerfectSquare(1)); // Expected: true

        // Test Case 4: Large perfect square
        System.out.println(solution.isPerfectSquare(2147395600)); // Expected: true (46340^2)

        // Test Newton's method
        System.out.println(solution.isPerfectSquareNewton(25)); // Expected: true

        // Test mathematical approach
        System.out.println(solution.isPerfectSquareMath(9)); // Expected: true
        System.out.println(solution.isPerfectSquareMath(8)); // Expected: false
    }
}
