package queues.hard;

/**
 * LeetCode 505: The Maze II
 * https://leetcode.com/problems/the-maze-ii/
 *
 * Description:
 * There is a ball in a maze with empty spaces (represented as 0) and walls
 * (represented as 1).
 *
 * Constraints:
 * - 1 <= maze.length, maze[i].length <= 100
 * - maze[i][j] is 0 or 1
 * - start.length == 2
 * - destination.length == 2
 * - 0 <= start[i], destination[i] < maze.length
 *
 * Follow-up:
 * - Can you solve it using Dijkstra's algorithm?
 * - Can you optimize for very large mazes?
 */
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * LeetCode 505: The Maze II
 * https://leetcode.com/problems/the-maze-ii/
 *
 * Description:
 * There is a ball in a maze with empty spaces (represented as 0) and walls
 * (represented as 1).
 *
 * Constraints:
 * - 1 <= maze.length, maze[i].length <= 100
 * - maze[i][j] is 0 or 1
 * - start.length == 2
 * - destination.length == 2
 * - 0 <= start[i], destination[i] < maze.length
 *
 * Follow-up:
 * - Can you solve it using Dijkstra's algorithm?
 * - Can you optimize for very large mazes?
 */
public class TheMazeII {
    public int shortestDistance(int[][] maze, int[] start, int[] destination) {
        int m = maze.length, n = maze[0].length;
        int[][] dist = new int[m][n];
        for (int[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[2] - b[2]);
        pq.offer(new int[] { start[0], start[1], 0 });
        dist[start[0]][start[1]] = 0;

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int row = curr[0], col = curr[1], d = curr[2];

            if (row == destination[0] && col == destination[1]) {
                return d;
            }

            if (d > dist[row][col])
                continue;

            for (int[] dir : directions) {
                int newRow = row, newCol = col, steps = 0;

                // Keep rolling until hitting a wall
                while (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                        maze[newRow][newCol] == 0) {
                    newRow += dir[0];
                    newCol += dir[1];
                    steps++;
                }

                // Step back to the last valid position
                newRow -= dir[0];
                newCol -= dir[1];
                steps--;

                int newDist = d + steps;
                if (newDist < dist[newRow][newCol]) {
                    dist[newRow][newCol] = newDist;
                    pq.offer(new int[] { newRow, newCol, newDist });
                }
            }
        }

        return dist[destination[0]][destination[1]] == Integer.MAX_VALUE ? -1 : dist[destination[0]][destination[1]];
    }

    public static void main(String[] args) {
        TheMazeII solution = new TheMazeII();
        int[][] maze1 = { { 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0 }, { 1, 1, 0, 1, 1 },
                { 0, 0, 0, 0, 0 } };
        System.out.println(solution.shortestDistance(maze1, new int[] { 0, 4 }, new int[] { 4, 4 })); // 12

        int[][] maze2 = { { 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0 }, { 1, 1, 0, 1, 1 },
                { 0, 0, 0, 0, 0 } };
        System.out.println(solution.shortestDistance(maze2, new int[] { 0, 4 }, new int[] { 3, 2 })); // -1

        // Edge Case: Start equals destination
        System.out.println(solution.shortestDistance(maze1, new int[] { 0, 0 }, new int[] { 0, 0 })); // 0
    }
}
