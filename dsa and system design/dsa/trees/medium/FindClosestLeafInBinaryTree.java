package trees.medium;

import java.util.*;

/**
 * LeetCode 742: Find Closest Leaf in a Binary Tree
 * https://leetcode.com/problems/find-closest-leaf-in-a-binary-tree/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree where every node has a unique
 * value and a target integer k, return the value of the nearest leaf node to
 * the target k in the tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 1000]
 * - 1 <= Node.val <= 1000
 * - All values are unique
 * - target k is the value of one of the nodes in the tree
 * 
 * Follow-up Questions:
 * 1. Can you find all leaves at minimum distance?
 * 2. Can you handle multiple target nodes?
 * 3. Can you optimize for repeated queries?
 */
public class FindClosestLeafInBinaryTree {

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

    // Approach 1: Build graph and BFS from target
    public int findClosestLeaf(TreeNode root, int k) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        TreeNode target = buildParentMapAndFindTarget(root, null, parent, k);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();
        queue.offer(target);
        visited.add(target);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (isLeaf(node))
                return node.val;

            // Add neighbors (parent, left child, right child)
            for (TreeNode neighbor : Arrays.asList(parent.get(node), node.left, node.right)) {
                if (neighbor != null && !visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return -1; // Should never reach here
    }

    private TreeNode buildParentMapAndFindTarget(TreeNode node, TreeNode par, Map<TreeNode, TreeNode> parent, int k) {
        if (node == null)
            return null;

        parent.put(node, par);
        if (node.val == k)
            return node;

        TreeNode found = buildParentMapAndFindTarget(node.left, node, parent, k);
        if (found != null)
            return found;

        return buildParentMapAndFindTarget(node.right, node, parent, k);
    }

    // Follow-up 1: Find all leaves at minimum distance
    public List<Integer> findAllClosestLeaves(TreeNode root, int k) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        TreeNode target = buildParentMapAndFindTarget(root, null, parent, k);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();
        queue.offer(target);
        visited.add(target);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> currentLevelLeaves = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (isLeaf(node)) {
                    currentLevelLeaves.add(node.val);
                } else {
                    for (TreeNode neighbor : Arrays.asList(parent.get(node), node.left, node.right)) {
                        if (neighbor != null && !visited.contains(neighbor)) {
                            queue.offer(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }

            if (!currentLevelLeaves.isEmpty()) {
                return currentLevelLeaves;
            }
        }

        return new ArrayList<>();
    }

    // Follow-up 2: Handle multiple target nodes
    public int findClosestLeafMultipleTargets(TreeNode root, List<Integer> targets) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        buildParentMap(root, null, parent);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();

        // Add all target nodes to queue
        addTargetsToQueue(root, targets, queue, visited);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (isLeaf(node))
                return node.val;

            for (TreeNode neighbor : Arrays.asList(parent.get(node), node.left, node.right)) {
                if (neighbor != null && !visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return -1;
    }

    // Follow-up 3: Optimize for repeated queries by precomputing distances
    public Map<Integer, Integer> precomputeClosestLeaves(TreeNode root) {
        Map<Integer, Integer> nodeToClosestLeaf = new HashMap<>();
        List<TreeNode> allNodes = new ArrayList<>();
        collectAllNodes(root, allNodes);

        for (TreeNode node : allNodes) {
            int closestLeaf = findClosestLeaf(root, node.val);
            nodeToClosestLeaf.put(node.val, closestLeaf);
        }

        return nodeToClosestLeaf;
    }

    // Helper methods
    private boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }

    private void buildParentMap(TreeNode node, TreeNode par, Map<TreeNode, TreeNode> parent) {
        if (node == null)
            return;

        parent.put(node, par);
        buildParentMap(node.left, node, parent);
        buildParentMap(node.right, node, parent);
    }

    private void addTargetsToQueue(TreeNode node, List<Integer> targets, Queue<TreeNode> queue, Set<TreeNode> visited) {
        if (node == null)
            return;

        if (targets.contains(node.val)) {
            queue.offer(node);
            visited.add(node);
        }

        addTargetsToQueue(node.left, targets, queue, visited);
        addTargetsToQueue(node.right, targets, queue, visited);
    }

    private void collectAllNodes(TreeNode node, List<TreeNode> nodes) {
        if (node == null)
            return;

        nodes.add(node);
        collectAllNodes(node.left, nodes);
        collectAllNodes(node.right, nodes);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindClosestLeafInBinaryTree solution = new FindClosestLeafInBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(2);
        root1.left.left = new TreeNode(5);
        root1.left.right = new TreeNode(6);
        root1.left.left.left = new TreeNode(7);

        System.out.println("Test 1 - Closest leaf to node 2:");
        System.out.println("Result: " + solution.findClosestLeaf(root1, 2));

        // Test case 2: All closest leaves
        System.out.println("\nTest 2 - All closest leaves to node 3:");
        System.out.println("Result: " + solution.findAllClosestLeaves(root1, 3));

        // Test case 3: Multiple targets
        List<Integer> targets = Arrays.asList(5, 6);
        System.out.println("\nTest 3 - Closest leaf to multiple targets [5, 6]:");
        System.out.println("Result: " + solution.findClosestLeafMultipleTargets(root1, targets));

        // Test case 4: Precomputed distances
        System.out.println("\nTest 4 - Precomputed closest leaves:");
        Map<Integer, Integer> precomputed = solution.precomputeClosestLeaves(root1);
        System.out.println("Precomputed map: " + precomputed);

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node (itself is leaf): " + solution.findClosestLeaf(singleNode, 1));

        TreeNode linearTree = new TreeNode(1);
        linearTree.right = new TreeNode(2);
        linearTree.right.right = new TreeNode(3);
        linearTree.right.right.right = new TreeNode(4);
        System.out.println("Linear tree closest to root: " + solution.findClosestLeaf(linearTree, 1));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(500);

        long start = System.nanoTime();
        int result = solution.findClosestLeaf(largeTree, 250);
        long end = System.nanoTime();
        System.out.println("Large tree (500 nodes) result: " + result + " in " + (end - start) / 1_000_000 + " ms");
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
