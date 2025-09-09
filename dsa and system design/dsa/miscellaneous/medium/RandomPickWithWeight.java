package miscellaneous.medium;

import java.util.*;

/**
 * LeetCode 528: Random Pick with Weight
 * https://leetcode.com/problems/random-pick-with-weight/
 * 
 * Companies: Facebook, Google, Amazon, Microsoft, LinkedIn, Uber
 * Frequency: Very High (Asked in 600+ interviews)
 *
 * Description:
 * You are given a 0-indexed array of positive integers w where w[i] describes
 * the weight of the ith index.
 * You need to implement the function pickIndex(), which randomly picks an index
 * in the range [0, w.length - 1]
 * (inclusive) and returns it. The probability of picking an index i is w[i] /
 * sum(w).
 *
 * For example, if w = [1, 3], the probability of picking index 0 is 1 / (1 + 3)
 * = 1 / 4 = 25%,
 * and the probability of picking index 1 is 3 / (1 + 3) = 3 / 4 = 75%.
 * 
 * Constraints:
 * - 1 <= w.length <= 10^4
 * - 1 <= w[i] <= 10^5
 * - pickIndex will be called at most 10^4 times.
 * 
 * Follow-up Questions:
 * 1. How would you handle dynamic weight updates?
 * 2. Can you implement using different random algorithms?
 * 3. How to handle negative weights or zero weights?
 * 4. What about memory-optimized version for large weights?
 * 5. Can you support range-based picking?
 */
public class RandomPickWithWeight {

    // Approach 1: Prefix Sum + Binary Search - O(n) init, O(log n) pick
    public static class Solution {
        private int[] prefixSums;
        private Random random;

        public Solution(int[] w) {
            random = new Random();
            prefixSums = new int[w.length];
            prefixSums[0] = w[0];

            // Build prefix sum array
            for (int i = 1; i < w.length; i++) {
                prefixSums[i] = prefixSums[i - 1] + w[i];
            }
        }

        public int pickIndex() {
            int totalWeight = prefixSums[prefixSums.length - 1];
            int target = random.nextInt(totalWeight) + 1; // 1 to totalWeight

            // Binary search for the target
            int left = 0, right = prefixSums.length - 1;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (prefixSums[mid] < target) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            return left;
        }
    }

    // Approach 2: Optimized with long for large weights
    public static class LongWeightSolution {
        private long[] prefixSums;
        private Random random;

        public LongWeightSolution(int[] w) {
            random = new Random();
            prefixSums = new long[w.length];
            prefixSums[0] = w[0];

            for (int i = 1; i < w.length; i++) {
                prefixSums[i] = prefixSums[i - 1] + w[i];
            }
        }

        public int pickIndex() {
            long totalWeight = prefixSums[prefixSums.length - 1];
            long target = (long) (random.nextDouble() * totalWeight) + 1;

            int left = 0, right = prefixSums.length - 1;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (prefixSums[mid] < target) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            return left;
        }
    }

    // Follow-up 1: Dynamic weight updates
    public static class DynamicWeightSolution {
        private List<Integer> weights;
        private List<Long> prefixSums;
        private Random random;

        public DynamicWeightSolution(int[] w) {
            random = new Random();
            weights = new ArrayList<>();
            prefixSums = new ArrayList<>();

            for (int weight : w) {
                addWeight(weight);
            }
        }

        public void addWeight(int weight) {
            weights.add(weight);
            long prevSum = prefixSums.isEmpty() ? 0 : prefixSums.get(prefixSums.size() - 1);
            prefixSums.add(prevSum + weight);
        }

        public void updateWeight(int index, int newWeight) {
            if (index < 0 || index >= weights.size()) {
                throw new IndexOutOfBoundsException("Invalid index: " + index);
            }

            int oldWeight = weights.get(index);
            weights.set(index, newWeight);

            // Update prefix sums from this index onwards
            long diff = newWeight - oldWeight;
            for (int i = index; i < prefixSums.size(); i++) {
                prefixSums.set(i, prefixSums.get(i) + diff);
            }
        }

        public void removeWeight(int index) {
            if (index < 0 || index >= weights.size()) {
                throw new IndexOutOfBoundsException("Invalid index: " + index);
            }

            int removedWeight = weights.remove(index);
            prefixSums.remove(index);

            // Update remaining prefix sums
            for (int i = index; i < prefixSums.size(); i++) {
                prefixSums.set(i, prefixSums.get(i) - removedWeight);
            }
        }

        public int pickIndex() {
            if (prefixSums.isEmpty()) {
                throw new IllegalStateException("No weights available");
            }

            long totalWeight = prefixSums.get(prefixSums.size() - 1);
            long target = (long) (random.nextDouble() * totalWeight) + 1;

            int left = 0, right = prefixSums.size() - 1;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (prefixSums.get(mid) < target) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            return left;
        }

        public List<Integer> getWeights() {
            return new ArrayList<>(weights);
        }

        public int size() {
            return weights.size();
        }
    }

    // Follow-up 2: Different random algorithms
    public static class AliasMethodSolution {
        private int[] alias;
        private double[] probability;
        private Random random;
        private int n;

        public AliasMethodSolution(int[] w) {
            random = new Random();
            n = w.length;
            alias = new int[n];
            probability = new double[n];

            // Calculate total weight
            long totalWeight = 0;
            for (int weight : w) {
                totalWeight += weight;
            }

            // Normalize probabilities
            double[] normalizedProbs = new double[n];
            for (int i = 0; i < n; i++) {
                normalizedProbs[i] = (double) w[i] * n / totalWeight;
            }

            // Build alias table using Walker's method
            Queue<Integer> small = new ArrayDeque<>();
            Queue<Integer> large = new ArrayDeque<>();

            for (int i = 0; i < n; i++) {
                if (normalizedProbs[i] < 1.0) {
                    small.offer(i);
                } else {
                    large.offer(i);
                }
            }

            while (!small.isEmpty() && !large.isEmpty()) {
                int smaller = small.poll();
                int larger = large.poll();

                probability[smaller] = normalizedProbs[smaller];
                alias[smaller] = larger;

                normalizedProbs[larger] = normalizedProbs[larger] + normalizedProbs[smaller] - 1.0;

                if (normalizedProbs[larger] < 1.0) {
                    small.offer(larger);
                } else {
                    large.offer(larger);
                }
            }

            while (!large.isEmpty()) {
                probability[large.poll()] = 1.0;
            }

            while (!small.isEmpty()) {
                probability[small.poll()] = 1.0;
            }
        }

        public int pickIndex() {
            int index = random.nextInt(n);
            return random.nextDouble() < probability[index] ? index : alias[index];
        }
    }

    // Follow-up 3: Handling zero and negative weights
    public static class RobustWeightSolution {
        private int[] prefixSums;
        private Random random;
        private int[] originalWeights;
        private Set<Integer> zeroWeightIndices;

        public RobustWeightSolution(int[] w) {
            random = new Random();
            originalWeights = w.clone();
            zeroWeightIndices = new HashSet<>();

            // Handle zero and negative weights
            List<Integer> validWeights = new ArrayList<>();
            List<Integer> validIndices = new ArrayList<>();

            for (int i = 0; i < w.length; i++) {
                if (w[i] > 0) {
                    validWeights.add(w[i]);
                    validIndices.add(i);
                } else {
                    zeroWeightIndices.add(i);
                }
            }

            if (validWeights.isEmpty()) {
                throw new IllegalArgumentException("All weights are zero or negative");
            }

            // Build prefix sum for valid weights only
            prefixSums = new int[validWeights.size()];
            prefixSums[0] = validWeights.get(0);

            for (int i = 1; i < validWeights.size(); i++) {
                prefixSums[i] = prefixSums[i - 1] + validWeights.get(i);
            }
        }

        public int pickIndex() {
            int totalWeight = prefixSums[prefixSums.length - 1];
            int target = random.nextInt(totalWeight) + 1;

            int left = 0, right = prefixSums.length - 1;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (prefixSums[mid] < target) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            // Map back to original index
            int validIndex = left;
            int originalIndex = 0;
            int validCount = 0;

            for (int i = 0; i < originalWeights.length; i++) {
                if (originalWeights[i] > 0) {
                    if (validCount == validIndex) {
                        originalIndex = i;
                        break;
                    }
                    validCount++;
                }
            }

            return originalIndex;
        }

        public Set<Integer> getZeroWeightIndices() {
            return new HashSet<>(zeroWeightIndices);
        }
    }

    // Follow-up 4: Memory optimized for large weights
    public static class MemoryOptimizedSolution {
        private int[] weights;
        private Random random;
        private long totalWeight;

        public MemoryOptimizedSolution(int[] w) {
            random = new Random();
            weights = w.clone();
            totalWeight = 0;
            for (int weight : w) {
                totalWeight += weight;
            }
        }

        public int pickIndex() {
            long target = (long) (random.nextDouble() * totalWeight);
            long currentSum = 0;

            for (int i = 0; i < weights.length; i++) {
                currentSum += weights[i];
                if (currentSum > target) {
                    return i;
                }
            }

            return weights.length - 1;
        }

        public long getTotalWeight() {
            return totalWeight;
        }

        public void updateWeight(int index, int newWeight) {
            totalWeight = totalWeight - weights[index] + newWeight;
            weights[index] = newWeight;
        }
    }

    // Follow-up 5: Range-based picking
    public static class RangeBasedSolution {
        private int[] prefixSums;
        private Random random;
        private int[] weights;

        public RangeBasedSolution(int[] w) {
            random = new Random();
            weights = w.clone();
            prefixSums = new int[w.length];
            prefixSums[0] = w[0];

            for (int i = 1; i < w.length; i++) {
                prefixSums[i] = prefixSums[i - 1] + w[i];
            }
        }

        public int pickIndex() {
            return pickIndexInRange(0, weights.length - 1);
        }

        public int pickIndexInRange(int start, int end) {
            if (start < 0 || end >= weights.length || start > end) {
                throw new IllegalArgumentException("Invalid range [" + start + ", " + end + "]");
            }

            // Calculate weight sum in range
            int rangeWeightSum = getRangeWeightSum(start, end);
            if (rangeWeightSum == 0) {
                throw new IllegalArgumentException("No positive weights in range");
            }

            int target = random.nextInt(rangeWeightSum) + 1;
            int currentSum = 0;

            for (int i = start; i <= end; i++) {
                currentSum += weights[i];
                if (currentSum >= target) {
                    return i;
                }
            }

            return end;
        }

        private int getRangeWeightSum(int start, int end) {
            int sum = 0;
            for (int i = start; i <= end; i++) {
                sum += weights[i];
            }
            return sum;
        }

        public List<Integer> pickMultipleIndices(int count) {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(pickIndex());
            }
            return result;
        }

        public List<Integer> pickUniqueIndices(int count) {
            if (count > weights.length) {
                throw new IllegalArgumentException("Count exceeds available indices");
            }

            Set<Integer> picked = new HashSet<>();
            while (picked.size() < count) {
                picked.add(pickIndex());
            }

            return new ArrayList<>(picked);
        }
    }

    // Advanced: Thread-safe version
    public static class ThreadSafeSolution {
        private final int[] prefixSums;
        private final ThreadLocal<Random> random;
        private final Object lock = new Object();

        public ThreadSafeSolution(int[] w) {
            random = ThreadLocal.withInitial(Random::new);
            prefixSums = new int[w.length];
            prefixSums[0] = w[0];

            for (int i = 1; i < w.length; i++) {
                prefixSums[i] = prefixSums[i - 1] + w[i];
            }
        }

        public int pickIndex() {
            int totalWeight = prefixSums[prefixSums.length - 1];
            int target = random.get().nextInt(totalWeight) + 1;

            synchronized (lock) {
                int left = 0, right = prefixSums.length - 1;
                while (left < right) {
                    int mid = left + (right - left) / 2;
                    if (prefixSums[mid] < target) {
                        left = mid + 1;
                    } else {
                        right = mid;
                    }
                }
                return left;
            }
        }
    }

    // Advanced: Statistical analysis support
    public static class StatisticalSolution {
        private int[] prefixSums;
        private Random random;
        private int[] weights;
        private Map<Integer, Integer> pickCount;
        private int totalPicks;

        public StatisticalSolution(int[] w) {
            random = new Random();
            weights = w.clone();
            prefixSums = new int[w.length];
            prefixSums[0] = w[0];

            for (int i = 1; i < w.length; i++) {
                prefixSums[i] = prefixSums[i - 1] + w[i];
            }

            pickCount = new HashMap<>();
            totalPicks = 0;
        }

        public int pickIndex() {
            int totalWeight = prefixSums[prefixSums.length - 1];
            int target = random.nextInt(totalWeight) + 1;

            int left = 0, right = prefixSums.length - 1;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (prefixSums[mid] < target) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            // Update statistics
            pickCount.put(left, pickCount.getOrDefault(left, 0) + 1);
            totalPicks++;

            return left;
        }

        public Map<Integer, Double> getActualProbabilities() {
            Map<Integer, Double> result = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : pickCount.entrySet()) {
                result.put(entry.getKey(), (double) entry.getValue() / totalPicks);
            }
            return result;
        }

        public Map<Integer, Double> getExpectedProbabilities() {
            Map<Integer, Double> result = new HashMap<>();
            int totalWeight = prefixSums[prefixSums.length - 1];
            for (int i = 0; i < weights.length; i++) {
                result.put(i, (double) weights[i] / totalWeight);
            }
            return result;
        }

        public double getChiSquaredStatistic() {
            double chiSquared = 0.0;
            int totalWeight = prefixSums[prefixSums.length - 1];

            for (int i = 0; i < weights.length; i++) {
                double expected = (double) weights[i] * totalPicks / totalWeight;
                int observed = pickCount.getOrDefault(i, 0);
                if (expected > 0) {
                    chiSquared += Math.pow(observed - expected, 2) / expected;
                }
            }

            return chiSquared;
        }

        public void resetStatistics() {
            pickCount.clear();
            totalPicks = 0;
        }

        public int getTotalPicks() {
            return totalPicks;
        }
    }

    // Performance testing
    public static void performanceTest() {
        System.out.println("=== Performance Testing ===");

        int[] weights = new int[10000];
        Random rand = new Random(42);
        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextInt(1000) + 1;
        }

        // Test binary search approach
        Solution binarySearchSolution = new Solution(weights);
        long start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            binarySearchSolution.pickIndex();
        }
        long binarySearchTime = System.nanoTime() - start;

        // Test memory optimized approach
        MemoryOptimizedSolution memOptSolution = new MemoryOptimizedSolution(weights);
        start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            memOptSolution.pickIndex();
        }
        long memOptTime = System.nanoTime() - start;

        // Test alias method
        AliasMethodSolution aliasSolution = new AliasMethodSolution(weights);
        start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            aliasSolution.pickIndex();
        }
        long aliasTime = System.nanoTime() - start;

        System.out.println("Binary Search: " + binarySearchTime / 1_000_000 + " ms");
        System.out.println("Memory Optimized: " + memOptTime / 1_000_000 + " ms");
        System.out.println("Alias Method: " + aliasTime / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        Solution solution = new Solution(new int[] { 1, 3 });

        // Pick 1000 times and check distribution
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            int pick = solution.pickIndex();
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Distribution for weights [1, 3]:");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test Case 2: Dynamic weights
        System.out.println("\n=== Test Case 2: Dynamic Weights ===");

        DynamicWeightSolution dynamicSolution = new DynamicWeightSolution(new int[] { 1, 2, 3 });
        System.out.println("Initial weights: " + dynamicSolution.getWeights());

        dynamicSolution.addWeight(4);
        System.out.println("After adding weight 4: " + dynamicSolution.getWeights());

        dynamicSolution.updateWeight(1, 5);
        System.out.println("After updating index 1 to weight 5: " + dynamicSolution.getWeights());

        // Sample from updated distribution
        counts.clear();
        for (int i = 0; i < 1000; i++) {
            int pick = dynamicSolution.pickIndex();
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Distribution after updates:");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test Case 3: Alias method
        System.out.println("\n=== Test Case 3: Alias Method ===");

        AliasMethodSolution aliasSolution = new AliasMethodSolution(new int[] { 1, 2, 3, 4 });

        counts.clear();
        for (int i = 0; i < 1000; i++) {
            int pick = aliasSolution.pickIndex();
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Alias method distribution for weights [1, 2, 3, 4]:");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test Case 4: Robust weight handling
        System.out.println("\n=== Test Case 4: Robust Weight Handling ===");

        RobustWeightSolution robustSolution = new RobustWeightSolution(new int[] { 0, 1, -2, 3, 0, 4 });
        System.out.println("Zero weight indices: " + robustSolution.getZeroWeightIndices());

        counts.clear();
        for (int i = 0; i < 1000; i++) {
            int pick = robustSolution.pickIndex();
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Distribution (only positive weights picked):");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test Case 5: Range-based picking
        System.out.println("\n=== Test Case 5: Range-based Picking ===");

        RangeBasedSolution rangeSolution = new RangeBasedSolution(new int[] { 1, 2, 3, 4, 5 });

        counts.clear();
        for (int i = 0; i < 1000; i++) {
            int pick = rangeSolution.pickIndexInRange(1, 3); // Pick from indices 1-3
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Range [1, 3] distribution:");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test multiple unique picks
        List<Integer> uniquePicks = rangeSolution.pickUniqueIndices(3);
        System.out.println("3 unique indices: " + uniquePicks);

        // Test Case 6: Statistical analysis
        System.out.println("\n=== Test Case 6: Statistical Analysis ===");

        StatisticalSolution statsSolution = new StatisticalSolution(new int[] { 1, 2, 3, 4 });

        // Generate many samples
        for (int i = 0; i < 10000; i++) {
            statsSolution.pickIndex();
        }

        System.out.println("Expected probabilities:");
        statsSolution.getExpectedProbabilities().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("Index %d: %.3f\n",
                        entry.getKey(), entry.getValue()));

        System.out.println("\nActual probabilities:");
        statsSolution.getActualProbabilities().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("Index %d: %.3f\n",
                        entry.getKey(), entry.getValue()));

        System.out.printf("Chi-squared statistic: %.3f\n", statsSolution.getChiSquaredStatistic());
        System.out.println("Total picks: " + statsSolution.getTotalPicks());

        // Test Case 7: Large weights
        System.out.println("\n=== Test Case 7: Large Weights ===");

        LongWeightSolution largeSolution = new LongWeightSolution(
                new int[] { 100000, 200000, 300000 });

        counts.clear();
        for (int i = 0; i < 1000; i++) {
            int pick = largeSolution.pickIndex();
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Large weights distribution:");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test Case 8: Edge cases
        System.out.println("\n=== Test Case 8: Edge Cases ===");

        // Single element
        Solution singleSolution = new Solution(new int[] { 42 });
        System.out.println("Single element pick: " + singleSolution.pickIndex()); // Should always be 0

        // All equal weights
        Solution equalSolution = new Solution(new int[] { 5, 5, 5, 5 });
        counts.clear();
        for (int i = 0; i < 400; i++) {
            int pick = equalSolution.pickIndex();
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Equal weights distribution:");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("Index %d: %d times\n",
                        entry.getKey(), entry.getValue()));

        // Test Case 9: Memory optimization
        System.out.println("\n=== Test Case 9: Memory Optimization ===");

        MemoryOptimizedSolution memSolution = new MemoryOptimizedSolution(new int[] { 1, 2, 3 });
        System.out.println("Total weight: " + memSolution.getTotalWeight());

        memSolution.updateWeight(1, 10);
        System.out.println("After updating index 1 to 10, total weight: " + memSolution.getTotalWeight());

        // Test Case 10: Thread safety
        System.out.println("\n=== Test Case 10: Thread Safety ===");

        ThreadSafeSolution threadSafeSolution = new ThreadSafeSolution(new int[] { 1, 2, 3, 4, 5 });
        Map<Integer, Integer> threadSafeCounts = new HashMap<>();

        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < 5; t++) {
            threads.add(new Thread(() -> {
                for (int i = 0; i < 200; i++) {
                    int pick = threadSafeSolution.pickIndex();
                    synchronized (threadSafeCounts) {
                        threadSafeCounts.put(pick, threadSafeCounts.getOrDefault(pick, 0) + 1);
                    }
                }
            }));
        }

        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("Thread-safe distribution:");
        threadSafeCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Performance testing
        performanceTest();

        System.out.println("\nRandom Pick with Weight testing completed successfully!");
    }
}
