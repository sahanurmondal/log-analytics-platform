package linkedlist.medium;

import linkedlist.ListNode;

/**
 * GeeksforGeeks: Detect and Remove Loop in Linked List
 * https://www.geeksforgeeks.org/detect-and-remove-loop-in-a-linked-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Detect and remove a loop in a linked list.
 *
 * Constraints:
 * - 0 <= n <= 1000
 *
 * Follow-ups:
 * 1. Can you do it without extra space?
 * 2. Can you return the starting node of the loop?
 * 3. Can you count the length of the loop?
 */
public class DetectAndRemoveLoop {
    // Floyd's Cycle Detection
    public boolean hasLoop(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast)
                return true;
        }
        return false;
    }

    // Remove loop if exists
    public void removeLoop(ListNode head) {
        ListNode slow = head, fast = head;
        boolean loop = false;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                loop = true;
                break;
            }
        }
        if (!loop)
            return;
        slow = head;
        if (slow == fast) {
            while (fast.next != slow)
                fast = fast.next;
        } else {
            while (slow.next != fast.next) {
                slow = slow.next;
                fast = fast.next;
            }
        }
        fast.next = null;
    }

    // Follow-up 1: Return starting node of loop
    public ListNode getLoopStart(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast)
                break;
        }
        if (fast == null || fast.next == null)
            return null;
        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }

    // Follow-up 2: Count length of loop
    public int getLoopLength(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                int len = 1;
                fast = fast.next;
                while (fast != slow) {
                    fast = fast.next;
                    len++;
                }
                return len;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        DetectAndRemoveLoop solution = new DetectAndRemoveLoop();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3)));
        head.next.next.next = head.next; // loop
        solution.removeLoop(head);
        // Edge Case: No loop
        solution.removeLoop(new ListNode(1, new ListNode(2)));
        // Edge Case: Single node, no loop
        solution.removeLoop(new ListNode(1));
        // Edge Case: Empty list
        solution.removeLoop(null);
    }
}
