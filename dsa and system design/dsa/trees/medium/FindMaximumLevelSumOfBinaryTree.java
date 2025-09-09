package trees.medium;

import java.util.*;

/**
 * LeetCode 1161: Maximum Level Sum of a Binary Tree
 * https://leetcode.com/problems/maximum-level-sum-of-a-binary-tree/
 * 
 * Companies: Amazon, Google
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return the smallest level x
 * such that the sum of all the values of nodes at level x is maximal.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -10^5 <= Node.val <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you find all levels with maximum sum?
 * 2. Can you use DFS instead of BFS?
 * 3. Can you handle weighted levels?
 */
public class FindMaximumLevelSumOfBinaryTree {

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

    // Approach 1: BFS level order traversal
    public int maxLevelSum(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int maxSum = Integer.MIN_VALUE;
        int maxLevel = 1;
        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            long levelSum = 0;
            currentLevel++;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                levelSum += node.val;

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            if (levelSum > maxSum) {
                maxSum = (int) levelSum;
                maxLevel = currentLevel;
            }
        }

        return maxLevel;
    }

    // Follow-up 1: Find all levels with maximum sum
    public List<Integer> findAllMaxLevels(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        long maxSum = Long.MIN_VALUE;
        int currentLevel = 0;
        List<Integer> maxLevels = new ArrayList<>();

        while (!queue.isEmpty()) {
            int size = queue.size();
            long levelSum = 0;
            currentLevel++;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                levelSum += node.val;

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            if (levelSum > maxSum) {
                maxSum = levelSum;
                maxLevels.clear();
                maxLevels.add(currentLevel);
            } else if (levelSum == maxSum) {
                maxLevels.add(currentLevel);
            }
        }

        return maxLevels;
    }

    // Follow-up 2: DFS approach
    public int maxLevelSumDFS(TreeNode root) {
        Map<Integer, Long> levelSums = new HashMap<>();
        dfs(root, 1, levelSums);

        long maxSum = Long.MIN_VALUE;
        int maxLevel = 1;

        for (Map.Entry<Integer, Long> entry : levelSums.entrySet()) {
            if (entry.getValue() > maxSum) {
                maxSum = entry.getValue();
                maxLevel = entry.getKey();
            }
        }

        return maxLevel;
    }

    private void dfs(TreeNode node, int level, Map<Integer, Long> levelSums) {
        if (node == null)
            return;

        levelSums.put(level, levelSums.getOrDefault(level, 0L) + node.val);
        dfs(node.left, level + 1, levelSums);
        dfs(node.right, level + 1, levelSums);
    }

    // Follow-up 3: Handle weighted levels
    public int maxWeightedLevelSum(TreeNode root, Map<Integer, Double> levelWeights) {
        if (root == null)
            return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        double maxWeightedSum = Double.MIN_VALUE;
        int maxLevel = 1;
        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            long levelSum = 0;
            currentLevel++;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                levelSum += node.val;

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            double weight = levelWeights.getOrDefault(currentLevel, 1.0);
            double weightedSum = levelSum * weight;

            if (weightedSum > maxWeightedSum) {
                maxWeightedSum = weightedSum;
                maxLevel = currentLevel;
            }
        }

        return maxLevel;
    }

    // Additional: Get all level sums
    public Map<Integer, Long> getAllLevelSums(TreeNode root) {
        Map<Integer, Long> levelSums = new HashMap<>();
        if (root == null)
            return levelSums;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            long levelSum = 0;
            currentLevel++;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                levelSum += node.val;

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            levelSums.put(currentLevel, levelSum);
        }

        return levelSums;
    }

    // Additional: Find minimum level sum
    public int minLevelSum(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        long minSum = Long.MAX_VALUE;
        int minLevel = 1;
        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            long levelSum = 0;
            currentLevel++;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                levelSum += node.val;

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            if (levelSum < minSum) {
                minSum = levelSum;
                minLevel = currentLevel;
            }
        }

        return minLevel;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumLevelSumOfBinaryTree solution = new FindMaximumLevelSumOfBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(7);
        root1.right = new TreeNode(0);
        root1.left.left = new TreeNode(7);
        root1.left.right = new TreeNode(-8);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Max level sum (BFS): " + solution.maxLevelSum(root1));
        System.out.println("Max level sum (DFS): " + solution.maxLevelSumDFS(root1));
        System.out.println("All max levels: " + solution.findAllMaxLevels(root1));
        System.out.println("Min level sum: " + solution.minLevelSum(root1));

        // Test case 2: Tree with negative values
        TreeNode root2 = new TreeNode(989);
        root2.right = new TreeNode(10250);
        root2.right.left = new TreeNode(98693);
        root2.right.right = new TreeNode(-89388);
        root2.right.right.right = new TreeNode(-32127);

        System.out.println("\nTest 2 - Tree with negative values:");
        System.out.println("Max level sum: " + solution.maxLevelSum(root2));

        Map<Integer, Long> allSums = solution.getAllLevelSums(root2);
        System.out.println("All level sums:");
        for (Map.Entry<Integer, Long> entry : allSums.entrySet()) {
            System.out.println("Level " + entry.getKey() + ": " + entry.getValue());
        }

        // Test case 3: Weighted levels
        Map<Integer, Double> weights = new HashMap<>();
        weights.put(1, 1.0);
        weights.put(2, 2.0);
        weights.put(3, 0.5);

        System.out.println("\nTest 3 - Weighted levels:");
        System.out.println("Max weighted level: " + solution.maxWeightedLevelSum(root2, weights));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(100);
        System.out.println("Single node: " + solution.maxLevelSum(singleNode));

        TreeNode allNegative = new TreeNode(-1);
        allNegative.left = new TreeNode(-2);
        allNegative.right = new TreeNode(-3);
        System.out.println("All negative: " + solution.maxLevelSum(allNegative));

        TreeNode balanced = new TreeNode(1);
        balanced.left = new TreeNode(1);
        balanced.right = new TreeNode(1);
        balanced.left.left = new TreeNode(1);
        balanced.left.right = new TreeNode(1);
        balanced.right.left = new TreeNode(1);
        balanced.right.right = new TreeNode(1);
        System.out.println("Balanced equal sums: " + solution.maxLevelSum(balanced));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(5000);

        long start = System.nanoTime();
        int result = solution.maxLevelSum(largeTree);
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
                current.left = new TreeNode(rand.nextInt(200) - 100);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(rand.nextInt(200) - 100);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
