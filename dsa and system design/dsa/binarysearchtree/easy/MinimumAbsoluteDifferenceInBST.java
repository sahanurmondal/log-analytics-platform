package binarysearchtree.easy;

/**
 * LeetCode 530: Minimum Absolute Difference in BST
 * https://leetcode.com/problems/minimum-absolute-difference-in-bst/
 *
 * Description: Given the root of a Binary Search Tree (BST), return the minimum
 * absolute difference
 * between the values of any two different nodes in the tree.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^4]
 * - 0 <= Node.val <= 10^5
 *
 * Follow-up:
 * - Can you do it in one pass?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook
 */
public class MinimumAbsoluteDifferenceInBST {

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

    private int minDiff = Integer.MAX_VALUE;
    private TreeNode prev = null;

    // Main optimized solution - Inorder traversal
    public int getMinimumDifference(TreeNode root) {
        minDiff = Integer.MAX_VALUE;
        prev = null;
        inorder(root);
        return minDiff;
    }

    private void inorder(TreeNode node) {
        if (node == null)
            return;

        inorder(node.left);

        if (prev != null) {
            minDiff = Math.min(minDiff, node.val - prev.val);
        }
        prev = node;

        inorder(node.right);
    }

    // Alternative solution - Convert to sorted array
    public int getMinimumDifferenceArray(TreeNode root) {
        java.util.List<Integer> values = new java.util.ArrayList<>();
        inorderToList(root, values);

        int minDiff = Integer.MAX_VALUE;
        for (int i = 1; i < values.size(); i++) {
            minDiff = Math.min(minDiff, values.get(i) - values.get(i - 1));
        }

        return minDiff;
    }

    private void inorderToList(TreeNode node, java.util.List<Integer> values) {
        if (node == null)
            return;

        inorderToList(node.left, values);
        values.add(node.val);
        inorderToList(node.right, values);
    }

    public static void main(String[] args) {
        MinimumAbsoluteDifferenceInBST solution = new MinimumAbsoluteDifferenceInBST();

        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        System.out.println(solution.getMinimumDifference(root)); // Expected: 1
    }
}
