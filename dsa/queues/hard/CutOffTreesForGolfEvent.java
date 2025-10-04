package queues.hard;

import java.util.List;

/**
 * LeetCode 675: Cut Off Trees for Golf Event
 * https://leetcode.com/problems/cut-off-trees-for-golf-event/
 *
 * Description:
 * You are asked to cut off all the trees in a forest for a golf event.
 *
 * Constraints:
 * - 1 <= forest.length <= 50
 * - 1 <= forest[i].length <= 50
 * - 0 <= forest[i][j] <= 10^9
 *
 * Follow-up:
 * - Can you optimize the path finding between trees?
 * - Can you use A* algorithm for better performance?
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 675: Cut Off Trees for Golf Event
 * https://leetcode.com/problems/cut-off-trees-for-golf-event/
 *
 * Description:
 * You are asked to cut off all the trees in a forest for a golf event.
 *
 * Constraints:
 * - 1 <= forest.length <= 50
 * - 1 <= forest[i].length <= 50
 * - 0 <= forest[i][j] <= 10^9
 *
 * Follow-up:
 * - Can you optimize the path finding between trees?
 * - Can you use A* algorithm for better performance?
 */
public class CutOffTreesForGolfEvent {
    public int cutOffTree(List<List<Integer>> forest) {
        if (forest == null || forest.isEmpty())
            return -1;

        int m = forest.size(), n = forest.get(0).size();
        List<int[]> trees = new ArrayList<>();

        // Collect all trees and sort by height
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int val = forest.get(i).get(j);
                if (val > 1) {
                    trees.add(new int[] { val, i, j });
                }
            }
        }

        Collections.sort(trees, (a, b) -> a[0] - b[0]);

        int startR = 0, startC = 0, steps = 0;

        // Visit trees in order of height
        for (int[] tree : trees) {
            int targetR = tree[1], targetC = tree[2];
            int dist = bfs(forest, startR, startC, targetR, targetC);
            if (dist == -1)
                return -1;
            steps += dist;
            startR = targetR;
            startC = targetC;
        }

        return steps;
    }

    private int bfs(List<List<Integer>> forest, int startR, int startC, int targetR, int targetC) {
        if (startR == targetR && startC == targetC)
            return 0;

        int m = forest.size(), n = forest.get(0).size();
        boolean[][] visited = new boolean[m][n];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] { startR, startC, 0 });
        visited[startR][startC] = true;

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1], dist = curr[2];

            for (int[] dir : directions) {
                int nr = r + dir[0], nc = c + dir[1];

                if (nr >= 0 && nr < m && nc >= 0 && nc < n &&
                        !visited[nr][nc] && forest.get(nr).get(nc) != 0) {
                    if (nr == targetR && nc == targetC) {
                        return dist + 1;
                    }
                    visited[nr][nc] = true;
                    queue.offer(new int[] { nr, nc, dist + 1 });
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        CutOffTreesForGolfEvent solution = new CutOffTreesForGolfEvent();
        List<List<Integer>> forest1 = java.util.Arrays.asList(
                java.util.Arrays.asList(1, 2, 3),
                java.util.Arrays.asList(0, 0, 4),
                java.util.Arrays.asList(7, 6, 5));
        System.out.println(solution.cutOffTree(forest1)); // 6

        List<List<Integer>> forest2 = java.util.Arrays.asList(
                java.util.Arrays.asList(1, 2, 3),
                java.util.Arrays.asList(0, 0, 0),
                java.util.Arrays.asList(7, 6, 5));
        System.out.println(solution.cutOffTree(forest2)); // -1

        // Edge Case: Single tree
        List<List<Integer>> forest3 = java.util.Arrays.asList(
                java.util.Arrays.asList(2));
        System.out.println(solution.cutOffTree(forest3)); // 0
    }
}
