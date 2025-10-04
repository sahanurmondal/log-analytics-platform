package binarysearchtree.medium;

import binarysearchtree.TreeNode;
import java.util.*;

/**
 * LeetCode 701: Insert into a Binary Search Tree
 * https://leetcode.com/problems/insert-into-a-binary-search-tree/
 *
 * Description:
 * Given the root of a BST and a value to insert, insert the value into the BST.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^5 <= Node.val <= 10^5
 *
 * Follow-up:
 * - Can you solve it recursively and iteratively?
 */
public class InsertIntoBST {

    // Main solution: Recursive - O(h) time, O(h) space
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else {
            root.right = insertIntoBST(root.right, val);
        }

        return root;
    }

    // Iterative solution - O(h) time, O(1) space
    public TreeNode insertIntoBSTIterative(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        TreeNode current = root;
        TreeNode parent = null;

        while (current != null) {
            parent = current;
            if (val < current.val) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        if (val < parent.val) {
            parent.left = new TreeNode(val);
        } else {
            parent.right = new TreeNode(val);
        }

        return root;
    }

    // Insert with parent tracking
    public TreeNode insertIntoBSTWithParent(TreeNode root, int val) {
        TreeNode newNode = new TreeNode(val);

        if (root == null) {
            return newNode;
        }

        TreeNode current = root;

        while (true) {
            if (val < current.val) {
                if (current.left == null) {
                    current.left = newNode;
                    break;
                } else {
                    current = current.left;
                }
            } else {
                if (current.right == null) {
                    current.right = newNode;
                    break;
                } else {
                    current = current.right;
                }
            }
        }

        return root;
    }

    // Insert multiple values
    public TreeNode insertMultiple(TreeNode root, int[] values) {
        for (int val : values) {
            root = insertIntoBST(root, val);
        }
        return root;
    }

    // Helper: Print inorder traversal
    public void printInorder(TreeNode root) {
        if (root == null)
            return;

        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
    }

    public static void main(String[] args) {
        InsertIntoBST solution = new InsertIntoBST();

        // Test Case 1: Normal case
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(3);

        TreeNode inserted1 = solution.insertIntoBST(root1, 5);
        solution.printInorder(inserted1); // Expected: 1 2 3 4 5 7
        System.out.println();

        // Test Case 2: Insert into empty tree
        TreeNode root2 = null;
        TreeNode inserted2 = solution.insertIntoBST(root2, 5);
        solution.printInorder(inserted2); // Expected: 5
        System.out.println();

        // Test iterative
        TreeNode root3 = new TreeNode(40);
        root3.left = new TreeNode(20);
        root3.right = new TreeNode(60);
        root3.left.left = new TreeNode(10);
        root3.left.right = new TreeNode(30);
        root3.right.left = new TreeNode(50);
        root3.right.right = new TreeNode(70);

        TreeNode inserted3 = solution.insertIntoBSTIterative(root3, 25);
        solution.printInorder(inserted3); // Expected: 10 20 25 30 40 50 60 70
        System.out.println();
    }
}
