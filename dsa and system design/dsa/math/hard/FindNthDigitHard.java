package math.hard;

/**
 * LeetCode 400: Nth Digit (Hard Variant)
 * https://leetcode.com/problems/nth-digit/
 *
 * Companies: Amazon, Google
 * Frequency: Medium
 *
 * Description:
 * Find the nth digit in the infinite integer sequence 123456789101112...
 *
 * Constraints:
 * - 1 <= n <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you find the nth digit in a custom base?
 * 2. Can you find the nth digit in a sequence of squares/cubes?
 * 3. Can you find the nth digit in a sequence of concatenated primes?
 */
public class FindNthDigitHard {
    public int findNthDigit(int n) {
        int len = 1;
        long count = 9, start = 1;
        while (n > len * count) {
            n -= len * count;
            len++;
            count *= 10;
            start *= 10;
        }
        start += (n - 1) / len;
        String s = Long.toString(start);
        return s.charAt((n - 1) % len) - '0';
    }

    // Follow-up 1: nth digit in custom base
    public int findNthDigitBase(int n, int base) {
        int len = 1;
        long count = base - 1, start = 1;
        while (n > len * count) {
            n -= len * count;
            len++;
            count *= base;
            start *= base;
        }
        start += (n - 1) / len;
        String s = Long.toString(start, base);
        return Character.digit(s.charAt((n - 1) % len), base);
    }

    // Follow-up 2: nth digit in sequence of squares
    public int findNthDigitSquares(int n) {
        int num = 1, total = 0;
        while (true) {
            String s = Integer.toString(num * num);
            if (total + s.length() >= n)
                return s.charAt(n - total - 1) - '0';
            total += s.length();
            num++;
        }
    }

    // Follow-up 3: nth digit in sequence of concatenated primes
    public int findNthDigitPrimes(int n) {
        int num = 2, total = 0;
        while (true) {
            if (isPrime(num)) {
                String s = Integer.toString(num);
                if (total + s.length() >= n)
                    return s.charAt(n - total - 1) - '0';
                total += s.length();
            }
            num++;
        }
    }

    private boolean isPrime(int x) {
        if (x < 2)
            return false;
        for (int i = 2; i * i <= x; i++)
            if (x % i == 0)
                return false;
        return true;
    }

    public static void main(String[] args) {
        FindNthDigitHard solution = new FindNthDigitHard();
        System.out.println(solution.findNthDigit(11)); // 0
        System.out.println(solution.findNthDigitBase(11, 16)); // hex digit
        System.out.println(solution.findNthDigitSquares(10)); // digit in squares
        System.out.println(solution.findNthDigitPrimes(10)); // digit in primes
    }
}
