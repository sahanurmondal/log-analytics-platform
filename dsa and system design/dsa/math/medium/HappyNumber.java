package math.medium;

/**
 * LeetCode 202: Happy Number
 * https://leetcode.com/problems/happy-number/
 *
 * Description:
 * Write an algorithm to determine if a number is "happy".
 *
 * Constraints:
 * - 1 <= n <= 2^31-1
 *
 * Follow-up:
 * - Can you solve it with cycle detection?
 */
import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 202: Happy Number
 * https://leetcode.com/problems/happy-number/
 *
 * Description:
 * Write an algorithm to determine if a number is "happy".
 *
 * Constraints:
 * - 1 <= n <= 2^31-1
 *
 * Follow-up:
 * - Can you solve it with cycle detection?
 * - Can you implement Floyd's cycle detection algorithm?
 */
public class HappyNumber {
    public boolean isHappy(int n) {
        Set<Integer> seen = new HashSet<>();

        while (n != 1 && !seen.contains(n)) {
            seen.add(n);
            n = getSumOfSquares(n);
        }

        return n == 1;
    }

    private int getSumOfSquares(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }

    public static void main(String[] args) {
        HappyNumber solution = new HappyNumber();
        // Edge Case 1: Normal case
        System.out.println(solution.isHappy(19)); // true
        // Edge Case 2: Not happy
        System.out.println(solution.isHappy(2)); // false
        // Edge Case 3: Large n
        System.out.println(solution.isHappy(100000)); // false
    }
}
