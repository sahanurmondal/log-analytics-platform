package binarysearchtree.medium;

import binarysearchtree.TreeNode;
import java.util.*;

/**
 * LeetCode Variation: Find Minimum Value in BST
 * https://leetcode.com/problems/minimum-absolute-difference-in-bst/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 45+ interviews)
 *
 * Description:
 * Given the root of a binary search tree (BST), find the minimum value in the
 * BST.
 * In BST, the minimum value is always the leftmost node.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * - The tree is guaranteed to be a valid BST.
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively and recursively?
 * 2. What if we need to find the kth minimum element?
 * 3. How would you handle if the tree is modified frequently?
 * 4. Can you find min without accessing the leftmost node?
 */
public class FindMinInBST {

    // Approach 1: Recursive - O(h) time, O(h) space where h is height
    public int findMin(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Tree is empty");
        }

        // In BST, minimum is always in the leftmost node
        if (root.left == null) {
            return root.val;
        }

        return findMin(root.left);
    }

    // Approach 2: Iterative - O(h) time, O(1) space
    public int findMinIterative(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Tree is empty");
        }

        TreeNode current = root;
        while (current.left != null) {
            current = current.left;
        }

        return current.val;
    }

    // Approach 3: Using Morris Traversal - O(n) time, O(1) space
    public int findMinMorris(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Tree is empty");
        }

        TreeNode current = root;
        int minVal = root.val;

        while (current != null) {
            if (current.left == null) {
                minVal = Math.min(minVal, current.val);
                current = current.right;
            } else {
                // Find inorder predecessor
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    predecessor.right = current;
                    minVal = Math.min(minVal, current.val);
                    current = current.left;
                } else {
                    predecessor.right = null;
                    current = current.right;
                }
            }
        }

        return minVal;
    }

    // Approach 4: Using Inorder Traversal - O(n) time, O(h) space
    public int findMinInorder(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Tree is empty");
        }

        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);
        return inorder.get(0); // First element in inorder is min
    }

    private void inorderTraversal(TreeNode root, List<Integer> inorder) {
        if (root == null)
            return;

        inorderTraversal(root.left, inorder);
        inorder.add(root.val);
        inorderTraversal(root.right, inorder);
    }

    // Follow-up 1: Find Kth Minimum Element
    public int findKthMin(TreeNode root, int k) {
        if (root == null || k <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);

        if (k > inorder.size()) {
            throw new IllegalArgumentException("k is larger than number of nodes");
        }

        return inorder.get(k - 1);
    }

    // Follow-up 2: Find Min Node (returns the node, not just value)
    public TreeNode findMinNode(TreeNode root) {
        if (root == null) {
            return null;
        }

        while (root.left != null) {
            root = root.left;
        }

        return root;
    }

    // Follow-up 3: Find Min in range [low, high]
    public int findMinInRange(TreeNode root, int low, int high) {
        if (root == null) {
            return Integer.MAX_VALUE;
        }

        // If current node is less than low, search right subtree
        if (root.val < low) {
            return findMinInRange(root.right, low, high);
        }

        // If current node is greater than high, search left subtree
        if (root.val > high) {
            return findMinInRange(root.left, low, high);
        }

        // Current node is in range
        int minLeft = findMinInRange(root.left, low, high);
        return Math.min(root.val, minLeft);
    }

    // Follow-up 4: Find minimum absolute difference in BST
    public int findMinAbsoluteDifference(TreeNode root) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);

        int minDiff = Integer.MAX_VALUE;
        for (int i = 1; i < inorder.size(); i++) {
            minDiff = Math.min(minDiff, inorder.get(i) - inorder.get(i - 1));
        }

        return minDiff;
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
        FindMinInBST solution = new FindMinInBST();

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

        System.out.println("Min (Recursive): " + solution.findMin(root1)); // Expected: 1
        System.out.println("Min (Iterative): " + solution.findMinIterative(root1)); // Expected: 1
        System.out.println("Min (Morris): " + solution.findMinMorris(root1)); // Expected: 1
        System.out.println("Min (Inorder): " + solution.findMinInorder(root1)); // Expected: 1
        System.out.println("2nd Min: " + solution.findKthMin(root1, 2)); // Expected: 3
        System.out.println("Min in range [2,8]: " + solution.findMinInRange(root1, 2, 8)); // Expected: 3
        System.out.println("Min Absolute Diff: " + solution.findMinAbsoluteDifference(root1)); // Expected: 1
        System.out.println();

        // Test Case 2: Single node
        TreeNode root2 = new TreeNode(42);
        System.out.println("=== Test Case 2: Single Node ===");
        System.out.println("Min: " + solution.findMin(root2)); // Expected: 42
        System.out.println();

        // Test Case 3: Left skewed tree
        TreeNode root3 = new TreeNode(4);
        root3.left = new TreeNode(3);
        root3.left.left = new TreeNode(2);
        root3.left.left.left = new TreeNode(1);

        System.out.println("=== Test Case 3: Left Skewed ===");
        System.out.println("Min: " + solution.findMin(root3)); // Expected: 1
        System.out.println();

        // Test Case 4: Perfect BST
        TreeNode root4 = new TreeNode(4);
        root4.left = new TreeNode(2);
        root4.right = new TreeNode(6);
        root4.left.left = new TreeNode(1);
        root4.left.right = new TreeNode(3);
        root4.right.left = new TreeNode(5);
        root4.right.right = new TreeNode(7);

        System.out.println("=== Test Case 4: Perfect BST ===");
        System.out.print("Inorder: ");
        solution.printInorder(root4);
        System.out.println();
        System.out.println("Min: " + solution.findMin(root4)); // Expected: 1
        System.out.println("3rd Min: " + solution.findKthMin(root4, 3)); // Expected: 3
        System.out.println("Min in range [2,5]: " + solution.findMinInRange(root4, 2, 5)); // Expected: 2
    }
}
