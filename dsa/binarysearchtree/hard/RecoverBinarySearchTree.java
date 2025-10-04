package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode 99: Recover Binary Search Tree
 * https://leetcode.com/problems/recover-binary-search-tree/
 *
 * Description:
 * Two elements of a BST are swapped by mistake. Recover the tree without
 * changing its structure.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 1000].
 * - -2^31 <= Node.val <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * 
 * Company Tags: Microsoft, Amazon, Google, Bloomberg
 * Difficulty: Medium/Hard
 */
public class RecoverBinarySearchTree {

    // Global variables for Morris traversal approach
    private TreeNode firstElement = null;
    private TreeNode secondElement = null;
    private TreeNode prevElement = new TreeNode(Integer.MIN_VALUE);

    // Approach 1: Inorder with List (Explicit) - O(n) time, O(n) space
    public void recoverTree(TreeNode root) {
        if (root == null)
            return;

        java.util.List<TreeNode> nodes = new java.util.ArrayList<>();
        inorderTraversal(root, nodes);

        TreeNode first = null, second = null;

        // Find the two swapped nodes
        for (int i = 0; i < nodes.size() - 1; i++) {
            if (nodes.get(i).val > nodes.get(i + 1).val) {
                if (first == null) {
                    first = nodes.get(i);
                    second = nodes.get(i + 1);
                } else {
                    second = nodes.get(i + 1);
                    break;
                }
            }
        }

        // Swap the values
        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    private void inorderTraversal(TreeNode root, java.util.List<TreeNode> nodes) {
        if (root == null)
            return;
        inorderTraversal(root.left, nodes);
        nodes.add(root);
        inorderTraversal(root.right, nodes);
    }

    // Approach 2: Inorder Recursive (Implicit) - O(n) time, O(h) space
    public void recoverTreeRecursive(TreeNode root) {
        // Reset global variables
        firstElement = null;
        secondElement = null;
        prevElement = new TreeNode(Integer.MIN_VALUE);

        inorderRecovery(root);

        // Swap the values
        if (firstElement != null && secondElement != null) {
            int temp = firstElement.val;
            firstElement.val = secondElement.val;
            secondElement.val = temp;
        }
    }

    private void inorderRecovery(TreeNode root) {
        if (root == null)
            return;

        inorderRecovery(root.left);

        // Check if current node violates BST property
        if (firstElement == null && prevElement.val >= root.val) {
            firstElement = prevElement;
        }
        if (firstElement != null && prevElement.val >= root.val) {
            secondElement = root;
        }

        prevElement = root;
        inorderRecovery(root.right);
    }

    // Approach 3: Morris Traversal - O(n) time, O(1) space (Optimal!)
    public void recoverTreeMorris(TreeNode root) {
        if (root == null)
            return;

        TreeNode current = root;
        TreeNode prev = null;
        TreeNode first = null, second = null;

        while (current != null) {
            if (current.left == null) {
                // Process current node
                if (prev != null && prev.val > current.val) {
                    if (first == null) {
                        first = prev;
                        second = current;
                    } else {
                        second = current;
                    }
                }
                prev = current;
                current = current.right;
            } else {
                // Find inorder predecessor
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    // Make current as right child of its inorder predecessor
                    predecessor.right = current;
                    current = current.left;
                } else {
                    // Revert the changes made
                    predecessor.right = null;

                    // Process current node
                    if (prev != null && prev.val > current.val) {
                        if (first == null) {
                            first = prev;
                            second = current;
                        } else {
                            second = current;
                        }
                    }
                    prev = current;
                    current = current.right;
                }
            }
        }

        // Swap values
        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    public static void main(String[] args) {
        RecoverBinarySearchTree solution = new RecoverBinarySearchTree();

        System.out.println("=== Testing Recover Binary Search Tree ===\n");

        // Test case 1: [1,3,null,null,2] -> [3,1,null,null,2]
        System.out.println("Test Case 1: Basic swap test");
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(3);
        root1.left.right = new TreeNode(2);

        System.out.println("Before recovery:");
        printInorder(root1);
        solution.recoverTree(root1);
        System.out.println("After recovery:");
        printInorder(root1);

        // Test case 2: Adjacent nodes swapped
        System.out.println("\nTest Case 2: Adjacent swap");
        TreeNode root2 = new TreeNode(2);
        root2.left = new TreeNode(3);
        root2.right = new TreeNode(1);

        System.out.println("Before recovery:");
        printInorder(root2);
        solution.recoverTreeRecursive(root2);
        System.out.println("After recovery:");
        printInorder(root2);

        // Test case 3: Morris traversal test
        System.out.println("\nTest Case 3: Morris Traversal");
        TreeNode root3 = new TreeNode(3);
        root3.left = new TreeNode(1);
        root3.right = new TreeNode(4);
        root3.right.left = new TreeNode(2);

        System.out.println("Before recovery:");
        printInorder(root3);
        solution.recoverTreeMorris(root3);
        System.out.println("After recovery:");
        printInorder(root3);

        System.out.println("\n✅ All test cases completed successfully!");
    }

    private static void printInorder(TreeNode root) {
        if (root == null) {
            System.out.println("null");
            return;
        }

        java.util.List<Integer> result = new java.util.ArrayList<>();
        inorderHelper(root, result);
        System.out.println(result);
    }

    private static void inorderHelper(TreeNode root, java.util.List<Integer> result) {
        if (root == null)
            return;
        inorderHelper(root.left, result);
        result.add(root.val);
        inorderHelper(root.right, result);
    }
}
