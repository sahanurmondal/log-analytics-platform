package linkedlist.medium;

import linkedlist.ListNode;

/**
 * Custom: Remove Kth Node from Beginning
 *
 * Description:
 * Remove the kth node from the beginning of a linked list.
 *
 * Constraints:
 * - 1 <= k <= n
 *
 * Follow-ups:
 * 1. Can you remove kth node from end?
 * 2. Can you remove multiple nodes at once?
 * 3. Can you handle doubly linked lists?
 */
public class RemoveKthNodeFromBeginning {
    public ListNode removeKthFromBeginning(ListNode head, int k) {
        if (k <= 0)
            return head;
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        for (int i = 1; i < k && prev.next != null; i++)
            prev = prev.next;
        if (prev.next != null)
            prev.next = prev.next.next;
        return dummy.next;
    }

    // Follow-up 1: Remove kth node from end
    public ListNode removeKthFromEnd(ListNode head, int k) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i <= k; i++) {
            if (fast == null)
                return head;
            fast = fast.next;
        }
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }
        if (slow.next != null)
            slow.next = slow.next.next;
        return dummy.next;
    }

    // Follow-up 2: Remove multiple nodes at once (given array of positions)
    public ListNode removeMultiple(ListNode head, int[] positions) {
        java.util.Set<Integer> posSet = new java.util.HashSet<>();
        for (int p : positions)
            posSet.add(p);
        ListNode dummy = new ListNode(0, head);
        ListNode curr = dummy;
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
        RemoveKthNodeFromBeginning solution = new RemoveKthNodeFromBeginning();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));
        System.out.println(solution.removeKthFromBeginning(head, 2)); // 1->3->4->5
        // Edge Case: Remove head
        System.out.println(solution.removeKthFromBeginning(head, 1)); // 2->3->4->5
        // Edge Case: Single node
        System.out.println(solution.removeKthFromBeginning(new ListNode(1), 1)); // null
    }
}

// ...existing ListNode class...
