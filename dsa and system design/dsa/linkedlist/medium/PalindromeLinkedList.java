package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 234: Palindrome Linked List
 * https://leetcode.com/problems/palindrome-linked-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: High
 *
 * Description:
 * Check if a linked list is a palindrome.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 *
 * Follow-ups:
 * 1. Can you do it in O(1) space?
 * 2. Can you restore the list after checking?
 * 3. Can you handle doubly linked lists?
 */
public class PalindromeLinkedList {
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null)
            return true;
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode second = reverse(slow);
        ListNode first = head;
        boolean result = true;
        ListNode secondCopy = second;
        while (second != null) {
            if (first.val != second.val) {
                result = false;
                break;
            }
            first = first.next;
            second = second.next;
        }
        reverse(secondCopy); // Restore
        return result;
    }

    private ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    // Follow-up 1: O(1) space (already handled above)
    // Follow-up 2: Restore list (already handled above)
    // Follow-up 3: Doubly linked list version
    // ...existing code for doubly linked list...

    public static void main(String[] args) {
        PalindromeLinkedList solution = new PalindromeLinkedList();
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(2, new ListNode(1))));
        System.out.println(solution.isPalindrome(head)); // true
        // Edge Case: Odd length
        System.out.println(solution
                .isPalindrome(new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(2, new ListNode(1))))))); // true
        // Edge Case: Not palindrome
        System.out.println(solution.isPalindrome(new ListNode(1, new ListNode(2)))); // false
        // Edge Case: Single node
        System.out.println(solution.isPalindrome(new ListNode(1))); // true
    }
}
