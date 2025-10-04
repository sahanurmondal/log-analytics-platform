package dp.state_machine;

/**
 * LeetCode 91: Decode Ways
 * https://leetcode.com/problems/decode-ways/
 *
 * Description:
 * A message containing letters from A-Z can be encoded into numbers using the
 * following mapping:
 * 'A' -> "1", 'B' -> "2", ..., 'Z' -> "26"
 * Given a string s containing only digits, return the number of ways to decode
 * it.
 *
 * Constraints:
 * - 1 <= s.length <= 100
 * - s contains only digits.
 * - s may contain leading zeros.
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * 
 * Company Tags: Microsoft, Amazon, Google, Facebook, Apple, Uber
 * Difficulty: Medium
 */
public class DecodeWays {

    // Approach 1: Dynamic Programming - O(n) time, O(n) space
    public int numDecodings(String s) {
        if (s == null || s.length() == 0 || s.charAt(0) == '0') {
            return 0;
        }

        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1; // Empty string has 1 way to decode
        dp[1] = 1; // First character (non-zero) has 1 way

        for (int i = 2; i <= n; i++) {
            // Check single digit
            int oneDigit = Integer.parseInt(s.substring(i - 1, i));
            if (oneDigit >= 1 && oneDigit <= 9) {
                dp[i] += dp[i - 1];
            }

            // Check two digits
            int twoDigits = Integer.parseInt(s.substring(i - 2, i));
            if (twoDigits >= 10 && twoDigits <= 26) {
                dp[i] += dp[i - 2];
            }
        }

        return dp[n];
    }

    // Approach 2: Space Optimized DP - O(n) time, O(1) space
    public int numDecodingsOptimized(String s) {
        if (s == null || s.length() == 0 || s.charAt(0) == '0') {
            return 0;
        }

        int n = s.length();
        int prev2 = 1; // dp[i-2]
        int prev1 = 1; // dp[i-1]

        for (int i = 2; i <= n; i++) {
            int curr = 0;

            // Check single digit
            int oneDigit = s.charAt(i - 1) - '0';
            if (oneDigit >= 1 && oneDigit <= 9) {
                curr += prev1;
            }

            // Check two digits
            int twoDigits = (s.charAt(i - 2) - '0') * 10 + (s.charAt(i - 1) - '0');
            if (twoDigits >= 10 && twoDigits <= 26) {
                curr += prev2;
            }

            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }

    // Approach 3: Recursive with Memoization - O(n) time, O(n) space
    public int numDecodingsMemo(String s) {
        if (s == null || s.length() == 0)
            return 0;

        Integer[] memo = new Integer[s.length()];
        return decode(s, 0, memo);
    }

    private int decode(String s, int index, Integer[] memo) {
        if (index == s.length())
            return 1;
        if (s.charAt(index) == '0')
            return 0;

        if (memo[index] != null)
            return memo[index];

        int result = decode(s, index + 1, memo);

        if (index + 1 < s.length()) {
            int twoDigit = Integer.parseInt(s.substring(index, index + 2));
            if (twoDigit <= 26) {
                result += decode(s, index + 2, memo);
            }
        }

        memo[index] = result;
        return result;
    }

    // Approach 4: Iterative with clear logic - O(n) time, O(1) space
    public int numDecodingsIterative(String s) {
        if (s == null || s.length() == 0 || s.charAt(0) == '0') {
            return 0;
        }

        int n = s.length();
        if (n == 1)
            return 1;

        int ways1 = 1; // ways[i-1]
        int ways2 = 1; // ways[i-2]

        for (int i = 1; i < n; i++) {
            int currentWays = 0;

            // Current digit forms valid single character code
            if (s.charAt(i) != '0') {
                currentWays += ways1;
            }

            // Previous and current digits form valid two character code
            int twoDigitValue = (s.charAt(i - 1) - '0') * 10 + (s.charAt(i) - '0');
            if (twoDigitValue >= 10 && twoDigitValue <= 26) {
                currentWays += ways2;
            }

            ways2 = ways1;
            ways1 = currentWays;
        }

        return ways1;
    }

    // Approach 5: Handle edge cases explicitly - O(n) time, O(1) space
    public int numDecodingsRobust(String s) {
        if (s == null || s.length() == 0)
            return 0;

        int n = s.length();

        // Handle first character
        if (s.charAt(0) == '0')
            return 0;
        if (n == 1)
            return 1;

        int prev2 = 1; // Number of ways to decode s[0...i-2]
        int prev1 = 1; // Number of ways to decode s[0...i-1]

        for (int i = 1; i < n; i++) {
            int curr = 0;

            // Single character decode
            char currentChar = s.charAt(i);
            if (currentChar != '0') {
                curr = prev1;
            }

            // Two character decode
            char prevChar = s.charAt(i - 1);
            if (prevChar == '1' || (prevChar == '2' && currentChar <= '6')) {
                curr += prev2;
            }

            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }

    public static void main(String[] args) {
        DecodeWays solution = new DecodeWays();

        System.out.println("=== Decode Ways Test Cases ===");

        // Test case 1: Normal case "12"
        String s1 = "12";
        System.out.println("String: \"" + s1 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s1)); // Expected: 2 ("AB" or "L")
        System.out.println("Optimized: " + solution.numDecodingsOptimized(s1)); // Expected: 2
        System.out.println("Memoization: " + solution.numDecodingsMemo(s1)); // Expected: 2
        System.out.println("Iterative: " + solution.numDecodingsIterative(s1)); // Expected: 2
        System.out.println("Robust: " + solution.numDecodingsRobust(s1)); // Expected: 2

        // Test case 2: "226"
        String s2 = "226";
        System.out.println("\nString: \"" + s2 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s2)); // Expected: 3 ("BBF", "BZ", "VF")
        System.out.println("Optimized: " + solution.numDecodingsOptimized(s2)); // Expected: 3

        // Test case 3: Leading zero "06"
        String s3 = "06";
        System.out.println("\nString: \"" + s3 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s3)); // Expected: 0
        System.out.println("Optimized: " + solution.numDecodingsOptimized(s3)); // Expected: 0

        // Test case 4: Contains zero "10"
        String s4 = "10";
        System.out.println("\nString: \"" + s4 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s4)); // Expected: 1 ("J")
        System.out.println("Optimized: " + solution.numDecodingsOptimized(s4)); // Expected: 1

        // Test case 5: Multiple zeros "102"
        String s5 = "102";
        System.out.println("\nString: \"" + s5 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s5)); // Expected: 1 ("JB")
        System.out.println("Memoization: " + solution.numDecodingsMemo(s5)); // Expected: 1

        // Test case 6: Single digit "7"
        String s6 = "7";
        System.out.println("\nString: \"" + s6 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s6)); // Expected: 1 ("G")

        // Test case 7: Edge case "27"
        String s7 = "27";
        System.out.println("\nString: \"" + s7 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s7)); // Expected: 1 ("BG")
        System.out.println("Robust: " + solution.numDecodingsRobust(s7)); // Expected: 1

        // Test case 8: Complex case "1123"
        String s8 = "1123";
        System.out.println("\nString: \"" + s8 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s8)); // Expected: 5
        System.out.println("Optimized: " + solution.numDecodingsOptimized(s8)); // Expected: 5

        // Test case 9: Edge case with 0 "1001"
        String s9 = "1001";
        System.out.println("\nString: \"" + s9 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s9)); // Expected: 0

        // Test case 10: All same digits "1111"
        String s10 = "1111";
        System.out.println("\nString: \"" + s10 + "\"");
        System.out.println("DP Array: " + solution.numDecodings(s10)); // Expected: 5
        System.out.println("Optimized: " + solution.numDecodingsOptimized(s10)); // Expected: 5

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Comparison ===");
        DecodeWays solution = new DecodeWays();

        // Create large test string (avoiding zeros for valid decoding)
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < 100; i++) {
            sb.append(random.nextInt(9) + 1); // Digits 1-9
        }
        String largeString = sb.toString();

        long startTime, endTime;

        // Test DP Array approach
        startTime = System.nanoTime();
        int result1 = solution.numDecodings(largeString);
        endTime = System.nanoTime();
        System.out.println("DP Array: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Optimized approach
        startTime = System.nanoTime();
        int result2 = solution.numDecodingsOptimized(largeString);
        endTime = System.nanoTime();
        System.out.println("Optimized: " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Memoization approach
        startTime = System.nanoTime();
        int result3 = solution.numDecodingsMemo(largeString);
        endTime = System.nanoTime();
        System.out.println("Memoization: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Iterative approach
        startTime = System.nanoTime();
        int result4 = solution.numDecodingsIterative(largeString);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result4 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Robust approach
        startTime = System.nanoTime();
        int result5 = solution.numDecodingsRobust(largeString);
        endTime = System.nanoTime();
        System.out.println("Robust: " + result5 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        System.out.println("All approaches should return the same result: " +
                (result1 == result2 && result2 == result3 &&
                        result3 == result4 && result4 == result5));
    }
}
