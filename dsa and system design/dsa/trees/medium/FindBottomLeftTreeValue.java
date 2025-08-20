package trees.medium;

import java.util.*;

/**
 * LeetCode 513: Find Bottom Left Tree Value
 * https://leetcode.com/problems/find-bottom-left-tree-value/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return the leftmost value in
 * the last row of the tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -2^31 <= Node.val <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. Can you find the bottom right value?
 * 2. Can you find all bottom level values?
 * 3. Can you use DFS instead of BFS?
 */
public class FindBottomLeftTreeValue {

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
    public int findBottomLeftValue(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        TreeNode current = null;

        while (!queue.isEmpty()) {
            current = queue.poll();

            // Add right first, then left to get leftmost at the end
            if (current.right != null)
                queue.offer(current.right);
            if (current.left != null)
                queue.offer(current.left);
        }

        return current.val;
    }

    // Follow-up 1: Find bottom right value
    public int findBottomRightValue(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        TreeNode current = null;

        while (!queue.isEmpty()) {
            current = queue.poll();

            // Add left first, then right to get rightmost at the end
            if (current.left != null)
                queue.offer(current.left);
            if (current.right != null)
                queue.offer(current.right);
        }

        return current.val;
    }

    // Follow-up 2: Find all bottom level values
    public List<Integer> findBottomLevelValues(TreeNode root) {
        if (root == null)
            return new ArrayList<>();

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        List<Integer> bottomLevel = new ArrayList<>();

        while (!queue.isEmpty()) {
            int size = queue.size();
            bottomLevel.clear();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                bottomLevel.add(node.val);

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }
        }

        return bottomLevel;
    }

    // Follow-up 3: DFS approach
    private int maxDepth = -1;
    private int bottomLeftValue = 0;

    public int findBottomLeftValueDFS(TreeNode root) {
        maxDepth = -1;
        bottomLeftValue = 0;
        dfs(root, 0);
        return bottomLeftValue;
    }

    private void dfs(TreeNode node, int depth) {
        if (node == null)
            return;

        if (depth > maxDepth) {
            maxDepth = depth;
            bottomLeftValue = node.val;
        }

        dfs(node.left, depth + 1);
        dfs(node.right, depth + 1);
    }

    // Alternative DFS for bottom right
    public int findBottomRightValueDFS(TreeNode root) {
        maxDepth = -1;
        bottomLeftValue = 0;
        dfsRight(root, 0);
        return bottomLeftValue;
    }

    private void dfsRight(TreeNode node, int depth) {
        if (node == null)
            return;

        if (depth > maxDepth) {
            maxDepth = depth;
            bottomLeftValue = node.val;
        }

        dfsRight(node.right, depth + 1);
        dfsRight(node.left, depth + 1);
    }

    // Helper: Get tree height
    private int getHeight(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + Math.max(getHeight(root.left), getHeight(root.right));
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindBottomLeftTreeValue solution = new FindBottomLeftTreeValue();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(3);

        System.out.println("Test 1 - Simple tree:");
        System.out.println("Bottom left (BFS): " + solution.findBottomLeftValue(root1));
        System.out.println("Bottom left (DFS): " + solution.findBottomLeftValueDFS(root1));
        System.out.println("Bottom right: " + solution.findBottomRightValue(root1));
        System.out.println("Bottom level: " + solution.findBottomLevelValues(root1));

        // Test case 2: Complex tree
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(3);
        root2.left.left = new TreeNode(4);
        root2.right.left = new TreeNode(5);
        root2.right.right = new TreeNode(6);
        root2.right.left.left = new TreeNode(7);

        System.out.println("\nTest 2 - Complex tree:");
        System.out.println("Bottom left (BFS): " + solution.findBottomLeftValue(root2));
        System.out.println("Bottom left (DFS): " + solution.findBottomLeftValueDFS(root2));
        System.out.println("Bottom right (BFS): " + solution.findBottomRightValue(root2));
        System.out.println("Bottom right (DFS): " + solution.findBottomRightValueDFS(root2));
        System.out.println("Bottom level: " + solution.findBottomLevelValues(root2));

        // Test case 3: Left skewed tree
        TreeNode leftSkewed = new TreeNode(1);
        leftSkewed.left = new TreeNode(2);
        leftSkewed.left.left = new TreeNode(3);
        leftSkewed.left.left.left = new TreeNode(4);

        System.out.println("\nTest 3 - Left skewed tree:");
        System.out.println("Bottom left: " + solution.findBottomLeftValue(leftSkewed));
        System.out.println("Bottom right: " + solution.findBottomRightValue(leftSkewed));

        // Test case 4: Right skewed tree
        TreeNode rightSkewed = new TreeNode(1);
        rightSkewed.right = new TreeNode(2);
        rightSkewed.right.right = new TreeNode(3);
        rightSkewed.right.right.right = new TreeNode(4);

        System.out.println("\nTest 4 - Right skewed tree:");
        System.out.println("Bottom left: " + solution.findBottomLeftValue(rightSkewed));
        System.out.println("Bottom right: " + solution.findBottomRightValue(rightSkewed));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(42);
        System.out.println("Single node: " + solution.findBottomLeftValue(singleNode));

        TreeNode perfectTree = new TreeNode(1);
        perfectTree.left = new TreeNode(2);
        perfectTree.right = new TreeNode(3);
        perfectTree.left.left = new TreeNode(4);
        perfectTree.left.right = new TreeNode(5);
        perfectTree.right.left = new TreeNode(6);
        perfectTree.right.right = new TreeNode(7);
        System.out.println("Perfect tree bottom left: " + solution.findBottomLeftValue(perfectTree));
        System.out.println("Perfect tree bottom level: " + solution.findBottomLevelValues(perfectTree));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int resultBFS = solution.findBottomLeftValue(largeTree);
        long end = System.nanoTime();
        System.out.println("BFS result (1000 nodes): " + resultBFS + " in " + (end - start) / 1_000_000 + " ms");

        start = System.nanoTime();
        int resultDFS = solution.findBottomLeftValueDFS(largeTree);
        end = System.nanoTime();
        System.out.println("DFS result: " + resultDFS + " in " + (end - start) / 1_000_000 + " ms");
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
