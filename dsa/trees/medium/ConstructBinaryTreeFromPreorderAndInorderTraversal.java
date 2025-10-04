package trees.medium;

import java.util.*;

/**
 * LeetCode 105: Construct Binary Tree from Preorder and Inorder Traversal
 * https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/
 * 
 * Companies: Google, Amazon, Microsoft
 * Frequency: Very High
 *
 * Description: Given two integer arrays preorder and inorder, construct and
 * return the binary tree.
 *
 * Constraints:
 * - 1 <= preorder.length <= 3000
 * - inorder.length == preorder.length
 * - All values are unique
 * 
 * Follow-up Questions:
 * 1. Can you handle duplicate values?
 * 2. Can you use iterative approach?
 * 3. Can you optimize space complexity?
 */
public class ConstructBinaryTreeFromPreorderAndInorderTraversal {

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

    private int preIndex = 0;
    private Map<Integer, Integer> inorderMap;

    // Approach 1: Recursive with HashMap
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        preIndex = 0;
        inorderMap = new HashMap<>();

        for (int i = 0; i < inorder.length; i++) {
            inorderMap.put(inorder[i], i);
        }

        return buildTreeHelper(preorder, 0, inorder.length - 1);
    }

    private TreeNode buildTreeHelper(int[] preorder, int left, int right) {
        if (left > right)
            return null;

        int rootVal = preorder[preIndex++];
        TreeNode root = new TreeNode(rootVal);

        int inorderIndex = inorderMap.get(rootVal);

        // Build left subtree first (preorder: root -> left -> right)
        root.left = buildTreeHelper(preorder, left, inorderIndex - 1);
        root.right = buildTreeHelper(preorder, inorderIndex + 1, right);

        return root;
    }

    // Follow-up 1: Handle duplicate values
    public TreeNode buildTreeWithDuplicates(int[] preorder, int[] inorder) {
        Map<Integer, Queue<Integer>> inorderPositions = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderPositions.computeIfAbsent(inorder[i], k -> new LinkedList<>()).offer(i);
        }

        preIndex = 0;
        return buildTreeHelperDup(preorder, 0, inorder.length - 1, inorderPositions);
    }

    private TreeNode buildTreeHelperDup(int[] preorder, int left, int right, Map<Integer, Queue<Integer>> positions) {
        if (left > right || preIndex >= preorder.length)
            return null;

        int rootVal = preorder[preIndex++];
        TreeNode root = new TreeNode(rootVal);

        // Find the correct position for this occurrence
        Queue<Integer> posList = positions.get(rootVal);
        int inorderIndex = -1;

        // Find the first unused position within bounds
        while (!posList.isEmpty()) {
            int pos = posList.poll();
            if (pos >= left && pos <= right) {
                inorderIndex = pos;
                break;
            }
        }

        if (inorderIndex == -1)
            return root; // No valid position found

        root.left = buildTreeHelperDup(preorder, left, inorderIndex - 1, positions);
        root.right = buildTreeHelperDup(preorder, inorderIndex + 1, right, positions);

        return root;
    }

    // Follow-up 2: Iterative approach
    public TreeNode buildTreeIterative(int[] preorder, int[] inorder) {
        if (preorder.length == 0)
            return null;

        TreeNode root = new TreeNode(preorder[0]);
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        int inorderIndex = 0;

        for (int i = 1; i < preorder.length; i++) {
            TreeNode node = new TreeNode(preorder[i]);
            TreeNode parent = null;

            while (!stack.isEmpty() && stack.peek().val == inorder[inorderIndex]) {
                parent = stack.pop();
                inorderIndex++;
            }

            if (parent != null) {
                parent.right = node;
            } else {
                stack.peek().left = node;
            }

            stack.push(node);
        }

        return root;
    }

    // Follow-up 3: Space optimized (no hashmap)
    public TreeNode buildTreeSpaceOptimized(int[] preorder, int[] inorder) {
        return buildTreeOptimizedHelper(preorder, inorder, new int[] { 0 }, 0, inorder.length - 1);
    }

    private TreeNode buildTreeOptimizedHelper(int[] preorder, int[] inorder, int[] preIndex, int left, int right) {
        if (left > right || preIndex[0] >= preorder.length)
            return null;

        int rootVal = preorder[preIndex[0]++];
        TreeNode root = new TreeNode(rootVal);

        // Find root in inorder
        int inorderIndex = -1;
        for (int i = left; i <= right; i++) {
            if (inorder[i] == rootVal) {
                inorderIndex = i;
                break;
            }
        }

        root.left = buildTreeOptimizedHelper(preorder, inorder, preIndex, left, inorderIndex - 1);
        root.right = buildTreeOptimizedHelper(preorder, inorder, preIndex, inorderIndex + 1, right);

        return root;
    }

    // Helper: Print tree preorder to verify
    private void printPreorder(TreeNode root, List<Integer> result) {
        if (root == null)
            return;
        result.add(root.val);
        printPreorder(root.left, result);
        printPreorder(root.right, result);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ConstructBinaryTreeFromPreorderAndInorderTraversal solution = new ConstructBinaryTreeFromPreorderAndInorderTraversal();

        // Test case 1: Basic case
        int[] preorder1 = { 3, 9, 20, 15, 7 };
        int[] inorder1 = { 9, 3, 15, 20, 7 };
        System.out.println("Test 1 - Basic case:");
        TreeNode tree1 = solution.buildTree(preorder1, inorder1);
        List<Integer> result1 = new ArrayList<>();
        solution.printPreorder(tree1, result1);
        System.out.println("Reconstructed preorder: " + result1);

        // Test case 2: Iterative approach
        System.out.println("\nTest 2 - Iterative approach:");
        TreeNode tree2 = solution.buildTreeIterative(preorder1, inorder1);
        List<Integer> result2 = new ArrayList<>();
        solution.printPreorder(tree2, result2);
        System.out.println("Reconstructed preorder: " + result2);

        // Test case 3: Space optimized
        System.out.println("\nTest 3 - Space optimized:");
        TreeNode tree3 = solution.buildTreeSpaceOptimized(preorder1, inorder1);
        List<Integer> result3 = new ArrayList<>();
        solution.printPreorder(tree3, result3);
        System.out.println("Reconstructed preorder: " + result3);

        // Edge cases
        System.out.println("\nEdge cases:");
        int[] singlePre = { 1 };
        int[] singleIn = { 1 };
        TreeNode singleTree = solution.buildTree(singlePre, singleIn);
        System.out.println("Single node: " + (singleTree != null ? singleTree.val : "null"));

        int[] leftSkewed = { 1, 2, 3 };
        int[] leftSkewedIn = { 3, 2, 1 };
        TreeNode leftTree = solution.buildTree(leftSkewed, leftSkewedIn);
        System.out.println("Left skewed tree constructed: " + (leftTree != null));

        // Stress test
        System.out.println("\nStress test:");
        int size = 1000;
        int[] largePre = new int[size];
        int[] largeIn = new int[size];

        // Create a balanced tree pattern
        for (int i = 0; i < size; i++) {
            largePre[i] = i + 1;
            largeIn[i] = i + 1;
        }

        long start = System.nanoTime();
        TreeNode largeTree = solution.buildTree(largePre, largeIn);
        long end = System.nanoTime();
        System.out.println("Large tree (1000 nodes) built in: " + (end - start) / 1_000_000 + " ms");
    }
}
