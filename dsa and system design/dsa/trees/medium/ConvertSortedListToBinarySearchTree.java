package trees.medium;

import java.util.*;

/**
 * LeetCode 109: Convert Sorted List to Binary Search Tree
 * https://leetcode.com/problems/convert-sorted-list-to-binary-search-tree/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given the head of a singly linked list where elements are sorted
 * in ascending order, convert it to a height-balanced BST.
 *
 * Constraints:
 * - The number of nodes in the head is in the range [0, 2 * 10^4]
 * - -10^5 <= Node.val <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you do it in O(n) time?
 * 2. Can you handle circular linked lists?
 * 3. Can you build without converting to array?
 */
public class ConvertSortedListToBinarySearchTree {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

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

    // Approach 1: Convert to array first
    public TreeNode sortedListToBST(ListNode head) {
        List<Integer> values = new ArrayList<>();
        ListNode current = head;
        while (current != null) {
            values.add(current.val);
            current = current.next;
        }
        return sortedArrayToBST(values, 0, values.size() - 1);
    }

    private TreeNode sortedArrayToBST(List<Integer> values, int left, int right) {
        if (left > right)
            return null;

        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(values.get(mid));

        root.left = sortedArrayToBST(values, left, mid - 1);
        root.right = sortedArrayToBST(values, mid + 1, right);

        return root;
    }

    // Follow-up 1: O(n) time using inorder simulation
    private ListNode current;

    public TreeNode sortedListToBSTOptimal(ListNode head) {
        current = head;
        int size = getSize(head);
        return inorderHelper(0, size - 1);
    }

    private int getSize(ListNode head) {
        int size = 0;
        while (head != null) {
            size++;
            head = head.next;
        }
        return size;
    }

    private TreeNode inorderHelper(int left, int right) {
        if (left > right)
            return null;

        int mid = left + (right - left) / 2;

        TreeNode leftChild = inorderHelper(left, mid - 1);

        TreeNode root = new TreeNode(current.val);
        root.left = leftChild;
        current = current.next;

        root.right = inorderHelper(mid + 1, right);

        return root;
    }

    // Follow-up 2: Handle circular linked lists
    public TreeNode sortedCircularListToBST(ListNode head) {
        if (head == null)
            return null;

        // Break the circular connection
        ListNode tail = head;
        int count = 1;
        while (tail.next != head) {
            tail = tail.next;
            count++;
        }
        tail.next = null;

        // Convert normally
        TreeNode result = sortedListToBST(head);

        // Restore circular connection
        tail.next = head;

        return result;
    }

    // Follow-up 3: Using slow-fast pointers to find middle
    public TreeNode sortedListToBSTPointers(ListNode head) {
        if (head == null)
            return null;
        if (head.next == null)
            return new TreeNode(head.val);

        // Find middle using slow-fast pointers
        ListNode prev = null, slow = head, fast = head;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        // Break the list
        if (prev != null)
            prev.next = null;

        TreeNode root = new TreeNode(slow.val);
        root.left = sortedListToBSTPointers(head);
        root.right = sortedListToBSTPointers(slow.next);

        return root;
    }

    // Helper: Build linked list from array
    private ListNode buildList(int[] values) {
        if (values.length == 0)
            return null;

        ListNode head = new ListNode(values[0]);
        ListNode current = head;

        for (int i = 1; i < values.length; i++) {
            current.next = new ListNode(values[i]);
            current = current.next;
        }

        return head;
    }

    // Helper: Check if tree is balanced
    private boolean isBalanced(TreeNode root) {
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
        ConvertSortedListToBinarySearchTree solution = new ConvertSortedListToBinarySearchTree();

        // Test case 1: Basic case
        int[] values1 = { -10, -3, 0, 5, 9 };
        ListNode head1 = solution.buildList(values1);
        System.out.println("Test 1 - Basic list: " + Arrays.toString(values1));
        TreeNode tree1 = solution.sortedListToBST(head1);
        List<Integer> inorder1 = new ArrayList<>();
        solution.inorderTraversal(tree1, inorder1);
        System.out.println("Inorder traversal: " + inorder1);
        System.out.println("Is balanced: " + solution.isBalanced(tree1));

        // Test case 2: Optimal O(n) approach
        ListNode head2 = solution.buildList(values1);
        System.out.println("\nTest 2 - Optimal approach:");
        TreeNode tree2 = solution.sortedListToBSTOptimal(head2);
        List<Integer> inorder2 = new ArrayList<>();
        solution.inorderTraversal(tree2, inorder2);
        System.out.println("Inorder traversal: " + inorder2);
        System.out.println("Is balanced: " + solution.isBalanced(tree2));

        // Test case 3: Using pointers
        ListNode head3 = solution.buildList(values1);
        System.out.println("\nTest 3 - Using pointers:");
        TreeNode tree3 = solution.sortedListToBSTPointers(head3);
        List<Integer> inorder3 = new ArrayList<>();
        solution.inorderTraversal(tree3, inorder3);
        System.out.println("Inorder traversal: " + inorder3);
        System.out.println("Is balanced: " + solution.isBalanced(tree3));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty list: " + (solution.sortedListToBST(null) == null));

        ListNode single = new ListNode(1);
        TreeNode singleTree = solution.sortedListToBST(single);
        System.out.println("Single node: " + (singleTree != null ? singleTree.val : "null"));

        int[] even = { 1, 2, 3, 4 };
        ListNode evenHead = solution.buildList(even);
        TreeNode evenTree = solution.sortedListToBST(evenHead);
        System.out.println("Even length - balanced: " + solution.isBalanced(evenTree));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        for (int i = 0; i < large.length; i++) {
            large[i] = i;
        }
        ListNode largeHead = solution.buildList(large);

        long start = System.nanoTime();
        TreeNode largeTree = solution.sortedListToBSTOptimal(largeHead);
        long end = System.nanoTime();
        System.out.println("Large list (10000 nodes) processed in: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Large tree is balanced: " + solution.isBalanced(largeTree));
    }
}
