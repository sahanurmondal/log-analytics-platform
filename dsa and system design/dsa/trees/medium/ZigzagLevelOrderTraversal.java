package trees.medium;

import java.util.*;

/**
 * LeetCode 103: Binary Tree Zigzag Level Order Traversal (Alternative
 * Implementation)
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
 * 1. Can you use two stacks instead of deque?
 * 2. Can you reverse only specific levels?
 * 3. Can you implement spiral traversal?
 */
public class ZigzagLevelOrderTraversal {

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

    // Approach 1: BFS with level tracking and reversal
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            if (!leftToRight) {
                Collections.reverse(level);
            }

            result.add(level);
            leftToRight = !leftToRight;
        }

        return result;
    }

    // Follow-up 1: Two stacks approach
    public List<List<Integer>> zigzagLevelOrderTwoStacks(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        Stack<TreeNode> currentLevel = new Stack<>();
        Stack<TreeNode> nextLevel = new Stack<>();

        currentLevel.push(root);
        boolean leftToRight = true;

        while (!currentLevel.isEmpty()) {
            List<Integer> level = new ArrayList<>();

            while (!currentLevel.isEmpty()) {
                TreeNode node = currentLevel.pop();
                level.add(node.val);

                if (leftToRight) {
                    if (node.left != null)
                        nextLevel.push(node.left);
                    if (node.right != null)
                        nextLevel.push(node.right);
                } else {
                    if (node.right != null)
                        nextLevel.push(node.right);
                    if (node.left != null)
                        nextLevel.push(node.left);
                }
            }

            result.add(level);
            leftToRight = !leftToRight;

            Stack<TreeNode> temp = currentLevel;
            currentLevel = nextLevel;
            nextLevel = temp;
        }

        return result;
    }

    // Follow-up 2: Reverse only specific levels
    public List<List<Integer>> zigzagSpecificLevels(TreeNode root, Set<Integer> levelsToReverse) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int currentLevel = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            if (levelsToReverse.contains(currentLevel)) {
                Collections.reverse(level);
            }

            result.add(level);
            currentLevel++;
        }

        return result;
    }

    // Follow-up 3: Spiral traversal (outside to inside)
    public List<Integer> spiralTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;

        List<List<Integer>> levels = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            levels.add(level);
        }

        // Convert to spiral order
        boolean leftToRight = true;
        for (List<Integer> level : levels) {
            if (leftToRight) {
                result.addAll(level);
            } else {
                for (int i = level.size() - 1; i >= 0; i--) {
                    result.add(level.get(i));
                }
            }
            leftToRight = !leftToRight;
        }

        return result;
    }

    // Additional: Deque-based approach
    public List<List<Integer>> zigzagLevelOrderDeque(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int size = queue.size();
            Deque<Integer> level = new ArrayDeque<>();

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

            result.add(new ArrayList<>(level));
            leftToRight = !leftToRight;
        }

        return result;
    }

    // Additional: Get zigzag as single list
    public List<Integer> zigzagAsList(TreeNode root) {
        List<List<Integer>> levelOrder = zigzagLevelOrder(root);
        List<Integer> result = new ArrayList<>();

        for (List<Integer> level : levelOrder) {
            result.addAll(level);
        }

        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ZigzagLevelOrderTraversal solution = new ZigzagLevelOrderTraversal();

        // Test case 1: Basic zigzag
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(9);
        root1.right = new TreeNode(20);
        root1.right.left = new TreeNode(15);
        root1.right.right = new TreeNode(7);

        System.out.println("Test 1 - Basic zigzag:");
        System.out.println("Standard: " + solution.zigzagLevelOrder(root1));
        System.out.println("Two stacks: " + solution.zigzagLevelOrderTwoStacks(root1));
        System.out.println("Deque: " + solution.zigzagLevelOrderDeque(root1));
        System.out.println("As list: " + solution.zigzagAsList(root1));

        // Test case 2: Reverse specific levels
        System.out.println("\nTest 2 - Reverse levels 2 and 3:");
        Set<Integer> levelsToReverse = Set.of(2, 3);
        System.out.println("Result: " + solution.zigzagSpecificLevels(root1, levelsToReverse));

        // Test case 3: Spiral traversal
        System.out.println("\nTest 3 - Spiral traversal:");
        System.out.println("Spiral: " + solution.spiralTraversal(root1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.zigzagLevelOrder(null));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.zigzagLevelOrder(singleNode));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        List<List<Integer>> result = solution.zigzagLevelOrderDeque(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree zigzag levels: " + result.size() + " in " + (end - start) / 1_000_000 + " ms");
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
