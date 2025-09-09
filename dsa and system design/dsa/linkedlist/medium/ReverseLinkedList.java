package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 206: Reverse Linked List
 * https://leetcode.com/problems/reverse-linked-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: High
 *
 * Description:
 * Reverse a singly linked list.
 *
 * Constraints:
 * - 0 <= n <= 5000
 *
 * Follow-ups:
 * 1. Can you do it recursively?
 * 2. Can you reverse only a portion?
 * 3. Can you reverse a doubly linked list?
 */
public class ReverseLinkedList {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    // Follow-up 1: Recursive
    public ListNode reverseListRecursive(ListNode head) {
        if (head == null || head.next == null)
            return head;
        ListNode newHead = reverseListRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    // Follow-up 2: Reverse portion [m, n]
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (m == 1)
            return reverseFirstN(head, n);
        head.next = reverseBetween(head.next, m - 1, n - 1);
        return head;
    }

    private ListNode successor = null;

    private ListNode reverseFirstN(ListNode head, int n) {
        if (n == 1) {
            successor = head.next;
            return head;
        }
        ListNode last = reverseFirstN(head.next, n - 1);
        head.next.next = head;
        head.next = successor;
        return last;
    }

    // Follow-up 3: Doubly linked list version
    // ...existing code for doubly linked list...

    public static void main(String[] args) {
        ReverseLinkedList solution = new ReverseLinkedList();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3)));
        ListNode reversed = solution.reverseList(head);
        // Edge Case: Empty list
        System.out.println(solution.reverseList(null)); // null
        // Edge Case: Single node
        System.out.println(solution.reverseList(new ListNode(1))); // 1
        // Edge Case: Two nodes
        System.out.println(solution.reverseList(new ListNode(1, new ListNode(2)))); // 2->1
    }
}
