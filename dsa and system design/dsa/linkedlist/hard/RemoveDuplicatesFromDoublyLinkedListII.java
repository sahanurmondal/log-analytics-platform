package linkedlist.hard;

import java.util.*;

/**
 * LeetCode 82: Remove Duplicates from Sorted List II (Doubly Linked List
 * Version)
 * https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 90+ interviews)
 *
 * Description: Remove all nodes that have duplicate numbers in a sorted doubly
 * linked list,
 * leaving only distinct numbers from the original list.
 *
 * Constraints:
 * - Number of nodes <= 300
 * - -100 <= Node.val <= 100
 * - List is sorted in ascending order
 * 
 * Follow-up Questions:
 * 1. Can you solve it recursively?
 * 2. What if we need to keep track of removed elements?
 * 3. How to optimize for space complexity?
 */
public class RemoveDuplicatesFromDoublyLinkedListII {

    static class DoublyListNode {
        int val;
        DoublyListNode prev;
        DoublyListNode next;

        DoublyListNode(int val) {
            this.val = val;
        }
    }

    // Approach 1: Iterative with dummy node - O(n) time, O(1) space
    public DoublyListNode deleteDuplicates(DoublyListNode head) {
        if (head == null || head.next == null)
            return head;

        DoublyListNode dummy = new DoublyListNode(0);
        dummy.next = head;
        head.prev = dummy;

        DoublyListNode prev = dummy;
        DoublyListNode curr = head;

        while (curr != null) {
            // Check if current node has duplicates
            if (curr.next != null && curr.val == curr.next.val) {
                int duplicateVal = curr.val;

                // Skip all nodes with this value
                while (curr != null && curr.val == duplicateVal) {
                    curr = curr.next;
                }

                // Connect previous to current (skipping duplicates)
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

    // Approach 2: Recursive approach - O(n) time, O(n) space
    public DoublyListNode deleteDuplicatesRecursive(DoublyListNode head) {
        if (head == null || head.next == null)
            return head;

        if (head.val == head.next.val) {
            // Skip all duplicates
            int val = head.val;
            while (head != null && head.val == val) {
                head = head.next;
            }
            if (head != null) {
                head.prev = null;
            }
            return deleteDuplicatesRecursive(head);
        } else {
            head.next = deleteDuplicatesRecursive(head.next);
            if (head.next != null) {
                head.next.prev = head;
            }
            return head;
        }
    }

    // Approach 3: Two-pass with set - O(n) time, O(n) space
    public DoublyListNode deleteDuplicatesTwoPass(DoublyListNode head) {
        if (head == null)
            return null;

        // First pass: identify duplicate values
        Set<Integer> duplicates = new HashSet<>();
        Set<Integer> seen = new HashSet<>();

        DoublyListNode curr = head;
        while (curr != null) {
            if (seen.contains(curr.val)) {
                duplicates.add(curr.val);
            } else {
                seen.add(curr.val);
            }
            curr = curr.next;
        }

        // Second pass: remove nodes with duplicate values
        DoublyListNode dummy = new DoublyListNode(0);
        DoublyListNode prev = dummy;
        curr = head;

        while (curr != null) {
            if (!duplicates.contains(curr.val)) {
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

    // Follow-up: Keep track of removed elements
    public List<Integer> deleteDuplicatesWithTracking(DoublyListNode head) {
        List<Integer> removedElements = new ArrayList<>();
        if (head == null)
            return removedElements;

        DoublyListNode dummy = new DoublyListNode(0);
        dummy.next = head;
        head.prev = dummy;

        DoublyListNode prev = dummy;
        DoublyListNode curr = head;

        while (curr != null) {
            if (curr.next != null && curr.val == curr.next.val) {
                int duplicateVal = curr.val;

                // Track removed elements
                while (curr != null && curr.val == duplicateVal) {
                    removedElements.add(curr.val);
                    curr = curr.next;
                }

                prev.next = curr;
                if (curr != null) {
                    curr.prev = prev;
                }
            } else {
                prev = curr;
                curr = curr.next;
            }
        }

        return removedElements;
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
        if (head == null) {
            System.out.println("Empty list");
            return;
        }

        System.out.print("Forward: ");
        DoublyListNode curr = head;
        while (curr != null) {
            System.out.print(curr.val + " ");
            curr = curr.next;
        }
        System.out.println();
    }

    // Helper: Validate doubly linked list structure
    private boolean validateList(DoublyListNode head) {
        if (head == null)
            return true;

        DoublyListNode curr = head;
        while (curr.next != null) {
            if (curr.next.prev != curr)
                return false;
            curr = curr.next;
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        RemoveDuplicatesFromDoublyLinkedListII solver = new RemoveDuplicatesFromDoublyLinkedListII();

        // Test case 1: Basic case with duplicates
        DoublyListNode head1 = solver.buildList(new int[] { 1, 2, 3, 3, 4, 4, 5 });
        System.out.println("Test 1 - Original:");
        solver.printList(head1);
        DoublyListNode result1 = solver.deleteDuplicates(head1);
        System.out.println("After removing duplicates:");
        solver.printList(result1);
        System.out.println("Valid structure: " + solver.validateList(result1));

        // Test case 2: All duplicates
        DoublyListNode head2 = solver.buildList(new int[] { 1, 1, 1, 2, 2, 3, 3 });
        System.out.println("\nTest 2 - All duplicates:");
        solver.printList(head2);
        DoublyListNode result2 = solver.deleteDuplicates(head2);
        System.out.println("After removing all duplicates:");
        solver.printList(result2);

        // Test case 3: No duplicates
        DoublyListNode head3 = solver.buildList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("\nTest 3 - No duplicates:");
        solver.printList(head3);
        DoublyListNode result3 = solver.deleteDuplicates(head3);
        System.out.println("Should remain same:");
        solver.printList(result3);

        // Test case 4: Recursive approach
        DoublyListNode head4 = solver.buildList(new int[] { 1, 1, 2, 3, 3, 4 });
        System.out.println("\nTest 4 - Recursive approach:");
        solver.printList(head4);
        DoublyListNode result4 = solver.deleteDuplicatesRecursive(head4);
        System.out.println("Recursive result:");
        solver.printList(result4);

        // Test case 5: With tracking
        DoublyListNode head5 = solver.buildList(new int[] { 1, 2, 2, 3, 4, 4, 5 });
        System.out.println("\nTest 5 - With tracking:");
        solver.printList(head5);
        List<Integer> removed = solver.deleteDuplicatesWithTracking(head5);
        System.out.println("Removed elements: " + removed);
    }
}
