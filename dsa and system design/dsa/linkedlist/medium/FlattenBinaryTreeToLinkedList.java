package linkedlist.medium;

import trees.TreeNode;
import linkedlist.DListNode;

/**
 * LeetCode 114: Flatten Binary Tree to Linked List
 * https://leetcode.com/problems/flatten-binary-tree-to-linked-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: High
 *
 * Description:
 * Flatten a binary tree to a linked list in-place.
 *
 * Constraints:
 * - 0 <= n <= 2000
 *
 * Follow-ups:
 * 1. Can you do it without recursion?
 * 2. Can you flatten to a doubly linked list?
 * 3. Can you flatten only a subtree?
 */
public class FlattenBinaryTreeToLinkedList {
    // Approach 1: Recursive
    public void flatten(TreeNode root) {
        if (root == null)
            return;
        flatten(root.left);
        flatten(root.right);
        TreeNode right = root.right;
        root.right = root.left;
        root.left = null;
        TreeNode curr = root;
        while (curr.right != null)
            curr = curr.right;
        curr.right = right;
    }

    // Approach 2: Iterative
    public void flattenIterative(TreeNode root) {
        TreeNode curr = root;
        while (curr != null) {
            if (curr.left != null) {
                TreeNode right = curr.right;
                curr.right = curr.left;
                curr.left = null;
                TreeNode tail = curr.right;
                while (tail.right != null)
                    tail = tail.right;
                tail.right = right;
            }
            curr = curr.right;
        }
    }

    // Follow-up 1: Flatten to doubly linked list
    public DListNode flattenToDoublyList(TreeNode root) {
        if (root == null)
            return null;
        DListNode dummy = new DListNode(0);
        DListNode prev = dummy;
        flattenHelper(root, prev);
        DListNode head = dummy.next;
        if (head != null)
            head.prev = null;
        return head;
    }

    private DListNode flattenHelper(TreeNode node, DListNode prev) {
        if (node == null)
            return prev;
        DListNode curr = new DListNode(node.val);
        prev.next = curr;
        curr.prev = prev;
        DListNode rightTail = flattenHelper(node.left, curr);
        return flattenHelper(node.right, rightTail);
    }

    // Follow-up 2: Flatten only a subtree
    public DListNode flattenSubtree(TreeNode root, int val) {
        TreeNode subtree = findNode(root, val);
        return flattenToDoublyList(subtree);
    }

    private TreeNode findNode(TreeNode root, int val) {
        if (root == null)
            return null;
        if (root.val == val)
            return root;
        TreeNode left = findNode(root.left, val);
        if (left != null)
            return left;
        return findNode(root.right, val);
    }

    public static void main(String[] args) {
        FlattenBinaryTreeToLinkedList solution = new FlattenBinaryTreeToLinkedList();
        TreeNode root = new TreeNode(1, new TreeNode(2, new TreeNode(3), new TreeNode(4)),
                new TreeNode(5, null, new TreeNode(6)));
        solution.flatten(root);
        // Edge Case: Single node
        solution.flatten(new TreeNode(1));
        // Edge Case: Empty tree
        solution.flatten(null);
    }
}
