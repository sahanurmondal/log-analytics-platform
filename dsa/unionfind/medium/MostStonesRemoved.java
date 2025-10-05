package unionfind.medium;

import java.util.*;
import java.util.function.Function;

/**
 * LeetCode 947: Most Stones Removed with Same Row or Column
 * https://leetcode.com/problems/most-stones-removed-with-same-row-or-column/
 *
 * Description:
 * On a 2D plane, we place n stones at some integer coordinate points.
 * Each coordinate point may have at most one stone.
 * A stone can be removed if it shares a row or column with another stone that
 * has not been removed.
 * Given an array stones, return the largest possible number of stones that can
 * be removed.
 *
 * Constraints:
 * - 1 <= stones.length <= 1000
 * - 0 <= xi, yi <= 10^4
 *
 * Visual Example:
 * stones = [[0,0],[0,1],[1,0],[1,2],[2,1],[2,2]]
 * 
 * Grid visualization:
 * 0 1 2
 * -----
 * X X . | 0
 * X . X | 1
 * . X X | 2
 * 
 * Can remove 5 stones, leaving 1
 *
 * Follow-up:
 * - Can you solve it using DFS as well?
 * - How would you handle 3D coordinates?
 */
public class MostStonesRemoved {

    class UnionFind {
        private Map<Integer, Integer> parent;
        private int components;

        public UnionFind() {
            parent = new HashMap<>();
            components = 0;
        }

        public int find(int x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                components++;
            }

            if (parent.get(x) != x) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                parent.put(rootX, rootY);
                components--;
            }
        }

        public int getComponents() {
            return components;
        }
    }

    public int removeStones(int[][] stones) {
        UnionFind uf = new UnionFind();

        // Union stones that share row or column
        // Use different encoding for rows and columns to avoid conflicts
        for (int[] stone : stones) {
            int row = stone[0];
            int col = stone[1] + 10001; // Offset to distinguish from rows
            uf.union(row, col);
        }

        return stones.length - uf.getComponents();
    }

    // DFS version for LeetCode 947
    public int removeStonesDFS(int[][] stones) {
        int n = stones.length;
        // Build adjacency list: stones are nodes, edges if share row or column
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (stones[i][0] == stones[j][0] || stones[i][1] == stones[j][1]) {
                    graph.get(i).add(j);
                    graph.get(j).add(i);
                }
            }
        }
        boolean[] visited = new boolean[n];
        int components = 0;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, graph, visited);
                components++;
            }
        }
        return n - components;
    }

    private void dfs(int node, List<List<Integer>> graph, boolean[] visited) {
        visited[node] = true;
        for (int nei : graph.get(node)) {
            if (!visited[nei]) dfs(nei, graph, visited);
        }
    }

    // 2D DFS version for LeetCode 947
    public int removeStones2DDFS(int[][] stones) {
        Set<String> stoneSet = new HashSet<>();
        for (int[] stone : stones) {
            stoneSet.add(stone[0] + "," + stone[1]);
        }
        int components = 0;
        for (int[] stone : stones) {
            String key = stone[0] + "," + stone[1];
            if (stoneSet.contains(key)) {
                dfs2D(stone[0], stone[1], stoneSet, stones);
                components++;
            }
        }
        return stones.length - components;
    }

    private void dfs2D(int x, int y, Set<String> stoneSet, int[][] stones) {
        String key = x + "," + y;
        stoneSet.remove(key);
        // Visit all stones in the same row
        for (int[] stone : stones) {
            if (stone[0] == x && stoneSet.contains(stone[0] + "," + stone[1])) {
                dfs2D(stone[0], stone[1], stoneSet, stones);
            }
        }
        // Visit all stones in the same column
        for (int[] stone : stones) {
            if (stone[1] == y && stoneSet.contains(stone[0] + "," + stone[1])) {
                dfs2D(stone[0], stone[1], stoneSet, stones);
            }
        }
    }

    // 2D Union-Find version for LeetCode 947
    public int removeStones2DUF(int[][] stones) {
        int n = stones.length;
        // Use string key for sparse grid
        Map<String, String> parent = new HashMap<>();
        // Row and column maps for fast union
        Map<Integer, String> rowMap = new HashMap<>();
        Map<Integer, String> colMap = new HashMap<>();

        // Helper: find root
        Function<String, String> find = new Function<>() {
            @Override
            public String apply(String key) {
                if (!parent.get(key).equals(key)) {
                    parent.put(key, this.apply(parent.get(key)));
                }
                return parent.get(key);
            }
        };

        // Union stones in same row/col
        for (int[] stone : stones) {
            String key = stone[0] + "," + stone[1];
            parent.putIfAbsent(key, key);
            // Union with previous stone in same row
            if (rowMap.containsKey(stone[0])) {
                String prev = rowMap.get(stone[0]);
                String root1 = find.apply(key);
                String root2 = find.apply(prev);
                if (!root1.equals(root2)) parent.put(root1, root2);
            }
            rowMap.put(stone[0], key);
            // Union with previous stone in same col
            if (colMap.containsKey(stone[1])) {
                String prev = colMap.get(stone[1]);
                String root1 = find.apply(key);
                String root2 = find.apply(prev);
                if (!root1.equals(root2)) parent.put(root1, root2);
            }
            colMap.put(stone[1], key);
        }

        // Count unique roots
        Set<String> roots = new HashSet<>();
        for (String key : parent.keySet()) {
            roots.add(find.apply(key));
        }
        return n - roots.size();
    }

    public static void main(String[] args) {
        MostStonesRemoved solution = new MostStonesRemoved();

        // Test case 1: Multiple connected components
        int[][] stones1 = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 2 }, { 2, 1 }, { 2, 2 } };
        System.out.println(solution.removeStones(stones1)); // UF: 5
        System.out.println(solution.removeStonesDFS(stones1)); // DFS: 5
        System.out.println(solution.removeStones2DDFS(stones1)); // 2D DFS: 5
        System.out.println(solution.removeStones2DUF(stones1)); // 2D UF: 5

        // Test case 2: All stones in same row
        int[][] stones2 = { { 0, 0 }, { 0, 2 }, { 1, 1 }, { 2, 0 }, { 2, 2 } };
        System.out.println(solution.removeStones(stones2)); // UF: 3
        System.out.println(solution.removeStonesDFS(stones2)); // DFS: 3
        System.out.println(solution.removeStones2DDFS(stones2)); // 2D DFS: 3
        System.out.println(solution.removeStones2DUF(stones2)); // 2D UF: 3

        // Test case 3: Single stone
        int[][] stones3 = { { 0, 0 } };
        System.out.println(solution.removeStones(stones3)); // UF: 0
        System.out.println(solution.removeStonesDFS(stones3)); // DFS: 0
        System.out.println(solution.removeStones2DDFS(stones3)); // 2D DFS: 0
        System.out.println(solution.removeStones2DUF(stones3)); // 2D UF: 0

        // Test case 4: No stones share row/column
        int[][] stones4 = { { 0, 0 }, { 1, 1 }, { 2, 2 } };
        System.out.println(solution.removeStones(stones4)); // UF: 0
        System.out.println(solution.removeStonesDFS(stones4)); // DFS: 0
        System.out.println(solution.removeStones2DDFS(stones4)); // 2D DFS: 0
        System.out.println(solution.removeStones2DUF(stones4)); // 2D UF: 0

        // Test case 5: All stones in same column
        int[][] stones5 = { { 0, 0 }, { 1, 0 }, { 2, 0 } };
        System.out.println(solution.removeStones(stones5)); // UF: 2
        System.out.println(solution.removeStonesDFS(stones5)); // DFS: 2
        System.out.println(solution.removeStones2DDFS(stones5)); // 2D DFS: 2
        System.out.println(solution.removeStones2DUF(stones5)); // 2D UF: 2
    }
}
