package binarysearch.hard;

import java.util.Comparator;

/**
 * LeetCode 23: Merge k Sorted Lists (Binary Search + Divide & Conquer approach)
 * https://leetcode.com/problems/merge-k-sorted-lists/
 *
 * Description:
 * You are given an array of k linked-lists lists, each linked-list is sorted in
 * ascending order.
 * Merge all the linked-lists into one sorted linked-list and return it.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Bloomberg,
 * Adobe, Uber, Airbnb
 * Difficulty: Hard
 * Asked: 2023-2024 (Very High Frequency)
 *
 * Constraints:
 * - k == lists.length
 * - 0 <= k <= 10^4
 * - 0 <= lists[i].length <= 500
 * - -10^4 <= lists[i][j] <= 10^4
 * - lists[i] is sorted in ascending order
 * - The sum of lists[i].length will not exceed 10^4
 *
 * Follow-ups:
 * - Can you solve this with constant extra space?
 * - What if lists are very large and don't fit in memory?
 * - How would you merge k sorted streams?
 */
public class MergeKSortedLists {

    // Definition for singly-linked list
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

    // Divide and Conquer (Binary Search-like) - O(N log k) time, O(log k) space
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        return mergeKListsHelper(lists, 0, lists.length - 1);
    }

    private ListNode mergeKListsHelper(ListNode[] lists, int left, int right) {
        if (left == right) {
            return lists[left];
        }

        if (left > right) {
            return null;
        }

        int mid = left + (right - left) / 2;
        ListNode leftList = mergeKListsHelper(lists, left, mid);
        ListNode rightList = mergeKListsHelper(lists, mid + 1, right);

        return mergeTwoLists(leftList, rightList);
    }

    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }

        // Attach remaining nodes
        current.next = (l1 != null) ? l1 : l2;

        return dummy.next;
    }

    // Priority Queue approach - O(N log k) time, O(k) space
    public ListNode mergeKListsPriorityQueue(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        java.util.PriorityQueue<ListNode> pq = new java.util.PriorityQueue<>(
                Comparator.comparingInt(a -> a.val));

        // Add all heads to priority queue
        for (ListNode list : lists) {
            if (list != null) {
                pq.offer(list);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (!pq.isEmpty()) {
            ListNode smallest = pq.poll();
            current.next = smallest;
            current = current.next;

            if (smallest.next != null) {
                pq.offer(smallest.next);
            }
        }

        return dummy.next;
    }

    // Iterative merge - O(N log k) time, O(1) space
    public ListNode mergeKListsIterative(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        while (lists.length > 1) {
            java.util.List<ListNode> mergedLists = new java.util.ArrayList<>();

            // Merge pairs of lists
            for (int i = 0; i < lists.length; i += 2) {
                ListNode l1 = lists[i];
                ListNode l2 = (i + 1 < lists.length) ? lists[i + 1] : null;
                mergedLists.add(mergeTwoLists(l1, l2));
            }

            lists = mergedLists.toArray(new ListNode[0]);
        }

        return lists[0];
    }

    // Brute force approach - O(N log N) time where N is total nodes
    public ListNode mergeKListsBruteForce(ListNode[] lists) {
        java.util.List<Integer> values = new java.util.ArrayList<>();

        // Collect all values
        for (ListNode list : lists) {
            while (list != null) {
                values.add(list.val);
                list = list.next;
            }
        }

        // Sort values
        values.sort(Integer::compareTo);

        // Build result list
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        for (int val : values) {
            current.next = new ListNode(val);
            current = current.next;
        }

        return dummy.next;
    }

    // Sequential merge approach - O(N*k) time, O(1) space
    public ListNode mergeKListsSequential(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        ListNode result = null;

        for (ListNode list : lists) {
            result = mergeTwoLists(result, list);
        }

        return result;
    }

    // Memory-efficient approach for large datasets
    public ListNode mergeKListsMemoryEfficient(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        // Use a min-heap but limit its size
        java.util.PriorityQueue<ListNode> pq = new java.util.PriorityQueue<>(
                Math.min(lists.length, 1000), // Limit heap size
                (a, b) -> Integer.compare(a.val, b.val));

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        // Initialize with first node from each non-empty list
        for (ListNode list : lists) {
            if (list != null) {
                pq.offer(list);
            }
        }

        while (!pq.isEmpty()) {
            ListNode smallest = pq.poll();
            current.next = new ListNode(smallest.val); // Create new node to save memory
            current = current.next;

            if (smallest.next != null) {
                pq.offer(smallest.next);
            }
        }

        return dummy.next;
    }

    // Helper methods for testing
    private ListNode createList(int[] values) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        for (int val : values) {
            current.next = new ListNode(val);
            current = current.next;
        }

        return dummy.next;
    }

    private void printList(ListNode head) {
        java.util.List<Integer> values = new java.util.ArrayList<>();
        while (head != null) {
            values.add(head.val);
            head = head.next;
        }
        System.out.println(values);
    }

    private java.util.List<Integer> listToArray(ListNode head) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        while (head != null) {
            result.add(head.val);
            head = head.next;
        }
        return result;
    }

    public static void main(String[] args) {
        MergeKSortedLists solution = new MergeKSortedLists();

        // Test Case 1: [[1,4,5],[1,3,4],[2,6]]
        ListNode[] lists1 = new ListNode[3];
        lists1[0] = solution.createList(new int[] { 1, 4, 5 });
        lists1[1] = solution.createList(new int[] { 1, 3, 4 });
        lists1[2] = solution.createList(new int[] { 2, 6 });

        ListNode result1 = solution.mergeKLists(lists1);
        System.out.print("Test Case 1: ");
        solution.printList(result1); // Expected: [1,1,2,3,4,4,5,6]

        // Test Case 2: Empty lists
        ListNode[] lists2 = new ListNode[0];
        ListNode result2 = solution.mergeKLists(lists2);
        System.out.print("Test Case 2 (Empty): ");
        solution.printList(result2); // Expected: []

        // Test Case 3: Single empty list
        ListNode[] lists3 = new ListNode[1];
        lists3[0] = null;
        ListNode result3 = solution.mergeKLists(lists3);
        System.out.print("Test Case 3 (Single null): ");
        solution.printList(result3); // Expected: []

        // Test Case 4: Single list
        ListNode[] lists4 = new ListNode[1];
        lists4[0] = solution.createList(new int[] { 1, 2, 3 });
        ListNode result4 = solution.mergeKLists(lists4);
        System.out.print("Test Case 4 (Single list): ");
        solution.printList(result4); // Expected: [1,2,3]

        // Test Case 5: Mixed empty and non-empty lists
        ListNode[] lists5 = new ListNode[3];
        lists5[0] = solution.createList(new int[] { 1, 3 });
        lists5[1] = null;
        lists5[2] = solution.createList(new int[] { 2, 4 });
        ListNode result5 = solution.mergeKLists(lists5);
        System.out.print("Test Case 5 (Mixed): ");
        solution.printList(result5); // Expected: [1,2,3,4]

        // Test Priority Queue approach
        ListNode[] lists6 = new ListNode[3];
        lists6[0] = solution.createList(new int[] { 1, 4, 5 });
        lists6[1] = solution.createList(new int[] { 1, 3, 4 });
        lists6[2] = solution.createList(new int[] { 2, 6 });
        ListNode resultPQ = solution.mergeKListsPriorityQueue(lists6);
        System.out.print("Priority Queue: ");
        solution.printList(resultPQ); // Expected: [1,1,2,3,4,4,5,6]

        // Test Iterative approach
        ListNode[] lists7 = new ListNode[3];
        lists7[0] = solution.createList(new int[] { 1, 4, 5 });
        lists7[1] = solution.createList(new int[] { 1, 3, 4 });
        lists7[2] = solution.createList(new int[] { 2, 6 });
        ListNode resultIter = solution.mergeKListsIterative(lists7);
        System.out.print("Iterative: ");
        solution.printList(resultIter); // Expected: [1,1,2,3,4,4,5,6]

        // Test Brute Force approach
        ListNode[] lists8 = new ListNode[3];
        lists8[0] = solution.createList(new int[] { 1, 4, 5 });
        lists8[1] = solution.createList(new int[] { 1, 3, 4 });
        lists8[2] = solution.createList(new int[] { 2, 6 });
        ListNode resultBF = solution.mergeKListsBruteForce(lists8);
        System.out.print("Brute Force: ");
        solution.printList(resultBF); // Expected: [1,1,2,3,4,4,5,6]

        // Large test case
        ListNode[] largeLists = new ListNode[100];
        for (int i = 0; i < 100; i++) {
            largeLists[i] = solution.createList(new int[] { i, i + 100, i + 200 });
        }
        ListNode largeResult = solution.mergeKLists(largeLists);
        System.out.println("Large test completed - first 10 elements:");
        ListNode temp = largeResult;
        for (int i = 0; i < 10 && temp != null; i++) {
            System.out.print(temp.val + " ");
            temp = temp.next;
        }
        System.out.println();

        // Test with negative numbers
        ListNode[] negativeLists = new ListNode[2];
        negativeLists[0] = solution.createList(new int[] { -2, -1, 0 });
        negativeLists[1] = solution.createList(new int[] { -3, 1, 2 });
        ListNode negativeResult = solution.mergeKLists(negativeLists);
        System.out.print("Negative numbers: ");
        solution.printList(negativeResult); // Expected: [-3,-2,-1,0,1,2]
    }
}
