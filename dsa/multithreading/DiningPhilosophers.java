package multithreading;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Q14: Dining Philosophers Problem
 * Classic synchronization problem with philosophers sitting at a table sharing
 * forks
 */
public class DiningPhilosophers {
    private static final int NUM_PHILOSOPHERS = 5;

    public static void main(String[] args) {
        DiningPhilosophersSolution solution = new DiningPhilosophersSolution();

        Thread[] philosophers = new Thread[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            final int philosopherId = i;
            philosophers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 3; j++) { // Each philosopher eats 3 times
                        solution.pickForks(philosopherId);
                        System.out.println("Philosopher " + philosopherId + " is eating");
                        Thread.sleep(100); // Eating time
                        solution.putForks(philosopherId);
                        System.out.println("Philosopher " + philosopherId + " is thinking");
                        Thread.sleep(100); // Thinking time
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            philosophers[i].start();
        }

        for (Thread philosopher : philosophers) {
            try {
                philosopher.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class DiningPhilosophersSolution {
        // Solution 1: Using locks for each fork
        private ReentrantLock[] forks = new ReentrantLock[NUM_PHILOSOPHERS];

        // Solution 2: Using a semaphore to limit concurrent diners
        private Semaphore semaphore = new Semaphore(NUM_PHILOSOPHERS - 1);

        // Solution 3: Breaking the circular dependency by changing fork pickup order
        private ReentrantLock tableLock = new ReentrantLock();

        public DiningPhilosophersSolution() {
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                forks[i] = new ReentrantLock();
            }
        }

        // Using solution 3: Breaking circular dependency
        public void pickForks(int philosopherId) throws InterruptedException {
            int leftFork = philosopherId;
            int rightFork = (philosopherId + 1) % NUM_PHILOSOPHERS;

            // Prevent deadlock by ensuring all philosophers pick forks in the same order
            if (philosopherId == NUM_PHILOSOPHERS - 1) {
                // Last philosopher picks right fork first
                forks[rightFork].lock();
                System.out.println("Philosopher " + philosopherId + " picked right fork");
                forks[leftFork].lock();
                System.out.println("Philosopher " + philosopherId + " picked left fork");
            } else {
                // Other philosophers pick left fork first
                forks[leftFork].lock();
                System.out.println("Philosopher " + philosopherId + " picked left fork");
                forks[rightFork].lock();
                System.out.println("Philosopher " + philosopherId + " picked right fork");
            }
        }

        public void putForks(int philosopherId) {
            int leftFork = philosopherId;
            int rightFork = (philosopherId + 1) % NUM_PHILOSOPHERS;

            forks[leftFork].unlock();
            System.out.println("Philosopher " + philosopherId + " put down left fork");
            forks[rightFork].unlock();
            System.out.println("Philosopher " + philosopherId + " put down right fork");
        }

        // Alternative implementation using semaphore
        public void pickForksWithSemaphore(int philosopherId) throws InterruptedException {
            semaphore.acquire(); // Limit the number of philosophers who can eat simultaneously

            int leftFork = philosopherId;
            int rightFork = (philosopherId + 1) % NUM_PHILOSOPHERS;

            forks[leftFork].lock();
            forks[rightFork].lock();
        }

        public void putForksWithSemaphore(int philosopherId) {
            int leftFork = philosopherId;
            int rightFork = (philosopherId + 1) % NUM_PHILOSOPHERS;

            forks[leftFork].unlock();
            forks[rightFork].unlock();

            semaphore.release();
        }
    }
}
