package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 24: Swap Nodes in Pairs
 * https://leetcode.com/problems/swap-nodes-in-pairs/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Swap every two adjacent nodes.
 *
 * Constraints:
 * - 0 <= n <= 100
 *
 * Follow-ups:
 * 1. Can you do it recursively?
 * 2. Can you swap in groups of k?
 * 3. Can you swap a doubly linked list?
 */
public class SwapNodesInPairs {
    public ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null)
            return head;
        ListNode next = head.next;
        head.next = swapPairs(next.next);
        next.next = head;
        return next;
    }

    // Follow-up 1: Recursive (already handled above)
    // Follow-up 2: Swap in groups of k
    public ListNode swapKGroup(ListNode head, int k) {
        ListNode curr = head;
        int count = 0;
        while (curr != null && count < k) {
            curr = curr.next;
            count++;
        }
        if (count < k)
            return head;
        curr = head;
        ListNode prev = null;
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head.next = swapKGroup(curr, k);
        return prev;
    }

    // Follow-up 3: Doubly linked list version
    // ...existing code for doubly linked list...

    public static void main(String[] args) {
        SwapNodesInPairs solution = new SwapNodesInPairs();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4))));
        System.out.println(solution.swapPairs(head)); // 2->1->4->3
        // Edge Case: Odd length
        System.out.println(solution.swapPairs(new ListNode(1, new ListNode(2, new ListNode(3))))); // 2->1->3
        // Edge Case: Single node
        System.out.println(solution.swapPairs(new ListNode(1))); // 1
        // Edge Case: Empty list
        System.out.println(solution.swapPairs(null)); // null
    }
}
