package trees.hard;

import java.util.*;

/**
 * LeetCode 333: Largest BST Subtree
 * https://leetcode.com/problems/largest-bst-subtree/
 * 
 * Companies: Google, Amazon, Microsoft
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, find the largest subtree which
 * is also a Binary Search Tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - -10^4 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you return the actual BST subtree?
 * 2. Can you count all BST subtrees?
 * 3. Can you find the BST with maximum sum?
 */
public class FindLargestBSTSubtree {

    public static class TreeNode {
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

    private static class BSTInfo {
        boolean isBST;
        int size;
        int min;
        int max;
        long sum;
        TreeNode root;

        BSTInfo(boolean isBST, int size, int min, int max, long sum, TreeNode root) {
            this.isBST = isBST;
            this.size = size;
            this.min = min;
            this.max = max;
            this.sum = sum;
            this.root = root;
        }
    }

    // Approach 1: Bottom-up DFS (O(n) time)
    public int largestBSTSubtree(TreeNode root) {
        return largestBSTHelper(root).size;
    }

    private BSTInfo largestBSTHelper(TreeNode node) {
        if (node == null) {
            return new BSTInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, null);
        }

        BSTInfo left = largestBSTHelper(node.left);
        BSTInfo right = largestBSTHelper(node.right);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            int size = left.size + right.size + 1;
            int min = left.size == 0 ? node.val : left.min;
            int max = right.size == 0 ? node.val : right.max;
            long sum = left.sum + right.sum + node.val;
            return new BSTInfo(true, size, min, max, sum, node);
        } else {
            int maxSize = Math.max(left.size, right.size);
            TreeNode maxRoot = left.size > right.size ? left.root : right.root;
            return new BSTInfo(false, maxSize, 0, 0, 0, maxRoot);
        }
    }

    // Follow-up 1: Return the actual BST subtree
    public TreeNode findLargestBSTSubtree(TreeNode root) {
        return largestBSTHelper(root).root;
    }

    // Follow-up 2: Count all BST subtrees
    public int countBSTSubtrees(TreeNode root) {
        return countBSTHelper(root).isBST ? countBSTHelper(root).size : 0;
    }

    private BSTInfo countBSTHelper(TreeNode node) {
        if (node == null) {
            return new BSTInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, null);
        }

        BSTInfo left = countBSTHelper(node.left);
        BSTInfo right = countBSTHelper(node.right);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            int count = 1;
            if (node.left != null)
                count += left.size;
            if (node.right != null)
                count += right.size;
            return new BSTInfo(true, count,
                    left.size == 0 ? node.val : left.min,
                    right.size == 0 ? node.val : right.max, 0, node);
        } else {
            int totalCount = 0;
            if (left.isBST)
                totalCount += left.size;
            if (right.isBST)
                totalCount += right.size;
            return new BSTInfo(false, totalCount, 0, 0, 0, null);
        }
    }

    // Follow-up 3: Find BST with maximum sum
    public long maxSumBSTSubtree(TreeNode root) {
        return maxSumBSTHelper(root).sum;
    }

    private BSTInfo maxSumBSTHelper(TreeNode node) {
        if (node == null) {
            return new BSTInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, null);
        }

        BSTInfo left = maxSumBSTHelper(node.left);
        BSTInfo right = maxSumBSTHelper(node.right);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            long sum = left.sum + right.sum + node.val;
            return new BSTInfo(true, left.size + right.size + 1,
                    left.size == 0 ? node.val : left.min,
                    right.size == 0 ? node.val : right.max, sum, node);
        } else {
            long maxSum = Math.max(left.sum, right.sum);
            return new BSTInfo(false, 0, 0, 0, maxSum, null);
        }
    }

    // Helper: Validate BST
    private boolean isValidBST(TreeNode root, long min, long max) {
        if (root == null)
            return true;
        if (root.val <= min || root.val >= max)
            return false;
        return isValidBST(root.left, min, root.val) && isValidBST(root.right, root.val, max);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindLargestBSTSubtree solution = new FindLargestBSTSubtree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(10);
        root1.left = new TreeNode(5);
        root1.right = new TreeNode(15);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(8);
        root1.right.right = new TreeNode(7);

        System.out.println("Test 1 - Largest BST size: " + solution.largestBSTSubtree(root1));
        TreeNode largestBST = solution.findLargestBSTSubtree(root1);
        System.out.println("Largest BST root: " + (largestBST != null ? largestBST.val : "null"));

        // Test case 2: Entire tree is BST
        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(3);
        root2.right = new TreeNode(7);
        root2.left.left = new TreeNode(2);
        root2.left.right = new TreeNode(4);
        root2.right.left = new TreeNode(6);
        root2.right.right = new TreeNode(8);

        System.out.println("\nTest 2 - Entire tree BST: " + solution.largestBSTSubtree(root2));

        // Test case 3: Count BST subtrees
        System.out.println("\nTest 3 - Count BST subtrees: " + solution.countBSTSubtrees(root1));

        // Test case 4: Maximum sum BST
        System.out.println("\nTest 4 - Maximum sum BST: " + solution.maxSumBSTSubtree(root1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Null root: " + solution.largestBSTSubtree(null));

        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node: " + solution.largestBSTSubtree(singleNode));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);
        long start = System.nanoTime();
        int result = solution.largestBSTSubtree(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;
        TreeNode root = new TreeNode(500);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();
            if (count < nodes) {
                current.left = new TreeNode(count % 1000);
                queue.offer(current.left);
                count++;
            }
            if (count < nodes) {
                current.right = new TreeNode(count % 1000 + 500);
                queue.offer(current.right);
                count++;
            }
        }
        return root;
    }
}
