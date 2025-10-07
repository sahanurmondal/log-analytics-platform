# Problem 22: Cache Library (LRU / LFU / TinyLFU)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a generic in-memory cache library supporting multiple eviction policies (LRU, LFU, TinyLFU), thread-safe operations, and configurable size limits with O(1) access time.

**Assumptions / Scope:**
- Generic cache (key-value store) with type safety
- Multiple eviction policies: LRU, LFU, TinyLFU
- Fixed capacity with automatic eviction
- Thread-safe for concurrent access
- TTL (time-to-live) support per entry
- O(1) get, put, remove operations
- Scale: 1M entries, 100K ops/sec
- Out of scope: Distributed cache, persistence, write-through

**Non-Functional Goals:**
- O(1) time complexity for get/put
- Thread-safe (concurrent reads/writes)
- Memory efficient (no memory leaks)
- High hit rate (>80% for typical workloads)
- Low latency (< 1ms per operation)

### 2. Core Requirements

**Functional:**
- Put key-value with optional TTL
- Get value by key
- Remove key
- Clear all entries
- Support different eviction policies
- Automatic eviction when capacity reached
- Expire entries based on TTL
- Get cache statistics (hit rate, eviction count)

**Non-Functional:**
- **Performance**: O(1) get/put, < 1ms latency
- **Concurrency**: Thread-safe operations
- **Memory**: O(n) space for n entries
- **Accuracy**: Correct eviction order
- **Scalability**: Handle 1M entries efficiently

### 3. Main Engineering Challenges & Solutions

**Challenge 1: LRU Cache (Least Recently Used)**
- **Problem**: Evict least recently accessed item in O(1) time
- **Solution**: HashMap + Doubly Linked List
- **Algorithm**:
```java
// LRU: HashMap + Doubly Linked List
// Access order: [Most Recent] ← → ← → [Least Recent]

class LRUCache<K, V> {
    private Map<K, Node<K, V>> map;
    private DoublyLinkedList<K, V> accessList;
    private int capacity;
    
    // O(1) Get
    public V get(K key) {
        Node<K, V> node = map.get(key);
        
        if (node == null) {
            return null; // Cache miss
        }
        
        // Move to front (most recently used)
        accessList.moveToFront(node);
        
        return node.value;
    }
    
    // O(1) Put
    public void put(K key, V value) {
        Node<K, V> existingNode = map.get(key);
        
        if (existingNode != null) {
            // Update existing entry
            existingNode.value = value;
            accessList.moveToFront(existingNode);
        } else {
            // Add new entry
            if (map.size() >= capacity) {
                // Evict LRU (tail of list)
                Node<K, V> lru = accessList.removeTail();
                map.remove(lru.key);
            }
            
            Node<K, V> newNode = new Node<>(key, value);
            accessList.addToFront(newNode);
            map.put(key, newNode);
        }
    }
}

// Doubly Linked List Node
class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev;
    Node<K, V> next;
}
```

**Challenge 2: LFU Cache (Least Frequently Used)**
- **Problem**: Evict least frequently accessed item in O(1) time
- **Solution**: HashMap + Frequency Map + Min Heap
- **Algorithm**:
```java
// LFU: Track access frequency, evict minimum frequency

class LFUCache<K, V> {
    private Map<K, Node<K, V>> cache;
    private Map<Integer, DoublyLinkedList<K, V>> frequencyMap;
    private int capacity;
    private int minFrequency;
    
    // O(1) Get
    public V get(K key) {
        Node<K, V> node = cache.get(key);
        
        if (node == null) {
            return null;
        }
        
        // Update frequency
        updateFrequency(node);
        
        return node.value;
    }
    
    // O(1) Put
    public void put(K key, V value) {
        if (capacity == 0) return;
        
        Node<K, V> existingNode = cache.get(key);
        
        if (existingNode != null) {
            existingNode.value = value;
            updateFrequency(existingNode);
        } else {
            if (cache.size() >= capacity) {
                evictLFU();
            }
            
            Node<K, V> newNode = new Node<>(key, value, 1);
            cache.put(key, newNode);
            
            frequencyMap.computeIfAbsent(1, k -> new DoublyLinkedList<>())
                .addToFront(newNode);
            
            minFrequency = 1;
        }
    }
    
    // CRITICAL: Update frequency when accessed
    private void updateFrequency(Node<K, V> node) {
        int oldFreq = node.frequency;
        
        // Remove from old frequency list
        DoublyLinkedList<K, V> oldList = frequencyMap.get(oldFreq);
        oldList.remove(node);
        
        // If this was the only node at minFreq, update minFreq
        if (oldFreq == minFrequency && oldList.isEmpty()) {
            minFrequency++;
        }
        
        // Add to new frequency list
        node.frequency++;
        frequencyMap.computeIfAbsent(node.frequency, k -> new DoublyLinkedList<>())
            .addToFront(node);
    }
    
    // CRITICAL: Evict least frequently used
    private void evictLFU() {
        DoublyLinkedList<K, V> minFreqList = frequencyMap.get(minFrequency);
        
        // Remove LRU among LFU (tail of list)
        Node<K, V> nodeToEvict = minFreqList.removeTail();
        cache.remove(nodeToEvict.key);
    }
}

class Node<K, V> {
    K key;
    V value;
    int frequency;
    Node<K, V> prev;
    Node<K, V> next;
}
```

**Challenge 3: TTL (Time-To-Live) Support**
- **Problem**: Expire entries after specified time
- **Solution**: Lazy deletion + Background cleanup
- **Algorithm**:
```java
class TTLCache<K, V> {
    private Map<K, CacheEntry<V>> cache;
    private ScheduledExecutorService cleanupExecutor;
    
    static class CacheEntry<V> {
        V value;
        Instant expiresAt;
        
        boolean isExpired() {
            return expiresAt != null && Instant.now().isAfter(expiresAt);
        }
    }
    
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry == null) {
            return null;
        }
        
        // Lazy deletion: Check on access
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        
        return entry.value;
    }
    
    public void put(K key, V value, Duration ttl) {
        Instant expiresAt = ttl != null 
            ? Instant.now().plus(ttl)
            : null;
        
        cache.put(key, new CacheEntry<>(value, expiresAt));
    }
    
    // Background cleanup (every 60 seconds)
    @Scheduled(fixedDelay = 60_000)
    private void cleanupExpired() {
        Instant now = Instant.now();
        
        cache.entrySet().removeIf(entry -> {
            CacheEntry<V> cacheEntry = entry.getValue();
            return cacheEntry.expiresAt != null && 
                   now.isAfter(cacheEntry.expiresAt);
        });
    }
}
```

**Challenge 4: Thread-Safe Cache**
- **Problem**: Concurrent reads/writes cause race conditions
- **Solution**: ReadWriteLock or ConcurrentHashMap + Synchronized blocks
- **Algorithm**:
```java
// Thread-Safe LRU Cache
class ConcurrentLRUCache<K, V> {
    private final Map<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> accessList;
    private final int capacity;
    private final ReadWriteLock lock;
    
    public ConcurrentLRUCache(int capacity) {
        this.cache = new ConcurrentHashMap<>();
        this.accessList = new DoublyLinkedList<>();
        this.capacity = capacity;
        this.lock = new ReentrantReadWriteLock();
    }
    
    // CRITICAL: Thread-safe get with read lock
    public V get(K key) {
        lock.readLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            
            if (node == null) {
                return null;
            }
            
            // Upgrade to write lock for access list update
            lock.readLock().unlock();
            lock.writeLock().lock();
            try {
                accessList.moveToFront(node);
                return node.value;
            } finally {
                lock.readLock().lock();
                lock.writeLock().unlock();
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // CRITICAL: Thread-safe put with write lock
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            Node<K, V> existingNode = cache.get(key);
            
            if (existingNode != null) {
                existingNode.value = value;
                accessList.moveToFront(existingNode);
            } else {
                if (cache.size() >= capacity) {
                    Node<K, V> lru = accessList.removeTail();
                    cache.remove(lru.key);
                }
                
                Node<K, V> newNode = new Node<>(key, value);
                accessList.addToFront(newNode);
                cache.put(key, newNode);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Strategy** | Eviction policies (LRU, LFU, FIFO) | Swap algorithms without changing cache core |
| **Decorator** | Add TTL, statistics, logging to base cache | Extend behavior incrementally |
| **Template Method** | Common cache operations (get, put, evict) | Reuse common logic across policies |
| **Factory** | Create cache instances with specific policies | Centralize instantiation |
| **Builder** | Configure cache (capacity, TTL, policy) | Fluent configuration API |

### 5. Domain Model & Class Structure

```
┌─────────────────┐
│  Cache<K, V>    │ (Interface)
│  + get(K)       │
│  + put(K, V)    │
│  + remove(K)    │
│  + clear()      │
└────────┬────────┘
         │ implements
    ┌────┴────────────────────┐
    ▼                         ▼
┌─────────────┐      ┌──────────────┐
│  LRUCache   │      │  LFUCache    │
│  - map      │      │  - cache     │
│  - list     │      │  - freqMap   │
└─────────────┘      └──────────────┘

┌──────────────────┐
│ DoublyLinkedList │
│  - head          │
│  - tail          │
│  + addToFront()  │
│  + removeTail()  │
│  + moveToFront() │
└──────────────────┘

┌──────────────────┐
│  Node<K, V>      │
│  - key           │
│  - value         │
│  - prev          │
│  - next          │
└──────────────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: LRU Cache Get**
```
Client   LRUCache   HashMap   LinkedList
  │         │          │          │
  ├─get(K)──>│          │          │
  │         ├─get(K)───>│          │
  │         │<─node─────┤          │
  │         ├─moveToFront────────>│
  │         │          │   (update access order)
  │<─value──┤          │          │
```

**Sequence: LRU Cache Put (Eviction)**
```
Client   LRUCache   HashMap   LinkedList
  │         │          │          │
  ├─put(K,V)>│          │          │
  │         ├─size()───>│          │
  │         │<─full─────┤          │
  │         ├─removeTail───────────>│
  │         │          │   (evict LRU)
  │         │<─lruNode──────────────┤
  │         ├─remove(K)>│          │
  │         ├─put(K,V)─>│          │
  │         ├─addToFront───────────>│
  │<─success┤          │          │
```

### 7. Core Implementation (Interview-Critical Methods)

```java
// ============================================
// CACHE INTERFACE
// ============================================

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    void clear();
    int size();
    CacheStats getStats();
}

// ============================================
// DOUBLY LINKED LIST (LRU/LFU Helper)
// ============================================

public class DoublyLinkedList<K, V> {
    private Node<K, V> head;
    private Node<K, V> tail;
    private int size;
    
    public DoublyLinkedList() {
        // Sentinel nodes to simplify edge cases
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }
    
    /**
     * INTERVIEW CRITICAL: Add node to front (most recently used)
     */
    public void addToFront(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        size++;
    }
    
    /**
     * INTERVIEW CRITICAL: Remove node from list
     */
    public void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
    }
    
    /**
     * INTERVIEW CRITICAL: Remove tail (least recently used)
     */
    public Node<K, V> removeTail() {
        if (isEmpty()) {
            return null;
        }
        
        Node<K, V> lastNode = tail.prev;
        remove(lastNode);
        return lastNode;
    }
    
    /**
     * INTERVIEW CRITICAL: Move node to front (update access order)
     */
    public void moveToFront(Node<K, V> node) {
        remove(node);
        addToFront(node);
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
}

// ============================================
// NODE (Generic)
// ============================================

public class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev;
    Node<K, V> next;
    int frequency; // For LFU
    Instant expiresAt; // For TTL
    
    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.frequency = 1;
    }
}

// ============================================
// LRU CACHE IMPLEMENTATION
// ============================================

public class LRUCache<K, V> implements Cache<K, V> {
    private final Map<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> accessList;
    private final int capacity;
    private final CacheStats stats;
    
    public LRUCache(int capacity) {
        this.cache = new HashMap<>();
        this.accessList = new DoublyLinkedList<>();
        this.capacity = capacity;
        this.stats = new CacheStats();
    }
    
    /**
     * INTERVIEW CRITICAL: O(1) get with LRU update
     */
    @Override
    public V get(K key) {
        Node<K, V> node = cache.get(key);
        
        if (node == null) {
            stats.recordMiss();
            return null;
        }
        
        // Move to front (most recently used)
        accessList.moveToFront(node);
        stats.recordHit();
        
        return node.value;
    }
    
    /**
     * INTERVIEW CRITICAL: O(1) put with eviction
     */
    @Override
    public void put(K key, V value) {
        Node<K, V> existingNode = cache.get(key);
        
        if (existingNode != null) {
            // Update existing entry
            existingNode.value = value;
            accessList.moveToFront(existingNode);
        } else {
            // Check capacity
            if (cache.size() >= capacity) {
                // Evict LRU (tail)
                Node<K, V> lru = accessList.removeTail();
                cache.remove(lru.key);
                stats.recordEviction();
            }
            
            // Add new entry
            Node<K, V> newNode = new Node<>(key, value);
            accessList.addToFront(newNode);
            cache.put(key, newNode);
        }
    }
    
    @Override
    public void remove(K key) {
        Node<K, V> node = cache.remove(key);
        if (node != null) {
            accessList.remove(node);
        }
    }
    
    @Override
    public void clear() {
        cache.clear();
        accessList = new DoublyLinkedList<>();
    }
    
    @Override
    public int size() {
        return cache.size();
    }
    
    @Override
    public CacheStats getStats() {
        return stats;
    }
}

// ============================================
// LFU CACHE IMPLEMENTATION
// ============================================

public class LFUCache<K, V> implements Cache<K, V> {
    private final Map<K, Node<K, V>> cache;
    private final Map<Integer, DoublyLinkedList<K, V>> frequencyMap;
    private final int capacity;
    private int minFrequency;
    private final CacheStats stats;
    
    public LFUCache(int capacity) {
        this.cache = new HashMap<>();
        this.frequencyMap = new HashMap<>();
        this.capacity = capacity;
        this.minFrequency = 0;
        this.stats = new CacheStats();
    }
    
    /**
     * INTERVIEW CRITICAL: O(1) get with frequency update
     */
    @Override
    public V get(K key) {
        Node<K, V> node = cache.get(key);
        
        if (node == null) {
            stats.recordMiss();
            return null;
        }
        
        updateFrequency(node);
        stats.recordHit();
        
        return node.value;
    }
    
    /**
     * INTERVIEW CRITICAL: O(1) put with LFU eviction
     */
    @Override
    public void put(K key, V value) {
        if (capacity == 0) {
            return;
        }
        
        Node<K, V> existingNode = cache.get(key);
        
        if (existingNode != null) {
            existingNode.value = value;
            updateFrequency(existingNode);
        } else {
            if (cache.size() >= capacity) {
                evictLFU();
            }
            
            Node<K, V> newNode = new Node<>(key, value);
            newNode.frequency = 1;
            
            cache.put(key, newNode);
            
            frequencyMap.computeIfAbsent(1, k -> new DoublyLinkedList<>())
                .addToFront(newNode);
            
            minFrequency = 1;
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Update frequency when accessed
     */
    private void updateFrequency(Node<K, V> node) {
        int oldFreq = node.frequency;
        
        // Remove from old frequency list
        DoublyLinkedList<K, V> oldList = frequencyMap.get(oldFreq);
        oldList.remove(node);
        
        // Update minFrequency if needed
        if (oldFreq == minFrequency && oldList.isEmpty()) {
            minFrequency++;
        }
        
        // Add to new frequency list
        node.frequency++;
        frequencyMap.computeIfAbsent(node.frequency, k -> new DoublyLinkedList<>())
            .addToFront(node);
    }
    
    /**
     * INTERVIEW CRITICAL: Evict least frequently used
     */
    private void evictLFU() {
        DoublyLinkedList<K, V> minFreqList = frequencyMap.get(minFrequency);
        
        // Among LFU, evict LRU (tail)
        Node<K, V> nodeToEvict = minFreqList.removeTail();
        cache.remove(nodeToEvict.key);
        stats.recordEviction();
    }
    
    @Override
    public void remove(K key) {
        Node<K, V> node = cache.remove(key);
        if (node != null) {
            DoublyLinkedList<K, V> list = frequencyMap.get(node.frequency);
            if (list != null) {
                list.remove(node);
            }
        }
    }
    
    @Override
    public void clear() {
        cache.clear();
        frequencyMap.clear();
        minFrequency = 0;
    }
    
    @Override
    public int size() {
        return cache.size();
    }
    
    @Override
    public CacheStats getStats() {
        return stats;
    }
}

// ============================================
// THREAD-SAFE LRU CACHE
// ============================================

public class ConcurrentLRUCache<K, V> implements Cache<K, V> {
    private final Map<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> accessList;
    private final int capacity;
    private final ReadWriteLock lock;
    private final CacheStats stats;
    
    public ConcurrentLRUCache(int capacity) {
        this.cache = new ConcurrentHashMap<>();
        this.accessList = new DoublyLinkedList<>();
        this.capacity = capacity;
        this.lock = new ReentrantReadWriteLock();
        this.stats = new CacheStats();
    }
    
    /**
     * INTERVIEW CRITICAL: Thread-safe get with read-write lock
     */
    @Override
    public V get(K key) {
        Node<K, V> node = cache.get(key);
        
        if (node == null) {
            stats.recordMiss();
            return null;
        }
        
        // Write lock for access list update
        lock.writeLock().lock();
        try {
            accessList.moveToFront(node);
            stats.recordHit();
            return node.value;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Thread-safe put with write lock
     */
    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            Node<K, V> existingNode = cache.get(key);
            
            if (existingNode != null) {
                existingNode.value = value;
                accessList.moveToFront(existingNode);
            } else {
                if (cache.size() >= capacity) {
                    Node<K, V> lru = accessList.removeTail();
                    cache.remove(lru.key);
                    stats.recordEviction();
                }
                
                Node<K, V> newNode = new Node<>(key, value);
                accessList.addToFront(newNode);
                cache.put(key, newNode);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void remove(K key) {
        lock.writeLock().lock();
        try {
            Node<K, V> node = cache.remove(key);
            if (node != null) {
                accessList.remove(node);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
            accessList = new DoublyLinkedList<>();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public int size() {
        return cache.size();
    }
    
    @Override
    public CacheStats getStats() {
        return stats;
    }
}

// ============================================
// CACHE STATISTICS
// ============================================

public class CacheStats {
    private final AtomicLong hitCount;
    private final AtomicLong missCount;
    private final AtomicLong evictionCount;
    
    public CacheStats() {
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);
        this.evictionCount = new AtomicLong(0);
    }
    
    public void recordHit() {
        hitCount.incrementAndGet();
    }
    
    public void recordMiss() {
        missCount.incrementAndGet();
    }
    
    public void recordEviction() {
        evictionCount.incrementAndGet();
    }
    
    public double getHitRate() {
        long hits = hitCount.get();
        long misses = missCount.get();
        long total = hits + misses;
        
        return total == 0 ? 0.0 : (double) hits / total;
    }
    
    public long getTotalRequests() {
        return hitCount.get() + missCount.get();
    }
    
    public long getEvictionCount() {
        return evictionCount.get();
    }
}

// ============================================
// CACHE BUILDER (Fluent API)
// ============================================

public class CacheBuilder<K, V> {
    private int capacity = 1000;
    private EvictionPolicy policy = EvictionPolicy.LRU;
    private boolean threadSafe = false;
    
    public enum EvictionPolicy {
        LRU, LFU, FIFO
    }
    
    public CacheBuilder<K, V> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }
    
    public CacheBuilder<K, V> evictionPolicy(EvictionPolicy policy) {
        this.policy = policy;
        return this;
    }
    
    public CacheBuilder<K, V> threadSafe(boolean threadSafe) {
        this.threadSafe = threadSafe;
        return this;
    }
    
    public Cache<K, V> build() {
        Cache<K, V> cache;
        
        switch (policy) {
            case LRU:
                cache = threadSafe 
                    ? new ConcurrentLRUCache<>(capacity)
                    : new LRUCache<>(capacity);
                break;
            case LFU:
                cache = new LFUCache<>(capacity);
                break;
            default:
                throw new IllegalArgumentException("Unsupported policy: " + policy);
        }
        
        return cache;
    }
}

// Usage:
Cache<String, User> cache = new CacheBuilder<String, User>()
    .capacity(10000)
    .evictionPolicy(EvictionPolicy.LRU)
    .threadSafe(true)
    .build();
```

### 8. Thread Safety & Concurrency

**LRU Cache:**
- `ReadWriteLock`: Multiple concurrent reads, exclusive writes
- `ConcurrentHashMap` for cache storage
- Synchronized access list updates

**LFU Cache:**
- More complex due to frequency tracking
- Consider segment locking for better parallelism

**Performance Trade-offs:**
- Read lock overhead ~5-10μs
- Write lock contention under high load
- Alternative: Lock-free with CAS (more complex)

### 9. Top Interview Questions & Answers

**Q1: Why HashMap + Doubly Linked List for LRU?**
**A:**
```
HashMap: O(1) key lookup
Doubly Linked List: O(1) node removal/insertion

Alternative data structures:
❌ Array: O(n) for removal
❌ Single Linked List: O(n) to find previous node
❌ TreeMap: O(log n) lookup
✅ HashMap + DLL: O(1) for all operations

Critical operations:
1. Get: HashMap lookup + Move node to front
2. Put: HashMap insert + Add node to front
3. Evict: Remove tail node + HashMap delete
```

**Q2: LRU vs LFU - when to use each?**
**A:**
| Scenario | Use LRU | Use LFU |
|----------|---------|---------|
| Recent access pattern | ✅ | ❌ |
| Frequency-based access | ❌ | ✅ |
| Simple implementation | ✅ | ❌ |
| Memory efficiency | ✅ (O(n)) | ❌ (O(n × f)) |
| Cache pollution resistance | ❌ | ✅ |
| General purpose | ✅ | ❌ |

**Q3: How do you handle concurrent modifications to the linked list?**
**A:**
```java
// Option 1: Coarse-grained locking (simple)
public synchronized V get(K key) {
    // Entire method is atomic
}

// Option 2: Read-write lock (better parallelism)
public V get(K key) {
    lock.writeLock().lock(); // Even for reads (due to list update)
    try {
        // Access + update list
    } finally {
        lock.writeLock().unlock();
    }
}

// Option 3: Segmented locking (highest concurrency)
// Divide cache into segments, each with own lock
// Similar to ConcurrentHashMap's approach
```

**Q4: What if two threads try to evict simultaneously?**
**A:**
```java
public void put(K key, V value) {
    lock.writeLock().lock();
    try {
        // Check size INSIDE lock
        if (cache.size() >= capacity) {
            Node<K, V> lru = accessList.removeTail();
            cache.remove(lru.key);
        }
        
        // Add new node
        Node<K, V> newNode = new Node<>(key, value);
        accessList.addToFront(newNode);
        cache.put(key, newNode);
    } finally {
        lock.writeLock().unlock();
    }
}

// Without lock:
// Thread 1: size() == capacity → evict → (interrupted)
// Thread 2: size() == capacity → evict → (both evict, size < capacity-1)
```

**Q5: How do you test the LRU eviction order?**
**A:**
```java
@Test
public void testLRUEvictionOrder() {
    Cache<Integer, String> cache = new LRUCache<>(3);
    
    // Fill cache
    cache.put(1, "one");
    cache.put(2, "two");
    cache.put(3, "three");
    
    // Access order: 3 → 2 → 1 (most to least recent)
    
    // Access 1 (moves to front)
    cache.get(1);
    
    // Access order: 1 → 3 → 2
    
    // Add 4 (should evict 2, the LRU)
    cache.put(4, "four");
    
    // Verify
    assertNotNull(cache.get(1)); // Still present
    assertNull(cache.get(2));    // Evicted
    assertNotNull(cache.get(3)); // Still present
    assertNotNull(cache.get(4)); // Newly added
}
```

**Q6: How do you implement cache warming?**
**A:**
```java
public class CacheWarmer<K, V> {
    private final Cache<K, V> cache;
    private final DataSource<K, V> dataSource;
    
    public void warmUp(List<K> keysToPreload) {
        // Load frequently accessed keys at startup
        for (K key : keysToPreload) {
            V value = dataSource.load(key);
            if (value != null) {
                cache.put(key, value);
            }
        }
    }
    
    // Adaptive warming based on historical access patterns
    public void adaptiveWarmUp(CacheStats stats) {
        List<K> hotKeys = stats.getMostAccessedKeys(100);
        warmUp(hotKeys);
    }
}
```

**Q7: What metrics should you track?**
**A:**
```java
Cache Metrics:
1. Hit Rate: hits / (hits + misses)
2. Miss Rate: 1 - hit rate
3. Eviction Count: Total evictions
4. Average Latency: P50/P95/P99 for get/put
5. Memory Usage: Current size / capacity
6. Hot Keys: Top N most accessed keys

Alerts:
- Hit rate < 80% → Consider increasing capacity
- Eviction rate > 1000/sec → Capacity too small
- P99 latency > 10ms → Contention issue
```

**Q8: How do you handle cache stampede?**
**A:**
```java
// Problem: Many threads request same missing key simultaneously
// All miss cache, all query DB (thundering herd)

public class StampedeProtectedCache<K, V> {
    private final Cache<K, V> cache;
    private final ConcurrentMap<K, CompletableFuture<V>> loadingKeys;
    
    public V get(K key, Function<K, V> loader) {
        // Check cache first
        V cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        
        // Atomic: Only one thread loads for a key
        CompletableFuture<V> future = loadingKeys.computeIfAbsent(key, k -> 
            CompletableFuture.supplyAsync(() -> {
                V value = loader.apply(k);
                cache.put(k, value);
                return value;
            })
        );
        
        try {
            return future.get(); // All threads wait for same load
        } finally {
            loadingKeys.remove(key);
        }
    }
}
```

**Q9: How do you implement write-through vs write-back?**
**A:**
```java
// Write-Through: Sync write to cache + DB
public void put(K key, V value) {
    database.save(key, value); // Write to DB first
    cache.put(key, value);      // Then update cache
}

// Write-Back: Async write to DB
public void put(K key, V value) {
    cache.put(key, value);       // Update cache immediately
    dirtyKeys.add(key);          // Mark as dirty
    
    // Background thread flushes dirty keys
    flushScheduler.schedule(() -> {
        database.save(key, cache.get(key));
        dirtyKeys.remove(key);
    }, 5, TimeUnit.SECONDS);
}

// Trade-offs:
// Write-Through: Slower writes, guaranteed consistency
// Write-Back: Faster writes, risk of data loss
```

**Q10: How would you scale to distributed cache?**
**A:**
```
Architecture:
1. Consistent hashing for key distribution
2. Redis cluster for shared cache layer
3. Local L1 cache + Redis L2 cache (multi-tier)
4. Cache invalidation via pub/sub

Example:
┌──────────┐   ┌──────────┐   ┌──────────┐
│  App 1   │   │  App 2   │   │  App 3   │
│ (L1 LRU) │   │ (L1 LRU) │   │ (L1 LRU) │
└────┬─────┘   └────┬─────┘   └────┬─────┘
     │              │              │
     └──────────────┼──────────────┘
                    │
            ┌───────▼────────┐
            │  Redis Cluster │ (L2)
            │  (Distributed) │
            └────────────────┘

Invalidation:
- App 1 updates key → Pub/sub message → Apps 2,3 invalidate L1
- TTL-based expiry (consistency vs freshness trade-off)
```

### 10. Extensions & Variations

1. **TTL Support**: Expire entries after time duration
2. **Soft/Weak References**: Allow GC to reclaim under memory pressure
3. **Write-Through/Write-Back**: Sync with backing store
4. **Tiered Caching**: L1 (local) + L2 (distributed)
5. **Bloom Filter**: Fast negative lookups (key not in cache)

### 11. Testing Strategy

**Unit Tests:**
- LRU eviction order correctness
- LFU frequency tracking accuracy
- Thread-safe concurrent access
- Edge cases (capacity = 1, empty cache)

**Performance Tests:**
- 100K ops/sec throughput
- Concurrent read/write (10 threads)
- Memory usage (1M entries)
- Hit rate with realistic workload

**Property-Based Tests:**
```java
@Property
public void cacheNeverExceedsCapacity(@ForAll List<Integer> keys) {
    Cache<Integer, String> cache = new LRUCache<>(100);
    
    for (Integer key : keys) {
        cache.put(key, key.toString());
    }
    
    assertTrue(cache.size() <= 100);
}
```

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Using single linked list (O(n) to find previous node)
✅ **Do**: Doubly linked list for O(1) removal

❌ **Avoid**: No thread safety (race conditions)
✅ **Do**: ReadWriteLock or synchronized methods

❌ **Avoid**: HashMap without linked list (can't track access order)
✅ **Do**: HashMap + Doubly Linked List

❌ **Avoid**: Iterating to find LRU/LFU (O(n))
✅ **Do**: Maintain order explicitly (O(1))

### 13. Complexity Analysis

| Operation | LRU | LFU | Space |
|-----------|-----|-----|-------|
| Get | O(1) | O(1) | O(n) |
| Put | O(1) | O(1) | O(n) |
| Remove | O(1) | O(1) | - |
| Evict | O(1) | O(1) | - |

**Space Breakdown:**
- HashMap: O(n) for n entries
- Doubly Linked List: O(n) for n nodes (3 pointers per node)
- Total: ~48 bytes per entry (64-bit JVM)

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Data Structures** | 35% | HashMap + Doubly Linked List, understands why |
| **Algorithm** | 25% | Correct LRU/LFU eviction logic |
| **Thread Safety** | 20% | Proper locking, concurrent collections |
| **Complexity** | 15% | All operations O(1) |
| **Code Quality** | 5% | Clean, testable, extensible |

**Red Flags:**
- Array or list for cache (O(n) operations)
- No thread safety consideration
- Incorrect eviction order
- Missing edge cases (empty, capacity = 1)
- Can't explain data structure choice

---
