package trees.medium;

import java.util.*;

/**
 * LeetCode 98: Validate Binary Search Tree
 * https://leetcode.com/problems/validate-binary-search-tree/
 * 
 * Companies: Amazon, Google, Microsoft, Facebook
 * Frequency: Very High
 *
 * Description: Given the root of a binary tree, determine if it is a valid
 * binary search tree (BST).
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -2^31 <= Node.val <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. Can you solve using inorder traversal?
 * 2. Can you find the invalid node?
 * 3. Can you validate with custom comparator?
 */
public class ValidateBinarySearchTree {

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

    // Approach 1: Recursive with bounds
    public boolean isValidBST(TreeNode root) {
        return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean isValidBST(TreeNode node, long minVal, long maxVal) {
        if (node == null)
            return true;

        if (node.val <= minVal || node.val >= maxVal)
            return false;

        return isValidBST(node.left, minVal, node.val) &&
                isValidBST(node.right, node.val, maxVal);
    }

    // Follow-up 1: Inorder traversal approach
    private TreeNode prev = null;

    public boolean isValidBSTInorder(TreeNode root) {
        prev = null;
        return inorderValidate(root);
    }

    private boolean inorderValidate(TreeNode node) {
        if (node == null)
            return true;

        if (!inorderValidate(node.left))
            return false;

        if (prev != null && prev.val >= node.val)
            return false;
        prev = node;

        return inorderValidate(node.right);
    }

    // Follow-up 2: Find the invalid node
    public TreeNode findInvalidNode(TreeNode root) {
        List<TreeNode> inorderList = new ArrayList<>();
        inorderTraversal(root, inorderList);

        for (int i = 1; i < inorderList.size(); i++) {
            if (inorderList.get(i - 1).val >= inorderList.get(i).val) {
                return inorderList.get(i);
            }
        }

        return null; // All nodes are valid
    }

    private void inorderTraversal(TreeNode node, List<TreeNode> list) {
        if (node == null)
            return;

        inorderTraversal(node.left, list);
        list.add(node);
        inorderTraversal(node.right, list);
    }

    // Follow-up 3: Validate with custom comparator
    public boolean isValidBSTCustom(TreeNode root, Comparator<Integer> comparator) {
        return isValidBSTCustomHelper(root, null, null, comparator);
    }

    private boolean isValidBSTCustomHelper(TreeNode node, Integer minVal, Integer maxVal, Comparator<Integer> comp) {
        if (node == null)
            return true;

        if (minVal != null && comp.compare(node.val, minVal) <= 0)
            return false;
        if (maxVal != null && comp.compare(node.val, maxVal) >= 0)
            return false;

        return isValidBSTCustomHelper(node.left, minVal, node.val, comp) &&
                isValidBSTCustomHelper(node.right, node.val, maxVal, comp);
    }

    // Additional: Iterative inorder validation
    public boolean isValidBSTIterative(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        TreeNode prev = null;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();

            if (prev != null && prev.val >= current.val) {
                return false;
            }
            prev = current;
            current = current.right;
        }

        return true;
    }

    // Additional: Count valid BST nodes
    public int countValidBSTNodes(TreeNode root) {
        return countValidNodes(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private int countValidNodes(TreeNode node, long minVal, long maxVal) {
        if (node == null)
            return 0;

        int count = 0;
        if (node.val > minVal && node.val < maxVal) {
            count = 1;
        }

        count += countValidNodes(node.left, minVal, Math.min(maxVal, node.val));
        count += countValidNodes(node.right, Math.max(minVal, node.val), maxVal);

        return count;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ValidateBinarySearchTree solution = new ValidateBinarySearchTree();

        // Test case 1: Valid BST
        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(3);

        System.out.println("Test 1 - Valid BST:");
        System.out.println("Recursive: " + solution.isValidBST(root1));
        System.out.println("Inorder: " + solution.isValidBSTInorder(root1));
        System.out.println("Iterative: " + solution.isValidBSTIterative(root1));

        // Test case 2: Invalid BST
        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.right.left = new TreeNode(3);
        root2.right.right = new TreeNode(6);

        System.out.println("\nTest 2 - Invalid BST:");
        System.out.println("Is valid: " + solution.isValidBST(root2));
        TreeNode invalidNode = solution.findInvalidNode(root2);
        System.out.println("Invalid node: " + (invalidNode != null ? invalidNode.val : "null"));

        // Test case 3: Custom comparator (reverse order)
        Comparator<Integer> reverseComp = (a, b) -> b.compareTo(a);
        System.out.println("\nTest 3 - Custom comparator (reverse):");
        System.out.println("Valid with reverse comparator: " + solution.isValidBSTCustom(root2, reverseComp));

        // Test case 4: Edge cases
        System.out.println("\nTest 4 - Edge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.isValidBST(singleNode));

        TreeNode duplicates = new TreeNode(1);
        duplicates.left = new TreeNode(1);
        System.out.println("Duplicate values: " + solution.isValidBST(duplicates));

        // Test case 5: Count valid nodes
        System.out.println("\nTest 5 - Count valid BST nodes:");
        System.out.println("Valid nodes in root2: " + solution.countValidBSTNodes(root2));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeBST(1000);

        long start = System.nanoTime();
        boolean result = solution.isValidBST(largeTree);
        long end = System.nanoTime();
        System.out.println("Large BST validation: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeBST(int nodes) {
        return buildBalancedBST(1, nodes);
    }

    private static TreeNode buildBalancedBST(int start, int end) {
        if (start > end)
            return null;

        int mid = (start + end) / 2;
        TreeNode root = new TreeNode(mid);
        root.left = buildBalancedBST(start, mid - 1);
        root.right = buildBalancedBST(mid + 1, end);
        return root;
    }
}
