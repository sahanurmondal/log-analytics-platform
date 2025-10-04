package arrays.medium;

/**
 * LeetCode 91: Decode Ways
 * https://leetcode.com/problems/decode-ways/
 *
 * Description:
 * A message containing letters from A-Z can be encoded into numbers using the
 * following mapping:
 * 'A' -> "1", 'B' -> "2", ..., 'Z' -> "26"
 * To decode an encoded message, all the digits must be grouped then mapped back
 * into letters.
 * Given a string s containing only digits, return the number of ways to decode
 * it.
 *
 * Constraints:
 * - 1 <= s.length <= 100
 * - s contains only digits and may contain leading zero(s)
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Use two variables to track ways to decode previous positions
 * 2. For each digit, check single digit (1-9) and two digit (10-26) validity
 * 3. Update ways based on valid combinations
 */
public class DecodeWays {
    public int numDecodings(String s) {
        if (s == null || s.length() == 0 || s.charAt(0) == '0') {
            return 0;
        }

        int prev2 = 1; // dp[i-2]
        int prev1 = 1; // dp[i-1]

        for (int i = 1; i < s.length(); i++) {
            int current = 0;

            // Single digit
            if (s.charAt(i) != '0') {
                current += prev1;
            }

            // Two digits
            int twoDigit = Integer.parseInt(s.substring(i - 1, i + 1));
            if (twoDigit >= 10 && twoDigit <= 26) {
                current += prev2;
            }

            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    public static void main(String[] args) {
        DecodeWays solution = new DecodeWays();

        // Test Case 1: Normal case
        System.out.println(solution.numDecodings("12")); // Expected: 2

        // Test Case 2: Edge case - with zero
        System.out.println(solution.numDecodings("226")); // Expected: 3

        // Test Case 3: Corner case - starts with zero
        System.out.println(solution.numDecodings("06")); // Expected: 0

        // Test Case 4: Large input
        System.out.println(solution.numDecodings("111111")); // Expected: 13

        // Test Case 5: Minimum input
        System.out.println(solution.numDecodings("1")); // Expected: 1

        // Test Case 6: Special case - only zeros
        System.out.println(solution.numDecodings("10")); // Expected: 1

        // Test Case 7: Boundary case - invalid two digit
        System.out.println(solution.numDecodings("27")); // Expected: 1

        // Test Case 8: Multiple zeros
        System.out.println(solution.numDecodings("2101")); // Expected: 1

        // Test Case 9: All valid two digits
        System.out.println(solution.numDecodings("1226")); // Expected: 5

        // Test Case 10: Invalid sequence
        System.out.println(solution.numDecodings("100")); // Expected: 0
    }
}
