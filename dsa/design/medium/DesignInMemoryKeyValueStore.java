package design.medium;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Variation: Design In-Memory Key-Value Store
 *
 * Description:
 * Design a simple in-memory key-value store supporting put, get, and delete
 * operations.
 *
 * Constraints:
 * - Keys and values are strings.
 * - At most 10^5 operations.
 *
 * Follow-up:
 * - Can you optimize for thread safety?
 * - Can you support transactions or versioning?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n) where n is number of key-value pairs
 * 
 * Company Tags: System Design, Database
 */
public class DesignInMemoryKeyValueStore {

    private final Map<String, String> store;
    private final Map<String, Long> timestamps; // For versioning support
    private final boolean threadSafe;
    private final ReentrantReadWriteLock lock;
    private final int maxSize;
    private long currentTimestamp;

    public DesignInMemoryKeyValueStore() {
        this(false, Integer.MAX_VALUE);
    }

    public DesignInMemoryKeyValueStore(boolean threadSafe) {
        this(threadSafe, Integer.MAX_VALUE);
    }

    public DesignInMemoryKeyValueStore(boolean threadSafe, int maxSize) {
        this.threadSafe = threadSafe;
        this.maxSize = maxSize;
        this.currentTimestamp = 0;

        if (threadSafe) {
            this.store = new ConcurrentHashMap<>();
            this.timestamps = new ConcurrentHashMap<>();
            this.lock = new ReentrantReadWriteLock();
        } else {
            this.store = new HashMap<>();
            this.timestamps = new HashMap<>();
            this.lock = null;
        }
    }

    public void put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if (threadSafe) {
            lock.writeLock().lock();
            try {
                putInternal(key, value);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            putInternal(key, value);
        }
    }

    private void putInternal(String key, String value) {
        // Check capacity before adding new key
        if (!store.containsKey(key) && store.size() >= maxSize) {
            throw new IllegalStateException("Store capacity exceeded");
        }

        store.put(key, value);
        timestamps.put(key, ++currentTimestamp);
    }

    public String get(String key) {
        if (key == null) {
            return null;
        }

        if (threadSafe) {
            lock.readLock().lock();
            try {
                return getInternal(key);
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return getInternal(key);
        }
    }

    private String getInternal(String key) {
        return store.get(key);
    }

    public void delete(String key) {
        if (key == null) {
            return;
        }

        if (threadSafe) {
            lock.writeLock().lock();
            try {
                deleteInternal(key);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            deleteInternal(key);
        }
    }

    private void deleteInternal(String key) {
        store.remove(key);
        timestamps.remove(key);
    }

    // Additional utility methods

    public boolean containsKey(String key) {
        if (key == null) {
            return false;
        }

        if (threadSafe) {
            lock.readLock().lock();
            try {
                return store.containsKey(key);
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return store.containsKey(key);
        }
    }

    public int size() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return store.size();
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return store.size();
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                clearInternal();
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            clearInternal();
        }
    }

    private void clearInternal() {
        store.clear();
        timestamps.clear();
        currentTimestamp = 0;
    }

    public Set<String> keySet() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return new HashSet<>(store.keySet());
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return new HashSet<>(store.keySet());
        }
    }

    public Collection<String> values() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return new ArrayList<>(store.values());
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return new ArrayList<>(store.values());
        }
    }

    // Batch operations
    public void putAll(Map<String, String> map) {
        if (map == null) {
            return;
        }

        if (threadSafe) {
            lock.writeLock().lock();
            try {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    putInternal(entry.getKey(), entry.getValue());
                }
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                putInternal(entry.getKey(), entry.getValue());
            }
        }
    }

    public Map<String, String> getAll(Set<String> keys) {
        if (keys == null) {
            return new HashMap<>();
        }

        Map<String, String> result = new HashMap<>();

        if (threadSafe) {
            lock.readLock().lock();
            try {
                for (String key : keys) {
                    String value = getInternal(key);
                    if (value != null) {
                        result.put(key, value);
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
        } else {
            for (String key : keys) {
                String value = getInternal(key);
                if (value != null) {
                    result.put(key, value);
                }
            }
        }

        return result;
    }

    // Versioning support
    public Long getTimestamp(String key) {
        if (key == null) {
            return null;
        }

        if (threadSafe) {
            lock.readLock().lock();
            try {
                return timestamps.get(key);
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return timestamps.get(key);
        }
    }

    // Statistics
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        if (threadSafe) {
            lock.readLock().lock();
            try {
                stats.put("size", store.size());
                stats.put("maxSize", maxSize);
                stats.put("currentTimestamp", currentTimestamp);
                stats.put("threadSafe", threadSafe);
            } finally {
                lock.readLock().unlock();
            }
        } else {
            stats.put("size", store.size());
            stats.put("maxSize", maxSize);
            stats.put("currentTimestamp", currentTimestamp);
            stats.put("threadSafe", threadSafe);
        }

        return stats;
    }

    @Override
    public String toString() {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return String.format("InMemoryKVStore[size=%d, maxSize=%d, threadSafe=%s]",
                        store.size(), maxSize, threadSafe);
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return String.format("InMemoryKVStore[size=%d, maxSize=%d, threadSafe=%s]",
                    store.size(), maxSize, threadSafe);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Basic In-Memory Key-Value Store Tests ===");

        DesignInMemoryKeyValueStore store = new DesignInMemoryKeyValueStore();
        System.out.println("Initial store: " + store);

        // Basic operations
        System.out.println("\n--- Basic Operations ---");
        store.put("a", "1");
        store.put("b", "2");
        System.out.println("After put(a,1), put(b,2): " + store);
        System.out.println("get(a): " + store.get("a")); // 1
        System.out.println("get(b): " + store.get("b")); // 2

        store.delete("a");
        System.out.println("After delete(a): " + store);
        System.out.println("get(a): " + store.get("a")); // null

        // Edge cases
        System.out.println("\n--- Edge Cases ---");
        // Get non-existent key
        System.out.println("get(nonexistent): " + store.get("c")); // null

        // Delete non-existent key
        store.delete("c");
        System.out.println("After delete(nonexistent): " + store);

        // Null key operations
        try {
            store.put(null, "value");
        } catch (IllegalArgumentException e) {
            System.out.println("Null key put caught: " + e.getMessage());
        }

        System.out.println("get(null): " + store.get(null)); // null
        store.delete(null); // should not throw

        System.out.println("\n--- Advanced Operations ---");

        // Batch operations
        Map<String, String> batch = new HashMap<>();
        batch.put("x", "10");
        batch.put("y", "20");
        batch.put("z", "30");
        store.putAll(batch);
        System.out.println("After putAll: " + store);

        Set<String> keysToGet = new HashSet<>(Arrays.asList("x", "y", "nonexistent"));
        Map<String, String> results = store.getAll(keysToGet);
        System.out.println("getAll(x,y,nonexistent): " + results);

        // Key operations
        System.out.println("keySet: " + store.keySet());
        System.out.println("values: " + store.values());
        System.out.println("containsKey(x): " + store.containsKey("x"));
        System.out.println("containsKey(deleted): " + store.containsKey("a"));

        // Versioning
        System.out.println("\n--- Versioning ---");
        System.out.println("Timestamp for x: " + store.getTimestamp("x"));
        System.out.println("Timestamp for y: " + store.getTimestamp("y"));

        store.put("x", "100"); // Update x
        System.out.println("After updating x, timestamp: " + store.getTimestamp("x"));

        // Statistics
        System.out.println("\n--- Statistics ---");
        Map<String, Object> stats = store.getStats();
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("\n=== Capacity Test ===");
        DesignInMemoryKeyValueStore limitedStore = new DesignInMemoryKeyValueStore(false, 3);
        limitedStore.put("1", "one");
        limitedStore.put("2", "two");
        limitedStore.put("3", "three");
        System.out.println("Limited store: " + limitedStore);

        try {
            limitedStore.put("4", "four"); // Should fail
        } catch (IllegalStateException e) {
            System.out.println("Capacity exceeded caught: " + e.getMessage());
        }

        // Update existing key should work
        limitedStore.put("1", "ONE");
        System.out.println("After update: " + limitedStore.get("1"));

        System.out.println("\n=== Thread-Safe Test ===");
        DesignInMemoryKeyValueStore threadSafeStore = new DesignInMemoryKeyValueStore(true);

        // Simulate concurrent access
        Thread writer1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                threadSafeStore.put("key" + i, "value" + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread writer2 = new Thread(() -> {
            for (int i = 50; i < 100; i++) {
                threadSafeStore.put("key" + i, "value" + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread reader = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Current size: " + threadSafeStore.size());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        writer1.start();
        writer2.start();
        reader.start();

        try {
            writer1.join();
            writer2.join();
            reader.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final thread-safe store size: " + threadSafeStore.size());

        System.out.println("\n=== Performance Test ===");
        DesignInMemoryKeyValueStore perfStore = new DesignInMemoryKeyValueStore();

        long startTime = System.currentTimeMillis();

        // Performance test: 10k operations
        for (int i = 0; i < 5000; i++) {
            perfStore.put("key" + i, "value" + i);
        }

        for (int i = 0; i < 5000; i++) {
            perfStore.get("key" + (i % 1000));
        }

        long endTime = System.currentTimeMillis();
        System.out.println("10k operations completed in " + (endTime - startTime) + "ms");
        System.out.println("Final store size: " + perfStore.size());

        // Cleanup
        store.clear();
        System.out.println("After clear: " + store);
    }
}
