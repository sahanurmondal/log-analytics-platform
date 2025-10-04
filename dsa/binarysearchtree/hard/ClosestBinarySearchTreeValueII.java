package binarysearchtree.hard;

import java.util.*;

/**
 * LeetCode 272: Closest Binary Search Tree Value II
 * https://leetcode.com/problems/closest-binary-search-tree-value-ii/
 *
 * Description: Given the root of a binary search tree, a target value, and an
 * integer k,
 * return the k values in the BST that are closest to the target.
 * 
 * Constraints:
 * - The number of nodes in the tree is n
 * - 1 <= k <= n <= 10^4
 * - 0 <= Node.val <= 10^9
 * - -10^9 <= target <= 10^9
 *
 * Follow-up:
 * - Can you solve it in O(log n + k) time?
 * 
 * Time Complexity: O(n) for heap approach, O(log n + k) for optimal
 * Space Complexity: O(k)
 * 
 * Company Tags: Google, Facebook
 */
public class ClosestBinarySearchTreeValueII {

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

    // Main optimized solution - Two stacks approach
    public List<Integer> closestKValues(TreeNode root, double target, int k) {
        Stack<TreeNode> predecessors = new Stack<>();
        Stack<TreeNode> successors = new Stack<>();

        // Initialize stacks
        initializePredecessors(root, target, predecessors);
        initializeSuccessors(root, target, successors);

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            if (predecessors.isEmpty()) {
                result.add(getSuccessor(successors));
            } else if (successors.isEmpty()) {
                result.add(getPredecessor(predecessors));
            } else {
                double predDiff = Math.abs(predecessors.peek().val - target);
                double succDiff = Math.abs(successors.peek().val - target);

                if (predDiff < succDiff) {
                    result.add(getPredecessor(predecessors));
                } else {
                    result.add(getSuccessor(successors));
                }
            }
        }

        return result;
    }

    private void initializePredecessors(TreeNode root, double target, Stack<TreeNode> stack) {
        while (root != null) {
            if (root.val <= target) {
                stack.push(root);
                root = root.right;
            } else {
                root = root.left;
            }
        }
    }

    private void initializeSuccessors(TreeNode root, double target, Stack<TreeNode> stack) {
        while (root != null) {
            if (root.val > target) {
                stack.push(root);
                root = root.left;
            } else {
                root = root.right;
            }
        }
    }

    private int getPredecessor(Stack<TreeNode> stack) {
        TreeNode node = stack.pop();
        int val = node.val;
        node = node.left;

        while (node != null) {
            stack.push(node);
            node = node.right;
        }

        return val;
    }

    private int getSuccessor(Stack<TreeNode> stack) {
        TreeNode node = stack.pop();
        int val = node.val;
        node = node.right;

        while (node != null) {
            stack.push(node);
            node = node.left;
        }

        return val;
    }

    // Alternative solution - Priority Queue
    public List<Integer> closestKValuesHeap(TreeNode root, double target, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
                (a, b) -> Double.compare(Math.abs(b - target), Math.abs(a - target)));

        inorder(root, target, k, maxHeap);

        return new ArrayList<>(maxHeap);
    }

    private void inorder(TreeNode node, double target, int k, PriorityQueue<Integer> heap) {
        if (node == null)
            return;

        inorder(node.left, target, k, heap);

        if (heap.size() < k) {
            heap.offer(node.val);
        } else if (Math.abs(node.val - target) < Math.abs(heap.peek() - target)) {
            heap.poll();
            heap.offer(node.val);
        }

        inorder(node.right, target, k, heap);
    }

    public static void main(String[] args) {
        ClosestBinarySearchTreeValueII solution = new ClosestBinarySearchTreeValueII();

        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        System.out.println(solution.closestKValues(root, 3.714286, 2)); // Expected: [4,3] or [3,4]
        System.out.println(solution.closestKValuesHeap(root, 3.714286, 2)); // Expected: [4,3] or [3,4]
    }
}
