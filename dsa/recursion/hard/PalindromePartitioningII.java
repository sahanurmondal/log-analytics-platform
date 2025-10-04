package recursion.hard;

/**
 * LeetCode 132: Palindrome Partitioning II
 * https://leetcode.com/problems/palindrome-partitioning-ii/
 *
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * Given a string s, partition s such that every substring is a palindrome.
 * Return the minimum cuts needed.
 *
 * Constraints:
 * - 1 <= s.length <= 2000
 *
 * Follow-ups:
 * 1. Can you return the actual partitions?
 * 2. Can you optimize for large strings?
 * 3. Can you handle only even-length palindromes?
 */
public class PalindromePartitioningII {
    public int minCut(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];
        int[] cuts = new int[n];
        for (int i = 0; i < n; i++) {
            int min = i;
            for (int j = 0; j <= i; j++) {
                if (s.charAt(j) == s.charAt(i) && (i - j < 2 || pal[j + 1][i - 1])) {
                    pal[j][i] = true;
                    min = j == 0 ? 0 : Math.min(min, cuts[j - 1] + 1);
                }
            }
            cuts[i] = min;
        }
        return cuts[n - 1];
    }

    // Follow-up 1: Return actual partitions
    public java.util.List<java.util.List<String>> allPartitions(String s) {
        java.util.List<java.util.List<String>> res = new java.util.ArrayList<>();
        dfs(s, 0, new java.util.ArrayList<>(), res);
        return res;
    }

    private void dfs(String s, int start, java.util.List<String> path, java.util.List<java.util.List<String>> res) {
        if (start == s.length()) {
            res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int end = start + 1; end <= s.length(); end++) {
            String sub = s.substring(start, end);
            if (isPalindrome(sub)) {
                path.add(sub);
                dfs(s, end, path, res);
                path.remove(path.size() - 1);
            }
        }
    }

    private boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r)
            if (s.charAt(l++) != s.charAt(r--))
                return false;
        return true;
    }

    // Follow-up 2: Optimize for large strings (already handled above)
    // Follow-up 3: Only even-length palindromes
    public int minCutEvenPal(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];
        int[] cuts = new int[n];
        for (int i = 0; i < n; i++) {
            int min = i;
            for (int j = 0; j <= i; j++) {
                if ((i - j + 1) % 2 == 0 && s.charAt(j) == s.charAt(i) && (i - j < 2 || pal[j + 1][i - 1])) {
                    pal[j][i] = true;
                    min = j == 0 ? 0 : Math.min(min, cuts[j - 1] + 1);
                }
            }
            cuts[i] = min;
        }
        return cuts[n - 1];
    }

    public static void main(String[] args) {
        PalindromePartitioningII solution = new PalindromePartitioningII();
        System.out.println(solution.minCut("aab")); // 1
        System.out.println(solution.minCut("a")); // 0
        System.out.println(solution.minCut("ab")); // 1
        // Edge Case: All palindrome
        System.out.println(solution.minCut("aba")); // 0
        System.out.println(solution.minCut("abccba")); // 0
        // Edge Case: No palindromes
        System.out.println(solution.minCut("abcdef")); // 5
        // Edge Case: Long string
        System.out.println(solution.minCut("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")); // 0

        // Follow-up 1: Actual partitions
        System.out.println(solution.allPartitions("aab")); // [["a","a","b"],["aa","b"]]

        // Follow-up 3: Only even-length palindromes
        System.out.println(solution.minCutEvenPal("aabbaa")); // 0
    }
}
