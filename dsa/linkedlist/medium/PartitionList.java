package linkedlist.medium;

import linkedlist.ListNode;

/**
 * LeetCode 86: Partition List
 * https://leetcode.com/problems/partition-list/
 *
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Medium
 *
 * Description:
 * Partition a linked list around a value x.
 *
 * Constraints:
 * - 0 <= n <= 200
 *
 * Follow-ups:
 * 1. Can you do it in-place?
 * 2. Can you keep the original relative order?
 * 3. Can you partition by multiple values?
 */
public class PartitionList {
    public ListNode partition(ListNode head, int x) {
        ListNode beforeDummy = new ListNode(0), afterDummy = new ListNode(0);
        ListNode before = beforeDummy, after = afterDummy;
        while (head != null) {
            if (head.val < x) {
                before.next = head;
                before = before.next;
            } else {
                after.next = head;
                after = after.next;
            }
            head = head.next;
        }
        before.next = afterDummy.next;
        after.next = null;
        return beforeDummy.next;
    }

    // Follow-up 1: In-place (already handled above)
    // Follow-up 2: Keep original order (already handled above)
    // Follow-up 3: Partition by multiple values
    public ListNode partitionMultiple(ListNode head, int[] xs) {
        java.util.List<ListNode> dummies = new java.util.ArrayList<>();
        for (int i = 0; i <= xs.length; i++)
            dummies.add(new ListNode(0));
        ListNode[] tails = dummies.toArray(new ListNode[0]);
        while (head != null) {
            int idx = 0;
            while (idx < xs.length && head.val >= xs[idx])
                idx++;
            tails[idx].next = head;
            tails[idx] = tails[idx].next;
            head = head.next;
        }
        for (int i = 0; i < xs.length; i++)
            tails[i].next = dummies.get(i + 1).next;
        tails[xs.length].next = null;
        return dummies.get(0).next;
    }

    public static void main(String[] args) {
        PartitionList solution = new PartitionList();
        ListNode head = new ListNode(1,
                new ListNode(4, new ListNode(3, new ListNode(2, new ListNode(5, new ListNode(2))))));
        System.out.println(solution.partition(head, 3)); // 1->2->2->4->3->5
        // Edge Case: All less than x
        System.out.println(solution.partition(new ListNode(1, new ListNode(2)), 3)); // 1->2
        // Edge Case: All greater than or equal to x
        System.out.println(solution.partition(new ListNode(4, new ListNode(5)), 3)); // 4->5
        // Edge Case: Single node
        System.out.println(solution.partition(new ListNode(1), 1)); // 1
        // Edge Case: Empty list
        System.out.println(solution.partition(null, 3)); // null
    }
}
