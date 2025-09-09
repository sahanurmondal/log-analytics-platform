package linkedlist.hard;

import linkedlist.ListNode;

/**
 * LeetCode 25: Reverse Nodes in k-Group (Recursive Approach)
 * https://leetcode.com/problems/reverse-nodes-in-k-group/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 180+ interviews)
 *
 * Description: Reverse the nodes of a linked list k at a time using recursion.
 *
 * Constraints:
 * - The number of nodes in the list is n.
 * - 1 <= k <= n <= 5000
 * - 0 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you implement without counting nodes first?
 * 2. How to handle different group sizes?
 * 3. What's the space complexity trade-off?
 */
public class ReverseLinkedListInGroupsOfKRecursively {

    // Approach 1: Pure Recursion - O(n) time, O(n/k) space
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k == 1)
            return head;

        // Check if we have at least k nodes
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            if (curr == null)
                return head; // Not enough nodes
            curr = curr.next;
        }

        // Reverse first k nodes
        ListNode prev = null;
        curr = head;
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        // Recursively process remaining groups
        head.next = reverseKGroup(curr, k);
        return prev;
    }

    // Approach 2: Tail Recursion with accumulator - O(n) time, O(n/k) space
    public ListNode reverseKGroupTail(ListNode head, int k) {
        return reverseKGroupTailHelper(head, k, null);
    }

    private ListNode reverseKGroupTailHelper(ListNode head, int k, ListNode processedTail) {
        if (head == null)
            return processedTail;

        // Check if we have k nodes
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            if (curr == null) {
                // Connect remaining nodes to processed part
                return connectLists(head, processedTail);
            }
            curr = curr.next;
        }

        // Reverse current group
        ReverseResult result = reverseKNodes(head, k);

        // Connect with processed part
        result.tail.next = processedTail;

        // Recursively process remaining
        return reverseKGroupTailHelper(curr, k, result.head);
    }

    // Approach 3: Continuation-based recursion - O(n) time, O(n/k) space
    public ListNode reverseKGroupContinuation(ListNode head, int k) {
        return reverseKGroupWithContinuation(head, k, tail -> tail);
    }

    private ListNode reverseKGroupWithContinuation(ListNode head, int k,
            java.util.function.Function<ListNode, ListNode> continuation) {
        if (head == null)
            return continuation.apply(null);

        // Check if we have k nodes
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            if (curr == null)
                return continuation.apply(head);
            curr = curr.next;
        }

        // Reverse current group
        ReverseResult result = reverseKNodes(head, k);

        // Continue with remaining nodes
        return reverseKGroupWithContinuation(curr, k,
                remainingHead -> {
                    result.tail.next = remainingHead;
                    return continuation.apply(result.head);
                });
    }

    // Follow-up 1: Reverse alternate groups recursively
    public ListNode reverseAlternateKGroups(ListNode head, int k) {
        return reverseAlternateHelper(head, k, true);
    }

    private ListNode reverseAlternateHelper(ListNode head, int k, boolean shouldReverse) {
        if (head == null)
            return null;

        // Check if we have k nodes
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            if (curr == null)
                return head;
            curr = curr.next;
        }

        if (shouldReverse) {
            // Reverse current group
            ReverseResult result = reverseKNodes(head, k);
            result.tail.next = reverseAlternateHelper(curr, k, false);
            return result.head;
        } else {
            // Skip current group
            ListNode kthNode = head;
            for (int i = 1; i < k; i++) {
                kthNode = kthNode.next;
            }
            kthNode.next = reverseAlternateHelper(curr, k, true);
            return head;
        }
    }

    // Follow-up 2: Reverse with different group sizes
    public ListNode reverseVariableGroups(ListNode head, int[] groupSizes) {
        return reverseVariableHelper(head, groupSizes, 0);
    }

    private ListNode reverseVariableHelper(ListNode head, int[] groupSizes, int index) {
        if (head == null || index >= groupSizes.length)
            return head;

        int k = groupSizes[index];

        // Check if we have k nodes
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            if (curr == null)
                return head;
            curr = curr.next;
        }

        // Reverse current group
        ReverseResult result = reverseKNodes(head, k);
        result.tail.next = reverseVariableHelper(curr, groupSizes, index + 1);
        return result.head;
    }

    // Follow-up 3: Reverse with condition checking
    public ListNode reverseConditionalGroups(ListNode head, int k,
            java.util.function.Predicate<ListNode> condition) {
        if (head == null || k == 1)
            return head;

        // Check if we have k nodes and they satisfy condition
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            if (curr == null || !condition.test(curr))
                return head;
            curr = curr.next;
        }

        // Reverse current group
        ReverseResult result = reverseKNodes(head, k);
        result.tail.next = reverseConditionalGroups(curr, k, condition);
        return result.head;
    }

    // Follow-up 4: Reverse with group numbering
    public ListNode reverseNumberedGroups(ListNode head, int k,
            java.util.function.IntPredicate shouldReverse) {
        return reverseNumberedHelper(head, k, shouldReverse, 1);
    }

    private ListNode reverseNumberedHelper(ListNode head, int k,
            java.util.function.IntPredicate shouldReverse,
            int groupNumber) {
        if (head == null)
            return null;

        // Check if we have k nodes
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            if (curr == null)
                return head;
            curr = curr.next;
        }

        if (shouldReverse.test(groupNumber)) {
            // Reverse current group
            ReverseResult result = reverseKNodes(head, k);
            result.tail.next = reverseNumberedHelper(curr, k, shouldReverse, groupNumber + 1);
            return result.head;
        } else {
            // Skip current group
            ListNode kthNode = head;
            for (int i = 1; i < k; i++) {
                kthNode = kthNode.next;
            }
            kthNode.next = reverseNumberedHelper(curr, k, shouldReverse, groupNumber + 1);
            return head;
        }
    }

    // Helper methods
    private ReverseResult reverseKNodes(ListNode head, int k) {
        ListNode prev = null;
        ListNode curr = head;
        ListNode originalHead = head;

        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        return new ReverseResult(prev, originalHead);
    }

    private ListNode connectLists(ListNode first, ListNode second) {
        if (first == null)
            return second;

        ListNode curr = first;
        while (curr.next != null) {
            curr = curr.next;
        }
        curr.next = second;
        return first;
    }

    private static class ReverseResult {
        ListNode head;
        ListNode tail;

        ReverseResult(ListNode head, ListNode tail) {
            this.head = head;
            this.tail = tail;
        }
    }

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

    private void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ReverseLinkedListInGroupsOfKRecursively solution = new ReverseLinkedListInGroupsOfKRecursively();

        // Test case 1: Pure recursion
        ListNode head1 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6 });
        System.out.print("Original: ");
        solution.printList(head1);
        ListNode result1 = solution.reverseKGroup(head1, 2);
        System.out.print("Pure recursion (k=2): ");
        solution.printList(result1);

        // Test case 2: Tail recursion
        ListNode head2 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6 });
        ListNode result2 = solution.reverseKGroupTail(head2, 3);
        System.out.print("Tail recursion (k=3): ");
        solution.printList(result2);

        // Test case 3: Continuation-based
        ListNode head3 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        ListNode result3 = solution.reverseKGroupContinuation(head3, 2);
        System.out.print("Continuation-based (k=2): ");
        solution.printList(result3);

        // Test case 4: Follow-up - Alternate groups
        ListNode head4 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        ListNode result4 = solution.reverseAlternateKGroups(head4, 2);
        System.out.print("Alternate groups (k=2): ");
        solution.printList(result4);

        // Test case 5: Follow-up - Variable group sizes
        ListNode head5 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        int[] groupSizes = { 2, 3, 2 };
        ListNode result5 = solution.reverseVariableGroups(head5, groupSizes);
        System.out.print("Variable groups [2,3,2]: ");
        solution.printList(result5);

        // Test case 6: Follow-up - Conditional reversal
        ListNode head6 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6 });
        ListNode result6 = solution.reverseConditionalGroups(head6, 2, node -> node.val % 2 == 1);
        System.out.print("Conditional (odd values): ");
        solution.printList(result6);

        // Test case 7: Follow-up - Numbered groups
        ListNode head7 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        ListNode result7 = solution.reverseNumberedGroups(head7, 2, groupNum -> groupNum % 2 == 1);
        System.out.print("Odd-numbered groups: ");
        solution.printList(result7);

        // Test case 8: Edge cases
        ListNode single = solution.buildList(new int[] { 1 });
        ListNode singleResult = solution.reverseKGroup(single, 2);
        System.out.print("Insufficient nodes: ");
        solution.printList(singleResult);
    }
}
