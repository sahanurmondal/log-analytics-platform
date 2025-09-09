package math.hard;

/**
 * LeetCode 866: Prime Palindrome
 * https://leetcode.com/problems/prime-palindrome/
 *
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description:
 * Find the smallest prime palindrome greater than or equal to n.
 *
 * Constraints:
 * - 1 <= n <= 10^8
 *
 * Follow-ups:
 * 1. Can you generate all prime palindromes in a range?
 * 2. Can you optimize for large n?
 * 3. Can you count prime palindromes?
 */
public class PrimePalindrome {
    public int primePalindrome(int n) {
        for (int i = n;; i++) {
            if (isPalindrome(i) && isPrime(i))
                return i;
            if (i > 10 && i < 10000000 && (int) Math.log10(i) % 2 == 0)
                i = (int) Math.pow(10, (int) Math.log10(i));
        }
    }

    private boolean isPalindrome(int x) {
        String s = Integer.toString(x);
        int l = 0, r = s.length() - 1;
        while (l < r)
            if (s.charAt(l++) != s.charAt(r--))
                return false;
        return true;
    }

    private boolean isPrime(int x) {
        if (x < 2)
            return false;
        for (int i = 2; i * i <= x; i++)
            if (x % i == 0)
                return false;
        return true;
    }

    // Follow-up 1: Generate all prime palindromes in [L, R]
    public java.util.List<Integer> primePalindromesInRange(int L, int R) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        for (int i = L; i <= R; i++)
            if (isPalindrome(i) && isPrime(i))
                res.add(i);
        return res;
    }

    // Follow-up 2: Count prime palindromes in [L, R]
    public int countPrimePalindromes(int L, int R) {
        int count = 0;
        for (int i = L; i <= R; i++)
            if (isPalindrome(i) && isPrime(i))
                count++;
        return count;
    }

    // Follow-up 3: Optimize for large n (generate palindromes only)
    public int primePalindromeOptimized(int n) {
        for (int len = 1; len <= 9; len++) {
            for (int pal : generatePalindromes(len)) {
                if (pal >= n && isPrime(pal))
                    return pal;
            }
        }
        return -1;
    }

    private java.util.List<Integer> generatePalindromes(int len) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        int start = (int) Math.pow(10, (len - 1) / 2), end = (int) Math.pow(10, (len + 1) / 2);
        for (int i = start; i < end; i++) {
            String s = Integer.toString(i);
            String rev = new StringBuilder(s.substring(0, len / 2)).reverse().toString();
            int pal = Integer.parseInt(s + rev);
            res.add(pal);
        }
        return res;
    }

    public static void main(String[] args) {
        PrimePalindrome solution = new PrimePalindrome();
        System.out.println(solution.primePalindrome(6)); // 7
        System.out.println(solution.primePalindromesInRange(1, 100)); // [2,3,5,7,11,101]
        System.out.println(solution.countPrimePalindromes(1, 100)); // 6
        System.out.println(solution.primePalindromeOptimized(100)); // 101
    }
}
