package trees.medium;

import java.util.*;

/**
 * LeetCode 104: Maximum Depth of Binary Tree
 * https://leetcode.com/problems/maximum-depth-of-binary-tree/
 * 
 * Companies: Amazon, Google, Microsoft
 * Frequency: Very High
 *
 * Description: Given the root of a binary tree, return its maximum depth.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you solve iteratively?
 * 2. Can you find all nodes at maximum depth?
 * 3. Can you calculate depth for each node?
 */
public class FindMaximumDepthOfBinaryTree {

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
    public int maxDepth(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    // Follow-up 1: Iterative BFS approach
    public int maxDepthIterative(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            depth++;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }
        }

        return depth;
    }

    // Follow-up 1: Iterative DFS with stack
    public int maxDepthIterativeDFS(TreeNode root) {
        if (root == null)
            return 0;

        Stack<TreeNode> nodeStack = new Stack<>();
        Stack<Integer> depthStack = new Stack<>();

        nodeStack.push(root);
        depthStack.push(1);
        int maxDepth = 0;

        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            int depth = depthStack.pop();

            maxDepth = Math.max(maxDepth, depth);

            if (node.left != null) {
                nodeStack.push(node.left);
                depthStack.push(depth + 1);
            }
            if (node.right != null) {
                nodeStack.push(node.right);
                depthStack.push(depth + 1);
            }
        }

        return maxDepth;
    }

    // Follow-up 2: Find all nodes at maximum depth
    public List<Integer> findNodesAtMaxDepth(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;

        int maxDepth = maxDepth(root);
        findNodesAtDepth(root, 1, maxDepth, result);
        return result;
    }

    private void findNodesAtDepth(TreeNode node, int currentDepth, int targetDepth, List<Integer> result) {
        if (node == null)
            return;

        if (currentDepth == targetDepth) {
            result.add(node.val);
            return;
        }

        findNodesAtDepth(node.left, currentDepth + 1, targetDepth, result);
        findNodesAtDepth(node.right, currentDepth + 1, targetDepth, result);
    }

    // Follow-up 3: Calculate depth for each node
    public Map<TreeNode, Integer> calculateDepthForAllNodes(TreeNode root) {
        Map<TreeNode, Integer> depthMap = new HashMap<>();
        calculateDepthHelper(root, 1, depthMap);
        return depthMap;
    }

    private void calculateDepthHelper(TreeNode node, int depth, Map<TreeNode, Integer> depthMap) {
        if (node == null)
            return;

        depthMap.put(node, depth);
        calculateDepthHelper(node.left, depth + 1, depthMap);
        calculateDepthHelper(node.right, depth + 1, depthMap);
    }

    // Additional: Find minimum depth
    public int minDepth(TreeNode root) {
        if (root == null)
            return 0;

        if (root.left == null && root.right == null)
            return 1;

        int minDepth = Integer.MAX_VALUE;
        if (root.left != null) {
            minDepth = Math.min(minDepth, minDepth(root.left));
        }
        if (root.right != null) {
            minDepth = Math.min(minDepth, minDepth(root.right));
        }

        return minDepth + 1;
    }

    // Additional: Find average depth
    public double averageDepth(TreeNode root) {
        if (root == null)
            return 0.0;

        int[] result = calculateSumAndCount(root, 1);
        return (double) result[0] / result[1];
    }

    private int[] calculateSumAndCount(TreeNode node, int depth) {
        if (node == null)
            return new int[] { 0, 0 };

        int[] left = calculateSumAndCount(node.left, depth + 1);
        int[] right = calculateSumAndCount(node.right, depth + 1);

        return new int[] { depth + left[0] + right[0], 1 + left[1] + right[1] };
    }

    // Additional: Check if tree is balanced
    public boolean isBalanced(TreeNode root) {
        return checkBalance(root) != -1;
    }

    private int checkBalance(TreeNode node) {
        if (node == null)
            return 0;

        int left = checkBalance(node.left);
        if (left == -1)
            return -1;

        int right = checkBalance(node.right);
        if (right == -1)
            return -1;

        if (Math.abs(left - right) > 1)
            return -1;
        return Math.max(left, right) + 1;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumDepthOfBinaryTree solution = new FindMaximumDepthOfBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(9);
        root1.right = new TreeNode(20);
        root1.right.left = new TreeNode(15);
        root1.right.right = new TreeNode(7);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Max depth (recursive): " + solution.maxDepth(root1));
        System.out.println("Max depth (iterative BFS): " + solution.maxDepthIterative(root1));
        System.out.println("Max depth (iterative DFS): " + solution.maxDepthIterativeDFS(root1));
        System.out.println("Min depth: " + solution.minDepth(root1));
        System.out.printf("Average depth: %.2f%n", solution.averageDepth(root1));
        System.out.println("Is balanced: " + solution.isBalanced(root1));

        // Test case 2: Nodes at max depth
        System.out.println("\nTest 2 - Nodes at max depth:");
        List<Integer> maxDepthNodes = solution.findNodesAtMaxDepth(root1);
        System.out.println("Nodes: " + maxDepthNodes);

        // Test case 3: Depth for all nodes
        System.out.println("\nTest 3 - Depth for all nodes:");
        Map<TreeNode, Integer> allDepths = solution.calculateDepthForAllNodes(root1);
        for (Map.Entry<TreeNode, Integer> entry : allDepths.entrySet()) {
            System.out.println("Node " + entry.getKey().val + ": depth " + entry.getValue());
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree depth: " + solution.maxDepth(null));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node depth: " + solution.maxDepth(singleNode));

        TreeNode leftSkewed = new TreeNode(1);
        leftSkewed.left = new TreeNode(2);
        leftSkewed.left.left = new TreeNode(3);
        leftSkewed.left.left.left = new TreeNode(4);
        System.out.println("Left skewed depth: " + solution.maxDepth(leftSkewed));
        System.out.println("Left skewed is balanced: " + solution.isBalanced(leftSkewed));

        TreeNode balanced = new TreeNode(1);
        balanced.left = new TreeNode(2);
        balanced.right = new TreeNode(3);
        balanced.left.left = new TreeNode(4);
        balanced.left.right = new TreeNode(5);
        System.out.println("Balanced tree depth: " + solution.maxDepth(balanced));
        System.out.println("Balanced tree is balanced: " + solution.isBalanced(balanced));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(10000);

        long start = System.nanoTime();
        int depth1 = solution.maxDepth(largeTree);
        long end = System.nanoTime();
        System.out.println("Recursive (10000 nodes): " + depth1 + " in " + (end - start) / 1_000_000 + " ms");

        start = System.nanoTime();
        int depth2 = solution.maxDepthIterative(largeTree);
        end = System.nanoTime();
        System.out.println("Iterative: " + depth2 + " in " + (end - start) / 1_000_000 + " ms");

        System.out.println("Results match: " + (depth1 == depth2));
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
