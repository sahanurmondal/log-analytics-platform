package graphs.medium;

import java.util.*;

/**
 * LeetCode 332: Reconstruct Itinerary
 * https://leetcode.com/problems/reconstruct-itinerary/
 * 
 * Problem:
 * You are given a list of airline tickets where tickets[i] = [fromi, toi]
 * represent the departure and the arrival airports of one flight.
 * Reconstruct the itinerary in order and return it.
 * All of the tickets belong to a man who departs from "JFK", thus, the
 * itinerary must begin with "JFK".
 * If there are multiple valid itineraries, you should return the itinerary that
 * has the smallest lexical order when read as a single string.
 * 
 * For example, the itinerary ["JFK", "LGA"] has a smaller lexical order than
 * ["JFK", "LGB"].
 * You may assume all tickets form at least one valid itinerary. You may also
 * assume that you will always be given at least one valid path.
 * 
 * Example 1:
 * Input: tickets = [["MUC","LHR"],["JFK","MUC"],["SFO","SJC"],["LHR","SFO"]]
 * Output: ["JFK","MUC","LHR","SFO","SJC"]
 * 
 * Example 2:
 * Input: tickets =
 * [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]
 * Output: ["JFK","ATL","JFK","SFO","ATL","SFO"]
 * Explanation: Another possible reconstruction is
 * ["JFK","SFO","ATL","JFK","ATL","SFO"] but it is larger in lexical order.
 * 
 * Constraints:
 * 1 <= tickets.length <= 300
 * tickets[i].length == 2
 * fromi.length == 3
 * toi.length == 3
 * fromi and toi consist of uppercase English letters.
 * fromi != toi
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple
 * Frequency: High
 */
public class ReconstructItinerary {

    /**
     * Approach 1: Hierholzer's Algorithm (Eulerian Path)
     * Time Complexity: O(E log E) where E is number of edges (tickets)
     * Space Complexity: O(E)
     */
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();

        // Build graph with priority queues for lexical ordering
        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);
            graph.putIfAbsent(from, new PriorityQueue<>());
            graph.get(from).offer(to);
        }

        List<String> result = new ArrayList<>();
        dfsHierholzer("JFK", graph, result);

        Collections.reverse(result);
        return result;
    }

    private void dfsHierholzer(String airport, Map<String, PriorityQueue<String>> graph, List<String> result) {
        PriorityQueue<String> destinations = graph.get(airport);

        while (destinations != null && !destinations.isEmpty()) {
            String nextAirport = destinations.poll();
            dfsHierholzer(nextAirport, graph, result);
        }

        result.add(airport);
    }

    /**
     * Approach 2: Backtracking with DFS
     * Time Complexity: O(E^d) where d is maximum depth
     * Space Complexity: O(E)
     */
    public List<String> findItineraryBacktrack(List<List<String>> tickets) {
        Map<String, List<String>> graph = new HashMap<>();

        // Build adjacency list
        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);
            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);
        }

        // Sort destinations for lexical order
        for (String airport : graph.keySet()) {
            Collections.sort(graph.get(airport));
        }

        List<String> result = new ArrayList<>();
        result.add("JFK");

        Set<String> usedTickets = new HashSet<>();
        backtrack("JFK", graph, usedTickets, result, tickets.size());

        return result;
    }

    private boolean backtrack(String airport, Map<String, List<String>> graph,
            Set<String> usedTickets, List<String> result, int totalTickets) {
        if (result.size() == totalTickets + 1) {
            return true; // Found complete itinerary
        }

        if (!graph.containsKey(airport)) {
            return false;
        }

        List<String> destinations = graph.get(airport);
        for (int i = 0; i < destinations.size(); i++) {
            String destination = destinations.get(i);
            String ticketKey = airport + "->" + destination + "#" + i;

            if (!usedTickets.contains(ticketKey)) {
                usedTickets.add(ticketKey);
                result.add(destination);

                if (backtrack(destination, graph, usedTickets, result, totalTickets)) {
                    return true;
                }

                // Backtrack
                result.remove(result.size() - 1);
                usedTickets.remove(ticketKey);
            }
        }

        return false;
    }

    /**
     * Approach 3: Iterative DFS with Stack
     * Time Complexity: O(E log E)
     * Space Complexity: O(E)
     */
    public List<String> findItineraryIterative(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();

        // Build graph
        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);
            graph.putIfAbsent(from, new PriorityQueue<>());
            graph.get(from).offer(to);
        }

        Stack<String> stack = new Stack<>();
        List<String> result = new ArrayList<>();
        stack.push("JFK");

        while (!stack.isEmpty()) {
            String airport = stack.peek();

            if (graph.containsKey(airport) && !graph.get(airport).isEmpty()) {
                stack.push(graph.get(airport).poll());
            } else {
                result.add(stack.pop());
            }
        }

        Collections.reverse(result);
        return result;
    }

    /**
     * Follow-up: Find all possible itineraries
     */
    public List<List<String>> findAllItineraries(List<List<String>> tickets) {
        Map<String, List<String>> graph = new HashMap<>();

        // Build adjacency list
        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);
            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);
        }

        // Sort destinations for lexical order
        for (String airport : graph.keySet()) {
            Collections.sort(graph.get(airport));
        }

        List<List<String>> allItineraries = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        currentPath.add("JFK");

        Set<String> usedTickets = new HashSet<>();
        findAllPaths("JFK", graph, usedTickets, currentPath, tickets.size(), allItineraries);

        return allItineraries;
    }

    private void findAllPaths(String airport, Map<String, List<String>> graph,
            Set<String> usedTickets, List<String> currentPath,
            int totalTickets, List<List<String>> allItineraries) {
        if (currentPath.size() == totalTickets + 1) {
            allItineraries.add(new ArrayList<>(currentPath));
            return;
        }

        if (!graph.containsKey(airport)) {
            return;
        }

        List<String> destinations = graph.get(airport);
        for (int i = 0; i < destinations.size(); i++) {
            String destination = destinations.get(i);
            String ticketKey = airport + "->" + destination + "#" + i;

            if (!usedTickets.contains(ticketKey)) {
                usedTickets.add(ticketKey);
                currentPath.add(destination);

                findAllPaths(destination, graph, usedTickets, currentPath, totalTickets, allItineraries);

                // Backtrack
                currentPath.remove(currentPath.size() - 1);
                usedTickets.remove(ticketKey);
            }
        }
    }

    /**
     * Follow-up: Check if tickets form valid Eulerian path
     */
    public boolean hasEulerianPath(List<List<String>> tickets) {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, Integer> outDegree = new HashMap<>();

        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);

            outDegree.put(from, outDegree.getOrDefault(from, 0) + 1);
            inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);
        }

        Set<String> allAirports = new HashSet<>();
        allAirports.addAll(inDegree.keySet());
        allAirports.addAll(outDegree.keySet());

        int startNodes = 0; // outDegree - inDegree = 1
        int endNodes = 0; // inDegree - outDegree = 1

        for (String airport : allAirports) {
            int in = inDegree.getOrDefault(airport, 0);
            int out = outDegree.getOrDefault(airport, 0);

            if (out - in == 1) {
                startNodes++;
            } else if (in - out == 1) {
                endNodes++;
            } else if (in != out) {
                return false; // Neither balanced nor start/end node
            }
        }

        // Valid Eulerian path: exactly one start node and one end node
        return startNodes == 1 && endNodes == 1;
    }

    public static void main(String[] args) {
        ReconstructItinerary solution = new ReconstructItinerary();

        // Test case 1
        List<List<String>> tickets1 = Arrays.asList(
                Arrays.asList("MUC", "LHR"),
                Arrays.asList("JFK", "MUC"),
                Arrays.asList("SFO", "SJC"),
                Arrays.asList("LHR", "SFO"));
        System.out.println("Test 1: " + solution.findItinerary(tickets1));
        // ["JFK","MUC","LHR","SFO","SJC"]

        // Test case 2
        List<List<String>> tickets2 = Arrays.asList(
                Arrays.asList("JFK", "SFO"),
                Arrays.asList("JFK", "ATL"),
                Arrays.asList("SFO", "ATL"),
                Arrays.asList("ATL", "JFK"),
                Arrays.asList("ATL", "SFO"));
        System.out.println("Test 2: " + solution.findItinerary(tickets2));
        // ["JFK","ATL","JFK","SFO","ATL","SFO"]

        // Test case 3: Simple round trip
        List<List<String>> tickets3 = Arrays.asList(
                Arrays.asList("JFK", "ATL"),
                Arrays.asList("ATL", "JFK"));
        System.out.println("Test 3: " + solution.findItinerary(tickets3));
        // ["JFK","ATL","JFK"]

        // Test iterative approach
        System.out.println("Test 2 (Iterative): " + solution.findItineraryIterative(tickets2));

        // Test Eulerian path check
        System.out.println("Test 1 has Eulerian path: " + solution.hasEulerianPath(tickets1)); // true
        System.out.println("Test 2 has Eulerian path: " + solution.hasEulerianPath(tickets2)); // true

        System.out.println("\nAll test cases completed successfully!");
    }
}
