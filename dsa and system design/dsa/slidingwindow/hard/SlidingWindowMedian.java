package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 480: Sliding Window Median
 * URL: https://leetcode.com/problems/sliding-window-median/
 * Difficulty: Hard
 * 
 * Companies: Amazon, Google, Microsoft, Facebook, Apple, Uber
 * Frequency: High
 * 
 * Description:
 * Given an array nums and a sliding window of size k, find the median
 * of each window as it moves from left to right. Return an array of medians.
 * 
 * Constraints:
 * - 1 <= k <= nums.length <= 10^5
 * - -2^31 <= nums[i] <= 2^31 - 1
 * - All medians should fit in 32-bit signed integer
 * 
 * Follow-up Questions:
 * 1. Can you solve it using two heaps efficiently?
 * 2. How would you handle the removal of elements from heaps?
 * 3. Can you optimize using an order statistic tree?
 * 4. How would you handle integer overflow?
 * 5. How would you optimize for memory usage?
 */
public class SlidingWindowMedian {

    // Approach 1: Two Heaps with Lazy Deletion
    public double[] medianSlidingWindow(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        Map<Integer, Integer> toRemove = new HashMap<>();

        // Initialize first window
        for (int i = 0; i < k; i++) {
            maxHeap.offer(nums[i]);
        }

        // Balance heaps for first window
        for (int i = 0; i < k / 2; i++) {
            minHeap.offer(maxHeap.poll());
        }

        result[0] = getMedian(maxHeap, minHeap, k);

        // Process remaining windows
        for (int i = k; i < nums.length; i++) {
            int toAdd = nums[i];
            int toRemoveNum = nums[i - k];

            // Mark element for removal
            toRemove.put(toRemoveNum, toRemove.getOrDefault(toRemoveNum, 0) + 1);

            // Add new element to appropriate heap
            if (!maxHeap.isEmpty() && toAdd <= maxHeap.peek()) {
                maxHeap.offer(toAdd);
            } else {
                minHeap.offer(toAdd);
            }

            // Balance heaps
            balance(maxHeap, minHeap, toRemove);

            result[i - k + 1] = getMedian(maxHeap, minHeap, k);
        }

        return result;
    }

    private void balance(PriorityQueue<Integer> maxHeap, PriorityQueue<Integer> minHeap,
            Map<Integer, Integer> toRemove) {
        // Remove invalid elements from heap tops
        while (!maxHeap.isEmpty() && toRemove.getOrDefault(maxHeap.peek(), 0) > 0) {
            int removed = maxHeap.poll();
            toRemove.put(removed, toRemove.get(removed) - 1);
        }

        while (!minHeap.isEmpty() && toRemove.getOrDefault(minHeap.peek(), 0) > 0) {
            int removed = minHeap.poll();
            toRemove.put(removed, toRemove.get(removed) - 1);
        }

        // Balance heap sizes
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    private double getMedian(PriorityQueue<Integer> maxHeap, PriorityQueue<Integer> minHeap, int k) {
        if (k % 2 == 1) {
            return maxHeap.peek();
        } else {
            return ((long) maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }

    // Approach 2: TreeMap with count tracking
    public double[] medianSlidingWindowTreeMap(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];
        TreeMap<Integer, Integer> map = new TreeMap<>();

        // Initialize first window
        for (int i = 0; i < k; i++) {
            map.put(nums[i], map.getOrDefault(nums[i], 0) + 1);
        }

        result[0] = findKthElement(map, k);

        // Process remaining windows
        for (int i = k; i < nums.length; i++) {
            // Remove element going out of window
            int toRemove = nums[i - k];
            map.put(toRemove, map.get(toRemove) - 1);
            if (map.get(toRemove) == 0) {
                map.remove(toRemove);
            }

            // Add new element
            map.put(nums[i], map.getOrDefault(nums[i], 0) + 1);

            result[i - k + 1] = findKthElement(map, k);
        }

        return result;
    }

    private double findKthElement(TreeMap<Integer, Integer> map, int k) {
        int count = 0;
        List<Integer> elements = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                elements.add(entry.getKey());
                count++;
                if (count >= k)
                    break;
            }
            if (count >= k)
                break;
        }

        if (k % 2 == 1) {
            return elements.get(k / 2);
        } else {
            return ((long) elements.get(k / 2 - 1) + elements.get(k / 2)) / 2.0;
        }
    }

    // Approach 3: Simplified two heaps with clean removal
    public double[] medianSlidingWindowSimple(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];

        for (int i = 0; i <= nums.length - k; i++) {
            List<Integer> window = new ArrayList<>();
            for (int j = i; j < i + k; j++) {
                window.add(nums[j]);
            }
            Collections.sort(window);

            if (k % 2 == 1) {
                result[i] = window.get(k / 2);
            } else {
                result[i] = ((long) window.get(k / 2 - 1) + window.get(k / 2)) / 2.0;
            }
        }

        return result;
    }

    // Approach 4: Multiset simulation using TreeMap
    public double[] medianSlidingWindowMultiset(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];
        TreeMap<Integer, Integer> left = new TreeMap<>(Collections.reverseOrder()); // Max heap simulation
        TreeMap<Integer, Integer> right = new TreeMap<>(); // Min heap simulation

        int leftSize = 0, rightSize = 0;

        // Process each window
        for (int i = 0; i < nums.length; i++) {
            // Add new element
            if (leftSize == 0 || nums[i] <= left.lastKey()) {
                left.put(nums[i], left.getOrDefault(nums[i], 0) + 1);
                leftSize++;
            } else {
                right.put(nums[i], right.getOrDefault(nums[i], 0) + 1);
                rightSize++;
            }

            // Remove element if window size exceeded
            if (i >= k) {
                int toRemove = nums[i - k];
                if (left.containsKey(toRemove)) {
                    left.put(toRemove, left.get(toRemove) - 1);
                    if (left.get(toRemove) == 0)
                        left.remove(toRemove);
                    leftSize--;
                } else {
                    right.put(toRemove, right.get(toRemove) - 1);
                    if (right.get(toRemove) == 0)
                        right.remove(toRemove);
                    rightSize--;
                }
            }

            // Balance heaps
            while (leftSize > rightSize + 1) {
                int moveElement = left.lastKey();
                right.put(moveElement, right.getOrDefault(moveElement, 0) + 1);
                rightSize++;

                left.put(moveElement, left.get(moveElement) - 1);
                if (left.get(moveElement) == 0)
                    left.remove(moveElement);
                leftSize--;
            }

            while (rightSize > leftSize) {
                int moveElement = right.firstKey();
                left.put(moveElement, left.getOrDefault(moveElement, 0) + 1);
                leftSize++;

                right.put(moveElement, right.get(moveElement) - 1);
                if (right.get(moveElement) == 0)
                    right.remove(moveElement);
                rightSize--;
            }

            // Calculate median for current window
            if (i >= k - 1) {
                if (k % 2 == 1) {
                    result[i - k + 1] = left.lastKey();
                } else {
                    result[i - k + 1] = ((long) left.lastKey() + right.firstKey()) / 2.0;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SlidingWindowMedian solution = new SlidingWindowMedian();

        // Test Case 1: Basic example
        int[] nums1 = { 1, 3, -1, -3, 5, 3, 6, 7 };
        System.out.println("Basic: " + Arrays.toString(solution.medianSlidingWindow(nums1, 3)));
        // Expected: [1.0, -1.0, -1.0, 3.0, 5.0, 6.0]

        // Test Case 2: Another example
        int[] nums2 = { 1, 2, 3, 4, 2, 3, 1, 4, 2 };
        System.out.println("Example 2: " + Arrays.toString(solution.medianSlidingWindow(nums2, 3)));
        // Expected: [2.0, 3.0, 3.0, 3.0, 2.0, 3.0, 2.0]

        // Test Case 3: k = 1 (each element is median)
        int[] nums3 = { 1, 4, 2, 3 };
        System.out.println("k=1: " + Arrays.toString(solution.medianSlidingWindow(nums3, 1)));
        // Expected: [1.0, 4.0, 2.0, 3.0]

        // Test Case 4: k = array length
        int[] nums4 = { 1, 2, 3, 4 };
        System.out.println("k=length: " + Arrays.toString(solution.medianSlidingWindow(nums4, 4)));
        // Expected: [2.5]

        // Test Case 5: Even k
        int[] nums5 = { 1, 2, 3, 4, 5, 6 };
        System.out.println("Even k: " + Arrays.toString(solution.medianSlidingWindow(nums5, 2)));
        // Expected: [1.5, 2.5, 3.5, 4.5, 5.5]

        // Test Case 6: TreeMap approach
        System.out.println("TreeMap: " + Arrays.toString(solution.medianSlidingWindowTreeMap(nums1, 3)));

        // Test Case 7: Simple approach (for small arrays)
        int[] nums7 = { 1, 2, 3, 4, 5 };
        System.out.println("Simple: " + Arrays.toString(solution.medianSlidingWindowSimple(nums7, 3)));

        // Test Case 8: All same elements
        int[] nums8 = { 1, 1, 1, 1, 1 };
        System.out.println("All same: " + Arrays.toString(solution.medianSlidingWindow(nums8, 3)));
        // Expected: [1.0, 1.0, 1.0]

        // Test Case 9: Negative numbers
        int[] nums9 = { -1, -2, -3, -4, -5 };
        System.out.println("Negative: " + Arrays.toString(solution.medianSlidingWindow(nums9, 3)));
        // Expected: [-2.0, -3.0, -4.0]

        // Test Case 10: Mixed positive and negative
        int[] nums10 = { -1, 3, -1, -3, 5 };
        System.out.println("Mixed: " + Arrays.toString(solution.medianSlidingWindow(nums10, 3)));
        // Expected: [-1.0, -1.0, -1.0]

        // Test Case 11: Large numbers (testing overflow prevention)
        int[] nums11 = { Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MIN_VALUE, Integer.MIN_VALUE + 1 };
        System.out.println("Large numbers: " + Arrays.toString(solution.medianSlidingWindow(nums11, 2)));

        // Test Case 12: Multiset approach
        System.out.println("Multiset: " + Arrays.toString(solution.medianSlidingWindowMultiset(nums1, 3)));
    }
}
