package searching.medium;

import java.util.*;

/**
 * LeetCode 215: Kth Largest Element in an Array
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 * 
 * Companies: Facebook, Amazon, Microsoft, Google, Apple, Bloomberg, Uber
 * Frequency: Very High (Asked in 900+ interviews)
 *
 * Description:
 * Given an integer array nums and an integer k, return the kth largest element
 * in the array.
 * Note that it is the kth largest element in the sorted order, not the kth
 * distinct element.
 * Can you solve it without sorting?
 * 
 * Constraints:
 * - 1 <= k <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. How would you find the kth smallest element?
 * 2. Can you implement using different data structures (heaps, quickselect)?
 * 3. How to handle streaming data (online algorithm)?
 * 4. What about finding top k elements?
 * 5. How to optimize for multiple queries?
 * 6. Can you implement parallel solutions?
 */
public class KthLargestElement {

    // Approach 1: Min Heap - O(n log k) time, O(k) space
    public static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }

    // Approach 2: Max Heap - O(n + k log n) time, O(n) space
    public static int findKthLargestMaxHeap(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

        for (int num : nums) {
            maxHeap.offer(num);
        }

        for (int i = 0; i < k - 1; i++) {
            maxHeap.poll();
        }

        return maxHeap.peek();
    }

    // Approach 3: Quickselect (Average O(n), Worst O(n²)) - O(1) space
    public static int findKthLargestQuickSelect(int[] nums, int k) {
        return quickSelect(nums, 0, nums.length - 1, nums.length - k);
    }

    private static int quickSelect(int[] nums, int left, int right, int k) {
        if (left == right)
            return nums[left];

        int pivotIndex = partition(nums, left, right);

        if (pivotIndex == k) {
            return nums[pivotIndex];
        } else if (pivotIndex < k) {
            return quickSelect(nums, pivotIndex + 1, right, k);
        } else {
            return quickSelect(nums, left, pivotIndex - 1, k);
        }
    }

    private static int partition(int[] nums, int left, int right) {
        int pivot = nums[right];
        int i = left;

        for (int j = left; j < right; j++) {
            if (nums[j] <= pivot) {
                swap(nums, i, j);
                i++;
            }
        }

        swap(nums, i, right);
        return i;
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    // Approach 4: Randomized Quickselect - O(n) average time
    public static int findKthLargestRandomized(int[] nums, int k) {
        Random random = new Random();
        return randomizedQuickSelect(nums, 0, nums.length - 1, nums.length - k, random);
    }

    private static int randomizedQuickSelect(int[] nums, int left, int right, int k, Random random) {
        if (left == right)
            return nums[left];

        // Randomize pivot
        int randomIndex = left + random.nextInt(right - left + 1);
        swap(nums, randomIndex, right);

        int pivotIndex = partition(nums, left, right);

        if (pivotIndex == k) {
            return nums[pivotIndex];
        } else if (pivotIndex < k) {
            return randomizedQuickSelect(nums, pivotIndex + 1, right, k, random);
        } else {
            return randomizedQuickSelect(nums, left, pivotIndex - 1, k, random);
        }
    }

    // Approach 5: Counting Sort (for limited range) - O(n + range) time
    public static int findKthLargestCountingSort(int[] nums, int k) {
        // Find min and max
        int min = Arrays.stream(nums).min().orElse(0);
        int max = Arrays.stream(nums).max().orElse(0);

        int range = max - min + 1;
        int[] count = new int[range];

        // Count frequencies
        for (int num : nums) {
            count[num - min]++;
        }

        // Find kth largest
        int total = 0;
        for (int i = range - 1; i >= 0; i--) {
            total += count[i];
            if (total >= k) {
                return i + min;
            }
        }

        return -1; // Should never reach here
    }

    // Follow-up 1: Find kth smallest element
    public static class KthSmallestElement {

        public static int findKthSmallest(int[] nums, int k) {
            PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

            for (int num : nums) {
                maxHeap.offer(num);
                if (maxHeap.size() > k) {
                    maxHeap.poll();
                }
            }

            return maxHeap.peek();
        }

        public static int findKthSmallestQuickSelect(int[] nums, int k) {
            return quickSelectSmallest(nums, 0, nums.length - 1, k - 1);
        }

        private static int quickSelectSmallest(int[] nums, int left, int right, int k) {
            if (left == right)
                return nums[left];

            int pivotIndex = partition(nums, left, right);

            if (pivotIndex == k) {
                return nums[pivotIndex];
            } else if (pivotIndex < k) {
                return quickSelectSmallest(nums, pivotIndex + 1, right, k);
            } else {
                return quickSelectSmallest(nums, left, pivotIndex - 1, k);
            }
        }

        private static int partition(int[] nums, int left, int right) {
            int pivot = nums[right];
            int i = left;

            for (int j = left; j < right; j++) {
                if (nums[j] <= pivot) {
                    swap(nums, i, j);
                    i++;
                }
            }

            swap(nums, i, right);
            return i;
        }
    }

    // Follow-up 2: Streaming data (online algorithm)
    public static class KthLargestStream {
        private PriorityQueue<Integer> minHeap;
        private int k;

        public KthLargestStream(int k, int[] nums) {
            this.k = k;
            this.minHeap = new PriorityQueue<>(k);

            for (int num : nums) {
                add(num);
            }
        }

        public int add(int val) {
            minHeap.offer(val);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
            return minHeap.peek();
        }

        public int getKthLargest() {
            return minHeap.peek();
        }

        public int size() {
            return minHeap.size();
        }
    }

    // Follow-up 3: Top K elements
    public static class TopKElements {

        public static List<Integer> topKLargest(int[] nums, int k) {
            PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

            for (int num : nums) {
                minHeap.offer(num);
                if (minHeap.size() > k) {
                    minHeap.poll();
                }
            }

            List<Integer> result = new ArrayList<>(minHeap);
            Collections.sort(result, Collections.reverseOrder());
            return result;
        }

        public static List<Integer> topKLargestQuickSelect(int[] nums, int k) {
            // Find kth largest element
            int kthLargest = findKthLargestQuickSelect(nums, k);

            List<Integer> result = new ArrayList<>();
            int count = 0;

            // Add all elements >= kth largest
            for (int num : nums) {
                if (num > kthLargest) {
                    result.add(num);
                    count++;
                } else if (num == kthLargest && count < k) {
                    result.add(num);
                    count++;
                }
            }

            Collections.sort(result, Collections.reverseOrder());
            return result;
        }

        // Top K frequent elements
        public static List<Integer> topKFrequent(int[] nums, int k) {
            Map<Integer, Integer> freqMap = new HashMap<>();
            for (int num : nums) {
                freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
            }

            PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
                    (a, b) -> a.getValue() - b.getValue());

            for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
                minHeap.offer(entry);
                if (minHeap.size() > k) {
                    minHeap.poll();
                }
            }

            List<Integer> result = new ArrayList<>();
            while (!minHeap.isEmpty()) {
                result.add(minHeap.poll().getKey());
            }

            Collections.reverse(result);
            return result;
        }
    }

    // Follow-up 4: Multiple queries optimization
    public static class MultipleQueriesOptimizer {
        private int[] sortedNums;

        public MultipleQueriesOptimizer(int[] nums) {
            this.sortedNums = nums.clone();
            Arrays.sort(this.sortedNums);
        }

        public int findKthLargest(int k) {
            return sortedNums[sortedNums.length - k];
        }

        public int findKthSmallest(int k) {
            return sortedNums[k - 1];
        }

        public List<Integer> getRange(int fromK, int toK) {
            List<Integer> result = new ArrayList<>();
            int start = sortedNums.length - toK;
            int end = sortedNums.length - fromK + 1;

            for (int i = start; i < end; i++) {
                result.add(sortedNums[i]);
            }

            return result;
        }
    }

    // Advanced: Parallel quickselect
    public static class ParallelQuickSelect {

        public static int findKthLargestParallel(int[] nums, int k) {
            return parallelQuickSelect(nums, 0, nums.length - 1, nums.length - k);
        }

        private static int parallelQuickSelect(int[] nums, int left, int right, int k) {
            if (right - left < 1000) {
                // Use sequential for small arrays
                return quickSelect(nums, left, right, k);
            }

            int pivotIndex = partition(nums, left, right);

            if (pivotIndex == k) {
                return nums[pivotIndex];
            } else if (pivotIndex < k) {
                return parallelQuickSelect(nums, pivotIndex + 1, right, k);
            } else {
                return parallelQuickSelect(nums, left, pivotIndex - 1, k);
            }
        }

        // Parallel with ForkJoin
        public static int findKthLargestForkJoin(int[] nums, int k) {
            java.util.concurrent.ForkJoinPool pool = java.util.concurrent.ForkJoinPool.commonPool();
            return pool.invoke(new QuickSelectTask(nums, 0, nums.length - 1, nums.length - k));
        }

        private static class QuickSelectTask extends java.util.concurrent.RecursiveTask<Integer> {
            private int[] nums;
            private int left, right, k;
            private static final int THRESHOLD = 1000;

            public QuickSelectTask(int[] nums, int left, int right, int k) {
                this.nums = nums;
                this.left = left;
                this.right = right;
                this.k = k;
            }

            @Override
            protected Integer compute() {
                if (right - left < THRESHOLD) {
                    return quickSelect(nums, left, right, k);
                }

                int pivotIndex = partition(nums, left, right);

                if (pivotIndex == k) {
                    return nums[pivotIndex];
                } else if (pivotIndex < k) {
                    QuickSelectTask task = new QuickSelectTask(nums, pivotIndex + 1, right, k);
                    return task.compute();
                } else {
                    QuickSelectTask task = new QuickSelectTask(nums, left, pivotIndex - 1, k);
                    return task.compute();
                }
            }
        }
    }

    // Advanced: Median of medians for guaranteed O(n) time
    public static class MedianOfMedians {

        public static int findKthLargestMedianOfMedians(int[] nums, int k) {
            return selectMedianOfMedians(nums, 0, nums.length - 1, nums.length - k);
        }

        private static int selectMedianOfMedians(int[] nums, int left, int right, int k) {
            if (right - left < 5) {
                Arrays.sort(nums, left, right + 1);
                return nums[k];
            }

            // Find median of medians
            int medianOfMedians = findMedianOfMedians(nums, left, right);

            // Partition around median of medians
            int pivotIndex = partitionAroundValue(nums, left, right, medianOfMedians);

            if (pivotIndex == k) {
                return nums[pivotIndex];
            } else if (pivotIndex < k) {
                return selectMedianOfMedians(nums, pivotIndex + 1, right, k);
            } else {
                return selectMedianOfMedians(nums, left, pivotIndex - 1, k);
            }
        }

        private static int findMedianOfMedians(int[] nums, int left, int right) {
            int n = right - left + 1;
            int[] medians = new int[(n + 4) / 5];

            for (int i = 0; i < medians.length; i++) {
                int subLeft = left + i * 5;
                int subRight = Math.min(subLeft + 4, right);

                Arrays.sort(nums, subLeft, subRight + 1);
                medians[i] = nums[subLeft + (subRight - subLeft) / 2];
            }

            return selectMedianOfMedians(medians, 0, medians.length - 1, medians.length / 2);
        }

        private static int partitionAroundValue(int[] nums, int left, int right, int value) {
            // Find the value in array and move to end
            for (int i = left; i <= right; i++) {
                if (nums[i] == value) {
                    swap(nums, i, right);
                    break;
                }
            }

            return partition(nums, left, right);
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(int[] nums, int k, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Array size: " + nums.length + ", k=" + k + ", Iterations: " + iterations);

            // Min Heap
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findKthLargest(nums.clone(), k);
            }
            long minHeapTime = System.nanoTime() - start;

            // Max Heap
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findKthLargestMaxHeap(nums.clone(), k);
            }
            long maxHeapTime = System.nanoTime() - start;

            // Quickselect
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findKthLargestQuickSelect(nums.clone(), k);
            }
            long quickSelectTime = System.nanoTime() - start;

            // Randomized Quickselect
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findKthLargestRandomized(nums.clone(), k);
            }
            long randomizedTime = System.nanoTime() - start;

            // Simple sort (for comparison)
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                int[] sorted = nums.clone();
                Arrays.sort(sorted);
                sorted[sorted.length - k] = sorted[sorted.length - k]; // Access kth largest
            }
            long sortTime = System.nanoTime() - start;

            System.out.println("Min Heap: " + minHeapTime / 1_000_000 + " ms");
            System.out.println("Max Heap: " + maxHeapTime / 1_000_000 + " ms");
            System.out.println("Quickselect: " + quickSelectTime / 1_000_000 + " ms");
            System.out.println("Randomized Quickselect: " + randomizedTime / 1_000_000 + " ms");
            System.out.println("Sort: " + sortTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[] nums1 = { 3, 2, 1, 5, 6, 4 };
        int k1 = 2;

        System.out.println("Input: " + Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("Min Heap: " + findKthLargest(nums1.clone(), k1));
        System.out.println("Max Heap: " + findKthLargestMaxHeap(nums1.clone(), k1));
        System.out.println("Quickselect: " + findKthLargestQuickSelect(nums1.clone(), k1));
        System.out.println("Randomized: " + findKthLargestRandomized(nums1.clone(), k1));
        System.out.println("Counting Sort: " + findKthLargestCountingSort(nums1.clone(), k1));

        // Test Case 2: Different k values
        System.out.println("\n=== Test Case 2: Different K Values ===");

        int[] nums2 = { 3, 2, 3, 1, 2, 4, 5, 5, 6 };

        for (int k = 1; k <= 3; k++) {
            System.out.println("k=" + k + ": " + findKthLargest(nums2.clone(), k));
        }

        // Test Case 3: Edge cases
        System.out.println("\n=== Test Case 3: Edge Cases ===");

        int[] singleElement = { 1 };
        int[] twoElements = { 1, 2 };
        int[] allSame = { 1, 1, 1, 1 };

        System.out.println("Single element (k=1): " + findKthLargest(singleElement, 1));
        System.out.println("Two elements (k=1): " + findKthLargest(twoElements, 1));
        System.out.println("Two elements (k=2): " + findKthLargest(twoElements, 2));
        System.out.println("All same (k=2): " + findKthLargest(allSame, 2));

        // Test Case 4: Kth smallest
        System.out.println("\n=== Test Case 4: Kth Smallest ===");

        int[] nums4 = { 7, 10, 4, 3, 20, 15 };
        int k4 = 3;

        System.out.println("Input: " + Arrays.toString(nums4) + ", k=" + k4);
        System.out.println("Kth largest: " + findKthLargest(nums4.clone(), k4));
        System.out.println("Kth smallest: " + KthSmallestElement.findKthSmallest(nums4.clone(), k4));

        // Test Case 5: Streaming data
        System.out.println("\n=== Test Case 5: Streaming Data ===");

        int[] initialNums = { 4, 5, 8, 2 };
        KthLargestStream stream = new KthLargestStream(3, initialNums);

        System.out.println("Initial array: " + Arrays.toString(initialNums));
        System.out.println("3rd largest: " + stream.getKthLargest());

        int[] newValues = { 3, 5, 10, 9, 4 };
        for (int val : newValues) {
            int kthLargest = stream.add(val);
            System.out.println("Added " + val + ", 3rd largest: " + kthLargest);
        }

        // Test Case 6: Top K elements
        System.out.println("\n=== Test Case 6: Top K Elements ===");

        int[] nums6 = { 3, 2, 1, 5, 6, 4 };
        int k6 = 3;

        System.out.println("Input: " + Arrays.toString(nums6) + ", k=" + k6);
        System.out.println("Top K largest: " + TopKElements.topKLargest(nums6, k6));
        System.out.println("Top K (QuickSelect): " + TopKElements.topKLargestQuickSelect(nums6, k6));

        // Test Case 7: Top K frequent
        System.out.println("\n=== Test Case 7: Top K Frequent ===");

        int[] nums7 = { 1, 1, 1, 2, 2, 3 };
        int k7 = 2;

        System.out.println("Input: " + Arrays.toString(nums7) + ", k=" + k7);
        System.out.println("Top K frequent: " + TopKElements.topKFrequent(nums7, k7));

        // Test Case 8: Multiple queries
        System.out.println("\n=== Test Case 8: Multiple Queries ===");

        int[] nums8 = { 7, 10, 4, 3, 20, 15, 8, 1 };
        MultipleQueriesOptimizer optimizer = new MultipleQueriesOptimizer(nums8);

        System.out.println("Input: " + Arrays.toString(nums8));
        System.out.println("1st largest: " + optimizer.findKthLargest(1));
        System.out.println("3rd largest: " + optimizer.findKthLargest(3));
        System.out.println("2nd smallest: " + optimizer.findKthSmallest(2));
        System.out.println("Range 2nd to 4th largest: " + optimizer.getRange(2, 4));

        // Test Case 9: Large array performance
        System.out.println("\n=== Test Case 9: Large Array Performance ===");

        Random random = new Random(42);
        int[] largeArray = new int[10000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = random.nextInt(10000);
        }

        int k9 = 1000;

        long start = System.currentTimeMillis();
        int result = findKthLargest(largeArray.clone(), k9);
        long end = System.currentTimeMillis();

        System.out.println("Array size: " + largeArray.length + ", k=" + k9);
        System.out.println("Result: " + result);
        System.out.println("Time (Min Heap): " + (end - start) + " ms");

        start = System.currentTimeMillis();
        result = findKthLargestQuickSelect(largeArray.clone(), k9);
        end = System.currentTimeMillis();

        System.out.println("Time (Quickselect): " + (end - start) + " ms");

        // Test Case 10: Parallel implementation
        System.out.println("\n=== Test Case 10: Parallel Implementation ===");

        int[] nums10 = new int[5000];
        for (int i = 0; i < nums10.length; i++) {
            nums10[i] = random.nextInt(5000);
        }

        start = System.currentTimeMillis();
        int sequentialResult = findKthLargestQuickSelect(nums10.clone(), 500);
        long sequentialTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        int parallelResult = ParallelQuickSelect.findKthLargestForkJoin(nums10.clone(), 500);
        long parallelTime = System.currentTimeMillis() - start;

        System.out.println("Sequential result: " + sequentialResult + " (Time: " + sequentialTime + " ms)");
        System.out.println("Parallel result: " + parallelResult + " (Time: " + parallelTime + " ms)");
        System.out.println("Results match: " + (sequentialResult == parallelResult));

        // Test Case 11: Median of medians
        System.out.println("\n=== Test Case 11: Median of Medians ===");

        int[] nums11 = { 12, 3, 5, 7, 4, 19, 26, 1, 8, 15 };
        int k11 = 4;

        System.out.println("Input: " + Arrays.toString(nums11) + ", k=" + k11);

        int quickSelectResult = findKthLargestQuickSelect(nums11.clone(), k11);
        int medianOfMediansResult = MedianOfMedians.findKthLargestMedianOfMedians(nums11.clone(), k11);

        System.out.println("Quickselect: " + quickSelectResult);
        System.out.println("Median of Medians: " + medianOfMediansResult);
        System.out.println("Results match: " + (quickSelectResult == medianOfMediansResult));

        // Test Case 12: Stress test
        System.out.println("\n=== Test Case 12: Stress Test ===");

        int testCases = 100;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int size = random.nextInt(100) + 1;
            int[] arr = new int[size];
            for (int i = 0; i < size; i++) {
                arr[i] = random.nextInt(1000);
            }

            int k = random.nextInt(size) + 1;

            // Get expected result using sorting
            int[] sorted = arr.clone();
            Arrays.sort(sorted);
            int expected = sorted[size - k];

            // Test our implementation
            int actual = findKthLargest(arr.clone(), k);

            if (actual == expected) {
                passed++;
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Performance comparison
        PerformanceComparison.compareApproaches(new int[] { 3, 2, 1, 5, 6, 4, 7, 8, 9, 10 }, 5, 1000);

        System.out.println("\nKth Largest Element testing completed successfully!");
    }
}
