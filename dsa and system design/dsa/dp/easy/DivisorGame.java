package dp.easy;

/**
 * LeetCode 1025: Divisor Game
 * https://leetcode.com/problems/divisor-game/
 *
 * Description:
 * Alice and Bob take turns playing a game, with Alice starting first.
 * Initially, there is a number n on the chalkboard. On each player's turn, that
 * player makes a move consisting of:
 * - Choosing any x with 0 < x < n and n % x == 0.
 * - Replacing the number n on the chalkboard with n - x.
 * Also, if a player cannot make a move, they lose the game.
 * Return true if and only if Alice wins the game, assuming both players play
 * optimally.
 *
 * Constraints:
 * - 1 <= n <= 1000
 *
 * Company Tags: Google, Amazon
 * Difficulty: Easy
 */
public class DivisorGame {

    // Approach 1: Mathematical Solution - O(1) time, O(1) space
    public boolean divisorGame(int n) {
        return n % 2 == 0;
    }

    // Approach 2: DP - O(n^2) time, O(n) space
    public boolean divisorGameDP(int n) {
        boolean[] dp = new boolean[n + 1];

        for (int i = 2; i <= n; i++) {
            for (int x = 1; x < i; x++) {
                if (i % x == 0 && !dp[i - x]) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }

    // Approach 3: Recursive with Memoization - O(n^2) time, O(n) space
    public boolean divisorGameMemo(int n) {
        Boolean[] memo = new Boolean[n + 1];
        return helper(n, memo);
    }

    private boolean helper(int n, Boolean[] memo) {
        if (n == 1)
            return false;

        if (memo[n] != null)
            return memo[n];

        for (int x = 1; x < n; x++) {
            if (n % x == 0 && !helper(n - x, memo)) {
                memo[n] = true;
                return true;
            }
        }

        memo[n] = false;
        return false;
    }

    public static void main(String[] args) {
        DivisorGame solution = new DivisorGame();

        System.out.println("=== Divisor Game Test Cases ===");

        for (int n = 1; n <= 10; n++) {
            System.out.println("n = " + n + ": Math = " + solution.divisorGame(n) +
                    ", DP = " + solution.divisorGameDP(n));
        }
    }
}
