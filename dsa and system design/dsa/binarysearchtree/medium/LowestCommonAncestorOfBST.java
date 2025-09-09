package binarysearchtree.medium;

/**
 * LeetCode 235: Lowest Common Ancestor of a Binary Search Tree
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/
 *
 * Description: Given a binary search tree (BST), find the lowest common
 * ancestor (LCA) of two given nodes in the BST.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^5]
 * - -10^9 <= Node.val <= 10^9
 * - All Node.val are unique
 * - p != q
 * - p and q will exist in the BST
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * 
 * Time Complexity: O(h)
 * Space Complexity: O(h) for recursive, O(1) for iterative
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class LowestCommonAncestorOfBST {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    // Main optimized solution - Recursive
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        } else if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        } else {
            return root;
        }
    }

    // Alternative solution - Iterative
    public TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        while (root != null) {
            if (p.val < root.val && q.val < root.val) {
                root = root.left;
            } else if (p.val > root.val && q.val > root.val) {
                root = root.right;
            } else {
                return root;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        LowestCommonAncestorOfBST solution = new LowestCommonAncestorOfBST();

        TreeNode root = new TreeNode(6);
        root.left = new TreeNode(2);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);
        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);

        TreeNode result = solution.lowestCommonAncestor(root, root.left, root.left.right);
        System.out.println(result.val); // Expected: 2
    }
}
