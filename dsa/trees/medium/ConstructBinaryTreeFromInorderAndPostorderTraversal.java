package trees.medium;

import java.util.*;

/**
 * LeetCode 106: Construct Binary Tree from Inorder and Postorder Traversal
 * https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/
 * 
 * Companies: Google, Amazon, Microsoft
 * Frequency: High
 *
 * Description: Given two integer arrays inorder and postorder, construct and
 * return the binary tree.
 *
 * Constraints:
 * - 1 <= inorder.length <= 3000
 * - postorder.length == inorder.length
 * - All values are unique
 * 
 * Follow-up Questions:
 * 1. Can you handle duplicate values?
 * 2. Can you optimize using iterative approach?
 * 3. Can you validate the input arrays?
 */
public class ConstructBinaryTreeFromInorderAndPostorderTraversal {

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

    private int postIndex;
    private Map<Integer, Integer> inorderMap;

    // Approach 1: Recursive with HashMap
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        postIndex = postorder.length - 1;
        inorderMap = new HashMap<>();

        for (int i = 0; i < inorder.length; i++) {
            inorderMap.put(inorder[i], i);
        }

        return buildTreeHelper(postorder, 0, inorder.length - 1);
    }

    private TreeNode buildTreeHelper(int[] postorder, int left, int right) {
        if (left > right)
            return null;

        int rootVal = postorder[postIndex--];
        TreeNode root = new TreeNode(rootVal);

        int inorderIndex = inorderMap.get(rootVal);

        // Build right subtree first (postorder: left -> right -> root)
        root.right = buildTreeHelper(postorder, inorderIndex + 1, right);
        root.left = buildTreeHelper(postorder, left, inorderIndex - 1);

        return root;
    }

    // Follow-up 1: Handle duplicate values with position tracking
    public TreeNode buildTreeWithDuplicates(int[] inorder, int[] postorder) {
        Map<Integer, List<Integer>> inorderPositions = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderPositions.computeIfAbsent(inorder[i], k -> new ArrayList<>()).add(i);
        }

        postIndex = postorder.length - 1;
        return buildTreeHelperDup(postorder, inorder, 0, inorder.length - 1, inorderPositions, new HashMap<>());
    }

    private TreeNode buildTreeHelperDup(int[] postorder, int[] inorder, int left, int right,
            Map<Integer, List<Integer>> positions, Map<Integer, Integer> usedCount) {
        if (left > right)
            return null;

        int rootVal = postorder[postIndex--];
        TreeNode root = new TreeNode(rootVal);

        // Find the correct position for this occurrence
        int currentUse = usedCount.getOrDefault(rootVal, 0);
        List<Integer> posList = positions.get(rootVal);
        int inorderIndex = -1;

        for (int pos : posList) {
            if (pos >= left && pos <= right && currentUse == 0) {
                inorderIndex = pos;
                break;
            }
        }

        usedCount.put(rootVal, currentUse + 1);

        root.right = buildTreeHelperDup(postorder, inorder, inorderIndex + 1, right, positions, usedCount);
        root.left = buildTreeHelperDup(postorder, inorder, left, inorderIndex - 1, positions, usedCount);

        return root;
    }

    // Follow-up 2: Iterative approach
    public TreeNode buildTreeIterative(int[] inorder, int[] postorder) {
        if (inorder.length == 0)
            return null;

        Stack<TreeNode> stack = new Stack<>();
        TreeNode root = new TreeNode(postorder[postorder.length - 1]);
        stack.push(root);

        int inorderIndex = inorder.length - 1;

        for (int i = postorder.length - 2; i >= 0; i--) {
            TreeNode node = new TreeNode(postorder[i]);
            TreeNode parent = null;

            while (!stack.isEmpty() && stack.peek().val == inorder[inorderIndex]) {
                parent = stack.pop();
                inorderIndex--;
            }

            if (parent != null) {
                parent.left = node;
            } else {
                stack.peek().right = node;
            }

            stack.push(node);
        }

        return root;
    }

    // Follow-up 3: Validate input arrays
    public boolean validateInput(int[] inorder, int[] postorder) {
        if (inorder.length != postorder.length)
            return false;

        Set<Integer> inorderSet = new HashSet<>();
        Set<Integer> postorderSet = new HashSet<>();

        for (int val : inorder)
            inorderSet.add(val);
        for (int val : postorder)
            postorderSet.add(val);

        return inorderSet.equals(postorderSet) && inorderSet.size() == inorder.length;
    }

    // Helper: Print tree inorder to verify
    private void printInorder(TreeNode root, List<Integer> result) {
        if (root == null)
            return;
        printInorder(root.left, result);
        result.add(root.val);
        printInorder(root.right, result);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ConstructBinaryTreeFromInorderAndPostorderTraversal solution = new ConstructBinaryTreeFromInorderAndPostorderTraversal();

        // Test case 1: Basic case
        int[] inorder1 = { 9, 3, 15, 20, 7 };
        int[] postorder1 = { 9, 15, 7, 20, 3 };
        System.out.println("Test 1 - Basic case:");
        TreeNode tree1 = solution.buildTree(inorder1, postorder1);
        List<Integer> result1 = new ArrayList<>();
        solution.printInorder(tree1, result1);
        System.out.println("Reconstructed inorder: " + result1);

        // Test case 2: Iterative approach
        System.out.println("\nTest 2 - Iterative approach:");
        TreeNode tree2 = solution.buildTreeIterative(inorder1, postorder1);
        List<Integer> result2 = new ArrayList<>();
        solution.printInorder(tree2, result2);
        System.out.println("Reconstructed inorder: " + result2);

        // Test case 3: Validation
        System.out.println("\nTest 3 - Input validation:");
        System.out.println("Valid input: " + solution.validateInput(inorder1, postorder1));
        System.out.println("Invalid input: " + solution.validateInput(new int[] { 1, 2 }, new int[] { 1, 2, 3 }));

        // Edge cases
        System.out.println("\nEdge cases:");
        int[] single = { 1 };
        TreeNode singleTree = solution.buildTree(single, single);
        System.out.println("Single node tree root: " + (singleTree != null ? singleTree.val : "null"));

        int[] linear = { 1, 2, 3 };
        int[] linearPost = { 3, 2, 1 };
        TreeNode linearTree = solution.buildTree(linear, linearPost);
        System.out.println("Linear tree constructed successfully: " + (linearTree != null));

        // Stress test
        System.out.println("\nStress test:");
        int[] largeInorder = new int[1000];
        int[] largePostorder = new int[1000];
        for (int i = 0; i < 1000; i++) {
            largeInorder[i] = i;
            largePostorder[999 - i] = i;
        }

        long start = System.nanoTime();
        TreeNode largeTree = solution.buildTree(largeInorder, largePostorder);
        long end = System.nanoTime();
        System.out.println("Large tree constructed in: " + (end - start) / 1_000_000 + " ms");
    }
}
