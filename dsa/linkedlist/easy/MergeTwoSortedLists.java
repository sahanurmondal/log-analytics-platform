package linkedlist.easy;

import java.util.*;

/**
 * LeetCode 21: Merge Two Sorted Lists
 * https://leetcode.com/problems/merge-two-sorted-lists/
 * 
 * Companies: Google, Apple, Amazon, Microsoft, Meta, LinkedIn
 * Frequency: Very High (Asked in 600+ interviews)
 *
 * Description:
 * You are given the heads of two sorted linked lists list1 and list2.
 * Merge the two lists in a one sorted list. The list should be made by
 * splicing together the nodes of the first two lists.
 * Return the head of the merged linked list.
 *
 * Constraints:
 * - The number of nodes in both lists is in the range [0, 50].
 * - -100 <= Node.val <= 100
 * - Both list1 and list2 are sorted in non-decreasing order.
 * 
 * Follow-up Questions:
 * 1. Can you merge k sorted linked lists?
 * 2. What if the lists are sorted in descending order?
 * 3. Can you merge without creating new nodes?
 * 4. How would you handle different data types?
 */
public class MergeTwoSortedLists {

    // Definition for singly-linked list
    static class ListNode {
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

    // Approach 1: Iterative with Dummy Head - O(m+n) time, O(1) space
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        // Attach remaining nodes
        current.next = (list1 != null) ? list1 : list2;

        return dummy.next;
    }

    // Approach 2: Recursive - O(m+n) time, O(m+n) space (call stack)
    public ListNode mergeTwoListsRecursive(ListNode list1, ListNode list2) {
        if (list1 == null)
            return list2;
        if (list2 == null)
            return list1;

        if (list1.val <= list2.val) {
            list1.next = mergeTwoListsRecursive(list1.next, list2);
            return list1;
        } else {
            list2.next = mergeTwoListsRecursive(list1, list2.next);
            return list2;
        }
    }

    // Approach 3: In-place without dummy - O(m+n) time, O(1) space
    public ListNode mergeTwoListsInPlace(ListNode list1, ListNode list2) {
        if (list1 == null)
            return list2;
        if (list2 == null)
            return list1;

        // Ensure list1 starts with smaller value
        if (list1.val > list2.val) {
            ListNode temp = list1;
            list1 = list2;
            list2 = temp;
        }

        ListNode head = list1;

        while (list1.next != null && list2 != null) {
            if (list1.next.val <= list2.val) {
                list1 = list1.next;
            } else {
                ListNode temp = list1.next;
                list1.next = list2;
                list2 = temp;
                list1 = list1.next;
            }
        }

        if (list2 != null) {
            list1.next = list2;
        }

        return head;
    }

    // Follow-up 1: Merge k sorted lists (LeetCode 23)
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0)
            return null;

        while (lists.length > 1) {
            List<ListNode> mergedLists = new ArrayList<>();

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

    // Alternative: Merge k lists using priority queue
    public ListNode mergeKListsPriorityQueue(ListNode[] lists) {
        if (lists == null || lists.length == 0)
            return null;

        PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);

        // Add all non-null heads to priority queue
        for (ListNode list : lists) {
            if (list != null) {
                pq.offer(list);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (!pq.isEmpty()) {
            ListNode node = pq.poll();
            current.next = node;
            current = current.next;

            if (node.next != null) {
                pq.offer(node.next);
            }
        }

        return dummy.next;
    }

    // Follow-up 2: Merge descending sorted lists
    public ListNode mergeTwoDescendingLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val >= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        current.next = (list1 != null) ? list1 : list2;
        return dummy.next;
    }

    // Follow-up 3: Merge with creating new nodes
    public ListNode mergeTwoListsNewNodes(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = new ListNode(list1.val);
                list1 = list1.next;
            } else {
                current.next = new ListNode(list2.val);
                list2 = list2.next;
            }
            current = current.next;
        }

        // Copy remaining nodes
        while (list1 != null) {
            current.next = new ListNode(list1.val);
            list1 = list1.next;
            current = current.next;
        }

        while (list2 != null) {
            current.next = new ListNode(list2.val);
            list2 = list2.next;
            current = current.next;
        }

        return dummy.next;
    }

    // Follow-up 4: Generic merge with custom comparator
    static class GenericListNode<T> {
        T val;
        GenericListNode<T> next;

        GenericListNode(T val) {
            this.val = val;
        }
    }

    public <T> GenericListNode<T> mergeGeneric(GenericListNode<T> list1,
            GenericListNode<T> list2,
            Comparator<T> comparator) {
        GenericListNode<T> dummy = new GenericListNode<>(null);
        GenericListNode<T> current = dummy;

        while (list1 != null && list2 != null) {
            if (comparator.compare(list1.val, list2.val) <= 0) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        current.next = (list1 != null) ? list1 : list2;
        return dummy.next;
    }

    // Helper: Create linked list from array
    public static ListNode createList(int[] values) {
        if (values == null || values.length == 0)
            return null;

        ListNode head = new ListNode(values[0]);
        ListNode current = head;

        for (int i = 1; i < values.length; i++) {
            current.next = new ListNode(values[i]);
            current = current.next;
        }

        return head;
    }

    // Helper: Convert linked list to array
    public static List<Integer> listToArray(ListNode head) {
        List<Integer> result = new ArrayList<>();
        ListNode current = head;

        while (current != null) {
            result.add(current.val);
            current = current.next;
        }

        return result;
    }

    // Helper: Check if list is sorted
    public static boolean isSorted(ListNode head) {
        if (head == null || head.next == null)
            return true;

        ListNode current = head;
        while (current.next != null) {
            if (current.val > current.next.val) {
                return false;
            }
            current = current.next;
        }
        return true;
    }

    // Helper: Get length of list
    public static int getLength(ListNode head) {
        int length = 0;
        ListNode current = head;

        while (current != null) {
            length++;
            current = current.next;
        }

        return length;
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(int[] arr1, int[] arr2) {
        Map<String, Long> results = new HashMap<>();

        // Test iterative approach
        ListNode list1 = createList(arr1);
        ListNode list2 = createList(arr2);
        long start = System.nanoTime();
        mergeTwoLists(list1, list2);
        results.put("Iterative", System.nanoTime() - start);

        // Test recursive approach
        list1 = createList(arr1);
        list2 = createList(arr2);
        start = System.nanoTime();
        mergeTwoListsRecursive(list1, list2);
        results.put("Recursive", System.nanoTime() - start);

        // Test in-place approach
        list1 = createList(arr1);
        list2 = createList(arr2);
        start = System.nanoTime();
        mergeTwoListsInPlace(list1, list2);
        results.put("InPlace", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        MergeTwoSortedLists solution = new MergeTwoSortedLists();

        // Test Case 1: Normal merge
        System.out.println("=== Test Case 1: Normal Merge ===");
        ListNode list1 = createList(new int[] { 1, 2, 4 });
        ListNode list2 = createList(new int[] { 1, 3, 4 });

        System.out.println("List 1: " + listToArray(list1));
        System.out.println("List 2: " + listToArray(list2));

        ListNode merged = solution.mergeTwoLists(list1, list2);
        System.out.println("Merged: " + listToArray(merged));
        System.out.println("Is sorted: " + isSorted(merged));

        // Test Case 2: Empty lists
        System.out.println("\n=== Test Case 2: Empty Lists ===");
        ListNode empty1 = null;
        ListNode empty2 = createList(new int[] { 0 });

        ListNode mergedEmpty = solution.mergeTwoLists(empty1, empty2);
        System.out.println("Merge empty + [0]: " + listToArray(mergedEmpty));

        // Test Case 3: Different lengths
        System.out.println("\n=== Test Case 3: Different Lengths ===");
        ListNode short1 = createList(new int[] { 5 });
        ListNode long2 = createList(new int[] { 1, 2, 3, 4, 6, 7 });

        System.out.println("Short: " + listToArray(short1));
        System.out.println("Long: " + listToArray(long2));

        ListNode mergedDiff = solution.mergeTwoLists(short1, long2);
        System.out.println("Merged: " + listToArray(mergedDiff));

        // Test Case 4: Compare approaches
        System.out.println("\n=== Test Case 4: Compare Approaches ===");
        int[] arr1 = { 2, 5, 8, 12 };
        int[] arr2 = { 1, 3, 6, 9, 11 };

        ListNode iter = solution.mergeTwoLists(createList(arr1), createList(arr2));
        ListNode recur = solution.mergeTwoListsRecursive(createList(arr1), createList(arr2));
        ListNode inPlace = solution.mergeTwoListsInPlace(createList(arr1), createList(arr2));

        System.out.println("Iterative: " + listToArray(iter));
        System.out.println("Recursive: " + listToArray(recur));
        System.out.println("In-place: " + listToArray(inPlace));

        List<Integer> iterResult = listToArray(iter);
        List<Integer> recurResult = listToArray(recur);
        List<Integer> inPlaceResult = listToArray(inPlace);

        System.out.println("All approaches consistent: " +
                (iterResult.equals(recurResult) && recurResult.equals(inPlaceResult)));

        // Follow-up 1: Merge k lists
        System.out.println("\n=== Follow-up 1: Merge K Lists ===");
        ListNode[] kLists = {
                createList(new int[] { 1, 4, 5 }),
                createList(new int[] { 1, 3, 4 }),
                createList(new int[] { 2, 6 })
        };

        for (int i = 0; i < kLists.length; i++) {
            System.out.println("List " + (i + 1) + ": " + listToArray(kLists[i]));
        }

        ListNode mergedK = solution.mergeKLists(kLists);
        System.out.println("Merged K lists: " + listToArray(mergedK));

        // Compare with priority queue approach
        ListNode[] kLists2 = {
                createList(new int[] { 1, 4, 5 }),
                createList(new int[] { 1, 3, 4 }),
                createList(new int[] { 2, 6 })
        };

        ListNode mergedKPQ = solution.mergeKListsPriorityQueue(kLists2);
        System.out.println("Merged K (PQ): " + listToArray(mergedKPQ));
        System.out.println("K-merge methods consistent: " +
                listToArray(mergedK).equals(listToArray(mergedKPQ)));

        // Follow-up 2: Descending lists
        System.out.println("\n=== Follow-up 2: Descending Lists ===");
        ListNode desc1 = createList(new int[] { 5, 3, 1 });
        ListNode desc2 = createList(new int[] { 4, 2 });

        System.out.println("Descending 1: " + listToArray(desc1));
        System.out.println("Descending 2: " + listToArray(desc2));

        ListNode mergedDesc = solution.mergeTwoDescendingLists(desc1, desc2);
        System.out.println("Merged descending: " + listToArray(mergedDesc));

        // Follow-up 3: New nodes
        System.out.println("\n=== Follow-up 3: Creating New Nodes ===");
        ListNode orig1 = createList(new int[] { 1, 3, 5 });
        ListNode orig2 = createList(new int[] { 2, 4, 6 });

        System.out.println("Original 1: " + listToArray(orig1));
        System.out.println("Original 2: " + listToArray(orig2));

        ListNode newMerged = solution.mergeTwoListsNewNodes(orig1, orig2);
        System.out.println("New merged: " + listToArray(newMerged));
        System.out.println("Original 1 unchanged: " + listToArray(orig1));
        System.out.println("Original 2 unchanged: " + listToArray(orig2));

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        int[] perfArr1 = new int[500];
        int[] perfArr2 = new int[500];

        for (int i = 0; i < 500; i++) {
            perfArr1[i] = i * 2;
            perfArr2[i] = i * 2 + 1;
        }

        Map<String, Long> performance = solution.comparePerformance(perfArr1, perfArr2);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1_000_000.0 + " ms"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Both empty
        ListNode bothEmpty = solution.mergeTwoLists(null, null);
        System.out.println("Both empty: " + listToArray(bothEmpty));

        // Same values
        ListNode same1 = createList(new int[] { 1, 1, 1 });
        ListNode same2 = createList(new int[] { 1, 1, 1 });
        ListNode sameResult = solution.mergeTwoLists(same1, same2);
        System.out.println("Same values: " + listToArray(sameResult));

        // Negative values
        ListNode neg1 = createList(new int[] { -3, -1, 2 });
        ListNode neg2 = createList(new int[] { -2, 0, 4 });
        ListNode negResult = solution.mergeTwoLists(neg1, neg2);
        System.out.println("Negative values: " + listToArray(negResult));

        System.out.println("\nTotal merged length: " + getLength(negResult));
    }
}
