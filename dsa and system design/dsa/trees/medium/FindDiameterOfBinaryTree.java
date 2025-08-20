package trees.medium;

import java.util.*;

/**
 * LeetCode 543: Diameter of Binary Tree
 * https://leetcode.com/problems/diameter-of-binary-tree/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Very High
 *
 * Description: Given the root of a binary tree, return the length of the
 * diameter of the tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you find the actual path of the diameter?
 * 2. Can you find all diameters of equal length?
 * 3. Can you handle weighted edges?
 */
public class FindDiameterOfBinaryTree {

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

    private int maxDiameter = 0;

    // Approach 1: DFS with height tracking
    public int diameterOfBinaryTree(TreeNode root) {
        maxDiameter = 0;
        height(root);
        return maxDiameter;
    }

    private int height(TreeNode node) {
        if (node == null)
            return 0;

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        // Update diameter if path through current node is longer
        maxDiameter = Math.max(maxDiameter, leftHeight + rightHeight);

        return Math.max(leftHeight, rightHeight) + 1;
    }

    // Follow-up 1: Find the actual path of the diameter
    private List<TreeNode> diameterPath = new ArrayList<>();

    public List<TreeNode> findDiameterPath(TreeNode root) {
        maxDiameter = 0;
        diameterPath.clear();
        findDiameterPathHelper(root);
        return new ArrayList<>(diameterPath);
    }

    private int findDiameterPathHelper(TreeNode node) {
        if (node == null)
            return 0;

        int leftHeight = findDiameterPathHelper(node.left);
        int rightHeight = findDiameterPathHelper(node.right);

        if (leftHeight + rightHeight > maxDiameter) {
            maxDiameter = leftHeight + rightHeight;

            // Build the diameter path
            diameterPath.clear();

            // Add left path
            List<TreeNode> leftPath = getPathToLeaf(node.left, leftHeight);
            Collections.reverse(leftPath);
            diameterPath.addAll(leftPath);

            // Add current node
            diameterPath.add(node);

            // Add right path
            List<TreeNode> rightPath = getPathToLeaf(node.right, rightHeight);
            diameterPath.addAll(rightPath);
        }

        return Math.max(leftHeight, rightHeight) + 1;
    }

    private List<TreeNode> getPathToLeaf(TreeNode node, int targetDepth) {
        List<TreeNode> path = new ArrayList<>();
        if (node == null || targetDepth == 0)
            return path;

        path.add(node);
        if (targetDepth == 1)
            return path;

        // Try left subtree first
        List<TreeNode> leftPath = getPathToLeaf(node.left, targetDepth - 1);
        if (!leftPath.isEmpty()) {
            path.addAll(leftPath);
            return path;
        }

        // Try right subtree
        List<TreeNode> rightPath = getPathToLeaf(node.right, targetDepth - 1);
        path.addAll(rightPath);
        return path;
    }

    // Follow-up 2: Find all diameters of equal length
    public List<List<TreeNode>> findAllMaxDiameters(TreeNode root) {
        List<List<TreeNode>> allDiameters = new ArrayList<>();
        maxDiameter = 0;

        // First pass to find max diameter
        height(root);

        // Second pass to collect all paths of max length
        collectAllDiameters(root, allDiameters);
        return allDiameters;
    }

    private int collectAllDiameters(TreeNode node, List<List<TreeNode>> allDiameters) {
        if (node == null)
            return 0;

        int leftHeight = collectAllDiameters(node.left, allDiameters);
        int rightHeight = collectAllDiameters(node.right, allDiameters);

        if (leftHeight + rightHeight == maxDiameter) {
            List<TreeNode> path = new ArrayList<>();

            // Build diameter path through current node
            List<TreeNode> leftPath = getPathToLeaf(node.left, leftHeight);
            Collections.reverse(leftPath);
            path.addAll(leftPath);
            path.add(node);
            List<TreeNode> rightPath = getPathToLeaf(node.right, rightHeight);
            path.addAll(rightPath);

            allDiameters.add(path);
        }

        return Math.max(leftHeight, rightHeight) + 1;
    }

    // Follow-up 3: Handle weighted edges
    public int diameterWithWeights(TreeNode root, Map<TreeNode, Map<TreeNode, Integer>> weights) {
        return diameterWithWeightsHelper(root, weights)[1];
    }

    private int[] diameterWithWeightsHelper(TreeNode node, Map<TreeNode, Map<TreeNode, Integer>> weights) {
        if (node == null)
            return new int[] { 0, 0 }; // {maxDepth, maxDiameter}

        int[] left = diameterWithWeightsHelper(node.left, weights);
        int[] right = diameterWithWeightsHelper(node.right, weights);

        int leftWeight = getWeight(node, node.left, weights);
        int rightWeight = getWeight(node, node.right, weights);

        int maxDepth = Math.max(left[0] + leftWeight, right[0] + rightWeight);
        int currentDiameter = left[0] + leftWeight + right[0] + rightWeight;
        int maxDiameter = Math.max(currentDiameter, Math.max(left[1], right[1]));

        return new int[] { maxDepth, maxDiameter };
    }

    private int getWeight(TreeNode parent, TreeNode child, Map<TreeNode, Map<TreeNode, Integer>> weights) {
        if (parent == null || child == null)
            return 0;
        return weights.getOrDefault(parent, new HashMap<>()).getOrDefault(child, 1);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindDiameterOfBinaryTree solution = new FindDiameterOfBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Diameter: " + solution.diameterOfBinaryTree(root1));

        List<TreeNode> diameterPath = solution.findDiameterPath(root1);
        System.out.print("Diameter path: ");
        for (TreeNode node : diameterPath) {
            System.out.print(node.val + " ");
        }
        System.out.println();

        // Test case 2: Linear tree
        TreeNode linear = new TreeNode(1);
        linear.left = new TreeNode(2);
        linear.left.left = new TreeNode(3);
        linear.left.left.left = new TreeNode(4);

        System.out.println("\nTest 2 - Linear tree:");
        System.out.println("Diameter: " + solution.diameterOfBinaryTree(linear));

        // Test case 3: All max diameters
        System.out.println("\nTest 3 - All max diameter paths:");
        List<List<TreeNode>> allDiameters = solution.findAllMaxDiameters(root1);
        for (int i = 0; i < allDiameters.size(); i++) {
            System.out.print("Path " + (i + 1) + ": ");
            for (TreeNode node : allDiameters.get(i)) {
                System.out.print(node.val + " ");
            }
            System.out.println();
        }

        // Test case 4: Weighted edges
        Map<TreeNode, Map<TreeNode, Integer>> weights = new HashMap<>();
        Map<TreeNode, Integer> root1Weights = new HashMap<>();
        root1Weights.put(root1.left, 3);
        root1Weights.put(root1.right, 2);
        weights.put(root1, root1Weights);

        System.out.println("\nTest 4 - Weighted edges:");
        System.out.println("Weighted diameter: " + solution.diameterWithWeights(root1, weights));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node diameter: " + solution.diameterOfBinaryTree(singleNode));

        TreeNode balanced = new TreeNode(1);
        balanced.left = new TreeNode(2);
        balanced.right = new TreeNode(3);
        balanced.left.left = new TreeNode(4);
        balanced.left.right = new TreeNode(5);
        balanced.right.left = new TreeNode(6);
        balanced.right.right = new TreeNode(7);
        System.out.println("Balanced tree diameter: " + solution.diameterOfBinaryTree(balanced));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int largeDiameter = solution.diameterOfBinaryTree(largeTree);
        long end = System.nanoTime();
        System.out.println(
                "Large tree (1000 nodes) diameter: " + largeDiameter + " in " + (end - start) / 1_000_000 + " ms");
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
