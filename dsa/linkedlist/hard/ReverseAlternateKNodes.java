package linkedlist.hard;

import java.util.*;

/**
 * LeetCode Custom: Reverse Alternate K Nodes
 * https://leetcode.com/problems/reverse-nodes-in-k-group/ (Modified)
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 70+ interviews)
 *
 * Description: Reverse alternate groups of k nodes in a linked list.
 * For example, if k=2: 1->2->3->4->5->6 becomes 2->1->4->3->5->6
 *
 * Constraints:
 * - Number of nodes <= 5000
 * - 1 <= k <= number of nodes
 * - 1 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you do it recursively?
 * 2. What if k is larger than remaining nodes?
 * 3. How to handle different patterns (reverse every 2nd group, etc.)?
 */
public class ReverseAlternateKNodes {

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }

    // Approach 1: Iterative - O(n) time, O(1) space
    public ListNode reverseAlternateKNodes(ListNode head, int k) {
        if (head == null || k <= 1)
            return head;

        ListNode curr = head;
        int count = 0;
        boolean reverse = true;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prevGroupEnd = dummy;

        while (curr != null) {
            ListNode groupStart = curr;
            ListNode groupEnd = curr;

            // Find end of current group
            for (int i = 0; i < k && groupEnd != null; i++) {
                groupEnd = groupEnd.next;
            }

            if (reverse) {
                // Reverse current group
                ListNode reversedHead = reverseKNodes(groupStart, k);
                prevGroupEnd.next = reversedHead;
                groupStart.next = groupEnd;
                prevGroupEnd = groupStart;
            } else {
                // Skip current group
                for (int i = 0; i < k && curr != null; i++) {
                    prevGroupEnd = curr;
                    curr = curr.next;
                }
                continue;
            }

            curr = groupEnd;
            reverse = !reverse; // Alternate between reverse and skip
        }

        return dummy.next;
    }

    // Approach 2: Recursive - O(n) time, O(n/k) space
    public ListNode reverseAlternateKNodesRecursive(ListNode head, int k) {
        return reverseAlternateHelper(head, k, true);
    }

    private ListNode reverseAlternateHelper(ListNode head, int k, boolean shouldReverse) {
        if (head == null)
            return null;

        ListNode curr = head;
        int count = 0;

        // Count k nodes
        while (curr != null && count < k) {
            curr = curr.next;
            count++;
        }

        if (count == k) {
            if (shouldReverse) {
                // Reverse current k nodes
                ListNode reversedHead = reverseKNodes(head, k);
                // Connect with rest of the list (don't reverse next group)
                head.next = reverseAlternateHelper(curr, k, false);
                return reversedHead;
            } else {
                // Skip current k nodes, reverse next group
                ListNode kthNode = head;
                for (int i = 1; i < k; i++) {
                    kthNode = kthNode.next;
                }
                kthNode.next = reverseAlternateHelper(curr, k, true);
                return head;
            }
        }

        return head; // Less than k nodes remaining
    }

    // Approach 3: Pattern-based reversal - O(n) time, O(1) space
    public ListNode reverseWithPattern(ListNode head, int k, int[] pattern) {
        if (head == null || k <= 1)
            return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prevGroupEnd = dummy;
        ListNode curr = head;
        int patternIndex = 0;

        while (curr != null) {
            boolean shouldReverse = pattern[patternIndex % pattern.length] == 1;
            patternIndex++;

            if (shouldReverse) {
                ListNode groupStart = curr;
                ListNode groupEnd = curr;

                // Find end of group
                for (int i = 0; i < k && groupEnd != null; i++) {
                    groupEnd = groupEnd.next;
                }

                ListNode reversedHead = reverseKNodes(groupStart, k);
                prevGroupEnd.next = reversedHead;
                groupStart.next = groupEnd;
                prevGroupEnd = groupStart;
                curr = groupEnd;
            } else {
                // Skip k nodes
                for (int i = 0; i < k && curr != null; i++) {
                    prevGroupEnd = curr;
                    curr = curr.next;
                }
            }
        }

        return dummy.next;
    }

    // Helper: Reverse exactly k nodes starting from head
    private ListNode reverseKNodes(ListNode head, int k) {
        ListNode prev = null;
        ListNode curr = head;

        for (int i = 0; i < k && curr != null; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        return prev; // New head of reversed group
    }
}
