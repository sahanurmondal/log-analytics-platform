package multithreading;

/**
 * Q23: Java Memory Model and visibility problems
 * Demonstrates issues with memory visibility and how to fix them
 */
public class MemoryModelExample {

    // Bad example - no synchronization
    private static class UnsafeCounter {
        private int count = 0;
        private boolean running = true;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }

        public void stop() {
            running = false;
        }

        public boolean isRunning() {
            return running;
        }
    }

    // Good example - with proper synchronization
    private static class SafeCounter {
        private volatile int count = 0; // Volatile for visibility
        private volatile boolean running = true;

        // Synchronize to ensure atomic increment
        public synchronized void increment() {
            count++;
        }

        // No need to synchronize read of volatile field
        public int getCount() {
            return count;
        }

        // No need to synchronize write to volatile field
        public void stop() {
            running = false;
        }

        // No need to synchronize read of volatile field
        public boolean isRunning() {
            return running;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Java Memory Model Example ===");

        // Demonstrate the visibility problem
        demonstrateVisibilityProblem();

        // Demonstrate the solution with volatile
        demonstrateVolatileSolution();

        // Demonstrate the happens-before relationship
        demonstrateHappensBefore();
    }

    private static void demonstrateVisibilityProblem() throws InterruptedException {
        System.out.println("\n1. Visibility Problem Demonstration:");

        UnsafeCounter unsafeCounter = new UnsafeCounter();

        // Create a thread that increments the counter
        Thread incrementer = new Thread(() -> {
            System.out.println("Incrementer thread starting");
            while (unsafeCounter.isRunning()) {
                unsafeCounter.increment();
            }
            System.out.println("Incrementer thread stopped");
        });

        // Start the thread
        incrementer.start();

        // Give it some time to run
        Thread.sleep(100);

        // Stop the thread - this might not be visible to the incrementer thread!
        unsafeCounter.stop();

        // Give the thread a chance to see the change and exit
        Thread.sleep(1000);

        // Check if the thread is still running
        boolean threadIsAlive = incrementer.isAlive();
        System.out.println("Thread still running: " + threadIsAlive);

        if (threadIsAlive) {
            System.out.println("Visibility problem detected - thread didn't see the stop flag change!");
            System.out.println("Final count reached before interrupting: " + unsafeCounter.getCount());
            incrementer.interrupt();
        } else {
            System.out.println("Thread stopped normally - final count: " + unsafeCounter.getCount());
        }

        // Wait for thread to finish
        incrementer.join(5000);
    }

    private static void demonstrateVolatileSolution() throws InterruptedException {
        System.out.println("\n2. Volatile Solution Demonstration:");

        SafeCounter safeCounter = new SafeCounter();

        // Create a thread that increments the counter
        Thread incrementer = new Thread(() -> {
            System.out.println("Incrementer thread starting with volatile");
            int lastCount = 0;
            int unchangedCount = 0;

            while (safeCounter.isRunning()) {
                safeCounter.increment();

                // Detect if we're making progress
                if (safeCounter.getCount() == lastCount) {
                    unchangedCount++;
                    if (unchangedCount > 1000000) {
                        System.out.println("Warning: Counter appears stuck!");
                        break;
                    }
                } else {
                    lastCount = safeCounter.getCount();
                    unchangedCount = 0;
                }
            }
            System.out.println("Incrementer thread with volatile stopped normally");
        });

        // Start the thread
        incrementer.start();

        // Give it some time to run
        Thread.sleep(500);

        // Get the count before stopping
        int countBeforeStop = safeCounter.getCount();

        // Stop the thread - this will be visible to the incrementer thread due to
        // volatile
        safeCounter.stop();

        // Wait for thread to finish
        incrementer.join(5000);

        System.out.println("Thread stopped: " + !incrementer.isAlive());
        System.out.println("Count before stop: " + countBeforeStop);
        System.out.println("Final count: " + safeCounter.getCount());
        System.out.println("Increments after stop signal: " + (safeCounter.getCount() - countBeforeStop));
    }

    private static void demonstrateHappensBefore() throws InterruptedException {
        System.out.println("\n3. Happens-Before Relationship Demonstration:");

        // Different synchronization mechanisms establish happens-before relationships

        // 1. Synchronized method/block
        Object lock = new Object();
        int[] data = new int[10];

        Thread writer = new Thread(() -> {
            synchronized (lock) {
                // Write to data
                for (int i = 0; i < data.length; i++) {
                    data[i] = i + 1;
                }
                System.out.println("Writer thread updated data");
            }
        });

        Thread reader = new Thread(() -> {
            synchronized (lock) {
                // Read from data - happens-after writer's synchronized block
                System.out.println("Reader thread reads: " + data[5]);
                // This is guaranteed to see the updated value due to happens-before
            }
        });

        writer.start();
        writer.join(); // Ensure writer completes before reader starts
        reader.start();
        reader.join();

        // 2. Thread start and join
        Thread t1 = new Thread(() -> {
            System.out.println("Thread t1 executing");
            data[0] = 42; // This write happens-before the main thread after join()
        });

        t1.start(); // start() creates a happens-before edge
        t1.join(); // join() creates a happens-before edge

        // This read happens-after t1's execution due to join()
        System.out.println("Main thread reads after join: " + data[0]);

        // 3. Volatile variables
        class VolatileExample {
            int normalVar = 0;
            volatile boolean flag = false;

            void writer() {
                normalVar = 100; // Write to normal variable
                flag = true; // Write to volatile - creates a memory barrier
            }

            void reader() {
                if (flag) { // Read volatile - also creates a memory barrier
                    // If flag is true, normalVar is guaranteed to be 100
                    System.out.println("Reader sees flag true, normalVar: " + normalVar);
                }
            }
        }

        VolatileExample ve = new VolatileExample();

        Thread volatileWriter = new Thread(ve::writer);
        Thread volatileReader = new Thread(ve::reader);

        volatileWriter.start();
        Thread.sleep(50); // Small delay to ensure writer executes first
        volatileReader.start();

        volatileWriter.join();
        volatileReader.join();

        System.out.println("All happens-before examples completed");
    }
}
