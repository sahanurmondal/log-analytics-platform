package binarysearch.easy;

/**
 * LeetCode 69: Sqrt(x)
 * https://leetcode.com/problems/sqrtx/
 *
 * Description:
 * Given a non-negative integer x, return the square root of x rounded down to
 * the nearest integer.
 * The returned integer should be non-negative as well.
 * You must not use any built-in exponent function or operator.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Bloomberg,
 * Adobe, Uber
 * Difficulty: Easy
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 0 <= x <= 2^31 - 1
 *
 * Follow-ups:
 * - Can you implement this using Newton's method?
 * - What if you need to find the exact square root (with decimals)?
 * - How would you handle very large numbers?
 */
public class SqrtX {

    // Binary Search - O(log n) time, O(1) space
    public int mySqrt(int x) {
        if (x == 0)
            return 0;

        int left = 1;
        int right = x;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            // Use division to avoid overflow
            if (mid == x / mid) {
                return mid;
            } else if (mid < x / mid) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return right; // Return the floor of sqrt
    }

    // Alternative binary search with explicit overflow handling
    public int mySqrtSafe(int x) {
        if (x == 0)
            return 0;

        int left = 1;
        int right = x;
        int result = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            long square = (long) mid * mid;

            if (square == x) {
                return mid;
            } else if (square < x) {
                result = mid; // Store the potential answer
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    // Newton's Method - O(log n) time, faster convergence
    public int mySqrtNewton(int x) {
        if (x == 0)
            return 0;

        long r = x;
        while (r * r > x) {
            r = (r + x / r) / 2;
        }

        return (int) r;
    }

    // Bit manipulation approach
    public int mySqrtBitwise(int x) {
        if (x == 0)
            return 0;

        int result = 0;
        int bit = 1 << 15; // Start from the 15th bit (since sqrt(2^31-1) < 2^16)

        while (bit > 0) {
            int temp = result | bit;
            if ((long) temp * temp <= x) {
                result = temp;
            }
            bit >>= 1;
        }

        return result;
    }

    // Linear search for comparison - O(sqrt(n)) time
    public int mySqrtLinear(int x) {
        if (x == 0)
            return 0;

        int i = 1;
        while ((long) i * i <= x) {
            i++;
        }

        return i - 1;
    }

    // Exponential search + Binary search
    public int mySqrtExponential(int x) {
        if (x == 0)
            return 0;

        // Find the range using exponential search
        int bound = 1;
        while ((long) bound * bound <= x) {
            bound *= 2;
        }

        // Binary search in the range [bound/2, bound]
        int left = bound / 2;
        int right = bound;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            long square = (long) mid * mid;

            if (square == x) {
                return mid;
            } else if (square < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return right;
    }

    // Find exact square root with decimal places
    public double mySqrtExact(int x, int precision) {
        if (x == 0)
            return 0.0;

        double left = 0;
        double right = x;
        double epsilon = Math.pow(10, -precision);

        while (right - left > epsilon) {
            double mid = (left + right) / 2.0;
            double square = mid * mid;

            if (Math.abs(square - x) <= epsilon) {
                return mid;
            } else if (square < x) {
                left = mid;
            } else {
                right = mid;
            }
        }

        return (left + right) / 2.0;
    }

    // Babylonian method (another form of Newton's method)
    public int mySqrtBabylonian(int x) {
        if (x == 0)
            return 0;

        double guess = x / 2.0;
        double prev;

        do {
            prev = guess;
            guess = (guess + x / guess) / 2.0;
        } while (Math.abs(guess - prev) >= 1);

        return (int) guess;
    }

    // Check if a number is a perfect square
    public boolean isPerfectSquare(int x) {
        int sqrt = mySqrt(x);
        return sqrt * sqrt == x;
    }

    // Find the next perfect square
    public int nextPerfectSquare(int x) {
        int sqrt = mySqrt(x);
        if (sqrt * sqrt == x) {
            return (sqrt + 1) * (sqrt + 1);
        } else {
            return (sqrt + 1) * (sqrt + 1);
        }
    }

    public static void main(String[] args) {
        SqrtX solution = new SqrtX();

        // Test Case 1: x = 4
        System.out.println(solution.mySqrt(4)); // Expected: 2

        // Test Case 2: x = 8
        System.out.println(solution.mySqrt(8)); // Expected: 2

        // Test Case 3: x = 0
        System.out.println(solution.mySqrt(0)); // Expected: 0

        // Test Case 4: x = 1
        System.out.println(solution.mySqrt(1)); // Expected: 1

        // Test Case 5: Perfect square
        System.out.println(solution.mySqrt(9)); // Expected: 3
        System.out.println(solution.mySqrt(16)); // Expected: 4
        System.out.println(solution.mySqrt(25)); // Expected: 5

        // Test Case 6: Large number
        System.out.println(solution.mySqrt(2147395600)); // Expected: 46340

        // Test Case 7: Maximum int value
        System.out.println(solution.mySqrt(Integer.MAX_VALUE)); // Expected: 46340

        // Test different approaches
        System.out.println("Safe: " + solution.mySqrtSafe(8)); // Expected: 2
        System.out.println("Newton: " + solution.mySqrtNewton(8)); // Expected: 2
        System.out.println("Bitwise: " + solution.mySqrtBitwise(8)); // Expected: 2
        System.out.println("Linear: " + solution.mySqrtLinear(8)); // Expected: 2
        System.out.println("Exponential: " + solution.mySqrtExponential(8)); // Expected: 2
        System.out.println("Babylonian: " + solution.mySqrtBabylonian(8)); // Expected: 2

        // Test exact square root
        System.out.println("Exact sqrt(8): " + solution.mySqrtExact(8, 6)); // Expected: ~2.828427

        // Test perfect square check
        System.out.println("Is 9 perfect square: " + solution.isPerfectSquare(9)); // Expected: true
        System.out.println("Is 8 perfect square: " + solution.isPerfectSquare(8)); // Expected: false

        // Test next perfect square
        System.out.println("Next perfect square after 8: " + solution.nextPerfectSquare(8)); // Expected: 9
        System.out.println("Next perfect square after 9: " + solution.nextPerfectSquare(9)); // Expected: 16

        // Performance test
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            solution.mySqrt(i);
        }
        long binaryTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            solution.mySqrtNewton(i);
        }
        long newtonTime = System.currentTimeMillis() - startTime;

        System.out.println("Binary search time: " + binaryTime + "ms");
        System.out.println("Newton's method time: " + newtonTime + "ms");

        // Edge cases
        System.out.println("Edge case - sqrt(2): " + solution.mySqrt(2)); // Expected: 1
        System.out.println("Edge case - sqrt(3): " + solution.mySqrt(3)); // Expected: 1
        System.out.println("Edge case - sqrt(15): " + solution.mySqrt(15)); // Expected: 3
        System.out.println("Edge case - sqrt(24): " + solution.mySqrt(24)); // Expected: 4
    }
}
