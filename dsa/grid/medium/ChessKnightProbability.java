package grid.medium;

/**
 * LeetCode 688: Knight Probability in Chessboard
 * https://leetcode.com/problems/knight-probability-in-chessboard/
 *
 * Description:
 * On an n x n chessboard, a knight starts at the cell (row, column) and
 * attempts to make exactly k moves.
 * The rows and columns are 0-indexed, so the top-left cell is (0, 0), and the
 * bottom-right cell is (n - 1, n - 1).
 * A chess knight has eight possible moves it can make. Each move is two cells
 * in a cardinal direction, then one cell in an orthogonal direction.
 * Each time the knight is to move, it chooses one of eight possible moves
 * uniformly at random (even if the piece would go off the chessboard)
 * and moves there.
 * The knight continues moving until it has made exactly k moves or has moved
 * off the chessboard.
 * Return the probability that the knight remains on the board after it has
 * stopped moving.
 *
 * Constraints:
 * - 1 <= n <= 25
 * - 0 <= k <= 100
 * - 0 <= row, column <= n - 1
 */
public class ChessKnightProbability {

    private int[][] directions = { { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, { 1, -2 }, { 1, 2 }, { 2, -1 },
            { 2, 1 } };

    public double knightProbability(int n, int k, int row, int column) {
        double[][][] dp = new double[k + 1][n][n];
        return dfs(n, k, row, column, dp);
    }

    private double dfs(int n, int k, int r, int c, double[][][] dp) {
        if (r < 0 || r >= n || c < 0 || c >= n) {
            return 0;
        }

        if (k == 0) {
            return 1;
        }

        if (dp[k][r][c] != 0) {
            return dp[k][r][c];
        }

        double probability = 0;
        for (int[] dir : directions) {
            probability += dfs(n, k - 1, r + dir[0], c + dir[1], dp);
        }

        dp[k][r][c] = probability / 8.0;
        return dp[k][r][c];
    }

    public static void main(String[] args) {
        ChessKnightProbability solution = new ChessKnightProbability();

        System.out.println(solution.knightProbability(3, 2, 0, 0)); // 0.0625
        System.out.println(solution.knightProbability(1, 0, 0, 0)); // 1.0
    }
}
