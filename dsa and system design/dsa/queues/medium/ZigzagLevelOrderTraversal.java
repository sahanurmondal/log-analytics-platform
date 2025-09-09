package queues.medium;

import java.util.List;

/**
 * LeetCode 103: Binary Tree Zigzag Level Order Traversal
 * https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 80+ interviews)
 *
 * Description:
 * Given the root of a binary tree, return the zigzag level order traversal of
 * its nodes' values. (i.e., from left to right, then right to left for the next
 * level
 * and alternate between).
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 2000]
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you solve it using two queues or deque?
 * 2. Can you extend to k-ary trees?
 * 3. Can you implement it iteratively and recursively?
 */
public class ZigzagLevelOrderTraversal {

    // Approach 1: BFS with Queue and Level Direction - O(n) time, O(n) space
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new java.util.ArrayList<>();
        if (root == null)
            return result;

        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            java.util.List<Integer> currentLevel = new java.util.ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                // Add to current level based on direction
                if (leftToRight) {
                    currentLevel.add(node.val);
                } else {
                    currentLevel.add(0, node.val); // Add at beginning for reverse
                }

                // Add children to queue
                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            result.add(currentLevel);
            leftToRight = !leftToRight; // Toggle direction
        }

        return result;
    }

    // Approach 2: Using Deque - O(n) time, O(n) space
    public List<List<Integer>> zigzagLevelOrderDeque(TreeNode root) {
        List<List<Integer>> result = new java.util.ArrayList<>();
        if (root == null)
            return result;

        java.util.Deque<TreeNode> deque = new java.util.ArrayDeque<>();
        deque.offer(root);
        boolean leftToRight = true;

        while (!deque.isEmpty()) {
            int levelSize = deque.size();
            java.util.List<Integer> currentLevel = new java.util.ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node;

                if (leftToRight) {
                    node = deque.pollFirst();
                    if (node.left != null)
                        deque.offerLast(node.left);
                    if (node.right != null)
                        deque.offerLast(node.right);
                } else {
                    node = deque.pollLast();
                    if (node.right != null)
                        deque.offerFirst(node.right);
                    if (node.left != null)
                        deque.offerFirst(node.left);
                }

                currentLevel.add(node.val);
            }

            result.add(currentLevel);
            leftToRight = !leftToRight;
        }

        return result;
    }

    // Approach 3: Recursive with Level Tracking - O(n) time, O(h) space
    public List<List<Integer>> zigzagLevelOrderRecursive(TreeNode root) {
        List<List<Integer>> result = new java.util.ArrayList<>();
        dfs(root, 0, result);
        return result;
    }

    private void dfs(TreeNode node, int level, List<List<Integer>> result) {
        if (node == null)
            return;

        // Initialize level if needed
        if (level >= result.size()) {
            result.add(new java.util.ArrayList<>());
        }

        // Add to appropriate position based on level
        if (level % 2 == 0) {
            result.get(level).add(node.val); // Left to right
        } else {
            result.get(level).add(0, node.val); // Right to left
        }

        // Recursively process children
        dfs(node.left, level + 1, result);
        dfs(node.right, level + 1, result);
    }

    public static void main(String[] args) {
        ZigzagLevelOrderTraversal solution = new ZigzagLevelOrderTraversal();

        // Test Case 1: Standard example
        TreeNode root = new TreeNode(3, new TreeNode(9), new TreeNode(20, new TreeNode(15), new TreeNode(7)));
        System.out.println("Standard: " + solution.zigzagLevelOrder(root)); // [[3],[20,9],[15,7]]

        // Test Case 2: Single node
        System.out.println("Single: " + solution.zigzagLevelOrder(new TreeNode(1))); // [[1]]

        // Test Case 3: Empty tree
        System.out.println("Empty: " + solution.zigzagLevelOrder(null)); // []

        // Test Case 4: Left skewed tree
        TreeNode leftSkewed = new TreeNode(1, new TreeNode(2, new TreeNode(3), null), null);
        System.out.println("Left skewed: " + solution.zigzagLevelOrder(leftSkewed)); // [[1],[2],[3]]

        // Test Case 5: Right skewed tree
        TreeNode rightSkewed = new TreeNode(1, null, new TreeNode(2, null, new TreeNode(3)));
        System.out.println("Right skewed: " + solution.zigzagLevelOrder(rightSkewed)); // [[1],[2],[3]]

        // Test Case 6: Complete binary tree
        TreeNode complete = new TreeNode(1,
                new TreeNode(2, new TreeNode(4), new TreeNode(5)),
                new TreeNode(3, new TreeNode(6), new TreeNode(7)));
        System.out.println("Complete: " + solution.zigzagLevelOrder(complete)); // [[1],[3,2],[4,5,6,7]]

        // Test Case 7: Larger tree
        TreeNode large = new TreeNode(1,
                new TreeNode(2, new TreeNode(4, new TreeNode(8), new TreeNode(9)), new TreeNode(5)),
                new TreeNode(3, new TreeNode(6), new TreeNode(7, new TreeNode(10), new TreeNode(11))));
        System.out.println("Large tree: " + solution.zigzagLevelOrder(large));

        // Test deque approach
        System.out.println("Deque approach: " + solution.zigzagLevelOrderDeque(root));

        // Test recursive approach
        System.out.println("Recursive approach: " + solution.zigzagLevelOrderRecursive(root));
    }
}

class TreeNode {
    int val;
    TreeNode left, right;

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
