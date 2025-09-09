package multithreading;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Q15: Convert Asynchronous to Synchronous Call
 * Shows different techniques to make an asynchronous operation appear
 * synchronous
 */
public class AsyncToSyncExample {
    public static void main(String[] args) {
        System.out.println("Method 1: Using CountDownLatch");
        method1_usingCountDownLatch();

        System.out.println("\nMethod 2: Using CompletableFuture");
        method2_usingCompletableFuture();

        System.out.println("\nMethod 3: Using Join/Wait");
        method3_usingWait();
    }

    public static void method1_usingCountDownLatch() {
        CountDownLatch latch = new CountDownLatch(1);

        // Simulated asynchronous call
        Thread asyncThread = new Thread(() -> {
            try {
                System.out.println("Async operation started");
                Thread.sleep(1000);
                System.out.println("Async operation completed");
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        asyncThread.start();

        try {
            System.out.println("Waiting for async operation to complete");
            latch.await(); // Wait until the async operation completes
            System.out.println("Continuing with the main thread after async completion");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void method2_usingCompletableFuture() {
        CompletableFuture<String> future = new CompletableFuture<>();

        // Simulated asynchronous call
        new Thread(() -> {
            try {
                System.out.println("Async operation started");
                Thread.sleep(1000);
                System.out.println("Async operation completed");
                future.complete("Operation result");
            } catch (InterruptedException e) {
                future.completeExceptionally(e);
                Thread.currentThread().interrupt();
            }
        }).start();

        try {
            System.out.println("Waiting for async operation to complete");
            String result = future.get(); // This will block until the future is completed
            System.out.println("Result: " + result);
            System.out.println("Continuing with the main thread after async completion");
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void method3_usingWait() {
        Object lock = new Object();
        AsyncAPI asyncAPI = new AsyncAPI();

        asyncAPI.performAsyncOperation(result -> {
            synchronized (lock) {
                System.out.println("Async callback received: " + result);
                lock.notify();
            }
        });

        synchronized (lock) {
            try {
                System.out.println("Waiting for async operation to complete");
                lock.wait(5000); // Wait with a timeout to avoid infinite wait
                System.out.println("Continuing with the main thread after async completion");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class AsyncAPI {
        public void performAsyncOperation(Consumer<String> callback) {
            new Thread(() -> {
                try {
                    System.out.println("Async operation started");
                    Thread.sleep(1000);
                    System.out.println("Async operation completed");
                    callback.accept("Operation result");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
