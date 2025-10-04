package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * Variation: Lowest Common Ancestor in BST (Iterative)
 * Related to LeetCode 235: Lowest Common Ancestor of a Binary Search Tree
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: High
 *
 * Description:
 * Given a BST, find the lowest common ancestor (LCA) of two given nodes using
 * an iterative approach.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * - All Node.val are unique
 * - p != q
 * - p and q will exist in the BST
 * 
 * Follow-up Questions:
 * 1. What if p or q might not exist in the tree?
 * 2. Can you solve it in O(h) time and O(1) space?
 * 3. What if the tree is not a BST?
 */
public class FindLCAInBSTIterative {

    // Approach 1: Iterative using BST property - O(h) time, O(1) space
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        while (root != null) {
            // Both p and q are in left subtree
            if (p.val < root.val && q.val < root.val) {
                root = root.left;
            }
            // Both p and q are in right subtree
            else if (p.val > root.val && q.val > root.val) {
                root = root.right;
            }
            // We found the split point (LCA)
            else {
                return root;
            }
        }

        return null; // Should not reach here with valid input
    }

    public static void main(String[] args) {
        FindLCAInBSTIterative solution = new FindLCAInBSTIterative();
        // Edge Case 1: Normal case
        TreeNode root1 = new TreeNode(6,
                new TreeNode(2, new TreeNode(0), new TreeNode(4, new TreeNode(3), new TreeNode(5))),
                new TreeNode(8, new TreeNode(7), new TreeNode(9)));
        TreeNode p1 = root1.left; // 2
        TreeNode q1 = root1.right; // 8
        System.out.println(solution.lowestCommonAncestor(root1, p1, q1).val); // 6
        // Edge Case 2: p and q are same
        System.out.println(solution.lowestCommonAncestor(root1, p1, p1).val); // 2
        // Edge Case 3: One node is ancestor of other
        TreeNode p2 = root1.left;
        TreeNode q2 = root1.left.right;
        System.out.println(solution.lowestCommonAncestor(root1, p2, q2).val); // 2
        // Edge Case 4: No tree (null)
        System.out.println(solution.lowestCommonAncestor(null, p1, q1)); // null
    }
}
