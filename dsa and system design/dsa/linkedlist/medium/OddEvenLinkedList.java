package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 328: Odd Even Linked List
 * https://leetcode.com/problems/odd-even-linked-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Group all odd-indexed nodes together followed by even-indexed nodes.
 *
 * Constraints:
 * - 0 <= n <= 10^4
 *
 * Follow-ups:
 * 1. Can you do it in-place?
 * 2. Can you handle doubly linked lists?
 * 3. Can you group by value instead of index?
 */
public class OddEvenLinkedList {
    public ListNode oddEvenList(ListNode head) {
        if (head == null)
            return null;
        ListNode odd = head, even = head.next, evenHead = even;
        while (even != null && even.next != null) {
            odd.next = even.next;
            odd = odd.next;
            even.next = odd.next;
            even = even.next;
        }
        odd.next = evenHead;
        return head;
    }

    // Follow-up 1: In-place (already handled above)
    // Follow-up 2: Doubly linked list version
    // ...existing code for doubly linked list...

    // Follow-up 3: Group by value
    public ListNode groupByValue(ListNode head) {
        if (head == null)
            return null;
        ListNode oddDummy = new ListNode(0), evenDummy = new ListNode(0);
        ListNode odd = oddDummy, even = evenDummy;
        while (head != null) {
            if (head.val % 2 == 1) {
                odd.next = head;
                odd = odd.next;
            } else {
                even.next = head;
                even = even.next;
            }
            head = head.next;
        }
        odd.next = evenDummy.next;
        even.next = null;
        return oddDummy.next;
    }

    public static void main(String[] args) {
        OddEvenLinkedList solution = new OddEvenLinkedList();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));
        System.out.println(solution.oddEvenList(head)); // 1->3->5->2->4
        // Edge Case: Single node
        System.out.println(solution.oddEvenList(new ListNode(1))); // 1
        // Edge Case: Two nodes
        System.out.println(solution.oddEvenList(new ListNode(1, new ListNode(2)))); // 1->2
        // Edge Case: Empty list
        System.out.println(solution.oddEvenList(null)); // null
    }
}
