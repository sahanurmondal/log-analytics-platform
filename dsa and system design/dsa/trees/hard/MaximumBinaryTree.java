package trees.hard;

import trees.TreeNode;
import java.util.*;

/**
 * LeetCode 654: Maximum Binary Tree
 * URL: https://leetcode.com/problems/maximum-binary-tree/
 * Difficulty: Medium (but in hard folder)
 * 
 * Companies: Amazon, Google, Microsoft, Facebook, Apple
 * Frequency: Medium
 * 
 * Description:
 * Given an integer array with no duplicates, construct a maximum binary tree:
 * 1. Root is maximum number in the array
 * 2. Left subtree is constructed from left part of array (before max)
 * 3. Right subtree is constructed from right part of array (after max)
 * 
 * Constraints:
 * - 1 <= nums.length <= 1000
 * - 0 <= nums[i] <= 1000
 * - All elements are unique
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively using a stack?
 * 2. How would you handle duplicate values?
 * 3. Can you optimize for better time complexity?
 * 4. How would you serialize/deserialize the result?
 * 5. How would you construct minimum binary tree instead?
 */
public class MaximumBinaryTree {

    // Approach 1: Recursive Divide and Conquer
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        return construct(nums, 0, nums.length - 1);
    }

    /**
     * Recursive construction
     * Time: O(n^2) worst case, O(n log n) average, Space: O(n)
     */
    private TreeNode construct(int[] nums, int left, int right) {
        if (left > right)
            return null;

        // Find index of maximum element
        int maxIdx = findMaxIndex(nums, left, right);

        // Create root with maximum value
        TreeNode root = new TreeNode(nums[maxIdx]);

        // Recursively construct left and right subtrees
        root.left = construct(nums, left, maxIdx - 1);
        root.right = construct(nums, maxIdx + 1, right);

        return root;
    }

    private int findMaxIndex(int[] nums, int left, int right) {
        int maxIdx = left;
        for (int i = left + 1; i <= right; i++) {
            if (nums[i] > nums[maxIdx]) {
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    // Approach 2: Iterative using Stack (Monotonic Stack)
    public TreeNode constructMaximumBinaryTreeIterative(int[] nums) {
        Deque<TreeNode> stack = new ArrayDeque<>();

        for (int num : nums) {
            TreeNode curr = new TreeNode(num);

            // Pop smaller elements and make them left child
            while (!stack.isEmpty() && stack.peek().val < num) {
                curr.left = stack.pop();
            }

            // If stack not empty, current becomes right child
            if (!stack.isEmpty()) {
                stack.peek().right = curr;
            }

            stack.push(curr);
        }

        // Return bottom of stack (root)
        return stack.isEmpty() ? null : stack.peekLast();
    }

    // Approach 3: Optimized recursive with precomputed max indices
    public TreeNode constructMaximumBinaryTreeOptimized(int[] nums) {
        int n = nums.length;
        int[][] maxIndices = new int[n][n];

        // Precompute max indices for all subarrays
        for (int i = 0; i < n; i++) {
            maxIndices[i][i] = i;
            for (int j = i + 1; j < n; j++) {
                maxIndices[i][j] = nums[maxIndices[i][j - 1]] > nums[j] ? maxIndices[i][j - 1] : j;
            }
        }

        return constructOptimized(nums, 0, n - 1, maxIndices);
    }

    private TreeNode constructOptimized(int[] nums, int left, int right, int[][] maxIndices) {
        if (left > right)
            return null;

        int maxIdx = maxIndices[left][right];
        TreeNode root = new TreeNode(nums[maxIdx]);

        root.left = constructOptimized(nums, left, maxIdx - 1, maxIndices);
        root.right = constructOptimized(nums, maxIdx + 1, right, maxIndices);

        return root;
    }

    // Follow-up: Construct minimum binary tree
    public TreeNode constructMinimumBinaryTree(int[] nums) {
        return constructMin(nums, 0, nums.length - 1);
    }

    private TreeNode constructMin(int[] nums, int left, int right) {
        if (left > right)
            return null;

        int minIdx = findMinIndex(nums, left, right);
        TreeNode root = new TreeNode(nums[minIdx]);

        root.left = constructMin(nums, left, minIdx - 1);
        root.right = constructMin(nums, minIdx + 1, right);

        return root;
    }

    private int findMinIndex(int[] nums, int left, int right) {
        int minIdx = left;
        for (int i = left + 1; i <= right; i++) {
            if (nums[i] < nums[minIdx]) {
                minIdx = i;
            }
        }
        return minIdx;
    }

    // Helper: Tree serialization for testing
    public List<Integer> serialize(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                result.add(null);
            } else {
                result.add(node.val);
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }

        // Remove trailing nulls
        while (!result.isEmpty() && result.get(result.size() - 1) == null) {
            result.remove(result.size() - 1);
        }

        return result;
    }

    // Helper: Tree height calculation
    public int getHeight(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + Math.max(getHeight(root.left), getHeight(root.right));
    }

    // Helper: Count nodes
    public int countNodes(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    public static void main(String[] args) {
        MaximumBinaryTree solution = new MaximumBinaryTree();

        // Test Case 1: Basic example [3,2,1,6,0,5]
        int[] nums1 = { 3, 2, 1, 6, 0, 5 };
        TreeNode root1 = solution.constructMaximumBinaryTree(nums1);
        System.out.println("Basic tree: " + solution.serialize(root1));
        System.out.println("Basic height: " + solution.getHeight(root1));

        // Test Case 2: Single element
        int[] nums2 = { 1 };
        TreeNode root2 = solution.constructMaximumBinaryTree(nums2);
        System.out.println("Single element: " + solution.serialize(root2));

        // Test Case 3: Ascending order
        int[] nums3 = { 1, 2, 3, 4, 5 };
        TreeNode root3 = solution.constructMaximumBinaryTree(nums3);
        System.out.println("Ascending: " + solution.serialize(root3));
        System.out.println("Ascending height: " + solution.getHeight(root3));

        // Test Case 4: Descending order
        int[] nums4 = { 5, 4, 3, 2, 1 };
        TreeNode root4 = solution.constructMaximumBinaryTree(nums4);
        System.out.println("Descending: " + solution.serialize(root4));
        System.out.println("Descending height: " + solution.getHeight(root4));

        // Test Case 5: Iterative approach
        TreeNode root5 = solution.constructMaximumBinaryTreeIterative(nums1);
        System.out.println("Iterative: " + solution.serialize(root5));

        // Test Case 6: Two elements
        int[] nums6 = { 2, 1 };
        TreeNode root6 = solution.constructMaximumBinaryTree(nums6);
        System.out.println("Two elements: " + solution.serialize(root6));

        // Test Case 7: Optimized approach
        TreeNode root7 = solution.constructMaximumBinaryTreeOptimized(nums1);
        System.out.println("Optimized: " + solution.serialize(root7));

        // Test Case 8: All same maximum at different positions
        int[] nums8 = { 1, 3, 2 };
        TreeNode root8 = solution.constructMaximumBinaryTree(nums8);
        System.out.println("Mixed: " + solution.serialize(root8));

        // Test Case 9: Follow-up minimum binary tree
        TreeNode minRoot = solution.constructMinimumBinaryTree(nums1);
        System.out.println("Minimum tree: " + solution.serialize(minRoot));

        // Test Case 10: Large array with pattern
        int[] nums10 = { 6, 3, 5, 4, 7, 2 };
        TreeNode root10 = solution.constructMaximumBinaryTree(nums10);
        System.out.println("Large pattern: " + solution.serialize(root10));
        System.out.println("Nodes count: " + solution.countNodes(root10));

        // Test Case 11: Edge case with zeros
        int[] nums11 = { 0, 1, 0 };
        TreeNode root11 = solution.constructMaximumBinaryTree(nums11);
        System.out.println("With zeros: " + solution.serialize(root11));

        // Test Case 12: Maximum values
        int[] nums12 = { 1000, 999, 998 };
        TreeNode root12 = solution.constructMaximumBinaryTree(nums12);
        System.out.println("Max values: " + solution.serialize(root12));
    }
}
