package heap.hard;

import java.util.PriorityQueue;

/**
 * LeetCode 23: Merge k Sorted Lists
 * https://leetcode.com/problems/merge-k-sorted-lists/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Very High (Asked in 25+ interviews)
 *
 * Description:
 * You are given an array of `k` linked-lists `lists`, each linked-list is
 * sorted in ascending order.
 * Merge all the linked-lists into one sorted linked-list and return it.
 *
 * Constraints:
 * - k == lists.length
 * - 0 <= k <= 10^4
 * - 0 <= lists[i].length <= 500
 * - -10^4 <= lists[i][j] <= 10^4
 * - `lists[i]` is sorted in ascending order.
 * - The sum of `lists[i].length` will not exceed 10^4.
 * 
 * Follow-up Questions:
 * 1. Can you analyze the time and space complexity of the heap and
 * divide-and-conquer approaches?
 * 2. What if the lists are extremely long and cannot fit into memory?
 * 3. How would you handle merging if the lists were doubly-linked?
 */
public class MergeKSortedLists {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    // Approach 1: Min-Heap - O(N log k) time, O(k) space. N is total nodes, k is
    // number of lists.
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> a.val - b.val);

        // Add the head of each list to the heap
        for (ListNode node : lists) {
            if (node != null) {
                minHeap.offer(node);
            }
        }

        ListNode dummyHead = new ListNode(-1);
        ListNode current = dummyHead;

        // Repeatedly extract the minimum node from the heap and add its next node
        while (!minHeap.isEmpty()) {
            ListNode minNode = minHeap.poll();
            current.next = minNode;
            current = current.next;

            if (minNode.next != null) {
                minHeap.offer(minNode.next);
            }
        }

        return dummyHead.next;
    }

    // Approach 2: Divide and Conquer - O(N log k) time, O(log k) space for
    // recursion stack.
    public ListNode mergeKListsDivideAndConquer(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        return merge(lists, 0, lists.length - 1);
    }

    private ListNode merge(ListNode[] lists, int left, int right) {
        if (left == right) {
            return lists[left];
        }
        if (left > right) {
            return null;
        }
        int mid = left + (right - left) / 2;
        ListNode l1 = merge(lists, left, mid);
        ListNode l2 = merge(lists, mid + 1, right);
        return mergeTwoLists(l1, l2);
    }

    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(-1);
        ListNode current = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }
        current.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }

    public static void main(String[] args) {
        MergeKSortedLists solution = new MergeKSortedLists();

        // Test case 1
        ListNode l1 = new ListNode(1, new ListNode(4, new ListNode(5)));
        ListNode l2 = new ListNode(1, new ListNode(3, new ListNode(4)));
        ListNode l3 = new ListNode(2, new ListNode(6));
        ListNode[] lists1 = { l1, l2, l3 };

        ListNode result1 = solution.mergeKLists(lists1);
        printList(result1); // 1 1 2 3 4 4 5 6

        // Test case 2: Empty lists
        ListNode[] lists2 = {};
        ListNode result2 = solution.mergeKLists(lists2);
        printList(result2); // (empty)

        // Test case 3: Lists with nulls
        ListNode[] lists3 = { null, new ListNode(1) };
        ListNode result3 = solution.mergeKLists(lists3);
        printList(result3); // 1
    }

    public static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
        System.out.println();
    }
}
