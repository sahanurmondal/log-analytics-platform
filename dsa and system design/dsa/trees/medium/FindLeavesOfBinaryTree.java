package trees.medium;

import java.util.*;

/**
 * LeetCode 366: Find Leaves of Binary Tree
 * https://leetcode.com/problems/find-leaves-of-binary-tree/
 * 
 * Companies: Google, LinkedIn
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, collect a tree's nodes as if
 * you were doing this: collect all the leaf nodes, remove them, and repeat
 * until the tree is empty.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 100]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you collect leaves from specific levels?
 * 2. Can you collect internal nodes instead?
 * 3. Can you handle very large trees efficiently?
 */
public class FindLeavesOfBinaryTree {

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

    // Approach 1: Height-based grouping (optimal)
    public List<List<Integer>> findLeaves(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        getHeight(root, result);
        return result;
    }

    private int getHeight(TreeNode node, List<List<Integer>> result) {
        if (node == null)
            return -1;

        int leftHeight = getHeight(node.left, result);
        int rightHeight = getHeight(node.right, result);
        int height = Math.max(leftHeight, rightHeight) + 1;

        // Ensure result list has enough sublists
        if (result.size() <= height) {
            result.add(new ArrayList<>());
        }

        result.get(height).add(node.val);
        return height;
    }

    // Follow-up 1: Collect leaves from specific levels only
    public List<Integer> findLeavesAtLevel(TreeNode root, int targetLevel) {
        List<List<Integer>> allLeaves = findLeaves(root);
        if (targetLevel >= 0 && targetLevel < allLeaves.size()) {
            return allLeaves.get(targetLevel);
        }
        return new ArrayList<>();
    }

    // Follow-up 2: Collect internal nodes by removal order
    public List<List<Integer>> findInternalNodes(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        getHeightInternal(root, result);
        return result;
    }

    private int getHeightInternal(TreeNode node, List<List<Integer>> result) {
        if (node == null)
            return -1;

        int leftHeight = getHeightInternal(node.left, result);
        int rightHeight = getHeightInternal(node.right, result);
        int height = Math.max(leftHeight, rightHeight) + 1;

        // Only add if it's not a leaf (has at least one child)
        if (node.left != null || node.right != null) {
            if (result.size() <= height) {
                result.add(new ArrayList<>());
            }
            result.get(height).add(node.val);
        }

        return height;
    }

    // Follow-up 3: Iterative approach for large trees
    public List<List<Integer>> findLeavesIterative(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        Map<TreeNode, Integer> heights = new HashMap<>();
        Map<TreeNode, TreeNode> parents = new HashMap<>();

        // Build parent map and calculate heights
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        parents.put(root, null);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();

            if (node.left != null) {
                parents.put(node.left, node);
                stack.push(node.left);
            }
            if (node.right != null) {
                parents.put(node.right, node);
                stack.push(node.right);
            }
        }

        // Calculate heights bottom-up
        calculateHeights(root, heights, result);
        return result;
    }

    private int calculateHeights(TreeNode node, Map<TreeNode, Integer> heights, List<List<Integer>> result) {
        if (node == null)
            return -1;

        if (heights.containsKey(node))
            return heights.get(node);

        int leftHeight = calculateHeights(node.left, heights, result);
        int rightHeight = calculateHeights(node.right, heights, result);
        int height = Math.max(leftHeight, rightHeight) + 1;

        heights.put(node, height);

        if (result.size() <= height) {
            result.add(new ArrayList<>());
        }
        result.get(height).add(node.val);

        return height;
    }

    // Additional: Get removal order with actual nodes
    public List<List<TreeNode>> findLeavesNodes(TreeNode root) {
        List<List<TreeNode>> result = new ArrayList<>();
        getHeightNodes(root, result);
        return result;
    }

    private int getHeightNodes(TreeNode node, List<List<TreeNode>> result) {
        if (node == null)
            return -1;

        int leftHeight = getHeightNodes(node.left, result);
        int rightHeight = getHeightNodes(node.right, result);
        int height = Math.max(leftHeight, rightHeight) + 1;

        if (result.size() <= height) {
            result.add(new ArrayList<>());
        }

        result.get(height).add(node);
        return height;
    }

    // Helper: Simulate actual removal process
    public List<List<Integer>> simulateRemoval(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();

        while (root != null) {
            List<Integer> currentLeaves = new ArrayList<>();
            root = removeLeaves(root, currentLeaves);
            if (!currentLeaves.isEmpty()) {
                result.add(currentLeaves);
            }
        }

        return result;
    }

    private TreeNode removeLeaves(TreeNode node, List<Integer> leaves) {
        if (node == null)
            return null;

        if (node.left == null && node.right == null) {
            leaves.add(node.val);
            return null;
        }

        node.left = removeLeaves(node.left, leaves);
        node.right = removeLeaves(node.right, leaves);

        return node;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindLeavesOfBinaryTree solution = new FindLeavesOfBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Leaves removal order: " + solution.findLeaves(root1));
        System.out.println("Leaves at level 0: " + solution.findLeavesAtLevel(root1, 0));
        System.out.println("Leaves at level 1: " + solution.findLeavesAtLevel(root1, 1));

        // Test case 2: Complex tree
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(3);
        root2.left.left = new TreeNode(4);
        root2.left.right = new TreeNode(5);
        root2.right.right = new TreeNode(6);
        root2.left.left.left = new TreeNode(7);

        System.out.println("\nTest 2 - Complex tree:");
        System.out.println("Leaves removal order: " + solution.findLeaves(root2));
        System.out.println("Internal nodes: " + solution.findInternalNodes(root2));

        // Test case 3: Iterative approach
        System.out.println("\nTest 3 - Iterative approach:");
        System.out.println("Result: " + solution.findLeavesIterative(root2));

        // Test case 4: Simulation
        TreeNode root4 = new TreeNode(1);
        root4.left = new TreeNode(2);
        root4.right = new TreeNode(3);
        root4.left.left = new TreeNode(4);
        root4.left.right = new TreeNode(5);

        System.out.println("\nTest 4 - Simulation:");
        System.out.println("Simulated removal: " + solution.simulateRemoval(root4));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.findLeaves(singleNode));

        TreeNode linear = new TreeNode(1);
        linear.right = new TreeNode(2);
        linear.right.right = new TreeNode(3);
        System.out.println("Linear tree: " + solution.findLeaves(linear));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(100);

        long start = System.nanoTime();
        List<List<Integer>> result = solution.findLeaves(largeTree);
        long end = System.nanoTime();
        System.out.println(
                "Large tree (100 nodes): " + result.size() + " levels in " + (end - start) / 1_000_000 + " ms");
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
