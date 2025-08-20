package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Variation: Design Distributed Lock Manager
 *
 * Description:
 * Design a distributed lock manager supporting acquire and release operations.
 *
 * Constraints:
 * - Lock IDs are strings.
 * - At most 10^5 operations.
 *
 * Follow-up:
 * - Can you optimize for deadlock detection?
 * - Can you support lock expiration?
 * 
 * Time Complexity: O(1) for acquire/release operations
 * Space Complexity: O(n) where n is number of locks
 * 
 * Company Tags: System Design, Distributed Systems, Concurrency
 */
public class DesignDistributedLockManager {

    private static class LockInfo {
        String clientId;
        long acquiredTime;
        long expirationTime; // 0 means no expiration
        Queue<LockRequest> waitingQueue;

        LockInfo(String lockId, String clientId, long ttlMs) {
            this.clientId = clientId;
            this.acquiredTime = System.currentTimeMillis();
            this.expirationTime = ttlMs > 0 ? this.acquiredTime + ttlMs : 0;
            this.waitingQueue = new LinkedList<>();
        }

        boolean isExpired() {
            return expirationTime > 0 && System.currentTimeMillis() > expirationTime;
        }

        boolean isOwnedBy(String clientId) {
            return this.clientId.equals(clientId);
        }
    }

    private static class LockRequest {
        String clientId;
        long requestTime;
        CompletableFuture<Boolean> future;
        long timeoutMs;

        LockRequest(String clientId, long timeoutMs) {
            this.clientId = clientId;
            this.requestTime = System.currentTimeMillis();
            this.future = new CompletableFuture<>();
            this.timeoutMs = timeoutMs;
        }

        boolean isTimedOut() {
            return timeoutMs > 0 && System.currentTimeMillis() - requestTime > timeoutMs;
        }
    }

    private final Map<String, LockInfo> locks; // lockId -> LockInfo
    private final Map<String, Set<String>> clientLocks; // clientId -> set of lockIds
    private final ReentrantReadWriteLock globalLock;
    private final ScheduledExecutorService maintenanceService;
    private final ExecutorService lockProcessingService;
    private volatile boolean enableDeadlockDetection;
    private volatile long defaultLockTTL; // Default TTL in milliseconds

    public DesignDistributedLockManager() {
        this(true, 30000); // Enable deadlock detection, 30 second default TTL
    }

    public DesignDistributedLockManager(boolean enableDeadlockDetection, long defaultTTLMs) {
        this.locks = new ConcurrentHashMap<>();
        this.clientLocks = new ConcurrentHashMap<>();
        this.globalLock = new ReentrantReadWriteLock();
        this.maintenanceService = Executors.newScheduledThreadPool(2);
        this.lockProcessingService = Executors.newCachedThreadPool();
        this.enableDeadlockDetection = enableDeadlockDetection;
        this.defaultLockTTL = defaultTTLMs;

        startMaintenance();

        System.out.println("Initialized Distributed Lock Manager with deadlock detection: " +
                enableDeadlockDetection + ", default TTL: " + defaultTTLMs + "ms");
    }

    public boolean acquire(String lockId, String clientId) {
        return acquire(lockId, clientId, defaultLockTTL, 0);
    }

    public boolean acquire(String lockId, String clientId, long ttlMs) {
        return acquire(lockId, clientId, ttlMs, 0);
    }

    public boolean acquire(String lockId, String clientId, long ttlMs, long timeoutMs) {
        if (lockId == null || clientId == null) {
            throw new IllegalArgumentException("Lock ID and Client ID cannot be null");
        }

        globalLock.writeLock().lock();
        try {
            // Check for deadlock before acquiring
            if (enableDeadlockDetection && wouldCauseDeadlock(lockId, clientId)) {
                System.out.println("Deadlock detected! Cannot acquire lock " + lockId +
                        " for client " + clientId);
                return false;
            }

            LockInfo existingLock = locks.get(lockId);

            // If lock doesn't exist or is expired, acquire it
            if (existingLock == null || existingLock.isExpired()) {
                if (existingLock != null && existingLock.isExpired()) {
                    cleanupExpiredLock(lockId);
                }

                LockInfo newLock = new LockInfo(lockId, clientId, ttlMs);
                locks.put(lockId, newLock);

                @SuppressWarnings("unused")
                Set<String> clientLockSet = clientLocks.computeIfAbsent(clientId, k -> ConcurrentHashMap.newKeySet());
                clientLockSet.add(lockId);

                System.out.println("Lock " + lockId + " acquired by " + clientId);
                return true;
            }

            // If already owned by the same client, extend TTL (reentrant lock)
            if (existingLock.isOwnedBy(clientId)) {
                if (ttlMs > 0) {
                    existingLock.expirationTime = System.currentTimeMillis() + ttlMs;
                }
                System.out.println("Lock " + lockId + " re-acquired by " + clientId);
                return true;
            }

            // Lock is held by another client
            if (timeoutMs <= 0) {
                return false; // No waiting
            }

            // Add to waiting queue with timeout
            LockRequest request = new LockRequest(clientId, timeoutMs);
            existingLock.waitingQueue.offer(request);

            globalLock.writeLock().unlock();

            try {
                // Wait for lock with timeout
                return request.future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                // Remove from waiting queue if timed out
                globalLock.writeLock().lock();
                try {
                    LockInfo lockInfo = locks.get(lockId);
                    if (lockInfo != null) {
                        lockInfo.waitingQueue.remove(request);
                    }
                } finally {
                    globalLock.writeLock().unlock();
                }
                return false;
            }

        } finally {
            if (globalLock.writeLock().isHeldByCurrentThread()) {
                globalLock.writeLock().unlock();
            }
        }
    }

    public boolean release(String lockId, String clientId) {
        if (lockId == null || clientId == null) {
            return false;
        }

        globalLock.writeLock().lock();
        try {
            LockInfo lockInfo = locks.get(lockId);

            if (lockInfo == null) {
                return false; // Lock doesn't exist
            }

            if (!lockInfo.isOwnedBy(clientId)) {
                return false; // Not owned by this client
            }

            // Remove lock
            locks.remove(lockId);

            // Remove from client's lock set
            Set<String> clientLockSet = clientLocks.get(clientId);
            if (clientLockSet != null) {
                clientLockSet.remove(lockId);
                if (clientLockSet.isEmpty()) {
                    clientLocks.remove(clientId);
                }
            }

            // Process waiting queue
            processWaitingQueue(lockId, lockInfo);

            System.out.println("Lock " + lockId + " released by " + clientId);
            return true;

        } finally {
            globalLock.writeLock().unlock();
        }
    }

    public void releaseAllLocks(String clientId) {
        if (clientId == null) {
            return;
        }

        globalLock.writeLock().lock();
        try {
            Set<String> clientLockSet = clientLocks.get(clientId);
            if (clientLockSet != null) {
                // Create a copy to avoid concurrent modification
                Set<String> locksToRelease = new HashSet<>(clientLockSet);
                for (String lockId : locksToRelease) {
                    release(lockId, clientId);
                }
            }
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    public boolean isLocked(String lockId) {
        globalLock.readLock().lock();
        try {
            LockInfo lockInfo = locks.get(lockId);
            return lockInfo != null && !lockInfo.isExpired();
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public String getLockOwner(String lockId) {
        globalLock.readLock().lock();
        try {
            LockInfo lockInfo = locks.get(lockId);
            if (lockInfo != null && !lockInfo.isExpired()) {
                return lockInfo.clientId;
            }
            return null;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public Set<String> getClientLocks(String clientId) {
        globalLock.readLock().lock();
        try {
            Set<String> clientLockSet = clientLocks.get(clientId);
            return clientLockSet != null ? new HashSet<>(clientLockSet) : new HashSet<>();
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public int getTotalLocks() {
        return locks.size();
    }

    public Map<String, String> getAllLocks() {
        globalLock.readLock().lock();
        try {
            Map<String, String> result = new HashMap<>();
            for (Map.Entry<String, LockInfo> entry : locks.entrySet()) {
                LockInfo lockInfo = entry.getValue();
                if (!lockInfo.isExpired()) {
                    result.put(entry.getKey(), lockInfo.clientId);
                }
            }
            return result;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    // Private helper methods

    private void processWaitingQueue(String lockId, LockInfo releasedLock) {
        // Process the waiting queue for this lock
        LockRequest nextRequest = releasedLock.waitingQueue.poll();
        while (nextRequest != null) {
            if (nextRequest.isTimedOut()) {
                nextRequest.future.complete(false);
                nextRequest = releasedLock.waitingQueue.poll();
                continue;
            }

            // Grant lock to next waiting client
            LockInfo newLock = new LockInfo(lockId, nextRequest.clientId, defaultLockTTL);
            locks.put(lockId, newLock);

            @SuppressWarnings("unused")
            Set<String> nextClientLocks = clientLocks.computeIfAbsent(nextRequest.clientId,
                    k -> ConcurrentHashMap.newKeySet());
            nextClientLocks.add(lockId);

            nextRequest.future.complete(true);
            System.out.println("Lock " + lockId + " granted to waiting client " + nextRequest.clientId);
            break;
        }
    }

    private boolean wouldCauseDeadlock(String lockId, String clientId) {
        // Simple deadlock detection using wait-for graph
        Map<String, Set<String>> waitForGraph = buildWaitForGraph();

        // Add the potential edge
        @SuppressWarnings("unused")
        Set<String> currentlyWaiting = waitForGraph.computeIfAbsent(clientId, k -> new HashSet<>());
        LockInfo lockInfo = locks.get(lockId);
        if (lockInfo != null && !lockInfo.isExpired()) {
            currentlyWaiting.add(lockInfo.clientId);
        }

        // Detect cycle using DFS
        return hasCycle(waitForGraph, clientId, new HashSet<>(), new HashSet<>());
    }

    private Map<String, Set<String>> buildWaitForGraph() {
        Map<String, Set<String>> waitForGraph = new HashMap<>();

        for (LockInfo lockInfo : locks.values()) {
            if (!lockInfo.isExpired()) {
                for (LockRequest request : lockInfo.waitingQueue) {
                    if (!request.isTimedOut()) {
                        @SuppressWarnings("unused")
                        Set<String> waitingFor = waitForGraph.computeIfAbsent(request.clientId, k -> new HashSet<>());
                        waitingFor.add(lockInfo.clientId);
                    }
                }
            }
        }

        return waitForGraph;
    }

    private boolean hasCycle(Map<String, Set<String>> graph, String node,
            Set<String> visited, Set<String> recursionStack) {
        visited.add(node);
        recursionStack.add(node);

        Set<String> neighbors = graph.get(node);
        if (neighbors != null) {
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    if (hasCycle(graph, neighbor, visited, recursionStack)) {
                        return true;
                    }
                } else if (recursionStack.contains(neighbor)) {
                    return true; // Back edge found - cycle detected
                }
            }
        }

        recursionStack.remove(node);
        return false;
    }

    private void cleanupExpiredLock(String lockId) {
        LockInfo expiredLock = locks.remove(lockId);
        if (expiredLock != null) {
            Set<String> clientLockSet = clientLocks.get(expiredLock.clientId);
            if (clientLockSet != null) {
                clientLockSet.remove(lockId);
                if (clientLockSet.isEmpty()) {
                    clientLocks.remove(expiredLock.clientId);
                }
            }

            // Process waiting queue for expired lock
            processWaitingQueue(lockId, expiredLock);

            System.out.println("Expired lock " + lockId + " cleaned up");
        }
    }

    private void startMaintenance() {
        // Cleanup expired locks
        maintenanceService.scheduleWithFixedDelay(() -> {
            globalLock.writeLock().lock();
            try {
                List<String> expiredLocks = new ArrayList<>();

                for (Map.Entry<String, LockInfo> entry : locks.entrySet()) {
                    if (entry.getValue().isExpired()) {
                        expiredLocks.add(entry.getKey());
                    }
                }

                for (String lockId : expiredLocks) {
                    cleanupExpiredLock(lockId);
                }

                if (!expiredLocks.isEmpty()) {
                    System.out.println("Cleaned up " + expiredLocks.size() + " expired locks");
                }

            } finally {
                globalLock.writeLock().unlock();
            }
        }, 5, 5, TimeUnit.SECONDS);

        // Deadlock detection and resolution
        if (enableDeadlockDetection) {
            maintenanceService.scheduleWithFixedDelay(() -> {
                globalLock.readLock().lock();
                try {
                    Map<String, Set<String>> waitForGraph = buildWaitForGraph();

                    for (String clientId : waitForGraph.keySet()) {
                        if (hasCycle(waitForGraph, clientId, new HashSet<>(), new HashSet<>())) {
                            System.out.println("Deadlock detected involving client: " + clientId);
                            // In a real system, you might implement deadlock resolution strategies
                            // such as aborting the youngest transaction, etc.
                        }
                    }
                } finally {
                    globalLock.readLock().unlock();
                }
            }, 10, 10, TimeUnit.SECONDS);
        }
    }

    public void shutdown() {
        maintenanceService.shutdown();
        lockProcessingService.shutdown();
        try {
            if (!maintenanceService.awaitTermination(5, TimeUnit.SECONDS)) {
                maintenanceService.shutdownNow();
            }
            if (!lockProcessingService.awaitTermination(5, TimeUnit.SECONDS)) {
                lockProcessingService.shutdownNow();
            }
        } catch (InterruptedException e) {
            maintenanceService.shutdownNow();
            lockProcessingService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("=== Distributed Lock Manager Tests ===");

        DesignDistributedLockManager manager = new DesignDistributedLockManager();

        System.out.println("\n--- Basic Operations Test ---");
        System.out.println("Acquire lock1 by clientA: " + manager.acquire("lock1", "clientA")); // true
        System.out.println("Acquire lock1 by clientB: " + manager.acquire("lock1", "clientB")); // false
        System.out.println("Release lock1 by clientA: " + manager.release("lock1", "clientA")); // true
        System.out.println("Acquire lock1 by clientB: " + manager.acquire("lock1", "clientB")); // true

        // Edge Case: Release non-held lock
        System.out.println("Release lock2 by clientA: " + manager.release("lock2", "clientA")); // false

        // Edge Case: Acquire after release
        System.out.println("Acquire lock1 by clientA: " + manager.acquire("lock1", "clientA")); // false (held by
                                                                                                // clientB)

        manager.release("lock1", "clientB");

        System.out.println("\n--- Reentrant Lock Test ---");
        System.out.println("Acquire lock2 by clientA: " + manager.acquire("lock2", "clientA")); // true
        System.out.println("Re-acquire lock2 by clientA: " + manager.acquire("lock2", "clientA")); // true (reentrant)
        System.out.println("Release lock2 by clientA: " + manager.release("lock2", "clientA")); // true

        System.out.println("\n--- Lock Expiration Test ---");
        System.out.println("Acquire lock3 with 2 second TTL: " +
                manager.acquire("lock3", "clientA", 2000)); // true
        System.out.println("Lock3 is locked: " + manager.isLocked("lock3")); // true
        System.out.println("Lock3 owner: " + manager.getLockOwner("lock3")); // clientA

        Thread.sleep(2500); // Wait for expiration

        System.out.println("After expiration:");
        System.out.println("Lock3 is locked: " + manager.isLocked("lock3")); // false
        System.out.println("Acquire expired lock3 by clientB: " +
                manager.acquire("lock3", "clientB")); // true

        System.out.println("\n--- Multiple Locks Test ---");
        manager.acquire("lockA", "client1");
        manager.acquire("lockB", "client1");
        manager.acquire("lockC", "client2");

        System.out.println("Client1 locks: " + manager.getClientLocks("client1"));
        System.out.println("Client2 locks: " + manager.getClientLocks("client2"));
        System.out.println("All locks: " + manager.getAllLocks());

        System.out.println("\n--- Release All Locks Test ---");
        manager.releaseAllLocks("client1");
        System.out.println("After releasing all client1 locks:");
        System.out.println("Client1 locks: " + manager.getClientLocks("client1"));
        System.out.println("All locks: " + manager.getAllLocks());

        System.out.println("\n--- Concurrent Lock Operations Test ---");
        DesignDistributedLockManager concurrentManager = new DesignDistributedLockManager();

        Thread client1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                String lockId = "concurrent-lock-" + (i % 3);
                if (concurrentManager.acquire(lockId, "client1", 1000)) {
                    try {
                        Thread.sleep(50); // Simulate work
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    concurrentManager.release(lockId, "client1");
                }
            }
        });

        Thread client2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                String lockId = "concurrent-lock-" + (i % 3);
                if (concurrentManager.acquire(lockId, "client2", 1000)) {
                    try {
                        Thread.sleep(50); // Simulate work
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    concurrentManager.release(lockId, "client2");
                }
            }
        });

        client1.start();
        client2.start();

        client1.join();
        client2.join();

        System.out.println("Concurrent test completed. Final locks: " +
                concurrentManager.getAllLocks());

        System.out.println("\n--- Waiting Queue Test ---");
        DesignDistributedLockManager waitManager = new DesignDistributedLockManager();

        // Client1 acquires lock
        waitManager.acquire("wait-lock", "client1", 5000);

        // Client2 tries to acquire with timeout
        CompletableFuture<Boolean> client2Future = CompletableFuture
                .supplyAsync(() -> waitManager.acquire("wait-lock", "client2", 3000, 2000));

        // Wait a bit, then release lock
        Thread.sleep(1000);
        waitManager.release("wait-lock", "client1");

        System.out.println("Client2 got lock after waiting: " + client2Future.get());

        System.out.println("\n--- System Statistics ---");
        System.out.println("Total locks: " + manager.getTotalLocks());
        System.out.println("All active locks: " + manager.getAllLocks());

        manager.shutdown();
        concurrentManager.shutdown();
        waitManager.shutdown();

        System.out.println("Distributed Lock Manager tests completed.");
    }
}
