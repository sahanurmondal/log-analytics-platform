package trees.medium;

import java.util.*;

/**
 * LeetCode 404: Sum of Left Leaves
 * https://leetcode.com/problems/sum-of-left-leaves/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return the sum of all left
 * leaves.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 1000]
 * - -1000 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find the sum of right leaves?
 * 2. Can you count left leaves instead of summing?
 * 3. Can you find all left leaves at a specific level?
 */
public class FindSumOfLeftLeaves {

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
    public int sumOfLeftLeaves(TreeNode root) {
        return sumOfLeftLeavesHelper(root, false);
    }

    private int sumOfLeftLeavesHelper(TreeNode node, boolean isLeft) {
        if (node == null)
            return 0;

        // If it's a left leaf
        if (isLeft && node.left == null && node.right == null) {
            return node.val;
        }

        return sumOfLeftLeavesHelper(node.left, true) +
                sumOfLeftLeavesHelper(node.right, false);
    }

    // Follow-up 1: Sum of right leaves
    public int sumOfRightLeaves(TreeNode root) {
        return sumOfRightLeavesHelper(root, false);
    }

    private int sumOfRightLeavesHelper(TreeNode node, boolean isRight) {
        if (node == null)
            return 0;

        // If it's a right leaf
        if (isRight && node.left == null && node.right == null) {
            return node.val;
        }

        return sumOfRightLeavesHelper(node.left, false) +
                sumOfRightLeavesHelper(node.right, true);
    }

    // Follow-up 2: Count left leaves
    public int countLeftLeaves(TreeNode root) {
        return countLeftLeavesHelper(root, false);
    }

    private int countLeftLeavesHelper(TreeNode node, boolean isLeft) {
        if (node == null)
            return 0;

        if (isLeft && node.left == null && node.right == null) {
            return 1;
        }

        return countLeftLeavesHelper(node.left, true) +
                countLeftLeavesHelper(node.right, false);
    }

    // Follow-up 3: Find all left leaves at specific level
    public List<Integer> findLeftLeavesAtLevel(TreeNode root, int targetLevel) {
        List<Integer> result = new ArrayList<>();
        findLeftLeavesAtLevelHelper(root, false, 1, targetLevel, result);
        return result;
    }

    private void findLeftLeavesAtLevelHelper(TreeNode node, boolean isLeft, int currentLevel,
            int targetLevel, List<Integer> result) {
        if (node == null)
            return;

        if (isLeft && node.left == null && node.right == null && currentLevel == targetLevel) {
            result.add(node.val);
            return;
        }

        findLeftLeavesAtLevelHelper(node.left, true, currentLevel + 1, targetLevel, result);
        findLeftLeavesAtLevelHelper(node.right, false, currentLevel + 1, targetLevel, result);
    }

    // Additional: Iterative BFS approach
    public int sumOfLeftLeavesBFS(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        Queue<Boolean> isLeftQueue = new LinkedList<>();

        queue.offer(root);
        isLeftQueue.offer(false);
        int sum = 0;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            boolean isLeft = isLeftQueue.poll();

            if (isLeft && node.left == null && node.right == null) {
                sum += node.val;
            }

            if (node.left != null) {
                queue.offer(node.left);
                isLeftQueue.offer(true);
            }

            if (node.right != null) {
                queue.offer(node.right);
                isLeftQueue.offer(false);
            }
        }

        return sum;
    }

    // Additional: Get all left leaves with their depths
    public Map<Integer, List<Integer>> getLeftLeavesByDepth(TreeNode root) {
        Map<Integer, List<Integer>> result = new HashMap<>();
        getLeftLeavesByDepthHelper(root, false, 1, result);
        return result;
    }

    private void getLeftLeavesByDepthHelper(TreeNode node, boolean isLeft, int depth,
            Map<Integer, List<Integer>> result) {
        if (node == null)
            return;

        if (isLeft && node.left == null && node.right == null) {
            result.computeIfAbsent(depth, k -> new ArrayList<>()).add(node.val);
            return;
        }

        getLeftLeavesByDepthHelper(node.left, true, depth + 1, result);
        getLeftLeavesByDepthHelper(node.right, false, depth + 1, result);
    }

    // Additional: Find maximum left leaf value
    public int maxLeftLeaf(TreeNode root) {
        return maxLeftLeafHelper(root, false);
    }

    private int maxLeftLeafHelper(TreeNode node, boolean isLeft) {
        if (node == null)
            return Integer.MIN_VALUE;

        if (isLeft && node.left == null && node.right == null) {
            return node.val;
        }

        int leftMax = maxLeftLeafHelper(node.left, true);
        int rightMax = maxLeftLeafHelper(node.right, false);

        return Math.max(leftMax, rightMax);
    }

    // Helper: Check if node is leaf
    private boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindSumOfLeftLeaves solution = new FindSumOfLeftLeaves();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(9);
        root1.right = new TreeNode(20);
        root1.right.left = new TreeNode(15);
        root1.right.right = new TreeNode(7);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Sum of left leaves: " + solution.sumOfLeftLeaves(root1));
        System.out.println("Sum of right leaves: " + solution.sumOfRightLeaves(root1));
        System.out.println("Count of left leaves: " + solution.countLeftLeaves(root1));
        System.out.println("Sum of left leaves (BFS): " + solution.sumOfLeftLeavesBFS(root1));

        // Test case 2: Left leaves at specific level
        System.out.println("\nTest 2 - Left leaves at level 3:");
        List<Integer> leftLeavesLevel3 = solution.findLeftLeavesAtLevel(root1, 3);
        System.out.println("Left leaves at level 3: " + leftLeavesLevel3);

        // Test case 3: Left leaves by depth
        System.out.println("\nTest 3 - Left leaves by depth:");
        Map<Integer, List<Integer>> leftLeavesByDepth = solution.getLeftLeavesByDepth(root1);
        for (Map.Entry<Integer, List<Integer>> entry : leftLeavesByDepth.entrySet()) {
            System.out.println("Depth " + entry.getKey() + ": " + entry.getValue());
        }

        // Test case 4: Max left leaf
        System.out.println("\nTest 4 - Maximum left leaf: " + solution.maxLeftLeaf(root1));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.sumOfLeftLeaves(singleNode));

        TreeNode onlyLeftChild = new TreeNode(1);
        onlyLeftChild.left = new TreeNode(2);
        System.out.println("Only left child: " + solution.sumOfLeftLeaves(onlyLeftChild));

        TreeNode onlyRightChild = new TreeNode(1);
        onlyRightChild.right = new TreeNode(2);
        System.out.println("Only right child: " + solution.sumOfLeftLeaves(onlyRightChild));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int result = solution.sumOfLeftLeaves(largeTree);
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
