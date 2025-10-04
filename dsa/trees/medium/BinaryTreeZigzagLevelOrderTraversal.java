package trees.medium;

import java.util.*;

/**
 * LeetCode 103: Binary Tree Zigzag Level Order Traversal
 * https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/
 * 
 * Companies: Amazon, Microsoft, Google
 * Frequency: High
 *
 * Description: Given the root of a binary tree, return the zigzag level order
 * traversal of its nodes' values.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 2000]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you implement using only one queue?
 * 2. Can you return nodes instead of values?
 * 3. Can you handle trees with duplicate values?
 */
public class BinaryTreeZigzagLevelOrderTraversal {

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

    // Approach 1: BFS with level tracking
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int size = queue.size();
            LinkedList<Integer> level = new LinkedList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (leftToRight) {
                    level.addLast(node.val);
                } else {
                    level.addFirst(node.val);
                }

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            result.add(level);
            leftToRight = !leftToRight;
        }

        return result;
    }

    // Follow-up 1: Using only one queue with delimiter
    public List<List<Integer>> zigzagLevelOrderOneQueue(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        queue.offer(null); // Level delimiter
        boolean leftToRight = true;
        LinkedList<Integer> currentLevel = new LinkedList<>();

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (node == null) {
                if (!currentLevel.isEmpty()) {
                    result.add(new ArrayList<>(currentLevel));
                    currentLevel.clear();
                    leftToRight = !leftToRight;
                }
                if (!queue.isEmpty())
                    queue.offer(null);
            } else {
                if (leftToRight) {
                    currentLevel.addLast(node.val);
                } else {
                    currentLevel.addFirst(node.val);
                }

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }
        }

        return result;
    }

    // Follow-up 2: Return nodes instead of values
    public List<List<TreeNode>> zigzagLevelOrderNodes(TreeNode root) {
        List<List<TreeNode>> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int size = queue.size();
            LinkedList<TreeNode> level = new LinkedList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (leftToRight) {
                    level.addLast(node);
                } else {
                    level.addFirst(node);
                }

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            result.add(level);
            leftToRight = !leftToRight;
        }

        return result;
    }

    // Follow-up 3: Handle duplicate values with position tracking
    public List<List<String>> zigzagLevelOrderWithPosition(TreeNode root) {
        List<List<String>> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<String> posQueue = new LinkedList<>();
        nodeQueue.offer(root);
        posQueue.offer("0");
        boolean leftToRight = true;

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();
            LinkedList<String> level = new LinkedList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = nodeQueue.poll();
                String pos = posQueue.poll();
                String nodeInfo = node.val + "@" + pos;

                if (leftToRight) {
                    level.addLast(nodeInfo);
                } else {
                    level.addFirst(nodeInfo);
                }

                if (node.left != null) {
                    nodeQueue.offer(node.left);
                    posQueue.offer(pos + "L");
                }
                if (node.right != null) {
                    nodeQueue.offer(node.right);
                    posQueue.offer(pos + "R");
                }
            }

            result.add(level);
            leftToRight = !leftToRight;
        }

        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        BinaryTreeZigzagLevelOrderTraversal solution = new BinaryTreeZigzagLevelOrderTraversal();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(9);
        root1.right = new TreeNode(20);
        root1.right.left = new TreeNode(15);
        root1.right.right = new TreeNode(7);

        System.out.println("Test 1 - Basic zigzag traversal:");
        System.out.println(solution.zigzagLevelOrder(root1));

        // Test case 2: One queue approach
        System.out.println("\nTest 2 - One queue approach:");
        System.out.println(solution.zigzagLevelOrderOneQueue(root1));

        // Test case 3: Return nodes
        System.out.println("\nTest 3 - Return nodes:");
        List<List<TreeNode>> nodeResult = solution.zigzagLevelOrderNodes(root1);
        for (List<TreeNode> level : nodeResult) {
            System.out.print("[");
            for (int i = 0; i < level.size(); i++) {
                System.out.print(level.get(i).val);
                if (i < level.size() - 1)
                    System.out.print(", ");
            }
            System.out.println("]");
        }

        // Test case 4: With position tracking
        System.out.println("\nTest 4 - With position tracking:");
        System.out.println(solution.zigzagLevelOrderWithPosition(root1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.zigzagLevelOrder(null));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.zigzagLevelOrder(singleNode));

        TreeNode skewed = new TreeNode(1);
        skewed.right = new TreeNode(2);
        skewed.right.right = new TreeNode(3);
        System.out.println("Right skewed tree: " + solution.zigzagLevelOrder(skewed));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildCompleteTree(10);
        long start = System.nanoTime();
        List<List<Integer>> result = solution.zigzagLevelOrder(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree levels: " + result.size() + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildCompleteTree(int levels) {
        if (levels <= 0)
            return null;
        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int val = 2;

        for (int level = 1; level < levels; level++) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                node.left = new TreeNode(val++);
                node.right = new TreeNode(val++);
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }
        return root;
    }
}
