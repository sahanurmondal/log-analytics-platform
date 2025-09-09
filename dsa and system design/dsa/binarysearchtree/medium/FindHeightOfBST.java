package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * Variation: Find Height of BST
 * Related to LeetCode 104: Maximum Depth of Binary Tree
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: High
 *
 * Description:
 * Given a BST, return its height (also known as maximum depth).
 * Height is the number of edges on the longest path from root to leaf.
 * Some definitions count height as number of nodes instead of edges.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively?
 * 2. What if you need to find height of specific subtree?
 * 3. Can you solve it without recursion stack overflow for very deep trees?
 */
public class FindHeightOfBST {

    // Approach 1: Recursive (Standard) - O(n) time, O(h) space
    public int height(TreeNode root) {
        if (root == null)
            return 0;

        int leftHeight = height(root.left);
        int rightHeight = height(root.right);

        return 1 + Math.max(leftHeight, rightHeight);
    }

    // Approach 2: Iterative using Level Order (BFS) - O(n) time, O(w) space where w
    // is max width
    public int heightIterative(TreeNode root) {
        if (root == null)
            return 0;

        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int height = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            height++;

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }

        return height;
    }

    // Approach 3: Iterative using Stack (DFS) - O(n) time, O(h) space
    public int heightIterativeDFS(TreeNode root) {
        if (root == null)
            return 0;

        java.util.Stack<TreeNode> nodeStack = new java.util.Stack<>();
        java.util.Stack<Integer> depthStack = new java.util.Stack<>();

        nodeStack.push(root);
        depthStack.push(1);

        int maxHeight = 0;

        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            int depth = depthStack.pop();

            maxHeight = Math.max(maxHeight, depth);

            if (node.left != null) {
                nodeStack.push(node.left);
                depthStack.push(depth + 1);
            }

            if (node.right != null) {
                nodeStack.push(node.right);
                depthStack.push(depth + 1);
            }
        }

        return maxHeight;
    }

    // Approach 4: Optimized for BST properties - can find min/max height
    // efficiently
    public int minHeight(TreeNode root) {
        if (root == null)
            return 0;

        int leftMin = minHeight(root.left);
        int rightMin = minHeight(root.right);

        return 1 + Math.min(leftMin, rightMin);
    }

    // Check if BST is balanced (height difference <= 1)
    public boolean isBalanced(TreeNode root) {
        return checkBalance(root) != -1;
    }

    private int checkBalance(TreeNode root) {
        if (root == null)
            return 0;

        int leftHeight = checkBalance(root.left);
        if (leftHeight == -1)
            return -1;

        int rightHeight = checkBalance(root.right);
        if (rightHeight == -1)
            return -1;

        if (Math.abs(leftHeight - rightHeight) > 1) {
            return -1;
        }

        return 1 + Math.max(leftHeight, rightHeight);
    }

    // Helper: Build BST for testing
    public TreeNode buildBST(int[] nums) {
        if (nums.length == 0)
            return null;

        TreeNode root = new TreeNode(nums[0]);
        for (int i = 1; i < nums.length; i++) {
            insertIntoBST(root, nums[i]);
        }

        return root;
    }

    private TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else {
            root.right = insertIntoBST(root.right, val);
        }

        return root;
    }

    public static void main(String[] args) {
        FindHeightOfBST solution = new FindHeightOfBST();

        // Test Case 1: Balanced BST
        TreeNode root1 = new TreeNode(5);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(4);
        root1.right.right = new TreeNode(10);

        System.out.println("=== Test Case 1: Balanced BST ===");
        System.out.println("Height (Recursive): " + solution.height(root1)); // Expected: 3
        System.out.println("Height (BFS): " + solution.heightIterative(root1)); // Expected: 3
        System.out.println("Height (DFS): " + solution.heightIterativeDFS(root1)); // Expected: 3
        System.out.println("Min Height: " + solution.minHeight(root1)); // Expected: 2
        System.out.println("Is Balanced: " + solution.isBalanced(root1)); // Expected: true
        System.out.println();

        // Test Case 2: Single node
        TreeNode root2 = new TreeNode(42);
        System.out.println("=== Test Case 2: Single Node ===");
        System.out.println("Height: " + solution.height(root2)); // Expected: 1
        System.out.println("Is Balanced: " + solution.isBalanced(root2)); // Expected: true
        System.out.println();

        // Test Case 3: Empty tree
        TreeNode root3 = null;
        System.out.println("=== Test Case 3: Empty Tree ===");
        System.out.println("Height: " + solution.height(root3)); // Expected: 0
        System.out.println("Is Balanced: " + solution.isBalanced(root3)); // Expected: true
        System.out.println();

        // Test Case 4: Right skewed tree (unbalanced)
        TreeNode root4 = new TreeNode(1);
        root4.right = new TreeNode(2);
        root4.right.right = new TreeNode(3);
        root4.right.right.right = new TreeNode(4);

        System.out.println("=== Test Case 4: Right Skewed Tree ===");
        System.out.println("Height (Recursive): " + solution.height(root4)); // Expected: 4
        System.out.println("Height (BFS): " + solution.heightIterative(root4)); // Expected: 4
        System.out.println("Min Height: " + solution.minHeight(root4)); // Expected: 1
        System.out.println("Is Balanced: " + solution.isBalanced(root4)); // Expected: false
        System.out.println();

        // Test Case 5: Perfect BST
        TreeNode root5 = solution.buildBST(new int[] { 4, 2, 6, 1, 3, 5, 7 });
        System.out.println("=== Test Case 5: Perfect BST ===");
        System.out.println("Height: " + solution.height(root5)); // Expected: 3
        System.out.println("Min Height: " + solution.minHeight(root5)); // Expected: 3
        System.out.println("Is Balanced: " + solution.isBalanced(root5)); // Expected: true
        System.out.println();

        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        TreeNode largeBST = solution.buildBST(new int[] { 50, 25, 75, 12, 37, 62, 87, 6, 18, 31, 43 });

        long startTime, endTime;

        startTime = System.nanoTime();
        int recursiveResult = solution.height(largeBST);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + recursiveResult + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int bfsResult = solution.heightIterative(largeBST);
        endTime = System.nanoTime();
        System.out.println("BFS: " + bfsResult + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int dfsResult = solution.heightIterativeDFS(largeBST);
        endTime = System.nanoTime();
        System.out.println("DFS: " + dfsResult + " (Time: " + (endTime - startTime) + " ns)");
    }
}
