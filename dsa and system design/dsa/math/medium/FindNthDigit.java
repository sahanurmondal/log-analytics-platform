package math.medium;

/**
 * LeetCode 400: Nth Digit
 * https://leetcode.com/problems/nth-digit/
 *
 * Description:
 * Find the nth digit of the infinite integer sequence 123456789101112...
 *
 * Constraints:
 * - 1 <= n <= 2^31-1
 *
 * Follow-up:
 * - Can you solve it in O(log n) time?
 */
/**
 * LeetCode 400: Nth Digit
 * https://leetcode.com/problems/nth-digit/
 *
 * Description:
 * Find the nth digit of the infinite integer sequence 123456789101112...
 *
 * Constraints:
 * - 1 <= n <= 2^31-1
 *
 * Follow-up:
 * - Can you solve it in O(log n) time?
 * - Can you handle the pattern of 1-digit, 2-digit, 3-digit numbers?
 */
public class FindNthDigit {
    public int findNthDigit(int n) {
        int digits = 1;
        long count = 9;
        long start = 1;

        // Find which group of digits the nth digit belongs to
        while (n > digits * count) {
            n -= digits * count;
            digits++;
            count *= 10;
            start *= 10;
        }

        // Find the actual number
        long number = start + (n - 1) / digits;

        // Find which digit in the number
        String numStr = String.valueOf(number);
        return Character.getNumericValue(numStr.charAt((n - 1) % digits));
    }

    public static void main(String[] args) {
        FindNthDigit solution = new FindNthDigit();
        // Edge Case 1: Normal case
        System.out.println(solution.findNthDigit(3)); // 3
        // Edge Case 2: n in double digits
        System.out.println(solution.findNthDigit(11)); // 0
        // Edge Case 3: Large n
        System.out.println(solution.findNthDigit(1000000)); // Should be a digit
    }
}
