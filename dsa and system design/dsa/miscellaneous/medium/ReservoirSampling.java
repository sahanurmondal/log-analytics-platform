package miscellaneous.medium;

import java.util.*;

/**
 * LeetCode 398: Random Pick Index / Reservoir Sampling
 * https://leetcode.com/problems/random-pick-index/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, LinkedIn, Uber
 * Frequency: Very High (Asked in 500+ interviews)
 *
 * Description:
 * Given an integer array nums with possible duplicates, randomly output the
 * index of a given target number.
 * You can assume that the given target number must exist in the array.
 *
 * Implement the Solution class:
 * - Solution(int[] nums) Initializes the object with the array nums.
 * - int pick(int target) Picks a random index i from nums where nums[i] ==
 * target.
 * If there are multiple valid i's, then each index should have an equal
 * probability of returning.
 * 
 * Follow-up Questions:
 * 1. How to implement true reservoir sampling for streams?
 * 2. Can you handle very large datasets that don't fit in memory?
 * 3. How to sample k elements instead of just one?
 * 4. What about weighted reservoir sampling?
 * 5. How to maintain reservoir when elements are deleted?
 */
public class ReservoirSampling {

    // Approach 1: Reservoir Sampling - O(n) space preprocessing, O(n) pick
    public static class Solution {
        private int[] nums;
        private Random random;

        public Solution(int[] nums) {
            this.nums = nums;
            this.random = new Random();
        }

        public int pick(int target) {
            int result = -1;
            int count = 0;

            for (int i = 0; i < nums.length; i++) {
                if (nums[i] == target) {
                    count++;
                    // With probability 1/count, pick this index
                    if (random.nextInt(count) == 0) {
                        result = i;
                    }
                }
            }

            return result;
        }
    }

    // Approach 2: HashMap preprocessing - O(n) space, O(1) pick
    public static class OptimizedSolution {
        private Map<Integer, List<Integer>> indices;
        private Random random;

        public OptimizedSolution(int[] nums) {
            indices = new HashMap<>();
            random = new Random();

            for (int i = 0; i < nums.length; i++) {
                indices.computeIfAbsent(nums[i], k -> new ArrayList<>()).add(i);
            }
        }

        public int pick(int target) {
            List<Integer> targetIndices = indices.get(target);
            return targetIndices.get(random.nextInt(targetIndices.size()));
        }
    }

    // Follow-up 1: True Reservoir Sampling for streams
    public static class StreamReservoirSampling {
        private Random random;
        private int result;
        private int count;

        public StreamReservoirSampling() {
            random = new Random();
            result = -1;
            count = 0;
        }

        public void addElement(int value, int index) {
            count++;
            // With probability 1/count, replace the current result
            if (random.nextInt(count) == 0) {
                result = index;
            }
        }

        public int getResult() {
            return result;
        }

        public void reset() {
            result = -1;
            count = 0;
        }

        public int getCount() {
            return count;
        }
    }

    // Follow-up 2: Memory-efficient version for large datasets
    public static class MemoryEfficientSolution {
        private Iterator<Integer> dataSource;
        private Random random;
        private int target;

        public MemoryEfficientSolution(Iterator<Integer> dataSource) {
            this.dataSource = dataSource;
            this.random = new Random();
        }

        public int pick(int target) {
            this.target = target;
            int result = -1;
            int count = 0;
            int index = 0;

            // Reset iterator if needed (in real scenarios, this would be a fresh stream)
            while (dataSource.hasNext()) {
                int value = dataSource.next();
                if (value == target) {
                    count++;
                    if (random.nextInt(count) == 0) {
                        result = index;
                    }
                }
                index++;
            }

            return result;
        }
    }

    // Follow-up 3: Sample k elements using reservoir sampling
    public static class KReservoirSampling {
        private int[] nums;
        private Random random;

        public KReservoirSampling(int[] nums) {
            this.nums = nums;
            this.random = new Random();
        }

        public List<Integer> pickK(int target, int k) {
            List<Integer> reservoir = new ArrayList<>();
            int count = 0;

            for (int i = 0; i < nums.length; i++) {
                if (nums[i] == target) {
                    count++;
                    if (reservoir.size() < k) {
                        reservoir.add(i);
                    } else {
                        // Replace element with probability k/count
                        int replaceIndex = random.nextInt(count);
                        if (replaceIndex < k) {
                            reservoir.set(replaceIndex, i);
                        }
                    }
                }
            }

            return reservoir;
        }

        public List<Integer> pickKDistinct(int k) {
            if (k > nums.length) {
                throw new IllegalArgumentException("k cannot be larger than array size");
            }

            List<Integer> reservoir = new ArrayList<>();

            // Fill reservoir with first k elements
            for (int i = 0; i < Math.min(k, nums.length); i++) {
                reservoir.add(i);
            }

            // For remaining elements, randomly replace
            for (int i = k; i < nums.length; i++) {
                int replaceIndex = random.nextInt(i + 1);
                if (replaceIndex < k) {
                    reservoir.set(replaceIndex, i);
                }
            }

            return reservoir;
        }
    }

    // Follow-up 4: Weighted Reservoir Sampling
    public static class WeightedReservoirSampling {
        private int[] nums;
        private int[] weights;
        private Random random;

        public WeightedReservoirSampling(int[] nums, int[] weights) {
            this.nums = nums;
            this.weights = weights;
            this.random = new Random();
        }

        public int pickWeighted(int target) {
            int result = -1;
            double totalWeight = 0.0;

            for (int i = 0; i < nums.length; i++) {
                if (nums[i] == target) {
                    totalWeight += weights[i];
                    // With probability weight[i] / totalWeight, pick this index
                    if (random.nextDouble() * totalWeight <= weights[i]) {
                        result = i;
                    }
                }
            }

            return result;
        }

        public List<Integer> pickKWeighted(int target, int k) {
            List<Integer> candidates = new ArrayList<>();
            List<Double> candidateWeights = new ArrayList<>();

            // Collect all candidates with their weights
            for (int i = 0; i < nums.length; i++) {
                if (nums[i] == target) {
                    candidates.add(i);
                    candidateWeights.add((double) weights[i]);
                }
            }

            if (candidates.size() <= k) {
                return candidates;
            }

            // Use weighted reservoir sampling
            List<Integer> reservoir = new ArrayList<>();
            List<Double> reservoirWeights = new ArrayList<>();

            for (int i = 0; i < candidates.size(); i++) {
                int candidate = candidates.get(i);
                double weight = candidateWeights.get(i);

                if (reservoir.size() < k) {
                    reservoir.add(candidate);
                    reservoirWeights.add(weight);
                } else {
                    // Calculate total weight in reservoir
                    double totalReservoirWeight = reservoirWeights.stream().mapToDouble(Double::doubleValue).sum();
                    double acceptanceProbability = weight / (totalReservoirWeight + weight);

                    if (random.nextDouble() < acceptanceProbability) {
                        // Remove random element and add new one
                        int removeIndex = random.nextInt(k);
                        reservoir.set(removeIndex, candidate);
                        reservoirWeights.set(removeIndex, weight);
                    }
                }
            }

            return reservoir;
        }
    }

    // Follow-up 5: Dynamic Reservoir Sampling with deletions
    public static class DynamicReservoirSampling {
        private List<Integer> nums;
        private Map<Integer, List<Integer>> indices;
        private Random random;

        public DynamicReservoirSampling() {
            nums = new ArrayList<>();
            indices = new HashMap<>();
            random = new Random();
        }

        public void addElement(int value) {
            int index = nums.size();
            nums.add(value);
            indices.computeIfAbsent(value, k -> new ArrayList<>()).add(index);
        }

        public boolean removeElement(int value) {
            List<Integer> valueIndices = indices.get(value);
            if (valueIndices == null || valueIndices.isEmpty()) {
                return false;
            }

            // Remove last occurrence
            int indexToRemove = valueIndices.remove(valueIndices.size() - 1);

            // Mark as deleted (in real implementation, might use a different strategy)
            nums.set(indexToRemove, null);

            if (valueIndices.isEmpty()) {
                indices.remove(value);
            }

            return true;
        }

        public int pick(int target) {
            List<Integer> targetIndices = indices.get(target);
            if (targetIndices == null || targetIndices.isEmpty()) {
                return -1;
            }

            // Use reservoir sampling on valid indices
            int result = -1;
            int count = 0;

            for (int index : targetIndices) {
                if (nums.get(index) != null) { // Not deleted
                    count++;
                    if (random.nextInt(count) == 0) {
                        result = index;
                    }
                }
            }

            return result;
        }

        public List<Integer> getAllElements() {
            return new ArrayList<>(nums);
        }

        public void cleanup() {
            // Remove null elements and update indices
            List<Integer> newNums = new ArrayList<>();
            Map<Integer, List<Integer>> newIndices = new HashMap<>();

            for (int i = 0; i < nums.size(); i++) {
                Integer value = nums.get(i);
                if (value != null) {
                    int newIndex = newNums.size();
                    newNums.add(value);
                    newIndices.computeIfAbsent(value, k -> new ArrayList<>()).add(newIndex);
                }
            }

            nums = newNums;
            indices = newIndices;
        }
    }

    // Advanced: Parallel Reservoir Sampling
    public static class ParallelReservoirSampling {
        private int[] nums;
        private Random random;

        public ParallelReservoirSampling(int[] nums) {
            this.nums = nums;
            this.random = new Random();
        }

        public int pickParallel(int target) {
            // This is a simplified version - in practice, you'd use actual parallel
            // processing
            List<Integer> candidates = new ArrayList<>();

            // Simulate parallel collection
            for (int i = 0; i < nums.length; i++) {
                if (nums[i] == target) {
                    candidates.add(i);
                }
            }

            if (candidates.isEmpty()) {
                return -1;
            }

            return candidates.get(random.nextInt(candidates.size()));
        }
    }

    // Advanced: Reservoir Sampling with history tracking
    public static class HistoryTrackingReservoirSampling {
        private int[] nums;
        private Random random;
        private Map<Integer, List<Integer>> pickHistory;
        private Map<Integer, Integer> pickCounts;

        public HistoryTrackingReservoirSampling(int[] nums) {
            this.nums = nums;
            this.random = new Random();
            this.pickHistory = new HashMap<>();
            this.pickCounts = new HashMap<>();
        }

        public int pick(int target) {
            int result = -1;
            int count = 0;

            for (int i = 0; i < nums.length; i++) {
                if (nums[i] == target) {
                    count++;
                    if (random.nextInt(count) == 0) {
                        result = i;
                    }
                }
            }

            // Track history
            pickHistory.computeIfAbsent(target, k -> new ArrayList<>()).add(result);
            pickCounts.put(target, pickCounts.getOrDefault(target, 0) + 1);

            return result;
        }

        public List<Integer> getPickHistory(int target) {
            return new ArrayList<>(pickHistory.getOrDefault(target, new ArrayList<>()));
        }

        public int getPickCount(int target) {
            return pickCounts.getOrDefault(target, 0);
        }

        public Map<Integer, Double> getPickDistribution(int target) {
            List<Integer> history = pickHistory.getOrDefault(target, new ArrayList<>());
            Map<Integer, Integer> indexCounts = new HashMap<>();

            for (int index : history) {
                indexCounts.put(index, indexCounts.getOrDefault(index, 0) + 1);
            }

            Map<Integer, Double> distribution = new HashMap<>();
            int totalPicks = history.size();

            for (Map.Entry<Integer, Integer> entry : indexCounts.entrySet()) {
                distribution.put(entry.getKey(), (double) entry.getValue() / totalPicks);
            }

            return distribution;
        }

        public void clearHistory() {
            pickHistory.clear();
            pickCounts.clear();
        }
    }

    // Advanced: Adaptive Reservoir Sampling
    public static class AdaptiveReservoirSampling {
        private int[] nums;
        private Random random;
        private Map<Integer, Integer> targetCounts;
        private boolean useOptimized;
        private Map<Integer, List<Integer>> indices;

        public AdaptiveReservoirSampling(int[] nums) {
            this.nums = nums;
            this.random = new Random();
            this.targetCounts = new HashMap<>();

            // Count occurrences
            for (int num : nums) {
                targetCounts.put(num, targetCounts.getOrDefault(num, 0) + 1);
            }

            // Use optimized approach if average target frequency is high
            double avgFrequency = targetCounts.values().stream().mapToInt(Integer::intValue).average().orElse(0);
            useOptimized = avgFrequency > nums.length * 0.1; // More than 10% frequency

            if (useOptimized) {
                indices = new HashMap<>();
                for (int i = 0; i < nums.length; i++) {
                    indices.computeIfAbsent(nums[i], k -> new ArrayList<>()).add(i);
                }
            }
        }

        public int pick(int target) {
            if (useOptimized && indices.containsKey(target)) {
                List<Integer> targetIndices = indices.get(target);
                return targetIndices.get(random.nextInt(targetIndices.size()));
            } else {
                // Use reservoir sampling
                int result = -1;
                int count = 0;

                for (int i = 0; i < nums.length; i++) {
                    if (nums[i] == target) {
                        count++;
                        if (random.nextInt(count) == 0) {
                            result = i;
                        }
                    }
                }

                return result;
            }
        }

        public boolean isUsingOptimized() {
            return useOptimized;
        }
    }

    // Performance testing
    public static void performanceTest() {
        System.out.println("=== Performance Testing ===");

        Random rand = new Random(42);
        int[] nums = new int[100000];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = rand.nextInt(1000); // Values from 0 to 999
        }

        // Test reservoir sampling
        Solution reservoirSolution = new Solution(nums);
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            reservoirSolution.pick(rand.nextInt(1000));
        }
        long reservoirTime = System.nanoTime() - start;

        // Test optimized solution
        OptimizedSolution optimizedSolution = new OptimizedSolution(nums);
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            optimizedSolution.pick(rand.nextInt(1000));
        }
        long optimizedTime = System.nanoTime() - start;

        // Test adaptive solution
        AdaptiveReservoirSampling adaptiveSolution = new AdaptiveReservoirSampling(nums);
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            adaptiveSolution.pick(rand.nextInt(1000));
        }
        long adaptiveTime = System.nanoTime() - start;

        System.out.println("Reservoir Sampling: " + reservoirTime / 1_000_000 + " ms");
        System.out.println("Optimized (HashMap): " + optimizedTime / 1_000_000 + " ms");
        System.out.println("Adaptive: " + adaptiveTime / 1_000_000 + " ms");
        System.out.println("Adaptive using optimized: " + adaptiveSolution.isUsingOptimized());
    }

    public static void main(String[] args) {
        // Test Case 1: Basic Reservoir Sampling
        System.out.println("=== Test Case 1: Basic Reservoir Sampling ===");

        Solution solution = new Solution(new int[] { 1, 2, 3, 3, 3 });

        // Pick target 3 multiple times
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            int pick = solution.pick(3);
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Distribution for target 3 (indices 2, 3, 4):");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test Case 2: Optimized Solution
        System.out.println("\n=== Test Case 2: Optimized Solution ===");

        OptimizedSolution optimizedSolution = new OptimizedSolution(new int[] { 1, 2, 3, 3, 3 });

        counts.clear();
        for (int i = 0; i < 1000; i++) {
            int pick = optimizedSolution.pick(3);
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Optimized distribution for target 3:");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test Case 3: Stream Reservoir Sampling
        System.out.println("\n=== Test Case 3: Stream Reservoir Sampling ===");

        StreamReservoirSampling streamSampling = new StreamReservoirSampling();

        // Simulate streaming data
        int[] streamData = { 5, 3, 8, 3, 1, 3, 9, 3 };
        for (int i = 0; i < streamData.length; i++) {
            if (streamData[i] == 3) {
                streamSampling.addElement(streamData[i], i);
                System.out.println("After adding 3 at index " + i + ", current result: " + streamSampling.getResult());
            }
        }

        System.out.println("Final result index: " + streamSampling.getResult());
        System.out.println("Total count of target: " + streamSampling.getCount());

        // Test Case 4: K Reservoir Sampling
        System.out.println("\n=== Test Case 4: K Reservoir Sampling ===");

        KReservoirSampling kSampling = new KReservoirSampling(new int[] { 1, 3, 2, 3, 4, 3, 5, 3 });

        List<Integer> k3Samples = kSampling.pickK(3, 2);
        System.out.println("2 samples for target 3: " + k3Samples);

        List<Integer> k3Distinct = kSampling.pickKDistinct(4);
        System.out.println("4 distinct indices: " + k3Distinct);

        // Verify distribution of K sampling
        Map<String, Integer> kCounts = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            List<Integer> samples = kSampling.pickK(3, 2);
            Collections.sort(samples);
            String key = samples.toString();
            kCounts.put(key, kCounts.getOrDefault(key, 0) + 1);
        }

        System.out.println("K=2 sampling distribution:");
        kCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.printf("%s: %d times\n", entry.getKey(), entry.getValue()));

        // Test Case 5: Weighted Reservoir Sampling
        System.out.println("\n=== Test Case 5: Weighted Reservoir Sampling ===");

        WeightedReservoirSampling weightedSampling = new WeightedReservoirSampling(
                new int[] { 1, 3, 2, 3, 4, 3 },
                new int[] { 1, 5, 1, 2, 1, 3 });

        counts.clear();
        for (int i = 0; i < 1000; i++) {
            int pick = weightedSampling.pickWeighted(3);
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("Weighted distribution for target 3 (weights 5, 2, 3 at indices 1, 3, 5):");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percentage = (double) entry.getValue() / 1000 * 100;
                    System.out.printf("Index %d: %d times (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage);
                });

        // Test Case 6: Dynamic Reservoir Sampling
        System.out.println("\n=== Test Case 6: Dynamic Reservoir Sampling ===");

        DynamicReservoirSampling dynamicSampling = new DynamicReservoirSampling();

        // Add elements
        int[] elements = { 1, 3, 2, 3, 4, 3 };
        for (int element : elements) {
            dynamicSampling.addElement(element);
        }

        System.out.println("Initial elements: " + dynamicSampling.getAllElements());
        System.out.println("Pick 3: " + dynamicSampling.pick(3));

        // Remove an element
        dynamicSampling.removeElement(3);
        System.out.println("After removing one 3: " + dynamicSampling.getAllElements());
        System.out.println("Pick 3: " + dynamicSampling.pick(3));

        dynamicSampling.cleanup();
        System.out.println("After cleanup: " + dynamicSampling.getAllElements());

        // Test Case 7: History Tracking
        System.out.println("\n=== Test Case 7: History Tracking ===");

        HistoryTrackingReservoirSampling historySampling = new HistoryTrackingReservoirSampling(
                new int[] { 1, 3, 2, 3, 4, 3 });

        // Make multiple picks
        for (int i = 0; i < 10; i++) {
            historySampling.pick(3);
        }

        System.out.println("Pick history for target 3: " + historySampling.getPickHistory(3));
        System.out.println("Pick count for target 3: " + historySampling.getPickCount(3));
        System.out.println("Pick distribution: " + historySampling.getPickDistribution(3));

        // Test Case 8: Adaptive Sampling
        System.out.println("\n=== Test Case 8: Adaptive Sampling ===");

        // Low frequency array
        int[] lowFreq = new int[1000];
        Arrays.fill(lowFreq, 1);
        lowFreq[500] = 2; // Only one occurrence of 2

        AdaptiveReservoirSampling adaptiveLow = new AdaptiveReservoirSampling(lowFreq);
        System.out.println("Low frequency adaptive using optimized: " + adaptiveLow.isUsingOptimized());

        // High frequency array
        int[] highFreq = new int[1000];
        for (int i = 0; i < 1000; i++) {
            highFreq[i] = i % 5; // Values 0-4, each appearing 200 times
        }

        AdaptiveReservoirSampling adaptiveHigh = new AdaptiveReservoirSampling(highFreq);
        System.out.println("High frequency adaptive using optimized: " + adaptiveHigh.isUsingOptimized());

        // Test Case 9: Large dataset test
        System.out.println("\n=== Test Case 9: Large Dataset Test ===");

        Random rand = new Random(42);
        int[] largeArray = new int[10000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = rand.nextInt(100);
        }

        Solution largeSolution = new Solution(largeArray);

        long startTime = System.currentTimeMillis();
        Map<Integer, Integer> largeCounts = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            int pick = largeSolution.pick(42);
            largeCounts.put(pick, largeCounts.getOrDefault(pick, 0) + 1);
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Large dataset test completed in " + (endTime - startTime) + " ms");
        System.out.println("Number of different indices picked for target 42: " + largeCounts.size());

        // Test Case 10: Edge cases
        System.out.println("\n=== Test Case 10: Edge Cases ===");

        // Single element
        Solution singleSolution = new Solution(new int[] { 42 });
        System.out.println("Single element pick: " + singleSolution.pick(42)); // Should always be 0

        // All same elements
        Solution allSameSolution = new Solution(new int[] { 5, 5, 5, 5, 5 });
        counts.clear();
        for (int i = 0; i < 500; i++) {
            int pick = allSameSolution.pick(5);
            counts.put(pick, counts.getOrDefault(pick, 0) + 1);
        }

        System.out.println("All same elements distribution:");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("Index %d: %d times\n",
                        entry.getKey(), entry.getValue()));

        // Performance testing
        performanceTest();

        System.out.println("\nReservoir Sampling testing completed successfully!");
    }
}
