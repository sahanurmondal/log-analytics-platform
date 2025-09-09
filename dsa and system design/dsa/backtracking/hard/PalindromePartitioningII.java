package backtracking.hard;

/**
 * LeetCode 132: Palindrome Partitioning II
 * https://leetcode.com/problems/palindrome-partitioning-ii/
 *
 * Description: Given a string s, partition s such that every substring of the
 * partition is a palindrome.
 * Return the minimum cuts needed for a palindrome partitioning of s.
 * 
 * Constraints:
 * - 1 <= s.length <= 2000
 * - s consists of lowercase English letters only
 *
 * Follow-up:
 * - Can you solve it using DP?
 * 
 * Time Complexity: O(n^2)
 * Space Complexity: O(n^2)
 * 
 * Company Tags: Google, Facebook
 */
public class PalindromePartitioningII {

    public int minCut(String s) {
        int n = s.length();
        boolean[][] isPalindrome = new boolean[n][n];

        // Precompute palindrome information
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                if (s.charAt(i) == s.charAt(j) && (i - j <= 2 || isPalindrome[j + 1][i - 1])) {
                    isPalindrome[j][i] = true;
                }
            }
        }

        // DP for minimum cuts
        int[] dp = new int[n];
        for (int i = 0; i < n; i++) {
            if (isPalindrome[0][i]) {
                dp[i] = 0;
            } else {
                dp[i] = i; // Maximum cuts needed
                for (int j = 1; j <= i; j++) {
                    if (isPalindrome[j][i]) {
                        dp[i] = Math.min(dp[i], dp[j - 1] + 1);
                    }
                }
            }
        }

        return dp[n - 1];
    }

    // Alternative solution - Expand around centers
    public int minCutExpandCenters(String s) {
        int n = s.length();
        int[] cuts = new int[n];

        for (int i = 0; i < n; i++) {
            cuts[i] = i; // Maximum cuts
        }

        for (int i = 0; i < n; i++) {
            // Odd length palindromes
            expandAroundCenter(s, i, i, cuts);
            // Even length palindromes
            expandAroundCenter(s, i, i + 1, cuts);
        }

        return cuts[n - 1];
    }

    private void expandAroundCenter(String s, int left, int right, int[] cuts) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            int newCuts = (left == 0) ? 0 : cuts[left - 1] + 1;
            cuts[right] = Math.min(cuts[right], newCuts);
            left--;
            right++;
        }
    }

    public static void main(String[] args) {
        PalindromePartitioningII solution = new PalindromePartitioningII();

        System.out.println(solution.minCut("aab")); // Expected: 1
        System.out.println(solution.minCut("abcdefg")); // Expected: 6
        System.out.println(solution.minCut("aba")); // Expected: 0
        System.out.println(solution.minCut("racecar")); // Expected: 0
    }
}
