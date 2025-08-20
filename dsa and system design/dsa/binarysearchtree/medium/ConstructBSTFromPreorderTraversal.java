package binarysearchtree.medium;

import java.util.*;

/**
 * LeetCode 1008: Construct Binary Search Tree from Preorder Traversal
 * https://leetcode.com/problems/construct-binary-search-tree-from-preorder-traversal/
 *
 * Description: Given an array of integers preorder, which represents the
 * preorder traversal of a BST,
 * construct the tree and return its root.
 * 
 * Constraints:
 * - 1 <= preorder.length <= 100
 * - 1 <= preorder[i] <= 1000
 * - All the values of preorder are unique
 *
 * Follow-up:
 * - Can you do it without sorting?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class ConstructBSTFromPreorderTraversal {

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

    private int index = 0;

    // Main optimized solution - Using bounds
    public TreeNode bstFromPreorder(int[] preorder) {
        index = 0;
        return buildBST(preorder, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode buildBST(int[] preorder, int min, int max) {
        if (index >= preorder.length)
            return null;

        int val = preorder[index];
        if (val < min || val > max)
            return null;

        index++;
        TreeNode root = new TreeNode(val);
        root.left = buildBST(preorder, min, val);
        root.right = buildBST(preorder, val, max);

        return root;
    }

    // Alternative solution - Using stack
    public TreeNode bstFromPreorderStack(int[] preorder) {
        if (preorder.length == 0)
            return null;

        TreeNode root = new TreeNode(preorder[0]);
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        for (int i = 1; i < preorder.length; i++) {
            TreeNode node = new TreeNode(preorder[i]);
            TreeNode parent = null;

            while (!stack.isEmpty() && stack.peek().val < node.val) {
                parent = stack.pop();
            }

            if (parent != null) {
                parent.right = node;
            } else {
                stack.peek().left = node;
            }

            stack.push(node);
        }

        return root;
    }

    public static void main(String[] args) {
        ConstructBSTFromPreorderTraversal solution = new ConstructBSTFromPreorderTraversal();

        int[] preorder1 = { 8, 5, 1, 7, 10, 12 };
        TreeNode root1 = solution.bstFromPreorder(preorder1);
        System.out.println("Root: " + root1.val); // Expected: 8

        int[] preorder2 = { 1, 3 };
        TreeNode root2 = solution.bstFromPreorderStack(preorder2);
        System.out.println("Root: " + root2.val); // Expected: 1
    }
}
