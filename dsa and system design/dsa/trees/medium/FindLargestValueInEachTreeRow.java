package trees.medium;

import java.util.*;

/**
 * LeetCode 515: Find Largest Value in Each Tree Row
 * https://leetcode.com/problems/find-largest-value-in-each-tree-row/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return an array of the largest
 * value in each row of the tree.
 *
 * Constraints:
 * - The number of nodes in the tree will be in the range [0, 10^4]
 * - -2^31 <= Node.val <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. Can you find the smallest value in each row?
 * 2. Can you find both min and max in each row?
 * 3. Can you use DFS instead of BFS?
 */
public class FindLargestValueInEachTreeRow {

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
    public List<Integer> largestValues(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            int maxVal = Integer.MIN_VALUE;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                maxVal = Math.max(maxVal, node.val);

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            result.add(maxVal);
        }

        return result;
    }

    // Follow-up 1: Find smallest value in each row
    public List<Integer> smallestValues(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            int minVal = Integer.MAX_VALUE;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                minVal = Math.min(minVal, node.val);

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            result.add(minVal);
        }

        return result;
    }

    // Follow-up 2: Find both min and max in each row
    public List<int[]> minMaxValues(TreeNode root) {
        List<int[]> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            int minVal = Integer.MAX_VALUE;
            int maxVal = Integer.MIN_VALUE;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                minVal = Math.min(minVal, node.val);
                maxVal = Math.max(maxVal, node.val);

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            result.add(new int[] { minVal, maxVal });
        }

        return result;
    }

    // Follow-up 3: DFS approach
    public List<Integer> largestValuesDFS(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        dfs(root, 0, result);
        return result;
    }

    private void dfs(TreeNode node, int level, List<Integer> result) {
        if (node == null)
            return;

        // Extend result list if this is a new level
        if (level >= result.size()) {
            result.add(node.val);
        } else {
            // Update max for current level
            result.set(level, Math.max(result.get(level), node.val));
        }

        dfs(node.left, level + 1, result);
        dfs(node.right, level + 1, result);
    }

    // Additional: Find sum of each row
    public List<Long> sumOfEachRow(TreeNode root) {
        List<Long> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            long sum = 0;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                sum += node.val;

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            result.add(sum);
        }

        return result;
    }

    // Additional: Find average of each row
    public List<Double> averageOfEachRow(TreeNode root) {
        List<Double> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            long sum = 0;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                sum += node.val;

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            result.add((double) sum / size);
        }

        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindLargestValueInEachTreeRow solution = new FindLargestValueInEachTreeRow();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(2);
        root1.left.left = new TreeNode(5);
        root1.left.right = new TreeNode(3);
        root1.right.right = new TreeNode(9);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Largest values (BFS): " + solution.largestValues(root1));
        System.out.println("Largest values (DFS): " + solution.largestValuesDFS(root1));
        System.out.println("Smallest values: " + solution.smallestValues(root1));

        List<int[]> minMax = solution.minMaxValues(root1);
        System.out.print("Min-Max values: ");
        for (int[] pair : minMax) {
            System.out.print("[" + pair[0] + "," + pair[1] + "] ");
        }
        System.out.println();

        // Test case 2: Tree with negative values
        TreeNode root2 = new TreeNode(-1);
        root2.left = new TreeNode(-3);
        root2.right = new TreeNode(-2);
        root2.left.left = new TreeNode(-5);
        root2.left.right = new TreeNode(-4);

        System.out.println("\nTest 2 - Tree with negative values:");
        System.out.println("Largest values: " + solution.largestValues(root2));
        System.out.println("Smallest values: " + solution.smallestValues(root2));

        // Test case 3: Additional statistics
        System.out.println("\nTest 3 - Additional statistics:");
        System.out.println("Sum of each row: " + solution.sumOfEachRow(root1));
        System.out.println("Average of each row: " + solution.averageOfEachRow(root1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.largestValues(null));

        TreeNode singleNode = new TreeNode(42);
        System.out.println("Single node: " + solution.largestValues(singleNode));

        TreeNode leftSkewed = new TreeNode(1);
        leftSkewed.left = new TreeNode(2);
        leftSkewed.left.left = new TreeNode(3);
        leftSkewed.left.left.left = new TreeNode(4);
        System.out.println("Left skewed: " + solution.largestValues(leftSkewed));

        TreeNode rightSkewed = new TreeNode(1);
        rightSkewed.right = new TreeNode(2);
        rightSkewed.right.right = new TreeNode(3);
        rightSkewed.right.right.right = new TreeNode(4);
        System.out.println("Right skewed: " + solution.largestValues(rightSkewed));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        List<Integer> resultBFS = solution.largestValues(largeTree);
        long end = System.nanoTime();
        System.out.println(
                "BFS result (1000 nodes): " + resultBFS.size() + " levels in " + (end - start) / 1_000_000 + " ms");

        start = System.nanoTime();
        List<Integer> resultDFS = solution.largestValuesDFS(largeTree);
        end = System.nanoTime();
        System.out.println("DFS result: " + resultDFS.size() + " levels in " + (end - start) / 1_000_000 + " ms");

        System.out.println("Results match: " + resultBFS.equals(resultDFS));
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
                current.left = new TreeNode(rand.nextInt(2000) - 1000);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(rand.nextInt(2000) - 1000);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
