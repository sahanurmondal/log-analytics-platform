package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode 538: Convert BST to Greater Tree (Hard Variant)
 * https://leetcode.com/problems/convert-bst-to-greater-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Meta, Apple
 * Frequency: High
 *
 * Description:
 * Given a BST, convert it to a Greater Tree such that every key of the original
 * BST is changed to the original key plus the sum of all keys greater than the
 * original key. This hard variant includes advanced techniques and
 * optimizations.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * - All node values are unique
 *
 * Follow-up Questions:
 * 1. Can you solve it with constant space?
 * 2. What if we need to handle concurrent modifications?
 * 3. Can you use Morris traversal?
 * 4. What if the tree is extremely large?
 */
public class ConvertBSTToGreaterTree {

    // Approach 1: Reverse Inorder Traversal (Recursive) - O(n) time, O(h) space
    private int sum = 0;

    public TreeNode convertBST(TreeNode root) {
        sum = 0;
        reverseInorder(root);
        return root;
    }

    private void reverseInorder(TreeNode node) {
        if (node == null)
            return;

        // Traverse right subtree first (larger values)
        reverseInorder(node.right);

        // Process current node
        sum += node.val;
        node.val = sum;

        // Traverse left subtree (smaller values)
        reverseInorder(node.left);
    }

    // Approach 2: Iterative with Stack - O(n) time, O(h) space
    public TreeNode convertBSTIterative(TreeNode root) {
        if (root == null)
            return null;

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        TreeNode current = root;
        int sum = 0;

        // Reverse inorder traversal using stack
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

    // Approach 3: Morris Traversal (Constant Space) - O(n) time, O(1) space
    public TreeNode convertBSTMorris(TreeNode root) {
        if (root == null)
            return null;

        TreeNode current = root;
        int sum = 0;

        // Reverse Morris Traversal (right -> root -> left)
        while (current != null) {
            if (current.right == null) {
                // Visit current node
                sum += current.val;
                current.val = sum;
                current = current.left;
            } else {
                // Find inorder successor (leftmost node in right subtree)
                TreeNode successor = current.right;
                while (successor.left != null && successor.left != current) {
                    successor = successor.left;
                }

                if (successor.left == null) {
                    // Create thread
                    successor.left = current;
                    current = current.right;
                } else {
                    // Remove thread and visit current
                    successor.left = null;
                    sum += current.val;
                    current.val = sum;
                    current = current.left;
                }
            }
        }

        return root;
    }

    // Approach 4: Two-Pass Solution with Array - O(n) time, O(n) space
    public TreeNode convertBSTTwoPass(TreeNode root) {
        if (root == null)
            return null;

        // First pass: collect all values in descending order
        java.util.List<Integer> values = new java.util.ArrayList<>();
        reverseInorderCollect(root, values);

        // Calculate prefix sums
        for (int i = 1; i < values.size(); i++) {
            values.set(i, values.get(i) + values.get(i - 1));
        }

        // Second pass: update the tree
        int[] index = { 0 };
        reverseInorderUpdate(root, values, index);

        return root;
    }

    private void reverseInorderCollect(TreeNode node, java.util.List<Integer> values) {
        if (node == null)
            return;
        reverseInorderCollect(node.right, values);
        values.add(node.val);
        reverseInorderCollect(node.left, values);
    }

    private void reverseInorderUpdate(TreeNode node, java.util.List<Integer> values, int[] index) {
        if (node == null)
            return;
        reverseInorderUpdate(node.right, values, index);
        node.val = values.get(index[0]++);
        reverseInorderUpdate(node.left, values, index);
    }

    public static void main(String[] args) {
        ConvertBSTToGreaterTree solution = new ConvertBSTToGreaterTree();

        // Test case 1: Normal BST [4,1,6,0,2,5,7,null,null,null,3,null,null,null,8]
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(6);
        root1.left.left = new TreeNode(0);
        root1.left.right = new TreeNode(2);
        root1.right.left = new TreeNode(5);
        root1.right.right = new TreeNode(7);
        root1.left.right.right = new TreeNode(3);
        root1.right.right.right = new TreeNode(8);

        System.out.println("Test Case 1 - Original tree inorder:");
        printInorder(root1);

        // Test recursive approach
        TreeNode result1 = deepCopy(root1);
        solution.convertBST(result1);
        System.out.println("\nRecursive result:");
        printInorder(result1);

        // Test iterative approach
        TreeNode result2 = deepCopy(root1);
        solution.convertBSTIterative(result2);
        System.out.println("\nIterative result:");
        printInorder(result2);

        // Test Morris approach
        TreeNode result3 = deepCopy(root1);
        solution.convertBSTMorris(result3);
        System.out.println("\nMorris result:");
        printInorder(result3);

        // Test two-pass approach
        TreeNode result4 = deepCopy(root1);
        solution.convertBSTTwoPass(result4);
        System.out.println("\nTwo-pass result:");
        printInorder(result4);

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Single node
        TreeNode single = new TreeNode(5);
        TreeNode singleResult = solution.convertBST(deepCopy(single));
        System.out.println("Single node (5): " + singleResult.val);

        // Empty tree
        TreeNode empty = solution.convertBST(null);
        System.out.println("Empty tree: " + (empty == null ? "null" : empty.val));

        // Performance test
        performanceTest(solution);
    }

    private static TreeNode deepCopy(TreeNode root) {
        if (root == null)
            return null;
        TreeNode copy = new TreeNode(root.val);
        copy.left = deepCopy(root.left);
        copy.right = deepCopy(root.right);
        return copy;
    }

    private static void printInorder(TreeNode root) {
        if (root == null)
            return;
        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
    }

    private static void performanceTest(ConvertBSTToGreaterTree solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger balanced BST
        TreeNode largeRoot = createBalancedBST(1, 1000);

        long startTime, endTime;

        // Test recursive approach
        startTime = System.nanoTime();
        TreeNode result1 = solution.convertBST(deepCopy(largeRoot));
        endTime = System.nanoTime();
        System.out.println("Recursive: " + getSum(result1) + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test iterative approach
        startTime = System.nanoTime();
        TreeNode result2 = solution.convertBSTIterative(deepCopy(largeRoot));
        endTime = System.nanoTime();
        System.out.println("Iterative: " + getSum(result2) + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Morris approach
        startTime = System.nanoTime();
        TreeNode result3 = solution.convertBSTMorris(deepCopy(largeRoot));
        endTime = System.nanoTime();
        System.out.println("Morris: " + getSum(result3) + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test two-pass approach
        startTime = System.nanoTime();
        TreeNode result4 = solution.convertBSTTwoPass(deepCopy(largeRoot));
        endTime = System.nanoTime();
        System.out.println("Two-pass: " + getSum(result4) + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");
    }

    private static long getSum(TreeNode root) {
        if (root == null)
            return 0;
        return root.val + getSum(root.left) + getSum(root.right);
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
