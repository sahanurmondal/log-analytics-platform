package linkedlist.hard;

import linkedlist.ListNode;

/**
 * LeetCode 92: Reverse Linked List II (Recursive Approach)
 * https://leetcode.com/problems/reverse-linked-list-ii/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 150+ interviews)
 *
 * Description: Reverse a linked list from position left to right recursively.
 *
 * Constraints:
 * - The number of nodes in the list is n.
 * - 1 <= n <= 500
 * - -500 <= Node.val <= 500
 * - 1 <= left <= right <= n
 * 
 * Follow-up Questions:
 * 1. Can you do it iteratively as well?
 * 2. What if we need to reverse multiple segments?
 * 3. How to handle edge cases efficiently?
 */
public class ReverseLinkedListBetweenMAndNRecursively {

    private ListNode successor = null;

    // Approach 1: Recursive - O(n) time, O(n) space
    public ListNode reverseBetween(ListNode head, int left, int right) {
        if (left == 1) {
            return reverseFirstN(head, right);
        }

        head.next = reverseBetween(head.next, left - 1, right - 1);
        return head;
    }

    // Helper: Reverse first N nodes
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

    // Approach 2: Pure Recursive without global variable - O(n) time, O(n) space
    public ListNode reverseBetweenPure(ListNode head, int left, int right) {
        return reverseBetweenHelper(head, left, right).head;
    }

    private ReverseResult reverseBetweenHelper(ListNode head, int left, int right) {
        if (left == 1) {
            return reverseFirstNHelper(head, right);
        }

        ReverseResult result = reverseBetweenHelper(head.next, left - 1, right - 1);
        head.next = result.head;
        return new ReverseResult(head, result.tail);
    }

    private ReverseResult reverseFirstNHelper(ListNode head, int n) {
        if (n == 1) {
            return new ReverseResult(head, head.next);
        }

        ReverseResult result = reverseFirstNHelper(head.next, n - 1);
        head.next.next = head;
        head.next = result.tail;
        return new ReverseResult(result.head, result.tail);
    }

    private static class ReverseResult {
        ListNode head;
        ListNode tail;

        ReverseResult(ListNode head, ListNode tail) {
            this.head = head;
            this.tail = tail;
        }
    }

    // Follow-up 1: Reverse multiple segments
    public ListNode reverseMultipleSegments(ListNode head, int[][] segments) {
        for (int[] segment : segments) {
            head = reverseBetween(head, segment[0], segment[1]);
        }
        return head;
    }

    // Follow-up 2: Reverse alternating segments of size k
    public ListNode reverseAlternatingSegments(ListNode head, int k) {
        return reverseAlternatingHelper(head, k, true);
    }

    private ListNode reverseAlternatingHelper(ListNode head, int k, boolean shouldReverse) {
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
                ListNode reversedHead = reverseFirstN(head, k);
                head.next = reverseAlternatingHelper(curr, k, false);
                return reversedHead;
            } else {
                ListNode kthNode = head;
                for (int i = 1; i < k; i++) {
                    kthNode = kthNode.next;
                }
                kthNode.next = reverseAlternatingHelper(curr, k, true);
                return head;
            }
        }

        return head;
    }

    // Follow-up 3: Reverse with condition checking
    public ListNode reverseWithCondition(ListNode head, int left, int right,
            java.util.function.Predicate<Integer> condition) {
        if (!isValidRange(head, left, right, condition)) {
            return head;
        }
        return reverseBetween(head, left, right);
    }

    private boolean isValidRange(ListNode head, int left, int right,
            java.util.function.Predicate<Integer> condition) {
        ListNode curr = head;
        int pos = 1;

        while (curr != null && pos <= right) {
            if (pos >= left && !condition.test(curr.val)) {
                return false;
            }
            curr = curr.next;
            pos++;
        }
        return true;
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

    public static void main(String[] args) {
        ReverseLinkedListBetweenMAndNRecursively solution = new ReverseLinkedListBetweenMAndNRecursively();

        // Test case 1: Basic reversal
        ListNode head1 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        System.out.print("Original: ");
        solution.printList(head1);
        ListNode result1 = solution.reverseBetween(head1, 2, 4);
        System.out.print("Reverse between 2 and 4: ");
        solution.printList(result1);

        // Test case 2: Pure recursive approach
        ListNode head2 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode result2 = solution.reverseBetweenPure(head2, 2, 4);
        System.out.print("Pure recursive (2-4): ");
        solution.printList(result2);

        // Test case 3: Reverse entire list
        ListNode head3 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode result3 = solution.reverseBetween(head3, 1, 5);
        System.out.print("Reverse entire list: ");
        solution.printList(result3);

        // Test case 4: Single element range
        ListNode head4 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode result4 = solution.reverseBetween(head4, 3, 3);
        System.out.print("Single element (3-3): ");
        solution.printList(result4);

        // Test case 5: Follow-up - Multiple segments
        ListNode head5 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        int[][] segments = { { 2, 4 }, { 6, 8 } };
        ListNode result5 = solution.reverseMultipleSegments(head5, segments);
        System.out.print("Multiple segments: ");
        solution.printList(result5);

        // Test case 6: Follow-up - Alternating segments
        ListNode head6 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        ListNode result6 = solution.reverseAlternatingSegments(head6, 2);
        System.out.print("Alternating segments of 2: ");
        solution.printList(result6);

        // Test case 7: Follow-up - Conditional reversal
        ListNode head7 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode result7 = solution.reverseWithCondition(head7, 2, 4, x -> x > 1);
        System.out.print("Conditional reverse (>1): ");
        solution.printList(result7);

        // Test case 8: Edge cases
        ListNode single = solution.buildList(new int[] { 42 });
        ListNode singleResult = solution.reverseBetween(single, 1, 1);
        System.out.print("Single node: ");
        solution.printList(singleResult);
    }
}
