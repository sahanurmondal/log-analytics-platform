package trees.medium;

import java.util.*;

/**
 * Advanced Variation: Maximum Sum Root to Leaf Path
 * 
 * Description: Given a binary tree, find the maximum sum path from root to any
 * leaf node.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -1000 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find the actual path?
 * 2. Can you find all paths with maximum sum?
 * 3. Can you handle weighted paths?
 */
public class FindMaximumSumOfRootToLeafPath {

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

    // Approach 1: DFS traversal
    public int maxPathSum(TreeNode root) {
        if (root == null)
            return 0;
        return maxPathSumHelper(root);
    }

    private int maxPathSumHelper(TreeNode node) {
        if (node == null)
            return Integer.MIN_VALUE;

        if (isLeaf(node))
            return node.val;

        int leftMax = maxPathSumHelper(node.left);
        int rightMax = maxPathSumHelper(node.right);

        return node.val + Math.max(leftMax, rightMax);
    }

    // Follow-up 1: Find the actual path
    public List<Integer> findMaxSumPath(TreeNode root) {
        List<Integer> maxPath = new ArrayList<>();
        if (root == null)
            return maxPath;

        List<Integer> currentPath = new ArrayList<>();
        findMaxSumPathHelper(root, currentPath, maxPath, Integer.MIN_VALUE);
        return maxPath;
    }

    private int findMaxSumPathHelper(TreeNode node, List<Integer> currentPath,
            List<Integer> maxPath, int maxSum) {
        if (node == null)
            return Integer.MIN_VALUE;

        currentPath.add(node.val);

        if (isLeaf(node)) {
            int currentSum = currentPath.stream().mapToInt(Integer::intValue).sum();
            if (currentSum > maxSum) {
                maxPath.clear();
                maxPath.addAll(currentPath);
                maxSum = currentSum;
            }
        } else {
            int leftSum = findMaxSumPathHelper(node.left, currentPath, maxPath, maxSum);
            int rightSum = findMaxSumPathHelper(node.right, currentPath, maxPath, maxSum);
            maxSum = Math.max(maxSum, Math.max(leftSum, rightSum));
        }

        currentPath.remove(currentPath.size() - 1);
        return maxSum;
    }

    // Follow-up 2: Find all paths with maximum sum
    public List<List<Integer>> findAllMaxSumPaths(TreeNode root) {
        List<List<Integer>> allMaxPaths = new ArrayList<>();
        if (root == null)
            return allMaxPaths;

        int maxSum = maxPathSum(root);
        List<Integer> currentPath = new ArrayList<>();
        collectAllMaxPaths(root, currentPath, allMaxPaths, maxSum, 0);
        return allMaxPaths;
    }

    private void collectAllMaxPaths(TreeNode node, List<Integer> currentPath,
            List<List<Integer>> allMaxPaths, int targetSum, int currentSum) {
        if (node == null)
            return;

        currentPath.add(node.val);
        currentSum += node.val;

        if (isLeaf(node) && currentSum == targetSum) {
            allMaxPaths.add(new ArrayList<>(currentPath));
        } else {
            collectAllMaxPaths(node.left, currentPath, allMaxPaths, targetSum, currentSum);
            collectAllMaxPaths(node.right, currentPath, allMaxPaths, targetSum, currentSum);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    // Follow-up 3: Handle weighted paths
    public int maxWeightedPathSum(TreeNode root, Map<TreeNode, Integer> weights) {
        if (root == null)
            return 0;
        return maxWeightedPathHelper(root, weights);
    }

    private int maxWeightedPathHelper(TreeNode node, Map<TreeNode, Integer> weights) {
        if (node == null)
            return Integer.MIN_VALUE;

        int nodeWeight = weights.getOrDefault(node, 1);
        int weightedValue = node.val * nodeWeight;

        if (isLeaf(node))
            return weightedValue;

        int leftMax = maxWeightedPathHelper(node.left, weights);
        int rightMax = maxWeightedPathHelper(node.right, weights);

        return weightedValue + Math.max(leftMax, rightMax);
    }

    // Additional: Find minimum sum path
    public int minPathSum(TreeNode root) {
        if (root == null)
            return 0;
        return minPathSumHelper(root);
    }

    private int minPathSumHelper(TreeNode node) {
        if (node == null)
            return Integer.MAX_VALUE;

        if (isLeaf(node))
            return node.val;

        int leftMin = minPathSumHelper(node.left);
        int rightMin = minPathSumHelper(node.right);

        return node.val + Math.min(leftMin, rightMin);
    }

    // Additional: Count paths with specific sum
    public int countPathsWithSum(TreeNode root, int targetSum) {
        return countPathsHelper(root, targetSum, 0);
    }

    private int countPathsHelper(TreeNode node, int targetSum, int currentSum) {
        if (node == null)
            return 0;

        currentSum += node.val;

        if (isLeaf(node)) {
            return currentSum == targetSum ? 1 : 0;
        }

        return countPathsHelper(node.left, targetSum, currentSum) +
                countPathsHelper(node.right, targetSum, currentSum);
    }

    // Additional: Get all root-to-leaf sums
    public List<Integer> getAllRootToLeafSums(TreeNode root) {
        List<Integer> sums = new ArrayList<>();
        getAllSumsHelper(root, 0, sums);
        return sums;
    }

    private void getAllSumsHelper(TreeNode node, int currentSum, List<Integer> sums) {
        if (node == null)
            return;

        currentSum += node.val;

        if (isLeaf(node)) {
            sums.add(currentSum);
            return;
        }

        getAllSumsHelper(node.left, currentSum, sums);
        getAllSumsHelper(node.right, currentSum, sums);
    }

    // Helper methods
    private boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumSumOfRootToLeafPath solution = new FindMaximumSumOfRootToLeafPath();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);
        root1.right.right = new TreeNode(6);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Max path sum: " + solution.maxPathSum(root1));
        System.out.println("Max path: " + solution.findMaxSumPath(root1));
        System.out.println("Min path sum: " + solution.minPathSum(root1));

        // Test case 2: Tree with negative values
        TreeNode root2 = new TreeNode(-3);
        root2.left = new TreeNode(9);
        root2.right = new TreeNode(20);
        root2.right.left = new TreeNode(15);
        root2.right.right = new TreeNode(7);

        System.out.println("\nTest 2 - Tree with negative values:");
        System.out.println("Max path sum: " + solution.maxPathSum(root2));
        System.out.println("Max path: " + solution.findMaxSumPath(root2));

        // Test case 3: All max paths
        System.out.println("\nTest 3 - All max sum paths:");
        List<List<Integer>> allMaxPaths = solution.findAllMaxSumPaths(root1);
        for (int i = 0; i < allMaxPaths.size(); i++) {
            System.out.println("Path " + (i + 1) + ": " + allMaxPaths.get(i));
        }

        // Test case 4: Weighted paths
        Map<TreeNode, Integer> weights = new HashMap<>();
        weights.put(root1, 2);
        weights.put(root1.left, 1);
        weights.put(root1.right, 3);

        System.out.println("\nTest 4 - Weighted paths:");
        System.out.println("Weighted max sum: " + solution.maxWeightedPathSum(root1, weights));

        // Test case 5: Count paths with sum
        System.out.println("\nTest 5 - Count paths:");
        System.out.println("Paths with sum 10: " + solution.countPathsWithSum(root1, 10));

        List<Integer> allSums = solution.getAllRootToLeafSums(root1);
        System.out.println("All root-to-leaf sums: " + allSums);

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(42);
        System.out.println("Single node sum: " + solution.maxPathSum(singleNode));
        System.out.println("Single node path: " + solution.findMaxSumPath(singleNode));

        TreeNode linear = new TreeNode(1);
        linear.left = new TreeNode(2);
        linear.left.left = new TreeNode(3);
        System.out.println("Linear tree sum: " + solution.maxPathSum(linear));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int result = solution.maxPathSum(largeTree);
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
        Random rand = new Random(42);

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                current.left = new TreeNode(rand.nextInt(20) - 10);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(rand.nextInt(20) - 10);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
