package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 141: Linked List Cycle
 * https://leetcode.com/problems/linked-list-cycle/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: High
 *
 * Description:
 * Detect if a linked list has a cycle.
 *
 * Constraints:
 * - 0 <= n <= 10^4
 *
 * Follow-ups:
 * 1. Can you return the length of the cycle?
 * 2. Can you remove the cycle?
 * 3. Can you find the cycle's starting node?
 */
public class LinkedListCycle {
    public boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast)
                return true;
        }
        return false;
    }

    // Follow-up 1: Length of cycle
    public int cycleLength(ListNode head) {
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

    // Follow-up 2: Remove cycle
    public void removeCycle(ListNode head) {
        // ...existing code for removing cycle...
    }

    // Follow-up 3: Find cycle's starting node
    public ListNode cycleStart(ListNode head) {
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

    public static void main(String[] args) {
        LinkedListCycle solution = new LinkedListCycle();
        ListNode head = new ListNode(3, new ListNode(2, new ListNode(0, new ListNode(-4))));
        head.next.next.next.next = head.next; // cycle
        System.out.println(solution.hasCycle(head)); // true
        // Edge Case: No cycle
        System.out.println(solution.hasCycle(new ListNode(1, new ListNode(2)))); // false
        // Edge Case: Single node, no cycle
        System.out.println(solution.hasCycle(new ListNode(1))); // false
        // Edge Case: Empty list
        System.out.println(solution.hasCycle(null)); // false
    }
}
