package multithreading;

import java.util.concurrent.CountDownLatch;

/**
 * Q6: Use CountDownLatch for coordination between threads
 * Shows how to make threads wait until a set of operations completes
 */
public class CountDownLatchExample {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(3);

        System.out.println("Main thread starting workers...");

        for (int i = 0; i < 3; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    System.out.println("Worker " + workerId + " starting");
                    Thread.sleep(1000 + (int) (Math.random() * 1000));
                    System.out.println("Worker " + workerId + " finished");
                    latch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        try {
            System.out.println("Main thread waiting for workers to complete");
            latch.await();
            System.out.println("All workers completed, main thread proceeding");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
