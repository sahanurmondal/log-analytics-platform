package graphs.hard;

import java.util.*;

/**
 * LeetCode 332: Reconstruct Itinerary (Eulerian Path)
 * https://leetcode.com/problems/reconstruct-itinerary/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 6+ interviews)
 *
 * Description: Find Eulerian path in a directed/undirected graph.
 *
 * Constraints:
 * - 1 <= tickets.length <= 300
 * - tickets[i].length == 2
 * 
 * Follow-up Questions:
 * 1. Can you handle multiple Eulerian paths?
 * 2. Can you solve for undirected graphs?
 * 3. Can you detect if no Eulerian path exists?
 */
public class FindEulerianPath {
    // Approach 1: Hierholzer's Algorithm - O(E) time, O(E) space
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();
        for (List<String> ticket : tickets) {
            graph.computeIfAbsent(ticket.get(0), k -> new PriorityQueue<>()).add(ticket.get(1));
        }
        List<String> path = new ArrayList<>();
        dfs("JFK", graph, path);
        Collections.reverse(path);
        return path;
    }

    private void dfs(String airport, Map<String, PriorityQueue<String>> graph, List<String> path) {
        PriorityQueue<String> destinations = graph.get(airport);
        while (destinations != null && !destinations.isEmpty()) {
            dfs(destinations.poll(), graph, path);
        }
        path.add(airport);
    }

    // Approach 2: For general Eulerian path detection
    public List<Integer> findEulerianPathDirected(int n, int[][] edges) {
        Map<Integer, Deque<Integer>> graph = new HashMap<>();
        int[] in = new int[n], out = new int[n];
        for (int[] e : edges) {
            graph.computeIfAbsent(e[0], k -> new ArrayDeque<>()).add(e[1]);
            out[e[0]]++;
            in[e[1]]++;
        }
        int start = findStartNode(n, in, out);
        if (start == -1)
            return Collections.emptyList();
        List<Integer> path = new ArrayList<>();
        dfsInteger(start, graph, path);
        Collections.reverse(path);
        return path.size() == edges.length + 1 ? path : Collections.emptyList();
    }

    private int findStartNode(int n, int[] in, int[] out) {
        int start = 0;
        for (int i = 0; i < n; i++) {
            if (out[i] - in[i] == 1)
                return i;
            if (out[i] > 0)
                start = i;
        }
        return start;
    }

    private void dfsInteger(int u, Map<Integer, Deque<Integer>> graph, List<Integer> path) {
        Deque<Integer> neighbors = graph.getOrDefault(u, new ArrayDeque<>());
        while (!neighbors.isEmpty()) {
            dfsInteger(neighbors.poll(), graph, path);
        }
        path.add(u);
    }

    public static void main(String[] args) {
        FindEulerianPath fep = new FindEulerianPath();
        // Basic case
        List<List<String>> tickets1 = Arrays.asList(
                Arrays.asList("MUC", "LHR"), Arrays.asList("JFK", "MUC"), Arrays.asList("SFO", "SJC"),
                Arrays.asList("LHR", "SFO"));
        System.out.println(fep.findItinerary(tickets1)); // [JFK, MUC, LHR, SFO, SJC]

        // Multiple valid paths
        List<List<String>> tickets2 = Arrays.asList(
                Arrays.asList("JFK", "SFO"), Arrays.asList("JFK", "ATL"), Arrays.asList("SFO", "ATL"),
                Arrays.asList("ATL", "JFK"), Arrays.asList("ATL", "SFO"));
        System.out.println(fep.findItinerary(tickets2)); // [JFK, ATL, JFK, SFO, ATL, SFO]

        // Integer graph tests
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 } };
        System.out.println(fep.findEulerianPathDirected(4, edges1)); // [0,1,2,0,1,3] or similar

        // No Eulerian path
        int[][] edges2 = { { 0, 1 }, { 2, 3 } };
        System.out.println(fep.findEulerianPathDirected(4, edges2)); // []
    }
}
