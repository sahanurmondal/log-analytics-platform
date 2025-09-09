package binarysearchtree.medium;

import java.util.*;

/**
 * LeetCode 173: Binary Search Tree Iterator
 * https://leetcode.com/problems/binary-search-tree-iterator/
 *
 * Description: Implement the BSTIterator class that represents an iterator over
 * the inorder traversal of a BST.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^5]
 * - 0 <= Node.val <= 10^6
 * - At most 10^5 calls will be made to hasNext, and next
 *
 * Follow-up:
 * - Can you implement next() and hasNext() to run in average O(1) time?
 * 
 * Time Complexity: O(1) amortized for next()
 * Space Complexity: O(h)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class BSTIterator {

    static class TreeNode {
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

    private Stack<TreeNode> stack;

    public BSTIterator(TreeNode root) {
        stack = new Stack<>();
        pushLeft(root);
    }

    private void pushLeft(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }

    public int next() {
        TreeNode node = stack.pop();
        pushLeft(node.right);
        return node.val;
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }

    // Alternative solution - Pre-populate list
    static class BSTIteratorList {
        private List<Integer> values;
        private int index;

        public BSTIteratorList(TreeNode root) {
            values = new ArrayList<>();
            index = 0;
            inorder(root);
        }

        private void inorder(TreeNode node) {
            if (node == null)
                return;
            inorder(node.left);
            values.add(node.val);
            inorder(node.right);
        }

        public int next() {
            return values.get(index++);
        }

        public boolean hasNext() {
            return index < values.size();
        }
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(7);
        root.left = new TreeNode(3);
        root.right = new TreeNode(15);
        root.right.left = new TreeNode(9);
        root.right.right = new TreeNode(20);

        BSTIterator iterator = new BSTIterator(root);
        System.out.println(iterator.next()); // Expected: 3
        System.out.println(iterator.next()); // Expected: 7
        System.out.println(iterator.hasNext()); // Expected: true
        System.out.println(iterator.next()); // Expected: 9
    }
}
