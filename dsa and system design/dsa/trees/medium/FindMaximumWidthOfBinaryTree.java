package trees.medium;

import java.util.*;

/**
 * LeetCode 662: Maximum Width of Binary Tree
 * https://leetcode.com/problems/maximum-width-of-binary-tree/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return the maximum width of the
 * given tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 3000]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you find the widest level number?
 * 2. Can you handle very large position values?
 * 3. Can you find nodes at maximum width positions?
 */
public class FindMaximumWidthOfBinaryTree {

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

    // Follow-up 1: Find the widest level number
    public int findWidestLevel(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> posQueue = new LinkedList<>();
        nodeQueue.offer(root);
        posQueue.offer(0);

        int maxWidth = 1;
        int widestLevel = 1;
        int currentLevel = 0;

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();
            currentLevel++;
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

            int width = end - start + 1;
            if (width > maxWidth) {
                maxWidth = width;
                widestLevel = currentLevel;
            }
        }
        return widestLevel;
    }

    // Follow-up 2: Handle large position values with normalization
    public int widthOfBinaryTreeOptimized(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Long> posQueue = new LinkedList<>();
        nodeQueue.offer(root);
        posQueue.offer(0L);
        int maxWidth = 1;

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();
            long start = 0, end = 0;
            long minPos = posQueue.peek(); // Normalize positions to prevent overflow

            for (int i = 0; i < size; i++) {
                TreeNode node = nodeQueue.poll();
                long pos = posQueue.poll() - minPos; // Normalize

                if (i == 0)
                    start = pos;
                if (i == size - 1)
                    end = pos;

                if (node.left != null) {
                    nodeQueue.offer(node.left);
                    posQueue.offer(2 * pos + minPos);
                }
                if (node.right != null) {
                    nodeQueue.offer(node.right);
                    posQueue.offer(2 * pos + 1 + minPos);
                }
            }
            maxWidth = Math.max(maxWidth, (int) (end - start + 1));
        }
        return maxWidth;
    }

    // Follow-up 3: Find nodes at maximum width positions
    public List<TreeNode> findMaxWidthNodes(TreeNode root) {
        List<TreeNode> maxWidthNodes = new ArrayList<>();
        if (root == null)
            return maxWidthNodes;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> posQueue = new LinkedList<>();
        nodeQueue.offer(root);
        posQueue.offer(0);

        int maxWidth = 1;
        List<TreeNode> currentMaxNodes = new ArrayList<>();
        currentMaxNodes.add(root);

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();
            List<TreeNode> levelNodes = new ArrayList<>();
            int start = 0, end = 0;

            for (int i = 0; i < size; i++) {
                TreeNode node = nodeQueue.poll();
                int pos = posQueue.poll();
                levelNodes.add(node);

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

            int width = end - start + 1;
            if (width > maxWidth) {
                maxWidth = width;
                currentMaxNodes.clear();
                currentMaxNodes.add(levelNodes.get(0));
                if (levelNodes.size() > 1) {
                    currentMaxNodes.add(levelNodes.get(levelNodes.size() - 1));
                }
            }
        }

        return currentMaxNodes;
    }

    // Additional: DFS approach
    public int widthOfBinaryTreeDFS(TreeNode root) {
        List<Integer> leftmost = new ArrayList<>();
        return dfs(root, 0, 0, leftmost);
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

    // Additional: Get width at each level
    public Map<Integer, Integer> getWidthAtEachLevel(TreeNode root) {
        Map<Integer, Integer> levelWidths = new HashMap<>();
        if (root == null)
            return levelWidths;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> posQueue = new LinkedList<>();
        nodeQueue.offer(root);
        posQueue.offer(0);
        int currentLevel = 0;

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();
            currentLevel++;
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
            levelWidths.put(currentLevel, end - start + 1);
        }
        return levelWidths;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumWidthOfBinaryTree solution = new FindMaximumWidthOfBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(2);
        root1.left.left = new TreeNode(5);
        root1.left.right = new TreeNode(3);
        root1.right.right = new TreeNode(9);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Maximum width: " + solution.widthOfBinaryTree(root1));
        System.out.println("Widest level: " + solution.findWidestLevel(root1));
        System.out.println("DFS approach: " + solution.widthOfBinaryTreeDFS(root1));

        // Test case 2: Max width nodes
        System.out.println("\nTest 2 - Max width nodes:");
        List<TreeNode> maxNodes = solution.findMaxWidthNodes(root1);
        System.out.print("Nodes at max width: ");
        for (TreeNode node : maxNodes) {
            System.out.print(node.val + " ");
        }
        System.out.println();

        // Test case 3: Width at each level
        System.out.println("\nTest 3 - Width at each level:");
        Map<Integer, Integer> levelWidths = solution.getWidthAtEachLevel(root1);
        for (Map.Entry<Integer, Integer> entry : levelWidths.entrySet()) {
            System.out.println("Level " + entry.getKey() + ": width " + entry.getValue());
        }

        // Test case 4: Optimized for large positions
        System.out.println("\nTest 4 - Optimized approach:");
        System.out.println("Result: " + solution.widthOfBinaryTreeOptimized(root1));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.widthOfBinaryTree(singleNode));

        TreeNode skewed = new TreeNode(1);
        skewed.left = new TreeNode(2);
        skewed.left.left = new TreeNode(3);
        skewed.left.left.left = new TreeNode(4);
        System.out.println("Left skewed: " + solution.widthOfBinaryTree(skewed));

        TreeNode sparse = new TreeNode(1);
        sparse.left = new TreeNode(2);
        sparse.right = new TreeNode(3);
        sparse.left.left = new TreeNode(4);
        sparse.right.right = new TreeNode(5);
        System.out.println("Sparse tree: " + solution.widthOfBinaryTree(sparse));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int result = solution.widthOfBinaryTreeOptimized(largeTree);
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
