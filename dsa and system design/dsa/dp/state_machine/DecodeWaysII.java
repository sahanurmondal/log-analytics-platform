package dp.state_machine;

import java.util.Arrays;

/**
 * LeetCode 639: Decode Ways II
 * https://leetcode.com/problems/decode-ways-ii/
 *
 * Description:
 * A message containing letters from A-Z can be encoded into numbers using the
 * following mapping:
 * 'A' -> "1", 'B' -> "2", ..., 'Z' -> "26"
 * To decode an encoded message, all the digits must be grouped then mapped back
 * into letters using the reverse of the mapping above.
 * Now, we're given a string s containing digits and '*' characters. '*' can
 * represent any digit from 1 to 9.
 * Return the number of ways to decode it. Since the answer may be very large,
 * return it modulo 10^9 + 7.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s[i] is a digit or '*'.
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * - What if '*' can represent 0 as well?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard (categorized as Medium for this collection)
 */
public class DecodeWaysII {

    private static final int MOD = 1000000007;

    // Approach 1: DP with Cases - O(n) time, O(n) space
    public int numDecodings(String s) {
        int n = s.length();
        if (n == 0)
            return 0;

        long[] dp = new long[n + 1];
        dp[0] = 1;
        dp[1] = getWays(s.charAt(0));

        for (int i = 2; i <= n; i++) {
            // Single character decode
            dp[i] = (dp[i - 1] * getWays(s.charAt(i - 1))) % MOD;

            // Two character decode
            dp[i] = (dp[i] + dp[i - 2] * getWays(s.charAt(i - 2), s.charAt(i - 1))) % MOD;
        }

        return (int) dp[n];
    }

    private int getWays(char c) {
        if (c == '*')
            return 9; // 1-9
        if (c == '0')
            return 0; // Invalid single decode
        return 1; // Valid single digit
    }

    private int getWays(char c1, char c2) {
        if (c1 == '*' && c2 == '*') {
            return 15; // 11-19 (9 ways) + 21-26 (6 ways)
        }

        if (c1 == '*') {
            if (c2 >= '0' && c2 <= '6')
                return 2; // 1x, 2x
            if (c2 >= '7' && c2 <= '9')
                return 1; // 1x only
            return 0;
        }

        if (c2 == '*') {
            if (c1 == '1')
                return 9; // 11-19
            if (c1 == '2')
                return 6; // 21-26
            return 0;
        }

        // Both are digits
        int num = (c1 - '0') * 10 + (c2 - '0');
        return (num >= 10 && num <= 26) ? 1 : 0;
    }

    // Approach 2: Space Optimized DP - O(n) time, O(1) space
    public int numDecodingsOptimized(String s) {
        int n = s.length();
        if (n == 0)
            return 0;

        long prev2 = 1; // dp[i-2]
        long prev1 = getWays(s.charAt(0)); // dp[i-1]

        if (n == 1)
            return (int) prev1;

        for (int i = 2; i <= n; i++) {
            long current = (prev1 * getWays(s.charAt(i - 1))) % MOD;
            current = (current + prev2 * getWays(s.charAt(i - 2), s.charAt(i - 1))) % MOD;

            prev2 = prev1;
            prev1 = current;
        }

        return (int) prev1;
    }

    // Approach 3: Memoization - O(n) time, O(n) space
    public int numDecodingsMemo(String s) {
        Long[] memo = new Long[s.length()];
        return (int) numDecodingsMemoHelper(s, 0, memo);
    }

    private long numDecodingsMemoHelper(String s, int index, Long[] memo) {
        if (index >= s.length())
            return 1;

        if (memo[index] != null)
            return memo[index];

        long result = 0;

        // Single character decode
        int singleWays = getWays(s.charAt(index));
        if (singleWays > 0) {
            result = (result + singleWays * numDecodingsMemoHelper(s, index + 1, memo)) % MOD;
        }

        // Two character decode
        if (index + 1 < s.length()) {
            int doubleWays = getWays(s.charAt(index), s.charAt(index + 1));
            if (doubleWays > 0) {
                result = (result + doubleWays * numDecodingsMemoHelper(s, index + 2, memo)) % MOD;
            }
        }

        memo[index] = result;
        return result;
    }

    // Approach 4: Detailed Case Analysis - O(n) time, O(1) space
    public int numDecodingsDetailed(String s) {
        int n = s.length();
        if (n == 0)
            return 0;

        long first = 1, second = 0;

        for (int i = n - 1; i >= 0; i--) {
            long current = 0;

            // Single digit decode
            if (s.charAt(i) == '*') {
                current = (9 * first) % MOD; // 1-9
            } else if (s.charAt(i) != '0') {
                current = first;
            }

            // Two digit decode
            if (i + 1 < n) {
                if (s.charAt(i) == '1') {
                    if (s.charAt(i + 1) == '*') {
                        current = (current + 9 * second) % MOD; // 11-19
                    } else {
                        current = (current + second) % MOD; // 10-19
                    }
                } else if (s.charAt(i) == '2') {
                    if (s.charAt(i + 1) == '*') {
                        current = (current + 6 * second) % MOD; // 21-26
                    } else if (s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '6') {
                        current = (current + second) % MOD; // 20-26
                    }
                } else if (s.charAt(i) == '*') {
                    if (s.charAt(i + 1) == '*') {
                        current = (current + 15 * second) % MOD; // 11-19, 21-26
                    } else if (s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '6') {
                        current = (current + 2 * second) % MOD; // 1x, 2x
                    } else {
                        current = (current + second) % MOD; // 1x only
                    }
                }
            }

            second = first;
            first = current;
        }

        return (int) first;
    }

    // Approach 5: Iterative with State Machine - O(n) time, O(1) space
    public int numDecodingsStateMachine(String s) {
        int n = s.length();
        if (n == 0)
            return 0;

        // State: number of ways to decode up to current position
        long ways0 = 1; // Base case
        long ways1 = 0; // Will be computed for first character

        // Process first character
        if (s.charAt(0) == '*') {
            ways1 = 9;
        } else if (s.charAt(0) != '0') {
            ways1 = 1;
        }

        if (n == 1)
            return (int) ways1;

        for (int i = 1; i < n; i++) {
            long newWays = 0;

            // Current character as single decode
            if (s.charAt(i) == '*') {
                newWays = (newWays + 9 * ways1) % MOD;
            } else if (s.charAt(i) != '0') {
                newWays = (newWays + ways1) % MOD;
            }

            // Previous + current as double decode
            char prev = s.charAt(i - 1);
            char curr = s.charAt(i);

            if (prev == '1') {
                if (curr == '*') {
                    newWays = (newWays + 9 * ways0) % MOD;
                } else {
                    newWays = (newWays + ways0) % MOD;
                }
            } else if (prev == '2') {
                if (curr == '*') {
                    newWays = (newWays + 6 * ways0) % MOD;
                } else if (curr >= '0' && curr <= '6') {
                    newWays = (newWays + ways0) % MOD;
                }
            } else if (prev == '*') {
                if (curr == '*') {
                    newWays = (newWays + 15 * ways0) % MOD;
                } else if (curr >= '0' && curr <= '6') {
                    newWays = (newWays + 2 * ways0) % MOD;
                } else {
                    newWays = (newWays + ways0) % MOD;
                }
            }

            ways0 = ways1;
            ways1 = newWays;
        }

        return (int) ways1;
    }

    public static void main(String[] args) {
        DecodeWaysII solution = new DecodeWaysII();

        System.out.println("=== Decode Ways II Test Cases ===");

        // Test Case 1: Example with *
        String s1 = "*";
        System.out.println("Test 1 - String: \"" + s1 + "\"");
        System.out.println("DP: " + solution.numDecodings(s1));
        System.out.println("Optimized: " + solution.numDecodingsOptimized(s1));
        System.out.println("Memoization: " + solution.numDecodingsMemo(s1));
        System.out.println("Detailed: " + solution.numDecodingsDetailed(s1));
        System.out.println("State Machine: " + solution.numDecodingsStateMachine(s1));
        System.out.println("Expected: 9\n");

        // Test Case 2: Multiple *
        String s2 = "1*";
        System.out.println("Test 2 - String: \"" + s2 + "\"");
        System.out.println("DP: " + solution.numDecodings(s2));
        System.out.println("Expected: 18\n");

        // Test Case 3: Complex case
        String s3 = "2*";
        System.out.println("Test 3 - String: \"" + s3 + "\"");
        System.out.println("DP: " + solution.numDecodings(s3));
        System.out.println("Expected: 15\n");

        performanceTest();
    }

    private static void performanceTest() {
        DecodeWaysII solution = new DecodeWaysII();

        // Create large test string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append(Math.random() > 0.5 ? "*" : "1");
        }
        String testString = sb.toString();

        System.out.println("=== Performance Test (String length: " + testString.length() + ") ===");

        long start = System.nanoTime();
        int result1 = solution.numDecodingsOptimized(testString);
        long end = System.nanoTime();
        System.out.println("Optimized: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.numDecodingsDetailed(testString);
        end = System.nanoTime();
        System.out.println("Detailed: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.numDecodingsStateMachine(testString);
        end = System.nanoTime();
        System.out.println("State Machine: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
