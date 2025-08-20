package binarysearchtree.medium;

/**
 * LeetCode 285: Inorder Successor in BST
 * https://leetcode.com/problems/inorder-successor-in-bst/
 *
 * Description: Given the root of a binary search tree and a node p in it,
 * return the in-order successor of that node in the BST.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -10^5 <= Node.val <= 10^5
 * - All Nodes will have unique values
 *
 * Follow-up:
 * - What if we need predecessor instead?
 * 
 * Time Complexity: O(h)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class InorderSuccessorInBST {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    // Main optimized solution - Iterative
    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        TreeNode successor = null;

        while (root != null) {
            if (p.val < root.val) {
                successor = root;
                root = root.left;
            } else {
                root = root.right;
            }
        }

        return successor;
    }

    // Alternative solution - Using BST property
    public TreeNode inorderSuccessorBST(TreeNode root, TreeNode p) {
        // Case 1: p has right subtree
        if (p.right != null) {
            TreeNode node = p.right;
            while (node.left != null) {
                node = node.left;
            }
            return node;
        }

        // Case 2: p has no right subtree, find successor from root
        TreeNode successor = null;
        TreeNode current = root;

        while (current != null) {
            if (p.val < current.val) {
                successor = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return successor;
    }

    // Follow-up - Inorder predecessor
    public TreeNode inorderPredecessor(TreeNode root, TreeNode p) {
        TreeNode predecessor = null;

        while (root != null) {
            if (p.val > root.val) {
                predecessor = root;
                root = root.right;
            } else {
                root = root.left;
            }
        }

        return predecessor;
    }

    public static void main(String[] args) {
        InorderSuccessorInBST solution = new InorderSuccessorInBST();

        TreeNode root = new TreeNode(2);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);

        TreeNode p = root.left; // Node with value 1
        TreeNode successor = solution.inorderSuccessor(root, p);
        System.out.println(successor != null ? successor.val : null); // Expected: 2

        TreeNode predecessor = solution.inorderPredecessor(root, root.right);
        System.out.println(predecessor != null ? predecessor.val : null); // Expected: 2
    }
}
