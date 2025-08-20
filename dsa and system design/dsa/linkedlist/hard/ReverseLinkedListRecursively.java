package linkedlist.hard;

import linkedlist.ListNode;

/**
 * LeetCode 206: Reverse Linked List (Recursive Approach)
 * https://leetcode.com/problems/reverse-linked-list/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 200+ interviews)
 *
 * Description: Reverse a singly linked list using recursion.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [0, 5000].
 * - -5000 <= Node.val <= 5000
 * 
 * Follow-up Questions:
 * 1. Can you reverse iteratively as well?
 * 2. What's the space complexity of recursive approach?
 * 3. How to reverse only a portion of the list?
 */
public class ReverseLinkedListRecursively {

    // Approach 1: Standard Recursion - O(n) time, O(n) space
    public ListNode reverseListRecursive(ListNode head) {
        // Base case: empty list or single node
        if (head == null || head.next == null) {
            return head;
        }

        // Recursively reverse the rest of the list
        ListNode newHead = reverseListRecursive(head.next);

        // Reverse the current connection
        head.next.next = head;
        head.next = null;

        return newHead;
    }

    // Approach 2: Tail Recursion with Helper - O(n) time, O(n) space
    public ListNode reverseListTailRecursive(ListNode head) {
        return reverseHelper(head, null);
    }

    private ListNode reverseHelper(ListNode current, ListNode previous) {
        // Base case: reached end of list
        if (current == null) {
            return previous;
        }

        ListNode next = current.next;
        current.next = previous;
        return reverseHelper(next, current);
    }

    // Approach 3: Recursive with accumulator pattern - O(n) time, O(n) space
    public ListNode reverseListAccumulator(ListNode head) {
        return reverseAccumulator(head, null);
    }

    private ListNode reverseAccumulator(ListNode head, ListNode acc) {
        if (head == null)
            return acc;

        ListNode next = head.next;
        head.next = acc;
        return reverseAccumulator(next, head);
    }

    // Follow-up: Reverse first N nodes recursively
    private ListNode successor = null;

    public ListNode reverseFirstN(ListNode head, int n) {
        if (n == 1) {
            successor = head.next;
            return head;
        }

        ListNode last = reverseFirstN(head.next, n - 1);
        head.next.next = head;
        head.next = successor;
        return last;
    }

    // Follow-up: Reverse between positions m and n
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (m == 1) {
            return reverseFirstN(head, n);
        }

        head.next = reverseBetween(head.next, m - 1, n - 1);
        return head;
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

    // Helper: Get list length
    private int getLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
    }

    public static void main(String[] args) {
        ReverseLinkedListRecursively solution = new ReverseLinkedListRecursively();

        // Test case 1: Basic reversal
        ListNode head1 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        System.out.print("Original: ");
        solution.printList(head1);
        ListNode reversed1 = solution.reverseListRecursive(head1);
        System.out.print("Reversed (Standard): ");
        solution.printList(reversed1);

        // Test case 2: Tail recursive approach
        ListNode head2 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode reversed2 = solution.reverseListTailRecursive(head2);
        System.out.print("Reversed (Tail Recursive): ");
        solution.printList(reversed2);

        // Test case 3: Accumulator pattern
        ListNode head3 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode reversed3 = solution.reverseListAccumulator(head3);
        System.out.print("Reversed (Accumulator): ");
        solution.printList(reversed3);

        // Test case 4: Edge case - empty list
        ListNode empty = solution.reverseListRecursive(null);
        System.out.print("Empty list: ");
        solution.printList(empty);

        // Test case 5: Edge case - single node
        ListNode single = solution.reverseListRecursive(new ListNode(42));
        System.out.print("Single node: ");
        solution.printList(single);

        // Test case 6: Follow-up - reverse first N nodes
        ListNode head4 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        System.out.print("Original: ");
        solution.printList(head4);
        ListNode reversedN = solution.reverseFirstN(head4, 3);
        System.out.print("Reverse first 3: ");
        solution.printList(reversedN);

        // Test case 7: Follow-up - reverse between positions
        ListNode head5 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode reversedBetween = solution.reverseBetween(head5, 2, 4);
        System.out.print("Reverse between 2 and 4: ");
        solution.printList(reversedBetween);

        // Test case 8: Large list performance test
        int[] largeArray = new int[1000];
        for (int i = 0; i < 1000; i++) {
            largeArray[i] = i + 1;
        }
        ListNode largeList = solution.buildList(largeArray);
        long startTime = System.nanoTime();
        solution.reverseListRecursive(largeList);
        long endTime = System.nanoTime();
        System.out.println("Large list (1000 nodes) reversed in " +
                (endTime - startTime) / 1_000_000.0 + " ms");
    }
}
