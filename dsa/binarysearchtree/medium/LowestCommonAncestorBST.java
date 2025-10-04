package binarysearchtree.medium;

import binarysearchtree.TreeNode;
import java.util.*;

/**
 * LeetCode 235: Lowest Common Ancestor of a BST
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/
 *
 * Description:
 * Given a BST, find the lowest common ancestor (LCA) of two given nodes.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^5].
 * - -10^9 <= Node.val <= 10^9
 *
 * Follow-up:
 * - Can you solve it recursively and iteratively?
 */
public class LowestCommonAncestorBST {

    // Main solution: Recursive - O(h) time, O(h) space
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null)
            return null;

        // If both p and q are smaller than root, LCA is in left subtree
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }

        // If both p and q are greater than root, LCA is in right subtree
        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }

        // If p and q are on different sides, current root is LCA
        return root;
    }

    // Iterative solution - O(h) time, O(1) space
    public TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode current = root;

        while (current != null) {
            if (p.val < current.val && q.val < current.val) {
                current = current.left;
            } else if (p.val > current.val && q.val > current.val) {
                current = current.right;
            } else {
                return current;
            }
        }

        return null;
    }

    // Find path from root to node
    public TreeNode lowestCommonAncestorWithPath(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> pathP = findPath(root, p);
        List<TreeNode> pathQ = findPath(root, q);

        TreeNode lca = null;
        int i = 0;

        while (i < pathP.size() && i < pathQ.size() && pathP.get(i) == pathQ.get(i)) {
            lca = pathP.get(i);
            i++;
        }

        return lca;
    }

    private List<TreeNode> findPath(TreeNode root, TreeNode target) {
        List<TreeNode> path = new ArrayList<>();
        TreeNode current = root;

        while (current != null) {
            path.add(current);
            if (current.val == target.val) {
                break;
            } else if (target.val < current.val) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return path;
    }

    // Generic LCA (works for any binary tree, not just BST)
    public TreeNode lowestCommonAncestorGeneric(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = lowestCommonAncestorGeneric(root.left, p, q);
        TreeNode right = lowestCommonAncestorGeneric(root.right, p, q);

        if (left != null && right != null) {
            return root;
        }

        return left != null ? left : right;
    }

    // Find distance between two nodes
    public int findDistance(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode lca = lowestCommonAncestor(root, p, q);
        return getDistance(lca, p) + getDistance(lca, q);
    }

    private int getDistance(TreeNode root, TreeNode target) {
        if (root == null)
            return -1;

        if (root.val == target.val)
            return 0;

        if (target.val < root.val) {
            int leftDist = getDistance(root.left, target);
            return leftDist == -1 ? -1 : leftDist + 1;
        } else {
            int rightDist = getDistance(root.right, target);
            return rightDist == -1 ? -1 : rightDist + 1;
        }
    }

    public static void main(String[] args) {
        LowestCommonAncestorBST solution = new LowestCommonAncestorBST();

        // Test Case 1: Normal case
        TreeNode root1 = new TreeNode(6);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(8);
        root1.left.left = new TreeNode(0);
        root1.left.right = new TreeNode(4);
        root1.left.right.left = new TreeNode(3);
        root1.left.right.right = new TreeNode(5);
        root1.right.left = new TreeNode(7);
        root1.right.right = new TreeNode(9);

        TreeNode p1 = root1.left; // 2
        TreeNode q1 = root1.right; // 8
        System.out.println(solution.lowestCommonAncestor(root1, p1, q1).val); // Expected: 6

        // Test Case 2: One node is ancestor of another
        TreeNode p2 = root1.left; // 2
        TreeNode q2 = root1.left.right; // 4
        System.out.println(solution.lowestCommonAncestor(root1, p2, q2).val); // Expected: 2

        // Test iterative
        System.out.println(solution.lowestCommonAncestorIterative(root1, p1, q1).val); // Expected: 6

        // Test distance
        System.out.println(solution.findDistance(root1, p1, q1)); // Expected: 2
    }
}
