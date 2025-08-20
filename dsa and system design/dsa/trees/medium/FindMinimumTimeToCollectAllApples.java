package trees.medium;

import java.util.*;

/**
 * LeetCode 1443: Minimum Time to Collect All Apples in a Tree
 * https://leetcode.com/problems/minimum-time-to-collect-all-apples-in-a-tree/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given an undirected tree consisting of n vertices numbered from
 * 0 to n-1, which has some apples in their vertices. Return the minimum time to
 * collect all apples in the tree starting from vertex 0.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - edges.length == n - 1
 * - hasApple.length == n
 * 
 * Follow-up Questions:
 * 1. Can you find the actual path taken?
 * 2. Can you handle different starting points?
 * 3. Can you optimize for multiple collectors?
 */
public class FindMinimumTimeToCollectAllApples {

    // Approach 1: DFS with pruning
    public int minTimeToCollectApples(int n, int[][] edges, List<Boolean> hasApple) {
        // Build adjacency list
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        return dfs(0, -1, graph, hasApple);
    }

    private int dfs(int node, int parent, Map<Integer, List<Integer>> graph, List<Boolean> hasApple) {
        int totalTime = 0;

        for (int child : graph.get(node)) {
            if (child != parent) {
                int childTime = dfs(child, node, graph, hasApple);
                if (childTime > 0 || hasApple.get(child)) {
                    totalTime += childTime + 2; // 2 for going down and coming back
                }
            }
        }

        return totalTime;
    }

    // Follow-up 1: Find the actual path taken
    public List<Integer> findCollectionPath(int n, int[][] edges, List<Boolean> hasApple) {
        Map<Integer, List<Integer>> graph = buildGraph(n, edges);
        List<Integer> path = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        dfsWithPath(0, -1, graph, hasApple, path, visited);
        return path;
    }

    private boolean dfsWithPath(int node, int parent, Map<Integer, List<Integer>> graph,
            List<Boolean> hasApple, List<Integer> path, Set<Integer> visited) {
        visited.add(node);
        path.add(node);

        boolean hasAppleInSubtree = hasApple.get(node);

        for (int child : graph.get(node)) {
            if (child != parent && !visited.contains(child)) {
                if (dfsWithPath(child, node, graph, hasApple, path, visited)) {
                    hasAppleInSubtree = true;
                    path.add(node); // Coming back to current node
                }
            }
        }

        if (!hasAppleInSubtree && node != 0) {
            // Remove this node from path if no apple in subtree
            path.remove(path.size() - 1);
        }

        return hasAppleInSubtree;
    }

    // Follow-up 2: Handle different starting points
    public int minTimeFromStartingPoint(int n, int[][] edges, List<Boolean> hasApple, int start) {
        Map<Integer, List<Integer>> graph = buildGraph(n, edges);
        return dfsFromStart(start, -1, graph, hasApple);
    }

    private int dfsFromStart(int node, int parent, Map<Integer, List<Integer>> graph, List<Boolean> hasApple) {
        int totalTime = 0;

        for (int child : graph.get(node)) {
            if (child != parent) {
                int childTime = dfsFromStart(child, node, graph, hasApple);
                if (childTime > 0 || hasApple.get(child)) {
                    totalTime += childTime + 2;
                }
            }
        }

        return totalTime;
    }

    // Follow-up 3: Optimize for multiple collectors
    public int minTimeWithMultipleCollectors(int n, int[][] edges, List<Boolean> hasApple, int numCollectors) {
        if (numCollectors >= n)
            return 0; // More collectors than nodes

        Map<Integer, List<Integer>> graph = buildGraph(n, edges);
        List<Integer> appleNodes = new ArrayList<>();

        for (int i = 0; i < hasApple.size(); i++) {
            if (hasApple.get(i)) {
                appleNodes.add(i);
            }
        }

        if (appleNodes.isEmpty())
            return 0;

        // For simplicity, assign apple nodes to collectors greedily
        int totalTime = 0;
        int applesPerCollector = (int) Math.ceil((double) appleNodes.size() / numCollectors);

        for (int i = 0; i < numCollectors && i * applesPerCollector < appleNodes.size(); i++) {
            int start = i * applesPerCollector;
            int end = Math.min(start + applesPerCollector, appleNodes.size());

            // Calculate time for this collector to visit assigned apples
            List<Boolean> collectorApples = new ArrayList<>(Collections.nCopies(n, false));
            for (int j = start; j < end; j++) {
                collectorApples.set(appleNodes.get(j), true);
            }

            totalTime = Math.max(totalTime, minTimeToCollectApples(n, edges, collectorApples));
        }

        return totalTime;
    }

    // Additional: Count total apples that need to be collected
    public int countApplesToCollect(List<Boolean> hasApple) {
        int count = 0;
        for (boolean apple : hasApple) {
            if (apple)
                count++;
        }
        return count;
    }

    // Additional: Find farthest apple from root
    public int findFarthestApple(int n, int[][] edges, List<Boolean> hasApple) {
        Map<Integer, List<Integer>> graph = buildGraph(n, edges);
        return findFarthestAppleDFS(0, -1, graph, hasApple, 0)[1];
    }

    private int[] findFarthestAppleDFS(int node, int parent, Map<Integer, List<Integer>> graph,
            List<Boolean> hasApple, int depth) {
        int maxDepth = hasApple.get(node) ? depth : -1;

        for (int child : graph.get(node)) {
            if (child != parent) {
                int childMaxDepth = findFarthestAppleDFS(child, node, graph, hasApple, depth + 1)[1];
                maxDepth = Math.max(maxDepth, childMaxDepth);
            }
        }

        return new int[] { node, maxDepth };
    }

    // Helper method to build graph
    private Map<Integer, List<Integer>> buildGraph(int n, int[][] edges) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        return graph;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMinimumTimeToCollectAllApples solution = new FindMinimumTimeToCollectAllApples();

        // Test case 1: Basic case
        int n1 = 7;
        int[][] edges1 = { { 0, 1 }, { 0, 2 }, { 1, 4 }, { 1, 5 }, { 2, 3 }, { 2, 6 } };
        List<Boolean> hasApple1 = Arrays.asList(false, false, true, false, true, true, false);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Min time: " + solution.minTimeToCollectApples(n1, edges1, hasApple1));
        System.out.println("Apples to collect: " + solution.countApplesToCollect(hasApple1));
        System.out.println("Farthest apple depth: " + solution.findFarthestApple(n1, edges1, hasApple1));

        // Test case 2: Collection path
        System.out.println("\nTest 2 - Collection path:");
        List<Integer> path = solution.findCollectionPath(n1, edges1, hasApple1);
        System.out.println("Path taken: " + path);

        // Test case 3: Different starting point
        System.out.println("\nTest 3 - Different starting point (node 2):");
        System.out.println("Min time from node 2: " + solution.minTimeFromStartingPoint(n1, edges1, hasApple1, 2));

        // Test case 4: Multiple collectors
        System.out.println("\nTest 4 - Multiple collectors (2 collectors):");
        System.out.println(
                "Min time with 2 collectors: " + solution.minTimeWithMultipleCollectors(n1, edges1, hasApple1, 2));

        // Edge cases
        System.out.println("\nEdge cases:");
        List<Boolean> noApples = Arrays.asList(false, false, false, false, false, false, false);
        System.out.println("No apples: " + solution.minTimeToCollectApples(n1, edges1, noApples));

        List<Boolean> allApples = Arrays.asList(true, true, true, true, true, true, true);
        System.out.println("All apples: " + solution.minTimeToCollectApples(n1, edges1, allApples));

        // Single node
        int[][] edgesSingle = {};
        List<Boolean> singleApple = Arrays.asList(true);
        System.out.println("Single node with apple: " + solution.minTimeToCollectApples(1, edgesSingle, singleApple));

        // Stress test
        System.out.println("\nStress test:");
        int largeN = 1000;
        int[][] largeEdges = buildLargeTreeEdges(largeN);
        List<Boolean> largeApples = new ArrayList<>();
        Random rand = new Random(42);
        for (int i = 0; i < largeN; i++) {
            largeApples.add(rand.nextBoolean());
        }

        long start = System.nanoTime();
        int result = solution.minTimeToCollectApples(largeN, largeEdges, largeApples);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static int[][] buildLargeTreeEdges(int n) {
        int[][] edges = new int[n - 1][2];
        for (int i = 1; i < n; i++) {
            edges[i - 1] = new int[] { i / 2, i }; // Build a binary tree structure
        }
        return edges;
    }
}
