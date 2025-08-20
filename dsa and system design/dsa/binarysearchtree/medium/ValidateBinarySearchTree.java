package binarysearchtree.medium;

/**
 * LeetCode 98: Validate Binary Search Tree
 * https://leetcode.com/problems/validate-binary-search-tree/
 *
 * Description: Given the root of a binary tree, determine if it is a valid
 * binary search tree (BST).
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -2^31 <= Node.val <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it using inorder traversal?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class ValidateBinarySearchTree {

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

    // Main optimized solution - Bounds checking
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

    // Alternative solution - Inorder traversal
    private Integer prev = null;

    public boolean isValidBSTInorder(TreeNode root) {
        prev = null;
        return inorderValidate(root);
    }

    private boolean inorderValidate(TreeNode node) {
        if (node == null)
            return true;

        if (!inorderValidate(node.left))
            return false;

        if (prev != null && node.val <= prev)
            return false;
        prev = node.val;

        return inorderValidate(node.right);
    }

    public static void main(String[] args) {
        ValidateBinarySearchTree solution = new ValidateBinarySearchTree();

        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(3);
        System.out.println(solution.isValidBST(root1)); // Expected: true

        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.right.left = new TreeNode(3);
        root2.right.right = new TreeNode(6);
        System.out.println(solution.isValidBST(root2)); // Expected: false
    }
}
