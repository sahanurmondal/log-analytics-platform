package binarysearchtree.hard;

import binarysearchtree.TreeNode;
import java.util.*;

/**
 * Variation: Kth Largest Element in a BST
 * Related to LeetCode 230: Kth Smallest Element in a BST
 * 
 * Companies: Amazon, Microsoft, Google, Facebook
 * Frequency: High
 *
 * Description:
 * Given a BST, find the kth largest element.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - 1 <= k <= number of nodes
 * - -10^4 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve it with O(h + k) time complexity?
 * 2. What if you need to find kth largest frequently?
 * 3. Can you solve it without building the entire inorder traversal?
 */
public class FindKthLargestInBST {

    // Approach 1: Reverse Inorder Traversal (Right -> Root -> Left) - O(h + k)
    // time, O(h) space
    public int kthLargest(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        int count = 0;

        while (current != null || !stack.isEmpty()) {
            // Go to rightmost node first
            while (current != null) {
                stack.push(current);
                current = current.right;
            }

            current = stack.pop();
            count++;

            if (count == k) {
                return current.val;
            }

            current = current.left;
        }

        return -1; // Should never reach here with valid input
    }

    // Approach 2: Recursive Reverse Inorder - O(h + k) time, O(h) space
    private int count = 0;
    private int result = 0;

    public int kthLargestRecursive(TreeNode root, int k) {
        count = 0;
        result = 0;
        reverseInorderHelper(root, k);
        return result;
    }

    private void reverseInorderHelper(TreeNode root, int k) {
        if (root == null)
            return;

        reverseInorderHelper(root.right, k);

        count++;
        if (count == k) {
            result = root.val;
            return;
        }

        reverseInorderHelper(root.left, k);
    }

    // Approach 3: Using Inorder + Count Nodes - O(n) time, O(n) space
    public int kthLargestInorder(TreeNode root, int k) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);
        return inorder.get(inorder.size() - k);
    }

    private void inorderTraversal(TreeNode root, List<Integer> inorder) {
        if (root == null)
            return;

        inorderTraversal(root.left, inorder);
        inorder.add(root.val);
        inorderTraversal(root.right, inorder);
    }

    // Approach 4: Count nodes and convert to kth smallest - O(n + h + k) time
    public int kthLargestViaSmallest(TreeNode root, int k) {
        int totalNodes = countNodes(root);
        return kthSmallest(root, totalNodes - k + 1);
    }

    private int countNodes(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    private int kthSmallest(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            k--;

            if (k == 0) {
                return current.val;
            }

            current = current.right;
        }

        return -1;
    }

    // Helper: Build BST from array for testing
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

    public static void main(String[] args) {
        FindKthLargestInBST solution = new FindKthLargestInBST();

        // Test Case 1: Normal BST
        TreeNode root1 = new TreeNode(5);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(4);
        root1.right.left = new TreeNode(6);
        root1.right.right = new TreeNode(8);

        System.out.println("=== Test Case 1: Normal BST [1,3,4,5,6,7,8] ===");
        System.out.println("1st largest: " + solution.kthLargest(root1, 1)); // Expected: 8
        System.out.println("3rd largest: " + solution.kthLargest(root1, 3)); // Expected: 6
        System.out.println("7th largest: " + solution.kthLargest(root1, 7)); // Expected: 1
        System.out.println("1st largest (Recursive): " + solution.kthLargestRecursive(root1, 1)); // Expected: 8
        System.out.println("3rd largest (Inorder): " + solution.kthLargestInorder(root1, 3)); // Expected: 6
        System.out.println("5th largest (Via Smallest): " + solution.kthLargestViaSmallest(root1, 5)); // Expected: 4
        System.out.println();

        // Test Case 2: LeetCode Example
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.left.right = new TreeNode(2);

        System.out.println("=== Test Case 2: LeetCode Example [1,2,3,4] ===");
        System.out.println("1st largest: " + solution.kthLargest(root2, 1)); // Expected: 4
        System.out.println("2nd largest: " + solution.kthLargest(root2, 2)); // Expected: 3
        System.out.println("3rd largest: " + solution.kthLargest(root2, 3)); // Expected: 2
        System.out.println("4th largest: " + solution.kthLargest(root2, 4)); // Expected: 1
        System.out.println();

        // Test Case 3: Single node
        TreeNode root3 = new TreeNode(42);
        System.out.println("=== Test Case 3: Single Node ===");
        System.out.println("1st largest: " + solution.kthLargest(root3, 1)); // Expected: 42
        System.out.println();

        // Test Case 4: Right skewed tree
        TreeNode root4 = new TreeNode(1);
        root4.right = new TreeNode(2);
        root4.right.right = new TreeNode(3);
        root4.right.right.right = new TreeNode(4);

        System.out.println("=== Test Case 4: Right Skewed Tree ===");
        System.out.println("1st largest: " + solution.kthLargest(root4, 1)); // Expected: 4
        System.out.println("4th largest: " + solution.kthLargest(root4, 4)); // Expected: 1
        System.out.println();

        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        int[] nums = { 50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45, 55, 65, 75, 85 };
        TreeNode largeBST = solution.buildBST(nums);
        int k = 5;

        long startTime, endTime;

        startTime = System.nanoTime();
        int result1 = solution.kthLargest(largeBST, k);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result1 + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int result2 = solution.kthLargestRecursive(largeBST, k);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + result2 + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int result3 = solution.kthLargestInorder(largeBST, k);
        endTime = System.nanoTime();
        System.out.println("Inorder: " + result3 + " (Time: " + (endTime - startTime) + " ns)");
    }
}
