package queues.hard;

/**
 * LeetCode 1284: Minimum Number of Flips to Convert Binary Matrix to Zero
 * Matrix
 * https://leetcode.com/problems/minimum-number-of-flips-to-convert-binary-matrix-to-zero-matrix/
 *
 * Description:
 * Given a m x n binary matrix mat. In one step, you can choose one cell and
 * flip it and all the four neighbors.
 *
 * Constraints:
 * - m == mat.length
 * - n == mat[i].length
 * - 1 <= m, n <= 3
 * - mat[i][j] is 0 or 1
 *
 * Follow-up:
 * - Can you solve it using BFS with state compression?
 * - Can you extend to larger matrices?
 */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * LeetCode 1284: Minimum Number of Flips to Convert Binary Matrix to Zero
 * Matrix
 * https://leetcode.com/problems/minimum-number-of-flips-to-convert-binary-matrix-to-zero-matrix/
 *
 * Description:
 * Given a m x n binary matrix mat. In one step, you can choose one cell and
 * flip it and all the four neighbors.
 *
 * Constraints:
 * - m == mat.length
 * - n == mat[i].length
 * - 1 <= m, n <= 3
 * - mat[i][j] is 0 or 1
 *
 * Follow-up:
 * - Can you solve it using BFS with state compression?
 * - Can you extend to larger matrices?
 */
public class MinimumNumberOfFlipsToConvertBinaryMatrixToZeroMatrix {
    public int minFlips(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        int start = 0;

        // Convert matrix to bitmask
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                start |= (mat[i][j] << (i * n + j));
            }
        }

        if (start == 0)
            return 0;

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(start);
        visited.add(start);

        int[][] directions = { { 0, 0 }, { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            steps++;

            for (int k = 0; k < size; k++) {
                int curr = queue.poll();

                // Try flipping each cell
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        int next = curr;

                        // Flip current cell and neighbors
                        for (int[] dir : directions) {
                            int ni = i + dir[0], nj = j + dir[1];
                            if (ni >= 0 && ni < m && nj >= 0 && nj < n) {
                                next ^= (1 << (ni * n + nj));
                            }
                        }

                        if (next == 0)
                            return steps;

                        if (!visited.contains(next)) {
                            visited.add(next);
                            queue.offer(next);
                        }
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        MinimumNumberOfFlipsToConvertBinaryMatrixToZeroMatrix solution = new MinimumNumberOfFlipsToConvertBinaryMatrixToZeroMatrix();
        System.out.println(solution.minFlips(new int[][] { { 0, 0 }, { 0, 1 } })); // 3
        System.out.println(solution.minFlips(new int[][] { { 0 } })); // 0
        System.out.println(solution.minFlips(new int[][] { { 1, 0, 0 }, { 1, 0, 0 } })); // -1
        // Edge Case: All ones
        System.out.println(solution.minFlips(new int[][] { { 1, 1 }, { 1, 1 } })); // Should be solvable
        // Edge Case: Single cell
        System.out.println(solution.minFlips(new int[][] { { 1 } })); // 1
    }
}
