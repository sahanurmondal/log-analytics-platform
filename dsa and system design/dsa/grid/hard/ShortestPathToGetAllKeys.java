package grid.hard;

import java.util.*;

/**
 * LeetCode 864: Shortest Path to Get All Keys
 * https://leetcode.com/problems/shortest-path-to-get-all-keys/
 *
 * Description:
 * You are given an m x n grid grid where:
 * - '.' is an empty cell.
 * - '#' is a wall.
 * - '@' is the starting point.
 * - Lowercase letters represent keys.
 * - Uppercase letters represent locks.
 * You start at the starting point and one move consists of walking one space in
 * one of the four cardinal directions.
 * You cannot walk outside the grid, or walk into a wall.
 * If you walk over a key, you can pick it up and you cannot walk over a lock
 * unless you have its corresponding key.
 * For some 1 <= k <= 6, there are exactly k keys and k locks in the grid.
 * Return the lowest number of steps to acquire all keys. If it is impossible,
 * return -1.
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 30
 * - grid[i][j] is either '.', '#', '@', a lowercase letter, or an uppercase
 * letter
 * - The number of keys in the grid is in the range [1, 6]
 */
public class ShortestPathToGetAllKeys {

    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public int shortestPathAllKeys(String[] grid) {
        int m = grid.length, n = grid[0].length();
        int startX = 0, startY = 0;
        int keyCount = 0;

        // Find starting position and count keys
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                char c = grid[i].charAt(j);
                if (c == '@') {
                    startX = i;
                    startY = j;
                } else if (c >= 'a' && c <= 'f') {
                    keyCount++;
                }
            }
        }

        int targetState = (1 << keyCount) - 1; // All keys collected

        // BFS with state: (row, col, keys_bitmask)
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(new int[] { startX, startY, 0, 0 }); // x, y, keys, steps
        visited.add(startX + "," + startY + "," + 0);

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0], y = curr[1], keys = curr[2], steps = curr[3];

            if (keys == targetState) {
                return steps;
            }

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < m && ny >= 0 && ny < n) {
                    char c = grid[nx].charAt(ny);

                    if (c == '#')
                        continue; // Wall

                    int newKeys = keys;

                    // Check if it's a key
                    if (c >= 'a' && c <= 'f') {
                        newKeys |= (1 << (c - 'a'));
                    }

                    // Check if it's a lock
                    if (c >= 'A' && c <= 'F') {
                        if ((keys & (1 << (c - 'A'))) == 0) {
                            continue; // Don't have the key
                        }
                    }

                    String state = nx + "," + ny + "," + newKeys;
                    if (!visited.contains(state)) {
                        visited.add(state);
                        queue.offer(new int[] { nx, ny, newKeys, steps + 1 });
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        ShortestPathToGetAllKeys solution = new ShortestPathToGetAllKeys();

        String[] grid1 = { "@.a.#", "###.#", "b.A.B" };
        System.out.println(solution.shortestPathAllKeys(grid1)); // 8

        String[] grid2 = { "@..aA", "..B#.", "....b" };
        System.out.println(solution.shortestPathAllKeys(grid2)); // 6
    }
}
