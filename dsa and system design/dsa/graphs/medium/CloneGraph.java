package graphs.medium;

import java.util.*;

/**
 * LeetCode 133: Clone Graph
 * https://leetcode.com/problems/clone-graph/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 18+ interviews)
 *
 * Description: Given a reference of a node in a connected undirected graph,
 * return a deep copy (clone) of the graph.
 * Each node in the graph contains a value (int) and a list (List[Node]) of its
 * neighbors.
 *
 * Constraints:
 * - The number of nodes in the graph is in the range [0, 100].
 * - 1 <= Node.val <= 100
 * - Node.val is unique for each node.
 * - There are no repeated edges and no self-loops in the graph.
 * - The Graph is connected and all nodes can be visited starting from the given
 * node.
 * 
 * Follow-up Questions:
 * 1. What if the graph is disconnected?
 * 2. How would you handle very large graphs?
 * 3. Can you do it iteratively instead of recursively?
 */
public class CloneGraph {

    class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }

    // Approach 1: DFS with HashMap - O(N + M) time, O(N) space
    public Node cloneGraph(Node node) {
        if (node == null)
            return null;

        Map<Node, Node> visited = new HashMap<>();
        return dfsClone(node, visited);
    }

    private Node dfsClone(Node node, Map<Node, Node> visited) {
        if (visited.containsKey(node)) {
            return visited.get(node);
        }

        // Create clone of current node
        Node clone = new Node(node.val);
        visited.put(node, clone);

        // Clone all neighbors
        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(dfsClone(neighbor, visited));
        }

        return clone;
    }

    // Approach 2: BFS with HashMap - O(N + M) time, O(N) space
    public Node cloneGraphBFS(Node node) {
        if (node == null)
            return null;

        Map<Node, Node> visited = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();

        // Clone the first node and add to queue
        Node clone = new Node(node.val);
        visited.put(node, clone);
        queue.offer(node);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Process all neighbors
            for (Node neighbor : current.neighbors) {
                if (!visited.containsKey(neighbor)) {
                    // Clone the neighbor and add to queue
                    visited.put(neighbor, new Node(neighbor.val));
                    queue.offer(neighbor);
                }

                // Add the clone of neighbor to current node's clone
                visited.get(current).neighbors.add(visited.get(neighbor));
            }
        }

        return clone;
    }

    // Follow-up: Handle disconnected graph
    public List<Node> cloneDisconnectedGraph(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty())
            return new ArrayList<>();

        Map<Node, Node> visited = new HashMap<>();
        List<Node> result = new ArrayList<>();

        for (Node node : nodes) {
            if (!visited.containsKey(node)) {
                Node clone = dfsClone(node, visited);
                result.add(clone);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        CloneGraph solution = new CloneGraph();

        // Create test graph: 1-2-3-4 with 2-4 edge
        CloneGraph.Node node1 = solution.new Node(1);
        CloneGraph.Node node2 = solution.new Node(2);
        CloneGraph.Node node3 = solution.new Node(3);
        CloneGraph.Node node4 = solution.new Node(4);

        node1.neighbors.add(node2);
        node2.neighbors.addAll(Arrays.asList(node1, node3, node4));
        node3.neighbors.add(node2);
        node4.neighbors.add(node2);

        CloneGraph.Node cloned = solution.cloneGraph(node1);
        System.out.println("Original node 1 value: " + node1.val);
        System.out.println("Cloned node 1 value: " + cloned.val);
        System.out.println("Are they the same object? " + (node1 == cloned)); // false
    }
}
