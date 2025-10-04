package binarysearchtree.medium;

import binarysearchtree.TreeNode;
import java.util.*;

/**
 * LeetCode Variation: Find Maximum Value in BST
 * https://leetcode.com/problems/find-mode-in-binary-search-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 40+ interviews)
 *
 * Description:
 * Given the root of a binary search tree (BST), find the maximum value in the
 * BST.
 * In BST, the maximum value is always the rightmost node.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * - The tree is guaranteed to be a valid BST.
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively and recursively?
 * 2. What if we need to find the kth maximum element?
 * 3. How would you handle if the tree is modified frequently?
 * 4. Can you find max without accessing the rightmost node?
 */
public class FindMaxInBST {

    // Approach 1: Recursive - O(h) time, O(h) space where h is height
    public int findMax(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Tree is empty");
        }

        // In BST, maximum is always in the rightmost node
        if (root.right == null) {
            return root.val;
        }

        return findMax(root.right);
    }

    // Approach 2: Iterative - O(h) time, O(1) space
    public int findMaxIterative(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Tree is empty");
        }

        TreeNode current = root;
        while (current.right != null) {
            current = current.right;
        }

        return current.val;
    }

    // Approach 3: Using Morris Traversal - O(n) time, O(1) space
    public int findMaxMorris(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Tree is empty");
        }

        TreeNode current = root;
        int maxVal = root.val;

        while (current != null) {
            if (current.right == null) {
                maxVal = Math.max(maxVal, current.val);
                current = current.left;
            } else {
                // Find inorder predecessor
                TreeNode predecessor = current.right;
                while (predecessor.left != null && predecessor.left != current) {
                    predecessor = predecessor.left;
                }

                if (predecessor.left == null) {
                    predecessor.left = current;
                    maxVal = Math.max(maxVal, current.val);
                    current = current.right;
                } else {
                    predecessor.left = null;
                    current = current.left;
                }
            }
        }

        return maxVal;
    }

    // Approach 4: Using Inorder Traversal - O(n) time, O(h) space
    public int findMaxInorder(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Tree is empty");
        }

        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);
        return inorder.get(inorder.size() - 1); // Last element in inorder is max
    }

    private void inorderTraversal(TreeNode root, List<Integer> inorder) {
        if (root == null)
            return;

        inorderTraversal(root.left, inorder);
        inorder.add(root.val);
        inorderTraversal(root.right, inorder);
    }

    // Follow-up 1: Find Kth Maximum Element
    public int findKthMax(TreeNode root, int k) {
        if (root == null || k <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);

        if (k > inorder.size()) {
            throw new IllegalArgumentException("k is larger than number of nodes");
        }

        return inorder.get(inorder.size() - k);
    }

    // Follow-up 2: Find Max Node (returns the node, not just value)
    public TreeNode findMaxNode(TreeNode root) {
        if (root == null) {
            return null;
        }

        while (root.right != null) {
            root = root.right;
        }

        return root;
    }

    // Follow-up 3: Find Max in range [low, high]
    public int findMaxInRange(TreeNode root, int low, int high) {
        if (root == null) {
            return Integer.MIN_VALUE;
        }

        // If current node is greater than high, search left subtree
        if (root.val > high) {
            return findMaxInRange(root.left, low, high);
        }

        // If current node is less than low, search right subtree
        if (root.val < low) {
            return findMaxInRange(root.right, low, high);
        }

        // Current node is in range
        int maxRight = findMaxInRange(root.right, low, high);
        return Math.max(root.val, maxRight);
    }

    // Helper: Validate BST property
    public boolean isValidBST(TreeNode root) {
        return isValidBSTHelper(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean isValidBSTHelper(TreeNode node, long minVal, long maxVal) {
        if (node == null)
            return true;

        if (node.val <= minVal || node.val >= maxVal) {
            return false;
        }

        return isValidBSTHelper(node.left, minVal, node.val) &&
                isValidBSTHelper(node.right, node.val, maxVal);
    }

    // Helper: Print tree structure
    public void printInorder(TreeNode root) {
        if (root == null)
            return;

        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
    }

    public static void main(String[] args) {
        FindMaxInBST solution = new FindMaxInBST();

        // Test Case 1: Normal BST
        TreeNode root1 = new TreeNode(5);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(4);
        root1.right.right = new TreeNode(10);

        System.out.println("=== Test Case 1: Normal BST ===");
        System.out.print("Inorder: ");
        solution.printInorder(root1);
        System.out.println();

        System.out.println("Max (Recursive): " + solution.findMax(root1)); // Expected: 10
        System.out.println("Max (Iterative): " + solution.findMaxIterative(root1)); // Expected: 10
        System.out.println("Max (Morris): " + solution.findMaxMorris(root1)); // Expected: 10
        System.out.println("Max (Inorder): " + solution.findMaxInorder(root1)); // Expected: 10
        System.out.println("2nd Max: " + solution.findKthMax(root1, 2)); // Expected: 7
        System.out.println("Max in range [2,8]: " + solution.findMaxInRange(root1, 2, 8)); // Expected: 7
        System.out.println();

        // Test Case 2: Single node
        TreeNode root2 = new TreeNode(42);
        System.out.println("=== Test Case 2: Single Node ===");
        System.out.println("Max: " + solution.findMax(root2)); // Expected: 42
        System.out.println();

        // Test Case 3: Right skewed tree
        TreeNode root3 = new TreeNode(1);
        root3.right = new TreeNode(2);
        root3.right.right = new TreeNode(3);
        root3.right.right.right = new TreeNode(4);

        System.out.println("=== Test Case 3: Right Skewed ===");
        System.out.println("Max: " + solution.findMax(root3)); // Expected: 4
        System.out.println();

        // Test Case 4: Left skewed tree
        TreeNode root4 = new TreeNode(4);
        root4.left = new TreeNode(3);
        root4.left.left = new TreeNode(2);
        root4.left.left.left = new TreeNode(1);

        System.out.println("=== Test Case 4: Left Skewed ===");
        System.out.println("Max: " + solution.findMax(root4)); // Expected: 4
        System.out.println();

        // Test Case 5: Perfect BST
        TreeNode root5 = new TreeNode(4);
        root5.left = new TreeNode(2);
        root5.right = new TreeNode(6);
        root5.left.left = new TreeNode(1);
        root5.left.right = new TreeNode(3);
        root5.right.left = new TreeNode(5);
        root5.right.right = new TreeNode(7);

        System.out.println("=== Test Case 5: Perfect BST ===");
        System.out.print("Inorder: ");
        solution.printInorder(root5);
        System.out.println();
        System.out.println("Max: " + solution.findMax(root5)); // Expected: 7
        System.out.println("3rd Max: " + solution.findKthMax(root5, 3)); // Expected: 5
        System.out.println("Max in range [2,5]: " + solution.findMaxInRange(root5, 2, 5)); // Expected: 5

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        long startTime, endTime;

        // Large BST for performance testing
        TreeNode largeBST = buildLargeBST(1000);

        startTime = System.nanoTime();
        int result1 = solution.findMax(largeBST);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + result1 + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int result2 = solution.findMaxIterative(largeBST);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result2 + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int result3 = solution.findMaxInorder(largeBST);
        endTime = System.nanoTime();
        System.out.println("Inorder: " + result3 + " (Time: " + (endTime - startTime) + " ns)");
    }

    // Helper to build large BST for performance testing
    private static TreeNode buildLargeBST(int n) {
        TreeNode root = new TreeNode(n / 2);
        for (int i = 1; i < n; i++) {
            if (i != n / 2) {
                insertBST(root, i);
            }
        }
        return root;
    }

    private static TreeNode insertBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insertBST(root.left, val);
        } else {
            root.right = insertBST(root.right, val);
        }

        return root;
    }
}
