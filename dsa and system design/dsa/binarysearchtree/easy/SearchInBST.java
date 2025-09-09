package binarysearchtree.easy;

/**
 * LeetCode 700: Search in a Binary Search Tree
 * https://leetcode.com/problems/search-in-a-binary-search-tree/
 *
 * Description: You are given the root of a binary search tree (BST) and an
 * integer val.
 * Find the node in the BST that the node's value equals val and return the
 * subtree rooted with that node.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 5000]
 * - 1 <= Node.val <= 10^7
 * - root is a binary search tree
 * - 1 <= val <= 10^7
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * 
 * Time Complexity: O(h) where h is height
 * Space Complexity: O(h) for recursive, O(1) for iterative
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class SearchInBST {

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
    public TreeNode searchBST(TreeNode root, int val) {
        if (root == null || root.val == val) {
            return root;
        }

        return val < root.val ? searchBST(root.left, val) : searchBST(root.right, val);
    }

    // Alternative solution - Iterative
    public TreeNode searchBSTIterative(TreeNode root, int val) {
        while (root != null && root.val != val) {
            root = val < root.val ? root.left : root.right;
        }
        return root;
    }

    public static void main(String[] args) {
        SearchInBST solution = new SearchInBST();

        // Test Case 1: Normal case
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(3);
        TreeNode result1 = solution.searchBST(root1, 2);
        System.out.println(result1 != null ? result1.val : null); // Expected: 2

        // Test Case 2: Not found
        TreeNode result2 = solution.searchBST(root1, 5);
        System.out.println(result2); // Expected: null

        // Additional test cases...
        System.out.println(solution.searchBSTIterative(root1, 7).val); // Expected: 7
    }
}
