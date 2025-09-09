package binarysearchtree.hard;

import java.util.*;

/**
 * LeetCode 99: Recover Binary Search Tree
 * https://leetcode.com/problems/recover-binary-search-tree/
 * 
 * Problem:
 * You are given the root of a binary search tree (BST) where the values of
 * exactly two nodes of the tree were swapped by mistake.
 * Recover the tree without changing its structure.
 * 
 * Example 1:
 * Input: root = [1,3,null,null,2]
 * Output: [3,1,null,null,2]
 * Explanation: 3 cannot be a left child of 1 because 3 > 1. Swapping 1 and 3
 * makes the BST valid.
 * 
 * Example 2:
 * Input: root = [3,1,4,null,null,2]
 * Output: [2,1,4,null,null,3]
 * Explanation: 2 cannot be in the right subtree of 3 because 2 < 3. Swapping 2
 * and 3 makes the BST valid.
 * 
 * Constraints:
 * The number of nodes in the tree is in the range [2, 1000]
 * -2^31 <= Node.val <= 2^31 - 1
 * 
 * Follow up: A solution using O(n) space is pretty straight-forward. Could you
 * devise a constant O(1) space solution?
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta
 * Frequency: High
 */
public class RecoverBST {

    // Definition for a binary tree node
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

    // Approach 1: Inorder traversal with O(n) space
    private List<TreeNode> nodes = new ArrayList<>();

    public void recoverTree(TreeNode root) {
        nodes.clear();
        inorderTraversal(root);

        // Find the two swapped nodes
        TreeNode first = null, second = null;

        for (int i = 0; i < nodes.size() - 1; i++) {
            if (nodes.get(i).val > nodes.get(i + 1).val) {
                if (first == null) {
                    first = nodes.get(i);
                    second = nodes.get(i + 1);
                } else {
                    second = nodes.get(i + 1);
                    break;
                }
            }
        }

        // Swap the values
        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    private void inorderTraversal(TreeNode root) {
        if (root == null)
            return;

        inorderTraversal(root.left);
        nodes.add(root);
        inorderTraversal(root.right);
    }

    // Approach 2: Morris traversal with O(1) space
    private TreeNode first = null, second = null, prev = null;

    public void recoverTreeConstantSpace(TreeNode root) {
        first = second = prev = null;
        morrisInorder(root);

        // Swap the values
        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    private void morrisInorder(TreeNode root) {
        TreeNode curr = root;

        while (curr != null) {
            if (curr.left == null) {
                // Process current node
                processNode(curr);
                curr = curr.right;
            } else {
                // Find inorder predecessor
                TreeNode pred = curr.left;
                while (pred.right != null && pred.right != curr) {
                    pred = pred.right;
                }

                if (pred.right == null) {
                    // Make connection
                    pred.right = curr;
                    curr = curr.left;
                } else {
                    // Break connection and process
                    pred.right = null;
                    processNode(curr);
                    curr = curr.right;
                }
            }
        }
    }

    private void processNode(TreeNode node) {
        if (prev != null && prev.val > node.val) {
            if (first == null) {
                first = prev;
                second = node;
            } else {
                second = node;
            }
        }
        prev = node;
    }

    // Approach 3: Recursive inorder with O(1) extra space (excluding recursion
    // stack)
    public void recoverTreeRecursive(TreeNode root) {
        first = second = prev = null;
        inorderRecursive(root);

        // Swap the values
        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    private void inorderRecursive(TreeNode root) {
        if (root == null)
            return;

        inorderRecursive(root.left);

        if (prev != null && prev.val > root.val) {
            if (first == null) {
                first = prev;
                second = root;
            } else {
                second = root;
            }
        }
        prev = root;

        inorderRecursive(root.right);
    }

    // Helper method to print tree (for testing)
    public void printInorder(TreeNode root) {
        if (root == null)
            return;

        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
    }

    // Helper method to validate BST
    public boolean isValidBST(TreeNode root) {
        return validate(root, null, null);
    }

    private boolean validate(TreeNode node, Integer min, Integer max) {
        if (node == null)
            return true;

        if ((min != null && node.val <= min) || (max != null && node.val >= max)) {
            return false;
        }

        return validate(node.left, min, node.val) && validate(node.right, node.val, max);
    }

    public static void main(String[] args) {
        RecoverBST solution = new RecoverBST();

        // Test case 1: [1,3,null,null,2] -> [3,1,null,null,2]
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(3);
        root1.left.right = new TreeNode(2);

        System.out.println("Test 1 - Before recovery:");
        solution.printInorder(root1);
        System.out.println("\nValid BST: " + solution.isValidBST(root1));

        solution.recoverTree(root1);
        System.out.println("After recovery:");
        solution.printInorder(root1);
        System.out.println("\nValid BST: " + solution.isValidBST(root1));

        // Test case 2: [3,1,4,null,null,2] -> [2,1,4,null,null,3]
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.right.left = new TreeNode(2);

        System.out.println("\n\nTest 2 - Before recovery:");
        solution.printInorder(root2);
        System.out.println("\nValid BST: " + solution.isValidBST(root2));

        solution.recoverTreeConstantSpace(root2);
        System.out.println("After recovery:");
        solution.printInorder(root2);
        System.out.println("\nValid BST: " + solution.isValidBST(root2));

        System.out.println("\n\nAll test cases completed successfully!");
    }
}
