package recursion.medium;

import java.util.*;

/**
 * LeetCode 78: Subsets
 * https://leetcode.com/problems/subsets/
 * 
 * Companies: Amazon, Microsoft, Facebook, Google, Apple, Bloomberg, Uber
 * Frequency: Very High (Asked in 800+ interviews)
 *
 * Description:
 * Given an integer array nums of unique elements, return all possible subsets
 * (the power set).
 * The solution set must not contain duplicate subsets. Return the solution in
 * any order.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10
 * - -10 <= nums[i] <= 10
 * - All the numbers of nums are unique.
 * 
 * Follow-up Questions:
 * 1. How would you handle duplicate elements?
 * 2. Can you implement iterative solutions?
 * 3. How to generate subsets in lexicographical order?
 * 4. What about generating subsets of specific size k?
 * 5. How to implement using bit manipulation?
 * 6. Can you optimize for memory usage?
 */
public class Subsets {

    // Approach 1: Backtracking (Recursive) - O(2^n * n) time, O(n) space (excluding
    // result)
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] nums, int start, List<Integer> current,
            List<List<Integer>> result) {
        // Add current subset to result
        result.add(new ArrayList<>(current));

        // Generate subsets by adding remaining elements
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrack(nums, i + 1, current, result);
            current.remove(current.size() - 1); // backtrack
        }
    }

    // Approach 2: Iterative (Building subsets incrementally) - O(2^n * n) time,
    // O(1) space
    public static List<List<Integer>> subsetsIterative(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); // Start with empty subset

        for (int num : nums) {
            int size = result.size();
            for (int i = 0; i < size; i++) {
                List<Integer> newSubset = new ArrayList<>(result.get(i));
                newSubset.add(num);
                result.add(newSubset);
            }
        }

        return result;
    }

    // Approach 3: Bit Manipulation - O(2^n * n) time, O(1) space
    public static List<List<Integer>> subsetsBitManipulation(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        int totalSubsets = 1 << n; // 2^n

        for (int mask = 0; mask < totalSubsets; mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(nums[i]);
                }
            }
            result.add(subset);
        }

        return result;
    }

    // Follow-up 1: Handle duplicate elements (Subsets II)
    public static class SubsetsWithDuplicates {

        public static List<List<Integer>> subsetsWithDup(int[] nums) {
            Arrays.sort(nums); // Sort to group duplicates
            List<List<Integer>> result = new ArrayList<>();
            backtrackWithDup(nums, 0, new ArrayList<>(), result);
            return result;
        }

        private static void backtrackWithDup(int[] nums, int start, List<Integer> current,
                List<List<Integer>> result) {
            result.add(new ArrayList<>(current));

            for (int i = start; i < nums.length; i++) {
                // Skip duplicates at the same level
                if (i > start && nums[i] == nums[i - 1])
                    continue;

                current.add(nums[i]);
                backtrackWithDup(nums, i + 1, current, result);
                current.remove(current.size() - 1);
            }
        }

        // Iterative approach for duplicates
        public static List<List<Integer>> subsetsWithDupIterative(int[] nums) {
            Arrays.sort(nums);
            List<List<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>());

            int startIndex = 0;

            for (int i = 0; i < nums.length; i++) {
                int size = result.size();

                // If current element is duplicate, only add to recently added subsets
                if (i > 0 && nums[i] == nums[i - 1]) {
                    startIndex = size - startIndex;
                } else {
                    startIndex = 0;
                }

                for (int j = startIndex; j < size; j++) {
                    List<Integer> newSubset = new ArrayList<>(result.get(j));
                    newSubset.add(nums[i]);
                    result.add(newSubset);
                }
            }

            return result;
        }
    }

    // Follow-up 2: Generate subsets in lexicographical order
    public static class LexicographicalSubsets {

        public static List<List<Integer>> subsetsLexicographical(int[] nums) {
            Arrays.sort(nums);
            List<List<Integer>> result = new ArrayList<>();
            generateLexicographical(nums, 0, new ArrayList<>(), result);
            return result;
        }

        private static void generateLexicographical(int[] nums, int start,
                List<Integer> current,
                List<List<Integer>> result) {
            result.add(new ArrayList<>(current));

            for (int i = start; i < nums.length; i++) {
                current.add(nums[i]);
                generateLexicographical(nums, i + 1, current, result);
                current.remove(current.size() - 1);
            }
        }

        // Generate in specific lexicographical order using next permutation concept
        public static List<List<Integer>> subsetsNextPermutation(int[] nums) {
            Arrays.sort(nums);
            List<List<Integer>> result = new ArrayList<>();
            boolean[] used = new boolean[nums.length];

            // Generate all possible combinations
            for (int size = 0; size <= nums.length; size++) {
                generateCombinations(nums, 0, size, new ArrayList<>(), result);
            }

            return result;
        }

        private static void generateCombinations(int[] nums, int start, int size,
                List<Integer> current,
                List<List<Integer>> result) {
            if (current.size() == size) {
                result.add(new ArrayList<>(current));
                return;
            }

            for (int i = start; i < nums.length; i++) {
                current.add(nums[i]);
                generateCombinations(nums, i + 1, size, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    // Follow-up 3: Generate subsets of specific size k
    public static class SubsetsOfSizeK {

        public static List<List<Integer>> combine(int[] nums, int k) {
            List<List<Integer>> result = new ArrayList<>();
            backtrackCombine(nums, 0, k, new ArrayList<>(), result);
            return result;
        }

        private static void backtrackCombine(int[] nums, int start, int k,
                List<Integer> current,
                List<List<Integer>> result) {
            if (current.size() == k) {
                result.add(new ArrayList<>(current));
                return;
            }

            // Optimization: if we need more elements than available, return
            int needed = k - current.size();
            int available = nums.length - start;
            if (needed > available)
                return;

            for (int i = start; i < nums.length; i++) {
                current.add(nums[i]);
                backtrackCombine(nums, i + 1, k, current, result);
                current.remove(current.size() - 1);
            }
        }

        // Iterative approach for combinations
        public static List<List<Integer>> combineIterative(int[] nums, int k) {
            List<List<Integer>> result = new ArrayList<>();
            if (k == 0) {
                result.add(new ArrayList<>());
                return result;
            }

            Queue<List<Integer>> queue = new LinkedList<>();

            // Start with single elements
            for (int num : nums) {
                List<Integer> subset = new ArrayList<>();
                subset.add(num);
                queue.offer(subset);
            }

            while (!queue.isEmpty()) {
                List<Integer> current = queue.poll();

                if (current.size() == k) {
                    result.add(current);
                    continue;
                }

                // Find the last element's index
                int lastElement = current.get(current.size() - 1);
                int lastIndex = -1;
                for (int i = 0; i < nums.length; i++) {
                    if (nums[i] == lastElement) {
                        lastIndex = i;
                        break;
                    }
                }

                // Add next elements
                for (int i = lastIndex + 1; i < nums.length; i++) {
                    List<Integer> newSubset = new ArrayList<>(current);
                    newSubset.add(nums[i]);
                    queue.offer(newSubset);
                }
            }

            return result;
        }
    }

    // Follow-up 4: Memory-optimized implementation using iterators
    public static class MemoryOptimizedSubsets {

        public static class SubsetIterator implements Iterator<List<Integer>> {
            private int[] nums;
            private int current;
            private int total;

            public SubsetIterator(int[] nums) {
                this.nums = nums.clone();
                this.current = 0;
                this.total = 1 << nums.length;
            }

            @Override
            public boolean hasNext() {
                return current < total;
            }

            @Override
            public List<Integer> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                List<Integer> subset = new ArrayList<>();
                int mask = current;

                for (int i = 0; i < nums.length; i++) {
                    if ((mask & (1 << i)) != 0) {
                        subset.add(nums[i]);
                    }
                }

                current++;
                return subset;
            }
        }

        public static Iterable<List<Integer>> getSubsets(int[] nums) {
            return () -> new SubsetIterator(nums);
        }
    }

    // Advanced: Parallel subset generation
    public static class ParallelSubsets {

        public static List<List<Integer>> subsetsParallel(int[] nums) {
            int n = nums.length;
            int totalSubsets = 1 << n;

            return java.util.stream.IntStream.range(0, totalSubsets)
                    .parallel()
                    .mapToObj(mask -> {
                        List<Integer> subset = new ArrayList<>();
                        for (int i = 0; i < n; i++) {
                            if ((mask & (1 << i)) != 0) {
                                subset.add(nums[i]);
                            }
                        }
                        return subset;
                    })
                    .collect(java.util.stream.Collectors.toList());
        }

        // Parallel with custom ForkJoin
        public static List<List<Integer>> subsetsParallelForkJoin(int[] nums) {
            return new SubsetGenerationTask(nums, 0, 1 << nums.length).compute();
        }

        private static class SubsetGenerationTask extends java.util.concurrent.RecursiveTask<List<List<Integer>>> {
            private int[] nums;
            private int start;
            private int end;
            private static final int THRESHOLD = 1000;

            public SubsetGenerationTask(int[] nums, int start, int end) {
                this.nums = nums;
                this.start = start;
                this.end = end;
            }

            @Override
            protected List<List<Integer>> compute() {
                if (end - start <= THRESHOLD) {
                    // Direct computation
                    List<List<Integer>> result = new ArrayList<>();
                    for (int mask = start; mask < end; mask++) {
                        List<Integer> subset = new ArrayList<>();
                        for (int i = 0; i < nums.length; i++) {
                            if ((mask & (1 << i)) != 0) {
                                subset.add(nums[i]);
                            }
                        }
                        result.add(subset);
                    }
                    return result;
                } else {
                    // Split task
                    int mid = start + (end - start) / 2;
                    SubsetGenerationTask leftTask = new SubsetGenerationTask(nums, start, mid);
                    SubsetGenerationTask rightTask = new SubsetGenerationTask(nums, mid, end);

                    leftTask.fork();
                    List<List<Integer>> rightResult = rightTask.compute();
                    List<List<Integer>> leftResult = leftTask.join();

                    leftResult.addAll(rightResult);
                    return leftResult;
                }
            }
        }
    }

    // Advanced: Subset generation with constraints
    public static class ConstrainedSubsets {

        // Generate subsets with sum equal to target
        public static List<List<Integer>> subsetsWithTargetSum(int[] nums, int target) {
            List<List<Integer>> result = new ArrayList<>();
            backtrackWithSum(nums, 0, target, new ArrayList<>(), result);
            return result;
        }

        private static void backtrackWithSum(int[] nums, int start, int target,
                List<Integer> current,
                List<List<Integer>> result) {
            if (target == 0) {
                result.add(new ArrayList<>(current));
                return;
            }

            for (int i = start; i < nums.length; i++) {
                if (nums[i] <= target) {
                    current.add(nums[i]);
                    backtrackWithSum(nums, i + 1, target - nums[i], current, result);
                    current.remove(current.size() - 1);
                }
            }
        }

        // Generate subsets with sum in range [minSum, maxSum]
        public static List<List<Integer>> subsetsWithSumRange(int[] nums, int minSum, int maxSum) {
            List<List<Integer>> result = new ArrayList<>();
            backtrackWithSumRange(nums, 0, 0, minSum, maxSum, new ArrayList<>(), result);
            return result;
        }

        private static void backtrackWithSumRange(int[] nums, int start, int currentSum,
                int minSum, int maxSum,
                List<Integer> current,
                List<List<Integer>> result) {
            if (currentSum >= minSum && currentSum <= maxSum) {
                result.add(new ArrayList<>(current));
            }

            for (int i = start; i < nums.length; i++) {
                int newSum = currentSum + nums[i];
                if (newSum <= maxSum) {
                    current.add(nums[i]);
                    backtrackWithSumRange(nums, i + 1, newSum, minSum, maxSum, current, result);
                    current.remove(current.size() - 1);
                }
            }
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(int[] nums, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Array: " + Arrays.toString(nums) + ", Iterations: " + iterations);

            // Backtracking
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                subsets(nums);
            }
            long backtrackTime = System.nanoTime() - start;

            // Iterative
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                subsetsIterative(nums);
            }
            long iterativeTime = System.nanoTime() - start;

            // Bit manipulation
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                subsetsBitManipulation(nums);
            }
            long bitTime = System.nanoTime() - start;

            System.out.println("Backtracking: " + backtrackTime / 1_000_000 + " ms");
            System.out.println("Iterative: " + iterativeTime / 1_000_000 + " ms");
            System.out.println("Bit Manipulation: " + bitTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[] nums1 = { 1, 2, 3 };

        System.out.println("Input: " + Arrays.toString(nums1));
        System.out.println("Backtracking: " + subsets(nums1));
        System.out.println("Iterative: " + subsetsIterative(nums1));
        System.out.println("Bit Manipulation: " + subsetsBitManipulation(nums1));

        // Test Case 2: Single element
        System.out.println("\n=== Test Case 2: Single Element ===");

        int[] nums2 = { 0 };
        System.out.println("Input: " + Arrays.toString(nums2));
        System.out.println("Result: " + subsets(nums2));

        // Test Case 3: Empty result case
        System.out.println("\n=== Test Case 3: Empty Array ===");

        int[] nums3 = {};
        System.out.println("Input: " + Arrays.toString(nums3));
        System.out.println("Result: " + subsets(nums3));

        // Test Case 4: Subsets with duplicates
        System.out.println("\n=== Test Case 4: Subsets with Duplicates ===");

        int[] nums4 = { 1, 2, 2 };
        System.out.println("Input: " + Arrays.toString(nums4));
        System.out.println("With duplicates: " + SubsetsWithDuplicates.subsetsWithDup(nums4));
        System.out.println("Iterative with duplicates: " +
                SubsetsWithDuplicates.subsetsWithDupIterative(nums4));

        // Test Case 5: Lexicographical order
        System.out.println("\n=== Test Case 5: Lexicographical Order ===");

        int[] nums5 = { 3, 1, 2 };
        System.out.println("Input: " + Arrays.toString(nums5));
        System.out.println("Lexicographical: " +
                LexicographicalSubsets.subsetsLexicographical(nums5));

        // Test Case 6: Subsets of specific size
        System.out.println("\n=== Test Case 6: Subsets of Size K ===");

        int[] nums6 = { 1, 2, 3, 4 };
        int k = 2;
        System.out.println("Input: " + Arrays.toString(nums6) + ", k=" + k);
        System.out.println("Combinations: " + SubsetsOfSizeK.combine(nums6, k));
        System.out.println("Iterative combinations: " +
                SubsetsOfSizeK.combineIterative(nums6, k));

        // Test Case 7: Memory-optimized iterator
        System.out.println("\n=== Test Case 7: Memory-optimized Iterator ===");

        int[] nums7 = { 1, 2, 3 };
        System.out.println("Input: " + Arrays.toString(nums7));
        System.out.print("Iterator results: ");

        int count = 0;
        for (List<Integer> subset : MemoryOptimizedSubsets.getSubsets(nums7)) {
            System.out.print(subset + " ");
            count++;
            if (count >= 8)
                break; // Limit output
        }
        System.out.println();

        // Test Case 8: Parallel generation
        System.out.println("\n=== Test Case 8: Parallel Generation ===");

        int[] nums8 = { 1, 2, 3, 4 };
        System.out.println("Input: " + Arrays.toString(nums8));

        long start = System.currentTimeMillis();
        List<List<Integer>> parallelResult = ParallelSubsets.subsetsParallel(nums8);
        long parallelTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        List<List<Integer>> sequentialResult = subsets(nums8);
        long sequentialTime = System.currentTimeMillis() - start;

        System.out.println("Parallel result size: " + parallelResult.size());
        System.out.println("Sequential result size: " + sequentialResult.size());
        System.out.println("Parallel time: " + parallelTime + " ms");
        System.out.println("Sequential time: " + sequentialTime + " ms");

        // Test Case 9: Constrained subsets
        System.out.println("\n=== Test Case 9: Constrained Subsets ===");

        int[] nums9 = { 1, 2, 3, 4, 5 };
        int target = 5;
        System.out.println("Input: " + Arrays.toString(nums9) + ", target sum=" + target);
        System.out.println("Subsets with target sum: " +
                ConstrainedSubsets.subsetsWithTargetSum(nums9, target));

        int minSum = 3, maxSum = 7;
        System.out.println("Subsets with sum range [" + minSum + ", " + maxSum + "]: " +
                ConstrainedSubsets.subsetsWithSumRange(nums9, minSum, maxSum));

        // Test Case 10: Large input
        System.out.println("\n=== Test Case 10: Large Input ===");

        int[] nums10 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        System.out.println("Input size: " + nums10.length);

        start = System.currentTimeMillis();
        List<List<Integer>> result = subsets(nums10);
        long end = System.currentTimeMillis();

        System.out.println("Result size: " + result.size());
        System.out.println("Time taken: " + (end - start) + " ms");
        System.out.println("Expected size (2^n): " + (1 << nums10.length));

        // Test Case 11: Memory usage comparison
        System.out.println("\n=== Test Case 11: Memory Usage Comparison ===");

        int[] nums11 = { 1, 2, 3, 4, 5, 6 };

        Runtime runtime = Runtime.getRuntime();

        // Test memory usage for different approaches
        runtime.gc();
        long memBefore = runtime.totalMemory() - runtime.freeMemory();

        List<List<Integer>> bitResult = subsetsBitManipulation(nums11);

        runtime.gc();
        long memAfter = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Memory used (bit manipulation): " + (memAfter - memBefore) + " bytes");
        System.out.println("Result size: " + bitResult.size());

        // Test Case 12: Edge cases
        System.out.println("\n=== Test Case 12: Edge Cases ===");

        // Maximum size input
        int[] maxNums = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println("Max input size: " + maxNums.length);
        System.out.println("Expected subsets: " + (1 << maxNums.length));

        // Negative numbers
        int[] negativeNums = { -1, -2, 3 };
        System.out.println("With negative numbers: " + Arrays.toString(negativeNums));
        System.out.println("Result: " + subsets(negativeNums));

        // Performance comparison
        PerformanceComparison.compareApproaches(new int[] { 1, 2, 3, 4, 5 }, 1000);

        System.out.println("\nSubsets testing completed successfully!");
    }
}
