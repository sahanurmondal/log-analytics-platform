package design.medium;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Variation: Design Circular Buffer
 *
 * Description:
 * Design a circular buffer supporting enqueue, dequeue, and peek operations.
 *
 * Constraints:
 * - 1 <= capacity <= 10^4
 * - At most 10^5 operations.
 *
 * Follow-up:
 * - Can you optimize for thread safety?
 * - Can you generalize for double-ended buffer?
 * - Can you support resizing?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(capacity)
 * 
 * Company Tags: System Design, Buffer Management
 */
public class DesignCircularBuffer {

    private final int[] buffer;
    private final int capacity;
    private int head;
    private int tail;
    private int size;
    private final ReentrantReadWriteLock lock;
    private final boolean threadSafe;

    public DesignCircularBuffer(int capacity) {
        this(capacity, false);
    }

    public DesignCircularBuffer(int capacity, boolean threadSafe) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
        this.buffer = new int[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
        this.threadSafe = threadSafe;
        this.lock = threadSafe ? new ReentrantReadWriteLock() : null;
    }

    public boolean enqueue(int value) {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                return enqueueInternal(value);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            return enqueueInternal(value);
        }
    }

    private boolean enqueueInternal(int value) {
        if (size == capacity) {
            return false; // Buffer is full
        }

        buffer[tail] = value;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    public boolean dequeue() {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                return dequeueInternal();
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            return dequeueInternal();
        }
    }

    private boolean dequeueInternal() {
        if (size == 0) {
            return false; // Buffer is empty
        }

        head = (head + 1) % capacity;
        size--;
        return true;
    }

    public int peek() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return peekInternal();
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return peekInternal();
        }
    }

    private int peekInternal() {
        if (size == 0) {
            return -1; // Buffer is empty
        }
        return buffer[head];
    }

    public boolean isEmpty() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return size == 0;
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return size == 0;
        }
    }

    public boolean isFull() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return size == capacity;
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return size == capacity;
        }
    }

    // Additional utility methods

    public int size() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return size;
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return size;
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void clear() {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                clearInternal();
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            clearInternal();
        }
    }

    private void clearInternal() {
        head = 0;
        tail = 0;
        size = 0;
        // Optional: clear the array for security
        Arrays.fill(buffer, 0);
    }

    // Get all elements as a list (for debugging/testing)
    public List<Integer> toList() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return toListInternal();
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return toListInternal();
        }
    }

    private List<Integer> toListInternal() {
        List<Integer> result = new ArrayList<>();
        if (size == 0) {
            return result;
        }

        int current = head;
        for (int i = 0; i < size; i++) {
            result.add(buffer[current]);
            current = (current + 1) % capacity;
        }

        return result;
    }

    // Overwrite oldest element when buffer is full (ring buffer behavior)
    public void enqueueOverwrite(int value) {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                enqueueOverwriteInternal(value);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            enqueueOverwriteInternal(value);
        }
    }

    private void enqueueOverwriteInternal(int value) {
        if (size == capacity) {
            // Overwrite the oldest element
            buffer[tail] = value;
            tail = (tail + 1) % capacity;
            head = (head + 1) % capacity; // Move head forward
        } else {
            buffer[tail] = value;
            tail = (tail + 1) % capacity;
            size++;
        }
    }

    @Override
    public String toString() {
        return String.format("CircularBuffer[capacity=%d, size=%d, elements=%s]",
                capacity, size(), toList());
    }

    public static void main(String[] args) {
        System.out.println("=== Basic Circular Buffer Tests ===");
        DesignCircularBuffer buffer = new DesignCircularBuffer(3);

        System.out.println("Initial state: " + buffer);
        System.out.println("Is empty: " + buffer.isEmpty()); // true
        System.out.println("Is full: " + buffer.isFull()); // false

        // Test enqueue
        System.out.println("\nEnqueuing 1, 2, 3:");
        System.out.println("Enqueue 1: " + buffer.enqueue(1)); // true
        System.out.println("Enqueue 2: " + buffer.enqueue(2)); // true
        System.out.println("Enqueue 3: " + buffer.enqueue(3)); // true
        System.out.println("Buffer state: " + buffer);
        System.out.println("Is full: " + buffer.isFull()); // true

        // Test enqueue when full
        System.out.println("Enqueue 4 (should fail): " + buffer.enqueue(4)); // false

        // Test peek and dequeue
        System.out.println("\nTesting peek and dequeue:");
        System.out.println("Peek: " + buffer.peek()); // 1
        System.out.println("Dequeue: " + buffer.dequeue()); // true
        System.out.println("Buffer after dequeue: " + buffer);
        System.out.println("Peek: " + buffer.peek()); // 2

        // Test circular nature
        System.out.println("\nTesting circular nature:");
        System.out.println("Enqueue 4: " + buffer.enqueue(4)); // true
        System.out.println("Buffer state: " + buffer);

        // Empty the buffer
        System.out.println("\nEmptying buffer:");
        while (!buffer.isEmpty()) {
            System.out.println("Dequeue: " + buffer.dequeue() + ", Buffer: " + buffer);
        }

        // Test dequeue when empty
        System.out.println("Dequeue when empty: " + buffer.dequeue()); // false
        System.out.println("Peek when empty: " + buffer.peek()); // -1

        System.out.println("\n=== Ring Buffer (Overwrite) Tests ===");
        DesignCircularBuffer ringBuffer = new DesignCircularBuffer(3);

        // Fill the buffer
        ringBuffer.enqueue(1);
        ringBuffer.enqueue(2);
        ringBuffer.enqueue(3);
        System.out.println("Full buffer: " + ringBuffer);

        // Test overwrite behavior
        ringBuffer.enqueueOverwrite(4);
        System.out.println("After overwrite with 4: " + ringBuffer);
        ringBuffer.enqueueOverwrite(5);
        System.out.println("After overwrite with 5: " + ringBuffer);

        System.out.println("\n=== Thread-Safe Buffer Tests ===");
        DesignCircularBuffer threadSafeBuffer = new DesignCircularBuffer(5, true);

        // Simulate concurrent access
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                boolean success = threadSafeBuffer.enqueue(i);
                System.out.println("Producer: enqueue " + i + " -> " + success);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                int value = threadSafeBuffer.peek();
                boolean success = threadSafeBuffer.dequeue();
                System.out.println("Consumer: peek " + value + ", dequeue -> " + success);
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final thread-safe buffer state: " + threadSafeBuffer);

        System.out.println("\n=== Performance Test ===");
        DesignCircularBuffer perfBuffer = new DesignCircularBuffer(1000);

        long startTime = System.currentTimeMillis();

        // Performance test: 100k operations
        for (int i = 0; i < 50000; i++) {
            perfBuffer.enqueue(i);
        }

        for (int i = 0; i < 25000; i++) {
            perfBuffer.dequeue();
        }

        for (int i = 0; i < 25000; i++) {
            perfBuffer.enqueue(i + 50000);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("100k operations completed in " + (endTime - startTime) + "ms");
        System.out.println("Final buffer size: " + perfBuffer.size());
    }
}
