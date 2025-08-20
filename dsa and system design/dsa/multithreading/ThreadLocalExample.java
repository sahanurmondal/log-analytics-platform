package multithreading;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Q10: Use ThreadLocal for thread-confined variables
 * Shows how to maintain thread-specific variables that are not shared
 */
public class ThreadLocalExample {
    // Bad practice: SimpleDateFormat is not thread-safe
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Good practice: Use ThreadLocal to give each thread its own instance
    private static final ThreadLocal<SimpleDateFormat> threadLocalDateFormat = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 20; i++) {
            final int id = i;
            executor.submit(() -> {
                // Using shared dateFormat (not thread-safe)
                String sharedResult = formatDateUnsafe(new Date());

                // Using ThreadLocal dateFormat (thread-safe)
                String threadLocalResult = formatDateSafe(new Date());

                System.out.println("Thread " + id + " - Unsafe: " + sharedResult
                        + ", Safe: " + threadLocalResult);
            });
        }

        executor.shutdown();

        // Example of using ThreadLocal for user context
        System.out.println("\nExample of thread-local user context:");
        new UserContextExample().run();
    }

    private static String formatDateUnsafe(Date date) {
        // Potentially unsafe because multiple threads access the same formatter
        return dateFormat.format(date);
    }

    private static String formatDateSafe(Date date) {
        // Safe because each thread gets its own formatter instance
        return threadLocalDateFormat.get().format(date);
    }

    static class UserContextExample {
        private static class UserContext {
            private final String userName;

            public UserContext(String userName) {
                this.userName = userName;
            }

            public String getUserName() {
                return userName;
            }
        }

        private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

        public void run() {
            // Create threads for different users
            Thread userThread1 = new Thread(() -> processUser("Alice"));
            Thread userThread2 = new Thread(() -> processUser("Bob"));
            Thread userThread3 = new Thread(() -> processUser("Charlie"));

            userThread1.start();
            userThread2.start();
            userThread3.start();

            try {
                userThread1.join();
                userThread2.join();
                userThread3.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void processUser(String name) {
            // Set up user context for this thread
            userContext.set(new UserContext(name));

            // Process steps where each accesses the thread-local user
            step1();
            step2();
            step3();

            // Clean up ThreadLocal to prevent memory leaks
            userContext.remove();
        }

        private void step1() {
            System.out.println(Thread.currentThread().getName()
                    + " Step 1: Processing for user " + userContext.get().getUserName());
        }

        private void step2() {
            System.out.println(Thread.currentThread().getName()
                    + " Step 2: Processing for user " + userContext.get().getUserName());
        }

        private void step3() {
            System.out.println(Thread.currentThread().getName()
                    + " Step 3: Processing for user " + userContext.get().getUserName());
        }
    }
}
