package trees.medium;

import trees.TreeNode;
import java.util.*;

/**
 * LeetCode 101: Symmetric Tree
 * https://leetcode.com/problems/symmetric-tree/
 * 
 * Companies: Amazon, Google, Microsoft
 * Frequency: High
 *
 * Description: Given the root of a binary tree, check whether it is a mirror of
 * itself (i.e., symmetric around its center).
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 1000]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively?
 * 2. Can you handle trees with duplicate values?
 */
public class SymmetricTree {

    // Approach 1: Recursive
    public boolean isSymmetric(TreeNode root) {
        if (root == null)
            return true;
        return isSymmetricHelper(root.left, root.right);
    }

    private boolean isSymmetricHelper(TreeNode left, TreeNode right) {
        if (left == null && right == null)
            return true;
        if (left == null || right == null)
            return false;

        return left.val == right.val &&
                isSymmetricHelper(left.left, right.right) &&
                isSymmetricHelper(left.right, right.left);
    }

    // Follow-up 1: Iterative approach
    public boolean isSymmetricIterative(TreeNode root) {
        if (root == null)
            return true;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root.left);
        queue.offer(root.right);

        while (!queue.isEmpty()) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();

            if (left == null && right == null)
                continue;
            if (left == null || right == null)
                return false;
            if (left.val != right.val)
                return false;

            queue.offer(left.left);
            queue.offer(right.right);
            queue.offer(left.right);
            queue.offer(right.left);
        }

        return true;
    }

    public static void main(String[] args) {
        SymmetricTree solution = new SymmetricTree();

        // Test case 1: Basic symmetric tree
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(2);
        root1.left.left = new TreeNode(3);
        root1.left.right = new TreeNode(4);
        root1.right.left = new TreeNode(4);
        root1.right.right = new TreeNode(3);

        System.out.println("Test 1 - Symmetric tree: " + solution.isSymmetric(root1)); // true
        System.out.println("Test 1 - Symmetric tree (iterative): " + solution.isSymmetricIterative(root1)); // true

        // Test case 2: Asymmetric tree
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(2);
        root2.left.right = new TreeNode(3);
        root2.right.right = new TreeNode(3);

        System.out.println("Test 2 - Asymmetric tree: " + solution.isSymmetric(root2)); // false
    }
}
