package dp.string.subsequence;

import java.util.*;

/**
 * LeetCode 730: Count Different Palindromic Subsequences
 * https://leetcode.com/problems/count-different-palindromic-subsequences/
 *
 * Description:
 * Given a string s, return the number of different non-empty palindromic
 * subsequences in s.
 * Since the answer may be very large, return it modulo 10^9 + 7.
 * A subsequence of a string is obtained by deleting some (possibly none)
 * characters from the string.
 * A sequence is palindromic if it is equal to the sequence reversed.
 * Two sequences A and B are different if there is some index i where A[i] !=
 * B[i].
 *
 * Constraints:
 * - 1 <= s.length <= 1000
 * - s[i] is either 'a', 'b', 'c', or 'd'.
 *
 * Follow-up:
 * - Can you solve it for any alphabet size?
 * - What if we need to find the actual palindromic subsequences?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class CountDifferentPalindromicSubsequences {

    private static final int MOD = 1000000007;

    // Approach 1: 3D DP - O(n^2) time, O(n^2) space
    public int countPalindromicSubsequences(String s) {
        int n = s.length();
        // dp[c][i][j] = number of palindromic subsequences in s[i..j] starting and
        // ending with character c
        long[][][] dp = new long[4][n][n];

        // Base case: single characters
        for (int i = 0; i < n; i++) {
            int c = s.charAt(i) - 'a';
            dp[c][i][i] = 1;
        }

        // Fill for lengths 2 to n
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;

                for (int c = 0; c < 4; c++) {
                    char ch = (char) ('a' + c);

                    if (s.charAt(i) != ch) {
                        dp[c][i][j] = dp[c][i + 1][j];
                    } else if (s.charAt(j) != ch) {
                        dp[c][i][j] = dp[c][i][j - 1];
                    } else if (i == j) {
                        dp[c][i][j] = 1;
                    } else if (i + 1 == j) {
                        dp[c][i][j] = 2; // "c" and "cc"
                    } else {
                        // s[i] == s[j] == ch
                        dp[c][i][j] = (2 + dp[0][i + 1][j - 1] + dp[1][i + 1][j - 1] +
                                dp[2][i + 1][j - 1] + dp[3][i + 1][j - 1]) % MOD;
                    }
                }
            }
        }

        return (int) ((dp[0][0][n - 1] + dp[1][0][n - 1] + dp[2][0][n - 1] + dp[3][0][n - 1]) % MOD);
    }

    // Approach 2: Interval DP with Character Tracking - O(n^2) time, O(n^2) space
    public int countPalindromicSubsequencesInterval(String s) {
        int n = s.length();
        Integer[][] memo = new Integer[n][n];
        return (int) countHelper(s, 0, n - 1, memo);
    }

    private long countHelper(String s, int i, int j, Integer[][] memo) {
        if (i > j)
            return 0;
        if (i == j)
            return 1;

        if (memo[i][j] != null)
            return memo[i][j];

        long result = 0;

        // Try each character 'a' to 'd'
        for (char c = 'a'; c <= 'd'; c++) {
            int left = i, right = j;

            // Find leftmost occurrence of c
            while (left <= right && s.charAt(left) != c)
                left++;

            // Find rightmost occurrence of c
            while (left <= right && s.charAt(right) != c)
                right--;

            if (left > right) {
                // Character c not found in range [i, j]
                continue;
            } else if (left == right) {
                // Single occurrence of c
                result = (result + 1) % MOD;
            } else {
                // Multiple occurrences of c
                result = (result + 2 + countHelper(s, left + 1, right - 1, memo)) % MOD;
            }
        }

        memo[i][j] = (int) result;
        return result;
    }

    // Approach 3: Optimized with Next/Prev Arrays - O(n^2) time, O(n) space
    public int countPalindromicSubsequencesOptimized(String s) {
        int n = s.length();

        // Precompute next and previous occurrence arrays
        int[][] next = new int[n][4];
        int[][] prev = new int[n][4];

        Arrays.fill(next[n - 1], n);
        Arrays.fill(prev[0], -1);

        // Fill next array
        for (int i = n - 2; i >= 0; i--) {
            System.arraycopy(next[i + 1], 0, next[i], 0, 4);
            next[i][s.charAt(i + 1) - 'a'] = i + 1;
        }

        // Fill prev array
        for (int i = 1; i < n; i++) {
            System.arraycopy(prev[i - 1], 0, prev[i], 0, 4);
            prev[i][s.charAt(i - 1) - 'a'] = i - 1;
        }

        Integer[][] memo = new Integer[n][n];
        return (int) countOptimizedHelper(s, 0, n - 1, next, prev, memo);
    }

    private long countOptimizedHelper(String s, int i, int j, int[][] next, int[][] prev, Integer[][] memo) {
        if (i > j)
            return 0;
        if (i == j)
            return 1;

        if (memo[i][j] != null)
            return memo[i][j];

        long result = 0;

        for (int c = 0; c < 4; c++) {
            int left = (i == 0) ? next[i][c] : (s.charAt(i) - 'a' == c) ? i : next[i][c];
            int right = (j == s.length() - 1) ? prev[j][c] : (s.charAt(j) - 'a' == c) ? j : prev[j][c];

            if (left > j || right < i || left > right)
                continue;

            if (left == right) {
                result = (result + 1) % MOD;
            } else {
                result = (result + 2 + countOptimizedHelper(s, left + 1, right - 1, next, prev, memo)) % MOD;
            }
        }

        memo[i][j] = (int) result;
        return result;
    }

    // Approach 4: Bottom-up DP - O(n^2) time, O(n^2) space
    public int countPalindromicSubsequencesBottomUp(String s) {
        int n = s.length();
        long[][] dp = new long[n][n];

        // Base case
        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
        }

        // Fill for lengths 2 to n
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;

                Set<Character> chars = new HashSet<>();
                for (int k = i; k <= j; k++) {
                    chars.add(s.charAt(k));
                }

                for (char c : chars) {
                    int left = i, right = j;

                    while (s.charAt(left) != c)
                        left++;
                    while (s.charAt(right) != c)
                        right--;

                    if (left == right) {
                        dp[i][j] = (dp[i][j] + 1) % MOD;
                    } else {
                        dp[i][j] = (dp[i][j] + 2 + dp[left + 1][right - 1]) % MOD;
                    }
                }
            }
        }

        return (int) dp[0][n - 1];
    }

    // Approach 5: Get All Palindromic Subsequences - O(2^n) time, O(2^n) space
    public Set<String> getAllPalindromicSubsequences(String s) {
        Set<String> result = new HashSet<>();
        getAllPalindromicHelper(s, 0, new StringBuilder(), result);
        return result;
    }

    private void getAllPalindromicHelper(String s, int index, StringBuilder current, Set<String> result) {
        if (index == s.length()) {
            String str = current.toString();
            if (str.length() > 0 && isPalindrome(str)) {
                result.add(str);
            }
            return;
        }

        // Don't include current character
        getAllPalindromicHelper(s, index + 1, current, result);

        // Include current character
        current.append(s.charAt(index));
        getAllPalindromicHelper(s, index + 1, current, result);
        current.deleteCharAt(current.length() - 1);
    }

    private boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right))
                return false;
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        CountDifferentPalindromicSubsequences solution = new CountDifferentPalindromicSubsequences();

        System.out.println("=== Count Different Palindromic Subsequences Test Cases ===");

        // Test Case 1: Example from problem
        String s1 = "bccb";
        System.out.println("Test 1 - String: \"" + s1 + "\"");
        System.out.println("3D DP: " + solution.countPalindromicSubsequences(s1));
        System.out.println("Interval DP: " + solution.countPalindromicSubsequencesInterval(s1));
        System.out.println("Optimized: " + solution.countPalindromicSubsequencesOptimized(s1));
        System.out.println("Bottom-up: " + solution.countPalindromicSubsequencesBottomUp(s1));

        Set<String> palindromes1 = solution.getAllPalindromicSubsequences(s1);
        System.out.println("All palindromic subsequences (" + palindromes1.size() + " total):");
        List<String> sortedPalindromes = new ArrayList<>(palindromes1);
        Collections.sort(sortedPalindromes);
        for (String p : sortedPalindromes) {
            System.out.println("  \"" + p + "\"");
        }
        System.out.println("Expected: 6\n");

        // Test Case 2: More complex
        String s2 = "abcdabcdabcdabcdabcdabcdabcdabcddcbadcbadcbadcbadcbadcbadcbadcba";
        System.out.println("Test 2 - String: \"" + s2 + "\"");
        System.out.println("3D DP: " + solution.countPalindromicSubsequences(s2));
        System.out.println("Expected: 104860361\n");

        performanceTest();
    }

    private static void performanceTest() {
        CountDifferentPalindromicSubsequences solution = new CountDifferentPalindromicSubsequences();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append((char) ('a' + (int) (Math.random() * 4)));
        }
        String testString = sb.toString();

        System.out.println("=== Performance Test (String length: " + testString.length() + ") ===");

        long start = System.nanoTime();
        int result1 = solution.countPalindromicSubsequences(testString);
        long end = System.nanoTime();
        System.out.println("3D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.countPalindromicSubsequencesInterval(testString);
        end = System.nanoTime();
        System.out.println("Interval DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.countPalindromicSubsequencesOptimized(testString);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
