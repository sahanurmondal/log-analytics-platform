package trees.hard;

import java.util.*;

/**
 * LeetCode 998: Maximum Binary Tree II
 * https://leetcode.com/problems/maximum-binary-tree-ii/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a maximum binary tree and an integer val,
 * insert val into the tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 100]
 * - 1 <= Node.val <= 100
 * - All values are unique
 * 
 * Follow-up Questions:
 * 1. Can you handle multiple insertions efficiently?
 * 2. Can you validate if tree is maximum binary tree?
 * 3. Can you reconstruct from array efficiently?
 */
public class MaximumBinaryTreeII {

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

    // Approach 1: Recursive insertion
    public TreeNode insertIntoMaxTree(TreeNode root, int val) {
        if (root == null || val > root.val) {
            TreeNode newRoot = new TreeNode(val);
            newRoot.left = root;
            return newRoot;
        }

        root.right = insertIntoMaxTree(root.right, val);
        return root;
    }

    // Follow-up 1: Handle multiple insertions efficiently
    public TreeNode insertMultiple(TreeNode root, int[] vals) {
        TreeNode current = root;
        for (int val : vals) {
            current = insertIntoMaxTree(current, val);
        }
        return current;
    }

    // Follow-up 2: Validate if tree is maximum binary tree
    public boolean isMaximumBinaryTree(TreeNode root) {
        return validateMaxTree(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private boolean validateMaxTree(TreeNode node, int min, int max) {
        if (node == null)
            return true;

        if (node.val <= min || node.val >= max)
            return false;

        // Check if current node is maximum in its subtree
        if (!isMaxInSubtree(node, node.val))
            return false;

        return validateMaxTree(node.left, min, node.val) &&
                validateMaxTree(node.right, min, node.val);
    }

    private boolean isMaxInSubtree(TreeNode node, int maxVal) {
        if (node == null)
            return true;

        if (node.val > maxVal)
            return false;

        return isMaxInSubtree(node.left, maxVal) && isMaxInSubtree(node.right, maxVal);
    }

    // Follow-up 3: Reconstruct from array efficiently using stack
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        Stack<TreeNode> stack = new Stack<>();

        for (int num : nums) {
            TreeNode current = new TreeNode(num);
            TreeNode last = null;

            while (!stack.isEmpty() && stack.peek().val < num) {
                last = stack.pop();
            }

            current.left = last;

            if (!stack.isEmpty()) {
                stack.peek().right = current;
            }

            stack.push(current);
        }

        return stack.isEmpty() ? null : stack.get(0);
    }

    // Helper: Convert tree to array (inorder)
    public List<Integer> treeToArray(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }

    private void inorderTraversal(TreeNode node, List<Integer> result) {
        if (node == null)
            return;

        inorderTraversal(node.left, result);
        result.add(node.val);
        inorderTraversal(node.right, result);
    }

    // Helper: Print tree structure
    public void printTree(TreeNode root) {
        printTreeHelper(root, "", true);
    }

    private void printTreeHelper(TreeNode node, String prefix, boolean isLast) {
        if (node == null)
            return;

        System.out.println(prefix + (isLast ? "└── " : "├── ") + node.val);

        if (node.left != null || node.right != null) {
            if (node.left != null) {
                printTreeHelper(node.left, prefix + (isLast ? "    " : "│   "), node.right == null);
            }
            if (node.right != null) {
                printTreeHelper(node.right, prefix + (isLast ? "    " : "│   "), true);
            }
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MaximumBinaryTreeII solution = new MaximumBinaryTreeII();

        // Test case 1: Basic insertion
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(3);
        root1.right.right = new TreeNode(2);

        System.out.println("Test 1 - Original tree:");
        solution.printTree(root1);

        TreeNode newRoot = solution.insertIntoMaxTree(root1, 5);
        System.out.println("\nAfter inserting 5:");
        solution.printTree(newRoot);

        // Test case 2: Multiple insertions
        int[] vals = { 6, 1, 7 };
        TreeNode multiRoot = solution.insertMultiple(newRoot, vals);
        System.out.println("\nTest 2 - After multiple insertions [6,1,7]:");
        solution.printTree(multiRoot);

        // Test case 3: Validate maximum binary tree
        System.out.println("\nTest 3 - Validation:");
        System.out.println("Is valid max tree: " + solution.isMaximumBinaryTree(multiRoot));

        // Test case 4: Construct from array
        int[] array = { 3, 2, 1, 6, 0, 5 };
        TreeNode constructed = solution.constructMaximumBinaryTree(array);
        System.out.println("\nTest 4 - Constructed from array [3,2,1,6,0,5]:");
        solution.printTree(constructed);

        // Edge cases
        System.out.println("\nEdge cases:");

        // Insert into null tree
        TreeNode nullInsert = solution.insertIntoMaxTree(null, 10);
        System.out.println("Insert into null tree:");
        solution.printTree(nullInsert);

        // Single node tree
        TreeNode single = new TreeNode(5);
        TreeNode singleInsert = solution.insertIntoMaxTree(single, 3);
        System.out.println("\nInsert into single node:");
        solution.printTree(singleInsert);

        // Stress test
        System.out.println("\nStress test:");
        int[] largeArray = new int[100];
        for (int i = 0; i < 100; i++) {
            largeArray[i] = (i * 17 + 23) % 1000; // Some pseudo-random values
        }

        long start = System.nanoTime();
        TreeNode largeTree = solution.constructMaximumBinaryTree(largeArray);
        long end = System.nanoTime();
        System.out.println("Large tree construction (100 nodes): " + (end - start) / 1_000_000 + " ms");

        // Test validation on large tree
        start = System.nanoTime();
        boolean isValid = solution.isMaximumBinaryTree(largeTree);
        end = System.nanoTime();
        System.out.println("Large tree validation: " + isValid + " (" + (end - start) / 1_000_000 + " ms)");
    }
}
