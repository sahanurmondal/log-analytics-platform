package grid.hard;

import java.util.*;

/**
 * LeetCode 1301: Number of Paths with Max Score
 * https://leetcode.com/problems/number-of-paths-with-max-score/
 *
 * Description:
 * You are given a square board of characters. You can move on the board
 * starting at the bottom right square
 * marked with the character 'S'.
 * You need to reach the top left square marked with the character 'E'. The rest
 * of the squares are labeled either
 * with a numeric character 1-9 or with an obstacle 'X'. In one move you can go
 * up, left or up-left (diagonally) only if there is no obstacle there.
 * Return a list of two integers: the first integer is the maximum sum of
 * numeric characters you can collect,
 * and the second is the number of such paths that you can take to get this
 * maximum sum, taken modulo 10^9 + 7.
 * In case there is no path, return [0, 0].
 *
 * Constraints:
 * - 2 <= board.length <= 100
 * - board[i].length == board.length
 * - board[i][j] is either 'S', 'E', 'X', or a digit from '1' to '9'
 */
public class NumberOfPathsWithMaxScore {

    private static final int MOD = 1000000007;

    public int[] pathsWithMaxScore(List<String> board) {
        int n = board.size();
        int[][] score = new int[n][n];
        long[][] ways = new long[n][n];

        // Initialize starting position
        ways[n - 1][n - 1] = 1;

        // Fill DP table from bottom-right to top-left
        for (int i = n - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (board.get(i).charAt(j) == 'X')
                    continue;
                if (i == n - 1 && j == n - 1)
                    continue;

                int maxScore = -1;
                long totalWays = 0;

                // Check three possible moves: right, down, diagonal
                int[][] dirs = { { 0, 1 }, { 1, 0 }, { 1, 1 } };

                for (int[] dir : dirs) {
                    int ni = i + dir[0];
                    int nj = j + dir[1];

                    if (ni < n && nj < n && board.get(ni).charAt(nj) != 'X' && ways[ni][nj] > 0) {
                        if (score[ni][nj] > maxScore) {
                            maxScore = score[ni][nj];
                            totalWays = ways[ni][nj];
                        } else if (score[ni][nj] == maxScore) {
                            totalWays = (totalWays + ways[ni][nj]) % MOD;
                        }
                    }
                }

                if (maxScore != -1) {
                    char c = board.get(i).charAt(j);
                    if (c != 'E') {
                        score[i][j] = maxScore + (c - '0');
                    } else {
                        score[i][j] = maxScore;
                    }
                    ways[i][j] = totalWays;
                }
            }
        }

        return new int[] { score[0][0], (int) ways[0][0] };
    }

    public static void main(String[] args) {
        NumberOfPathsWithMaxScore solution = new NumberOfPathsWithMaxScore();

        List<String> board1 = Arrays.asList("E23", "2X2", "12S");
        System.out.println(Arrays.toString(solution.pathsWithMaxScore(board1))); // [7, 1]

        List<String> board2 = Arrays.asList("E12", "1X1", "21S");
        System.out.println(Arrays.toString(solution.pathsWithMaxScore(board2))); // [4, 2]
    }
}
