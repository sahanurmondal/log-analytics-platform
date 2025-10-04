package sorting.easy;

import java.util.*;

/**
 * LeetCode 88: Merge Sorted Array
 * https://leetcode.com/problems/merge-sorted-array/
 * 
 * Companies: Facebook, Amazon, Microsoft, Google, Apple, Bloomberg, Adobe
 * Frequency: Very High (Asked in 1000+ interviews)
 *
 * Description:
 * You are given two integer arrays nums1 and nums2, sorted in non-decreasing
 * order,
 * and two integers m and n, representing the number of elements in nums1 and
 * nums2 respectively.
 * Merge nums1 and nums2 into a single array sorted in non-decreasing order.
 * 
 * The final sorted array should not be returned by the function, but instead be
 * stored inside the array nums1. To accommodate this, nums1 has a length of m +
 * n,
 * where the first m elements denote the elements that should be merged, and the
 * last n elements are set to 0 and should be ignored. nums2 has a length of n.
 * 
 * Constraints:
 * - nums1.length == m + n
 * - nums2.length == n
 * - 0 <= m, n <= 200
 * - 1 <= m + n <= 200
 * - -10^9 <= nums1[i], nums2[j] <= 10^9
 * 
 * Follow-up Questions:
 * 1. How would you handle merging multiple sorted arrays?
 * 2. Can you implement an in-place merge for equal-sized arrays?
 * 3. What about merging in descending order?
 * 4. How to merge with custom comparators?
 * 5. Can you handle duplicate elements specially?
 * 6. What about merging linked lists instead of arrays?
 */
public class MergeSortedArray {

    // Approach 1: Two pointers from end (optimal) - O(m + n) time, O(1) space
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1; // Last element in nums1
        int j = n - 1; // Last element in nums2
        int k = m + n - 1; // Last position in merged array

        // Merge from the end
        while (i >= 0 && j >= 0) {
            if (nums1[i] > nums2[j]) {
                nums1[k] = nums1[i];
                i--;
            } else {
                nums1[k] = nums2[j];
                j--;
            }
            k--;
        }

        // Copy remaining elements from nums2
        while (j >= 0) {
            nums1[k] = nums2[j];
            j--;
            k--;
        }

        // Note: No need to copy remaining elements from nums1
        // as they are already in correct position
    }

    // Approach 2: Two pointers from start with extra space - O(m + n) time, O(m +
    // n) space
    public static void mergeWithExtraSpace(int[] nums1, int m, int[] nums2, int n) {
        int[] temp = new int[m + n];
        int i = 0, j = 0, k = 0;

        // Merge both arrays into temp array
        while (i < m && j < n) {
            if (nums1[i] <= nums2[j]) {
                temp[k] = nums1[i];
                i++;
            } else {
                temp[k] = nums2[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements
        while (i < m) {
            temp[k] = nums1[i];
            i++;
            k++;
        }

        while (j < n) {
            temp[k] = nums2[j];
            j++;
            k++;
        }

        // Copy back to nums1
        System.arraycopy(temp, 0, nums1, 0, m + n);
    }

    // Approach 3: Insert and sort (naive) - O((m + n) log(m + n)) time, O(1) space
    public static void mergeAndSort(int[] nums1, int m, int[] nums2, int n) {
        // Copy nums2 elements to nums1
        for (int i = 0; i < n; i++) {
            nums1[m + i] = nums2[i];
        }

        // Sort the entire array
        Arrays.sort(nums1);
    }

    // Follow-up 1: Merge multiple sorted arrays
    public static class MergeMultipleSortedArrays {

        public static int[] mergeKSortedArrays(int[][] arrays) {
            if (arrays == null || arrays.length == 0) {
                return new int[0];
            }

            return mergeKSortedArraysHelper(arrays, 0, arrays.length - 1);
        }

        private static int[] mergeKSortedArraysHelper(int[][] arrays, int start, int end) {
            if (start == end) {
                return arrays[start].clone();
            }

            if (start + 1 == end) {
                return mergeTwoSortedArrays(arrays[start], arrays[end]);
            }

            int mid = start + (end - start) / 2;
            int[] left = mergeKSortedArraysHelper(arrays, start, mid);
            int[] right = mergeKSortedArraysHelper(arrays, mid + 1, end);

            return mergeTwoSortedArrays(left, right);
        }

        private static int[] mergeTwoSortedArrays(int[] arr1, int[] arr2) {
            int[] result = new int[arr1.length + arr2.length];
            int i = 0, j = 0, k = 0;

            while (i < arr1.length && j < arr2.length) {
                if (arr1[i] <= arr2[j]) {
                    result[k] = arr1[i];
                    i++;
                } else {
                    result[k] = arr2[j];
                    j++;
                }
                k++;
            }

            while (i < arr1.length) {
                result[k] = arr1[i];
                i++;
                k++;
            }

            while (j < arr2.length) {
                result[k] = arr2[j];
                j++;
                k++;
            }

            return result;
        }

        // Using Min Heap approach
        public static int[] mergeKSortedArraysHeap(int[][] arrays) {
            PriorityQueue<int[]> minHeap = new PriorityQueue<>(
                    (a, b) -> Integer.compare(arrays[a[0]][a[1]], arrays[b[0]][b[1]]));

            int totalSize = 0;

            // Initialize heap with first element of each array
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i].length > 0) {
                    minHeap.offer(new int[] { i, 0 }); // {array_index, element_index}
                    totalSize += arrays[i].length;
                }
            }

            int[] result = new int[totalSize];
            int resultIndex = 0;

            while (!minHeap.isEmpty()) {
                int[] current = minHeap.poll();
                int arrayIndex = current[0];
                int elementIndex = current[1];

                result[resultIndex++] = arrays[arrayIndex][elementIndex];

                // Add next element from same array if exists
                if (elementIndex + 1 < arrays[arrayIndex].length) {
                    minHeap.offer(new int[] { arrayIndex, elementIndex + 1 });
                }
            }

            return result;
        }
    }

    // Follow-up 2: In-place merge for equal-sized arrays
    public static class InPlaceMergeEqualSize {

        public static void mergeInPlace(int[] arr1, int[] arr2) {
            int n = arr1.length;

            // Start from last element of arr1 and first element of arr2
            int i = n - 1;
            int j = 0;

            // Swap elements if arr1[i] > arr2[j]
            while (i >= 0 && j < n && arr1[i] > arr2[j]) {
                int temp = arr1[i];
                arr1[i] = arr2[j];
                arr2[j] = temp;
                i--;
                j++;
            }

            // Sort both arrays
            Arrays.sort(arr1);
            Arrays.sort(arr2);
        }

        // Gap method (more efficient)
        public static void mergeInPlaceGap(int[] arr1, int[] arr2) {
            int n = arr1.length;
            int gap = (2 * n + 1) / 2; // Ceiling of (2*n)/2

            while (gap > 0) {
                int i = 0;

                // Compare elements in first array
                while (i + gap < n) {
                    if (arr1[i] > arr1[i + gap]) {
                        swap(arr1, i, i + gap);
                    }
                    i++;
                }

                // Compare elements between arrays
                int j = (gap > n) ? gap - n : 0;
                while (i < n && j < n) {
                    if (arr1[i] > arr2[j]) {
                        int temp = arr1[i];
                        arr1[i] = arr2[j];
                        arr2[j] = temp;
                    }
                    i++;
                    j++;
                }

                // Compare elements in second array
                if (j < n) {
                    while (j + gap < n) {
                        if (arr2[j] > arr2[j + gap]) {
                            swap(arr2, j, j + gap);
                        }
                        j++;
                    }
                }

                gap = (gap == 1) ? 0 : (gap + 1) / 2;
            }
        }

        private static void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Follow-up 3: Merge in descending order
    public static class MergeDescending {

        public static void mergeDescending(int[] nums1, int m, int[] nums2, int n) {
            int i = 0; // Start of nums1
            int j = 0; // Start of nums2
            int k = 0; // Start of merged position

            int[] temp = new int[m + n];

            // Merge in descending order
            while (i < m && j < n) {
                if (nums1[i] >= nums2[j]) {
                    temp[k] = nums1[i];
                    i++;
                } else {
                    temp[k] = nums2[j];
                    j++;
                }
                k++;
            }

            // Copy remaining elements
            while (i < m) {
                temp[k] = nums1[i];
                i++;
                k++;
            }

            while (j < n) {
                temp[k] = nums2[j];
                j++;
                k++;
            }

            // Copy back to nums1
            System.arraycopy(temp, 0, nums1, 0, m + n);
        }

        // In-place descending merge
        public static void mergeDescendingInPlace(int[] nums1, int m, int[] nums2, int n) {
            int i = 0; // Start of nums1
            int j = 0; // Start of nums2
            int k = 0; // Current position in merged array

            while (i < m && j < n) {
                if (nums1[i] >= nums2[j]) {
                    // nums1[i] should be at position k
                    if (i != k) {
                        // Shift elements to make space
                        int temp = nums1[i];
                        System.arraycopy(nums1, k, nums1, k + 1, i - k);
                        nums1[k] = temp;
                    }
                    i++;
                } else {
                    // nums2[j] should be at position k
                    // Shift elements in nums1 to make space
                    System.arraycopy(nums1, k, nums1, k + 1, m + n - k - 1);
                    nums1[k] = nums2[j];
                    j++;
                    m++; // Increase effective size of nums1
                }
                k++;
            }

            // Copy remaining elements from nums2
            while (j < n) {
                nums1[k] = nums2[j];
                j++;
                k++;
            }
        }
    }

    // Follow-up 4: Merge with custom comparators
    public static class CustomComparator {

        public static void mergeWithComparator(int[] nums1, int m, int[] nums2, int n,
                Comparator<Integer> comparator) {
            int i = m - 1;
            int j = n - 1;
            int k = m + n - 1;

            while (i >= 0 && j >= 0) {
                if (comparator.compare(nums1[i], nums2[j]) > 0) {
                    nums1[k] = nums1[i];
                    i--;
                } else {
                    nums1[k] = nums2[j];
                    j--;
                }
                k--;
            }

            while (j >= 0) {
                nums1[k] = nums2[j];
                j--;
                k--;
            }
        }

        // Merge by absolute value
        public static void mergeByAbsoluteValue(int[] nums1, int m, int[] nums2, int n) {
            mergeWithComparator(nums1, m, nums2, n,
                    (a, b) -> Integer.compare(Math.abs(a), Math.abs(b)));
        }

        // Merge even numbers first, then odd numbers
        public static void mergeEvenFirst(int[] nums1, int m, int[] nums2, int n) {
            mergeWithComparator(nums1, m, nums2, n, (a, b) -> {
                boolean aEven = a % 2 == 0;
                boolean bEven = b % 2 == 0;

                if (aEven && !bEven)
                    return -1;
                if (!aEven && bEven)
                    return 1;

                return Integer.compare(a, b);
            });
        }
    }

    // Follow-up 5: Handle duplicates specially
    public static class HandleDuplicates {

        public static void mergeRemoveDuplicates(int[] nums1, int m, int[] nums2, int n) {
            Set<Integer> seen = new HashSet<>();
            List<Integer> result = new ArrayList<>();

            int i = 0, j = 0;

            while (i < m && j < n) {
                int val;
                if (nums1[i] <= nums2[j]) {
                    val = nums1[i];
                    i++;
                } else {
                    val = nums2[j];
                    j++;
                }

                if (!seen.contains(val)) {
                    seen.add(val);
                    result.add(val);
                }
            }

            while (i < m) {
                if (!seen.contains(nums1[i])) {
                    seen.add(nums1[i]);
                    result.add(nums1[i]);
                }
                i++;
            }

            while (j < n) {
                if (!seen.contains(nums2[j])) {
                    seen.add(nums2[j]);
                    result.add(nums2[j]);
                }
                j++;
            }

            // Copy result back to nums1
            for (int k = 0; k < result.size(); k++) {
                nums1[k] = result.get(k);
            }

            // Fill remaining positions with a marker (e.g., Integer.MAX_VALUE)
            for (int k = result.size(); k < m + n; k++) {
                nums1[k] = Integer.MAX_VALUE;
            }
        }

        public static int mergeCountDuplicates(int[] nums1, int m, int[] nums2, int n) {
            Map<Integer, Integer> count = new HashMap<>();

            // Count elements in both arrays
            for (int i = 0; i < m; i++) {
                count.put(nums1[i], count.getOrDefault(nums1[i], 0) + 1);
            }

            for (int j = 0; j < n; j++) {
                count.put(nums2[j], count.getOrDefault(nums2[j], 0) + 1);
            }

            // Merge normally
            merge(nums1, m, nums2, n);

            // Count duplicates
            int duplicates = 0;
            for (int freq : count.values()) {
                if (freq > 1) {
                    duplicates += freq - 1;
                }
            }

            return duplicates;
        }
    }

    // Follow-up 6: Merge linked lists (related concept)
    public static class MergeLinkedLists {

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
                while (current != null) {
                    sb.append(current.val);
                    if (current.next != null)
                        sb.append(" -> ");
                    current = current.next;
                }
                return sb.toString();
            }
        }

        public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
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

        // Merge k sorted linked lists
        public static ListNode mergeKLists(ListNode[] lists) {
            if (lists == null || lists.length == 0)
                return null;

            PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));

            // Add head of each list to heap
            for (ListNode list : lists) {
                if (list != null) {
                    minHeap.offer(list);
                }
            }

            ListNode dummy = new ListNode(0);
            ListNode current = dummy;

            while (!minHeap.isEmpty()) {
                ListNode node = minHeap.poll();
                current.next = node;
                current = current.next;

                if (node.next != null) {
                    minHeap.offer(node.next);
                }
            }

            return dummy.next;
        }

        // Helper method to create linked list from array
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
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(int[] nums1, int m, int[] nums2, int n, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Array sizes: nums1=" + nums1.length + " (m=" + m + "), nums2=" + n +
                    ", Iterations: " + iterations);

            // Optimal approach (two pointers from end)
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                int[] copy1 = Arrays.copyOf(nums1, nums1.length);
                int[] copy2 = Arrays.copyOf(nums2, nums2.length);
                merge(copy1, m, copy2, n);
            }
            long optimalTime = System.nanoTime() - start;

            // Extra space approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                int[] copy1 = Arrays.copyOf(nums1, nums1.length);
                int[] copy2 = Arrays.copyOf(nums2, nums2.length);
                mergeWithExtraSpace(copy1, m, copy2, n);
            }
            long extraSpaceTime = System.nanoTime() - start;

            // Sort approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                int[] copy1 = Arrays.copyOf(nums1, nums1.length);
                int[] copy2 = Arrays.copyOf(nums2, nums2.length);
                mergeAndSort(copy1, m, copy2, n);
            }
            long sortTime = System.nanoTime() - start;

            System.out.println("Optimal (two pointers): " + optimalTime / 1_000_000 + " ms");
            System.out.println("Extra space: " + extraSpaceTime / 1_000_000 + " ms");
            System.out.println("Sort approach: " + sortTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[] nums1 = { 1, 2, 3, 0, 0, 0 };
        int[] nums2 = { 2, 5, 6 };
        int m = 3, n = 3;

        System.out.println("Before merge:");
        System.out.println("nums1: " + Arrays.toString(nums1) + " (m=" + m + ")");
        System.out.println("nums2: " + Arrays.toString(nums2) + " (n=" + n + ")");

        merge(nums1, m, nums2, n);
        System.out.println("After merge: " + Arrays.toString(nums1));

        // Test Case 2: Different merge approaches
        System.out.println("\n=== Test Case 2: Different Approaches ===");

        int[] test1 = { 1, 2, 3, 0, 0, 0 };
        int[] test2 = { 1, 2, 3, 0, 0, 0 };
        int[] test3 = { 1, 2, 3, 0, 0, 0 };
        int[] nums2Copy = { 2, 5, 6 };

        merge(test1, 3, nums2Copy.clone(), 3);
        System.out.println("Optimal approach: " + Arrays.toString(test1));

        mergeWithExtraSpace(test2, 3, nums2Copy.clone(), 3);
        System.out.println("Extra space approach: " + Arrays.toString(test2));

        mergeAndSort(test3, 3, nums2Copy.clone(), 3);
        System.out.println("Sort approach: " + Arrays.toString(test3));

        // Test Case 3: Edge cases
        System.out.println("\n=== Test Case 3: Edge Cases ===");

        // Empty nums2
        int[] edge1 = { 1, 2, 3 };
        int[] empty = {};
        merge(edge1, 3, empty, 0);
        System.out.println("Empty nums2: " + Arrays.toString(edge1));

        // Empty nums1
        int[] edge2 = { 0, 0, 0 };
        int[] fill = { 1, 2, 3 };
        merge(edge2, 0, fill, 3);
        System.out.println("Empty nums1: " + Arrays.toString(edge2));

        // Single elements
        int[] single1 = { 2, 0 };
        int[] single2 = { 1 };
        merge(single1, 1, single2, 1);
        System.out.println("Single elements: " + Arrays.toString(single1));

        // Test Case 4: Merge multiple sorted arrays
        System.out.println("\n=== Test Case 4: Merge Multiple Arrays ===");

        int[][] multipleArrays = {
                { 1, 4, 7 },
                { 2, 5, 8 },
                { 3, 6, 9 },
                { 0, 10, 11 }
        };

        System.out.println("Multiple arrays:");
        for (int[] arr : multipleArrays) {
            System.out.println("  " + Arrays.toString(arr));
        }

        int[] mergedMultiple = MergeMultipleSortedArrays.mergeKSortedArrays(multipleArrays);
        System.out.println("Merged (divide & conquer): " + Arrays.toString(mergedMultiple));

        int[] mergedHeap = MergeMultipleSortedArrays.mergeKSortedArraysHeap(multipleArrays);
        System.out.println("Merged (heap): " + Arrays.toString(mergedHeap));

        // Test Case 5: In-place merge equal size
        System.out.println("\n=== Test Case 5: In-place Merge Equal Size ===");

        int[] arr1 = { 1, 3, 5, 7 };
        int[] arr2 = { 2, 4, 6, 8 };

        System.out.println("Before in-place merge:");
        System.out.println("arr1: " + Arrays.toString(arr1));
        System.out.println("arr2: " + Arrays.toString(arr2));

        InPlaceMergeEqualSize.mergeInPlace(arr1, arr2);
        System.out.println("After in-place merge:");
        System.out.println("arr1: " + Arrays.toString(arr1));
        System.out.println("arr2: " + Arrays.toString(arr2));

        // Test Case 6: Descending order merge
        System.out.println("\n=== Test Case 6: Descending Order ===");

        int[] desc1 = { 6, 5, 3, 0, 0, 0 };
        int[] desc2 = { 4, 2, 1 };

        System.out.println("Before descending merge:");
        System.out.println("nums1: " + Arrays.toString(desc1));
        System.out.println("nums2: " + Arrays.toString(desc2));

        MergeDescending.mergeDescending(desc1, 3, desc2, 3);
        System.out.println("After descending merge: " + Arrays.toString(desc1));

        // Test Case 7: Custom comparator
        System.out.println("\n=== Test Case 7: Custom Comparators ===");

        int[] custom1 = { -3, -1, 2, 0, 0, 0 };
        int[] custom2 = { -2, 1, 3 };

        System.out.println("Original arrays:");
        System.out.println("nums1: " + Arrays.toString(Arrays.copyOf(custom1, custom1.length)));
        System.out.println("nums2: " + Arrays.toString(custom2));

        CustomComparator.mergeByAbsoluteValue(custom1.clone(), 3, custom2.clone(), 3);
        System.out.println("Merged by absolute value: " + Arrays.toString(custom1));

        int[] evenFirst1 = { 1, 3, 4, 0, 0, 0 };
        int[] evenFirst2 = { 2, 5, 6 };
        CustomComparator.mergeEvenFirst(evenFirst1, 3, evenFirst2, 3);
        System.out.println("Even numbers first: " + Arrays.toString(evenFirst1));

        // Test Case 8: Handle duplicates
        System.out.println("\n=== Test Case 8: Handle Duplicates ===");

        int[] dup1 = { 1, 2, 3, 0, 0, 0, 0 };
        int[] dup2 = { 2, 3, 4, 5 };

        System.out.println("Before handling duplicates:");
        System.out.println("nums1: " + Arrays.toString(Arrays.copyOf(dup1, dup1.length)));
        System.out.println("nums2: " + Arrays.toString(dup2));

        int duplicateCount = HandleDuplicates.mergeCountDuplicates(dup1.clone(), 3, dup2.clone(), 4);
        System.out.println("Duplicate count: " + duplicateCount);

        HandleDuplicates.mergeRemoveDuplicates(dup1, 3, dup2, 4);
        System.out.println("After removing duplicates: " + Arrays.toString(dup1));

        // Test Case 9: Merge linked lists
        System.out.println("\n=== Test Case 9: Merge Linked Lists ===");

        MergeLinkedLists.ListNode list1 = MergeLinkedLists.createList(new int[] { 1, 2, 4 });
        MergeLinkedLists.ListNode list2 = MergeLinkedLists.createList(new int[] { 1, 3, 4 });

        System.out.println("List 1: " + list1);
        System.out.println("List 2: " + list2);

        MergeLinkedLists.ListNode merged = MergeLinkedLists.mergeTwoLists(list1, list2);
        System.out.println("Merged: " + merged);

        // Merge k lists
        MergeLinkedLists.ListNode[] kLists = {
                MergeLinkedLists.createList(new int[] { 1, 4, 5 }),
                MergeLinkedLists.createList(new int[] { 1, 3, 4 }),
                MergeLinkedLists.createList(new int[] { 2, 6 })
        };

        MergeLinkedLists.ListNode mergedK = MergeLinkedLists.mergeKLists(kLists);
        System.out.println("Merged k lists: " + mergedK);

        // Test Case 10: Large arrays performance
        System.out.println("\n=== Test Case 10: Large Arrays Performance ===");

        int size = 1000;
        int[] large1 = new int[size * 2];
        int[] large2 = new int[size];

        // Fill with sorted data
        for (int i = 0; i < size; i++) {
            large1[i] = i * 2;
            large2[i] = i * 2 + 1;
        }

        long start = System.currentTimeMillis();
        merge(large1, size, large2, size);
        long end = System.currentTimeMillis();

        System.out.println("Large array merge (size=" + size + " each): " + (end - start) + " ms");
        System.out.println("First 10 elements: " + Arrays.toString(Arrays.copyOf(large1, 10)));
        System.out.println("Last 10 elements: " + Arrays.toString(
                Arrays.copyOfRange(large1, large1.length - 10, large1.length)));

        // Test Case 11: Stress test
        System.out.println("\n=== Test Case 11: Stress Test ===");

        Random random = new Random(42);
        int testCases = 100;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int testM = random.nextInt(10) + 1;
            int testN = random.nextInt(10) + 1;

            int[] testNums1 = new int[testM + testN];
            int[] testNums2 = new int[testN];

            // Fill with sorted random numbers
            for (int i = 0; i < testM; i++) {
                testNums1[i] = random.nextInt(100);
            }
            Arrays.sort(testNums1, 0, testM);

            for (int i = 0; i < testN; i++) {
                testNums2[i] = random.nextInt(100);
            }
            Arrays.sort(testNums2);

            // Test all approaches
            int[] copy1 = Arrays.copyOf(testNums1, testNums1.length);
            int[] copy2 = Arrays.copyOf(testNums1, testNums1.length);
            int[] copy3 = Arrays.copyOf(testNums1, testNums1.length);

            merge(copy1, testM, testNums2.clone(), testN);
            mergeWithExtraSpace(copy2, testM, testNums2.clone(), testN);
            mergeAndSort(copy3, testM, testNums2.clone(), testN);

            if (Arrays.equals(copy1, copy2) && Arrays.equals(copy2, copy3)) {
                passed++;
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Performance comparison
        int[] perfNums1 = { 1, 3, 5, 7, 9, 0, 0, 0, 0, 0 };
        int[] perfNums2 = { 2, 4, 6, 8, 10 };
        PerformanceComparison.compareApproaches(perfNums1, 5, perfNums2, 5, 10000);

        System.out.println("\nMerge Sorted Array testing completed successfully!");
    }
}
