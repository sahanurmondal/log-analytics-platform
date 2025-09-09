package binarysearchtree.easy;

import java.util.*;

/**
 * LeetCode 938: Range Sum of BST
 * https://leetcode.com/problems/range-sum-of-bst/
 *
 * Description: Given the root node of a binary search tree and two integers low
 * and high,
 * return the sum of values of all nodes with a value in the inclusive range
 * [low, high].
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 2 * 10^4]
 * - 1 <= Node.val <= 10^5
 * - 1 <= low <= high <= 10^5
 * - All Node.val are unique
 *
 * Follow-up:
 * - Can you optimize by pruning branches?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook
 */
public class RangeSumOfBST {

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

    // Main optimized solution - DFS with pruning
    public int rangeSumBST(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        if (root.val < low) {
            return rangeSumBST(root.right, low, high);
        }

        if (root.val > high) {
            return rangeSumBST(root.left, low, high);
        }

        return root.val + rangeSumBST(root.left, low, high) + rangeSumBST(root.right, low, high);
    }

    // Alternative solution - Iterative with stack
    public int rangeSumBSTIterative(TreeNode root, int low, int high) {
        int sum = 0;
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (node == null)
                continue;

            if (node.val >= low && node.val <= high) {
                sum += node.val;
            }

            if (node.val > low) {
                stack.push(node.left);
            }

            if (node.val < high) {
                stack.push(node.right);
            }
        }

        return sum;
    }

    public static void main(String[] args) {
        RangeSumOfBST solution = new RangeSumOfBST();

        TreeNode root = new TreeNode(10);
        root.left = new TreeNode(5);
        root.right = new TreeNode(15);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(7);
        root.right.right = new TreeNode(18);

        System.out.println(solution.rangeSumBST(root, 7, 15)); // Expected: 32
        System.out.println(solution.rangeSumBSTIterative(root, 7, 15)); // Expected: 32
    }
}
