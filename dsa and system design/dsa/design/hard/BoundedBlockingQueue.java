package design.hard;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LeetCode 1188: Design Bounded Blocking Queue
 * https://leetcode.com/problems/design-bounded-blocking-queue/
 *
 * Description: Implement a thread-safe bounded blocking queue that has the
 * following methods:
 * 
 * Constraints:
 * - 1 <= capacity <= 1000
 * - 0 <= element <= 1000
 * - The methods enqueue and dequeue may be called concurrently by multiple
 * threads
 *
 * Follow-up:
 * - Can you implement it using semaphores?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(capacity)
 * 
 * Company Tags: Google, Amazon
 */
public class BoundedBlockingQueue {

    private int[] queue;
    private int head, tail, size;
    private int capacity;
    private ReentrantLock lock;
    private Condition notEmpty;
    private Condition notFull;

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new int[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    public void enqueue(int element) throws InterruptedException {
        lock.lock();
        try {
            while (size == capacity) {
                notFull.await();
            }

            queue[tail] = element;
            tail = (tail + 1) % capacity;
            size++;

            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public int dequeue() throws InterruptedException {
        lock.lock();
        try {
            while (size == 0) {
                notEmpty.await();
            }

            int element = queue[head];
            head = (head + 1) % capacity;
            size--;

            notFull.signal();
            return element;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }

    // Alternative implementation using Semaphores
    static class BoundedBlockingQueueSemaphore {
        private int[] queue;
        private int head, tail;
        private int capacity;
        private Semaphore enqueuePermits;
        private Semaphore dequeuePermits;
        private ReentrantLock lock;

        public BoundedBlockingQueueSemaphore(int capacity) {
            this.capacity = capacity;
            this.queue = new int[capacity];
            this.head = 0;
            this.tail = 0;
            this.enqueuePermits = new Semaphore(capacity);
            this.dequeuePermits = new Semaphore(0);
            this.lock = new ReentrantLock();
        }

        public void enqueue(int element) throws InterruptedException {
            enqueuePermits.acquire();
            lock.lock();
            try {
                queue[tail] = element;
                tail = (tail + 1) % capacity;
            } finally {
                lock.unlock();
            }
            dequeuePermits.release();
        }

        public int dequeue() throws InterruptedException {
            dequeuePermits.acquire();
            lock.lock();
            try {
                int element = queue[head];
                head = (head + 1) % capacity;
                return element;
            } finally {
                lock.unlock();
            }
        }

        public int size() {
            return dequeuePermits.availablePermits();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedBlockingQueue queue = new BoundedBlockingQueue(2);

        // Test basic functionality
        queue.enqueue(1);
        queue.enqueue(0);
        System.out.println(queue.size()); // Expected: 2
        System.out.println(queue.dequeue()); // Expected: 1
        System.out.println(queue.dequeue()); // Expected: 0
        queue.enqueue(0);
        System.out.println(queue.size()); // Expected: 1
    }
}
