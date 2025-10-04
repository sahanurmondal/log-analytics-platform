package dp.linear.optimization;

/**
 * LeetCode 276: Paint Fence
 * https://leetcode.com/problems/paint-fence/
 *
 * Description:
 * You are painting a fence of n posts with k different colors. You must paint
 * the posts following these rules:
 * - Every post must be painted exactly one color.
 * - There cannot be 3 or more consecutive posts with the same color.
 * Given the two integers n and k, return the number of ways you can paint the
 * fence.
 *
 * Constraints:
 * - 1 <= n <= 50
 * - 1 <= k <= 10^5
 *
 * Company Tags: Google, Amazon
 * Difficulty: Easy
 */
public class PaintFence {

    // Approach 1: DP - O(n) time, O(1) space
    public int numWaysToPaintFence(int n, int k) {
        if (n == 1)
            return k;
        if (n == 2)
            return k * k;

        int same = k; // Ways to paint with same color as previous
        int diff = k * (k - 1); // Ways to paint with different color

        for (int i = 3; i <= n; i++) {
            int prevSame = same;
            same = diff;
            diff = (prevSame + diff) * (k - 1);
        }

        return same + diff;
    }

    // Approach 2: DP with Array - O(n) time, O(n) space
    public int numWaysToPaintFenceDP(int n, int k) {
        if (n == 1)
            return k;
        if (n == 2)
            return k * k;

        int[] dp = new int[n + 1];
        dp[1] = k;
        dp[2] = k * k;

        for (int i = 3; i <= n; i++) {
            dp[i] = (dp[i - 1] + dp[i - 2]) * (k - 1);
        }

        return dp[n];
    }

    public static void main(String[] args) {
        PaintFence solution = new PaintFence();

        System.out.println("=== Paint Fence Test Cases ===");

        System.out.println("n = 3, k = 2: " + solution.numWaysToPaintFence(3, 2));
        System.out.println("Expected: 6\n");

        System.out.println("n = 1, k = 1: " + solution.numWaysToPaintFence(1, 1));
        System.out.println("Expected: 1\n");
    }
}
