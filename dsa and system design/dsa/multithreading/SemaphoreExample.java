package multithreading;

import java.util.concurrent.Semaphore;

/**
 * Q8: Use Semaphore for controlling access to resources
 * Demonstrates limiting concurrent access to a resource
 */
public class SemaphoreExample {
    private static final int MAX_CONCURRENT_THREADS = 3;
    private static final int TOTAL_THREADS = 10;
    private static final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_THREADS);

    public static void main(String[] args) {
        LimitedResource resource = new LimitedResource();

        for (int i = 0; i < TOTAL_THREADS; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + threadId + " is waiting for a permit");
                    semaphore.acquire();
                    System.out.println("Thread " + threadId + " acquired a permit");

                    resource.access();

                    System.out.println("Thread " + threadId + " releasing permit");
                    semaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    static class LimitedResource {
        private int count = 0;

        public void access() throws InterruptedException {
            synchronized (this) {
                count++;
                System.out.println("Thread " + Thread.currentThread().getId()
                        + " accessing resource. Current count: " + count);

                if (count > MAX_CONCURRENT_THREADS) {
                    System.err.println("Error: More than " + MAX_CONCURRENT_THREADS
                            + " threads accessing resource!");
                }

                Thread.sleep(1000);

                synchronized (this) {
                    count--;
                }
            }
        }
    }
}
