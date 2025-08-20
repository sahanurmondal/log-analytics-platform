package design.medium;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LeetCode Problem: Design Circular Priority Queue
 * URL: https://leetcode.com/problems/design-circular-queue/ (variation)
 * Difficulty: Medium
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High
 * 
 * Description:
 * Design a circular priority queue that supports enqueue, dequeue, and peek
 * operations.
 * Elements are dequeued in priority order (max-heap by default).
 * 
 * Constraints:
 * - 1 <= capacity <= 10^4
 * - At most 10^5 operations
 * - -10^9 <= value <= 10^9
 * 
 * Follow-up Questions:
 * 1. How would you make this thread-safe?
 * 2. How would you implement a double-ended priority queue?
 * 3. How would you support dynamic resizing?
 * 4. How would you implement min-heap version?
 * 5. How would you handle custom comparators?
 */
public class DesignCircularPriorityQueue {
    private int[] heap;
    private int capacity;
    private int size;
    private final ReentrantLock lock;

    // Approach 1: Array-based Max Heap Implementation
    public DesignCircularPriorityQueue(int capacity) {
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.size = 0;
        this.lock = new ReentrantLock();
    }

    /**
     * Enqueue element with priority
     * Time: O(log n), Space: O(1)
     */
    public boolean enqueue(int value) {
        if (isFull())
            return false;

        heap[size] = value;
        heapifyUp(size);
        size++;
        return true;
    }

    /**
     * Dequeue highest priority element
     * Time: O(log n), Space: O(1)
     */
    public boolean dequeue() {
        if (isEmpty())
            return false;

        heap[0] = heap[size - 1];
        size--;
        if (size > 0) {
            heapifyDown(0);
        }
        return true;
    }

    /**
     * Peek highest priority element
     * Time: O(1), Space: O(1)
     */
    public int peek() {
        return isEmpty() ? -1 : heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index] <= heap[parent])
                break;
            swap(index, parent);
            index = parent;
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int largest = index;
            int left = 2 * index + 1;
            int right = 2 * index + 2;

            if (left < size && heap[left] > heap[largest]) {
                largest = left;
            }
            if (right < size && heap[right] > heap[largest]) {
                largest = right;
            }
            if (largest == index)
                break;

            swap(index, largest);
            index = largest;
        }
    }

    private void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    // Approach 2: Thread-Safe Version with PriorityQueue
    static class ThreadSafeCircularPriorityQueue {
        private final PriorityQueue<Integer> pq;
        private final int capacity;
        private final ReentrantLock threadLock;

        public ThreadSafeCircularPriorityQueue(int capacity) {
            this.capacity = capacity;
            this.pq = new PriorityQueue<>(Collections.reverseOrder()); // Max heap
            this.threadLock = new ReentrantLock();
        }

        public boolean enqueue(int value) {
            threadLock.lock();
            try {
                if (pq.size() >= capacity)
                    return false;
                return pq.offer(value);
            } finally {
                threadLock.unlock();
            }
        }

        public boolean dequeue() {
            threadLock.lock();
            try {
                return pq.poll() != null;
            } finally {
                threadLock.unlock();
            }
        }

        public int peek() {
            threadLock.lock();
            try {
                Integer result = pq.peek();
                return result == null ? -1 : result;
            } finally {
                threadLock.unlock();
            }
        }
    }

    // Approach 3: Double-Ended Priority Queue (Deque)
    static class CircularDoublePriorityQueue {
        private final PriorityQueue<Integer> maxHeap;
        private final PriorityQueue<Integer> minHeap;
        private final int capacity;

        public CircularDoublePriorityQueue(int capacity) {
            this.capacity = capacity;
            this.maxHeap = new PriorityQueue<>(Collections.reverseOrder());
            this.minHeap = new PriorityQueue<>();
        }

        public boolean enqueue(int value) {
            if (maxHeap.size() >= capacity)
                return false;
            maxHeap.offer(value);
            minHeap.offer(value);
            return true;
        }

        public boolean dequeueMax() {
            if (maxHeap.isEmpty())
                return false;
            int max = maxHeap.poll();
            minHeap.remove(max);
            return true;
        }

        public boolean dequeueMin() {
            if (minHeap.isEmpty())
                return false;
            int min = minHeap.poll();
            maxHeap.remove(min);
            return true;
        }
    }

    public static void main(String[] args) {
        DesignCircularPriorityQueue queue = new DesignCircularPriorityQueue(3);

        // Test Case 1: Basic priority operations
        System.out.println("Empty: " + queue.isEmpty()); // true
        System.out.println("Enqueue 10: " + queue.enqueue(10)); // true
        System.out.println("Enqueue 5: " + queue.enqueue(5)); // true
        System.out.println("Enqueue 15: " + queue.enqueue(15)); // true
        System.out.println("Full: " + queue.isFull()); // true
        System.out.println("Peek: " + queue.peek()); // 15 (max priority)

        // Test Case 2: Priority order dequeue
        System.out.println("Dequeue: " + queue.dequeue()); // true (removes 15)
        System.out.println("Peek after dequeue: " + queue.peek()); // 10
        System.out.println("Dequeue: " + queue.dequeue()); // true (removes 10)
        System.out.println("Peek after second dequeue: " + queue.peek()); // 5

        // Test Case 3: Empty queue operations
        DesignCircularPriorityQueue empty = new DesignCircularPriorityQueue(1);
        System.out.println("Empty peek: " + empty.peek()); // -1
        System.out.println("Empty dequeue: " + empty.dequeue()); // false

        // Test Case 4: Full capacity rejection
        queue.enqueue(20); // Fill remaining slot
        System.out.println("Enqueue to full: " + queue.enqueue(25)); // false

        // Test Case 5: Thread-safe version
        ThreadSafeCircularPriorityQueue tsQueue = new ThreadSafeCircularPriorityQueue(2);
        System.out.println("TS Enqueue 100: " + tsQueue.enqueue(100)); // true
        System.out.println("TS Enqueue 50: " + tsQueue.enqueue(50)); // true
        System.out.println("TS Peek: " + tsQueue.peek()); // 100 (max)

        // Test Case 6: Double-ended priority queue
        CircularDoublePriorityQueue deque = new CircularDoublePriorityQueue(3);
        deque.enqueue(1);
        deque.enqueue(5);
        deque.enqueue(3);
        System.out.println("Deque max: " + deque.dequeueMax()); // true (removes 5)
        System.out.println("Deque min: " + deque.dequeueMin()); // true (removes 1)

        // Test Case 7: Negative numbers priority
        DesignCircularPriorityQueue negQueue = new DesignCircularPriorityQueue(3);
        negQueue.enqueue(-5);
        negQueue.enqueue(-1);
        negQueue.enqueue(-10);
        System.out.println("Negative peek: " + negQueue.peek()); // -1 (highest)

        // Test Case 8: Single element queue
        DesignCircularPriorityQueue single = new DesignCircularPriorityQueue(1);
        single.enqueue(42);
        System.out.println("Single peek: " + single.peek()); // 42
        single.dequeue();
        System.out.println("After dequeue empty: " + single.isEmpty()); // true

        // Test Case 9: Duplicate values
        DesignCircularPriorityQueue dup = new DesignCircularPriorityQueue(4);
        dup.enqueue(7);
        dup.enqueue(7);
        dup.enqueue(8);
        dup.enqueue(7);
        System.out.println("Duplicate peek: " + dup.peek()); // 8
        dup.dequeue();
        System.out.println("After dequeue: " + dup.peek()); // 7

        // Test Case 10: Sequential operations
        DesignCircularPriorityQueue seq = new DesignCircularPriorityQueue(2);
        seq.enqueue(1);
        seq.dequeue();
        seq.enqueue(2);
        seq.enqueue(3);
        System.out.println("Sequential peek: " + seq.peek()); // 3
    }
}
