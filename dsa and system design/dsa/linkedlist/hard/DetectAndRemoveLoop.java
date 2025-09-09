package linkedlist.hard;

import linkedlist.ListNode;

/**
 * Variation: Detect and Remove Loop in Linked List (Hard)
 *
 * Description:
 * Detect if a linked list has a loop and remove it.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [0, 10^4].
 */
public class DetectAndRemoveLoop {
    public void detectAndRemoveLoop(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        // Step 1: Detect loop using Floyd's Cycle Detection Algorithm
        ListNode slow = head;
        ListNode fast = head;

        // Find meeting point
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                break;
            }
        }

        // If no loop found
        if (fast == null || fast.next == null) {
            return;
        }

        // Step 2: Find the start of the loop
        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }

        // Step 3: Remove the loop
        // Find the node just before the loop starts
        while (fast.next != slow) {
            fast = fast.next;
        }

        // Break the loop
        fast.next = null;
    }

    public static void main(String[] args) {
        DetectAndRemoveLoop solution = new DetectAndRemoveLoop();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3)));
        head.next.next.next = head.next; // loop
        solution.detectAndRemoveLoop(head);
        // Edge Case: No loop
        solution.detectAndRemoveLoop(new ListNode(1, new ListNode(2)));
        // Edge Case: Single node, no loop
        solution.detectAndRemoveLoop(new ListNode(1));
        // Edge Case: Empty list
        solution.detectAndRemoveLoop(null);
    }
}

// ...existing ListNode class...
