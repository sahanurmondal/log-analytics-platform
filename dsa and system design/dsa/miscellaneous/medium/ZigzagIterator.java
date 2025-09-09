package miscellaneous.medium;

import java.util.*;

/**
 * LeetCode 281: Zigzag Iterator
 * https://leetcode.com/problems/zigzag-iterator/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, LinkedIn
 * Frequency: Medium (Asked in 200+ interviews)
 *
 * Description:
 * Given two vectors, implement an iterator to return their elements
 * alternately.
 *
 * Implement the ZigzagIterator class:
 * - ZigzagIterator(List<Integer> v1, List<Integer> v2) initializes the object
 * with the two vectors v1 and v2.
 * - boolean hasNext() Returns true if the iterator has more elements, and false
 * otherwise.
 * - int next() Returns the next element of the iterator.
 * 
 * Follow-up Questions:
 * 1. How would you handle k vectors instead of just two?
 * 2. Can you support vectors of different types?
 * 3. How to handle infinite or very large vectors?
 * 4. What about supporting insertion/deletion during iteration?
 * 5. How to implement weighted zigzag iteration?
 */
public class ZigzagIterator {

    // Approach 1: Two pointers - O(1) space, O(1) next/hasNext
    public static class TwoVectorIterator {
        private List<Integer> v1;
        private List<Integer> v2;
        private int index1;
        private int index2;
        private boolean turn; // true for v1, false for v2

        public TwoVectorIterator(List<Integer> v1, List<Integer> v2) {
            this.v1 = v1;
            this.v2 = v2;
            this.index1 = 0;
            this.index2 = 0;
            this.turn = true;
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result;
            if (turn && index1 < v1.size()) {
                result = v1.get(index1++);
                turn = false;
            } else if (!turn && index2 < v2.size()) {
                result = v2.get(index2++);
                turn = true;
            } else if (index1 < v1.size()) {
                result = v1.get(index1++);
            } else {
                result = v2.get(index2++);
            }

            return result;
        }

        public boolean hasNext() {
            return index1 < v1.size() || index2 < v2.size();
        }
    }

    // Approach 2: Queue-based for multiple vectors - O(k) space, O(1) amortized
    public static class MultiVectorIterator {
        private Queue<Iterator<Integer>> iterators;

        public MultiVectorIterator(List<List<Integer>> vectors) {
            iterators = new LinkedList<>();

            for (List<Integer> vector : vectors) {
                if (!vector.isEmpty()) {
                    iterators.offer(vector.iterator());
                }
            }
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Iterator<Integer> current = iterators.poll();
            int result = current.next();

            if (current.hasNext()) {
                iterators.offer(current);
            }

            return result;
        }

        public boolean hasNext() {
            return !iterators.isEmpty();
        }
    }

    // Follow-up 1: K vectors with round-robin
    public static class KVectorIterator {
        private List<List<Integer>> vectors;
        private int[] indices;
        private int currentVector;
        private int activeVectors;

        public KVectorIterator(List<List<Integer>> vectors) {
            this.vectors = new ArrayList<>(vectors);
            this.indices = new int[vectors.size()];
            this.currentVector = 0;
            this.activeVectors = 0;

            // Count non-empty vectors
            for (List<Integer> vector : vectors) {
                if (!vector.isEmpty()) {
                    activeVectors++;
                }
            }
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // Find next available vector
            int attempts = 0;
            while (attempts < vectors.size()) {
                if (indices[currentVector] < vectors.get(currentVector).size()) {
                    int result = vectors.get(currentVector).get(indices[currentVector]++);

                    // Check if this vector is now exhausted
                    if (indices[currentVector] >= vectors.get(currentVector).size()) {
                        activeVectors--;
                    }

                    currentVector = (currentVector + 1) % vectors.size();
                    return result;
                }

                currentVector = (currentVector + 1) % vectors.size();
                attempts++;
            }

            throw new NoSuchElementException();
        }

        public boolean hasNext() {
            return activeVectors > 0;
        }

        public void reset() {
            Arrays.fill(indices, 0);
            currentVector = 0;
            activeVectors = 0;

            for (List<Integer> vector : vectors) {
                if (!vector.isEmpty()) {
                    activeVectors++;
                }
            }
        }
    }

    // Follow-up 2: Generic type support
    public static class GenericZigzagIterator<T> {
        private Queue<Iterator<T>> iterators;

        @SafeVarargs
        public GenericZigzagIterator(List<T>... vectors) {
            iterators = new LinkedList<>();

            for (List<T> vector : vectors) {
                if (!vector.isEmpty()) {
                    iterators.offer(vector.iterator());
                }
            }
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Iterator<T> current = iterators.poll();
            T result = current.next();

            if (current.hasNext()) {
                iterators.offer(current);
            }

            return result;
        }

        public boolean hasNext() {
            return !iterators.isEmpty();
        }

        public List<T> remaining() {
            List<T> result = new ArrayList<>();
            while (hasNext()) {
                result.add(next());
            }
            return result;
        }
    }

    // Follow-up 3: Lazy evaluation for large vectors
    public static class LazyZigzagIterator {
        private Queue<java.util.function.Supplier<Iterator<Integer>>> suppliers;
        private Queue<Iterator<Integer>> activeIterators;

        public LazyZigzagIterator(List<java.util.function.Supplier<Iterator<Integer>>> suppliers) {
            this.suppliers = new LinkedList<>(suppliers);
            this.activeIterators = new LinkedList<>();
        }

        private void ensureActiveIterators() {
            while (activeIterators.isEmpty() && !suppliers.isEmpty()) {
                java.util.function.Supplier<Iterator<Integer>> supplier = suppliers.poll();
                Iterator<Integer> iterator = supplier.get();

                if (iterator.hasNext()) {
                    activeIterators.offer(iterator);
                }
            }
        }

        public int next() {
            ensureActiveIterators();

            if (activeIterators.isEmpty()) {
                throw new NoSuchElementException();
            }

            Iterator<Integer> current = activeIterators.poll();
            int result = current.next();

            if (current.hasNext()) {
                activeIterators.offer(current);
            }

            return result;
        }

        public boolean hasNext() {
            ensureActiveIterators();
            return !activeIterators.isEmpty();
        }
    }

    // Follow-up 4: Dynamic modification support
    public static class DynamicZigzagIterator {
        private List<List<Integer>> vectors;
        private List<Integer> indices;
        private int currentVector;
        private Set<Integer> removedVectors;

        public DynamicZigzagIterator(List<List<Integer>> vectors) {
            this.vectors = new ArrayList<>();
            for (List<Integer> vector : vectors) {
                this.vectors.add(new ArrayList<>(vector));
            }
            this.indices = new ArrayList<>(Collections.nCopies(vectors.size(), 0));
            this.currentVector = 0;
            this.removedVectors = new HashSet<>();
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int attempts = 0;
            while (attempts < vectors.size()) {
                if (!removedVectors.contains(currentVector) &&
                        indices.get(currentVector) < vectors.get(currentVector).size()) {

                    int result = vectors.get(currentVector).get(indices.get(currentVector));
                    indices.set(currentVector, indices.get(currentVector) + 1);

                    currentVector = (currentVector + 1) % vectors.size();
                    return result;
                }

                currentVector = (currentVector + 1) % vectors.size();
                attempts++;
            }

            throw new NoSuchElementException();
        }

        public boolean hasNext() {
            for (int i = 0; i < vectors.size(); i++) {
                if (!removedVectors.contains(i) && indices.get(i) < vectors.get(i).size()) {
                    return true;
                }
            }
            return false;
        }

        public void addVector(List<Integer> vector) {
            vectors.add(new ArrayList<>(vector));
            indices.add(0);
        }

        public void removeVector(int index) {
            if (index >= 0 && index < vectors.size()) {
                removedVectors.add(index);
            }
        }

        public void insertElement(int vectorIndex, int element) {
            if (vectorIndex >= 0 && vectorIndex < vectors.size() &&
                    !removedVectors.contains(vectorIndex)) {
                vectors.get(vectorIndex).add(element);
            }
        }

        public boolean removeElement(int vectorIndex, int element) {
            if (vectorIndex >= 0 && vectorIndex < vectors.size() &&
                    !removedVectors.contains(vectorIndex)) {

                List<Integer> vector = vectors.get(vectorIndex);
                int removeIndex = vector.indexOf(element);

                if (removeIndex != -1) {
                    vector.remove(removeIndex);

                    // Adjust current index if needed
                    if (removeIndex < indices.get(vectorIndex)) {
                        indices.set(vectorIndex, indices.get(vectorIndex) - 1);
                    }

                    return true;
                }
            }
            return false;
        }
    }

    // Follow-up 5: Weighted zigzag iteration
    public static class WeightedZigzagIterator {
        private List<List<Integer>> vectors;
        private List<Integer> weights;
        private List<Integer> indices;
        private List<Integer> remainingWeights;
        private int currentVector;

        public WeightedZigzagIterator(List<List<Integer>> vectors, List<Integer> weights) {
            if (vectors.size() != weights.size()) {
                throw new IllegalArgumentException("Vectors and weights must have same size");
            }

            this.vectors = new ArrayList<>(vectors);
            this.weights = new ArrayList<>(weights);
            this.indices = new ArrayList<>(Collections.nCopies(vectors.size(), 0));
            this.remainingWeights = new ArrayList<>(weights);
            this.currentVector = 0;
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // Find next available vector with remaining weight
            int attempts = 0;
            while (attempts < vectors.size()) {
                if (indices.get(currentVector) < vectors.get(currentVector).size() &&
                        remainingWeights.get(currentVector) > 0) {

                    int idx = indices.get(currentVector);
                    int result = vectors.get(currentVector).get(idx);
                    indices.set(currentVector, idx + 1);
                    remainingWeights.set(currentVector, remainingWeights.get(currentVector) - 1);

                    // If weight exhausted, reset and move to next vector
                    if (remainingWeights.get(currentVector) == 0) {
                        remainingWeights.set(currentVector, weights.get(currentVector));
                        currentVector = (currentVector + 1) % vectors.size();
                    }

                    return result;
                }

                // Reset weight and try next vector
                remainingWeights.set(currentVector, weights.get(currentVector));
                currentVector = (currentVector + 1) % vectors.size();
                attempts++;
            }

            throw new NoSuchElementException();
        }

        public boolean hasNext() {
            for (int i = 0; i < vectors.size(); i++) {
                if (indices.get(i) < vectors.get(i).size()) {
                    return true;
                }
            }
            return false;
        }

        public void updateWeight(int vectorIndex, int newWeight) {
            if (vectorIndex >= 0 && vectorIndex < weights.size()) {
                weights.set(vectorIndex, newWeight);
                remainingWeights.set(vectorIndex, newWeight);
            }
        }
    }

    // Advanced: Priority-based zigzag
    public static class PriorityZigzagIterator {
        private static class VectorInfo {
            Iterator<Integer> iterator;
            int priority;
            int id;

            VectorInfo(Iterator<Integer> iterator, int priority, int id) {
                this.iterator = iterator;
                this.priority = priority;
                this.id = id;
            }
        }

        private PriorityQueue<VectorInfo> pq;
        private int idCounter;

        public PriorityZigzagIterator(List<List<Integer>> vectors, List<Integer> priorities) {
            if (vectors.size() != priorities.size()) {
                throw new IllegalArgumentException("Vectors and priorities must have same size");
            }

            this.pq = new PriorityQueue<>((a, b) -> {
                if (a.priority != b.priority) {
                    return Integer.compare(b.priority, a.priority); // Higher priority first
                }
                return Integer.compare(a.id, b.id); // Stable ordering
            });

            this.idCounter = 0;

            for (int i = 0; i < vectors.size(); i++) {
                if (!vectors.get(i).isEmpty()) {
                    pq.offer(new VectorInfo(vectors.get(i).iterator(), priorities.get(i), idCounter++));
                }
            }
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            VectorInfo current = pq.poll();
            int result = current.iterator.next();

            if (current.iterator.hasNext()) {
                pq.offer(new VectorInfo(current.iterator, current.priority, idCounter++));
            }

            return result;
        }

        public boolean hasNext() {
            return !pq.isEmpty();
        }
    }

    // Advanced: Buffered zigzag for performance
    public static class BufferedZigzagIterator {
        private List<List<Integer>> vectors;
        private List<Integer> indices;
        private Queue<Integer> buffer;
        private int bufferSize;
        private int currentVector;

        public BufferedZigzagIterator(List<List<Integer>> vectors, int bufferSize) {
            this.vectors = new ArrayList<>(vectors);
            this.indices = new ArrayList<>(Collections.nCopies(vectors.size(), 0));
            this.buffer = new LinkedList<>();
            this.bufferSize = bufferSize;
            this.currentVector = 0;

            fillBuffer();
        }

        private void fillBuffer() {
            while (buffer.size() < bufferSize && hasMoreElements()) {
                int attempts = 0;

                while (attempts < vectors.size() && buffer.size() < bufferSize) {
                    if (indices.get(currentVector) < vectors.get(currentVector).size()) {
                        int idx = indices.get(currentVector);
                        buffer.offer(vectors.get(currentVector).get(idx));
                        indices.set(currentVector, idx + 1);
                    }

                    currentVector = (currentVector + 1) % vectors.size();
                    attempts++;
                }
            }
        }

        private boolean hasMoreElements() {
            for (int i = 0; i < vectors.size(); i++) {
                if (indices.get(i) < vectors.get(i).size()) {
                    return true;
                }
            }
            return false;
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = buffer.poll();
            fillBuffer();
            return result;
        }

        public boolean hasNext() {
            return !buffer.isEmpty() || hasMoreElements();
        }

        public int getBufferSize() {
            return buffer.size();
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic Two Vector Iterator
        System.out.println("=== Test Case 1: Basic Two Vector Iterator ===");

        List<Integer> v1 = Arrays.asList(1, 2);
        List<Integer> v2 = Arrays.asList(3, 4, 5, 6);

        TwoVectorIterator it1 = new TwoVectorIterator(v1, v2);

        System.out.print("Zigzag order: ");
        while (it1.hasNext()) {
            System.out.print(it1.next() + " ");
        }
        System.out.println(); // Expected: 1 3 2 4 5 6

        // Test Case 2: Multi Vector Iterator
        System.out.println("\n=== Test Case 2: Multi Vector Iterator ===");

        List<List<Integer>> vectors = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6, 7),
                Arrays.asList(8, 9));

        MultiVectorIterator it2 = new MultiVectorIterator(vectors);

        System.out.print("Multi-vector zigzag: ");
        while (it2.hasNext()) {
            System.out.print(it2.next() + " ");
        }
        System.out.println(); // Expected: 1 4 8 2 5 9 3 6 7

        // Test Case 3: K Vector Iterator
        System.out.println("\n=== Test Case 3: K Vector Iterator ===");

        KVectorIterator it3 = new KVectorIterator(vectors);

        System.out.print("K-vector round-robin: ");
        while (it3.hasNext()) {
            System.out.print(it3.next() + " ");
        }
        System.out.println();

        // Test reset functionality
        it3.reset();
        System.out.print("After reset: ");
        for (int i = 0; i < 5 && it3.hasNext(); i++) {
            System.out.print(it3.next() + " ");
        }
        System.out.println();

        // Test Case 4: Generic Type Support
        System.out.println("\n=== Test Case 4: Generic Type Support ===");

        List<String> stringV1 = Arrays.asList("a", "b");
        List<String> stringV2 = Arrays.asList("c", "d", "e");

        @SuppressWarnings("unchecked")
        GenericZigzagIterator<String> stringIt = new GenericZigzagIterator<>(stringV1, stringV2);

        System.out.print("String zigzag: ");
        while (stringIt.hasNext()) {
            System.out.print(stringIt.next() + " ");
        }
        System.out.println();

        // Test Case 5: Weighted Zigzag
        System.out.println("\n=== Test Case 5: Weighted Zigzag ===");

        List<List<Integer>> weightedVectors = Arrays.asList(
                Arrays.asList(1, 2, 3, 4, 5),
                Arrays.asList(10, 20, 30),
                Arrays.asList(100, 200));

        List<Integer> weights = Arrays.asList(2, 1, 3); // Take 2 from first, 1 from second, 3 from third

        WeightedZigzagIterator weightedIt = new WeightedZigzagIterator(weightedVectors, weights);

        System.out.print("Weighted zigzag (2:1:3): ");
        while (weightedIt.hasNext()) {
            System.out.print(weightedIt.next() + " ");
        }
        System.out.println();

        // Test Case 6: Priority-based Zigzag
        System.out.println("\n=== Test Case 6: Priority-based Zigzag ===");

        List<List<Integer>> priorityVectors = Arrays.asList(
                Arrays.asList(1, 2, 3), // Priority 1 (low)
                Arrays.asList(10, 20, 30), // Priority 3 (high)
                Arrays.asList(100, 200) // Priority 2 (medium)
        );

        List<Integer> priorities = Arrays.asList(1, 3, 2);

        PriorityZigzagIterator priorityIt = new PriorityZigzagIterator(priorityVectors, priorities);

        System.out.print("Priority zigzag (1:3:2): ");
        while (priorityIt.hasNext()) {
            System.out.print(priorityIt.next() + " ");
        }
        System.out.println();

        // Test Case 7: Dynamic Modification
        System.out.println("\n=== Test Case 7: Dynamic Modification ===");

        List<List<Integer>> dynamicVectors = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8, 9));

        DynamicZigzagIterator dynamicIt = new DynamicZigzagIterator(dynamicVectors);

        System.out.print("Initial: ");
        for (int i = 0; i < 5 && dynamicIt.hasNext(); i++) {
            System.out.print(dynamicIt.next() + " ");
        }
        System.out.println();

        // Add a new vector
        dynamicIt.addVector(Arrays.asList(100, 200));
        System.out.print("After adding vector: ");
        while (dynamicIt.hasNext()) {
            System.out.print(dynamicIt.next() + " ");
        }
        System.out.println();

        // Test Case 8: Buffered Iterator
        System.out.println("\n=== Test Case 8: Buffered Iterator ===");

        List<List<Integer>> bufferedVectors = Arrays.asList(
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(10, 20, 30),
                Arrays.asList(100, 200, 300, 400, 500));

        BufferedZigzagIterator bufferedIt = new BufferedZigzagIterator(bufferedVectors, 5);

        System.out.print("Buffered zigzag: ");
        while (bufferedIt.hasNext()) {
            System.out.print(bufferedIt.next() + " ");
            if (bufferedIt.hasNext()) {
                System.out.print("(buffer: " + bufferedIt.getBufferSize() + ") ");
            }
        }
        System.out.println();

        // Test Case 9: Edge Cases
        System.out.println("\n=== Test Case 9: Edge Cases ===");

        // Empty vectors
        List<Integer> empty1 = Arrays.asList();
        List<Integer> empty2 = Arrays.asList(1, 2, 3);

        TwoVectorIterator emptyIt = new TwoVectorIterator(empty1, empty2);
        System.out.print("One empty vector: ");
        while (emptyIt.hasNext()) {
            System.out.print(emptyIt.next() + " ");
        }
        System.out.println();

        // Both empty
        TwoVectorIterator bothEmptyIt = new TwoVectorIterator(Arrays.asList(), Arrays.asList());
        System.out.println("Both empty hasNext: " + bothEmptyIt.hasNext()); // Should be false

        // Single element vectors
        TwoVectorIterator singleIt = new TwoVectorIterator(Arrays.asList(1), Arrays.asList(2));
        System.out.print("Single elements: ");
        while (singleIt.hasNext()) {
            System.out.print(singleIt.next() + " ");
        }
        System.out.println();

        // Test Case 10: Large Vector Performance
        System.out.println("\n=== Test Case 10: Large Vector Performance ===");

        List<Integer> large1 = new ArrayList<>();
        List<Integer> large2 = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            large1.add(i);
            large2.add(i + 10000);
        }

        TwoVectorIterator largeIt = new TwoVectorIterator(large1, large2);

        long startTime = System.currentTimeMillis();
        int count = 0;
        while (largeIt.hasNext()) {
            largeIt.next();
            count++;
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Processed " + count + " elements in " + (endTime - startTime) + " ms");

        // Compare with multi-vector approach
        List<List<Integer>> largeVectors = Arrays.asList(large1, large2);
        MultiVectorIterator largeMultiIt = new MultiVectorIterator(largeVectors);

        startTime = System.currentTimeMillis();
        count = 0;
        while (largeMultiIt.hasNext()) {
            largeMultiIt.next();
            count++;
        }
        endTime = System.currentTimeMillis();

        System.out.println("Multi-vector processed " + count + " elements in " + (endTime - startTime) + " ms");

        // Test exception handling
        System.out.println("\n=== Exception Handling ===");

        TwoVectorIterator exhaustedIt = new TwoVectorIterator(Arrays.asList(1), Arrays.asList(2));
        exhaustedIt.next(); // 1
        exhaustedIt.next(); // 2

        try {
            exhaustedIt.next(); // Should throw exception
        } catch (NoSuchElementException e) {
            System.out.println("Correctly threw NoSuchElementException: " + e.getMessage());
        }

        System.out.println("\nZigzag Iterator testing completed successfully!");
    }
}
