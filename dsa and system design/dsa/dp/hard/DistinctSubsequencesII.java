package dp.hard;

/**
 * LeetCode 940: Distinct Subsequences II
 * https://leetcode.com/problems/distinct-subsequences-ii/
 *
 * Description:
 * Given a string s, return the number of distinct non-empty subsequences of s.
 *
 * Constraints:
 * - 1 <= s.length <= 2000
 * - s consists of lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * 
 * Company Tags: Google, Amazon
 * Difficulty: Hard
 */
public class DistinctSubsequencesII {

    private static final int MOD = 1000000007;

    // Approach 1: DP with Last Occurrence - O(n) time, O(1) space
    public int distinctSubseqII(String s) {
        int n = s.length();
        long[] last = new long[26]; // Last occurrence of each character
        long dp = 1; // Number of distinct subsequences (including empty)

        for (int i = 0; i < n; i++) {
            int c = s.charAt(i) - 'a';
            long prev = dp;
            dp = (dp * 2 - last[c] + MOD) % MOD;
            last[c] = prev;
        }

        return (int) ((dp - 1 + MOD) % MOD); // Exclude empty subsequence
    }

    // Approach 2: DP Array - O(n) time, O(n) space
    public int distinctSubseqIIDP(String s) {
        int n = s.length();
        long[] dp = new long[n + 1];
        dp[0] = 1; // Empty subsequence

        int[] last = new int[26];
        for (int i = 0; i < 26; i++) {
            last[i] = -1;
        }

        for (int i = 1; i <= n; i++) {
            int c = s.charAt(i - 1) - 'a';

            if (last[c] == -1) {
                dp[i] = (dp[i - 1] * 2) % MOD;
            } else {
                dp[i] = (dp[i - 1] * 2 - dp[last[c]] + MOD) % MOD;
            }

            last[c] = i - 1;
        }

        return (int) ((dp[n] - 1 + MOD) % MOD);
    }

    // Approach 3: Set-based DP - O(n^2) time, O(2^n) space (for small inputs)
    public int distinctSubseqIISet(String s) {
        java.util.Set<String>[] dp = new java.util.Set[s.length() + 1];
        for (int i = 0; i <= s.length(); i++) {
            dp[i] = new java.util.HashSet<>();
        }

        dp[0].add(""); // Empty subsequence

        for (int i = 1; i <= s.length(); i++) {
            char c = s.charAt(i - 1);

            // Copy all subsequences from previous step
            dp[i].addAll(dp[i - 1]);

            // Add current character to all previous subsequences
            for (String subseq : dp[i - 1]) {
                dp[i].add(subseq + c);
            }
        }

        return dp[s.length()].size() - 1; // Exclude empty subsequence
    }

    public static void main(String[] args) {
        DistinctSubsequencesII solution = new DistinctSubsequencesII();

        System.out.println("=== Distinct Subsequences II Test Cases ===");

        // Test Case 1
        System.out.println("Test 1 - s: \"abc\"");
        System.out.println("Optimized: " + solution.distinctSubseqII("abc"));
        System.out.println("DP Array: " + solution.distinctSubseqIIDP("abc"));
        System.out.println("Expected: 7\n");

        // Test Case 2
        System.out.println("Test 2 - s: \"aba\"");
        System.out.println("Optimized: " + solution.distinctSubseqII("aba"));
        System.out.println("Expected: 6\n");

        // Test Case 3
        System.out.println("Test 3 - s: \"aaa\"");
        System.out.println("Optimized: " + solution.distinctSubseqII("aaa"));
        System.out.println("Expected: 3\n");

        performanceTest();
    }

    private static void performanceTest() {
        DistinctSubsequencesII solution = new DistinctSubsequencesII();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        String testString = sb.toString();

        System.out.println("=== Performance Test (String length: " + testString.length() + ") ===");

        long start = System.nanoTime();
        int result = solution.distinctSubseqII(testString);
        long end = System.nanoTime();
        System.out.println("Optimized: " + result + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
