package hashmaps.medium;

import java.util.*;

/**
 * LeetCode 347: Top K Frequent Elements
 * https://leetcode.com/problems/top-k-frequent-elements/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description: Given an integer array `nums` and an integer `k`, return the `k`
 * most frequent elements. You may return the answer in any order.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - k is in the range [1, the number of unique elements in the array].
 * - It is guaranteed that the answer is unique.
 * 
 * Follow-up Questions:
 * 1. Can you solve this with a time complexity better than O(n log n)?
 * 2. What if the data is a stream? (Heap approach is good for this)
 * 3. How would you handle ties in frequency?
 */
public class TopKFrequentElements {

    // Approach 1: HashMap + Min-Heap - O(n log k) time, O(n) space
    public int[] topKFrequent(int[] nums, int k) {
        // 1. Count frequencies
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        // 2. Use a min-heap to keep track of the top k elements
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(Comparator.comparingInt(freqMap::get));

        for (int num : freqMap.keySet()) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        // 3. Extract elements from the heap
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = minHeap.poll();
        }

        return result;
    }

    // Approach 2: Bucket Sort - O(n) time, O(n) space
    public int[] topKFrequentBucketSort(int[] nums, int k) {
        // 1. Count frequencies
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        // 2. Create buckets for frequencies
        List<Integer>[] buckets = new List[nums.length + 1];
        for (int num : freqMap.keySet()) {
            int freq = freqMap.get(num);
            if (buckets[freq] == null) {
                buckets[freq] = new ArrayList<>();
            }
            buckets[freq].add(num);
        }

        // 3. Collect top k elements from buckets
        int[] result = new int[k];
        int index = 0;
        for (int i = buckets.length - 1; i >= 0 && index < k; i--) {
            if (buckets[i] != null) {
                for (int num : buckets[i]) {
                    result[index++] = num;
                    if (index == k)
                        break;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        TopKFrequentElements solution = new TopKFrequentElements();

        // Test case 1
        int[] nums1 = { 1, 1, 1, 2, 2, 3 };
        int k1 = 2;
        System.out.println("Top K (Heap) 1: " + Arrays.toString(solution.topKFrequent(nums1, k1))); // [1, 2]
        System.out.println("Top K (Bucket) 1: " + Arrays.toString(solution.topKFrequentBucketSort(nums1, k1))); // [1,
                                                                                                                // 2]

        // Test case 2
        int[] nums2 = { 1 };
        int k2 = 1;
        System.out.println("Top K 2: " + Arrays.toString(solution.topKFrequent(nums2, k2))); // [1]

        // Test case 3: Negative numbers
        int[] nums3 = { -1, -1 };
        int k3 = 1;
        System.out.println("Top K 3: " + Arrays.toString(solution.topKFrequent(nums3, k3))); // [-1]

        // Test case 4: All elements are unique
        int[] nums4 = { 1, 2, 3, 4, 5 };
        int k4 = 3;
        System.out.println("Top K 4: " + Arrays.toString(solution.topKFrequent(nums4, k4))); // Any 3 elements
    }
}
