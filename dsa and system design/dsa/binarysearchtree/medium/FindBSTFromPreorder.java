package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * LeetCode 1008: Construct Binary Search Tree from Preorder Traversal
 * https://leetcode.com/problems/construct-binary-search-tree-from-preorder-traversal/
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: High
 *
 * Description:
 * Given preorder traversal of a BST, construct the BST.
 * You may assume that duplicates do not exist in the tree.
 *
 * Constraints:
 * - 1 <= preorder.length <= 100
 * - 1 <= preorder[i] <= 1000
 * - All the values of preorder are unique.
 * 
 * Follow-up Questions:
 * 1. Can you solve it without using extra space for bounds?
 * 2. What's the time complexity of your solution?
 * 3. How would you handle duplicates if they were allowed?
 */
public class FindBSTFromPreorder {

    // Approach 1: Using Stack - O(n) time, O(n) space
    public TreeNode bstFromPreorder(int[] preorder) {
        if (preorder == null || preorder.length == 0)
            return null;

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        TreeNode root = new TreeNode(preorder[0]);
        stack.push(root);

        // Process from second element to last
        for (int i = 1; i < preorder.length; i++) {
            TreeNode node = new TreeNode(preorder[i]);
            TreeNode parent = null;

            // Find the correct position to insert this node
            while (!stack.isEmpty() && stack.peek().val < node.val) {
                parent = stack.pop();
            }

            if (parent != null) {
                parent.right = node;
            } else {
                stack.peek().left = node;
            }

            stack.push(node);
        }

        return root;
    }

    // Approach 2: Recursive with Bounds - O(n) time, O(h) space
    public TreeNode bstFromPreorderRecursive(int[] preorder) {
        if (preorder == null || preorder.length == 0)
            return null;
        int[] index = { 0 };
        return buildTree(preorder, index, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode buildTree(int[] preorder, int[] index, int min, int max) {
        if (index[0] >= preorder.length)
            return null;

        int val = preorder[index[0]];
        if (val < min || val > max)
            return null;

        index[0]++;
        TreeNode root = new TreeNode(val);

        // In preorder: root, left, right
        // So we build left first, then right
        root.left = buildTree(preorder, index, min, val);
        root.right = buildTree(preorder, index, val, max);

        return root;
    }

    // Approach 3: Divide and Conquer - O(n log n) time, O(n) space
    public TreeNode bstFromPreorderDivideConquer(int[] preorder) {
        if (preorder == null || preorder.length == 0)
            return null;
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

    // Approach 4: Using Binary Search for Optimization - O(n log n) time, O(n)
    // space
    public TreeNode bstFromPreorderBinarySearch(int[] preorder) {
        if (preorder == null || preorder.length == 0)
            return null;
        return buildWithBinarySearch(preorder, 0, preorder.length - 1);
    }

    private TreeNode buildWithBinarySearch(int[] preorder, int start, int end) {
        if (start > end)
            return null;

        TreeNode root = new TreeNode(preorder[start]);

        // Use binary search to find the first element greater than root
        int splitIndex = binarySearchGreater(preorder, start + 1, end, root.val);

        root.left = buildWithBinarySearch(preorder, start + 1, splitIndex - 1);
        root.right = buildWithBinarySearch(preorder, splitIndex, end);

        return root;
    }

    private int binarySearchGreater(int[] arr, int start, int end, int target) {
        int left = start, right = end + 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] > target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    // Approach 5: Morris-like Traversal Simulation - O(n) time, O(1) space
    public TreeNode bstFromPreorderMorris(int[] preorder) {
        if (preorder == null || preorder.length == 0)
            return null;

        TreeNode root = new TreeNode(preorder[0]);
        TreeNode current = root;

        for (int i = 1; i < preorder.length; i++) {
            TreeNode node = new TreeNode(preorder[i]);

            if (preorder[i] < current.val) {
                // Go left
                current.left = node;
                current = node;
            } else {
                // Find the correct position to insert
                TreeNode parent = findInsertionPoint(root, preorder[i]);
                if (parent.val < preorder[i]) {
                    parent.right = node;
                } else {
                    parent.left = node;
                }
                current = node;
            }
        }

        return root;
    }

    private TreeNode findInsertionPoint(TreeNode root, int val) {
        TreeNode current = root;
        TreeNode parent = null;

        while (current != null) {
            parent = current;
            if (val < current.val) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return parent;
    }

    public static void main(String[] args) {
        FindBSTFromPreorder solution = new FindBSTFromPreorder();

        // Test case 1: [8,5,1,7,10,12] -> preorder of balanced BST
        int[] preorder1 = { 8, 5, 1, 7, 10, 12 };
        System.out.println("Test Case 1: " + java.util.Arrays.toString(preorder1));
        TreeNode root1 = solution.bstFromPreorder(preorder1);
        System.out.println("Stack approach - Inorder: " + getInorder(root1));

        TreeNode root1b = solution.bstFromPreorderRecursive(preorder1);
        System.out.println("Recursive approach - Inorder: " + getInorder(root1b));

        TreeNode root1c = solution.bstFromPreorderDivideConquer(preorder1);
        System.out.println("Divide & Conquer - Inorder: " + getInorder(root1c));

        TreeNode root1d = solution.bstFromPreorderBinarySearch(preorder1);
        System.out.println("Binary Search - Inorder: " + getInorder(root1d));

        // Test case 2: Single node
        int[] preorder2 = { 42 };
        System.out.println("\nTest Case 2: " + java.util.Arrays.toString(preorder2));
        TreeNode root2 = solution.bstFromPreorder(preorder2);
        System.out.println("Single node result: " + getInorder(root2));

        // Test case 3: Right skewed tree
        int[] preorder3 = { 1, 2, 3, 4, 5 };
        System.out.println("\nTest Case 3: " + java.util.Arrays.toString(preorder3));
        TreeNode root3 = solution.bstFromPreorder(preorder3);
        System.out.println("Right skewed tree: " + getInorder(root3));

        // Test case 4: Left skewed tree
        int[] preorder4 = { 5, 4, 3, 2, 1 };
        System.out.println("\nTest Case 4: " + java.util.Arrays.toString(preorder4));
        TreeNode root4 = solution.bstFromPreorder(preorder4);
        System.out.println("Left skewed tree: " + getInorder(root4));

        // Test case 5: Complex balanced tree
        int[] preorder5 = { 10, 5, 1, 7, 6, 8, 15, 12, 20 };
        System.out.println("\nTest Case 5: " + java.util.Arrays.toString(preorder5));
        TreeNode root5 = solution.bstFromPreorder(preorder5);
        System.out.println("Complex tree: " + getInorder(root5));

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

    private static void performanceTest(FindBSTFromPreorder solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger preorder sequence
        int[] largePreorder = { 10, 5, 2, 1, 3, 7, 6, 8, 15, 12, 18, 20 };

        long startTime, endTime;

        // Test stack approach
        startTime = System.nanoTime();
        TreeNode result1 = solution.bstFromPreorder(largePreorder);
        endTime = System.nanoTime();
        System.out.println("Stack approach: " + getInorder(result1).size() + " nodes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test recursive approach
        startTime = System.nanoTime();
        TreeNode result2 = solution.bstFromPreorderRecursive(largePreorder);
        endTime = System.nanoTime();
        System.out.println("Recursive approach: " + getInorder(result2).size() + " nodes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test divide and conquer approach
        startTime = System.nanoTime();
        TreeNode result3 = solution.bstFromPreorderDivideConquer(largePreorder);
        endTime = System.nanoTime();
        System.out.println("Divide & Conquer: " + getInorder(result3).size() + " nodes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test binary search approach
        startTime = System.nanoTime();
        TreeNode result4 = solution.bstFromPreorderBinarySearch(largePreorder);
        endTime = System.nanoTime();
        System.out.println("Binary Search: " + getInorder(result4).size() + " nodes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Verify all approaches produce the same result
        System.out.println("\nVerification:");
        boolean allSame = getInorder(result1).equals(getInorder(result2)) &&
                getInorder(result2).equals(getInorder(result3)) &&
                getInorder(result3).equals(getInorder(result4));
        System.out.println("All approaches produce same inorder: " + allSame);
    }
}