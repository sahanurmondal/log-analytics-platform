package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 2461: Maximum Sum of Distinct Subarrays With Length K
 * https://leetcode.com/problems/maximum-sum-of-distinct-subarrays-with-length-k/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 400+ interviews)
 *
 * Description:
 * You are given an integer array nums and an integer k. Find the maximum
 * subarray sum
 * of all the subarrays of nums that meet the following conditions:
 * - The length of the subarray is k
 * - All the elements of the subarray are distinct
 * 
 * Return the maximum subarray sum of all the subarrays that meet the
 * conditions.
 * If no subarray meets the conditions, return 0.
 * 
 * Constraints:
 * - 1 <= k <= nums.length <= 10^5
 * - 1 <= nums[i] <= 10^5
 * 
 * Follow-up Questions:
 * 1. How would you find all subarrays with maximum sum?
 * 2. Can you handle the case where k can vary?
 * 3. What about finding minimum sum with distinct elements?
 * 4. How to count total number of valid subarrays?
 * 5. Can you optimize for multiple queries with different k values?
 * 6. What about handling negative numbers?
 */
public class MaximumSumOfDistinctSubarrays {

    // Approach 1: Sliding Window with HashSet - O(n) time, O(k) space
    public static long maximumSubarraySum(int[] nums, int k) {
        if (nums.length < k)
            return 0;

        Set<Integer> window = new HashSet<>();
        long currentSum = 0;
        long maxSum = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            // If duplicate found, shrink window from left
            while (window.contains(nums[right])) {
                window.remove(nums[left]);
                currentSum -= nums[left];
                left++;
            }

            // Add current element
            window.add(nums[right]);
            currentSum += nums[right];

            // Check if window size is k
            if (right - left + 1 == k) {
                maxSum = Math.max(maxSum, currentSum);

                // Remove leftmost element for next iteration
                window.remove(nums[left]);
                currentSum -= nums[left];
                left++;
            } else if (right - left + 1 > k) {
                // This shouldn't happen with correct implementation
                while (right - left + 1 > k) {
                    window.remove(nums[left]);
                    currentSum -= nums[left];
                    left++;
                }
            }
        }

        return maxSum;
    }

    // Approach 2: Sliding Window with HashMap (frequency count) - O(n) time, O(k)
    // space
    public static long maximumSubarraySumHashMap(int[] nums, int k) {
        if (nums.length < k)
            return 0;

        Map<Integer, Integer> freq = new HashMap<>();
        long currentSum = 0;
        long maxSum = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            // Add current element
            freq.put(nums[right], freq.getOrDefault(nums[right], 0) + 1);
            currentSum += nums[right];

            // Maintain window size
            if (right - left + 1 > k) {
                int leftVal = nums[left];
                freq.put(leftVal, freq.get(leftVal) - 1);
                if (freq.get(leftVal) == 0) {
                    freq.remove(leftVal);
                }
                currentSum -= leftVal;
                left++;
            }

            // Check if all elements are distinct and window size is k
            if (right - left + 1 == k && freq.size() == k) {
                maxSum = Math.max(maxSum, currentSum);
            }
        }

        return maxSum;
    }

    // Approach 3: Optimized with early termination
    public static long maximumSubarraySumOptimized(int[] nums, int k) {
        if (nums.length < k)
            return 0;

        Map<Integer, Integer> lastIndex = new HashMap<>();
        long currentSum = 0;
        long maxSum = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            // If element seen before and within current window
            if (lastIndex.containsKey(nums[right]) && lastIndex.get(nums[right]) >= left) {
                // Move left pointer to after the duplicate
                int duplicateIndex = lastIndex.get(nums[right]);

                // Remove elements from sum
                for (int i = left; i <= duplicateIndex; i++) {
                    currentSum -= nums[i];
                }
                left = duplicateIndex + 1;
            }

            lastIndex.put(nums[right], right);
            currentSum += nums[right];

            // Check if window has exactly k elements
            if (right - left + 1 == k) {
                maxSum = Math.max(maxSum, currentSum);

                // Remove leftmost element
                currentSum -= nums[left];
                left++;
            } else if (right - left + 1 > k) {
                // Remove excess elements from left
                while (right - left + 1 > k) {
                    currentSum -= nums[left];
                    left++;
                }
            }
        }

        return maxSum;
    }

    // Follow-up 1: Find all subarrays with maximum sum
    public static class FindAllMaximumSubarrays {

        public static class SubarrayInfo {
            int start;
            int end;
            long sum;
            List<Integer> elements;

            public SubarrayInfo(int start, int end, long sum, List<Integer> elements) {
                this.start = start;
                this.end = end;
                this.sum = sum;
                this.elements = new ArrayList<>(elements);
            }

            @Override
            public String toString() {
                return String.format("[%d,%d]: sum=%d, elements=%s", start, end, sum, elements);
            }
        }

        public static List<SubarrayInfo> findAllMaximumSubarrays(int[] nums, int k) {
            List<SubarrayInfo> result = new ArrayList<>();
            long maxSum = maximumSubarraySum(nums, k);

            if (maxSum == 0)
                return result;

            Map<Integer, Integer> freq = new HashMap<>();
            long currentSum = 0;
            int left = 0;

            for (int right = 0; right < nums.length; right++) {
                freq.put(nums[right], freq.getOrDefault(nums[right], 0) + 1);
                currentSum += nums[right];

                if (right - left + 1 > k) {
                    int leftVal = nums[left];
                    freq.put(leftVal, freq.get(leftVal) - 1);
                    if (freq.get(leftVal) == 0) {
                        freq.remove(leftVal);
                    }
                    currentSum -= leftVal;
                    left++;
                }

                if (right - left + 1 == k && freq.size() == k && currentSum == maxSum) {
                    List<Integer> elements = new ArrayList<>();
                    for (int i = left; i <= right; i++) {
                        elements.add(nums[i]);
                    }
                    result.add(new SubarrayInfo(left, right, currentSum, elements));
                }
            }

            return result;
        }

        public static List<int[]> findAllMaximumSubarrayIndices(int[] nums, int k) {
            List<int[]> result = new ArrayList<>();
            long maxSum = maximumSubarraySum(nums, k);

            if (maxSum == 0)
                return result;

            Map<Integer, Integer> freq = new HashMap<>();
            long currentSum = 0;
            int left = 0;

            for (int right = 0; right < nums.length; right++) {
                freq.put(nums[right], freq.getOrDefault(nums[right], 0) + 1);
                currentSum += nums[right];

                if (right - left + 1 > k) {
                    int leftVal = nums[left];
                    freq.put(leftVal, freq.get(leftVal) - 1);
                    if (freq.get(leftVal) == 0) {
                        freq.remove(leftVal);
                    }
                    currentSum -= leftVal;
                    left++;
                }

                if (right - left + 1 == k && freq.size() == k && currentSum == maxSum) {
                    result.add(new int[] { left, right });
                }
            }

            return result;
        }
    }

    // Follow-up 2: Variable k
    public static class VariableK {

        public static Map<Integer, Long> findMaxSumForAllK(int[] nums, int maxK) {
            Map<Integer, Long> results = new HashMap<>();

            for (int k = 1; k <= Math.min(maxK, nums.length); k++) {
                results.put(k, maximumSubarraySum(nums, k));
            }

            return results;
        }

        public static long findMaxSumAnyK(int[] nums, int minK, int maxK) {
            long maxSum = 0;

            for (int k = minK; k <= Math.min(maxK, nums.length); k++) {
                maxSum = Math.max(maxSum, maximumSubarraySum(nums, k));
            }

            return maxSum;
        }

        // Find the k that gives maximum sum
        public static int[] findBestK(int[] nums, int maxK) {
            long maxSum = 0;
            int bestK = 0;

            for (int k = 1; k <= Math.min(maxK, nums.length); k++) {
                long sum = maximumSubarraySum(nums, k);
                if (sum > maxSum) {
                    maxSum = sum;
                    bestK = k;
                }
            }

            return new int[] { bestK, (int) maxSum };
        }
    }

    // Follow-up 3: Minimum sum with distinct elements
    public static class MinimumSum {

        public static long minimumSubarraySum(int[] nums, int k) {
            if (nums.length < k)
                return 0;

            Map<Integer, Integer> freq = new HashMap<>();
            long currentSum = 0;
            long minSum = Long.MAX_VALUE;
            int left = 0;
            boolean foundValid = false;

            for (int right = 0; right < nums.length; right++) {
                freq.put(nums[right], freq.getOrDefault(nums[right], 0) + 1);
                currentSum += nums[right];

                if (right - left + 1 > k) {
                    int leftVal = nums[left];
                    freq.put(leftVal, freq.get(leftVal) - 1);
                    if (freq.get(leftVal) == 0) {
                        freq.remove(leftVal);
                    }
                    currentSum -= leftVal;
                    left++;
                }

                if (right - left + 1 == k && freq.size() == k) {
                    minSum = Math.min(minSum, currentSum);
                    foundValid = true;
                }
            }

            return foundValid ? minSum : 0;
        }

        public static long minimumSubarraySumPositive(int[] nums, int k) {
            // Only consider positive sums
            long minSum = minimumSubarraySum(nums, k);
            return minSum > 0 ? minSum : 0;
        }
    }

    // Follow-up 4: Count valid subarrays
    public static class CountValidSubarrays {

        public static int countDistinctSubarrays(int[] nums, int k) {
            if (nums.length < k)
                return 0;

            Map<Integer, Integer> freq = new HashMap<>();
            int count = 0;
            int left = 0;

            for (int right = 0; right < nums.length; right++) {
                freq.put(nums[right], freq.getOrDefault(nums[right], 0) + 1);

                if (right - left + 1 > k) {
                    int leftVal = nums[left];
                    freq.put(leftVal, freq.get(leftVal) - 1);
                    if (freq.get(leftVal) == 0) {
                        freq.remove(leftVal);
                    }
                    left++;
                }

                if (right - left + 1 == k && freq.size() == k) {
                    count++;
                }
            }

            return count;
        }

        public static Map<Long, Integer> countSubarraysBySum(int[] nums, int k) {
            Map<Long, Integer> sumCounts = new HashMap<>();

            if (nums.length < k)
                return sumCounts;

            Map<Integer, Integer> freq = new HashMap<>();
            long currentSum = 0;
            int left = 0;

            for (int right = 0; right < nums.length; right++) {
                freq.put(nums[right], freq.getOrDefault(nums[right], 0) + 1);
                currentSum += nums[right];

                if (right - left + 1 > k) {
                    int leftVal = nums[left];
                    freq.put(leftVal, freq.get(leftVal) - 1);
                    if (freq.get(leftVal) == 0) {
                        freq.remove(leftVal);
                    }
                    currentSum -= leftVal;
                    left++;
                }

                if (right - left + 1 == k && freq.size() == k) {
                    sumCounts.put(currentSum, sumCounts.getOrDefault(currentSum, 0) + 1);
                }
            }

            return sumCounts;
        }
    }

    // Follow-up 5: Multiple queries optimization
    public static class MultipleQueriesOptimizer {
        private int[] nums;
        private Map<Integer, Long> cache;

        public MultipleQueriesOptimizer(int[] nums) {
            this.nums = nums.clone();
            this.cache = new HashMap<>();
        }

        public long getMaximumSum(int k) {
            if (cache.containsKey(k)) {
                return cache.get(k);
            }

            long result = maximumSubarraySum(nums, k);
            cache.put(k, result);
            return result;
        }

        public void updateArray(int[] newNums) {
            this.nums = newNums.clone();
            this.cache.clear();
        }

        public Map<Integer, Long> precomputeResults(int maxK) {
            for (int k = 1; k <= Math.min(maxK, nums.length); k++) {
                getMaximumSum(k);
            }
            return new HashMap<>(cache);
        }

        public void clearCache() {
            cache.clear();
        }
    }

    // Follow-up 6: Handle negative numbers
    public static class HandleNegativeNumbers {

        public static long maximumSubarraySumWithNegatives(int[] nums, int k) {
            if (nums.length < k)
                return 0;

            Map<Integer, Integer> freq = new HashMap<>();
            long currentSum = 0;
            long maxSum = Long.MIN_VALUE;
            int left = 0;
            boolean foundValid = false;

            for (int right = 0; right < nums.length; right++) {
                freq.put(nums[right], freq.getOrDefault(nums[right], 0) + 1);
                currentSum += nums[right];

                if (right - left + 1 > k) {
                    int leftVal = nums[left];
                    freq.put(leftVal, freq.get(leftVal) - 1);
                    if (freq.get(leftVal) == 0) {
                        freq.remove(leftVal);
                    }
                    currentSum -= leftVal;
                    left++;
                }

                if (right - left + 1 == k && freq.size() == k) {
                    maxSum = Math.max(maxSum, currentSum);
                    foundValid = true;
                }
            }

            return foundValid ? maxSum : 0;
        }

        public static long maximumPositiveSum(int[] nums, int k) {
            long maxSum = maximumSubarraySumWithNegatives(nums, k);
            return Math.max(0, maxSum);
        }

        // Find subarray with sum closest to target
        public static long findSumClosestToTarget(int[] nums, int k, long target) {
            if (nums.length < k)
                return 0;

            Map<Integer, Integer> freq = new HashMap<>();
            long currentSum = 0;
            long closestSum = Long.MAX_VALUE;
            long minDifference = Long.MAX_VALUE;
            int left = 0;

            for (int right = 0; right < nums.length; right++) {
                freq.put(nums[right], freq.getOrDefault(nums[right], 0) + 1);
                currentSum += nums[right];

                if (right - left + 1 > k) {
                    int leftVal = nums[left];
                    freq.put(leftVal, freq.get(leftVal) - 1);
                    if (freq.get(leftVal) == 0) {
                        freq.remove(leftVal);
                    }
                    currentSum -= leftVal;
                    left++;
                }

                if (right - left + 1 == k && freq.size() == k) {
                    long difference = Math.abs(currentSum - target);
                    if (difference < minDifference) {
                        minDifference = difference;
                        closestSum = currentSum;
                    }
                }
            }

            return closestSum == Long.MAX_VALUE ? 0 : closestSum;
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(int[] nums, int k, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Array length: " + nums.length + ", k=" + k + ", Iterations: " + iterations);

            // HashSet approach
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                maximumSubarraySum(nums, k);
            }
            long hashSetTime = System.nanoTime() - start;

            // HashMap approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                maximumSubarraySumHashMap(nums, k);
            }
            long hashMapTime = System.nanoTime() - start;

            // Optimized approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                maximumSubarraySumOptimized(nums, k);
            }
            long optimizedTime = System.nanoTime() - start;

            System.out.println("HashSet: " + hashSetTime / 1_000_000 + " ms");
            System.out.println("HashMap: " + hashMapTime / 1_000_000 + " ms");
            System.out.println("Optimized: " + optimizedTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[] nums1 = { 1, 5, 4, 2, 9, 9, 9 };
        int k1 = 3;

        System.out.println("Input: " + Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("HashSet approach: " + maximumSubarraySum(nums1, k1));
        System.out.println("HashMap approach: " + maximumSubarraySumHashMap(nums1, k1));
        System.out.println("Optimized approach: " + maximumSubarraySumOptimized(nums1, k1));

        // Test Case 2: Different examples
        System.out.println("\n=== Test Case 2: Different Examples ===");

        int[] nums2a = { 4, 4, 4 };
        int[] nums2b = { 1, 2, 3, 4, 5 };
        int[] nums2c = { 5, 3, 3, 1, 1 };

        System.out.println("All duplicates: " + Arrays.toString(nums2a) + ", k=3 -> " +
                maximumSubarraySum(nums2a, 3));
        System.out.println("All distinct: " + Arrays.toString(nums2b) + ", k=3 -> " +
                maximumSubarraySum(nums2b, 3));
        System.out.println("Mixed: " + Arrays.toString(nums2c) + ", k=3 -> " +
                maximumSubarraySum(nums2c, 3));

        // Test Case 3: Edge cases
        System.out.println("\n=== Test Case 3: Edge Cases ===");

        int[] singleElement = { 5 };
        int[] twoElements = { 1, 2 };
        int[] empty = {};

        System.out.println("Single element (k=1): " + maximumSubarraySum(singleElement, 1));
        System.out.println("Two elements (k=1): " + maximumSubarraySum(twoElements, 1));
        System.out.println("Two elements (k=2): " + maximumSubarraySum(twoElements, 2));
        System.out.println("Empty array: " + maximumSubarraySum(empty, 1));
        System.out.println("k > array length: " + maximumSubarraySum(twoElements, 5));

        // Test Case 4: Find all maximum subarrays
        System.out.println("\n=== Test Case 4: Find All Maximum Subarrays ===");

        int[] nums4 = { 1, 5, 4, 2, 9, 9, 9 };
        int k4 = 3;

        System.out.println("Input: " + Arrays.toString(nums4) + ", k=" + k4);

        List<FindAllMaximumSubarrays.SubarrayInfo> allMax = FindAllMaximumSubarrays.findAllMaximumSubarrays(nums4, k4);
        System.out.println("All maximum subarrays:");
        for (FindAllMaximumSubarrays.SubarrayInfo info : allMax) {
            System.out.println("  " + info);
        }

        List<int[]> indices = FindAllMaximumSubarrays.findAllMaximumSubarrayIndices(nums4, k4);
        System.out.print("Indices: ");
        for (int[] idx : indices) {
            System.out.print("[" + idx[0] + "," + idx[1] + "] ");
        }
        System.out.println();

        // Test Case 5: Variable k
        System.out.println("\n=== Test Case 5: Variable K ===");

        int[] nums5 = { 1, 2, 3, 4, 5 };

        System.out.println("Input: " + Arrays.toString(nums5));
        Map<Integer, Long> allKResults = VariableK.findMaxSumForAllK(nums5, 5);
        System.out.println("Results for all k: " + allKResults);

        System.out.println("Max sum for k in [2,4]: " + VariableK.findMaxSumAnyK(nums5, 2, 4));

        int[] bestK = VariableK.findBestK(nums5, 5);
        System.out.println("Best k: " + bestK[0] + ", max sum: " + bestK[1]);

        // Test Case 6: Minimum sum
        System.out.println("\n=== Test Case 6: Minimum Sum ===");

        int[] nums6 = { 1, 2, 3, 4, 5, 1, 2 };
        int k6 = 3;

        System.out.println("Input: " + Arrays.toString(nums6) + ", k=" + k6);
        System.out.println("Maximum sum: " + maximumSubarraySum(nums6, k6));
        System.out.println("Minimum sum: " + MinimumSum.minimumSubarraySum(nums6, k6));

        // Test Case 7: Count valid subarrays
        System.out.println("\n=== Test Case 7: Count Valid Subarrays ===");

        int[] nums7 = { 1, 2, 1, 3, 4 };
        int k7 = 3;

        System.out.println("Input: " + Arrays.toString(nums7) + ", k=" + k7);
        System.out.println("Count of distinct subarrays: " +
                CountValidSubarrays.countDistinctSubarrays(nums7, k7));

        Map<Long, Integer> sumCounts = CountValidSubarrays.countSubarraysBySum(nums7, k7);
        System.out.println("Subarrays by sum: " + sumCounts);

        // Test Case 8: Multiple queries optimization
        System.out.println("\n=== Test Case 8: Multiple Queries ===");

        int[] nums8 = { 1, 5, 4, 2, 9, 9, 9 };
        MultipleQueriesOptimizer optimizer = new MultipleQueriesOptimizer(nums8);

        System.out.println("Input: " + Arrays.toString(nums8));
        for (int k = 1; k <= 4; k++) {
            System.out.println("k=" + k + ": " + optimizer.getMaximumSum(k));
        }

        Map<Integer, Long> precomputed = optimizer.precomputeResults(6);
        System.out.println("Precomputed results: " + precomputed);

        // Test Case 9: Handle negative numbers
        System.out.println("\n=== Test Case 9: Handle Negative Numbers ===");

        int[] nums9 = { -1, 2, -3, 4, 5, -2, 1 };
        int k9 = 3;

        System.out.println("Input: " + Arrays.toString(nums9) + ", k=" + k9);
        System.out.println("Maximum sum (with negatives): " +
                HandleNegativeNumbers.maximumSubarraySumWithNegatives(nums9, k9));
        System.out.println("Maximum positive sum: " +
                HandleNegativeNumbers.maximumPositiveSum(nums9, k9));
        System.out.println("Sum closest to 5: " +
                HandleNegativeNumbers.findSumClosestToTarget(nums9, k9, 5));

        // Test Case 10: Large array performance
        System.out.println("\n=== Test Case 10: Large Array Performance ===");

        Random random = new Random(42);
        int[] largeArray = new int[50000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = random.nextInt(1000) + 1;
        }

        int largeK = 1000;

        long start = System.currentTimeMillis();
        long result = maximumSubarraySum(largeArray, largeK);
        long end = System.currentTimeMillis();

        System.out.println("Large array length: " + largeArray.length);
        System.out.println("k: " + largeK);
        System.out.println("Result: " + result);
        System.out.println("Time taken: " + (end - start) + " ms");

        // Test Case 11: Stress test
        System.out.println("\n=== Test Case 11: Stress Test ===");

        int testCases = 100;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int length = random.nextInt(20) + 1;
            int[] testArray = new int[length];
            for (int i = 0; i < length; i++) {
                testArray[i] = random.nextInt(10) + 1;
            }

            int testK = random.nextInt(length) + 1;

            long result1 = maximumSubarraySum(testArray, testK);
            long result2 = maximumSubarraySumHashMap(testArray, testK);
            long result3 = maximumSubarraySumOptimized(testArray, testK);

            if (result1 == result2 && result2 == result3) {
                passed++;
            } else {
                System.out.println("Mismatch for: " + Arrays.toString(testArray) +
                        ", k=" + testK + " -> " + result1 + ", " + result2 + ", " + result3);
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Test Case 12: Boundary conditions
        System.out.println("\n=== Test Case 12: Boundary Conditions ===");

        int[] maxValues = new int[100];
        Arrays.fill(maxValues, 100000);
        System.out.println("Max values array (k=50): " + maximumSubarraySum(maxValues, 50));

        int[] minValues = new int[100];
        Arrays.fill(minValues, 1);
        System.out.println("Min values array (k=50): " + maximumSubarraySum(minValues, 50));

        int[] alternating = new int[100];
        for (int i = 0; i < alternating.length; i++) {
            alternating[i] = (i % 2) + 1;
        }
        System.out.println("Alternating pattern (k=50): " + maximumSubarraySum(alternating, 50));

        // Performance comparison
        PerformanceComparison.compareApproaches(nums1, k1, 10000);

        System.out.println("\nMaximum Sum of Distinct Subarrays testing completed successfully!");
    }
}
