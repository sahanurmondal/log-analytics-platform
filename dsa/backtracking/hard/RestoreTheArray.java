package backtracking.hard;

import java.util.*;

/**
 * LeetCode 1416: Restore The Array
 * URL: https://leetcode.com/problems/restore-the-array/
 * Difficulty: Hard
 * Companies: Google, Amazon, Microsoft, Facebook
 * Frequency: Medium
 *
 * Description:
 * A program was supposed to print an array of integers. The program forgot to
 * print whitespaces
 * and the array is printed as a string of digits s and all we know is that all
 * integers in the
 * array were in the range [1, k] and there are no leading zeros in the array.
 * Given the string s and the integer k, return the number of the possible
 * arrays that can be
 * printed as a string s using the mentioned program. Since the answer may be
 * very large,
 * return it modulo 10^9 + 7.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of only digits and does not contain leading zeros
 * - 1 <= k <= 10^9
 *
 * Follow-up Questions:
 * 1. Can you solve it using dynamic programming?
 * 2. How to optimize for very large k values?
 * 3. What if we need to return actual arrays?
 * 4. Can you solve it with memoization?
 */
public class RestoreTheArray {

    private static final int MOD = 1000000007;

    // Approach 1: Dynamic Programming with memoization - O(n * log k)
    public int numberOfArrays(String s, int k) {
        if (s == null || s.length() == 0)
            return 0;

        Map<Integer, Integer> memo = new HashMap<>();
        return dfs(s, 0, k, memo);
    }

    private int dfs(String s, int index, int k, Map<Integer, Integer> memo) {
        if (index == s.length())
            return 1;

        if (s.charAt(index) == '0')
            return 0; // No leading zeros

        if (memo.containsKey(index)) {
            return memo.get(index);
        }

        int count = 0;
        long num = 0;

        for (int i = index; i < s.length(); i++) {
            num = num * 10 + (s.charAt(i) - '0');

            if (num > k)
                break; // Exceeds the limit

            count = (count + dfs(s, i + 1, k, memo)) % MOD;
        }

        memo.put(index, count);
        return count;
    }

    // Approach 2: Bottom-up DP - O(n * log k)
    public int numberOfArraysBottomUp(String s, int k) {
        if (s == null || s.length() == 0)
            return 0;

        int n = s.length();
        int[] dp = new int[n + 1];
        dp[n] = 1; // Base case: empty suffix has 1 way

        for (int i = n - 1; i >= 0; i--) {
            if (s.charAt(i) == '0') {
                dp[i] = 0; // No leading zeros
                continue;
            }

            long num = 0;
            for (int j = i; j < n; j++) {
                num = num * 10 + (s.charAt(j) - '0');

                if (num > k)
                    break;

                dp[i] = (dp[i] + dp[j + 1]) % MOD;
            }
        }

        return dp[0];
    }

    // Approach 3: Optimized space DP - O(n * log k) time, O(log k) space
    public int numberOfArraysOptimized(String s, int k) {
        if (s == null || s.length() == 0)
            return 0;

        int n = s.length();
        int kDigits = String.valueOf(k).length();
        int[] dp = new int[Math.min(n + 1, kDigits + 1)];
        dp[0] = 1;

        for (int i = 1; i <= n; i++) {
            int ways = 0;
            long num = 0;

            for (int j = i - 1; j >= 0 && j >= i - kDigits; j--) {
                if (s.charAt(j) == '0' && j < i - 1)
                    break;

                num = (s.charAt(j) - '0') * pow10(i - j - 1) + num / 10;

                if (num > k)
                    break;

                ways = (ways + dp[j % dp.length]) % MOD;
            }

            dp[i % dp.length] = ways;
        }

        return dp[n % dp.length];
    }

    private long pow10(int exp) {
        long result = 1;
        for (int i = 0; i < exp; i++) {
            result *= 10;
        }
        return result;
    }

    // Follow-up 3: Get all possible arrays (for small inputs)
    public List<List<Integer>> getAllArrays(String s, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (s == null || s.length() == 0)
            return result;

        backtrackArrays(s, 0, k, new ArrayList<>(), result);
        return result;
    }

    private void backtrackArrays(String s, int index, int k, List<Integer> current,
            List<List<Integer>> result) {
        if (index == s.length()) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (s.charAt(index) == '0')
            return;

        long num = 0;
        for (int i = index; i < s.length(); i++) {
            num = num * 10 + (s.charAt(i) - '0');

            if (num > k)
                break;

            current.add((int) num);
            backtrackArrays(s, i + 1, k, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Helper method to validate array formation
    public boolean canFormArray(String s, int[] array, int k) {
        StringBuilder sb = new StringBuilder();
        for (int num : array) {
            if (num < 1 || num > k)
                return false;
            sb.append(num);
        }
        return sb.toString().equals(s);
    }

    // Helper method to get maximum number length within k
    public int getMaxNumberLength(int k) {
        return String.valueOf(k).length();
    }

    // Helper method for performance analysis
    public Map<String, Long> analyzePerformance(String s, int k) {
        Map<String, Long> analysis = new HashMap<>();

        long start = System.currentTimeMillis();
        int result1 = numberOfArrays(s, k);
        long end = System.currentTimeMillis();
        analysis.put("memoization_time", end - start);
        analysis.put("memoization_result", (long) result1);

        start = System.currentTimeMillis();
        int result2 = numberOfArraysBottomUp(s, k);
        end = System.currentTimeMillis();
        analysis.put("bottom_up_time", end - start);
        analysis.put("bottom_up_result", (long) result2);

        return analysis;
    }

    public static void main(String[] args) {
        RestoreTheArray solution = new RestoreTheArray();

        // Test Case 1: Basic example
        System.out.println("Test 1: " + solution.numberOfArrays("1234", 34));
        // Expected: 4

        // Test Case 2: No valid arrays due to leading zeros
        System.out.println("Test 2: " + solution.numberOfArrays("1000", 10));
        // Expected: 0

        // Test Case 3: Single valid split
        System.out.println("Test 3: " + solution.numberOfArrays("2020", 30));
        // Expected: 1

        // Test Case 4: Empty string
        System.out.println("Test 4: " + solution.numberOfArrays("", 10));
        // Expected: 0

        // Test Case 5: Large k value
        System.out.println("Test 5: " + solution.numberOfArrays("123456789", 1000000000));
        // Expected: depends on valid splits

        // Test Case 6: Bottom-up approach
        System.out.println("Test 6 (Bottom-up): " + solution.numberOfArraysBottomUp("1234", 34));
        // Expected: 4

        // Test Case 7: Single digit
        System.out.println("Test 7: " + solution.numberOfArrays("1", 5));
        // Expected: 1

        // Test Case 8: All digits exceed k
        System.out.println("Test 8: " + solution.numberOfArrays("999", 100));
        // Expected: 0

        // Test Case 9: Get all possible arrays
        System.out.println("Test 9 (All Arrays): " + solution.getAllArrays("123", 50));
        // Expected: list of valid arrays

        // Test Case 10: Validate array formation
        System.out.println("Test 10 (Validate): " + solution.canFormArray("123", new int[] { 1, 2, 3 }, 10));
        // Expected: true

        // Test Case 11: Maximum number length
        System.out.println("Test 11 (Max Length): " + solution.getMaxNumberLength(1000));
        // Expected: 4

        // Test Case 12: Performance analysis
        System.out.println("Test 12 (Performance): " + solution.analyzePerformance("12345", 100));
        // Expected: performance metrics

        // Test Case 13: Large input test
        StringBuilder largeInput = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            largeInput.append("1");
        }
        System.out.println("Test 13 (Large): " + solution.numberOfArrays(largeInput.toString(), 1000000000));

        // Test Case 14: Consistency check
        boolean consistent = solution.numberOfArrays("1234", 34) == solution.numberOfArraysBottomUp("1234", 34);
        System.out.println("Test 14 (Consistency): " + consistent);
        // Expected: true

        // Test Case 15: Edge case with zeros
        System.out.println("Test 15: " + solution.numberOfArrays("10203", 1023));
        // Expected: valid count considering no leading zeros
    }
}
