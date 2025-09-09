package trees.medium;

import java.util.*;

/**
 * LeetCode 226: Invert Binary Tree
 * https://leetcode.com/problems/invert-binary-tree/
 * 
 * Companies: Google, Amazon, Facebook, Apple
 * Frequency: Very High
 *
 * Description: Given the root of a binary tree, invert the tree, and return its
 * root.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 100]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you do it iteratively?
 * 2. Can you invert only specific levels?
 * 3. Can you check if a tree is its own inverse?
 */
public class InvertBinaryTree {

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

    // Approach 1: Recursive
    public TreeNode invertTree(TreeNode root) {
        if (root == null)
            return null;

        // Swap children
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        // Recursively invert subtrees
        invertTree(root.left);
        invertTree(root.right);

        return root;
    }

    // Follow-up 1: Iterative using queue (BFS)
    public TreeNode invertTreeIterative(TreeNode root) {
        if (root == null)
            return null;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();

            // Swap children
            TreeNode temp = current.left;
            current.left = current.right;
            current.right = temp;

            // Add children to queue
            if (current.left != null)
                queue.offer(current.left);
            if (current.right != null)
                queue.offer(current.right);
        }

        return root;
    }

    // Follow-up 1: Iterative using stack (DFS)
    public TreeNode invertTreeIterativeStack(TreeNode root) {
        if (root == null)
            return null;

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode current = stack.pop();

            // Swap children
            TreeNode temp = current.left;
            current.left = current.right;
            current.right = temp;

            // Add children to stack
            if (current.left != null)
                stack.push(current.left);
            if (current.right != null)
                stack.push(current.right);
        }

        return root;
    }

    // Follow-up 2: Invert only specific levels
    public TreeNode invertSpecificLevels(TreeNode root, Set<Integer> levelsToInvert) {
        invertAtLevels(root, 1, levelsToInvert);
        return root;
    }

    private void invertAtLevels(TreeNode node, int currentLevel, Set<Integer> levelsToInvert) {
        if (node == null)
            return;

        if (levelsToInvert.contains(currentLevel)) {
            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;
        }

        invertAtLevels(node.left, currentLevel + 1, levelsToInvert);
        invertAtLevels(node.right, currentLevel + 1, levelsToInvert);
    }

    // Follow-up 3: Check if tree is its own inverse (symmetric)
    public boolean isSymmetric(TreeNode root) {
        if (root == null)
            return true;
        return isSymmetricHelper(root.left, root.right);
    }

    private boolean isSymmetricHelper(TreeNode left, TreeNode right) {
        if (left == null && right == null)
            return true;
        if (left == null || right == null)
            return false;

        return left.val == right.val &&
                isSymmetricHelper(left.left, right.right) &&
                isSymmetricHelper(left.right, right.left);
    }

    // Additional: Create a copy and then invert
    public TreeNode invertTreeCopy(TreeNode root) {
        TreeNode copy = copyTree(root);
        return invertTree(copy);
    }

    private TreeNode copyTree(TreeNode node) {
        if (node == null)
            return null;

        TreeNode newNode = new TreeNode(node.val);
        newNode.left = copyTree(node.left);
        newNode.right = copyTree(node.right);

        return newNode;
    }

    // Additional: Check if two trees are inverses of each other
    public boolean areTreesInverse(TreeNode tree1, TreeNode tree2) {
        if (tree1 == null && tree2 == null)
            return true;
        if (tree1 == null || tree2 == null)
            return false;

        return tree1.val == tree2.val &&
                areTreesInverse(tree1.left, tree2.right) &&
                areTreesInverse(tree1.right, tree2.left);
    }

    // Additional: Invert tree and return both original and inverted
    public TreeNode[] invertAndKeepOriginal(TreeNode root) {
        TreeNode original = copyTree(root);
        TreeNode inverted = invertTree(root);
        return new TreeNode[] { original, inverted };
    }

    // Helper: Print tree structure
    private void printTree(TreeNode root, String prefix, boolean isLeft) {
        if (root == null)
            return;

        System.out.println(prefix + (isLeft ? "├── " : "└── ") + root.val);

        if (root.left != null || root.right != null) {
            if (root.left != null) {
                printTree(root.left, prefix + (isLeft ? "│   " : "    "), true);
            } else {
                System.out.println(prefix + (isLeft ? "│   " : "    ") + "├── null");
            }

            if (root.right != null) {
                printTree(root.right, prefix + (isLeft ? "│   " : "    "), false);
            } else {
                System.out.println(prefix + (isLeft ? "│   " : "    ") + "└── null");
            }
        }
    }

    // Helper: Compare trees for equality
    private boolean areTreesEqual(TreeNode tree1, TreeNode tree2) {
        if (tree1 == null && tree2 == null)
            return true;
        if (tree1 == null || tree2 == null)
            return false;

        return tree1.val == tree2.val &&
                areTreesEqual(tree1.left, tree2.left) &&
                areTreesEqual(tree1.right, tree2.right);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        InvertBinaryTree solution = new InvertBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(3);
        root1.right.left = new TreeNode(6);
        root1.right.right = new TreeNode(9);

        System.out.println("Test 1 - Original tree:");
        solution.printTree(root1, "", false);

        TreeNode inverted1 = solution.invertTree(root1);
        System.out.println("\nInverted tree:");
        solution.printTree(inverted1, "", false);

        // Test case 2: Iterative approaches
        TreeNode root2 = new TreeNode(2);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(3);

        System.out.println("\nTest 2 - Iterative approaches:");
        TreeNode copy1 = solution.copyTree(root2);
        TreeNode copy2 = solution.copyTree(root2);

        solution.invertTreeIterative(copy1);
        solution.invertTreeIterativeStack(copy2);

        System.out.println("Both iterative methods produce same result: " +
                solution.areTreesEqual(copy1, copy2));

        // Test case 3: Invert specific levels
        TreeNode root3 = new TreeNode(1);
        root3.left = new TreeNode(2);
        root3.right = new TreeNode(3);
        root3.left.left = new TreeNode(4);
        root3.left.right = new TreeNode(5);
        root3.right.left = new TreeNode(6);
        root3.right.right = new TreeNode(7);

        System.out.println("\nTest 3 - Invert only level 2:");
        Set<Integer> levelsToInvert = Set.of(2);
        solution.invertSpecificLevels(root3, levelsToInvert);
        solution.printTree(root3, "", false);

        // Test case 4: Symmetric tree check
        TreeNode symmetric = new TreeNode(1);
        symmetric.left = new TreeNode(2);
        symmetric.right = new TreeNode(2);
        symmetric.left.left = new TreeNode(3);
        symmetric.left.right = new TreeNode(4);
        symmetric.right.left = new TreeNode(4);
        symmetric.right.right = new TreeNode(3);

        System.out.println("\nTest 4 - Symmetric tree check: " + solution.isSymmetric(symmetric));

        // Test case 5: Check if trees are inverses
        TreeNode tree1 = new TreeNode(1);
        tree1.left = new TreeNode(2);
        tree1.right = new TreeNode(3);

        TreeNode tree2 = new TreeNode(1);
        tree2.left = new TreeNode(3);
        tree2.right = new TreeNode(2);

        System.out.println("\nTest 5 - Trees are inverses: " + solution.areTreesInverse(tree1, tree2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Null tree inversion: " + (solution.invertTree(null) == null));

        TreeNode singleNode = new TreeNode(1);
        TreeNode invertedSingle = solution.invertTree(singleNode);
        System.out.println("Single node remains same: " + (invertedSingle.val == 1));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(100);

        long start = System.nanoTime();
        solution.invertTreeIterative(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree (100 nodes) inverted in: " + (end - start) / 1_000_000 + " ms");
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
