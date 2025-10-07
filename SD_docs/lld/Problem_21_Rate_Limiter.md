# Problem 21: Rate Limiter (Token Bucket + Leaky Bucket + Sliding Window)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a rate limiter that restricts the number of requests a client can make within a time window, supporting multiple algorithms (token bucket, leaky bucket, sliding window) and distributed deployment.

**Assumptions / Scope:**
- Support per-user, per-IP, per-API-key rate limiting
- Multiple algorithms: Token Bucket, Leaky Bucket, Sliding Window
- Configurable limits: requests/second, requests/minute, requests/hour
- Distributed rate limiting across multiple servers
- Real-time limit enforcement with low latency (< 5ms)
- Scale: 100K requests/sec, 10M unique keys
- Out of scope: DDoS protection, geographic rate limiting

**Non-Functional Goals:**
- Low latency (< 5ms overhead per request)
- High accuracy (< 1% error rate)
- Distributed consistency (no drift across nodes)
- Memory efficient (O(k) space for k keys)
- Thread-safe for concurrent requests

### 2. Core Requirements

**Functional:**
- Allow/deny requests based on rate limit
- Support burst traffic (temporary spikes)
- Multiple limit tiers (free: 10/min, premium: 1000/min)
- Return remaining quota and reset time
- Support different limit keys (user ID, IP, API key)
- Configurable time windows (second, minute, hour)
- Graceful degradation on limit breach

**Non-Functional:**
- **Performance**: < 5ms per request check
- **Accuracy**: Within 1% of configured limit
- **Scalability**: Handle 100K RPS on single node
- **Consistency**: Distributed rate limits synchronized
- **Availability**: 99.99% uptime

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Token Bucket Algorithm**
- **Problem**: Allow burst traffic while maintaining average rate
- **Solution**: Bucket fills with tokens at fixed rate, requests consume tokens
- **Algorithm**:
```java
// Token Bucket: Allows bursts up to bucket capacity
class TokenBucket {
    private long capacity;           // Max tokens
    private long tokens;             // Current tokens
    private long refillRate;         // Tokens per second
    private Instant lastRefillTime;
    
    public synchronized boolean allowRequest(int tokensRequired) {
        refill();
        
        if (tokens >= tokensRequired) {
            tokens -= tokensRequired;
            return true; // Allow
        }
        
        return false; // Deny
    }
    
    // CRITICAL: Refill tokens based on elapsed time
    private void refill() {
        Instant now = Instant.now();
        Duration elapsed = Duration.between(lastRefillTime, now);
        
        // Calculate new tokens
        long tokensToAdd = elapsed.toMillis() * refillRate / 1000;
        
        if (tokensToAdd > 0) {
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }
}

// Example: 100 requests/second with burst of 200
TokenBucket bucket = new TokenBucket(
    capacity = 200,      // Allow burst of 200
    refillRate = 100     // Refill 100 tokens/second
);

// Burst scenario:
// t=0s:  200 requests → All allowed (tokens: 200 → 0)
// t=1s:  150 requests → 100 allowed, 50 denied (tokens: 100 → 0)
// t=2s:  50 requests  → All allowed (tokens: 100 → 50)
```

**Challenge 2: Leaky Bucket Algorithm**
- **Problem**: Smooth traffic to constant rate, no bursts
- **Solution**: Queue requests, process at fixed rate
- **Algorithm**:
```java
// Leaky Bucket: Processes requests at constant rate
class LeakyBucket {
    private Queue<Request> queue;
    private long capacity;           // Max queue size
    private long leakRate;           // Requests per second
    private ScheduledExecutorService executor;
    
    public LeakyBucket(long capacity, long leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.queue = new LinkedBlockingQueue<>((int) capacity);
        
        // Background thread: Leak (process) requests at fixed rate
        executor = Executors.newScheduledThreadPool(1);
        long intervalMs = 1000 / leakRate;
        
        executor.scheduleAtFixedRate(
            this::leak,
            0,
            intervalMs,
            TimeUnit.MILLISECONDS
        );
    }
    
    public boolean allowRequest(Request request) {
        // Try to add to queue
        return queue.offer(request);
    }
    
    private void leak() {
        Request request = queue.poll();
        if (request != null) {
            processRequest(request);
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}

// Example: 10 requests/second, queue capacity 50
LeakyBucket bucket = new LeakyBucket(
    capacity = 50,
    leakRate = 10  // Process 10/second
);

// Burst scenario:
// t=0s:  100 requests → 50 queued, 50 rejected
// t=1s:  10 requests processed from queue (40 remaining)
// t=5s:  All 50 processed
```

**Challenge 3: Sliding Window Log Algorithm**
- **Problem**: Accurate rate limiting without burst allowance
- **Solution**: Track timestamps of all requests in window
- **Algorithm**:
```java
// Sliding Window Log: Precise but memory-intensive
class SlidingWindowLog {
    private TreeMap<Long, Integer> requestLog; // timestamp → count
    private long windowSizeMs;
    private long maxRequests;
    
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSizeMs;
        
        // Remove requests outside window
        requestLog.headMap(windowStart).clear();
        
        // Count requests in current window
        int requestCount = requestLog.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
        
        if (requestCount < maxRequests) {
            requestLog.merge(now, 1, Integer::sum);
            return true;
        }
        
        return false;
    }
}

// Problem: Memory usage O(maxRequests) per key
// 1M requests/min = 1M timestamps = ~8MB per user
```

**Challenge 4: Sliding Window Counter (Optimized)**
- **Problem**: Sliding window log uses too much memory
- **Solution**: Approximate with two fixed windows
- **Algorithm**:
```java
// Sliding Window Counter: Memory-efficient approximation
class SlidingWindowCounter {
    private long previousWindowCount;
    private long currentWindowCount;
    private Instant currentWindowStart;
    private Duration windowSize;
    private long maxRequests;
    
    public synchronized boolean allowRequest() {
        Instant now = Instant.now();
        
        // Check if we've moved to new window
        if (Duration.between(currentWindowStart, now).compareTo(windowSize) >= 0) {
            previousWindowCount = currentWindowCount;
            currentWindowCount = 0;
            currentWindowStart = now;
        }
        
        // Calculate weighted count
        double elapsedPercentage = Duration.between(currentWindowStart, now).toMillis() 
            / (double) windowSize.toMillis();
        
        double weightedCount = 
            previousWindowCount * (1 - elapsedPercentage) + currentWindowCount;
        
        if (weightedCount < maxRequests) {
            currentWindowCount++;
            return true;
        }
        
        return false;
    }
}

// Example: 100 requests/minute
// t=0:00  → Previous: 80, Current: 0, Elapsed: 0%
//           Weighted: 80 * 1.0 + 0 = 80
// t=0:30  → Previous: 80, Current: 50, Elapsed: 50%
//           Weighted: 80 * 0.5 + 50 = 90
// t=1:00  → Previous: 50, Current: 0, Elapsed: 0%
//           Weighted: 50 * 1.0 + 0 = 50

// Memory: O(1) per key (only 2 counters)
```

**Challenge 5: Distributed Rate Limiting (Redis)**
- **Problem**: Multiple servers need shared rate limit state
- **Solution**: Centralized Redis with atomic operations
- **Algorithm**:
```lua
-- Lua script for atomic Redis token bucket
local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local requested = tonumber(ARGV[3])
local now = tonumber(ARGV[4])

-- Get current state
local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
local tokens = tonumber(bucket[1]) or capacity
local last_refill = tonumber(bucket[2]) or now

-- Refill tokens
local elapsed = now - last_refill
local tokens_to_add = elapsed * refill_rate / 1000
tokens = math.min(capacity, tokens + tokens_to_add)

-- Check if request allowed
if tokens >= requested then
    tokens = tokens - requested
    
    -- Update state
    redis.call('HMSET', key, 'tokens', tokens, 'last_refill', now)
    redis.call('EXPIRE', key, 3600)  -- TTL 1 hour
    
    return {1, tokens}  -- Allow, remaining tokens
else
    return {0, tokens}  -- Deny, remaining tokens
end
```

```java
// Java client using Redis Lua script
public class DistributedTokenBucket {
    private RedisTemplate<String, String> redis;
    private String luaScript;
    
    public RateLimitResult allowRequest(String key, long capacity, 
                                        long refillRate, int requested) {
        long now = System.currentTimeMillis();
        
        List<Long> result = redis.execute(
            new DefaultRedisScript<>(luaScript, List.class),
            Collections.singletonList(key),
            capacity, refillRate, requested, now
        );
        
        boolean allowed = result.get(0) == 1;
        long remainingTokens = result.get(1);
        
        return new RateLimitResult(allowed, remainingTokens);
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Strategy** | Rate limiting algorithms (Token/Leaky/Sliding) | Swap algorithms at runtime |
| **Template Method** | Common rate limit flow (check → update → respond) | Reuse common logic |
| **Decorator** | Add features (logging, metrics) to limiters | Extend behavior without modifying core |
| **Factory** | Create appropriate limiter for config | Centralize instantiation |
| **Proxy** | Distributed rate limiter wraps local limiter | Add Redis layer transparently |
| **Singleton** | Rate limiter registry | Single instance per application |

### 5. Domain Model & Class Structure

```
┌─────────────────────┐
│  RateLimiter        │ (Interface)
│  + allowRequest()   │
│  + getRemainingQuota() │
└──────────┬──────────┘
           │ implements
    ┌──────┴─────────────────────────┐
    ▼                                ▼
┌──────────────────┐      ┌──────────────────┐
│ TokenBucket      │      │  LeakyBucket     │
│  - capacity      │      │  - queue         │
│  - tokens        │      │  - leakRate      │
│  - refillRate    │      └──────────────────┘
└──────────────────┘
    ▼
┌──────────────────┐      ┌──────────────────┐
│ SlidingWindow    │      │ Distributed      │
│  - requestLog    │      │ TokenBucket      │
│  - windowSize    │      │  - redis         │
└──────────────────┘      └──────────────────┘

┌─────────────────────┐
│ RateLimiterConfig   │
│  - algorithm        │
│  - maxRequests      │
│  - windowDuration   │
└─────────────────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Token Bucket Rate Limit Check**
```
Client    API       RateLimiter   TokenBucket   Redis
  │         │            │             │          │
  ├─request─>│            │             │          │
  │         ├─checkLimit─────────────>│          │
  │         │            │            ├─refill───┤
  │         │            │            │<─state───┤
  │         │            │            │          │
  │         │            │<─allowed───┤          │
  │         │            │ (remaining: 95)       │
  │         │<─200 OK────┤             │          │
  │<─response┤            │             │          │
  │         │            │             │          │
  │ (96th request)       │             │          │
  │         │            │             │          │
  ├─request─>│            │             │          │
  │         ├─checkLimit─────────────>│          │
  │         │            │<─denied─────┤          │
  │         │            │ (remaining: 0)         │
  │         │<─429───────┤             │          │
  │         │ Too Many Requests        │          │
  │<─error──┤            │             │          │
```

### 7. Core Implementation (Interview-Critical Methods)

```java
// ============================================
// RATE LIMITER INTERFACE
// ============================================

public interface RateLimiter {
    RateLimitResult allowRequest(String key);
    RateLimitResult allowRequest(String key, int tokens);
    long getRemainingQuota(String key);
}

public class RateLimitResult {
    private final boolean allowed;
    private final long remainingQuota;
    private final Instant resetTime;
    
    // Constructor and getters omitted
}

// ============================================
// TOKEN BUCKET IMPLEMENTATION
// ============================================

public class TokenBucketRateLimiter implements RateLimiter {
    private final Map<String, TokenBucket> buckets;
    private final long capacity;
    private final long refillRate; // tokens per second
    
    public TokenBucketRateLimiter(long capacity, long refillRate) {
        this.buckets = new ConcurrentHashMap<>();
        this.capacity = capacity;
        this.refillRate = refillRate;
    }
    
    @Override
    public RateLimitResult allowRequest(String key, int tokensRequired) {
        TokenBucket bucket = buckets.computeIfAbsent(
            key,
            k -> new TokenBucket(capacity, refillRate)
        );
        
        return bucket.tryConsume(tokensRequired);
    }
    
    @Override
    public long getRemainingQuota(String key) {
        TokenBucket bucket = buckets.get(key);
        return bucket != null ? bucket.getAvailableTokens() : capacity;
    }
}

/**
 * INTERVIEW CRITICAL: Token Bucket with time-based refill
 */
public class TokenBucket {
    private final long capacity;
    private final long refillRate; // tokens per second
    private long tokens;
    private Instant lastRefillTime;
    
    public TokenBucket(long capacity, long refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity; // Start full
        this.lastRefillTime = Instant.now();
    }
    
    /**
     * INTERVIEW CRITICAL: Thread-safe token consumption with refill
     */
    public synchronized RateLimitResult tryConsume(int tokensRequired) {
        refill();
        
        if (tokens >= tokensRequired) {
            tokens -= tokensRequired;
            
            // Calculate reset time (when bucket will be full again)
            long tokensNeeded = capacity - tokens;
            long secondsToFull = tokensNeeded / refillRate;
            Instant resetTime = Instant.now().plusSeconds(secondsToFull);
            
            return new RateLimitResult(true, tokens, resetTime);
        }
        
        // Calculate when enough tokens will be available
        long tokensShort = tokensRequired - tokens;
        long waitSeconds = (tokensShort + refillRate - 1) / refillRate; // Ceiling
        Instant resetTime = Instant.now().plusSeconds(waitSeconds);
        
        return new RateLimitResult(false, tokens, resetTime);
    }
    
    /**
     * INTERVIEW CRITICAL: Time-based token refill
     */
    private void refill() {
        Instant now = Instant.now();
        Duration elapsed = Duration.between(lastRefillTime, now);
        
        if (elapsed.toMillis() > 0) {
            // Calculate tokens to add based on elapsed time
            long tokensToAdd = elapsed.toMillis() * refillRate / 1000;
            
            if (tokensToAdd > 0) {
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }
    }
    
    public synchronized long getAvailableTokens() {
        refill();
        return tokens;
    }
}

// ============================================
// SLIDING WINDOW COUNTER (Memory-Efficient)
// ============================================

public class SlidingWindowRateLimiter implements RateLimiter {
    private final Map<String, SlidingWindow> windows;
    private final long maxRequests;
    private final Duration windowSize;
    
    public SlidingWindowRateLimiter(long maxRequests, Duration windowSize) {
        this.windows = new ConcurrentHashMap<>();
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }
    
    @Override
    public RateLimitResult allowRequest(String key) {
        SlidingWindow window = windows.computeIfAbsent(
            key,
            k -> new SlidingWindow(maxRequests, windowSize)
        );
        
        return window.tryConsume();
    }
}

/**
 * INTERVIEW CRITICAL: Sliding window with weighted previous window
 */
public class SlidingWindow {
    private long previousWindowCount;
    private long currentWindowCount;
    private Instant currentWindowStart;
    private final Duration windowSize;
    private final long maxRequests;
    
    public SlidingWindow(long maxRequests, Duration windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
        this.currentWindowStart = Instant.now();
        this.previousWindowCount = 0;
        this.currentWindowCount = 0;
    }
    
    /**
     * INTERVIEW CRITICAL: Weighted sliding window calculation
     */
    public synchronized RateLimitResult tryConsume() {
        Instant now = Instant.now();
        
        // Check if we've moved to new window
        Duration elapsed = Duration.between(currentWindowStart, now);
        
        if (elapsed.compareTo(windowSize) >= 0) {
            // Move to new window
            previousWindowCount = currentWindowCount;
            currentWindowCount = 0;
            currentWindowStart = now;
            elapsed = Duration.ZERO;
        }
        
        // Calculate weighted request count
        double windowProgress = elapsed.toMillis() / (double) windowSize.toMillis();
        double weightedCount = 
            previousWindowCount * (1 - windowProgress) + currentWindowCount;
        
        if (weightedCount < maxRequests) {
            currentWindowCount++;
            
            long remaining = maxRequests - (long) Math.ceil(weightedCount);
            Instant resetTime = currentWindowStart.plus(windowSize);
            
            return new RateLimitResult(true, remaining, resetTime);
        }
        
        Instant resetTime = currentWindowStart.plus(windowSize);
        return new RateLimitResult(false, 0, resetTime);
    }
}

// ============================================
// DISTRIBUTED RATE LIMITER (Redis-based)
// ============================================

public class DistributedTokenBucketRateLimiter implements RateLimiter {
    private final RedisTemplate<String, String> redisTemplate;
    private final String luaScript;
    private final long capacity;
    private final long refillRate;
    
    public DistributedTokenBucketRateLimiter(
            RedisTemplate<String, String> redisTemplate,
            long capacity,
            long refillRate) {
        this.redisTemplate = redisTemplate;
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.luaScript = loadLuaScript();
    }
    
    /**
     * INTERVIEW CRITICAL: Atomic rate limit check with Redis Lua script
     */
    @Override
    public RateLimitResult allowRequest(String key, int tokensRequired) {
        long now = System.currentTimeMillis();
        
        try {
            List<Long> result = redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, List.class),
                Collections.singletonList(key),
                String.valueOf(capacity),
                String.valueOf(refillRate),
                String.valueOf(tokensRequired),
                String.valueOf(now)
            );
            
            boolean allowed = result.get(0) == 1;
            long remainingTokens = result.get(1);
            
            // Calculate reset time
            long tokensNeeded = capacity - remainingTokens;
            long secondsToFull = tokensNeeded / refillRate;
            Instant resetTime = Instant.now().plusSeconds(secondsToFull);
            
            return new RateLimitResult(allowed, remainingTokens, resetTime);
            
        } catch (Exception e) {
            // Fallback: Allow request on Redis failure (fail-open)
            log.error("Redis rate limiter failed, allowing request", e);
            return new RateLimitResult(true, capacity, Instant.now());
        }
    }
    
    private String loadLuaScript() {
        return """
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refill_rate = tonumber(ARGV[2])
            local requested = tonumber(ARGV[3])
            local now = tonumber(ARGV[4])
            
            -- Get current state
            local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
            local tokens = tonumber(bucket[1]) or capacity
            local last_refill = tonumber(bucket[2]) or now
            
            -- Refill tokens based on elapsed time
            local elapsed = now - last_refill
            local tokens_to_add = elapsed * refill_rate / 1000
            tokens = math.min(capacity, tokens + tokens_to_add)
            
            -- Try to consume tokens
            if tokens >= requested then
                tokens = tokens - requested
                redis.call('HMSET', key, 'tokens', tokens, 'last_refill', now)
                redis.call('EXPIRE', key, 3600)
                return {1, tokens}  -- Allow
            else
                return {0, tokens}  -- Deny
            end
            """;
    }
}

// ============================================
// RATE LIMITER FACTORY
// ============================================

public class RateLimiterFactory {
    public static RateLimiter create(RateLimiterConfig config) {
        switch (config.getAlgorithm()) {
            case TOKEN_BUCKET:
                return new TokenBucketRateLimiter(
                    config.getCapacity(),
                    config.getRefillRate()
                );
                
            case SLIDING_WINDOW:
                return new SlidingWindowRateLimiter(
                    config.getMaxRequests(),
                    config.getWindowSize()
                );
                
            case DISTRIBUTED_TOKEN_BUCKET:
                return new DistributedTokenBucketRateLimiter(
                    config.getRedisTemplate(),
                    config.getCapacity(),
                    config.getRefillRate()
                );
                
            default:
                throw new IllegalArgumentException(
                    "Unknown algorithm: " + config.getAlgorithm()
                );
        }
    }
}

// ============================================
// CONFIGURATION
// ============================================

public class RateLimiterConfig {
    private Algorithm algorithm;
    private long capacity;
    private long refillRate;
    private long maxRequests;
    private Duration windowSize;
    private RedisTemplate<String, String> redisTemplate;
    
    public enum Algorithm {
        TOKEN_BUCKET,
        LEAKY_BUCKET,
        SLIDING_WINDOW,
        SLIDING_WINDOW_LOG,
        DISTRIBUTED_TOKEN_BUCKET
    }
    
    // Builder pattern for configuration
    public static class Builder {
        private RateLimiterConfig config = new RateLimiterConfig();
        
        public Builder algorithm(Algorithm algorithm) {
            config.algorithm = algorithm;
            return this;
        }
        
        public Builder capacity(long capacity) {
            config.capacity = capacity;
            return this;
        }
        
        public Builder refillRate(long refillRate) {
            config.refillRate = refillRate;
            return this;
        }
        
        public Builder maxRequests(long maxRequests) {
            config.maxRequests = maxRequests;
            return this;
        }
        
        public Builder windowSize(Duration windowSize) {
            config.windowSize = windowSize;
            return this;
        }
        
        public Builder redisTemplate(RedisTemplate<String, String> redis) {
            config.redisTemplate = redis;
            return this;
        }
        
        public RateLimiterConfig build() {
            return config;
        }
    }
}

// ============================================
// USAGE EXAMPLE
// ============================================

public class RateLimitMiddleware {
    private final RateLimiter rateLimiter;
    
    public boolean handleRequest(HttpRequest request) {
        String key = extractKey(request); // user_id, IP, API_key
        
        RateLimitResult result = rateLimiter.allowRequest(key);
        
        if (result.isAllowed()) {
            // Add rate limit headers
            response.addHeader("X-RateLimit-Remaining", 
                String.valueOf(result.getRemainingQuota()));
            response.addHeader("X-RateLimit-Reset", 
                String.valueOf(result.getResetTime().getEpochSecond()));
            
            return true; // Allow request
        } else {
            // Rate limit exceeded
            response.setStatus(429); // Too Many Requests
            response.addHeader("Retry-After", 
                String.valueOf(Duration.between(
                    Instant.now(), 
                    result.getResetTime()
                ).getSeconds()));
            
            return false; // Deny request
        }
    }
    
    private String extractKey(HttpRequest request) {
        // Priority: API Key > User ID > IP Address
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null) {
            return "api_key:" + apiKey;
        }
        
        String userId = request.getAttribute("user_id");
        if (userId != null) {
            return "user:" + userId;
        }
        
        return "ip:" + request.getRemoteAddr();
    }
}
```

### 8. Thread Safety & Concurrency

**Token Bucket:**
- `synchronized` methods for thread safety
- Alternative: `ReentrantLock` for better performance
- `ConcurrentHashMap` for bucket registry

**Distributed:**
- Redis Lua scripts provide atomicity
- No race conditions across multiple servers
- Fallback to local rate limiting on Redis failure

**Memory Management:**
- Periodic cleanup of inactive buckets (TTL-based eviction)
- LRU cache for bucket storage

### 9. Top Interview Questions & Answers

**Q1: Token Bucket vs Leaky Bucket - which to use?**
**A:**
| Scenario | Use Token Bucket | Use Leaky Bucket |
|----------|------------------|------------------|
| Allow burst traffic | ✅ | ❌ |
| Smooth traffic | ❌ | ✅ |
| Simple implementation | ✅ | ❌ (needs queue) |
| Memory efficiency | ✅ | ❌ (stores requests) |
| API rate limiting | ✅ | ❌ |
| Traffic shaping | ❌ | ✅ |

**Q2: How do you handle clock skew in distributed systems?**
**A:**
```java
// Use Redis server time instead of application server time
String luaScript = """
    local now = redis.call('TIME')
    local timestamp = tonumber(now[1]) * 1000 + tonumber(now[2]) / 1000
    -- Use timestamp for calculations
    """;

// Benefits:
// 1. Single source of truth (Redis clock)
// 2. No drift between application servers
// 3. Consistent rate limiting across nodes
```

**Q3: What if Redis goes down?**
**A:**
```java
try {
    result = distributedRateLimiter.allowRequest(key);
} catch (RedisConnectionException e) {
    // Option 1: Fail-open (allow request, risky)
    return new RateLimitResult(true, capacity, Instant.now());
    
    // Option 2: Fail-closed (deny request, safer)
    return new RateLimitResult(false, 0, Instant.now());
    
    // Option 3: Fallback to local rate limiter
    return localRateLimiter.allowRequest(key);
    
    // Best: Circuit breaker pattern
    if (redisCircuitBreaker.isOpen()) {
        return localRateLimiter.allowRequest(key);
    }
}
```

**Q4: How do you test rate limiters?**
**A:**
```java
@Test
public void testTokenBucketBurst() {
    TokenBucket bucket = new TokenBucket(capacity = 100, refillRate = 10);
    
    // Burst: 100 requests instantly
    for (int i = 0; i < 100; i++) {
        assertTrue(bucket.tryConsume(1).isAllowed());
    }
    
    // 101st request should fail
    assertFalse(bucket.tryConsume(1).isAllowed());
    
    // Wait 1 second (10 tokens refilled)
    Thread.sleep(1000);
    
    // Next 10 requests should succeed
    for (int i = 0; i < 10; i++) {
        assertTrue(bucket.tryConsume(1).isAllowed());
    }
    
    // 11th fails
    assertFalse(bucket.tryConsume(1).isAllowed());
}

@Test
public void testConcurrentRequests() throws Exception {
    RateLimiter limiter = new TokenBucketRateLimiter(100, 10);
    
    int numThreads = 200;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    CountDownLatch latch = new CountDownLatch(numThreads);
    
    AtomicInteger allowed = new AtomicInteger(0);
    AtomicInteger denied = new AtomicInteger(0);
    
    for (int i = 0; i < numThreads; i++) {
        executor.submit(() -> {
            latch.countDown();
            latch.await();
            
            RateLimitResult result = limiter.allowRequest("test_key");
            if (result.isAllowed()) {
                allowed.incrementAndGet();
            } else {
                denied.incrementAndGet();
            }
        });
    }
    
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
    
    // Verify: Exactly 100 allowed, 100 denied
    assertEquals(100, allowed.get());
    assertEquals(100, denied.get());
}
```

**Q5: How do you handle different rate limit tiers (free vs premium)?**
**A:**
```java
public class TieredRateLimiter {
    private Map<String, RateLimiter> limiters;
    
    public TieredRateLimiter() {
        limiters = Map.of(
            "free", new TokenBucketRateLimiter(10, 10),    // 10/sec
            "basic", new TokenBucketRateLimiter(100, 100),  // 100/sec
            "premium", new TokenBucketRateLimiter(1000, 1000) // 1000/sec
        );
    }
    
    public RateLimitResult checkLimit(User user, String apiKey) {
        String tier = user.getSubscriptionTier(); // "free", "basic", "premium"
        RateLimiter limiter = limiters.get(tier);
        
        return limiter.allowRequest("user:" + user.getId());
    }
}
```

**Q6: What metrics should you track?**
**A:**
```java
Metrics:
1. Request Rate: Requests per second (per key, per tier)
2. Throttle Rate: % requests denied
3. Burst Utilization: Max tokens consumed in 1 second
4. Average Remaining Quota: Mean tokens left across all keys
5. Rate Limit Errors: Count of 429 responses
6. Latency: P50/P95/P99 overhead of rate limit check

Alerts:
- Throttle rate > 10% → Consider increasing limits
- Redis latency > 10ms → Scale Redis
- Burst utilization > 80% → Users hitting burst cap frequently
```

**Q7: How do you handle rate limit resets?**
**A:**
```java
// Include reset time in response headers
response.addHeader("X-RateLimit-Limit", "100");
response.addHeader("X-RateLimit-Remaining", "45");
response.addHeader("X-RateLimit-Reset", "1609459200"); // Unix timestamp

// Client can calculate wait time
long resetTime = Long.parseLong(response.getHeader("X-RateLimit-Reset"));
long waitSeconds = resetTime - Instant.now().getEpochSecond();

if (waitSeconds > 0) {
    Thread.sleep(waitSeconds * 1000);
    retry();
}
```

**Q8: How do you implement exponential backoff?**
**A:**
```java
public class RateLimitClient {
    private static final int MAX_RETRIES = 5;
    
    public Response makeRequest(Request request) {
        int attempt = 0;
        
        while (attempt < MAX_RETRIES) {
            Response response = httpClient.execute(request);
            
            if (response.getStatus() != 429) {
                return response; // Success or non-rate-limit error
            }
            
            // Rate limited - exponential backoff
            long retryAfter = Long.parseLong(
                response.getHeader("Retry-After")
            );
            
            long backoffMs = Math.min(
                (long) Math.pow(2, attempt) * 1000, // Exponential
                retryAfter * 1000                   // Server suggestion
            );
            
            // Add jitter to avoid thundering herd
            long jitter = ThreadLocalRandom.current().nextLong(0, 1000);
            
            Thread.sleep(backoffMs + jitter);
            attempt++;
        }
        
        throw new RateLimitExceededException("Max retries exceeded");
    }
}
```

**Q9: How would you scale to billions of requests?**
**A:**
```
Horizontal Scaling:
1. Partition keys across Redis cluster (consistent hashing)
2. Each partition handles subset of keys independently
3. No cross-partition coordination needed

Optimization:
1. Local cache (reduce Redis calls by 90%)
   - Cache rate limit state for 100ms
   - Acceptable 10% drift for most use cases
2. Batch requests (check multiple keys in single Redis call)
3. Bloom filter (skip check for new users)

Architecture:
- Edge rate limiting (at CDN/load balancer)
- Service-level rate limiting (per microservice)
- Global rate limiting (Redis cluster)
```

**Q10: What's the Redis memory usage?**
**A:**
```
Per key storage:
- Token bucket: 32 bytes (tokens + last_refill)
- Sliding window: 16 bytes (2 counters)

10M keys with token bucket:
- Memory: 10M * 32 bytes = 320 MB
- With Redis overhead (~50%): ~480 MB

Optimization:
1. TTL-based eviction (expire inactive keys after 1 hour)
2. Sampling (check only X% of requests, scale up limits)
3. Approximate counting (HyperLogLog for unique IPs)
```

### 10. Extensions & Variations

1. **Geographic Rate Limiting**: Different limits per region
2. **Hierarchical Limits**: User + IP + API endpoint limits
3. **Adaptive Rate Limiting**: Auto-adjust based on load
4. **Quota Management**: Monthly quotas with rollover
5. **Rate Limit Allowlisting**: Bypass limits for trusted IPs

### 11. Testing Strategy

**Unit Tests:**
- Token refill calculation
- Burst handling
- Concurrent requests (race conditions)
- Time-based window transitions

**Integration Tests:**
- Redis Lua script execution
- Distributed rate limiting across nodes
- Fallback to local limiter on Redis failure

**Load Tests:**
- 100K RPS sustained load
- Burst scenario (10K requests in 1 second)
- Redis cluster failover

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Storing individual request timestamps (memory explosion)
✅ **Do**: Use counters (sliding window counter)

❌ **Avoid**: Synchronous Redis calls in critical path
✅ **Do**: Async with fallback to local limiter

❌ **Avoid**: Global lock for all keys
✅ **Do**: Per-key locks (fine-grained)

❌ **Avoid**: Fixed window (allows 2x burst at boundary)
✅ **Do**: Sliding window or token bucket

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Token Bucket Check | O(1) | O(1) per key | Refill + consume |
| Sliding Window Check | O(1) | O(1) per key | 2 counters only |
| Distributed Check | O(1) | O(1) per key | Redis Lua script |
| Cleanup (inactive keys) | O(k) | O(k) | k = number of keys |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Algorithm Choice** | 30% | Understands token bucket, leaky bucket, sliding window trade-offs |
| **Thread Safety** | 25% | Synchronized refill, atomic operations, CAS |
| **Distributed Design** | 20% | Redis Lua scripts, clock skew handling, fallback strategies |
| **Accuracy** | 15% | Precise time calculations, no off-by-one errors |
| **Real-world Awareness** | 10% | Burst handling, retry logic, monitoring |

**Red Flags:**
- No thread safety (race conditions)
- Fixed window algorithm (allows burst)
- No distributed consideration
- Storing all request timestamps (memory leak)
- No fallback on Redis failure

---
