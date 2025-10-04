package design.medium;

import java.util.*;

/**
 * Design Cache with Time-To-Live (TTL)
 *
 * Description: Design a cache that automatically expires entries after a
 * specified TTL.
 * Support operations: put, get, cleanup
 * 
 * Constraints:
 * - 1 <= capacity <= 1000
 * - 1 <= ttl <= 10^6 seconds
 * - At most 10^4 operations
 *
 * Follow-up:
 * - Can you make cleanup automatic?
 * - What about LRU eviction with TTL?
 * 
 * Time Complexity: O(1) for put/get, O(n) for cleanup
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Amazon, Facebook
 */
public class DesignCacheWithTTL<K, V> {

    class CacheEntry {
        V value;
        long expirationTime;

        CacheEntry(V value, long ttl) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttl;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }

    private Map<K, CacheEntry> cache;
    private int capacity;
    private long defaultTTL;

    public DesignCacheWithTTL(int capacity, long defaultTTL) {
        this.capacity = capacity;
        this.defaultTTL = defaultTTL;
        this.cache = new LinkedHashMap<K, CacheEntry>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry> eldest) {
                return size() > capacity;
            }
        };
    }

    public void put(K key, V value) {
        put(key, value, defaultTTL);
    }

    public void put(K key, V value, long ttl) {
        cleanupExpired();
        cache.put(key, new CacheEntry(value, ttl));
    }

    public V get(K key) {
        cleanupExpired();
        CacheEntry entry = cache.get(key);

        if (entry == null || entry.isExpired()) {
            if (entry != null) {
                cache.remove(key);
            }
            return null;
        }

        return entry.value;
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public int size() {
        cleanupExpired();
        return cache.size();
    }

    public void cleanupExpired() {
        Iterator<Map.Entry<K, CacheEntry>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, CacheEntry> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }

    public void clear() {
        cache.clear();
    }

    public Set<K> keySet() {
        cleanupExpired();
        return new HashSet<>(cache.keySet());
    }

    public static void main(String[] args) throws InterruptedException {
        DesignCacheWithTTL<String, String> cache = new DesignCacheWithTTL<>(3, 2000); // 2 second TTL

        cache.put("key1", "value1");
        cache.put("key2", "value2", 1000); // 1 second TTL
        cache.put("key3", "value3");

        System.out.println("Initial size: " + cache.size());
        System.out.println("key1: " + cache.get("key1"));
        System.out.println("key2: " + cache.get("key2"));

        Thread.sleep(1500); // Wait 1.5 seconds

        System.out.println("After 1.5s - key2: " + cache.get("key2")); // Should be expired
        System.out.println("After 1.5s - key1: " + cache.get("key1")); // Should still exist
        System.out.println("Size after cleanup: " + cache.size());

        Thread.sleep(1000); // Wait another 1 second

        System.out.println("After 2.5s total - key1: " + cache.get("key1")); // Should be expired
        System.out.println("Final size: " + cache.size());
    }
}
