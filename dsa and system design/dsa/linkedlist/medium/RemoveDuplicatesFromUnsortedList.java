package linkedlist.medium;

import linkedlist.ListNode;

/**
 * GeeksforGeeks: Remove Duplicates from Unsorted Linked List
 * https://www.geeksforgeeks.org/remove-duplicates-from-an-unsorted-linked-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Remove duplicates from an unsorted linked list.
 *
 * Constraints:
 * - 0 <= n <= 1000
 *
 * Follow-ups:
 * 1. Can you do it in O(1) space?
 * 2. Can you keep only the first occurrence?
 * 3. Can you remove only one occurrence of duplicates?
 */
public class RemoveDuplicatesFromUnsortedList {
    public ListNode removeDuplicates(ListNode head) {
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        while (head != null) {
            if (seen.contains(head.val)) {
                prev.next = head.next;
            } else {
                seen.add(head.val);
                prev = head;
            }
            head = head.next;
        }
        return dummy.next;
    }

    // Follow-up 1: O(1) space (brute force)
    public ListNode removeDuplicatesO1Space(ListNode head) {
        ListNode curr = head;
        while (curr != null) {
            ListNode runner = curr;
            while (runner.next != null) {
                if (runner.next.val == curr.val) {
                    runner.next = runner.next.next;
                } else {
                    runner = runner.next;
                }
            }
            curr = curr.next;
        }
        return head;
    }

    // Follow-up 2: Keep only first occurrence (already handled above)
    // Follow-up 3: Remove only one occurrence of duplicates
    public ListNode removeOneOccurrence(ListNode head) {
        if (head == null)
            return null;
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
                freq.put(curr.val, freq.get(curr.val) - 1);
                prev.next = curr.next;
            } else {
                prev = curr;
            }
            curr = curr.next;
        }
        return dummy.next;
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
