# eBay System Design Interview Questions (2023-2025)

## 📚 Quick Navigation to eBay System Design Topics

| **Core Architectures** | **Services** | **Domain-Specific** | **Advanced Topics** |
|------------------------|--------------|---------------------|--------------------|
| [🏛️ Monolithic vs Microservices](#monolithic-vs-microservices) | [🔍 Search Service](#search-service) | [🛒 E-Commerce Platform](#ecommerce-platform) | [🔄 Consistency Models](#consistency-models) |
| [📡 Event-Driven](#event-driven-architecture) | [💳 Payment Service](#payment-service) | [📦 Inventory System](#inventory-system) | [⚡ Scalability](#scalability-strategies) |
| [🚀 Microservices](#microservices-architecture) | [📧 Notification Service](#notification-service) | [🚚 Shipping & Logistics](#shipping--logistics) | [🛡️ Security](#security-considerations) |
| [📊 Data Layer](#data-layer-architecture) | [⭐ Recommendation Engine](#recommendation-engine) | [💰 Payment Processing](#payment-processing) | [🔐 Reliability](#reliability--resilience) |

---

## Core Architectural Patterns

### 1. Monolithic vs Microservices

#### When to Use Monolithic

**Scenario**: Building eBay from scratch (Day 1)
- **Constraints**: Small team (5-10 engineers)
- **Requirements**: Basic marketplace functionality
- **Trade-offs**:
  - ✅ Fast development and deployment
  - ✅ Easier debugging and monitoring
  - ❌ Scaling becomes bottleneck
  - ❌ Technology lock-in
  - ❌ Single point of failure

**Architecture**:
```
┌──────────────────────────────┐
│  Single Rails/Spring App     │
├──────────────────────────────┤
│ - User Management            │
│ - Product Listing            │
│ - Order Processing           │
│ - Payment Integration        │
│ - Notifications              │
└──────────────┬───────────────┘
               │
        ┌──────▼──────┐
        │ PostgreSQL  │
        │ (Single DB) │
        └─────────────┘
```

#### When to Use Microservices

**Scenario**: Scale to 100M+ products, 50M sellers
- **Constraints**: 200+ engineers across multiple teams
- **Requirements**: Independent scaling, polyglot needs
- **Trade-offs**:
  - ✅ Independent scaling per service
  - ✅ Team autonomy
  - ✅ Technology flexibility
  - ❌ Distributed systems complexity
  - ❌ Data consistency challenges
  - ❌ Operational overhead

**Architecture**:
```
┌─────────────────────────────────┐
│  API Gateway (Kong/AWS API GW)  │
└──────────────┬──────────────────┘
               │
     ┌─────────┼─────────┬──────────┐
     │         │         │          │
┌────▼──┐ ┌───▼──┐ ┌────▼───┐ ┌──▼────┐
│ User  │ │Catalog│ │ Order  │ │Payment│
│Service│ │Service│ │Service │ │Service│
└────┬──┘ └───┬──┘ └────┬───┘ └──┬────┘
     │        │         │        │
  ┌──▼──┐ ┌──▼──┐   ┌──▼──┐ ┌──▼──┐
  │User │ │Prod │   │Order│ │Txn  │
  │DB   │ │DB   │   │DB   │ │DB   │
  └─────┘ └─────┘   └─────┘ └─────┘
```

---

### 2. Event-Driven Architecture

**Use Case**: eBay's auction system, order lifecycle management

**Flow**:
```
Product Listed
    │
    ├─→ Event: product.listed
    │
    ├─→ [Search Service] Updates catalog
    │├─→ [Recommendation] Updates trending
    │├─→ [Notification] Notifies followers
    │└─→ [Analytics] Logs event
    │
Bid Placed
    │
    ├─→ Event: bid.placed
    │
    ├─→ [Auction Service] Updates bid state
    │├─→ [Notification] Notifies bidders
    │├─→ [Fraud Detection] Analyzes patterns
    │└─→ [Analytics] Tracks activity
    │
Auction Ends
    │
    ├─→ Event: auction.ended
    │
    ├─→ [Order Service] Creates order
    │├─→ [Seller Notification] Alerts seller
    │├─→ [Buyer Notification] Alerts winner
    │└─→ [Payment Service] Charges buyer
```

**Implementation**:
- **Event Bus**: Apache Kafka or AWS EventBridge
- **Event Schema**: Protobuf or Avro
- **Guarantees**: At-least-once delivery with idempotency

---

### 3. Microservices Architecture

**Key Services**:

#### User Service
```
Responsibilities:
- User authentication & authorization
- Profile management
- Trust & reputation
- Address management

API Endpoints:
POST /users/register
POST /users/login
GET /users/{userId}
PUT /users/{userId}/profile
GET /users/{userId}/reputation
```

#### Catalog Service
```
Responsibilities:
- Product listing management
- Category management
- Search indexing
- Product metadata

API Endpoints:
POST /products/create
PUT /products/{productId}
GET /products/{productId}
GET /products/search?query={query}
GET /products/category/{categoryId}
```

#### Order Service
```
Responsibilities:
- Order creation and management
- Order status tracking
- Order history
- Return management

API Endpoints:
POST /orders/create
GET /orders/{orderId}
PUT /orders/{orderId}/cancel
POST /orders/{orderId}/return
GET /users/{userId}/orders
```

#### Payment Service
```
Responsibilities:
- Payment processing
- Refund handling
- Transaction logging
- Reconciliation

API Endpoints:
POST /payments/process
POST /payments/{paymentId}/refund
GET /payments/{paymentId}
GET /transactions (for reconciliation)
```

#### Notification Service
```
Responsibilities:
- Multi-channel notifications (Email, SMS, Push)
- Notification templates
- Retry logic
- Delivery tracking

API Endpoints:
POST /notifications/send
GET /notifications/{userId}
PUT /notifications/{notificationId}/read
```

#### Recommendation Service
```
Responsibilities:
- Personalized product recommendations
- Trending products
- Collaborative filtering
- Content-based recommendations

API Endpoints:
GET /recommendations/{userId}
GET /products/trending
POST /recommendations/feedback
```

---

### 4. Data Layer Architecture

**Database Selection**:

| Service | Database | Reason |
|---------|----------|--------|
| User Service | PostgreSQL | Strong consistency, ACID for auth |
| Catalog Service | Elasticsearch + PostgreSQL | Search performance, metadata |
| Order Service | PostgreSQL | Transaction requirements |
| Payment Service | PostgreSQL | ACID, audit trail |
| Notification | MongoDB | Flexible schema, high write volume |
| Recommendations | Redis + Cassandra | Fast access + historical data |

**Example: Catalog Service Stack**
```
┌─────────────────────────────────────┐
│  Catalog Service                    │
└─────────────────┬───────────────────┘
                  │
    ┌─────────────┼──────────────┐
    │             │              │
    ▼             ▼              ▼
PostgreSQL   Elasticsearch    Redis
(Metadata)   (Full-text)    (Cache)
    │             │              │
    ├─────────────┼──────────────┘
    │             │
    ▼             ▼
Data Cache    Search Index
```

---

## Domain-Specific Designs

### E-Commerce Platform

#### 1. Complete eBay Platform

**High-Level Architecture**:

```
┌────────────────────────────────────────────────────┐
│          Client Layer (Web/Mobile/API)            │
└───────────────┬────────────────────────────────────┘
                │
        ┌───────▼──────┐
        │ Load Balancer│ (Geographic distribution)
        └───────┬──────┘
                │
        ┌───────▼──────┐
        │ API Gateway  │ (Auth, Rate limiting, Routing)
        └───────┬──────┘
                │
    ┌───────────┼────────────────┬──────────────┐
    │           │                │              │
    ▼           ▼                ▼              ▼
┌────────┐ ┌────────┐      ┌────────┐    ┌────────┐
│ Search │ │Catalog │      │ Order  │    │ User   │
│Service │ │Service │      │Service │    │Service │
└────┬───┘ └────┬───┘      └────┬───┘    └────┬───┘
     │          │               │             │
     ▼          ▼               ▼             ▼
┌─────────┐ ┌─────────┐   ┌─────────┐   ┌─────────┐
│Elasticsearch
│PostgreSQL│Postgres│PostgreSQL│
└─────────┘ └─────────┘   └─────────┘   └─────────┘

        ┌──────────────────────────┐
        │   Message Bus (Kafka)    │
        │ For async communication  │
        └──────────────────────────┘

        ┌──────────────────────────┐
        │ Cache Layer (Redis)      │
        │ Session, recommendations │
        └──────────────────────────┘
```

---

### 2. Inventory System

**Requirements**:
- Real-time inventory tracking
- Multi-warehouse support
- Stock level alerts
- Overselling prevention

**Design**:

```
┌─────────────────────────────┐
│   Inventory Service         │
└──────────────┬──────────────┘
               │
       ┌───────┴────────┐
       │                │
       ▼                ▼
┌─────────────┐   ┌──────────────┐
│ PostgreSQL  │   │  Redis Cache │
│ (Source)    │   │  (Quick access)
└─────────────┘   └──────────────┘

Inventory States:
1. Available (in-stock, unallocated)
2. Reserved (in-cart, not paid)
3. Allocated (order placed, awaiting fulfillment)
4. Shipped (in-transit)
5. Returned (awaiting restock)
6. Damaged (write-off)
```

**API**:
```java
// Reserve inventory when item added to cart
POST /inventory/reserve
{
  "productId": "123",
  "quantity": 1,
  "cartId": "cart-xyz"
}

// Confirm reservation when payment processed
POST /inventory/allocate
{
  "orderId": "order-456",
  "reservationId": "res-789"
}

// Release reservation when cart abandoned
POST /inventory/release
{
  "reservationId": "res-789"
}

// Check availability
GET /inventory/{productId}
→ {
    "available": 50,
    "reserved": 10,
    "allocated": 25,
    "total": 85
  }
```

---

### 3. Payment Processing

**Requirements**:
- PCI DSS compliance
- Multiple payment methods (Credit, PayPal, etc.)
- Idempotent transactions
- Fraud detection
- Refund management

**Design**:

```
┌───────────────────────────────┐
│   Payment Service             │
│ (PCI-compliant, isolated)     │
└──────────┬────────────────────┘
           │
       ┌───┴──────┬───────────┐
       │          │           │
       ▼          ▼           ▼
┌──────────┐ ┌─────────┐ ┌──────────┐
│Stripe/   │ │PayPal   │ │Internal  │
│Square    │ │API      │ │Ledger    │
│Gateway   │ │         │ │(Audit)   │
└──────────┘ └─────────┘ └──────────┘

Payment Flow:
1. Validate cart & inventory
2. Create payment intent with idempotency key
3. Tokenize payment method
4. Submit to payment gateway
5. Wait for authorization
6. Record in ledger on success
7. Retry logic with exponential backoff
8. Update order status
9. Trigger fulfillment event
```

**Code Example**:
```java
// Request body
{
  "orderId": "order-123",
  "amount": 99.99,
  "currency": "USD",
  "paymentMethod": "credit_card",
  "idempotencyKey": "unique-request-id",
  "buyerId": "user-456"
}

// Response
{
  "transactionId": "txn-789",
  "status": "AUTHORIZED",
  "timestamp": "2025-01-15T10:30:00Z"
}

// Retry with same idempotencyKey returns same result
```

---

### 4. Shipping & Logistics

**Requirements**:
- Multi-carrier support (USPS, FedEx, UPS, DHL)
- Rate shopping
- Label generation
- Tracking updates
- Delivery time estimation

**Design**:

```
┌───────────────────────────┐
│   Shipping Service        │
└────────────┬──────────────┘
             │
    ┌────────┼──────────┐
    │        │          │
    ▼        ▼          ▼
┌────────┐┌────────┐┌────────┐
│USPS   ││FedEx  ││UPS     │
│API    ││API    ││API     │
└────────┘└────────┘└────────┘

Rate Comparison:
1. Get rates from multiple carriers
2. Filter by delivery window
3. Rank by price + reliability
4. Select based on seller preference
5. Generate label
6. Update tracking

Tracking Events:
- package.created
- package.shipped
- package.in_transit
- package.out_for_delivery
- package.delivered
- package.delayed
- package.lost
```

**API**:
```
POST /shipping/rates
{
  "origin": {"zip": "94105", "country": "US"},
  "destination": {"zip": "75001", "country": "US"},
  "weight": 2.5,
  "dimensions": {"length": 10, "width": 8, "height": 5}
}

POST /shipping/labels
{
  "shipmentId": "ship-123",
  "carrierCode": "UPS",
  "serviceLevel": "ground"
}

GET /shipping/tracking/{trackingNumber}
→ Events with timeline
```

---

## Advanced Designs

### Search Service

**Requirements**:
- Full-text search across billions of products
- Faceted search (category, price, condition)
- Autocomplete
- Typo tolerance
- Real-time updates

**Architecture**:

```
┌────────────────────────────────┐
│   Search Query               │
└───────────────┬────────────────┘
                │
        ┌───────▼────────┐
        │ Query Parser   │
        │ (Expand typos) │
        └───────┬────────┘
                │
        ┌───────▼────────────┐
        │ Elasticsearch      │
        │ (Query execution)  │
        └───────┬────────────┘
                │
    ┌───────────┴───────────┐
    │                       │
    ▼                       ▼
┌─────────────┐         ┌──────────────┐
│Full-text    │         │Facet counts  │
│Results      │         │(Categories)  │
└─────────────┘         └──────────────┘
```

**Indexing**:
```
Data Flow:
Product Catalog Service
    │
    └─→ Kafka Event: product.updated
        │
        └─→ Search Service
            │
            ├─→ Parse product
            ├─→ Extract text
            ├─→ Build inverted index
            └─→ Update Elasticsearch
```

---

### Recommendation Engine

**Algorithm Options**:

#### Collaborative Filtering
```
Steps:
1. Build user-item interaction matrix
2. Calculate user similarity (cosine)
3. Find similar users' purchases
4. Rank by rating and newness

Pros: Simple, captures user preferences
Cons: Cold start problem (new users/items)

Implementation:
- Store interaction matrix in Cassandra
- Use Redis for recent interactions
- Update daily batch job
- Serve from cache layer
```

#### Content-Based
```
Steps:
1. Extract product features (category, price, seller)
2. Calculate product similarity
3. Find similar products to user's purchases
4. Rank by relevance

Pros: Handles cold start
Cons: Limited serendipity

Implementation:
- Feature extraction from catalog
- Similarity calculation using Minhash
- Redis for lookup table
- Real-time calculation possible
```

#### Hybrid Approach (eBay Reality)
```
Combine both:
- New user: Use content-based
- Existing user: Weight collaborative filtering
- Cold product: Boost by popularity
- Trending: Time-decay factor
- Personalization: User explicit preferences

Score = 0.4 * collaborative + 0.3 * content + 0.3 * popularity
```

---

### Auction & Bidding System

**Requirements**:
- Real-time bid processing
- Prevent bid sniping
- Calculate winning bids
- Handle concurrent bids

**Design**:

```
Auction State Machine:
NOT_STARTED → ACTIVE → ENDING → ENDED → COMPLETED

API:
POST /auctions/{auctionId}/bid
{
  "bidderId": "user-123",
  "amount": 150.00
}

Response:
{
  "bidId": "bid-456",
  "currentBid": 145.00,
  "highestBidder": "user-456",
  "status": "outbid"
}

Implementation:
- Use Redis sorted sets for bid tracking
- Prevent overbidding with atomic operations
- Cache current bid state
- Event stream for bid history
```

---

## Scalability Strategies

### 1. Horizontal Scaling

**Problem**: Single database becoming bottleneck

**Solution**: Database Sharding
```
By Seller ID:
┌────────────────┐
│ Shard 0 (0-1M) │ ← Sellers 0 to 1M
└────────────────┘

┌────────────────┐
│ Shard 1 (1-2M) │ ← Sellers 1M to 2M
└────────────────┘

┌────────────────┐
│ Shard N (...)  │ ← Sellers ... to ...
└────────────────┘

Routing Logic:
shard_id = seller_id % num_shards
```

**By Time**:
```
- January 2025 orders → shard_jan_2025
- February 2025 orders → shard_feb_2025
- Older orders archived to cold storage

Advantage: Easier archive and cleanup
```

---

### 2. Caching Strategy

**Three-Tier Cache**:

```
┌──────────────┐
│ Browser Cache│ (User, Catalog Pages)
└──────────────┘
       │
┌──────▼──────────────┐
│ CDN Cache           │ (Cloudflare, CloudFront)
│ (Product images)    │
└──────┬──────────────┘
       │
┌──────▼──────────────┐
│ Application Cache   │ (Redis)
│ (Recommendations)   │
└──────┬──────────────┘
       │
┌──────▼──────────────┐
│ Database            │ (PostgreSQL)
│ (Source of truth)   │
└─────────────────────┘
```

---

### 3. API Rate Limiting

**Token Bucket Algorithm**:
```
Seller API:
- 100 requests per minute per seller
- Burst: 200 requests per minute

Implementation:
- Redis for counter storage
- Key: {seller_id}:{minute}
- Increment and check threshold
- Set expiry to 2 minutes
```

---

## Consistency Models

### 1. Strong Consistency (Payment Service)

**Requirement**: Money must be exact
```
Transaction:
BEGIN;
  SELECT balance FROM accounts WHERE id = seller_id FOR UPDATE;
  UPDATE accounts SET balance = balance + amount WHERE id = seller_id;
COMMIT;

Guarantees:
- Atomicity: All-or-nothing
- Consistency: Balance never negative
- Isolation: No dirty reads
- Durability: Survives crashes
```

---

### 2. Eventual Consistency (Product Catalog)

**Requirement**: Immediate consistency not critical
```
Write Path (Master):
POST /products → PostgreSQL master → Return success

Read Path (Replica):
GET /products → Read from replicas
                → May be 100ms to 1s behind

Acceptable because:
- Product metadata changes rarely
- Users don't expect 100% accuracy
- Temporary inconsistency acceptable
```

---

### 3. Causal Consistency (Order Processing)

**Requirement**: Preserve cause-effect relationships
```
Example:
1. Order created → 2. Inventory decremented → 3. Notification sent

These must maintain order. But other users' orders can be concurrent.

Implementation:
- Version vector clocks
- Kafka partitioning by userId
```

---

## Security Considerations

### 1. Data Security

**PCI DSS Compliance**:
- Never store full credit card numbers
- Use tokenization (Stripe, Square)
- Encrypt data in transit (TLS 1.3)
- Encrypt data at rest
- Regular penetration testing

### 2. Authentication & Authorization

**OAuth 2.0 Flow**:
```
Client → Authorization Server → User consents → Code returned
Code → Backend → Exchange for Access Token
Access Token → Authenticate API calls
```

**Multi-factor Authentication (MFA)**:
- Required for seller accounts
- TOTP or SMS-based
- Fallback recovery codes

### 3. Fraud Detection

**Signals**:
- Unusual geographic location for user
- Payment method different from history
- Rapid consecutive orders
- High-value purchases without history
- Shipping address mismatch with billing

**Implementation**:
- Real-time risk scoring
- Rules engine (if X and Y then flag)
- Machine learning model
- Human review queue

---

## Reliability & Resilience

### 1. Circuit Breaker Pattern

**Scenario**: Payment gateway is down

```
State Transitions:
CLOSED (normal)
  ↓ (failures exceed threshold)
OPEN (reject requests immediately)
  ↓ (wait timeout, allow one request)
HALF_OPEN
  ↓ (request succeeds)
CLOSED
  OR (request fails)
OPEN
```

---

### 2. Bulkhead Pattern

**Isolate resources** between services:
```
Web Server Thread Pool:
- Payments: 50 threads
- Search: 100 threads
- Catalog: 50 threads

If Payments slow, only those threads block
Search continues operating normally
```

---

### 3. Retry Logic

**Exponential Backoff**:
```
Attempt 1: Fail → Wait 100ms
Attempt 2: Fail → Wait 200ms
Attempt 3: Fail → Wait 400ms
Attempt 4: Fail → Wait 800ms
Attempt 5: Fail → Give up

Only retry on idempotent operations
```

---

### 4. Monitoring & Alerting

**Key Metrics**:
```
1. Latency (P50, P95, P99)
2. Error Rate (4xx, 5xx)
3. Throughput (requests/sec)
4. Database connections
5. Cache hit rate
6. Queue depth
```

**Tools**:
- Datadog / New Relic (Monitoring)
- PagerDuty (Alerting)
- ELK Stack (Logging)
- Jaeger (Distributed Tracing)

---

## Interview Questions by Difficulty

### Easy (Warm-up)

1. **Design a URL shortener**
   - Basic hash table approach
   - Collision handling
   - Expiration strategy

2. **Design a parking lot**
   - Class hierarchy
   - Search strategies
   - Reservation system

---

### Medium (Core eBay)

1. **Design eBay Search**
   - Elasticsearch architecture
   - Indexing pipeline
   - Query expansion

2. **Design eBay Order System**
   - Order state machine
   - Payment integration
   - Inventory management

3. **Design Notification Service**
   - Multi-channel (Email, SMS, Push)
   - Retry logic
   - Template management

4. **Design Shopping Cart**
   - Add/remove items
   - Quantity management
   - Price recalculation
   - Checkout flow

5. **Design Product Recommendations**
   - Collaborative filtering
   - Cold start solutions
   - Real-time updates

---

### Hard (Advanced)

1. **Design eBay from Scratch**
   - All services
   - Data consistency
   - Scalability challenges
   - Trade-offs

2. **Design Auction System**
   - Real-time bidding
   - Concurrent bids
   - Sniping prevention
   - Winning logic

3. **Design Seller Analytics Dashboard**
   - Real-time metrics
   - Historical analysis
   - Custom reports
   - Time-series queries

4. **Design Fraud Detection System**
   - Feature extraction
   - Real-time scoring
   - Batch model training
   - Alert prioritization

5. **Design Multi-Region eBay**
   - Data replication
   - Consistency guarantees
   - Cross-region transactions
   - Disaster recovery

---

## Common Mistakes

1. **Not clarifying requirements** → "Assume 100M daily users"
2. **Overcomplicating early** → "Start simple, add complexity if needed"
3. **Ignoring bottlenecks** → "Database becomes bottleneck at 10K QPS"
4. **Not discussing trade-offs** → "Strong consistency vs availability"
5. **Forgetting non-functional requirements** → "LatencyP99 < 500ms"
6. **Not discussing failure scenarios** → "What if payment gateway is down?"
7. **Incomplete API design** → "GET /orders missing filters and pagination"

---

## Interview Success Tips

1. **Ask clarifying questions** (20 seconds)
   - Scale: Users, QPS, data size
   - Consistency: Strong vs eventual
   - Latency: P99 requirements
   - Features: Core vs nice-to-have

2. **Propose high-level design** (5 minutes)
   - Draw boxes for services
   - Connect with arrows
   - Label technologies

3. **Deep dive into 1-2 components** (15 minutes)
   - Pick based on interviewer interest
   - Go deeper (schema, API details)
   - Discuss trade-offs

4. **Discuss scalability** (5 minutes)
   - Database sharding strategy
   - Caching layers
   - Horizontal scaling

5. **Discuss reliability** (5 minutes)
   - Failure scenarios
   - Recovery strategies
   - Monitoring

6. **Be prepared to pivot** (on demand)
   - Change constraints
   - Add complexity
   - Defend your choices

---

## Resources

**System Design Preparation**:
- Alex Xu: "System Design Interview" (2 volumes)
- Educative: "Grokking System Design"
- Glassdoor: eBay System Design questions
- YouTube: eBay engineering talks

**Real eBay Architecture References**:
- eBay Tech Blog: https://innovation.ebaytech.com/
- eBay Engineering talks (YouTube)
- Patent filings: https://patents.google.com/?assignee=ebay

**Key Technologies Used at eBay**:
- **Languages**: Java, Python, Node.js
- **Databases**: PostgreSQL, Cassandra, Elasticsearch
- **Messaging**: Kafka
- **Caching**: Redis, Memcached
- **Container**: Docker, Kubernetes
- **Cloud**: AWS, Azure

---

## Real-World E-Commerce Technical Challenges

### Challenge 1: Flash Sale & Inventory Management

**Problem**: Black Friday sale with 1M+ concurrent users buying limited stock (10K items)

**Why It's Hard**:
- Race conditions: Multiple users buying same item simultaneously
- Overselling: Inventory can go negative if not locked properly
- Fairness: Everyone should have equal chance
- Performance: Must handle 100K+ requests/second

**Design Approach**:

```
Architecture:
┌─────────────────────────┐
│  Load Balancer (NGINX)  │ (Distribute 100K req/s)
└────────────┬────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
┌─────────┐      ┌─────────┐
│App1     │      │App2     │ (100 instances)
│Reserve  │      │Reserve  │
│Service  │      │Service  │
└────┬────┘      └────┬────┘
     │                │
     └────────┬───────┘
              │
        ┌─────▼──────┐
        │Redis Queue │ (Inventory lock)
        └─────┬──────┘
              │
        ┌─────▼──────────────┐
        │PostgreSQL Ledger   │
        │(Source of truth)   │
        └────────────────────┘
```

**Implementation Strategy**:

1. **Pre-Flash Sale** (Preparation Phase):
```java
// Pre-load inventory into Redis with versioning
HSET inventory:product123 available:10000 reserved:0 sold:0 version:1

// Setup distributed lock
SET lock:product123 0 NX EX 300
```

2. **During Sale** (Real-time Phase):
```java
// Atomic inventory check and reserve
// Use Lua script for atomicity in Redis
String script = """
  local available = tonumber(redis.call('HGET', KEYS[1], 'available'))
  if available >= tonumber(ARGV[1]) then
    redis.call('HINCRBY', KEYS[1], 'available', -ARGV[1])
    redis.call('HINCRBY', KEYS[1], 'reserved', ARGV[1])
    return 1
  else
    return 0
  end
""";

// Execute with EVALSHA
boolean reserved = redis.evalSha(script, 1, "inventory:product123", "1");
```

3. **Post-Purchase** (Settlement Phase):
```
Async Job:
1. Every 100ms, batch write Redis changes to PostgreSQL
2. Reconcile final inventory count
3. Flag overselling issues for investigation
4. Trigger fulfillment for sold items

Recovery Logic:
- If Postgres write fails, retry with exponential backoff
- If write partially succeeds, reconcile via transaction log
- Alert ops if discrepancy > 0.1%
```

**Monitoring**:
```
Metrics to Track:
- Inventory write latency (p99 < 10ms)
- Inventory discrepancy (< 0.01%)
- Overselling incidents (target: 0)
- Failed reservations (< 0.1%)
- Queue depth (< 5000 pending)
```

**Trade-offs**:
| Aspect | Choice | Why |
|--------|--------|-----|
| Lock Strategy | Optimistic (Redis) | Lower latency than pessimistic |
| Ledger | Eventual consistency | Accept 1-5 min reconciliation lag |
| Fairness | FIFO queue | Vs fair random access |

---

### Challenge 2: Price Surge During Traffic Spike (Dynamic Pricing)

**Problem**: Prices fluctuate based on demand; ensure consistent pricing throughout user session

**Why It's Hard**:
- Price consistency: User's cart price must match checkout price
- Race conditions: Price updates while user in checkout
- Fairness: All users should see recent price, but shouldn't refresh mid-checkout
- Performance: Must query prices millions of times/second

**Design Approach**:

```
Price Calculation Flow:

1. Browse Phase (Static Pricing):
   User views product → Cache hit (Redis) → Show price from cache

2. Cart Phase (Pinned Pricing):
   Add to cart → Lock price for user session
   Cart expires after 30 minutes → Release lock
   
3. Checkout Phase (Locked Pricing):
   Payment processing → Use pinned price (no refresh)
   Prevents surprise charges

Architecture:
┌────────────────────────────────┐
│   Price Service                │
└────────────┬───────────────────┘
             │
      ┌──────┴────────┐
      │               │
      ▼               ▼
┌──────────┐   ┌────────────────┐
│Redis     │   │PostgreSQL      │
│Cache     │   │(Time-series)   │
│(Hot)     │   │Price history   │
└──────────┘   └────────────────┘

Session Pricing:
┌──────────────────────────────┐
│ Session Price Store (Redis)  │
│ Key: session:user123:prod456 │
│ Value: {price: 99.99,        │
│         timestamp: now,      │
│         locked: true,        │
│         expires: now+30min}  │
└──────────────────────────────┘
```

**Implementation**:

```java
// Step 1: Get current market price
public Price getCurrentPrice(String productId) {
  // Try Redis cache first
  Price cached = redis.get("price:" + productId);
  if (cached != null && !cached.isExpired()) {
    return cached;
  }
  
  // Cache miss: Query database
  Price current = db.queryLatestPrice(productId);
  
  // Cache with TTL (5 minutes)
  redis.setex("price:" + productId, 300, current);
  return current;
}

// Step 2: Pin price when added to cart
public void addToCart(String userId, String productId) {
  Price current = getCurrentPrice(productId);
  
  // Store pinned price (not locked yet)
  redis.setex(
    "cart_price:" + userId + ":" + productId,
    1800,  // 30 minute TTL
    current.serialize()
  );
}

// Step 3: Lock price during checkout
public void lockCheckoutPrice(String userId, String cartId) {
  // Atomic operation: read pinned prices + create lock
  String script = """
    local prices = redis.call('MGET', KEYS[1])
    redis.call('SET', KEYS[2], '1', 'EX', '600')
    return prices
  """;
  
  List<Price> pinnedPrices = redis.evalSha(script, 2,
    "cart_price:" + userId + "*",
    "checkout_lock:" + cartId
  );
  
  // Use pinnedPrices for entire checkout
  return pinnedPrices;
}

// Step 4: Validate price at payment
public boolean validateCheckoutPrice(String cartId, List<Item> items) {
  // Check if lock still valid
  boolean locked = redis.exists("checkout_lock:" + cartId);
  if (!locked) {
    throw new CheckoutExpiredException();
  }
  
  // Verify prices haven't changed beyond threshold
  for (Item item : items) {
    Price stored = redis.get("cart_price:" + item.userId + ":" + item.productId);
    Price current = getCurrentPrice(item.productId);
    
    double threshold = current.getBasePrice() * 0.05; // 5% tolerance
    if (Math.abs(stored.getPrice() - current.getPrice()) > threshold) {
      // Price changed too much - ask user to confirm
      return false;
    }
  }
  
  return true;
}
```

**Recovery**:
```
Scenarios:
1. Price lock expires before checkout → Refresh price, ask user confirmation
2. Price surge > 5% → Notify user, give 30s to confirm
3. Price drop > 5% → Auto-apply discount (good luck!)
4. Database unavailable → Use cached price from 15 min ago
```

---

### Challenge 3: Fraudulent Payment Detection (Real-time Scoring)

**Problem**: Detect fraud in <100ms before charging customer

**Why It's Hard**:
- Real-time: Must score payment before authorization
- Complex patterns: Thousands of fraud signals
- False positives: Block legitimate purchases = lost revenue
- Evolving attacks: New fraud patterns emerge daily
- Latency budget: <50ms for model inference

**Design Approach**:

```
Fraud Detection Pipeline:

Request → Feature Extraction → Model Scoring → Rule Engine → Decision

Architecture:
┌─────────────────────────────────┐
│ Payment Service                 │
└────────────────┬────────────────┘
                 │
        ┌────────▼───────┐
        │Feature Engine  │ (30ms budget)
        └────────┬───────┘
                 │
    ┌────────────┼──────────────┐
    │            │              │
    ▼            ▼              ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│User      │ │Payment   │ │Network   │
│Profile   │ │History   │ │Data      │
│(Redis)   │ │(PG)      │ │(Redis)   │
└──────────┘ └──────────┘ └──────────┘

    └────────────┬──────────────┘
                 │
        ┌────────▼────────┐
        │ML Model         │ (15ms budget)
        │(TensorFlow)     │
        └────────┬────────┘
                 │
        ┌────────▼────────┐
        │Rule Engine      │ (5ms budget)
        │(Hard stops)     │
        └────────┬────────┘
                 │
        ┌────────▼────────┐
        │Decision         │ (Allow/Block/Review)
        └─────────────────┘
```

**Feature Extraction** (30ms):

```java
public FraudFeatures extractFeatures(Payment payment) {
  return FraudFeatures.builder()
    // User features (cached)
    .userAge(getDaysSinceUserCreated(payment.userId))
    .accountRiskScore(redis.get("risk:" + payment.userId))
    .userHistoricalSpend(db.queryLifetimeSpend(payment.userId))
    .userTransactionFrequency(getTransactionsLast24h(payment.userId))
    
    // Payment features
    .amount(payment.amount)
    .amountVsHistorical(payment.amount / userHistoricalAvg)
    .currencyMismatch(payment.currency != userPreferredCurrency)
    .paymentMethodAge(getDaysSincePaymentMethodAdded(payment.paymentId))
    .paymentMethodUsageFrequency(redis.incr("pm_usage:" + payment.paymentId))
    
    // Geographic features
    .locationChange(calculateLocationDistance(lastLocation, currentLocation))
    .timezoneChange(calculateTimezoneDifference(lastLocation, currentLocation))
    .usualCountry(payment.country != userUsualCountry)
    .vpnDetected(geoIPService.isVPN(payment.ipAddress))
    
    // Network features
    .deviceFirstSeen(!deviceCache.contains(payment.deviceId))
    .deviceRisk(getDeviceRiskScore(payment.deviceId))
    .ipReputation(ipReputationService.getScore(payment.ipAddress))
    .vpnScore(vpnDetectionService.getScore(payment.ipAddress))
    
    // Behavioral features
    .cartAbandonmentRate(userBehavior.abandonmentRate)
    .browserUserAgentEntropy(calculateUserAgentSuspicion(payment.userAgent))
    .clickStreamBotlikeness(detectBotlikeClickPattern(payment.sessionId))
    
    .build();
}
```

**ML Model Scoring** (15ms):

```
Model Training:
- Algorithm: Gradient Boosted Trees (XGBoost) or Random Forest
- Training Data: 1 year of historical fraud labels
- Features: 50+ features
- Output: Fraud probability (0.0 - 1.0)

Model Serving:
- Framework: TensorFlow Serving or KServe
- Deployment: GPU instances for fast inference
- Latency: <10ms p99
- Updates: New model deployed weekly with A/B testing
```

**Rule Engine** (5ms):

```java
public FraudDecision makeDecision(Payment payment, FraudScore score) {
  // Hard stops (instant block)
  if (score.mlScore > 0.95) {
    return BLOCK("Model score too high");
  }
  
  if (isBlacklistedCard(payment.cardNumber)) {
    return BLOCK("Card blacklisted");
  }
  
  if (payment.amount > 10000 && score.mlScore > 0.50) {
    return BLOCK("High amount + suspicious pattern");
  }
  
  // Review queue (manual inspection)
  if (score.mlScore > 0.70) {
    return REVIEW("Manual review needed");
  }
  
  if (isHighRiskCountry(payment.country) && score.mlScore > 0.40) {
    return REVIEW("High risk country");
  }
  
  if (locationDistance > 5000km && timezoneJump > 8h && score.mlScore > 0.30) {
    return REVIEW("Impossible travel detected");
  }
  
  // Allow with monitoring
  if (score.mlScore > 0.20) {
    return ALLOW("Flagged for post-transaction monitoring");
  }
  
  // Clean allow
  return ALLOW();
}
```

**Monitoring & Feedback Loop**:

```
1. Track metrics:
   - False positive rate (target: <0.5%)
   - False negative rate (fraud caught / fraud that slipped)
   - Model drift (performance degradation over time)
   - Review queue accuracy (manual reviewers)

2. Daily retraining:
   - Collect new labels from manual reviews
   - Detect fraud patterns that got through
   - Retrain model with latest fraud trends
   - A/B test new model before deployment

3. Feedback loop:
   Fraud incident → Root cause analysis → Feature engineering → Model update
```

---

### Challenge 4: Search Relevance at Scale (Elasticsearch Tuning)

**Problem**: Find relevant products among 1B+ items in <100ms with good ranking

**Why It's Hard**:
- Scale: Billions of products, trillions of possible queries
- Relevance: Ranking by relevance, not just keyword match
- Personalization: Different users want different results
- Recency: New listings should appear in results
- Freshness: Index updates lag behind inventory

**Design Approach**:

```
Search Request Flow:

User Query → Query Parsing → Elasticsearch → Ranking → Response
                                (50ms)        (30ms)    (20ms)

Multi-level Architecture:
┌──────────────────────────────┐
│ Query Layer                  │
│ - Typo correction           │
│ - Query expansion           │
│ - Query caching             │
└────────────┬─────────────────┘
             │
┌────────────▼─────────────────────────────┐
│ Elasticsearch Cluster                   │
│ - 100 nodes, 500 shards                 │
│ - Search replicas for availability     │
│ - Tiered storage (hot/warm/cold)       │
└────────────┬─────────────────────────────┘
             │
┌────────────▼─────────────────────────────┐
│ Ranking Service                         │
│ - ML ranking (LambdaMART)              │
│ - Business rules                        │
│ - Personalization                      │
└──────────────────────────────────────────┘
```

**Indexing Strategy**:

```java
// Product Mapping
{
  "mappings": {
    "properties": {
      // Text fields - analyzed
      "title": {
        "type": "text",
        "analyzer": "standard",
        "fields": {
          "keyword": {"type": "keyword"},
          "english": {"type": "text", "analyzer": "english"}
        }
      },
      
      // Numeric fields - aggregatable
      "price": {
        "type": "scaled_float",
        "scaling_factor": 100
      },
      "category_id": {"type": "keyword"},
      
      // Dates
      "listed_at": {"type": "date"},
      
      // Geo
      "seller_location": {"type": "geo_point"},
      
      // Denormalized for relevance
      "seller_rating": {"type": "float"},
      "sales_velocity": {"type": "float"},
      "review_count": {"type": "integer"},
      
      // Nested for faceting
      "attributes": {
        "type": "nested",
        "properties": {
          "name": {"type": "keyword"},
          "value": {"type": "keyword"}
        }
      }
    }
  }
}
```

**Query Optimization**:

```java
// Multi-field ranking query
public SearchResults search(String query, SearchFilters filters) {
  BoolQuery boolQuery = QueryBuilders.boolQuery()
    // Must match query
    .must(QueryBuilders.multiMatchQuery(query)
      .field("title", 3.0f)        // Title matches worth 3x
      .field("description", 1.5f)
      .field("brand", 2.0f)
      .fuzziness("AUTO")           // Handle typos
      .analyzer("standard")
    )
    
    // Filter by category
    .filter(QueryBuilders.termQuery("category_id", filters.categoryId))
    
    // Exclude certain conditions
    .mustNot(QueryBuilders.termQuery("status", "delisted"));
  
  SearchRequest request = new SearchRequest("products")
    .source(new SearchSourceBuilder()
      .query(boolQuery)
      .from(filters.offset)
      .size(20)
      .timeout(new TimeValue(100, TimeUnit.MILLISECONDS))
      
      // Scoring
      .explain(false)
      
      // Aggregations for facets
      .aggregation(AggregationBuilders
        .terms("categories")
        .field("category_id")
        .size(20)
      )
    );
  
  return client.search(request);
}
```

**Ranking (Post-Search)**:

```java
public List<Product> rankResults(List<Product> searchResults,
                                  String query,
                                  User user) {
  return searchResults.stream()
    .map(product -> {
      double score = product.getElasticsearchScore(); // Base score
      
      // Business signals
      score += product.getSellerRating() * 0.5;       // Rating influence
      score += Math.log(product.getSalesVelocity());  // Popular = relevant
      score += (System.currentTimeMillis() - product.getListedAt()) * -0.0001;
      
      // Personalization
      if (user.getPreferedBrand().equals(product.getBrand())) {
        score *= 1.5;  // Boost brand preferences
      }
      
      if (user.getBrowsingHistory().contains(product.getCategory())) {
        score *= 1.2;  // Boost categories user browses
      }
      
      // ML model scoring
      FraudFeatures features = new FraudFeatures()
        .elasticsearchScore(product.getElasticsearchScore())
        .sellerRating(product.getSellerRating())
        .priceCompetitiveness(calculateCompetitiveness(product.getPrice()))
        .listingAge(calculateListingAge(product.getListedAt()))
        .userRelevance(calculateRelevance(user, product));
      
      double mlScore = rankingModel.predict(features);  // ML model
      
      return new RankedProduct(product, score * mlScore);
    })
    .sorted(Comparator.reverseOrder())
    .map(RankedProduct::getProduct)
    .collect(toList());
}
```

**Caching Strategy**:

```
Cache Layers:
1. Query Cache (Redis)
   - Popular queries cached for 1 hour
   - Key: hash(query + filters)
   - Invalidated on product updates

2. Suggestion Cache
   - Autocomplete suggestions (top 1000)
   - Updated hourly

3. Aggregation Cache
   - Category facets
   - Price ranges
   - Updated every 5 minutes
```

---

### Challenge 5: Returns & Refunds Processing (Complex State Machine)

**Problem**: Process returns across multiple warehouses with refund coordination

**Why It's Hard**:
- Multiple state machines: Return item pickup, inspection, refund processing
- Concurrency: Buyer and seller both can initiate actions
- Refund splitting: Refund to original payment method, store credit, etc.
- Timeouts: Different actions have different TTLs
- Idempotency: Same action should have same result if retried

**Design Approach**:

```
Return State Machine:

INITIATED → AWAITING_PICKUP → IN_TRANSIT → RECEIVED → INSPECTING → 
  APPROVED → REFUNDING → REFUNDED
                    ↓
                  REJECTED → RETURN_SHIPPING

Architecture:
┌────────────────────────────────┐
│ Return Service                 │
└────────────┬───────────────────┘
             │
      ┌──────┴──────┐
      │             │
      ▼             ▼
  PostgreSQL   Kafka
  (Ledger)   (Events)
             ├─return.initiated
             ├─return.pickup_scheduled
             ├─return.item_received
             ├─return.inspected
             ├─return.approved
             └─return.refunded
```

**State Transitions**:

```java
public enum ReturnState {
  INITIATED,
  AWAITING_PICKUP,
  IN_TRANSIT,
  RECEIVED,
  INSPECTING,
  APPROVED,
  REFUNDING,
  REFUNDED,
  REJECTED,
  RETURN_SHIPPING,
  COMPLETED
}

public class ReturnProcessing {
  public ReturnState processReturn(Return ret, Action action) {
    switch (ret.getStatus()) {
      case INITIATED:
        if (action == Action.REQUEST_PICKUP) {
          // Validate return window (14 days)
          if (isWithinReturnWindow(ret.getOrderDate())) {
            return AWAITING_PICKUP;
          } else {
            return REJECTED;
          }
        }
        break;
        
      case AWAITING_PICKUP:
        if (action == Action.PICKUP_SCHEDULED) {
          // Create shipping label
          createShippingLabel(ret);
          return IN_TRANSIT;
        }
        if (action == Action.CANCEL_RETURN) {
          // Within 24h of requesting
          if (System.currentTimeMillis() - ret.getInitiatedAt() < 86400000) {
            return COMPLETED;  // Return cancelled
          }
        }
        break;
        
      case IN_TRANSIT:
        if (action == Action.ITEM_RECEIVED) {
          // Validate warehouse received it
          verifyWarehouseReceipt(ret);
          return RECEIVED;
        }
        if (action == Action.TIMEOUT) {
          // After 30 days in transit
          return REJECTED;  // Assume lost
        }
        break;
        
      case RECEIVED:
        // Start inspection
        scheduleInspection(ret);
        return INSPECTING;
        
      case INSPECTING:
        if (action == Action.INSPECTION_PASSED) {
          return APPROVED;
        } else if (action == Action.INSPECTION_FAILED) {
          // Item damaged, wrong condition, etc.
          return REJECTED;
        }
        break;
        
      case APPROVED:
        if (action == Action.REFUND_INITIATED) {
          return REFUNDING;
        }
        break;
        
      case REFUNDING:
        if (action == Action.REFUND_COMPLETED) {
          sendRefundNotification(ret);
          return REFUNDED;
        }
        if (action == Action.REFUND_FAILED) {
          // Retry next day
          scheduleRetry(ret);
        }
        break;
        
      // ... other cases
    }
    return ret.getStatus();  // No state change
  }
}
```

**Refund Coordination**:

```java
public void processRefund(Return ret) {
  // Idempotency key prevents double refund
  String idempotencyKey = "refund:" + ret.getId();
  
  // Check if already processed
  if (redis.exists(idempotencyKey)) {
    return;  // Already processed
  }
  
  // Atomic refund
  try {
    RefundRecord record = new RefundRecord()
      .setReturnId(ret.getId())
      .setAmount(ret.getRefundAmount())
      .setTimestamp(System.currentTimeMillis());
    
    // Step 1: Reverse payment authorization
    paymentService.refundToOriginalPayment(
      ret.getOrderId(),
      ret.getRefundAmount(),
      idempotencyKey
    );
    
    // Step 2: If original payment method unavailable, offer alternatives
    if (paymentService.cannotRefundOriginal()) {
      // Option 1: Store credit
      sellerService.addStoreCredit(
        ret.getBuyerId(),
        ret.getRefundAmount()
      );
      
      // Option 2: Alternative payment method
      paymentService.refundToAlternateMethod(
        ret.getAlternatePaymentMethod(),
        ret.getRefundAmount()
      );
    }
    
    // Step 3: Update inventory (add back unsold count)
    inventoryService.addBack(
      ret.getProductId(),
      ret.getQuantity()
    );
    
    // Step 4: Mark refund complete
    db.updateReturnStatus(ret.getId(), REFUNDED);
    
    // Step 5: Store idempotency result
    redis.setex(idempotencyKey, 604800, "COMPLETED");  // 7 days
    
    // Step 6: Emit event
    kafkaService.publish("return.refunded", ret);
    
  } catch (Exception e) {
    logger.error("Refund failed for return: " + ret.getId(), e);
    scheduleRetry(ret);  // Retry with exponential backoff
  }
}
```

---

### Challenge 6: Seller Account Abuse Detection (Behavioral Analysis)

**Problem**: Detect fake sellers, dropshipping, account takeovers in real-time

**Why It's Hard**:
- Sophisticated attacks: Fake reviews, artificial sales velocity
- Global scale: Different fraud patterns per region
- Latency: Must detect during listing creation (<100ms)
- False positives: Legitimate sellers shouldn't be suspended
- Evolving: Fraudsters learn from blocked tactics

**Design Approach**:

```
Detection Pipeline:

Seller Action → Risk Scoring → Rule Engine → Suspension/Review

Risk Scoring:
1. Account Age (New accounts = higher risk)
2. Sales Velocity (Too fast = suspicious)
3. Price Competitiveness (Underpriced = dropshipper)
4. Review Quality (Fake reviews pattern)
5. Category Mixing (Too many different categories)
6. Chargeback Rate (Too many disputes)
7. Listing Velocity (Too many listings at once)
8. IP/Device Pattern (Multiple accounts from same IP)

Architecture:
┌─────────────────────────────┐
│ Real-time Stream Processing │
│ (Kafka + Flink)             │
└────────────┬────────────────┘
             │
    ┌────────▼────────┐
    │ Feature Window  │ (Sliding 24h)
    │ (Aggregations)  │
    └────────┬────────┘
             │
    ┌────────▼────────────────┐
    │ Risk Score Model        │
    │ (Isolation Forest)      │
    └────────┬────────────────┘
             │
    ┌────────▼────────────────┐
    │ Decision Engine         │
    │ Block/Review/Allow      │
    └────────────────────────┘
```

**Risk Scoring Calculation**:

```java
public double calculateSellerRiskScore(Seller seller) {
  double riskScore = 0.0;
  
  // Account Age Risk
  long accountAgeDays = (System.currentTimeMillis() - seller.getCreatedAt()) / (1000 * 86400);
  if (accountAgeDays < 30) {
    riskScore += 30;  // New account = high risk
  } else if (accountAgeDays < 90) {
    riskScore += 15;
  }
  
  // Sales Velocity Risk
  long salesLast7Days = seller.getSalesLast7Days();
  double expectedVelocity = seller.getAverageSalesPerDay() * 7;
  double velocityRatio = salesLast7Days / expectedVelocity;
  
  if (velocityRatio > 5) {
    riskScore += 40;  // 5x normal velocity
  } else if (velocityRatio > 3) {
    riskScore += 20;
  }
  
  // Price Competitiveness Risk
  double avgListingPrice = seller.getAverageListingPrice();
  double marketAvgPrice = marketPriceService.getAveragePrice(seller.getPrimaryCategory());
  double priceRatio = avgListingPrice / marketAvgPrice;
  
  if (priceRatio < 0.7) {
    riskScore += 30;  // Undercut by 30%+ = suspicious
  } else if (priceRatio < 0.85) {
    riskScore += 10;
  }
  
  // Review Authenticity Risk
  double reviewAuth = analyzeReviewAuthenticity(seller);
  if (reviewAuth < 0.5) {
    riskScore += 35;  // Likely fake reviews
  }
  
  // Category Mixing Risk
  int uniqueCategories = seller.getUniqueListingCategories();
  if (uniqueCategories > 50) {
    riskScore += 20;  // Too diverse
  }
  
  // Chargeback Rate Risk
  double chargebackRate = seller.getTotalChargebacks() / seller.getTotalSales();
  if (chargebackRate > 0.05) {
    riskScore += 40;  // >5% chargebacks = suspicious
  }
  
  // IP/Device Pattern Risk
  int accountsFromSameIP = accountService.countAccountsFromIP(seller.getLastIP());
  if (accountsFromSameIP > 5) {
    riskScore += 35;  // Multiple accounts from same IP
  }
  
  return riskScore;  // 0-100+ scale
}
```

**Decision Making**:

```java
public SellerAction makeDecision(double riskScore, Seller seller) {
  if (riskScore > 80) {
    // Immediate suspension
    return SellerAction.SUSPEND(
      reason: "High-risk seller behavior",
      appeal: true
    );
  }
  
  if (riskScore > 60) {
    // Manual review
    return SellerAction.REVIEW_QUEUE(
      priority: HIGH,
      reassess_in: 48h
    );
  }
  
  if (riskScore > 40) {
    // Monitor closely
    return SellerAction.HEIGHTENED_MONITORING(
      additional_verification: true,
      manual_review_percentage: 20  // Check 20% of listings
    );
  }
  
  if (riskScore > 20) {
    // Standard monitoring
    return SellerAction.ALLOW(
      monitor: true
    );
  }
  
  // Clean seller
  return SellerAction.ALLOW();
}
```

---

### Challenge 7: Multi-Warehouse Inventory Synchronization

**Problem**: Keep 50+ warehouses synchronized when inventory changes; latency <500ms

**Why It's Hard**:
- Eventual consistency: WH1 updates stock, WH2 doesn't know for seconds
- Network partitions: What if WH2 loses connection?
- Overselling: Both warehouses might sell same item if not careful
- Rebalancing: Move stock between warehouses based on demand
- Regional optimization: Minimize shipping costs

**Design Approach**:

```
Multi-Warehouse Sync:

Central Authority (PostgreSQL Master):
  - Single source of truth for inventory
  - One row per (product, warehouse)
  
Local Caches (Redis per WH):
  - Fast local reads
  - Async sync to master
  - Conflict resolution: Last-write-wins

Data Flow:
┌──────────────────┐
│ Warehouse System │ (10 locations)
│ (MySQL read-only)│
└────────┬─────────┘
         │
  ┌──────▼────────────┐
  │ Message Queue     │ (Kafka)
  │ inventory.updated │
  └──────┬────────────┘
         │
  ┌──────▼────────────┐
  │ Central Inventory │
  │ Service (PG)      │
  └──────┬────────────┘
         │
  ┌──────▼────────────┐
  │ Redis Cache Layer │
  │ (per warehouse)   │
  └───────────────────┘
```

**Synchronization Protocol**:

```java
// Local warehouse update (fast)
public void reserveLocalInventory(String productId, int quantity, String warehouseId) {
  // Step 1: Update local Redis immediately
  redis.decrby("inventory:" + productId + ":" + warehouseId, quantity);
  
  // Step 2: Queue for central sync (async)
  kafka.publish("inventory.reserved", {
    productId,
    warehouseId,
    quantity,
    timestamp: System.currentTimeMillis(),
    idempotencyKey: UUID.randomUUID()
  });
  
  // Return immediately to user (optimistic)
}

// Central coordinator (eventual consistency)
public void processInventoryUpdate(InventoryEvent event) {
  // Idempotency: Check if already processed
  if (redis.get("processed:" + event.idempotencyKey) != null) {
    return;  // Already processed
  }
  
  try {
    // Atomic update in database
    db.transaction(() -> {
      InventoryRecord record = db.queryForUpdate(
        event.productId,
        event.warehouseId
      );
      
      // Validate not overselling
      if (record.available >= event.quantity) {
        record.available -= event.quantity;
        record.reserved += event.quantity;
        record.lastUpdated = System.currentTimeMillis();
        db.update(record);
      } else {
        // Insufficient inventory - rollback local reservation
        kafka.publish("inventory.failed", event);
      }
    });
    
    // Mark as processed
    redis.setex("processed:" + event.idempotencyKey, 604800, "YES");
    
  } catch (Exception e) {
    // Retry with exponential backoff
    scheduleRetry(event);
  }
}

// Reconciliation job (daily)
public void reconcileInventory() {
  for (String warehouseId : getAllWarehouses()) {
    // Compare local cache vs central database
    Map<String, Integer> localCache = redis.getAllInventory(warehouseId);
    Map<String, Integer> centralDB = db.queryWarehouseInventory(warehouseId);
    
    for (String productId : localCache.keySet()) {
      int localAmount = localCache.get(productId);
      int centralAmount = centralDB.get(productId);
      
      if (localAmount != centralAmount) {
        // Discrepancy found - which is source of truth?
        // Answer: Central DB
        redis.set(
          "inventory:" + productId + ":" + warehouseId,
          centralAmount
        );
        
        // Log for investigation
        logger.warn(f"Inventory discrepancy for {productId} in {warehouseId}");
      }
    }
  }
}
```

---

### Challenge 8: Review & Rating Authenticity (Spam Detection)

**Problem**: Detect fake reviews while allowing legitimate feedback at scale

**Why It's Hard**:
- Sophisticated fraud: Coordinated fake reviewers, stolen accounts
- Time-sensitive: Must catch spam before it damages reputation
- Scale: 100M+ reviews annually
- Fairness: Legitimate low ratings shouldn't be flagged
- Latency: Real-time detection needed

**Design Approach**:

```
Review Authenticity Check:

Submission → NLP Analysis → Reviewer Profile → Pattern Detection → Accept/Flag

Factors:
1. Text Analysis (NLP):
   - Similarity to other reviews (plagiarism check)
   - Linguistic patterns (AI-generated?)
   - Sentiment consistency with rating
   
2. Reviewer Profile:
   - New account? (higher risk)
   - Review history (quality consistent?)
   - Verification (email, phone)
   
3. Pattern Detection:
   - Same IP multiple reviews
   - Timing patterns (all in one day?)
   - Reviewer bought this product?
```

**Implementation**:

```java
public ReviewDecision validateReview(Review review, Reviewer reviewer) {
  double authScore = 0.0;  // 0-100 scale
  List<String> flags = new ArrayList<>();
  
  // 1. NLP Analysis
  String reviewText = review.getText();
  
  // Check for plagiarism
  List<Review> similarReviews = elasticsearch
    .moreLikeThis(reviewText, "reviews_index");
  if (similarReviews.size() > 2) {
    authScore += 20;
    flags.add("Similar to " + similarReviews.size() + " other reviews");
  }
  
  // Check sentiment consistency
  double sentimentScore = nlpService.analyzeSentiment(reviewText);  // -1 to 1
  int rating = review.getRating();  // 1-5 stars
  
  double expectedSentiment = (rating - 3) / 2.0;  // 3 stars = neutral
  if (Math.abs(sentimentScore - expectedSentiment) > 0.5) {
    authScore += 15;
    flags.add("Sentiment inconsistent with rating");
  }
  
  // Check for AI-generated text
  if (aiTextDetector.isLikelyAI(reviewText)) {
    authScore += 25;
    flags.add("Likely AI-generated text");
  }
  
  // 2. Reviewer Profile Checks
  long accountAgeDays = getDaysSinceAccountCreated(reviewer);
  if (accountAgeDays < 30) {
    authScore += 15;
    flags.add("Brand new account");
  }
  
  // Check if reviewer bought this product
  boolean purchased = orderService.didReviewerPurchaseProduct(
    reviewer.getId(),
    review.getProductId()
  );
  if (!purchased) {
    authScore += 30;
    flags.add("Reviewer didn't purchase product");
  }
  
  // Check verification status
  if (!reviewer.isEmailVerified()) {
    authScore += 5;
  }
  if (!reviewer.isPhoneVerified()) {
    authScore += 10;
  }
  
  // 3. Pattern Detection
  // Reviews from same IP/device
  int reviewsFromSameIP = db.countReviewsFromIP(reviewer.getLastIP());
  if (reviewsFromSameIP > 10) {
    authScore += 20;
    flags.add("Many reviews from same IP");
  }
  
  // Timing pattern: All reviews in short time?
  List<Review> reviewerHistory = db.getReviewerRecentReviews(reviewer.getId(), 24);
  if (reviewerHistory.size() > 5) {
    authScore += 15;
    flags.add("Unnatural review rate (>5 reviews in 24h)");
  }
  
  // 4. Decision
  if (authScore > 75) {
    return ReviewDecision.REJECT(
      reason: "Likely fraudulent review",
      flags: flags,
      appeal: true
    );
  }
  
  if (authScore > 50) {
    return ReviewDecision.MANUAL_REVIEW(
      priority: MEDIUM,
      flags: flags
    );
  }
  
  // Publish event for analytics
  kafka.publish("review.submitted", review);
  
  return ReviewDecision.ACCEPT();
}
```

---

### Challenge 9: Personalized Product Recommendations at Scale

**Problem**: Generate personalized recommendations for 100M+ users in real-time

**Why It's Hard**:
- Cold start: New users have no history
- Scalability: Train model on 1B+ interactions
- Latency: Return top-10 results in <100ms
- Diversity: Don't recommend same items repeatedly
- Freshness: New products should get recommendations

**Design Approach**:

```
Recommendation System:

Two-Tower Model:

┌─────────────┐              ┌──────────────┐
│ User Tower  │              │ Item Tower   │
│ (User embed)│              │(Item embed)  │
└──────┬──────┘              └──────┬───────┘
       │                            │
       │    Cosine Similarity       │
       └────────────┬───────────────┘
                    │
            ┌───────▼────────┐
            │ Top K Similar  │
            │ Items          │
            └────────────────┘

Architecture:
┌────────────────────────────────┐
│ Recommendation Service         │
└──────────┬─────────────────────┘
           │
    ┌──────┴──────┐
    │             │
    ▼             ▼
User Vec      Item Vec
Cache(Redis)  Cache(Redis)
    
    ├─ Updated daily via Spark
    ├─ 512-d embeddings
    └─ Quantized to int8 for speed
```

**Training Pipeline**:

```
Day 1: Training
  - Collect interactions (100B events)
  - Build user-item interaction matrix
  - Train TensorFlow Two-Tower model
  - Validate accuracy (NDCG@10)
  
Day 2: Deployment
  - Generate embeddings for all users/items
  - Store in Redis with TTL
  - Shadow model runs in parallel
  
Day 3: Monitoring
  - Track CTR, conversion, diversity
  - Compare with control group
  - Ready to rollback if issues

Metrics:
- NDCG (Normalized DCG)
- HitRate@10
- Diversity (unique items)
- Coverage (% items recommended)
```

**Real-time Inference**:

```java
public List<Product> getRecommendations(String userId, int topK) {
  // Step 1: Get user embedding (cached)
  float[] userEmbedding = redis.getEmbedding("user_vec:" + userId);
  
  if (userEmbedding == null) {
    // Cold start: new user
    return getPopularProducts(topK);
  }
  
  // Step 2: Search item embeddings using approximate nearest neighbor
  List<String> nearestItemIds = vectorDb.search(
    embedding: userEmbedding,
    topK: topK * 2,  // Get more, then filter
    index: "item_embeddings_quantized"  // Use quantized for speed
  );
  
  // Step 3: Fetch item details and rank
  List<Product> candidates = db.queryProducts(nearestItemIds);
  
  List<Product> recommendations = candidates.stream()
    .filter(item -> {
      // Filter out: already purchased, out of stock, blacklisted
      return !userService.hasPurchased(userId, item.getId()) &&
             item.isInStock() &&
             !isBlacklisted(item);
    })
    .map(item -> {
      // Re-rank with business signals
      item.setRecommendationScore(
        calculateBusinessScore(item, userId)
      );
      return item;
    })
    .sorted(Comparator.reverseOrder())
    .limit(topK)
    .collect(toList());
  
  return recommendations;
}

private double calculateBusinessScore(Product item, String userId) {
  double score = item.getEmbeddingScore();  // From nearest neighbor
  
  // Boost signals
  score *= (1 + Math.log(item.getSalesVelocity() + 1) * 0.1);
  
  // Freshness boost for new items
  if (getDaysSinceListed(item) < 7) {
    score *= 1.2;
  }
  
  // Seller reputation
  score *= (1 + item.getSellerRating() * 0.1);
  
  // Diversity: penalize items similar to others in list
  // (Prevent all recommending same brand)
  
  return score;
}
```

**Cold Start Handling**:

```
New User (0 interactions):
  1. Use demographic matching
     - Country, language, device type
     - Find similar users
     - Recommend their favorites
  
  2. Use popularity ranking
     - Top sellers in categories
     - Best rated products
     - Trending items
  
  3. Use product features
     - Category, price range, shipping
     - Match to user profile

New Product (0 interactions):
  1. Use content-based features
     - Title, category, attributes
     - Find similar products users like
  
  2. Use item tags
     - Similar to products user viewed
  
  3. Give new items visibility boost
     - Explore-exploit tradeoff
     - Show to some users, measure response
```

---

### Challenge 10: Cart Persistence & Checkout Optimization

**Problem**: Maintain consistent cart across web/mobile with sub-100ms latency, prevent race conditions

**Why It's Hard**:
- Multi-device: User switches between phone and web
- Offline support: Mobile app works offline
- Race conditions: Add item from both devices simultaneously
- Price changes: Item price changed while in cart
- Inventory: Item went out of stock

**Design Approach**:

```
Cart Data Model:

┌──────────────────────────────┐
│ Cart Service                 │
└──────────────┬───────────────┘
               │
      ┌────────┴────────┐
      │                 │
      ▼                 ▼
   Redis          PostgreSQL
  (Cache,        (Audit Trail,
   Fast)          Durable)
      │                 │
      └────────┬────────┘
               │
      ┌────────▼────────┐
      │ Offline Queue   │
      │ (Local Mobile)  │
      └─────────────────┘

Cart States:
1. Active: User adding/removing items
2. Checkout: Payment in progress
3. Completed: Order created
4. Abandoned: Not accessed in 30 days
```

**Cart Operations**:

```java
public class CartService {
  private static final long CART_TTL = 2592000;  // 30 days
  
  // Add item - handle race conditions
  public CartItem addItem(String userId, String productId, int quantity) {
    String cartKey = "cart:" + userId;
    String itemKey = cartKey + ":item:" + productId;
    
    // Check inventory first
    int available = inventoryService.checkAvailability(productId);
    if (available < quantity) {
      throw new InsufficientInventoryException(available);
    }
    
    // Atomic add to cart
    Map<String, String> itemData = new HashMap<>();
    itemData.put("productId", productId);
    itemData.put("quantity", String.valueOf(quantity));
    itemData.put("addedAt", String.valueOf(System.currentTimeMillis()));
    itemData.put("price", String.valueOf(
      productService.getCurrentPrice(productId)
    ));
    
    // Use Redis transaction for atomicity
    redis.multi();
    redis.hset(itemKey, itemData);
    redis.expire(itemKey, CART_TTL);
    redis.lpush(cartKey + ":items", productId);
    redis.expire(cartKey, CART_TTL);
    redis.exec();
    
    // Audit trail (async)
    kafka.publish("cart.item.added", {
      userId, productId, quantity, timestamp: now()
    });
    
    return new CartItem(productId, quantity);
  }
  
  // Get cart with validation
  public Cart getCart(String userId) {
    String cartKey = "cart:" + userId;
    
    // Get all items
    List<String> itemIds = redis.lrange(cartKey + ":items", 0, -1);
    List<CartItem> items = new ArrayList<>();
    double totalPrice = 0;
    
    for (String itemId : itemIds) {
      String itemKey = cartKey + ":item:" + itemId;
      Map<String, String> itemData = redis.hgetall(itemKey);
      
      // Validate item still exists
      if (!productService.exists(itemId)) {
        redis.hdel(itemKey, "*");  // Remove invalid item
        continue;
      }
      
      // Check if price changed too much
      double oldPrice = Double.parseDouble(itemData.get("price"));
      double currentPrice = productService.getCurrentPrice(itemId);
      
      if (currentPrice != oldPrice) {
        // Price changed - notify user
        addPriceChangeNotification(userId, itemId, oldPrice, currentPrice);
        
        // Update price in cart
        redis.hset(itemKey, "price", String.valueOf(currentPrice));
      }
      
      // Add to cart
      CartItem item = new CartItem(
        itemId,
        Integer.parseInt(itemData.get("quantity")),
        currentPrice
      );
      items.add(item);
      totalPrice += item.getPrice() * item.getQuantity();
    }
    
    return new Cart(userId, items, totalPrice);
  }
  
  // Merge offline changes (mobile sync)
  public Cart mergeOfflineChanges(String userId, List<CartChange> offlineChanges) {
    // Get current cart
    Cart currentCart = getCart(userId);
    
    // Replay offline changes
    for (CartChange change : offlineChanges) {
      if (change.getAction() == Action.ADD) {
        addItem(userId, change.getProductId(), change.getQuantity());
      } else if (change.getAction() == Action.REMOVE) {
        removeItem(userId, change.getProductId());
      } else if (change.getAction() == Action.UPDATE_QUANTITY) {
        updateQuantity(userId, change.getProductId(), change.getQuantity());
      }
    }
    
    // Return merged cart
    return getCart(userId);
  }
  
  // Checkout - lock cart
  public Order checkout(String userId, PaymentInfo payment) {
    String cartKey = "cart:" + userId;
    
    // Lock cart for checkout
    String lockKey = cartKey + ":checkout_lock";
    if (!redis.setIfNotExists(lockKey, "1", 600)) {  // 10 min lock
      throw new CheckoutInProgressException();
    }
    
    try {
      // Validate inventory for all items
      Cart cart = getCart(userId);
      for (CartItem item : cart.getItems()) {
        int available = inventoryService.reserveInventory(
          item.getProductId(),
          item.getQuantity()
        );
        if (available < item.getQuantity()) {
          throw new InsufficientInventoryException();
        }
      }
      
      // Process payment
      PaymentResult result = paymentService.charge(payment, cart.getTotal());
      
      // Create order
      Order order = orderService.createOrder(userId, cart, result.getTransactionId());
      
      // Clear cart
      redis.del(cartKey);
      
      return order;
      
    } finally {
      // Release lock
      redis.del(lockKey);
    }
  }
}
```

---

## Summary

**Key Designs to Master for eBay**:
1. ✅ E-Commerce Platform Architecture
2. ✅ Search Service
3. ✅ Order & Payment System
4. ✅ Notification Service
5. ✅ Recommendation Engine
6. ✅ Auction System
7. ✅ Inventory Management
8. ✅ Shipping & Logistics

**Critical Trade-offs**:
- Consistency vs Availability
- Latency vs Throughput
- Storage vs Computation
- Operational Complexity vs Feature Richness

**Most Asked Topics** (2023-2025):
1. Microservices decomposition
2. Database sharding strategies
3. Caching patterns
4. Event-driven architecture
5. Order processing pipeline
6. Payment system design
7. Search optimization
8. Real-time recommendations

---

**Last Updated**: December 2025
**Hiring Season Covered**: 2023-2025
**Interview Format**: System Design Round (60-75 minutes)

