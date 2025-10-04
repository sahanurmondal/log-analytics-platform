package miscellaneous.google;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Interview Question: Design a Log Storage System
 * 
 * Description:
 * Design a system that can store logs with timestamps and retrieve logs within
 * a time range.
 * Each log entry has a timestamp and a message.
 * Support operations:
 * 1. addLog(timestamp, message) - Add a log entry
 * 2. getLogs(startTime, endTime) - Get all logs within time range
 * 3. getLogsByPattern(pattern) - Get logs containing a specific pattern
 * 
 * Company: Google
 * Difficulty: Medium
 * Asked: Multiple times in 2023-2024 (System Design rounds)
 */
public class AddAndSearchWord {

    /**
     * Custom Question: Design a Real-time Typing Indicator
     */
    class TypingIndicator {
        private Map<String, Long> typingUsers = new ConcurrentHashMap<>();
        private Set<String> currentlyTyping = Collections.newSetFromMap(new ConcurrentHashMap<>());
        private final long TYPING_TIMEOUT = 3000; // 3 seconds

        public void startTyping(String userId) {
            typingUsers.put(userId, System.currentTimeMillis());
            currentlyTyping.add(userId);
        }

        public void stopTyping(String userId) {
            typingUsers.remove(userId);
            currentlyTyping.remove(userId);
        }

        public Set<String> getTypingUsers() {
            long currentTime = System.currentTimeMillis();
            Set<String> activeTypers = new HashSet<>();

            for (Map.Entry<String, Long> entry : typingUsers.entrySet()) {
                if (currentTime - entry.getValue() < TYPING_TIMEOUT) {
                    activeTypers.add(entry.getKey());
                } else {
                    stopTyping(entry.getKey());
                }
            }

            return activeTypers;
        }
    }

    /**
     * Custom Question: Design a Rate Limiter for API calls
     * 
     * Description:
     * Implement a rate limiter that allows at most N requests per time window.
     * Use sliding window approach.
     * 
     * Company: Google
     * Difficulty: Medium
     * Asked: Backend interviews 2023-2024
     */
    class RateLimiter {
        private Map<String, Queue<Long>> userRequests = new ConcurrentHashMap<>();
        private final int maxRequests;
        private final long windowSize;

        public RateLimiter(int maxRequests, long windowSizeMs) {
            this.maxRequests = maxRequests;
            this.windowSize = windowSizeMs;
        }

        public boolean allowRequest(String userId) {
            long currentTime = System.currentTimeMillis();
            Queue<Long> requests = userRequests.computeIfAbsent(userId, k -> new LinkedList<>());

            // Remove old requests outside the window
            while (!requests.isEmpty() && currentTime - requests.peek() > windowSize) {
                requests.poll();
            }

            if (requests.size() < maxRequests) {
                requests.offer(currentTime);
                return true;
            }

            return false;
        }
    }

    /**
     * Custom Question: Design a TTL Cache
     */
    class TTLCache<K, V> {
        private Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
        private PriorityQueue<CacheEntry<V>> expirationQueue = new PriorityQueue<>();
        private final long defaultTTL;

        class CacheEntry<V> implements Comparable<CacheEntry<V>> {
            K key;
            V value;
            long expirationTime;

            CacheEntry(K key, V value, long ttl) {
                this.key = key;
                this.value = value;
                this.expirationTime = System.currentTimeMillis() + ttl;
            }

            @Override
            public int compareTo(CacheEntry<V> other) {
                return Long.compare(this.expirationTime, other.expirationTime);
            }
        }

        public TTLCache(long defaultTTL) {
            this.defaultTTL = defaultTTL;
        }

        public void put(K key, V value) {
            put(key, value, defaultTTL);
        }

        public void put(K key, V value, long ttl) {
            evictExpired();
            CacheEntry<V> entry = new CacheEntry<>(key, value, ttl);
            cache.put(key, entry);
            expirationQueue.offer(entry);
        }

        public V get(K key) {
            evictExpired();
            CacheEntry<V> entry = cache.get(key);
            return entry != null && entry.expirationTime > System.currentTimeMillis() ? entry.value : null;
        }

        private void evictExpired() {
            long currentTime = System.currentTimeMillis();
            while (!expirationQueue.isEmpty() && expirationQueue.peek().expirationTime <= currentTime) {
                CacheEntry<V> expired = expirationQueue.poll();
                cache.remove(expired.key);
            }
        }
    }

}
