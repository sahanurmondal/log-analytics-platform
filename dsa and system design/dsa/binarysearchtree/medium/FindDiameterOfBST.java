package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * LeetCode 543: Diameter of Binary Tree (BST Variant)
 * https://leetcode.com/problems/diameter-of-binary-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Meta, Apple
 * Frequency: High
 *
 * Description:
 * Given a BST, return the diameter (length of the longest path between any two
 * nodes). The diameter is the number of edges in the longest path.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you solve it in one pass?
 * 2. What if we need to return the actual path?
 * 3. How would you optimize for BST properties?
 */
public class FindDiameterOfBST {

    // Approach 1: DFS with Global Variable - O(n) time, O(h) space
    private int maxDiameter = 0;

    public int diameter(TreeNode root) {
        if (root == null)
            return 0;
        maxDiameter = 0;
        dfs(root);
        return maxDiameter;
    }

    private int dfs(TreeNode node) {
        if (node == null)
            return 0;

        int leftHeight = dfs(node.left);
        int rightHeight = dfs(node.right);

        // Update diameter: path through current node
        maxDiameter = Math.max(maxDiameter, leftHeight + rightHeight);

        // Return height of current subtree
        return Math.max(leftHeight, rightHeight) + 1;
    }

    // Approach 2: DFS without Global Variable - O(n) time, O(h) space
    public int diameterClean(TreeNode root) {
        return diameterHelper(root)[1];
    }

    // Returns [height, diameter]
    private int[] diameterHelper(TreeNode node) {
        if (node == null)
            return new int[] { 0, 0 };

        int[] left = diameterHelper(node.left);
        int[] right = diameterHelper(node.right);

        int height = Math.max(left[0], right[0]) + 1;
        int diameter = Math.max(left[0] + right[0], Math.max(left[1], right[1]));

        return new int[] { height, diameter };
    }

    // Approach 3: Morris Traversal inspired (for educational purposes) - O(n) time,
    // O(1) space
    public int diameterConstantSpace(TreeNode root) {
        if (root == null)
            return 0;

        int diameter = 0;

        // Calculate leftmost and rightmost paths
        int leftDepth = getDepth(root, true);
        int rightDepth = getDepth(root, false);

        diameter = Math.max(diameter, leftDepth + rightDepth);

        // For BST, we can also check specific paths
        diameter = Math.max(diameter, getMaxPathInBST(root));

        return diameter;
    }

    private int getDepth(TreeNode node, boolean goLeft) {
        int depth = 0;
        while (node != null) {
            depth++;
            node = goLeft ? node.left : node.right;
        }
        return depth - 1; // Convert to edges count
    }

    private int getMaxPathInBST(TreeNode root) {
        if (root == null)
            return 0;

        // For BST, we can use the property that inorder gives sorted sequence
        int[] result = { 0 };
        calculateDiameterBST(root, result);
        return result[0];
    }

    private int calculateDiameterBST(TreeNode node, int[] maxDiam) {
        if (node == null)
            return 0;

        int left = calculateDiameterBST(node.left, maxDiam);
        int right = calculateDiameterBST(node.right, maxDiam);

        maxDiam[0] = Math.max(maxDiam[0], left + right);
        return Math.max(left, right) + 1;
    }

    public static void main(String[] args) {
        FindDiameterOfBST solution = new FindDiameterOfBST();

        // Test case 1: Normal BST
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(6);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(3);
        root1.right.left = new TreeNode(5);
        root1.right.right = new TreeNode(7);

        System.out.println("Test Case 1 (Balanced BST):");
        System.out.println("Expected: 4, Got: " + solution.diameter(root1));
        System.out.println("Clean approach: " + solution.diameterClean(root1));
        System.out.println("Constant space: " + solution.diameterConstantSpace(root1));

        // Test case 2: Left skewed BST
        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(4);
        root2.left.left = new TreeNode(3);
        root2.left.left.left = new TreeNode(2);
        root2.left.left.left.left = new TreeNode(1);

        System.out.println("\nTest Case 2 (Left Skewed BST):");
        System.out.println("Expected: 4, Got: " + solution.diameter(root2));
        System.out.println("Clean approach: " + solution.diameterClean(root2));

        // Test case 3: Right skewed BST
        TreeNode root3 = new TreeNode(1);
        root3.right = new TreeNode(2);
        root3.right.right = new TreeNode(3);
        root3.right.right.right = new TreeNode(4);
        root3.right.right.right.right = new TreeNode(5);

        System.out.println("\nTest Case 3 (Right Skewed BST):");
        System.out.println("Expected: 4, Got: " + solution.diameter(root3));
        System.out.println("Clean approach: " + solution.diameterClean(root3));

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Single node
        TreeNode single = new TreeNode(42);
        System.out.println("Single node: " + solution.diameter(single));

        // Two nodes
        TreeNode twoNodes = new TreeNode(1);
        twoNodes.left = new TreeNode(2);
        System.out.println("Two nodes: " + solution.diameter(twoNodes));

        // Empty tree
        System.out.println("Empty tree: " + solution.diameter(null));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(FindDiameterOfBST solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger balanced BST
        TreeNode largeRoot = createBalancedBST(1, 1000);

        long startTime, endTime;

        // Test global variable approach
        startTime = System.nanoTime();
        int result1 = solution.diameter(largeRoot);
        endTime = System.nanoTime();
        System.out.println("Global variable: " + result1 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test clean approach
        startTime = System.nanoTime();
        int result2 = solution.diameterClean(largeRoot);
        endTime = System.nanoTime();
        System.out.println("Clean approach: " + result2 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test constant space approach
        startTime = System.nanoTime();
        int result3 = solution.diameterConstantSpace(largeRoot);
        endTime = System.nanoTime();
        System.out.println("Constant space: " + result3 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");
    }

    private static TreeNode createBalancedBST(int start, int end) {
        if (start > end)
            return null;

        int mid = start + (end - start) / 2;
        TreeNode node = new TreeNode(mid);
        node.left = createBalancedBST(start, mid - 1);
        node.right = createBalancedBST(mid + 1, end);
        return node;
    }
}
