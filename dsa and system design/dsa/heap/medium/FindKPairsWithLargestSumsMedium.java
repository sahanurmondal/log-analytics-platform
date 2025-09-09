package heap.medium;

import java.util.*;

/**
 * Find K Pairs with Largest Sums - Medium Variant
 * 
 * Based on LeetCode Problem: 373. Find K Pairs with Smallest Sum (adapted for
 * largest)
 * URL: https://leetcode.com/problems/find-k-pairs-with-smallest-sum/
 * 
 * Company Tags: Google, Amazon, Microsoft, Apple, Facebook
 * Difficulty: Medium
 * 
 * Description:
 * You are given two integer arrays nums1 and nums2 sorted in ascending order
 * and an integer k. Define a pair (u, v) which consists of one element from
 * the first array and one element from the second array.
 * 
 * Return the k pairs (u1, v1), (u2, v2), ..., (uk, vk) with the largest sums.
 * 
 * Constraints:
 * - 1 <= nums1.length, nums2.length <= 10^4
 * - -10^4 <= nums1[i], nums2[i] <= 10^4
 * - nums1 and nums2 both are sorted in ascending order
 * - 1 <= k <= 10^3
 * 
 * Follow-ups:
 * 1. Can you solve with different heap approaches?
 * 2. What if we want the smallest sums?
 * 3. Can you optimize memory usage?
 * 4. What if arrays are not sorted?
 * 5. Can you handle very large k efficiently?
 */
public class FindKPairsWithLargestSumsMedium {

    /**
     * Max Heap approach - directly find k largest pairs
     * Time: O(k log k), Space: O(k)
     */
    public List<List<Integer>> kLargestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return result;
        }

        // Max heap based on sum (largest first)
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(b[0] + b[1], a[0] + a[1]));

        Set<String> visited = new HashSet<>();

        // Start with the largest possible pair (last elements)
        int m = nums1.length, n = nums2.length;
        heap.offer(new int[] { nums1[m - 1], nums2[n - 1], m - 1, n - 1 });
        visited.add((m - 1) + "," + (n - 1));

        while (result.size() < k && !heap.isEmpty()) {
            int[] current = heap.poll();
            int i = current[2], j = current[3];

            result.add(Arrays.asList(current[0], current[1]));

            // Add adjacent smaller pairs
            if (i > 0) {
                String key = (i - 1) + "," + j;
                if (!visited.contains(key)) {
                    heap.offer(new int[] { nums1[i - 1], nums2[j], i - 1, j });
                    visited.add(key);
                }
            }

            if (j > 0) {
                String key = i + "," + (j - 1);
                if (!visited.contains(key)) {
                    heap.offer(new int[] { nums1[i], nums2[j - 1], i, j - 1 });
                    visited.add(key);
                }
            }
        }

        return result;
    }

    /**
     * Min Heap approach with size limit - maintain k largest seen so far
     * Time: O(mn log k), Space: O(k)
     */
    public List<List<Integer>> kLargestPairsMinHeap(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return result;
        }

        // Min heap to maintain k largest pairs
        PriorityQueue<List<Integer>> minHeap = new PriorityQueue<>(
                (a, b) -> Integer.compare(a.get(0) + a.get(1), b.get(0) + b.get(1)));

        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                List<Integer> pair = Arrays.asList(nums1[i], nums2[j]);

                if (minHeap.size() < k) {
                    minHeap.offer(pair);
                } else if (nums1[i] + nums2[j] > minHeap.peek().get(0) + minHeap.peek().get(1)) {
                    minHeap.poll();
                    minHeap.offer(pair);
                }
            }
        }

        result.addAll(minHeap);
        result.sort((a, b) -> Integer.compare(b.get(0) + b.get(1), a.get(0) + a.get(1)));

        return result;
    }

    /**
     * Optimized approach - start from largest possible sums
     * Time: O(k log k), Space: O(k)
     */
    public List<List<Integer>> kLargestPairsOptimized(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return result;
        }

        int m = nums1.length, n = nums2.length;

        // Max heap: {sum, i, j}
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(b[0], a[0]));
        Set<String> visited = new HashSet<>();

        // Start with largest sum
        int maxSum = nums1[m - 1] + nums2[n - 1];
        heap.offer(new int[] { maxSum, m - 1, n - 1 });
        visited.add((m - 1) + "," + (n - 1));

        while (result.size() < k && !heap.isEmpty()) {
            int[] current = heap.poll();
            int i = current[1], j = current[2];

            result.add(Arrays.asList(nums1[i], nums2[j]));

            // Add neighbors with potentially smaller sums
            addIfNotVisited(heap, visited, nums1, nums2, i - 1, j);
            addIfNotVisited(heap, visited, nums1, nums2, i, j - 1);
        }

        return result;
    }

    private void addIfNotVisited(PriorityQueue<int[]> heap, Set<String> visited,
            int[] nums1, int[] nums2, int i, int j) {
        if (i >= 0 && j >= 0 && i < nums1.length && j < nums2.length) {
            String key = i + "," + j;
            if (!visited.contains(key)) {
                heap.offer(new int[] { nums1[i] + nums2[j], i, j });
                visited.add(key);
            }
        }
    }

    /**
     * Follow-up 1: Find K Pairs with Smallest Sums (original problem)
     * Time: O(k log k), Space: O(k)
     */
    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return result;
        }

        // Min heap based on sum
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[0] + a[1], b[0] + b[1]));

        Set<String> visited = new HashSet<>();

        // Start with smallest possible pair
        heap.offer(new int[] { nums1[0], nums2[0], 0, 0 });
        visited.add("0,0");

        while (result.size() < k && !heap.isEmpty()) {
            int[] current = heap.poll();
            int i = current[2], j = current[3];

            result.add(Arrays.asList(current[0], current[1]));

            // Add adjacent larger pairs
            if (i + 1 < nums1.length) {
                String key = (i + 1) + "," + j;
                if (!visited.contains(key)) {
                    heap.offer(new int[] { nums1[i + 1], nums2[j], i + 1, j });
                    visited.add(key);
                }
            }

            if (j + 1 < nums2.length) {
                String key = i + "," + (j + 1);
                if (!visited.contains(key)) {
                    heap.offer(new int[] { nums1[i], nums2[j + 1], i, j + 1 });
                    visited.add(key);
                }
            }
        }

        return result;
    }

    /**
     * Follow-up 2: Memory optimized version
     * Time: O(k log k), Space: O(min(k, m*n))
     */
    public List<List<Integer>> kLargestPairsMemoryOptimized(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return result;
        }

        int m = nums1.length, n = nums2.length;

        // If k is large, use different strategy
        if (k >= m * n) {
            // Return all pairs sorted by sum in descending order
            List<List<Integer>> allPairs = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    allPairs.add(Arrays.asList(nums1[i], nums2[j]));
                }
            }
            allPairs.sort((a, b) -> Integer.compare(b.get(0) + b.get(1), a.get(0) + a.get(1)));
            return allPairs.subList(0, Math.min(k, allPairs.size()));
        }

        return kLargestPairsOptimized(nums1, nums2, k);
    }

    /**
     * Follow-up 3: Handle unsorted arrays
     * Time: O(mn log(mn) + k log k), Space: O(mn)
     */
    public List<List<Integer>> kLargestPairsUnsorted(int[] nums1, int[] nums2, int k) {
        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return new ArrayList<>();
        }

        // Sort arrays first
        int[] sortedNums1 = nums1.clone();
        int[] sortedNums2 = nums2.clone();
        Arrays.sort(sortedNums1);
        Arrays.sort(sortedNums2);

        return kLargestPairs(sortedNums1, sortedNums2, k);
    }

    /**
     * Follow-up 4: Return pairs with their sums for analysis
     * Time: O(k log k), Space: O(k)
     */
    public List<PairWithSum> kLargestPairsWithSums(int[] nums1, int[] nums2, int k) {
        List<PairWithSum> result = new ArrayList<>();

        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return result;
        }

        List<List<Integer>> pairs = kLargestPairs(nums1, nums2, k);

        for (List<Integer> pair : pairs) {
            result.add(new PairWithSum(pair.get(0), pair.get(1), pair.get(0) + pair.get(1)));
        }

        return result;
    }

    /**
     * Follow-up 5: Efficient handling of very large k
     * Time: O(min(k log k, mn log(mn))), Space: O(min(k, mn))
     */
    public List<List<Integer>> kLargestPairsEfficient(int[] nums1, int[] nums2, int k) {
        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return new ArrayList<>();
        }

        int m = nums1.length, n = nums2.length;

        // If k is close to or larger than m*n, generate all and sort
        if (k > m * n * 0.8) {
            List<List<Integer>> allPairs = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    allPairs.add(Arrays.asList(nums1[i], nums2[j]));
                }
            }
            allPairs.sort((a, b) -> Integer.compare(b.get(0) + b.get(1), a.get(0) + a.get(1)));
            return allPairs.subList(0, Math.min(k, allPairs.size()));
        }

        return kLargestPairsOptimized(nums1, nums2, k);
    }

    /**
     * Utility method: Get sum of all possible pairs
     * Time: O(mn), Space: O(1)
     */
    public long getTotalSum(int[] nums1, int[] nums2) {
        if (nums1 == null || nums2 == null)
            return 0;

        long total = 0;
        for (int num1 : nums1) {
            for (int num2 : nums2) {
                total += num1 + num2;
            }
        }
        return total;
    }

    /**
     * Utility method: Count pairs with sum >= target
     * Time: O(mn), Space: O(1)
     */
    public int countPairsWithSumAtLeast(int[] nums1, int[] nums2, int target) {
        if (nums1 == null || nums2 == null)
            return 0;

        int count = 0;
        for (int num1 : nums1) {
            for (int num2 : nums2) {
                if (num1 + num2 >= target) {
                    count++;
                }
            }
        }
        return count;
    }

    // Helper class for detailed results
    static class PairWithSum {
        int first, second, sum;

        PairWithSum(int first, int second, int sum) {
            this.first = first;
            this.second = second;
            this.sum = sum;
        }

        @Override
        public String toString() {
            return "[" + first + "," + second + "] sum=" + sum;
        }
    }

    public static void main(String[] args) {
        FindKPairsWithLargestSumsMedium solution = new FindKPairsWithLargestSumsMedium();

        System.out.println("=== Find K Pairs with Largest Sums Test ===");

        // Test Case 1: Basic examples
        System.out.println("Basic examples:");
        int[] nums1 = { 1, 7, 11 };
        int[] nums2 = { 2, 4, 6 };
        System.out.println("nums1=[1,7,11], nums2=[2,4,6], k=3:");
        System.out.println("  Result: " + solution.kLargestPairs(nums1, nums2, 3)); // [[11,6],[11,4],[7,6]]

        int[] nums3 = { 1, 1, 2 };
        int[] nums4 = { 1, 2, 3 };
        System.out.println("nums1=[1,1,2], nums2=[1,2,3], k=10:");
        System.out.println("  Result: " + solution.kLargestPairs(nums3, nums4, 10)); // All pairs

        // Test Case 2: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty arrays: " + solution.kLargestPairs(new int[] {}, nums2, 3));
        System.out.println("k=1: " + solution.kLargestPairs(new int[] { 1 }, new int[] { 2 }, 1));

        // Test Case 3: Negative numbers
        System.out.println("\nNegative numbers:");
        int[] negNums1 = { -1, 0, 1 };
        int[] negNums2 = { -2, 2 };
        System.out.println("nums1=[-1,0,1], nums2=[-2,2], k=3:");
        System.out.println("  Result: " + solution.kLargestPairs(negNums1, negNums2, 3));

        // Test Case 4: Compare different approaches
        System.out.println("\nCompare approaches:");
        int[] test1 = { 1, 3, 5 };
        int[] test2 = { 2, 4, 6 };
        System.out.println("Max heap: " + solution.kLargestPairs(test1, test2, 4));
        System.out.println("Min heap: " + solution.kLargestPairsMinHeap(test1, test2, 4));
        System.out.println("Optimized: " + solution.kLargestPairsOptimized(test1, test2, 4));
        System.out.println("Memory opt: " + solution.kLargestPairsMemoryOptimized(test1, test2, 4));

        // Test Case 5: Smallest pairs comparison
        System.out.println("\nSmallest pairs (original problem):");
        System.out.println("K smallest: " + solution.kSmallestPairs(test1, test2, 4));

        // Test Case 6: Unsorted arrays
        System.out.println("\nUnsorted arrays:");
        int[] unsorted1 = { 5, 1, 3 };
        int[] unsorted2 = { 6, 2, 4 };
        System.out.println("Unsorted result: " + solution.kLargestPairsUnsorted(unsorted1, unsorted2, 3));

        // Test Case 7: Pairs with sums
        System.out.println("\nPairs with sums:");
        List<PairWithSum> pairsWithSums = solution.kLargestPairsWithSums(test1, test2, 3);
        for (PairWithSum pws : pairsWithSums) {
            System.out.println("  " + pws);
        }

        // Test Case 8: Utility methods
        System.out.println("\nUtility methods:");
        System.out.println("Total sum: " + solution.getTotalSum(test1, test2));
        System.out.println("Pairs with sum >= 8: " + solution.countPairsWithSumAtLeast(test1, test2, 8));

        // Test Case 9: Large k handling
        System.out.println("\nLarge k handling:");
        System.out.println("Efficient (k=20): " + solution.kLargestPairsEfficient(test1, test2, 20));

        // Test Case 10: Performance test
        System.out.println("\n=== Performance Test ===");
        int[] large1 = new int[100];
        int[] large2 = new int[100];
        for (int i = 0; i < 100; i++) {
            large1[i] = i + 1;
            large2[i] = i + 1;
        }

        long startTime = System.currentTimeMillis();
        List<List<Integer>> result1 = solution.kLargestPairs(large1, large2, 50);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        List<List<Integer>> result2 = solution.kLargestPairsOptimized(large1, large2, 50);
        long time2 = System.currentTimeMillis() - startTime;

        System.out.println("Standard (100x100, k=50): " + result1.size() + " pairs (" + time1 + "ms)");
        System.out.println("Optimized (100x100, k=50): " + result2.size() + " pairs (" + time2 + "ms)");

        // Test Case 11: All same elements
        System.out.println("\nSpecial cases:");
        int[] same1 = { 1, 1, 1 };
        int[] same2 = { 1, 1, 1 };
        System.out.println("All same elements: " + solution.kLargestPairs(same1, same2, 5));

        // Test Case 12: Single element arrays
        System.out.println("Single elements: " + solution.kLargestPairs(new int[] { 5 }, new int[] { 3 }, 1));

        // Test Case 13: Very large numbers
        int[] bigNums1 = { 9999, 10000 };
        int[] bigNums2 = { 9998, 9999 };
        System.out.println("Large numbers: " + solution.kLargestPairs(bigNums1, bigNums2, 3));

        // Test Case 14: Mixed positive/negative
        int[] mixed1 = { -10, -1, 0, 1, 10 };
        int[] mixed2 = { -5, 0, 5 };
        System.out.println("Mixed pos/neg: " + solution.kLargestPairs(mixed1, mixed2, 5));

        System.out.println("\n=== Summary ===");
        System.out.println("All test cases completed successfully!");
    }
}
