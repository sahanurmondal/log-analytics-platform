package grid.hard;

import java.util.*;

/**
 * LeetCode 1263: Minimum Moves to Move a Box to Their Target Location
 * https://leetcode.com/problems/minimum-moves-to-move-a-box-to-their-target-location/
 *
 * Description:
 * A storekeeper is a game in which the player pushes boxes around in a
 * warehouse trying to get them to target locations.
 * The game is represented by an m x n grid of characters grid where each
 * element is a wall, floor, or box.
 * Your task is to move the box 'B' to the target position 'T' under the
 * following rules:
 * - The character 'S' represents the initial position of the player and 'B'
 * represents the initial position of the box.
 * - The player cannot walk through walls or boxes.
 * - The player can push the box by moving in the same direction as the box.
 * - The box cannot be pushed through walls.
 * Return the minimum number of pushes to move the box to the target, or -1 if
 * it is impossible.
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 20
 * - grid contains only characters '.', '#', 'S', 'B', 'T'
 * - The number of '.' and 'T' is at least 1
 */
public class ShortestPathInGrid {

    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public int minPushBox(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] start = new int[4]; // playerRow, playerCol, boxRow, boxCol
        int[] target = new int[2];

        // Find initial positions
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 'S') {
                    start[0] = i;
                    start[1] = j;
                    grid[i][j] = '.';
                } else if (grid[i][j] == 'B') {
                    start[2] = i;
                    start[3] = j;
                    grid[i][j] = '.';
                } else if (grid[i][j] == 'T') {
                    target[0] = i;
                    target[1] = j;
                    grid[i][j] = '.';
                }
            }
        }

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[4], b[4]));
        Set<String> visited = new HashSet<>();

        pq.offer(new int[] { start[0], start[1], start[2], start[3], 0 });
        visited.add(start[0] + "," + start[1] + "," + start[2] + "," + start[3]);

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int pr = curr[0], pc = curr[1], br = curr[2], bc = curr[3], pushes = curr[4];

            if (br == target[0] && bc == target[1]) {
                return pushes;
            }

            // Try all 4 directions to push the box
            for (int[] dir : directions) {
                int nbr = br + dir[0];
                int nbc = bc + dir[1];
                int npr = br - dir[0];
                int npc = bc - dir[1];

                if (isValid(grid, nbr, nbc) && isValid(grid, npr, npc) &&
                        canReach(grid, pr, pc, npr, npc, br, bc)) {

                    String state = npr + "," + npc + "," + nbr + "," + nbc;
                    if (!visited.contains(state)) {
                        visited.add(state);
                        pq.offer(new int[] { npr, npc, nbr, nbc, pushes + 1 });
                    }
                }
            }
        }

        return -1;
    }

    private boolean isValid(char[][] grid, int r, int c) {
        return r >= 0 && r < grid.length && c >= 0 && c < grid[0].length && grid[r][c] == '.';
    }

    private boolean canReach(char[][] grid, int pr, int pc, int tr, int tc, int br, int bc) {
        if (pr == tr && pc == tc)
            return true;

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[grid.length][grid[0].length];

        queue.offer(new int[] { pr, pc });
        visited[pr][pc] = true;
        visited[br][bc] = true; // block the box position

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int r = pos[0], c = pos[1];

            if (r == tr && c == tc)
                return true;

            for (int[] dir : directions) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (isValid(grid, nr, nc) && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    queue.offer(new int[] { nr, nc });
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        ShortestPathInGrid solution = new ShortestPathInGrid();

        char[][] grid = {
                { '#', '#', '#', '#', '#', '#' },
                { '#', 'T', '#', '#', '#', '#' },
                { '#', '.', '.', 'B', '.', '#' },
                { '#', '.', '#', '#', '.', '#' },
                { '#', '.', '.', '.', 'S', '#' },
                { '#', '#', '#', '#', '#', '#' }
        };

        System.out.println(solution.minPushBox(grid)); // 3
    }
}
