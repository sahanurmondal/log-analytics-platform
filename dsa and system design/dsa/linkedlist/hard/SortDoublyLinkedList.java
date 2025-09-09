package linkedlist.hard;

import linkedlist.DListNode;

/**
 * LeetCode 148: Sort List (adapted for doubly linked list)
 * https://leetcode.com/problems/sort-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: Medium
 *
 * Description:
 * Sort a doubly linked list in O(n log n) time.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [0, 5 * 10^4].
 *
 * Follow-ups:
 * 1. Can you sort by a custom comparator?
 * 2. Can you sort only a subrange of the list?
 * 3. Can you do stable sorting?
 */
public class SortDoublyLinkedList {
    // Approach: Merge Sort for Doubly Linked List - O(n log n) time, O(log n) space
    public DListNode sortDList(DListNode head) {
        if (head == null || head.next == null)
            return head;
        DListNode mid = getMiddle(head);
        DListNode right = mid.next;
        mid.next = null;
        if (right != null)
            right.prev = null;
        DListNode leftSorted = sortDList(head);
        DListNode rightSorted = sortDList(right);
        return merge(leftSorted, rightSorted);
    }

    // Helper: Find middle node (slow/fast pointer)
    private DListNode getMiddle(DListNode head) {
        DListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    // Helper: Merge two sorted doubly linked lists
    private DListNode merge(DListNode l1, DListNode l2) {
        DListNode dummy = new DListNode(0);
        DListNode curr = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                curr.next = l1;
                l1.prev = curr;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2.prev = curr;
                l2 = l2.next;
            }
            curr = curr.next;
        }
        if (l1 != null) {
            curr.next = l1;
            l1.prev = curr;
        }
        if (l2 != null) {
            curr.next = l2;
            l2.prev = curr;
        }
        DListNode head = dummy.next;
        if (head != null)
            head.prev = null;
        return head;
    }

    // Follow-up 1: Sort by custom comparator
    public DListNode sortDListComparator(DListNode head, java.util.Comparator<Integer> cmp) {
        if (head == null || head.next == null)
            return head;
        DListNode mid = getMiddle(head);
        DListNode right = mid.next;
        mid.next = null;
        if (right != null)
            right.prev = null;
        DListNode leftSorted = sortDListComparator(head, cmp);
        DListNode rightSorted = sortDListComparator(right, cmp);
        return mergeComparator(leftSorted, rightSorted, cmp);
    }

    private DListNode mergeComparator(DListNode l1, DListNode l2, java.util.Comparator<Integer> cmp) {
        DListNode dummy = new DListNode(0);
        DListNode curr = dummy;
        while (l1 != null && l2 != null) {
            if (cmp.compare(l1.val, l2.val) <= 0) {
                curr.next = l1;
                l1.prev = curr;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2.prev = curr;
                l2 = l2.next;
            }
            curr = curr.next;
        }
        if (l1 != null) {
            curr.next = l1;
            l1.prev = curr;
        }
        if (l2 != null) {
            curr.next = l2;
            l2.prev = curr;
        }
        DListNode head = dummy.next;
        if (head != null)
            head.prev = null;
        return head;
    }

    // Follow-up 2: Sort only a subrange [start, end) (0-based)
    public DListNode sortDListRange(DListNode head, int start, int end) {
        if (head == null || start >= end)
            return head;
        DListNode dummy = new DListNode(0);
        dummy.next = head;
        DListNode prev = dummy;
        for (int i = 0; i < start; i++)
            prev = prev.next;
        DListNode rangeStart = prev.next;
        DListNode rangeEnd = rangeStart;
        for (int i = start; i < end - 1 && rangeEnd != null; i++)
            rangeEnd = rangeEnd.next;
        DListNode after = rangeEnd != null ? rangeEnd.next : null;
        if (rangeEnd != null)
            rangeEnd.next = null;
        if (after != null)
            after.prev = null;
        DListNode sortedRange = sortDList(rangeStart);
        prev.next = sortedRange;
        if (sortedRange != null)
            sortedRange.prev = prev == dummy ? null : prev;
        DListNode tail = sortedRange;
        while (tail != null && tail.next != null)
            tail = tail.next;
        if (tail != null) {
            tail.next = after;
            if (after != null)
                after.prev = tail;
        }
        return dummy.next;
    }

    // Follow-up 3: Stable sort (merge sort is stable)
    public DListNode stableSortDList(DListNode head) {
        return sortDList(head); // Already stable
    }

    public static void main(String[] args) {
        SortDoublyLinkedList solution = new SortDoublyLinkedList();
        DListNode head = new DListNode(4, new DListNode(2, new DListNode(1, new DListNode(3))));
        head.next.prev = head;
        head.next.next.prev = head.next;
        head.next.next.next.prev = head.next.next;
        System.out.println(solution.sortDList(head)); // 1<->2<->3<->4

        DListNode sorted = new DListNode(1, new DListNode(2, new DListNode(3)));
        sorted.next.prev = sorted;
        sorted.next.next.prev = sorted.next;
        System.out.println(solution.sortDList(sorted)); // 1<->2<->3

        DListNode single = new DListNode(1);
        System.out.println(solution.sortDList(single)); // 1

        System.out.println(solution.sortDList(null)); // null

        // Follow-up 1: Custom comparator (descending)
        DListNode head2 = new DListNode(4, new DListNode(2, new DListNode(1, new DListNode(3))));
        head2.next.prev = head2;
        head2.next.next.prev = head2.next;
        head2.next.next.next.prev = head2.next.next;
        System.out.println(solution.sortDListComparator(head2, (a, b) -> b - a)); // 4<->3<->2<->1

        // Follow-up 2: Sort subrange [1,3)
        DListNode head3 = new DListNode(5, new DListNode(3, new DListNode(2, new DListNode(4))));
        head3.next.prev = head3;
        head3.next.next.prev = head3.next;
        head3.next.next.next.prev = head3.next.next;
        System.out.println(solution.sortDListRange(head3, 1, 3)); // 5<->2<->3<->4

        // Follow-up 3: Stable sort
        DListNode head4 = new DListNode(2, new DListNode(2, new DListNode(1, new DListNode(1))));
        head4.next.prev = head4;
        head4.next.next.prev = head4.next;
        head4.next.next.next.prev = head4.next.next;
        System.out.println(solution.stableSortDList(head4)); // 1<->1<->2<->2
    }
}
