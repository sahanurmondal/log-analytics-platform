package linkedlist.hard;

import linkedlist.ListNode;

/**
 * Variation: Remove Duplicates from Unsorted List (Hard)
 *
 * Description:
 * Remove duplicates from an unsorted linked list.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [0, 10^4].
 */
public class RemoveDuplicatesFromUnsortedList {
    public ListNode removeDuplicates(ListNode head) {
        if (head == null) {
            return null;
        }

        java.util.Set<Integer> seen = new java.util.HashSet<>();
        ListNode current = head;
        ListNode prev = null;

        while (current != null) {
            if (seen.contains(current.val)) {
                // Remove duplicate
                prev.next = current.next;
            } else {
                seen.add(current.val);
                prev = current;
            }
            current = current.next;
        }

        return head;
    }

    public static void main(String[] args) {
        RemoveDuplicatesFromUnsortedList solution = new RemoveDuplicatesFromUnsortedList();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(1, new ListNode(3, new ListNode(2)))));
        System.out.println(solution.removeDuplicates(head)); // 1->2->3
        // Edge Case: All duplicates
        System.out.println(solution.removeDuplicates(new ListNode(1, new ListNode(1, new ListNode(1))))); // 1
        // Edge Case: No duplicates
        System.out.println(solution.removeDuplicates(new ListNode(1, new ListNode(2, new ListNode(3))))); // 1->2->3
        // Edge Case: Single node
        System.out.println(solution.removeDuplicates(new ListNode(1))); // 1
        // Edge Case: Empty list
        System.out.println(solution.removeDuplicates(null)); // null
    }
}

// ...existing ListNode class...
