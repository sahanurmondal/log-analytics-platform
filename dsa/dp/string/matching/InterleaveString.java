package dp.string.matching;

import java.util.*;

/**
 * LeetCode 97: Interleaving String
 * https://leetcode.com/problems/interleaving-string/
 *
 * Description:
 * Given strings s1, s2, and s3, find whether s3 is formed by an interleaving of
 * s1 and s2.
 * An interleaving of two strings s and t is a configuration where s and t are
 * divided into n and m non-empty substrings respectively, such that:
 * s = s1 + s2 + ... + sn
 * t = t1 + t2 + ... + tm
 * |n - m| <= 1
 * The interleaving is s1 + t1 + s2 + t2 + ... or t1 + s1 + t2 + s2 + ...
 * Note: a + b is the concatenation of strings a and b.
 *
 * Constraints:
 * - 0 <= s1.length, s2.length <= 100
 * - 0 <= s3.length <= 200
 * - s1, s2, and s3 consist of lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it in O(min(m,n)) space?
 * - What if we need to find one valid interleaving?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class InterleaveString {

    // Approach 1: 2D DP - O(m*n) time, O(m*n) space
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();

        if (m + n != s3.length())
            return false;

        // dp[i][j] = true if s1[0..i-1] and s2[0..j-1] can form s3[0..i+j-1]
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        // Fill first row (only using s2)
        for (int j = 1; j <= n; j++) {
            dp[0][j] = dp[0][j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        // Fill first column (only using s1)
        for (int i = 1; i <= m; i++) {
            dp[i][0] = dp[i - 1][0] && s1.charAt(i - 1) == s3.charAt(i - 1);
        }

        // Fill the rest of the table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int k = i + j - 1;
                dp[i][j] = (dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(k)) ||
                        (dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(k));
            }
        }

        return dp[m][n];
    }

    // Approach 2: Space Optimized DP - O(m*n) time, O(min(m,n)) space
    public boolean isInterleaveOptimized(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();

        if (m + n != s3.length())
            return false;

        // Ensure s2 is the shorter string for space optimization
        if (m < n) {
            return isInterleaveOptimized(s2, s1, s3);
        }

        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        // Fill first row
        for (int j = 1; j <= n; j++) {
            dp[j] = dp[j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        // Fill remaining rows
        for (int i = 1; i <= m; i++) {
            dp[0] = dp[0] && s1.charAt(i - 1) == s3.charAt(i - 1);

            for (int j = 1; j <= n; j++) {
                int k = i + j - 1;
                dp[j] = (dp[j] && s1.charAt(i - 1) == s3.charAt(k)) ||
                        (dp[j - 1] && s2.charAt(j - 1) == s3.charAt(k));
            }
        }

        return dp[n];
    }

    // Approach 3: Memoization - O(m*n) time, O(m*n) space
    public boolean isInterleaveMemo(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();

        if (m + n != s3.length())
            return false;

        Boolean[][] memo = new Boolean[m + 1][n + 1];
        return isInterleaveMemoHelper(s1, s2, s3, 0, 0, 0, memo);
    }

    private boolean isInterleaveMemoHelper(String s1, String s2, String s3,
            int i, int j, int k, Boolean[][] memo) {
        if (k == s3.length())
            return i == s1.length() && j == s2.length();

        if (memo[i][j] != null)
            return memo[i][j];

        boolean result = false;

        // Try using character from s1
        if (i < s1.length() && s1.charAt(i) == s3.charAt(k)) {
            result = isInterleaveMemoHelper(s1, s2, s3, i + 1, j, k + 1, memo);
        }

        // Try using character from s2
        if (!result && j < s2.length() && s2.charAt(j) == s3.charAt(k)) {
            result = isInterleaveMemoHelper(s1, s2, s3, i, j + 1, k + 1, memo);
        }

        memo[i][j] = result;
        return result;
    }

    // Approach 4: BFS - O(m*n) time, O(m*n) space
    public boolean isInterleaveBFS(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();

        if (m + n != s3.length())
            return false;

        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(new int[] { 0, 0 });
        visited.add("0,0");

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int i = current[0], j = current[1];
            int k = i + j;

            if (k == s3.length())
                return true;

            // Try using character from s1
            if (i < m && s1.charAt(i) == s3.charAt(k)) {
                String next = (i + 1) + "," + j;
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.offer(new int[] { i + 1, j });
                }
            }

            // Try using character from s2
            if (j < n && s2.charAt(j) == s3.charAt(k)) {
                String next = i + "," + (j + 1);
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.offer(new int[] { i, j + 1 });
                }
            }
        }

        return false;
    }

    // Approach 5: Get One Valid Interleaving - O(m*n) time, O(m*n) space
    public String getOneInterleaving(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();

        if (m + n != s3.length())
            return "";

        // First check if interleaving is possible
        if (!isInterleave(s1, s2, s3))
            return "";

        // Reconstruct one valid interleaving pattern
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        // Fill DP table
        for (int j = 1; j <= n; j++) {
            dp[0][j] = dp[0][j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        for (int i = 1; i <= m; i++) {
            dp[i][0] = dp[i - 1][0] && s1.charAt(i - 1) == s3.charAt(i - 1);
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int k = i + j - 1;
                dp[i][j] = (dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(k)) ||
                        (dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(k));
            }
        }

        // Reconstruct path
        StringBuilder pattern = new StringBuilder();
        int i = m, j = n;

        while (i > 0 || j > 0) {
            int k = i + j - 1;

            if (i > 0 && dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(k)) {
                pattern.append("s1[" + (i - 1) + "]");
                i--;
            } else if (j > 0 && dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(k)) {
                pattern.append("s2[" + (j - 1) + "]");
                j--;
            }

            if (i > 0 || j > 0)
                pattern.append(" + ");
        }

        return pattern.reverse().toString();
    }

    public static void main(String[] args) {
        InterleaveString solution = new InterleaveString();

        System.out.println("=== Interleaving String Test Cases ===");

        // Test Case 1: Example from problem
        String s1_1 = "aabcc", s2_1 = "dbbca", s3_1 = "aadbbcbcac";
        System.out.println("Test 1 - s1: \"" + s1_1 + "\", s2: \"" + s2_1 + "\", s3: \"" + s3_1 + "\"");
        System.out.println("2D DP: " + solution.isInterleave(s1_1, s2_1, s3_1));
        System.out.println("Optimized: " + solution.isInterleaveOptimized(s1_1, s2_1, s3_1));
        System.out.println("Memoization: " + solution.isInterleaveMemo(s1_1, s2_1, s3_1));
        System.out.println("BFS: " + solution.isInterleaveBFS(s1_1, s2_1, s3_1));
        System.out.println("One Interleaving: " + solution.getOneInterleaving(s1_1, s2_1, s3_1));
        System.out.println("Expected: true\n");

        // Test Case 2: False case
        String s1_2 = "aabcc", s2_2 = "dbbca", s3_2 = "aadbbbaccc";
        System.out.println("Test 2 - s1: \"" + s1_2 + "\", s2: \"" + s2_2 + "\", s3: \"" + s3_2 + "\"");
        System.out.println("2D DP: " + solution.isInterleave(s1_2, s2_2, s3_2));
        System.out.println("Expected: false\n");

        // Test Case 3: Empty strings
        String s1_3 = "", s2_3 = "", s3_3 = "";
        System.out.println("Test 3 - s1: \"" + s1_3 + "\", s2: \"" + s2_3 + "\", s3: \"" + s3_3 + "\"");
        System.out.println("2D DP: " + solution.isInterleave(s1_3, s2_3, s3_3));
        System.out.println("Expected: true\n");

        performanceTest();
    }

    private static void performanceTest() {
        InterleaveString solution = new InterleaveString();

        String s1 = "a".repeat(50);
        String s2 = "b".repeat(50);
        String s3 = ("ab".repeat(50));

        System.out.println("=== Performance Test (String lengths: " + s1.length() + ", " + s2.length() + ", "
                + s3.length() + ") ===");

        long start = System.nanoTime();
        boolean result1 = solution.isInterleave(s1, s2, s3);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result2 = solution.isInterleaveOptimized(s1, s2, s3);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result3 = solution.isInterleaveBFS(s1, s2, s3);
        end = System.nanoTime();
        System.out.println("BFS: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
