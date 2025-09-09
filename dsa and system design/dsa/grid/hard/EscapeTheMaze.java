package grid.hard;

import java.util.*;

/**
 * LeetCode 1036: Escape a Large Maze
 * https://leetcode.com/problems/escape-a-large-maze/
 *
 * Description:
 * There is a 1 million by 1 million grid on an XY-plane, and the coordinates of
 * each grid square are (x, y).
 * We start at the source = [sx, sy] square and want to reach the target = [tx,
 * ty] square.
 * There is also an array of blocked squares, where each blocked[i] = [xi, yi]
 * represents a blocked square with coordinates (xi, yi).
 * Each move, we can walk one square north, east, south, or west if the square
 * is not blocked and is within the grid.
 * We are also not allowed to walk outside of the grid.
 * Return true if and only if it is possible to reach the target square from the
 * source square through a sequence of valid moves.
 *
 * Constraints:
 * - 0 <= blocked.length <= 200
 * - blocked[i].length == 2
 * - 0 <= xi, yi < 10^6
 * - source.length == target.length == 2
 * - 0 <= sx, sy, tx, ty < 10^6
 * - source != target
 * - It is guaranteed that source and target are not blocked
 */
public class EscapeTheMaze {

    private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    private static final int MAX_AREA = 20000; // Maximum area that can be blocked

    public boolean isEscapePossible(int[][] blocked, int[] source, int[] target) {
        if (blocked.length < 2)
            return true;

        Set<String> blockedSet = new HashSet<>();
        for (int[] block : blocked) {
            blockedSet.add(block[0] + "," + block[1]);
        }

        return bfs(source, target, blockedSet) && bfs(target, source, blockedSet);
    }

    private boolean bfs(int[] start, int[] end, Set<String> blocked) {
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start[0] + "," + start[1]);

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();

            if (curr[0] == end[0] && curr[1] == end[1]) {
                return true;
            }

            // If we've visited enough cells, we're not trapped
            if (visited.size() >= MAX_AREA) {
                return true;
            }

            for (int[] dir : DIRECTIONS) {
                int nx = curr[0] + dir[0];
                int ny = curr[1] + dir[1];

                if (nx >= 0 && nx < 1000000 && ny >= 0 && ny < 1000000) {
                    String key = nx + "," + ny;
                    if (!blocked.contains(key) && !visited.contains(key)) {
                        visited.add(key);
                        queue.offer(new int[] { nx, ny });
                    }
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        EscapeTheMaze solution = new EscapeTheMaze();

        int[][] blocked = { { 0, 1 }, { 1, 0 } };
        int[] source = { 0, 0 };
        int[] target = { 0, 2 };

        System.out.println(solution.isEscapePossible(blocked, source, target)); // false
    }
}
