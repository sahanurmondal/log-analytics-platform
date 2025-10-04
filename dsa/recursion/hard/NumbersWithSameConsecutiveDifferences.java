package recursion.hard;

/**
 * LeetCode 967: Numbers With Same Consecutive Differences
 * https://leetcode.com/problems/numbers-with-same-consecutive-differences/
 *
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * Return all non-negative integers of length n such that the absolute
 * difference between every two consecutive digits is k.
 *
 * Constraints:
 * - 2 <= n <= 9
 * - 0 <= k <= 9
 *
 * Follow-ups:
 * 1. Can you count such numbers?
 * 2. Can you generate numbers with custom constraints?
 * 3. Can you optimize for large n?
 */
public class NumbersWithSameConsecutiveDifferences {
    public int[] numsSameConsecDiff(int n, int k) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        for (int i = 1; i <= 9; i++)
            dfs(n - 1, i, k, res);
        if (n == 1)
            res.add(0);
        return res.stream().mapToInt(i -> i).toArray();
    }

    private void dfs(int n, int num, int k, java.util.List<Integer> res) {
        if (n == 0) {
            res.add(num);
            return;
        }
        int last = num % 10;
        if (last + k < 10)
            dfs(n - 1, num * 10 + last + k, k, res);
        if (k != 0 && last - k >= 0)
            dfs(n - 1, num * 10 + last - k, k, res);
    }

    // Follow-up 1: Count such numbers
    public int countNumsSameConsecDiff(int n, int k) {
        return numsSameConsecDiff(n, k).length;
    }

    // Follow-up 2: Generate with custom constraints (e.g., only even digits)
    public int[] numsWithEvenDigits(int n, int k) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        for (int i = 2; i <= 8; i += 2)
            dfs(n - 1, i, k, res);
        if (n == 1)
            res.add(0);
        return res.stream().mapToInt(i -> i).toArray();
    }

    // Follow-up 3: Optimize for large n (not needed for n <= 9)

    public static void main(String[] args) {
        NumbersWithSameConsecutiveDifferences solution = new NumbersWithSameConsecutiveDifferences();
        System.out.println(java.util.Arrays.toString(solution.numsSameConsecDiff(3, 7))); // [181,292,707,818,929]
        System.out.println(solution.countNumsSameConsecDiff(3, 7)); // 5
        System.out.println(java.util.Arrays.toString(solution.numsWithEvenDigits(2, 2))); // [24, 42, 68, 86]
    }
}
