package linkedlist.hard;

import linkedlist.ListNode;

/**
 * LeetCode 148: Sort List (Hard)
 * https://leetcode.com/problems/sort-list/
 *
 * Description:
 * Sort a linked list in O(n log n) time using constant space complexity.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [0, 5 * 10^4].
 */
public class SortList {
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        // Split the list into two halves
        ListNode mid = getMiddle(head);
        ListNode secondHalf = mid.next;
        mid.next = null;

        // Recursively sort both halves
        ListNode left = sortList(head);
        ListNode right = sortList(secondHalf);

        // Merge the sorted halves
        return merge(left, right);
    }

    private ListNode getMiddle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        ListNode prev = null;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        return prev;
    }

    private ListNode merge(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }

        current.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }

    public static void main(String[] args) {
        SortList solution = new SortList();
        ListNode head = new ListNode(4, new ListNode(2, new ListNode(1, new ListNode(3))));
        System.out.println(solution.sortList(head)); // 1->2->3->4
        // Edge Case: Already sorted
        System.out.println(solution.sortList(new ListNode(1, new ListNode(2, new ListNode(3))))); // 1->2->3
        // Edge Case: Single node
        System.out.println(solution.sortList(new ListNode(1))); // 1
        // Edge Case: Empty list
        System.out.println(solution.sortList(null)); // null
    }
}

// ...existing ListNode class...
