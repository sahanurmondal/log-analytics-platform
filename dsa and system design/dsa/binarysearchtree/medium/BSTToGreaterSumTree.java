package binarysearchtree.medium;

/**
 * LeetCode 1038: Binary Search Tree to Greater Sum Tree
 * https://leetcode.com/problems/binary-search-tree-to-greater-sum-tree/
 *
 * Description: Given the root of a Binary Search Tree (BST), convert it to a
 * Greater Tree
 * such that every key of the original BST is changed to the original key plus
 * sum of all keys greater than the original key in BST.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 100]
 * - 0 <= Node.val <= 100
 * - All the values in the tree are unique
 *
 * Follow-up:
 * - Can you do it iteratively?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook
 */
public class BSTToGreaterSumTree {

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

    private int sum = 0;

    // Main optimized solution - Reverse inorder traversal
    public TreeNode bstToGst(TreeNode root) {
        sum = 0;
        reverseInorder(root);
        return root;
    }

    private void reverseInorder(TreeNode node) {
        if (node == null)
            return;

        reverseInorder(node.right);
        sum += node.val;
        node.val = sum;
        reverseInorder(node.left);
    }

    // Alternative solution - Iterative
    public TreeNode bstToGstIterative(TreeNode root) {
        int sum = 0;
        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        TreeNode node = root;

        while (!stack.isEmpty() || node != null) {
            while (node != null) {
                stack.push(node);
                node = node.right;
            }

            node = stack.pop();
            sum += node.val;
            node.val = sum;
            node = node.left;
        }

        return root;
    }

    public static void main(String[] args) {
        BSTToGreaterSumTree solution = new BSTToGreaterSumTree();

        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(1);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(2);
        root.left.right.right = new TreeNode(3);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(7);
        root.right.right.right = new TreeNode(8);

        TreeNode result = solution.bstToGst(root);
        System.out.println("Conversion completed");
    }
}
