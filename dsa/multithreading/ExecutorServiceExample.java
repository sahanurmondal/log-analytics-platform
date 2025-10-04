package multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Q16: Demonstrate advanced ExecutorService usage patterns
 * Shows various ways to manage and control thread execution
 */
public class ExecutorServiceExample {
    public static void main(String[] args) {
        System.out.println("1. Basic thread pool execution:");
        basicExecution();

        System.out.println("\n2. Using Callable for results:");
        callableExecution();

        System.out.println("\n3. Executor service shutdown patterns:");
        shutdownPatterns();

        System.out.println("\n4. Handling exceptions in tasks:");
        exceptionHandling();

        System.out.println("\n5. Custom thread factory:");
        customThreadFactory();
    }

    private static void basicExecution() {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("Task " + taskId + " executing in " + threadName);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        shutdownAndAwaitTermination(executor, 5);
    }

    private static void callableExecution() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Integer>> results = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            Future<Integer> result = executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("Callable " + taskId + " executing in " + threadName);
                Thread.sleep(500);
                return taskId * 10;
            });
            results.add(result);
        }

        for (Future<Integer> result : results) {
            try {
                System.out.println("Result: " + result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        shutdownAndAwaitTermination(executor, 5);
    }

    private static void shutdownPatterns() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Task " + taskId + " started");
                    Thread.sleep(1000);
                    System.out.println("Task " + taskId + " completed");
                } catch (InterruptedException e) {
                    System.out.println("Task " + taskId + " interrupted");
                    Thread.currentThread().interrupt();
                }
                return null;
            });
        }

        System.out.println("Initiating shutdown...");
        executor.shutdown(); // Reject new tasks but allow existing ones to complete

        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                System.out.println("Timeout elapsed before termination, forcing shutdown...");

                // Cancel currently executing tasks
                List<Runnable> pendingTasks = executor.shutdownNow();
                System.out.println(pendingTasks.size() + " tasks were never commenced execution");

                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    System.out.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Shutdown completed");
    }

    private static void exceptionHandling() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Task that throws an exception
        Callable<Integer> failingTask = () -> {
            System.out.println("Starting task that will fail");
            throw new RuntimeException("Task failed intentionally");
        };

        Future<Integer> future = executor.submit(failingTask);

        try {
            Integer result = future.get(); // This will throw ExecutionException
            System.out.println("Result: " + result); // Never reached
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.out.println("Task execution failed: " + e.getCause().getMessage());
        }

        shutdownAndAwaitTermination(executor, 2);
    }

    private static void customThreadFactory() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int counter = 0;

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("CustomThread-" + counter++);
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.setUncaughtExceptionHandler(
                        (t, e) -> System.out.println("Thread " + t.getName() + " threw exception: " + e.getMessage()));
                return thread;
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(2, threadFactory);

        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " running in " + Thread.currentThread().getName());
                if (taskId == 1) {
                    throw new RuntimeException("Deliberate exception in task " + taskId);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        shutdownAndAwaitTermination(executor, 2);
    }

    // Helper method to properly shutdown an ExecutorService
    private static void shutdownAndAwaitTermination(ExecutorService pool, int timeoutSeconds) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
