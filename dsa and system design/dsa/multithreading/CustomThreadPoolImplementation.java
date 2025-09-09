package multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Q24: Implement a custom thread pool from scratch
 * Shows how thread pools work internally
 */
public class CustomThreadPoolImplementation {

    public static void main(String[] args) {
        System.out.println("=== Custom Thread Pool Implementation ===");

        // Create a custom thread pool
        CustomThreadPool threadPool = new CustomThreadPool(3);

        // Submit tasks
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            threadPool.submit(() -> {
                System.out.println("Task " + taskId + " started by " +
                        Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Task " + taskId + " completed by " +
                        Thread.currentThread().getName());
            });
        }

        // Wait a bit
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Shutting down the thread pool...");
        threadPool.shutdown();

        System.out.println("Main thread exiting");
    }

    public static class CustomThreadPool {
        private final BlockingQueue<Runnable> taskQueue;
        private final List<WorkerThread> workers;
        private final AtomicBoolean isShutdown = new AtomicBoolean(false);
        private final AtomicInteger completedTasks = new AtomicInteger(0);

        public CustomThreadPool(int numThreads) {
            taskQueue = new LinkedBlockingQueue<>();
            workers = new ArrayList<>(numThreads);

            // Create and start worker threads
            for (int i = 0; i < numThreads; i++) {
                WorkerThread worker = new WorkerThread("Worker-" + i);
                workers.add(worker);
                worker.start();
            }

            System.out.println("Thread pool created with " + numThreads + " threads");
        }

        public void submit(Runnable task) {
            if (isShutdown.get()) {
                throw new IllegalStateException("Thread pool has been shut down");
            }

            try {
                taskQueue.put(task);
                System.out.println("Task submitted, queue size: " + taskQueue.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void shutdown() {
            isShutdown.set(true);

            // Interrupt all worker threads
            for (WorkerThread worker : workers) {
                worker.interrupt();
            }

            // Wait for all workers to complete
            for (WorkerThread worker : workers) {
                try {
                    worker.join(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("Thread pool shut down. Completed tasks: " + completedTasks.get());
        }

        private class WorkerThread extends Thread {
            public WorkerThread(String name) {
                super(name);
            }

            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Runnable task;

                        // If shutting down, don't wait for new tasks
                        if (isShutdown.get() && taskQueue.isEmpty()) {
                            break;
                        }

                        // Take a task from the queue, waiting if necessary
                        task = taskQueue.take();

                        // Execute the task
                        task.run();

                        // Increment completed tasks counter
                        completedTasks.incrementAndGet();

                    } catch (InterruptedException e) {
                        // If interrupted while waiting, check if we should exit
                        if (isShutdown.get()) {
                            break;
                        }
                    } catch (Exception e) {
                        // Log any exception from task execution
                        System.err.println("Error executing task in " + getName() + ": " + e.getMessage());
                    }
                }

                System.out.println(getName() + " is shutting down");
            }
        }
    }
}
