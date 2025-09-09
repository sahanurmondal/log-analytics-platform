package dp.string.palindrome;

/**
 * LeetCode 1278: Palindrome Partitioning III
 * https://leetcode.com/problems/palindrome-partitioning-iii/
 *
 * Description:
 * Given a string s and an integer k, partition s into k substrings such that
 * the sum of the minimum number of characters to change to make each substring
 * a palindrome is minimized.
 *
 * Constraints:
 * - 1 <= s.length <= 100
 * - 1 <= k <= s.length
 * - s consists of lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it in O(n^2*k) time?
 * 
 * Company Tags: Google, Amazon
 * Difficulty: Hard
 */
public class PalindromePartitioningIII {

    // Approach 1: DP with Precomputed Costs - O(n^2*k) time, O(n^2 + n*k) space
    public int palindromePartition(String s, int k) {
        int n = s.length();

        // Precompute cost to make substring palindrome
        int[][] cost = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                cost[i][j] = costToPalindrome(s, i, j);
            }
        }

        // dp[i][j] = minimum cost to partition s[0..i-1] into j parts
        int[][] dp = new int[n + 1][k + 1];

        // Initialize with large values
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                dp[i][j] = Integer.MAX_VALUE;
            }
        }

        dp[0][0] = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= Math.min(i, k); j++) {
                for (int prev = j - 1; prev < i; prev++) {
                    if (dp[prev][j - 1] != Integer.MAX_VALUE) {
                        dp[i][j] = Math.min(dp[i][j], dp[prev][j - 1] + cost[prev][i - 1]);
                    }
                }
            }
        }

        return dp[n][k];
    }

    private int costToPalindrome(String s, int left, int right) {
        int cost = 0;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                cost++;
            }
            left++;
            right--;
        }
        return cost;
    }

    // Approach 2: Memoization - O(n^2*k) time, O(n^2*k) space
    public int palindromePartitionMemo(String s, int k) {
        int n = s.length();
        int[][] cost = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                cost[i][j] = costToPalindrome(s, i, j);
            }
        }

        Integer[][] memo = new Integer[n][k + 1];
        return partitionHelper(cost, 0, k, memo);
    }

    private int partitionHelper(int[][] cost, int start, int parts, Integer[][] memo) {
        int n = cost.length;

        if (parts == 1) {
            return cost[start][n - 1];
        }

        if (memo[start][parts] != null) {
            return memo[start][parts];
        }

        int result = Integer.MAX_VALUE;

        for (int end = start; end <= n - parts; end++) {
            result = Math.min(result, cost[start][end] + partitionHelper(cost, end + 1, parts - 1, memo));
        }

        memo[start][parts] = result;
        return result;
    }

    public static void main(String[] args) {
        PalindromePartitioningIII solution = new PalindromePartitioningIII();

        System.out.println("=== Palindrome Partitioning III Test Cases ===");

        // Test Case 1: Example from problem
        System.out.println("Test 1 - s: \"abc\", k: 2");
        System.out.println("DP: " + solution.palindromePartition("abc", 2));
        System.out.println("Memoization: " + solution.palindromePartitionMemo("abc", 2));
        System.out.println("Expected: 1\n");

        // Test Case 2: Already palindromes
        System.out.println("Test 2 - s: \"aabbc\", k: 3");
        System.out.println("DP: " + solution.palindromePartition("aabbc", 3));
        System.out.println("Expected: 0\n");

        // Test Case 3: Each character separate
        System.out.println("Test 3 - s: \"leetcode\", k: 8");
        System.out.println("DP: " + solution.palindromePartition("leetcode", 8));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void performanceTest() {
        PalindromePartitioningIII solution = new PalindromePartitioningIII();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        String testString = sb.toString();

        System.out.println("=== Performance Test (String length: " + testString.length() + ") ===");

        long start = System.nanoTime();
        int result = solution.palindromePartition(testString, 10);
        long end = System.nanoTime();
        System.out.println("DP: " + result + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
