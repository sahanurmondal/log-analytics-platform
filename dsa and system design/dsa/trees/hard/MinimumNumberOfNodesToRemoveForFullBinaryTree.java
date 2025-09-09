package trees.hard;

import java.util.*;

/**
 * Advanced Variation: Minimum Nodes to Remove for Full Binary Tree (Duplicate
 * handling)
 * 
 * Description: Given a binary tree, find the minimum number of nodes to remove
 * to make it a full binary tree. This version handles edge cases and
 * optimizations.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - 1 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you return the optimal tree structure?
 * 2. Can you handle trees with duplicate values?
 * 3. Can you convert to complete binary tree instead?
 */
public class MinimumNumberOfNodesToRemoveForFullBinaryTree {

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

    // Approach 1: Post-order DFS with optimal choice
    public int minNodesToRemove(TreeNode root) {
        if (root == null)
            return 0;
        return dfs(root)[1];
    }

    private int[] dfs(TreeNode node) {
        if (node == null)
            return new int[] { 0, 0 }; // {remaining, removed}

        if (isLeaf(node))
            return new int[] { 1, 0 };

        int[] left = dfs(node.left);
        int[] right = dfs(node.right);

        // Both children exist - keep all
        if (node.left != null && node.right != null) {
            return new int[] { 1 + left[0] + right[0], left[1] + right[1] };
        }

        // Only one child - choose optimal strategy
        if (node.left != null) {
            // Option 1: Remove entire left subtree, current becomes leaf
            int removeSubtree = left[0] + left[1];
            // Option 2: Remove current, promote left subtree
            int removeCurrent = 1 + left[1];

            if (removeSubtree <= removeCurrent) {
                return new int[] { 1, removeSubtree };
            } else {
                return new int[] { left[0], removeCurrent };
            }
        } else {
            // Similar for right child
            int removeSubtree = right[0] + right[1];
            int removeCurrent = 1 + right[1];

            if (removeSubtree <= removeCurrent) {
                return new int[] { 1, removeSubtree };
            } else {
                return new int[] { right[0], removeCurrent };
            }
        }
    }

    // Follow-up 1: Return the optimal tree structure
    public TreeNode getOptimalFullTree(TreeNode root) {
        if (root == null)
            return null;
        return buildOptimalTree(root)[0];
    }

    private TreeNode[] buildOptimalTree(TreeNode node) {
        if (node == null)
            return new TreeNode[] { null, null };

        if (isLeaf(node))
            return new TreeNode[] { new TreeNode(node.val), null };

        TreeNode[] left = buildOptimalTree(node.left);
        TreeNode[] right = buildOptimalTree(node.right);

        if (node.left != null && node.right != null) {
            TreeNode newNode = new TreeNode(node.val);
            newNode.left = left[0];
            newNode.right = right[0];
            return new TreeNode[] { newNode, null };
        }

        if (node.left != null) {
            TreeNode newLeaf = new TreeNode(node.val);
            return new TreeNode[] { newLeaf, left[0] };
        } else {
            TreeNode newLeaf = new TreeNode(node.val);
            return new TreeNode[] { newLeaf, right[0] };
        }
    }

    // Follow-up 2: Handle duplicate values by grouping
    public int minRemovalsWithDuplicates(TreeNode root) {
        Map<Integer, List<TreeNode>> valueGroups = new HashMap<>();
        collectValueGroups(root, valueGroups);

        int totalRemovals = 0;
        for (List<TreeNode> group : valueGroups.values()) {
            // For each group, find optimal tree and count removals
            for (TreeNode node : group) {
                totalRemovals += minNodesToRemove(node);
            }
        }

        return totalRemovals;
    }

    // Follow-up 3: Convert to complete binary tree
    public int minRemovalsForComplete(TreeNode root) {
        if (root == null)
            return 0;

        int totalNodes = countNodes(root);
        int height = getHeight(root);
        int completeNodes = (1 << height) - 1; // 2^h - 1

        return Math.max(0, totalNodes - completeNodes);
    }

    // Helper methods
    private boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }

    private void collectValueGroups(TreeNode node, Map<Integer, List<TreeNode>> groups) {
        if (node == null)
            return;

        groups.computeIfAbsent(node.val, k -> new ArrayList<>()).add(node);
        collectValueGroups(node.left, groups);
        collectValueGroups(node.right, groups);
    }

    private int countNodes(TreeNode node) {
        if (node == null)
            return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    private int getHeight(TreeNode node) {
        if (node == null)
            return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    private void printTree(TreeNode root, String prefix, boolean isLast) {
        if (root == null)
            return;

        System.out.println(prefix + (isLast ? "└── " : "├── ") + root.val);

        if (root.left != null || root.right != null) {
            if (root.left != null) {
                printTree(root.left, prefix + (isLast ? "    " : "│   "), root.right == null);
            }
            if (root.right != null) {
                printTree(root.right, prefix + (isLast ? "    " : "│   "), true);
            }
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MinimumNumberOfNodesToRemoveForFullBinaryTree solution = new MinimumNumberOfNodesToRemoveForFullBinaryTree();

        // Test case 1: Complex tree
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.right.left = new TreeNode(5);
        root1.right.right = new TreeNode(6);
        root1.right.left.left = new TreeNode(7);

        System.out.println("Test 1 - Original tree:");
        solution.printTree(root1, "", true);
        System.out.println("Minimum removals: " + solution.minNodesToRemove(root1));

        // Test case 2: Optimal tree structure
        TreeNode optimalTree = solution.getOptimalFullTree(root1);
        System.out.println("\nTest 2 - Optimal full tree:");
        solution.printTree(optimalTree, "", true);

        // Test case 3: Already full tree
        TreeNode fullTree = new TreeNode(1);
        fullTree.left = new TreeNode(2);
        fullTree.right = new TreeNode(3);
        fullTree.left.left = new TreeNode(4);
        fullTree.left.right = new TreeNode(5);

        System.out.println("\nTest 3 - Already full tree:");
        System.out.println("Removals needed: " + solution.minNodesToRemove(fullTree));

        // Test case 4: Complete tree conversion
        System.out.println("\nTest 4 - Complete tree conversion:");
        System.out.println("Removals for complete: " + solution.minRemovalsForComplete(root1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Null root: " + solution.minNodesToRemove(null));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.minNodesToRemove(singleNode));

        TreeNode zigzag = new TreeNode(1);
        zigzag.left = new TreeNode(2);
        zigzag.left.right = new TreeNode(3);
        zigzag.left.right.left = new TreeNode(4);
        System.out.println("Zigzag tree: " + solution.minNodesToRemove(zigzag));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildRandomTree(500);
        long start = System.nanoTime();
        int result = solution.minNodesToRemove(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildRandomTree(int nodes) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;
        Random rand = new Random(42);

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes && rand.nextBoolean()) {
                current.left = new TreeNode(count + 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes && rand.nextBoolean()) {
                current.right = new TreeNode(count + 1);
                queue.offer(current.right);
                count++;
            }
        }
        return root;
    }
}
