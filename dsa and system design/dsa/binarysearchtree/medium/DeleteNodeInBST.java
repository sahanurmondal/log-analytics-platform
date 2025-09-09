package binarysearchtree.medium;

/**
 * LeetCode 450: Delete Node in a BST
 * https://leetcode.com/problems/delete-node-in-a-bst/
 *
 * Description: Given a root node reference of a BST and a key, delete the node
 * with the given key in the BST.
 * Return the root node reference (possibly updated) of the BST.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - -10^5 <= Node.val <= 10^5
 * - Each node has a unique value
 * - root is a valid binary search tree
 * - -10^5 <= key <= 10^5
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * 
 * Time Complexity: O(h)
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class DeleteNodeInBST {

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
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null)
            return null;

        if (key < root.val) {
            root.left = deleteNode(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNode(root.right, key);
        } else {
            // Node to be deleted found
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            } else {
                // Node has two children
                TreeNode successor = findMin(root.right);
                root.val = successor.val;
                root.right = deleteNode(root.right, successor.val);
            }
        }

        return root;
    }

    private TreeNode findMin(TreeNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Alternative solution - Using predecessor
    public TreeNode deleteNodePredecessor(TreeNode root, int key) {
        if (root == null)
            return null;

        if (key < root.val) {
            root.left = deleteNodePredecessor(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNodePredecessor(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            } else {
                TreeNode predecessor = findMax(root.left);
                root.val = predecessor.val;
                root.left = deleteNodePredecessor(root.left, predecessor.val);
            }
        }

        return root;
    }

    private TreeNode findMax(TreeNode node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    public static void main(String[] args) {
        DeleteNodeInBST solution = new DeleteNodeInBST();

        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.right = new TreeNode(7);

        TreeNode result = solution.deleteNode(root, 3);
        System.out.println("Node deleted successfully");
    }
}
