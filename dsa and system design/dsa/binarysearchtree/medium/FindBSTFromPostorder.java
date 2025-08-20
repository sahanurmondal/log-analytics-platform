package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * LeetCode 1008: Construct Binary Search Tree from Postorder Traversal
 * https://leetcode.com/problems/construct-binary-search-tree-from-postorder-traversal/
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: Medium
 *
 * Description:
 * Given postorder traversal of a BST, construct the BST.
 * You may assume that duplicates do not exist in the tree.
 *
 * Constraints:
 * - 1 <= postorder.length <= 100
 * - 1 <= postorder[i] <= 1000
 * - All the values of postorder are unique.
 * 
 * Follow-up Questions:
 * 1. Can you solve it without using extra space for bounds?
 * 2. What's the time complexity of your solution?
 * 3. How would you handle duplicates if they were allowed?
 */
public class FindBSTFromPostorder {

    // Approach 1: Using Stack - O(n) time, O(n) space
    public TreeNode bstFromPostorder(int[] postorder) {
        if (postorder == null || postorder.length == 0)
            return null;

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        TreeNode root = new TreeNode(postorder[postorder.length - 1]);
        stack.push(root);

        // Process from second last element to first
        for (int i = postorder.length - 2; i >= 0; i--) {
            TreeNode node = new TreeNode(postorder[i]);
            TreeNode parent = null;

            // Find the correct position to insert this node
            while (!stack.isEmpty() && stack.peek().val > node.val) {
                parent = stack.pop();
            }

            if (parent != null) {
                parent.left = node;
            } else {
                stack.peek().right = node;
            }

            stack.push(node);
        }

        return root;
    }

    // Approach 2: Recursive with Bounds - O(n) time, O(h) space
    public TreeNode bstFromPostorderRecursive(int[] postorder) {
        if (postorder == null || postorder.length == 0)
            return null;
        int[] index = { postorder.length - 1 };
        return buildTree(postorder, index, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode buildTree(int[] postorder, int[] index, int min, int max) {
        if (index[0] < 0)
            return null;

        int val = postorder[index[0]];
        if (val < min || val > max)
            return null;

        index[0]--;
        TreeNode root = new TreeNode(val);

        // In postorder: left, right, root
        // So we build right first, then left
        root.right = buildTree(postorder, index, val, max);
        root.left = buildTree(postorder, index, min, val);

        return root;
    }

    // Approach 3: Convert to Preorder and Build - O(n) time, O(n) space
    public TreeNode bstFromPostorderConvert(int[] postorder) {
        if (postorder == null || postorder.length == 0)
            return null;

        // Convert postorder to preorder by reversing
        int[] preorder = new int[postorder.length];
        for (int i = 0; i < postorder.length; i++) {
            preorder[i] = postorder[postorder.length - 1 - i];
        }

        return buildFromPreorder(preorder, 0, preorder.length - 1);
    }

    private TreeNode buildFromPreorder(int[] preorder, int start, int end) {
        if (start > end)
            return null;

        TreeNode root = new TreeNode(preorder[start]);
        int i = start + 1;

        // Find the first element greater than root
        while (i <= end && preorder[i] < root.val) {
            i++;
        }

        root.left = buildFromPreorder(preorder, start + 1, i - 1);
        root.right = buildFromPreorder(preorder, i, end);

        return root;
    }

    public static void main(String[] args) {
        FindBSTFromPostorder solution = new FindBSTFromPostorder();

        // Test case 1: [1,7,5,50,40,10] -> postorder of tree with root 10
        int[] postorder1 = { 1, 7, 5, 50, 40, 10 };
        System.out.println("Test Case 1: " + java.util.Arrays.toString(postorder1));
        TreeNode root1 = solution.bstFromPostorder(postorder1);
        System.out.println("Stack approach - Inorder: " + getInorder(root1));

        TreeNode root1b = solution.bstFromPostorderRecursive(postorder1);
        System.out.println("Recursive approach - Inorder: " + getInorder(root1b));

        TreeNode root1c = solution.bstFromPostorderConvert(postorder1);
        System.out.println("Convert approach - Inorder: " + getInorder(root1c));

        // Test case 2: Single node
        int[] postorder2 = { 42 };
        System.out.println("\nTest Case 2: " + java.util.Arrays.toString(postorder2));
        TreeNode root2 = solution.bstFromPostorder(postorder2);
        System.out.println("Single node result: " + getInorder(root2));

        // Test case 3: Ascending sequence (right skewed tree)
        int[] postorder3 = { 1, 2, 3, 4, 5 };
        System.out.println("\nTest Case 3: " + java.util.Arrays.toString(postorder3));
        TreeNode root3 = solution.bstFromPostorder(postorder3);
        System.out.println("Ascending sequence: " + getInorder(root3));

        // Test case 4: Right skewed tree
        // This is actually [5,4,3,2,1] in postorder for left skewed
        int[] actualPostorder4 = { 1, 2, 3, 4, 5 }; // postorder of right skewed [1,2,3,4,5]
        System.out.println("\nTest Case 4: " + java.util.Arrays.toString(actualPostorder4));
        TreeNode root4 = solution.bstFromPostorder(actualPostorder4);
        System.out.println("Right skewed tree: " + getInorder(root4));

        // Performance test
        performanceTest(solution);
    }

    private static java.util.List<Integer> getInorder(TreeNode root) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }

    private static void inorderTraversal(TreeNode node, java.util.List<Integer> result) {
        if (node == null)
            return;
        inorderTraversal(node.left, result);
        result.add(node.val);
        inorderTraversal(node.right, result);
    }

    private static void performanceTest(FindBSTFromPostorder solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger postorder sequence
        int[] largePostorder = { 1, 3, 2, 6, 8, 7, 5, 15, 20, 18, 10 };

        long startTime, endTime;

        // Test stack approach
        startTime = System.nanoTime();
        TreeNode result1 = solution.bstFromPostorder(largePostorder);
        endTime = System.nanoTime();
        System.out.println("Stack approach: " + getInorder(result1).size() + " nodes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test recursive approach
        startTime = System.nanoTime();
        TreeNode result2 = solution.bstFromPostorderRecursive(largePostorder);
        endTime = System.nanoTime();
        System.out.println("Recursive approach: " + getInorder(result2).size() + " nodes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test convert approach
        startTime = System.nanoTime();
        TreeNode result3 = solution.bstFromPostorderConvert(largePostorder);
        endTime = System.nanoTime();
        System.out.println("Convert approach: " + getInorder(result3).size() + " nodes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }
}
