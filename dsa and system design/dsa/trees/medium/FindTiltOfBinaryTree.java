package trees.medium;

import java.util.*;

/**
 * LeetCode 563: Binary Tree Tilt
 * https://leetcode.com/problems/binary-tree-tilt/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return the sum of every tree
 * node's tilt.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - -1000 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find the node with maximum tilt?
 * 2. Can you calculate tilt for each node individually?
 * 3. Can you handle weighted nodes?
 */
public class FindTiltOfBinaryTree {

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

    private int totalTilt = 0;

    // Approach 1: Post-order DFS
    public int findTilt(TreeNode root) {
        totalTilt = 0;
        calculateSum(root);
        return totalTilt;
    }

    private int calculateSum(TreeNode node) {
        if (node == null)
            return 0;

        int leftSum = calculateSum(node.left);
        int rightSum = calculateSum(node.right);

        // Add current node's tilt to total
        totalTilt += Math.abs(leftSum - rightSum);

        // Return sum of current subtree
        return node.val + leftSum + rightSum;
    }

    // Follow-up 1: Find node with maximum tilt
    private int maxTilt = 0;
    private TreeNode maxTiltNode = null;

    public TreeNode findNodeWithMaxTilt(TreeNode root) {
        maxTilt = 0;
        maxTiltNode = null;
        calculateSumWithMaxTilt(root);
        return maxTiltNode;
    }

    private int calculateSumWithMaxTilt(TreeNode node) {
        if (node == null)
            return 0;

        int leftSum = calculateSumWithMaxTilt(node.left);
        int rightSum = calculateSumWithMaxTilt(node.right);

        int currentTilt = Math.abs(leftSum - rightSum);
        if (currentTilt > maxTilt) {
            maxTilt = currentTilt;
            maxTiltNode = node;
        }

        return node.val + leftSum + rightSum;
    }

    // Follow-up 2: Calculate tilt for each node
    public Map<TreeNode, Integer> calculateTiltForEachNode(TreeNode root) {
        Map<TreeNode, Integer> tiltMap = new HashMap<>();
        calculateSumWithTiltMap(root, tiltMap);
        return tiltMap;
    }

    private int calculateSumWithTiltMap(TreeNode node, Map<TreeNode, Integer> tiltMap) {
        if (node == null)
            return 0;

        int leftSum = calculateSumWithTiltMap(node.left, tiltMap);
        int rightSum = calculateSumWithTiltMap(node.right, tiltMap);

        int currentTilt = Math.abs(leftSum - rightSum);
        tiltMap.put(node, currentTilt);

        return node.val + leftSum + rightSum;
    }

    // Follow-up 3: Handle weighted nodes
    public int findWeightedTilt(TreeNode root, Map<TreeNode, Integer> weights) {
        totalTilt = 0;
        calculateWeightedSum(root, weights);
        return totalTilt;
    }

    private int calculateWeightedSum(TreeNode node, Map<TreeNode, Integer> weights) {
        if (node == null)
            return 0;

        int leftSum = calculateWeightedSum(node.left, weights);
        int rightSum = calculateWeightedSum(node.right, weights);

        totalTilt += Math.abs(leftSum - rightSum);

        int weight = weights.getOrDefault(node, 1);
        return node.val * weight + leftSum + rightSum;
    }

    // Additional: Find average tilt
    public double findAverageTilt(TreeNode root) {
        Map<TreeNode, Integer> tiltMap = calculateTiltForEachNode(root);
        if (tiltMap.isEmpty())
            return 0.0;

        int totalTilt = tiltMap.values().stream().mapToInt(Integer::intValue).sum();
        return (double) totalTilt / tiltMap.size();
    }

    // Additional: Count nodes with zero tilt
    public int countNodesWithZeroTilt(TreeNode root) {
        Map<TreeNode, Integer> tiltMap = calculateTiltForEachNode(root);
        int count = 0;
        for (int tilt : tiltMap.values()) {
            if (tilt == 0)
                count++;
        }
        return count;
    }

    // Additional: Find nodes with tilt greater than threshold
    public List<TreeNode> findNodesWithTiltAboveThreshold(TreeNode root, int threshold) {
        Map<TreeNode, Integer> tiltMap = calculateTiltForEachNode(root);
        List<TreeNode> result = new ArrayList<>();

        for (Map.Entry<TreeNode, Integer> entry : tiltMap.entrySet()) {
            if (entry.getValue() > threshold) {
                result.add(entry.getKey());
            }
        }

        return result;
    }

    // Additional: Calculate subtree sums
    public Map<TreeNode, Integer> calculateSubtreeSums(TreeNode root) {
        Map<TreeNode, Integer> sumMap = new HashMap<>();
        calculateSumWithSumMap(root, sumMap);
        return sumMap;
    }

    private int calculateSumWithSumMap(TreeNode node, Map<TreeNode, Integer> sumMap) {
        if (node == null)
            return 0;

        int leftSum = calculateSumWithSumMap(node.left, sumMap);
        int rightSum = calculateSumWithSumMap(node.right, sumMap);

        int totalSum = node.val + leftSum + rightSum;
        sumMap.put(node, totalSum);

        return totalSum;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindTiltOfBinaryTree solution = new FindTiltOfBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Total tilt: " + solution.findTilt(root1));

        TreeNode maxTiltNode = solution.findNodeWithMaxTilt(root1);
        System.out.println("Node with max tilt: " + (maxTiltNode != null ? maxTiltNode.val : "null"));

        // Test case 2: More complex tree
        TreeNode root2 = new TreeNode(4);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(9);
        root2.left.left = new TreeNode(3);
        root2.left.right = new TreeNode(5);
        root2.right.right = new TreeNode(7);

        System.out.println("\nTest 2 - Complex tree:");
        System.out.println("Total tilt: " + solution.findTilt(root2));
        System.out.println("Average tilt: " + String.format("%.2f", solution.findAverageTilt(root2)));
        System.out.println("Nodes with zero tilt: " + solution.countNodesWithZeroTilt(root2));

        // Test case 3: Tilt for each node
        System.out.println("\nTest 3 - Tilt for each node:");
        Map<TreeNode, Integer> tiltMap = solution.calculateTiltForEachNode(root2);
        for (Map.Entry<TreeNode, Integer> entry : tiltMap.entrySet()) {
            System.out.println("Node " + entry.getKey().val + ": tilt = " + entry.getValue());
        }

        // Test case 4: Subtree sums
        System.out.println("\nTest 4 - Subtree sums:");
        Map<TreeNode, Integer> sumMap = solution.calculateSubtreeSums(root2);
        for (Map.Entry<TreeNode, Integer> entry : sumMap.entrySet()) {
            System.out.println("Node " + entry.getKey().val + ": subtree sum = " + entry.getValue());
        }

        // Test case 5: Weighted tilt
        Map<TreeNode, Integer> weights = new HashMap<>();
        weights.put(root2, 2);
        weights.put(root2.left, 1);
        weights.put(root2.right, 3);

        System.out.println("\nTest 5 - Weighted tilt: " + solution.findWeightedTilt(root2, weights));

        // Test case 6: Nodes above threshold
        List<TreeNode> nodesAboveThreshold = solution.findNodesWithTiltAboveThreshold(root2, 2);
        System.out.println("\nTest 6 - Nodes with tilt > 2:");
        for (TreeNode node : nodesAboveThreshold) {
            System.out.print(node.val + " ");
        }
        System.out.println();

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.findTilt(null));

        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node: " + solution.findTilt(singleNode));

        TreeNode balanced = new TreeNode(1);
        balanced.left = new TreeNode(2);
        balanced.right = new TreeNode(2);
        System.out.println("Balanced tree: " + solution.findTilt(balanced));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int result = solution.findTilt(largeTree);
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
                current.left = new TreeNode(rand.nextInt(100) + 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(rand.nextInt(100) + 1);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
