package trees.medium;

import java.util.*;

/**
 * LeetCode 108: Convert Sorted Array to Binary Search Tree
 * https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given an integer array nums where elements are sorted in
 * ascending order, convert it to a height-balanced BST.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 * - nums is sorted in ascending order
 * 
 * Follow-up Questions:
 * 1. Can you generate all possible balanced BSTs?
 * 2. Can you handle duplicate values?
 * 3. Can you build iteratively?
 */
public class ConvertSortedArrayToBinarySearchTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    // Approach 1: Recursive middle selection
    public TreeNode sortedArrayToBST(int[] nums) {
        return sortedArrayToBSTHelper(nums, 0, nums.length - 1);
    }

    private TreeNode sortedArrayToBSTHelper(int[] nums, int left, int right) {
        if (left > right)
            return null;

        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(nums[mid]);

        root.left = sortedArrayToBSTHelper(nums, left, mid - 1);
        root.right = sortedArrayToBSTHelper(nums, mid + 1, right);

        return root;
    }

    // Follow-up 1: Generate all possible balanced BSTs
    public List<TreeNode> generateAllBSTs(int[] nums) {
        return generateAllBSTsHelper(nums, 0, nums.length - 1);
    }

    private List<TreeNode> generateAllBSTsHelper(int[] nums, int left, int right) {
        List<TreeNode> result = new ArrayList<>();
        if (left > right) {
            result.add(null);
            return result;
        }

        for (int i = left; i <= right; i++) {
            List<TreeNode> leftTrees = generateAllBSTsHelper(nums, left, i - 1);
            List<TreeNode> rightTrees = generateAllBSTsHelper(nums, i + 1, right);

            for (TreeNode leftTree : leftTrees) {
                for (TreeNode rightTree : rightTrees) {
                    TreeNode root = new TreeNode(nums[i]);
                    root.left = leftTree;
                    root.right = rightTree;
                    result.add(root);
                }
            }
        }

        return result;
    }

    // Follow-up 2: Handle duplicate values
    public TreeNode sortedArrayToBSTWithDuplicates(int[] nums) {
        // Remove duplicates while preserving order
        List<Integer> unique = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();

        for (int num : nums) {
            if (!seen.contains(num)) {
                unique.add(num);
                seen.add(num);
            }
        }

        int[] uniqueArray = unique.stream().mapToInt(i -> i).toArray();
        return sortedArrayToBST(uniqueArray);
    }

    // Follow-up 3: Iterative approach
    public TreeNode sortedArrayToBSTIterative(int[] nums) {
        if (nums.length == 0)
            return null;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<int[]> rangeQueue = new LinkedList<>();

        int mid = nums.length / 2;
        TreeNode root = new TreeNode(nums[mid]);
        nodeQueue.offer(root);
        rangeQueue.offer(new int[] { 0, nums.length - 1 });

        while (!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.poll();
            int[] range = rangeQueue.poll();
            int left = range[0], right = range[1];

            int currentMid = left + (right - left) / 2;

            // Process left child
            if (left <= currentMid - 1) {
                int leftMid = left + (currentMid - 1 - left) / 2;
                node.left = new TreeNode(nums[leftMid]);
                nodeQueue.offer(node.left);
                rangeQueue.offer(new int[] { left, currentMid - 1 });
            }

            // Process right child
            if (currentMid + 1 <= right) {
                int rightMid = (currentMid + 1) + (right - (currentMid + 1)) / 2;
                node.right = new TreeNode(nums[rightMid]);
                nodeQueue.offer(node.right);
                rangeQueue.offer(new int[] { currentMid + 1, right });
            }
        }

        return root;
    }

    // Helper: Check if tree is balanced
    public boolean isBalanced(TreeNode root) {
        return checkBalance(root) != -1;
    }

    private int checkBalance(TreeNode node) {
        if (node == null)
            return 0;

        int left = checkBalance(node.left);
        if (left == -1)
            return -1;

        int right = checkBalance(node.right);
        if (right == -1)
            return -1;

        if (Math.abs(left - right) > 1)
            return -1;
        return Math.max(left, right) + 1;
    }

    // Helper: Inorder traversal
    private void inorderTraversal(TreeNode root, List<Integer> result) {
        if (root == null)
            return;
        inorderTraversal(root.left, result);
        result.add(root.val);
        inorderTraversal(root.right, result);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ConvertSortedArrayToBinarySearchTree solution = new ConvertSortedArrayToBinarySearchTree();

        // Test case 1: Basic case
        int[] nums1 = { -10, -3, 0, 5, 9 };
        System.out.println("Test 1 - Basic array: " + Arrays.toString(nums1));
        TreeNode tree1 = solution.sortedArrayToBST(nums1);
        List<Integer> inorder1 = new ArrayList<>();
        solution.inorderTraversal(tree1, inorder1);
        System.out.println("Inorder traversal: " + inorder1);
        System.out.println("Is balanced: " + solution.isBalanced(tree1));

        // Test case 2: Iterative approach
        System.out.println("\nTest 2 - Iterative approach:");
        TreeNode tree2 = solution.sortedArrayToBSTIterative(nums1);
        List<Integer> inorder2 = new ArrayList<>();
        solution.inorderTraversal(tree2, inorder2);
        System.out.println("Inorder traversal: " + inorder2);
        System.out.println("Is balanced: " + solution.isBalanced(tree2));

        // Test case 3: Generate all possible BSTs (small array)
        int[] small = { 1, 2, 3 };
        System.out.println("\nTest 3 - All possible BSTs for " + Arrays.toString(small) + ":");
        List<TreeNode> allTrees = solution.generateAllBSTs(small);
        System.out.println("Number of possible BSTs: " + allTrees.size());

        // Test case 4: Handle duplicates
        int[] withDups = { 1, 1, 2, 2, 3, 3 };
        System.out.println("\nTest 4 - With duplicates: " + Arrays.toString(withDups));
        TreeNode tree4 = solution.sortedArrayToBSTWithDuplicates(withDups);
        List<Integer> inorder4 = new ArrayList<>();
        solution.inorderTraversal(tree4, inorder4);
        System.out.println("Inorder after removing duplicates: " + inorder4);

        // Edge cases
        System.out.println("\nEdge cases:");
        int[] single = { 1 };
        TreeNode singleTree = solution.sortedArrayToBST(single);
        System.out.println("Single element tree root: " + singleTree.val);

        int[] even = { 1, 2, 3, 4 };
        TreeNode evenTree = solution.sortedArrayToBST(even);
        System.out.println("Even length array - balanced: " + solution.isBalanced(evenTree));

        int[] odd = { 1, 2, 3, 4, 5 };
        TreeNode oddTree = solution.sortedArrayToBST(odd);
        System.out.println("Odd length array - balanced: " + solution.isBalanced(oddTree));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        for (int i = 0; i < large.length; i++) {
            large[i] = i;
        }

        long start = System.nanoTime();
        TreeNode largeTree = solution.sortedArrayToBST(large);
        long end = System.nanoTime();
        System.out.println("Large array (10000 elements) processed in: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Large tree is balanced: " + solution.isBalanced(largeTree));
    }
}
