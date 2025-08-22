package multithreading;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Q22: Virtual Threads (Project Loom) for high-throughput concurrency
 * Demonstrates how to use virtual threads (lightweight threads) introduced in
 * JDK 19/21
 * Note: This requires Java 19+ with preview features enabled or Java 21+
 */
public class VirtualThreadsExample {

    private static final int TASK_COUNT = 10_000;

    public static void main(String[] args) {
        System.out.println("Note: This example requires Java 19+ with preview features or Java 21+");
        System.out.println("If you're using an older Java version, this will use platform threads instead\n");

        try {
            compareThreadingModels();
            demonstrateVirtualThreads();
        } catch (Exception e) {
            System.err.println("Error running virtual threads example: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void compareThreadingModels() throws Exception {
        System.out.println("=== Comparing Threading Models ===");

        // 1. Platform threads with fixed thread pool
        Instant start = Instant.now();

        ExecutorService executorService = Executors.newFixedThreadPool(200);
        try {
            AtomicInteger completedTasks = new AtomicInteger(0);

            for (int i = 0; i < TASK_COUNT; i++) {
                executorService.submit(() -> {
                    try {
                        // Simulate I/O blocking operation
                        Thread.sleep(50);
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            executorService.shutdown();

            // Wait for tasks to complete or timeout
            boolean completed = false;
            try {
                // Use reflection for compatibility with older Java versions
                java.lang.reflect.Method awaitTermination = ExecutorService.class.getMethod("awaitTermination",
                        long.class, java.util.concurrent.TimeUnit.class);
                completed = (boolean) awaitTermination.invoke(executorService, 30L,
                        java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception e) {
                System.out.println("Error in await termination: " + e);
            }

            System.out.println("Platform threads: " + completedTasks.get() +
                    " tasks completed, all done: " + completed);
        } finally {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdownNow();
            }
        }

        Duration platformTime = Duration.between(start, Instant.now());
        System.out.println("Platform threads execution time: " + platformTime.toMillis() + " ms");

        // 2. Virtual threads executor
        // Using try-with-resources to auto-close the executor
        start = Instant.now();
        AtomicInteger completedTasks = new AtomicInteger(0);

        try {
            // Try to use newVirtualThreadPerTaskExecutor if available (Java 19+)
            ExecutorService virtualExecutor;
            try {
                java.lang.reflect.Method newVirtualThreadPerTaskExecutor = Executors.class
                        .getMethod("newVirtualThreadPerTaskExecutor");
                virtualExecutor = (ExecutorService) newVirtualThreadPerTaskExecutor.invoke(null);
            } catch (Exception e) {
                // Fallback to cached thread pool if virtual threads not available
                System.out.println("Virtual threads not available, falling back to cached thread pool");
                virtualExecutor = Executors.newCachedThreadPool();
            }

            ExecutorService executor = virtualExecutor;
            try {
                for (int i = 0; i < TASK_COUNT; i++) {
                    executor.submit(() -> {
                        try {
                            // Simulate I/O blocking operation
                            Thread.sleep(50);
                            completedTasks.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                }

                // Wait for tasks to complete
                boolean completed = false;
                try {
                    java.lang.reflect.Method awaitTermination = ExecutorService.class.getMethod("awaitTermination",
                            long.class, java.util.concurrent.TimeUnit.class);
                    executor.shutdown();
                    completed = (boolean) awaitTermination.invoke(executor, 30L, java.util.concurrent.TimeUnit.SECONDS);
                } catch (Exception e) {
                    System.out.println("Error in await termination: " + e);
                }

                System.out.println("Virtual threads: " + completedTasks.get() +
                        " tasks completed, all done: " + completed);
            } finally {
                if (executor != null && !executor.isShutdown()) {
                    executor.shutdownNow();
                }
            }
        } catch (Exception e) {
            System.out.println("Error working with virtual threads: " + e);
        }

        Duration virtualTime = Duration.between(start, Instant.now());
        System.out.println("Virtual threads execution time: " + virtualTime.toMillis() + " ms");
        System.out.println("Performance difference: " +
                (platformTime.toMillis() > virtualTime.toMillis()
                        ? "Virtual threads were " + (platformTime.toMillis() / Math.max(1, virtualTime.toMillis()))
                                + "x faster"
                        : "Platform threads were " + (virtualTime.toMillis() / Math.max(1, platformTime.toMillis()))
                                + "x faster"));
    }

    private static void demonstrateVirtualThreads() {
        System.out.println("\n=== Virtual Thread Features ===");

        try {
            // Try to create a named virtual thread using Thread.Builder
            Thread virtualThread = null;
            try {
                // Use reflection to handle different Java versions
                Class<?> builderClass = Class.forName("java.lang.Thread$Builder");

                java.lang.reflect.Method ofVirtualMethod = builderClass.getMethod("ofVirtual");
                Object virtualBuilder = ofVirtualMethod.invoke(null);

                java.lang.reflect.Method nameMethod = virtualBuilder.getClass().getMethod("name", String.class);
                virtualBuilder = nameMethod.invoke(virtualBuilder, "MyVirtualThread");

                java.lang.reflect.Method startMethod = virtualBuilder.getClass().getMethod("start", Runnable.class);
                virtualThread = (Thread) startMethod.invoke(virtualBuilder, (Runnable) () -> {
                    System.out.println("Running in virtual thread: " + Thread.currentThread());
                    System.out.println("Is virtual: " + isVirtualThread(Thread.currentThread()));
                });

                virtualThread.join();

            } catch (ClassNotFoundException e) {
                System.out.println(
                        "Thread.Builder not found. Virtual threads likely not available in this Java version.");
            } catch (Exception e) {
                System.out.println("Error creating virtual thread: " + e);
            }

            // Show how to create many virtual threads
            System.out.println("\nCreating many virtual threads:");
            createManyThreads();

        } catch (Exception e) {
            System.out.println("Error demonstrating virtual threads: " + e);
        }
    }

    private static boolean isVirtualThread(Thread thread) {
        try {
            // Try to use the isVirtual method if available (Java 19+)
            java.lang.reflect.Method isVirtualMethod = Thread.class.getMethod("isVirtual");
            return (boolean) isVirtualMethod.invoke(thread);
        } catch (Exception e) {
            // Fall back to checking the thread class name as a heuristic
            return thread.getClass().getName().contains("VirtualThread");
        }
    }

    private static void createManyThreads() {
        final int THREAD_COUNT = 100_000;
        System.out.println("Attempting to create " + THREAD_COUNT + " threads");

        try {
            ExecutorService executor;
            try {
                // Try to use virtual threads if available
                java.lang.reflect.Method newVirtualThreadPerTaskExecutor = Executors.class
                        .getMethod("newVirtualThreadPerTaskExecutor");
                executor = (ExecutorService) newVirtualThreadPerTaskExecutor.invoke(null);
                System.out.println("Using virtual threads");
            } catch (Exception e) {
                // Fall back to a limited number of platform threads
                System.out.println("Virtual threads not available, using limited platform threads");
                executor = Executors.newFixedThreadPool(1000);
            }

            final ExecutorService finalExecutor = executor;
            AtomicInteger counter = new AtomicInteger(0);
            Instant start = Instant.now();

            try {
                IntStream.range(0, THREAD_COUNT).forEach(ignored -> {
                    finalExecutor.submit(() -> {
                        counter.incrementAndGet();
                        try {
                            Thread.sleep(10); // Simulate some work
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        return null;
                    });
                });
            } catch (Exception e) {
                System.out.println("Error submitting tasks: " + e.getMessage());
            } finally {
                if (finalExecutor != null && !finalExecutor.isShutdown()) {
                    finalExecutor.shutdown();
                }
            }

            boolean terminated = false;
            try {
                java.lang.reflect.Method awaitTermination = ExecutorService.class.getMethod("awaitTermination",
                        long.class, java.util.concurrent.TimeUnit.class);
                terminated = (boolean) awaitTermination.invoke(executor, 30L, java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception e) {
                System.out.println("Error waiting for termination: " + e);
            }

            Duration duration = Duration.between(start, Instant.now());
            System.out.println("Created and executed " + counter.get() + " threads");
            System.out.println("All completed: " + terminated);
            System.out.println("Time taken: " + duration.toMillis() + " ms");

        } catch (Exception e) {
            System.out.println("Error in createManyThreads: " + e);
        }
    }
}
