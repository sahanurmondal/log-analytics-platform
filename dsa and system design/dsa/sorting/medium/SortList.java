package sorting.medium;

import java.util.*;

/**
 * LeetCode 148: Sort List
 * https://leetcode.com/problems/sort-list/
 * 
 * Companies: Facebook, Amazon, Microsoft, Google, Apple, Bloomberg, Adobe, Uber
 * Frequency: Very High (Asked in 1200+ interviews)
 *
 * Description:
 * Given the head of a linked list, return the list after sorting it in
 * ascending order.
 * Can you sort the linked list in O(n log n) time and O(1) memory (i.e.
 * constant space)?
 * 
 * Constraints:
 * - The number of nodes in the list is in the range [0, 5 * 10^4]
 * - -10^5 <= Node.val <= 10^5
 * 
 * Follow-up Questions:
 * 1. How would you sort in descending order?
 * 2. Can you implement different sorting algorithms for linked lists?
 * 3. What about sorting with custom comparators?
 * 4. How to handle duplicate values specially?
 * 5. Can you sort multiple linked lists efficiently?
 * 6. What about sorting doubly linked lists?
 */
public class SortList {

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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            ListNode current = this;
            int count = 0;
            while (current != null && count < 20) { // Prevent infinite loops
                sb.append(current.val);
                if (current.next != null)
                    sb.append(" -> ");
                current = current.next;
                count++;
            }
            if (current != null)
                sb.append(" -> ...");
            return sb.toString();
        }
    }

    // Approach 1: Merge Sort (Top Down) - O(n log n) time, O(log n) space
    // (recursion stack)
    public static ListNode sortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        // Find middle and split the list
        ListNode mid = getMiddle(head);
        ListNode rightHead = mid.next;
        mid.next = null;

        // Recursively sort both halves
        ListNode left = sortList(head);
        ListNode right = sortList(rightHead);

        // Merge sorted halves
        return merge(left, right);
    }

    private static ListNode getMiddle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        ListNode prev = null;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        return prev; // Returns node before middle for proper splitting
    }

    private static ListNode merge(ListNode l1, ListNode l2) {
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

    // Approach 2: Merge Sort (Bottom Up) - O(n log n) time, O(1) space
    public static ListNode sortListBottomUp(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        // Get length of list
        int length = getLength(head);

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        // Merge sublists of size 1, 2, 4, 8, ...
        for (int size = 1; size < length; size *= 2) {
            ListNode prev = dummy;
            ListNode current = dummy.next;

            while (current != null) {
                // Get first sublist of size 'size'
                ListNode left = current;
                ListNode right = split(left, size);
                current = split(right, size);

                // Merge and connect
                prev.next = merge(left, right);

                // Move prev to end of merged list
                while (prev.next != null) {
                    prev = prev.next;
                }
            }
        }

        return dummy.next;
    }

    private static int getLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
    }

    private static ListNode split(ListNode head, int size) {
        for (int i = 1; i < size && head != null; i++) {
            head = head.next;
        }

        if (head == null)
            return null;

        ListNode next = head.next;
        head.next = null;
        return next;
    }

    // Approach 3: Quick Sort for Linked List - O(n log n) average, O(n^2) worst
    // case
    public static ListNode sortListQuickSort(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        return quickSort(head, null);
    }

    private static ListNode quickSort(ListNode head, ListNode end) {
        if (head == null || head == end) {
            return head;
        }

        // Partition around last element as pivot
        PartitionResult result = partition(head, end);

        if (result.newHead != result.pivot) {
            // Recursively sort before pivot
            ListNode temp = result.newHead;
            while (temp.next != result.pivot) {
                temp = temp.next;
            }
            temp.next = null;

            result.newHead = quickSort(result.newHead, temp);

            // Reconnect
            temp = getTail(result.newHead);
            temp.next = result.pivot;
        }

        // Recursively sort after pivot
        result.pivot.next = quickSort(result.pivot.next, end);

        return result.newHead;
    }

    private static class PartitionResult {
        ListNode newHead;
        ListNode pivot;

        PartitionResult(ListNode newHead, ListNode pivot) {
            this.newHead = newHead;
            this.pivot = pivot;
        }
    }

    private static PartitionResult partition(ListNode head, ListNode end) {
        ListNode pivot = head;
        ListNode prev = null;
        ListNode current = head;
        ListNode tail = pivot;

        while (current != end) {
            if (current.val < pivot.val) {
                if (prev != null) {
                    prev.next = current.next;
                }

                ListNode next = current.next;
                current.next = head;
                head = current;
                current = next;
            } else {
                prev = current;
                current = current.next;
                tail = prev;
            }
        }

        if (head != pivot) {
            tail.next = pivot.next;
            pivot.next = null;
        }

        return new PartitionResult(head, pivot);
    }

    private static ListNode getTail(ListNode head) {
        while (head != null && head.next != null) {
            head = head.next;
        }
        return head;
    }

    // Approach 4: Convert to Array, Sort, Convert Back - O(n log n) time, O(n)
    // space
    public static ListNode sortListArray(ListNode head) {
        if (head == null)
            return null;

        // Convert to array
        List<Integer> values = new ArrayList<>();
        ListNode current = head;

        while (current != null) {
            values.add(current.val);
            current = current.next;
        }

        // Sort array
        Collections.sort(values);

        // Convert back to linked list
        ListNode dummy = new ListNode(0);
        current = dummy;

        for (int val : values) {
            current.next = new ListNode(val);
            current = current.next;
        }

        return dummy.next;
    }

    // Follow-up 1: Sort in descending order
    public static class SortDescending {

        public static ListNode sortListDesc(ListNode head) {
            if (head == null || head.next == null) {
                return head;
            }

            ListNode mid = getMiddle(head);
            ListNode rightHead = mid.next;
            mid.next = null;

            ListNode left = sortListDesc(head);
            ListNode right = sortListDesc(rightHead);

            return mergeDesc(left, right);
        }

        private static ListNode mergeDesc(ListNode l1, ListNode l2) {
            ListNode dummy = new ListNode(0);
            ListNode current = dummy;

            while (l1 != null && l2 != null) {
                if (l1.val >= l2.val) { // Changed to >= for descending
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

        private static ListNode getMiddle(ListNode head) {
            ListNode slow = head;
            ListNode fast = head;
            ListNode prev = null;

            while (fast != null && fast.next != null) {
                prev = slow;
                slow = slow.next;
                fast = fast.next.next;
            }

            return prev;
        }
    }

    // Follow-up 2: Different sorting algorithms
    public static class DifferentSortingAlgorithms {

        // Insertion Sort - O(n^2) time, O(1) space
        public static ListNode insertionSort(ListNode head) {
            if (head == null || head.next == null) {
                return head;
            }

            ListNode dummy = new ListNode(0);
            ListNode current = head;

            while (current != null) {
                ListNode next = current.next;

                // Find position to insert current node
                ListNode prev = dummy;
                while (prev.next != null && prev.next.val < current.val) {
                    prev = prev.next;
                }

                // Insert current node
                current.next = prev.next;
                prev.next = current;

                current = next;
            }

            return dummy.next;
        }

        // Selection Sort - O(n^2) time, O(1) space
        public static ListNode selectionSort(ListNode head) {
            if (head == null || head.next == null) {
                return head;
            }

            ListNode sorted = null;
            ListNode current = head;

            while (current != null) {
                // Find minimum node
                ListNode minPrev = null;
                ListNode min = current;
                ListNode temp = current;
                ListNode tempPrev = null;

                while (temp != null) {
                    if (temp.val < min.val) {
                        minPrev = tempPrev;
                        min = temp;
                    }
                    tempPrev = temp;
                    temp = temp.next;
                }

                // Remove min from unsorted list
                if (minPrev != null) {
                    minPrev.next = min.next;
                } else {
                    current = min.next;
                }

                // Add min to sorted list
                min.next = sorted;
                sorted = min;
            }

            // Reverse to get ascending order
            return reverse(sorted);
        }

        // Bubble Sort - O(n^2) time, O(1) space
        public static ListNode bubbleSort(ListNode head) {
            if (head == null || head.next == null) {
                return head;
            }

            boolean swapped;

            do {
                swapped = false;
                ListNode current = head;

                while (current != null && current.next != null) {
                    if (current.val > current.next.val) {
                        // Swap values
                        int temp = current.val;
                        current.val = current.next.val;
                        current.next.val = temp;
                        swapped = true;
                    }
                    current = current.next;
                }
            } while (swapped);

            return head;
        }

        private static ListNode reverse(ListNode head) {
            ListNode prev = null;
            ListNode current = head;

            while (current != null) {
                ListNode next = current.next;
                current.next = prev;
                prev = current;
                current = next;
            }

            return prev;
        }
    }

    // Follow-up 3: Custom comparators
    public static class CustomComparator {

        @FunctionalInterface
        public interface ListNodeComparator {
            int compare(ListNode a, ListNode b);
        }

        public static ListNode sortWithComparator(ListNode head, ListNodeComparator comparator) {
            if (head == null || head.next == null) {
                return head;
            }

            ListNode mid = getMiddle(head);
            ListNode rightHead = mid.next;
            mid.next = null;

            ListNode left = sortWithComparator(head, comparator);
            ListNode right = sortWithComparator(rightHead, comparator);

            return mergeWithComparator(left, right, comparator);
        }

        private static ListNode mergeWithComparator(ListNode l1, ListNode l2, ListNodeComparator comparator) {
            ListNode dummy = new ListNode(0);
            ListNode current = dummy;

            while (l1 != null && l2 != null) {
                if (comparator.compare(l1, l2) <= 0) {
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

        private static ListNode getMiddle(ListNode head) {
            ListNode slow = head;
            ListNode fast = head;
            ListNode prev = null;

            while (fast != null && fast.next != null) {
                prev = slow;
                slow = slow.next;
                fast = fast.next.next;
            }

            return prev;
        }

        // Sort by absolute value
        public static ListNode sortByAbsoluteValue(ListNode head) {
            return sortWithComparator(head, (a, b) -> Integer.compare(Math.abs(a.val), Math.abs(b.val)));
        }

        // Sort even numbers first, then odd numbers
        public static ListNode sortEvenFirst(ListNode head) {
            return sortWithComparator(head, (a, b) -> {
                boolean aEven = a.val % 2 == 0;
                boolean bEven = b.val % 2 == 0;

                if (aEven && !bEven)
                    return -1;
                if (!aEven && bEven)
                    return 1;

                return Integer.compare(a.val, b.val);
            });
        }
    }

    // Follow-up 4: Handle duplicates specially
    public static class HandleDuplicates {

        // Remove duplicates while sorting
        public static ListNode sortAndRemoveDuplicates(ListNode head) {
            head = sortList(head);

            if (head == null)
                return null;

            ListNode current = head;

            while (current.next != null) {
                if (current.val == current.next.val) {
                    current.next = current.next.next;
                } else {
                    current = current.next;
                }
            }

            return head;
        }

        // Count duplicates while sorting
        public static class SortResult {
            ListNode head;
            Map<Integer, Integer> duplicateCount;

            SortResult(ListNode head, Map<Integer, Integer> duplicateCount) {
                this.head = head;
                this.duplicateCount = duplicateCount;
            }
        }

        public static SortResult sortAndCountDuplicates(ListNode head) {
            Map<Integer, Integer> count = new HashMap<>();

            // Count occurrences
            ListNode current = head;
            while (current != null) {
                count.put(current.val, count.getOrDefault(current.val, 0) + 1);
                current = current.next;
            }

            // Sort normally
            ListNode sorted = sortList(head);

            // Find duplicates
            Map<Integer, Integer> duplicates = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
                if (entry.getValue() > 1) {
                    duplicates.put(entry.getKey(), entry.getValue());
                }
            }

            return new SortResult(sorted, duplicates);
        }

        // Group duplicates together but keep them separate
        public static ListNode sortGroupDuplicates(ListNode head) {
            if (head == null)
                return null;

            // Count frequencies
            Map<Integer, Integer> freq = new HashMap<>();
            ListNode current = head;

            while (current != null) {
                freq.put(current.val, freq.getOrDefault(current.val, 0) + 1);
                current = current.next;
            }

            // Get sorted unique values
            List<Integer> uniqueValues = new ArrayList<>(freq.keySet());
            Collections.sort(uniqueValues);

            // Rebuild list with grouped duplicates
            ListNode dummy = new ListNode(0);
            current = dummy;

            for (int val : uniqueValues) {
                int count = freq.get(val);
                for (int i = 0; i < count; i++) {
                    current.next = new ListNode(val);
                    current = current.next;
                }
            }

            return dummy.next;
        }
    }

    // Follow-up 5: Sort multiple linked lists
    public static class SortMultipleLists {

        // Merge k sorted lists
        public static ListNode mergeKSortedLists(ListNode[] lists) {
            if (lists == null || lists.length == 0) {
                return null;
            }

            return mergeKListsHelper(lists, 0, lists.length - 1);
        }

        private static ListNode mergeKListsHelper(ListNode[] lists, int start, int end) {
            if (start == end) {
                return lists[start];
            }

            if (start + 1 == end) {
                return merge(lists[start], lists[end]);
            }

            int mid = start + (end - start) / 2;
            ListNode left = mergeKListsHelper(lists, start, mid);
            ListNode right = mergeKListsHelper(lists, mid + 1, end);

            return merge(left, right);
        }

        // Sort multiple unsorted lists
        public static ListNode[] sortMultipleLists(ListNode[] lists) {
            ListNode[] sortedLists = new ListNode[lists.length];

            for (int i = 0; i < lists.length; i++) {
                sortedLists[i] = sortList(lists[i]);
            }

            return sortedLists;
        }

        // Merge all lists into one sorted list
        public static ListNode mergeAllAndSort(ListNode[] lists) {
            List<Integer> allValues = new ArrayList<>();

            for (ListNode list : lists) {
                ListNode current = list;
                while (current != null) {
                    allValues.add(current.val);
                    current = current.next;
                }
            }

            Collections.sort(allValues);

            ListNode dummy = new ListNode(0);
            ListNode current = dummy;

            for (int val : allValues) {
                current.next = new ListNode(val);
                current = current.next;
            }

            return dummy.next;
        }
    }

    // Follow-up 6: Doubly linked list sorting
    public static class DoublyLinkedListSort {

        public static class DoublyListNode {
            int val;
            DoublyListNode next;
            DoublyListNode prev;

            DoublyListNode(int val) {
                this.val = val;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                DoublyListNode current = this;

                // Find head
                while (current.prev != null) {
                    current = current.prev;
                }

                int count = 0;
                while (current != null && count < 20) {
                    sb.append(current.val);
                    if (current.next != null)
                        sb.append(" <-> ");
                    current = current.next;
                    count++;
                }
                if (current != null)
                    sb.append(" <-> ...");

                return sb.toString();
            }
        }

        public static DoublyListNode sortDoublyList(DoublyListNode head) {
            if (head == null || head.next == null) {
                return head;
            }

            DoublyListNode mid = getMiddle(head);
            DoublyListNode rightHead = mid.next;

            // Split the list
            mid.next = null;
            if (rightHead != null) {
                rightHead.prev = null;
            }

            DoublyListNode left = sortDoublyList(head);
            DoublyListNode right = sortDoublyList(rightHead);

            return mergeDoubly(left, right);
        }

        private static DoublyListNode getMiddle(DoublyListNode head) {
            DoublyListNode slow = head;
            DoublyListNode fast = head;
            DoublyListNode prev = null;

            while (fast != null && fast.next != null) {
                prev = slow;
                slow = slow.next;
                fast = fast.next.next;
            }

            return prev;
        }

        private static DoublyListNode mergeDoubly(DoublyListNode l1, DoublyListNode l2) {
            DoublyListNode dummy = new DoublyListNode(0);
            DoublyListNode current = dummy;

            while (l1 != null && l2 != null) {
                if (l1.val <= l2.val) {
                    current.next = l1;
                    l1.prev = current;
                    l1 = l1.next;
                } else {
                    current.next = l2;
                    l2.prev = current;
                    l2 = l2.next;
                }
                current = current.next;
            }

            // Attach remaining nodes
            if (l1 != null) {
                current.next = l1;
                l1.prev = current;
            }
            if (l2 != null) {
                current.next = l2;
                l2.prev = current;
            }

            DoublyListNode result = dummy.next;
            if (result != null) {
                result.prev = null;
            }

            return result;
        }

        // Helper to create doubly linked list from array
        public static DoublyListNode createDoublyList(int[] arr) {
            if (arr.length == 0)
                return null;

            DoublyListNode head = new DoublyListNode(arr[0]);
            DoublyListNode current = head;

            for (int i = 1; i < arr.length; i++) {
                DoublyListNode newNode = new DoublyListNode(arr[i]);
                current.next = newNode;
                newNode.prev = current;
                current = newNode;
            }

            return head;
        }
    }

    // Utility methods
    public static ListNode createList(int[] arr) {
        if (arr.length == 0)
            return null;

        ListNode head = new ListNode(arr[0]);
        ListNode current = head;

        for (int i = 1; i < arr.length; i++) {
            current.next = new ListNode(arr[i]);
            current = current.next;
        }

        return head;
    }

    public static int[] listToArray(ListNode head) {
        List<Integer> result = new ArrayList<>();

        while (head != null) {
            result.add(head.val);
            head = head.next;
        }

        return result.stream().mapToInt(i -> i).toArray();
    }

    public static boolean isSorted(ListNode head) {
        while (head != null && head.next != null) {
            if (head.val > head.next.val) {
                return false;
            }
            head = head.next;
        }
        return true;
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(int[] arr, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Array size: " + arr.length + ", Iterations: " + iterations);

            // Merge Sort (Top Down)
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                ListNode list = createList(arr);
                sortList(list);
            }
            long mergeTopTime = System.nanoTime() - start;

            // Merge Sort (Bottom Up)
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                ListNode list = createList(arr);
                sortListBottomUp(list);
            }
            long mergeBottomTime = System.nanoTime() - start;

            // Array conversion approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                ListNode list = createList(arr);
                sortListArray(list);
            }
            long arrayTime = System.nanoTime() - start;

            // Quick Sort
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                ListNode list = createList(arr);
                sortListQuickSort(list);
            }
            long quickTime = System.nanoTime() - start;

            System.out.println("Merge Sort (Top Down): " + mergeTopTime / 1_000_000 + " ms");
            System.out.println("Merge Sort (Bottom Up): " + mergeBottomTime / 1_000_000 + " ms");
            System.out.println("Array Conversion: " + arrayTime / 1_000_000 + " ms");
            System.out.println("Quick Sort: " + quickTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[] arr1 = { 4, 2, 1, 3 };
        ListNode list1 = createList(arr1);

        System.out.println("Original list: " + list1);
        System.out.println("Merge Sort (Top Down): " + sortList(createList(arr1)));
        System.out.println("Merge Sort (Bottom Up): " + sortListBottomUp(createList(arr1)));
        System.out.println("Quick Sort: " + sortListQuickSort(createList(arr1)));
        System.out.println("Array Method: " + sortListArray(createList(arr1)));

        // Test Case 2: Edge cases
        System.out.println("\n=== Test Case 2: Edge Cases ===");

        // Empty list
        System.out.println("Empty list: " + sortList(null));

        // Single element
        ListNode single = new ListNode(1);
        System.out.println("Single element: " + sortList(single));

        // Two elements
        ListNode two = createList(new int[] { 2, 1 });
        System.out.println("Two elements [2,1]: " + sortList(two));

        // Already sorted
        ListNode sorted = createList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("Already sorted: " + sortList(sorted));

        // Reverse sorted
        ListNode reverse = createList(new int[] { 5, 4, 3, 2, 1 });
        System.out.println("Reverse sorted: " + sortList(reverse));

        // All same elements
        ListNode same = createList(new int[] { 3, 3, 3, 3 });
        System.out.println("All same: " + sortList(same));

        // Test Case 3: Different sorting algorithms
        System.out.println("\n=== Test Case 3: Different Sorting Algorithms ===");

        int[] testArr = { 64, 34, 25, 12, 22, 11, 90 };
        ListNode testList = createList(testArr);

        System.out.println("Original: " + testList);
        System.out.println("Insertion Sort: " + DifferentSortingAlgorithms.insertionSort(createList(testArr)));
        System.out.println("Selection Sort: " + DifferentSortingAlgorithms.selectionSort(createList(testArr)));
        System.out.println("Bubble Sort: " + DifferentSortingAlgorithms.bubbleSort(createList(testArr)));

        // Test Case 4: Sort in descending order
        System.out.println("\n=== Test Case 4: Descending Order ===");

        int[] desc = { 4, 2, 1, 3, 5 };
        System.out.println("Original: " + createList(desc));
        System.out.println("Descending: " + SortDescending.sortListDesc(createList(desc)));

        // Test Case 5: Custom comparators
        System.out.println("\n=== Test Case 5: Custom Comparators ===");

        int[] custom = { -3, -1, 2, -4, 5 };
        System.out.println("Original: " + createList(custom));
        System.out.println("By absolute value: " + CustomComparator.sortByAbsoluteValue(createList(custom)));

        int[] evenOdd = { 1, 2, 3, 4, 5, 6 };
        System.out.println("Original: " + createList(evenOdd));
        System.out.println("Even first: " + CustomComparator.sortEvenFirst(createList(evenOdd)));

        // Test Case 6: Handle duplicates
        System.out.println("\n=== Test Case 6: Handle Duplicates ===");

        int[] dups = { 3, 1, 4, 1, 5, 9, 2, 6, 5, 3 };
        ListNode dupList = createList(dups);

        System.out.println("Original with duplicates: " + dupList);
        System.out.println("Sorted with duplicates: " + sortList(createList(dups)));
        System.out.println("Sorted without duplicates: " + HandleDuplicates.sortAndRemoveDuplicates(createList(dups)));

        HandleDuplicates.SortResult result = HandleDuplicates.sortAndCountDuplicates(createList(dups));
        System.out.println("Sorted: " + result.head);
        System.out.println("Duplicate counts: " + result.duplicateCount);

        System.out.println("Grouped duplicates: " + HandleDuplicates.sortGroupDuplicates(createList(dups)));

        // Test Case 7: Sort multiple lists
        System.out.println("\n=== Test Case 7: Sort Multiple Lists ===");

        ListNode[] lists = {
                createList(new int[] { 1, 4, 5 }),
                createList(new int[] { 1, 3, 4 }),
                createList(new int[] { 2, 6 })
        };

        System.out.println("Multiple lists:");
        for (int i = 0; i < lists.length; i++) {
            System.out.println("  List " + (i + 1) + ": " + lists[i]);
        }

        System.out.println("Merged k sorted lists: " + SortMultipleLists.mergeKSortedLists(lists));

        ListNode[] unsortedLists = {
                createList(new int[] { 4, 1, 5 }),
                createList(new int[] { 3, 1, 4 }),
                createList(new int[] { 6, 2 })
        };

        System.out.println("Unsorted lists:");
        for (int i = 0; i < unsortedLists.length; i++) {
            System.out.println("  List " + (i + 1) + ": " + unsortedLists[i]);
        }

        System.out.println("Merge all and sort: " + SortMultipleLists.mergeAllAndSort(unsortedLists));

        // Test Case 8: Doubly linked list
        System.out.println("\n=== Test Case 8: Doubly Linked List ===");

        int[] doublyArr = { 4, 2, 1, 3, 5 };
        DoublyLinkedListSort.DoublyListNode doublyList = DoublyLinkedListSort.createDoublyList(doublyArr);

        System.out.println("Original doubly list: " + doublyList);
        DoublyLinkedListSort.DoublyListNode sortedDoubly = DoublyLinkedListSort.sortDoublyList(doublyList);
        System.out.println("Sorted doubly list: " + sortedDoubly);

        // Test Case 9: Large list performance
        System.out.println("\n=== Test Case 9: Large List Performance ===");

        Random random = new Random(42);
        int size = 1000;
        int[] largeArr = new int[size];

        for (int i = 0; i < size; i++) {
            largeArr[i] = random.nextInt(10000);
        }

        long start = System.currentTimeMillis();
        ListNode sortedLarge = sortList(createList(largeArr));
        long end = System.currentTimeMillis();

        System.out.println("Large list size: " + size);
        System.out.println("Sort time: " + (end - start) + " ms");
        System.out.println("Is sorted: " + isSorted(sortedLarge));

        // Show first and last 5 elements
        ListNode current = sortedLarge;
        System.out.print("First 5 elements: ");
        for (int i = 0; i < 5 && current != null; i++) {
            System.out.print(current.val + " ");
            current = current.next;
        }
        System.out.println();

        // Test Case 10: Stress test
        System.out.println("\n=== Test Case 10: Stress Test ===");

        int testCases = 100;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int n = random.nextInt(20) + 1;
            int[] testArray = new int[n];

            for (int i = 0; i < n; i++) {
                testArray[i] = random.nextInt(100);
            }

            // Test all approaches
            ListNode sorted1 = sortList(createList(testArray));
            ListNode sorted2 = sortListBottomUp(createList(testArray));
            ListNode sorted3 = sortListArray(createList(testArray));

            int[] sortedArr1 = listToArray(sorted1);
            int[] sortedArr2 = listToArray(sorted2);
            int[] sortedArr3 = listToArray(sorted3);

            if (Arrays.equals(sortedArr1, sortedArr2) && Arrays.equals(sortedArr2, sortedArr3) &&
                    isSorted(sorted1) && isSorted(sorted2) && isSorted(sorted3)) {
                passed++;
            } else {
                System.out.println("Failed test: " + Arrays.toString(testArray));
                break;
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Performance comparison
        int[] perfArray = new int[500];
        for (int i = 0; i < perfArray.length; i++) {
            perfArray[i] = random.nextInt(1000);
        }

        PerformanceComparison.compareApproaches(perfArray, 100);

        System.out.println("\nSort List testing completed successfully!");
    }
}
