package trees.hard;

import java.util.*;

/**
 * Advanced Variation: Minimum Nodes to Remove for Full Binary Tree
 * 
 * Description: Given a binary tree, find the minimum number of nodes to remove
 * to make it a full binary tree.
 * A full binary tree is one where every node has either 0 or 2 children.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - 1 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you return which nodes to remove?
 * 2. Can you maximize the remaining tree size?
 * 3. Can you handle complete binary tree conversion?
 */
public class FindMinimumNumberOfNodesToRemoveForFullBinaryTree {

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

    // Approach 1: Post-order DFS
    public int minNodesToRemove(TreeNode root) {
        if (root == null)
            return 0;
        return countRemovals(root)[1];
    }

    private int[] countRemovals(TreeNode node) {
        if (node == null)
            return new int[] { 0, 0 }; // {remaining_nodes, removed_nodes}

        if (isLeaf(node))
            return new int[] { 1, 0 };

        int[] left = countRemovals(node.left);
        int[] right = countRemovals(node.right);

        // If both children exist, keep current structure
        if (node.left != null && node.right != null) {
            return new int[] { 1 + left[0] + right[0], left[1] + right[1] };
        }

        // If only one child exists, we need to decide
        if (node.left != null) {
            // Option 1: Remove left subtree, current becomes leaf
            int removeLeft = left[0] + left[1];
            // Option 2: Remove current node, keep left subtree
            int removeCurrent = 1 + left[1];

            if (removeLeft <= removeCurrent) {
                return new int[] { 1, removeLeft };
            } else {
                return new int[] { left[0], removeCurrent };
            }
        } else {
            // Similar logic for right child
            int removeRight = right[0] + right[1];
            int removeCurrent = 1 + right[1];

            if (removeRight <= removeCurrent) {
                return new int[] { 1, removeRight };
            } else {
                return new int[] { right[0], removeCurrent };
            }
        }
    }

    // Follow-up 1: Return which nodes to remove
    public List<Integer> nodesToRemove(TreeNode root) {
        List<Integer> toRemove = new ArrayList<>();
        if (root == null)
            return toRemove;

        findNodesToRemove(root, toRemove);
        return toRemove;
    }

    private boolean findNodesToRemove(TreeNode node, List<Integer> toRemove) {
        if (node == null)
            return false;

        if (isLeaf(node))
            return true; // Keep leaf nodes

        boolean keepLeft = findNodesToRemove(node.left, toRemove);
        boolean keepRight = findNodesToRemove(node.right, toRemove);

        // If only one child should be kept, remove the other
        if (keepLeft && !keepRight) {
            if (node.right != null) {
                addSubtreeToRemove(node.right, toRemove);
            }
            return true;
        } else if (!keepLeft && keepRight) {
            if (node.left != null) {
                addSubtreeToRemove(node.left, toRemove);
            }
            return true;
        } else if (keepLeft && keepRight) {
            return true; // Keep current node
        } else {
            // Both children should be removed, remove current node too
            toRemove.add(node.val);
            return false;
        }
    }

    // Follow-up 2: Maximize remaining tree size
    public int maxRemainingNodes(TreeNode root) {
        if (root == null)
            return 0;
        return maxNodesHelper(root);
    }

    private int maxNodesHelper(TreeNode node) {
        if (node == null)
            return 0;

        if (isLeaf(node))
            return 1;

        int leftMax = maxNodesHelper(node.left);
        int rightMax = maxNodesHelper(node.right);

        if (node.left != null && node.right != null) {
            return 1 + leftMax + rightMax;
        }

        // Choose the option that maximizes nodes
        int keepCurrent = 1; // Current becomes leaf
        int keepChild = Math.max(leftMax, rightMax);

        return Math.max(keepCurrent, keepChild);
    }

    // Follow-up 3: Convert to complete binary tree
    public int minRemovalsForComplete(TreeNode root) {
        if (root == null)
            return 0;

        int totalNodes = countNodes(root);
        int maxCompleteNodes = getMaxCompleteNodes(totalNodes);

        return totalNodes - maxCompleteNodes;
    }

    // Helper methods
    private boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }

    private void addSubtreeToRemove(TreeNode node, List<Integer> toRemove) {
        if (node == null)
            return;

        toRemove.add(node.val);
        addSubtreeToRemove(node.left, toRemove);
        addSubtreeToRemove(node.right, toRemove);
    }

    private int countNodes(TreeNode node) {
        if (node == null)
            return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    private int getMaxCompleteNodes(int n) {
        // Find largest complete binary tree that fits in n nodes
        int levels = (int) (Math.log(n + 1) / Math.log(2));
        return (1 << levels) - 1;
    }

    private boolean isFullBinaryTree(TreeNode node) {
        if (node == null)
            return true;

        if (isLeaf(node))
            return true;

        if (node.left != null && node.right != null) {
            return isFullBinaryTree(node.left) && isFullBinaryTree(node.right);
        }

        return false;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMinimumNumberOfNodesToRemoveForFullBinaryTree solution = new FindMinimumNumberOfNodesToRemoveForFullBinaryTree();

        // Test case 1: Tree with single children
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.right.left = new TreeNode(5);
        root1.right.right = new TreeNode(6);
        root1.right.left.left = new TreeNode(7);

        System.out.println("Test 1 - Nodes to remove: " + solution.minNodesToRemove(root1));
        System.out.println("Which nodes to remove: " + solution.nodesToRemove(root1));

        // Test case 2: Already full binary tree
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(3);
        root2.left.left = new TreeNode(4);
        root2.left.right = new TreeNode(5);

        System.out.println("\nTest 2 - Already full tree: " + solution.minNodesToRemove(root2));

        // Test case 3: Maximize remaining nodes
        System.out.println("\nTest 3 - Max remaining nodes: " + solution.maxRemainingNodes(root1));

        // Test case 4: Linear tree (worst case)
        TreeNode linear = new TreeNode(1);
        linear.left = new TreeNode(2);
        linear.left.left = new TreeNode(3);
        linear.left.left.left = new TreeNode(4);
        linear.left.left.left.left = new TreeNode(5);

        System.out.println("\nTest 4 - Linear tree: " + solution.minNodesToRemove(linear));

        // Test case 5: Complete tree conversion
        TreeNode root3 = buildRandomTree(15);
        System.out.println("\nTest 5 - Complete tree conversion: " + solution.minRemovalsForComplete(root3));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Null root: " + solution.minNodesToRemove(null));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.minNodesToRemove(singleNode));

        TreeNode rootWithOneChild = new TreeNode(1);
        rootWithOneChild.left = new TreeNode(2);
        System.out.println("Root with one child: " + solution.minNodesToRemove(rootWithOneChild));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildRandomTree(1000);
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

            // Randomly decide whether to add left child
            if (count < nodes && rand.nextBoolean()) {
                current.left = new TreeNode(count + 1);
                queue.offer(current.left);
                count++;
            }

            // Randomly decide whether to add right child
            if (count < nodes && rand.nextBoolean()) {
                current.right = new TreeNode(count + 1);
                queue.offer(current.right);
                count++;
            }
        }
        return root;
    }
}
