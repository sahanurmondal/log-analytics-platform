package binarysearchtree.hard;

/**
 * LeetCode 1373: Maximum Sum BST in Binary Tree
 * https://leetcode.com/problems/maximum-sum-bst-in-binary-tree/
 *
 * Description: Given a binary tree root, return the maximum sum of all keys of
 * any sub-tree which is also a Binary Search Tree (BST).
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 4 * 10^4]
 * - -4 * 10^4 <= Node.val <= 4 * 10^4
 *
 * Follow-up:
 * - Can you do it in one pass?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook
 */
public class MaximumSumBSTInBinaryTree {

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

    private int maxSum = 0;

    // Main optimized solution - Post-order traversal
    public int maxSumBST(TreeNode root) {
        maxSum = 0;
        postorder(root);
        return maxSum;
    }

    // Returns: [isBST, min, max, sum]
    private int[] postorder(TreeNode node) {
        if (node == null) {
            return new int[] { 1, Integer.MAX_VALUE, Integer.MIN_VALUE, 0 };
        }

        int[] left = postorder(node.left);
        int[] right = postorder(node.right);

        // Check if current subtree is BST
        if (left[0] == 1 && right[0] == 1 &&
                node.val > left[2] && node.val < right[1]) {

            int sum = node.val + left[3] + right[3];
            maxSum = Math.max(maxSum, sum);

            int min = node.left == null ? node.val : left[1];
            int max = node.right == null ? node.val : right[2];

            return new int[] { 1, min, max, sum };
        }

        return new int[] { 0, 0, 0, 0 };
    }

    // Alternative solution - Using custom class
    class Result {
        boolean isBST;
        int min, max, sum;

        Result(boolean isBST, int min, int max, int sum) {
            this.isBST = isBST;
            this.min = min;
            this.max = max;
            this.sum = sum;
        }
    }

    public int maxSumBSTAlternative(TreeNode root) {
        maxSum = 0;
        dfs(root);
        return maxSum;
    }

    private Result dfs(TreeNode node) {
        if (node == null) {
            return new Result(true, Integer.MAX_VALUE, Integer.MIN_VALUE, 0);
        }

        Result left = dfs(node.left);
        Result right = dfs(node.right);

        if (left.isBST && right.isBST &&
                node.val > left.max && node.val < right.min) {

            int sum = node.val + left.sum + right.sum;
            maxSum = Math.max(maxSum, sum);

            int min = node.left == null ? node.val : left.min;
            int max = node.right == null ? node.val : right.max;

            return new Result(true, min, max, sum);
        }

        return new Result(false, 0, 0, 0);
    }

    public static void main(String[] args) {
        MaximumSumBSTInBinaryTree solution = new MaximumSumBSTInBinaryTree();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(4);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(2);
        root.right.right = new TreeNode(5);
        root.right.right.left = new TreeNode(4);
        root.right.right.right = new TreeNode(6);

        System.out.println(solution.maxSumBST(root)); // Expected: 20
    }
}
