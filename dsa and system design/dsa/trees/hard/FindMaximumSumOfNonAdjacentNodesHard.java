package trees.hard;

import java.util.*;

/**
 * LeetCode 337: House Robber III
 * https://leetcode.com/problems/house-robber-iii/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: The houses form a binary tree. Find the maximum amount of money
 * you can rob without alerting police (no two directly-connected houses).
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - 0 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you track which nodes were robbed?
 * 2. Can you handle weighted edges between nodes?
 * 3. Can you solve for k-distance constraint?
 */
public class FindMaximumSumOfNonAdjacentNodesHard {

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

    // Approach 1: DFS with memoization (O(n) time)
    public int rob(TreeNode root) {
        int[] result = robHelper(root);
        return Math.max(result[0], result[1]);
    }

    private int[] robHelper(TreeNode node) {
        if (node == null)
            return new int[] { 0, 0 };

        int[] left = robHelper(node.left);
        int[] right = robHelper(node.right);

        // result[0] = max money when current node is NOT robbed
        // result[1] = max money when current node IS robbed
        int notRob = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        int rob = node.val + left[0] + right[0];

        return new int[] { notRob, rob };
    }

    // Follow-up 1: Track which nodes were robbed
    public List<Integer> robWithPath(TreeNode root) {
        Map<TreeNode, Boolean> robbedNodes = new HashMap<>();
        int maxMoney = robWithPathHelper(root, robbedNodes);

        List<Integer> robbedValues = new ArrayList<>();
        collectRobbedNodes(root, robbedNodes, robbedValues);
        return robbedValues;
    }

    private int robWithPathHelper(TreeNode node, Map<TreeNode, Boolean> robbedNodes) {
        if (node == null)
            return 0;

        int[] result = robHelper(node);
        boolean shouldRob = result[1] > result[0];
        robbedNodes.put(node, shouldRob);

        robWithPathHelper(node.left, robbedNodes);
        robWithPathHelper(node.right, robbedNodes);

        return Math.max(result[0], result[1]);
    }

    private void collectRobbedNodes(TreeNode node, Map<TreeNode, Boolean> robbedNodes, List<Integer> result) {
        if (node == null)
            return;

        if (robbedNodes.getOrDefault(node, false)) {
            result.add(node.val);
        }

        collectRobbedNodes(node.left, robbedNodes, result);
        collectRobbedNodes(node.right, robbedNodes, result);
    }

    // Follow-up 2: Handle weighted edges
    public int robWithEdgeWeights(TreeNode root, Map<TreeNode, Map<TreeNode, Integer>> edgeWeights) {
        return robWithEdgeWeightsHelper(root, edgeWeights)[1];
    }

    private int[] robWithEdgeWeightsHelper(TreeNode node, Map<TreeNode, Map<TreeNode, Integer>> edgeWeights) {
        if (node == null)
            return new int[] { 0, 0 };

        int[] left = robWithEdgeWeightsHelper(node.left, edgeWeights);
        int[] right = robWithEdgeWeightsHelper(node.right, edgeWeights);

        int leftWeight = getEdgeWeight(node, node.left, edgeWeights);
        int rightWeight = getEdgeWeight(node, node.right, edgeWeights);

        int notRob = Math.max(left[0], left[1]) * leftWeight + Math.max(right[0], right[1]) * rightWeight;
        int rob = node.val + left[0] * leftWeight + right[0] * rightWeight;

        return new int[] { notRob, rob };
    }

    private int getEdgeWeight(TreeNode parent, TreeNode child, Map<TreeNode, Map<TreeNode, Integer>> edgeWeights) {
        if (parent == null || child == null)
            return 1;
        return edgeWeights.getOrDefault(parent, new HashMap<>()).getOrDefault(child, 1);
    }

    // Follow-up 3: K-distance constraint (can't rob nodes within k distance)
    public int robWithKDistance(TreeNode root, int k) {
        Map<TreeNode, Integer> memo = new HashMap<>();
        return robKDistanceHelper(root, k, memo);
    }

    private int robKDistanceHelper(TreeNode node, int k, Map<TreeNode, Integer> memo) {
        if (node == null)
            return 0;
        if (memo.containsKey(node))
            return memo.get(node);

        // Option 1: Don't rob current node
        int notRob = robKDistanceHelper(node.left, k, memo) + robKDistanceHelper(node.right, k, memo);

        // Option 2: Rob current node (can't rob within k distance)
        int rob = node.val;
        List<TreeNode> kDistanceNodes = getNodesAtDistance(node, k);

        // Add values from nodes beyond k distance
        for (TreeNode distant : kDistanceNodes) {
            rob += robKDistanceHelper(distant, k, memo);
        }

        int result = Math.max(notRob, rob);
        memo.put(node, result);
        return result;
    }

    private List<TreeNode> getNodesAtDistance(TreeNode root, int k) {
        List<TreeNode> result = new ArrayList<>();
        if (root == null || k < 0)
            return result;

        if (k == 0) {
            result.add(root);
            return result;
        }

        result.addAll(getNodesAtDistance(root.left, k - 1));
        result.addAll(getNodesAtDistance(root.right, k - 1));
        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumSumOfNonAdjacentNodesHard solution = new FindMaximumSumOfNonAdjacentNodesHard();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.right = new TreeNode(3);
        root1.right.right = new TreeNode(1);

        System.out.println("Test 1 - Basic rob: " + solution.rob(root1));

        // Test case 2: Track robbed nodes
        System.out.println("\nTest 2 - Robbed nodes: " + solution.robWithPath(root1));

        // Test case 3: Edge weights
        Map<TreeNode, Map<TreeNode, Integer>> edgeWeights = new HashMap<>();
        Map<TreeNode, Integer> root1Edges = new HashMap<>();
        root1Edges.put(root1.left, 2);
        root1Edges.put(root1.right, 3);
        edgeWeights.put(root1, root1Edges);

        System.out.println("\nTest 3 - With edge weights: " + solution.robWithEdgeWeights(root1, edgeWeights));

        // Test case 4: K-distance constraint
        System.out.println("\nTest 4 - K-distance (k=2): " + solution.robWithKDistance(root1, 2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Null root: " + solution.rob(null));

        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node: " + solution.rob(singleNode));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);
        long start = System.nanoTime();
        int result = solution.rob(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;
        TreeNode root = new TreeNode(100);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();
            if (count < nodes) {
                current.left = new TreeNode((count % 100) + 1);
                queue.offer(current.left);
                count++;
            }
            if (count < nodes) {
                current.right = new TreeNode((count % 100) + 1);
                queue.offer(current.right);
                count++;
            }
        }
        return root;
    }
}
