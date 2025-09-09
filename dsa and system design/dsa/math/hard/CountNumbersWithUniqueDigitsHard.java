package math.hard;

/**
 * LeetCode 357: Count Numbers with Unique Digits (Hard Variant)
 * https://leetcode.com/problems/count-numbers-with-unique-digits/
 *
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description:
 * Given an integer n, return the count of all numbers with unique digits, x,
 * where 0 <= x < 10^n.
 *
 * Constraints:
 * - 0 <= n <= 10
 *
 * Follow-ups:
 * 1. Can you count for a given range [L, R]?
 * 2. Can you generate all such numbers?
 * 3. Can you count numbers with at most k repeated digits?
 */
public class CountNumbersWithUniqueDigitsHard {
    public int countNumbersWithUniqueDigits(int n) {
        if (n == 0)
            return 1;
        int res = 10, unique = 9, available = 9;
        for (int i = 2; i <= n && available > 0; i++) {
            unique *= available;
            res += unique;
            available--;
        }
        return res;
    }

    // Follow-up 1: Count in range [L, R]
    public int countUniqueInRange(int L, int R) {
        int count = 0;
        for (int i = L; i <= R; i++) {
            if (isUnique(i))
                count++;
        }
        return count;
    }

    private boolean isUnique(int num) {
        boolean[] seen = new boolean[10];
        while (num > 0) {
            int d = num % 10;
            if (seen[d])
                return false;
            seen[d] = true;
            num /= 10;
        }
        return true;
    }

    // Follow-up 2: Generate all such numbers up to n digits
    public java.util.List<Integer> generateUniqueNumbers(int n) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        dfs(n, 0, 0, res);
        return res;
    }

    private void dfs(int n, int num, int used, java.util.List<Integer> res) {
        if (n > 0)
            res.add(num);
        if (n == 0)
            return;
        for (int d = (num == 0 ? 1 : 0); d <= 9; d++) {
            if ((used & (1 << d)) == 0) {
                dfs(n - 1, num * 10 + d, used | (1 << d), res);
            }
        }
    }

    // Follow-up 3: Count numbers with at most k repeated digits
    public int countWithAtMostKRepeats(int n, int k) {
        return countWithRepeatsHelper(n, k, 0, 0, new int[10]);
    }

    private int countWithRepeatsHelper(int n, int k, int pos, int num, int[] freq) {
        if (pos == n) {
            int repeats = 0;
            for (int f : freq)
                if (f > 1)
                    repeats++;
            return repeats <= k ? 1 : 0;
        }
        int count = 0;
        for (int d = (pos == 0 ? 1 : 0); d <= 9; d++) {
            freq[d]++;
            count += countWithRepeatsHelper(n, k, pos + 1, num * 10 + d, freq);
            freq[d]--;
        }
        return count;
    }

    public static void main(String[] args) {
        CountNumbersWithUniqueDigitsHard solution = new CountNumbersWithUniqueDigitsHard();
        System.out.println(solution.countNumbersWithUniqueDigits(2)); // 91
        System.out.println(solution.countUniqueInRange(10, 99)); // 81
        System.out.println(solution.generateUniqueNumbers(2)); // [0, 1, ..., 98, 89]
        System.out.println(solution.countWithAtMostKRepeats(2, 0)); // 91
    }
}
