package company.ecommerce.ebay;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Time-Versioned In-Memory Database
 *
 * A complete database system supporting:
 * Level 1: Basic record operations with time-versioning
 * Level 2: Atomic operations (CAS - Compare And Set)
 * Level 3: Scanning and filtering capabilities
 * Level 4: Time-To-Live (TTL) with automatic expiration
 * Level 5: Look-back operations to historical timestamps
 *
 * All operations are thread-safe and maintain historical versions
 *
 * Time Complexity:
 * - set/get: O(log n) where n is number of versions
 * - compareAndSet: O(log n)
 * - scan: O(m) where m is number of fields
 * - TTL management: O(1) amortized
 *
 * Space Complexity: O(n*m*v) where n=keys, m=fields, v=versions
 */
public class TimeVersionedDatabase {

    /**
     * Represents a versioned value with timestamp and TTL
     */
    static class VersionedValue {
        String value;
        int timestamp;
        int ttl; // Time-to-live in seconds (0 = no expiration)

        public VersionedValue(String value, int timestamp, int ttl) {
            this.value = value;
            this.timestamp = timestamp;
            this.ttl = ttl;
        }

        /**
         * Check if this value is expired at given timestamp
         */
        public boolean isExpired(int currentTimestamp) {
            if (ttl <= 0) {
                return false; // No TTL, never expires
            }
            return currentTimestamp >= (timestamp + ttl);
        }

        @Override
        public String toString() {
            return String.format("{value: %s, ts: %d, ttl: %d}", value, timestamp, ttl);
        }
    }

    /**
     * Represents a field's version history (navigable sorted map)
     */
    static class FieldVersionHistory {
        TreeMap<Integer, VersionedValue> versions; // timestamp -> value

        public FieldVersionHistory() {
            this.versions = new TreeMap<>();
        }

        /**
         * Add a new version
         */
        public void addVersion(int timestamp, String value, int ttl) {
            versions.put(timestamp, new VersionedValue(value, timestamp, ttl));
        }

        /**
         * Get value at specific timestamp (or latest before it)
         */
        public String getAtTimestamp(int timestamp) {
            // Find the version that was active at this timestamp
            Map.Entry<Integer, VersionedValue> entry = versions.floorEntry(timestamp);

            if (entry == null) {
                return null;
            }

            VersionedValue versionedValue = entry.getValue();

            // Check if this value is expired
            if (versionedValue.isExpired(timestamp)) {
                return null;
            }

            return versionedValue.value;
        }

        /**
         * Get all versions that were active at timestamp
         */
        public List<Map.Entry<Integer, VersionedValue>> getVersionsUpto(int timestamp) {
            List<Map.Entry<Integer, VersionedValue>> result = new ArrayList<>();

            for (Map.Entry<Integer, VersionedValue> entry : versions.entrySet()) {
                if (entry.getKey() <= timestamp && !entry.getValue().isExpired(timestamp)) {
                    result.add(entry);
                }
            }

            return result;
        }

        /**
         * Get latest version
         */
        public Map.Entry<Integer, VersionedValue> getLatestVersion() {
            return versions.lastEntry();
        }
    }

    // Main storage: key -> (field -> version history)
    private ConcurrentHashMap<String, ConcurrentHashMap<String, FieldVersionHistory>> database;

    // Read-write lock for atomic operations
    private ReentrantReadWriteLock lock;

    public TimeVersionedDatabase() {
        this.database = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    // ==================== LEVEL 1: Basic Record Operations ====================

    /**
     * Set a value for key-field pair at given timestamp
     * Time: O(log n) where n is number of versions
     */
    public void set(int timestamp, String key, String field, String value) {
        lock.writeLock().lock();
        try {
            ConcurrentHashMap<String, FieldVersionHistory> fields =
                database.computeIfAbsent(key, k -> new ConcurrentHashMap<>());

            FieldVersionHistory history =
                fields.computeIfAbsent(field, f -> new FieldVersionHistory());

            history.addVersion(timestamp, value, 0); // ttl = 0 (no expiration)
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get value for key-field pair at given timestamp
     * Time: O(log n) where n is number of versions
     */
    public String get(int timestamp, String key, String field) {
        lock.readLock().lock();
        try {
            if (!database.containsKey(key)) {
                return null;
            }

            ConcurrentHashMap<String, FieldVersionHistory> fields = database.get(key);

            if (!fields.containsKey(field)) {
                return null;
            }

            FieldVersionHistory history = fields.get(field);
            return history.getAtTimestamp(timestamp);
        } finally {
            lock.readLock().unlock();
        }
    }

    // ==================== LEVEL 2: Atomic Operations ====================

    /**
     * Compare-And-Set: Atomically sets newValue only if current value matches expectedValue
     * Time: O(log n)
     */
    public boolean compareAndSet(int timestamp, String key, String field,
                                 String expectedValue, String newValue) {
        lock.writeLock().lock();
        try {
            // Get current value
            String currentValue = get(timestamp, key, field);

            // Check if it matches expected
            if (!Objects.equals(currentValue, expectedValue)) {
                return false;
            }

            // Set new value
            set(timestamp, key, field, newValue);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Compare-And-Delete: Atomically deletes field only if current value matches expectedValue
     * Time: O(log n)
     */
    public boolean compareAndDelete(int timestamp, String key, String field,
                                    String expectedValue) {
        lock.writeLock().lock();
        try {
            // Get current value
            String currentValue = get(timestamp, key, field);

            // Check if it matches expected
            if (!Objects.equals(currentValue, expectedValue)) {
                return false;
            }

            // Delete by setting a marker (special case for deletion)
            if (database.containsKey(key) && database.get(key).containsKey(field)) {
                // Add a null entry to mark deletion
                FieldVersionHistory history = database.get(key).get(field);
                history.addVersion(timestamp, null, 0);
            }

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==================== LEVEL 3: Scanning and Filtering ====================

    /**
     * Get all field-value pairs for a key at given timestamp
     * Time: O(m) where m is number of fields
     */
    public List<String> scan(int timestamp, String key) {
        lock.readLock().lock();
        try {
            List<String> result = new ArrayList<>();

            if (!database.containsKey(key)) {
                return result;
            }

            ConcurrentHashMap<String, FieldVersionHistory> fields = database.get(key);

            for (Map.Entry<String, FieldVersionHistory> entry : fields.entrySet()) {
                String field = entry.getKey();
                String value = entry.getValue().getAtTimestamp(timestamp);

                if (value != null) {
                    result.add(field + "=" + value);
                }
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get field-value pairs where field starts with prefix
     * Time: O(m) where m is number of fields
     */
    public List<String> scanByPrefix(int timestamp, String key, String prefix) {
        lock.readLock().lock();
        try {
            List<String> result = new ArrayList<>();

            if (!database.containsKey(key)) {
                return result;
            }

            ConcurrentHashMap<String, FieldVersionHistory> fields = database.get(key);

            for (Map.Entry<String, FieldVersionHistory> entry : fields.entrySet()) {
                String field = entry.getKey();

                if (field.startsWith(prefix)) {
                    String value = entry.getValue().getAtTimestamp(timestamp);

                    if (value != null) {
                        result.add(field + "=" + value);
                    }
                }
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    // ==================== LEVEL 4: Time-To-Live (TTL) ====================

    /**
     * Set value with TTL expiration
     * Time: O(log n)
     */
    public void setWithTTL(int timestamp, String key, String field, String value, int ttl) {
        lock.writeLock().lock();
        try {
            ConcurrentHashMap<String, FieldVersionHistory> fields =
                database.computeIfAbsent(key, k -> new ConcurrentHashMap<>());

            FieldVersionHistory history =
                fields.computeIfAbsent(field, f -> new FieldVersionHistory());

            history.addVersion(timestamp, value, ttl);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Compare-And-Set with TTL
     * Time: O(log n)
     */
    public boolean compareAndSetWithTTL(int timestamp, String key, String field,
                                        String expectedValue, String newValue, int ttl) {
        lock.writeLock().lock();
        try {
            // Get current value
            String currentValue = get(timestamp, key, field);

            // Check if it matches expected
            if (!Objects.equals(currentValue, expectedValue)) {
                return false;
            }

            // Set new value with TTL
            setWithTTL(timestamp, key, field, newValue, ttl);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==================== LEVEL 5: Look-Back Operations ====================

    /**
     * Get historical value at specific past timestamp
     * Time: O(log n)
     */
    public String getHistorical(int timestamp, String key, String field) {
        // This is essentially the same as get() which already supports look-back
        return get(timestamp, key, field);
    }

    /**
     * Get all versions of a field up to a specific timestamp
     * Time: O(v) where v is number of versions
     */
    public List<String> getVersionHistory(int timestamp, String key, String field) {
        lock.readLock().lock();
        try {
            List<String> result = new ArrayList<>();

            if (!database.containsKey(key) || !database.get(key).containsKey(field)) {
                return result;
            }

            FieldVersionHistory history = database.get(key).get(field);
            List<Map.Entry<Integer, VersionedValue>> versions = history.getVersionsUpto(timestamp);

            for (Map.Entry<Integer, VersionedValue> entry : versions) {
                int ts = entry.getKey();
                String value = entry.getValue().value;
                result.add(String.format("@%d: %s", ts, value));
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Scan with look-back at specific timestamp
     * Time: O(m) where m is number of fields
     */
    public List<String> scanHistorical(int timestamp, String key) {
        // This is essentially the same as scan() which already supports look-back
        return scan(timestamp, key);
    }

    /**
     * Get snapshot of entire database at specific timestamp
     * Time: O(n*m) where n is keys, m is average fields per key
     */
    public Map<String, List<String>> getSnapshot(int timestamp) {
        lock.readLock().lock();
        try {
            Map<String, List<String>> snapshot = new LinkedHashMap<>();

            for (Map.Entry<String, ConcurrentHashMap<String, FieldVersionHistory>> keyEntry : database.entrySet()) {
                String key = keyEntry.getKey();
                List<String> fields = scan(timestamp, key);

                if (!fields.isEmpty()) {
                    snapshot.put(key, fields);
                }
            }

            return snapshot;
        } finally {
            lock.readLock().unlock();
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Clear database
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            database.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get number of keys
     */
    public int keyCount() {
        return database.size();
    }

    /**
     * Delete entire key and all its fields
     */
    public void deleteKey(int timestamp, String key) {
        lock.writeLock().lock();
        try {
            if (database.containsKey(key)) {
                ConcurrentHashMap<String, FieldVersionHistory> fields = database.get(key);

                // Mark all fields as deleted at this timestamp
                for (String field : fields.keySet()) {
                    FieldVersionHistory history = fields.get(field);
                    history.addVersion(timestamp, null, 0);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get all keys in database at specific timestamp
     */
    public List<String> getAllKeys(int timestamp) {
        lock.readLock().lock();
        try {
            List<String> result = new ArrayList<>();

            for (String key : database.keySet()) {
                // Include key if it has any non-null fields at this timestamp
                List<String> fields = scan(timestamp, key);
                if (!fields.isEmpty()) {
                    result.add(key);
                }
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        TimeVersionedDatabase db = new TimeVersionedDatabase();

        System.out.println("=== LEVEL 1: Basic Record Operations ===\n");

        // Set values at different timestamps
        db.set(1, "user:1", "name", "Alice");
        db.set(1, "user:1", "email", "alice@example.com");
        db.set(2, "user:1", "age", "25");
        db.set(3, "user:1", "name", "Alice Smith"); // Update name

        System.out.println("Get at timestamp 1:");
        System.out.println("  user:1 name: " + db.get(1, "user:1", "name"));
        System.out.println("  user:1 age: " + db.get(1, "user:1", "age"));

        System.out.println("\nGet at timestamp 3:");
        System.out.println("  user:1 name: " + db.get(3, "user:1", "name"));
        System.out.println("  user:1 age: " + db.get(3, "user:1", "age"));

        System.out.println("\n=== LEVEL 2: Atomic Operations ===\n");

        boolean cas1 = db.compareAndSet(4, "user:1", "age", "25", "26");
        System.out.println("CAS: age 25->26 (expected match): " + cas1);
        System.out.println("  New age: " + db.get(4, "user:1", "age"));

        boolean cas2 = db.compareAndSet(5, "user:1", "age", "25", "27");
        System.out.println("\nCAS: age 25->27 (no match, age is 26): " + cas2);
        System.out.println("  Age remains: " + db.get(5, "user:1", "age"));

        boolean cad1 = db.compareAndDelete(6, "user:1", "email", "alice@example.com");
        System.out.println("\nCAD: delete email (matches): " + cad1);
        System.out.println("  Email after delete: " + db.get(6, "user:1", "email"));

        System.out.println("\n=== LEVEL 3: Scanning and Filtering ===\n");

        // Add more fields for scanning
        db.set(7, "user:1", "phone", "555-1234");
        db.set(7, "user:1", "phone_backup", "555-5678");
        db.set(7, "user:1", "address", "123 Main St");

        System.out.println("Scan user:1 at timestamp 7:");
        List<String> scan1 = db.scan(7, "user:1");
        scan1.forEach(s -> System.out.println("  " + s));

        System.out.println("\nScan by prefix 'phone' at timestamp 7:");
        List<String> scanPrefix = db.scanByPrefix(7, "user:1", "phone");
        scanPrefix.forEach(s -> System.out.println("  " + s));

        System.out.println("\n=== LEVEL 4: Time-To-Live (TTL) ===\n");

        db.setWithTTL(10, "cache:1", "session", "abc123", 5); // Expires at timestamp 15
        db.setWithTTL(10, "cache:2", "token", "xyz789", 100); // Expires at timestamp 110

        System.out.println("Cache values at timestamp 12:");
        System.out.println("  cache:1 session: " + db.get(12, "cache:1", "session"));
        System.out.println("  cache:2 token: " + db.get(12, "cache:2", "token"));

        System.out.println("\nCache values at timestamp 16 (after TTL expiry):");
        System.out.println("  cache:1 session: " + db.get(16, "cache:1", "session"));
        System.out.println("  cache:2 token: " + db.get(16, "cache:2", "token"));

        boolean cas3 = db.compareAndSetWithTTL(20, "user:1", "temp", null, "value", 10);
        System.out.println("\nCAS with TTL on new field: " + cas3);
        System.out.println("  At timestamp 25: " + db.get(25, "user:1", "temp"));
        System.out.println("  At timestamp 31: " + db.get(31, "user:1", "temp"));

        System.out.println("\n=== LEVEL 5: Look-Back Operations ===\n");

        System.out.println("Version history of user:1 name:");
        List<String> history = db.getVersionHistory(10, "user:1", "name");
        history.forEach(h -> System.out.println("  " + h));

        System.out.println("\nSnapshot at timestamp 3:");
        Map<String, List<String>> snapshot3 = db.getSnapshot(3);
        snapshot3.forEach((k, v) -> {
            System.out.println("  " + k + ":");
            v.forEach(f -> System.out.println("    " + f));
        });

        System.out.println("\nSnapshot at timestamp 7:");
        Map<String, List<String>> snapshot7 = db.getSnapshot(7);
        snapshot7.forEach((k, v) -> {
            System.out.println("  " + k + ":");
            v.forEach(f -> System.out.println("    " + f));
        });

        System.out.println("\n=== Advanced Test Cases ===\n");

        // Concurrent modifications
        System.out.println("Creating multiple keys:");
        for (int i = 1; i <= 3; i++) {
            db.set(50, "product:" + i, "name", "Product " + i);
            db.set(50, "product:" + i, "price", (i * 100) + "");
            db.set(50, "product:" + i, "stock", (i * 10) + "");
        }

        System.out.println("All keys at timestamp 50: " + db.getAllKeys(50));

        System.out.println("\nProduct snapshot at timestamp 50:");
        for (int i = 1; i <= 3; i++) {
            List<String> fields = db.scan(50, "product:" + i);
            System.out.println("  product:" + i + ": " + fields);
        }
    }
}

