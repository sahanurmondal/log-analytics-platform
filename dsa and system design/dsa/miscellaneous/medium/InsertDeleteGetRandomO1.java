package miscellaneous.medium;

import java.util.*;

/**
 * LeetCode 380: Insert Delete GetRandom O(1)
 * https://leetcode.com/problems/insert-delete-getrandom-o1/
 * 
 * Companies: Meta, Amazon, Google, Microsoft, Apple, Bloomberg, Uber
 * Frequency: Very High (Asked in 700+ interviews)
 *
 * Description:
 * Implement the RandomizedSet class:
 * - RandomizedSet() Initializes the RandomizedSet object.
 * - bool insert(int val) Inserts an item val into the set if not present.
 * Returns true if the item was not present, false otherwise.
 * - bool remove(int val) Removes an item val from the set if present. Returns
 * true if the item was present, false otherwise.
 * - int getRandom() Returns a random element from the current set of elements
 * (it's guaranteed that at least one element exists when this method is
 * called).
 * 
 * Each function must work in average O(1) time complexity.
 *
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 2 * 10^5 calls will be made to insert, remove, and getRandom.
 * - There will be at least one element in the data structure when getRandom is
 * called.
 * 
 * Follow-up Questions:
 * 1. How would you implement with duplicates allowed?
 * 2. Can you implement thread-safe version?
 * 3. How to handle weighted random selection?
 * 4. What about range queries?
 * 5. Can you optimize for memory usage?
 */
public class InsertDeleteGetRandomO1 {

    // Approach 1: ArrayList + HashMap - O(1) average time, O(n) space
    public static class RandomizedSet {
        private List<Integer> nums;
        private Map<Integer, Integer> valToIndex;
        private Random random;

        public RandomizedSet() {
            nums = new ArrayList<>();
            valToIndex = new HashMap<>();
            random = new Random();
        }

        public boolean insert(int val) {
            if (valToIndex.containsKey(val)) {
                return false;
            }

            valToIndex.put(val, nums.size());
            nums.add(val);
            return true;
        }

        public boolean remove(int val) {
            if (!valToIndex.containsKey(val)) {
                return false;
            }

            int index = valToIndex.get(val);
            int lastElement = nums.get(nums.size() - 1);

            // Move last element to the position of element to remove
            nums.set(index, lastElement);
            valToIndex.put(lastElement, index);

            // Remove last element
            nums.remove(nums.size() - 1);
            valToIndex.remove(val);

            return true;
        }

        public int getRandom() {
            return nums.get(random.nextInt(nums.size()));
        }
    }

    // Follow-up 1: RandomizedSet with duplicates allowed
    public static class RandomizedMultiset {
        private List<Integer> nums;
        private Map<Integer, Set<Integer>> valToIndices;
        private Random random;

        public RandomizedMultiset() {
            nums = new ArrayList<>();
            valToIndices = new HashMap<>();
            random = new Random();
        }

        public boolean insert(int val) {
            valToIndices.computeIfAbsent(val, k -> new HashSet<>()).add(nums.size());
            nums.add(val);
            return valToIndices.get(val).size() == 1; // true if first occurrence
        }

        public boolean remove(int val) {
            if (!valToIndices.containsKey(val) || valToIndices.get(val).isEmpty()) {
                return false;
            }

            Set<Integer> indices = valToIndices.get(val);
            int indexToRemove = indices.iterator().next();
            indices.remove(indexToRemove);

            int lastElement = nums.get(nums.size() - 1);
            nums.set(indexToRemove, lastElement);

            // Update indices for the moved element
            if (lastElement != val) {
                valToIndices.get(lastElement).remove(nums.size() - 1);
                valToIndices.get(lastElement).add(indexToRemove);
            }

            nums.remove(nums.size() - 1);

            if (indices.isEmpty()) {
                valToIndices.remove(val);
            }

            return true;
        }

        public int getRandom() {
            return nums.get(random.nextInt(nums.size()));
        }

        public int getCount(int val) {
            return valToIndices.getOrDefault(val, Collections.emptySet()).size();
        }
    }

    // Follow-up 2: Thread-safe version
    public static class ThreadSafeRandomizedSet {
        private final List<Integer> nums;
        private final Map<Integer, Integer> valToIndex;
        private final Random random;
        private final Object lock = new Object();

        public ThreadSafeRandomizedSet() {
            nums = new ArrayList<>();
            valToIndex = new HashMap<>();
            random = new Random();
        }

        public boolean insert(int val) {
            synchronized (lock) {
                if (valToIndex.containsKey(val)) {
                    return false;
                }

                valToIndex.put(val, nums.size());
                nums.add(val);
                return true;
            }
        }

        public boolean remove(int val) {
            synchronized (lock) {
                if (!valToIndex.containsKey(val)) {
                    return false;
                }

                int index = valToIndex.get(val);
                int lastElement = nums.get(nums.size() - 1);

                nums.set(index, lastElement);
                valToIndex.put(lastElement, index);

                nums.remove(nums.size() - 1);
                valToIndex.remove(val);

                return true;
            }
        }

        public int getRandom() {
            synchronized (lock) {
                if (nums.isEmpty()) {
                    throw new IllegalStateException("Set is empty");
                }
                return nums.get(random.nextInt(nums.size()));
            }
        }

        public int size() {
            synchronized (lock) {
                return nums.size();
            }
        }

        public boolean isEmpty() {
            synchronized (lock) {
                return nums.isEmpty();
            }
        }
    }

    // Follow-up 3: Weighted random selection
    public static class WeightedRandomizedSet {
        private List<Integer> nums;
        private List<Integer> weights;
        private Map<Integer, Integer> valToIndex;
        private Random random;
        private int totalWeight;

        public WeightedRandomizedSet() {
            nums = new ArrayList<>();
            weights = new ArrayList<>();
            valToIndex = new HashMap<>();
            random = new Random();
            totalWeight = 0;
        }

        public boolean insert(int val, int weight) {
            if (valToIndex.containsKey(val)) {
                return false;
            }

            valToIndex.put(val, nums.size());
            nums.add(val);
            weights.add(weight);
            totalWeight += weight;
            return true;
        }

        public boolean remove(int val) {
            if (!valToIndex.containsKey(val)) {
                return false;
            }

            int index = valToIndex.get(val);
            int lastElement = nums.get(nums.size() - 1);
            int removedWeight = weights.get(index);

            nums.set(index, lastElement);
            weights.set(index, weights.get(weights.size() - 1));
            valToIndex.put(lastElement, index);

            nums.remove(nums.size() - 1);
            weights.remove(weights.size() - 1);
            valToIndex.remove(val);
            totalWeight -= removedWeight;

            return true;
        }

        public int getWeightedRandom() {
            if (totalWeight == 0) {
                throw new IllegalStateException("No elements with positive weight");
            }

            int randomWeight = random.nextInt(totalWeight);
            int currentWeight = 0;

            for (int i = 0; i < nums.size(); i++) {
                currentWeight += weights.get(i);
                if (currentWeight > randomWeight) {
                    return nums.get(i);
                }
            }

            return nums.get(nums.size() - 1); // Should never reach here
        }

        public int getRandom() {
            return nums.get(random.nextInt(nums.size()));
        }

        public boolean updateWeight(int val, int newWeight) {
            if (!valToIndex.containsKey(val)) {
                return false;
            }

            int index = valToIndex.get(val);
            int oldWeight = weights.get(index);
            weights.set(index, newWeight);
            totalWeight = totalWeight - oldWeight + newWeight;
            return true;
        }
    }

    // Follow-up 4: Range queries support
    public static class RangeRandomizedSet {
        private List<Integer> nums;
        private Map<Integer, Integer> valToIndex;
        private Random random;

        public RangeRandomizedSet() {
            nums = new ArrayList<>();
            valToIndex = new HashMap<>();
            random = new Random();
        }

        public boolean insert(int val) {
            if (valToIndex.containsKey(val)) {
                return false;
            }

            valToIndex.put(val, nums.size());
            nums.add(val);
            return true;
        }

        public boolean remove(int val) {
            if (!valToIndex.containsKey(val)) {
                return false;
            }

            int index = valToIndex.get(val);
            int lastElement = nums.get(nums.size() - 1);

            nums.set(index, lastElement);
            valToIndex.put(lastElement, index);

            nums.remove(nums.size() - 1);
            valToIndex.remove(val);

            return true;
        }

        public int getRandom() {
            return nums.get(random.nextInt(nums.size()));
        }

        public int getRandomInRange(int min, int max) {
            List<Integer> inRange = new ArrayList<>();
            for (int num : nums) {
                if (num >= min && num <= max) {
                    inRange.add(num);
                }
            }

            if (inRange.isEmpty()) {
                throw new IllegalStateException("No elements in range [" + min + ", " + max + "]");
            }

            return inRange.get(random.nextInt(inRange.size()));
        }

        public List<Integer> getElementsInRange(int min, int max) {
            List<Integer> result = new ArrayList<>();
            for (int num : nums) {
                if (num >= min && num <= max) {
                    result.add(num);
                }
            }
            return result;
        }

        public int countInRange(int min, int max) {
            int count = 0;
            for (int num : nums) {
                if (num >= min && num <= max) {
                    count++;
                }
            }
            return count;
        }
    }

    // Follow-up 5: Memory optimized version using primitive arrays
    public static class MemoryOptimizedRandomizedSet {
        private int[] nums;
        private Map<Integer, Integer> valToIndex;
        private Random random;
        private int size;
        private int capacity;

        public MemoryOptimizedRandomizedSet() {
            this(16); // Initial capacity
        }

        public MemoryOptimizedRandomizedSet(int initialCapacity) {
            nums = new int[initialCapacity];
            valToIndex = new HashMap<>();
            random = new Random();
            size = 0;
            capacity = initialCapacity;
        }

        public boolean insert(int val) {
            if (valToIndex.containsKey(val)) {
                return false;
            }

            if (size == capacity) {
                resize();
            }

            valToIndex.put(val, size);
            nums[size] = val;
            size++;
            return true;
        }

        public boolean remove(int val) {
            if (!valToIndex.containsKey(val)) {
                return false;
            }

            int index = valToIndex.get(val);
            int lastElement = nums[size - 1];

            nums[index] = lastElement;
            valToIndex.put(lastElement, index);

            size--;
            valToIndex.remove(val);

            return true;
        }

        public int getRandom() {
            if (size == 0) {
                throw new IllegalStateException("Set is empty");
            }
            return nums[random.nextInt(size)];
        }

        private void resize() {
            capacity *= 2;
            int[] newNums = new int[capacity];
            System.arraycopy(nums, 0, newNums, 0, size);
            nums = newNums;
        }

        public int size() {
            return size;
        }

        public int capacity() {
            return capacity;
        }
    }

    // Advanced: RandomizedSet with statistics
    public static class StatisticalRandomizedSet {
        private List<Integer> nums;
        private Map<Integer, Integer> valToIndex;
        private Random random;
        private Map<Integer, Integer> accessCount;
        private long totalOperations;

        public StatisticalRandomizedSet() {
            nums = new ArrayList<>();
            valToIndex = new HashMap<>();
            random = new Random();
            accessCount = new HashMap<>();
            totalOperations = 0;
        }

        public boolean insert(int val) {
            totalOperations++;
            if (valToIndex.containsKey(val)) {
                return false;
            }

            valToIndex.put(val, nums.size());
            nums.add(val);
            accessCount.put(val, 0);
            return true;
        }

        public boolean remove(int val) {
            totalOperations++;
            if (!valToIndex.containsKey(val)) {
                return false;
            }

            int index = valToIndex.get(val);
            int lastElement = nums.get(nums.size() - 1);

            nums.set(index, lastElement);
            valToIndex.put(lastElement, index);

            nums.remove(nums.size() - 1);
            valToIndex.remove(val);
            accessCount.remove(val);

            return true;
        }

        public int getRandom() {
            totalOperations++;
            int randomVal = nums.get(random.nextInt(nums.size()));
            accessCount.put(randomVal, accessCount.get(randomVal) + 1);
            return randomVal;
        }

        public Map<Integer, Integer> getAccessStatistics() {
            return new HashMap<>(accessCount);
        }

        public int getMostAccessed() {
            return accessCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }

        public long getTotalOperations() {
            return totalOperations;
        }

        public double getAverageAccessCount() {
            if (accessCount.isEmpty())
                return 0.0;
            return accessCount.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
        }
    }

    // Advanced: Batch operations support
    public static class BatchRandomizedSet {
        private List<Integer> nums;
        private Map<Integer, Integer> valToIndex;
        private Random random;

        public BatchRandomizedSet() {
            nums = new ArrayList<>();
            valToIndex = new HashMap<>();
            random = new Random();
        }

        public List<Boolean> insertBatch(int[] vals) {
            List<Boolean> results = new ArrayList<>();
            for (int val : vals) {
                results.add(insert(val));
            }
            return results;
        }

        public List<Boolean> removeBatch(int[] vals) {
            List<Boolean> results = new ArrayList<>();
            for (int val : vals) {
                results.add(remove(val));
            }
            return results;
        }

        public List<Integer> getRandomBatch(int count) {
            List<Integer> results = new ArrayList<>();
            for (int i = 0; i < count && !nums.isEmpty(); i++) {
                results.add(getRandom());
            }
            return results;
        }

        public boolean insert(int val) {
            if (valToIndex.containsKey(val)) {
                return false;
            }

            valToIndex.put(val, nums.size());
            nums.add(val);
            return true;
        }

        public boolean remove(int val) {
            if (!valToIndex.containsKey(val)) {
                return false;
            }

            int index = valToIndex.get(val);
            int lastElement = nums.get(nums.size() - 1);

            nums.set(index, lastElement);
            valToIndex.put(lastElement, index);

            nums.remove(nums.size() - 1);
            valToIndex.remove(val);

            return true;
        }

        public int getRandom() {
            return nums.get(random.nextInt(nums.size()));
        }

        public Set<Integer> getAllElements() {
            return new HashSet<>(valToIndex.keySet());
        }

        public void clear() {
            nums.clear();
            valToIndex.clear();
        }
    }

    // Performance testing
    public static void performanceTest() {
        System.out.println("=== Performance Testing ===");

        RandomizedSet set = new RandomizedSet();
        MemoryOptimizedRandomizedSet memSet = new MemoryOptimizedRandomizedSet();

        int operations = 100000;

        // Test insertion performance
        long start = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            set.insert(i);
        }
        long arrayListTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            memSet.insert(i);
        }
        long arrayTime = System.nanoTime() - start;

        System.out.println("Insertion Performance:");
        System.out.println("ArrayList: " + arrayListTime / 1_000_000 + " ms");
        System.out.println("Array: " + arrayTime / 1_000_000 + " ms");

        // Test random access performance
        Random random = new Random(42);

        start = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            set.getRandom();
        }
        long randomArrayListTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            memSet.getRandom();
        }
        long randomArrayTime = System.nanoTime() - start;

        System.out.println("\nRandom Access Performance:");
        System.out.println("ArrayList: " + randomArrayListTime / 1_000_000 + " ms");
        System.out.println("Array: " + randomArrayTime / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        RandomizedSet randomizedSet = new RandomizedSet();

        System.out.println("Insert 1: " + randomizedSet.insert(1)); // true
        System.out.println("Remove 2: " + randomizedSet.remove(2)); // false
        System.out.println("Insert 2: " + randomizedSet.insert(2)); // true
        System.out.println("Random: " + randomizedSet.getRandom()); // 1 or 2
        System.out.println("Remove 1: " + randomizedSet.remove(1)); // true
        System.out.println("Insert 2: " + randomizedSet.insert(2)); // false
        System.out.println("Random: " + randomizedSet.getRandom()); // 2

        // Test Case 2: Multiset (with duplicates)
        System.out.println("\n=== Test Case 2: Multiset ===");

        RandomizedMultiset multiset = new RandomizedMultiset();

        System.out.println("Insert 1: " + multiset.insert(1)); // true (first occurrence)
        System.out.println("Insert 1: " + multiset.insert(1)); // false (duplicate)
        System.out.println("Insert 2: " + multiset.insert(2)); // true
        System.out.println("Count of 1: " + multiset.getCount(1)); // 2
        System.out.println("Remove 1: " + multiset.remove(1)); // true
        System.out.println("Count of 1: " + multiset.getCount(1)); // 1

        // Test Case 3: Weighted random
        System.out.println("\n=== Test Case 3: Weighted Random ===");

        WeightedRandomizedSet weightedSet = new WeightedRandomizedSet();

        weightedSet.insert(1, 1); // weight 1
        weightedSet.insert(2, 3); // weight 3
        weightedSet.insert(3, 1); // weight 1

        // Sample multiple times to see weight distribution
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            int val = weightedSet.getWeightedRandom();
            counts.put(val, counts.getOrDefault(val, 0) + 1);
        }

        System.out.println("Weighted sampling results (1000 samples):");
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println("Value " + entry.getKey() + ": " + entry.getValue() + " times"));

        // Test Case 4: Range queries
        System.out.println("\n=== Test Case 4: Range Queries ===");

        RangeRandomizedSet rangeSet = new RangeRandomizedSet();

        for (int i = 1; i <= 10; i++) {
            rangeSet.insert(i);
        }

        System.out.println("Elements in range [3, 7]: " + rangeSet.getElementsInRange(3, 7));
        System.out.println("Count in range [3, 7]: " + rangeSet.countInRange(3, 7));
        System.out.println("Random in range [3, 7]: " + rangeSet.getRandomInRange(3, 7));

        // Test Case 5: Memory optimized
        System.out.println("\n=== Test Case 5: Memory Optimized ===");

        MemoryOptimizedRandomizedSet memSet = new MemoryOptimizedRandomizedSet(4);

        System.out.println("Initial capacity: " + memSet.capacity());

        for (int i = 1; i <= 10; i++) {
            memSet.insert(i);
            System.out.println("After inserting " + i + ": size=" + memSet.size() + ", capacity=" + memSet.capacity());
        }

        // Test Case 6: Statistics
        System.out.println("\n=== Test Case 6: Statistics ===");

        StatisticalRandomizedSet statsSet = new StatisticalRandomizedSet();

        statsSet.insert(1);
        statsSet.insert(2);
        statsSet.insert(3);

        // Generate some random accesses
        for (int i = 0; i < 20; i++) {
            statsSet.getRandom();
        }

        System.out.println("Access statistics: " + statsSet.getAccessStatistics());
        System.out.println("Most accessed: " + statsSet.getMostAccessed());
        System.out.println("Average access count: " + String.format("%.2f", statsSet.getAverageAccessCount()));
        System.out.println("Total operations: " + statsSet.getTotalOperations());

        // Test Case 7: Batch operations
        System.out.println("\n=== Test Case 7: Batch Operations ===");

        BatchRandomizedSet batchSet = new BatchRandomizedSet();

        int[] insertVals = { 1, 2, 3, 2, 4 }; // 2 is duplicate
        List<Boolean> insertResults = batchSet.insertBatch(insertVals);
        System.out.println("Batch insert results: " + insertResults);

        List<Integer> randomBatch = batchSet.getRandomBatch(5);
        System.out.println("Random batch: " + randomBatch);

        System.out.println("All elements: " + batchSet.getAllElements());

        // Test Case 8: Thread safety (basic test)
        System.out.println("\n=== Test Case 8: Thread Safety ===");

        ThreadSafeRandomizedSet threadSafeSet = new ThreadSafeRandomizedSet();

        // Simulate concurrent operations
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads.add(new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    threadSafeSet.insert(threadId * 10 + j);
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

        System.out.println("Thread-safe set size: " + threadSafeSet.size());
        System.out.println("Sample random values: ");
        for (int i = 0; i < 5; i++) {
            System.out.print(threadSafeSet.getRandom() + " ");
        }
        System.out.println();

        // Test Case 9: Edge cases
        System.out.println("\n=== Test Case 9: Edge Cases ===");

        RandomizedSet edgeSet = new RandomizedSet();

        // Test remove from empty set
        System.out.println("Remove from empty: " + edgeSet.remove(1)); // false

        // Test single element
        edgeSet.insert(42);
        System.out.println("Single element random: " + edgeSet.getRandom()); // 42
        edgeSet.remove(42);

        // Test with negative numbers
        edgeSet.insert(-1);
        edgeSet.insert(0);
        edgeSet.insert(Integer.MAX_VALUE);
        edgeSet.insert(Integer.MIN_VALUE);

        System.out.println("With extreme values:");
        for (int i = 0; i < 4; i++) {
            System.out.print(edgeSet.getRandom() + " ");
        }
        System.out.println();

        // Performance testing
        performanceTest();

        // Test Case 10: Large scale operations
        System.out.println("\n=== Test Case 10: Large Scale Operations ===");

        RandomizedSet largeSet = new RandomizedSet();

        long startTime = System.currentTimeMillis();

        // Insert 10000 elements
        for (int i = 0; i < 10000; i++) {
            largeSet.insert(i);
        }

        // Remove every other element
        for (int i = 0; i < 10000; i += 2) {
            largeSet.remove(i);
        }

        // Generate 1000 random numbers
        Set<Integer> randomNumbers = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            randomNumbers.add(largeSet.getRandom());
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Large scale test completed in " + (endTime - startTime) + " ms");
        System.out.println("Unique random numbers generated: " + randomNumbers.size());

        System.out.println("\nInsert Delete GetRandom O(1) testing completed successfully!");
    }
}
