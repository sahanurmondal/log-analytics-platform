package graphs.hard;

import java.util.*;

/**
 * All Topological Sorts of a DAG
 * https://www.geeksforgeeks.org/all-topological-sorts-of-a-directed-acyclic-graph/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 3+ interviews)
 *
 * Description: Find all possible topological orderings of a directed acyclic
 * graph (DAG).
 * A topological sort is a linear ordering of vertices such that for every
 * directed edge (u,v),
 * vertex u comes before v in the ordering.
 *
 * Constraints:
 * - 1 <= n <= 15 (limitation due to exponential nature)
 * - 0 <= edges.length <= n*(n-1)/2
 * 
 * Follow-up Questions:
 * 1. Can you verify if the graph has a unique topological sort?
 * 2. Can you find the lexicographically smallest topological sort?
 * 3. How would you count the number of possible topological sorts without
 * generating them?
 */
public class AllTopologicalSorts {

    // Approach 1: Backtracking - O(V! * E) time, O(V+E) space
    public List<List<Integer>> allTopologicalSorts(int n, int[][] edges) {
        // Build adjacency list and calculate indegrees
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        int[] indegree = new int[n];
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            indegree[edge[1]]++;
        }

        List<List<Integer>> allSorts = new ArrayList<>();
        boolean[] visited = new boolean[n];

        // Start backtracking
        backtrack(n, adj, indegree, visited, new ArrayList<>(), allSorts);

        return allSorts;
    }

    private void backtrack(int n, List<List<Integer>> adj, int[] indegree,
            boolean[] visited, List<Integer> current, List<List<Integer>> allSorts) {
        boolean foundZero = false;

        for (int i = 0; i < n; i++) {
            // If indegree is 0 and not visited yet
            if (indegree[i] == 0 && !visited[i]) {
                // Visit the vertex
                visited[i] = true;
                current.add(i);

                // Reduce indegree of adjacent vertices
                for (int neighbor : adj.get(i)) {
                    indegree[neighbor]--;
                }

                // Recursive call for next level
                backtrack(n, adj, indegree, visited, current, allSorts);

                // Backtrack - restore state
                visited[i] = false;
                current.remove(current.size() - 1);

                for (int neighbor : adj.get(i)) {
                    indegree[neighbor]++;
                }

                foundZero = true;
            }
        }

        // If no vertex with indegree 0, we've used all vertices
        if (!foundZero && current.size() == n) {
            allSorts.add(new ArrayList<>(current));
        }
    }

    // Approach 2: Kahn's Algorithm variation - O(V! * E) time, O(V+E) space
    public List<List<Integer>> allTopologicalSortsKahn(int n, int[][] edges) {
        // Build adjacency list and calculate indegrees
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        int[] indegree = new int[n];
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            indegree[edge[1]]++;
        }

        List<List<Integer>> allSorts = new ArrayList<>();
        boolean[] visited = new boolean[n];

        kahnBacktrack(n, adj, indegree, visited, new ArrayList<>(), allSorts);

        return allSorts;
    }

    private void kahnBacktrack(int n, List<List<Integer>> adj, int[] indegree,
            boolean[] visited, List<Integer> current, List<List<Integer>> allSorts) {
        // Check if all vertices are visited
        if (current.size() == n) {
            allSorts.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < n; i++) {
            // Select vertices with indegree 0 and not visited
            if (indegree[i] == 0 && !visited[i]) {
                // Add to current ordering and mark as visited
                visited[i] = true;
                current.add(i);

                // Reduce indegree of neighbors
                for (int neighbor : adj.get(i)) {
                    indegree[neighbor]--;
                }

                // Recursively find all topological sorts
                kahnBacktrack(n, adj, indegree, visited, current, allSorts);

                // Backtrack
                visited[i] = false;
                current.remove(current.size() - 1);

                for (int neighbor : adj.get(i)) {
                    indegree[neighbor]++;
                }
            }
        }
    }

    // Follow-up 1: Check for unique topological sort
    public boolean hasUniqueTopologicalSort(int n, int[][] edges) {
        // Build adjacency list and calculate indegrees
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        int[] indegree = new int[n];
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            indegree[edge[1]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        // If at any point we have more than one node with indegree 0,
        // there's more than one possible ordering
        while (!queue.isEmpty()) {
            if (queue.size() > 1) {
                return false; // Multiple options, not unique
            }

            int u = queue.poll();

            for (int v : adj.get(u)) {
                indegree[v]--;
                if (indegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        return true;
    }

    // Follow-up 2: Find lexicographically smallest topological sort
    public List<Integer> lexicographicallySmallestTopSort(int n, int[][] edges) {
        // Build adjacency list and calculate indegrees
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        int[] indegree = new int[n];
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            indegree[edge[1]]++;
        }

        // Use priority queue to always choose smallest vertex with indegree 0
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                pq.offer(i);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            int u = pq.poll();
            result.add(u);

            for (int v : adj.get(u)) {
                indegree[v]--;
                if (indegree[v] == 0) {
                    pq.offer(v);
                }
            }
        }

        return result.size() == n ? result : new ArrayList<>(); // Check if valid topo sort
    }

    public static void main(String[] args) {
        AllTopologicalSorts ats = new AllTopologicalSorts();

        // Test case 1: Simple DAG
        int[][] edges1 = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 2, 3 } };
        System.out.println("All topological sorts 1: " + ats.allTopologicalSorts(4, edges1));
        // Outputs like: [[0, 1, 2, 3], [0, 2, 1, 3]]

        // Test case 2: Another DAG with more orderings
        int[][] edges2 = { { 5, 2 }, { 5, 0 }, { 4, 0 }, { 4, 1 }, { 2, 3 }, { 3, 1 } };
        List<List<Integer>> allSorts2 = ats.allTopologicalSorts(6, edges2);
        System.out.println("Number of topological sorts 2: " + allSorts2.size());
        // Too many to print

        // Test case 3: Check unique topological sort
        int[][] edges3 = { { 0, 1 }, { 1, 2 }, { 2, 3 } }; // Linear chain, unique sort
        System.out.println("Has unique topo sort 3: " + ats.hasUniqueTopologicalSort(4, edges3));
        // Output: true

        // Test case 4: Multiple possible orderings
        System.out.println("Has unique topo sort 4: " + ats.hasUniqueTopologicalSort(4, edges1));
        // Output: false

        // Test case 5: Lexicographically smallest sort
        System.out.println("Lexicographically smallest topo sort: " + ats.lexicographicallySmallestTopSort(4, edges1));
        // Output: [0, 1, 2, 3]

        // Test case 6: Empty graph
        int[][] edges6 = {};
        System.out.println("All topological sorts 6: " + ats.allTopologicalSorts(3, edges6));
        // Output: all permutations of [0,1,2]
    }
}
