package trees.medium;

import java.util.*;

/**
 * LeetCode 222: Count Complete Tree Nodes
 * https://leetcode.com/problems/count-complete-tree-nodes/
 * 
 * Companies: Google, Amazon, Microsoft
 * Frequency: Medium
 *
 * Description: Given the root of a complete binary tree, return the number of
 * nodes in the tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 5 * 10^4]
 * - 0 <= Node.val <= 5 * 10^4
 * - Tree is guaranteed to be complete
 * 
 * Follow-up Questions:
 * 1. Can you optimize better than O(n)?
 * 2. Can you find the last node?
 * 3. Can you validate completeness?
 */
public class CountCompleteTreeNodes {

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

    // Approach 1: Optimized binary search O(log^2 n)
    public int countNodes(TreeNode root) {
        if (root == null)
            return 0;

        int leftHeight = getLeftHeight(root);
        int rightHeight = getRightHeight(root);

        if (leftHeight == rightHeight) {
            return (1 << leftHeight) - 1; // 2^height - 1
        }

        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    private int getLeftHeight(TreeNode node) {
        int height = 0;
        while (node != null) {
            height++;
            node = node.left;
        }
        return height;
    }

    private int getRightHeight(TreeNode node) {
        int height = 0;
        while (node != null) {
            height++;
            node = node.right;
        }
        return height;
    }

    // Follow-up 1: Binary search approach O(log^2 n)
    public int countNodesBinarySearch(TreeNode root) {
        if (root == null)
            return 0;

        int height = getHeight(root);
        if (height == 1)
            return 1;

        // Binary search on the last level
        int left = 0, right = (1 << (height - 1)) - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nodeExists(root, height, mid)) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return (1 << (height - 1)) - 1 + left;
    }

    private int getHeight(TreeNode node) {
        int height = 0;
        while (node != null) {
            height++;
            node = node.left;
        }
        return height;
    }

    private boolean nodeExists(TreeNode root, int height, int index) {
        int left = 0, right = (1 << (height - 1)) - 1;

        for (int level = 1; level < height; level++) {
            int mid = left + (right - left) / 2;

            if (index <= mid) {
                right = mid;
                root = root.left;
            } else {
                left = mid + 1;
                root = root.right;
            }
        }

        return root != null;
    }

    // Follow-up 2: Find the last node
    public TreeNode findLastNode(TreeNode root) {
        if (root == null)
            return null;

        int height = getHeight(root);
        if (height == 1)
            return root;

        int left = 0, right = (1 << (height - 1)) - 1;
        int lastIndex = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nodeExists(root, height, mid)) {
                lastIndex = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return getNodeAtIndex(root, height, lastIndex);
    }

    private TreeNode getNodeAtIndex(TreeNode root, int height, int index) {
        int left = 0, right = (1 << (height - 1)) - 1;

        for (int level = 1; level < height; level++) {
            int mid = left + (right - left) / 2;

            if (index <= mid) {
                right = mid;
                root = root.left;
            } else {
                left = mid + 1;
                root = root.right;
            }
        }

        return root;
    }

    // Follow-up 3: Validate completeness
    public boolean isCompleteTree(TreeNode root) {
        if (root == null)
            return true;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean foundNull = false;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (node == null) {
                foundNull = true;
            } else {
                if (foundNull)
                    return false;
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }

        return true;
    }

    // Helper: Build complete tree for testing
    private TreeNode buildCompleteTree(int n) {
        if (n == 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int val = 2;

        while (!queue.isEmpty() && val <= n) {
            TreeNode node = queue.poll();

            if (val <= n) {
                node.left = new TreeNode(val++);
                queue.offer(node.left);
            }

            if (val <= n) {
                node.right = new TreeNode(val++);
                queue.offer(node.right);
            }
        }

        return root;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CountCompleteTreeNodes solution = new CountCompleteTreeNodes();

        // Test case 1: Perfect binary tree
        TreeNode perfect = solution.buildCompleteTree(7);
        System.out.println("Test 1 - Perfect tree (7 nodes):");
        System.out.println("Count: " + solution.countNodes(perfect));
        System.out.println("Binary search count: " + solution.countNodesBinarySearch(perfect));
        System.out.println("Is complete: " + solution.isCompleteTree(perfect));

        // Test case 2: Complete but not perfect
        TreeNode complete = solution.buildCompleteTree(6);
        System.out.println("\nTest 2 - Complete tree (6 nodes):");
        System.out.println("Count: " + solution.countNodes(complete));
        System.out.println("Binary search count: " + solution.countNodesBinarySearch(complete));

        TreeNode lastNode = solution.findLastNode(complete);
        System.out.println("Last node value: " + (lastNode != null ? lastNode.val : "null"));

        // Test case 3: Single node
        TreeNode single = new TreeNode(1);
        System.out.println("\nTest 3 - Single node:");
        System.out.println("Count: " + solution.countNodes(single));
        System.out.println("Is complete: " + solution.isCompleteTree(single));

        // Test case 4: Test completeness validation
        TreeNode incomplete = new TreeNode(1);
        incomplete.left = new TreeNode(2);
        incomplete.right = new TreeNode(3);
        incomplete.left.right = new TreeNode(4); // Missing left child, has right
        System.out.println("\nTest 4 - Incomplete tree:");
        System.out.println("Is complete: " + solution.isCompleteTree(incomplete));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree count: " + solution.countNodes(null));
        System.out.println("Empty tree is complete: " + solution.isCompleteTree(null));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = solution.buildCompleteTree(50000);

        long start = System.nanoTime();
        int count1 = solution.countNodes(largeTree);
        long end = System.nanoTime();
        System.out.println("Optimized count (50000 nodes): " + count1 + " in " + (end - start) / 1_000_000 + " ms");

        start = System.nanoTime();
        int count2 = solution.countNodesBinarySearch(largeTree);
        end = System.nanoTime();
        System.out.println("Binary search count: " + count2 + " in " + (end - start) / 1_000_000 + " ms");
    }
}
