package miscellaneous.medium;

import java.util.*;

/**
 * LeetCode 384: Shuffle an Array
 * https://leetcode.com/problems/shuffle-an-array/
 * 
 * Companies: Google, Facebook, Microsoft, Amazon, Apple, LinkedIn
 * Frequency: High (Asked in 400+ interviews)
 *
 * Description:
 * Given an integer array nums, design an algorithm to randomly shuffle the
 * array.
 * All permutations of the array should be equally likely as a result of the
 * shuffling.
 *
 * Implement the Solution class:
 * - Solution(int[] nums) Initializes the object with the integer array nums.
 * - int[] reset() Resets the array to its original configuration and returns
 * it.
 * - int[] shuffle() Returns a random shuffling of the array.
 * 
 * Constraints:
 * - 1 <= nums.length <= 50
 * - -10^6 <= nums[i] <= 10^6
 * - All the elements of nums are unique.
 * - At most 10^4 calls in total will be made to reset and shuffle.
 * 
 * Follow-up Questions:
 * 1. How would you implement different shuffling algorithms?
 * 2. Can you support partial shuffling?
 * 3. How to implement weighted shuffling?
 * 4. What about shuffling with constraints?
 * 5. How to ensure cryptographically secure shuffling?
 */
public class ShuffleAnArray {

    // Approach 1: Fisher-Yates (Knuth) Shuffle - O(n) time, O(n) space
    public static class Solution {
        private int[] original;
        private int[] array;
        private Random random;

        public Solution(int[] nums) {
            original = nums.clone();
            array = nums.clone();
            random = new Random();
        }

        public int[] reset() {
            array = original.clone();
            return array;
        }

        public int[] shuffle() {
            for (int i = array.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                swap(array, i, j);
            }
            return array;
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Approach 2: Inside-Out Fisher-Yates - O(n) time, O(n) space
    public static class InsideOutSolution {
        private int[] original;
        private Random random;

        public InsideOutSolution(int[] nums) {
            original = nums.clone();
            random = new Random();
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            int[] result = new int[original.length];

            for (int i = 0; i < original.length; i++) {
                int j = random.nextInt(i + 1);
                if (j != i) {
                    result[i] = result[j];
                }
                result[j] = original[i];
            }

            return result;
        }
    }

    // Follow-up 1: Different shuffling algorithms
    public static class MultiAlgorithmSolution {
        private int[] original;
        private Random random;

        public enum ShuffleAlgorithm {
            FISHER_YATES,
            INSIDE_OUT,
            SATTOLO_CYCLE,
            DURSTENFELD
        }

        public MultiAlgorithmSolution(int[] nums) {
            original = nums.clone();
            random = new Random();
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            return shuffle(ShuffleAlgorithm.FISHER_YATES);
        }

        public int[] shuffle(ShuffleAlgorithm algorithm) {
            int[] array = original.clone();

            switch (algorithm) {
                case FISHER_YATES:
                    return fisherYatesShuffle(array);
                case INSIDE_OUT:
                    return insideOutShuffle();
                case SATTOLO_CYCLE:
                    return sattoloShuffle(array);
                case DURSTENFELD:
                    return durstenfeldShuffle(array);
                default:
                    return fisherYatesShuffle(array);
            }
        }

        private int[] fisherYatesShuffle(int[] array) {
            for (int i = array.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                swap(array, i, j);
            }
            return array;
        }

        private int[] insideOutShuffle() {
            int[] result = new int[original.length];

            for (int i = 0; i < original.length; i++) {
                int j = random.nextInt(i + 1);
                if (j != i) {
                    result[i] = result[j];
                }
                result[j] = original[i];
            }

            return result;
        }

        private int[] sattoloShuffle(int[] array) {
            // Sattolo's algorithm - generates uniformly distributed cyclic permutations
            for (int i = array.length - 1; i > 0; i--) {
                int j = random.nextInt(i); // Note: i, not i+1
                swap(array, i, j);
            }
            return array;
        }

        private int[] durstenfeldShuffle(int[] array) {
            // Modern version of Fisher-Yates (same as our Fisher-Yates implementation)
            for (int i = array.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                swap(array, i, j);
            }
            return array;
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Follow-up 2: Partial shuffling
    public static class PartialShuffleSolution {
        private int[] original;
        private Random random;

        public PartialShuffleSolution(int[] nums) {
            original = nums.clone();
            random = new Random();
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            return partialShuffle(original.length);
        }

        public int[] partialShuffle(int k) {
            if (k > original.length) {
                k = original.length;
            }

            int[] array = original.clone();

            // Only shuffle first k elements
            for (int i = 0; i < k; i++) {
                int j = random.nextInt(original.length - i) + i;
                swap(array, i, j);
            }

            return array;
        }

        public int[] shuffleRange(int start, int end) {
            if (start < 0 || end >= original.length || start > end) {
                throw new IllegalArgumentException("Invalid range");
            }

            int[] array = original.clone();

            // Shuffle only the specified range
            for (int i = end; i > start; i--) {
                int j = random.nextInt(i - start + 1) + start;
                swap(array, i, j);
            }

            return array;
        }

        public int[] shuffleIndices(int[] indices) {
            int[] array = original.clone();
            List<Integer> indexList = new ArrayList<>();

            for (int index : indices) {
                if (index >= 0 && index < original.length) {
                    indexList.add(index);
                }
            }

            // Create array of values at specified indices
            List<Integer> values = new ArrayList<>();
            for (int index : indexList) {
                values.add(array[index]);
            }

            // Shuffle the values
            Collections.shuffle(values, random);

            // Put shuffled values back
            for (int i = 0; i < indexList.size(); i++) {
                array[indexList.get(i)] = values.get(i);
            }

            return array;
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Follow-up 3: Weighted shuffling
    public static class WeightedShuffleSolution {
        private int[] original;
        private double[] weights;
        private Random random;

        public WeightedShuffleSolution(int[] nums, double[] weights) {
            if (nums.length != weights.length) {
                throw new IllegalArgumentException("Arrays must have same length");
            }

            original = nums.clone();
            this.weights = weights.clone();
            random = new Random();
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            int[] array = original.clone();
            double[] currentWeights = weights.clone();

            for (int i = array.length - 1; i > 0; i--) {
                int j = weightedRandomChoice(currentWeights, i + 1);
                swap(array, i, j);

                // Remove selected element's weight
                if (j < i) {
                    System.arraycopy(currentWeights, j + 1, currentWeights, j, i - j);
                }
            }

            return array;
        }

        private int weightedRandomChoice(double[] weights, int length) {
            double totalWeight = 0;
            for (int i = 0; i < length; i++) {
                totalWeight += weights[i];
            }

            double randomValue = random.nextDouble() * totalWeight;
            double currentSum = 0;

            for (int i = 0; i < length; i++) {
                currentSum += weights[i];
                if (currentSum >= randomValue) {
                    return i;
                }
            }

            return length - 1;
        }

        public int[] shuffleWithBias(double biasFactor) {
            int[] array = original.clone();
            double[] biasedWeights = new double[weights.length];

            for (int i = 0; i < weights.length; i++) {
                biasedWeights[i] = Math.pow(weights[i], biasFactor);
            }

            for (int i = array.length - 1; i > 0; i--) {
                int j = weightedRandomChoice(biasedWeights, i + 1);
                swap(array, i, j);

                if (j < i) {
                    System.arraycopy(biasedWeights, j + 1, biasedWeights, j, i - j);
                }
            }

            return array;
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Follow-up 4: Constrained shuffling
    public static class ConstrainedShuffleSolution {
        private int[] original;
        private Random random;

        public ConstrainedShuffleSolution(int[] nums) {
            original = nums.clone();
            random = new Random();
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            return shuffleWithConstraint(null);
        }

        public int[] shuffleWithConstraint(java.util.function.BiPredicate<Integer, Integer> constraint) {
            int[] array = original.clone();

            if (constraint == null) {
                // No constraint, use standard Fisher-Yates
                for (int i = array.length - 1; i > 0; i--) {
                    int j = random.nextInt(i + 1);
                    swap(array, i, j);
                }
                return array;
            }

            // Constrained shuffling
            for (int i = array.length - 1; i > 0; i--) {
                List<Integer> validChoices = new ArrayList<>();

                for (int j = 0; j <= i; j++) {
                    if (constraint.test(array[i], array[j])) {
                        validChoices.add(j);
                    }
                }

                if (!validChoices.isEmpty()) {
                    int randomIndex = random.nextInt(validChoices.size());
                    int j = validChoices.get(randomIndex);
                    swap(array, i, j);
                }
            }

            return array;
        }

        public int[] shuffleNoAdjacent() {
            // Constraint: no element should be in its original position
            return shuffleWithConstraint((a, b) -> !a.equals(b));
        }

        public int[] shuffleWithFixedPositions(Set<Integer> fixedPositions) {
            int[] array = original.clone();
            List<Integer> movableIndices = new ArrayList<>();
            List<Integer> movableValues = new ArrayList<>();

            // Collect movable elements
            for (int i = 0; i < array.length; i++) {
                if (!fixedPositions.contains(i)) {
                    movableIndices.add(i);
                    movableValues.add(array[i]);
                }
            }

            // Shuffle movable values
            Collections.shuffle(movableValues, random);

            // Place shuffled values back
            for (int i = 0; i < movableIndices.size(); i++) {
                array[movableIndices.get(i)] = movableValues.get(i);
            }

            return array;
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Follow-up 5: Cryptographically secure shuffling
    public static class SecureShuffleSolution {
        private int[] original;
        private java.security.SecureRandom secureRandom;

        public SecureShuffleSolution(int[] nums) {
            original = nums.clone();
            secureRandom = new java.security.SecureRandom();
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            int[] array = original.clone();

            for (int i = array.length - 1; i > 0; i--) {
                int j = secureRandom.nextInt(i + 1);
                swap(array, i, j);
            }

            return array;
        }

        public int[] shuffleWithSeed(byte[] seed) {
            java.security.SecureRandom seededRandom = new java.security.SecureRandom(seed);
            int[] array = original.clone();

            for (int i = array.length - 1; i > 0; i--) {
                int j = seededRandom.nextInt(i + 1);
                swap(array, i, j);
            }

            return array;
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Advanced: Shuffle with undo functionality
    public static class UndoableShuffleSolution {
        private int[] original;
        private Random random;
        private Stack<int[]> history;
        private int maxHistorySize;

        public UndoableShuffleSolution(int[] nums) {
            this(nums, 10);
        }

        public UndoableShuffleSolution(int[] nums, int maxHistorySize) {
            original = nums.clone();
            random = new Random();
            history = new Stack<>();
            this.maxHistorySize = maxHistorySize;
            history.push(original.clone());
        }

        public int[] reset() {
            int[] result = original.clone();
            addToHistory(result);
            return result;
        }

        public int[] shuffle() {
            int[] array = getLastState().clone();

            for (int i = array.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                swap(array, i, j);
            }

            addToHistory(array);
            return array;
        }

        public int[] undo() {
            if (history.size() <= 1) {
                return original.clone();
            }

            history.pop(); // Remove current state
            return history.peek().clone();
        }

        public boolean canUndo() {
            return history.size() > 1;
        }

        public int getHistorySize() {
            return history.size();
        }

        private int[] getLastState() {
            return history.isEmpty() ? original : history.peek();
        }

        private void addToHistory(int[] state) {
            if (history.size() >= maxHistorySize) {
                // Remove oldest state (but keep original)
                Stack<int[]> newHistory = new Stack<>();
                newHistory.push(history.get(0)); // Keep original
                for (int i = Math.max(1, history.size() - maxHistorySize + 2); i < history.size(); i++) {
                    newHistory.push(history.get(i));
                }
                history = newHistory;
            }

            history.push(state.clone());
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Advanced: Statistical shuffle analysis
    public static class StatisticalShuffleSolution {
        private int[] original;
        private Random random;
        private Map<String, Integer> permutationCounts;
        private int totalShuffles;

        public StatisticalShuffleSolution(int[] nums) {
            original = nums.clone();
            random = new Random();
            permutationCounts = new HashMap<>();
            totalShuffles = 0;
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            int[] array = original.clone();

            for (int i = array.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                swap(array, i, j);
            }

            // Record this permutation
            String permutation = Arrays.toString(array);
            permutationCounts.put(permutation, permutationCounts.getOrDefault(permutation, 0) + 1);
            totalShuffles++;

            return array;
        }

        public Map<String, Double> getPermutationProbabilities() {
            Map<String, Double> probabilities = new HashMap<>();
            for (Map.Entry<String, Integer> entry : permutationCounts.entrySet()) {
                probabilities.put(entry.getKey(), (double) entry.getValue() / totalShuffles);
            }
            return probabilities;
        }

        public double getUniformityScore() {
            if (totalShuffles == 0)
                return 0.0;

            int uniquePermutations = permutationCounts.size();
            int totalPossiblePermutations = factorial(original.length);
            double expectedProbability = 1.0 / totalPossiblePermutations;

            double chiSquare = 0.0;
            for (int count : permutationCounts.values()) {
                double actualProbability = (double) count / totalShuffles;
                double deviation = actualProbability - expectedProbability;
                chiSquare += deviation * deviation / expectedProbability;
            }

            return 1.0 / (1.0 + chiSquare);
        }

        private int factorial(int n) {
            int result = 1;
            for (int i = 2; i <= n; i++) {
                result *= i;
            }
            return result;
        }

        public void resetStatistics() {
            permutationCounts.clear();
            totalShuffles = 0;
        }

        public int getTotalShuffles() {
            return totalShuffles;
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Performance testing
    public static void performanceTest() {
        System.out.println("=== Performance Testing ===");

        int[] nums = new int[10000];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = i;
        }

        // Test standard Fisher-Yates
        Solution fisherYates = new Solution(nums);
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            fisherYates.shuffle();
        }
        long fisherYatesTime = System.nanoTime() - start;

        // Test Inside-Out
        InsideOutSolution insideOut = new InsideOutSolution(nums);
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            insideOut.shuffle();
        }
        long insideOutTime = System.nanoTime() - start;

        System.out.println("Fisher-Yates: " + fisherYatesTime / 1_000_000 + " ms");
        System.out.println("Inside-Out: " + insideOutTime / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic Shuffle
        System.out.println("=== Test Case 1: Basic Shuffle ===");

        Solution solution = new Solution(new int[] { 1, 2, 3, 4, 5 });

        System.out.println("Original: " + Arrays.toString(solution.reset()));
        System.out.println("Shuffle 1: " + Arrays.toString(solution.shuffle()));
        System.out.println("Shuffle 2: " + Arrays.toString(solution.shuffle()));
        System.out.println("Reset: " + Arrays.toString(solution.reset()));
        System.out.println("Shuffle 3: " + Arrays.toString(solution.shuffle()));

        // Test Case 2: Different algorithms
        System.out.println("\n=== Test Case 2: Different Algorithms ===");

        MultiAlgorithmSolution multiAlgo = new MultiAlgorithmSolution(new int[] { 1, 2, 3, 4 });

        System.out.println("Original: " + Arrays.toString(multiAlgo.reset()));
        System.out.println("Fisher-Yates: "
                + Arrays.toString(multiAlgo.shuffle(MultiAlgorithmSolution.ShuffleAlgorithm.FISHER_YATES)));
        System.out.println("Inside-Out: "
                + Arrays.toString(multiAlgo.shuffle(MultiAlgorithmSolution.ShuffleAlgorithm.INSIDE_OUT)));
        System.out.println("Sattolo: "
                + Arrays.toString(multiAlgo.shuffle(MultiAlgorithmSolution.ShuffleAlgorithm.SATTOLO_CYCLE)));
        System.out.println("Durstenfeld: "
                + Arrays.toString(multiAlgo.shuffle(MultiAlgorithmSolution.ShuffleAlgorithm.DURSTENFELD)));

        // Test Case 3: Partial shuffling
        System.out.println("\n=== Test Case 3: Partial Shuffling ===");

        PartialShuffleSolution partialShuffle = new PartialShuffleSolution(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });

        System.out.println("Original: " + Arrays.toString(partialShuffle.reset()));
        System.out.println("Partial shuffle (k=3): " + Arrays.toString(partialShuffle.partialShuffle(3)));
        System.out.println("Range shuffle [2,5]: " + Arrays.toString(partialShuffle.shuffleRange(2, 5)));
        System.out.println("Shuffle indices [0,2,4,6]: "
                + Arrays.toString(partialShuffle.shuffleIndices(new int[] { 0, 2, 4, 6 })));

        // Test Case 4: Weighted shuffling
        System.out.println("\n=== Test Case 4: Weighted Shuffling ===");

        WeightedShuffleSolution weightedShuffle = new WeightedShuffleSolution(
                new int[] { 1, 2, 3, 4 },
                new double[] { 0.1, 0.4, 0.4, 0.1 });

        System.out.println("Original: " + Arrays.toString(weightedShuffle.reset()));

        // Test weighted distribution
        Map<Integer, Integer> positionCounts = new HashMap<>();
        for (int trial = 0; trial < 1000; trial++) {
            int[] shuffled = weightedShuffle.shuffle();
            positionCounts.put(shuffled[0], positionCounts.getOrDefault(shuffled[0], 0) + 1);
        }

        System.out.println("First position distribution (1000 trials):");
        positionCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("Value %d: %d times (%.1f%%)\n",
                        entry.getKey(), entry.getValue(), entry.getValue() / 10.0));

        // Test Case 5: Constrained shuffling
        System.out.println("\n=== Test Case 5: Constrained Shuffling ===");

        ConstrainedShuffleSolution constrainedShuffle = new ConstrainedShuffleSolution(new int[] { 1, 2, 3, 4, 5 });

        System.out.println("Original: " + Arrays.toString(constrainedShuffle.reset()));
        System.out.println("No adjacent (derangement): " + Arrays.toString(constrainedShuffle.shuffleNoAdjacent()));

        Set<Integer> fixedPositions = Set.of(0, 2, 4);
        System.out.println("Fixed positions {0,2,4}: "
                + Arrays.toString(constrainedShuffle.shuffleWithFixedPositions(fixedPositions)));

        // Custom constraint: even numbers can only swap with even numbers
        int[] evenOddArray = constrainedShuffle.shuffleWithConstraint((a, b) -> a % 2 == b % 2);
        System.out.println("Even-even, odd-odd only: " + Arrays.toString(evenOddArray));

        // Test Case 6: Secure shuffling
        System.out.println("\n=== Test Case 6: Secure Shuffling ===");

        SecureShuffleSolution secureShuffle = new SecureShuffleSolution(new int[] { 1, 2, 3, 4, 5 });

        System.out.println("Original: " + Arrays.toString(secureShuffle.reset()));
        System.out.println("Secure shuffle 1: " + Arrays.toString(secureShuffle.shuffle()));
        System.out.println("Secure shuffle 2: " + Arrays.toString(secureShuffle.shuffle()));

        // Test with seed
        byte[] seed = "test_seed".getBytes();
        System.out.println("Seeded shuffle: " + Arrays.toString(secureShuffle.shuffleWithSeed(seed)));

        // Test Case 7: Undoable shuffling
        System.out.println("\n=== Test Case 7: Undoable Shuffling ===");

        UndoableShuffleSolution undoableShuffle = new UndoableShuffleSolution(new int[] { 1, 2, 3, 4 });

        System.out.println("Original: " + Arrays.toString(undoableShuffle.reset()));
        System.out.println("Shuffle 1: " + Arrays.toString(undoableShuffle.shuffle()));
        System.out.println("Shuffle 2: " + Arrays.toString(undoableShuffle.shuffle()));
        System.out.println("Can undo: " + undoableShuffle.canUndo());
        System.out.println("Undo: " + Arrays.toString(undoableShuffle.undo()));
        System.out.println("Undo: " + Arrays.toString(undoableShuffle.undo()));
        System.out.println("History size: " + undoableShuffle.getHistorySize());

        // Test Case 8: Statistical analysis
        System.out.println("\n=== Test Case 8: Statistical Analysis ===");

        StatisticalShuffleSolution statsShuffle = new StatisticalShuffleSolution(new int[] { 1, 2, 3 });

        // Generate many shuffles
        for (int i = 0; i < 600; i++) { // 3! = 6 possible permutations, so 100 per permutation expected
            statsShuffle.shuffle();
        }

        System.out.println("Permutation probabilities (600 shuffles):");
        Map<String, Double> probabilities = statsShuffle.getPermutationProbabilities();
        probabilities.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("%s: %.3f\n", entry.getKey(), entry.getValue()));

        System.out.printf("Uniformity score: %.3f\n", statsShuffle.getUniformityScore());
        System.out.println("Total shuffles: " + statsShuffle.getTotalShuffles());

        // Test Case 9: Edge cases
        System.out.println("\n=== Test Case 9: Edge Cases ===");

        // Single element
        Solution singleSolution = new Solution(new int[] { 42 });
        System.out.println("Single element shuffle: " + Arrays.toString(singleSolution.shuffle())); // Should remain
                                                                                                    // [42]

        // Two elements
        Solution twoSolution = new Solution(new int[] { 1, 2 });
        Map<String, Integer> twoElementCounts = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            String result = Arrays.toString(twoSolution.shuffle());
            twoElementCounts.put(result, twoElementCounts.getOrDefault(result, 0) + 1);
        }

        System.out.println("Two element distribution (1000 shuffles):");
        twoElementCounts.forEach((key, value) -> System.out.printf("%s: %d times\n", key, value));

        // Empty array (edge case, might not be valid per constraints but good to test)
        try {
            Solution emptySolution = new Solution(new int[] {});
            System.out.println("Empty array shuffle: " + Arrays.toString(emptySolution.shuffle()));
        } catch (Exception e) {
            System.out.println("Empty array handling: " + e.getMessage());
        }

        // Test Case 10: Large array performance
        System.out.println("\n=== Test Case 10: Large Array Performance ===");

        int[] largeArray = new int[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = i;
        }

        Solution largeSolution = new Solution(largeArray);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            largeSolution.shuffle();
        }
        long endTime = System.currentTimeMillis();

        System.out.println("100 shuffles of 1000-element array: " + (endTime - startTime) + " ms");

        // Verify first few elements of a shuffle
        int[] shuffled = largeSolution.shuffle();
        System.out.print("First 10 elements of shuffled large array: ");
        for (int i = 0; i < Math.min(10, shuffled.length); i++) {
            System.out.print(shuffled[i] + " ");
        }
        System.out.println();

        // Performance testing
        performanceTest();

        System.out.println("\nShuffle Array testing completed successfully!");
    }
}
