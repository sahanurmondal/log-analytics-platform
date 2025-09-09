package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 346: Moving Average from Data Stream
 * https://leetcode.com/problems/moving-average-from-data-stream/
 * 
 * Advanced Variation: Sliding Window Average with Multiple Queries
 *
 * Description:
 * Given an array and multiple queries asking for average of sliding windows
 * with different sizes and positions, answer all queries efficiently.
 * Similar to but more advanced than standard LeetCode 346 as we need to handle:
 * - Multiple window sizes
 * - O(1) query time
 * - Array updates
 * - Additional statistics
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * - 1 <= queries.length <= 10^4
 * - Each query: [start, end] where 0 <= start <= end < nums.length
 *
 * Follow-up:
 * - Can you preprocess to answer queries in O(1) time?
 * - Can you handle updates to the array?
 * - Can you extend to other statistics like median, mode?
 */
public class SlidingWindowAverage {
    private final long[] prefixSum; // For O(1) range sum queries
    private final int[] bit; // Binary Indexed Tree for updates
    private final int[] nums; // Original array
    private final int n; // Array length

    public SlidingWindowAverage(int[] nums) {
        this.nums = nums.clone();
        this.n = nums.length;
        this.prefixSum = new long[n + 1];
        this.bit = new int[n + 1];

        // Build prefix sum array
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
            updateBIT(i + 1, nums[i]);
        }
    }

    // O(1) range average query
    public double rangeAverage(int start, int end) {
        return (double) (prefixSum[end + 1] - prefixSum[start]) / (end - start + 1);
    }

    // Follow-up 1: Handle updates efficiently using BIT
    public void update(int index, int newValue) {
        int diff = newValue - nums[index];
        nums[index] = newValue;
        for (int i = index + 1; i < prefixSum.length; i++) {
            prefixSum[i] += diff;
        }
        updateBIT(index + 1, diff);
    }

    // Follow-up 2: Get median of range using two heaps
    public double rangeMedian(int start, int end) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int i = start; i <= end; i++) {
            if (maxHeap.isEmpty() || nums[i] <= maxHeap.peek()) {
                maxHeap.offer(nums[i]);
            } else {
                minHeap.offer(nums[i]);
            }

            // Balance heaps
            while (maxHeap.size() > minHeap.size() + 1) {
                minHeap.offer(maxHeap.poll());
            }
            while (minHeap.size() > maxHeap.size()) {
                maxHeap.offer(minHeap.poll());
            }
        }

        if ((end - start + 1) % 2 == 0) {
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }
        return maxHeap.peek();
    }

    // Follow-up 3: Get mode of range
    public int rangeMode(int start, int end) {
        Map<Integer, Integer> freq = new HashMap<>();
        int maxFreq = 0, mode = 0;

        for (int i = start; i <= end; i++) {
            int count = freq.getOrDefault(nums[i], 0) + 1;
            freq.put(nums[i], count);
            if (count > maxFreq) {
                maxFreq = count;
                mode = nums[i];
            }
        }
        return mode;
    }

    // Helper: Update Binary Indexed Tree
    private void updateBIT(int index, int val) {
        while (index <= n) {
            bit[index] += val;
            index += index & (-index);
        }
    }

    // Helper: Query Binary Indexed Tree
    private int queryBIT(int index) {
        int sum = 0;
        while (index > 0) {
            sum += bit[index];
            index -= index & (-index);
        }
        return sum;
    }

    public static void main(String[] args) {
        // Basic test cases
        int[] nums = { 1, 3, 2, 6, -1, 4, 1, 8, 2 };
        SlidingWindowAverage swa = new SlidingWindowAverage(nums);

        // Test range average
        System.out.println("\nRange Average Tests:");
        System.out.println("Average [0,4]: " + swa.rangeAverage(0, 4)); // [1,3,2,6,-1]
        System.out.println("Average [2,6]: " + swa.rangeAverage(2, 6)); // [2,6,-1,4,1]
        System.out.println("Average [1,3]: " + swa.rangeAverage(1, 3)); // [3,2,6]

        // Test updates
        System.out.println("\nUpdate Tests:");
        System.out.println("Before update [1,3]: " + swa.rangeAverage(1, 3));
        swa.update(2, 10); // Change 2 to 10
        System.out.println("After update [1,3]: " + swa.rangeAverage(1, 3));

        // Test median
        System.out.println("\nMedian Tests:");
        System.out.println("Median [0,4]: " + swa.rangeMedian(0, 4));
        System.out.println("Median [2,6]: " + swa.rangeMedian(2, 6));

        // Test mode
        System.out.println("\nMode Tests:");
        System.out.println("Mode [0,4]: " + swa.rangeMode(0, 4));
        System.out.println("Mode [2,6]: " + swa.rangeMode(2, 6));

        // Edge cases
        System.out.println("\nEdge Cases:");
        // Single element
        System.out.println("Single element average: " + swa.rangeAverage(0, 0));
        System.out.println("Single element median: " + swa.rangeMedian(0, 0));
        System.out.println("Single element mode: " + swa.rangeMode(0, 0));

        // Entire array
        System.out.println("Entire array average: " + swa.rangeAverage(0, nums.length - 1));
        System.out.println("Entire array median: " + swa.rangeMedian(0, nums.length - 1));
        System.out.println("Entire array mode: " + swa.rangeMode(0, nums.length - 1));

        // Negative numbers
        System.out.println("Negative number handling: " + swa.rangeAverage(4, 4));

        // Stress test
        System.out.println("\nStress Test:");
        int[] largeArray = new int[100000];
        Arrays.fill(largeArray, 1);
        SlidingWindowAverage largeTest = new SlidingWindowAverage(largeArray);

        long start = System.nanoTime();
        double result = largeTest.rangeAverage(0, 99999);
        long end = System.nanoTime();

        System.out.println("Large array average: " + result);
        System.out.println("Time taken: " + (end - start) / 1_000_000.0 + " ms");
    }
}
