package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 203: Remove Linked List Elements
 * https://leetcode.com/problems/remove-linked-list-elements/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Remove all elements from a linked list of integers that have value val.
 *
 * Constraints:
 * - 0 <= n <= 10^4
 *
 * Follow-ups:
 * 1. Can you do it recursively?
 * 2. Can you remove only the first occurrence?
 * 3. Can you handle doubly linked lists?
 */
public class RemoveLinkedListElements {
    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy = new ListNode(0, head);
        ListNode curr = dummy;
        while (curr.next != null) {
            if (curr.next.val == val)
                curr.next = curr.next.next;
            else
                curr = curr.next;
        }
        return dummy.next;
    }

    // Follow-up 1: Recursive
    public ListNode removeElementsRecursive(ListNode head, int val) {
        if (head == null)
            return null;
        head.next = removeElementsRecursive(head.next, val);
        return head.val == val ? head.next : head;
    }

    // Follow-up 2: Remove only first occurrence
    public ListNode removeFirstOccurrence(ListNode head, int val) {
        ListNode dummy = new ListNode(0, head);
        ListNode curr = dummy;
        while (curr.next != null) {
            if (curr.next.val == val) {
                curr.next = curr.next.next;
                break;
            }
            curr = curr.next;
        }
        return dummy.next;
    }

    // Follow-up 3: Doubly linked list version
    // ...existing code for doubly linked list...

    public static void main(String[] args) {
        RemoveLinkedListElements solution = new RemoveLinkedListElements();
        ListNode head = new ListNode(1,
                new ListNode(2, new ListNode(6, new ListNode(3, new ListNode(4, new ListNode(5, new ListNode(6)))))));
        System.out.println(solution.removeElements(head, 6)); // 1->2->3->4->5
        // Edge Case: Remove all
        System.out.println(solution.removeElements(new ListNode(7, new ListNode(7, new ListNode(7))), 7)); // null
        // Edge Case: Remove none
        System.out.println(solution.removeElements(new ListNode(1, new ListNode(2)), 3)); // 1->2
        // Edge Case: Single node
        System.out.println(solution.removeElements(new ListNode(1), 1)); // null
        // Edge Case: Empty list
        System.out.println(solution.removeElements(null, 1)); // null
    }
}
