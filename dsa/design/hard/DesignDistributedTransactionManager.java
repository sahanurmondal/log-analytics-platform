package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Variation: Design Distributed Transaction Manager
 *
 * Description:
 * Design a distributed transaction manager supporting begin, commit, and
 * rollback.
 *
 * Constraints:
 * - At most 10^5 operations.
 *
 * Follow-up:
 * - Can you optimize for atomicity and durability?
 * - Can you support nested transactions?
 * 
 * Time Complexity:
 * - begin: O(1)
 * - commit: O(n) where n is number of operations in transaction
 * - rollback: O(n) where n is number of operations in transaction
 * Space Complexity: O(m*n) where m is transactions, n is operations per
 * transaction
 * 
 * Company Tags: System Design, Database, Distributed Systems
 */
public class DesignDistributedTransactionManager {

    // Transaction states
    public enum TransactionState {
        ACTIVE,
        COMMITTED,
        ABORTED,
        PREPARING,
        PREPARED
    }

    // Operation types
    public enum OperationType {
        READ,
        WRITE,
        DELETE
    }

    // Transaction operation
    private static class TransactionOperation {
        String resourceId;
        OperationType type;
        Object oldValue;
        Object newValue;

        TransactionOperation(String resourceId, OperationType type, Object oldValue, Object newValue) {
            this.resourceId = resourceId;
            this.type = type;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }

    // Transaction information
    private static class Transaction {
        String transactionId;
        TransactionState state;
        List<TransactionOperation> operations;
        long startTime;
        long endTime;
        String parentTransactionId; // For nested transactions
        Set<String> childTransactions; // For nested transactions
        int isolationLevel; // 0=READ_UNCOMMITTED, 1=READ_COMMITTED, 2=REPEATABLE_READ, 3=SERIALIZABLE

        Transaction(String transactionId) {
            this.transactionId = transactionId;
            this.state = TransactionState.ACTIVE;
            this.operations = new ArrayList<>();
            this.startTime = System.currentTimeMillis();
            this.childTransactions = new HashSet<>();
            this.isolationLevel = 2; // Default: REPEATABLE_READ
        }

        Transaction(String transactionId, String parentId) {
            this(transactionId);
            this.parentTransactionId = parentId;
        }
    }

    // Resource manager
    private static class ResourceManager {
        Object currentValue;
        final ReentrantReadWriteLock lock;

        ResourceManager(String resourceId) {
            this.lock = new ReentrantReadWriteLock();
        }
    }

    private final Map<String, Transaction> activeTransactions;
    private final Map<String, Transaction> completedTransactions;
    private final Map<String, ResourceManager> resources;
    private final ExecutorService executorService;
    private final ScheduledExecutorService timeoutService;
    private final ReentrantReadWriteLock globalLock;
    private final long transactionTimeout;
    private final boolean supportNestedTransactions;

    public DesignDistributedTransactionManager() {
        this(true, 30000); // 30 second timeout by default
    }

    public DesignDistributedTransactionManager(boolean supportNested, long timeoutMs) {
        this.activeTransactions = new ConcurrentHashMap<>();
        this.completedTransactions = new ConcurrentHashMap<>();
        this.resources = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
        this.timeoutService = Executors.newScheduledThreadPool(2);
        this.globalLock = new ReentrantReadWriteLock();
        this.transactionTimeout = timeoutMs;
        this.supportNestedTransactions = supportNested;

        startTimeoutMonitor();
    }

    public void begin(String transactionId) {
        begin(transactionId, null);
    }

    public void begin(String transactionId, String parentTransactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }

        globalLock.writeLock().lock();
        try {
            if (activeTransactions.containsKey(transactionId)) {
                throw new IllegalStateException("Transaction " + transactionId + " already exists");
            }

            // Check parent transaction if specified
            if (parentTransactionId != null) {
                if (!supportNestedTransactions) {
                    throw new UnsupportedOperationException("Nested transactions not supported");
                }

                Transaction parent = activeTransactions.get(parentTransactionId);
                if (parent == null || parent.state != TransactionState.ACTIVE) {
                    throw new IllegalStateException("Parent transaction " + parentTransactionId + " not active");
                }

                parent.childTransactions.add(transactionId);
            }

            Transaction transaction = parentTransactionId != null ? new Transaction(transactionId, parentTransactionId)
                    : new Transaction(transactionId);

            activeTransactions.put(transactionId, transaction);

            // Schedule timeout
            scheduleTransactionTimeout(transactionId);

            System.out.println("Started transaction: " + transactionId +
                    (parentTransactionId != null ? " (parent: " + parentTransactionId + ")" : ""));
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    public boolean commit(String transactionId) {
        if (transactionId == null) {
            return false;
        }

        globalLock.writeLock().lock();
        try {
            Transaction transaction = activeTransactions.get(transactionId);
            if (transaction == null) {
                return false; // Transaction doesn't exist
            }

            if (transaction.state != TransactionState.ACTIVE) {
                return false; // Transaction not in active state
            }

            // Two-phase commit for distributed transactions
            if (!preparePhase(transaction)) {
                rollback(transactionId);
                return false;
            }

            if (!commitPhase(transaction)) {
                rollback(transactionId);
                return false;
            }

            // Handle nested transactions - commit children first
            for (String childId : transaction.childTransactions) {
                if (!commit(childId)) {
                    rollback(transactionId);
                    return false;
                }
            }

            transaction.state = TransactionState.COMMITTED;
            transaction.endTime = System.currentTimeMillis();

            // Move to completed transactions
            activeTransactions.remove(transactionId);
            completedTransactions.put(transactionId, transaction);

            System.out.println("Committed transaction: " + transactionId +
                    " (" + transaction.operations.size() + " operations)");
            return true;

        } finally {
            globalLock.writeLock().unlock();
        }
    }

    public void rollback(String transactionId) {
        if (transactionId == null) {
            return;
        }

        globalLock.writeLock().lock();
        try {
            Transaction transaction = activeTransactions.get(transactionId);
            if (transaction == null) {
                return; // Transaction doesn't exist
            }

            // Rollback child transactions first
            for (String childId : transaction.childTransactions) {
                rollback(childId);
            }

            // Rollback all operations in reverse order
            for (int i = transaction.operations.size() - 1; i >= 0; i--) {
                TransactionOperation op = transaction.operations.get(i);
                rollbackOperation(op);
            }

            transaction.state = TransactionState.ABORTED;
            transaction.endTime = System.currentTimeMillis();

            // Move to completed transactions
            activeTransactions.remove(transactionId);
            completedTransactions.put(transactionId, transaction);

            System.out.println("Rolled back transaction: " + transactionId +
                    " (" + transaction.operations.size() + " operations)");
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    // Additional methods for transaction operations

    public boolean read(String transactionId, String resourceId) {
        return performOperation(transactionId, resourceId, OperationType.READ, null, null);
    }

    public boolean write(String transactionId, String resourceId, Object value) {
        Object oldValue = getCurrentValue(resourceId);
        return performOperation(transactionId, resourceId, OperationType.WRITE, oldValue, value);
    }

    public boolean delete(String transactionId, String resourceId) {
        Object oldValue = getCurrentValue(resourceId);
        return performOperation(transactionId, resourceId, OperationType.DELETE, oldValue, null);
    }

    public TransactionState getTransactionState(String transactionId) {
        Transaction transaction = activeTransactions.get(transactionId);
        if (transaction != null) {
            return transaction.state;
        }

        transaction = completedTransactions.get(transactionId);
        if (transaction != null) {
            return transaction.state;
        }

        return null; // Transaction not found
    }

    public List<String> getActiveTransactions() {
        globalLock.readLock().lock();
        try {
            return new ArrayList<>(activeTransactions.keySet());
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public Map<String, Object> getTransactionStats(String transactionId) {
        Transaction transaction = activeTransactions.get(transactionId);
        if (transaction == null) {
            transaction = completedTransactions.get(transactionId);
        }

        if (transaction == null) {
            return null;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("transactionId", transaction.transactionId);
        stats.put("state", transaction.state.toString());
        stats.put("startTime", transaction.startTime);
        stats.put("endTime", transaction.endTime);
        stats.put("duration", transaction.endTime > 0 ? transaction.endTime - transaction.startTime
                : System.currentTimeMillis() - transaction.startTime);
        stats.put("operationCount", transaction.operations.size());
        stats.put("parentTransaction", transaction.parentTransactionId);
        stats.put("childTransactions", new ArrayList<>(transaction.childTransactions));
        stats.put("isolationLevel", transaction.isolationLevel);

        return stats;
    }

    // Private helper methods

    private boolean performOperation(String transactionId, String resourceId,
            OperationType type, Object oldValue, Object newValue) {
        globalLock.readLock().lock();
        try {
            Transaction transaction = activeTransactions.get(transactionId);
            if (transaction == null || transaction.state != TransactionState.ACTIVE) {
                return false;
            }

            // Ensure resource exists
            resources.computeIfAbsent(resourceId, ResourceManager::new);

            // Add operation to transaction log
            TransactionOperation operation = new TransactionOperation(resourceId, type, oldValue, newValue);
            transaction.operations.add(operation);

            return true;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    private Object getCurrentValue(String resourceId) {
        ResourceManager resource = resources.get(resourceId);
        return resource != null ? resource.currentValue : null;
    }

    private boolean preparePhase(Transaction transaction) {
        transaction.state = TransactionState.PREPARING;

        // Validate all operations can be committed
        for (TransactionOperation op : transaction.operations) {
            ResourceManager resource = resources.get(op.resourceId);
            if (resource != null) {
                // Try to acquire locks
                if (!resource.lock.writeLock().tryLock()) {
                    return false; // Cannot acquire lock
                }
                resource.lock.writeLock().unlock();
            }
        }

        transaction.state = TransactionState.PREPARED;
        return true;
    }

    private boolean commitPhase(Transaction transaction) {
        // Apply all operations
        for (TransactionOperation op : transaction.operations) {
            if (!applyOperation(op)) {
                return false;
            }
        }

        return true;
    }

    private boolean applyOperation(TransactionOperation op) {
        ResourceManager resource = resources.computeIfAbsent(op.resourceId, ResourceManager::new);

        resource.lock.writeLock().lock();
        try {
            switch (op.type) {
                case WRITE:
                    resource.currentValue = op.newValue;
                    break;
                case DELETE:
                    resource.currentValue = null;
                    break;
                case READ:
                    // No state change for reads
                    break;
            }
            return true;
        } finally {
            resource.lock.writeLock().unlock();
        }
    }

    private void rollbackOperation(TransactionOperation op) {
        ResourceManager resource = resources.get(op.resourceId);
        if (resource == null) {
            return;
        }

        resource.lock.writeLock().lock();
        try {
            switch (op.type) {
                case WRITE:
                case DELETE:
                    resource.currentValue = op.oldValue;
                    break;
                case READ:
                    // No rollback needed for reads
                    break;
            }
        } finally {
            resource.lock.writeLock().unlock();
        }
    }

    private void scheduleTransactionTimeout(String transactionId) {
        timeoutService.schedule(() -> {
            Transaction transaction = activeTransactions.get(transactionId);
            if (transaction != null && transaction.state == TransactionState.ACTIVE) {
                System.out.println("Transaction " + transactionId + " timed out, rolling back");
                rollback(transactionId);
            }
        }, transactionTimeout, TimeUnit.MILLISECONDS);
    }

    private void startTimeoutMonitor() {
        timeoutService.scheduleWithFixedDelay(() -> {
            long currentTime = System.currentTimeMillis();
            List<String> timedOutTransactions = new ArrayList<>();

            for (Transaction transaction : activeTransactions.values()) {
                if (currentTime - transaction.startTime > transactionTimeout) {
                    timedOutTransactions.add(transaction.transactionId);
                }
            }

            for (String transactionId : timedOutTransactions) {
                System.out.println("Auto-rolling back timed out transaction: " + transactionId);
                rollback(transactionId);
            }
        }, transactionTimeout / 2, transactionTimeout / 2, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        // Rollback all active transactions
        List<String> activeIds = new ArrayList<>(activeTransactions.keySet());
        for (String transactionId : activeIds) {
            rollback(transactionId);
        }

        timeoutService.shutdown();
        executorService.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Distributed Transaction Manager Tests ===");

        DesignDistributedTransactionManager tm = new DesignDistributedTransactionManager();

        System.out.println("\n--- Basic Transaction Test ---");
        tm.begin("tx1");
        tm.write("tx1", "resource1", "value1");
        tm.write("tx1", "resource2", "value2");
        System.out.println("Commit tx1: " + tm.commit("tx1")); // true

        System.out.println("\n--- Rollback Test ---");
        tm.begin("tx2");
        tm.write("tx2", "resource3", "value3");
        tm.rollback("tx2");

        System.out.println("\n--- Edge Cases Test ---");
        // Commit non-existent transaction
        System.out.println("Commit non-existent: " + tm.commit("tx3")); // false

        // Rollback non-existent transaction
        tm.rollback("tx4"); // Should not throw

        // Double commit
        tm.begin("tx5");
        tm.commit("tx5");
        System.out.println("Double commit: " + tm.commit("tx5")); // false

        System.out.println("\n--- Nested Transaction Test ---");
        tm.begin("parent");
        tm.write("parent", "shared", "parent_value");

        tm.begin("child1", "parent");
        tm.write("child1", "child1_resource", "child1_value");
        tm.commit("child1");

        tm.begin("child2", "parent");
        tm.write("child2", "child2_resource", "child2_value");
        tm.rollback("child2");

        tm.commit("parent");

        System.out.println("\n--- Transaction States ---");
        tm.begin("state_test");
        System.out.println("State after begin: " + tm.getTransactionState("state_test"));
        tm.write("state_test", "test_resource", "test_value");
        tm.commit("state_test");
        System.out.println("State after commit: " + tm.getTransactionState("state_test"));

        System.out.println("\n--- Active Transactions ---");
        tm.begin("active1");
        tm.begin("active2");
        tm.begin("active3");
        System.out.println("Active transactions: " + tm.getActiveTransactions());
        tm.commit("active1");
        tm.rollback("active2");
        System.out.println("Active after commit/rollback: " + tm.getActiveTransactions());
        tm.commit("active3");

        System.out.println("\n--- Transaction Statistics ---");
        tm.begin("stats_test");
        tm.write("stats_test", "stats_resource", "stats_value");
        Thread.sleep(100); // Small delay
        tm.commit("stats_test");

        Map<String, Object> stats = tm.getTransactionStats("stats_test");
        if (stats != null) {
            stats.forEach((key, value) -> System.out.println(key + ": " + value));
        }

        System.out.println("\n--- Concurrent Transaction Test ---");
        // Simulate concurrent transactions
        Thread t1 = new Thread(() -> {
            tm.begin("concurrent1");
            tm.write("concurrent1", "shared_resource", "thread1_value");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            tm.commit("concurrent1");
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            tm.begin("concurrent2");
            tm.write("concurrent2", "shared_resource", "thread2_value");
            tm.commit("concurrent2");
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("\n--- Timeout Test ---");
        DesignDistributedTransactionManager shortTimeoutTm = new DesignDistributedTransactionManager(true, 1000); // 1
                                                                                                                  // second
                                                                                                                  // timeout

        shortTimeoutTm.begin("timeout_test");
        shortTimeoutTm.write("timeout_test", "timeout_resource", "timeout_value");

        Thread.sleep(1500); // Wait longer than timeout

        System.out.println("Timeout test state: " +
                shortTimeoutTm.getTransactionState("timeout_test"));

        shortTimeoutTm.shutdown();
        tm.shutdown();

        System.out.println("\nTransaction Manager shutdown complete.");
    }
}
