package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * LeetCode 538: Convert BST to Greater Tree (Medium Variant)
 * https://leetcode.com/problems/convert-bst-to-greater-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Meta, Apple
 * Frequency: High
 *
 * Description:
 * Given the root of a Binary Search Tree (BST), convert it to a Greater Tree
 * such that every key
 * of the original BST is changed to the original key plus the sum of all keys
 * greater than the
 * original key in BST.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * - All the values in the tree are unique.
 * - root is guaranteed to be a valid binary search tree.
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively?
 * 2. Can you solve it with O(1) space complexity?
 * 3. How would you handle negative values efficiently?
 */
public class ConvertBSTToGreaterTreeMedium {

    // Approach 1: Reverse Inorder Traversal (Recursive) - O(n) time, O(h) space
    private int sum = 0;

    public TreeNode convertBST(TreeNode root) {
        sum = 0; // Reset sum for each call
        reverseInorder(root);
        return root;
    }

    private void reverseInorder(TreeNode node) {
        if (node == null)
            return;

        // Traverse right first (larger values)
        reverseInorder(node.right);

        // Update current node
        sum += node.val;
        node.val = sum;

        // Traverse left (smaller values)
        reverseInorder(node.left);
    }

    // Approach 2: Iterative using Stack - O(n) time, O(h) space
    public TreeNode convertBSTIterative(TreeNode root) {
        if (root == null)
            return null;

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        TreeNode current = root;
        int sum = 0;

        while (current != null || !stack.isEmpty()) {
            // Go to the rightmost node
            while (current != null) {
                stack.push(current);
                current = current.right;
            }

            // Process current node
            current = stack.pop();
            sum += current.val;
            current.val = sum;

            // Move to left subtree
            current = current.left;
        }

        return root;
    }

    // Approach 3: Morris Traversal (Reverse Inorder) - O(n) time, O(1) space
    public TreeNode convertBSTMorris(TreeNode root) {
        if (root == null)
            return null;

        int sum = 0;
        TreeNode current = root;

        while (current != null) {
            if (current.right == null) {
                // Process current node
                sum += current.val;
                current.val = sum;
                current = current.left;
            } else {
                // Find inorder successor (rightmost node in right subtree)
                TreeNode successor = current.right;
                while (successor.left != null && successor.left != current) {
                    successor = successor.left;
                }

                if (successor.left == null) {
                    // Make current as left child of successor
                    successor.left = current;
                    current = current.right;
                } else {
                    // Revert changes
                    successor.left = null;
                    // Process current node
                    sum += current.val;
                    current.val = sum;
                    current = current.left;
                }
            }
        }

        return root;
    }

    // Approach 4: Two-pass solution (Collect then Update) - O(n) time, O(n) space
    public TreeNode convertBSTTwoPass(TreeNode root) {
        if (root == null)
            return null;

        // First pass: collect all values in sorted order
        java.util.List<Integer> values = new java.util.ArrayList<>();
        collectValues(root, values);

        // Calculate suffix sums
        int[] suffixSums = new int[values.size()];
        suffixSums[values.size() - 1] = values.get(values.size() - 1);
        for (int i = values.size() - 2; i >= 0; i--) {
            suffixSums[i] = suffixSums[i + 1] + values.get(i);
        }

        // Second pass: update tree with new values
        java.util.Map<Integer, Integer> valueMap = new java.util.HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            valueMap.put(values.get(i), suffixSums[i]);
        }

        updateTree(root, valueMap);
        return root;
    }

    private void collectValues(TreeNode node, java.util.List<Integer> values) {
        if (node == null)
            return;
        collectValues(node.left, values);
        values.add(node.val);
        collectValues(node.right, values);
    }

    private void updateTree(TreeNode node, java.util.Map<Integer, Integer> valueMap) {
        if (node == null)
            return;
        node.val = valueMap.get(node.val);
        updateTree(node.left, valueMap);
        updateTree(node.right, valueMap);
    }

    // Approach 5: Using TreeMap for sorted accumulation - O(n log n) time, O(n)
    // space
    public TreeNode convertBSTTreeMap(TreeNode root) {
        if (root == null)
            return null;

        java.util.TreeMap<Integer, Integer> valueMap = new java.util.TreeMap<>();

        // Collect all unique values
        collectUniqueValues(root, valueMap);

        // Calculate greater sums using TreeMap's sorted property
        java.util.Map<Integer, Integer> greaterSumMap = new java.util.HashMap<>();
        int runningSum = 0;

        // Iterate in reverse order (largest to smallest)
        java.util.NavigableMap<Integer, Integer> descendingMap = valueMap.descendingMap();
        for (int value : descendingMap.keySet()) {
            runningSum += value;
            greaterSumMap.put(value, runningSum);
        }

        // Update tree
        updateTreeWithMap(root, greaterSumMap);
        return root;
    }

    private void collectUniqueValues(TreeNode node, java.util.TreeMap<Integer, Integer> valueMap) {
        if (node == null)
            return;
        valueMap.put(node.val, 1);
        collectUniqueValues(node.left, valueMap);
        collectUniqueValues(node.right, valueMap);
    }

    private void updateTreeWithMap(TreeNode node, java.util.Map<Integer, Integer> greaterSumMap) {
        if (node == null)
            return;
        node.val = greaterSumMap.get(node.val);
        updateTreeWithMap(node.left, greaterSumMap);
        updateTreeWithMap(node.right, greaterSumMap);
    }

    // Helper methods for testing
    public java.util.List<Integer> inorderTraversal(TreeNode root) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        inorderHelper(root, result);
        return result;
    }

    private void inorderHelper(TreeNode node, java.util.List<Integer> result) {
        if (node == null)
            return;
        inorderHelper(node.left, result);
        result.add(node.val);
        inorderHelper(node.right, result);
    }

    // Deep copy tree for testing multiple approaches
    public TreeNode copyTree(TreeNode root) {
        if (root == null)
            return null;
        TreeNode newRoot = new TreeNode(root.val);
        newRoot.left = copyTree(root.left);
        newRoot.right = copyTree(root.right);
        return newRoot;
    }

    public static void main(String[] args) {
        ConvertBSTToGreaterTreeMedium solution = new ConvertBSTToGreaterTreeMedium();

        // Test case 1: Balanced BST [4,1,6,0,2,5,7,null,null,null,3,null,null,null,8]
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(6);
        root1.left.left = new TreeNode(0);
        root1.left.right = new TreeNode(2);
        root1.right.left = new TreeNode(5);
        root1.right.right = new TreeNode(7);
        root1.left.right.right = new TreeNode(3);
        root1.right.right.right = new TreeNode(8);

        System.out.println("Test Case 1: Balanced BST");
        System.out.println("Original inorder: " + solution.inorderTraversal(root1));

        TreeNode result1 = solution.convertBST(solution.copyTree(root1));
        System.out.println("Recursive result: " + solution.inorderTraversal(result1));

        TreeNode result2 = solution.convertBSTIterative(solution.copyTree(root1));
        System.out.println("Iterative result: " + solution.inorderTraversal(result2));

        TreeNode result3 = solution.convertBSTMorris(solution.copyTree(root1));
        System.out.println("Morris result: " + solution.inorderTraversal(result3));

        // Test case 2: Right skewed tree
        TreeNode root2 = new TreeNode(1);
        root2.right = new TreeNode(2);
        root2.right.right = new TreeNode(3);
        root2.right.right.right = new TreeNode(4);

        System.out.println("\nTest Case 2: Right skewed BST");
        System.out.println("Original inorder: " + solution.inorderTraversal(root2));
        TreeNode result4 = solution.convertBSTTwoPass(solution.copyTree(root2));
        System.out.println("Two-pass result: " + solution.inorderTraversal(result4));

        // Test case 3: Left skewed tree
        TreeNode root3 = new TreeNode(4);
        root3.left = new TreeNode(3);
        root3.left.left = new TreeNode(2);
        root3.left.left.left = new TreeNode(1);

        System.out.println("\nTest Case 3: Left skewed BST");
        System.out.println("Original inorder: " + solution.inorderTraversal(root3));
        TreeNode result5 = solution.convertBSTTreeMap(solution.copyTree(root3));
        System.out.println("TreeMap result: " + solution.inorderTraversal(result5));

        // Test case 4: Single node
        TreeNode root4 = new TreeNode(5);
        System.out.println("\nTest Case 4: Single node");
        System.out.println("Original: " + solution.inorderTraversal(root4));
        TreeNode result6 = solution.convertBST(solution.copyTree(root4));
        System.out.println("Result: " + solution.inorderTraversal(result6));

        // Test case 5: Tree with negative values
        TreeNode root5 = new TreeNode(0);
        root5.left = new TreeNode(-3);
        root5.right = new TreeNode(2);
        root5.left.left = new TreeNode(-5);
        root5.right.right = new TreeNode(4);

        System.out.println("\nTest Case 5: Tree with negative values");
        System.out.println("Original inorder: " + solution.inorderTraversal(root5));
        TreeNode result7 = solution.convertBST(solution.copyTree(root5));
        System.out.println("Result: " + solution.inorderTraversal(result7));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(ConvertBSTToGreaterTreeMedium solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger BST
        TreeNode largeRoot = createLargeBST();

        long startTime, endTime;

        // Test recursive approach
        startTime = System.nanoTime();
        solution.convertBST(solution.copyTree(largeRoot));
        endTime = System.nanoTime();
        System.out.println("Recursive: (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test iterative approach
        startTime = System.nanoTime();
        solution.convertBSTIterative(solution.copyTree(largeRoot));
        endTime = System.nanoTime();
        System.out.println("Iterative: (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Morris approach
        startTime = System.nanoTime();
        solution.convertBSTMorris(solution.copyTree(largeRoot));
        endTime = System.nanoTime();
        System.out.println("Morris: (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test two-pass approach
        startTime = System.nanoTime();
        solution.convertBSTTwoPass(solution.copyTree(largeRoot));
        endTime = System.nanoTime();
        System.out.println("Two-pass: (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test TreeMap approach
        startTime = System.nanoTime();
        solution.convertBSTTreeMap(solution.copyTree(largeRoot));
        endTime = System.nanoTime();
        System.out.println("TreeMap: (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Verify all approaches produce the same result
        TreeNode original = createLargeBST();
        java.util.List<Integer> result1 = solution.inorderTraversal(solution.convertBST(solution.copyTree(original)));
        java.util.List<Integer> result2 = solution
                .inorderTraversal(solution.convertBSTIterative(solution.copyTree(original)));
        java.util.List<Integer> result3 = solution
                .inorderTraversal(solution.convertBSTMorris(solution.copyTree(original)));

        System.out.println("\nVerification:");
        boolean allSame = result1.equals(result2) && result2.equals(result3);
        System.out.println("All approaches produce same result: " + allSame);
    }

    private static TreeNode createLargeBST() {
        TreeNode root = new TreeNode(50);

        // Left subtree
        root.left = new TreeNode(25);
        root.left.left = new TreeNode(10);
        root.left.right = new TreeNode(35);
        root.left.left.left = new TreeNode(5);
        root.left.left.right = new TreeNode(15);
        root.left.right.left = new TreeNode(30);
        root.left.right.right = new TreeNode(40);

        // Right subtree
        root.right = new TreeNode(75);
        root.right.left = new TreeNode(60);
        root.right.right = new TreeNode(85);
        root.right.left.left = new TreeNode(55);
        root.right.left.right = new TreeNode(65);
        root.right.right.left = new TreeNode(80);
        root.right.right.right = new TreeNode(90);

        return root;
    }
}