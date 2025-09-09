package binarysearchtree.easy;

/**
 * LeetCode 701: Insert into a Binary Search Tree
 * https://leetcode.com/problems/insert-into-a-binary-search-tree/
 *
 * Description: You are given the root node of a binary search tree (BST) and a
 * value to insert into the tree.
 * Return the root node of the BST after the insertion.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - -10^8 <= Node.val <= 10^8
 * - All the values Node.val are unique
 * - -10^8 <= val <= 10^8
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * 
 * Time Complexity: O(h)
 * Space Complexity: O(h) for recursive, O(1) for iterative
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class InsertIntoBST {

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
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else {
            root.right = insertIntoBST(root.right, val);
        }

        return root;
    }

    // Alternative solution - Iterative
    public TreeNode insertIntoBSTIterative(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        TreeNode current = root;
        while (true) {
            if (val < current.val) {
                if (current.left == null) {
                    current.left = new TreeNode(val);
                    break;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new TreeNode(val);
                    break;
                }
                current = current.right;
            }
        }

        return root;
    }

    public static void main(String[] args) {
        InsertIntoBST solution = new InsertIntoBST();

        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        TreeNode result = solution.insertIntoBST(root, 5);
        System.out.println("Inserted successfully"); // BST structure maintained
    }
}
