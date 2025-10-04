package design.hard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Design Distributed Barrier
 *
 * Description: Design a distributed barrier for synchronizing multiple clients.
 * A barrier blocks until all specified parties arrive at the barrier point.
 *
 * Constraints:
 * - At most 10^5 operations
 * - Support multiple concurrent barriers
 * - Handle client failures gracefully
 *
 * Follow-up:
 * - Can you optimize for dynamic barrier size?
 * - Can you support timeout?
 * - How to handle network partitions?
 * 
 * Time Complexity: O(1) for await operation
 * Space Complexity: O(parties) per barrier
 * 
 * Company Tags: Google, Amazon, Facebook, Uber
 */
public class DesignDistributedBarrier {

    private final int parties;
    private final Set<String> arrivedClients;
    private final Object lock;
    private volatile boolean barrierPassed;
    private final Map<String, Long> clientTimestamps;

    public DesignDistributedBarrier(int parties) {
        this.parties = parties;
        this.arrivedClients = ConcurrentHashMap.newKeySet();
        this.lock = new Object();
        this.barrierPassed = false;
        this.clientTimestamps = new ConcurrentHashMap<>();
    }

    public boolean await(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }

        synchronized (lock) {
            // If barrier already passed, return immediately
            if (barrierPassed) {
                return true;
            }

            // Add client to arrived set
            arrivedClients.add(clientId);
            clientTimestamps.put(clientId, System.currentTimeMillis());

            // Check if all parties have arrived
            if (arrivedClients.size() >= parties) {
                barrierPassed = true;
                lock.notifyAll(); // Wake up all waiting threads
                return true;
            }

            // Wait for other parties
            try {
                while (!barrierPassed && arrivedClients.size() < parties) {
                    lock.wait();
                }
                return barrierPassed;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    public boolean awaitWithTimeout(String clientId, long timeoutMs) {
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }

        long startTime = System.currentTimeMillis();
        synchronized (lock) {
            if (barrierPassed) {
                return true;
            }

            arrivedClients.add(clientId);
            clientTimestamps.put(clientId, startTime);

            if (arrivedClients.size() >= parties) {
                barrierPassed = true;
                lock.notifyAll();
                return true;
            }

            try {
                long remainingTime = timeoutMs;
                while (!barrierPassed && arrivedClients.size() < parties && remainingTime > 0) {
                    long waitStart = System.currentTimeMillis();
                    lock.wait(remainingTime);
                    remainingTime -= (System.currentTimeMillis() - waitStart);
                }

                if (!barrierPassed && remainingTime <= 0) {
                    // Timeout occurred, remove client from barrier
                    arrivedClients.remove(clientId);
                    clientTimestamps.remove(clientId);
                    return false;
                }

                return barrierPassed;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                arrivedClients.remove(clientId);
                clientTimestamps.remove(clientId);
                return false;
            }
        }
    }

    public void reset() {
        synchronized (lock) {
            arrivedClients.clear();
            clientTimestamps.clear();
            barrierPassed = false;
        }
    }

    public int getWaitingParties() {
        return arrivedClients.size();
    }

    public boolean isBarrierPassed() {
        return barrierPassed;
    }

    public static void main(String[] args) {
        System.out.println("=== Distributed Barrier Test ===");

        // Test 1: Basic barrier functionality
        DesignDistributedBarrier barrier = new DesignDistributedBarrier(3);

        // Simulate concurrent clients
        Thread client1 = new Thread(() -> {
            System.out.println("Client A arriving...");
            boolean result = barrier.await("A");
            System.out.println("Client A result: " + result);
        });

        Thread client2 = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            System.out.println("Client B arriving...");
            boolean result = barrier.await("B");
            System.out.println("Client B result: " + result);
        });

        Thread client3 = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            System.out.println("Client C arriving...");
            boolean result = barrier.await("C");
            System.out.println("Client C result: " + result);
        });

        client1.start();
        client2.start();
        client3.start();

        try {
            client1.join();
            client2.join();
            client3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Barrier passed: " + barrier.isBarrierPassed());
        System.out.println("Waiting parties: " + barrier.getWaitingParties());

        // Test 2: Timeout functionality
        System.out.println("\n=== Timeout Test ===");
        DesignDistributedBarrier timeoutBarrier = new DesignDistributedBarrier(2);

        long startTime = System.currentTimeMillis();
        boolean timeoutResult = timeoutBarrier.awaitWithTimeout("TimeoutClient", 500);
        long endTime = System.currentTimeMillis();

        System.out.println("Timeout result: " + timeoutResult);
        System.out.println("Time elapsed: " + (endTime - startTime) + "ms");

        // Test 3: Reset functionality
        System.out.println("\n=== Reset Test ===");
        barrier.reset();
        System.out.println("After reset - Barrier passed: " + barrier.isBarrierPassed());
        System.out.println("After reset - Waiting parties: " + barrier.getWaitingParties());
    }
}
