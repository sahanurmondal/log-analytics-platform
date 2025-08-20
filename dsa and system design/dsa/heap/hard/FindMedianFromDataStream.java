package heap.hard;

import java.util.Collections;
import java.util.PriorityQueue;

/**
 * LeetCode 295: Find Median from Data Stream
 * https://leetcode.com/problems/find-median-from-data-stream/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Very High (Asked in 25+ interviews)
 *
 * Description:
 * The median is the middle value in an ordered integer list. If the size of the
 * list is even, there is no middle value, and the median is the mean of the two
 * middle values.
 * Implement the MedianFinder class:
 * - `MedianFinder()` initializes the `MedianFinder` object.
 * - `void addNum(int num)` adds the integer `num` from the data stream to the
 * data structure.
 * - `double findMedian()` returns the median of all elements so far.
 *
 * Constraints:
 * - -10^5 <= num <= 10^5
 * - There will be at least one element in the data structure before calling
 * `findMedian`.
 * - At most 5 * 10^4 calls will be made to `addNum` and `findMedian`.
 * 
 * Follow-up Questions:
 * 1. If all integer numbers from the stream are in the range [0, 100], how
 * would you optimize?
 * 2. If 99% of all integer numbers from the stream are in the range [0, 100],
 * how would you optimize?
 * 3. Can you explain the logic of balancing the two heaps?
 */
public class FindMedianFromDataStream {

    // Approach 1: Two Heaps - O(log n) for addNum, O(1) for findMedian. O(n) space.
    private PriorityQueue<Integer> smallHalf; // Max-heap
    private PriorityQueue<Integer> largeHalf; // Min-heap

    public FindMedianFromDataStream() {
        // `smallHalf` stores the smaller half of the numbers, so we use a max-heap.
        smallHalf = new PriorityQueue<>(Collections.reverseOrder());
        // `largeHalf` stores the larger half of the numbers, so we use a min-heap.
        largeHalf = new PriorityQueue<>();
    }

    public void addNum(int num) {
        // Add to max-heap first
        smallHalf.offer(num);

        // Balance the heaps: move the largest element from smallHalf to largeHalf
        if (!smallHalf.isEmpty() && !largeHalf.isEmpty() && smallHalf.peek() > largeHalf.peek()) {
            largeHalf.offer(smallHalf.poll());
        }

        // Ensure heaps are of similar size (or smallHalf has one more element)
        if (smallHalf.size() > largeHalf.size() + 1) {
            largeHalf.offer(smallHalf.poll());
        }
        if (largeHalf.size() > smallHalf.size()) {
            smallHalf.offer(largeHalf.poll());
        }
    }

    public double findMedian() {
        if (smallHalf.size() > largeHalf.size()) {
            return smallHalf.peek();
        } else {
            return (smallHalf.peek() + largeHalf.peek()) / 2.0;
        }
    }

    // Follow-up 1 & 2: If numbers are in a limited range [0, 100]
    // We can use an integer array (bucket/frequency count) of size 101.
    // `addNum` is O(1). `findMedian` is O(range), which is O(101) -> O(1).
    // For the 99% case, we can use a hybrid approach: buckets for the [0,100] range
    // and the two-heap approach for outliers.

    public static void main(String[] args) {
        FindMedianFromDataStream medianFinder = new FindMedianFromDataStream();
        medianFinder.addNum(1);
        medianFinder.addNum(2);
        System.out.println("Median: " + medianFinder.findMedian()); // 1.5
        medianFinder.addNum(3);
        System.out.println("Median: " + medianFinder.findMedian()); // 2
        medianFinder.addNum(0);
        System.out.println("Median: " + medianFinder.findMedian()); // 1.5
        medianFinder.addNum(5);
        System.out.println("Median: " + medianFinder.findMedian()); // 2

        // Edge Case: Negative numbers
        FindMedianFromDataStream medianFinder2 = new FindMedianFromDataStream();
        medianFinder2.addNum(-1);
        System.out.println("Median: " + medianFinder2.findMedian()); // -1.0
        medianFinder2.addNum(-2);
        System.out.println("Median: " + medianFinder2.findMedian()); // -1.5
        medianFinder2.addNum(-3);
        System.out.println("Median: " + medianFinder2.findMedian()); // -2.0
    }
}
