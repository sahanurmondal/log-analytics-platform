package unionfind.hard;

import java.util.*;

/**
 * LeetCode 1135: Connecting Cities With Minimum Cost
 * https://leetcode.com/problems/connecting-cities-with-minimum-cost/
 * 
 * Companies: Amazon, Google, Microsoft, Facebook, Apple, Uber, Bloomberg
 * Frequency: Very High (Asked in 2000+ interviews)
 *
 * Description:
 * There are n cities numbered from 1 to n.
 * You are given connections, where each connections[i] = [city1, city2, cost]
 * represents the cost to connect city1 and city2 together.
 * (A connection is bidirectional: connecting city1 and city2 is the same as
 * connecting city2 and city1.)
 * 
 * Return the minimum cost so that for every pair of cities, there exists a path
 * of connections (possibly of length 1)
 * directly or indirectly connecting the two cities. If the task is impossible,
 * return -1.
 * 
 * The cost of connecting the graph is the sum of the connection costs used.
 * 
 * Constraints:
 * - 1 <= n <= 10^4
 * - 1 <= connections.length <= 10^4
 * - connections[i].length == 3
 * - 1 <= city1, city2 <= n
 * - city1 != city2
 * - 0 <= cost <= 10^5
 * 
 * Follow-up Questions:
 * 1. What if you need to find the second minimum spanning tree?
 * 2. How would you handle dynamic edge insertions/deletions?
 * 3. Can you find MST with constraints (must include/exclude certain edges)?
 * 4. What about finding k-MST (minimum spanning forest with k components)?
 * 5. How to handle negative edge weights?
 * 6. What about maximum spanning tree instead of minimum?
 */
public class ConnectingCitiesWithMinimumCost {

    // Approach 1: Kruskal's Algorithm with Union-Find - O(E log E) time, O(V) space
    public static int minimumCost(int n, int[][] connections) {
        if (connections.length < n - 1) {
            return -1; // Not enough edges to connect all cities
        }

        // Sort edges by cost
        Arrays.sort(connections, (a, b) -> Integer.compare(a[2], b[2]));

        UnionFind uf = new UnionFind(n + 1); // 1-indexed
        int totalCost = 0;
        int edgesUsed = 0;

        for (int[] connection : connections) {
            int city1 = connection[0];
            int city2 = connection[1];
            int cost = connection[2];

            if (uf.union(city1, city2)) {
                totalCost += cost;
                edgesUsed++;

                if (edgesUsed == n - 1) {
                    break; // MST complete
                }
            }
        }

        return edgesUsed == n - 1 ? totalCost : -1;
    }

    // Approach 2: Prim's Algorithm - O(E log V) time, O(V + E) space
    public static int minimumCostPrim(int n, int[][] connections) {
        // Build adjacency list
        Map<Integer, List<int[]>> graph = new HashMap<>();

        for (int i = 1; i <= n; i++) {
            graph.put(i, new ArrayList<>());
        }

        for (int[] connection : connections) {
            int city1 = connection[0];
            int city2 = connection[1];
            int cost = connection[2];

            graph.get(city1).add(new int[] { city2, cost });
            graph.get(city2).add(new int[] { city1, cost });
        }

        // Prim's algorithm
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        Set<Integer> visited = new HashSet<>();

        // Start from city 1
        pq.offer(new int[] { 1, 0 });
        int totalCost = 0;

        while (!pq.isEmpty() && visited.size() < n) {
            int[] current = pq.poll();
            int city = current[0];
            int cost = current[1];

            if (visited.contains(city)) {
                continue;
            }

            visited.add(city);
            totalCost += cost;

            // Add all adjacent edges
            for (int[] neighbor : graph.get(city)) {
                int nextCity = neighbor[0];
                int nextCost = neighbor[1];

                if (!visited.contains(nextCity)) {
                    pq.offer(new int[] { nextCity, nextCost });
                }
            }
        }

        return visited.size() == n ? totalCost : -1;
    }

    // Approach 3: Optimized Kruskal with path compression and union by rank
    public static int minimumCostOptimized(int n, int[][] connections) {
        if (connections.length < n - 1) {
            return -1;
        }

        // Sort edges by cost
        Arrays.sort(connections, Comparator.comparingInt(a -> a[2]));

        OptimizedUnionFind uf = new OptimizedUnionFind(n + 1);
        int totalCost = 0;
        int components = n; // Initially n components

        for (int[] connection : connections) {
            int city1 = connection[0];
            int city2 = connection[1];
            int cost = connection[2];

            if (uf.union(city1, city2)) {
                totalCost += cost;
                components--;

                if (components == 1) {
                    break; // All cities connected
                }
            }
        }

        return components == 1 ? totalCost : -1;
    }

    // Approach 4: Modified Prim's with indexed priority queue
    public static int minimumCostIndexedPrim(int n, int[][] connections) {
        // Build adjacency list
        List<List<int[]>> graph = new ArrayList<>();

        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] connection : connections) {
            int city1 = connection[0];
            int city2 = connection[1];
            int cost = connection[2];

            graph.get(city1).add(new int[] { city2, cost });
            graph.get(city2).add(new int[] { city1, cost });
        }

        // Prim's with distance array
        int[] minCost = new int[n + 1];
        Arrays.fill(minCost, Integer.MAX_VALUE);
        boolean[] inMST = new boolean[n + 1];

        minCost[1] = 0;
        int totalCost = 0;

        for (int count = 0; count < n; count++) {
            // Find minimum cost vertex not in MST
            int u = -1;
            for (int v = 1; v <= n; v++) {
                if (!inMST[v] && (u == -1 || minCost[v] < minCost[u])) {
                    u = v;
                }
            }

            if (minCost[u] == Integer.MAX_VALUE) {
                return -1; // Graph not connected
            }

            inMST[u] = true;
            totalCost += minCost[u];

            // Update adjacent vertices
            for (int[] edge : graph.get(u)) {
                int v = edge[0];
                int cost = edge[1];

                if (!inMST[v] && cost < minCost[v]) {
                    minCost[v] = cost;
                }
            }
        }

        return totalCost;
    }

    // Basic Union-Find implementation
    public static class UnionFind {
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

        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) {
                return false; // Already connected
            }

            // Union by rank
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }

            return true;
        }

        public boolean connected(int x, int y) {
            return find(x) == find(y);
        }

        public int getComponents() {
            Set<Integer> roots = new HashSet<>();
            for (int i = 0; i < parent.length; i++) {
                roots.add(find(i));
            }
            return roots.size();
        }
    }

    // Optimized Union-Find with additional methods
    public static class OptimizedUnionFind {
        private int[] parent;
        private int[] rank;
        private int components;

        public OptimizedUnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            components = n - 1; // Excluding index 0

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

        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) {
                return false;
            }

            // Union by rank
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }

            components--;
            return true;
        }

        public int getComponents() {
            return components;
        }

        public boolean isConnected() {
            return components == 1;
        }
    }

    // Follow-up 1: Second minimum spanning tree
    public static class SecondMST {

        public static int secondMinimumSpanningTree(int n, int[][] connections) {
            // First, find the MST
            List<int[]> mstEdges = findMSTEdges(n, connections);

            if (mstEdges.size() != n - 1) {
                return -1; // No MST exists
            }

            int originalMSTCost = mstEdges.stream().mapToInt(edge -> edge[2]).sum();
            int secondMSTCost = Integer.MAX_VALUE;

            // Try removing each MST edge and find alternative MST
            for (int i = 0; i < mstEdges.size(); i++) {
                int[] removedEdge = mstEdges.get(i);

                // Create connections without this edge
                List<int[]> modifiedConnections = new ArrayList<>();

                for (int[] connection : connections) {
                    if (!(connection[0] == removedEdge[0] && connection[1] == removedEdge[1]
                            && connection[2] == removedEdge[2])) {
                        modifiedConnections.add(connection);
                    }
                }

                // Find MST cost without this edge
                int altCost = minimumCost(n, modifiedConnections.toArray(new int[0][]));

                if (altCost != -1 && altCost > originalMSTCost) {
                    secondMSTCost = Math.min(secondMSTCost, altCost);
                }
            }

            return secondMSTCost == Integer.MAX_VALUE ? -1 : secondMSTCost;
        }

        private static List<int[]> findMSTEdges(int n, int[][] connections) {
            Arrays.sort(connections, Comparator.comparingInt(a -> a[2]));

            UnionFind uf = new UnionFind(n + 1);
            List<int[]> mstEdges = new ArrayList<>();

            for (int[] connection : connections) {
                if (uf.union(connection[0], connection[1])) {
                    mstEdges.add(connection);

                    if (mstEdges.size() == n - 1) {
                        break;
                    }
                }
            }

            return mstEdges;
        }
    }

    // Follow-up 2: Dynamic MST with edge insertions/deletions
    public static class DynamicMST {

        private List<int[]> edges;
        private int n;
        private int currentMSTCost;

        public DynamicMST(int n) {
            this.n = n;
            this.edges = new ArrayList<>();
            this.currentMSTCost = -1;
        }

        public void addEdge(int city1, int city2, int cost) {
            edges.add(new int[] { city1, city2, cost });
            updateMST();
        }

        public void removeEdge(int city1, int city2, int cost) {
            edges.removeIf(edge -> edge[0] == city1 && edge[1] == city2 && edge[2] == cost);
            updateMST();
        }

        private void updateMST() {
            currentMSTCost = minimumCost(n, edges.toArray(new int[0][]));
        }

        public int getCurrentMSTCost() {
            return currentMSTCost;
        }

        public boolean isConnected() {
            return currentMSTCost != -1;
        }
    }

    // Follow-up 3: MST with constraints
    public static class ConstrainedMST {

        public static int minimumCostWithMandatoryEdges(int n, int[][] connections, int[][] mandatoryEdges) {
            UnionFind uf = new UnionFind(n + 1);
            int totalCost = 0;
            int edgesUsed = 0;

            // First, add all mandatory edges
            for (int[] edge : mandatoryEdges) {
                if (uf.union(edge[0], edge[1])) {
                    totalCost += edge[2];
                    edgesUsed++;
                }
            }

            // Sort remaining connections by cost
            Arrays.sort(connections, Comparator.comparingInt(a -> a[2]));

            // Add remaining edges using Kruskal's algorithm
            for (int[] connection : connections) {
                if (uf.union(connection[0], connection[1])) {
                    totalCost += connection[2];
                    edgesUsed++;

                    if (edgesUsed == n - 1) {
                        break;
                    }
                }
            }

            return edgesUsed == n - 1 ? totalCost : -1;
        }

        public static int minimumCostWithForbiddenEdges(int n, int[][] connections, int[][] forbiddenEdges) {
            Set<String> forbidden = new HashSet<>();

            for (int[] edge : forbiddenEdges) {
                forbidden.add(edge[0] + "-" + edge[1]);
                forbidden.add(edge[1] + "-" + edge[0]);
            }

            // Filter out forbidden edges
            List<int[]> allowedConnections = new ArrayList<>();

            for (int[] connection : connections) {
                String key = connection[0] + "-" + connection[1];
                if (!forbidden.contains(key)) {
                    allowedConnections.add(connection);
                }
            }

            return minimumCost(n, allowedConnections.toArray(new int[0][]));
        }

        public static int minimumCostWithBudgetLimit(int n, int[][] connections, int budget) {
            // Sort by cost
            Arrays.sort(connections, Comparator.comparingInt(a -> a[2]));

            UnionFind uf = new UnionFind(n + 1);
            int totalCost = 0;
            int edgesUsed = 0;

            for (int[] connection : connections) {
                if (totalCost + connection[2] > budget) {
                    continue; // Skip if exceeds budget
                }

                if (uf.union(connection[0], connection[1])) {
                    totalCost += connection[2];
                    edgesUsed++;

                    if (edgesUsed == n - 1) {
                        break;
                    }
                }
            }

            return edgesUsed == n - 1 ? totalCost : -1;
        }
    }

    // Follow-up 4: k-MST (Minimum Spanning Forest)
    public static class MinimumSpanningForest {

        public static Result findKMST(int n, int[][] connections, int k) {
            if (k <= 0 || k > n) {
                return new Result(-1, new ArrayList<>());
            }

            Arrays.sort(connections, Comparator.comparingInt(a -> a[2]));

            UnionFind uf = new UnionFind(n + 1);
            int totalCost = 0;
            int edgesUsed = 0;
            List<int[]> forestEdges = new ArrayList<>();

            for (int[] connection : connections) {
                if (uf.union(connection[0], connection[1])) {
                    totalCost += connection[2];
                    edgesUsed++;
                    forestEdges.add(connection);

                    // Stop when we have k components (n-k edges)
                    if (edgesUsed == n - k) {
                        break;
                    }
                }
            }

            // Check if we achieved k components
            int actualComponents = uf.getComponents() - 1; // Exclude index 0

            return new Result(actualComponents == k ? totalCost : -1, forestEdges);
        }

        public static class Result {
            public final int totalCost;
            public final List<int[]> edges;

            public Result(int totalCost, List<int[]> edges) {
                this.totalCost = totalCost;
                this.edges = edges;
            }
        }
    }

    // Follow-up 5: Handle negative weights (Bellman-Ford based)
    public static class NegativeWeightMST {

        public static int minimumCostWithNegativeWeights(int n, int[][] connections) {
            // For MST with negative weights, we can still use Kruskal's algorithm
            // The key insight is that negative weights don't create negative cycles in MST

            if (connections.length < n - 1) {
                return Integer.MIN_VALUE; // Impossible
            }

            // Sort edges by cost (negative weights will come first)
            Arrays.sort(connections, Comparator.comparingInt(a -> a[2]));

            UnionFind uf = new UnionFind(n + 1);
            long totalCost = 0; // Use long to handle potential overflow
            int edgesUsed = 0;

            for (int[] connection : connections) {
                if (uf.union(connection[0], connection[1])) {
                    totalCost += connection[2];
                    edgesUsed++;

                    if (edgesUsed == n - 1) {
                        break;
                    }
                }
            }

            return edgesUsed == n - 1 ? (int) totalCost : Integer.MIN_VALUE;
        }

        public static boolean hasNegativeCycle(int n, int[][] connections) {
            // Build adjacency list
            List<List<int[]>> graph = new ArrayList<>();

            for (int i = 0; i <= n; i++) {
                graph.add(new ArrayList<>());
            }

            for (int[] connection : connections) {
                graph.get(connection[0]).add(new int[] { connection[1], connection[2] });
                graph.get(connection[1]).add(new int[] { connection[0], connection[2] });
            }

            // Use Bellman-Ford to detect negative cycles
            int[] dist = new int[n + 1];
            Arrays.fill(dist, Integer.MAX_VALUE);
            dist[1] = 0;

            // Relax edges n-1 times
            for (int i = 0; i < n - 1; i++) {
                for (int u = 1; u <= n; u++) {
                    if (dist[u] != Integer.MAX_VALUE) {
                        for (int[] edge : graph.get(u)) {
                            int v = edge[0];
                            int weight = edge[1];

                            if (dist[u] + weight < dist[v]) {
                                dist[v] = dist[u] + weight;
                            }
                        }
                    }
                }
            }

            // Check for negative cycles
            for (int u = 1; u <= n; u++) {
                if (dist[u] != Integer.MAX_VALUE) {
                    for (int[] edge : graph.get(u)) {
                        int v = edge[0];
                        int weight = edge[1];

                        if (dist[u] + weight < dist[v]) {
                            return true; // Negative cycle detected
                        }
                    }
                }
            }

            return false;
        }
    }

    // Follow-up 6: Maximum Spanning Tree
    public static class MaximumSpanningTree {

        public static int maximumCost(int n, int[][] connections) {
            if (connections.length < n - 1) {
                return -1;
            }

            // Sort edges by cost in descending order
            Arrays.sort(connections, (a, b) -> Integer.compare(b[2], a[2]));

            UnionFind uf = new UnionFind(n + 1);
            int totalCost = 0;
            int edgesUsed = 0;

            for (int[] connection : connections) {
                if (uf.union(connection[0], connection[1])) {
                    totalCost += connection[2];
                    edgesUsed++;

                    if (edgesUsed == n - 1) {
                        break;
                    }
                }
            }

            return edgesUsed == n - 1 ? totalCost : -1;
        }

        public static List<int[]> findMaximumSpanningTreeEdges(int n, int[][] connections) {
            Arrays.sort(connections, (a, b) -> Integer.compare(b[2], a[2]));

            UnionFind uf = new UnionFind(n + 1);
            List<int[]> mstEdges = new ArrayList<>();

            for (int[] connection : connections) {
                if (uf.union(connection[0], connection[1])) {
                    mstEdges.add(connection);

                    if (mstEdges.size() == n - 1) {
                        break;
                    }
                }
            }

            return mstEdges;
        }
    }

    // Utility methods for testing
    public static void printConnections(int[][] connections) {
        System.out.println("Connections:");
        for (int[] connection : connections) {
            System.out.println("  " + connection[0] + " <-> " + connection[1] + " (cost: " + connection[2] + ")");
        }
    }

    public static void printMSTEdges(List<int[]> edges) {
        System.out.println("MST Edges:");
        int totalCost = 0;
        for (int[] edge : edges) {
            System.out.println("  " + edge[0] + " <-> " + edge[1] + " (cost: " + edge[2] + ")");
            totalCost += edge[2];
        }
        System.out.println("Total cost: " + totalCost);
    }

    public static void compareAlgorithms(int n, int[][] connections) {
        System.out.println("=== Algorithm Comparison ===");

        long start, end;

        // Kruskal's algorithm
        start = System.nanoTime();
        int kruskals = minimumCost(n, connections);
        end = System.nanoTime();
        System.out.println("Kruskal's: " + kruskals + " (Time: " + (end - start) / 1_000_000 + " ms)");

        // Prim's algorithm
        start = System.nanoTime();
        int prims = minimumCostPrim(n, connections);
        end = System.nanoTime();
        System.out.println("Prim's: " + prims + " (Time: " + (end - start) / 1_000_000 + " ms)");

        // Optimized Kruskal's
        start = System.nanoTime();
        int optimized = minimumCostOptimized(n, connections);
        end = System.nanoTime();
        System.out.println("Optimized: " + optimized + " (Time: " + (end - start) / 1_000_000 + " ms)");

        // Indexed Prim's
        start = System.nanoTime();
        int indexed = minimumCostIndexedPrim(n, connections);
        end = System.nanoTime();
        System.out.println("Indexed Prim's: " + indexed + " (Time: " + (end - start) / 1_000_000 + " ms)");

        System.out.println("All results match: " + (kruskals == prims && prims == optimized && optimized == indexed));
        System.out.println();
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Example ===");

        int n1 = 3;
        int[][] connections1 = { { 1, 2, 5 }, { 1, 3, 6 }, { 2, 3, 1 } };

        printConnections(connections1);

        int result1 = minimumCost(n1, connections1);
        System.out.println("Minimum cost: " + result1);
        System.out.println();

        // Test Case 2: Algorithm comparison
        System.out.println("=== Test Case 2: Algorithm Comparison ===");

        int n2 = 4;
        int[][] connections2 = { { 1, 2, 3 }, { 3, 4, 4 }, { 1, 3, 1 }, { 1, 4, 6 }, { 2, 3, 5 }, { 2, 4, 2 } };

        compareAlgorithms(n2, connections2);

        // Test Case 3: Impossible case
        System.out.println("=== Test Case 3: Impossible Case ===");

        int n3 = 4;
        int[][] connections3 = { { 1, 2, 3 }, { 3, 4, 4 } }; // Not enough edges

        int result3 = minimumCost(n3, connections3);
        System.out.println("Minimum cost (should be -1): " + result3);
        System.out.println();

        // Test Case 4: Second MST
        System.out.println("=== Test Case 4: Second MST ===");

        int n4 = 4;
        int[][] connections4 = { { 1, 2, 1 }, { 2, 3, 2 }, { 3, 4, 3 }, { 1, 4, 4 }, { 2, 4, 5 } };

        int firstMST = minimumCost(n4, connections4);
        int secondMST = SecondMST.secondMinimumSpanningTree(n4, connections4);

        System.out.println("First MST cost: " + firstMST);
        System.out.println("Second MST cost: " + secondMST);
        System.out.println();

        // Test Case 5: Dynamic MST
        System.out.println("=== Test Case 5: Dynamic MST ===");

        DynamicMST dynamicMST = new DynamicMST(4);

        System.out.println("Initial MST cost: " + dynamicMST.getCurrentMSTCost());

        dynamicMST.addEdge(1, 2, 1);
        System.out.println("After adding edge (1,2,1): " + dynamicMST.getCurrentMSTCost());

        dynamicMST.addEdge(2, 3, 2);
        System.out.println("After adding edge (2,3,2): " + dynamicMST.getCurrentMSTCost());

        dynamicMST.addEdge(3, 4, 3);
        System.out.println("After adding edge (3,4,3): " + dynamicMST.getCurrentMSTCost());

        dynamicMST.addEdge(1, 4, 4);
        System.out.println("After adding edge (1,4,4): " + dynamicMST.getCurrentMSTCost());

        dynamicMST.removeEdge(3, 4, 3);
        System.out.println("After removing edge (3,4,3): " + dynamicMST.getCurrentMSTCost());
        System.out.println();

        // Test Case 6: Constrained MST
        System.out.println("=== Test Case 6: Constrained MST ===");

        int n6 = 4;
        int[][] connections6 = { { 1, 2, 3 }, { 2, 3, 1 }, { 3, 4, 2 }, { 1, 4, 5 }, { 2, 4, 4 } };
        int[][] mandatory = { { 1, 2, 3 } };
        int[][] forbidden = { { 2, 4, 4 } };

        int normalMST = minimumCost(n6, connections6);
        int mandatoryMST = ConstrainedMST.minimumCostWithMandatoryEdges(n6, connections6, mandatory);
        int forbiddenMST = ConstrainedMST.minimumCostWithForbiddenEdges(n6, connections6, forbidden);
        int budgetMST = ConstrainedMST.minimumCostWithBudgetLimit(n6, connections6, 8);

        System.out.println("Normal MST: " + normalMST);
        System.out.println("With mandatory edge (1,2,3): " + mandatoryMST);
        System.out.println("With forbidden edge (2,4,4): " + forbiddenMST);
        System.out.println("With budget limit 8: " + budgetMST);
        System.out.println();

        // Test Case 7: k-MST
        System.out.println("=== Test Case 7: k-MST (Minimum Spanning Forest) ===");

        int n7 = 5;
        int[][] connections7 = { { 1, 2, 1 }, { 2, 3, 2 }, { 3, 4, 3 }, { 4, 5, 4 }, { 1, 5, 5 } };

        for (int k = 1; k <= 5; k++) {
            MinimumSpanningForest.Result result = MinimumSpanningForest.findKMST(n7, connections7, k);
            System.out.println("k=" + k + " MST cost: " + result.totalCost + " (edges: " + result.edges.size() + ")");
        }
        System.out.println();

        // Test Case 8: Maximum Spanning Tree
        System.out.println("=== Test Case 8: Maximum Spanning Tree ===");

        int n8 = 4;
        int[][] connections8 = { { 1, 2, 3 }, { 2, 3, 1 }, { 3, 4, 2 }, { 1, 4, 5 }, { 2, 4, 4 } };

        int minMST = minimumCost(n8, connections8);
        int maxMST = MaximumSpanningTree.maximumCost(n8, connections8);

        System.out.println("Minimum MST cost: " + minMST);
        System.out.println("Maximum MST cost: " + maxMST);

        List<int[]> maxMSTEdges = MaximumSpanningTree.findMaximumSpanningTreeEdges(n8, connections8);
        printMSTEdges(maxMSTEdges);
        System.out.println();

        // Test Case 9: Negative weights
        System.out.println("=== Test Case 9: Negative Weights ===");

        int n9 = 3;
        int[][] connections9 = { { 1, 2, -5 }, { 2, 3, 3 }, { 1, 3, 2 } };

        int negativeWeightMST = NegativeWeightMST.minimumCostWithNegativeWeights(n9, connections9);
        boolean hasNegCycle = NegativeWeightMST.hasNegativeCycle(n9, connections9);

        System.out.println("MST with negative weights: " + negativeWeightMST);
        System.out.println("Has negative cycle: " + hasNegCycle);
        System.out.println();

        // Test Case 10: Large graph stress test
        System.out.println("=== Test Case 10: Stress Test ===");

        int n10 = 1000;
        int[][] connections10 = new int[5000][3];
        Random random = new Random(42);

        for (int i = 0; i < connections10.length; i++) {
            connections10[i][0] = random.nextInt(n10) + 1;
            connections10[i][1] = random.nextInt(n10) + 1;
            connections10[i][2] = random.nextInt(100) + 1;

            // Ensure no self loops
            while (connections10[i][0] == connections10[i][1]) {
                connections10[i][1] = random.nextInt(n10) + 1;
            }
        }

        long start = System.nanoTime();
        int stressResult = minimumCost(n10, connections10);
        long end = System.nanoTime();

        System.out.println("Stress test (1000 cities, 5000 edges): " + stressResult +
                " (Time: " + (end - start) / 1_000_000 + " ms)");

        // Test Case 11: Edge cases
        System.out.println("\n=== Test Case 11: Edge Cases ===");

        // Single city
        int single = minimumCost(1, new int[0][]);
        System.out.println("Single city: " + single);

        // Two cities, one edge
        int two = minimumCost(2, new int[][] { { 1, 2, 5 } });
        System.out.println("Two cities, one edge: " + two);

        // Two cities, no edge
        int twoNoEdge = minimumCost(2, new int[0][]);
        System.out.println("Two cities, no edge: " + twoNoEdge);

        // Complete graph
        int n11 = 4;
        List<int[]> completeEdges = new ArrayList<>();
        for (int i = 1; i <= n11; i++) {
            for (int j = i + 1; j <= n11; j++) {
                completeEdges.add(new int[] { i, j, i + j });
            }
        }

        int complete = minimumCost(n11, completeEdges.toArray(new int[0][]));
        System.out.println("Complete graph: " + complete);

        System.out.println("\nConnecting Cities with Minimum Cost testing completed successfully!");
    }
}
