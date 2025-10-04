package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 82: Remove Duplicates from Sorted List II
 * https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Remove all nodes that have duplicate numbers, leaving only distinct numbers.
 *
 * Constraints:
 * - 0 <= n <= 300
 *
 * Follow-ups:
 * 1. Can you do it in O(1) space?
 * 2. Can you handle unsorted lists?
 * 3. Can you remove only one occurrence of duplicates?
 */
public class RemoveDuplicatesFromSortedListII {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        while (head != null) {
            boolean dup = false;
            while (head.next != null && head.val == head.next.val) {
                head = head.next;
                dup = true;
            }
            if (dup) {
                prev.next = head.next;
            } else {
                prev = prev.next;
            }
            head = head.next;
        }
        return dummy.next;
    }

    // Follow-up 1: O(1) space (already handled above)
    // Follow-up 2: Unsorted list
    public ListNode deleteDuplicatesUnsorted(ListNode head) {
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        ListNode curr = head;
        while (curr != null) {
            freq.put(curr.val, freq.getOrDefault(curr.val, 0) + 1);
            curr = curr.next;
        }
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        curr = head;
        while (curr != null) {
            if (freq.get(curr.val) > 1) {
                prev.next = curr.next;
            } else {
                prev = curr;
            }
            curr = curr.next;
        }
        return dummy.next;
    }

    // Follow-up 3: Remove only one occurrence of duplicates
    public ListNode removeOneOccurrence(ListNode head) {
        if (head == null)
            return null;
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        while (head != null && head.next != null) {
            if (head.val == head.next.val) {
                prev.next = head.next;
                head = head.next;
            } else {
                prev = head;
                head = head.next;
            }
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        RemoveDuplicatesFromSortedListII solution = new RemoveDuplicatesFromSortedListII();
        ListNode head = new ListNode(1,
                new ListNode(2, new ListNode(3, new ListNode(3, new ListNode(4, new ListNode(4, new ListNode(5)))))));
        System.out.println(solution.deleteDuplicates(head)); // 1->2->5
        // Edge Case: All duplicates
        System.out.println(solution.deleteDuplicates(new ListNode(1, new ListNode(1, new ListNode(1))))); // null
        // Edge Case: No duplicates
        System.out.println(solution.deleteDuplicates(new ListNode(1, new ListNode(2, new ListNode(3))))); // 1->2->3
        // Edge Case: Single node
        System.out.println(solution.deleteDuplicates(new ListNode(1))); // 1
        // Edge Case: Empty list
        System.out.println(solution.deleteDuplicates(null)); // null
    }
}
