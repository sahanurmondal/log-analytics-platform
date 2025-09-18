# Real-Time Stock Price Maximum in Sliding Window

## 📋 **Navigation**
- **Previous Question**: [Q3: E-Commerce Portal Design](./q003_e_commerce_portal_design.md)
- **Next Question**: [Q5: Library Management System](./q005_library_management_system.md)
- **Main Menu**: [System Design Questions](../README.md)

---

## 📝 **Problem Statement**

**Company**: Salesforce  
**Difficulty**: Hard  
**Question**: Maximum stock price in last t minutes

Design a real-time system that receives stock price updates and efficiently returns the maximum stock price seen within a sliding time window. Focus on optimization, design principles, CI/CD pipeline considerations, and distributed system challenges.

---

## 1. 🎯 **PROBLEM UNDERSTANDING & REQUIREMENTS GATHERING**

### Problem Restatement
Design a high-performance, real-time system that processes continuous stock price updates and provides instant queries for maximum price within any given time window (e.g., last 5 minutes, 1 hour). The system must handle millions of price updates per second while maintaining sub-millisecond query latency.

### Clarifying Questions

**Scale & Performance:**
- How many stocks are we tracking? (Estimated: 10K+ stocks globally)
- What's the price update frequency? (1M+ updates/second during market hours)
- What's the expected query frequency? (100K+ queries/second)
- What's the acceptable query latency? (<1ms p99)

**Technical Requirements:**
- What time window ranges should we support? (1min to 24 hours)
- How precise should timestamps be? (Microsecond precision)
- Do we need historical data beyond the window? (Yes, for analytics)
- What's the expected data retention period? (7 years for compliance)

**Business Logic:**
- Do we handle multiple exchanges? (Yes, NYSE, NASDAQ, LSE, etc.)
- How do we handle market hours vs after-hours? (24/7 operation)
- What about stock splits and corporate actions? (Event-driven adjustments)
- Do we need real-time alerts for price thresholds? (Yes, for trading systems)

### Functional Requirements

**Core Features:**
- Ingest real-time stock price updates from multiple data feeds
- Process sliding window maximum queries with configurable time ranges
- Support multiple stock symbols simultaneously
- Provide WebSocket/gRPC streaming for real-time updates
- Handle market data normalization across exchanges
- Support historical maximum queries for backtesting

**Advanced Features:**
- Real-time price alerts and threshold monitoring
- Statistical analytics (VWAP, moving averages, volatility)
- Market data replay for testing and simulation
- Cross-exchange price arbitrage detection

### Non-Functional Requirements

**Performance:**
- Process 1M+ price updates/second
- Query latency < 1ms (p99)
- 99.99% uptime during market hours
- Handle 10x traffic spikes during market events

**Scalability:**
- Horizontal scaling across multiple data centers
- Auto-scaling based on market activity
- Support for new markets and exchanges

**Reliability:**
- Zero data loss for price updates
- Exactly-once processing semantics
- Real-time failover and disaster recovery

### Success Metrics
- **Latency**: p99 query response < 1ms
- **Throughput**: 1M+ updates/second sustained
- **Accuracy**: 100% correctness for maximum calculations
- **Uptime**: 99.99% during market hours

### Constraints & Assumptions
- Market data feeds provide microsecond timestamps
- Regulatory compliance for financial data (MiFID II, SEC)
- Budget: High-performance infrastructure required
- Team: Quantitative developers and infrastructure engineers

---

## 2. 📊 **CAPACITY PLANNING & SCALE ESTIMATION**

### Back-of-envelope Calculations

**Data Ingestion:**
- 10,000 stocks × 100 updates/second/stock = 1M updates/second
- Each update: 64 bytes (symbol, price, timestamp, volume, exchange)
- Peak ingestion: 64 MB/second = 5.5 TB/day

**Memory Requirements:**
- 1-hour window: 3.6B updates × 64 bytes = 230 GB
- 24-hour window: 86.4B updates × 64 bytes = 5.5 TB
- In-memory index structures: 2-3x data size = 16.5 TB total

**Query Load:**
- 100K queries/second during peak trading
- Each query scans time-series index: ~1KB memory access
- Network bandwidth: 100 MB/second for responses

**Storage Requirements:**
- Hot data (1 week): 38.5 TB
- Warm data (1 month): 165 TB  
- Cold data (7 years): 14 PB
- With replication and compression: ~50 PB total

### Peak Load Scenarios
- **Market Open**: 5x normal traffic (first 30 minutes)
- **Earnings Announcements**: 10x traffic for specific stocks
- **Market Crashes**: 20x global traffic spikes
- **Algorithm Trading**: Sustained high-frequency bursts

### Growth Projections
- **Year 1**: Current scale (10K stocks, 1M updates/sec)
- **Year 3**: 50K stocks, 5M updates/sec (crypto + global expansion)
- **Year 5**: 100K instruments, 20M updates/sec (derivatives + forex)

---

## 3. 🏗️ **HIGH-LEVEL SYSTEM ARCHITECTURE**

```
                Real-Time Stock Price Maximum System Architecture
    
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Market Data    │───▶│   Data Ingress  │───▶│  Load Balancer  │
│   Providers     │    │     Gateway     │    │   (HAProxy)     │
│ (Reuters/BBG)   │    │ (Protocol Conv) │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                       ┌────────────────────────────────┼────────────────────────────────┐
                       ▼                                ▼                                ▼
           ┌─────────────────┐            ┌─────────────────┐            ┌─────────────────┐
           │  Stream Proc    │            │  Query Engine   │            │  Alert Engine   │
           │    (Kafka)      │            │   (C++/Rust)    │            │ (Event Driven)  │
           └─────────────────┘            └─────────────────┘            └─────────────────┘
                       │                           │                           │
        ┌──────────────┼──────────────┬───────────┼───────────┬──────────────┼──────────────┐
        ▼              ▼              ▼           ▼           ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌─────────────┐ ┌─────────────┐ ┌──────────────┐ ┌──────────────┐
│   Price      │ │  Window      │ │  Historical │ │   Query     │ │    Alert     │ │  Analytics   │
│  Validator   │ │  Aggregator  │ │   Storage   │ │   Cache     │ │   Service    │ │   Service    │
└──────────────┘ └──────────────┘ └─────────────┘ └─────────────┘ └──────────────┘ └──────────────┘
        │              │              │           │           │              │              │
        ▼              ▼              ▼           ▼           ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌─────────────┐ ┌─────────────┐ ┌──────────────┐ ┌──────────────┐
│    Redis     │ │    Redis     │ │ TimescaleDB │ │    Redis    │ │     Kafka    │ │ ClickHouse   │
│(Validation)  │ │ (Sliding Win)│ │(Long-term)  │ │  (L1 Cache) │ │ (Messaging)  │ │ (Analytics)  │
└──────────────┘ └──────────────┘ └─────────────┘ └─────────────┘ └──────────────┘ └──────────────┘
```

### Component Responsibilities

**Data Ingress Gateway:**
- Protocol conversion (FIX, binary feeds → internal format)
- Data validation and normalization
- Duplicate detection and filtering
- Market hours handling and exchange mapping

**Stream Processing (Kafka + Custom Processors):**
- High-throughput message ingestion
- Partitioning by stock symbol for parallel processing
- Real-time data transformation and enrichment
- Exactly-once delivery semantics

**Window Aggregator (Redis + Custom Logic):**
- Sliding window maximum calculation using efficient data structures
- Multi-level time window support (1min, 5min, 1hour, etc.)
- Lock-free concurrent access patterns
- Memory-optimized storage with TTL management

**Query Engine (C++/Rust):**
- Ultra-low latency query processing
- Lock-free data structure access
- SIMD optimizations for bulk operations
- Connection pooling and request batching

---

## 4. 🔧 **DETAILED COMPONENT DESIGN**

### 4.1 API Design

```yaml
# WebSocket Real-time API
ws://api.stockmax.com/v1/stream
{
  "subscribe": ["AAPL", "GOOGL", "MSFT"],
  "windows": ["1m", "5m", "1h"],
  "events": ["price_update", "max_change"]
}

# REST Query API
GET /api/v1/stocks/{symbol}/max?window=5m&start=2024-01-10T09:30:00Z
GET /api/v1/stocks/{symbol}/history?start=2024-01-10&end=2024-01-11
POST /api/v1/queries/batch
{
  "queries": [
    {"symbol": "AAPL", "window": "5m"},
    {"symbol": "GOOGL", "window": "1h"}
  ]
}

# gRPC High-Performance API
service StockPriceService {
  rpc GetMaxPrice(MaxPriceRequest) returns (MaxPriceResponse);
  rpc StreamPrices(StreamRequest) returns (stream PriceUpdate);
  rpc BatchQuery(BatchRequest) returns (BatchResponse);
}
```

### 4.2 Data Models & Storage

**Price Update Schema:**
```protobuf
message PriceUpdate {
  string symbol = 1;
  double price = 2;
  int64 timestamp_us = 3;  // Microsecond precision
  double volume = 4;
  string exchange = 5;
  int64 sequence_number = 6;
}
```

**Sliding Window Implementation:**
- **Deque-based approach** for simple windows (O(1) amortized)
- **Segment Tree** for complex range queries (O(log n))
- **Monotonic Deque** for maximum in sliding window (optimal O(1))

### 4.3 Algorithmic Optimization

**Sliding Window Maximum Algorithm:**
```conceptual
MonotonicDeque approach:
1. Maintain deque of (timestamp, price) pairs
2. Keep deque in decreasing order of prices
3. For new price: remove expired entries, remove smaller prices, add new
4. Maximum is always at front of deque
5. Time: O(1) amortized, Space: O(window_size)
```

**Multi-Window Optimization:**
- Use hierarchical aggregation (1min → 5min → 1hour)
- Precompute common window sizes
- Share computation across similar time ranges

### 4.4 Caching Strategy

```
L1: CPU Cache-friendly data structures (lock-free)
L2: Redis Cluster (sub-millisecond access)
L3: Query result cache (5-second TTL)
L4: Historical data cache (compressed, longer TTL)
```

---

## 5. ⚡ **ADVANCED SCALABILITY PATTERNS**

### 5.1 Horizontal Scaling

**Partitioning Strategy:**
- **Symbol-based sharding**: Hash(symbol) → partition
- **Time-based partitioning**: Separate recent vs historical data
- **Geographic partitioning**: Regional market data centers

**Load Balancing:**
- Consistent hashing for cache distribution
- Sticky sessions for WebSocket connections
- Health check-based failover

### 5.2 Performance Optimization

**CPU Optimization:**
- SIMD instructions for batch calculations
- CPU affinity for critical threads
- Lock-free data structures (compare-and-swap)
- Branch prediction optimization

**Memory Optimization:**
- Memory pools for object allocation
- Compression for historical data
- NUMA-aware memory placement
- Zero-copy network operations

**Network Optimization:**
- Kernel bypass (DPDK) for ultra-low latency
- Message batching and compression
- TCP/UDP hybrid protocols
- Multicast for market data distribution

---

## 6. 🛡️ **RELIABILITY & FAULT TOLERANCE**

### 6.1 Data Consistency

**Exactly-Once Processing:**
- Idempotent message processing with sequence numbers
- Distributed transaction coordination
- Kafka exactly-once semantics
- Graceful handling of duplicate messages

**Conflict Resolution:**
- Last-writer-wins for price updates
- Timestamp-based ordering
- Exchange priority rules
- Manual intervention for edge cases

### 6.2 Disaster Recovery

**Multi-Region Setup:**
- Active-active deployment across regions
- Cross-region data replication
- DNS-based failover (sub-second)
- Regional cache warming

**Backup Strategy:**
- Continuous WAL shipping to secondary regions
- Point-in-time recovery capability
- Market data replay from tape
- Automated failover testing

---

## 7. 🔒 **SECURITY & COMPLIANCE**

### 7.1 Financial Data Security

**Data Protection:**
- End-to-end encryption for market data feeds
- API authentication with short-lived tokens
- Rate limiting to prevent market manipulation
- Audit trails for all data access

**Regulatory Compliance:**
- MiFID II transaction reporting
- SEC market data requirements
- Data lineage and immutable logs
- Clock synchronization with atomic time

---

## 8. 📊 **MONITORING & OBSERVABILITY**

### 8.1 Key Metrics

**Business Metrics:**
- Price update latency (source to query)
- Query accuracy (validation against reference)
- Market data coverage (% of updates captured)
- Alert generation speed

**Technical Metrics:**
- Message throughput (updates/second)
- Query latency (p50, p95, p99, p99.9)
- Memory usage and GC impact
- Network packet loss and jitter

### 8.2 Real-time Monitoring

**Alert Conditions:**
- Query latency > 1ms threshold
- Message loss detection
- Clock drift beyond tolerance
- Market data feed disconnection

---

## 9. ⚖️ **TRADE-OFFS ANALYSIS**

### 9.1 Latency vs Throughput
- **Ultra-low latency**: Dedicated CPU cores, kernel bypass
- **High throughput**: Batch processing, compression
- **Chosen approach**: Hybrid with priority queues

### 9.2 Memory vs Accuracy
- **Full precision**: Store all historical prices
- **Approximation**: Sampling and interpolation
- **Chosen approach**: Tiered storage with different precisions

### 9.3 Consistency vs Availability
- **Strong consistency**: Synchronous replication
- **Eventual consistency**: Async replication with conflict resolution
- **Chosen approach**: Tunable consistency per use case

---

## 10. 🎨 **DESIGN PATTERNS & CONCEPTS**

### Applied Patterns

**Event Sourcing:**
- All price updates stored as immutable events
- State reconstruction from event log
- Enables historical analysis and debugging

**CQRS:**
- Separate write path (price ingestion) from read path (queries)
- Optimized data structures for each access pattern
- Independent scaling of read and write workloads

**Circuit Breaker:**
- Protection against cascade failures
- Graceful degradation during market data outages
- Automatic recovery when feeds restored

---

## 11. 🛠️ **TECHNOLOGY STACK**

### Core Components
- **Stream Processing**: Apache Kafka + Custom C++ consumers
- **Query Engine**: Custom C++/Rust with Redis
- **Time Series DB**: TimescaleDB for historical data
- **Caching**: Redis Cluster with consistent hashing
- **Message Queue**: Kafka with exactly-once semantics

### Infrastructure
- **Cloud**: AWS/GCP with dedicated instances
- **Networking**: 10Gbps dedicated connections to exchanges
- **Monitoring**: Prometheus + Grafana with custom metrics
- **Deployment**: Kubernetes with pod affinity rules

---

## 12. 🤔 **FOLLOW-UP QUESTIONS & ANSWERS**

### Q1: How would you optimize for different time window sizes?

**Answer:**
Implement hierarchical aggregation with precomputed windows:
- **1-minute buckets**: Raw data with microsecond precision
- **5-minute aggregates**: Max of 5 one-minute buckets
- **1-hour aggregates**: Max of 12 five-minute buckets
- **Custom windows**: Real-time computation using segment trees

Trade-off: Storage overhead vs query speed. For common windows (1m, 5m, 1h), precomputation gives O(1) queries. For arbitrary windows, use efficient range query algorithms.

### Q2: Design CI/CD pipeline for zero-downtime deployments

**Answer:**
Multi-stage deployment with canary releases:
1. **Blue-Green Infrastructure**: Parallel production environments
2. **Canary Deployment**: Route 1% traffic to new version
3. **Health Validation**: Automated latency and accuracy checks
4. **Gradual Rollout**: Increase traffic percentage over 30 minutes
5. **Rollback Capability**: Instant traffic redirection on anomalies

Critical: Market data cannot be interrupted, so deployment during market hours requires careful coordination.

### Q3: Handle distributed system challenges (network partitions, clock skew)

**Answer:**
**Network Partitions:**
- Regional failover with 99.9% availability SLA
- Cross-region replication with conflict-free resolution
- Client-side retry with exponential backoff

**Clock Synchronization:**
- NTP synchronization with atomic clock sources
- Logical clocks (Lamport timestamps) for ordering
- Clock skew detection and compensation algorithms
- Maximum allowed drift: 1 microsecond

**Split-Brain Prevention:**
- Consensus protocols (Raft) for leader election
- Quorum-based decisions for critical operations
- External coordination service (etcd/ZooKeeper)

### Q4: Implement testing strategy for financial systems

**Answer:**
**Market Data Replay:**
- Historical market data simulation
- Deterministic replay for debugging
- Performance regression testing

**Property-Based Testing:**
- Invariant checking (max value correctness)
- Fuzz testing with random market conditions
- Concurrent access safety verification

**Chaos Engineering:**
- Random component failures during trading hours
- Network latency injection
- Clock skew simulation

---

## 13. 🚀 **IMPLEMENTATION ROADMAP**

### Phase 1: Core Engine (0-3 months)
- Sliding window maximum algorithm implementation
- Single-machine, single-symbol prototype
- Basic WebSocket API for real-time queries
- Performance benchmarking and optimization

### Phase 2: Production Scale (3-6 months)
- Multi-symbol support with partitioning
- Distributed Redis cluster for state management
- Kafka integration for reliable message processing
- Monitoring and alerting infrastructure

### Phase 3: Enterprise Features (6-12 months)
- Multi-region deployment with failover
- Historical analytics and reporting
- Advanced alerting and threshold monitoring
- Integration with trading systems and risk management

---

## 14. 🔄 **ALTERNATIVE APPROACHES**

### Approach 1: In-Memory Computing (Apache Ignite)
**Pros**: Unified compute and storage, SQL queries, automatic persistence
**Cons**: Complex cluster management, higher memory requirements
**Use Case**: When SQL analytics are important alongside real-time queries

### Approach 2: Stream Processing Framework (Apache Flink)
**Pros**: Built-in windowing, exactly-once semantics, fault tolerance
**Cons**: Higher latency overhead, complex state management
**Use Case**: When broader stream processing capabilities are needed

### Approach 3: Specialized Time-Series Database (InfluxDB)
**Pros**: Purpose-built for time-series, compression, retention policies
**Cons**: Limited real-time query performance, SQL overhead
**Use Case**: When historical analytics are more important than real-time performance

---

## 15. 📚 **LEARNING RESOURCES**

### Financial Systems Architecture
- **"Inside the Black Box" by Rishi Narang** - Quantitative trading systems
- **"Flash Boys" by Michael Lewis** - High-frequency trading insights
- **FIX Protocol Documentation** - Financial messaging standards
- **Market Data Vendor APIs** - Reuters, Bloomberg, IEX integration guides

### Performance Engineering
- **"Systems Performance" by Brendan Gregg** - Linux performance tuning
- **Intel Optimization Manuals** - CPU-specific optimizations
- **DPDK Documentation** - Kernel bypass networking
- **Lock-free Programming** - Mechanical sympathy techniques

### Time Series & Streaming
- **"Designing Data-Intensive Applications"** - Distributed systems fundamentals
- **Apache Kafka Documentation** - Event streaming patterns
- **Redis Documentation** - In-memory data structures
- **TimescaleDB Best Practices** - Time-series database optimization

---

## 🎯 **Key Takeaways**

This real-time stock price system demonstrates ultra-low latency architecture suitable for financial trading environments. The design prioritizes performance and reliability while maintaining strict consistency requirements.

**Critical Success Factors:**
1. **Algorithm Efficiency**: Monotonic deque for O(1) sliding window maximum
2. **Hardware Optimization**: CPU affinity, NUMA awareness, SIMD instructions
3. **Data Locality**: Partitioning and caching strategies for hot data
4. **Fault Tolerance**: Exactly-once processing with rapid failover capability
5. **Monitoring**: Real-time latency tracking with microsecond precision

**Interview Performance Tips:**
- Emphasize algorithmic efficiency and big-O analysis
- Discuss hardware-level optimizations for financial systems
- Show understanding of trading system requirements and constraints
- Demonstrate knowledge of distributed systems challenges in real-time environments