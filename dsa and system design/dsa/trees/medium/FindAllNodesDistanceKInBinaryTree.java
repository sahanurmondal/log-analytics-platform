package trees.medium;

import java.util.*;

/**
 * LeetCode 863: All Nodes Distance K in Binary Tree
 * https://leetcode.com/problems/all-nodes-distance-k-in-binary-tree/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given the root of a binary tree, the value of a target node
 * target, and an integer k, return an array of the values of all nodes that
 * have a distance k from the target node.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 500]
 * - 0 <= Node.val <= 500
 * - All values are unique
 * - target is the value of one of the nodes in the tree
 * 
 * Follow-up Questions:
 * 1. Can you find nodes at distance range [k1, k2]?
 * 2. Can you handle multiple target nodes?
 * 3. Can you optimize for repeated queries?
 */
public class FindAllNodesDistanceKInBinaryTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    // Approach 1: Build graph and BFS
    public List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        buildParentMap(root, null, parent);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();

        queue.offer(target);
        visited.add(target);

        int currentDistance = 0;

        while (!queue.isEmpty()) {
            if (currentDistance == k) {
                List<Integer> result = new ArrayList<>();
                while (!queue.isEmpty()) {
                    result.add(queue.poll().val);
                }
                return result;
            }

            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                // Add left child
                if (node.left != null && !visited.contains(node.left)) {
                    queue.offer(node.left);
                    visited.add(node.left);
                }

                // Add right child
                if (node.right != null && !visited.contains(node.right)) {
                    queue.offer(node.right);
                    visited.add(node.right);
                }

                // Add parent
                if (parent.get(node) != null && !visited.contains(parent.get(node))) {
                    queue.offer(parent.get(node));
                    visited.add(parent.get(node));
                }
            }

            currentDistance++;
        }

        return new ArrayList<>();
    }

    private void buildParentMap(TreeNode node, TreeNode par, Map<TreeNode, TreeNode> parent) {
        if (node == null)
            return;

        parent.put(node, par);
        buildParentMap(node.left, node, parent);
        buildParentMap(node.right, node, parent);
    }

    // Follow-up 1: Find nodes at distance range [k1, k2]
    public List<Integer> distanceRange(TreeNode root, TreeNode target, int k1, int k2) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        buildParentMap(root, null, parent);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();
        List<Integer> result = new ArrayList<>();

        queue.offer(target);
        visited.add(target);

        int currentDistance = 0;

        while (!queue.isEmpty()) {
            if (currentDistance >= k1 && currentDistance <= k2) {
                for (TreeNode node : queue) {
                    result.add(node.val);
                }
            }

            if (currentDistance > k2)
                break;

            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                // Add children and parent
                for (TreeNode neighbor : Arrays.asList(node.left, node.right, parent.get(node))) {
                    if (neighbor != null && !visited.contains(neighbor)) {
                        queue.offer(neighbor);
                        visited.add(neighbor);
                    }
                }
            }

            currentDistance++;
        }

        return result;
    }

    // Follow-up 2: Handle multiple target nodes
    public List<Integer> distanceKMultipleTargets(TreeNode root, List<TreeNode> targets, int k) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        buildParentMap(root, null, parent);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();

        for (TreeNode target : targets) {
            queue.offer(target);
            visited.add(target);
        }

        int currentDistance = 0;

        while (!queue.isEmpty()) {
            if (currentDistance == k) {
                List<Integer> result = new ArrayList<>();
                while (!queue.isEmpty()) {
                    result.add(queue.poll().val);
                }
                return result;
            }

            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                for (TreeNode neighbor : Arrays.asList(node.left, node.right, parent.get(node))) {
                    if (neighbor != null && !visited.contains(neighbor)) {
                        queue.offer(neighbor);
                        visited.add(neighbor);
                    }
                }
            }

            currentDistance++;
        }

        return new ArrayList<>();
    }

    // Follow-up 3: Optimize for repeated queries using precomputed distances
    public Map<TreeNode, Map<TreeNode, Integer>> precomputeAllDistances(TreeNode root) {
        Map<TreeNode, Map<TreeNode, Integer>> allDistances = new HashMap<>();
        List<TreeNode> allNodes = new ArrayList<>();
        collectAllNodes(root, allNodes);

        for (TreeNode node : allNodes) {
            allDistances.put(node, computeDistancesFromNode(root, node));
        }

        return allDistances;
    }

    private void collectAllNodes(TreeNode node, List<TreeNode> nodes) {
        if (node == null)
            return;

        nodes.add(node);
        collectAllNodes(node.left, nodes);
        collectAllNodes(node.right, nodes);
    }

    private Map<TreeNode, Integer> computeDistancesFromNode(TreeNode root, TreeNode target) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        buildParentMap(root, null, parent);

        Map<TreeNode, Integer> distances = new HashMap<>();
        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();

        queue.offer(target);
        visited.add(target);
        distances.put(target, 0);

        int currentDistance = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            currentDistance++;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                for (TreeNode neighbor : Arrays.asList(node.left, node.right, parent.get(node))) {
                    if (neighbor != null && !visited.contains(neighbor)) {
                        queue.offer(neighbor);
                        visited.add(neighbor);
                        distances.put(neighbor, currentDistance);
                    }
                }
            }
        }

        return distances;
    }

    // Helper: Find node by value
    private TreeNode findNode(TreeNode root, int val) {
        if (root == null)
            return null;
        if (root.val == val)
            return root;

        TreeNode left = findNode(root.left, val);
        if (left != null)
            return left;

        return findNode(root.right, val);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindAllNodesDistanceKInBinaryTree solution = new FindAllNodesDistanceKInBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(5);
        root1.right = new TreeNode(1);
        root1.left.left = new TreeNode(6);
        root1.left.right = new TreeNode(2);
        root1.right.left = new TreeNode(0);
        root1.right.right = new TreeNode(8);
        root1.left.right.left = new TreeNode(7);
        root1.left.right.right = new TreeNode(4);

        TreeNode target1 = root1.left; // Node with value 5
        System.out.println("Test 1 - Distance 2 from node 5:");
        List<Integer> result1 = solution.distanceK(root1, target1, 2);
        System.out.println("Result: " + result1);

        // Test case 2: Distance range
        System.out.println("\nTest 2 - Distance range [1, 2] from node 5:");
        List<Integer> result2 = solution.distanceRange(root1, target1, 1, 2);
        System.out.println("Result: " + result2);

        // Test case 3: Multiple targets
        List<TreeNode> targets = Arrays.asList(root1.left, root1.right);
        System.out.println("\nTest 3 - Distance 1 from multiple targets (5 and 1):");
        List<Integer> result3 = solution.distanceKMultipleTargets(root1, targets, 1);
        System.out.println("Result: " + result3);

        // Test case 4: Precomputed distances
        System.out.println("\nTest 4 - Precomputed distances:");
        Map<TreeNode, Map<TreeNode, Integer>> allDistances = solution.precomputeAllDistances(root1);
        System.out.println("Precomputed " + allDistances.size() + " distance maps");

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode single = new TreeNode(1);
        System.out.println("Single node at distance 0: " + solution.distanceK(single, single, 0));
        System.out.println("Single node at distance 1: " + solution.distanceK(single, single, 1));

        TreeNode linear = new TreeNode(1);
        linear.right = new TreeNode(2);
        linear.right.right = new TreeNode(3);
        System.out.println("Linear tree distance 2 from root: " + solution.distanceK(linear, linear, 2));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(500);
        TreeNode largeTarget = solution.findNode(largeTree, 250);

        long start = System.nanoTime();
        List<Integer> largeResult = solution.distanceK(largeTree, largeTarget, 3);
        long end = System.nanoTime();
        System.out.println("Large tree (500 nodes) distance 3: " + largeResult.size() + " nodes in "
                + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                current.left = new TreeNode(count + 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(count + 1);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
