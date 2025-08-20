package heap.hard;

import java.util.PriorityQueue;

/**
 * LeetCode 295: Find Median from Data Stream (Heap Variant)
 * https://leetcode.com/problems/find-median-from-data-stream/
 *
 * Description:
 * The MedianFinder class supports adding numbers and finding the median
 * efficiently using heaps.
 *
 * Constraints:
 * - -10^5 <= num <= 10^5
 * - At most 5 * 10^4 calls will be made to addNum and findMedian.
 *
 * Follow-up:
 * - Can you solve it in O(log n) time per operation?
 */
public class FindMedianFromDataStreamHeap {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

    public FindMedianFromDataStreamHeap() {
        // Constructor is intentionally empty.
    }

    /**
     * Adds a number to the data structure.
     * Balances the two heaps to maintain the median property.
     *
     * @param num The number to add.
     */
    public void addNum(int num) {
        maxHeap.offer(num);
        minHeap.offer(maxHeap.poll());
        if (maxHeap.size() < minHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    /**
     * Returns the median of all elements so far.
     *
     * @return The median.
     */
    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        } else {
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }

    public static void main(String[] args) {
        FindMedianFromDataStreamHeap mf = new FindMedianFromDataStreamHeap();
        mf.addNum(1);
        mf.addNum(2);
        System.out.println(mf.findMedian()); // 1.5
        mf.addNum(3);
        System.out.println(mf.findMedian()); // 2.0
        mf.addNum(4);
        mf.addNum(5);
        System.out.println(mf.findMedian()); // 3.0
        // Edge Case 1: Negative numbers
        FindMedianFromDataStreamHeap mf2 = new FindMedianFromDataStreamHeap();
        mf2.addNum(-1);
        mf2.addNum(-2);
        System.out.println(mf2.findMedian()); // -1.5
        // Edge Case 2: Single element
        FindMedianFromDataStreamHeap mf3 = new FindMedianFromDataStreamHeap();
        mf3.addNum(42);
        System.out.println(mf3.findMedian()); // 42.0
        // Edge Case 3: Large input
        FindMedianFromDataStreamHeap mf4 = new FindMedianFromDataStreamHeap();
        for (int i = 0; i < 1000; i++)
            mf4.addNum(i);
        System.out.println(mf4.findMedian()); // 499.5
    }
}
