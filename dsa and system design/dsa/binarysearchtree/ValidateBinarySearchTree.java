package binarysearchtree;

import java.util.*;

/**
 * LeetCode 98: Validate Binary Search Tree
 *
 * Description:
 * Given the root of a binary tree, determine if it is a valid binary search
 * tree (BST).
 *
 * Input: TreeNode root
 * Output: boolean (true if valid BST)
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -2^31 <= Node.val <= 2^31 - 1
 *
 * Solution Approaches:
 * 1. Inorder Traversal (O(n) time, O(n) space)
 * Steps:
 * a. Traverse the tree in inorder and check if the sequence is strictly
 * increasing.
 * Time: O(n) for visiting all nodes.
 * Space: O(n) for recursion stack or explicit stack.
 * - Example: [2,1,3] → true
 * 2. Recursion with Bounds (O(n) time, O(n) space)
 * Steps:
 * a. Pass down min and max bounds for each node.
 * Time: O(n)
 * Space: O(n) for recursion stack.
 */
public class ValidateBinarySearchTree {

    // Main solution: Recursion with bounds - O(n) time, O(n) space
    public boolean isValidBST(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean validate(TreeNode node, long minVal, long maxVal) {
        if (node == null) {
            return true;
        }

        if (node.val <= minVal || node.val >= maxVal) {
            return false;
        }

        return validate(node.left, minVal, node.val) &&
                validate(node.right, node.val, maxVal);
    }

    // Alternative: Inorder traversal - O(n) time, O(n) space
    public boolean isValidBSTInorder(TreeNode root) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);

        for (int i = 1; i < inorder.size(); i++) {
            if (inorder.get(i) <= inorder.get(i - 1)) {
                return false;
            }
        }

        return true;
    }

    private void inorderTraversal(TreeNode node, List<Integer> inorder) {
        if (node == null)
            return;

        inorderTraversal(node.left, inorder);
        inorder.add(node.val);
        inorderTraversal(node.right, inorder);
    }

    // Optimized inorder with early termination
    public boolean isValidBSTOptimized(TreeNode root) {
        return inorderCheck(root, new int[] { Integer.MIN_VALUE }, new boolean[] { true });
    }

    private boolean inorderCheck(TreeNode node, int[] prev, boolean[] isFirst) {
        if (node == null)
            return true;

        if (!inorderCheck(node.left, prev, isFirst)) {
            return false;
        }

        if (!isFirst[0] && node.val <= prev[0]) {
            return false;
        }

        prev[0] = node.val;
        isFirst[0] = false;

        return inorderCheck(node.right, prev, isFirst);
    }

    // Iterative inorder approach
    public boolean isValidBSTIterative(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        Integer prev = null;
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();

            if (prev != null && current.val <= prev) {
                return false;
            }

            prev = current.val;
            current = current.right;
        }

        return true;
    }

    // Morris inorder traversal - O(n) time, O(1) space
    public boolean isValidBSTMorris(TreeNode root) {
        TreeNode current = root;
        Integer prev = null;

        while (current != null) {
            if (current.left == null) {
                if (prev != null && current.val <= prev) {
                    return false;
                }
                prev = current.val;
                current = current.right;
            } else {
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    predecessor.right = current;
                    current = current.left;
                } else {
                    predecessor.right = null;
                    if (prev != null && current.val <= prev) {
                        return false;
                    }
                    prev = current.val;
                    current = current.right;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        ValidateBinarySearchTree solution = new ValidateBinarySearchTree();

        // Test Case 1: Valid BST [2,1,3]
        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(3);
        System.out.println(solution.isValidBST(root1)); // Expected: true

        // Test Case 2: Invalid BST [5,1,4,null,null,3,6]
        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.right.left = new TreeNode(3);
        root2.right.right = new TreeNode(6);
        System.out.println(solution.isValidBST(root2)); // Expected: false

        // Test Case 3: Single node
        TreeNode root3 = new TreeNode(1);
        System.out.println(solution.isValidBST(root3)); // Expected: true
        // Edge Case 4: Duplicates (should not be valid)
        // Edge Case 5: Left child greater than root
        // Edge Case 6: Right child less than root
        // Edge Case 7: Large tree
        // Edge Case 8: Negative values
        // Edge Case 9: All right children
        // Edge Case 10: All left children
    }
}
