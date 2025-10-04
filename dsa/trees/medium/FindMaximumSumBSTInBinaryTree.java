package trees.medium;

import java.util.*;

/**
 * LeetCode 1373: Maximum Sum BST in Binary Tree
 * https://leetcode.com/problems/maximum-sum-bst-in-binary-tree/
 * 
 * Companies: Google, Amazon
 * Frequency: Hard
 *
 * Description: Given a binary tree root, return the maximum sum of all keys of
 * any sub-tree which is also a Binary Search Tree (BST).
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 4 * 10^4]
 * - -4 * 10^4 <= Node.val <= 4 * 10^4
 * 
 * Follow-up Questions:
 * 1. Can you find the actual BST subtree?
 * 2. Can you count all valid BST subtrees?
 * 3. Can you find the largest BST by node count?
 */
public class FindMaximumSumBSTInBinaryTree {

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
        int sum;
        int min;
        int max;
        int size;
        TreeNode root;

        BSTInfo(boolean isBST, int sum, int min, int max, int size, TreeNode root) {
            this.isBST = isBST;
            this.sum = sum;
            this.min = min;
            this.max = max;
            this.size = size;
            this.root = root;
        }
    }

    private int maxSum = 0;

    // Approach 1: Post-order DFS
    public int maxSumBST(TreeNode root) {
        maxSum = 0;
        postOrder(root);
        return maxSum;
    }

    private BSTInfo postOrder(TreeNode node) {
        if (node == null) {
            return new BSTInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, null);
        }

        BSTInfo left = postOrder(node.left);
        BSTInfo right = postOrder(node.right);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            // Current subtree is a BST
            int sum = node.val + left.sum + right.sum;
            maxSum = Math.max(maxSum, sum);

            int min = left.size == 0 ? node.val : left.min;
            int max = right.size == 0 ? node.val : right.max;
            int size = 1 + left.size + right.size;

            return new BSTInfo(true, sum, min, max, size, node);
        } else {
            // Current subtree is not a BST
            return new BSTInfo(false, 0, 0, 0, 0, null);
        }
    }

    // Follow-up 1: Find the actual BST subtree with maximum sum
    private TreeNode maxSumBSTRoot = null;

    public TreeNode findMaxSumBSTSubtree(TreeNode root) {
        maxSum = 0;
        maxSumBSTRoot = null;
        findMaxSumBSTHelper(root);
        return maxSumBSTRoot;
    }

    private BSTInfo findMaxSumBSTHelper(TreeNode node) {
        if (node == null) {
            return new BSTInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, null);
        }

        BSTInfo left = findMaxSumBSTHelper(node.left);
        BSTInfo right = findMaxSumBSTHelper(node.right);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            int sum = node.val + left.sum + right.sum;
            if (sum > maxSum) {
                maxSum = sum;
                maxSumBSTRoot = node;
            }

            int min = left.size == 0 ? node.val : left.min;
            int max = right.size == 0 ? node.val : right.max;
            int size = 1 + left.size + right.size;

            return new BSTInfo(true, sum, min, max, size, node);
        } else {
            return new BSTInfo(false, 0, 0, 0, 0, null);
        }
    }

    // Follow-up 2: Count all valid BST subtrees
    public int countBSTSubtrees(TreeNode root) {
        return countBSTHelper(root).size;
    }

    private BSTInfo countBSTHelper(TreeNode node) {
        if (node == null) {
            return new BSTInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, null);
        }

        BSTInfo left = countBSTHelper(node.left);
        BSTInfo right = countBSTHelper(node.right);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            int sum = node.val + left.sum + right.sum;
            int min = left.size == 0 ? node.val : left.min;
            int max = right.size == 0 ? node.val : right.max;
            int size = 1 + left.size + right.size;

            return new BSTInfo(true, sum, min, max, size, node);
        } else {
            // Return count from left and right subtrees
            int totalCount = 0;
            if (left.isBST && left.size > 0)
                totalCount += left.size;
            if (right.isBST && right.size > 0)
                totalCount += right.size;

            return new BSTInfo(false, 0, 0, 0, totalCount, null);
        }
    }

    // Follow-up 3: Find largest BST by node count
    private int maxBSTSize = 0;
    private TreeNode largestBSTRoot = null;

    public TreeNode findLargestBSTSubtree(TreeNode root) {
        maxBSTSize = 0;
        largestBSTRoot = null;
        findLargestBSTHelper(root);
        return largestBSTRoot;
    }

    private BSTInfo findLargestBSTHelper(TreeNode node) {
        if (node == null) {
            return new BSTInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, null);
        }

        BSTInfo left = findLargestBSTHelper(node.left);
        BSTInfo right = findLargestBSTHelper(node.right);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            int sum = node.val + left.sum + right.sum;
            int min = left.size == 0 ? node.val : left.min;
            int max = right.size == 0 ? node.val : right.max;
            int size = 1 + left.size + right.size;

            if (size > maxBSTSize) {
                maxBSTSize = size;
                largestBSTRoot = node;
            }

            return new BSTInfo(true, sum, min, max, size, node);
        } else {
            return new BSTInfo(false, 0, 0, 0, 0, null);
        }
    }

    // Helper: Get all BST subtrees with their sums
    public List<BSTInfo> getAllBSTSubtrees(TreeNode root) {
        List<BSTInfo> allBSTs = new ArrayList<>();
        getAllBSTHelper(root, allBSTs);
        return allBSTs;
    }

    private BSTInfo getAllBSTHelper(TreeNode node, List<BSTInfo> allBSTs) {
        if (node == null) {
            return new BSTInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, null);
        }

        BSTInfo left = getAllBSTHelper(node.left, allBSTs);
        BSTInfo right = getAllBSTHelper(node.right, allBSTs);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            int sum = node.val + left.sum + right.sum;
            int min = left.size == 0 ? node.val : left.min;
            int max = right.size == 0 ? node.val : right.max;
            int size = 1 + left.size + right.size;

            BSTInfo current = new BSTInfo(true, sum, min, max, size, node);
            allBSTs.add(current);

            return current;
        } else {
            return new BSTInfo(false, 0, 0, 0, 0, null);
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumSumBSTInBinaryTree solution = new FindMaximumSumBSTInBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(4);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(2);
        root1.left.right = new TreeNode(4);
        root1.right.left = new TreeNode(2);
        root1.right.right = new TreeNode(5);
        root1.right.right.left = new TreeNode(4);
        root1.right.right.right = new TreeNode(6);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Max sum BST: " + solution.maxSumBST(root1));

        TreeNode maxSumRoot = solution.findMaxSumBSTSubtree(root1);
        System.out.println("Max sum BST root: " + (maxSumRoot != null ? maxSumRoot.val : "null"));

        // Test case 2: All BST subtrees
        System.out.println("\nTest 2 - All BST subtrees:");
        List<BSTInfo> allBSTs = solution.getAllBSTSubtrees(root1);
        System.out.println("Total BST subtrees: " + allBSTs.size());
        for (BSTInfo bst : allBSTs) {
            System.out.println("Root: " + bst.root.val + ", Sum: " + bst.sum + ", Size: " + bst.size);
        }

        // Test case 3: Count BST subtrees
        System.out.println("\nTest 3 - Count BST subtrees: " + solution.countBSTSubtrees(root1));

        // Test case 4: Largest BST by size
        TreeNode largestRoot = solution.findLargestBSTSubtree(root1);
        System.out.println("\nTest 4 - Largest BST root: " + (largestRoot != null ? largestRoot.val : "null"));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node: " + solution.maxSumBST(singleNode));

        TreeNode invalidBST = new TreeNode(5);
        invalidBST.left = new TreeNode(10);
        invalidBST.right = new TreeNode(3);
        System.out.println("Invalid BST: " + solution.maxSumBST(invalidBST));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int result = solution.maxSumBST(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
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
                current.left = new TreeNode(current.val - (count % 100) - 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(current.val + (count % 100) + 1);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
