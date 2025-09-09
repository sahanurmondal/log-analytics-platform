package miscellaneous.medium;

import java.util.*;

/**
 * LeetCode 251: Flatten 2D Vector
 * https://leetcode.com/problems/flatten-2d-vector/
 * 
 * Companies: Google, Meta, Amazon, Microsoft, Apple, Uber
 * Frequency: Medium (Asked in 300+ interviews)
 *
 * Description:
 * Design an iterator to flatten a 2D vector. It should support the next and
 * hasNext operations.
 * 
 * Implement the Vector2D class:
 * - Vector2D(int[][] vec) initializes the object with the 2D vector vec.
 * - next() returns the next element from the 2D vector and moves the pointer
 * forward.
 * - hasNext() returns true if there are still some elements left in the 2D
 * vector.
 * 
 * Follow-up Questions:
 * 1. How would you handle empty rows?
 * 2. What if the input is very large and cannot fit in memory?
 * 3. Can you implement it without storing all elements?
 * 4. How to handle different data types?
 * 5. What about thread safety?
 */
public class Flatten2DVector {

    // Approach 1: Two-pointer approach - O(1) amortized time, O(1) space
    public static class Vector2D {
        private int[][] vector;
        private int row;
        private int col;

        public Vector2D(int[][] vec) {
            this.vector = vec;
            this.row = 0;
            this.col = 0;
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return vector[row][col++];
        }

        public boolean hasNext() {
            // Skip empty rows
            while (row < vector.length && col >= vector[row].length) {
                row++;
                col = 0;
            }

            return row < vector.length;
        }
    }

    // Approach 2: Flatten at initialization - O(m*n) time, O(m*n) space
    public static class Vector2DFlattened {
        private List<Integer> flattened;
        private int index;

        public Vector2DFlattened(int[][] vec) {
            this.flattened = new ArrayList<>();
            this.index = 0;

            for (int[] row : vec) {
                for (int val : row) {
                    flattened.add(val);
                }
            }
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return flattened.get(index++);
        }

        public boolean hasNext() {
            return index < flattened.size();
        }
    }

    // Approach 3: Iterator-based approach - O(1) amortized time, O(1) space
    public static class Vector2DIterator {
        private Iterator<int[]> rowIterator;
        private Iterator<Integer> colIterator;

        public Vector2DIterator(int[][] vec) {
            this.rowIterator = Arrays.asList(vec).iterator();
            this.colIterator = Collections.emptyIterator();
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return colIterator.next();
        }

        public boolean hasNext() {
            while (!colIterator.hasNext() && rowIterator.hasNext()) {
                int[] currentRow = rowIterator.next();
                colIterator = Arrays.stream(currentRow).boxed().iterator();
            }

            return colIterator.hasNext();
        }
    }

    // Approach 4: Lazy loading with queue - O(k) time where k is rows processed,
    // O(m) space
    public static class Vector2DLazy {
        private Queue<int[]> rowQueue;
        private int[] currentRow;
        private int col;

        public Vector2DLazy(int[][] vec) {
            this.rowQueue = new LinkedList<>();

            for (int[] row : vec) {
                if (row.length > 0) { // Skip empty rows
                    rowQueue.offer(row);
                }
            }

            this.currentRow = null;
            this.col = 0;
            advanceToNext();
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = currentRow[col++];
            advanceToNext();
            return result;
        }

        public boolean hasNext() {
            return currentRow != null && col < currentRow.length;
        }

        private void advanceToNext() {
            while ((currentRow == null || col >= currentRow.length) && !rowQueue.isEmpty()) {
                currentRow = rowQueue.poll();
                col = 0;
            }
        }
    }

    // Follow-up 1: Handle empty rows explicitly
    public static class Vector2DRobust {
        private int[][] vector;
        private int row;
        private int col;

        public Vector2DRobust(int[][] vec) {
            this.vector = vec != null ? vec : new int[0][];
            this.row = 0;
            this.col = 0;
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return vector[row][col++];
        }

        public boolean hasNext() {
            advanceToValidPosition();
            return row < vector.length && col < vector[row].length;
        }

        private void advanceToValidPosition() {
            while (row < vector.length) {
                if (vector[row] != null && col < vector[row].length) {
                    return; // Found valid position
                }
                row++;
                col = 0;
            }
        }
    }

    // Follow-up 2: Memory-efficient for large data (streaming approach)
    public static class Vector2DStreaming {
        private Iterator<int[]> rowIterator;
        private int[] currentRow;
        private int col;

        public Vector2DStreaming(List<int[]> rowStream) {
            this.rowIterator = rowStream.iterator();
            this.currentRow = null;
            this.col = 0;
            advanceToNext();
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = currentRow[col++];
            advanceToNext();
            return result;
        }

        public boolean hasNext() {
            return currentRow != null && col < currentRow.length;
        }

        private void advanceToNext() {
            while ((currentRow == null || col >= currentRow.length) && rowIterator.hasNext()) {
                currentRow = rowIterator.next();
                col = 0;

                // Skip empty rows
                if (currentRow != null && currentRow.length == 0) {
                    currentRow = null;
                }
            }
        }
    }

    // Follow-up 4: Generic type support
    public static class GenericVector2D<T> {
        private T[][] vector;
        private int row;
        private int col;

        @SuppressWarnings("unchecked")
        public GenericVector2D(T[][] vec) {
            this.vector = vec != null ? vec : (T[][]) new Object[0][];
            this.row = 0;
            this.col = 0;
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return vector[row][col++];
        }

        public boolean hasNext() {
            advanceToValidPosition();
            return row < vector.length && col < vector[row].length;
        }

        private void advanceToValidPosition() {
            while (row < vector.length) {
                if (vector[row] != null && col < vector[row].length) {
                    return;
                }
                row++;
                col = 0;
            }
        }
    }

    // Follow-up 5: Thread-safe implementation
    public static class Vector2DThreadSafe {
        private final int[][] vector;
        private volatile int row;
        private volatile int col;
        private final Object lock = new Object();

        public Vector2DThreadSafe(int[][] vec) {
            this.vector = vec != null ? copyArray(vec) : new int[0][];
            this.row = 0;
            this.col = 0;
        }

        public int next() {
            synchronized (lock) {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return vector[row][col++];
            }
        }

        public boolean hasNext() {
            synchronized (lock) {
                advanceToValidPosition();
                return row < vector.length && col < vector[row].length;
            }
        }

        private void advanceToValidPosition() {
            while (row < vector.length) {
                if (vector[row] != null && col < vector[row].length) {
                    return;
                }
                row++;
                col = 0;
            }
        }

        private int[][] copyArray(int[][] original) {
            int[][] copy = new int[original.length][];
            for (int i = 0; i < original.length; i++) {
                if (original[i] != null) {
                    copy[i] = Arrays.copyOf(original[i], original[i].length);
                }
            }
            return copy;
        }
    }

    // Advanced: Vector2D with reset capability
    public static class Vector2DResettable {
        private final int[][] vector;
        private int row;
        private int col;
        private final int initialRow;
        private final int initialCol;

        public Vector2DResettable(int[][] vec) {
            this.vector = vec != null ? vec : new int[0][];
            this.row = 0;
            this.col = 0;
            this.initialRow = 0;
            this.initialCol = 0;

            // Advance to first valid position
            advanceToValidPosition();
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = vector[row][col++];
            advanceToValidPosition();
            return result;
        }

        public boolean hasNext() {
            return row < vector.length && col < vector[row].length;
        }

        public void reset() {
            this.row = initialRow;
            this.col = initialCol;
            advanceToValidPosition();
        }

        private void advanceToValidPosition() {
            while (row < vector.length) {
                if (vector[row] != null && col < vector[row].length) {
                    return;
                }
                row++;
                col = 0;
            }
        }
    }

    // Advanced: Vector2D with peek capability
    public static class Vector2DPeekable {
        private int[][] vector;
        private int row;
        private int col;
        private Integer peekedValue;
        private boolean hasPeeked;

        public Vector2DPeekable(int[][] vec) {
            this.vector = vec != null ? vec : new int[0][];
            this.row = 0;
            this.col = 0;
            this.hasPeeked = false;
        }

        public int next() {
            if (hasPeeked) {
                hasPeeked = false;
                int result = peekedValue;
                advancePointer();
                return result;
            }

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = vector[row][col];
            advancePointer();
            return result;
        }

        public boolean hasNext() {
            if (hasPeeked) {
                return true;
            }

            advanceToValidPosition();
            return row < vector.length && col < vector[row].length;
        }

        public int peek() {
            if (hasPeeked) {
                return peekedValue;
            }

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            peekedValue = vector[row][col];
            hasPeeked = true;
            return peekedValue;
        }

        private void advancePointer() {
            col++;
            advanceToValidPosition();
        }

        private void advanceToValidPosition() {
            while (row < vector.length) {
                if (vector[row] != null && col < vector[row].length) {
                    return;
                }
                row++;
                col = 0;
            }
        }
    }

    // Advanced: Vector2D with filtering
    public static class Vector2DFiltered {
        private int[][] vector;
        private int row;
        private int col;
        private java.util.function.Predicate<Integer> filter;

        public Vector2DFiltered(int[][] vec, java.util.function.Predicate<Integer> filter) {
            this.vector = vec != null ? vec : new int[0][];
            this.filter = filter != null ? filter : x -> true;
            this.row = 0;
            this.col = 0;
            advanceToValidValue();
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = vector[row][col++];
            advanceToValidValue();
            return result;
        }

        public boolean hasNext() {
            return row < vector.length && col < vector[row].length;
        }

        private void advanceToValidValue() {
            while (row < vector.length) {
                while (col < vector[row].length) {
                    if (filter.test(vector[row][col])) {
                        return; // Found valid value
                    }
                    col++;
                }
                row++;
                col = 0;
            }
        }
    }

    // Advanced: Vector2D with transformation
    public static class Vector2DTransformed<T> {
        private int[][] vector;
        private int row;
        private int col;
        private java.util.function.Function<Integer, T> transformer;

        public Vector2DTransformed(int[][] vec, java.util.function.Function<Integer, T> transformer) {
            this.vector = vec != null ? vec : new int[0][];
            this.transformer = transformer != null ? transformer : x -> (T) x;
            this.row = 0;
            this.col = 0;
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int value = vector[row][col++];
            advanceToValidPosition();
            return transformer.apply(value);
        }

        public boolean hasNext() {
            advanceToValidPosition();
            return row < vector.length && col < vector[row].length;
        }

        private void advanceToValidPosition() {
            while (row < vector.length) {
                if (vector[row] != null && col < vector[row].length) {
                    return;
                }
                row++;
                col = 0;
            }
        }
    }

    // Advanced: Vector2D with batch operations
    public static class Vector2DBatch {
        private int[][] vector;
        private int row;
        private int col;

        public Vector2DBatch(int[][] vec) {
            this.vector = vec != null ? vec : new int[0][];
            this.row = 0;
            this.col = 0;
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return vector[row][col++];
        }

        public boolean hasNext() {
            advanceToValidPosition();
            return row < vector.length && col < vector[row].length;
        }

        public List<Integer> nextBatch(int size) {
            List<Integer> batch = new ArrayList<>();

            for (int i = 0; i < size && hasNext(); i++) {
                batch.add(next());
            }

            return batch;
        }

        public List<Integer> remaining() {
            List<Integer> remaining = new ArrayList<>();

            while (hasNext()) {
                remaining.add(next());
            }

            return remaining;
        }

        private void advanceToValidPosition() {
            while (row < vector.length) {
                if (vector[row] != null && col < vector[row].length) {
                    return;
                }
                row++;
                col = 0;
            }
        }
    }

    // Performance comparison
    public static Map<String, Long> comparePerformance(int[][] testVector) {
        Map<String, Long> results = new HashMap<>();

        // Test two-pointer approach
        long start = System.nanoTime();
        Vector2D v1 = new Vector2D(testVector);
        while (v1.hasNext()) {
            v1.next();
        }
        results.put("TwoPointer", System.nanoTime() - start);

        // Test flattened approach
        start = System.nanoTime();
        Vector2DFlattened v2 = new Vector2DFlattened(testVector);
        while (v2.hasNext()) {
            v2.next();
        }
        results.put("Flattened", System.nanoTime() - start);

        // Test iterator approach
        start = System.nanoTime();
        Vector2DIterator v3 = new Vector2DIterator(testVector);
        while (v3.hasNext()) {
            v3.next();
        }
        results.put("Iterator", System.nanoTime() - start);

        // Test lazy approach
        start = System.nanoTime();
        Vector2DLazy v4 = new Vector2DLazy(testVector);
        while (v4.hasNext()) {
            v4.next();
        }
        results.put("Lazy", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[][] vector1 = { { 1, 2 }, { 3 }, { 4, 5, 6 } };

        Vector2D v2d = new Vector2D(vector1);
        List<Integer> result1 = new ArrayList<>();

        while (v2d.hasNext()) {
            result1.add(v2d.next());
        }

        System.out.println("Input: " + Arrays.deepToString(vector1));
        System.out.println("Output: " + result1);

        // Test Case 2: Empty rows
        System.out.println("\n=== Test Case 2: Empty Rows ===");

        int[][] vector2 = { { 1 }, {}, { 2, 3 }, {}, { 4 } };

        Vector2DRobust v2dRobust = new Vector2DRobust(vector2);
        List<Integer> result2 = new ArrayList<>();

        while (v2dRobust.hasNext()) {
            result2.add(v2dRobust.next());
        }

        System.out.println("Input: " + Arrays.deepToString(vector2));
        System.out.println("Output: " + result2);

        // Test Case 3: Different approaches comparison
        System.out.println("\n=== Test Case 3: Different Approaches ===");

        int[][] vector3 = { { 1, 2, 3 }, { 4, 5 }, { 6, 7, 8, 9 } };

        // Two-pointer approach
        Vector2D twoPointer = new Vector2D(vector3);
        List<Integer> result3a = new ArrayList<>();
        while (twoPointer.hasNext()) {
            result3a.add(twoPointer.next());
        }

        // Flattened approach
        Vector2DFlattened flattened = new Vector2DFlattened(vector3);
        List<Integer> result3b = new ArrayList<>();
        while (flattened.hasNext()) {
            result3b.add(flattened.next());
        }

        // Iterator approach
        Vector2DIterator iterator = new Vector2DIterator(vector3);
        List<Integer> result3c = new ArrayList<>();
        while (iterator.hasNext()) {
            result3c.add(iterator.next());
        }

        System.out.println("Input: " + Arrays.deepToString(vector3));
        System.out.println("Two-pointer: " + result3a);
        System.out.println("Flattened: " + result3b);
        System.out.println("Iterator: " + result3c);
        System.out.println("All same: " + (result3a.equals(result3b) && result3b.equals(result3c)));

        // Test Case 4: Generic type support
        System.out.println("\n=== Test Case 4: Generic Type Support ===");

        String[][] stringVector = { { "a", "b" }, { "c" }, { "d", "e", "f" } };

        GenericVector2D<String> genericV2D = new GenericVector2D<>(stringVector);
        List<String> stringResult = new ArrayList<>();

        while (genericV2D.hasNext()) {
            stringResult.add(genericV2D.next());
        }

        System.out.println("String input: " + Arrays.deepToString(stringVector));
        System.out.println("String output: " + stringResult);

        // Test Case 5: Resettable iterator
        System.out.println("\n=== Test Case 5: Resettable Iterator ===");

        int[][] vector5 = { { 1, 2 }, { 3, 4 } };
        Vector2DResettable resettable = new Vector2DResettable(vector5);

        List<Integer> firstPass = new ArrayList<>();
        while (resettable.hasNext()) {
            firstPass.add(resettable.next());
        }

        System.out.println("First pass: " + firstPass);

        resettable.reset();
        List<Integer> secondPass = new ArrayList<>();
        while (resettable.hasNext()) {
            secondPass.add(resettable.next());
        }

        System.out.println("After reset: " + secondPass);
        System.out.println("Same result: " + firstPass.equals(secondPass));

        // Test Case 6: Peekable iterator
        System.out.println("\n=== Test Case 6: Peekable Iterator ===");

        int[][] vector6 = { { 1, 2 }, { 3, 4 } };
        Vector2DPeekable peekable = new Vector2DPeekable(vector6);

        System.out.println("Peek: " + peekable.peek());
        System.out.println("Peek again: " + peekable.peek());
        System.out.println("Next: " + peekable.next());
        System.out.println("Peek: " + peekable.peek());
        System.out.println("Next: " + peekable.next());

        // Test Case 7: Filtered iterator
        System.out.println("\n=== Test Case 7: Filtered Iterator ===");

        int[][] vector7 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };

        // Filter even numbers
        Vector2DFiltered filtered = new Vector2DFiltered(vector7, x -> x % 2 == 0);
        List<Integer> evenNumbers = new ArrayList<>();

        while (filtered.hasNext()) {
            evenNumbers.add(filtered.next());
        }

        System.out.println("Input: " + Arrays.deepToString(vector7));
        System.out.println("Even numbers: " + evenNumbers);

        // Test Case 8: Transformed iterator
        System.out.println("\n=== Test Case 8: Transformed Iterator ===");

        int[][] vector8 = { { 1, 2 }, { 3, 4 } };

        // Transform to strings
        Vector2DTransformed<String> transformed = new Vector2DTransformed<>(vector8,
                x -> "Value: " + x);
        List<String> transformedResult = new ArrayList<>();

        while (transformed.hasNext()) {
            transformedResult.add(transformed.next());
        }

        System.out.println("Input: " + Arrays.deepToString(vector8));
        System.out.println("Transformed: " + transformedResult);

        // Test Case 9: Batch operations
        System.out.println("\n=== Test Case 9: Batch Operations ===");

        int[][] vector9 = { { 1, 2, 3 }, { 4, 5 }, { 6, 7, 8, 9, 10 } };
        Vector2DBatch batch = new Vector2DBatch(vector9);

        System.out.println("Input: " + Arrays.deepToString(vector9));
        System.out.println("First batch of 3: " + batch.nextBatch(3));
        System.out.println("Next batch of 4: " + batch.nextBatch(4));
        System.out.println("Remaining: " + batch.remaining());

        // Test Case 10: Thread safety (basic test)
        System.out.println("\n=== Test Case 10: Thread Safety ===");

        int[][] vector10 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        Vector2DThreadSafe threadSafe = new Vector2DThreadSafe(vector10);

        List<Integer> threadSafeResult = new ArrayList<>();
        while (threadSafe.hasNext()) {
            threadSafeResult.add(threadSafe.next());
        }

        System.out.println("Thread-safe result: " + threadSafeResult);

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");

        // Create larger test vector
        int[][] largeVector = new int[100][];
        for (int i = 0; i < 100; i++) {
            largeVector[i] = new int[100];
            for (int j = 0; j < 100; j++) {
                largeVector[i][j] = i * 100 + j;
            }
        }

        Map<String, Long> performance = comparePerformance(largeVector);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Empty vector
        int[][] emptyVector = {};
        Vector2D emptyV2D = new Vector2D(emptyVector);
        System.out.println("Empty vector hasNext: " + emptyV2D.hasNext());

        // Vector with only empty rows
        int[][] onlyEmptyRows = { {}, {}, {} };
        Vector2DRobust onlyEmptyV2D = new Vector2DRobust(onlyEmptyRows);
        System.out.println("Only empty rows hasNext: " + onlyEmptyV2D.hasNext());

        // Single element
        int[][] singleElement = { { 42 } };
        Vector2D singleV2D = new Vector2D(singleElement);
        List<Integer> singleResult = new ArrayList<>();
        while (singleV2D.hasNext()) {
            singleResult.add(singleV2D.next());
        }
        System.out.println("Single element: " + singleResult);

        // Null handling
        Vector2DRobust nullSafe = new Vector2DRobust(null);
        System.out.println("Null input hasNext: " + nullSafe.hasNext());

        System.out.println("\nFlatten 2D Vector testing completed successfully!");
    }
}
