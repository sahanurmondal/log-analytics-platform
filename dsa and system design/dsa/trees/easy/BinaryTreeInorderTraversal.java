package trees.easy;

import java.util.*;
import trees.TreeNode;

/**
 * LeetCode 94: Binary Tree Inorder Traversal
 * https://leetcode.com/problems/binary-tree-inorder-traversal/
 *
 * Description: Given the root of a binary tree, return the inorder traversal of
 * its nodes' values.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 100]
 * - -100 <= Node.val <= 100
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * - What about Morris traversal for O(1) space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h) for recursive, O(1) for Morris
 * 
 * Algorithm:
 * 1. Recursive: Left -> Root -> Right
 * 2. Iterative: Use stack to simulate recursion
 * 3. Morris: Threading technique for constant space
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class BinaryTreeInorderTraversal {

    // Main optimized solution - Recursive
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }

    private void inorderHelper(TreeNode node, List<Integer> result) {
        if (node == null)
            return;

        inorderHelper(node.left, result); // Left
        result.add(node.val); // Root
        inorderHelper(node.right, result); // Right
    }

    // Alternative solution - Iterative with Stack
    public List<Integer> inorderTraversalIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            // Go to leftmost node
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            // Process current node
            current = stack.pop();
            result.add(current.val);

            // Move to right subtree
            current = current.right;
        }

        return result;
    }

    // Follow-up optimization - Morris Traversal (O(1) space)
    public List<Integer> inorderTraversalMorris(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        TreeNode current = root;

        while (current != null) {
            if (current.left == null) {
                // No left subtree, visit current and go right
                result.add(current.val);
                current = current.right;
            } else {
                // Find inorder predecessor
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    // Create thread
                    predecessor.right = current;
                    current = current.left;
                } else {
                    // Remove thread and visit current
                    predecessor.right = null;
                    result.add(current.val);
                    current = current.right;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        BinaryTreeInorderTraversal solution = new BinaryTreeInorderTraversal();

        // Test Case 1: Normal case - [1,null,2,3]
        TreeNode root1 = new TreeNode(1);
        root1.right = new TreeNode(2);
        root1.right.left = new TreeNode(3);
        System.out.println(solution.inorderTraversal(root1)); // Expected: [1,3,2]

        // Test Case 2: Edge case - empty tree
        System.out.println(solution.inorderTraversal(null)); // Expected: []

        // Test Case 3: Corner case - single node
        TreeNode root3 = new TreeNode(1);
        System.out.println(solution.inorderTraversal(root3)); // Expected: [1]

        // Test Case 4: Left skewed tree
        TreeNode root4 = new TreeNode(3);
        root4.left = new TreeNode(2);
        root4.left.left = new TreeNode(1);
        System.out.println(solution.inorderTraversal(root4)); // Expected: [1,2,3]

        // Test Case 5: Right skewed tree
        TreeNode root5 = new TreeNode(1);
        root5.right = new TreeNode(2);
        root5.right.right = new TreeNode(3);
        System.out.println(solution.inorderTraversal(root5)); // Expected: [1,2,3]

        // Test Case 6: Complete binary tree
        TreeNode root6 = new TreeNode(4);
        root6.left = new TreeNode(2);
        root6.right = new TreeNode(6);
        root6.left.left = new TreeNode(1);
        root6.left.right = new TreeNode(3);
        root6.right.left = new TreeNode(5);
        root6.right.right = new TreeNode(7);
        System.out.println(solution.inorderTraversal(root6)); // Expected: [1,2,3,4,5,6,7]

        // Test Case 7: Negative values
        TreeNode root7 = new TreeNode(-1);
        root7.left = new TreeNode(-2);
        root7.right = new TreeNode(-3);
        System.out.println(solution.inorderTraversal(root7)); // Expected: [-2,-1,-3]

        // Test Case 8: Mixed positive/negative
        TreeNode root8 = new TreeNode(0);
        root8.left = new TreeNode(-1);
        root8.right = new TreeNode(1);
        System.out.println(solution.inorderTraversal(root8)); // Expected: [-1,0,1]

        // Test Case 9: Duplicate values
        TreeNode root9 = new TreeNode(1);
        root9.left = new TreeNode(1);
        root9.right = new TreeNode(1);
        System.out.println(solution.inorderTraversal(root9)); // Expected: [1,1,1]

        // Test Case 10: Large values
        TreeNode root10 = new TreeNode(100);
        root10.left = new TreeNode(-100);
        System.out.println(solution.inorderTraversal(root10)); // Expected: [-100,100]
    }
}
