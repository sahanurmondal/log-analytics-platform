package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Design Distributed Semaphore
 * 
 * Related LeetCode Problems:
 * - Similar to: Semaphore implementation, Resource allocation
 * - No direct LeetCode equivalent (System Design)
 * 
 * Company Tags: Google, Microsoft, Amazon, ZooKeeper, Consul
 * Difficulty: Hard
 * 
 * Description:
 * Design a distributed semaphore that supports:
 * 1. acquire(clientId) - Acquire a permit for client
 * 2. release(clientId) - Release a permit from client
 * 3. Support fairness and dynamic resizing
 * 
 * The system should handle:
 * - Multiple clients competing for limited resources
 * - Fair resource allocation (FIFO)
 * - Client failures and timeouts
 * - Dynamic permit count changes
 * 
 * Constraints:
 * - At most 10^5 operations
 * - Support multiple distributed clients
 * - Handle network partitions
 * 
 * Follow-ups:
 * 1. Fairness optimization (FIFO ordering)
 * 2. Dynamic resizing support
 * 3. Timeout-based permit expiration
 * 4. Hierarchical semaphores
 */
public class DesignDistributedSemaphore {
    private final AtomicInteger availablePermits;
    private final int maxPermits;
    private final Queue<String> waitingQueue;
    private final Map<String, PermitInfo> heldPermits;
    private final Map<String, Long> clientTimeouts;
    private final ScheduledExecutorService timeoutExecutor;
    private final Object lock = new Object();

    // Permit information
    private static class PermitInfo {
        String clientId;
        long acquiredTime;
        long timeoutMs;

        PermitInfo(String clientId, long timeoutMs) {
            this.clientId = clientId;
            this.acquiredTime = System.currentTimeMillis();
            this.timeoutMs = timeoutMs;
        }

        boolean isExpired() {
            return timeoutMs > 0 &&
                    (System.currentTimeMillis() - acquiredTime) > timeoutMs;
        }
    }

    /**
     * Constructor - Initialize distributed semaphore
     * Time: O(1), Space: O(1)
     */
    public DesignDistributedSemaphore(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Permits must be positive");
        }

        this.maxPermits = permits;
        this.availablePermits = new AtomicInteger(permits);
        this.waitingQueue = new LinkedList<>();
        this.heldPermits = new ConcurrentHashMap<>();
        this.clientTimeouts = new ConcurrentHashMap<>();
        this.timeoutExecutor = Executors.newScheduledThreadPool(2);

        // Start timeout cleanup task
        startTimeoutMonitor();
    }

    /**
     * Acquire permit for client
     * Time: O(1) average, Space: O(1)
     */
    public boolean acquire(String clientId) {
        return acquire(clientId, 0); // No timeout by default
    }

    public boolean acquire(String clientId, long timeoutMs) {
        if (clientId == null || clientId.isEmpty()) {
            return false;
        }

        // Check if client already has a permit
        if (heldPermits.containsKey(clientId)) {
            return false; // Client already has a permit
        }

        synchronized (lock) {
            if (availablePermits.get() > 0) {
                // Grant permit immediately
                availablePermits.decrementAndGet();
                PermitInfo permitInfo = new PermitInfo(clientId, timeoutMs);
                heldPermits.put(clientId, permitInfo);

                if (timeoutMs > 0) {
                    scheduleTimeout(clientId, timeoutMs);
                }

                return true;
            } else {
                // Add to waiting queue for fairness
                if (!waitingQueue.contains(clientId)) {
                    waitingQueue.offer(clientId);
                    if (timeoutMs > 0) {
                        clientTimeouts.put(clientId, System.currentTimeMillis() + timeoutMs);
                    }
                }
                return false;
            }
        }
    }

    /**
     * Release permit from client
     * Time: O(1) average, Space: O(1)
     */
    public boolean release(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return false;
        }

        synchronized (lock) {
            PermitInfo permitInfo = heldPermits.remove(clientId);
            if (permitInfo == null) {
                return false; // Client doesn't have a permit
            }

            availablePermits.incrementAndGet();

            // Try to grant permit to next waiting client (fairness)
            while (!waitingQueue.isEmpty()) {
                String nextClient = waitingQueue.poll();
                Long timeout = clientTimeouts.remove(nextClient);

                // Check if the waiting client has timed out
                if (timeout != null && System.currentTimeMillis() > timeout) {
                    continue; // Skip timed out client
                }

                // Grant permit to next client
                if (availablePermits.get() > 0) {
                    availablePermits.decrementAndGet();
                    PermitInfo nextPermitInfo = new PermitInfo(nextClient, 0);
                    heldPermits.put(nextClient, nextPermitInfo);
                    break;
                }
            }

            return true;
        }
    }

    /**
     * Start timeout monitoring for permits
     */
    private void startTimeoutMonitor() {
        timeoutExecutor.scheduleAtFixedRate(() -> {
            List<String> expiredClients = new ArrayList<>();

            // Find expired permits
            for (Map.Entry<String, PermitInfo> entry : heldPermits.entrySet()) {
                if (entry.getValue().isExpired()) {
                    expiredClients.add(entry.getKey());
                }
            }

            // Release expired permits
            for (String clientId : expiredClients) {
                System.out.println("Releasing expired permit for client: " + clientId);
                release(clientId);
            }

            // Clean up timed out waiting clients
            synchronized (lock) {
                Iterator<String> iterator = waitingQueue.iterator();
                while (iterator.hasNext()) {
                    String clientId = iterator.next();
                    Long timeout = clientTimeouts.get(clientId);
                    if (timeout != null && System.currentTimeMillis() > timeout) {
                        iterator.remove();
                        clientTimeouts.remove(clientId);
                    }
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Schedule timeout for a specific client
     */
    private void scheduleTimeout(String clientId, long timeoutMs) {
        timeoutExecutor.schedule(() -> {
            PermitInfo permitInfo = heldPermits.get(clientId);
            if (permitInfo != null && permitInfo.isExpired()) {
                release(clientId);
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);
    }

    // Follow-up 1: Try acquire with timeout (non-blocking with wait)
    public boolean tryAcquire(String clientId, long waitTimeMs) {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < waitTimeMs) {
            if (acquire(clientId)) {
                return true;
            }

            try {
                Thread.sleep(100); // Small delay before retry
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    // Follow-up 2: Dynamic resizing - increase permits
    public void increasePermits(int additionalPermits) {
        if (additionalPermits <= 0) {
            return;
        }

        synchronized (lock) {
            availablePermits.addAndGet(additionalPermits);

            // Grant permits to waiting clients
            int granted = 0;
            while (!waitingQueue.isEmpty() && granted < additionalPermits) {
                String nextClient = waitingQueue.poll();
                Long timeout = clientTimeouts.remove(nextClient);

                if (timeout != null && System.currentTimeMillis() > timeout) {
                    continue; // Skip timed out client
                }

                if (availablePermits.get() > 0) {
                    availablePermits.decrementAndGet();
                    PermitInfo permitInfo = new PermitInfo(nextClient, 0);
                    heldPermits.put(nextClient, permitInfo);
                    granted++;
                }
            }
        }
    }

    // Follow-up 3: Dynamic resizing - decrease permits
    public boolean decreasePermits(int permitReduction) {
        if (permitReduction <= 0) {
            return false;
        }

        synchronized (lock) {
            int currentAvailable = availablePermits.get();
            if (currentAvailable >= permitReduction) {
                availablePermits.addAndGet(-permitReduction);
                return true;
            } else {
                // Can't reduce if not enough available permits
                return false;
            }
        }
    }

    // Follow-up 4: Get semaphore status
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();

        synchronized (lock) {
            status.put("maxPermits", maxPermits);
            status.put("availablePermits", availablePermits.get());
            status.put("heldPermits", heldPermits.size());
            status.put("waitingClients", waitingQueue.size());
            status.put("heldBy", new ArrayList<>(heldPermits.keySet()));
            status.put("waitingQueue", new ArrayList<>(waitingQueue));
        }

        return status;
    }

    // Follow-up 5: Force release all permits (emergency)
    public void forceReleaseAll() {
        synchronized (lock) {
            heldPermits.clear();
            waitingQueue.clear();
            clientTimeouts.clear();
            availablePermits.set(maxPermits);
        }
    }

    // Follow-up 6: Get client permit info
    public String getClientPermitInfo(String clientId) {
        PermitInfo permitInfo = heldPermits.get(clientId);
        if (permitInfo == null) {
            return "Client does not hold a permit";
        }

        long heldTime = System.currentTimeMillis() - permitInfo.acquiredTime;
        String timeoutInfo = permitInfo.timeoutMs > 0 ? ", timeout in " + (permitInfo.timeoutMs - heldTime) + "ms"
                : ", no timeout";

        return "Permit held for " + heldTime + "ms" + timeoutInfo;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Design Distributed Semaphore Test ===");

        // Test Case 1: Basic acquire/release
        DesignDistributedSemaphore semaphore = new DesignDistributedSemaphore(2);

        System.out.println("Acquire A: " + semaphore.acquire("A")); // true
        System.out.println("Acquire B: " + semaphore.acquire("B")); // true
        System.out.println("Acquire C: " + semaphore.acquire("C")); // false (no permits)

        System.out.println("Status: " + semaphore.getStatus());

        // Test Case 2: Release and fairness
        System.out.println("Release A: " + semaphore.release("A")); // true
        Thread.sleep(100); // Allow time for fairness processing
        System.out.println("C should now have permit: " + semaphore.heldPermits.containsKey("C"));

        // Test Case 3: Invalid operations
        System.out.println("Release non-holder D: " + semaphore.release("D")); // false
        System.out.println("Acquire with null: " + semaphore.acquire(null)); // false
        System.out.println("Double acquire B: " + semaphore.acquire("B")); // false

        // Test Case 4: Timeout-based acquire (Follow-up)
        System.out.println("\n=== Timeout Tests ===");
        boolean acquired = semaphore.acquire("E", 2000); // 2 second timeout
        System.out.println("Acquire E with timeout: " + acquired);

        if (acquired) {
            System.out.println("E permit info: " + semaphore.getClientPermitInfo("E"));
            Thread.sleep(3000); // Wait for timeout
            System.out.println("After timeout, E has permit: " + semaphore.heldPermits.containsKey("E"));
        }

        // Test Case 5: Try acquire with wait (Follow-up)
        System.out.println("\n=== Try Acquire Tests ===");
        semaphore.forceReleaseAll(); // Reset
        semaphore.acquire("X");
        semaphore.acquire("Y");

        // This should wait and eventually succeed when someone releases
        CompletableFuture<Boolean> futureResult = CompletableFuture.supplyAsync(() -> {
            return semaphore.tryAcquire("Z", 3000);
        });

        Thread.sleep(1000);
        semaphore.release("X"); // Release after 1 second

        try {
            System.out.println("Try acquire Z result: " + futureResult.get());
        } catch (ExecutionException e) {
            System.out.println("Error during tryAcquire for Z: " + e.getMessage());
        }

        // Test Case 6: Dynamic resizing (Follow-up)
        System.out.println("\n=== Dynamic Resizing ===");
        System.out.println("Before increase: " + semaphore.getStatus());
        semaphore.increasePermits(2);
        System.out.println("After increase: " + semaphore.getStatus());

        boolean decreased = semaphore.decreasePermits(1);
        System.out.println("Decrease success: " + decreased);
        System.out.println("After decrease: " + semaphore.getStatus());

        // Test Case 7: Multiple clients simulation
        System.out.println("\n=== Multiple Clients Simulation ===");
        semaphore.forceReleaseAll();

        // Simulate multiple clients trying to acquire
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final String clientId = "Client" + i;
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                boolean result = semaphore.tryAcquire(clientId, 5000);
                if (result) {
                    try {
                        Thread.sleep(1000); // Hold permit for 1 second
                        semaphore.release(clientId);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                return result;
            });
            futures.add(future);
        }

        // Wait for all clients and count successes
        long successCount = futures.stream()
                .mapToLong(f -> {
                    try {
                        return f.get() ? 1 : 0;
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();

        System.out.println("Successful acquisitions: " + successCount + "/10");
        System.out.println("Final status: " + semaphore.getStatus());

        // Cleanup
        semaphore.timeoutExecutor.shutdown();
    }
}
