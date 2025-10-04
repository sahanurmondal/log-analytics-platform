package bitmanipulation.medium;

/**
 * LeetCode 1680: Concatenation of Consecutive Binary Numbers
 * https://leetcode.com/problems/concatenation-of-consecutive-binary-numbers/
 *
 * Description: Given an integer n, return the decimal value of the binary
 * string formed by concatenating the binary representations of 1 to n in order,
 * modulo 10^9 + 7.
 * 
 * Constraints:
 * - 1 <= n <= 10^5
 *
 * Follow-up:
 * - Can you solve it efficiently using bit operations?
 * - What about the pattern of bit lengths?
 * 
 * Time Complexity: O(n log n)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google
 */
public class ConcatenationOfConsecutiveBinaryNumbers {

    private static final int MOD = 1_000_000_007;

    // Main optimized solution - Bit manipulation
    public int concatenatedBinary(int n) {
        long result = 0;
        int bitsLength = 0;

        for (int i = 1; i <= n; i++) {
            // Calculate bit length when i is power of 2
            if ((i & (i - 1)) == 0) {
                bitsLength++;
            }

            // Shift result left by bits length of current number and add current number
            result = ((result << bitsLength) + i) % MOD;
        }

        return (int) result;
    }

    // Alternative solution - String concatenation (less efficient)
    public int concatenatedBinaryString(int n) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= n; i++) {
            sb.append(Integer.toBinaryString(i));
        }

        long result = 0;
        long power = 1;

        for (int i = sb.length() - 1; i >= 0; i--) {
            if (sb.charAt(i) == '1') {
                result = (result + power) % MOD;
            }
            power = (power * 2) % MOD;
        }

        return (int) result;
    }

    // Helper method to calculate bit length
    private int getBitLength(int num) {
        return 32 - Integer.numberOfLeadingZeros(num);
    }

    public static void main(String[] args) {
        ConcatenationOfConsecutiveBinaryNumbers solution = new ConcatenationOfConsecutiveBinaryNumbers();

        System.out.println(solution.concatenatedBinary(1)); // Expected: 1
        System.out.println(solution.concatenatedBinary(3)); // Expected: 27
        System.out.println(solution.concatenatedBinary(12)); // Expected: 505379714
        System.out.println(solution.concatenatedBinaryString(3)); // Expected: 27
    }
}
