package searching.hard;

import java.util.*;

/**
 * LeetCode 295: Find Median from Data Stream
 * URL: https://leetcode.com/problems/find-median-from-data-stream/
 * Difficulty: Hard
 * 
 * Companies: Amazon, Google, Microsoft, Facebook, Apple, Netflix
 * Frequency: Very High
 * 
 * Description:
 * Design a data structure that supports adding integers from a data stream
 * and finding the median of all elements added so far.
 * 
 * Constraints:
 * - -10^5 <= num <= 10^5
 * - At least one element exists before calling findMedian
 * - At most 5 * 10^4 calls will be made to addNum and findMedian
 * 
 * Follow-up Questions:
 * 1. Can you solve it using two heaps?
 * 2. How would you solve with a balanced BST?
 * 3. How would you handle range [0, 100] efficiently?
 * 4. How would you optimize for memory usage?
 * 5. How would you handle duplicate values?
 */
public class MedianOfRunningStream {
    private PriorityQueue<Integer> maxHeap; // First half (smaller elements)
    private PriorityQueue<Integer> minHeap; // Second half (larger elements)

    // Approach 1: Two Heaps Implementation
    public MedianOfRunningStream() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder()); // Max heap
        minHeap = new PriorityQueue<>(); // Min heap
    }

    /**
     * Add number to data structure
     * Time: O(log n), Space: O(1)
     */
    public void addNum(int num) {
        // Add to max heap first
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }

        // Balance the heaps
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size() + 1) {
            maxHeap.offer(minHeap.poll());
        }
    }

    /**
     * Find median of current numbers
     * Time: O(1), Space: O(1)
     */
    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        } else if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        } else {
            return minHeap.peek();
        }
    }

    public int size() {
        return maxHeap.size() + minHeap.size();
    }

    public boolean isEmpty() {
        return maxHeap.isEmpty() && minHeap.isEmpty();
    }

    // Approach 2: Balanced BST Implementation (TreeMap)
    static class MedianFinderBST {
        private TreeMap<Integer, Integer> map;
        private int totalCount;

        public MedianFinderBST() {
            map = new TreeMap<>();
            totalCount = 0;
        }

        public void addNum(int num) {
            map.put(num, map.getOrDefault(num, 0) + 1);
            totalCount++;
        }

        public double findMedian() {
            if (totalCount % 2 == 1) {
                // Odd number of elements
                return findKthElement((totalCount + 1) / 2);
            } else {
                // Even number of elements
                int mid1 = findKthElement(totalCount / 2);
                int mid2 = findKthElement(totalCount / 2 + 1);
                return (mid1 + mid2) / 2.0;
            }
        }

        private int findKthElement(int k) {
            int count = 0;
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                count += entry.getValue();
                if (count >= k) {
                    return entry.getKey();
                }
            }
            return -1; // Should never reach here
        }
    }

    // Approach 3: Optimized for range [0, 100]
    static class MedianFinderRange {
        private int[] counts;
        private int totalCount;

        public MedianFinderRange() {
            counts = new int[101]; // Range [0, 100]
            totalCount = 0;
        }

        public void addNum(int num) {
            if (num >= 0 && num <= 100) {
                counts[num]++;
                totalCount++;
            }
        }

        public double findMedian() {
            if (totalCount % 2 == 1) {
                return findKthElement((totalCount + 1) / 2);
            } else {
                int mid1 = findKthElement(totalCount / 2);
                int mid2 = findKthElement(totalCount / 2 + 1);
                return (mid1 + mid2) / 2.0;
            }
        }

        private int findKthElement(int k) {
            int count = 0;
            for (int i = 0; i <= 100; i++) {
                count += counts[i];
                if (count >= k) {
                    return i;
                }
            }
            return -1;
        }
    }

    // Approach 4: Insertion sort approach (for small datasets)
    static class MedianFinderList {
        private List<Integer> list;

        public MedianFinderList() {
            list = new ArrayList<>();
        }

        public void addNum(int num) {
            // Binary search for insertion position
            int pos = Collections.binarySearch(list, num);
            if (pos < 0) {
                pos = -pos - 1;
            }
            list.add(pos, num);
        }

        public double findMedian() {
            int n = list.size();
            if (n % 2 == 1) {
                return list.get(n / 2);
            } else {
                return (list.get(n / 2 - 1) + list.get(n / 2)) / 2.0;
            }
        }
    }

    // Follow-up: Median of sliding window
    static class SlidingWindowMedian {
        private PriorityQueue<Integer> maxHeap;
        private PriorityQueue<Integer> minHeap;
        private Map<Integer, Integer> toRemove;

        public SlidingWindowMedian() {
            maxHeap = new PriorityQueue<>(Collections.reverseOrder());
            minHeap = new PriorityQueue<>();
            toRemove = new HashMap<>();
        }

        public double[] medianSlidingWindow(int[] nums, int k) {
            double[] result = new double[nums.length - k + 1];

            // Initialize first window
            for (int i = 0; i < k; i++) {
                addNum(nums[i]);
            }
            result[0] = findMedian();

            // Process remaining windows
            for (int i = k; i < nums.length; i++) {
                removeNum(nums[i - k]);
                addNum(nums[i]);
                result[i - k + 1] = findMedian();
            }

            return result;
        }

        private void addNum(int num) {
            if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
                maxHeap.offer(num);
            } else {
                minHeap.offer(num);
            }
            balance();
        }

        private void removeNum(int num) {
            toRemove.put(num, toRemove.getOrDefault(num, 0) + 1);

            if (num <= maxHeap.peek()) {
                if (maxHeap.peek() == num) {
                    maxHeap.poll();
                    toRemove.put(num, toRemove.get(num) - 1);
                }
            } else {
                if (minHeap.peek() == num) {
                    minHeap.poll();
                    toRemove.put(num, toRemove.get(num) - 1);
                }
            }

            balance();
            cleanup();
        }

        private void balance() {
            while (maxHeap.size() > minHeap.size() + 1) {
                minHeap.offer(maxHeap.poll());
            }
            while (minHeap.size() > maxHeap.size() + 1) {
                maxHeap.offer(minHeap.poll());
            }
        }

        private void cleanup() {
            while (!maxHeap.isEmpty() && toRemove.getOrDefault(maxHeap.peek(), 0) > 0) {
                int num = maxHeap.poll();
                toRemove.put(num, toRemove.get(num) - 1);
            }
            while (!minHeap.isEmpty() && toRemove.getOrDefault(minHeap.peek(), 0) > 0) {
                int num = minHeap.poll();
                toRemove.put(num, toRemove.get(num) - 1);
            }
        }

        private double findMedian() {
            cleanup();
            if (maxHeap.size() == minHeap.size()) {
                return (maxHeap.peek() + minHeap.peek()) / 2.0;
            } else if (maxHeap.size() > minHeap.size()) {
                return maxHeap.peek();
            } else {
                return minHeap.peek();
            }
        }
    }

    public static void main(String[] args) {
        MedianOfRunningStream medianFinder = new MedianOfRunningStream();

        // Test Case 1: Basic operations
        medianFinder.addNum(1);
        medianFinder.addNum(2);
        System.out.println("After adding 1,2: " + medianFinder.findMedian()); // 1.5
        medianFinder.addNum(3);
        System.out.println("After adding 3: " + medianFinder.findMedian()); // 2.0

        // Test Case 2: Single element
        MedianOfRunningStream single = new MedianOfRunningStream();
        single.addNum(5);
        System.out.println("Single element: " + single.findMedian()); // 5.0

        // Test Case 3: Negative numbers
        MedianOfRunningStream negative = new MedianOfRunningStream();
        negative.addNum(-1);
        negative.addNum(-2);
        negative.addNum(-3);
        System.out.println("Negative numbers: " + negative.findMedian()); // -2.0

        // Test Case 4: Duplicates
        MedianOfRunningStream dup = new MedianOfRunningStream();
        dup.addNum(1);
        dup.addNum(1);
        dup.addNum(1);
        System.out.println("Duplicates: " + dup.findMedian()); // 1.0

        // Test Case 5: BST approach
        MedianFinderBST bst = new MedianFinderBST();
        bst.addNum(1);
        bst.addNum(2);
        bst.addNum(3);
        System.out.println("BST approach: " + bst.findMedian()); // 2.0

        // Test Case 6: Range [0, 100] optimization
        MedianFinderRange range = new MedianFinderRange();
        range.addNum(50);
        range.addNum(25);
        range.addNum(75);
        System.out.println("Range approach: " + range.findMedian()); // 50.0

        // Test Case 7: List approach
        MedianFinderList list = new MedianFinderList();
        list.addNum(6);
        list.addNum(10);
        list.addNum(2);
        list.addNum(6);
        list.addNum(5);
        System.out.println("List approach: " + list.findMedian()); // 6.0

        // Test Case 8: Large sequence
        MedianOfRunningStream large = new MedianOfRunningStream();
        for (int i = 1; i <= 10; i++) {
            large.addNum(i);
        }
        System.out.println("Large sequence: " + large.findMedian()); // 5.5

        // Test Case 9: Descending order
        MedianOfRunningStream desc = new MedianOfRunningStream();
        for (int i = 10; i >= 1; i--) {
            desc.addNum(i);
        }
        System.out.println("Descending: " + desc.findMedian()); // 5.5

        // Test Case 10: Mixed operations
        MedianOfRunningStream mixed = new MedianOfRunningStream();
        mixed.addNum(4);
        mixed.addNum(2);
        mixed.addNum(6);
        mixed.addNum(1);
        mixed.addNum(3);
        System.out.println("Mixed: " + mixed.findMedian()); // 3.0
        System.out.println("Size: " + mixed.size()); // 5

        // Test Case 11: Sliding window median
        SlidingWindowMedian swm = new SlidingWindowMedian();
        int[] nums = { 1, 3, -1, -3, 5, 3, 6, 7 };
        double[] windowMedians = swm.medianSlidingWindow(nums, 3);
        System.out.println("Sliding window medians: " + Arrays.toString(windowMedians));

        // Test Case 12: Edge case with zero
        MedianOfRunningStream zero = new MedianOfRunningStream();
        zero.addNum(0);
        zero.addNum(-1);
        zero.addNum(1);
        System.out.println("With zero: " + zero.findMedian()); // 0.0
    }
}
