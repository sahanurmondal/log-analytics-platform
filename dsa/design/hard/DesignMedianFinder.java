package design.hard;

import java.util.*;

/**
 * LeetCode 295: Find Median from Data Stream
 * https://leetcode.com/problems/find-median-from-data-stream/
 *
 * Description: The median is the middle value in an ordered integer list.
 * Design a data structure that supports adding numbers and finding the median.
 * 
 * Constraints:
 * - -10^5 <= num <= 10^5
 * - There will be at least one element in the data structure before calling
 * findMedian
 * - At most 5 * 10^4 calls will be made to addNum and findMedian
 *
 * Follow-up:
 * - Can you solve it in O(log n) time for addNum and O(1) for findMedian?
 * - What if all numbers are in [0, 100]?
 * 
 * Time Complexity: O(log n) for addNum, O(1) for findMedian
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class DesignMedianFinder {

    private PriorityQueue<Integer> maxHeap; // Lower half
    private PriorityQueue<Integer> minHeap; // Upper half

    public DesignMedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }

        // Balance heaps
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size() + 1) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        } else if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        } else {
            return minHeap.peek();
        }
    }

    // Alternative for range [0, 100] - Bucket approach
    static class MedianFinderBucket {
        private int[] buckets;
        private int count;

        public MedianFinderBucket() {
            buckets = new int[101]; // 0 to 100
            count = 0;
        }

        public void addNum(int num) {
            buckets[num]++;
            count++;
        }

        public double findMedian() {
            int target1 = (count + 1) / 2;
            int target2 = (count + 2) / 2;

            int current = 0;
            int median1 = -1, median2 = -1;

            for (int i = 0; i <= 100; i++) {
                if (buckets[i] > 0) {
                    current += buckets[i];

                    if (median1 == -1 && current >= target1) {
                        median1 = i;
                    }
                    if (current >= target2) {
                        median2 = i;
                        break;
                    }
                }
            }

            return (median1 + median2) / 2.0;
        }
    }

    public static void main(String[] args) {
        DesignMedianFinder medianFinder = new DesignMedianFinder();
        medianFinder.addNum(1);
        medianFinder.addNum(2);
        System.out.println(medianFinder.findMedian()); // Expected: 1.5
        medianFinder.addNum(3);
        System.out.println(medianFinder.findMedian()); // Expected: 2.0

        // Test bucket approach
        MedianFinderBucket bucketFinder = new MedianFinderBucket();
        bucketFinder.addNum(6);
        System.out.println(bucketFinder.findMedian()); // Expected: 6.0
        bucketFinder.addNum(10);
        System.out.println(bucketFinder.findMedian()); // Expected: 8.0
    }
}
