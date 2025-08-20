# System Design Deep Dive: Caching, Async Processing & Observability / Monitoring

## Table of Contents
1. CACHING STRATEGIES  
   1.1 Fundamental Patterns  
   1.2 Cache Placement Strategies  
   1.3 Eviction Policies & Algorithms  
   1.4 Invalidation Strategies  
   1.5 Consistency Challenges & Solutions  
   1.6 Distributed Caching (Redis, ZippyDB)  
   1.7 Stampede / Thundering Herd Prevention  
   1.8 Hot Key Problem Mitigation  
2. ASYNCHRONOUS PROCESSING  
   2.1 Broker Architectures (Queues vs Topics)  
   2.2 Kafka Deep Dive  
   2.3 Delivery Semantics  
   2.4 Dead Letter Queues (DLQ)  
   2.5 Redis-based Messaging  
   2.6 Real-time Pub/Sub Systems Comparison  
   2.7 Event-driven Architecture Patterns (ES, CQRS, Sagas)  
   2.8 Schema Evolution Strategies  
   2.9 Backpressure Handling  
3. OBSERVABILITY & MONITORING  
   3.1 Core Concepts & Pillars  
   3.2 Metrics (RED/USE/Four Golden)  
   3.3 Tracing (Context Propagation)  
   3.4 Logging (Structure, Correlation)  
   3.5 Alerting, SLOs, Error Budgets  
   3.6 Patterns, Anti-Patterns & Cost Control  
   3.7 Scaling the Observability Stack  
4. Global Pitfalls & Best Practices Index  
5. Resource Index

---

## 1. CACHING STRATEGIES

### 1.1 Fundamental Patterns

| Pattern | Read Miss Handling | Write Path | Consistency | Latency (Read) | Typical Use |
|---------|--------------------|------------|-------------|----------------|-------------|
| Cache-aside | App loads + populates | DB then optionally invalidate/update | Eventual | First miss slower | General purpose |
| Read-through | Cache loader/provider fetches | Same as cache-aside but abstracted | Eventual | Simpler app code | Unified data facade |
| Write-through | Write hits cache & store synchronously | Atomic (cache + DB) | Strong (on success) | Fast (fresh) | Frequently read after write |
| Write-behind (Write-back) | Cache first, async flush | Buffered / batched | Eventual, risk on crash | Fastest | High write throughput, tolerant delay |
| Refresh-ahead | Background refresh before TTL | N/A | Eventual (lower staleness window) | Stable (avoid miss spike) | Hot keys with predictable access |

ASCII flow (Cache-aside):
```
GET -> Cache ? Hit -> Return
              Miss -> DB -> Cache.put -> Return
```

Spring Boot Cache-aside example (Caffeine or Redis):
```java
@Service
public class ProductService {
  private final ProductRepository repo;
  private final Cache cache;
  public ProductService(ProductRepository repo, CacheManager cm) {
    this.repo = repo;
    this.cache = cm.getCache("product");
  }
  public Product get(Long id) {
    Product p = cache.get(id, Product.class);
    if (p != null) return p;
    p = repo.findById(id).orElseThrow();
    cache.put(id, p);
    return p;
  }
  @Transactional
  public Product update(Long id, ProductUpdate dto) {
    Product p = repo.findById(id).orElseThrow();
    p.apply(dto);
    Product saved = repo.save(p);
    cache.put(id, saved); // write-through style update
    return saved;
  }
}
```

Write-behind (buffer + scheduled flush):
```java
@Component
public class WriteBehindBuffer {
  private final BlockingQueue<Product> queue = new LinkedBlockingQueue<>(10_000);
  private final ProductRepository repo;
  public WriteBehindBuffer(ProductRepository repo, @Value("${flush.batch:200}") int batchSize) {
    this.repo = repo;
    Executors.newSingleThreadScheduledExecutor()
      .scheduleWithFixedDelay(() -> flush(batchSize), 1, 1, TimeUnit.SECONDS);
  }
  public void enqueue(Product p){ queue.offer(p); }
  private void flush(int batchSize){
    List<Product> batch = new ArrayList<>(batchSize);
    queue.drainTo(batch, batchSize);
    if (!batch.isEmpty()) repo.saveAll(batch);
  }
}
```

Refresh-ahead (Caffeine refresh):
```java
@Bean CacheLoader<Long, Product> productLoader(ProductRepository r){
  return r::findById;
}
@Bean public LoadingCache<Long, Product> productCache(CacheLoader<Long, Product> loader){
  return Caffeine.newBuilder()
    .maximumSize(20_000)
    .expireAfterWrite(Duration.ofMinutes(15))
    .refreshAfterWrite(Duration.ofMinutes(12))
    .build(loader);
}
```

Pitfalls:
- Write-behind data loss on crash → add durable journal.
- Read-through loaders doing N+1 queries.
- Cache-aside stampede on hot key expirations.

Best Practices:
- Guard hot keys with mutex or single-flight.
- Instrument hit ratio, eviction count, load time.
- Separate critical vs non-critical caches (different TTL / eviction).

---

### 1.2 Cache Placement Strategies

| Layer | Scope | Latency | Invalidation Difficulty | Example |
|-------|-------|---------|-------------------------|---------|
| Client (browser/app) | User device | Lowest | High (distributed) | HTTP Cache-Control |
| CDN / Edge | Global edge PoP | Very low | Medium | CloudFront, Fastly |
| API Gateway | Pre-service | Low | Medium | Gateway response cache |
| Application In-memory | Instance-local | Low | Low (per instance) | Caffeine |
| Distributed (Redis) | Service cluster | Moderate (network) | Medium | Redis Cluster |
| DB Internal | Buffer pool | Moderate | Internal | Postgres shared_buffers |

ASCII stack:
```
User -> Browser Cache -> CDN -> Gateway Cache -> App Local Cache -> Distributed Cache -> DB
```

Guidelines:
- Put immutable assets at Edge (hash-versioned).
- Use application local cache for high QPS, small objects.
- Promote to Redis when needing sharing or > memory footprint per node.

---

### 1.3 Eviction Policies & Algorithms

| Policy | Core Idea | Strength | Weakness | Implementation Complexity | Workload Fit |
|--------|-----------|----------|----------|---------------------------|--------------|
| LRU | Remove least recently used | Temporal locality | Scan pollution | Low | Sessions, recency heavy |
| LFU | Remove least frequently used | Stable popularity | Slow adaptation | Medium | Stable hot sets |
| FIFO | First in first out | Simple | Ignores recency/frequency | Very low | Streaming pipeline buffers |
| Random | Evict random entry | Constant time | Non-optimal hit rate | Very low | Extreme simplicity |
| W-TinyLFU | Admission via frequency sketch + segmented LRU | High hit ratio | More CPU/memory | Higher | Mixed patterns / skew |

TinyLFU concept:
```
[ Window LRU ] -> Admission test (Count-Min Sketch) -> [ Probation LRU | Protected LRU ]
```

Implementation trade-offs:
- High cardinality + churn -> TinyLFU reduces churn misses.
- Frequent scans -> Segmented LRU or 2Q to isolate cold scan pages.

Metrics:
- Hit Ratio (overall, per region).
- Eviction Rate spikes correlated with GC / memory pressure.
- Load latency (cache -> loader).

---

### 1.4 Invalidation Strategies

| Strategy | Mechanism | Freshness | Complexity | Risk |
|----------|-----------|-----------|------------|------|
| TTL | Time-based expiry | Probabilistic | Low | Stale window |
| Explicit (Write Invalidate) | On update evict or update | High | Medium | Missing path causes staleness |
| Pub/Sub Invalidate | Broadcast changes | High | Higher infra | Message loss |
| Versioned Keys | Key suffix version bump | High | Medium | Accumulating stale entries |
| Soft TTL + Async Refresh | Serve stale + background refresh | High perceived | Medium | Serving brief stale |

Stale-while-revalidate pattern:
```java
class SWRCache {
  record Entry(Object value, long softExpire, long hardExpire){}
  private final ConcurrentHashMap<String, Entry> map = new ConcurrentHashMap<>();
  Object get(String k, Supplier<Object> loader){
    var e = map.get(k);
    long now = System.currentTimeMillis();
    if (e == null || now > e.hardExpire){
      return loadAndStore(k, loader, now);
    }
    if (now > e.softExpire){
      CompletableFuture.runAsync(() -> loadAndStore(k, loader, now));
    }
    return e.value();
  }
  private Object loadAndStore(String k, Supplier<Object> loader, long now){
    Object v = loader.get();
    map.put(k, new Entry(v, now+5_000, now+60_000));
    return v;
  }
}
```

---

### 1.5 Consistency Challenges & Solutions

Challenges:
- Stale reads after write.
- Race: Update DB then reader hits old cache entry.
- Distributed invalidation latency.

Patterns:
| Issue | Solution | Notes |
|-------|----------|-------|
| Read-after-write | Session stickiness / write-through | Lower user confusion |
| Stale hot key post-update | Delete-first then write DB + repopulate | Window risk—use double-checked load |
| Stampede on expire | Early refresh, mutex, jitter TTL | Adds variance reduces herd |
| Multi-region diverge | Region-local caches + write fanout | Accept bounded staleness |

Strong(ish) approach (write-through + version):
```java
@CachePut(value="product", key="#p.id")
@Transactional
public Product update(Product p){
  return repo.save(p); // Cache updated atomically via annotation
}
```

Event-driven invalidation:
- On DB change emit domain event -> consumers invalidate across cluster (pub/sub, Kafka + consumer updating cache map).

---

### 1.6 Distributed Caching

#### Redis Architecture & Data Structures
- Single-threaded event loop + I/O multiplexing.
- Data structures: String, Hash, List, Set, ZSet, HyperLogLog, Bitmap, Stream, Geo.
- Memory allocator: jemalloc (fragmentation tracking).

#### Persistence
| Mode | Mechanism | Durability | Latency Impact | Use |
|------|-----------|------------|----------------|-----|
| RDB | Snapshot (fork & dump) | Periodic | Fork time copy | Disaster recovery |
| AOF | Append write log | Configurable (every write / sec) | Higher fsync cost | Higher durability |
| Hybrid | AOF + RDB rewrite | Balanced | Medium | Production default often |

Config snippet:
```
save 900 1
appendonly yes
appendfsync everysec
```

#### Redis Cluster (Sharding)
- 16,384 hash slots -> nodes own slot ranges.
- Key slot = CRC16(key)%16384.
- MOVED / ASK redirection during reshard.
Mitigation for multi-key ops: hash tags {user:123}:profile to co-locate.

#### High Availability (Sentinel)
Sentinel monitors masters:
```
S1,S2,S3 -> monitor master -> quorum -> failover -> promote replica -> broadcast new config
```

#### ZippyDB (Facebook)
- RocksDB-based, distributed key/value store.
Features: Paxos-like replication, tail latency optimizations, failure injection tested.
Use case: metadata & config with multi-RDC replication.

Comparison:
| Feature | Redis | ZippyDB |
|---------|-------|---------|
| Persistence | RDB/AOF | RocksDB LSM |
| Consistency | Single node strong, cluster eventual | Strong (replication) |
| Data Types | Rich in-memory | KV |
| Latency | Microseconds (memory) | Higher (disk) |
| Use | Cache, ephemeral data | Metadata store requiring durability |

---

### 1.7 Stampede / Thundering Herd Prevention

| Technique | Description | Pros | Cons |
|-----------|-------------|------|------|
| Mutex / Single-flight | Lock around loader | Strong | Lock contention |
| Probabilistic Early Expiration | Random early refresh | Reduce sync expiry spikes | Possible extra load |
| Jittered TTL | Add random delta to TTL | Spreads expiration | Harder debugging |
| Request Coalescing | Dedup simultaneous loads | Efficient use of backend | Coordination complexity |
| Negative Caching | Cache 'not found' briefly | Avoid repeated misses | Must keep TTL very low |

Probabilistic early refresh (pseudo):
```
if (now > expire - (ttl * rand(0,0.1))) refresh()
```

---

### 1.8 Hot Key Mitigation

Symptoms:
- Disproportionate CPU / network usage for one key.
- Redis monitor shows >X% total ops single key.

Mitigations:
| Strategy | Method |
|----------|--------|
| Key replication | Duplicate key into multiple suffix variants & random pick |
| Local per-instance cache | Short-lived local memory layer |
| Lease caching | Serve slightly stale while refreshing asynchronously |
| Bloom/negative presence | Prevent repetitive misses |
| Write fanout throttle | Batch writes & push deltas |

Key replication (shard spray):
```java
String chooseReplicaKey(String base, int replicas){
  int r = ThreadLocalRandom.current().nextInt(replicas);
  return base + "::r" + r;
}
```

Monitoring:
- Track top-K keys by ops (Redis keyspace stats, custom Lua).
- Alert when key ops / total ops > threshold (e.g., 15%).

---

## 2. ASYNCHRONOUS PROCESSING

### 2.1 Broker Architectures

| Model | Entities | Ordering Scope | Fanout | Durability | Examples |
|-------|---------|----------------|--------|------------|----------|
| Queue | Producer -> Queue -> Consumer(s) | None / single queue | Limited (1 group) | Per message ack | SQS, Rabbit (classic queue) |
| Topic (log) | Append-only partitions | Partition | High (multi groups) | Replicated log | Kafka, Pulsar |
| Exchange (AMQP) | Exchange routes to queues | Depends | Flexible (routing keys) | Queue acks | RabbitMQ |
| Stream (Redis/Kafka) | Offset-based read | Partition/Shard | Consumer groups | Persisted (XADD) | Kafka, Redis Streams |

Message routing patterns:
- Direct (routing key match)
- Fanout (broadcast)
- Topic (pattern match)
- Headers (attribute-based)
- Consistent hash (affinity)

---

### 2.2 Kafka Deep Dive

Architecture:
```
Producers -> Brokers (Topic -> Partitions) -> Consumer Groups
                | Replicas (ISR)
Zookeeper/KRaft (metadata quorum)
```

Key Elements:
- Partition: append-only log; ordering guaranteed within partition.
- Replication Factor (RF): number of replicas; min.insync.replicas for durability.
- ISR: In-Sync Replicas set; leader + followers caught up.

Offset management:
- Consumers commit offset (sync/async) to __consumer_offsets.
- At-least-once: process then commit.
- At-most-once: commit before process.

Exactly-once semantics (EOS):
| Component | Config |
|-----------|--------|
| Idempotent Producer | enable.idempotence=true |
| Transactions | transactional.id |
| Consumer isolation | isolation.level=read_committed |

Spring Kafka transactional example:
```java
@Bean
public KafkaTemplate<String,String> kafkaTemplate(ProducerFactory<String,String> pf){
  KafkaTemplate<String,String> kt = new KafkaTemplate<>(pf);
  kt.setTransactionIdPrefix("tx-orders-");
  return kt;
}
@Transactional
public void publish(OrderEvent evt){
  kafkaTemplate.executeInTransaction(t -> {
     t.send("orders", evt.orderId(), json.serialize(evt));
     return true;
  });
}
```

Kafka Streams:
- DSL for stateful processing (KTable, KStream).
- RocksDB state stores + changelog topics.

KSQL:
- SQL-like continuous queries over topics.

Kafka Connect:
- Source & Sink connectors (schema registry integration).

---

### 2.3 Delivery Semantics

| Semantics | Description | Implementation Strategy | Tradeoffs |
|-----------|-------------|-------------------------|-----------|
| At-most-once | No retry after failure | Commit before process | Data loss possible |
| At-least-once | Retry until ack | Process → commit | Duplicates possible (idempotency needed) |
| Exactly-once | No loss, no duplicates (logical) | Transactions + idempotent writes | Complexity, overhead |

Idempotent processing pattern:
```java
boolean already = processedRepo.existsByMessageId(event.id());
if(already) return;
process(event);
processedRepo.save(new ProcessedMessage(event.id(), Instant.now()));
```

---

### 2.4 Dead Letter Queues (DLQ)

Purpose: isolate poison messages (repeatedly failing) for inspection.

Flow:
```
Main Topic -> Consumer -> Retry Logic -> (exceeded max attempts) -> DLQ Topic
```

Strategies:
| Retry Type | Mechanism | Use |
|------------|----------|-----|
| Immediate w/ backoff | Exponential + jitter | transient failures |
| Delayed Queue | separate topic with delay semantics | spaced retries |
| DLQ | final resting place after attempts | manual or automated remediation |

Monitoring:
- retry_count distribution
- DLQ rate
- Age in DLQ (stale backlog)

Spring example (custom header):
```java
@KafkaListener(topics="orders")
public void consume(ConsumerRecord<String,String> rec){
  int attempts = Optional.ofNullable(rec.headers().lastHeader("x-attempts"))
      .map(h -> Integer.parseInt(new String(h.value(), StandardCharsets.UTF_8)))
      .orElse(0);
  try {
    process(rec.value());
  } catch (Exception ex){
    if (attempts < 5){
      // re-publish with attempt increment
    } else {
      // send to DLQ
    }
  }
}
```

---

### 2.5 Redis-based Messaging

| Mechanism | Ordering | Persistence | Consumer Model | Use |
|-----------|----------|------------|----------------|-----|
| Pub/Sub | Best effort (fire & forget) | None | All subscribers receive | Ephemeral notifications |
| Lists (RPUSH/LPOP) | FIFO | Key in memory | Single worker / manual groups | Simple queue |
| Streams (XADD/XREAD) | Strict per stream | Yes (log) | Consumer groups (ack) | Durable queueing / at-least-once |
| Keyspace notifications | Event triggers | N/A | Subscriptions | Cache invalidation |

Redis Streams:
- XGROUP create group
- XREADGROUP consumes pending + new
- Pending entries list (XPENDING)
- Acknowledge via XACK

Throughput considerations:
- Keep entries trimmed (XTRIM) if retention not needed.
- Use pipeline or batch for high throughput.

---

### 2.6 Real-time Pub/Sub Systems Comparison

| System | Model | Ordering Scope | Latency | Durability | Scale Pattern | Notes |
|--------|-------|----------------|--------|------------|---------------|-------|
| SNS + SQS | Fanout + queue | Per SQS queue | Low | SQS persistent | Horizontal | Managed, simple |
| Google Pub/Sub | Topic-subscription ack | Per ordering key | Low | Persistent replicated | Auto-scaling | Ordering key required |
| RabbitMQ | Exchange -> Queues | Queue | Low | Disk (if durable) | Vertical + clusters | Complex topology features |
| NATS | Subject based | None (by subject) | Ultra low | JetStream optional | Cluster mesh | Lightweight |
| Kafka | Log partitions | Partition | Low (ms) | Replicated log | Horizontal partitions | High throughput |

Selection:
- High throughput + replay → Kafka
- Simplicity + serverless → SNS/SQS
- Ultra low latency ephemeral → NATS
- Complex routing patterns → RabbitMQ

---

### 2.7 Event-driven Architecture Patterns

| Pattern | Description | Pros | Cons |
|---------|-------------|------|------|
| Event Sourcing | Store immutable events, derive state | Full audit, temporal queries | Rebuild complexity, versioning |
| CQRS | Separate command & query models | Optimized read scaling | Eventual consistency |
| Sagas | Distributed transaction with compensations | Orchestration of multi-service workflows | Complexity, failure orchestration |
| Outbox | Atomic DB + event publish indirectly | Prevent dual-write inconsistency | Polling overhead |

Outbox table example:
```sql
CREATE TABLE outbox (
  id UUID PRIMARY KEY,
  aggregate_type TEXT,
  aggregate_id TEXT,
  event_type TEXT,
  payload JSONB,
  created_at TIMESTAMPTZ DEFAULT now(),
  processed BOOLEAN DEFAULT false
);
```

Saga flow ASCII:
```
CreateOrder -> ReserveInventory -> ProcessPayment -> ShipOrder
   | fail
CancelOrder <- ReleaseInventory <- RefundPayment
```

---

### 2.8 Schema Evolution Strategies

| Format | Forwards Compatible | Backwards Compatible | Tooling |
|--------|---------------------|----------------------|---------|
| Avro (with defaults) | Yes | Yes | Schema Registry |
| Protobuf (add fields) | Yes | Yes | Descriptors |
| JSON (loose) | Depends | Depends | Manual validation |
| Thrift | Similar to Proto | Similar | IDL |

Principles:
- Never reuse removed field identifiers (protobuf).
- Supply defaults for new required fields (Avro).
- Gate incompatible changes via CI schema registry check.

---

### 2.9 Backpressure Handling

Signals:
- Queue backlog length derivative.
- Lag (Kafka consumer lag).
- Stream watermark delays.
- Thread pool saturation (active vs queued ratio).

Mechanisms:
| Mechanism | Implementation | Notes |
|-----------|----------------|-------|
| Leaky Bucket / Token Bucket | Fixed issue rate | Rate smoothing |
| Reactive Streams | Publisher/Subscriber request(n) | Propagates demand |
| Circuit Breaker + Shed | Reject > threshold | Protect tail latency |
| Load Shedding (priority) | Drop low-priority tasks | Preserve core SLIs |
| Dynamic Batch Size | Shrink on latency spike | Adapt throughput/latency |

Example: Reactor backpressure (Flux):
```java
Flux.interval(Duration.ofMillis(1))
  .onBackpressureDrop()
  .publishOn(Schedulers.boundedElastic(), 256)
  .subscribe(this::process);
```

---

## 3. OBSERVABILITY & MONITORING

### 3.1 Core Concepts

| Pillar | Purpose | Key Outputs |
|--------|---------|-------------|
| Metrics | Quantitative time-series | SLIs, alerts |
| Traces | Distributed request path | Latency breakdown |
| Logs | Discrete event context | Root cause, audit |
| Profiles | Resource usage detail | Performance tuning |
| Events (audit/config) | Change tracking | Correlate incidents |

Observability vs Monitoring:
- Monitoring = known failure patterns.
- Observability = ability to ask new questions (high cardinality exploration, exemplars).

---

### 3.2 Metrics

RED (for services):
- Rate (RPS)
- Errors (error rate)
- Duration (latency percentiles)

USE (for resources):
- Utilization (% time busy)
- Saturation (queued work)
- Errors (hardware or retry faults)

Latency histogram configuration example (Micrometer / Prometheus):
```yaml
management:
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
      sla:
        http.server.requests: 50ms,100ms,200ms,400ms,800ms
      percentiles:
        http.server.requests: 0.5,0.95,0.99
```

Budget allocation:
```
Total p99 target 300ms:
Gateway 30 / Auth 20 / App Logic 120 / DB 100 / Cache 10 / Slack 20
```

---

### 3.3 Tracing

Context propagation:
- trace_id, span_id, parent_id carried via W3C Traceparent header.

Spring Boot OTel:
```properties
management.tracing.sampling.probability=0.2
```

Tail sampling (Collector):
```yaml
policies:
  - name: error-or-slow
    type: span
    span:
      conditions:
        - attributes["http.status_code"] >= 500
        - latency > 300ms
```

Span naming best practices:
| Layer | Pattern |
|-------|---------|
| HTTP Server | HTTP {METHOD} {route} |
| DB | DB {operation} {table} |
| External Call | CALL {service}.{endpoint} |
| Custom | domain.action |

---

### 3.4 Logging

Principles:
- Structured JSON.
- Include trace_id, span_id.
- Avoid PII unmasked.
- Use severity levels consistently.

Example Log (JSON):
```json
{"timestamp":"2025-01-01T10:00:00Z","level":"INFO","service":"orders","trace_id":"abc123","span_id":"def456","event":"order.created","order_id":"o-789","amount":129.99}
```

Sampling:
- Keep all WARN/ERROR.
- Sample INFO at rate (e.g., 1:50) if high volume.

Correlation:
- Use log exemplars linking to traces (Prometheus exemplars).

---

### 3.5 Alerting & SLOs

| Component | SLI | SLO | Alert Trigger |
|-----------|-----|-----|---------------|
| API | Availability (success %) | 99.9% / 30d | 2h predicted burn of 30d budget |
| API | p99 Latency | < 400ms | Fast burn (2x baseline) |
| Kafka Consumer | Lag | < 5,000 msgs | Lag derivative spike |
| Cache | Hit Rate | > 90% | <85% sustained 10m |
| DB | Replica Lag | < 2s | >5s 5m |

Multi-window multi-burn-rate:
- Fast window (5m) high multiplier
- Slow window (1h) lower multiplier.

---

### 3.6 Patterns, Anti-Patterns & Cost

| Area | Anti-Pattern | Impact | Fix |
|------|--------------|--------|-----|
| Metrics | High-cardinality user_id label | Memory explosion | Aggregate + exemplars |
| Logs | Unbounded debug logs | Storage cost | Dynamic log level + sampling |
| Traces | 100% sampling in peak | Cost & perf overhead | Adaptive / tail sampling |
| Alerts | Threshold noise | Pager fatigue | SLO-based alerts |
| Dashboards | Wall of charts | Cognitive overload | Narrative-driven panels |

Cost controls:
- Metric retention tiers (1m granularity drop → 5m).
- Downsample old data.
- Log retention policies & cold storage.

---

### 3.7 Scaling Observability Stack

Reference Pipeline:
```
Apps -> OTel SDK
     -> OTel Collector (batch/export)
        -> Metrics: Prometheus / Mimir
        -> Traces: Tempo / Jaeger
        -> Logs: Loki
        -> Alerts: Alertmanager
```

Scaling Strategies:
| Layer | Bottleneck | Scaling |
|-------|-----------|---------|
| Collector | CPU / queue backpressure | Horizontal shards |
| Metrics Store | TS churn | Remote write + sharding |
| Logs | Ingest throughput | Partition by time + label |
| Traces | Storage size | Tail sampling + TTL |
| Dashboards | Query latency | Caching / precompute |

Backpressure indicators:
- Collector export queue size
- Remote write retries
- Tail-latency queries > budget

---

## 4. Global Pitfalls & Best Practices Index

| Domain | Pitfall | Best Practice |
|--------|---------|---------------|
| Cache | Synchronized expirations | TTL jitter |
| Cache | Write-behind data loss | Persist buffer / ack flush |
| Messaging | Duplicate processing | Idempotent consumer store |
| Messaging | Growing DLQ unnoticed | DLQ age + rate alerts |
| Kafka | Large batches w/ high latency | Tune linger.ms & batch.size per SLA |
| Schemas | Breaking change deploy | Registry compatibility check in CI |
| Tracing | Missing async spans | Instrument async boundaries |
| Logging | PII leakage | Classification & redaction |
| Metrics | Silent SLO burn | Multi-window burn alerts |
| Backpressure | Infinite buffering | Bounded queues + shedding |
| Hot Key | Single shard overload | Replicate / shuffle shard |
| Saga | Orphan compensations | Timeout watchdog + audit log |
| EOS | Improper commit ordering | Commit offsets inside txn |
| Observability Cost | Retain raw high-card logs | Lifecycle tiering |

---

## 5. Resource Index

### Caching
- Redis Docs: https://redis.io/docs/latest/
- Caffeine (TinyLFU): https://github.com/ben-manes/caffeine
- Cache Stampede: https://blog.cloudflare.com/cache-stampede/
- Google Guava Cache Guide: https://github.com/google/guava/wiki/CachesExplained

### Distributed Storage & Consistency
- Dynamo Paper: https://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf
- Spanner Paper: https://research.google/pubs/pub39966/
- Jepsen Analyses: https://jepsen.io/analyses
- CRDT Primer: https://crdt.tech/

### Messaging & Streaming
- Kafka Docs: https://kafka.apache.org/documentation/
- Kafka EOS Blog: https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/
- Event Sourcing: https://martinfowler.com/eaaDev/EventSourcing.html
- CQRS: https://martinfowler.com/bliki/CQRS.html
- Outbox Pattern: https://microservices.io/patterns/data/transactional-outbox.html

### Observability
- OpenTelemetry: https://opentelemetry.io/docs/
- Prometheus Histograms: https://prometheus.io/docs/practices/histograms/
- Google SRE Workbook: https://sre.google/workbook/
- Tail Latency Talk (Gil Tene): https://www.infoq.com/presentations/latency-response-time/
- Honeycomb Sampling: https://www.honeycomb.io/blog/tag/sampling/
- RED/USE Patterns: https://www.weave.works/blog/the-red-method-key-metrics-for-microservices-architecture/
- Loki LogQL: https://grafana.com/docs/loki/latest/logql/

### Backpressure & Resilience
- Reactive Manifesto: https://www.reactivemanifesto.org/
- Resilience4j: https://resilience4j.readme.io/
- AWS Backoff & Jitter: https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/

### Schema Evolution
- Confluent Schema Compatibility: https://docs.confluent.io/platform/current/schema-registry/avro.html
- Protobuf Guidelines: https://protobuf.dev/programming-guides/proto3/

### Additional
- Designing Data-Intensive Apps: https://dataintensive.net/
- Database Internals: (O’Reilly reference details)
- TinyLFU Paper: https://arxiv.org/abs/1512.00727

---

## Quick Reference Tables

### Latency Budget Template
| Layer | Budget ms |
|-------|-----------|
| Edge/CDN | 20 |
| Gateway | 30 |
| Auth | 25 |
| App Logic | 120 |
| Cache/DB | 150 |
| Slack | 35 |

### Cache Metrics Dashboard Checklist
| Metric | Threshold |
|--------|-----------|
| Hit Ratio | >90% |
| Avg Load Time | <20ms |
| Evictions Spike | <2x baseline |
| Stampede Incidents | 0 sustained |
| Hot Key Ratio | <15% single key |

### Kafka Health
| Metric | Healthy |
|--------|---------|
| Lag Growth Derivative | Stable/negative |
| Under-replicated Partitions | 0 |
| ISR Shrink Events | Occasional only |
| Produce Error Rate | <0.1% |
| Consumer Rebalance Frequency | Low consistent |

---

## Appendix: Minimal Production Readiness Checklist

| Area | Item | Status |
|------|------|--------|
| Cache | Hit ratio alerting | |
| Cache | Stampede control present | |
| Messaging | DLQ monitored | |
| Messaging | Idempotency keys documented | |
| Tracing | >80% critical paths instrumented | |
| Metrics | SLO dashboards published | |
| Logs | PII redaction implemented | |
| Schema | Compatibility CI gate | |
| Backpressure | Bounded queues & shedding | |
| Hot Keys | Detection alert | |
| Disaster | Runbook for cache flush | |
| Cost | Observability retention policy | |

---

End of document.
