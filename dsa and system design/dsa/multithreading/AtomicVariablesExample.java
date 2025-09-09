package multithreading;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Q9: Use Atomic variables for thread-safe operations without locks
 * Shows how to perform thread-safe operations on variables
 */
public class AtomicVariablesExample {
    private static final int NUM_THREADS = 10;
    private static final int NUM_INCREMENTS = 1000;

    // Regular integer - not thread safe
    private static int regularCounter = 0;
    // AtomicInteger - thread safe
    private static AtomicInteger atomicCounter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // Submit tasks to the executor
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < NUM_INCREMENTS; j++) {
                    // Unsafe increment
                    regularCounter++;

                    // Safe increment using AtomicInteger
                    atomicCounter.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Expected count: " + (NUM_THREADS * NUM_INCREMENTS));
        System.out.println("Regular counter final value: " + regularCounter);
        System.out.println("AtomicInteger counter final value: " + atomicCounter.get());

        // Demonstrating other atomic operations
        AtomicInteger value = new AtomicInteger(10);
        System.out.println("\nDemonstrating other atomic operations:");
        System.out.println("Initial value: " + value.get());
        System.out.println("getAndIncrement(): " + value.getAndIncrement() + ", after: " + value.get());
        System.out.println("getAndAdd(5): " + value.getAndAdd(5) + ", after: " + value.get());
        System.out.println("getAndSet(20): " + value.getAndSet(20) + ", after: " + value.get());
        System.out.println("compareAndSet(20, 30): " + value.compareAndSet(20, 30) + ", after: " + value.get());
        System.out.println("compareAndSet(20, 40): " + value.compareAndSet(20, 40) + ", after: " + value.get());
    }
}
