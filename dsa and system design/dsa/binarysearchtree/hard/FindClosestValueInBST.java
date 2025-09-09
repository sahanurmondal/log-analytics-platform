package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode 270: Closest Binary Search Tree Value
 * https://leetcode.com/problems/closest-binary-search-tree-value/
 * 
 * Companies: Google, Microsoft, Facebook, Amazon
 * Frequency: High
 *
 * Description:
 * Given a BST and a target value, find the value in the BST that is closest to
 * the target.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * - There will be only one unique value closest to the target
 * 
 * Follow-up Questions:
 * 1. What if there are multiple values with the same closest distance?
 * 2. Can you solve it in O(h) time where h is the height?
 * 3. What if you need to find k closest values?
 */
public class FindClosestValueInBST {

    // Approach 1: Iterative Binary Search - O(h) time, O(1) space
    public int closestValue(TreeNode root, double target) {
        int closest = root.val;

        while (root != null) {
            // Update closest if current is closer
            if (Math.abs(root.val - target) < Math.abs(closest - target)) {
                closest = root.val;
            }

            // Move to appropriate subtree
            if (target < root.val) {
                root = root.left;
            } else {
                root = root.right;
            }
        }

        return closest;
    }

    // Approach 2: Recursive Binary Search - O(h) time, O(h) space
    public int closestValueRecursive(TreeNode root, double target) {
        int val = root.val;
        TreeNode child = target < val ? root.left : root.right;

        if (child == null) {
            return val;
        }

        int childClosest = closestValueRecursive(child, target);

        return Math.abs(val - target) < Math.abs(childClosest - target) ? val : childClosest;
    }

    // Approach 3: Inorder Traversal - O(n) time, O(h) space
    public int closestValueInorder(TreeNode root, double target) {
        int closest = root.val;
        double minDiff = Math.abs(root.val - target);

        return inorderHelper(root, target, closest, minDiff);
    }

    private int inorderHelper(TreeNode root, double target, int closest, double minDiff) {
        if (root == null)
            return closest;

        double currentDiff = Math.abs(root.val - target);
        if (currentDiff < minDiff) {
            closest = root.val;
            minDiff = currentDiff;
        }

        // Search both subtrees
        closest = inorderHelper(root.left, target, closest, minDiff);
        minDiff = Math.abs(closest - target);
        closest = inorderHelper(root.right, target, closest, minDiff);

        return closest;
    }

    // Approach 4: Lower and Upper Bound approach - O(h) time, O(1) space
    public int closestValueBounds(TreeNode root, double target) {
        Integer lowerBound = null;
        Integer upperBound = null;

        while (root != null) {
            if (root.val == target) {
                return root.val;
            } else if (root.val < target) {
                lowerBound = root.val;
                root = root.right;
            } else {
                upperBound = root.val;
                root = root.left;
            }
        }

        if (lowerBound == null)
            return upperBound;
        if (upperBound == null)
            return lowerBound;

        return target - lowerBound < upperBound - target ? lowerBound : upperBound;
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

    public static void main(String[] args) {
        FindClosestValueInBST solution = new FindClosestValueInBST();

        // Test Case 1: LeetCode Example
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(5);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(3);

        System.out.println("=== Test Case 1: LeetCode Example ===");
        System.out.println("Closest to 3.714286: " + solution.closestValue(root1, 3.714286)); // Expected: 4
        System.out.println("Closest to 2.1: " + solution.closestValue(root1, 2.1)); // Expected: 2
        System.out.println("Closest to 4.9: " + solution.closestValue(root1, 4.9)); // Expected: 5
        System.out.println("Closest to 1.5: " + solution.closestValue(root1, 1.5)); // Expected: 1 or 2
        System.out.println();

        // Test Case 2: Single node
        TreeNode root2 = new TreeNode(1);
        System.out.println("=== Test Case 2: Single Node ===");
        System.out.println("Closest to 4.428571: " + solution.closestValue(root2, 4.428571)); // Expected: 1
        System.out.println();

        // Test Case 3: Left skewed tree
        TreeNode root3 = new TreeNode(5);
        root3.left = new TreeNode(3);
        root3.left.left = new TreeNode(1);

        System.out.println("=== Test Case 3: Left Skewed Tree ===");
        System.out.println("Closest to 2.0: " + solution.closestValue(root3, 2.0)); // Expected: 1 or 3
        System.out.println("Closest to 4.0: " + solution.closestValue(root3, 4.0)); // Expected: 3 or 5
        System.out.println();

        // Test Case 4: Comparison of all approaches
        TreeNode root4 = solution.buildBST(new int[] { 8, 3, 10, 1, 6, 14, 4, 7, 13 });
        double target = 5.5;

        System.out.println("=== Test Case 4: Approach Comparison ===");
        System.out.println("Target: " + target);
        System.out.println("Iterative: " + solution.closestValue(root4, target));
        System.out.println("Recursive: " + solution.closestValueRecursive(root4, target));
        System.out.println("Inorder: " + solution.closestValueInorder(root4, target));
        System.out.println("Bounds: " + solution.closestValueBounds(root4, target));
        System.out.println();

        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        int[] nums = new int[1000];
        for (int i = 0; i < 1000; i++) {
            nums[i] = i * 2; // Even numbers
        }
        TreeNode largeBST = solution.buildBST(nums);
        double testTarget = 501.7;

        long startTime, endTime;

        startTime = System.nanoTime();
        int result1 = solution.closestValue(largeBST, testTarget);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result1 + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int result2 = solution.closestValueRecursive(largeBST, testTarget);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + result2 + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int result3 = solution.closestValueBounds(largeBST, testTarget);
        endTime = System.nanoTime();
        System.out.println("Bounds: " + result3 + " (Time: " + (endTime - startTime) + " ns)");
    }
}
