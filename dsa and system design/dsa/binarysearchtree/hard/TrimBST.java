package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode 669: Trim a Binary Search Tree
 * https://leetcode.com/problems/trim-a-binary-search-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: Medium-High
 *
 * Description:
 * Given a BST and a range [low, high], trim the tree so that all its elements
 * lie in [low, high]. Return the root of the trimmed BST.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - 0 <= low <= high <= 10^4
 * - All values are unique
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively?
 * 2. What if the range is empty?
 * 3. How to handle duplicates?
 */
public class TrimBST {

    // Approach 1: Recursive - O(n) time, O(h) space
    public TreeNode trimBST(TreeNode root, int low, int high) {
        if (root == null)
            return null;

        // If current node is less than low, trim left subtree and recurse right
        if (root.val < low) {
            return trimBST(root.right, low, high);
        }

        // If current node is greater than high, trim right subtree and recurse left
        if (root.val > high) {
            return trimBST(root.left, low, high);
        }

        // Current node is in range, trim both subtrees
        root.left = trimBST(root.left, low, high);
        root.right = trimBST(root.right, low, high);

        return root;
    }

    // Approach 2: Iterative - O(n) time, O(h) space
    public TreeNode trimBSTIterative(TreeNode root, int low, int high) {
        if (root == null)
            return null;

        // Find the new root within range
        while (root != null && (root.val < low || root.val > high)) {
            if (root.val < low) {
                root = root.right;
            } else {
                root = root.left;
            }
        }

        if (root == null)
            return null;

        // Trim left subtree
        TreeNode current = root;
        while (current.left != null) {
            if (current.left.val < low) {
                current.left = current.left.right;
            } else {
                current = current.left;
            }
        }

        // Trim right subtree
        current = root;
        while (current.right != null) {
            if (current.right.val > high) {
                current.right = current.right.left;
            } else {
                current = current.right;
            }
        }

        return root;
    }

    // Helper: Print inorder traversal
    public void printInorder(TreeNode root) {
        if (root == null)
            return;

        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
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

    // Helper: Clone tree for testing multiple approaches
    public TreeNode cloneTree(TreeNode root) {
        if (root == null)
            return null;

        TreeNode cloned = new TreeNode(root.val);
        cloned.left = cloneTree(root.left);
        cloned.right = cloneTree(root.right);

        return cloned;
    }

    public static void main(String[] args) {
        TrimBST solution = new TrimBST();

        // Test Case 1: LeetCode Example 1
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(0);
        root1.right = new TreeNode(2);

        System.out.println("=== Test Case 1: LeetCode Example 1 ===");
        System.out.print("Original tree: ");
        solution.printInorder(root1);
        System.out.println();

        TreeNode result1 = solution.trimBST(solution.cloneTree(root1), 1, 2);
        System.out.print("Trimmed [1,2]: ");
        solution.printInorder(result1);
        System.out.println();
        System.out.println();

        // Test Case 2: LeetCode Example 2
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(0);
        root2.right = new TreeNode(4);
        root2.left.right = new TreeNode(2);
        root2.left.right.left = new TreeNode(1);

        System.out.println("=== Test Case 2: LeetCode Example 2 ===");
        System.out.print("Original tree: ");
        solution.printInorder(root2);
        System.out.println();

        TreeNode result2 = solution.trimBST(solution.cloneTree(root2), 1, 3);
        System.out.print("Trimmed [1,3]: ");
        solution.printInorder(result2);
        System.out.println();
        System.out.println();

        // Test Case 3: All nodes out of range (too small)
        TreeNode root3 = solution.buildBST(new int[] { 1, 0, 2 });
        System.out.println("=== Test Case 3: All Nodes Too Small ===");
        System.out.print("Original tree: ");
        solution.printInorder(root3);
        System.out.println();

        TreeNode result3 = solution.trimBST(solution.cloneTree(root3), 3, 5);
        System.out.print("Trimmed [3,5]: ");
        if (result3 == null) {
            System.out.println("null (empty tree)");
        } else {
            solution.printInorder(result3);
            System.out.println();
        }
        System.out.println();

        // Test Case 4: All nodes out of range (too large)
        TreeNode root4 = solution.buildBST(new int[] { 8, 6, 10, 5, 7, 9, 11 });
        System.out.println("=== Test Case 4: All Nodes Too Large ===");
        System.out.print("Original tree: ");
        solution.printInorder(root4);
        System.out.println();

        TreeNode result4 = solution.trimBST(solution.cloneTree(root4), 1, 3);
        System.out.print("Trimmed [1,3]: ");
        if (result4 == null) {
            System.out.println("null (empty tree)");
        } else {
            solution.printInorder(result4);
            System.out.println();
        }
        System.out.println();

        // Test Case 5: Approach comparison
        System.out.println("=== Test Case 5: Approach Comparison ===");
        TreeNode testRoot = solution.buildBST(new int[] { 8, 3, 10, 1, 6, 14, 4, 7, 13 });
        System.out.print("Original tree: ");
        solution.printInorder(testRoot);
        System.out.println();

        TreeNode recursiveResult = solution.trimBST(solution.cloneTree(testRoot), 5, 12);
        System.out.print("Recursive [5,12]: ");
        solution.printInorder(recursiveResult);
        System.out.println();

        TreeNode iterativeResult = solution.trimBSTIterative(solution.cloneTree(testRoot), 5, 12);
        System.out.print("Iterative [5,12]: ");
        solution.printInorder(iterativeResult);
        System.out.println();
        System.out.println();

        // Test Case 6: Edge case - single node
        TreeNode root6 = new TreeNode(5);
        System.out.println("=== Test Case 6: Single Node ===");

        TreeNode inRange = solution.trimBST(solution.cloneTree(root6), 4, 6);
        System.out.print("Single node in range [4,6]: ");
        solution.printInorder(inRange);
        System.out.println();

        TreeNode outOfRange = solution.trimBST(solution.cloneTree(root6), 1, 3);
        System.out.print("Single node out of range [1,3]: ");
        if (outOfRange == null) {
            System.out.println("null");
        } else {
            solution.printInorder(outOfRange);
            System.out.println();
        }
    }
}
