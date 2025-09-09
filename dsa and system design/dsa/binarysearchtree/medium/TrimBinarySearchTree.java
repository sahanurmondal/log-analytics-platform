package binarysearchtree.medium;

/**
 * LeetCode 669: Trim a Binary Search Tree
 * https://leetcode.com/problems/trim-a-binary-search-tree/
 *
 * Description: Given the root of a binary search tree and the lowest and
 * highest boundaries as low and high,
 * trim the tree so that all its elements lie in [low, high].
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - 0 <= Node.val <= 10^4
 * - The value of each node in the tree is unique
 * - root is guaranteed to be a valid binary search tree
 * - 0 <= low <= high <= 10^4
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook
 */
public class TrimBinarySearchTree {

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

    // Main optimized solution - Recursive
    public TreeNode trimBST(TreeNode root, int low, int high) {
        if (root == null)
            return null;

        if (root.val < low) {
            return trimBST(root.right, low, high);
        }

        if (root.val > high) {
            return trimBST(root.left, low, high);
        }

        root.left = trimBST(root.left, low, high);
        root.right = trimBST(root.right, low, high);

        return root;
    }

    // Alternative solution - Iterative
    public TreeNode trimBSTIterative(TreeNode root, int low, int high) {
        if (root == null)
            return null;

        // Find valid root
        while (root != null && (root.val < low || root.val > high)) {
            if (root.val < low) {
                root = root.right;
            } else {
                root = root.left;
            }
        }

        if (root == null)
            return null;

        // Trim left subtree
        TreeNode node = root;
        while (node.left != null) {
            if (node.left.val < low) {
                node.left = node.left.right;
            } else {
                node = node.left;
            }
        }

        // Trim right subtree
        node = root;
        while (node.right != null) {
            if (node.right.val > high) {
                node.right = node.right.left;
            } else {
                node = node.right;
            }
        }

        return root;
    }

    public static void main(String[] args) {
        TrimBinarySearchTree solution = new TrimBinarySearchTree();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(0);
        root.right = new TreeNode(2);

        TreeNode trimmed = solution.trimBST(root, 1, 2);
        System.out.println("Root after trimming: " + trimmed.val); // Expected: 1
    }
}
