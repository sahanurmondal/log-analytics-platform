package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Design Distributed Lock System
 *
 * Description: Design a distributed lock system that supports:
 * - Mutual exclusion across multiple nodes
 * - Lock acquisition with timeout
 * - Lock renewal and heartbeat
 * - Deadlock detection and prevention
 * 
 * Constraints:
 * - Support multiple distributed nodes
 * - Handle node failures gracefully
 * - Prevent split-brain scenarios
 *
 * Follow-up:
 * - How to handle network partitions?
 * - Byzantine fault tolerance?
 * 
 * Time Complexity: O(log n) for lock operations
 * Space Complexity: O(locks * nodes)
 * 
 * Company Tags: Redis, Zookeeper, Consul
 */
public class DesignDistributedLock {

    enum LockStatus {
        AVAILABLE, ACQUIRED, EXPIRED, RELEASED
    }

    class LockInfo {
        String lockId;
        String resourceId;
        String ownerId;
        long acquiredTime;
        long expirationTime;
        LockStatus status;
        int renewalCount;

        LockInfo(String lockId, String resourceId, String ownerId, long ttlMs) {
            this.lockId = lockId;
            this.resourceId = resourceId;
            this.ownerId = ownerId;
            this.acquiredTime = System.currentTimeMillis();
            this.expirationTime = acquiredTime + ttlMs;
            this.status = LockStatus.ACQUIRED;
            this.renewalCount = 0;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        void renew(long ttlMs) {
            expirationTime = System.currentTimeMillis() + ttlMs;
            renewalCount++;
        }
    }

    class DistributedNode {
        String nodeId;
        Map<String, LockInfo> locks;
        Set<String> waitingClients;
        ReentrantLock nodeLock;
        boolean isLeader;
        long lastHeartbeat;

        DistributedNode(String nodeId) {
            this.nodeId = nodeId;
            this.locks = new ConcurrentHashMap<>();
            this.waitingClients = ConcurrentHashMap.newKeySet();
            this.nodeLock = new ReentrantLock();
            this.isLeader = false;
            this.lastHeartbeat = System.currentTimeMillis();
        }

        boolean tryAcquireLock(String resourceId, String clientId, long ttlMs) {
            nodeLock.lock();
            try {
                // Clean up expired locks first
                cleanupExpiredLocks();

                // Check if resource is already locked
                LockInfo existingLock = findLockForResource(resourceId);
                if (existingLock != null && !existingLock.isExpired()) {
                    if (existingLock.ownerId.equals(clientId)) {
                        // Reentrant lock - renew
                        existingLock.renew(ttlMs);
                        return true;
                    }
                    // Resource is locked by another client
                    waitingClients.add(clientId);
                    return false;
                }

                // Acquire new lock
                String lockId = UUID.randomUUID().toString();
                LockInfo lockInfo = new LockInfo(lockId, resourceId, clientId, ttlMs);
                locks.put(lockId, lockInfo);
                waitingClients.remove(clientId);

                return true;
            } finally {
                nodeLock.unlock();
            }
        }

        boolean releaseLock(String resourceId, String clientId) {
            nodeLock.lock();
            try {
                LockInfo lockInfo = findLockForResource(resourceId);
                if (lockInfo != null && lockInfo.ownerId.equals(clientId)) {
                    lockInfo.status = LockStatus.RELEASED;
                    locks.remove(lockInfo.lockId);

                    // Notify waiting clients
                    notifyWaitingClients(resourceId);
                    return true;
                }
                return false;
            } finally {
                nodeLock.unlock();
            }
        }

        boolean renewLock(String resourceId, String clientId, long ttlMs) {
            nodeLock.lock();
            try {
                LockInfo lockInfo = findLockForResource(resourceId);
                if (lockInfo != null && lockInfo.ownerId.equals(clientId) && !lockInfo.isExpired()) {
                    lockInfo.renew(ttlMs);
                    return true;
                }
                return false;
            } finally {
                nodeLock.unlock();
            }
        }

        private LockInfo findLockForResource(String resourceId) {
            return locks.values().stream()
                    .filter(lock -> lock.resourceId.equals(resourceId) &&
                            lock.status == LockStatus.ACQUIRED && !lock.isExpired())
                    .findFirst()
                    .orElse(null);
        }

        private void cleanupExpiredLocks() {
            Iterator<Map.Entry<String, LockInfo>> iterator = locks.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, LockInfo> entry = iterator.next();
                LockInfo lock = entry.getValue();
                if (lock.isExpired()) {
                    lock.status = LockStatus.EXPIRED;
                    iterator.remove();
                    notifyWaitingClients(lock.resourceId);
                }
            }
        }

        private void notifyWaitingClients(String resourceId) {
            // In a real system, this would notify waiting clients that the resource is
            // available
            System.out.println("Resource " + resourceId + " is now available on node " + nodeId);
        }
    }

    private Map<String, DistributedNode> nodes;
    private String leaderNodeId;
    private final ReentrantLock leaderElectionLock;
    private ScheduledExecutorService scheduler;
    private final int quorumSize;

    public DesignDistributedLock(int nodeCount) {
        nodes = new ConcurrentHashMap<>();
        leaderElectionLock = new ReentrantLock();
        scheduler = Executors.newScheduledThreadPool(2);
        quorumSize = (nodeCount / 2) + 1;

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            String nodeId = "node-" + i;
            nodes.put(nodeId, new DistributedNode(nodeId));
        }

        // Elect initial leader
        electLeader();

        // Start background tasks
        startLockCleanup();
        startLeaderElection();
    }

    public boolean acquireLock(String resourceId, String clientId, long timeoutMs) {
        return acquireLock(resourceId, clientId, timeoutMs, 30000); // Default 30s TTL
    }

    public boolean acquireLock(String resourceId, String clientId, long timeoutMs, long ttlMs) {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            // Try to acquire lock on majority of nodes
            int successCount = 0;

            for (DistributedNode node : nodes.values()) {
                if (node.tryAcquireLock(resourceId, clientId, ttlMs)) {
                    successCount++;
                }
            }

            // Check if we have quorum
            if (successCount >= quorumSize) {
                return true;
            }

            // Wait before retry
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    public boolean releaseLock(String resourceId, String clientId) {
        int successCount = 0;

        for (DistributedNode node : nodes.values()) {
            if (node.releaseLock(resourceId, clientId)) {
                successCount++;
            }
        }

        return successCount >= quorumSize;
    }

    public boolean renewLock(String resourceId, String clientId, long ttlMs) {
        int successCount = 0;

        for (DistributedNode node : nodes.values()) {
            if (node.renewLock(resourceId, clientId, ttlMs)) {
                successCount++;
            }
        }

        return successCount >= quorumSize;
    }

    private void electLeader() {
        leaderElectionLock.lock();
        try {
            // Simple leader election - lowest nodeId
            String newLeader = nodes.keySet().stream()
                    .filter(nodeId -> nodes.get(nodeId).lastHeartbeat > System.currentTimeMillis() - 10000)
                    .min(String::compareTo)
                    .orElse(null);

            if (newLeader != null && !newLeader.equals(leaderNodeId)) {
                if (leaderNodeId != null) {
                    nodes.get(leaderNodeId).isLeader = false;
                }

                leaderNodeId = newLeader;
                nodes.get(leaderNodeId).isLeader = true;
                System.out.println("New leader elected: " + leaderNodeId);
            }
        } finally {
            leaderElectionLock.unlock();
        }
    }

    private void startLockCleanup() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (DistributedNode node : nodes.values()) {
                node.cleanupExpiredLocks();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void startLeaderElection() {
        scheduler.scheduleWithFixedDelay(() -> {
            // Update heartbeats
            for (DistributedNode node : nodes.values()) {
                node.lastHeartbeat = System.currentTimeMillis();
            }

            // Re-elect leader if needed
            electLeader();
        }, 5, 5, TimeUnit.SECONDS);
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalLocks = nodes.values().stream()
                .mapToInt(node -> node.locks.size())
                .sum();

        int totalWaiting = nodes.values().stream()
                .mapToInt(node -> node.waitingClients.size())
                .sum();

        stats.put("totalNodes", nodes.size());
        stats.put("leaderNode", leaderNodeId);
        stats.put("quorumSize", quorumSize);
        stats.put("totalActiveLocks", totalLocks);
        stats.put("totalWaitingClients", totalWaiting);

        return stats;
    }

    public List<Map<String, Object>> getNodeStats() {
        List<Map<String, Object>> nodeStats = new ArrayList<>();

        for (DistributedNode node : nodes.values()) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("nodeId", node.nodeId);
            stats.put("isLeader", node.isLeader);
            stats.put("activeLocks", node.locks.size());
            stats.put("waitingClients", node.waitingClients.size());
            stats.put("lastHeartbeat", node.lastHeartbeat);

            nodeStats.add(stats);
        }

        return nodeStats;
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedLock lockSystem = new DesignDistributedLock(5);

        System.out.println("System stats: " + lockSystem.getSystemStats());

        // Test lock acquisition
        String resource = "shared-resource";
        String client1 = "client-1";
        String client2 = "client-2";

        System.out.println("\nTesting lock acquisition:");

        // Client 1 acquires lock
        boolean acquired1 = lockSystem.acquireLock(resource, client1, 5000, 10000);
        System.out.println("Client 1 acquired lock: " + acquired1);

        // Client 2 tries to acquire same lock (should fail)
        boolean acquired2 = lockSystem.acquireLock(resource, client2, 1000, 10000);
        System.out.println("Client 2 acquired lock: " + acquired2);

        // Client 1 renews lock
        boolean renewed = lockSystem.renewLock(resource, client1, 10000);
        System.out.println("Client 1 renewed lock: " + renewed);

        // Client 1 releases lock
        boolean released = lockSystem.releaseLock(resource, client1);
        System.out.println("Client 1 released lock: " + released);

        // Client 2 tries again (should succeed)
        acquired2 = lockSystem.acquireLock(resource, client2, 5000, 10000);
        System.out.println("Client 2 acquired lock after release: " + acquired2);

        System.out.println("\nFinal system stats: " + lockSystem.getSystemStats());

        // Show node stats
        System.out.println("\nNode statistics:");
        for (Map<String, Object> nodeStats : lockSystem.getNodeStats()) {
            System.out.println(nodeStats);
        }

        lockSystem.shutdown();
    }
}
