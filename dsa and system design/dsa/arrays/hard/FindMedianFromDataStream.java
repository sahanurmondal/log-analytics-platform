package arrays.hard;

import java.util.*;

/**
 * LeetCode 295: Find Median from Data Stream
 * https://leetcode.com/problems/find-median-from-data-stream/
 *
 * Description:
 * The median is the middle value in an ordered integer list. If the size of the
 * list is even,
 * there is no middle value and the median is the mean of the two middle values.
 * Design a data structure that supports the following two operations:
 * - void addNum(int num) - Add a integer number from the data stream to the
 * data structure.
 * - double findMedian() - Return the median of all elements so far.
 *
 * Constraints:
 * - -10^5 <= num <= 10^5
 * - There will be at least one element in the data structure before calling
 * findMedian
 * - At most 5 * 10^4 calls will be made to addNum and findMedian
 *
 * Follow-up:
 * - If all integer numbers from the stream are in the range [0, 100], how would
 * you optimize it?
 * - If 99% of all integer numbers from the stream are in the range [0, 100],
 * how would you optimize it?
 * 
 * Time Complexity: O(log n) for addNum, O(1) for findMedian
 * Space Complexity: O(n)
 */
public class FindMedianFromDataStream {

    class MedianFinder {
        private PriorityQueue<Integer> maxHeap; // Left half - smaller numbers
        private PriorityQueue<Integer> minHeap; // Right half - larger numbers

        public MedianFinder() {
            maxHeap = new PriorityQueue<>((a, b) -> b - a); // Max heap
            minHeap = new PriorityQueue<>(); // Min heap
        }

        public void addNum(int num) {
            // Always add to maxHeap first
            maxHeap.offer(num);

            // Balance the heaps
            minHeap.offer(maxHeap.poll());

            // Ensure maxHeap has equal or one more element than minHeap
            if (maxHeap.size() < minHeap.size()) {
                maxHeap.offer(minHeap.poll());
            }
        }

        public double findMedian() {
            if (maxHeap.size() > minHeap.size()) {
                return maxHeap.peek();
            } else {
                return (maxHeap.peek() + minHeap.peek()) / 2.0;
            }
        }
    }

    // Alternative implementation - Simpler approach
    class MedianFinderSimple {
        private PriorityQueue<Integer> small; // Max heap for smaller half
        private PriorityQueue<Integer> large; // Min heap for larger half

        public MedianFinderSimple() {
            small = new PriorityQueue<>(Collections.reverseOrder());
            large = new PriorityQueue<>();
        }

        public void addNum(int num) {
            if (small.isEmpty() || num <= small.peek()) {
                small.offer(num);
            } else {
                large.offer(num);
            }

            // Balance heaps
            if (small.size() > large.size() + 1) {
                large.offer(small.poll());
            } else if (large.size() > small.size() + 1) {
                small.offer(large.poll());
            }
        }

        public double findMedian() {
            if (small.size() == large.size()) {
                return (small.peek() + large.peek()) / 2.0;
            } else if (small.size() > large.size()) {
                return small.peek();
            } else {
                return large.peek();
            }
        }
    }

    // Follow-up solution - For numbers in range [0, 100]
    class MedianFinderOptimized {
        private int[] count = new int[101];
        private int totalCount = 0;

        public void addNum(int num) {
            count[num]++;
            totalCount++;
        }

        public double findMedian() {
            int target1 = (totalCount + 1) / 2;
            int target2 = (totalCount + 2) / 2;

            int currentCount = 0;
            int median1 = -1, median2 = -1;

            for (int i = 0; i <= 100; i++) {
                currentCount += count[i];
                if (median1 == -1 && currentCount >= target1) {
                    median1 = i;
                }
                if (currentCount >= target2) {
                    median2 = i;
                    break;
                }
            }

            return (median1 + median2) / 2.0;
        }
    }

    public static void main(String[] args) {
        FindMedianFromDataStream solution = new FindMedianFromDataStream();

        // Test Case 1: Normal case
        FindMedianFromDataStream.MedianFinder mf1 = solution.new MedianFinder();
        mf1.addNum(1);
        mf1.addNum(2);
        System.out.println(mf1.findMedian()); // Expected: 1.5
        mf1.addNum(3);
        System.out.println(mf1.findMedian()); // Expected: 2.0

        // Test Case 2: Edge case - single element
        FindMedianFromDataStream.MedianFinder mf2 = solution.new MedianFinder();
        mf2.addNum(5);
        System.out.println(mf2.findMedian()); // Expected: 5.0

        // Test Case 3: Negative numbers
        FindMedianFromDataStream.MedianFinder mf3 = solution.new MedianFinder();
        mf3.addNum(-1);
        mf3.addNum(-2);
        System.out.println(mf3.findMedian()); // Expected: -1.5

        // Test Case 4: Mixed positive/negative
        FindMedianFromDataStream.MedianFinder mf4 = solution.new MedianFinder();
        mf4.addNum(-1);
        mf4.addNum(1);
        mf4.addNum(0);
        System.out.println(mf4.findMedian()); // Expected: 0.0

        // Test Case 5: Duplicates
        FindMedianFromDataStream.MedianFinder mf5 = solution.new MedianFinder();
        mf5.addNum(1);
        mf5.addNum(1);
        mf5.addNum(1);
        System.out.println(mf5.findMedian()); // Expected: 1.0

        // Test Case 6: Large numbers
        FindMedianFromDataStream.MedianFinder mf6 = solution.new MedianFinder();
        mf6.addNum(100000);
        mf6.addNum(-100000);
        System.out.println(mf6.findMedian()); // Expected: 0.0

        // Test Case 7: Sequential addition
        FindMedianFromDataStream.MedianFinder mf7 = solution.new MedianFinder();
        for (int i = 1; i <= 5; i++) {
            mf7.addNum(i);
        }
        System.out.println(mf7.findMedian()); // Expected: 3.0

        // Test Case 8: Reverse order
        FindMedianFromDataStream.MedianFinder mf8 = solution.new MedianFinder();
        for (int i = 5; i >= 1; i--) {
            mf8.addNum(i);
        }
        System.out.println(mf8.findMedian()); // Expected: 3.0

        // Test Case 9: Random order
        FindMedianFromDataStream.MedianFinder mf9 = solution.new MedianFinder();
        int[] nums = { 6, 10, 2, 6, 5, 0, 6, 3, 1, 0, 0 };
        for (int num : nums) {
            mf9.addNum(num);
        }
        System.out.println(mf9.findMedian()); // Expected: 3.0

        // Test Case 10: Even count with duplicates
        FindMedianFromDataStream.MedianFinder mf10 = solution.new MedianFinder();
        mf10.addNum(1);
        mf10.addNum(2);
        mf10.addNum(2);
        mf10.addNum(3);
        System.out.println(mf10.findMedian()); // Expected: 2.0
    }
}
