package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 61: Rotate List
 * https://leetcode.com/problems/rotate-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Rotate the list to the right by k places.
 *
 * Constraints:
 * - 0 <= n <= 500
 * - 0 <= k <= 2 * 10^9
 *
 * Follow-ups:
 * 1. Can you do it in O(1) space?
 * 2. Can you rotate to the left?
 * 3. Can you rotate a doubly linked list?
 */
public class RotateList {
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || k == 0)
            return head;
        int len = 1;
        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
            len++;
        }
        k = k % len;
        if (k == 0)
            return head;
        ListNode curr = head;
        for (int i = 0; i < len - k - 1; i++)
            curr = curr.next;
        ListNode newHead = curr.next;
        curr.next = null;
        tail.next = head;
        return newHead;
    }

    // Follow-up 1: O(1) space (already handled above)
    // Follow-up 2: Rotate to the left
    public ListNode rotateLeft(ListNode head, int k) {
        if (head == null || k == 0)
            return head;
        int len = 1;
        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
            len++;
        }
        k = k % len;
        if (k == 0)
            return head;
        ListNode curr = head;
        for (int i = 0; i < k - 1; i++)
            curr = curr.next;
        ListNode newHead = curr.next;
        curr.next = null;
        tail.next = head;
        return newHead;
    }

    // Follow-up 3: Doubly linked list version
    // ...existing code for doubly linked list...

    public static void main(String[] args) {
        RotateList solution = new RotateList();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));
        System.out.println(solution.rotateRight(head, 2)); // 4->5->1->2->3
        // Edge Case: k == 0
        System.out.println(solution.rotateRight(head, 0)); // unchanged
        // Edge Case: k == length
        System.out.println(solution.rotateRight(head, 5)); // unchanged
        // Edge Case: k > length
        System.out.println(solution.rotateRight(head, 7)); // 4->5->1->2->3
        // Edge Case: Single node
        System.out.println(solution.rotateRight(new ListNode(1), 1)); // 1
        // Edge Case: Empty list
        System.out.println(solution.rotateRight(null, 3)); // null
    }
}
