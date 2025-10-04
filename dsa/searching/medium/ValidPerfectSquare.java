package searching.medium;

/**
 * LeetCode 367: Valid Perfect Square
 * https://leetcode.com/problems/valid-perfect-square/
 *
 * Description:
 * Given a positive integer num, write a function which returns True if num is a
 * perfect square else False.
 *
 * Constraints:
 * - 1 <= num <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you return the square root?
 * 2. Can you handle large numbers efficiently?
 * 3. Can you check for perfect cubes?
 */
public class ValidPerfectSquare {
    public boolean isPerfectSquare(int num) {
        long left = 1, right = num;
        while (left <= right) {
            long mid = left + (right - left) / 2;
            long sq = mid * mid;
            if (sq == num) return true;
            else if (sq < num) left = mid + 1;
            else right = mid - 1;
        }
        return false;
    }

    // Follow-up 1: Return square root if perfect, else -1
    public int perfectSquareRoot(int num) {
        long left = 1, right = num;
        while (left <= right) {
            long mid = left + (right - left) / 2;
            long sq = mid * mid;
            if (sq == num) return (int) mid;
            else if (sq < num) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    // Follow-up 2: Efficient for large numbers (Newton's method)
    public boolean isPerfectSquareNewton(int num) {
        if (num < 2) return true;
        long x = num / 2;
        while (x * x > num) x = (x + num / x) / 2;
        return x * x == num;
    }

    // Follow-up 3: Check for perfect cubes
    public boolean isPerfectCube(int num) {
        int left = 1, right = num;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            long cube = (long) mid * mid * mid;
            if (cube == num) return true;
            else if (cube < num) left = mid + 1;
            else right = mid - 1;
        }
        return false;
    }

    public static void main(String[] args) {
        ValidPerfectSquare solution = new ValidPerfectSquare();
        System.out.println("Basic: " + solution.isPerfectSquare(16)); // true
        System.out.println("Not perfect: " + solution.isPerfectSquare(14)); // false
        System.out.println("Edge 1: " + solution.isPerfectSquare(1)); // true
        System.out.println("Square root: " + solution.perfectSquareRoot(16)); // 4
        System.out.println("Newton: " + solution.isPerfectSquareNewton(16)); // true
        System.out.println("Perfect cube: " + solution.isPerfectCube(27)); // true
        System.out.println("Not perfect cube: " + solution.isPerfectCube(28)); // false
    }
}
