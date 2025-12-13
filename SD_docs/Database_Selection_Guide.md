# Database Selection Guide: Comprehensive DB Choices by Attributes

## Table of Contents
1. [Overview](#overview)
2. [Database Classification](#database-classification)
3. [Selection by Use Case](#selection-by-use-case)
4. [Selection by Data Size](#selection-by-data-size)
5. [Attribute-Based Comparison](#attribute-based-comparison)
6. [Decision Matrix](#decision-matrix)
7. [Case Studies & Real System Examples](#case-studies--real-system-examples)
8. [Schema Design Principles](#schema-design-principles)

---

## Overview

Choosing the right database is crucial for system performance, scalability, and cost-efficiency. This guide provides a comprehensive comparison of different database types based on key attributes like use case, data size, consistency, throughput, and latency requirements.

---

## Database Classification

### By Data Model
1. **Relational (SQL)**: Structured, ACID compliant
2. **NoSQL**: Document, Key-Value, Graph, Time-Series
3. **NewSQL**: Relational with horizontal scaling
4. **Search**: Full-text search and analytics

---

## Selection by Use Case

### 1. **Relational Databases (SQL)**

#### PostgreSQL / MySQL

**When to Use & Reasons:**
- **Complex relationships between entities** → Need to join users, orders, products - relational structure is natural
- **Complex queries with joins** → Reason: Data is normalized; queries often span multiple tables
- **ACID transactions critical** → Why: Banking, payments need atomicity - all-or-nothing operations
- **Data consistency paramount** → Reason: Cannot afford data corruption or race conditions
- **Well-defined schema** → Why: Schema changes are planned, data integrity enforced at DB level
- **Regulatory compliance** → Reason: HIPAA, PCI-DSS, GDPR require audit trails and strong consistency

**Data Size:**
- Small to Medium (GB to few TB on single instance)
- Can handle larger with proper sharding
  
**Real-World Examples:**
- Banking Systems, E-commerce order management, User authentication, Financial transactions

**Sample Schema:**

```sql
-- E-Commerce Order System
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  total_amount DECIMAL(10, 2),
  status VARCHAR(50) DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT NOW(),
  CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
  product_id BIGINT,
  quantity INT,
  unit_price DECIMAL(10, 2),
  CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_user_id ON orders(user_id);
CREATE INDEX idx_order_status ON orders(status);
```

**Trade-offs:**
| Aspect | Consideration |
|--------|---|
| **Scaling** | Vertical scaling easy, horizontal scaling complex (requires sharding) |
| **Write Performance** | Good for normal load, bottleneck under extreme write volume |
| **Schema Flexibility** | Low - schema changes require migrations and downtime |
| **Consistency vs Performance** | ACID guarantees may add latency during high concurrency |
| **Storage** | Normalized schema = smaller storage but complex queries |
| **Operational Complexity** | Medium - need backups, replication, monitoring |
  
**Typical Scale:**
- Single instance: 10-100 GB - 1 TB
- Clustered: 100 GB - 100 TB

---

### 2. **NoSQL - Document Databases**

#### MongoDB

**When to Use & Reasons:**
- **Schema is flexible/evolving** → Why: Early-stage products where data model changes frequently
- **Semi-structured data** → Reason: Each document can have different fields (e.g., user attributes vary by type)
- **Rapid development/iteration** → Why: No need for schema migrations between releases
- **Nested data structures common** → Reason: JSON-like storage matches application objects naturally
- **Horizontal scaling needed** → Why: Data grows beyond single server capacity
- **Mixed data types per entity** → Reason: Some users have premium features, others don't - different schema

**Data Size:**
- Medium to Large (GB to PB)
- Designed for horizontal scaling
  
**Real-World Examples:**
- User profiles with varying attributes, CMS, Real-time analytics, Mobile backends, IoT sensor data

**Sample Schema:**

```javascript
// User Profile Collection - Flexible Schema Example
db.users.insertOne({
  _id: ObjectId("507f1f77bcf86cd799439011"),
  email: "user@example.com",
  username: "johndoe",
  profile: {
    firstName: "John",
    lastName: "Doe",
    bio: "Software engineer",
    avatar: "https://example.com/avatar.jpg"
  },
  preferences: {
    theme: "dark",
    notifications: true,
    language: "en"
  },
  premiumUser: true,
  premiumFeatures: {
    storage: "1TB",
    collaborators: 50,
    expiresAt: ISODate("2025-12-31")
  },
  createdAt: ISODate("2024-01-15"),
  updatedAt: ISODate("2024-12-13")
});

// Note: Different user might have completely different structure
db.users.insertOne({
  _id: ObjectId("507f1f77bcf86cd799439012"),
  email: "basic@example.com",
  username: "janedoe",
  profile: {
    firstName: "Jane"
  },
  createdAt: ISODate("2024-06-01")
  // No premium fields, no preferences - schema flexibility!
});

// Index for performance
db.users.createIndex({ email: 1 }, { unique: true });
db.users.createIndex({ createdAt: -1 });
```

**Trade-offs:**
| Aspect | Consideration |
|--------|---|
| **Consistency** | Eventual consistency by default - may read stale data |
| **Transactions** | Limited; complex operations may not be atomic |
| **Storage** | Field names repeated in each document = larger storage footprint |
| **Query Power** | Good for document queries, but complex joins are difficult |
| **Schema Evolution** | Excellent - no migrations needed, but can lead to data inconsistency |
| **Indexing** | Requires careful planning; wrong indexes hurt performance significantly |
  
**Typical Scale:**
- Terabytes to Petabytes
- MongoDB Atlas clusters: 1 TB - 100+ TB

---

### 3. **NoSQL - Key-Value Databases**

#### Redis

**When to Use & Reasons:**
- **Cache layer needed** → Why: Reduce load on primary database by caching hot data
- **Sub-millisecond latency required** → Reason: In-memory storage beats disk-based databases
- **Session management** → Why: Users need instant session validation across requests
- **Real-time leaderboards** → Reason: Sorted sets provide O(log N) operations perfect for ranking
- **Rate limiting** → Why: Atomic operations increment counters in microseconds
- **Message queues** → Reason: Pub/Sub and list operations enable async communication

**Data Size:**
- Small to Medium (GB to few TB)
- Limited by RAM on single instance
  
**Real-World Examples:**
- Cache layer, session store, leaderboards, shopping carts, rate limiting, message broker

**Sample Schema:**

```javascript
// Session Cache - Simple Key-Value
SET session:user123 "{\"userId\":123,\"email\":\"user@example.com\"}" EX 3600

// Shopping Cart - Hash structure
HSET cart:user456 "product_1" "qty:2" "product_5" "qty:1"
HGETALL cart:user456

// Leaderboard - Sorted Set
ZADD leaderboard 1000 "player1"
ZADD leaderboard 1500 "player2"
ZADD leaderboard 2000 "player3"
ZREVRANGE leaderboard 0 10 WITHSCORES  // Get top 10

// Rate Limiting Counter
INCR api_requests:user789
EXPIRE api_requests:user789 60  // Reset every minute

// Real-time Feed Cache - List
LPUSH feed:user123 '{"id":456,"content":"post","timestamp":1234567}'
LRANGE feed:user123 0 50  // Get latest 50 posts

// Pub/Sub for notifications
PUBLISH notification_channel '{"type":"order","orderId":789}'
SUBSCRIBE notification_channel
```

**Trade-offs:**
| Aspect | Consideration |
|--------|---|
| **Persistence** | Data lost on crash unless RDB/AOF enabled (adds latency) |
| **Memory Limits** | All data must fit in RAM; expensive for large datasets |
| **Query Flexibility** | Simple key-value access; no complex queries |
| **Consistency** | Can be eventual due to replication lag |
| **Scalability** | Horizontal scaling complex; clustering has complexity |
| **Use Case Specificity** | Excellent for specific use cases, poor for general storage |
  
**Typical Scale:**
- Single instance: 1-100 GB
- Cluster: 100 GB - 1 TB

---

#### DynamoDB

**When to Use & Reasons:**
- **AWS-native solutions** → Why: Seamless integration with Lambda, S3, CloudWatch
- **Predictable, sustained traffic** → Reason: Easier to forecast capacity and budget
- **Key-value access patterns only** → Why: DynamoDB shines with simple lookups; complex queries perform poorly
- **Fully managed serverless** → Reason: No infrastructure to manage, auto-scaling included
- **Global replication needed** → Why: Built-in Global Tables for multi-region applications

**Data Size:**
- Small to Very Large (MB to PB)
- Unlimited with partitioning
  
**Real-World Examples:**
- User sessions in AWS, IoT device state, mobile app backend, notifications store

**Sample Schema:**

```json
// DynamoDB Table Schema - User Sessions
{
  "TableName": "user-sessions",
  "KeySchema": [
    { "AttributeName": "userId", "KeyType": "HASH" },
    { "AttributeName": "sessionId", "KeyType": "RANGE" }
  ],
  "AttributeDefinitions": [
    { "AttributeName": "userId", "AttributeType": "S" },
    { "AttributeName": "sessionId", "AttributeType": "S" }
  ],
  "BillingMode": "PAY_PER_REQUEST"
}

// Sample Item
{
  "userId": { "S": "user123" },
  "sessionId": { "S": "session-xyz-789" },
  "loginTime": { "N": "1702476000" },
  "expiresAt": { "N": "1702479600" },
  "deviceInfo": {
    "M": {
      "os": { "S": "iOS" },
      "appVersion": { "S": "2.5.0" }
    }
  },
  "ttl": { "N": "1702479600" }  // DynamoDB TTL for auto-cleanup
}

// IoT Device State Table
{
  "TableName": "device-state",
  "KeySchema": [
    { "AttributeName": "deviceId", "KeyType": "HASH" }
  ],
  "Item": {
    "deviceId": "device-456",
    "lastSeen": 1702476000,
    "status": "online",
    "temperature": 23.5,
    "humidity": 45,
    "location": "room-1"
  }
}
```

**Trade-offs:**
| Aspect | Consideration |
|--------|---|
| **Cost** | Pay-per-request can be expensive for unpredictable traffic |
| **Query Patterns** | Only supports key and range key queries; complex filtering via scans (expensive) |
| **Consistency** | Eventually consistent by default; strong consistency adds latency |
| **Vendor Lock-in** | AWS-only; migration to other platforms is difficult |
| **Capacity Planning** | Provisioned mode requires forecasting; on-demand lacks predictability |
| **Item Size** | 400 KB max item size; large documents must be compressed |
  
**Typical Scale:**
- Flexible: MB to Petabytes
- Cost varies significantly with usage

---

#### Neo4j

**When to Use & Reasons:**
- **Highly connected data** → Why: Graph traversals are orders of magnitude faster than SQL joins
- **Relationship analysis crucial** → Reason: Understanding how entities relate matters more than individual properties
- **Traversal queries common** → Why: "Find friends of friends" or "recommend products" need fast path traversal
- **Recommendation engines** → Reason: Collaborative filtering naturally maps to graph structures
- **Fraud detection systems** → Why: Identifying suspicious patterns in relationships is graph's strength

**Data Size:**
- Medium (GB to hundreds of GB)
- Can reach TB with Enterprise edition
  
**Real-World Examples:**
- Social networks, recommendation engines, knowledge graphs, fraud detection, master data management

**Sample Schema:**

```cypher
// Social Network Graph
CREATE (john:User {
  id: "user1",
  name: "John Doe",
  email: "john@example.com",
  joinedAt: datetime("2024-01-15")
})

CREATE (jane:User {
  id: "user2",
  name: "Jane Smith",
  email: "jane@example.com"
})

CREATE (tech:Interest {
  id: "interest1",
  name: "Technology",
  category: "tech"
})

// Relationships
CREATE (john)-[:FOLLOWS]->(jane)
CREATE (jane)-[:FOLLOWS]->(john)
CREATE (john)-[:INTERESTED_IN {strength: 0.95}]->(tech)
CREATE (jane)-[:INTERESTED_IN {strength: 0.87}]->(tech)

// Query: Find friends of friends
MATCH (user:User {id: "user1"})-[:FOLLOWS]->(friend)-[:FOLLOWS]->(fof)
RETURN DISTINCT fof.name
LIMIT 10

// Query: Recommend products based on similar users
MATCH (user:User {id: "user1"})-[:INTERESTED_IN]->(interest:Interest)
       <-[:INTERESTED_IN]-(similarUser:User)
MATCH (similarUser)-[:LIKED]->(product:Product)
RETURN product.name, COUNT(*) as score
ORDER BY score DESC
```

**Trade-offs:**
| Aspect | Consideration |
|--------|---|
| **Overkill for Non-Graph Data** | If your data isn't relational, graph adds unnecessary complexity |
| **Horizontal Scaling** | Graph databases scale horizontally less gracefully than document stores |
| **Learning Curve** | Cypher and graph thinking different from SQL; team needs training |
| **Licensing Costs** | Enterprise Neo4j can be expensive; community edition has limitations |
| **Smaller Ecosystem** | Fewer tools, drivers, and integration libraries vs PostgreSQL |
| **Memory Usage** | Relationship traversals can consume significant memory |
  
**Typical Scale:**
- 10 GB - 1 TB per instance

---

### 5. **NoSQL - Time-Series Databases**

#### InfluxDB / TimescaleDB

**When to Use & Reasons:**
- **Time-stamped data points** → Why: Natural ordering by timestamp; queries always filter by time range
- **Metrics and monitoring** → Reason: Every metric is tagged with timestamp; patterns change over time
- **High write throughput** → Why: Millions of data points/sec require optimized write paths
- **Data retention policies** → Reason: Old data automatically deleted; disk space constrained
- **Time-based aggregations common** → Why: "Average CPU over last hour" = rollups and downsampling

**Data Size:**
- Large volumes possible (TB+)
- Handles high insertion rate (millions/sec)
  
**Real-World Examples:**
- Application metrics, stock market data, IoT sensor data, system performance monitoring, APM, stock tracking

**Sample Schema:**

```sql
-- TimescaleDB Example (PostgreSQL extension)
CREATE TABLE metrics (
  time TIMESTAMPTZ NOT NULL,
  server_id TEXT NOT NULL,
  cpu_usage FLOAT,
  memory_used BIGINT,
  disk_io INT
);

-- Create hypertable for automatic partitioning
SELECT create_hypertable('metrics', 'time', 
  if_not_exists => TRUE);

-- Create index for fast time-range queries
CREATE INDEX ON metrics (server_id, time DESC);

-- Insert metrics
INSERT INTO metrics VALUES 
  (NOW(), 'server1', 45.2, 8589934592, 150),
  (NOW(), 'server2', 23.1, 4294967296, 80);

-- Query: Average CPU per server, last 24 hours
SELECT server_id,
  AVG(cpu_usage) as avg_cpu,
  MAX(cpu_usage) as peak_cpu
FROM metrics
WHERE time > NOW() - INTERVAL '24 hours'
GROUP BY server_id, time_bucket('1 hour', time);

-- Downsampling for long-term storage (aggregate old data)
SELECT
  server_id,
  time_bucket('1 day', time) as day,
  AVG(cpu_usage) as daily_avg
FROM metrics
WHERE time < NOW() - INTERVAL '30 days'
GROUP BY server_id, day;
```

**InfluxDB Example:**

```json
// InfluxDB Line Protocol
cpu,host=server1,region=us-west usage=43.5,idle=56.5 1609459200000000000
memory,host=server1,region=us-west used=8GB,free=2GB 1609459200000000000

// Query: Get CPU data for last 24 hours
SELECT mean("usage") FROM cpu 
WHERE time > now() - 24h 
GROUP BY host, time(1h)
```

**Trade-offs:**
| Aspect | Consideration |
|--------|---|
| **Use Case Specificity** | Only efficient for time-series; terrible for transactional data |
| **Query Language Learning** | InfluxDB uses different query language; retention policies have learning curve |
| **Data Deduplication** | Must handle duplicate writes; no built-in dedup |
| **Tag Cardinality** | High cardinality tags (unique server IDs) can explode memory usage |
| **Complex Transactions** | Cannot do multi-table joins with ACID; aggregations are reads-only |
| **Compression Overhead** | Delta-of-delta compression adds CPU; decompression on reads |
  
**Typical Scale:**
- Single instance: 100 GB - 1 TB
- Clustered: 1 TB - 100+ TB

---

### 6. **Search Databases**

#### Elasticsearch

**When to Use & Reasons:**
- **Full-text search required** → Why: Index inverted words; user types partial queries needing fuzzy matching
- **Log analysis and aggregation** → Reason: Logs volume enormous; need to search/aggregate across billions
- **Analytical queries on large datasets** → Why: Aggregations (min, max, percentiles) fast on indexed data
- **Complex filtering and faceting** → Reason: Users need to filter by 20 dimensions; traditional DB slow
- **Real-time analytics** → Why: Data searchable within seconds of ingestion; no batch jobs

**Data Size:**
- Large to Very Large (GB to PB)
- Designed for massive scale
  
**Real-World Examples:**
- Log aggregation (ELK), e-commerce product search, application analytics, security analytics, time-series analytics

**Sample Schema:**

```json
// Elasticsearch Index Mapping
{
  "settings": {
    "number_of_shards": 5,
    "number_of_replicas": 1,
    "index.lifecycle.name": "logs-policy",
    "index.lifecycle.rollover_alias": "logs"
  },
  "mappings": {
    "properties": {
      "timestamp": {
        "type": "date",
        "format": "strict_date_optional_time"
      },
      "message": {
        "type": "text",
        "analyzer": "standard"
      },
      "level": {
        "type": "keyword"
      },
      "service": {
        "type": "keyword"
      },
      "error_code": {
        "type": "integer"
      },
      "response_time_ms": {
        "type": "float"
      }
    }
  }
}

// Index a document
POST /logs-2024.12.13/_doc
{
  "timestamp": "2024-12-13T10:30:45.123Z",
  "message": "User login successful",
  "level": "INFO",
  "service": "auth-service",
  "user_id": "user123",
  "response_time_ms": 145.5
}

// Query: Full-text search with filtering
GET /logs/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "message": "login" } }
      ],
      "filter": [
        { "term": { "level": "ERROR" } },
        { "range": { "timestamp": { "gte": "now-24h" } } }
      ]
    }
  },
  "aggs": {
    "errors_by_service": {
      "terms": { "field": "service", "size": 10 }
    },
    "avg_response_time": {
      "avg": { "field": "response_time_ms" }
    }
  }
}
```

**Trade-offs:**
| Aspect | Consideration |
|--------|---|
| **Resource Consumption** | Requires lots of RAM and disk; cluster management complex |
| **Consistency Model** | Eventual consistency; near-real-time search (1-2 second lag) |
| **Not for Transactional** | Inverted index meant for search, not ACID transactions |
| **Operational Overhead** | Cluster needs monitoring, rebalancing, shard management |
| **Memory Footprint** | Indexes in memory; each unique term = memory overhead |
| **Configuration Complexity** | Tuning analyzers, tokenizers, scoring requires expertise |
  
**Typical Scale:**
- 100 GB - 100+ TB per cluster

---

### 7. **NewSQL Databases**

#### CockroachDB / Spanner

**When to Use & Reasons:**
- **Need SQL + horizontal scaling** → Why: Want SQL's power but need to scale beyond single machine
- **Strong consistency required** → Reason: Cannot tolerate eventual consistency issues (banking, inventory)
- **Global distributed system** → Why: Data replicated across regions; users served from nearest location
- **ACID transactions at scale** → Reason: Multi-row transactions must succeed together or fail together

**Data Size:**
- Medium to Very Large (TB to PB)
- Unlimited horizontal scaling
  
**Real-World Examples:**
- Global financial systems, multi-region applications, large-scale e-commerce, global user management

**Sample Schema:**

```sql
-- CockroachDB/Spanner Multi-Region Schema
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  name VARCHAR(255),
  created_at TIMESTAMP DEFAULT NOW(),
  country VARCHAR(2)
) PARTITION BY LIST (country) (
  PARTITION north_america VALUES IN ('US', 'CA', 'MX'),
  PARTITION europe VALUES IN ('GB', 'DE', 'FR'),
  PARTITION asia VALUES IN ('IN', 'JP', 'CN')
);

CREATE TABLE orders (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  amount DECIMAL(10, 2),
  status VARCHAR(50),
  created_at TIMESTAMP DEFAULT NOW(),
  FOREIGN KEY (user_id) REFERENCES users(id)
) INTERLEAVE IN PARENT users (user_id);

-- Multi-region replication config
ALTER DATABASE mydb CONFIGURE ZONE USING
  num_replicas = 3,
  constraints = '[+region=us-east,+region=us-west,+region=eu]';

-- ACID Transaction across regions
BEGIN;
  UPDATE users SET credits = credits - 100 WHERE id = 'user123';
  INSERT INTO orders VALUES (gen_random_uuid(), 'user123', 99.99, 'pending', NOW());
COMMIT;
```

**Trade-offs:**
| Aspect | Consideration |
|--------|---|
| **Latency** | Geographic distribution adds latency (consensus rounds across regions) |
| **Complexity** | Setup and maintenance more complex than single-region SQL |
| **Cost** | More expensive than traditional PostgreSQL; licensing costs (Spanner) |
| **Operational Overhead** | Requires distributed systems expertise; debugging harder |
| **Query Optimization** | Optimizer less mature than PostgreSQL; may need query rewrites |
| **Ecosystem** | Smaller community; fewer third-party tools and integrations |
  
**Typical Scale:**
- 1 TB - Petabytes

---

## Selection by Data Size

### Small Data (< 10 GB)

**Best Choices:**
1. **PostgreSQL / MySQL** - Simplest option
2. **SQLite** - For single-user/embedded
3. **Redis** - If caching/fast access needed

**Recommendation:** Start with PostgreSQL for most applications. Use SQLite only for embedded or development.

### Medium Data (10 GB - 1 TB)

**Best Choices:**
1. **PostgreSQL** - With read replicas if needed
2. **MongoDB** - If flexible schema needed
3. **MySQL** - With sharding strategy

**Recommendation:** PostgreSQL if data is relational and well-structured. MongoDB if schema flexibility needed.

### Large Data (1 TB - 100 TB)

**Best Choices:**
1. **MongoDB** - With sharding
2. **Elasticsearch** - For analytics/search
3. **TimescaleDB** - For time-series
4. **CockroachDB** - For distributed SQL

**Recommendation:** MongoDB for general NoSQL needs. Elasticsearch for search/analytics. TimescaleDB for metrics.

### Very Large Data (> 100 TB)

**Best Choices:**
1. **Elasticsearch** - For analytics and search
2. **Cassandra** - For write-heavy workloads
3. **BigQuery / Snowflake** - For data warehouse
4. **S3 + Parquet** - For data lake

**Recommendation:** Elasticsearch for search/analytics. BigQuery for data warehouse. S3 for raw data lake.

---

## Attribute-Based Comparison

### Consistency vs. Performance

| Database | Consistency | Performance | Use Case |
|----------|-------------|-------------|----------|
| PostgreSQL | Strong (ACID) | Medium | Financial, transactional |
| MongoDB | Eventual | High | User profiles, content |
| Redis | Eventual | Extreme | Cache, sessions |
| Cassandra | Eventual | Extreme | High-write, distributed |
| CockroachDB | Strong (ACID) | Medium-High | Global distributed |

---

### Write Throughput

| Database | Writes/sec (single) | Writes/sec (cluster) | Best For |
|----------|-------------------|-------------------|----------|
| PostgreSQL | 10K-100K | 100K-1M | Standard workloads |
| MongoDB | 100K-1M | 1M-100M+ | General NoSQL |
| Redis | 1M-10M | 10M-100M+ | Caching |
| Cassandra | 100K-1M | 1M-1B+ | Massive write scale |
| InfluxDB | 1M-10M | 10M-1B+ | Time-series data |

---

### Query Complexity

| Database | Query Types | Flexibility | Best For |
|----------|------------|------------|----------|
| PostgreSQL | Complex joins | Very High | Complex analytics |
| MongoDB | Document queries | High | Flexible schemas |
| Redis | Key lookups | Low | Simple access |
| Graph DB | Traversals | High | Relationship queries |
| Elasticsearch | Full-text search | High | Search & analytics |

---

### Scaling Approach

| Database | Vertical | Horizontal | Ease of Scale |
|----------|----------|-----------|---------------|
| PostgreSQL | Excellent | Medium | Medium |
| MongoDB | Good | Excellent | Medium |
| Redis | Excellent | Medium | Medium |
| Cassandra | Good | Excellent | Hard |
| Elasticsearch | Good | Excellent | Hard |

---

## Decision Matrix

### Quick Selection Guide

```
START: What's your primary use case?

├─ Transactional System?
│  └─ Complex relationships?
│     ├─ YES → PostgreSQL
│     └─ NO  → DynamoDB or Redis (if OLTP)
│
├─ Content/Document Storage?
│  ├─ Fixed schema?
│  │  └─ YES → PostgreSQL JSONB
│  └─ Flexible schema?
│     └─ YES → MongoDB
│
├─ Real-time Analytics?
│  ├─ Time-series metrics?
│  │  └─ YES → InfluxDB / TimescaleDB
│  └─ Log analysis / Full-text search?
│     └─ YES → Elasticsearch
│
├─ Relationship/Graph Data?
│  └─ YES → Neo4j
│
├─ Global Distributed?
│  └─ Need SQL?
│     ├─ YES → CockroachDB / Spanner
│     └─ NO  → Cassandra
│
└─ Cache Layer?
   └─ YES → Redis
```

---

## Case Studies

### Case Study 1: E-Commerce Platform

**Scenario:** Billion-dollar e-commerce platform with 100M+ users and 10TB+ data

**Database Strategy:**

```
Primary Data Layer:
├─ PostgreSQL (Master)
│  ├─ Users, Orders, Payments (strong consistency needed)
│  ├─ Replicas for read scaling
│  └─ Sharded for massive scale
│
├─ MongoDB
│  ├─ Product catalog (flexible schema)
│  ├─ User reviews and ratings
│  └─ Recommendations
│
├─ Elasticsearch
│  ├─ Product search
│  ├─ Faceted navigation
│  └─ Analytics
│
├─ Redis
│  ├─ Shopping carts
│  ├─ Session store
│  ├─ Product recommendations cache
│  └─ Rate limiting
│
└─ S3 + Parquet
   └─ Historical analytics data
```

**Why This Stack:**
- **PostgreSQL**: Users and transactions need ACID guarantees
- **MongoDB**: Product catalog has varying attributes
- **Elasticsearch**: Product search requires full-text indexing
- **Redis**: Speed critical for carts and sessions
- **S3**: Cost-effective for historical data

---

### Case Study 2: Real-Time Monitoring Platform

**Scenario:** Cloud monitoring service collecting 100K metrics/sec from 1000s of servers

**Database Strategy:**

```
Primary Data Layer:
├─ InfluxDB / TimescaleDB
│  ├─ Server metrics (CPU, memory, disk)
│  ├─ Application performance metrics
│  └─ Network metrics
│
├─ PostgreSQL
│  ├─ Alert rules
│  ├─ User accounts
│  └─ Configuration
│
├─ Elasticsearch
│  ├─ Log aggregation
│  ├─ Full-text log search
│  └─ Analytics
│
└─ Redis
   ├─ Current metric values
   ├─ Alert state cache
   └─ Session management
```

**Why This Stack:**
- **InfluxDB**: Optimized for time-series data ingestion and aggregation
- **PostgreSQL**: User management and alert configuration
- **Elasticsearch**: Log storage and analysis
- **Redis**: Current state and fast lookups

---

### Case Study 3: Social Network

**Scenario:** Social network with 500M users and billions of connections

**Database Strategy:**

```
Primary Data Layer:
├─ PostgreSQL
│  ├─ User accounts
│  ├─ Profiles
│  └─ User settings
│
├─ Neo4j
│  ├─ User relationships (follows, friends)
│  ├─ Recommendation algorithm
│  └─ Community detection
│
├─ MongoDB
│  ├─ Posts and content
│  ├─ Comments
│  └─ User-generated data
│
├─ Elasticsearch
│  ├─ Full-text search on posts
│  ├─ User search
│  └─ Trending topics
│
├─ Cassandra
│  ├─ Feed generation (high write)
│  ├─ Timeline data
│  └─ Notifications
│
└─ Redis
   ├─ Feed cache
   ├─ Like/comment counts
   ├─ Online status
   └─ Session store
```

**Why This Stack:**
- **PostgreSQL**: User master data
- **Neo4j**: Relationship queries for recommendations
- **MongoDB**: Flexible content storage
- **Elasticsearch**: Full-text search
- **Cassandra**: Write-heavy feed generation
- **Redis**: Cache for performance

---

## Selection Checklist

When choosing a database, ask yourself:

### 1. **Data Structure**
- [ ] Is data structured and relational? → SQL
- [ ] Is schema flexible? → MongoDB
- [ ] Is data a key-value pair? → Redis/DynamoDB
- [ ] Is data highly connected? → Graph DB
- [ ] Is data time-series? → InfluxDB/TimescaleDB
- [ ] Is data text-heavy? → Elasticsearch

### 2. **Consistency Requirements**
- [ ] Need ACID guarantees? → PostgreSQL/CockroachDB
- [ ] Eventual consistency acceptable? → MongoDB/Cassandra
- [ ] Need sub-millisecond response? → Redis

### 3. **Scale Requirements**
- [ ] < 10 GB? → PostgreSQL/SQLite
- [ ] 10 GB - 1 TB? → PostgreSQL or MongoDB
- [ ] 1 TB - 100 TB? → MongoDB or Elasticsearch
- [ ] > 100 TB? → Cassandra or Data Lake

### 4. **Query Patterns**
- [ ] Complex joins? → PostgreSQL
- [ ] Document queries? → MongoDB
- [ ] Key lookups? → Redis/DynamoDB
- [ ] Graph traversals? → Neo4j
- [ ] Full-text search? → Elasticsearch
- [ ] Time-range queries? → InfluxDB

### 5. **Operational Concerns**
- [ ] Want fully managed? → Cloud offerings (RDS, DynamoDB, Atlas, etc.)
- [ ] Need global distribution? → CockroachDB/Spanner
- [ ] Limited ops team? → Simpler SQL databases
- [ ] Can handle complex deployments? → Cassandra/Elasticsearch

---

## Cost Considerations

### Monthly Cost Estimates (1TB data, normal usage)

| Database | On-Premises | Cloud Managed |
|----------|------------|---------------|
| PostgreSQL | $500 | $2,000-5,000 |
| MySQL | $500 | $2,000-5,000 |
| MongoDB | $1,000 | $3,000-8,000 |
| Redis | $500 | $1,000-3,000 |
| Elasticsearch | $2,000 | $5,000-15,000 |
| DynamoDB | - | $2,000-20,000* |
| Neo4j | $3,000 | $5,000-50,000 |
| Cassandra | $2,000 | $10,000+ |
| CockroachDB | - | $5,000-30,000 |

*DynamoDB is pay-per-request, highly variable

---

## 30+ Real System Examples with Database Choices

### 1. **Uber/Lyft - Ride Sharing**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Analytics: Elasticsearch**

**Why:** PostgreSQL for transactions (bookings, payments), Redis for driver location cache (sub-sec latency), Elasticsearch for ride history search
```sql
-- Minimal Schema
CREATE TABLE rides (id UUID PRIMARY KEY, driver_id, user_id, fare DECIMAL, status VARCHAR);
-- Redis: geo:driver:${driverId} → {lat, lng, available}
```

---

### 2. **Netflix - Streaming Service**
**Primary DB: DynamoDB** | **Cache: Redis** | **Search: Elasticsearch** | **Analytics: Cassandra**

**Why:** DynamoDB for user profiles/watch history (serverless), Redis for user session/recommendations, Elasticsearch for content search, Cassandra for high-volume view events
```javascript
// DynamoDB: userId → {profile, watchHistory, preferences}
// Redis: cache:user:{userId} → {recommendations, bookmarks}
```

---

### 3. **LinkedIn - Social Network**
**Primary DB: PostgreSQL** | **Graph: Neo4j** | **Cache: Redis** | **Search: Elasticsearch**

**Why:** PostgreSQL for profile data, Neo4j for relationship/recommendation queries, Redis for feed cache, Elasticsearch for people/job search
```cypher
// Neo4j: (User)-[:CONNECTS]->(User), (User)-[:FOLLOWS]->(Company)
```

---

### 4. **Stripe - Payment Processing**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Audit: Cassandra**

**Why:** PostgreSQL for ACID transactions (payments must be exact), Redis for idempotency keys, Cassandra for immutable audit logs
```sql
-- PostgreSQL: Transactions table with strict ACID
CREATE TABLE payments (id UUID PRIMARY KEY, user_id, amount DECIMAL, status, created_at TIMESTAMP);
```

---

### 5. **Airbnb - Vacation Rentals**
**Primary DB: PostgreSQL** | **Search: Elasticsearch** | **Cache: Redis** | **Geospatial: PostGIS**

**Why:** PostgreSQL with PostGIS for location searches, Elasticsearch for text search (amenities, descriptions), Redis for availability cache
```sql
-- PostGIS: SELECT * FROM listings WHERE location <-> point(40.7128, -74.0060) LIMIT 10;
```

---

### 6. **Spotify - Music Streaming**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Search: Elasticsearch** | **Analytics: InfluxDB**

**Why:** PostgreSQL for user accounts/playlists, Redis for playback state, Elasticsearch for music search, InfluxDB for listen metrics
```javascript
// Redis: playback:user:{userId} → {songId, position, timestamp}
```

---

### 7. **Twitter/X - Social Network**
**Primary DB: Cassandra** | **Search: Elasticsearch** | **Cache: Redis** | **Graph: Neo4j**

**Why:** Cassandra for massive tweet write volume, Elasticsearch for tweet search, Redis for feed cache, Neo4j for recommendations
```javascript
// Cassandra: tweets table - write-optimized for billions of tweets
```

---

### 8. **YouTube - Video Platform**
**Primary DB: BigTable/Cassandra** | **Search: Elasticsearch** | **Cache: Redis** | **Analytics: InfluxDB**

**Why:** Cassandra for video metadata (massive scale), Elasticsearch for video search, Redis for recommendation cache, InfluxDB for watch metrics
```json
// Cassandra: video_id → {title, description, metadata, stats}
```

---

### 9. **Slack - Communication Platform**
**Primary DB: PostgreSQL** | **Message Queue: Redis** | **Search: Elasticsearch** | **Cache: Redis**

**Why:** PostgreSQL for team/user management, Redis for message queue and caching, Elasticsearch for message search
```sql
-- PostgreSQL: teams, users, workspaces (relational)
-- Redis Lists: message queues for real-time chat
-- Elasticsearch: indexed messages for full-text search
```

---

### 10. **Amazon S3 - Object Storage**
**Primary DB: DynamoDB** | **Index: Cassandra** | **Cache: Redis**

**Why:** DynamoDB for metadata (serverless, scalable), Cassandra for indexing, Redis for hot object cache
```json
// DynamoDB: bucket_id + object_key → {size, etag, metadata}
```

---

### 11. **Dropbox - File Sharing**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Search: Elasticsearch** | **Version Control: Git**

**Why:** PostgreSQL for file metadata, Redis for sync state, Elasticsearch for file search
```sql
-- PostgreSQL: files (owner_id, path, created_at, modified_at)
```

---

### 12. **Pinterest - Image Sharing**
**Primary DB: PostgreSQL** | **Search: Elasticsearch** | **Cache: Redis** | **Graph: Neo4j**

**Why:** PostgreSQL for pin metadata, Elasticsearch for image search, Redis for recommendation cache, Neo4j for user interests
```javascript
// Redis: recommendations:user:{userId} → [pin_ids]
```

---

### 13. **Grammarly - Text Analysis**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Analytics: ClickHouse**

**Why:** PostgreSQL for user documents and corrections, Redis for suggestion cache, ClickHouse for analytics
```sql
-- PostgreSQL: documents, corrections, user_preferences
```

---

### 14. **DoorDash - Food Delivery**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Search: Elasticsearch** | **Geospatial: PostGIS**

**Why:** PostgreSQL with PostGIS for location-based restaurant search, Redis for order status cache, Elasticsearch for restaurant search
```sql
-- PostGIS: SELECT * FROM restaurants WHERE location <-> delivery_point < 5km
```

---

### 15. **Twilio - Communication APIs**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Analytics: InfluxDB** | **Message Queue: Kafka**

**Why:** PostgreSQL for account/billing, Redis for API rate limit counters, InfluxDB for call metrics
```javascript
// Redis: rate_limit:account:{accountId} → {count, window}
```

---

### 16. **GitHub - Code Repository**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Search: Elasticsearch** | **File Storage: S3**

**Why:** PostgreSQL for repos/issues/PRs, Redis for session/access cache, Elasticsearch for code search
```sql
-- PostgreSQL: repositories, issues, pull_requests, commits
```

---

### 17. **Jira - Issue Tracking**
**Primary DB: PostgreSQL** | **Search: Elasticsearch** | **Cache: Redis**

**Why:** PostgreSQL for structured issue data, Elasticsearch for issue search/filtering, Redis for session cache
```sql
-- PostgreSQL: projects, issues, worklogs, custom_fields
```

---

### 18. **Datadog - Monitoring**
**Primary DB: PostgreSQL** | **Metrics: InfluxDB/TimescaleDB** | **Logs: Elasticsearch** | **Cache: Redis**

**Why:** PostgreSQL for dashboards/alerts, InfluxDB for metrics, Elasticsearch for log aggregation
```json
// InfluxDB: metric_name,host=server1,env=prod value=42.5 1702476000
```

---

### 19. **HashiCorp Vault - Secrets Management**
**Primary DB: PostgreSQL** | **Cache: Redis**

**Why:** PostgreSQL for secret storage with audit trails, Redis for cache
```sql
-- PostgreSQL: secrets, audit_logs, access_tokens
```

---

### 20. **Segment - CDP (Customer Data Platform)**
**Primary DB: PostgreSQL** | **Data Lake: S3 + Parquet** | **Analytics: Snowflake** | **Cache: Redis**

**Why:** PostgreSQL for config, S3 for raw data, Snowflake for analytics, Redis for caching
```sql
-- PostgreSQL: workspaces, integrations, configurations
-- S3: Parquet files for event data
```

---

### 21. **Plaid - Financial Data Aggregation**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Analytics: BigQuery**

**Why:** PostgreSQL for account linking (ACID), Redis for token cache, BigQuery for analytics
```sql
-- PostgreSQL: linked_accounts, transactions, balances
```

---

### 22. **Auth0 - Identity Provider**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Logs: Elasticsearch**

**Why:** PostgreSQL for user data (ACID), Redis for token/session cache, Elasticsearch for logs
```sql
-- PostgreSQL: users, applications, connections, rules
```

---

### 23. **Shopify - E-Commerce Platform**
**Primary DB: PostgreSQL** | **Search: Elasticsearch** | **Cache: Redis** | **Analytics: ClickHouse**

**Why:** PostgreSQL for orders, Redis for cart/session, Elasticsearch for product search, ClickHouse for analytics
```javascript
// Redis: cart:session:{sessionId} → {items: [{productId, qty, price}]}
```

---

### 24. **Figma - Design Collaboration**
**Primary DB: PostgreSQL** | **Real-time: WebSocket** | **Cache: Redis** | **File Storage: S3**

**Why:** PostgreSQL for project metadata, Redis for real-time collaboration state, S3 for design files
```javascript
// Redis: collaborative_state:project:{projectId} → {cursors, selections}
```

---

### 25. **Notion - Document Management**
**Primary DB: PostgreSQL** | **Search: Elasticsearch** | **Cache: Redis** | **File Storage: S3**

**Why:** PostgreSQL for page structure, Elasticsearch for content search, Redis for session
```sql
-- PostgreSQL: pages, blocks, permissions, workspaces
```

---

### 26. **Salesforce - CRM**
**Primary DB: PostgreSQL** | **Search: Elasticsearch** | **Cache: Redis** | **Analytics: BigQuery**

**Why:** PostgreSQL for CRM data, Elasticsearch for record search, Redis for session, BigQuery for reports
```sql
-- PostgreSQL: accounts, contacts, opportunities, activities
```

---

### 27. **Mailchimp - Email Marketing**
**Primary DB: PostgreSQL** | **Queue: Redis** | **Analytics: ClickHouse** | **Search: Elasticsearch**

**Why:** PostgreSQL for campaigns/subscribers, Redis for job queue, ClickHouse for open/click analytics
```javascript
// Redis: email_queue → {campaignId, recipientId, emailContent}
```

---

### 28. **Sentry - Error Tracking**
**Primary DB: PostgreSQL** | **Logs: Elasticsearch** | **Cache: Redis** | **Analytics: ClickHouse**

**Why:** PostgreSQL for projects/configs, Elasticsearch for error logs, Redis for caching, ClickHouse for stats
```json
// Elasticsearch: error_events with timestamp, stacktrace, environment
```

---

### 29. **Supabase - Backend as a Service**
**Primary DB: PostgreSQL** | **Real-time: WebSocket** | **Cache: Redis** | **Auth: JWT**

**Why:** PostgreSQL as primary (REST/GraphQL via PostgREST), Redis for real-time subscriptions
```sql
-- PostgreSQL: Direct exposure via PostgREST
```

---

### 30. **GitLab - DevOps Platform**
**Primary DB: PostgreSQL** | **Search: Elasticsearch** | **Cache: Redis** | **Job Queue: Redis**

**Why:** PostgreSQL for repos/issues/merge requests, Elasticsearch for code search, Redis for job queue
```sql
-- PostgreSQL: projects, issues, pipelines, deployments
-- Redis: ci_job_queue, cache
```

---

### 31. **Loom - Video Recording**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Video Storage: S3** | **Analytics: InfluxDB**

**Why:** PostgreSQL for video metadata, Redis for viewer cache, S3 for video files, InfluxDB for view metrics
```sql
-- PostgreSQL: videos, viewers, shares, comments
```

---

### 32. **Monday.com - Project Management**
**Primary DB: MongoDB** | **Cache: Redis** | **Search: Elasticsearch** | **Analytics: Mixpanel**

**Why:** MongoDB for flexible board structures, Redis for real-time cache, Elasticsearch for search
```javascript
// MongoDB: boards → {items: [{id, data, metadata}]}
```

---

### 33. **Canva - Design Editor**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Real-time State: Cassandra** | **File Storage: S3**

**Why:** PostgreSQL for user designs, Redis for session, Cassandra for collaborative editing state
```javascript
// Cassandra: design_collaborators:{designId} → [{userId, cursor, selection}]
```

---

### 34. **Vercel - Deployment Platform**
**Primary DB: PostgreSQL** | **Cache: Redis** | **Logs: Elasticsearch** | **Analytics: InfluxDB**

**Why:** PostgreSQL for projects/deployments, Redis for build status cache, Elasticsearch for logs
```sql
-- PostgreSQL: projects, deployments, builds, domains
```

---

### 35. **SendGrid - Email Service**
**Primary DB: PostgreSQL** | **Queue: Redis** | **Analytics: BigQuery** | **Logs: Elasticsearch**

**Why:** PostgreSQL for contacts/campaigns, Redis for send queue, BigQuery for delivery analytics
```javascript
// Redis: send_queue → {recipientId, emailTemplate, variables}
```

---

## Schema Design Principles

Effective database schema design is fundamental to system performance and maintainability. Schema design bridges application requirements with database capabilities, requiring careful consideration of data relationships, access patterns, normalization vs. denormalization trade-offs, and future scalability. The following principles guide schema decisions across different database types:

**1. Understand Your Access Patterns First** - Before creating any schema, identify how the application will query the data. Design the schema around these patterns, not the other way around. If 80% of queries need user + profile data, consider embedding vs. joining. This principle applies universally across SQL and NoSQL.

**2. Normalization vs. Denormalization Trade-off** - Traditional SQL teaching emphasizes normalization (eliminating redundancy), but modern systems often denormalize for performance. PostgreSQL works best when normalized (3NF), while MongoDB and DynamoDB often benefit from denormalization. The trade-off is storage vs. query performance and write complexity. For example, should "user.email" be normalized into a separate table or embedded in the user document? Denormalization means duplicating data, making updates harder but reads faster.

**3. Partition Keys and Distribution** - In distributed systems (Cassandra, DynamoDB, MongoDB sharding), partition key selection determines data distribution and query performance. Poor partition key choice can lead to hot partitions (some nodes handling more data than others). Ideal partition keys have high cardinality (many unique values) and distribute evenly. For example, using "user_id" as partition key distributes better than "country_code" (limited values = hotspots).

**4. Indexing Strategy** - Indexes are essential for performance but add write overhead and storage. Create indexes on fields frequently used in WHERE, ORDER BY, and JOIN clauses. Avoid over-indexing; each index slows down writes. Composite indexes (multi-column) can cover multiple query patterns. In Elasticsearch, the entire schema is indexed, different from SQL where you choose.

**5. Time-Series Specific Design** - For time-series data (InfluxDB, TimescaleDB), design around retention policies and downsampling. Store raw data at high resolution for recent periods, then aggregate/downsample older data. Use tags for dimensions that filter often (server_id, region) and fields for values being measured (cpu_usage, memory_used). This structure enables efficient compression and fast range queries.

**6. Immutability for Audit Trails** - For compliance and debugging, design tables/collections to be append-only with immutable records. Store change history separately (event sourcing pattern) rather than updating records. Use timestamps and versioning. Cassandra's write-once-append design naturally supports this; PostgreSQL requires careful design.

**7. Denormalization for Graph Relationships** - In graph databases, structure data around relationship traversals. Store inverse relationships if queries traverse both directions (e.g., both :FOLLOWS and reverse). Keep frequently accessed properties on nodes to avoid expensive additional lookups. For example, cache "friend_count" on User nodes even though it's derived.

**8. Soft Deletes vs. Hard Deletes** - For recovery and audit purposes, use soft deletes (add is_deleted flag) rather than physically removing records. This allows recovery from accidental deletions and maintains referential integrity. Hard deletes make recovery impossible and may violate foreign key constraints.

---

## Conclusion

There is no "one-size-fits-all" database. Choose based on:

1. **Data Structure** - How is your data organized?
2. **Scale** - How much data and traffic?
3. **Consistency** - How critical is data consistency?
4. **Query Patterns** - What questions will you ask?
5. **Operations** - What can your team support?
6. **Cost** - What's your budget?

Most production systems use **multiple databases** optimized for different purposes. Start simple, add complexity only when needed.

---

## Further Reading

- **PostgreSQL Documentation**: https://www.postgresql.org/docs/
- **MongoDB Manual**: https://docs.mongodb.com/manual/
- **Redis Documentation**: https://redis.io/docs/
- **Elasticsearch Guide**: https://www.elastic.co/guide/
- **Neo4j Documentation**: https://neo4j.com/docs/
- **CockroachDB**: https://www.cockroachlabs.com/docs/
- **AWS DynamoDB**: https://docs.aws.amazon.com/dynamodb/

