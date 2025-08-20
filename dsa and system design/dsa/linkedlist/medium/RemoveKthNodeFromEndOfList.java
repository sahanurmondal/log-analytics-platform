package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 19: Remove Nth Node From End of List
 * https://leetcode.com/problems/remove-nth-node-from-end-of-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: High
 *
 * Description:
 * Remove the nth node from the end of a linked list.
 *
 * Constraints:
 * - 1 <= n <= 30
 *
 * Follow-ups:
 * 1. Can you do it in one pass?
 * 2. Can you remove multiple nodes at once?
 * 3. Can you handle doubly linked lists?
 */
public class RemoveKthNodeFromEndOfList {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i <= n; i++) fast = fast.next;
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;
        return dummy.next;
    }

    // Follow-up 1: One pass (already handled above)
    // Follow-up 2: Remove multiple nodes at once (given array of positions from end)
    public ListNode removeMultipleFromEnd(ListNode head, int[] positions) {
        int len = 0;
        ListNode curr = head;
        while (curr != null) { len++; curr = curr.next; }
        java.util.Set<Integer> posSet = new java.util.HashSet<>();
        for (int p : positions) posSet.add(len - p + 1);
        ListNode dummy = new ListNode(0, head);
        curr = dummy;
        int idx = 1;
        while (curr.next != null) {
            if (posSet.contains(idx)) {
                curr.next = curr.next.next;
            } else {
                curr = curr.next;
            }
            idx++;
        }
        return dummy.next;
    }

    // Follow-up 3: Doubly linked list version
    // ...existing code for doubly linked list...

    public static void main(String[] args) {
        RemoveKthNodeFromEndOfList solution = new RemoveKthNodeFromEndOfList();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));
        System.out.println(solution.removeNthFromEnd(head, 2)); // 1->2->3->5
        // Edge Case: Remove head
        System.out.println(solution.removeNthFromEnd(head, 5)); // 2->3->4->5
        // Edge Case: Single node
        System.out.println(solution.removeNthFromEnd(new ListNode(1), 1)); // null
    }
}

// ...existing ListNode class...
