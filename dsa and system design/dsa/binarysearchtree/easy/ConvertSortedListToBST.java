package binarysearchtree.easy;

/**
 * LeetCode 109: Convert Sorted List to Binary Search Tree
 * https://leetcode.com/problems/convert-sorted-list-to-binary-search-tree/
 *
 * Description: Given the head of a singly linked list where elements are sorted
 * in ascending order,
 * convert it to a height balanced BST.
 * 
 * Constraints:
 * - The number of nodes in head is in the range [0, 2 * 10^4]
 * - -10^5 <= Node.val <= 10^5
 *
 * Follow-up:
 * - Can you do it in O(n) time?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(log n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class ConvertSortedListToBST {

    static class ListNode {
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

    private ListNode current;

    // Main optimized solution - Inorder simulation
    public TreeNode sortedListToBST(ListNode head) {
        int size = getSize(head);
        current = head;
        return buildBST(0, size - 1);
    }

    private int getSize(ListNode head) {
        int size = 0;
        while (head != null) {
            size++;
            head = head.next;
        }
        return size;
    }

    private TreeNode buildBST(int left, int right) {
        if (left > right) {
            return null;
        }

        int mid = left + (right - left) / 2;

        TreeNode leftChild = buildBST(left, mid - 1);

        TreeNode root = new TreeNode(current.val);
        current = current.next;

        TreeNode rightChild = buildBST(mid + 1, right);

        root.left = leftChild;
        root.right = rightChild;

        return root;
    }

    // Alternative solution - Find middle using slow/fast pointers
    public TreeNode sortedListToBSTSlowFast(ListNode head) {
        if (head == null)
            return null;
        if (head.next == null)
            return new TreeNode(head.val);

        ListNode prev = null;
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        prev.next = null; // Break the left part

        TreeNode root = new TreeNode(slow.val);
        root.left = sortedListToBSTSlowFast(head);
        root.right = sortedListToBSTSlowFast(slow.next);

        return root;
    }

    public static void main(String[] args) {
        ConvertSortedListToBST solution = new ConvertSortedListToBST();

        ListNode head = new ListNode(-10);
        head.next = new ListNode(-3);
        head.next.next = new ListNode(0);
        head.next.next.next = new ListNode(5);
        head.next.next.next.next = new ListNode(9);

        TreeNode root = solution.sortedListToBST(head);
        System.out.println("Root value: " + root.val); // Expected: 0 (middle element)
    }
}
