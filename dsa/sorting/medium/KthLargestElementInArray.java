package sorting.medium;

import java.util.*;

/**
 * LeetCode 215: Kth Largest Element in an Array
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 * 
 * Companies: Facebook, Amazon, Microsoft, Google, Apple, Bloomberg, Uber, Adobe
 * Frequency: Very High (Asked in 1500+ interviews)
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
 * 2. Can you implement this with guaranteed O(n) time complexity?
 * 3. What about finding the kth largest in a stream of numbers?
 * 4. How to handle duplicates when finding kth distinct largest?
 * 5. Can you find multiple kth largest elements efficiently?
 * 6. What about finding kth largest in multiple arrays?
 */
public class KthLargestElementInArray {

    // Approach 1: Min Heap - O(n log k) time, O(k) space
    public static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);

            // Keep only k largest elements
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }

    // Approach 2: Max Heap - O(n + k log n) time, O(n) space
    public static int findKthLargestMaxHeap(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        // Add all elements to max heap
        for (int num : nums) {
            maxHeap.offer(num);
        }

        // Extract k-1 largest elements
        for (int i = 0; i < k - 1; i++) {
            maxHeap.poll();
        }

        return maxHeap.poll();
    }

    // Approach 3: Quick Select (Optimal) - O(n) average, O(n^2) worst case, O(1)
    // space
    public static int findKthLargestQuickSelect(int[] nums, int k) {
        // Convert to 0-indexed (kth largest = n-k index in sorted array)
        return quickSelect(nums, 0, nums.length - 1, nums.length - k);
    }

    private static int quickSelect(int[] nums, int left, int right, int targetIndex) {
        if (left == right) {
            return nums[left];
        }

        // Random pivot for better average performance
        Random random = new Random();
        int pivotIndex = left + random.nextInt(right - left + 1);

        int partitionIndex = partition(nums, left, right, pivotIndex);

        if (partitionIndex == targetIndex) {
            return nums[partitionIndex];
        } else if (partitionIndex < targetIndex) {
            return quickSelect(nums, partitionIndex + 1, right, targetIndex);
        } else {
            return quickSelect(nums, left, partitionIndex - 1, targetIndex);
        }
    }

    private static int partition(int[] nums, int left, int right, int pivotIndex) {
        int pivotValue = nums[pivotIndex];

        // Move pivot to end
        swap(nums, pivotIndex, right);

        int storeIndex = left;

        // Move all smaller elements to left
        for (int i = left; i < right; i++) {
            if (nums[i] < pivotValue) {
                swap(nums, i, storeIndex);
                storeIndex++;
            }
        }

        // Move pivot to final position
        swap(nums, storeIndex, right);

        return storeIndex;
    }

    // Approach 4: Quick Select with 3-way partitioning (handles duplicates better)
    public static int findKthLargestThreeWayPartition(int[] nums, int k) {
        return quickSelectThreeWay(nums, 0, nums.length - 1, nums.length - k);
    }

    private static int quickSelectThreeWay(int[] nums, int left, int right, int targetIndex) {
        if (left >= right) {
            return nums[left];
        }

        Random random = new Random();
        int pivotIndex = left + random.nextInt(right - left + 1);
        int pivotValue = nums[pivotIndex];

        // Three-way partitioning
        int lt = left; // nums[left..lt-1] < pivot
        int gt = right; // nums[gt+1..right] > pivot
        int i = left; // nums[lt..i-1] == pivot, nums[i..gt] unknown

        while (i <= gt) {
            if (nums[i] < pivotValue) {
                swap(nums, lt++, i++);
            } else if (nums[i] > pivotValue) {
                swap(nums, i, gt--);
            } else {
                i++;
            }
        }

        // Now: nums[left..lt-1] < pivot, nums[lt..gt] == pivot, nums[gt+1..right] >
        // pivot

        if (targetIndex < lt) {
            return quickSelectThreeWay(nums, left, lt - 1, targetIndex);
        } else if (targetIndex > gt) {
            return quickSelectThreeWay(nums, gt + 1, right, targetIndex);
        } else {
            return pivotValue; // targetIndex is in the equal section
        }
    }

    // Approach 5: Sorting (baseline) - O(n log n) time, O(1) space
    public static int findKthLargestSort(int[] nums, int k) {
        Arrays.sort(nums);
        return nums[nums.length - k];
    }

    // Approach 6: Counting Sort (for limited range) - O(n + range) time, O(range)
    // space
    public static int findKthLargestCountingSort(int[] nums, int k) {
        // Find range
        int min = Arrays.stream(nums).min().orElse(0);
        int max = Arrays.stream(nums).max().orElse(0);

        // Count frequencies
        int[] count = new int[max - min + 1];
        for (int num : nums) {
            count[num - min]++;
        }

        // Find kth largest
        int remaining = k;
        for (int i = count.length - 1; i >= 0; i--) {
            remaining -= count[i];
            if (remaining <= 0) {
                return i + min;
            }
        }

        throw new IllegalArgumentException("Invalid k");
    }

    // Follow-up 1: Find kth smallest element
    public static class KthSmallest {

        public static int findKthSmallest(int[] nums, int k) {
            PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

            for (int num : nums) {
                maxHeap.offer(num);

                if (maxHeap.size() > k) {
                    maxHeap.poll();
                }
            }

            return maxHeap.peek();
        }

        public static int findKthSmallestQuickSelect(int[] nums, int k) {
            return quickSelect(nums, 0, nums.length - 1, k - 1);
        }

        private static int quickSelect(int[] nums, int left, int right, int targetIndex) {
            if (left == right) {
                return nums[left];
            }

            Random random = new Random();
            int pivotIndex = left + random.nextInt(right - left + 1);

            int partitionIndex = partition(nums, left, right, pivotIndex);

            if (partitionIndex == targetIndex) {
                return nums[partitionIndex];
            } else if (partitionIndex < targetIndex) {
                return quickSelect(nums, partitionIndex + 1, right, targetIndex);
            } else {
                return quickSelect(nums, left, partitionIndex - 1, targetIndex);
            }
        }

        private static int partition(int[] nums, int left, int right, int pivotIndex) {
            int pivotValue = nums[pivotIndex];
            swap(nums, pivotIndex, right);

            int storeIndex = left;
            for (int i = left; i < right; i++) {
                if (nums[i] < pivotValue) {
                    swap(nums, i, storeIndex);
                    storeIndex++;
                }
            }

            swap(nums, storeIndex, right);
            return storeIndex;
        }
    }

    // Follow-up 2: Guaranteed O(n) worst-case using Median of Medians
    public static class MedianOfMedians {

        public static int findKthLargestLinear(int[] nums, int k) {
            return quickSelectLinear(nums, 0, nums.length - 1, nums.length - k);
        }

        private static int quickSelectLinear(int[] nums, int left, int right, int targetIndex) {
            if (left == right) {
                return nums[left];
            }

            // Find median of medians as pivot
            int pivotIndex = medianOfMedians(nums, left, right);

            int partitionIndex = partition(nums, left, right, pivotIndex);

            if (partitionIndex == targetIndex) {
                return nums[partitionIndex];
            } else if (partitionIndex < targetIndex) {
                return quickSelectLinear(nums, partitionIndex + 1, right, targetIndex);
            } else {
                return quickSelectLinear(nums, left, partitionIndex - 1, targetIndex);
            }
        }

        private static int medianOfMedians(int[] nums, int left, int right) {
            int n = right - left + 1;

            if (n <= 5) {
                // Sort small array and return median
                Arrays.sort(nums, left, right + 1);
                return left + n / 2;
            }

            // Divide into groups of 5 and find medians
            int numGroups = (n + 4) / 5;
            int[] medians = new int[numGroups];

            for (int i = 0; i < numGroups; i++) {
                int groupLeft = left + i * 5;
                int groupRight = Math.min(groupLeft + 4, right);

                // Sort group and find median
                Arrays.sort(nums, groupLeft, groupRight + 1);
                medians[i] = nums[groupLeft + (groupRight - groupLeft) / 2];
            }

            // Recursively find median of medians
            int medianOfMedians = quickSelectLinear(medians, 0, numGroups - 1, numGroups / 2);

            // Find index of median of medians in original array
            for (int i = left; i <= right; i++) {
                if (nums[i] == medianOfMedians) {
                    return i;
                }
            }

            return left; // Should not reach here
        }

        private static int partition(int[] nums, int left, int right, int pivotIndex) {
            int pivotValue = nums[pivotIndex];
            swap(nums, pivotIndex, right);

            int storeIndex = left;
            for (int i = left; i < right; i++) {
                if (nums[i] < pivotValue) {
                    swap(nums, i, storeIndex);
                    storeIndex++;
                }
            }

            swap(nums, storeIndex, right);
            return storeIndex;
        }
    }

    // Follow-up 3: Kth largest in a stream
    public static class KthLargestInStream {
        private PriorityQueue<Integer> minHeap;
        private int k;

        public KthLargestInStream(int k, int[] nums) {
            this.k = k;
            this.minHeap = new PriorityQueue<>();

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

        // Alternative: Self-balancing BST approach
        public static class KthLargestBST {
            private TreeMap<Integer, Integer> treeMap;
            private int k;
            private int size;

            public KthLargestBST(int k, int[] nums) {
                this.k = k;
                this.treeMap = new TreeMap<>();
                this.size = 0;

                for (int num : nums) {
                    add(num);
                }
            }

            public int add(int val) {
                treeMap.put(val, treeMap.getOrDefault(val, 0) + 1);
                size++;

                // Remove smallest elements if size > k
                while (size > k) {
                    int smallest = treeMap.firstKey();
                    int count = treeMap.get(smallest);

                    if (count == 1) {
                        treeMap.remove(smallest);
                    } else {
                        treeMap.put(smallest, count - 1);
                    }
                    size--;
                }

                return treeMap.firstKey(); // Smallest in k largest elements
            }
        }
    }

    // Follow-up 4: Kth distinct largest element
    public static class KthDistinctLargest {

        public static int findKthDistinctLargest(int[] nums, int k) {
            Set<Integer> uniqueSet = new HashSet<>();
            for (int num : nums) {
                uniqueSet.add(num);
            }

            Integer[] uniqueArray = uniqueSet.toArray(new Integer[0]);
            Arrays.sort(uniqueArray, (a, b) -> b - a);

            if (k > uniqueArray.length) {
                throw new IllegalArgumentException("Not enough distinct elements");
            }

            return uniqueArray[k - 1];
        }

        public static int findKthDistinctLargestHeap(int[] nums, int k) {
            Set<Integer> seen = new HashSet<>();
            PriorityQueue<Integer> minHeap = new PriorityQueue<>();

            for (int num : nums) {
                if (!seen.contains(num)) {
                    seen.add(num);
                    minHeap.offer(num);

                    if (minHeap.size() > k) {
                        minHeap.poll();
                    }
                }
            }

            if (minHeap.size() < k) {
                throw new IllegalArgumentException("Not enough distinct elements");
            }

            return minHeap.peek();
        }

        // Count distinct elements greater than or equal to target
        public static int countDistinctGreaterEqual(int[] nums, int target) {
            Set<Integer> distinct = new HashSet<>();

            for (int num : nums) {
                if (num >= target) {
                    distinct.add(num);
                }
            }

            return distinct.size();
        }
    }

    // Follow-up 5: Multiple kth largest elements efficiently
    public static class MultipleKthLargest {

        public static int[] findMultipleKthLargest(int[] nums, int[] ks) {
            // Sort ks in descending order with original indices
            Integer[] indices = new Integer[ks.length];
            for (int i = 0; i < ks.length; i++) {
                indices[i] = i;
            }

            Arrays.sort(indices, (a, b) -> Integer.compare(ks[b], ks[a]));

            int[] results = new int[ks.length];
            PriorityQueue<Integer> minHeap = new PriorityQueue<>();

            // Process largest k first
            for (int idx : indices) {
                int k = ks[idx];

                // Expand heap if needed
                while (minHeap.size() < k) {
                    for (int num : nums) {
                        if (minHeap.size() < k) {
                            minHeap.offer(num);
                        } else if (num > minHeap.peek()) {
                            minHeap.poll();
                            minHeap.offer(num);
                        }
                    }
                    break;
                }

                // Contract heap if needed
                while (minHeap.size() > k) {
                    minHeap.poll();
                }

                results[idx] = minHeap.peek();
            }

            return results;
        }

        // Alternative: Sort once and answer multiple queries
        public static int[] findMultipleKthLargestSorted(int[] nums, int[] ks) {
            int[] sortedNums = nums.clone();
            Arrays.sort(sortedNums);

            int[] results = new int[ks.length];

            for (int i = 0; i < ks.length; i++) {
                if (ks[i] < 1 || ks[i] > nums.length) {
                    throw new IllegalArgumentException("Invalid k: " + ks[i]);
                }
                results[i] = sortedNums[nums.length - ks[i]];
            }

            return results;
        }
    }

    // Follow-up 6: Kth largest in multiple arrays
    public static class KthLargestMultipleArrays {

        public static int findKthLargestMultipleArrays(int[][] arrays, int k) {
            PriorityQueue<Integer> minHeap = new PriorityQueue<>();

            for (int[] array : arrays) {
                for (int num : array) {
                    minHeap.offer(num);

                    if (minHeap.size() > k) {
                        minHeap.poll();
                    }
                }
            }

            if (minHeap.size() < k) {
                throw new IllegalArgumentException("Not enough elements");
            }

            return minHeap.peek();
        }

        // Merge arrays first, then find kth largest
        public static int findKthLargestMerged(int[][] arrays, int k) {
            List<Integer> merged = new ArrayList<>();

            for (int[] array : arrays) {
                for (int num : array) {
                    merged.add(num);
                }
            }

            return findKthLargest(merged.stream().mapToInt(i -> i).toArray(), k);
        }

        // Using priority queue to merge arrays efficiently
        public static int findKthLargestMergeEfficient(int[][] arrays, int k) {
            // Sort each array in descending order
            for (int[] array : arrays) {
                Arrays.sort(array);
                reverse(array);
            }

            PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
                    (a, b) -> Integer.compare(arrays[b[0]][b[1]], arrays[a[0]][a[1]]));

            // Initialize heap with first element of each array
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i].length > 0) {
                    maxHeap.offer(new int[] { i, 0 }); // {array_index, element_index}
                }
            }

            int count = 0;

            while (!maxHeap.isEmpty() && count < k) {
                int[] current = maxHeap.poll();
                int arrayIndex = current[0];
                int elementIndex = current[1];

                count++;

                if (count == k) {
                    return arrays[arrayIndex][elementIndex];
                }

                // Add next element from same array
                if (elementIndex + 1 < arrays[arrayIndex].length) {
                    maxHeap.offer(new int[] { arrayIndex, elementIndex + 1 });
                }
            }

            throw new IllegalArgumentException("Not enough elements");
        }

        private static void reverse(int[] array) {
            int left = 0, right = array.length - 1;
            while (left < right) {
                int temp = array[left];
                array[left] = array[right];
                array[right] = temp;
                left++;
                right--;
            }
        }
    }

    // Utility methods
    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
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

            // Quick Select
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findKthLargestQuickSelect(nums.clone(), k);
            }
            long quickSelectTime = System.nanoTime() - start;

            // Sorting
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findKthLargestSort(nums.clone(), k);
            }
            long sortTime = System.nanoTime() - start;

            // Counting Sort (if range is reasonable)
            if (nums.length > 0) {
                int min = Arrays.stream(nums).min().orElse(0);
                int max = Arrays.stream(nums).max().orElse(0);

                if (max - min <= 20000) { // Reasonable range
                    start = System.nanoTime();
                    for (int i = 0; i < iterations; i++) {
                        findKthLargestCountingSort(nums.clone(), k);
                    }
                    long countingTime = System.nanoTime() - start;

                    System.out.println("Min Heap: " + minHeapTime / 1_000_000 + " ms");
                    System.out.println("Quick Select: " + quickSelectTime / 1_000_000 + " ms");
                    System.out.println("Sorting: " + sortTime / 1_000_000 + " ms");
                    System.out.println("Counting Sort: " + countingTime / 1_000_000 + " ms");
                } else {
                    System.out.println("Min Heap: " + minHeapTime / 1_000_000 + " ms");
                    System.out.println("Quick Select: " + quickSelectTime / 1_000_000 + " ms");
                    System.out.println("Sorting: " + sortTime / 1_000_000 + " ms");
                    System.out.println("Counting Sort: Skipped (range too large)");
                }
            }
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[] nums1 = { 3, 2, 1, 5, 6, 4 };
        int k1 = 2;

        System.out.println("Array: " + Arrays.toString(nums1));
        System.out.println("k = " + k1);

        System.out.println("Min Heap: " + findKthLargest(nums1.clone(), k1));
        System.out.println("Max Heap: " + findKthLargestMaxHeap(nums1.clone(), k1));
        System.out.println("Quick Select: " + findKthLargestQuickSelect(nums1.clone(), k1));
        System.out.println("Three-way Partition: " + findKthLargestThreeWayPartition(nums1.clone(), k1));
        System.out.println("Sorting: " + findKthLargestSort(nums1.clone(), k1));
        System.out.println("Counting Sort: " + findKthLargestCountingSort(nums1.clone(), k1));

        // Test Case 2: Edge cases
        System.out.println("\n=== Test Case 2: Edge Cases ===");

        // Single element
        int[] single = { 1 };
        System.out.println("Single element [1], k=1: " + findKthLargest(single.clone(), 1));

        // All same elements
        int[] same = { 1, 1, 1, 1, 1 };
        System.out.println("All same [1,1,1,1,1], k=3: " + findKthLargest(same.clone(), 3));

        // k = 1 (largest element)
        int[] test3 = { 3, 2, 3, 1, 2, 4, 5, 5, 6 };
        System.out.println("Array: " + Arrays.toString(test3) + ", k=1: " + findKthLargest(test3.clone(), 1));

        // k = length (smallest element)
        System.out.println("Array: " + Arrays.toString(test3) + ", k=" + test3.length + ": " +
                findKthLargest(test3.clone(), test3.length));

        // Test Case 3: Kth smallest
        System.out.println("\n=== Test Case 3: Kth Smallest ===");

        int[] nums3 = { 3, 2, 1, 5, 6, 4 };
        int k3 = 2;

        System.out.println("Array: " + Arrays.toString(nums3));
        System.out.println("2nd smallest (heap): " + KthSmallest.findKthSmallest(nums3.clone(), k3));
        System.out.println("2nd smallest (quick select): " + KthSmallest.findKthSmallestQuickSelect(nums3.clone(), k3));

        // Test Case 4: Median of Medians (guaranteed O(n))
        System.out.println("\n=== Test Case 4: Median of Medians ===");

        int[] nums4 = { 3, 2, 1, 5, 6, 4, 7, 8, 9, 10, 11, 12 };
        System.out.println("Array: " + Arrays.toString(nums4));
        System.out.println("5th largest (linear worst-case): " +
                MedianOfMedians.findKthLargestLinear(nums4.clone(), 5));

        // Test Case 5: Kth largest in stream
        System.out.println("\n=== Test Case 5: Kth Largest in Stream ===");

        KthLargestInStream stream = new KthLargestInStream(3, new int[] { 4, 5, 8, 2 });
        System.out.println("Initial 3rd largest: " + stream.add(0)); // Should return 4
        System.out.println("After adding 3: " + stream.add(3)); // Should return 4
        System.out.println("After adding 5: " + stream.add(5)); // Should return 5
        System.out.println("After adding 10: " + stream.add(10)); // Should return 5
        System.out.println("After adding 9: " + stream.add(9)); // Should return 8
        System.out.println("After adding 4: " + stream.add(4)); // Should return 8

        // Test BST approach
        KthLargestInStream.KthLargestBST bstStream = new KthLargestInStream.KthLargestBST(3, new int[] { 4, 5, 8, 2 });
        System.out.println("BST - After adding 3: " + bstStream.add(3));
        System.out.println("BST - After adding 10: " + bstStream.add(10));

        // Test Case 6: Kth distinct largest
        System.out.println("\n=== Test Case 6: Kth Distinct Largest ===");

        int[] nums6 = { 3, 2, 3, 1, 2, 4, 5, 5, 6 };
        System.out.println("Array with duplicates: " + Arrays.toString(nums6));
        System.out.println("3rd distinct largest: " + KthDistinctLargest.findKthDistinctLargest(nums6, 3));
        System.out.println("3rd distinct largest (heap): " + KthDistinctLargest.findKthDistinctLargestHeap(nums6, 3));
        System.out.println("Count distinct >= 4: " + KthDistinctLargest.countDistinctGreaterEqual(nums6, 4));

        // Test Case 7: Multiple kth largest queries
        System.out.println("\n=== Test Case 7: Multiple Kth Largest Queries ===");

        int[] nums7 = { 3, 2, 1, 5, 6, 4 };
        int[] ks = { 1, 2, 3, 4, 5, 6 };

        System.out.println("Array: " + Arrays.toString(nums7));
        System.out.println("Queries k: " + Arrays.toString(ks));

        int[] results = MultipleKthLargest.findMultipleKthLargestSorted(nums7, ks);
        System.out.println("Results: " + Arrays.toString(results));

        // Test Case 8: Kth largest in multiple arrays
        System.out.println("\n=== Test Case 8: Kth Largest in Multiple Arrays ===");

        int[][] arrays = {
                { 1, 4, 7, 10 },
                { 2, 5, 8, 11 },
                { 3, 6, 9, 12 }
        };

        System.out.println("Arrays:");
        for (int[] arr : arrays) {
            System.out.println("  " + Arrays.toString(arr));
        }

        System.out.println("5th largest across all arrays: " +
                KthLargestMultipleArrays.findKthLargestMultipleArrays(arrays, 5));
        System.out.println("5th largest (merged): " +
                KthLargestMultipleArrays.findKthLargestMerged(arrays, 5));
        System.out.println("5th largest (efficient): " +
                KthLargestMultipleArrays.findKthLargestMergeEfficient(arrays, 5));

        // Test Case 9: Large array performance
        System.out.println("\n=== Test Case 9: Large Array Performance ===");

        Random random = new Random(42);
        int size = 10000;
        int[] largeArray = new int[size];

        for (int i = 0; i < size; i++) {
            largeArray[i] = random.nextInt(10000);
        }

        int k = size / 4; // 25th percentile

        long start = System.currentTimeMillis();
        int result = findKthLargestQuickSelect(largeArray.clone(), k);
        long end = System.currentTimeMillis();

        System.out.println("Large array size: " + size + ", k=" + k);
        System.out.println("Result: " + result + ", Time: " + (end - start) + " ms");

        // Test Case 10: Stress test
        System.out.println("\n=== Test Case 10: Stress Test ===");

        int testCases = 100;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int n = random.nextInt(20) + 1;
            int[] testArray = new int[n];

            for (int i = 0; i < n; i++) {
                testArray[i] = random.nextInt(100);
            }

            int testK = random.nextInt(n) + 1;

            // Test all approaches
            int heap = findKthLargest(testArray.clone(), testK);
            int quickSelect = findKthLargestQuickSelect(testArray.clone(), testK);
            int sort = findKthLargestSort(testArray.clone(), testK);

            if (heap == quickSelect && quickSelect == sort) {
                passed++;
            } else {
                System.out.println("Failed test: " + Arrays.toString(testArray) + ", k=" + testK);
                System.out.println("Heap: " + heap + ", QuickSelect: " + quickSelect + ", Sort: " + sort);
                break;
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Performance comparison
        int[] perfArray = new int[1000];
        for (int i = 0; i < perfArray.length; i++) {
            perfArray[i] = random.nextInt(1000);
        }

        PerformanceComparison.compareApproaches(perfArray, 100, 1000);

        System.out.println("\nKth Largest Element testing completed successfully!");
    }
}
