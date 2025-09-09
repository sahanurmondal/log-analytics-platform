package multithreading;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Q21: Advanced thread pool patterns and custom implementations
 * Shows how to create and use custom thread pools for different scenarios
 */
public class ThreadPoolPatterns {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Different ThreadPool Configurations ===");

        // Fixed Thread Pool
        demonstrateFixedThreadPool();

        // Cached Thread Pool
        demonstrateCachedThreadPool();

        // Custom Thread Pool with custom thread factory
        demonstrateCustomThreadPool();

        // Work stealing pool
        demonstrateWorkStealingPool();

        // Rejected execution handling
        demonstrateRejectedExecution();

        // Fork/Join pool
        demonstrateForkJoinPool();
    }

    private static void demonstrateFixedThreadPool() throws InterruptedException {
        System.out.println("\n1. Fixed Thread Pool:");

        ExecutorService fixedPool = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            fixedPool.submit(() -> {
                System.out.println("Task " + taskId + " executed by " +
                        Thread.currentThread().getName());
                try {
                    // Simulate work
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        fixedPool.shutdown();
        fixedPool.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("Fixed pool tasks completed");
    }

    private static void demonstrateCachedThreadPool() throws InterruptedException {
        System.out.println("\n2. Cached Thread Pool:");

        ExecutorService cachedPool = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(20);

        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            cachedPool.submit(() -> {
                try {
                    System.out.println("Task " + taskId + " executed by " +
                            Thread.currentThread().getName());

                    // Different execution times to show thread reuse
                    Thread.sleep(taskId % 3 == 0 ? 1000 : 200);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Allow some time to see thread reuse/timeout behavior
        System.out.println("All tasks submitted, waiting to see thread pool behavior...");
        Thread.sleep(2000);

        cachedPool.shutdown();
        cachedPool.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("Cached pool shutdown completed");
    }

    private static void demonstrateCustomThreadPool() throws InterruptedException {
        System.out.println("\n3. Custom Thread Pool:");

        // Create a thread factory that names threads and handles uncaught exceptions
        ThreadFactory customThreadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "CustomThread-" + threadNumber.getAndIncrement());

                // Set thread as daemon (won't prevent JVM shutdown)
                thread.setDaemon(false);

                // Set priority
                thread.setPriority(Thread.NORM_PRIORITY);

                // Set uncaught exception handler
                thread.setUncaughtExceptionHandler((t, e) -> System.err.println("Thread " + t.getName() +
                        " threw an exception: " + e.getMessage()));

                return thread;
            }
        };

        // Create a ThreadPoolExecutor with custom parameters
        ThreadPoolExecutor customPool = new ThreadPoolExecutor(
                2, // Core pool size
                5, // Maximum pool size
                60, TimeUnit.SECONDS, // Keep alive time for idle threads
                new ArrayBlockingQueue<>(10), // Work queue size
                customThreadFactory, // Thread factory
                new ThreadPoolExecutor.CallerRunsPolicy() // Rejection handler
        );

        // Monitor the pool
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
        monitor.scheduleAtFixedRate(() -> {
            System.out.println(
                    String.format("[MONITOR] Pool size: %d, Active threads: %d, Task count: %d, Completed tasks: %d",
                            customPool.getPoolSize(),
                            customPool.getActiveCount(),
                            customPool.getTaskCount(),
                            customPool.getCompletedTaskCount()));
        }, 0, 1, TimeUnit.SECONDS);

        // Submit work that will cause pool to grow
        for (int i = 0; i < 15; i++) {
            final int taskId = i;
            customPool.submit(() -> {
                try {
                    System.out.println("Task " + taskId + " executed by " +
                            Thread.currentThread().getName());
                    Thread.sleep(2000); // Long-running task
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Let the monitoring run for a bit
        Thread.sleep(10000);

        monitor.shutdownNow();
        customPool.shutdown();
        customPool.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("Custom pool shutdown completed");
    }

    private static void demonstrateWorkStealingPool() throws InterruptedException {
        System.out.println("\n4. Work Stealing Pool:");

        // Create a work stealing pool (introduced in Java 8)
        ExecutorService workStealingPool = Executors.newWorkStealingPool();

        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            final long workTime = taskId % 3 == 0 ? 3000 : 500; // Some tasks take longer

            workStealingPool.submit(() -> {
                try {
                    System.out.println("Task " + taskId + " starting on " +
                            Thread.currentThread().getName());

                    // Simulate CPU-intensive work
                    long start = System.currentTimeMillis();
                    while (System.currentTimeMillis() - start < workTime) {
                        // Busy wait to simulate CPU work rather than blocking
                        if ((System.currentTimeMillis() % 1000) == 0) {
                            Thread.yield();
                        }
                    }

                    System.out.println("Task " + taskId + " completed on " +
                            Thread.currentThread().getName());

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Shutdown isn't strictly necessary for work stealing pool as it uses daemon
        // threads
        workStealingPool.shutdown();
        workStealingPool.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("Work stealing pool tasks completed");
    }

    private static void demonstrateRejectedExecution() throws InterruptedException {
        System.out.println("\n5. Rejected Execution Handling:");

        // Create a thread pool with limited queue size
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 2,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                        System.out.println("Task rejected: " + r.toString());
                        System.out.println("Executor state: " +
                                "[Pool size: " + e.getPoolSize() +
                                ", Active threads: " + e.getActiveCount() +
                                ", Queue size: " + e.getQueue().size() + "]");

                        // Here we could implement different strategies:
                        // 1. Run the task in the caller's thread
                        // r.run();

                        // 2. Discard the task
                        // (do nothing)

                        // 3. Throw an exception
                        // throw new RejectedExecutionException("Task " + r + " rejected");

                        // 4. Custom handling (e.g., add to another queue, log, etc.)
                        System.out.println("Custom handling: Saving task for later");
                    }
                });

        // Submit more tasks than the pool and queue can handle
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            try {
                executor.execute(() -> {
                    System.out.println("Task " + taskId + " executed by " +
                            Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000); // Each task takes 1 second
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                System.out.println("Submitted task " + taskId);
            } catch (Exception e) {
                System.out.println("Failed to submit task " + taskId + ": " + e);
            }

            // Add a small delay to make the output clearer
            Thread.sleep(100);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("Rejection demonstration completed");
    }

    private static void demonstrateForkJoinPool() {
        System.out.println("\n6. Fork/Join Pool:");

        // Create a ForkJoinPool with default parallelism
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // Generate a large array for demonstration
        int[] array = generateRandomArray(10000000);
        int[] arrayCopy = Arrays.copyOf(array, array.length);

        System.out.println("Array size: " + array.length);

        // Sort using single-threaded approach for comparison
        long startTime = System.currentTimeMillis();
        Arrays.sort(arrayCopy);
        long endTime = System.currentTimeMillis();
        System.out.println("Single-threaded sort time: " + (endTime - startTime) + " ms");

        // Sort using Fork/Join
        startTime = System.currentTimeMillis();

        // Submit the task to the pool
        ParallelMergeSort sortTask = new ParallelMergeSort(array);
        forkJoinPool.invoke(sortTask);

        endTime = System.currentTimeMillis();
        System.out.println("Fork/Join parallel sort time: " + (endTime - startTime) + " ms");

        // Verify the result is sorted
        System.out.println("Result is correctly sorted: " + isSorted(array));

        // Example of common fork/join pool
        System.out.println("\nUsing common ForkJoinPool for computation:");

        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            numbers.add(random.nextInt(100000));
        }

        startTime = System.currentTimeMillis();
        long sum = numbers.parallelStream()
                .mapToLong(i -> {
                    // Simulate complex computation
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return i * i;
                })
                .sum();
        endTime = System.currentTimeMillis();

        System.out.println("Parallel stream sum: " + sum);
        System.out.println("Parallel stream processing time: " + (endTime - startTime) + " ms");

        // Shutdown the pool
        forkJoinPool.shutdown();
        System.out.println("Fork/Join pool demonstration completed");
    }

    // Generate random array for sorting demonstration
    private static int[] generateRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(10000000);
        }

        return array;
    }

    // Check if array is sorted
    private static boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Recursive Fork/Join task for merge sort algorithm
     */
    static class ParallelMergeSort extends RecursiveAction {
        private static final int THRESHOLD = 10000; // Threshold for sequential processing
        private final int[] array;
        private final int start;
        private final int end;

        public ParallelMergeSort(int[] array) {
            this(array, 0, array.length);
        }

        private ParallelMergeSort(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                // Use sequential sort for small enough arrays
                Arrays.sort(array, start, end);
                return;
            }

            // Divide the array
            int middle = start + (end - start) / 2;

            // Fork the subtasks
            ParallelMergeSort left = new ParallelMergeSort(array, start, middle);
            ParallelMergeSort right = new ParallelMergeSort(array, middle, end);

            // Execute the right task directly to avoid excessive forking
            left.fork();
            right.compute();
            left.join();

            // Merge the results
            merge(start, middle, end);
        }

        private void merge(int start, int middle, int end) {
            int[] temp = new int[end - start];
            int i = start, j = middle, k = 0;

            // Merge two sorted subarrays into temp array
            while (i < middle && j < end) {
                if (array[i] <= array[j]) {
                    temp[k++] = array[i++];
                } else {
                    temp[k++] = array[j++];
                }
            }

            // Copy remaining elements
            while (i < middle) {
                temp[k++] = array[i++];
            }

            while (j < end) {
                temp[k++] = array[j++];
            }

            // Copy temp back to original array
            System.arraycopy(temp, 0, array, start, temp.length);
        }
    }
}
