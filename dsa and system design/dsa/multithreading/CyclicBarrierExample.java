package multithreading;

import java.util.concurrent.CyclicBarrier;

/**
 * Q7: Use CyclicBarrier for thread synchronization
 * Shows how to synchronize multiple threads at a common point
 */
public class CyclicBarrierExample {
    public static void main(String[] args) {
        final int THREAD_COUNT = 3;
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT,
                () -> System.out.println("Barrier reached, all threads continue!"));

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + threadId + " working on first phase");
                    Thread.sleep(1000 + (int) (Math.random() * 1000));
                    System.out.println("Thread " + threadId + " waiting at barrier");
                    barrier.await();

                    System.out.println("Thread " + threadId + " working on second phase");
                    Thread.sleep(1000 + (int) (Math.random() * 1000));
                    System.out.println("Thread " + threadId + " waiting at barrier");
                    barrier.await();

                    System.out.println("Thread " + threadId + " completed all phases");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
