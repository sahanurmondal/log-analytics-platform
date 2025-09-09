package linkedlist.hard;

import linkedlist.ListNode;

/**
 * LeetCode 25: Reverse Nodes in k-Group
 * https://leetcode.com/problems/reverse-nodes-in-k-group/
 *
 * Description:
 * Given a linked list, reverse the nodes of a linked list k at a time and
 * return its modified list.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [0, 5000].
 * - 1 <= k <= 5000
 */
public class ReverseNodesInKGroup {
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k == 1) {
            return head;
        }

        // Check if we have at least k nodes
        ListNode current = head;
        int count = 0;
        while (current != null && count < k) {
            current = current.next;
            count++;
        }

        if (count == k) {
            // Reverse the first k nodes
            current = reverseKGroup(current, k);

            // Reverse current group
            while (count > 0) {
                ListNode next = head.next;
                head.next = current;
                current = head;
                head = next;
                count--;
            }
            head = current;
        }

        return head;
    }

    public static void main(String[] args) {
        ReverseNodesInKGroup solution = new ReverseNodesInKGroup();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));
        System.out.println(solution.reverseKGroup(head, 2)); // 2->1->4->3->5
        System.out.println(solution.reverseKGroup(head, 3)); // 3->2->1->4->5
        // Edge Case: k == 1
        System.out.println(solution.reverseKGroup(head, 1)); // unchanged
        // Edge Case: k > length
        System.out.println(solution.reverseKGroup(head, 10)); // unchanged
        // Edge Case: Empty list
        System.out.println(solution.reverseKGroup(null, 2)); // null
    }
}

// ...existing ListNode class...
