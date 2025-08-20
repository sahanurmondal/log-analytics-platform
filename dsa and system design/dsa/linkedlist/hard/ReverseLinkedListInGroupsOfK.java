package linkedlist.hard;

import linkedlist.ListNode;

/**
 * LeetCode 25: Reverse Nodes in k-Group
 * https://leetcode.com/problems/reverse-nodes-in-k-group/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 180+ interviews)
 *
 * Description: Reverse the nodes of a linked list k at a time.
 *
 * Constraints:
 * - The number of nodes in the list is n.
 * - 1 <= k <= n <= 5000
 * - 0 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you solve it using O(1) extra memory?
 * 2. What if we need to reverse alternate groups?
 * 3. How to handle partial groups at the end?
 */
public class ReverseLinkedListInGroupsOfK {

    // Approach 1: Iterative - O(n) time, O(1) space
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k == 1)
            return head;

        // Check if we have at least k nodes
        ListNode curr = head;
        int count = 0;
        while (curr != null && count < k) {
            curr = curr.next;
            count++;
        }

        if (count < k)
            return head; // Not enough nodes

        // Reverse first k nodes
        ListNode prev = null;
        curr = head;
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        // Recursively reverse remaining groups
        head.next = reverseKGroup(curr, k);
        return prev;
    }

    // Approach 2: Stack-based - O(n) time, O(k) space
    public ListNode reverseKGroupStack(ListNode head, int k) {
        if (head == null || k == 1)
            return head;

        java.util.Stack<ListNode> stack = new java.util.Stack<>();
        ListNode dummy = new ListNode(0);
        ListNode prev = dummy;
        ListNode curr = head;

        while (curr != null) {
            // Fill stack with k nodes
            for (int i = 0; i < k && curr != null; i++) {
                stack.push(curr);
                curr = curr.next;
            }

            // If we don't have k nodes, connect remaining as is
            if (stack.size() < k) {
                prev.next = curr;
                break;
            }

            // Pop and connect in reverse order
            while (!stack.isEmpty()) {
                prev.next = stack.pop();
                prev = prev.next;
            }
            prev.next = curr;
        }

        return dummy.next;
    }

    // Approach 3: Two-pass with length calculation - O(n) time, O(1) space
    public ListNode reverseKGroupTwoPass(ListNode head, int k) {
        int length = getLength(head);
        int groups = length / k;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prevGroupEnd = dummy;

        for (int i = 0; i < groups; i++) {
            ListNode groupStart = prevGroupEnd.next;
            ListNode groupEnd = groupStart;

            // Find group end
            for (int j = 1; j < k; j++) {
                groupEnd = groupEnd.next;
            }

            ListNode nextGroupStart = groupEnd.next;

            // Reverse current group
            reverseSegment(groupStart, groupEnd);

            // Connect with previous and next
            prevGroupEnd.next = groupEnd;
            groupStart.next = nextGroupStart;
            prevGroupEnd = groupStart;
        }

        return dummy.next;
    }

    // Follow-up 1: Reverse alternate k groups
    public ListNode reverseAlternateKGroups(ListNode head, int k) {
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

        if (count < k)
            return head;

        if (shouldReverse) {
            // Reverse current group
            ListNode prev = null;
            curr = head;
            for (int i = 0; i < k; i++) {
                ListNode next = curr.next;
                curr.next = prev;
                prev = curr;
                curr = next;
            }
            head.next = reverseAlternateHelper(curr, k, false);
            return prev;
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

    // Follow-up 2: Reverse with partial groups handled differently
    public ListNode reverseKGroupWithPartial(ListNode head, int k, boolean reversePartial) {
        if (head == null || k == 1)
            return head;

        ListNode curr = head;
        int count = 0;

        while (curr != null && count < k) {
            curr = curr.next;
            count++;
        }

        if (count < k && !reversePartial) {
            return head;
        }

        if (count < k && reversePartial) {
            // Reverse partial group
            return reverseList(head);
        }

        // Reverse full group
        ListNode prev = null;
        curr = head;
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        head.next = reverseKGroupWithPartial(curr, k, reversePartial);
        return prev;
    }

    // Follow-up 3: Count-based reversal
    public ListNode reverseEveryKthGroup(ListNode head, int k, int interval) {
        return reverseEveryKthHelper(head, k, interval, 1);
    }

    private ListNode reverseEveryKthHelper(ListNode head, int k, int interval, int groupNum) {
        if (head == null)
            return null;

        ListNode curr = head;
        int count = 0;

        while (curr != null && count < k) {
            curr = curr.next;
            count++;
        }

        if (count < k)
            return head;

        if (groupNum % interval == 0) {
            // Reverse this group
            ListNode prev = null;
            curr = head;
            for (int i = 0; i < k; i++) {
                ListNode next = curr.next;
                curr.next = prev;
                prev = curr;
                curr = next;
            }
            head.next = reverseEveryKthHelper(curr, k, interval, groupNum + 1);
            return prev;
        } else {
            // Skip this group
            ListNode kthNode = head;
            for (int i = 1; i < k; i++) {
                kthNode = kthNode.next;
            }
            kthNode.next = reverseEveryKthHelper(curr, k, interval, groupNum + 1);
            return head;
        }
    }

    // Helper methods
    private void reverseSegment(ListNode start, ListNode end) {
        ListNode prev = null;
        ListNode curr = start;
        ListNode next = end.next;

        while (curr != next) {
            ListNode temp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = temp;
        }
    }

    private ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        return prev;
    }

    private int getLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
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
        ReverseLinkedListInGroupsOfK solution = new ReverseLinkedListInGroupsOfK();

        // Test case 1: Basic k-group reversal
        ListNode head1 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        System.out.print("Original: ");
        solution.printList(head1);
        ListNode result1 = solution.reverseKGroup(head1, 2);
        System.out.print("Reverse in groups of 2: ");
        solution.printList(result1);

        // Test case 2: Stack-based approach
        ListNode head2 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6 });
        ListNode result2 = solution.reverseKGroupStack(head2, 3);
        System.out.print("Stack-based (k=3): ");
        solution.printList(result2);

        // Test case 3: Two-pass approach
        ListNode head3 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        ListNode result3 = solution.reverseKGroupTwoPass(head3, 3);
        System.out.print("Two-pass (k=3): ");
        solution.printList(result3);

        // Test case 4: Follow-up - Alternate groups
        ListNode head4 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        ListNode result4 = solution.reverseAlternateKGroups(head4, 2);
        System.out.print("Alternate groups (k=2): ");
        solution.printList(result4);

        // Test case 5: Follow-up - With partial reversal
        ListNode head5 = solution.buildList(new int[] { 1, 2, 3, 4, 5 });
        ListNode result5 = solution.reverseKGroupWithPartial(head5, 3, true);
        System.out.print("With partial reversal (k=3): ");
        solution.printList(result5);

        // Test case 6: Follow-up - Every kth group
        ListNode head6 = solution.buildList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        ListNode result6 = solution.reverseEveryKthGroup(head6, 3, 2);
        System.out.print("Every 2nd group of 3: ");
        solution.printList(result6);

        // Test case 7: Edge cases
        ListNode single = solution.buildList(new int[] { 1 });
        ListNode singleResult = solution.reverseKGroup(single, 1);
        System.out.print("Single node: ");
        solution.printList(singleResult);

        ListNode empty = solution.reverseKGroup(null, 2);
        System.out.print("Empty list: ");
        solution.printList(empty);
    }
}
