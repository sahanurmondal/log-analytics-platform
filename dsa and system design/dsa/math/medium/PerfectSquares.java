package math.medium;

/**
 * LeetCode 279: Perfect Squares
 * https://leetcode.com/problems/perfect-squares/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given n, find the least number of perfect square numbers which sum to n.
 *
 * Constraints:
 * - 1 <= n <= 10^4
 *
 * Follow-ups:
 * 1. Can you return the actual squares used?
 * 2. Can you solve for very large n?
 * 3. Can you solve for cubes?
 */
public class PerfectSquares {
    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        java.util.Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= n; i++)
            for (int j = 1; j * j <= i; j++)
                dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
        return dp[n];
    }

    // Follow-up 1: Return actual squares used
    public java.util.List<Integer> squaresUsed(int n) {
        int[] dp = new int[n + 1];
        int[] prev = new int[n + 1];
        java.util.Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= n; i++)
            for (int j = 1; j * j <= i; j++)
                if (dp[i - j * j] + 1 < dp[i]) {
                    dp[i] = dp[i - j * j] + 1;
                    prev[i] = j * j;
                }
        java.util.List<Integer> res = new java.util.ArrayList<>();
        while (n > 0) {
            res.add(prev[n]);
            n -= prev[n];
        }
        return res;
    }

    // Follow-up 2: Very large n (use greedy + Lagrange's theorem)
    public int numSquaresLarge(int n) {
        while (n % 4 == 0)
            n /= 4;
        if (n % 8 == 7)
            return 4;
        for (int a = 0; a * a <= n; a++)
            for (int b = 0; a * a + b * b <= n; b++) {
                int c = (int) Math.sqrt(n - a * a - b * b);
                if (a * a + b * b + c * c == n)
                    return (a > 0 ? 1 : 0) + (b > 0 ? 1 : 0) + (c > 0 ? 1 : 0);
            }
        return 3;
    }

    // Follow-up 3: Solve for cubes
    public int numCubes(int n) {
        int[] dp = new int[n + 1];
        java.util.Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= n; i++)
            for (int j = 1; j * j * j <= i; j++)
                dp[i] = Math.min(dp[i], dp[i - j * j * j] + 1);
        return dp[n];
    }

    public static void main(String[] args) {
        PerfectSquares solution = new PerfectSquares();
        System.out.println(solution.numSquares(12)); // 3
        System.out.println(solution.squaresUsed(12)); // [4,4,4]
        System.out.println(solution.numSquaresLarge(9975)); // 4
        System.out.println(solution.numCubes(17)); // 3
    }
}
