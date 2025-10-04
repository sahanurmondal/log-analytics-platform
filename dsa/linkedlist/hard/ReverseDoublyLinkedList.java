package linkedlist.hard;

import java.util.*;

/**
 * LeetCode Custom: Reverse Doubly Linked List
 * https://leetcode.com/problems/reverse-linked-list/ (Extended)
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: Medium (Asked in 40+ interviews)
 *
 * Description: Reverse a doubly linked list by swapping prev and next pointers.
 *
 * Constraints:
 * - Number of nodes <= 1000
 * - -1000 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you reverse recursively?
 * 2. How to reverse only a portion of the list?
 * 3. Can you reverse in groups of k?
 */
public class ReverseDoublyLinkedList {

    static class DoublyListNode {
        int val;
        DoublyListNode prev;
        DoublyListNode next;

        DoublyListNode(int val) {
            this.val = val;
        }
    }

    // Approach 1: Iterative - O(n) time, O(1) space
    public DoublyListNode reverseList(DoublyListNode head) {
        if (head == null)
            return null;

        DoublyListNode current = head;
        DoublyListNode temp = null;

        // Swap next and prev for all nodes
        while (current != null) {
            temp = current.prev;
            current.prev = current.next;
            current.next = temp;
            current = current.prev; // Move to next node (which is now prev)
        }

        // Before changing head, check for cases like N = 0 and N = 1
        if (temp != null) {
            head = temp.prev;
        }

        return head;
    }

    // Approach 2: Recursive - O(n) time, O(n) space
    public DoublyListNode reverseListRecursive(DoublyListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        // Reverse the rest of the list
        DoublyListNode newHead = reverseListRecursive(head.next);

        // Reverse current connections
        head.next.next = head;
        head.next.prev = head.prev;
        head.prev = head.next;
        head.next = null;

        return newHead;
    }

    // Approach 3: Using stack - O(n) time, O(n) space
    public DoublyListNode reverseListStack(DoublyListNode head) {
        if (head == null)
            return null;

        Stack<DoublyListNode> stack = new Stack<>();
        DoublyListNode curr = head;

        // Push all nodes to stack
        while (curr != null) {
            stack.push(curr);
            curr = curr.next;
        }

        // Pop nodes and rebuild list
        DoublyListNode newHead = stack.pop();
        curr = newHead;
        curr.prev = null;

        while (!stack.isEmpty()) {
            DoublyListNode node = stack.pop();
            curr.next = node;
            node.prev = curr;
            node.next = null;
            curr = node;
        }

        return newHead;
    }

    // Follow-up 1: Reverse portion of list between positions
    public DoublyListNode reverseBetween(DoublyListNode head, int left, int right) {
        if (head == null || left == right)
            return head;

        DoublyListNode dummy = new DoublyListNode(0);
        dummy.next = head;
        head.prev = dummy;

        DoublyListNode leftPrev = dummy;

        // Move to position before left
        for (int i = 0; i < left - 1; i++) {
            leftPrev = leftPrev.next;
        }

        DoublyListNode leftNode = leftPrev.next;
        DoublyListNode rightNode = leftNode;

        // Find right node
        for (int i = 0; i < right - left; i++) {
            rightNode = rightNode.next;
        }

        DoublyListNode rightNext = rightNode.next;

        // Cut the portion to reverse
        leftPrev.next = null;
        leftNode.prev = null;
        rightNode.next = null;
        if (rightNext != null) {
            rightNext.prev = null;
        }

        // Reverse the portion
        DoublyListNode reversedHead = reverseList(leftNode);

        // Reconnect
        leftPrev.next = reversedHead;
        reversedHead.prev = leftPrev;

        leftNode.next = rightNext;
        if (rightNext != null) {
            rightNext.prev = leftNode;
        }

        DoublyListNode result = dummy.next;
        if (result != null) {
            result.prev = null;
        }
        return result;
    }

    // Follow-up 2: Reverse in groups of k
    public DoublyListNode reverseKGroup(DoublyListNode head, int k) {
        if (head == null || k <= 1)
            return head;

        // Check if we have k nodes
        DoublyListNode curr = head;
        int count = 0;
        while (curr != null && count < k) {
            curr = curr.next;
            count++;
        }

        if (count == k) {
            // We have k nodes, reverse them
            curr = reverseKGroup(curr, k); // Reverse next group

            // Reverse current group
            while (count-- > 0) {
                DoublyListNode next = head.next;
                head.next = curr;
                if (curr != null) {
                    curr.prev = head;
                }
                DoublyListNode temp = head.prev;
                head.prev = curr;
                curr = head;
                head = next;
            }
            head = curr;
        }

        return head;
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

    // Helper: Print list forward and backward
    private void printList(DoublyListNode head) {
        if (head == null) {
            System.out.println("Empty list");
            return;
        }

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

    // Helper: Validate doubly linked list structure
    private boolean validateList(DoublyListNode head) {
        if (head == null)
            return true;
        if (head.prev != null)
            return false;

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
        ReverseDoublyLinkedList solver = new ReverseDoublyLinkedList();

        // Test case 1: Basic reversal
        DoublyListNode head1 = solver.buildList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("Test 1 - Original:");
        solver.printList(head1);
        DoublyListNode result1 = solver.reverseList(head1);
        System.out.println("Reversed (Iterative):");
        solver.printList(result1);
        System.out.println("Valid: " + solver.validateList(result1));

        // Test case 2: Recursive reversal
        DoublyListNode head2 = solver.buildList(new int[] { 1, 2, 3, 4 });
        System.out.println("\nTest 2 - Recursive:");
        solver.printList(head2);
        DoublyListNode result2 = solver.reverseListRecursive(head2);
        System.out.println("Reversed (Recursive):");
        solver.printList(result2);

        // Test case 3: Stack-based reversal
        DoublyListNode head3 = solver.buildList(new int[] { 1, 2, 3 });
        System.out.println("\nTest 3 - Stack-based:");
        solver.printList(head3);
        DoublyListNode result3 = solver.reverseListStack(head3);
        System.out.println("Reversed (Stack):");
        solver.printList(result3);

        // Test case 4: Reverse between positions
        DoublyListNode head4 = solver.buildList(new int[] { 1, 2, 3, 4, 5, 6 });
        System.out.println("\nTest 4 - Reverse between 2 and 4:");
        solver.printList(head4);
        DoublyListNode result4 = solver.reverseBetween(head4, 2, 4);
        System.out.println("Result:");
        solver.printList(result4);

        // Test case 5: Single node
        DoublyListNode head5 = solver.buildList(new int[] { 42 });
        System.out.println("\nTest 5 - Single node:");
        solver.printList(head5);
        DoublyListNode result5 = solver.reverseList(head5);
        System.out.println("Reversed:");
        solver.printList(result5);
    }
}
