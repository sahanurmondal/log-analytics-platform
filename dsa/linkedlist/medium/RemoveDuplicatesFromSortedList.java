package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 83: Remove Duplicates from Sorted List
 * https://leetcode.com/problems/remove-duplicates-from-sorted-list/
 *
 * Description:
 * Remove duplicates from a sorted linked list.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [0, 300].
 */
public class RemoveDuplicatesFromSortedList {
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode current = head;
        while (current.next != null) {
            if (current.val == current.next.val) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }

        return head;
    }

    public static void main(String[] args) {
        RemoveDuplicatesFromSortedList solution = new RemoveDuplicatesFromSortedList();
        ListNode head = new ListNode(1, new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(3)))));
        System.out.println(solution.deleteDuplicates(head)); // 1->2->3
        // Edge Case: All duplicates
        System.out.println(solution.deleteDuplicates(new ListNode(1, new ListNode(1, new ListNode(1))))); // 1
        // Edge Case: No duplicates
        System.out.println(solution.deleteDuplicates(new ListNode(1, new ListNode(2, new ListNode(3))))); // 1->2->3
        // Edge Case: Single node
        System.out.println(solution.deleteDuplicates(new ListNode(1))); // 1
        // Edge Case: Empty list
        System.out.println(solution.deleteDuplicates(null)); // null
    }
}
