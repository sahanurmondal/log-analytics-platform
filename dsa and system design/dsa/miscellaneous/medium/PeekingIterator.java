package miscellaneous.medium;

import java.util.*;

/**
 * LeetCode 284: Peeking Iterator
 * https://leetcode.com/problems/peeking-iterator/
 * 
 * Companies: Google, Apple, Facebook, Amazon, Microsoft, LinkedIn
 * Frequency: High (Asked in 400+ interviews)
 *
 * Description:
 * Design an iterator that supports the peek operation on an existing iterator
 * in addition to the hasNext and the next operations.
 *
 * Implement the PeekingIterator class:
 * - PeekingIterator(Iterator<int> iterator) Initializes the object with the
 * given integer iterator iterator.
 * - int next() Returns the next element in the array and moves the pointer to
 * the next element.
 * - boolean hasNext() Returns true if there are still elements in the array.
 * - int peek() Returns the next element in the array without moving the
 * pointer.
 *
 * Note: Each language may have different implementation of the iterator
 * interface.
 * It is guaranteed that next() and hasNext() are valid.
 * 
 * Follow-up Questions:
 * 1. How would you implement a bi-directional peeking iterator?
 * 2. Can you support peek multiple elements ahead?
 * 3. How to implement with thread safety?
 * 4. Can you support peeking with different data types?
 * 5. How to handle infinite iterators?
 */
public class PeekingIterator {

    // Approach 1: Cached Next Element - O(1) all operations, O(1) space
    public static class PeekingIteratorV1 implements Iterator<Integer> {
        private Iterator<Integer> iterator;
        private Integer nextElement;
        private boolean hasNextElement;

        public PeekingIteratorV1(Iterator<Integer> iterator) {
            this.iterator = iterator;
            advance();
        }

        private void advance() {
            if (iterator.hasNext()) {
                nextElement = iterator.next();
                hasNextElement = true;
            } else {
                hasNextElement = false;
                nextElement = null;
            }
        }

        public int peek() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }
            return nextElement;
        }

        @Override
        public Integer next() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }

            Integer result = nextElement;
            advance();
            return result;
        }

        @Override
        public boolean hasNext() {
            return hasNextElement;
        }
    }

    // Approach 2: Buffer based approach for better flexibility
    public static class BufferedPeekingIterator implements Iterator<Integer> {
        private Iterator<Integer> iterator;
        private Queue<Integer> buffer;

        public BufferedPeekingIterator(Iterator<Integer> iterator) {
            this.iterator = iterator;
            this.buffer = new ArrayDeque<>();
        }

        public int peek() {
            if (buffer.isEmpty() && !iterator.hasNext()) {
                throw new NoSuchElementException("No more elements");
            }

            if (buffer.isEmpty()) {
                buffer.offer(iterator.next());
            }

            return buffer.peek();
        }

        @Override
        public Integer next() {
            if (!buffer.isEmpty()) {
                return buffer.poll();
            }

            if (!iterator.hasNext()) {
                throw new NoSuchElementException("No more elements");
            }

            return iterator.next();
        }

        @Override
        public boolean hasNext() {
            return !buffer.isEmpty() || iterator.hasNext();
        }
    }

    // Follow-up 1: Bi-directional peeking iterator
    public static class BiDirectionalPeekingIterator {
        private List<Integer> elements;
        private int currentIndex;

        public BiDirectionalPeekingIterator(Iterator<Integer> iterator) {
            elements = new ArrayList<>();
            while (iterator.hasNext()) {
                elements.add(iterator.next());
            }
            currentIndex = 0;
        }

        public int peek() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return elements.get(currentIndex);
        }

        public int peekPrevious() {
            if (!hasPrevious()) {
                throw new NoSuchElementException("No previous elements");
            }
            return elements.get(currentIndex - 1);
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return elements.get(currentIndex++);
        }

        public int previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException("No previous elements");
            }
            return elements.get(--currentIndex);
        }

        public boolean hasNext() {
            return currentIndex < elements.size();
        }

        public boolean hasPrevious() {
            return currentIndex > 0;
        }

        public void reset() {
            currentIndex = 0;
        }

        public void jumpToEnd() {
            currentIndex = elements.size();
        }
    }

    // Follow-up 2: Multi-element peek ahead
    public static class MultiPeekIterator implements Iterator<Integer> {
        private Iterator<Integer> iterator;
        private List<Integer> buffer;

        public MultiPeekIterator(Iterator<Integer> iterator) {
            this.iterator = iterator;
            this.buffer = new ArrayList<>();
        }

        public int peek() {
            return peek(0);
        }

        public int peek(int steps) {
            // Fill buffer if needed
            while (buffer.size() <= steps && iterator.hasNext()) {
                buffer.add(iterator.next());
            }

            if (buffer.size() <= steps) {
                throw new NoSuchElementException("Not enough elements to peek " + (steps + 1) + " ahead");
            }

            return buffer.get(steps);
        }

        public List<Integer> peekMultiple(int count) {
            List<Integer> result = new ArrayList<>();

            // Fill buffer if needed
            while (buffer.size() < count && iterator.hasNext()) {
                buffer.add(iterator.next());
            }

            int available = Math.min(count, buffer.size());
            for (int i = 0; i < available; i++) {
                result.add(buffer.get(i));
            }

            return result;
        }

        @Override
        public Integer next() {
            if (!buffer.isEmpty()) {
                return buffer.remove(0);
            }

            if (!iterator.hasNext()) {
                throw new NoSuchElementException("No more elements");
            }

            return iterator.next();
        }

        @Override
        public boolean hasNext() {
            return !buffer.isEmpty() || iterator.hasNext();
        }

        public boolean hasNext(int steps) {
            while (buffer.size() <= steps && iterator.hasNext()) {
                buffer.add(iterator.next());
            }
            return buffer.size() > steps;
        }
    }

    // Follow-up 3: Thread-safe peeking iterator
    public static class ThreadSafePeekingIterator implements Iterator<Integer> {
        private final Iterator<Integer> iterator;
        private Integer nextElement;
        private boolean hasNextElement;
        private final Object lock = new Object();

        public ThreadSafePeekingIterator(Iterator<Integer> iterator) {
            this.iterator = iterator;
            advance();
        }

        private void advance() {
            if (iterator.hasNext()) {
                nextElement = iterator.next();
                hasNextElement = true;
            } else {
                hasNextElement = false;
                nextElement = null;
            }
        }

        public int peek() {
            synchronized (lock) {
                if (!hasNextElement) {
                    throw new NoSuchElementException("No more elements");
                }
                return nextElement;
            }
        }

        @Override
        public Integer next() {
            synchronized (lock) {
                if (!hasNextElement) {
                    throw new NoSuchElementException("No more elements");
                }

                Integer result = nextElement;
                advance();
                return result;
            }
        }

        @Override
        public boolean hasNext() {
            synchronized (lock) {
                return hasNextElement;
            }
        }

        public Integer peekOrNull() {
            synchronized (lock) {
                return hasNextElement ? nextElement : null;
            }
        }

        public boolean isEmpty() {
            synchronized (lock) {
                return !hasNextElement;
            }
        }
    }

    // Follow-up 4: Generic peeking iterator for different types
    public static class GenericPeekingIterator<T> implements Iterator<T> {
        private Iterator<T> iterator;
        private T nextElement;
        private boolean hasNextElement;

        public GenericPeekingIterator(Iterator<T> iterator) {
            this.iterator = iterator;
            advance();
        }

        private void advance() {
            if (iterator.hasNext()) {
                nextElement = iterator.next();
                hasNextElement = true;
            } else {
                hasNextElement = false;
                nextElement = null;
            }
        }

        public T peek() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }
            return nextElement;
        }

        @Override
        public T next() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }

            T result = nextElement;
            advance();
            return result;
        }

        @Override
        public boolean hasNext() {
            return hasNextElement;
        }

        public Optional<T> peekOptional() {
            return hasNextElement ? Optional.of(nextElement) : Optional.empty();
        }

        public T peekOrDefault(T defaultValue) {
            return hasNextElement ? nextElement : defaultValue;
        }
    }

    // Follow-up 5: Infinite iterator support with lazy evaluation
    public static class LazyPeekingIterator implements Iterator<Integer> {
        private Iterator<Integer> iterator;
        private Integer nextElement;
        private boolean hasNextElement;
        private boolean nextComputed;

        public LazyPeekingIterator(Iterator<Integer> iterator) {
            this.iterator = iterator;
            this.nextComputed = false;
        }

        private void computeNext() {
            if (!nextComputed) {
                if (iterator.hasNext()) {
                    nextElement = iterator.next();
                    hasNextElement = true;
                } else {
                    hasNextElement = false;
                    nextElement = null;
                }
                nextComputed = true;
            }
        }

        public int peek() {
            computeNext();
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }
            return nextElement;
        }

        @Override
        public Integer next() {
            computeNext();
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }

            Integer result = nextElement;
            nextComputed = false;
            return result;
        }

        @Override
        public boolean hasNext() {
            computeNext();
            return hasNextElement;
        }
    }

    // Advanced: Peeking iterator with filtering
    public static class FilteringPeekingIterator implements Iterator<Integer> {
        private Iterator<Integer> iterator;
        private Integer nextElement;
        private boolean hasNextElement;
        private java.util.function.Predicate<Integer> filter;

        public FilteringPeekingIterator(Iterator<Integer> iterator, java.util.function.Predicate<Integer> filter) {
            this.iterator = iterator;
            this.filter = filter;
            advance();
        }

        private void advance() {
            hasNextElement = false;
            while (iterator.hasNext()) {
                Integer candidate = iterator.next();
                if (filter.test(candidate)) {
                    nextElement = candidate;
                    hasNextElement = true;
                    break;
                }
            }
        }

        public int peek() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }
            return nextElement;
        }

        @Override
        public Integer next() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }

            Integer result = nextElement;
            advance();
            return result;
        }

        @Override
        public boolean hasNext() {
            return hasNextElement;
        }

        public void updateFilter(java.util.function.Predicate<Integer> newFilter) {
            this.filter = newFilter;
            advance(); // Recompute with new filter
        }
    }

    // Advanced: Peeking iterator with transformation
    public static class TransformingPeekingIterator<T, R> implements Iterator<R> {
        private Iterator<T> iterator;
        private R nextElement;
        private boolean hasNextElement;
        private java.util.function.Function<T, R> transformer;

        public TransformingPeekingIterator(Iterator<T> iterator, java.util.function.Function<T, R> transformer) {
            this.iterator = iterator;
            this.transformer = transformer;
            advance();
        }

        private void advance() {
            if (iterator.hasNext()) {
                nextElement = transformer.apply(iterator.next());
                hasNextElement = true;
            } else {
                hasNextElement = false;
                nextElement = null;
            }
        }

        public R peek() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }
            return nextElement;
        }

        @Override
        public R next() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }

            R result = nextElement;
            advance();
            return result;
        }

        @Override
        public boolean hasNext() {
            return hasNextElement;
        }
    }

    // Advanced: Stateful peeking iterator with history
    public static class StatefulPeekingIterator implements Iterator<Integer> {
        private Iterator<Integer> iterator;
        private Integer nextElement;
        private boolean hasNextElement;
        private List<Integer> history;
        private int maxHistorySize;

        public StatefulPeekingIterator(Iterator<Integer> iterator, int maxHistorySize) {
            this.iterator = iterator;
            this.maxHistorySize = maxHistorySize;
            this.history = new ArrayList<>();
            advance();
        }

        private void advance() {
            if (iterator.hasNext()) {
                nextElement = iterator.next();
                hasNextElement = true;
            } else {
                hasNextElement = false;
                nextElement = null;
            }
        }

        public int peek() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }
            return nextElement;
        }

        @Override
        public Integer next() {
            if (!hasNextElement) {
                throw new NoSuchElementException("No more elements");
            }

            Integer result = nextElement;

            // Add to history
            history.add(result);
            if (history.size() > maxHistorySize) {
                history.remove(0);
            }

            advance();
            return result;
        }

        @Override
        public boolean hasNext() {
            return hasNextElement;
        }

        public List<Integer> getHistory() {
            return new ArrayList<>(history);
        }

        public Integer getLastConsumed() {
            return history.isEmpty() ? null : history.get(history.size() - 1);
        }

        public List<Integer> getLastN(int n) {
            int start = Math.max(0, history.size() - n);
            return new ArrayList<>(history.subList(start, history.size()));
        }

        public void clearHistory() {
            history.clear();
        }
    }

    // Test helper class for creating iterators
    public static class TestIterator implements Iterator<Integer> {
        private int[] array;
        private int index;

        public TestIterator(int[] array) {
            this.array = array;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return array[index++];
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic Peeking Iterator
        System.out.println("=== Test Case 1: Basic Peeking Iterator ===");

        PeekingIteratorV1 peekingIterator = new PeekingIteratorV1(
                new TestIterator(new int[] { 1, 2, 3 }));

        System.out.println("Peek: " + peekingIterator.peek()); // 1
        System.out.println("Next: " + peekingIterator.next()); // 1
        System.out.println("Next: " + peekingIterator.next()); // 2
        System.out.println("Peek: " + peekingIterator.peek()); // 3
        System.out.println("HasNext: " + peekingIterator.hasNext()); // true
        System.out.println("Next: " + peekingIterator.next()); // 3
        System.out.println("HasNext: " + peekingIterator.hasNext()); // false

        // Test Case 2: Buffered approach
        System.out.println("\n=== Test Case 2: Buffered Approach ===");

        BufferedPeekingIterator bufferedIterator = new BufferedPeekingIterator(
                new TestIterator(new int[] { 10, 20, 30 }));

        System.out.println("Peek: " + bufferedIterator.peek()); // 10
        System.out.println("Peek again: " + bufferedIterator.peek()); // 10
        System.out.println("Next: " + bufferedIterator.next()); // 10
        System.out.println("Next: " + bufferedIterator.next()); // 20
        System.out.println("HasNext: " + bufferedIterator.hasNext()); // true

        // Test Case 3: Bi-directional iterator
        System.out.println("\n=== Test Case 3: Bi-directional Iterator ===");

        BiDirectionalPeekingIterator biIterator = new BiDirectionalPeekingIterator(
                new TestIterator(new int[] { 1, 2, 3, 4, 5 }));

        System.out.println("Next: " + biIterator.next()); // 1
        System.out.println("Next: " + biIterator.next()); // 2
        System.out.println("Peek: " + biIterator.peek()); // 3
        System.out.println("Previous: " + biIterator.previous()); // 2
        System.out.println("Peek Previous: " + biIterator.peekPrevious()); // 1
        System.out.println("Next: " + biIterator.next()); // 2
        System.out.println("Next: " + biIterator.next()); // 3

        // Test Case 4: Multi-peek iterator
        System.out.println("\n=== Test Case 4: Multi-Peek Iterator ===");

        MultiPeekIterator multiPeek = new MultiPeekIterator(
                new TestIterator(new int[] { 1, 2, 3, 4, 5, 6 }));

        System.out.println("Peek 0: " + multiPeek.peek(0)); // 1
        System.out.println("Peek 1: " + multiPeek.peek(1)); // 2
        System.out.println("Peek 2: " + multiPeek.peek(2)); // 3
        System.out.println("Peek multiple (3): " + multiPeek.peekMultiple(3)); // [1, 2, 3]
        System.out.println("Next: " + multiPeek.next()); // 1
        System.out.println("Peek 0 after next: " + multiPeek.peek(0)); // 2
        System.out.println("Has next 3 positions: " + multiPeek.hasNext(3)); // true

        // Test Case 5: Generic peeking iterator
        System.out.println("\n=== Test Case 5: Generic Iterator ===");

        List<String> strings = Arrays.asList("hello", "world", "test");
        GenericPeekingIterator<String> genericIterator = new GenericPeekingIterator<>(strings.iterator());

        System.out.println("Peek: " + genericIterator.peek()); // "hello"
        System.out.println("Peek optional: " + genericIterator.peekOptional()); // Optional["hello"]
        System.out.println("Next: " + genericIterator.next()); // "hello"
        System.out.println("Peek or default: " + genericIterator.peekOrDefault("default")); // "world"

        // Test Case 6: Filtering iterator
        System.out.println("\n=== Test Case 6: Filtering Iterator ===");

        FilteringPeekingIterator filterIterator = new FilteringPeekingIterator(
                new TestIterator(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }),
                x -> x % 2 == 0); // Even numbers only

        System.out.println("Filtered peek: " + filterIterator.peek()); // 2
        System.out.println("Filtered next: " + filterIterator.next()); // 2
        System.out.println("Filtered next: " + filterIterator.next()); // 4
        System.out.println("Filtered next: " + filterIterator.next()); // 6

        // Update filter to odd numbers
        filterIterator.updateFilter(x -> x % 2 == 1);
        System.out.println("After filter update, peek: " + filterIterator.peek()); // 7 (next odd after 6)

        // Test Case 7: Transforming iterator
        System.out.println("\n=== Test Case 7: Transforming Iterator ===");

        TransformingPeekingIterator<Integer, String> transformIterator = new TransformingPeekingIterator<>(
                new TestIterator(new int[] { 1, 2, 3 }),
                x -> "Number: " + x);

        System.out.println("Transform peek: " + transformIterator.peek()); // "Number: 1"
        System.out.println("Transform next: " + transformIterator.next()); // "Number: 1"
        System.out.println("Transform next: " + transformIterator.next()); // "Number: 2"

        // Test Case 8: Stateful iterator with history
        System.out.println("\n=== Test Case 8: Stateful Iterator ===");

        StatefulPeekingIterator statefulIterator = new StatefulPeekingIterator(
                new TestIterator(new int[] { 1, 2, 3, 4, 5 }), 3);

        System.out.println("Next: " + statefulIterator.next()); // 1
        System.out.println("Next: " + statefulIterator.next()); // 2
        System.out.println("Next: " + statefulIterator.next()); // 3
        System.out.println("History: " + statefulIterator.getHistory()); // [1, 2, 3]
        System.out.println("Last consumed: " + statefulIterator.getLastConsumed()); // 3

        System.out.println("Next: " + statefulIterator.next()); // 4
        System.out.println("Next: " + statefulIterator.next()); // 5
        System.out.println("History (max 3): " + statefulIterator.getHistory()); // [3, 4, 5]
        System.out.println("Last 2: " + statefulIterator.getLastN(2)); // [4, 5]

        // Test Case 9: Thread safety test
        System.out.println("\n=== Test Case 9: Thread Safety ===");

        ThreadSafePeekingIterator threadSafeIterator = new ThreadSafePeekingIterator(
                new TestIterator(new int[] { 1, 2, 3, 4, 5 }));

        // Simple thread safety test
        List<Thread> threads = new ArrayList<>();
        List<Integer> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 3; i++) {
            threads.add(new Thread(() -> {
                try {
                    while (threadSafeIterator.hasNext()) {
                        Integer peeked = threadSafeIterator.peekOrNull();
                        if (peeked != null) {
                            Thread.sleep(1); // Simulate some work
                            Integer next = threadSafeIterator.next();
                            results.add(next);
                        }
                    }
                } catch (Exception e) {
                    // Expected due to concurrent access
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

        System.out.println("Thread-safe results: " + results);

        // Test Case 10: Edge cases
        System.out.println("\n=== Test Case 10: Edge Cases ===");

        // Empty iterator
        PeekingIteratorV1 emptyIterator = new PeekingIteratorV1(
                new TestIterator(new int[] {}));

        System.out.println("Empty iterator hasNext: " + emptyIterator.hasNext()); // false

        try {
            emptyIterator.peek();
        } catch (NoSuchElementException e) {
            System.out.println("Expected exception on empty peek: " + e.getMessage());
        }

        // Single element iterator
        PeekingIteratorV1 singleIterator = new PeekingIteratorV1(
                new TestIterator(new int[] { 42 }));

        System.out.println("Single element peek: " + singleIterator.peek()); // 42
        System.out.println("Single element next: " + singleIterator.next()); // 42
        System.out.println("After consuming single element hasNext: " + singleIterator.hasNext()); // false

        // Large iterator performance test
        System.out.println("\n=== Performance Test ===");

        int[] largeArray = new int[100000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = i;
        }

        PeekingIteratorV1 perfIterator = new PeekingIteratorV1(new TestIterator(largeArray));

        long startTime = System.currentTimeMillis();
        int count = 0;
        while (perfIterator.hasNext()) {
            perfIterator.peek(); // Peek every element
            perfIterator.next();
            count++;
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Processed " + count + " elements in " + (endTime - startTime) + " ms");

        System.out.println("\nPeeking Iterator testing completed successfully!");
    }
}
