package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 142: Linked List Cycle II
 * https://leetcode.com/problems/linked-list-cycle-ii/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: High
 *
 * Description:
 * Find the node where the cycle begins.
 *
 * Constraints:
 * - 0 <= n <= 10^4
 *
 * Follow-ups:
 * 1. Can you remove the cycle?
 * 2. Can you count the cycle length?
 * 3. Can you detect multiple cycles?
 */
public class LinkedListCycleII {
    public ListNode detectCycle(ListNode head) {
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

    // Follow-up 1: Remove cycle
    public void removeCycle(ListNode head) {
        // ...existing code for removing cycle...
    }

    // Follow-up 2: Count cycle length
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

    // Follow-up 3: Detect multiple cycles (not possible in singly linked list)
    public boolean hasMultipleCycles(ListNode head) {
        // Always false for singly linked list
        return false;
    }

    public static void main(String[] args) {
        LinkedListCycleII solution = new LinkedListCycleII();
        ListNode head = new ListNode(3, new ListNode(2, new ListNode(0, new ListNode(-4))));
        head.next.next.next.next = head.next; // cycle at node 2
        System.out.println(solution.detectCycle(head)); // node 2
        // Edge Case: No cycle
        System.out.println(solution.detectCycle(new ListNode(1, new ListNode(2)))); // null
        // Edge Case: Single node, no cycle
        System.out.println(solution.detectCycle(new ListNode(1))); // null
        // Edge Case: Empty list
        System.out.println(solution.detectCycle(null)); // null
    }
}
