package trees.medium;

import java.util.*;

/**
 * Advanced Variation: Maximum Path Sum Between Two Leaves
 * 
 * Description: Given a binary tree, find the maximum sum path between any two
 * leaf nodes.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^4]
 * - -1000 <= Node.val <= 1000
 * - Tree has at least 2 leaf nodes
 * 
 * Follow-up Questions:
 * 1. Can you find the actual path?
 * 2. Can you handle trees with only one leaf?
 * 3. Can you find k maximum paths?
 */
public class FindMaximumPathSumBetweenTwoLeaves {

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

    // Approach 1: DFS with path sum tracking
    public int maxPathSumBetweenLeaves(TreeNode root) {
        maxSum = Integer.MIN_VALUE;
        maxPathHelper(root);
        return maxSum;
    }

    private int maxPathHelper(TreeNode node) {
        if (node == null)
            return 0;

        if (isLeaf(node))
            return node.val;

        int leftMax = Integer.MIN_VALUE, rightMax = Integer.MIN_VALUE;

        if (node.left != null)
            leftMax = maxPathHelper(node.left);
        if (node.right != null)
            rightMax = maxPathHelper(node.right);

        // If both children exist, we can form a path through current node
        if (node.left != null && node.right != null) {
            maxSum = Math.max(maxSum, leftMax + rightMax + node.val);
            return Math.max(leftMax, rightMax) + node.val;
        }

        // Only one child exists
        return (node.left != null ? leftMax : rightMax) + node.val;
    }

    // Follow-up 1: Find the actual path between leaves
    public List<Integer> findMaxSumPathBetweenLeaves(TreeNode root) {
        List<Integer> maxPath = new ArrayList<>();
        maxSum = Integer.MIN_VALUE;
        findMaxPathHelper(root, maxPath);
        return maxPath;
    }

    private int findMaxPathHelper(TreeNode node, List<Integer> result) {
        if (node == null)
            return 0;

        if (isLeaf(node))
            return node.val;

        List<Integer> leftPath = new ArrayList<>();
        List<Integer> rightPath = new ArrayList<>();

        int leftMax = Integer.MIN_VALUE, rightMax = Integer.MIN_VALUE;

        if (node.left != null)
            leftMax = findMaxPathHelper(node.left, leftPath);
        if (node.right != null)
            rightMax = findMaxPathHelper(node.right, rightPath);

        if (node.left != null && node.right != null) {
            int pathSum = leftMax + rightMax + node.val;
            if (pathSum > maxSum) {
                maxSum = pathSum;
                result.clear();
                Collections.reverse(leftPath);
                result.addAll(leftPath);
                result.add(node.val);
                result.addAll(rightPath);
            }
            return Math.max(leftMax, rightMax) + node.val;
        }

        return (node.left != null ? leftMax : rightMax) + node.val;
    }

    // Follow-up 2: Handle trees with only one leaf
    public int maxPathSumSingleLeaf(TreeNode root) {
        if (root == null)
            return 0;
        if (isLeaf(root))
            return root.val;

        List<Integer> leafPaths = new ArrayList<>();
        findAllLeafPaths(root, 0, leafPaths);

        if (leafPaths.size() < 2)
            return leafPaths.isEmpty() ? 0 : leafPaths.get(0);

        return maxPathSumBetweenLeaves(root);
    }

    // Follow-up 3: Find k maximum paths between leaves
    public List<Integer> kMaxPathsBetweenLeaves(TreeNode root, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        findAllPathSumsBetweenLeaves(root, minHeap, k);
        return new ArrayList<>(minHeap);
    }

    private void findAllPathSumsBetweenLeaves(TreeNode node, PriorityQueue<Integer> heap, int k) {
        if (node == null || isLeaf(node))
            return;

        if (node.left != null && node.right != null) {
            int leftMax = maxPathToLeaf(node.left);
            int rightMax = maxPathToLeaf(node.right);
            int pathSum = leftMax + rightMax + node.val;

            if (heap.size() < k) {
                heap.offer(pathSum);
            } else if (pathSum > heap.peek()) {
                heap.poll();
                heap.offer(pathSum);
            }
        }

        findAllPathSumsBetweenLeaves(node.left, heap, k);
        findAllPathSumsBetweenLeaves(node.right, heap, k);
    }

    // Helper methods
    private boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }

    private int maxPathToLeaf(TreeNode node) {
        if (node == null)
            return 0;
        if (isLeaf(node))
            return node.val;

        int leftMax = Integer.MIN_VALUE, rightMax = Integer.MIN_VALUE;
        if (node.left != null)
            leftMax = maxPathToLeaf(node.left);
        if (node.right != null)
            rightMax = maxPathToLeaf(node.right);

        return node.val + Math.max(leftMax, rightMax);
    }

    private void findAllLeafPaths(TreeNode node, int currentSum, List<Integer> leafPaths) {
        if (node == null)
            return;

        currentSum += node.val;
        if (isLeaf(node)) {
            leafPaths.add(currentSum);
            return;
        }

        findAllLeafPaths(node.left, currentSum, leafPaths);
        findAllLeafPaths(node.right, currentSum, leafPaths);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumPathSumBetweenTwoLeaves solution = new FindMaximumPathSumBetweenTwoLeaves();

        // Test case 1: Basic case with negative values
        TreeNode root1 = new TreeNode(-15);
        root1.left = new TreeNode(5);
        root1.right = new TreeNode(6);
        root1.left.left = new TreeNode(-8);
        root1.left.right = new TreeNode(1);
        root1.right.left = new TreeNode(3);
        root1.right.right = new TreeNode(9);
        root1.right.right.left = new TreeNode(2);
        root1.right.right.right = new TreeNode(-9);
        root1.right.right.left.left = new TreeNode(4);
        root1.right.right.left.right = new TreeNode(-6);

        System.out.println("Test 1 - Basic case: " + solution.maxPathSumBetweenLeaves(root1));

        // Test case 2: Find actual path
        System.out.println("\nTest 2 - Actual path: " + solution.findMaxSumPathBetweenLeaves(root1));

        // Test case 3: Simple tree with two leaves
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(3);
        System.out.println("\nTest 3 - Simple tree: " + solution.maxPathSumBetweenLeaves(root2));

        // Test case 4: K maximum paths
        System.out.println("\nTest 4 - Top 3 paths: " + solution.kMaxPathsBetweenLeaves(root1, 3));

        // Edge cases
        System.out.println("\nEdge cases:");

        // Tree with only root and two leaves
        TreeNode singleLevel = new TreeNode(10);
        singleLevel.left = new TreeNode(5);
        singleLevel.right = new TreeNode(15);
        System.out.println("Single level tree: " + solution.maxPathSumBetweenLeaves(singleLevel));

        // Linear tree (all nodes in a line)
        TreeNode linear = new TreeNode(1);
        linear.left = new TreeNode(2);
        linear.left.left = new TreeNode(3);
        linear.left.left.left = new TreeNode(4);
        System.out.println("Linear tree with single leaf: " + solution.maxPathSumSingleLeaf(linear));

        // Tree with all negative values
        TreeNode allNegative = new TreeNode(-1);
        allNegative.left = new TreeNode(-2);
        allNegative.right = new TreeNode(-3);
        System.out.println("All negative values: " + solution.maxPathSumBetweenLeaves(allNegative));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildCompleteTree(10); // 2^10 - 1 nodes
        long start = System.nanoTime();
        int result = solution.maxPathSumBetweenLeaves(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildCompleteTree(int levels) {
        if (levels <= 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int currentLevel = 1;

        while (currentLevel < levels && !queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode current = queue.poll();
                current.left = new TreeNode((currentLevel * 2) % 10);
                current.right = new TreeNode((currentLevel * 2 + 1) % 10);
                queue.offer(current.left);
                queue.offer(current.right);
            }
            currentLevel++;
        }
        return root;
    }
}
