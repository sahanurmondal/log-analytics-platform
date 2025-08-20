package grid.hard;

import java.util.*;

/**
 * LeetCode 675: Cut Off Trees for Golf Event
 * https://leetcode.com/problems/cut-off-trees-for-golf-event/
 *
 * Description:
 * You are asked to cut off all the trees in a forest for a golf event. The
 * forest is represented as an m x n matrix.
 * In this matrix:
 * - 0 means the cell cannot be walked through.
 * - 1 represents an empty cell that can be walked through.
 * - A number greater than 1 represents a tree in a cell that can be walked
 * through, and this number is the tree's height.
 * In one step, you can walk in any of the four directions: north, east, south,
 * and west.
 * If you are standing in a cell with a tree, you can decide whether to cut it
 * off.
 * You must cut off the trees in order from shortest to tallest. When you cut
 * off a tree, the value at its cell becomes 1 (an empty cell).
 * Starting from the point (0, 0), return the minimum steps to cut off all the
 * trees. If you cannot cut off all the trees, return -1.
 *
 * Constraints:
 * - m == forest.length
 * - n == forest[i].length
 * - 1 <= m, n <= 50
 * - 0 <= forest[i][j] <= 10^9
 */
public class CutOffTreesForGolf {

    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public int cutOffTree(List<List<Integer>> forest) {
        if (forest == null || forest.isEmpty())
            return 0;

        int m = forest.size(), n = forest.get(0).size();

        // Collect all trees and sort by height
        List<int[]> trees = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int val = forest.get(i).get(j);
                if (val > 1) {
                    trees.add(new int[] { i, j, val });
                }
            }
        }

        Collections.sort(trees, (a, b) -> a[2] - b[2]);

        int totalSteps = 0;
        int[] start = { 0, 0 };

        for (int[] tree : trees) {
            int steps = bfs(forest, start, new int[] { tree[0], tree[1] });
            if (steps == -1)
                return -1;

            totalSteps += steps;
            start[0] = tree[0];
            start[1] = tree[1];
        }

        return totalSteps;
    }

    private int bfs(List<List<Integer>> forest, int[] start, int[] target) {
        if (start[0] == target[0] && start[1] == target[1])
            return 0;

        int m = forest.size(), n = forest.get(0).size();
        boolean[][] visited = new boolean[m][n];
        Queue<int[]> queue = new LinkedList<>();

        queue.offer(new int[] { start[0], start[1], 0 });
        visited[start[0]][start[1]] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0], y = curr[1], steps = curr[2];

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < m && ny >= 0 && ny < n &&
                        !visited[nx][ny] && forest.get(nx).get(ny) != 0) {

                    if (nx == target[0] && ny == target[1]) {
                        return steps + 1;
                    }

                    visited[nx][ny] = true;
                    queue.offer(new int[] { nx, ny, steps + 1 });
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        CutOffTreesForGolf solution = new CutOffTreesForGolf();

        List<List<Integer>> forest1 = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(0, 0, 4),
                Arrays.asList(7, 6, 5));
        System.out.println(solution.cutOffTree(forest1)); // 6

        List<List<Integer>> forest2 = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(0, 0, 0),
                Arrays.asList(7, 6, 5));
        System.out.println(solution.cutOffTree(forest2)); // -1
    }
}
