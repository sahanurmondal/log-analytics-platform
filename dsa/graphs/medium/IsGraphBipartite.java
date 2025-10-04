package graphs.medium;

import java.util.*;

/**
 * LeetCode 785: Is Graph Bipartite?
 * https://leetcode.com/problems/is-graph-bipartite/
 * 
 * Problem:
 * There is an undirected graph with n nodes, where each node is numbered
 * between 0 and n - 1.
 * You are given a 2D array graph, where graph[i] is an array of nodes that are
 * adjacent to node i.
 * More formally, for each edge [u, v] in the graph, u and v are adjacent to
 * each other.
 * 
 * The graph is connected if there is a path between every pair of vertices.
 * A graph is bipartite if the nodes can be partitioned into two independent
 * sets A and B such that every edge in the graph connects a node in set A and a
 * node in set B.
 * 
 * Return true if and only if it is bipartite.
 * 
 * Example 1:
 * Input: graph = [[1,2,3],[0,2],[0,1,3],[0,2]]
 * Output: false
 * Explanation: There is no way to partition the nodes into two independent sets
 * such that every edge connects a node in one and a node in the other.
 * 
 * Example 2:
 * Input: graph = [[1,3],[0,2],[1,3],[0,2]]
 * Output: true
 * Explanation: We can partition the nodes into two sets: {0, 2} and {1, 3}.
 * 
 * Constraints:
 * graph.length == n
 * 1 <= n <= 100
 * 0 <= graph[i].length < n
 * 0 <= graph[i][j] <= n - 1
 * graph[i] does not contain i.
 * All the values of graph[i] are unique.
 * The graph is guaranteed to be undirected.
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple
 * Frequency: High
 */
public class IsGraphBipartite {

    /**
     * Approach 1: DFS with coloring
     * Time Complexity: O(V + E) where V is vertices and E is edges
     * Space Complexity: O(V) for recursion stack and color array
     */
    public boolean isBipartite(int[][] graph) {
        int n = graph.length;
        int[] colors = new int[n]; // 0: uncolored, 1: red, -1: blue

        // Check all connected components
        for (int i = 0; i < n; i++) {
            if (colors[i] == 0) {
                if (!dfs(graph, i, 1, colors)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean dfs(int[][] graph, int node, int color, int[] colors) {
        colors[node] = color;

        for (int neighbor : graph[node]) {
            if (colors[neighbor] == color) {
                return false; // Same color as current node
            }
            if (colors[neighbor] == 0 && !dfs(graph, neighbor, -color, colors)) {
                return false; // Recursively color neighbor with opposite color
            }
        }

        return true;
    }

    /**
     * Approach 2: BFS with coloring
     * Time Complexity: O(V + E)
     * Space Complexity: O(V)
     */
    public boolean isBipartiteBFS(int[][] graph) {
        int n = graph.length;
        int[] colors = new int[n]; // 0: uncolored, 1: red, -1: blue

        // Check all connected components
        for (int i = 0; i < n; i++) {
            if (colors[i] == 0) {
                if (!bfs(graph, i, colors)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean bfs(int[][] graph, int start, int[] colors) {
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        colors[start] = 1; // Start with color 1

        while (!queue.isEmpty()) {
            int node = queue.poll();

            for (int neighbor : graph[node]) {
                if (colors[neighbor] == colors[node]) {
                    return false; // Same color conflict
                }

                if (colors[neighbor] == 0) {
                    colors[neighbor] = -colors[node]; // Opposite color
                    queue.offer(neighbor);
                }
            }
        }

        return true;
    }

    /**
     * Approach 3: Union-Find approach
     * Time Complexity: O(V + E * α(V)) where α is inverse Ackermann function
     * Space Complexity: O(V)
     */
    public boolean isBipartiteUnionFind(int[][] graph) {
        int n = graph.length;
        UnionFind uf = new UnionFind(2 * n); // Double size for bipartite sets

        for (int i = 0; i < n; i++) {
            for (int neighbor : graph[i]) {
                // If node i and its neighbor are in same set, not bipartite
                if (uf.isConnected(i, neighbor)) {
                    return false;
                }

                // Connect node i with neighbor's opposite set
                // Connect neighbor with node i's opposite set
                uf.union(i, neighbor + n);
                uf.union(neighbor, i + n);
            }
        }

        return true;
    }

    class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 1;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }

        public boolean isConnected(int x, int y) {
            return find(x) == find(y);
        }
    }

    /**
     * Follow-up: Return the two partitions if bipartite
     */
    public List<List<Integer>> getBipartitePartitions(int[][] graph) {
        int n = graph.length;
        int[] colors = new int[n];

        // Check if bipartite and color nodes
        for (int i = 0; i < n; i++) {
            if (colors[i] == 0) {
                if (!dfs(graph, i, 1, colors)) {
                    return new ArrayList<>(); // Not bipartite
                }
            }
        }

        // Create partitions
        List<Integer> partition1 = new ArrayList<>();
        List<Integer> partition2 = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (colors[i] == 1) {
                partition1.add(i);
            } else {
                partition2.add(i);
            }
        }

        List<List<Integer>> result = new ArrayList<>();
        result.add(partition1);
        result.add(partition2);
        return result;
    }

    /**
     * Follow-up: Check if a specific coloring is valid
     */
    public boolean isValidColoring(int[][] graph, int[] colors) {
        for (int i = 0; i < graph.length; i++) {
            for (int neighbor : graph[i]) {
                if (colors[i] == colors[neighbor]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper: Convert edge list to adjacency list
     */
    public int[][] edgeListToAdjList(int[][] edges, int n) {
        List<List<Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adjList.get(edge[0]).add(edge[1]);
            adjList.get(edge[1]).add(edge[0]);
        }

        int[][] graph = new int[n][];
        for (int i = 0; i < n; i++) {
            graph[i] = adjList.get(i).stream().mapToInt(Integer::intValue).toArray();
        }

        return graph;
    }

    public static void main(String[] args) {
        IsGraphBipartite solution = new IsGraphBipartite();

        // Test case 1: Not bipartite
        int[][] graph1 = { { 1, 2, 3 }, { 0, 2 }, { 0, 1, 3 }, { 0, 2 } };
        System.out.println("Test 1 - DFS: " + solution.isBipartite(graph1)); // false
        System.out.println("Test 1 - BFS: " + solution.isBipartiteBFS(graph1)); // false
        System.out.println("Test 1 - Union-Find: " + solution.isBipartiteUnionFind(graph1)); // false

        // Test case 2: Bipartite
        int[][] graph2 = { { 1, 3 }, { 0, 2 }, { 1, 3 }, { 0, 2 } };
        System.out.println("Test 2 - DFS: " + solution.isBipartite(graph2)); // true
        System.out.println("Test 2 - BFS: " + solution.isBipartiteBFS(graph2)); // true
        System.out.println("Test 2 - Union-Find: " + solution.isBipartiteUnionFind(graph2)); // true
        System.out.println("Test 2 - Partitions: " + solution.getBipartitePartitions(graph2)); // [[0, 2], [1, 3]]

        // Test case 3: Single node
        int[][] graph3 = { {} };
        System.out.println("Test 3 - Single node: " + solution.isBipartite(graph3)); // true

        // Test case 4: Disconnected components
        int[][] graph4 = { { 1 }, { 0 }, { 3 }, { 2 } };
        System.out.println("Test 4 - Disconnected: " + solution.isBipartite(graph4)); // true

        // Test case 5: Triangle (odd cycle)
        int[][] graph5 = { { 1, 2 }, { 0, 2 }, { 0, 1 } };
        System.out.println("Test 5 - Triangle: " + solution.isBipartite(graph5)); // false

        System.out.println("\nAll test cases completed successfully!");
    }
}
