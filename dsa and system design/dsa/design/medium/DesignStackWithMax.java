package design.medium;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Variation: Max Stack
 *
 * Description:
 * Design a stack that supports push, pop, top, and retrieving the maximum
 * element in constant time.
 *
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 3 * 10^4 calls will be made to push, pop, top, and getMax.
 *
 * Follow-up:
 * - Can you optimize for thread safety?
 * - Can you generalize for min stack?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n) where n is number of elements
 * 
 * Company Tags: Amazon, Google, Apple
 */
public class DesignStackWithMax {

    private final Stack<Integer> stack;
    private final Stack<Integer> maxStack;
    private final boolean threadSafe;
    private final ReentrantReadWriteLock lock;

    public DesignStackWithMax() {
        this(false);
    }

    public DesignStackWithMax(boolean threadSafe) {
        this.stack = new Stack<>();
        this.maxStack = new Stack<>();
        this.threadSafe = threadSafe;
        this.lock = threadSafe ? new ReentrantReadWriteLock() : null;
    }

    public void push(int val) {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                pushInternal(val);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            pushInternal(val);
        }
    }

    private void pushInternal(int val) {
        stack.push(val);

        // If maxStack is empty or val is greater than or equal to current max
        if (maxStack.isEmpty() || val >= maxStack.peek()) {
            maxStack.push(val);
        }
    }

    public int pop() {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                return popInternal();
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            return popInternal();
        }
    }

    private int popInternal() {
        if (stack.isEmpty()) {
            throw new EmptyStackException();
        }

        int val = stack.pop();

        // If the popped value is the current maximum, remove it from maxStack
        if (!maxStack.isEmpty() && val == maxStack.peek()) {
            maxStack.pop();
        }

        return val;
    }

    public int top() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return topInternal();
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return topInternal();
        }
    }

    private int topInternal() {
        if (stack.isEmpty()) {
            throw new EmptyStackException();
        }
        return stack.peek();
    }

    public int getMax() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return getMaxInternal();
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return getMaxInternal();
        }
    }

    private int getMaxInternal() {
        if (maxStack.isEmpty()) {
            throw new EmptyStackException();
        }
        return maxStack.peek();
    }

    // Additional utility methods

    public boolean isEmpty() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return stack.isEmpty();
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return stack.isEmpty();
        }
    }

    public int size() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return stack.size();
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return stack.size();
        }
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
        stack.clear();
        maxStack.clear();
    }

    // Get all elements as a list (for debugging/testing)
    public List<Integer> toList() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return new ArrayList<>(stack);
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return new ArrayList<>(stack);
        }
    }

    @Override
    public String toString() {
        return String.format("MaxStack[size=%d, elements=%s, max=%s]",
                size(), toList(),
                isEmpty() ? "null" : getMax());
    }

    public static void main(String[] args) {
        System.out.println("=== Basic Max Stack Tests ===");
        DesignStackWithMax stack = new DesignStackWithMax();

        System.out.println("Initial state: " + stack);
        System.out.println("Is empty: " + stack.isEmpty()); // true

        // Test basic operations
        System.out.println("\nPushing -2, 0, -3:");
        stack.push(-2);
        stack.push(0);
        stack.push(-3);
        System.out.println("Stack state: " + stack);
        System.out.println("Max: " + stack.getMax()); // 0

        System.out.println("\nAfter pop:");
        stack.pop();
        System.out.println("Top: " + stack.top()); // 0
        System.out.println("Max: " + stack.getMax()); // 0

        System.out.println("\nTesting duplicate max values:");
        stack.clear();
        stack.push(2);
        stack.push(2);
        stack.push(1);
        System.out.println("Stack state: " + stack);
        System.out.println("Max: " + stack.getMax()); // 2

        System.out.println("After popping 1:");
        stack.pop();
        System.out.println("Max: " + stack.getMax()); // 2

        System.out.println("After popping first 2:");
        stack.pop();
        System.out.println("Max: " + stack.getMax()); // 2

        System.out.println("\n=== Edge Case Tests ===");
        DesignStackWithMax edgeStack = new DesignStackWithMax();

        // Test empty stack operations
        System.out.println("Testing empty stack operations:");
        try {
            edgeStack.pop();
        } catch (EmptyStackException e) {
            System.out.println("Pop from empty stack correctly threw exception");
        }

        try {
            edgeStack.top();
        } catch (EmptyStackException e) {
            System.out.println("Top from empty stack correctly threw exception");
        }

        try {
            edgeStack.getMax();
        } catch (EmptyStackException e) {
            System.out.println("GetMax from empty stack correctly threw exception");
        }

        // Test with extreme values
        System.out.println("\nTesting with extreme values:");
        edgeStack.push(Integer.MIN_VALUE);
        edgeStack.push(Integer.MAX_VALUE);
        edgeStack.push(0);
        System.out.println("Stack with extreme values: " + edgeStack);
        System.out.println("Max: " + edgeStack.getMax()); // Integer.MAX_VALUE

        System.out.println("\n=== Thread-Safe Stack Tests ===");
        DesignStackWithMax threadSafeStack = new DesignStackWithMax(true);

        // Simulate concurrent access
        Thread pusher = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                threadSafeStack.push(i);
                System.out.println("Pushed: " + i + ", Max: " + threadSafeStack.getMax());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread popper = new Thread(() -> {
            try {
                Thread.sleep(500); // Let pusher get ahead
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            for (int i = 0; i < 15; i++) {
                try {
                    if (!threadSafeStack.isEmpty()) {
                        int val = threadSafeStack.pop();
                        int max = threadSafeStack.isEmpty() ? -1 : threadSafeStack.getMax();
                        System.out.println("Popped: " + val + ", New Max: " + max);
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (EmptyStackException e) {
                    System.out.println("Stack became empty during concurrent access");
                }
            }
        });

        pusher.start();
        popper.start();

        try {
            pusher.join();
            popper.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final thread-safe stack state: " + threadSafeStack);

        System.out.println("\n=== Performance Test ===");
        DesignStackWithMax perfStack = new DesignStackWithMax();

        long startTime = System.currentTimeMillis();

        // Performance test: 30k operations
        for (int i = 0; i < 10000; i++) {
            perfStack.push(i);
        }

        for (int i = 0; i < 5000; i++) {
            perfStack.pop();
        }

        for (int i = 0; i < 5000; i++) {
            perfStack.getMax();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("30k operations completed in " + (endTime - startTime) + "ms");
        System.out.println("Final stack size: " + perfStack.size());
        System.out.println("Final max: " + perfStack.getMax());
    }
}
