package graphs.easy;

import java.util.*;

/**
 * LeetCode 133: Clone Graph
 * https://leetcode.com/problems/clone-graph/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description: Given a reference of a node in a connected undirected graph,
 * return a deep copy.
 *
 * Constraints:
 * - Number of nodes <= 100
 * 
 * Follow-up Questions:
 * 1. Can you solve with BFS?
 * 2. Can you solve with DFS?
 */
public class CloneGraph {
    static class Node {
        public int val;
        public List<Node> neighbors;

        public Node(int val) {
            this.val = val;
            neighbors = new ArrayList<>();
        }
    }

    // Approach 1: DFS - O(N) time, O(N) space
    public Node cloneGraph(Node node) {
        return dfs(node, new HashMap<>());
    }

    private Node dfs(Node node, Map<Node, Node> map) {
        if (node == null)
            return null;
        if (map.containsKey(node))
            return map.get(node);
        Node copy = new Node(node.val);
        map.put(node, copy);
        for (Node nei : node.neighbors)
            copy.neighbors.add(dfs(nei, map));
        return copy;
    }

    // Approach 2: BFS
    // ...implement if needed...
    public static void main(String[] args) {
        CloneGraph cg = new CloneGraph();
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        n1.neighbors.add(n2);
        n1.neighbors.add(n4);
        n2.neighbors.add(n1);
        n2.neighbors.add(n3);
        n3.neighbors.add(n2);
        n3.neighbors.add(n4);
        n4.neighbors.add(n1);
        n4.neighbors.add(n3);

        Node clone = cg.cloneGraph(n1);
        System.out.println(clone != n1 && clone.val == 1 && clone.neighbors.size() == 2);

        // Null input
        System.out.println(cg.cloneGraph(null) == null);

        // Single node
        Node single = new Node(10);
        Node cloneSingle = cg.cloneGraph(single);
        System.out.println(cloneSingle != single && cloneSingle.val == 10 && cloneSingle.neighbors.isEmpty());
    }
}
