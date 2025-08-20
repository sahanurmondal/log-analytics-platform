package multithreading;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Q20: Lock-free algorithms using atomic operations
 * Demonstrates non-blocking algorithms and compare-and-swap operations
 */
public class LockFreeAlgorithms {

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 5;
        int iterations = 1000;

        // Compare lock-based vs lock-free counter
        compareCounters(numThreads, iterations);

        // Lock-free stack implementation
        demonstrateLockFreeStack(numThreads);

        // ABA problem and solution
        demonstrateABAProblem();
    }

    private static void compareCounters(int numThreads, int iterations) throws InterruptedException {
        System.out.println("=== Lock-based vs Lock-free Counter ===");

        // Lock-based counter
        LockBasedCounter lockCounter = new LockBasedCounter();

        // Lock-free counter
        LockFreeCounter atomicCounter = new LockFreeCounter();

        // Test lock-based counter
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch1 = new CountDownLatch(numThreads);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    lockCounter.increment();
                    lockCounter.get();
                }
                latch1.countDown();
            });
        }

        latch1.await();
        long lockBasedTime = System.currentTimeMillis() - startTime;

        // Test lock-free counter
        CountDownLatch latch2 = new CountDownLatch(numThreads);
        startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    atomicCounter.increment();
                    atomicCounter.get();
                }
                latch2.countDown();
            });
        }

        latch2.await();
        long lockFreeTime = System.currentTimeMillis() - startTime;

        System.out.println("Lock-based counter time: " + lockBasedTime + " ms, value: " + lockCounter.get());
        System.out.println("Lock-free counter time: " + lockFreeTime + " ms, value: " + atomicCounter.get());

        executor.shutdown();
    }

    private static void demonstrateLockFreeStack(int numThreads) throws InterruptedException {
        System.out.println("\n=== Lock-free Stack Implementation ===");

        LockFreeStack<Integer> stack = new LockFreeStack<>();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads * 2);

        // Push threads
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        stack.push(threadNum * 1000 + j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Pop threads
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    int count = 0;
                    while (count < 900) {
                        Integer item = stack.pop();
                        if (item != null) {
                            count++;
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("Final stack size: " + stack.approximate_size());

        executor.shutdown();
    }

    private static void demonstrateABAProblem() {
        System.out.println("\n=== ABA Problem Demonstration ===");

        // Setup - create a stack with elements A, B, C (top is A)
        LockFreeStack<String> stack = new LockFreeStack<>();
        stack.push("C");
        stack.push("B");
        stack.push("A");

        System.out.println("Initial stack: A (top), B, C");

        // Thread 1 reads the current top node (A) and its next (B)
        // but gets suspended before completing the pop operation
        LockFreeStack<String>.Node<String> topNode = stack.head.get();
        LockFreeStack<String>.Node<String> nextNode = topNode.next;

        System.out.println("Thread 1 reads top (A) and next (B), but gets suspended");

        // Thread 2 comes in and:
        // 1. Pops A -> stack is now: B (top), C
        System.out.println("Thread 2 pops A -> stack is now: B (top), C");
        String poppedA = stack.pop();

        // 2. Pops B -> stack is now: C (top)
        System.out.println("Thread 2 pops B -> stack is now: C (top)");
        String poppedB = stack.pop();

        // 3. Pushes A back -> stack is now: A (top), C
        System.out.println("Thread 2 pushes A back -> stack is now: A (top), C");
        stack.push("A");

        // Now Thread 1 resumes:
        // It compares the current top with its saved top - they're both A!
        // It doesn't detect that the stack was modified
        System.out.println("Thread 1 resumes, compares current top with saved top");
        System.out.println("Both are A, so it thinks nothing changed, but B is gone!");

        // This is the ABA problem - Thread 1 doesn't know the stack changed
        // The solution: use AtomicStampedReference which includes a stamp/version

        System.out.println("\nSolution: Using AtomicStampedReference");

        // Demonstrate AtomicStampedReference which prevents ABA
        AtomicStampedStack<String> stampedStack = new AtomicStampedStack<>();
        stampedStack.push("C");
        stampedStack.push("B");
        stampedStack.push("A");

        System.out.println("Now using versioned references that prevent ABA problem");
    }

    // Lock-based counter implementation
    static class LockBasedCounter {
        private long value = 0;

        public synchronized void increment() {
            value++;
        }

        public synchronized long get() {
            return value;
        }
    }

    // Lock-free counter implementation
    static class LockFreeCounter {
        private AtomicLong value = new AtomicLong(0);

        public void increment() {
            value.incrementAndGet();
        }

        public long get() {
            return value.get();
        }
    }

    // Lock-free stack implementation using AtomicReference
    static class LockFreeStack<T> {
        AtomicReference<Node<T>> head = new AtomicReference<>(null);
        AtomicInteger approxSize = new AtomicInteger(0); // Not exact, but gives an idea

        public void push(T value) {
            Node<T> newHead = new Node<>(value);
            Node<T> oldHead;

            do {
                oldHead = head.get();
                newHead.next = oldHead;
            } while (!head.compareAndSet(oldHead, newHead));

            approxSize.incrementAndGet();
        }

        public T pop() {
            Node<T> oldHead;
            Node<T> newHead;

            do {
                oldHead = head.get();
                if (oldHead == null) {
                    return null; // stack is empty
                }
                newHead = oldHead.next;
            } while (!head.compareAndSet(oldHead, newHead));

            approxSize.decrementAndGet();
            return oldHead.value;
        }

        public int approximate_size() {
            return approxSize.get();
        }

        class Node<E> {
            final E value;
            Node<E> next;

            Node(E value) {
                this.value = value;
            }
        }
    }

    // Stack implementation using AtomicStampedReference to avoid ABA problem
    static class AtomicStampedStack<T> {
        static class Node<E> {
            final E value;
            Node<E> next;

            Node(E value) {
                this.value = value;
            }
        }

        private final AtomicStampedReference<Node<T>> top = new AtomicStampedReference<>(null, 0);

        public void push(T value) {
            Node<T> newHead = new Node<>(value);
            Node<T> oldHead;
            int stamp;

            do {
                int[] stampHolder = new int[1];
                oldHead = top.get(stampHolder);
                stamp = stampHolder[0];
                newHead.next = oldHead;
            } while (!top.compareAndSet(oldHead, newHead, stamp, stamp + 1));
        }

        public T pop() {
            Node<T> oldHead;
            Node<T> newHead;
            int stamp;

            do {
                int[] stampHolder = new int[1];
                oldHead = top.get(stampHolder);
                stamp = stampHolder[0];

                if (oldHead == null) {
                    return null; // stack is empty
                }

                newHead = oldHead.next;
            } while (!top.compareAndSet(oldHead, newHead, stamp, stamp + 1));

            return oldHead.value;
        }
    }
}
