package trees.hard;

import java.util.*;

/**
 * LeetCode 124: Binary Tree Maximum Path Sum
 * https://leetcode.com/problems/binary-tree-maximum-path-sum/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Very High
 *
 * Description: Given the root of a binary tree, return the maximum path sum of
 * any non-empty path.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 3 * 10^4]
 * - -1000 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find the actual path with maximum sum?
 * 2. Can you handle paths that must go through root?
 * 3. Can you find k maximum path sums?
 */
public class BinaryTreeMaximumPathSum {

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

    private int maxSum = Integer.MIN_VALUE;

    // Approach 1: DFS with global maximum tracking
    public int maxPathSum(TreeNode root) {
        maxSum = Integer.MIN_VALUE;
        maxPathSumHelper(root);
        return maxSum;
    }

    private int maxPathSumHelper(TreeNode node) {
        if (node == null)
            return 0;

        int leftMax = Math.max(0, maxPathSumHelper(node.left));
        int rightMax = Math.max(0, maxPathSumHelper(node.right));

        int currentMax = node.val + leftMax + rightMax;
        maxSum = Math.max(maxSum, currentMax);

        return node.val + Math.max(leftMax, rightMax);
    }

    // Follow-up 1: Find the actual path with maximum sum
    public List<Integer> maxPathSumPath(TreeNode root) {
        List<Integer> maxPath = new ArrayList<>();
        maxSum = Integer.MIN_VALUE;
        findMaxPath(root, maxPath);
        return maxPath;
    }

    private int findMaxPath(TreeNode node, List<Integer> result) {
        if (node == null)
            return 0;

        List<Integer> leftPath = new ArrayList<>();
        List<Integer> rightPath = new ArrayList<>();

        int leftMax = Math.max(0, findMaxPath(node.left, leftPath));
        int rightMax = Math.max(0, findMaxPath(node.right, rightPath));

        int currentMax = node.val + leftMax + rightMax;
        if (currentMax > maxSum) {
            maxSum = currentMax;
            result.clear();
            for (int i = leftPath.size() - 1; i >= 0; i--)
                result.add(leftPath.get(i));
            result.add(node.val);
            result.addAll(rightPath);
        }

        return node.val + Math.max(leftMax, rightMax);
    }

    // Follow-up 2: Paths that must go through root
    public int maxPathSumThroughRoot(TreeNode root) {
        if (root == null)
            return 0;

        int leftMax = maxPathToLeaf(root.left);
        int rightMax = maxPathToLeaf(root.right);

        return root.val + Math.max(0, leftMax) + Math.max(0, rightMax);
    }

    private int maxPathToLeaf(TreeNode node) {
        if (node == null)
            return 0;

        int leftMax = maxPathToLeaf(node.left);
        int rightMax = maxPathToLeaf(node.right);

        return node.val + Math.max(0, Math.max(leftMax, rightMax));
    }

    // Follow-up 3: Find k maximum path sums
    public List<Integer> kMaxPathSums(TreeNode root, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        findAllPathSums(root, minHeap, k);
        return new ArrayList<>(minHeap);
    }

    private int findAllPathSums(TreeNode node, PriorityQueue<Integer> heap, int k) {
        if (node == null)
            return 0;

        int leftMax = Math.max(0, findAllPathSums(node.left, heap, k));
        int rightMax = Math.max(0, findAllPathSums(node.right, heap, k));

        int pathSum = node.val + leftMax + rightMax;

        if (heap.size() < k) {
            heap.offer(pathSum);
        } else if (pathSum > heap.peek()) {
            heap.poll();
            heap.offer(pathSum);
        }

        return node.val + Math.max(leftMax, rightMax);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        BinaryTreeMaximumPathSum solution = new BinaryTreeMaximumPathSum();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        System.out.println("Test 1 - Simple tree [1,2,3] Expected: 6");
        System.out.println("Result: " + solution.maxPathSum(root1));

        // Test case 2: Negative values
        TreeNode root2 = new TreeNode(-10);
        root2.left = new TreeNode(9);
        root2.right = new TreeNode(20);
        root2.right.left = new TreeNode(15);
        root2.right.right = new TreeNode(7);
        System.out.println("\nTest 2 - With negative values:");
        System.out.println("Result: " + solution.maxPathSum(root2));

        // Test case 3: Path through root
        System.out.println("\nTest 3 - Path through root:");
        System.out.println("Result: " + solution.maxPathSumThroughRoot(root2));

        // Test case 4: K maximum paths
        System.out.println("\nTest 4 - Top 3 path sums:");
        System.out.println("Result: " + solution.kMaxPathSums(root2, 3));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node: " + solution.maxPathSum(singleNode));

        TreeNode negativeNode = new TreeNode(-5);
        System.out.println("Single negative node: " + solution.maxPathSum(negativeNode));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildBalancedTree(1000);
        long start = System.nanoTime();
        int result = solution.maxPathSum(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildBalancedTree(int nodes) {
        if (nodes <= 0)
            return null;
        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();
            if (count < nodes) {
                current.left = new TreeNode(count % 10);
                queue.offer(current.left);
                count++;
            }
            if (count < nodes) {
                current.right = new TreeNode(count % 10);
                queue.offer(current.right);
                count++;
            }
        }
        return root;
    }
}
