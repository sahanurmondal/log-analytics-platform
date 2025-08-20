package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * LeetCode Variation: Find Inorder Predecessor and Successor in BST (Medium)
 * Related to: LeetCode 285 (Inorder Successor in BST) and 510 (Inorder
 * Successor in BST II)
 * 
 * Companies: Amazon, Microsoft, Google, Meta, Apple
 * Frequency: High
 *
 * Description:
 * Given a BST and a key, find its inorder predecessor and successor.
 * If the key is not present, find where it would be inserted.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^8 <= Node.val <= 10^8
 * - All node values are unique
 * 
 * Follow-up Questions:
 * 1. What if the key is not in the tree?
 * 2. Can you solve it without recursion?
 * 3. What if we need to handle duplicates?
 */
public class FindInorderPredecessorSuccessorMedium {

    // Approach 1: Two Pass Solution - O(h) time, O(1) space
    public int[] findPredecessorSuccessor(TreeNode root, int key) {
        int predecessor = findPredecessor(root, key);
        int successor = findSuccessor(root, key);
        return new int[] { predecessor, successor };
    }

    private int findPredecessor(TreeNode root, int key) {
        int predecessor = -1;

        while (root != null) {
            if (root.val < key) {
                predecessor = root.val;
                root = root.right;
            } else {
                root = root.left;
            }
        }

        return predecessor;
    }

    private int findSuccessor(TreeNode root, int key) {
        int successor = -1;

        while (root != null) {
            if (root.val > key) {
                successor = root.val;
                root = root.left;
            } else {
                root = root.right;
            }
        }

        return successor;
    }

    // Approach 2: Single Pass with Node Reference - O(h) time, O(1) space
    public int[] findPredecessorSuccessorSinglePass(TreeNode root, int key) {
        int[] result = { -1, -1 }; // [predecessor, successor]
        findBoth(root, key, result);
        return result;
    }

    private void findBoth(TreeNode root, int key, int[] result) {
        while (root != null) {
            if (root.val == key) {
                // Found the key, now find predecessor in left subtree and successor in right
                // subtree
                if (root.left != null) {
                    result[0] = findRightmost(root.left);
                }
                if (root.right != null) {
                    result[1] = findLeftmost(root.right);
                }
                return;
            } else if (root.val < key) {
                result[0] = root.val; // Potential predecessor
                root = root.right;
            } else {
                result[1] = root.val; // Potential successor
                root = root.left;
            }
        }
    }

    private int findRightmost(TreeNode node) {
        while (node.right != null) {
            node = node.right;
        }
        return node.val;
    }

    private int findLeftmost(TreeNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node.val;
    }

    // Approach 3: Recursive Solution - O(h) time, O(h) space
    public int[] findPredecessorSuccessorRecursive(TreeNode root, int key) {
        int[] result = { -1, -1 };
        findPredecessorRecursive(root, key, result);
        findSuccessorRecursive(root, key, result);
        return result;
    }

    private void findPredecessorRecursive(TreeNode root, int key, int[] result) {
        if (root == null)
            return;

        if (root.val < key) {
            result[0] = root.val;
            findPredecessorRecursive(root.right, key, result);
        } else {
            findPredecessorRecursive(root.left, key, result);
        }
    }

    private void findSuccessorRecursive(TreeNode root, int key, int[] result) {
        if (root == null)
            return;

        if (root.val > key) {
            result[1] = root.val;
            findSuccessorRecursive(root.left, key, result);
        } else {
            findSuccessorRecursive(root.right, key, result);
        }
    }

    public static void main(String[] args) {
        FindInorderPredecessorSuccessorMedium solution = new FindInorderPredecessorSuccessorMedium();

        // Test case 1: Normal BST with key present
        TreeNode root1 = new TreeNode(20);
        root1.left = new TreeNode(10);
        root1.right = new TreeNode(30);
        root1.left.left = new TreeNode(5);
        root1.left.right = new TreeNode(15);
        root1.right.left = new TreeNode(25);
        root1.right.right = new TreeNode(35);

        System.out.println("Test Case 1 (Key = 15):");
        int[] res1 = solution.findPredecessorSuccessor(root1, 15);
        System.out.println("Two-pass: " + java.util.Arrays.toString(res1));
        System.out.println(
                "Single-pass: " + java.util.Arrays.toString(solution.findPredecessorSuccessorSinglePass(root1, 15)));
        System.out.println(
                "Recursive: " + java.util.Arrays.toString(solution.findPredecessorSuccessorRecursive(root1, 15)));

        // Test case 2: Key not present (between existing values)
        System.out.println("\nTest Case 2 (Key = 18 - not present):");
        int[] res2 = solution.findPredecessorSuccessor(root1, 18);
        System.out.println("Two-pass: " + java.util.Arrays.toString(res2));
        System.out.println(
                "Single-pass: " + java.util.Arrays.toString(solution.findPredecessorSuccessorSinglePass(root1, 18)));

        // Test case 3: Key smaller than all values
        System.out.println("\nTest Case 3 (Key = 1 - smaller than all):");
        int[] res3 = solution.findPredecessorSuccessor(root1, 1);
        System.out.println("Result: " + java.util.Arrays.toString(res3));

        // Test case 4: Key larger than all values
        System.out.println("\nTest Case 4 (Key = 40 - larger than all):");
        int[] res4 = solution.findPredecessorSuccessor(root1, 40);
        System.out.println("Result: " + java.util.Arrays.toString(res4));

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Single node
        TreeNode single = new TreeNode(10);
        System.out.println(
                "Single node (key=10): " + java.util.Arrays.toString(solution.findPredecessorSuccessor(single, 10)));
        System.out.println(
                "Single node (key=5): " + java.util.Arrays.toString(solution.findPredecessorSuccessor(single, 5)));

        // Empty tree
        System.out.println("Empty tree: " + java.util.Arrays.toString(solution.findPredecessorSuccessor(null, 10)));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(FindInorderPredecessorSuccessorMedium solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger balanced BST
        TreeNode largeRoot = createBalancedBST(1, 1000);
        int testKey = 500;

        long startTime, endTime;

        // Test two-pass approach
        startTime = System.nanoTime();
        int[] result1 = solution.findPredecessorSuccessor(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Two-pass: " + java.util.Arrays.toString(result1) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test single-pass approach
        startTime = System.nanoTime();
        int[] result2 = solution.findPredecessorSuccessorSinglePass(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Single-pass: " + java.util.Arrays.toString(result2) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test recursive approach
        startTime = System.nanoTime();
        int[] result3 = solution.findPredecessorSuccessorRecursive(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + java.util.Arrays.toString(result3) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
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
