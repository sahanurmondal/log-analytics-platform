# System Design Concepts: Comprehensive Backend Architectures Guide

## Table of Contents

1. [Overview](#overview)
2. [Monolithic Architecture](#monolithic-architecture)
3. [Microservices Architecture](#microservices-architecture)
4. [Serverless Architecture](#serverless-architecture)
5. [Event-Driven Architecture](#event-driven-architecture)
6. [CQRS Architecture](#cqrs-architecture)
7. [Distributed Systems Patterns](#distributed-systems-patterns)
8. [API Architecture Styles](#api-architecture-styles)
9. [Message Queue & Stream Processing](#message-queue--stream-processing)
10. [Caching Strategies](#caching-strategies)
11. [Load Balancing & Scaling](#load-balancing--scaling)
12. [Data Consistency Models](#data-consistency-models)
13. [Disaster Recovery & High Availability](#disaster-recovery--high-availability)
14. [Architecture Decision Matrix](#architecture-decision-matrix)
15. [Real-World Architecture Examples](#real-world-architecture-examples)

---

## Overview

System design encompasses multiple architectural patterns and concepts that dictate how backend systems are structured, scaled, and maintained. This guide covers all major backend system architectures with their use cases, data size considerations, and trade-offs. Each concept includes real-world examples from major tech companies.

---

## Monolithic Architecture

### When to Use

**Reasons:**
- **Early-stage startups** → Why: Simplicity matters more than scaling; faster to market
- **Small teams** → Reason: Single codebase easier to manage with 2-3 engineers
- **Well-defined, static requirements** → Why: If business logic is stable, monolith avoids complexity
- **Simple CRUD applications** → Reason: Blog, todo app, basic CMS don't need distributed complexity
- **Real-time consistency critical** → Why: In-process calls guarantee ACID transactions
- **Minimal operational overhead** → Reason: Deploy one artifact, single database, easier CI/CD

### Data Size Considerations

- **Small to Medium** (MB to 10 GB)
- Single database instance handles all data
- Vertical scaling (bigger servers) until ~100-200 concurrent users efficiently
- Database becomes bottleneck around 1-5 TB of data

### Trade-offs

| Aspect | Consideration |
|--------|---|
| **Scaling** | Easy initially; horizontal scaling requires complex sharding/partitioning |
| **Deployment** | Single deployable unit; any change requires full redeployment |
| **Development Speed** | Fast initially; slows as codebase grows (modules tightly coupled) |
| **Technology Stack** | Locked into one language/framework for entire application |
| **Fault Isolation** | Single failure can bring down entire system |
| **Team Independence** | Teams must coordinate; shared codebase creates bottlenecks |
| **Database Scaling** | Vertical scaling only; horizontal scaling complex with monolith |
| **Testing** | Full system testing required for any change; slow feedback loops |

### Sample Architecture

```
┌─────────────────────────────────────┐
│   Load Balancer (Nginx)             │
└──────────────┬──────────────────────┘
               │
     ┌─────────┴──────────┐
     │                    │
┌────▼──────┐      ┌──────▼───┐
│ Monolith  │      │ Monolith │
│ Instance1 │      │ Instance2 │
├───────────┤      ├──────────┤
│ User Svc  │      │ User Svc │
│ Order Svc │      │ Order Svc│
│ Auth Svc  │      │ Auth Svc │
└────┬──────┘      └──────┬───┘
     │                    │
     └────────┬───────────┘
              │
         ┌────▼────────┐
         │  PostgreSQL │
         │  (Single DB)│
         └─────────────┘
```

### Real-World Examples

**Successful Monoliths:**
1. **Shopify (initially)** - Started as Rails monolith; served millions of dollars GMV
2. **Slack** - Maintained monolith for core messaging; added services later
3. **Basecamp** - Still using Rails monolith; proves monoliths can scale with discipline
4. **Airbnb** - Ruby monolith for years before splitting into services

**When Monolith Failed:**
- **Twitter** - Rails monolith couldn't handle follower/following scale; switched to JVM services
- **Netflix** - Monolith melted under peak traffic; moved to microservices
- **Uber** - Single codebase per city became unmaintainable; split by domain

---

## Microservices Architecture

### When to Use

**Reasons:**
- **Large, complex systems** → Why: Business logic spans 10+ independent domains (payments, inventory, shipping, analytics)
- **Independent deployment needed** → Reason: Payment service updated without affecting user service
- **Different scaling requirements** → Why: Search service needs 100x more instances than email service
- **Multiple teams** → Reason: 50+ engineers need to work independently without constant conflicts
- **Polyglot requirements** → Why: Order service in Python, recommendation in Java, analytics in Go
- **Organizational structure** → Reason: Conway's Law - system architecture mirrors team structure

### Data Size Considerations

- **Large** (10 GB to PB)
- Each microservice has own database (Database-per-service pattern)
- Horizontal scaling per service based on demand
- Total data distributed across multiple databases
- Example: 100 microservices × 50 GB avg = 5 TB distributed system

### Trade-offs

| Aspect | Consideration |
|--------|---|
| **Complexity** | Distributed systems problems: eventual consistency, network failures, debugging harder |
| **Operational Overhead** | Need service mesh, monitoring per service, distributed logging, tracing |
| **Development Speed** | Initial setup slower; APIs between services must be versioned |
| **Network Latency** | Inter-service calls slower than in-process; cascading failures possible |
| **Data Consistency** | ACID transactions across services difficult; must use eventual consistency |
| **Deployment** | Independent deployment easier; but requires robust CI/CD and feature flags |
| **Testing** | Contract testing, integration tests across services complex; slow test suites |
| **Debugging** | Distributed tracing needed; bug may span 5 services; harder to reproduce |

### Sample Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  API Gateway (Kong / AWS API Gateway)                       │
└────┬─────────────────────────────────────────────────────────┘
     │
     ├─────────────┬──────────────┬──────────────┬─────────────┐
     │             │              │              │             │
┌────▼──┐   ┌─────▼──┐   ┌──────▼──┐   ┌──────▼──┐    ┌───▼────┐
│ User  │   │ Order  │   │ Payment │   │ Shipping│    │ Search │
│Service│   │Service │   │ Service │   │ Service │    │Service │
├───────┤   ├────────┤   ├─────────┤   ├─────────┤    ├────────┤
│ Users │   │Orders  │   │Payments │   │Shipments    │Products │
│ DB    │   │ DB     │   │ DB      │   │ DB     │    │ DB     │
└───────┘   └────────┘   └─────────┘   └─────────┘    └────────┘

      ┌───────────────────────────────┐
      │ Service Mesh (Istio)          │
      │ - Circuit breaker             │
      │ - Retry logic                 │
      │ - Distributed tracing         │
      └───────────────────────────────┘

      ┌──────────────────────────────┐
      │ Message Broker (RabbitMQ)    │
      │ For async communication      │
      └──────────────────────────────┘
```

### Real-World Examples

1. **Netflix** - 700+ microservices; each team owns service; deployed independently
2. **Uber** - Microservices per city + global services; dynamic scaling per region
3. **Amazon** - Founded on microservices; 2-pizza teams = service ownership
4. **Google** - Thousands of microservices; strict API contracts; centralized monitoring
5. **LinkedIn** - Play framework microservices; database per service; strong consistency where needed

---

## Serverless Architecture

### When to Use

**Reasons:**
- **Variable, unpredictable load** → Why: Payment webhook handler gets 10 requests/day or 10,000/day; scaling automatically
- **Cost optimization for low traffic** → Reason: Pay only for execution; no idle servers
- **Simple, stateless functions** → Why: Image processing, email sending, webhooks - don't maintain connection state
- **Event-triggered workloads** → Reason: S3 file upload → Lambda triggers processing
- **Time-bound tasks** → Why: Batch jobs, scheduled reports, data migrations
- **Rapid prototyping** → Reason: Deploy in seconds; no infrastructure management

### Data Size Considerations

- **Small to Medium function data** (MB, not GB)
- Functions process data, not store it (stateless)
- Backing stores (DynamoDB, S3) scale independently
- Total system can handle large data; individual functions process small chunks
- Cold start latency: 100ms-1s for first invocation after idle period

### Trade-offs

| Aspect | Consideration |
|--------|---|
| **Vendor Lock-in** | AWS Lambda, Google Cloud Functions are proprietary; migration difficult |
| **Cold Start Latency** | First execution slow (~100ms); problematic for real-time applications |
| **Execution Time Limit** | AWS Lambda: 15-minute max execution; long-running jobs need other solutions |
| **Debugging** | Limited logging/debugging compared to servers you control; harder to reproduce |
| **Cost Complexity** | Unpredictable costs if load spikes; per-millisecond billing can surprise |
| **State Management** | Stateless design required; maintaining state across invocations complex |
| **Monitoring** | Built-in monitoring limited; need external APM tools |
| **Language Support** | Limited runtimes; niche languages not supported |
| **Testing Locally** | Requires emulation tools (SAM, LocalStack); imperfect parity with production |

### Sample Architecture

```
┌─────────────────────────────────────────────┐
│  API Gateway (HTTP Requests)                │
└──────────────────┬──────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
   ┌────▼──────┐       ┌──────▼────┐
   │  Lambda   │       │  Lambda   │
   │  Function1│       │ Function2 │
   │(Auth)     │       │(Webhook)  │
   └────┬──────┘       └──────┬────┘
        │                     │
        └──────────┬──────────┘
                   │
        ┌──────────┴────────────┐
        │                       │
   ┌────▼─────────┐   ┌────────▼──┐
   │ DynamoDB     │   │   S3      │
   │ (State)      │   │ (Files)   │
   └──────────────┘   └───────────┘

┌──────────────────────────────────────────┐
│ Event Sources:                           │
│ - API Gateway, S3, SNS, SQS, DynamoDB   │
│ - CloudWatch Events, EventBridge        │
└──────────────────────────────────────────┘
```

### Real-World Examples

1. **Slack** - Lambda for webhook processing, image processing
2. **Salesforce** - Serverless for event handling, batch jobs
3. **Stripe** - Lambdas for webhook processing, fraud detection
4. **Netflix** - Lambdas for content recommendation triggers, image processing
5. **Zoom** - Recording processing, transcoding via Lambda

---

## Event-Driven Architecture

### When to Use

**Reasons:**
- **Decoupled, independent systems** → Why: Payment processed; multiple systems react (email, inventory, analytics) without knowing each other
- **Asynchronous workflows** → Reason: Don't block user waiting for all side effects; notify user later
- **Complex business processes** → Why: Order → Payment → Inventory → Shipping → Notification spanning multiple systems
- **Real-time data propagation** → Reason: Stock price updates; notification to 1M+ clients in seconds
- **Audit trail & event sourcing** → Why: "What happened?" answered by replaying event log
- **Multiple consumer patterns** → Reason: Same event consumed by analytics, notifications, recommendations

### Data Size Considerations

- **Event log size**: Can grow unbounded (10GB/day × 365 days = 3.65 TB/year)
- **Event throughput**: Millions of events/second (typical: 10K-1M events/sec)
- **Retention**: 30 days to 7 years depending on compliance
- **Consumer lag**: Some consumers catch up in seconds; others days behind

### Trade-offs

| Aspect | Consideration |
|--------|---|
| **Complexity** | Event flow can be hard to trace; understanding system requires reading multiple services |
| **Testing** | Events can trigger cascading effects; integration tests complex |
| **Ordering Guarantees** | Exactly-once delivery difficult; at-least-once typical (idempotency needed) |
| **Debugging** | Event consumed by 3 services; failure in one doesn't block others (silent failures) |
| **Latency** | Event propagation adds latency; not suitable for real-time request-response |
| **Storage** | Event log immutable and grows forever; storage costs accumulate |
| **Consumer Coordination** | Multiple consumers competing; partition assignment can be complex |
| **Monitoring** | Harder to monitor; can't easily see which services are affected by an event |

### Sample Architecture

```
┌──────────────────────────────────────────────┐
│  Event Producers                             │
│  (User Service, Order Service, Payment Svc) │
└──────────────────┬───────────────────────────┘
                   │
        ┌──────────▼──────────┐
        │  Event Bus/Broker   │
        │  (Kafka, RabbitMQ)  │
        │  Topics:            │
        │  - user.created     │
        │  - order.placed     │
        │  - payment.success  │
        └──────────┬──────────┘
                   │
     ┌─────────────┼─────────────────┐
     │             │                 │
┌────▼────────┐ ┌──▼──────────┐ ┌───▼──────────┐
│Email Service│ │Notification │ │Analytics    │
│- Subscribe: │ │Service      │ │Service      │
│  order      │ │- Subscribe: │ │- Subscribe: │
│  .placed    │ │  order      │ │  all events │
└─────────────┘ │  .placed    │ └─────────────┘
                └─────────────┘

        ┌────────────────────────────┐
        │ Event Store / Event Log    │
        │ (Kafka, Event Sourcing DB) │
        │ - Immutable                │
        │ - Append-only              │
        └────────────────────────────┘
```

### Real-World Examples

1. **Uber** - Every trip creates events: trip.requested, driver.matched, trip.started, trip.ended, payment.processed
2. **Netflix** - User.watched events trigger: recommendation update, analytics, content popularity
3. **Airbnb** - Booking.created → Email, SMS, notification, analytics, fraud detection
4. **Kafka at LinkedIn** - 1T+ events/day; all major systems event-sourced

---

## CQRS Architecture

### When to Use

**Reasons:**
- **Read-heavy systems** → Why: Reports need 1000s of reads; updates rare. Separate optimized read model
- **Complex queries** → Reason: SQL joins across 5 tables for dashboard; denormalize read model
- **Real-time analytics** → Why: Event → Update read model → User sees updated stats in 100ms
- **Audit & compliance** → Reason: Write model = immutable facts; read model = current state
- **Independent scaling** → Why: Read replicas scaled to 100 instances; write single node
- **Temporal queries** → Reason: "What was state at 2pm yesterday?" - replay events to know

### Data Size Considerations

- **Write model**: Small, normalized (transactions only)
- **Read model**: Denormalized, can be 5-10x larger than write model
- **Synchronization lag**: 100ms to seconds between write and read model update
- **Storage doubles**: Both write and read models stored; requires more disk

### Trade-offs

| Aspect | Consideration |
|--------|---|
| **Complexity** | Two models to maintain; synchronization logic required |
| **Eventual Consistency** | Read model lags behind write model; users see stale data briefly |
| **Maintenance** | Read model changes require migration; write model changes require event upcasting |
| **Debugging** | Data inconsistency between models; harder to troubleshoot |
| **Storage Cost** | Storing both write and read model doubles disk usage |
| **Framework Support** | Less framework support; requires custom synchronization logic |
| **Team Overhead** | Team needs to understand both models; knowledge sharing harder |
| **Deployment Ordering** | Read model changes must be deployed carefully to avoid failures |

### Sample Architecture

```
┌─────────────────────────────────────┐
│  Command API (Write Model)          │
│  POST /orders                       │
│  PATCH /orders/{id}/cancel          │
│  DELETE /orders/{id}                │
└──────────────┬──────────────────────┘
               │
        ┌──────▼─────────┐
        │ Command Handler│
        │ - Validate     │
        │ - Apply        │
        └────┬────────┬──┘
             │        │
        ┌────▼──┐  ┌──▼────────┐
        │ Event │  │ Event Log │
        │Store  │  │ (Append)  │
        │(Fact) │  └───────────┘
        └────┬──┘
             │
        ┌────▼──────────────────┐
        │ Read Model Updater    │
        │ (Event Handler)       │
        │ - Updates denormalized│
        │   read model          │
        └────┬──────────────────┘
             │
        ┌────▼──────────────┐
        │ Read Model        │
        │ (Denormalized)    │
        │ - Elasticsearch   │
        │ - MongoDB         │
        │ - PostgreSQL view │
        └───────────────────┘

┌──────────────────────────────────┐
│  Query API (Read Model)          │
│  GET /orders                     │
│  GET /orders/{id}               │
│  GET /dashboard/top-products    │
└──────────────────────────────────┘
```

### Real-World Examples

1. **Microsoft** - CQRS for Azure services; separate read optimization
2. **Jet.com** - Event-sourced CQRS for inventory management
3. **Booking.com** - Denormalized read models for search performance

---

## Distributed Systems Patterns

### Pattern 1: Service Discovery

**When to Use:**
- Microservices environment where service instances scale up/down dynamically
- Load balancer doesn't know instance IPs in advance
- Need to route requests to healthy instances only

**Data Size:** Minimal (KB); contains service metadata
**Trade-offs:** Adds network call for discovery; reduces complexity of manual configuration

**Example:** Kubernetes Service DNS; Consul; Eureka
```
Service A needs to call Order Service
- Queries: orderservice.internal:50051
- DNS resolves to 3 instances
- Load balancer picks one
- Request routed
```

---

### Pattern 2: Circuit Breaker

**When to Use:**
- Downstream service failing (Payment API down)
- Cascading failures (User Service → Order Service → Payment Service chain fails)
- Need graceful degradation

**Data Size:** Minimal (state counter)
**Trade-offs:** Temporarily returns error to user; prevents system meltdown

**Example:** Stripe payment down
```
Attempt 1: FAIL (Payment API timeout)
Attempt 2: FAIL
Attempt 3: FAIL
→ Circuit OPEN; return error without calling Stripe
After 60 seconds: Circuit HALF-OPEN; try one request
If successful: Circuit CLOSED; resume normal operation
```

---

### Pattern 3: Bulkhead Pattern

**When to Use:**
- Isolate resource usage between components
- One slow component shouldn't starve others
- Need resource isolation in shared environment

**Data Size:** Resource limits configuration
**Trade-offs:** Wastes resources if one bulkhead underutilized; adds operational complexity

**Example:** Thread pool isolation
```
Payment Service:
- Bulkhead 1: Stripe integration (10 threads)
- Bulkhead 2: Square integration (10 threads)
- Bulkhead 3: Internal processing (5 threads)

If Stripe slow, only its 10 threads blocked; Square still responsive
```

---

### Pattern 4: Retry & Exponential Backoff

**When to Use:**
- Transient failures (network hiccup, temporary overload)
- Idempotent operations (safe to retry)
- Don't retry on permanent failures (4xx errors)

**Data Size:** Minimal (retry count, delay)
**Trade-offs:** Slows successful requests if retrying; must handle idempotency

**Example:** DynamoDB write
```
Attempt 1: Provisioned throughput exceeded → Wait 100ms
Attempt 2: Provisioned throughput exceeded → Wait 200ms
Attempt 3: Success
Total latency: 300ms + processing
```

---

### Pattern 5: Idempotent Operations

**When to Use:**
- Any operation that might be retried
- Payment processing, database updates, critical operations
- Distributed systems where retries are common

**Data Size:** Idempotency key storage (UUID + result cache)
**Trade-offs:** Slightly higher latency (check cache before processing); need deduplication logic

**Example:** Transferring $100
```
Request ID: abc123
First attempt: Transfer $100 from Alice to Bob
- Cache: abc123 → transfer_id_456
Second attempt (retry): 
- See cached result; return transfer_id_456
- Don't transfer again; same result
```

---

## API Architecture Styles

### Style 1: REST API

**When to Use:**
- Standard CRUD operations with resources
- Resource-centric API design
- Web/mobile clients with standard HTTP

**Data Size:** Per-request (KB to MB)
**Trade-offs:** 
- Simple and standard; but inflexible for complex queries
- Over-fetching (get full object when need 3 fields)
- Under-fetching (need multiple requests for related data)

**Example:**
```
GET /users/123
GET /users/123/orders
GET /users/123/orders/456/items
```

---

### Style 2: GraphQL API

**When to Use:**
- Complex, interconnected data (social graph, recommendations)
- Client specifies exact fields needed
- Reduce over/under-fetching

**Data Size:** Per-query (varies by selection)
**Trade-offs:**
- Flexible queries; but complex to implement
- Requires query validation and cost analysis
- Security concerns (malicious queries can be expensive)

**Example:**
```graphql
query {
  user(id: 123) {
    name
    email
    orders(limit: 5) {
      id
      total
      items {
        productId
        quantity
      }
    }
  }
}
```

---

### Style 3: gRPC

**When to Use:**
- High-performance service-to-service communication
- Microservices with strict latency requirements
- Streaming data (video, sensor streams)

**Data Size:** Efficient binary serialization (2-3x smaller than JSON)
**Trade-offs:**
- Fast and efficient; but not web-friendly (no browser support)
- Requires code generation for stubs
- Debugging harder (binary format)

**Example:**
```protobuf
service OrderService {
  rpc CreateOrder(CreateOrderRequest) returns (Order);
  rpc StreamOrders(Empty) returns (stream Order);
}
```

---

### Style 4: Webhook API

**When to Use:**
- Push notifications when event happens
- Reduce polling (checking every minute)
- Real-time updates for external systems

**Data Size:** Event payload (KB to MB)
**Trade-offs:**
- Reduces polling load; but retry/reliability more complex
- Customers must maintain endpoint; support burden

**Example:** GitHub webhook
```
POST https://customer.example.com/webhooks/github
{
  "event": "push",
  "repository": "my-repo",
  "commits": [...]
}
```

---

## Message Queue & Stream Processing

### Message Queue: RabbitMQ / AWS SQS

**When to Use:**
- Asynchronous tasks (send email, generate report)
- Decouple producer and consumer
- Guaranteed delivery required

**Data Size:** Per message (typically <256 KB)
**Trade-offs:**
- Guarantees delivery; but adds latency (message in queue)
- Reliable; but operational overhead (managing queues)

**Example:**
```
Order Service:
1. Create order
2. Publish: order.created to SQS
3. Return to user immediately

Email Service:
1. Consume: order.created
2. Send confirmation email
3. (User doesn't wait for email)
```

---

### Stream Processing: Kafka

**When to Use:**
- High-volume event processing (1M events/sec)
- Event replay/reprocessing needed
- Multiple consumers of same events

**Data Size:** Unbounded (GB/day)
**Trade-offs:**
- Scalable to massive volumes; but complex operational overhead
- Ordering guarantees per partition; no global ordering

**Example:** Netflix
```
Video View Event:
→ Kafka Topic: video-views
→ Consumed by:
   - Analytics: count views per title
   - Recommendation: update user profile
   - Billing: charge user
   - Compliance: audit trail
```

---

## Caching Strategies

### Strategy 1: Cache-Aside (Lazy Loading)

**When to Use:**
- Read-heavy workloads
- Cache hits = DB bypass
- Acceptable that cache can be stale

**Data Size:** Subset of DB in cache
**Trade-offs:**
- Fast reads on hit; slower on miss (DB query)
- Complexity: check cache, miss, query DB, populate cache

**Example:**
```python
def get_user(user_id):
    user = redis.get(f"user:{user_id}")
    if not user:
        user = db.query(f"SELECT * FROM users WHERE id={user_id}")
        redis.set(f"user:{user_id}", user, EX=3600)
    return user
```

---

### Strategy 2: Write-Through

**When to Use:**
- Data consistency critical
- Updates infrequent
- Can afford double-write latency

**Data Size:** Full dataset in cache
**Trade-offs:**
- Write latency higher (must write DB and cache); but consistent
- If cache fails, write fails (must retry)

**Example:**
```python
def update_user(user_id, data):
    cache.set(f"user:{user_id}", data)  # Write cache first
    db.update("users", data)              # Then write DB
    return success
```

---

### Strategy 3: Refresh-Ahead

**When to Use:**
- Predictable access patterns
- Cache expiry would be expensive
- Background refresh acceptable

**Data Size:** Precomputed cache
**Trade-offs:**
- Prevents cache misses; but wastes CPU refreshing unused items
- Need background job; additional complexity

**Example:**
```
Leaderboard cache:
- Every 5 minutes: fetch top-100 players from DB
- Update Redis with precomputed scores
- When user queries, always cache hit
```

---

## Load Balancing & Scaling

### Horizontal Scaling (Scale-Out)

**When to Use:**
- Can't add more CPU/RAM to single machine
- Application stateless (no sticky sessions)
- Cost-efficient at large scale

**Data Size:** Distributed across instances
**Trade-offs:**
- Complexity increases; but unlimited scaling
- Requires load balancer, session sharing, coordination

**Example:** Web server farm
```
Load Balancer (Round-Robin)
├─ Web Server 1 (10K requests/sec)
├─ Web Server 2 (10K requests/sec)
├─ Web Server 3 (10K requests/sec)
└─ Web Server N (10K requests/sec)
Total: N × 10K requests/sec
```

---

### Vertical Scaling (Scale-Up)

**When to Use:**
- Application can use more CPU/RAM efficiently
- Cost per unit cheaper at scale
- Simplicity preferred over unlimited scaling

**Data Size:** Single large instance
**Trade-offs:**
- Simple to manage; but finite ceiling (max hardware available)
- No redundancy if single instance down

**Example:** Database server
```
Before: 4-core 16GB RAM
After: 128-core 512GB RAM (AWS x1.32xlarge)
Requests served: 2x-5x increase
Cost: 20x increase
```

---

### Auto-scaling

**When to Use:**
- Traffic unpredictable and bursty
- Want to maintain performance while minimizing cost
- Can tolerate 2-3 minute scale-up delay

**Data Size:** Dynamic (number of instances varies)
**Trade-offs:**
- Cost-efficient for variable load; but adds operational complexity
- Need metrics to trigger scaling (CPU%, requests/sec)

**Example:** Thanksgiving traffic spike
```
9am: 10 instances (baseline)
10am: 20 instances (traffic increases)
11am: 50 instances (peak)
12pm: 100 instances (massive spike)
1pm: 20 instances (traffic drops)
Cost saved: Paying for 100 instances constantly = $10K/month
            vs dynamic = $2K/month
```

---

## Data Consistency Models

### Strong Consistency (ACID)

**When to Use:**
- Financial transactions (money must be exact)
- Inventory management (can't oversell)
- User authentication (login must be consistent)

**Data Size:** Single node or replicated with synchronous consensus
**Trade-offs:**
- Guaranteed correctness; but slower (must wait for replicas)
- Simpler application logic

**Example:** Bank transfer
```
ACID guarantees:
- Atomicity: Transfer either happens or doesn't (not partial)
- Consistency: Money conserved (debited from A, credited to B)
- Isolation: Other transfers don't see intermediate state
- Durability: After commit, persisted even if crash
```

---

### Eventual Consistency

**When to Use:**
- High availability preferred over immediate correctness
- Distributed system (data replicated across regions)
- Acceptable that users see slightly stale data

**Data Size:** Eventually synced across all replicas
**Trade-offs:**
- Available and fast (no waiting for replication); but users see stale data briefly
- Requires idempotent operations and conflict resolution

**Example:** Social network like count
```
User A likes post
- Increment local like_count immediately
- Propagate increment to other replicas asynchronously
User sees like_count = +1 instantly
But if they refresh, might still show old count briefly
```

---

### Causal Consistency

**When to Use:**
- Preserve causality between related operations
- Not as strict as strong, not as loose as eventual
- Session consistency (user sees their own writes)

**Data Size:** Causal dependencies tracked
**Trade-offs:**
- Correctness for related operations; but more complex than eventual
- Better than strong; not as available

**Example:** Comment thread
```
User A: Posts comment "Hello"
User B: Replies "Hi there"
User B must see User A's comment before their reply
(preserves causality)

But User C might not see comments for 100ms
(eventual visibility; not causally dependent on them)
```

---

## Disaster Recovery & High Availability

### High Availability (HA)

**When to Use:**
- System downtime costs money (SaaS, e-commerce)
- Goal: Minimize downtime (99.9% uptime = 43 minutes/month)

**Data Size:** Multiple replicas
**Trade-offs:**
- Increased cost (redundant infrastructure); but prevents revenue loss
- Operational complexity (monitoring, failover)

**Example:** Database replication
```
Primary DB (Washington DC)
├─ Real-time replication
└─ Standby DB (Virginia DC)

If primary fails:
- Automatic failover to standby
- Downtime: <1 minute
- Data loss: 0 (synchronous replication)
Cost: 2x database cost; saves revenue loss during outage
```

---

### Disaster Recovery (DR)

**When to Use:**
- System completely destroyed (data center fire)
- Goal: Recover after disaster (RTO, RPO)

**Data Size:** Backup copies in different geographic location
**Trade-offs:**
- High cost (backup infrastructure, frequent testing); but recovery possible
- Backup lag = potential data loss

**Example:** Cross-region backup
```
Primary: AWS us-east-1
Backup: AWS eu-west-1 (daily snapshots)

Disaster: us-east-1 completely destroyed
Recovery:
- Restore from eu-west-1 snapshot
- Recovery Time Objective (RTO): 4 hours
- Recovery Point Objective (RPO): 24 hours
- Data loss: 1 day's transactions
Cost: Backup storage + testing + team training
```

---

### RPO vs RTO

| Metric | Definition | Example |
|--------|-----------|---------|
| **RTO** | Recovery Time Objective | After disaster, restore service in 4 hours |
| **RPO** | Recovery Point Objective | Data from last backup; 24 hours of data loss acceptable |

---

## Architecture Decision Matrix

### Choosing Architecture Based on Requirements

```
System Characteristics vs Architecture Type

                    Monolith  Microservices  Serverless  Event-Driven
──────────────────────────────────────────────────────────────────────
Team Size (1-5)       ✓✓         ✗           ✓           ✗
Team Size (50+)       ✗          ✓✓          ✗✓          ✓
High Traffic          ✗          ✓✓          ✓           ✓✓
Variable Load         ✗          ✓           ✓✓          ✓
Real-time Need        ✓✓         ✓           ✗           ✗
Deployment Speed      ✓          ✗           ✓✓          ✓
Complexity Low        ✓✓         ✗           ✓           ✗
Stateless App         ✓          ✓           ✓✓          ✗
Complex Logic         ✓✓         ✓           ✗           ✓
──────────────────────────────────────────────────────────────────────
Scale: ✓✓ = Ideal, ✓ = Good, ✗ = Not Recommended
```

### Quick Decision Tree

```
START: What's your primary constraint?

├─ Team Size & Speed?
│  ├─ <10 people, <1 year → Monolith
│  └─ >50 people, mature product → Microservices
│
├─ Cost?
│  ├─ Variable/unpredictable load → Serverless
│  └─ Consistent load → Monolith or Microservices
│
├─ System Coupling?
│  ├─ Independent components → Event-Driven or Microservices
│  └─ Tightly coupled logic → Monolith
│
├─ Real-time Requirements?
│  ├─ Millisecond latency → Monolith or gRPC
│  └─ Seconds acceptable → Serverless or Event-Driven
│
└─ Operational Complexity?
   ├─ Minimal ops team → Serverless or Monolith
   └─ Dedicated DevOps → Microservices or Event-Driven
```

---

## Real-World Architecture Examples

### Example 1: YouTube (Video Streaming)

**Architecture:** Hybrid (Monolith core + Microservices)

```
┌─────────────────────────────────────────────────────┐
│ Client (Web/Mobile)                                 │
└────────────────┬────────────────────────────────────┘
                 │
        ┌────────▼────────┐
        │ Load Balancer   │
        └────────┬────────┘
                 │
     ┌───────────┼──────────────┐
     │           │              │
┌────▼──┐  ┌─────▼──┐  ┌──────▼──┐
│Frontend│  │Backend │  │Backend  │
│Service │  │Service1│  │Service2 │
│(Python)│  │(Go)    │  │(Java)   │
└────┬───┘  └───┬────┘  └────┬────┘
     │          │            │
     └──────────┼────────────┘
                │
        ┌───────┴─────────┐
        │                 │
   ┌────▼────┐    ┌──────▼──┐
   │Spanner  │    │Bigtable │
   │(Metadata)│    │(Videos) │
   └─────────┘    └─────────┘

┌──────────────────────────────────────┐
│ Cache Layer (Memcache)               │
│ Hot videos, user preferences         │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Message Queue (Pub/Sub)              │
│ video.uploaded → transcoding service │
│ user.watched → analytics service    │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Analytics (BigQuery)                 │
│ Watch history, recommendations      │
└──────────────────────────────────────┘
```

**Why This Design:**
- Frontend monolith for simplicity
- Microservices for independent scaling (transcoding needs 10x resources on upload spike)
- Spanner for consistent metadata (watch history, subscriptions)
- Bigtable for massive video data (billions of videos)
- Memcache for hot video cache (most viewed videos)
- Pub/Sub for loose coupling (upload → transcoding, analytics, etc.)

---

### Example 2: Netflix (Streaming Service)

**Architecture:** Microservices with Event Sourcing

```
┌─────────────────────────────────────────────────────┐
│ Client (Smart TV, Web, Mobile)                      │
└────────────────┬────────────────────────────────────┘
                 │
        ┌────────▼────────┐
        │ API Gateway     │
        │ (Edge Server)   │
        └────────┬────────┘
                 │
     ┌───────────┼──────────────────┐
     │           │                  │
┌────▼────┐ ┌────▼────┐  ┌─────────▼──┐
│ Auth    │ │ Browse  │  │Playback    │
│Service  │ │Service  │  │Service     │
└─────────┘ └────┬────┘  └─────────┬──┘
                 │                 │
          ┌──────▼────────┐   ┌────▼────┐
          │Recommendation │   │Analytics│
          │Service        │   │Service  │
          └───────────────┘   └─────────┘

┌──────────────────────────────────────┐
│ Kafka Streams (Event Processing)     │
│ Events: user.watched, user.rated     │
│         playback.started             │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Cassandra (Time-series)              │
│ Viewing history (2T+ events/day)     │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Elasticsearch (Search)               │
│ Content search, discovery            │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Cache (Redis)                        │
│ User sessions, recommendations cache │
└──────────────────────────────────────┘
```

**Why This Design:**
- 700+ microservices (domain-driven teams)
- Kafka for event streaming (user watching generates events for analytics, recommendations)
- Cassandra for massive write volume (2T events/day)
- Elasticsearch for content search
- Redis for session and recommendation cache
- Resilient to individual service failures

---

### Example 3: Stripe (Payment Processing)

**Architecture:** Hybrid (Monolith + Async Workers)

```
┌─────────────────────────────────────────────────────┐
│ API Clients (Merchants, Apps)                       │
└────────────────┬────────────────────────────────────┘
                 │
        ┌────────▼────────┐
        │ REST API Layer  │
        │ (Synchronous)   │
        └────────┬────────┘
                 │
        ┌────────▼────────┐
        │ Rails Monolith  │
        │ - Payments      │
        │ - Invoicing     │
        │ - Subscriptions │
        └────────┬────────┘
                 │
        ┌────────▼────────┐
        │ PostgreSQL      │
        │ (Transactional) │
        └────────┬────────┘
                 │
        ┌────────▼────────┐
        │ Job Queue       │
        │ (Sidekiq/Redis) │
        └────────┬────────┘
                 │
     ┌───────────┼──────────────┐
     │           │              │
┌────▼──┐  ┌─────▼──┐  ┌──────▼──┐
│Email  │  │Webhook │  │Analytics│
│Service│  │Service │  │Service  │
└───────┘  └────────┘  └─────────┘

┌──────────────────────────────────────┐
│ Message Queue (Webhooks)             │
│ For async notification of events     │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Ledger (Immutable Event Log)         │
│ For audit trail and reconciliation   │
└──────────────────────────────────────┘
```

**Why This Design:**
- Rails monolith for simplicity (payment logic complex; distributed consensus harder)
- PostgreSQL for ACID guarantees (money must be exact)
- Job queue for async tasks (emails, webhooks, analytics don't block payment)
- Webhook system for merchant notifications (asynchronous, reliable)
- Immutable ledger for audit trail (regulatory requirement, reconciliation)

---

### Example 4: Uber (Ride-Sharing)

**Architecture:** Microservices with Real-time Tracking

```
┌─────────────────────────────────────────────────────┐
│ Mobile Apps (Rider, Driver)                         │
└────────────────┬────────────────────────────────────┘
                 │
        ┌────────▼────────┐
        │ API Gateway     │
        │ (Regional)      │
        └────────┬────────┘
                 │
     ┌───────────┼───────────────┐
     │           │               │
┌────▼──┐  ┌─────▼──┐  ┌────────▼──┐
│Ride   │  │Payment │  │Driver     │
│Service│  │Service │  │Service    │
└────┬──┘  └────────┘  └───────────┘

     ┌─────┴──────────────┐
     │                    │
┌────▼────┐      ┌───────▼──┐
│Matching │      │Location  │
│Service  │      │Service   │
└─────────┘      │(Redis)   │
                 └──────────┘

┌──────────────────────────────────────┐
│ PostGIS (Geographic Queries)         │
│ - Find nearby drivers                │
│ - Estimated arrival                  │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Kafka (Event Streaming)              │
│ Events: trip.requested, trip.matched │
│ trip.started, trip.ended            │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Cassandra (Trip History)             │
│ Billions of completed trips          │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Redis (Caching)                      │
│ Active driver locations, trip cache  │
└──────────────────────────────────────┘
```

**Why This Design:**
- Microservices for independent scaling (driver service handles GPS updates, needs heavy load)
- Redis for real-time location tracking (sub-second latency)
- PostGIS for geographic queries (find drivers within 5km)
- Cassandra for trip history (high write volume: 50M rides/day)
- Kafka for event streaming (analytics, fraud detection, billing)
- Regional services for latency (users see nearby drivers quickly)

---

### Example 5: Slack (Communication Platform)

**Architecture:** Monolith + Microservices Hybrid

```
┌─────────────────────────────────────────────────────┐
│ Web/Mobile Clients                                  │
└────────────────┬────────────────────────────────────┘
                 │
        ┌────────▼────────┐
        │ WebSocket/HTTP  │
        │ Gateway         │
        └────────┬────────┘
                 │
        ┌────────▼────────┐
        │ PostgreSQL Core │
        │ (Monolith)      │
        │ - Messages      │
        │ - Users         │
        │ - Workspaces    │
        └────────┬────────┘
                 │
     ┌───────────┼──────────────┐
     │           │              │
┌────▼──┐  ┌─────▼──┐  ┌──────▼──┐
│Search │  │File    │  │Notif    │
│Svc    │  │Svc     │  │Svc      │
│(ES)   │  │(S3)    │  │(Email)  │
└───────┘  └────────┘  └─────────┘

┌──────────────────────────────────────┐
│ Redis (Caching)                      │
│ User sessions, message cache         │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Message Queue (RabbitMQ)             │
│ For async notifications, webhooks    │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ Elasticsearch (Message Search)       │
│ Full-text search across all messages │
└──────────────────────────────────────┘
```

**Why This Design:**
- PostgreSQL monolith for core messaging (simplicity; messages < 100K)
- Elasticsearch for message search (full-text search on billions of messages)
- S3 for file storage (scalable, reliable)
- Redis for session caching (sub-second lookups)
- Message queue for notifications (async; reduce latency)
- Hybrid approach: monolith handles core; microservices handle specialized needs

---

## Conclusion

Choosing the right architecture depends on:

1. **Team Size & Structure** - Monolith for small teams; microservices for 50+
2. **Scale Requirements** - Monolith for GB; microservices for TB-PB
3. **Deployment Frequency** - Fast iteration = Monolith; frequent changes = Microservices
4. **Availability Requirements** - High availability = Distributed; acceptable downtime = Monolith
5. **Technology Heterogeneity** - Polyglot = Microservices; single language = Monolith
6. **Real-time Needs** - Millisecond requirements = gRPC/WebSocket; seconds acceptable = REST/Webhooks

Most systems start as monoliths and evolve to microservices as they grow. The architectural decision should match the business and team maturity level.

---

## Further Reading

- **System Design Interview**: https://www.educative.io/courses/grokking-system-design
- **Microservices Patterns**: https://microservices.io/
- **AWS Well-Architected**: https://aws.amazon.com/architecture/well-architected/
- **Google Cloud Architecture**: https://cloud.google.com/architecture
- **12-Factor App**: https://12factor.net/
or 