package binarysearchtree.hard;

import binarysearchtree.TreeNode;
import java.util.*;

/**
 * LeetCode 230: Kth Smallest Element in a BST
 * https://leetcode.com/problems/kth-smallest-element-in-a-bst/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 65+ interviews)
 *
 * Description:
 * Given the root of a binary search tree, and an integer k, return the kth
 * smallest
 * value (1-indexed) of all the values of the nodes in the tree.
 *
 * Constraints:
 * - The number of nodes in the tree is n.
 * - 1 <= k <= n <= 10^4
 * - 0 <= Node.val <= 10^4
 * - The tree is guaranteed to be a valid BST.
 * 
 * Follow-up Questions:
 * 1. Can you solve it with O(h + k) time complexity?
 * 2. What if the BST is modified frequently (insert/delete) and you need to
 * find kth smallest frequently?
 * 3. Can you solve it without building the entire inorder traversal?
 * 4. How would you optimize for multiple queries?
 */
public class FindKthSmallestInBST {

    // Approach 1: Inorder Traversal + List - O(n) time, O(n) space
    public int kthSmallest(TreeNode root, int k) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);
        return inorder.get(k - 1);
    }

    private void inorderTraversal(TreeNode root, List<Integer> inorder) {
        if (root == null)
            return;

        inorderTraversal(root.left, inorder);
        inorder.add(root.val);
        inorderTraversal(root.right, inorder);
    }

    // Approach 2: Iterative Inorder with Early Stop - O(h + k) time, O(h) space
    public int kthSmallestIterative(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            // Go to leftmost node
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

        return -1; // Should never reach here with valid input
    }

    // Approach 3: Recursive with Counter - O(h + k) time, O(h) space
    private int count = 0;
    private int result = 0;

    public int kthSmallestRecursive(TreeNode root, int k) {
        count = 0;
        result = 0;
        inorderHelper(root, k);
        return result;
    }

    private void inorderHelper(TreeNode root, int k) {
        if (root == null)
            return;

        inorderHelper(root.left, k);

        count++;
        if (count == k) {
            result = root.val;
            return;
        }

        inorderHelper(root.right, k);
    }

    // Approach 4: Morris Traversal - O(n) time, O(1) space
    public int kthSmallestMorris(TreeNode root, int k) {
        TreeNode current = root;
        int count = 0;

        while (current != null) {
            if (current.left == null) {
                count++;
                if (count == k) {
                    return current.val;
                }
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
                    count++;
                    if (count == k) {
                        return current.val;
                    }
                    current = current.right;
                }
            }
        }

        return -1;
    }

    // Follow-up 1: BST with node count for O(h) solution
    static class TreeNodeWithCount {
        int val;
        int count; // Number of nodes in subtree including this node
        TreeNodeWithCount left, right;

        TreeNodeWithCount(int val) {
            this.val = val;
            this.count = 1;
        }
    }

    public int kthSmallestWithCount(TreeNodeWithCount root, int k) {
        int leftCount = (root.left != null) ? root.left.count : 0;

        if (k <= leftCount) {
            return kthSmallestWithCount(root.left, k);
        } else if (k > leftCount + 1) {
            return kthSmallestWithCount(root.right, k - leftCount - 1);
        } else {
            return root.val;
        }
    }

    // Follow-up 2: Find kth largest element
    public int kthLargest(TreeNode root, int k) {
        int totalNodes = countNodes(root);
        return kthSmallest(root, totalNodes - k + 1);
    }

    private int countNodes(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    // Follow-up 3: Find range of kth smallest elements
    public List<Integer> kthSmallestRange(TreeNode root, int k1, int k2) {
        List<Integer> result = new ArrayList<>();
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);

        for (int i = k1 - 1; i < Math.min(k2, inorder.size()); i++) {
            result.add(inorder.get(i));
        }

        return result;
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

    // Helper: Print inorder traversal
    public void printInorder(TreeNode root) {
        if (root == null)
            return;

        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
    }

    public static void main(String[] args) {
        FindKthSmallestInBST solution = new FindKthSmallestInBST();

        // Test Case 1: Normal BST
        TreeNode root1 = new TreeNode(5);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(4);
        root1.right.left = new TreeNode(6);
        root1.right.right = new TreeNode(8);

        System.out.println("=== Test Case 1: Normal BST ===");
        System.out.print("Inorder: ");
        solution.printInorder(root1);
        System.out.println();

        System.out.println("1st smallest: " + solution.kthSmallest(root1, 1)); // Expected: 1
        System.out.println("3rd smallest: " + solution.kthSmallest(root1, 3)); // Expected: 4
        System.out.println("5th smallest: " + solution.kthSmallest(root1, 5)); // Expected: 6
        System.out.println("1st smallest (Iterative): " + solution.kthSmallestIterative(root1, 1)); // Expected: 1
        System.out.println("3rd smallest (Recursive): " + solution.kthSmallestRecursive(root1, 3)); // Expected: 4
        System.out.println("5th smallest (Morris): " + solution.kthSmallestMorris(root1, 5)); // Expected: 6
        System.out.println("3rd largest: " + solution.kthLargest(root1, 3)); // Expected: 6
        System.out.println("Range 2-5: " + solution.kthSmallestRange(root1, 2, 5)); // Expected: [3, 4, 5, 6]
        System.out.println();

        // Test Case 2: Small BST from LeetCode example
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.left.right = new TreeNode(2);

        System.out.println("=== Test Case 2: LeetCode Example ===");
        System.out.print("Inorder: ");
        solution.printInorder(root2);
        System.out.println();
        System.out.println("1st smallest: " + solution.kthSmallest(root2, 1)); // Expected: 1
        System.out.println("2nd smallest: " + solution.kthSmallest(root2, 2)); // Expected: 2
        System.out.println("3rd smallest: " + solution.kthSmallest(root2, 3)); // Expected: 3
        System.out.println("4th smallest: " + solution.kthSmallest(root2, 4)); // Expected: 4
        System.out.println();

        // Test Case 3: Single node
        TreeNode root3 = new TreeNode(42);
        System.out.println("=== Test Case 3: Single Node ===");
        System.out.println("1st smallest: " + solution.kthSmallest(root3, 1)); // Expected: 42
        System.out.println();

        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        long startTime, endTime;
        int k = 500;

        // Build large BST for performance testing
        int[] nums = new int[1000];
        for (int i = 0; i < 1000; i++) {
            nums[i] = i + 1;
        }
        TreeNode largeBST = solution.buildBST(nums);

        startTime = System.nanoTime();
        int result1 = solution.kthSmallest(largeBST, k);
        endTime = System.nanoTime();
        System.out.println("Inorder List: " + result1 + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int result2 = solution.kthSmallestIterative(largeBST, k);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result2 + " (Time: " + (endTime - startTime) + " ns)");
    }
}
