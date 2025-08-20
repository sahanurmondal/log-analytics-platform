package graphs.hard;

/**
 * LeetCode 1129: Shortest Path with Alternating Colors
 * https://leetcode.com/problems/shortest-path-with-alternating-colors/
 *
 * Description:
 * Given a graph with red and blue edges, find the shortest path from node 0 to
 * every other node such that the path alternates colors.
 *
 * Constraints:
 * - 2 <= n <= 100
 * - 0 <= red_edges.length, blue_edges.length <= 400
 * - red_edges[i].length == blue_edges[i].length == 2
 *
 * Follow-up:
 * - Can you solve it with BFS?
 */
public class ShortestPathWithAlternatingColors {
    // Approach 1: BFS with color state - O(n + red_edges + blue_edges)
    // Color: 0 = red, 1 = blue
    public int[] shortestAlternatingPaths(int n, int[][] red_edges, int[][] blue_edges) {
        java.util.List<Integer>[] redAdj = new java.util.ArrayList[n];
        java.util.List<Integer>[] blueAdj = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++) {
            redAdj[i] = new java.util.ArrayList<>();
            blueAdj[i] = new java.util.ArrayList<>();
        }
        for (int[] e : red_edges)
            redAdj[e[0]].add(e[1]);
        for (int[] e : blue_edges)
            blueAdj[e[0]].add(e[1]);
        int[][] dist = new int[n][2];
        for (int i = 0; i < n; i++)
            dist[i][0] = dist[i][1] = Integer.MAX_VALUE;
        dist[0][0] = dist[0][1] = 0;
        java.util.Queue<int[]> q = new java.util.LinkedList<>();
        q.offer(new int[] { 0, 0 }); // node, last color red
        q.offer(new int[] { 0, 1 }); // node, last color blue
        boolean[][] visited = new boolean[n][2];
        visited[0][0] = visited[0][1] = true;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int node = cur[0], color = cur[1];
            java.util.List<Integer> nextAdj = color == 0 ? blueAdj[node] : redAdj[node];
            int nextColor = 1 - color;
            for (int nei : nextAdj) {
                if (!visited[nei][nextColor]) {
                    dist[nei][nextColor] = Math.min(dist[nei][nextColor], dist[node][color] + 1);
                    visited[nei][nextColor] = true;
                    q.offer(new int[] { nei, nextColor });
                }
            }
        }
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int d = Math.min(dist[i][0], dist[i][1]);
            res[i] = d == Integer.MAX_VALUE ? -1 : d;
        }
        return res;
    }

    // Approach 2: BFS with edge coloring (single adjacency list, color info per
    // edge)
    // This is an alternative, sometimes more memory efficient for large graphs.
    public int[] shortestAlternatingPathsSingleAdj(int n, int[][] red_edges, int[][] blue_edges) {
        java.util.List<int[]>[] adj = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new java.util.ArrayList<>();
        for (int[] e : red_edges)
            adj[e[0]].add(new int[] { e[1], 0 }); // 0 = red
        for (int[] e : blue_edges)
            adj[e[0]].add(new int[] { e[1], 1 }); // 1 = blue
        int[][] dist = new int[n][2];
        for (int i = 0; i < n; i++)
            dist[i][0] = dist[i][1] = Integer.MAX_VALUE;
        dist[0][0] = dist[0][1] = 0;
        java.util.Queue<int[]> q = new java.util.LinkedList<>();
        q.offer(new int[] { 0, 0 }); // node, last color red
        q.offer(new int[] { 0, 1 }); // node, last color blue
        boolean[][] visited = new boolean[n][2];
        visited[0][0] = visited[0][1] = true;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int node = cur[0], color = cur[1];
            for (int[] nei : adj[node]) {
                int next = nei[0], edgeColor = nei[1];
                if (edgeColor != color && !visited[next][edgeColor]) {
                    dist[next][edgeColor] = Math.min(dist[next][edgeColor], dist[node][color] + 1);
                    visited[next][edgeColor] = true;
                    q.offer(new int[] { next, edgeColor });
                }
            }
        }
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int d = Math.min(dist[i][0], dist[i][1]);
            res[i] = d == Integer.MAX_VALUE ? -1 : d;
        }
        return res;
    }

    public static void main(String[] args) {
        ShortestPathWithAlternatingColors solution = new ShortestPathWithAlternatingColors();
        // Edge Case 1: Normal case
        System.out.println(java.util.Arrays
                .toString(solution.shortestAlternatingPaths(3, new int[][] { { 0, 1 }, { 1, 2 } }, new int[][] {}))); // [0,1,2]
        // Edge Case 2: No path
        System.out.println(
                java.util.Arrays.toString(solution.shortestAlternatingPaths(3, new int[][] {}, new int[][] {}))); // [0,-1,-1]
        // Edge Case 3: Single node
        System.out.println(
                java.util.Arrays.toString(solution.shortestAlternatingPaths(1, new int[][] {}, new int[][] {}))); // [0]
        // Alternating required
        System.out.println(java.util.Arrays
                .toString(solution.shortestAlternatingPaths(3, new int[][] { { 0, 1 } }, new int[][] { { 1, 2 } }))); // [0,1,2]
        // Multiple paths, must alternate
        System.out.println(java.util.Arrays.toString(
                solution.shortestAlternatingPaths(3, new int[][] { { 0, 1 }, { 1, 2 } }, new int[][] { { 0, 2 } }))); // [0,1,1]
        // Cycle with alternating colors
        System.out.println(java.util.Arrays
                .toString(solution.shortestAlternatingPaths(3, new int[][] { { 0, 1 } }, new int[][] { { 1, 0 } }))); // [0,1,-1]

        // Alternative approach tests
        System.out.println(java.util.Arrays
                .toString(solution.shortestAlternatingPathsSingleAdj(3, new int[][] { { 0, 1 }, { 1, 2 } },
                        new int[][] {}))); // [0,1,2]
        System.out.println(
                java.util.Arrays
                        .toString(solution.shortestAlternatingPathsSingleAdj(3, new int[][] {}, new int[][] {}))); // [0,-1,-1]
        System.out.println(
                java.util.Arrays
                        .toString(solution.shortestAlternatingPathsSingleAdj(1, new int[][] {}, new int[][] {}))); // [0]
        System.out.println(java.util.Arrays.toString(
                solution.shortestAlternatingPathsSingleAdj(3, new int[][] { { 0, 1 } }, new int[][] { { 1, 2 } }))); // [0,1,2]
        System.out.println(java.util.Arrays.toString(solution.shortestAlternatingPathsSingleAdj(3,
                new int[][] { { 0, 1 }, { 1, 2 } }, new int[][] { { 0, 2 } }))); // [0,1,1]
        System.out.println(java.util.Arrays.toString(
                solution.shortestAlternatingPathsSingleAdj(3, new int[][] { { 0, 1 } }, new int[][] { { 1, 0 } }))); // [0,1,-1]
    }
}
