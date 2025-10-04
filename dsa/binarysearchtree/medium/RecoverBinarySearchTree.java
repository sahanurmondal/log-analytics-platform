package binarysearchtree.medium;

/**
 * LeetCode 99: Recover Binary Search Tree
 * https://leetcode.com/problems/recover-binary-search-tree/
 *
 * Description: You are given the root of a binary search tree (BST), where the
 * values of exactly two nodes
 * of the tree were swapped by mistake. Recover the tree without changing its
 * structure.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 1000]
 * - -2^31 <= Node.val <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it using O(1) space (Morris Traversal)?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h) for recursive, O(1) for Morris
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class RecoverBinarySearchTree {

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

    private TreeNode first = null;
    private TreeNode second = null;
    private TreeNode prev = null;

    // Main optimized solution - Inorder traversal
    public void recoverTree(TreeNode root) {
        first = second = prev = null;
        inorder(root);

        // Swap the values
        int temp = first.val;
        first.val = second.val;
        second.val = temp;
    }

    private void inorder(TreeNode node) {
        if (node == null)
            return;

        inorder(node.left);

        if (prev != null && prev.val > node.val) {
            if (first == null) {
                first = prev;
                second = node;
            } else {
                second = node;
            }
        }
        prev = node;

        inorder(node.right);
    }

    // Follow-up optimization - Morris Traversal O(1) space
    public void recoverTreeMorris(TreeNode root) {
        TreeNode first = null, second = null, prev = null;
        TreeNode current = root;

        while (current != null) {
            if (current.left == null) {
                // Process current node
                if (prev != null && prev.val > current.val) {
                    if (first == null) {
                        first = prev;
                        second = current;
                    } else {
                        second = current;
                    }
                }
                prev = current;
                current = current.right;
            } else {
                // Find inorder predecessor
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    predecessor.right = current;
                    current = current.left;
                } else {
                    predecessor.right = null;

                    // Process current node
                    if (prev != null && prev.val > current.val) {
                        if (first == null) {
                            first = prev;
                            second = current;
                        } else {
                            second = current;
                        }
                    }
                    prev = current;
                    current = current.right;
                }
            }
        }

        // Swap values
        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    public static void main(String[] args) {
        RecoverBinarySearchTree solution = new RecoverBinarySearchTree();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(3);
        root.left.right = new TreeNode(2);

        solution.recoverTree(root);
        System.out.println("Tree recovered successfully");
    }
}
