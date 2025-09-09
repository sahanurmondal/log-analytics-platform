package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 160: Intersection of Two Linked Lists
 * https://leetcode.com/problems/intersection-of-two-linked-lists/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: High
 *
 * Description:
 * Find the intersection node of two singly linked lists.
 *
 * Constraints:
 * - 0 <= n <= 10^5
 *
 * Follow-ups:
 * 1. Can you do it in O(1) space?
 * 2. Can you handle cycles?
 * 3. Can you return the length of the intersection?
 */
public class IntersectionOfTwoLinkedLists {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) {
            a = (a == null) ? headB : a.next;
            b = (b == null) ? headA : b.next;
        }
        return a;
    }

    // Follow-up 1: O(1) space (already handled above)
    // Follow-up 2: Handle cycles
    public ListNode getIntersectionWithCycle(ListNode headA, ListNode headB) {
        // ...existing code for cycle detection and intersection...
        return null;
    }

    // Follow-up 3: Return length of intersection
    public int getIntersectionLength(ListNode headA, ListNode headB) {
        ListNode inter = getIntersectionNode(headA, headB);
        int len = 0;
        while (inter != null) {
            len++;
            inter = inter.next;
        }
        return len;
    }

    public static void main(String[] args) {
        IntersectionOfTwoLinkedLists solution = new IntersectionOfTwoLinkedLists();
        ListNode common = new ListNode(8, new ListNode(4, new ListNode(5)));
        ListNode headA = new ListNode(4, new ListNode(1, common));
        ListNode headB = new ListNode(5, new ListNode(6, new ListNode(1, common)));
        System.out.println(solution.getIntersectionNode(headA, headB)); // 8
        // Edge Case: No intersection
        System.out.println(solution.getIntersectionNode(new ListNode(1), new ListNode(2))); // null
        // Edge Case: One list empty
        System.out.println(solution.getIntersectionNode(null, headB)); // null
        // Edge Case: Both lists empty
        System.out.println(solution.getIntersectionNode(null, null)); // null
    }
}
