package dp.medium;

/**
 * LeetCode 647: Palindromic Substrings
 * https://leetcode.com/problems/palindromic-substrings/
 *
 * Description:
 * Given a string s, return the number of palindromic substrings in it.
 *
 * Constraints:
 * - 1 <= s.length <= 1000
 * - s consists of lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it in O(n^2) time?
 */
public class PalindromicSubstrings {
    public int countSubstrings(String s) {
        int n = s.length();
        int count = 0;

        // Expand Around Center approach
        for (int center = 0; center < 2 * n - 1; center++) {
            int left = center / 2;
            int right = left + center % 2;

            while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) {
                count++;
                left--;
                right++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        PalindromicSubstrings solution = new PalindromicSubstrings();
        // Edge Case 1: Normal case
        System.out.println(solution.countSubstrings("abc")); // 3
        // Edge Case 2: All same
        System.out.println(solution.countSubstrings("aaa")); // 6
        // Edge Case 3: Empty string
        System.out.println(solution.countSubstrings("")); // 0
        // Edge Case 4: Large input
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++)
            sb.append('a');
        System.out.println(solution.countSubstrings(sb.toString())); // 500500
    }
}
