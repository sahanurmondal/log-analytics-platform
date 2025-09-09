package heap.medium;

import java.util.*;

/**
 * LeetCode 215: Kth Largest Element in an Array
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 * 
 * Companies: Amazon, Meta, Google, Microsoft, Apple, ByteDance
 * Frequency: Very High (Asked in 500+ interviews)
 *
 * Description:
 * Given an integer array nums and an integer k, return the kth largest element
 * in the array.
 * Note that it is the kth largest element in the sorted order, not the kth
 * distinct element.
 * 
 * You must solve it in O(n) time complexity.
 *
 * Constraints:
 * - 1 <= k <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you find the kth smallest element instead?
 * 2. What if we need to handle stream of numbers?
 * 3. Can you find all elements >= kth largest?
 * 4. What if array is mostly sorted?
 */
public class KthLargestElementInArray {

    // Approach 1: QuickSelect (Hoare's Selection) - O(n) average, O(n²) worst, O(1)
    // space
    public int findKthLargest(int[] nums, int k) {
        return quickSelect(nums, 0, nums.length - 1, nums.length - k);
    }

    private int quickSelect(int[] nums, int left, int right, int kIndex) {
        if (left == right)
            return nums[left];

        Random random = new Random();
        int pivotIndex = left + random.nextInt(right - left + 1);

        pivotIndex = partition(nums, left, right, pivotIndex);

        if (kIndex == pivotIndex) {
            return nums[kIndex];
        } else if (kIndex < pivotIndex) {
            return quickSelect(nums, left, pivotIndex - 1, kIndex);
        } else {
            return quickSelect(nums, pivotIndex + 1, right, kIndex);
        }
    }

    private int partition(int[] nums, int left, int right, int pivotIndex) {
        int pivotValue = nums[pivotIndex];

        // Move pivot to end
        swap(nums, pivotIndex, right);

        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (nums[i] < pivotValue) {
                swap(nums, storeIndex, i);
                storeIndex++;
            }
        }

        // Move pivot to its final place
        swap(nums, storeIndex, right);
        return storeIndex;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    // Approach 2: Min Heap - O(n log k) time, O(k) space
    public int findKthLargestHeap(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }

    // Approach 3: Sorting - O(n log n) time, O(1) space
    public int findKthLargestSort(int[] nums, int k) {
        Arrays.sort(nums);
        return nums[nums.length - k];
    }

    // Approach 4: Counting Sort (when range is limited) - O(n + range) time,
    // O(range) space
    public int findKthLargestCounting(int[] nums, int k) {
        int min = Arrays.stream(nums).min().orElse(0);
        int max = Arrays.stream(nums).max().orElse(0);

        int[] count = new int[max - min + 1];
        for (int num : nums) {
            count[num - min]++;
        }

        int remaining = k;
        for (int i = count.length - 1; i >= 0; i--) {
            remaining -= count[i];
            if (remaining <= 0) {
                return i + min;
            }
        }

        return -1; // Should never reach here
    }

    // Follow-up 1: Kth smallest element
    public int findKthSmallest(int[] nums, int k) {
        return quickSelect(nums, 0, nums.length - 1, k - 1);
    }

    // Follow-up 2: Stream of numbers (data structure design)
    static class KthLargestStream {
        private final PriorityQueue<Integer> minHeap;
        private final int k;

        public KthLargestStream(int k, int[] nums) {
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

        public int getKthLargest() {
            return minHeap.peek();
        }
    }

    // Follow-up 3: Find all elements >= kth largest
    public List<Integer> findElementsGeqKthLargest(int[] nums, int k) {
        int kthLargest = findKthLargest(nums.clone(), k);
        List<Integer> result = new ArrayList<>();

        for (int num : nums) {
            if (num >= kthLargest) {
                result.add(num);
            }
        }

        return result;
    }

    // Follow-up 4: Optimized for mostly sorted arrays
    public int findKthLargestAdaptive(int[] nums, int k) {
        // Check if array is nearly sorted
        if (isNearlySorted(nums)) {
            return findKthLargestSort(nums, k);
        } else {
            return findKthLargest(nums, k);
        }
    }

    private boolean isNearlySorted(int[] nums) {
        int inversions = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                inversions++;
                if (inversions > nums.length / 10) { // More than 10% inversions
                    return false;
                }
            }
        }
        return true;
    }

    // Advanced: Median of Medians for guaranteed O(n) worst case
    public int findKthLargestMedianOfMedians(int[] nums, int k) {
        return medianOfMediansSelect(nums, 0, nums.length - 1, nums.length - k);
    }

    private int medianOfMediansSelect(int[] nums, int left, int right, int kIndex) {
        if (left == right)
            return nums[left];

        int pivotIndex = medianOfMedians(nums, left, right);
        pivotIndex = partition(nums, left, right, pivotIndex);

        if (kIndex == pivotIndex) {
            return nums[kIndex];
        } else if (kIndex < pivotIndex) {
            return medianOfMediansSelect(nums, left, pivotIndex - 1, kIndex);
        } else {
            return medianOfMediansSelect(nums, pivotIndex + 1, right, kIndex);
        }
    }

    private int medianOfMedians(int[] nums, int left, int right) {
        int n = right - left + 1;
        if (n <= 5) {
            Arrays.sort(nums, left, right + 1);
            return left + n / 2;
        }

        // Divide into groups of 5 and find median of each group
        for (int i = left; i <= right; i += 5) {
            int subRight = Math.min(i + 4, right);
            Arrays.sort(nums, i, subRight + 1);
            int median = i + (subRight - i) / 2;

            // Move median to front part
            swap(nums, median, left + (i - left) / 5);
        }

        // Find median of medians recursively
        return medianOfMediansSelect(nums, left, left + (n - 1) / 5, left + (n - 1) / 10);
    }

    // Helper: Performance comparison
    public Map<String, Long> comparePerformance(int[] nums, int k) {
        Map<String, Long> results = new HashMap<>();

        // QuickSelect
        int[] nums1 = nums.clone();
        long start = System.nanoTime();
        findKthLargest(nums1, k);
        results.put("QuickSelect", System.nanoTime() - start);

        // Heap
        int[] nums2 = nums.clone();
        start = System.nanoTime();
        findKthLargestHeap(nums2, k);
        results.put("Heap", System.nanoTime() - start);

        // Sorting
        int[] nums3 = nums.clone();
        start = System.nanoTime();
        findKthLargestSort(nums3, k);
        results.put("Sorting", System.nanoTime() - start);

        // Counting Sort (if applicable)
        if (isCountingSortApplicable(nums)) {
            int[] nums4 = nums.clone();
            start = System.nanoTime();
            findKthLargestCounting(nums4, k);
            results.put("CountingSort", System.nanoTime() - start);
        }

        return results;
    }

    private boolean isCountingSortApplicable(int[] nums) {
        int min = Arrays.stream(nums).min().orElse(0);
        int max = Arrays.stream(nums).max().orElse(0);
        return (max - min) <= 10000; // Reasonable range
    }

    // Helper: Validate result across all methods
    public boolean validateAllMethods(int[] nums, int k) {
        int result1 = findKthLargest(nums.clone(), k);
        int result2 = findKthLargestHeap(nums.clone(), k);
        int result3 = findKthLargestSort(nums.clone(), k);

        return result1 == result2 && result2 == result3;
    }

    public static void main(String[] args) {
        KthLargestElementInArray solution = new KthLargestElementInArray();

        // Test Case 1: Standard case
        int[] nums1 = { 3, 2, 1, 5, 6, 4 };
        int k1 = 2;

        int result1 = solution.findKthLargest(nums1.clone(), k1);
        System.out.println("Test 1 - Kth largest: " + result1); // Expected: 5

        // Validate all methods give same result
        System.out.println("All methods consistent: " + solution.validateAllMethods(nums1, k1));

        // Test Case 2: Array with duplicates
        int[] nums2 = { 3, 2, 3, 1, 2, 4, 5, 5, 6 };
        int k2 = 4;
        int result2 = solution.findKthLargest(nums2.clone(), k2);
        System.out.println("Test 2 - With duplicates: " + result2); // Expected: 4

        // Test Case 3: Single element
        int[] nums3 = { 1 };
        int k3 = 1;
        int result3 = solution.findKthLargest(nums3.clone(), k3);
        System.out.println("Test 3 - Single element: " + result3); // Expected: 1

        // Test Case 4: All same elements
        int[] nums4 = { 2, 2, 2, 2 };
        int k4 = 3;
        int result4 = solution.findKthLargest(nums4.clone(), k4);
        System.out.println("Test 4 - All same: " + result4); // Expected: 2

        // Follow-up 1: Kth smallest
        System.out.println("\nFollow-up 1 - Kth smallest:");
        int kthSmallest = solution.findKthSmallest(nums1.clone(), k1);
        System.out.println("2nd smallest in [3,2,1,5,6,4]: " + kthSmallest); // Expected: 2

        // Follow-up 2: Stream
        System.out.println("\nFollow-up 2 - Stream:");
        KthLargestStream stream = new KthLargestStream(3, new int[] { 4, 5, 8, 2 });
        System.out.println("Initial 3rd largest: " + stream.getKthLargest()); // Expected: 4
        System.out.println("After adding 3: " + stream.add(3)); // Expected: 4
        System.out.println("After adding 5: " + stream.add(5)); // Expected: 5
        System.out.println("After adding 10: " + stream.add(10)); // Expected: 5
        System.out.println("After adding 9: " + stream.add(9)); // Expected: 8
        System.out.println("After adding 4: " + stream.add(4)); // Expected: 8

        // Follow-up 3: All elements >= kth largest
        System.out.println("\nFollow-up 3 - Elements >= kth largest:");
        List<Integer> largeElements = solution.findElementsGeqKthLargest(nums1, k1);
        System.out.println("Elements >= 2nd largest: " + largeElements);

        // Performance comparison
        System.out.println("\nPerformance comparison:");
        int[] largeNums = new int[10000];
        Random random = new Random(42);
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = random.nextInt(20000) - 10000;
        }

        Map<String, Long> performance = solution.comparePerformance(largeNums, 1000);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1_000_000.0 + " ms"));

        // Test adaptive approach
        System.out.println("\nTesting adaptive approach:");

        // Nearly sorted array
        int[] nearlySorted = new int[1000];
        for (int i = 0; i < 1000; i++) {
            nearlySorted[i] = i;
        }
        // Add few random swaps
        for (int i = 0; i < 10; i++) {
            int idx1 = random.nextInt(1000);
            int idx2 = random.nextInt(1000);
            int temp = nearlySorted[idx1];
            nearlySorted[idx1] = nearlySorted[idx2];
            nearlySorted[idx2] = temp;
        }

        long start = System.nanoTime();
        int adaptiveResult = solution.findKthLargestAdaptive(nearlySorted.clone(), 100);
        long adaptiveTime = System.nanoTime() - start;

        start = System.nanoTime();
        int quickSelectResult = solution.findKthLargest(nearlySorted.clone(), 100);
        long quickSelectTime = System.nanoTime() - start;

        System.out.println("Adaptive result: " + adaptiveResult);
        System.out.println("QuickSelect result: " + quickSelectResult);
        System.out.println("Results match: " + (adaptiveResult == quickSelectResult));
        System.out.println("Adaptive time: " + adaptiveTime / 1_000_000.0 + " ms");
        System.out.println("QuickSelect time: " + quickSelectTime / 1_000_000.0 + " ms");

        // Test median of medians
        System.out.println("\nTesting Median of Medians:");
        int medianResult = solution.findKthLargestMedianOfMedians(nums1.clone(), k1);
        System.out.println("Median of Medians result: " + medianResult);
        System.out.println("Matches QuickSelect: " + (medianResult == result1));
    }
}
