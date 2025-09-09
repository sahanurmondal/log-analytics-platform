package binarysearchtree.medium;

import java.util.*;

/**
 * LeetCode 230: Kth Smallest Element in a BST
 * https://leetcode.com/problems/kth-smallest-element-in-a-bst/
 *
 * Description: Given the root of a binary search tree, and an integer k,
 * return the kth smallest value (1-indexed) of all the values of the nodes in
 * the tree.
 * 
 * Constraints:
 * - The number of nodes in the tree is n
 * - 1 <= k <= n <= 10^4
 * - 0 <= Node.val <= 10^4
 *
 * Follow-up:
 * - If the BST is modified often, how would you optimize?
 * 
 * Time Complexity: O(h + k)
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class KthSmallestElementInBST {

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

    // Main optimized solution - Iterative inorder
    public int kthSmallest(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            k--;

            if (k == 0) {
                return current.val;
            }

            current = current.right;
        }

        return -1;
    }

    // Alternative solution - Recursive inorder
    private int count = 0;
    private int result = 0;

    public int kthSmallestRecursive(TreeNode root, int k) {
        count = 0;
        result = 0;
        inorder(root, k);
        return result;
    }

    private void inorder(TreeNode node, int k) {
        if (node == null)
            return;

        inorder(node.left, k);

        count++;
        if (count == k) {
            result = node.val;
            return;
        }

        inorder(node.right, k);
    }

    public static void main(String[] args) {
        KthSmallestElementInBST solution = new KthSmallestElementInBST();

        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(1);
        root.right = new TreeNode(4);
        root.left.right = new TreeNode(2);

        System.out.println(solution.kthSmallest(root, 1)); // Expected: 1
        System.out.println(solution.kthSmallestRecursive(root, 2)); // Expected: 2
    }
}
