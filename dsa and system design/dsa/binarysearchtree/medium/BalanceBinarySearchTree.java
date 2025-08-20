package binarysearchtree.medium;

import java.util.*;

/**
 * LeetCode 1382: Balance a Binary Search Tree
 * https://leetcode.com/problems/balance-a-binary-search-tree/
 *
 * Description: Given the root of a binary search tree, return a balanced binary
 * search tree with the same node values.
 * If there is more than one answer, return any of them.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - 1 <= Node.val <= 10^5
 * - All Node.val are unique
 *
 * Follow-up:
 * - Can you do it without using extra space for the sorted array?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook
 */
public class BalanceBinarySearchTree {

    static class TreeNode {
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

    // Main optimized solution - Inorder + Build balanced BST
    public TreeNode balanceBST(TreeNode root) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);
        return buildBalancedBST(inorder, 0, inorder.size() - 1);
    }

    private void inorderTraversal(TreeNode node, List<Integer> inorder) {
        if (node == null)
            return;

        inorderTraversal(node.left, inorder);
        inorder.add(node.val);
        inorderTraversal(node.right, inorder);
    }

    private TreeNode buildBalancedBST(List<Integer> inorder, int left, int right) {
        if (left > right)
            return null;

        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(inorder.get(mid));

        root.left = buildBalancedBST(inorder, left, mid - 1);
        root.right = buildBalancedBST(inorder, mid + 1, right);

        return root;
    }

    // Alternative solution - Day-Stout-Warren algorithm (O(1) space)
    public TreeNode balanceBSTDSW(TreeNode root) {
        if (root == null)
            return null;

        // Step 1: Convert to vine (right-skewed tree)
        TreeNode vineHead = new TreeNode(0);
        vineHead.right = root;
        TreeNode current = vineHead;

        while (current.right != null) {
            if (current.right.left != null) {
                rotateRight(current);
            } else {
                current = current.right;
            }
        }

        // Step 2: Convert vine to balanced BST
        int nodeCount = countNodes(vineHead.right);
        int leaves = nodeCount + 1 - Integer.highestOneBit(nodeCount + 1);
        compress(vineHead, leaves);

        nodeCount -= leaves;
        while (nodeCount > 1) {
            nodeCount /= 2;
            compress(vineHead, nodeCount);
        }

        return vineHead.right;
    }

    private void rotateRight(TreeNode node) {
        TreeNode temp = node.right;
        node.right = temp.left;
        temp.left = temp.left.right;
        node.right.right = temp;
    }

    private void rotateLeft(TreeNode node) {
        TreeNode temp = node.right;
        node.right = temp.right;
        temp.right = temp.right.left;
        node.right.left = temp;
    }

    private void compress(TreeNode root, int count) {
        TreeNode current = root;
        for (int i = 0; i < count; i++) {
            rotateLeft(current);
            current = current.right;
        }
    }

    private int countNodes(TreeNode root) {
        int count = 0;
        while (root != null) {
            count++;
            root = root.right;
        }
        return count;
    }

    public static void main(String[] args) {
        BalanceBinarySearchTree solution = new BalanceBinarySearchTree();

        TreeNode root = new TreeNode(1);
        root.right = new TreeNode(2);
        root.right.right = new TreeNode(3);
        root.right.right.right = new TreeNode(4);

        TreeNode balanced = solution.balanceBST(root);
        System.out.println("Tree balanced successfully");
    }
}
