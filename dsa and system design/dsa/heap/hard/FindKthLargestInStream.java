package heap.hard;

import java.util.PriorityQueue;

/**
 * LeetCode 703: Kth Largest Element in a Stream
 * https://leetcode.com/problems/kth-largest-element-in-a-stream/
 *
 * Description:
 * Design a class to find the kth largest element in a stream.
 *
 * Constraints:
 * - 1 <= k <= 10^4
 * - -10^4 <= val <= 10^4
 * - At most 10^5 calls to add.
 *
 * Follow-up:
 * - Can you solve it in O(log k) time per operation?
 */
public class FindKthLargestInStream {
    private final PriorityQueue<Integer> minHeap;
    private final int k;

    /**
     * Constructor to initialize the KthLargest object.
     *
     * @param k    The k value.
     * @param nums The initial numbers in the stream.
     */
    public FindKthLargestInStream(int k, int[] nums) {
        this.k = k;
        this.minHeap = new PriorityQueue<>(k);
        for (int num : nums) {
            add(num);
        }
    }

    /**
     * Adds a number to the stream and returns the Kth largest element.
     *
     * @param val The number to add.
     * @return The Kth largest element.
     */
    public int add(int val) {
        if (minHeap.size() < k) {
            minHeap.offer(val);
        } else if (val > minHeap.peek()) {
            minHeap.poll();
            minHeap.offer(val);
        }
        return minHeap.peek();
    }

    public static void main(String[] args) {
        FindKthLargestInStream kth = new FindKthLargestInStream(3, new int[] { 4, 5, 8, 2 });
        System.out.println(kth.add(3)); // 4
        System.out.println(kth.add(5)); // 5
        System.out.println(kth.add(10)); // 5
        System.out.println(kth.add(9)); // 8
        System.out.println(kth.add(4)); // 8
        // Edge Case 1: k == 1
        FindKthLargestInStream kth1 = new FindKthLargestInStream(1, new int[] { 1 });
        System.out.println(kth1.add(2)); // 2
        // Edge Case 2: All same
        FindKthLargestInStream kth2 = new FindKthLargestInStream(2, new int[] { 7, 7, 7 });
        System.out.println(kth2.add(7)); // 7
        // Edge Case 3: Negative numbers
        FindKthLargestInStream kth3 = new FindKthLargestInStream(2, new int[] { -1, -2, -3 });
        System.out.println(kth3.add(-4)); // -2
        // Edge Case 4: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = i;
        FindKthLargestInStream kthLarge = new FindKthLargestInStream(10000, large);
        System.out.println(kthLarge.add(10001)); // 1
    }
}
