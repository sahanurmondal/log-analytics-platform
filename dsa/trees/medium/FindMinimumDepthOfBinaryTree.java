package trees.medium;

import java.util.*;

/**
 * LeetCode 111: Minimum Depth of Binary Tree
 * https://leetcode.com/problems/minimum-depth-of-binary-tree/
 * 
 * Companies: Amazon, Google
 * Frequency: High
 *
 * Description: Given a binary tree, find its minimum depth. The minimum depth
 * is the number of nodes along the shortest path from the root node down to the
 * nearest leaf node.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^5]
 * - -1000 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find all nodes at minimum depth?
 * 2. Can you use iterative BFS?
 * 3. Can you find paths to all minimum depth leaves?
 */
public class FindMinimumDepthOfBinaryTree {

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

    // Approach 1: Recursive DFS
    public int minDepth(TreeNode root) {
        if (root == null)
            return 0;

        if (root.left == null && root.right == null)
            return 1;

        if (root.left == null)
            return minDepth(root.right) + 1;
        if (root.right == null)
            return minDepth(root.left) + 1;

        return Math.min(minDepth(root.left), minDepth(root.right)) + 1;
    }

    // Follow-up 1: Find all nodes at minimum depth
    public List<Integer> findNodesAtMinDepth(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;

        int minDepth = minDepth(root);
        findNodesAtDepth(root, 1, minDepth, result);
        return result;
    }

    private void findNodesAtDepth(TreeNode node, int currentDepth, int targetDepth, List<Integer> result) {
        if (node == null)
            return;

        if (currentDepth == targetDepth && isLeaf(node)) {
            result.add(node.val);
            return;
        }

        findNodesAtDepth(node.left, currentDepth + 1, targetDepth, result);
        findNodesAtDepth(node.right, currentDepth + 1, targetDepth, result);
    }

    // Follow-up 2: Iterative BFS approach
    public int minDepthBFS(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (isLeaf(node))
                    return depth;

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }
            depth++;
        }

        return depth;
    }

    // Follow-up 3: Find paths to all minimum depth leaves
    public List<List<Integer>> findPathsToMinDepthLeaves(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        int minDepth = minDepth(root);
        List<Integer> currentPath = new ArrayList<>();
        findAllMinDepthPaths(root, 1, minDepth, currentPath, result);
        return result;
    }

    private void findAllMinDepthPaths(TreeNode node, int currentDepth, int targetDepth,
            List<Integer> currentPath, List<List<Integer>> result) {
        if (node == null)
            return;

        currentPath.add(node.val);

        if (currentDepth == targetDepth && isLeaf(node)) {
            result.add(new ArrayList<>(currentPath));
        } else if (currentDepth < targetDepth) {
            findAllMinDepthPaths(node.left, currentDepth + 1, targetDepth, currentPath, result);
            findAllMinDepthPaths(node.right, currentDepth + 1, targetDepth, currentPath, result);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    // Additional: Early termination DFS
    public int minDepthEarlyTermination(TreeNode root) {
        if (root == null)
            return 0;
        return minDepthHelper(root, 1, Integer.MAX_VALUE);
    }

    private int minDepthHelper(TreeNode node, int currentDepth, int minSoFar) {
        if (node == null || currentDepth >= minSoFar)
            return minSoFar;

        if (isLeaf(node))
            return currentDepth;

        minSoFar = Math.min(minSoFar, minDepthHelper(node.left, currentDepth + 1, minSoFar));
        minSoFar = Math.min(minSoFar, minDepthHelper(node.right, currentDepth + 1, minSoFar));

        return minSoFar;
    }

    // Additional: Count nodes at minimum depth
    public int countNodesAtMinDepth(TreeNode root) {
        if (root == null)
            return 0;

        int minDepth = minDepth(root);
        return countNodesAtDepth(root, 1, minDepth);
    }

    private int countNodesAtDepth(TreeNode node, int currentDepth, int targetDepth) {
        if (node == null)
            return 0;

        if (currentDepth == targetDepth && isLeaf(node))
            return 1;
        if (currentDepth >= targetDepth)
            return 0;

        return countNodesAtDepth(node.left, currentDepth + 1, targetDepth) +
                countNodesAtDepth(node.right, currentDepth + 1, targetDepth);
    }

    // Additional: Find minimum depth with specific value
    public int minDepthWithValue(TreeNode root, int target) {
        return minDepthWithValueHelper(root, target, 1);
    }

    private int minDepthWithValueHelper(TreeNode node, int target, int depth) {
        if (node == null)
            return Integer.MAX_VALUE;

        if (node.val == target)
            return depth;

        int leftMin = minDepthWithValueHelper(node.left, target, depth + 1);
        int rightMin = minDepthWithValueHelper(node.right, target, depth + 1);

        return Math.min(leftMin, rightMin);
    }

    // Helper methods
    private boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMinimumDepthOfBinaryTree solution = new FindMinimumDepthOfBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(9);
        root1.right = new TreeNode(20);
        root1.right.left = new TreeNode(15);
        root1.right.right = new TreeNode(7);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Min depth (DFS): " + solution.minDepth(root1));
        System.out.println("Min depth (BFS): " + solution.minDepthBFS(root1));
        System.out.println("Min depth (early termination): " + solution.minDepthEarlyTermination(root1));

        // Test case 2: Nodes at minimum depth
        System.out.println("\nTest 2 - Nodes at min depth:");
        List<Integer> minDepthNodes = solution.findNodesAtMinDepth(root1);
        System.out.println("Nodes: " + minDepthNodes);
        System.out.println("Count: " + solution.countNodesAtMinDepth(root1));

        // Test case 3: Paths to minimum depth leaves
        System.out.println("\nTest 3 - Paths to min depth leaves:");
        List<List<Integer>> paths = solution.findPathsToMinDepthLeaves(root1);
        for (int i = 0; i < paths.size(); i++) {
            System.out.println("Path " + (i + 1) + ": " + paths.get(i));
        }

        // Test case 4: Skewed tree
        TreeNode skewed = new TreeNode(2);
        skewed.right = new TreeNode(3);
        skewed.right.right = new TreeNode(4);
        skewed.right.right.right = new TreeNode(5);
        skewed.right.right.right.right = new TreeNode(6);

        System.out.println("\nTest 4 - Right skewed tree:");
        System.out.println("Min depth: " + solution.minDepth(skewed));

        // Test case 5: Find minimum depth with specific value
        System.out.println("\nTest 5 - Min depth with value 20:");
        System.out.println("Result: " + solution.minDepthWithValue(root1, 20));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.minDepth(null));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.minDepth(singleNode));

        TreeNode leftOnly = new TreeNode(1);
        leftOnly.left = new TreeNode(2);
        System.out.println("Left child only: " + solution.minDepth(leftOnly));

        TreeNode rightOnly = new TreeNode(1);
        rightOnly.right = new TreeNode(2);
        System.out.println("Right child only: " + solution.minDepth(rightOnly));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(10000);

        long start = System.nanoTime();
        int result = solution.minDepthBFS(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                current.left = new TreeNode(count + 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(count + 1);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
