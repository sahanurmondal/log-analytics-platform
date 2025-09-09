package trees.hard;

import java.util.*;

/**
 * Advanced Variation: Maximum Width Ramp in Binary Tree
 * 
 * Description: Given a binary tree, find the maximum width ramp where width is
 * defined as the maximum difference between positions of any two nodes at the
 * same level.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 3000]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you find ramps across different levels?
 * 2. Can you find the actual nodes forming the ramp?
 * 3. Can you optimize for very wide trees?
 */
public class MaximumWidthRampInBinaryTree {

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

    // Approach 1: BFS with position tracking
    public int widthOfBinaryTree(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> posQueue = new LinkedList<>();
        nodeQueue.offer(root);
        posQueue.offer(0);
        int maxWidth = 1;

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();
            int start = 0, end = 0;

            for (int i = 0; i < size; i++) {
                TreeNode node = nodeQueue.poll();
                int pos = posQueue.poll();

                if (i == 0)
                    start = pos;
                if (i == size - 1)
                    end = pos;

                if (node.left != null) {
                    nodeQueue.offer(node.left);
                    posQueue.offer(2 * pos);
                }
                if (node.right != null) {
                    nodeQueue.offer(node.right);
                    posQueue.offer(2 * pos + 1);
                }
            }
            maxWidth = Math.max(maxWidth, end - start + 1);
        }
        return maxWidth;
    }

    // Follow-up 1: Find ramps across different levels
    public int maxCrossLevelRamp(TreeNode root) {
        Map<Integer, List<Integer>> levelPositions = new HashMap<>();
        collectLevelPositions(root, 0, 0, levelPositions);

        int maxRamp = 0;
        for (int level1 : levelPositions.keySet()) {
            for (int level2 : levelPositions.keySet()) {
                if (level1 != level2) {
                    List<Integer> pos1 = levelPositions.get(level1);
                    List<Integer> pos2 = levelPositions.get(level2);

                    for (int p1 : pos1) {
                        for (int p2 : pos2) {
                            maxRamp = Math.max(maxRamp, Math.abs(p2 - p1));
                        }
                    }
                }
            }
        }
        return maxRamp;
    }

    // Follow-up 2: Find actual nodes forming the maximum width
    public List<TreeNode> getMaxWidthNodes(TreeNode root) {
        if (root == null)
            return new ArrayList<>();

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> posQueue = new LinkedList<>();
        nodeQueue.offer(root);
        posQueue.offer(0);

        List<TreeNode> maxWidthNodes = new ArrayList<>();
        int maxWidth = 1;

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();
            TreeNode startNode = null, endNode = null;
            int start = 0, end = 0;

            for (int i = 0; i < size; i++) {
                TreeNode node = nodeQueue.poll();
                int pos = posQueue.poll();

                if (i == 0) {
                    start = pos;
                    startNode = node;
                }
                if (i == size - 1) {
                    end = pos;
                    endNode = node;
                }

                if (node.left != null) {
                    nodeQueue.offer(node.left);
                    posQueue.offer(2 * pos);
                }
                if (node.right != null) {
                    nodeQueue.offer(node.right);
                    posQueue.offer(2 * pos + 1);
                }
            }

            if (end - start + 1 > maxWidth) {
                maxWidth = end - start + 1;
                maxWidthNodes.clear();
                maxWidthNodes.add(startNode);
                if (startNode != endNode)
                    maxWidthNodes.add(endNode);
            }
        }
        return maxWidthNodes;
    }

    // Follow-up 3: Memory optimized for very wide trees
    public int widthOptimized(TreeNode root) {
        return dfs(root, 0, 0, new ArrayList<>());
    }

    private int dfs(TreeNode node, int level, int pos, List<Integer> leftmost) {
        if (node == null)
            return 0;

        if (level >= leftmost.size())
            leftmost.add(pos);

        return Math.max(pos - leftmost.get(level) + 1,
                Math.max(dfs(node.left, level + 1, 2 * pos, leftmost),
                        dfs(node.right, level + 1, 2 * pos + 1, leftmost)));
    }

    // Helper methods
    private void collectLevelPositions(TreeNode node, int level, int pos, Map<Integer, List<Integer>> levelPositions) {
        if (node == null)
            return;

        levelPositions.computeIfAbsent(level, k -> new ArrayList<>()).add(pos);
        collectLevelPositions(node.left, level + 1, 2 * pos, levelPositions);
        collectLevelPositions(node.right, level + 1, 2 * pos + 1, levelPositions);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MaximumWidthRampInBinaryTree solution = new MaximumWidthRampInBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(2);
        root1.left.left = new TreeNode(5);
        root1.left.right = new TreeNode(3);
        root1.right.right = new TreeNode(9);

        System.out.println("Test 1 - Maximum width: " + solution.widthOfBinaryTree(root1));

        // Test case 2: Cross-level ramp
        System.out.println("\nTest 2 - Cross-level ramp: " + solution.maxCrossLevelRamp(root1));

        // Test case 3: Actual nodes
        List<TreeNode> maxNodes = solution.getMaxWidthNodes(root1);
        System.out.println("\nTest 3 - Max width nodes: ");
        for (TreeNode node : maxNodes) {
            System.out.print(node.val + " ");
        }
        System.out.println();

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Null root: " + solution.widthOfBinaryTree(null));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.widthOfBinaryTree(singleNode));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildWideTree(10);
        long start = System.nanoTime();
        int result = solution.widthOptimized(largeTree);
        long end = System.nanoTime();
        System.out.println("Wide tree result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildWideTree(int levels) {
        if (levels <= 0)
            return null;
        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        for (int level = 1; level < levels; level++) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode current = queue.poll();
                current.left = new TreeNode(level * 2);
                current.right = new TreeNode(level * 2 + 1);
                queue.offer(current.left);
                queue.offer(current.right);
            }
        }
        return root;
    }
}
