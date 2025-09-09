package multithreading;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Q3: Create and detect a deadlock situation
 * Demonstrates how deadlocks occur and how to identify them
 */
public class DeadlockExample {
    private static final Object RESOURCE_1 = new Object();
    private static final Object RESOURCE_2 = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            synchronized (RESOURCE_1) {
                System.out.println("Thread 1: Holding resource 1...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                System.out.println("Thread 1: Waiting for resource 2...");

                synchronized (RESOURCE_2) {
                    System.out.println("Thread 1: Holding resource 1 and resource 2");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (RESOURCE_2) {
                System.out.println("Thread 2: Holding resource 2...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                System.out.println("Thread 2: Waiting for resource 1...");

                synchronized (RESOURCE_1) {
                    System.out.println("Thread 2: Holding resource 2 and resource 1");
                }
            }
        });

        thread1.start();
        thread2.start();

        // Deadlock detection
        try {
            Thread.sleep(1000);
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();

            if (deadlockedThreads != null) {
                System.out.println("Deadlock detected!");
                System.out.println("Number of deadlocked threads: " + deadlockedThreads.length);
            } else {
                System.out.println("No deadlock detected!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
