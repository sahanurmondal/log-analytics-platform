package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 92: Reverse Linked List II
 * https://leetcode.com/problems/reverse-linked-list-ii/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: High
 *
 * Description:
 * Reverse a linked list from position left to right.
 *
 * Constraints:
 * - 1 <= n <= 500
 * - 1 <= left <= right <= n
 *
 * Follow-ups:
 * 1. Can you do it recursively?
 * 2. Can you reverse multiple segments?
 * 3. Can you reverse a doubly linked list?
 */
public class ReverseLinkedListII {
    public ListNode reverseBetween(ListNode head, int left, int right) {
        if (left == 1)
            return reverseFirstN(head, right);
        head.next = reverseBetween(head.next, left - 1, right - 1);
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

    // Follow-up 1: Iterative
    public ListNode reverseBetweenIterative(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        for (int i = 1; i < left; i++)
            prev = prev.next;
        ListNode curr = prev.next;
        for (int i = 0; i < right - left; i++) {
            ListNode move = curr.next;
            curr.next = move.next;
            move.next = prev.next;
            prev.next = move;
        }
        return dummy.next;
    }

    // Follow-up 2: Reverse multiple segments
    public ListNode reverseMultipleSegments(ListNode head, int[][] segments) {
        for (int[] seg : segments)
            head = reverseBetween(head, seg[0], seg[1]);
        return head;
    }

    // Follow-up 3: Doubly linked list version
    // ...existing code for doubly linked list...

    public static void main(String[] args) {
        ReverseLinkedListII solution = new ReverseLinkedListII();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));
        System.out.println(solution.reverseBetween(head, 2, 4)); // 1->4->3->2->5
        // Edge Case: m == n
        System.out.println(solution.reverseBetween(head, 3, 3)); // unchanged
        // Edge Case: Reverse entire list
        System.out.println(solution.reverseBetween(head, 1, 5)); // 5->4->3->2->1
        // Edge Case: Single node
        System.out.println(solution.reverseBetween(new ListNode(1), 1, 1)); // 1
    }
}
