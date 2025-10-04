package linkedlist.hard;

import java.util.*;

/**
 * LeetCode 92: Reverse Linked List II
 * https://leetcode.com/problems/reverse-linked-list-ii/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 50+ interviews)
 *
 * Description: Reverse a linked list from position m to n.
 *
 * Constraints:
 * - Number of nodes <= 10^4
 * - 1 <= m <= n <= length of list
 * 
 * Follow-up Questions:
 * 1. Can you reverse recursively?
 * 2. What if m or n is out of bounds?
 * 3. Can you reverse in-place?
 */
public class ReverseLinkedListBetweenMAndN {

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }

    // Approach 1: Iterative - O(n) time, O(1) space
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (head == null || m == n)
            return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        // Move to position before m
        for (int i = 1; i < m; i++) {
            prev = prev.next;
        }

        ListNode curr = prev.next;
        // Reverse n-m nodes
        for (int i = 0; i < n - m; i++) {
            ListNode temp = curr.next;
            curr.next = temp.next;
            temp.next = prev.next;
            prev.next = temp;
        }

        return dummy.next;
    }

    // Approach 2: Recursive with helper
    public ListNode reverseBetweenRecursive(ListNode head, int m, int n) {
        if (head == null || m == n)
            return head;

        if (m == 1) {
            return reverseFirstN(head, n);
        }

        head.next = reverseBetweenRecursive(head.next, m - 1, n - 1);
        return head;
    }

    private ListNode successor = null;

    private ListNode reverseFirstN(ListNode head, int n) {
        if (n == 1) {
            successor = head.next;
            return head;
        }

        ListNode last = reverseFirstN(head.next, n - 1);
        head.next.next = head;
        head.next = successor;
        return last;
    }

    // Helper: Build list from array
    private ListNode buildList(int[] arr) {
        if (arr.length == 0)
            return null;
        ListNode head = new ListNode(arr[0]);
        ListNode curr = head;
        for (int i = 1; i < arr.length; i++) {
            curr.next = new ListNode(arr[i]);
            curr = curr.next;
        }
        return head;
    }

    // Helper: Print list
    private void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
        System.out.println();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ReverseLinkedListBetweenMAndN solver = new ReverseLinkedListBetweenMAndN();

        // Test case 1: [1,2,3,4,5], m=2, n=4
        ListNode head1 = solver.buildList(new int[] { 1, 2, 3, 4, 5 });
        System.out.print("Original: ");
        solver.printList(head1);
        ListNode result1 = solver.reverseBetween(head1, 2, 4);
        System.out.print("Reversed (2,4): ");
        solver.printList(result1);

        // Test case 2: Single node
        ListNode head2 = solver.buildList(new int[] { 5 });
        ListNode result2 = solver.reverseBetween(head2, 1, 1);
        System.out.print("Single node: ");
        solver.printList(result2);

        // Test case 3: Recursive approach
        ListNode head3 = solver.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode result3 = solver.reverseBetweenRecursive(head3, 2, 4);
        System.out.print("Recursive (2,4): ");
        solver.printList(result3);
    }
}
