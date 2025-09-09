package linkedlist.hard;

import java.util.*;

/**
 * LeetCode Custom: Remove Duplicates from Sorted Doubly Linked List
 * https://leetcode.com/problems/remove-duplicates-from-sorted-list/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: Medium (Asked in 60+ interviews)
 *
 * Description: Remove all duplicates from a sorted doubly linked list, leaving
 * only distinct numbers.
 *
 * Constraints:
 * - Number of nodes <= 300
 * - -100 <= Node.val <= 100
 * - List is sorted in ascending order
 * 
 * Follow-up Questions:
 * 1. What if list is not sorted?
 * 2. Can you do it in one pass?
 * 3. How to handle edge cases with all duplicates?
 */
public class RemoveDuplicatesFromDoublyLinkedList {

    static class DoublyListNode {
        int val;
        DoublyListNode prev;
        DoublyListNode next;

        DoublyListNode(int val) {
            this.val = val;
        }
    }

    // Approach 1: Keep first occurrence - O(n) time, O(1) space
    public DoublyListNode removeDuplicates(DoublyListNode head) {
        if (head == null)
            return null;

        DoublyListNode curr = head;

        while (curr != null && curr.next != null) {
            if (curr.val == curr.next.val) {
                DoublyListNode nodeToDelete = curr.next;
                curr.next = nodeToDelete.next;

                if (nodeToDelete.next != null) {
                    nodeToDelete.next.prev = curr;
                }
            } else {
                curr = curr.next;
            }
        }

        return head;
    }

    // Approach 2: Remove all duplicates (keep none) - O(n) time, O(1) space
    public DoublyListNode removeDuplicatesII(DoublyListNode head) {
        if (head == null)
            return null;

        DoublyListNode dummy = new DoublyListNode(0);
        dummy.next = head;
        head.prev = dummy;

        DoublyListNode prev = dummy;
        DoublyListNode curr = head;

        while (curr != null) {
            if (curr.next != null && curr.val == curr.next.val) {
                int duplicateVal = curr.val;

                // Skip all nodes with duplicate value
                while (curr != null && curr.val == duplicateVal) {
                    curr = curr.next;
                }

                // Connect prev to curr
                prev.next = curr;
                if (curr != null) {
                    curr.prev = prev;
                }
            } else {
                prev = curr;
                curr = curr.next;
            }
        }

        DoublyListNode result = dummy.next;
        if (result != null) {
            result.prev = null;
        }
        return result;
    }

    // Approach 3: Using frequency count - O(n) time, O(n) space
    public DoublyListNode removeDuplicatesFreq(DoublyListNode head) {
        if (head == null)
            return null;

        // Count frequencies
        Map<Integer, Integer> freq = new HashMap<>();
        DoublyListNode curr = head;
        while (curr != null) {
            freq.put(curr.val, freq.getOrDefault(curr.val, 0) + 1);
            curr = curr.next;
        }

        DoublyListNode dummy = new DoublyListNode(0);
        DoublyListNode prev = dummy;
        curr = head;

        while (curr != null) {
            if (freq.get(curr.val) == 1) {
                prev.next = curr;
                curr.prev = prev;
                prev = curr;
            }
            curr = curr.next;
        }

        prev.next = null;
        DoublyListNode result = dummy.next;
        if (result != null) {
            result.prev = null;
        }
        return result;
    }

    // Helper: Build doubly linked list from array
    private DoublyListNode buildList(int[] arr) {
        if (arr.length == 0)
            return null;

        DoublyListNode head = new DoublyListNode(arr[0]);
        DoublyListNode curr = head;

        for (int i = 1; i < arr.length; i++) {
            DoublyListNode newNode = new DoublyListNode(arr[i]);
            curr.next = newNode;
            newNode.prev = curr;
            curr = newNode;
        }

        return head;
    }

    // Helper: Print doubly linked list
    private void printList(DoublyListNode head) {
        System.out.print("Forward: ");
        DoublyListNode curr = head;
        DoublyListNode tail = null;

        while (curr != null) {
            System.out.print(curr.val + " ");
            tail = curr;
            curr = curr.next;
        }
        System.out.println();

        System.out.print("Backward: ");
        curr = tail;
        while (curr != null) {
            System.out.print(curr.val + " ");
            curr = curr.prev;
        }
        System.out.println();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        RemoveDuplicatesFromDoublyLinkedList solver = new RemoveDuplicatesFromDoublyLinkedList();

        // Test case 1: Keep first occurrence
        DoublyListNode head1 = solver.buildList(new int[] { 1, 1, 2, 3, 3 });
        System.out.println("Original list:");
        solver.printList(head1);
        DoublyListNode result1 = solver.removeDuplicates(head1);
        System.out.println("Remove duplicates (keep first):");
        solver.printList(result1);

        // Test case 2: Remove all duplicates
        DoublyListNode head2 = solver.buildList(new int[] { 1, 2, 3, 3, 4, 4, 5 });
        System.out.println("\nOriginal list:");
        solver.printList(head2);
        DoublyListNode result2 = solver.removeDuplicatesII(head2);
        System.out.println("Remove all duplicates:");
        solver.printList(result2);

        // Test case 3: All duplicates
        DoublyListNode head3 = solver.buildList(new int[] { 1, 1, 1, 1 });
        System.out.println("\nAll duplicates:");
        solver.printList(head3);
        DoublyListNode result3 = solver.removeDuplicatesII(head3);
        System.out.println("After removing all:");
        solver.printList(result3);

        // Test case 4: Using frequency approach
        DoublyListNode head4 = solver.buildList(new int[] { 1, 2, 2, 3, 4, 4, 5 });
        DoublyListNode result4 = solver.removeDuplicatesFreq(head4);
        System.out.println("\nFrequency approach result:");
        solver.printList(result4);
    }
}
