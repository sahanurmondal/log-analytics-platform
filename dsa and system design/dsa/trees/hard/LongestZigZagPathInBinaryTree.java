package trees.hard;

import java.util.*;

/**
 * LeetCode 1372: Longest ZigZag Path in a Binary Tree
 * https://leetcode.com/problems/longest-zigzag-path-in-a-binary-tree/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return the longest ZigZag path
 * contained in that tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 5 * 10^4]
 * - 1 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you find all ZigZag paths of maximum length?
 * 2. Can you count total ZigZag paths?
 * 3. Can you find ZigZag paths with specific patterns?
 */
public class LongestZigZagPathInBinaryTree {

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

    private int maxLength = 0;

    // Approach 1: DFS with direction tracking
    public int longestZigZag(TreeNode root) {
        maxLength = 0;
        dfs(root, true, 0); // Start going left
        dfs(root, false, 0); // Start going right
        return maxLength;
    }

    private void dfs(TreeNode node, boolean isLeft, int length) {
        if (node == null)
            return;

        maxLength = Math.max(maxLength, length);

        if (isLeft) {
            dfs(node.left, false, length + 1); // Continue zigzag
            dfs(node.right, true, 1); // Start new path
        } else {
            dfs(node.right, true, length + 1); // Continue zigzag
            dfs(node.left, false, 1); // Start new path
        }
    }

    // Follow-up 1: Find all ZigZag paths of maximum length
    public List<List<Integer>> findAllMaxZigZagPaths(TreeNode root) {
        List<List<Integer>> allPaths = new ArrayList<>();
        maxLength = 0;

        // First find max length
        longestZigZag(root);

        // Then collect all paths of max length
        List<Integer> currentPath = new ArrayList<>();
        findMaxPaths(root, true, 0, currentPath, allPaths);
        findMaxPaths(root, false, 0, currentPath, allPaths);

        return allPaths;
    }

    private void findMaxPaths(TreeNode node, boolean isLeft, int length,
            List<Integer> currentPath, List<List<Integer>> allPaths) {
        if (node == null)
            return;

        currentPath.add(node.val);

        if (length == maxLength) {
            allPaths.add(new ArrayList<>(currentPath));
        }

        if (isLeft) {
            findMaxPaths(node.left, false, length + 1, currentPath, allPaths);
            findMaxPaths(node.right, true, 1, new ArrayList<>(), allPaths);
        } else {
            findMaxPaths(node.right, true, length + 1, currentPath, allPaths);
            findMaxPaths(node.left, false, 1, new ArrayList<>(), allPaths);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    // Follow-up 2: Count total ZigZag paths
    public int countZigZagPaths(TreeNode root) {
        return countPaths(root, true, 0) + countPaths(root, false, 0);
    }

    private int countPaths(TreeNode node, boolean isLeft, int length) {
        if (node == null)
            return 0;

        int count = 1; // Current path

        if (isLeft) {
            count += countPaths(node.left, false, length + 1);
            count += countPaths(node.right, true, 1);
        } else {
            count += countPaths(node.right, true, length + 1);
            count += countPaths(node.left, false, 1);
        }

        return count;
    }

    // Follow-up 3: Find ZigZag paths with minimum length k
    public List<List<Integer>> findZigZagPathsMinLength(TreeNode root, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();

        findPathsMinLength(root, true, 0, k, currentPath, result);
        findPathsMinLength(root, false, 0, k, currentPath, result);

        return result;
    }

    private void findPathsMinLength(TreeNode node, boolean isLeft, int length, int k,
            List<Integer> currentPath, List<List<Integer>> result) {
        if (node == null)
            return;

        currentPath.add(node.val);

        if (length >= k) {
            result.add(new ArrayList<>(currentPath));
        }

        if (isLeft) {
            findPathsMinLength(node.left, false, length + 1, k, currentPath, result);
            findPathsMinLength(node.right, true, 1, k, new ArrayList<>(), result);
        } else {
            findPathsMinLength(node.right, true, length + 1, k, currentPath, result);
            findPathsMinLength(node.left, false, 1, k, new ArrayList<>(), result);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LongestZigZagPathInBinaryTree solution = new LongestZigZagPathInBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.right = new TreeNode(1);
        root1.right.left = new TreeNode(1);
        root1.right.right = new TreeNode(1);
        root1.right.right.left = new TreeNode(1);
        root1.right.right.right = new TreeNode(1);
        root1.right.right.left.right = new TreeNode(1);

        System.out.println("Test 1 - Longest ZigZag: " + solution.longestZigZag(root1));

        // Test case 2: All max paths
        System.out.println("\nTest 2 - All max ZigZag paths:");
        List<List<Integer>> maxPaths = solution.findAllMaxZigZagPaths(root1);
        for (List<Integer> path : maxPaths) {
            System.out.println(path);
        }

        // Test case 3: Count all paths
        System.out.println("\nTest 3 - Count ZigZag paths: " + solution.countZigZagPaths(root1));

        // Test case 4: Paths with min length
        System.out.println("\nTest 4 - Paths with min length 2:");
        List<List<Integer>> minPaths = solution.findZigZagPathsMinLength(root1, 2);
        for (List<Integer> path : minPaths) {
            System.out.println(path);
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.longestZigZag(singleNode));

        TreeNode linear = new TreeNode(1);
        linear.left = new TreeNode(2);
        linear.left.left = new TreeNode(3);
        System.out.println("Linear tree: " + solution.longestZigZag(linear));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeZigZagTree(1000);
        long start = System.nanoTime();
        int result = solution.longestZigZag(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildLargeZigZagTree(int nodes) {
        if (nodes <= 0)
            return null;
        TreeNode root = new TreeNode(1);
        TreeNode current = root;
        boolean goLeft = true;

        for (int i = 1; i < nodes; i++) {
            if (goLeft) {
                current.left = new TreeNode(i + 1);
                current = current.left;
            } else {
                current.right = new TreeNode(i + 1);
                current = current.right;
            }
            goLeft = !goLeft;
        }
        return root;
    }
}
