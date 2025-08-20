package trees.medium;

import java.util.*;

/**
 * LeetCode 1120: Maximum Average Subtree
 * https://leetcode.com/problems/maximum-average-subtree/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return the maximum average
 * value of a subtree of that tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - 0 <= Node.val <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you find the subtree with maximum average?
 * 2. Can you find all subtrees with average above threshold?
 * 3. Can you handle weighted nodes?
 */
public class FindMaximumAverageSubtree {

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

    private double maxAverage = 0.0;

    // Approach 1: Post-order DFS
    public double maximumAverageSubtree(TreeNode root) {
        maxAverage = 0.0;
        calculateSubtreeInfo(root);
        return maxAverage;
    }

    private int[] calculateSubtreeInfo(TreeNode node) {
        if (node == null)
            return new int[] { 0, 0 }; // {sum, count}

        int[] left = calculateSubtreeInfo(node.left);
        int[] right = calculateSubtreeInfo(node.right);

        int sum = node.val + left[0] + right[0];
        int count = 1 + left[1] + right[1];

        double average = (double) sum / count;
        maxAverage = Math.max(maxAverage, average);

        return new int[] { sum, count };
    }

    // Follow-up 1: Find the actual subtree with maximum average
    private TreeNode maxAverageSubtree = null;

    public TreeNode findMaximumAverageSubtree(TreeNode root) {
        maxAverage = 0.0;
        maxAverageSubtree = null;
        findMaxSubtreeHelper(root);
        return maxAverageSubtree;
    }

    private int[] findMaxSubtreeHelper(TreeNode node) {
        if (node == null)
            return new int[] { 0, 0 };

        int[] left = findMaxSubtreeHelper(node.left);
        int[] right = findMaxSubtreeHelper(node.right);

        int sum = node.val + left[0] + right[0];
        int count = 1 + left[1] + right[1];

        double average = (double) sum / count;
        if (average > maxAverage) {
            maxAverage = average;
            maxAverageSubtree = node;
        }

        return new int[] { sum, count };
    }

    // Follow-up 2: Find all subtrees with average above threshold
    public List<TreeNode> findSubtreesAboveThreshold(TreeNode root, double threshold) {
        List<TreeNode> result = new ArrayList<>();
        findSubtreesAboveThresholdHelper(root, threshold, result);
        return result;
    }

    private int[] findSubtreesAboveThresholdHelper(TreeNode node, double threshold, List<TreeNode> result) {
        if (node == null)
            return new int[] { 0, 0 };

        int[] left = findSubtreesAboveThresholdHelper(node.left, threshold, result);
        int[] right = findSubtreesAboveThresholdHelper(node.right, threshold, result);

        int sum = node.val + left[0] + right[0];
        int count = 1 + left[1] + right[1];

        double average = (double) sum / count;
        if (average >= threshold) {
            result.add(node);
        }

        return new int[] { sum, count };
    }

    // Follow-up 3: Handle weighted nodes
    public double maximumWeightedAverageSubtree(TreeNode root, Map<TreeNode, Integer> weights) {
        return findMaxWeightedHelper(root, weights)[2];
    }

    private double[] findMaxWeightedHelper(TreeNode node, Map<TreeNode, Integer> weights) {
        if (node == null)
            return new double[] { 0, 0, 0 }; // {sum, totalWeight, maxAverage}

        double[] left = findMaxWeightedHelper(node.left, weights);
        double[] right = findMaxWeightedHelper(node.right, weights);

        int nodeWeight = weights.getOrDefault(node, 1);
        double sum = node.val + left[0] + right[0];
        double totalWeight = nodeWeight + left[1] + right[1];

        double average = sum / totalWeight;
        double maxAvg = Math.max(average, Math.max(left[2], right[2]));

        return new double[] { sum, totalWeight, maxAvg };
    }

    // Additional: Get statistics for all subtrees
    public Map<TreeNode, Double> getAllSubtreeAverages(TreeNode root) {
        Map<TreeNode, Double> averages = new HashMap<>();
        getAllAveragesHelper(root, averages);
        return averages;
    }

    private int[] getAllAveragesHelper(TreeNode node, Map<TreeNode, Double> averages) {
        if (node == null)
            return new int[] { 0, 0 };

        int[] left = getAllAveragesHelper(node.left, averages);
        int[] right = getAllAveragesHelper(node.right, averages);

        int sum = node.val + left[0] + right[0];
        int count = 1 + left[1] + right[1];

        double average = (double) sum / count;
        averages.put(node, average);

        return new int[] { sum, count };
    }

    // Additional: Find minimum average subtree
    public double minimumAverageSubtree(TreeNode root) {
        return findMinAverageHelper(root)[2];
    }

    private double[] findMinAverageHelper(TreeNode node) {
        if (node == null)
            return new double[] { 0, 0, Double.MAX_VALUE }; // {sum, count, minAverage}

        double[] left = findMinAverageHelper(node.left);
        double[] right = findMinAverageHelper(node.right);

        double sum = node.val + left[0] + right[0];
        double count = 1 + left[1] + right[1];

        double average = sum / count;
        double minAvg = average;

        if (node.left != null)
            minAvg = Math.min(minAvg, left[2]);
        if (node.right != null)
            minAvg = Math.min(minAvg, right[2]);

        return new double[] { sum, count, minAvg };
    }

    // Helper: Print subtree rooted at node
    private void printSubtree(TreeNode root, String prefix, boolean isLast) {
        if (root == null)
            return;

        System.out.println(prefix + (isLast ? "└── " : "├── ") + root.val);

        if (root.left != null || root.right != null) {
            if (root.left != null) {
                printSubtree(root.left, prefix + (isLast ? "    " : "│   "), root.right == null);
            }
            if (root.right != null) {
                printSubtree(root.right, prefix + (isLast ? "    " : "│   "), true);
            }
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumAverageSubtree solution = new FindMaximumAverageSubtree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(5);
        root1.left = new TreeNode(6);
        root1.right = new TreeNode(1);

        System.out.println("Test 1 - Basic tree:");
        System.out.printf("Maximum average: %.5f%n", solution.maximumAverageSubtree(root1));

        TreeNode maxSubtree = solution.findMaximumAverageSubtree(root1);
        System.out.println("Maximum average subtree root: " + maxSubtree.val);

        // Test case 2: Complex tree
        TreeNode root2 = new TreeNode(0);
        root2.left = new TreeNode(0);
        root2.right = new TreeNode(1);

        // Rebuild properly
        root2 = new TreeNode(0);
        root2.right = new TreeNode(1);

        System.out.println("\nTest 2 - Complex tree:");
        System.out.printf("Maximum average: %.5f%n", solution.maximumAverageSubtree(root2));

        // Test case 3: Find subtrees above threshold
        TreeNode root3 = new TreeNode(1);
        root3.left = new TreeNode(2);
        root3.right = new TreeNode(3);
        root3.left.left = new TreeNode(4);
        root3.left.right = new TreeNode(5);

        System.out.println("\nTest 3 - Subtrees above threshold 3.0:");
        List<TreeNode> aboveThreshold = solution.findSubtreesAboveThreshold(root3, 3.0);
        System.out.println("Found " + aboveThreshold.size() + " subtrees");
        for (TreeNode node : aboveThreshold) {
            System.out.println("Root value: " + node.val);
        }

        // Test case 4: All subtree averages
        System.out.println("\nTest 4 - All subtree averages:");
        Map<TreeNode, Double> allAverages = solution.getAllSubtreeAverages(root3);
        for (Map.Entry<TreeNode, Double> entry : allAverages.entrySet()) {
            System.out.printf("Node %d: %.3f%n", entry.getKey().val, entry.getValue());
        }

        // Test case 5: Weighted nodes
        Map<TreeNode, Integer> weights = new HashMap<>();
        weights.put(root3, 2);
        weights.put(root3.left, 1);
        weights.put(root3.right, 3);

        System.out.println("\nTest 5 - Weighted averages:");
        System.out.printf("Weighted maximum: %.5f%n", solution.maximumWeightedAverageSubtree(root3, weights));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(42);
        System.out.printf("Single node: %.1f%n", solution.maximumAverageSubtree(singleNode));

        TreeNode uniform = new TreeNode(5);
        uniform.left = new TreeNode(5);
        uniform.right = new TreeNode(5);
        System.out.printf("Uniform values: %.1f%n", solution.maximumAverageSubtree(uniform));

        System.out.printf("Minimum average: %.5f%n", solution.minimumAverageSubtree(root3));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        double result = solution.maximumAverageSubtree(largeTree);
        long end = System.nanoTime();
        System.out.printf("Large tree result: %.5f in %d ms%n", result, (end - start) / 1_000_000);
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
                current.left = new TreeNode(rand.nextInt(100));
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(rand.nextInt(100));
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
