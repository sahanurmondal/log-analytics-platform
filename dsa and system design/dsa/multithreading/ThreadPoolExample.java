package multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Q4: Use a thread pool for executing tasks concurrently
 * Demonstrates how to use ExecutorService to manage threads efficiently
 */
public class ThreadPoolExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> results = new ArrayList<>();

        System.out.println("Submitting tasks to thread pool...");

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            Future<String> future = executor.submit(() -> {
                System.out.println("Task " + taskId + " executed by " +
                        Thread.currentThread().getName());
                Thread.sleep(500);
                return "Task " + taskId + " completed";
            });
            results.add(future);
        }

        System.out.println("All tasks submitted. Retrieving results...");

        // Get results
        for (Future<String> future : results) {
            try {
                String result = future.get();
                System.out.println("Result: " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Shutdown the executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Executor did not terminate in the specified time.");
                executor.shutdownNow();
            }
            System.out.println("Executor has been shutdown.");
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
