package twopointers.medium;

/**
 * LeetCode 680: Valid Palindrome II
 * https://leetcode.com/problems/valid-palindrome-ii/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given a string, return true if it can be a palindrome after deleting at most
 * one character.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 *
 * Follow-ups:
 * 1. Can you return the index to delete?
 * 2. Can you handle Unicode characters?
 * 3. Can you generalize to k deletions?
 */
public class ValidPalindromeII {
    public boolean validPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            if (s.charAt(l) != s.charAt(r))
                return isPal(s, l + 1, r) || isPal(s, l, r - 1);
            l++;
            r--;
        }
        return true;
    }

    private boolean isPal(String s, int l, int r) {
        while (l < r) {
            if (s.charAt(l++) != s.charAt(r--))
                return false;
        }
        return true;
    }

    // Follow-up 1: Return index to delete
    public int indexToDelete(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            if (s.charAt(l) != s.charAt(r)) {
                if (isPal(s, l + 1, r))
                    return l;
                if (isPal(s, l, r - 1))
                    return r;
                return -1;
            }
            l++;
            r--;
        }
        return -1;
    }

    // Follow-up 2: Unicode characters (already handled if input is Unicode)
    // Follow-up 3: Generalize to k deletions
    public boolean validPalindromeK(String s, int k) {
        return helper(s, 0, s.length() - 1, k);
    }

    private boolean helper(String s, int l, int r, int k) {
        if (k < 0)
            return false;
        while (l < r) {
            if (s.charAt(l) != s.charAt(r))
                return helper(s, l + 1, r, k - 1) || helper(s, l, r - 1, k - 1);
            l++;
            r--;
        }
        return true;
    }

    public static void main(String[] args) {
        ValidPalindromeII solution = new ValidPalindromeII();
        // Basic case
        System.out.println("Basic: " + solution.validPalindrome("abca")); // true

        // Edge: Already palindrome
        System.out.println("Already palindrome: " + solution.validPalindrome("racecar")); // true

        // Edge: Needs one deletion
        System.out.println("Needs one deletion: " + solution.validPalindrome("deeee")); // true

        // Edge: Cannot be palindrome
        System.out.println("Cannot be palindrome: " + solution.validPalindrome("abc")); // false

        // Follow-up: Index to delete
        System.out.println("Index to delete: " + solution.indexToDelete("abca")); // 1

        // Follow-up: k deletions
        System.out.println("Valid with k=2: " + solution.validPalindromeK("abcdeca", 2)); // true
    }
}
