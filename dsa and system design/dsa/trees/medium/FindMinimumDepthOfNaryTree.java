package trees.medium;

import java.util.*;

/**
 * Advanced Variation: Minimum Depth of N-ary Tree
 * 
 * Description: Given an n-ary tree, find its minimum depth. The minimum depth
 * is the number of nodes along the shortest path from the root node down to the
 * nearest leaf node.
 *
 * Constraints:
 * - The total number of nodes is in the range [0, 10^4]
 * - The depth of the n-ary tree is less than or equal to 1000
 * 
 * Follow-up Questions:
 * 1. Can you find all nodes at minimum depth?
 * 2. Can you use iterative approach?
 * 3. Can you find paths to minimum depth leaves?
 */
public class FindMinimumDepthOfNaryTree {

    public static class Node {
        public int val;
        public List<Node> children;

        public Node() {
        }

        public Node(int val) {
            this.val = val;
        }

        public Node(int val, List<Node> children) {
            this.val = val;
            this.children = children;
        }
    }

    // Approach 1: Recursive DFS
    public int minDepth(Node root) {
        if (root == null)
            return 0;

        if (root.children == null || root.children.isEmpty())
            return 1;

        int minDepth = Integer.MAX_VALUE;
        for (Node child : root.children) {
            minDepth = Math.min(minDepth, minDepth(child));
        }

        return minDepth + 1;
    }

    // Follow-up 1: Find all nodes at minimum depth
    public List<Integer> findNodesAtMinDepth(Node root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;

        int minDepth = minDepth(root);
        findNodesAtDepth(root, 1, minDepth, result);
        return result;
    }

    private void findNodesAtDepth(Node node, int currentDepth, int targetDepth, List<Integer> result) {
        if (node == null)
            return;

        if (currentDepth == targetDepth && isLeaf(node)) {
            result.add(node.val);
            return;
        }

        if (node.children != null) {
            for (Node child : node.children) {
                findNodesAtDepth(child, currentDepth + 1, targetDepth, result);
            }
        }
    }

    // Follow-up 2: Iterative BFS approach
    public int minDepthBFS(Node root) {
        if (root == null)
            return 0;

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                Node node = queue.poll();

                if (isLeaf(node))
                    return depth;

                if (node.children != null) {
                    for (Node child : node.children) {
                        queue.offer(child);
                    }
                }
            }
            depth++;
        }

        return depth;
    }

    // Follow-up 3: Find paths to minimum depth leaves
    public List<List<Integer>> findPathsToMinDepthLeaves(Node root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        int minDepth = minDepth(root);
        List<Integer> currentPath = new ArrayList<>();
        findAllMinDepthPaths(root, 1, minDepth, currentPath, result);
        return result;
    }

    private void findAllMinDepthPaths(Node node, int currentDepth, int targetDepth,
            List<Integer> currentPath, List<List<Integer>> result) {
        if (node == null)
            return;

        currentPath.add(node.val);

        if (currentDepth == targetDepth && isLeaf(node)) {
            result.add(new ArrayList<>(currentPath));
        } else if (currentDepth < targetDepth && node.children != null) {
            for (Node child : node.children) {
                findAllMinDepthPaths(child, currentDepth + 1, targetDepth, currentPath, result);
            }
        }

        currentPath.remove(currentPath.size() - 1);
    }

    // Additional: Count nodes at minimum depth
    public int countNodesAtMinDepth(Node root) {
        if (root == null)
            return 0;

        int minDepth = minDepth(root);
        return countNodesAtDepth(root, 1, minDepth);
    }

    private int countNodesAtDepth(Node node, int currentDepth, int targetDepth) {
        if (node == null)
            return 0;

        if (currentDepth == targetDepth && isLeaf(node))
            return 1;
        if (currentDepth >= targetDepth)
            return 0;

        int count = 0;
        if (node.children != null) {
            for (Node child : node.children) {
                count += countNodesAtDepth(child, currentDepth + 1, targetDepth);
            }
        }
        return count;
    }

    // Helper methods
    private boolean isLeaf(Node node) {
        return node != null && (node.children == null || node.children.isEmpty());
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMinimumDepthOfNaryTree solution = new FindMinimumDepthOfNaryTree();

        // Test case 1: Basic case
        Node root1 = new Node(1);
        root1.children = Arrays.asList(
                new Node(3, Arrays.asList(new Node(5), new Node(6))),
                new Node(2),
                new Node(4));

        System.out.println("Test 1 - Basic N-ary tree:");
        System.out.println("Min depth (DFS): " + solution.minDepth(root1));
        System.out.println("Min depth (BFS): " + solution.minDepthBFS(root1));

        // Test case 2: Nodes at minimum depth
        System.out.println("\nTest 2 - Nodes at min depth:");
        List<Integer> minDepthNodes = solution.findNodesAtMinDepth(root1);
        System.out.println("Nodes: " + minDepthNodes);
        System.out.println("Count: " + solution.countNodesAtMinDepth(root1));

        // Test case 3: Paths to minimum depth leaves
        System.out.println("\nTest 3 - Paths to min depth leaves:");
        List<List<Integer>> paths = solution.findPathsToMinDepthLeaves(root1);
        for (int i = 0; i < paths.size(); i++) {
            System.out.println("Path " + (i + 1) + ": " + paths.get(i));
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.minDepth(null));

        Node singleNode = new Node(1);
        System.out.println("Single node: " + solution.minDepth(singleNode));

        Node unbalanced = new Node(1);
        unbalanced.children = Arrays.asList(
                new Node(2, Arrays.asList(new Node(4), new Node(5))),
                new Node(3));
        System.out.println("Unbalanced tree: " + solution.minDepth(unbalanced));

        // Stress test
        System.out.println("\nStress test:");
        Node largeTree = buildLargeNaryTree(1000, 3);

        long start = System.nanoTime();
        int result = solution.minDepthBFS(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static Node buildLargeNaryTree(int totalNodes, int maxChildren) {
        if (totalNodes <= 0)
            return null;

        Node root = new Node(1);
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;
        Random rand = new Random(42);

        while (!queue.isEmpty() && count < totalNodes) {
            Node current = queue.poll();
            int numChildren = Math.min(rand.nextInt(maxChildren) + 1, totalNodes - count);

            if (numChildren > 0) {
                current.children = new ArrayList<>();
                for (int i = 0; i < numChildren && count < totalNodes; i++) {
                    Node child = new Node(count + 1);
                    current.children.add(child);
                    queue.offer(child);
                    count++;
                }
            }
        }

        return root;
    }
}
