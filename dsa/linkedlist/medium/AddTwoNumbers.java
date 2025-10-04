package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 2: Add Two Numbers
 * https://leetcode.com/problems/add-two-numbers/
 *
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: Very High
 *
 * Description:
 * Add two numbers represented by linked lists (digits in reverse order).
 *
 * Constraints:
 * - The number of nodes in each list is in the range [1, 100].
 * - 0 <= Node.val <= 9
 *
 * Follow-ups:
 * 1. What if the digits are stored in forward order?
 * 2. Can you handle lists of different lengths?
 * 3. Can you solve without modifying the input lists?
 */
public class AddTwoNumbers {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
            carry = sum / 10;
        }
        return dummy.next;
    }

    // Follow-up 1: Digits stored in forward order (LeetCode 445)
    public ListNode addTwoNumbersForward(ListNode l1, ListNode l2) {
        java.util.Stack<Integer> s1 = new java.util.Stack<>();
        java.util.Stack<Integer> s2 = new java.util.Stack<>();
        while (l1 != null) {
            s1.push(l1.val);
            l1 = l1.next;
        }
        while (l2 != null) {
            s2.push(l2.val);
            l2 = l2.next;
        }
        int carry = 0;
        ListNode head = null;
        while (!s1.isEmpty() || !s2.isEmpty() || carry != 0) {
            int sum = carry;
            if (!s1.isEmpty())
                sum += s1.pop();
            if (!s2.isEmpty())
                sum += s2.pop();
            ListNode node = new ListNode(sum % 10);
            node.next = head;
            head = node;
            carry = sum / 10;
        }
        return head;
    }

    // Follow-up 2: Handle lists of different lengths (already handled above)
    // Follow-up 3: Without modifying input lists (already handled above)

    public static void main(String[] args) {
        AddTwoNumbers solution = new AddTwoNumbers();
        ListNode l1 = new ListNode(2, new ListNode(4, new ListNode(3)));
        ListNode l2 = new ListNode(5, new ListNode(6, new ListNode(4)));
        System.out.println(solution.addTwoNumbers(l1, l2)); // 7->0->8

        ListNode l3 = new ListNode(9, new ListNode(9, new ListNode(9)));
        ListNode l4 = new ListNode(1);
        System.out.println(solution.addTwoNumbers(l3, l4)); // 0->0->0->1

        // Forward order
        ListNode f1 = new ListNode(7, new ListNode(2, new ListNode(4, new ListNode(3))));
        ListNode f2 = new ListNode(5, new ListNode(6, new ListNode(4)));
        System.out.println(solution.addTwoNumbersForward(f1, f2)); // 7->8->0->7
    }
}
