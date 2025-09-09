package binarysearchtree.easy;

import java.util.*;

/**
 * LeetCode 653: Two Sum IV - Input is a BST
 * https://leetcode.com/problems/two-sum-iv-input-is-a-bst/
 *
 * Description: Given the root of a Binary Search Tree and a target number k,
 * return true if there exist two elements in the BST such that their sum is
 * equal to the given target.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -10^4 <= Node.val <= 10^4
 * - root is guaranteed to be a valid binary search tree
 * - -10^5 <= k <= 10^5
 *
 * Follow-up:
 * - Can you solve it using O(h) space where h is height?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n) for HashSet, O(h) for two-pointer approach
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class TwoSumIVInputIsBST {

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

    // Main optimized solution - HashSet
    public boolean findTarget(TreeNode root, int k) {
        Set<Integer> seen = new HashSet<>();
        return dfs(root, k, seen);
    }

    private boolean dfs(TreeNode node, int k, Set<Integer> seen) {
        if (node == null)
            return false;

        if (seen.contains(k - node.val)) {
            return true;
        }

        seen.add(node.val);

        return dfs(node.left, k, seen) || dfs(node.right, k, seen);
    }

    // Alternative solution - Two pointers with inorder traversal
    public boolean findTargetTwoPointers(TreeNode root, int k) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);

        int left = 0, right = inorder.size() - 1;
        while (left < right) {
            int sum = inorder.get(left) + inorder.get(right);
            if (sum == k) {
                return true;
            } else if (sum < k) {
                left++;
            } else {
                right--;
            }
        }

        return false;
    }

    private void inorderTraversal(TreeNode node, List<Integer> inorder) {
        if (node == null)
            return;

        inorderTraversal(node.left, inorder);
        inorder.add(node.val);
        inorderTraversal(node.right, inorder);
    }

    public static void main(String[] args) {
        TwoSumIVInputIsBST solution = new TwoSumIVInputIsBST();

        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.right = new TreeNode(7);

        System.out.println(solution.findTarget(root, 9)); // Expected: true
        System.out.println(solution.findTarget(root, 28)); // Expected: false
        System.out.println(solution.findTargetTwoPointers(root, 9)); // Expected: true
    }
}
