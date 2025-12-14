# 100 Advanced System Design Challenges - MAANG & E-Commerce Focus

**Part 2: Challenges 101-200 with MAANG Interview & E-Commerce Domain Focus**

---

## Table of Contents

| Section | Challenges | Count | Focus |
|---------|-----------|-------|-------|
| [E-Commerce Core](#ecommerce-core-101-120) | 101-120 | 20 | Product catalog, inventory, checkout |
| [Payment & Transactions](#payment--transactions-121-135) | 121-135 | 15 | Payment processing, fraud, refunds |
| [Recommendation Engine](#recommendation-engine-136-150) | 136-150 | 15 | Personalization, collaborative filtering |
| [Search & Discovery](#search--discovery-151-165) | 151-165 | 15 | Full-text, filters, relevance ranking |
| [Inventory & Fulfillment](#inventory--fulfillment-166-180) | 166-180 | 15 | Stock management, logistics, shipping |
| [MAANG Backend Patterns](#maang-backend-patterns-181-200) | 181-200 | 20 | Real MAANG systems (Meta, Google, Amazon, etc.) |

---

## E-Commerce Core (101-120)

### Challenge 101: Product Catalog at Billion Scale
**Problem:** Design a product catalog like Amazon/Flipkart with 1B+ products, search in < 100ms

**Tool:** **Elasticsearch + DynamoDB + Redis**

**Architecture:**
```
User Search Query ("iphone 14")
    ↓
Redis (Cache layer)
    ├─ HIT (previous search) → 10ms
    └─ MISS
        ↓
    Elasticsearch
    ├─ Full-text search
    ├─ Filters (price, rating, brand)
    ├─ Facets aggregation
    └─ Relevance scoring
    ↓
    DynamoDB (Full details)
    ├─ product_id, images, specs
    ├─ reviews, ratings
    └─ inventory, pricing
    ↓
Redis Cache (5-minute TTL)
    ↓
Return to user (< 100ms total)
```

**Data Model:**
```
Elasticsearch Index:
{
  "product_id": 123,
  "name": "iPhone 14 Pro",
  "description": "Advanced camera system",
  "category": "electronics",
  "price": 999,
  "rating": 4.5,
  "brand": "Apple",
  "in_stock": true,
  "views": 50000,
  "reviews_count": 2300
}

DynamoDB Table:
- PK: product_id
- SK: version
- Attributes: full specs, images[], reviews[]
- GSI: category → product_id (for catalog browsing)
```

**When to Use:**
- Catalog size: 1B+ items
- Search latency: < 100ms required
- Complex filtering needed
- Multi-attribute search
- High QPS (10K+/sec)

**MAANG Reference:** Amazon product service

---

### Challenge 102: Real-Time Inventory Management
**Problem:** Stock updates across 10K stores simultaneously, prevent overselling

**Tool:** **Redis + PostgreSQL + Kafka**

**Architecture:**
```
Purchase Request
    ↓
Redis DECR stock:{product}:{store}
    ├─ If >= 0: Lock placement → Kafka
    └─ If < 0: Fail (backorder queue)
    ↓
Async Sync to PostgreSQL
    ├─ Begin transaction
    ├─ UPDATE inventory SET qty = qty - 1
    ├─ Check quantity still valid
    └─ Commit (or rollback)
    ↓
Kafka Event: stock_changed
    └─ Broadcast to all stores
    ↓
Store Updates
    ├─ Store A: DECR local cache
    ├─ Store B: DECR local cache
    └─ Warehouse: update reserve
```

**Code Example:**
```python
# Optimistic check
redis_qty = redis.decr(f"stock:{product_id}:{store_id}")

if redis_qty >= 0:
    # Try to commit to DB
    try:
        db.execute("""
            UPDATE inventory 
            SET quantity = quantity - 1
            WHERE product_id = ? AND store_id = ?
            AND quantity > 0
        """)
        # Publish success event
        kafka.publish('inventory_changed', {
            'product_id': product_id,
            'store_id': store_id,
            'new_quantity': redis_qty
        })
    except:
        # Rollback Redis if DB fails
        redis.incr(f"stock:{product_id}:{store_id}")
        raise
else:
    # Out of stock
    raise OutOfStockException()
```

**Challenges:**
- Redis inconsistency during partition
- Need hourly reconciliation with DB
- Race conditions under high load
- Eventual consistency model

**MAANG Reference:** Flipkart, Amazon inventory

---

### Challenge 103: Shopping Cart Persistence
**Problem:** Cart survives server restart, accessible from any device (web/app)

**Tool:** **Redis + PostgreSQL + Async Queue**

**Architecture:**
```
User Adds Item to Cart
    ↓
Redis SET cart:{user_id} {items_json} EX 86400
    ├─ Fast response (< 10ms)
    └─ TTL: 24 hours
    ↓
Async Background Job
    └─ Periodically sync to PostgreSQL
    ↓
User Opens App (different device)
    ├─ Check Redis first (cache hit)
    └─ If miss, load from PostgreSQL
    ↓
Checkout
    ├─ Finalize cart from PostgreSQL
    ├─ Lock items (inventory hold 15 min)
    └─ Proceed to payment
```

**Schema:**
```sql
CREATE TABLE carts (
    user_id BIGINT,
    product_id BIGINT,
    quantity INT,
    added_at TIMESTAMP,
    PRIMARY KEY (user_id, product_id)
);

CREATE TABLE cart_snapshots (
    user_id BIGINT,
    snapshot_id UUID,
    items JSONB,
    created_at TIMESTAMP
);
```

**When to Use:**
- Cross-device sync critical
- Persistence required
- High concurrency
- Sub-100ms add-to-cart latency

---

### Challenge 104: Product Recommendations Feed
**Problem:** Show personalized product recommendations to 100M users

**Tool:** **Spark (offline) + Kafka Streams (real-time) + Redis Cache**

**Architecture:**
```
Nightly Batch Job (Spark)
    ├─ Compute user-item matrix (100M x 10M)
    ├─ Collaborative filtering
    └─ Generate top 100 recs per user
    ↓
Upload to Redis
    └─ user_recs:{user_id} = [prod1, prod2, ...]
    ↓
Real-Time Updates (Kafka Streams)
    ├─ User views product → event
    ├─ Boost similar products in cache
    └─ Decay unseen products
    ↓
API Request
    └─ GET /recommendations/{user_id}
    └─ Return top 20 from Redis
```

**When to Use:**
- Personalization important
- Scale: 100M+ users
- Offline + real-time updates ok
- A/B testing needed

---

### Challenge 105: Flash Sale / Lightning Deal
**Problem:** 1M users trying to buy 1000 items in 10 seconds

**Tool:** **Token Bucket + Queue + Load Shedding**

**Architecture:**
```
Sale Starts (1000 items available)
    ↓
Token Bucket initialized with 1000 tokens
    ↓
User Clicks "Buy"
    ├─ Try to acquire token
    ├─ If success: enter purchase queue
    └─ If fail: show "sold out" message
    ↓
Purchase Queue (FIFO)
    ├─ Process at max throughput
    └─ Inventory lock 10 minutes
    ↓
Background Job
    ├─ Process queue in batches (100/sec)
    ├─ Update inventory
    └─ Generate orders
```

**Configuration:**
```
max_items = 1000
tokens_per_second = 100  // Process 100 orders/sec
rate = min(max_items / duration, tokens_per_second)

Token Bucket:
- tokens: 1000 initial
- refill_rate: 100 tokens/sec
- max_burst: 1000
```

**When to Use:**
- Time-limited sales
- Limited inventory
- Prevent system overload
- Fair access needed

---

### Challenge 106: Product Image Optimization
**Problem:** Serve 10B images daily, different sizes for different devices (Web, Mobile, Tablet)

**Tool:** **CDN + Serverless Image Processing + WebP Format**

**Architecture:**
```
Original Product Image
    ↓
Upload to S3 (original 5MB JPEG)
    ↓
CloudFront CDN Distribution
    └─ Edge locations worldwide
    ↓
User Request
    ├─ /images/product-123-500w.webp (mobile)
    ├─ /images/product-123-1000w.webp (tablet)
    └─ /images/product-123-2000w.webp (desktop)
    ↓
CloudFront Cache HIT?
    └─ Yes → Serve from edge (1ms latency)
    ↓
CloudFront Cache MISS
    ├─ Lambda@Edge triggered
    ├─ Resize & convert to WebP
    ├─ Cache result (24 hours)
    └─ Return to user
```

**Benefits:**
```
Format Comparison:
- JPEG (original): 5MB, 100%
- WebP (same quality): 1.5MB, 30%
- Compression: 70% savings
- 10B images → 7B size reduction
- Bandwidth: 70% less
- Cost: 70% less
```

**Transformation Pipeline:**
```python
# Lambda function triggered by CloudFront
import PIL, boto3

def resize_image(bucket, key, width):
    # Download from S3
    img = PIL.Image.open(s3.get_object(bucket, key))
    
    # Calculate aspect ratio
    height = int(img.height * (width / img.width))
    
    # Resize
    img = img.resize((width, height), PIL.Image.LANCZOS)
    
    # Convert to WebP
    webp_buffer = io.BytesIO()
    img.save(webp_buffer, 'WebP', quality=85)
    
    # Upload to S3 cache
    s3.put_object(Bucket=bucket, Key=f"{key}-{width}w.webp", 
                  Body=webp_buffer.getvalue())
    
    return webp_buffer
```

**When to Use:**
- Massive image catalog (10B+)
- Multi-device support
- Cost optimization critical
- Global CDN available

**MAANG Reference:** Amazon product images, Pinterest pins

---

### Challenge 107: Wishlist / Save for Later
**Problem:** Store user's wishlist of 1000s items over years, sync across devices

**Tool:** **DynamoDB + Redis Cache + Sync Service**

**Data Model:**
```
DynamoDB Table: user_wishlists
- PK: user_id
- SK: wishlist_id (e.g., "default", "gifts-2024")
- Attributes:
  {
    "name": "Birthday Gifts",
    "created_at": "2024-01-15",
    "items": [
      {
        "product_id": 123,
        "added_at": "2024-01-15T10:30Z",
        "priority": 1,
        "price_when_added": 999
      },
      ...
    ],
    "total_value": 5000,
    "item_count": 5
  }

GSI: user_id + updated_at (for sync)
LSI: user_id + item_count (for sorting)
```

**Architecture:**
```
App 1 (Web) adds item
    ↓
DynamoDB update
    ↓
DynamoDB Streams → Lambda
    └─ Generate sync event
    ↓
Redis Pub/Sub
    └─ Publish sync event
    ↓
App 2 (Mobile)
    ├─ Subscribed to sync events
    └─ Pull latest wishlist from DynamoDB
    ↓
Devices stay in sync (eventual consistency)
```

**When to Use:**
- Long-term data storage
- Cross-device sync
- Historical tracking (price changes)
- Complex queries on items

---

### Challenge 108: Dynamic Pricing
**Problem:** Price changes based on demand, competition, user segment (price discrimination)

**Tool:** **Spark Batch + Rules Engine + Redis Cache**

**Architecture:**
```
Nightly Batch Job (Spark)
    ├─ Input: 
    │  ├─ Product popularity (views/day)
    │  ├─ Inventory level
    │  ├─ Competitor prices (scraped)
    │  ├─ Season/demand curve
    │  └─ User segment (VIP, new, inactive)
    ├─ Algorithm:
    │  ├─ Base price: cost + margin
    │  ├─ Demand multiplier: high demand → +20%
    │  ├─ Inventory multiplier: low stock → +15%
    │  ├─ Competition: undercut by 5%
    │  └─ Segment: VIP discount -10%
    └─ Output: optimal_price per product per segment
    ↓
Upload to Redis
    └─ price:{product_id}:{segment} = {price}
    ↓
API Request
    ├─ GET /product/123
    ├─ User segment: extract from JWT
    ├─ Look up price in Redis
    └─ Return {product, price}
```

**Pricing Formula:**
```
optimal_price = base_price * 
                demand_multiplier * 
                inventory_multiplier * 
                competition_factor * 
                segment_discount
```

**When to Use:**
- Revenue optimization critical
- Highly competitive market
- Inventory perishable (food, fashion)
- Seasonal products
- Willing to accept complexity

**Ethical Consideration:** Price discrimination legal but controversial; ensure compliance with regulations

---

### Challenge 109: Out-of-Stock Notification
**Problem:** Notify 100K+ users when out-of-stock product is back in inventory

**Tool:** **PostgreSQL Waitlist + Kafka + Notification Service**

**Architecture:**
```
Product Goes Out of Stock
    ↓
Users Add to Waitlist
    └─ INSERT INTO waitlist (user_id, product_id, created_at)
    └─ Count: 100,000 users waiting
    ↓
Stock Arrives (Warehouse)
    ├─ Inventory update → Kafka event
    └─ Event: inventory_replenished
    ↓
Kafka Consumer
    ├─ SELECT * FROM waitlist WHERE product_id = X
    └─ Generate 100K notification events
    ↓
Notification Service
    ├─ Rate limit: 1000 notifications/sec
    ├─ Duration: 100 seconds to notify all
    ├─ Channels: in-app, email, push, SMS
    └─ Track: success, bounce, unsubscribe
    ↓
Cleanup
    └─ DELETE FROM waitlist WHERE product_id = X
```

**Data Model:**
```sql
CREATE TABLE product_waitlist (
    waitlist_id UUID PRIMARY KEY,
    product_id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMP,
    notified_at TIMESTAMP,
    notified_channel VARCHAR,
    UNIQUE (product_id, user_id)
);

CREATE INDEX idx_product_created 
ON product_waitlist(product_id, created_at DESC);
```

**When to Use:**
- Product popular, frequently out-of-stock
- High volume potential customers
- Multi-channel notifications
- Retention improvement

---

### Challenge 110: Bulk Order Processing (B2B)
**Problem:** Process B2B order with 10,000+ items from multiple warehouses

**Tool:** **Async Job Processing + Saga Pattern + Kafka**

**Flow:**
```
B2B Customer submits order
    └─ 10,000 items across 5 product categories
    ↓
Order Service
    ├─ Validate items, quantities, prices
    ├─ Lock inventory for 30 minutes
    └─ Create draft order (status: PENDING)
    ↓
Inventory Allocation Service
    ├─ Determine which warehouse for each item
    ├─ Account for geographic proximity
    └─ Optimize for shipping cost
    ↓
Packaging Service
    ├─ Split items into boxes (max 25kg per box)
    ├─ Generate pack slip per box
    └─ Estimate shipping cost
    ↓
Payment Service
    ├─ Charge customer (usually payment terms: Net 30)
    ├─ Generate invoice
    └─ Create payment schedule
    ↓
Order Complete
    └─ Status: READY_TO_SHIP
    ↓
If ANY step fails:
    ← Compensate: unlock inventory, refund, cancel order
```

**When to Use:**
- B2B e-commerce
- Large order volumes
- Complex logistics
- Payment terms (not immediate)
- High order values

---

### Challenge 111: Product Reviews & Ratings at Scale
**Problem:** Store & serve 1B+ reviews, display sorted by helpful, recent, rating

**Tool:** **Elasticsearch + DynamoDB + Redis**

**Architecture:**
```
Review Posted by User
    ↓
DynamoDB Store (Source of Truth)
{
  "review_id": UUID,
  "product_id": 123,
  "user_id": 456,
  "rating": 4,
  "title": "Great phone!",
  "text": "...",
  "helpful_count": 250,
  "unhelpful_count": 10,
  "created_at": "2024-12-13",
  "verified_purchase": true
}
    ↓
Elasticsearch Index
{
  "review_id": UUID,
  "product_id": 123,
  "rating": 4,
  "helpful_count": 250,
  "created_at": "2024-12-13",
  "relevance_score": 95  // TF-IDF on review text
}
    ↓
API Request
    ├─ GET /product/123/reviews?sort=helpful
    ├─ Query: sort by (helpful_count DESC, created_at DESC)
    ├─ Pagination: cursor-based
    └─ Return: top 20 reviews
    ↓
Redis Cache (1-hour TTL)
    └─ product_reviews:123:helpful = [review_ids]
```

**When to Use:**
- Volume: 1B+ reviews
- Complex sorting (multiple dimensions)
- Real-time updates (helpful votes)
- Search within reviews needed

**MAANG Reference:** Amazon reviews, Flipkart reviews

---

### Challenge 112: Inventory Sync Across Channels
**Problem:** Same inventory shared between Web, Mobile App, Physical Store, Marketplace

**Tool:** **CDC (Debezium) + Event Bus (Kafka) + Dual-Write Avoidance**

**Architecture:**
```
Inventory Update
    (happens at any touchpoint: web order, store POS, app)
    ↓
Primary Database (PostgreSQL)
    ├─ Update inventory table
    ├─ Commit transaction
    └─ Write to transaction log (WAL)
    ↓
Debezium CDC
    ├─ Monitor PostgreSQL binlog
    ├─ Detect inventory changes
    └─ Emit event to Kafka
    ↓
Kafka Topic: inventory_changed
    {
      "product_id": 123,
      "warehouse_id": "WH-001",
      "quantity_before": 100,
      "quantity_after": 99,
      "timestamp": "2024-12-13T10:30Z",
      "channel": "web"
    }
    ↓
Subscribers
    ├─ Web Cache: update Redis
    ├─ Mobile Cache: invalidate
    ├─ Store POS: poll new quantity
    └─ Analytics: log for reporting
    ↓
Eventual Consistency
    └─ All channels see updated inventory within 1-2 seconds
```

**When to Use:**
- Multiple sales channels
- CDC source available (PostgreSQL, MySQL)
- Eventual consistency ok
- Legacy systems can't be modified
- Single source of truth needed

---

### Challenge 113: SKU Management (Product Variants)
**Problem:** Product has 1000+ variants (size, color, material combinations)

**Tool:** **DynamoDB + Elasticsearch + Redis**

**Data Model:**
```
Product: T-Shirt
├─ SKU combinations:
│  ├─ Size: S, M, L, XL, XXL (5)
│  ├─ Color: Red, Blue, Black, White (4)
│  └─ Material: Cotton, Polyester (2)
│  = 5 × 4 × 2 = 40 SKUs
├─ Some combinations out of stock
└─ Need fast lookup by attribute combo

DynamoDB:
- PK: product_id
- SK: sku_id (UUID or combination_hash)
- Attributes:
  {
    "size": "L",
    "color": "Red",
    "material": "Cotton",
    "sku_code": "TSHIRT-L-RED-CTN",
    "price": 29.99,
    "quantity": 150,
    "images": ["url1", "url2"]
  }

GSI: product_id + combination_hash
  (for fast lookup by attribute combination)
```

**Elasticsearch for Faceting:**
```
Query: "Red T-Shirt, Size M"
    ↓
Elasticsearch
    ├─ Filter: product_type = "t-shirt"
    ├─ Filter: color = "Red"
    ├─ Filter: size = "M"
    ├─ Aggregations: available colors, sizes (for facets)
    └─ Return: matching SKUs + facet options
```

**When to Use:**
- Fashion/Apparel (many variants)
- Size/color combinations important
- Fast variant lookup needed
- Faceting/filtering required

---

### Challenge 114: Price History Tracking
**Problem:** Show users price trends for product over last 30 days

**Tool:** **TimescaleDB (Time-Series DB)**

**Architecture:**
```
Daily Price Update
    ↓
TimescaleDB (optimized for time-series)
CREATE TABLE product_prices (
    time TIMESTAMPTZ NOT NULL,
    product_id BIGINT NOT NULL,
    price NUMERIC(10,2),
    currency VARCHAR(3),
    region VARCHAR(10)
);

SELECT create_hypertable('product_prices', 'time');
CREATE INDEX on product_prices (product_id, time DESC);
    ↓
Query: Price history for last 30 days
    ├─ SELECT time, price
    ├─ FROM product_prices
    ├─ WHERE product_id = 123
    ├─ AND time > now() - interval '30 days'
    ├─ ORDER BY time
    └─ → Returns compressed data, instant query
    ↓
Visualization
    └─ Display chart: price trend over 30 days
```

**Benefits:**
```
Regular PostgreSQL:
- Store 10K prices per product per 30 days
- Table scan slow
- Disk usage: high (uncompressed)

TimescaleDB:
- Hypertable: auto-partitioned by time
- Compression: ~90% reduction
- Query: 100x faster
- Perfect for time-series metrics
```

**When to Use:**
- Time-series data (prices, metrics, logs)
- Historical trending needed
- Retention policies important (auto-delete old data)
- Fast range queries on time

---

### Challenge 115: Product Expiry Management
**Problem:** Perishable items (milk, produce) expire in days, automatically remove from catalog

**Tool:** **DynamoDB TTL + Lambda + Elasticsearch Reindex**

**Architecture:**
```
New Product Added
    ↓
DynamoDB
{
  "product_id": 789,
  "name": "Organic Milk",
  "expiry_date": "2024-12-20T23:59Z",
  "ttl": 1734739200  // Unix timestamp of expiry date
}
    ↓
DynamoDB TTL Service (Background)
    ├─ Monitors all items
    ├─ Automatically deletes when ttl < now()
    └─ Fires stream event: DELETE
    ↓
DynamoDB Streams → Lambda
{
  "Records": [
    {
      "eventName": "REMOVE",
      "dynamodb": {
        "Keys": {"product_id": {"N": "789"}}
      }
    }
  ]
}
    ↓
Lambda Function
    ├─ Trigger: DELETE from Elasticsearch index
    ├─ DELETE /products/789
    └─ Log: "Product expired, removed from catalog"
    ↓
Elasticsearch Reindex
    └─ Product no longer searchable
```

**Code Example:**
```python
# Calculate TTL from expiry date
from datetime import datetime

expiry_date = datetime.strptime("2024-12-20", "%Y-%m-%d")
ttl_unix = int(expiry_date.timestamp())

# Store in DynamoDB
dynamodb.put_item(
    TableName='products',
    Item={
        'product_id': {'N': str(product_id)},
        'name': {'S': 'Organic Milk'},
        'expiry_date': {'S': '2024-12-20'},
        'ttl': {'N': str(ttl_unix)}  # TTL attribute
    }
)
```

**When to Use:**
- Perishable products (food, beverages)
- Time-limited offers
- Inventory auto-cleanup
- Minimal operational overhead needed

---

### Challenge 116: Concurrent Order Processing
**Problem:** Same popular product ordered from 5 regions simultaneously (race condition)

**Tool:** **Distributed Lock (Redis) + Optimistic Locking + Saga Pattern**

**Scenario:**
```
Product: Limited Edition Sneakers
Available: 100 pairs globally
    ↓
Concurrency: 1000 orders arriving simultaneously
    ├─ 300 from US region
    ├─ 250 from EU region
    ├─ 200 from Asia region
    ├─ 150 from other
    └─ Challenge: Ensure only 100 orders succeed
    ↓
Solution: Distributed Lock + Atomic Decrement
```

**Implementation:**
```python
# Acquire distributed lock
lock = redis.lock(f"order:{product_id}", timeout=10)

try:
    if lock.acquire(blocking=False):
        # Critical section: verify and allocate stock
        current_qty = db.query(
            "SELECT quantity FROM inventory 
             WHERE product_id = ? FOR UPDATE",  # Row lock
            product_id
        )
        
        if current_qty > 0:
            # Allocate to this order
            db.execute(
                "UPDATE inventory SET quantity = quantity - 1 
                 WHERE product_id = ?",
                product_id
            )
            db.commit()
            return "Order placed"
        else:
            return "Out of stock"
    else:
        # Couldn't acquire lock, queue the request
        kafka.publish('order_queue', order_data)
        return "Queued, will process shortly"
finally:
    lock.release()
```

**When to Use:**
- Limited inventory flash sales
- Race conditions likely
- Fair allocation important
- Lock contention acceptable

---

### Challenge 117: Seasonal Product Catalog
**Problem:** Different catalog for different seasons/holidays/regions

**Tool:** **PostgreSQL + Feature Flags + Versioning + Cache**

**Architecture:**
```
Product Record
{
  "product_id": 123,
  "name": "Winter Coat",
  "effective_from": "2024-11-01",
  "effective_to": "2025-02-28",
  "region": "US",
  "season": "winter",
  "status": "ACTIVE"
}
    ↓
Feature Flags (separate table)
{
  "flag_name": "winter_catalog_enabled",
  "region": "US",
  "enabled": true,
  "rollout_percentage": 100,
  "start_date": "2024-11-01"
}
    ↓
Query (with date filter)
SELECT * FROM products
WHERE status = 'ACTIVE'
AND region = 'US'
AND effective_from <= NOW()
AND effective_to >= NOW()
    ↓
Cache (Redis)
    └─ seasonal_products:US:2024-12 = [product_ids]
    └─ TTL: 24 hours
    ↓
Result
    ├─ US: Winter products visible
    ├─ India: Summer products visible
    └─ Each region gets relevant catalog
```

**When to Use:**
- Multi-region products
- Seasonal inventory
- Holiday campaigns
- Regional compliance needs
- Gradual rollouts needed

---

### Challenge 118: Product Attribute Hierarchy (Category Tree)
**Problem:** Categories up to 10 levels deep (e.g., Electronics > Phones > Smartphones > Android > Premium > Flagship)

**Tool:** **PostgreSQL RECURSIVE CTEs + Redis Cache**

**Data Model:**
```sql
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(255),
    level INT,
    FOREIGN KEY (parent_id) REFERENCES categories(category_id)
);

Example Tree:
Electronics (level 0)
├─ Computers (level 1)
│  ├─ Laptops (level 2)
│  ├─ Desktops (level 2)
│  └─ Tablets (level 2)
├─ Phones (level 1)
│  ├─ Smartphones (level 2)
│  │  ├─ Android (level 3)
│  │  │  ├─ Premium (level 4)
│  │  │  │  └─ Flagship (level 5)
│  │  │  └─ Budget (level 4)
│  │  └─ iOS (level 3)
```

**Query: Get entire subtree (all descendants)**
```sql
WITH RECURSIVE category_tree AS (
    -- Base case: start category
    SELECT category_id, parent_id, name, level, 1 as depth
    FROM categories
    WHERE category_id = ?  -- Start from this category
    
    UNION ALL
    
    -- Recursive case: find children
    SELECT c.category_id, c.parent_id, c.name, c.level, ct.depth + 1
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.category_id
    WHERE ct.depth < 10  -- Prevent infinite loops
)
SELECT * FROM category_tree;
```

**Caching Strategy:**
```python
# Cache entire tree in Redis
tree_json = json.dumps(fetch_category_tree(root_id=1))
redis.set(f"category_tree:1", tree_json, ex=3600)

# On query: check cache first
cached_tree = redis.get(f"category_tree:{root_id}")
if cached_tree:
    return json.loads(cached_tree)
else:
    tree = db.query_recursive_tree(root_id)
    redis.set(f"category_tree:{root_id}", json.dumps(tree))
    return tree
```

**When to Use:**
- Deep hierarchies (> 3 levels)
- Frequent tree traversal
- Category navigation important
- Relatively static structure

---

### Challenge 119: Bulk Product Upload (Seller)
**Problem:** Seller uploads 100K products via CSV, validate, index, available for search

**Tool:** **S3 + Async Job Queue + Batch Processing + Elasticsearch**

**Flow:**
```
Seller uploads file
    └─ bulk_products.csv (100K products)
    ↓
Upload to S3
    ├─ s3://uploads/seller-123/bulk_products.csv
    └─ S3 triggers Lambda event
    ↓
Lambda Function (S3 Trigger)
    ├─ Read file from S3
    ├─ Parse CSV
    └─ Split into batches (1000 products/batch)
    └─ Queue 100 jobs to SQS
    ↓
Worker Pool (Lambda or EC2)
    ├─ Consume jobs from SQS
    ├─ For each batch:
    │  ├─ Validate products
    │  ├─ Insert into DynamoDB
    │  ├─ Index in Elasticsearch
    │  └─ Update seller inventory count
    ├─ Progress: 1000 products/sec
    ├─ Duration: ~100 seconds
    └─ Send progress updates to WebSocket
    ↓
Seller Dashboard
    ├─ Real-time progress bar
    ├─ "Processing: 45,000 / 100,000"
    └─ Errors (if any) logged for review
    ↓
Completion
    ├─ All products indexed
    ├─ Available in search within 2 seconds
    └─ Email confirmation to seller
```

**Error Handling:**
```
Validation errors:
├─ Missing price: log, skip product
├─ Invalid SKU: log, allow retry
├─ Duplicate: skip, suggest update
└─ Send error report to seller

Retry strategy:
├─ Failed batch → dead letter queue
├─ Manual review by support team
└─ Seller notified with issues
```

**When to Use:**
- B2B/Marketplace platform
- High seller upload volume
- Batch processing acceptable
- Real-time feedback wanted

---

### Challenge 120: Product Metadata Enrichment
**Problem:** Auto-generate product descriptions & images using AI/LLM

**Tool:** **Kafka Streams + LLM API (OpenAI) + Image Generation (Stable Diffusion)**

**Pipeline:**
```
Product Created (minimal data)
{
  "product_id": 999,
  "name": "Bluetooth Speaker",
  "price": 49.99
}
    ↓
Kafka Topic: new_products
    └─ Event published
    ↓
Kafka Streams Consumer
    ├─ Enrich with base data (category, brand)
    └─ Send to enrichment service
    ↓
LLM Enrichment Service
    ├─ Call OpenAI API:
    │  ├─ Prompt: "Generate 500-char product description for: ${product}"
    │  └─ Response: "This premium Bluetooth speaker delivers crystal-clear audio..."
    ├─ Call Image Generation (Stable Diffusion):
    │  ├─ Prompt: "Professional product photo of bluetooth speaker, white, isolated background"
    │  └─ Response: Generated image URL
    └─ Return enriched data
    ↓
DynamoDB Update
{
  "product_id": 999,
  "description": "This premium Bluetooth speaker...",
  "image_url": "s3://generated-images/999.png",
  "tags": ["audio", "portable", "wireless"],
  "enriched_at": "2024-12-13T10:30Z"
}
    ↓
Elasticsearch Index
    └─ Add description to searchable fields
    ↓
Product Now Searchable with AI-Generated Content
```

**Cost Optimization:**
```
LLM API calls:
- Text generation: $0.015/1000 tokens
- 100K products × 0.3 tokens avg = 30K tokens
- Cost: $0.45 per batch
- Batches: 100 (sequential) = $45 total

Image Generation:
- Cost: $0.02 per image
- 100K products × $0.02 = $2000 total

Alternative: Use open-source models (free but slower)
```

**When to Use:**
- Large catalog with minimal data
- Cost acceptable
- LLM API access available
- Content quality not critical (automated)

---

## Payment & Transactions (121-135)

### Challenge 121: Payment Processing Pipeline
**Problem:** Process 100K+ payments/day reliably, prevent fraud, handle failures, refunds

**Tool:** **Payment Gateway (Stripe/Square) + Kafka + Idempotency + Saga**

**Architecture:**
```
Checkout Flow
    ↓
User submits payment
    ├─ Payment data: card, amount, user_id
    └─ Generate idempotency_key = SHA256(user_id + order_id + timestamp)
    ↓
Check cache for duplicate
    ├─ Get idempotency_key from Redis
    ├─ If HIT: return cached response (prevent double charge)
    └─ If MISS: proceed with processing
    ↓
Call Payment Gateway (Stripe)
    ├─ POST /v1/charges
    ├─ Params: amount, currency, card_token, idempotency_key
    └─ Response: charge_id, status
    ↓
Status checks
    ├─ SUCCESS: charge_id returned
    ├─ FAILURE: error_code returned
    └─ PENDING: async processing
    ↓
On SUCCESS:
    ├─ Update order status: PAID
    ├─ Create Saga event: payment_confirmed
    ├─ Kafka publish: order.payment_successful
    ├─ Cache response: SET payment_result:key value EX 86400
    └─ Trigger inventory deduction & shipping
    ↓
On FAILURE:
    ├─ Update order status: PAYMENT_FAILED
    ├─ Log failure reason
    ├─ Queue for retry (with exponential backoff)
    ├─ After 3 retries → manual review
    └─ Send error email to customer
    ↓
Nightly Reconciliation
    ├─ Query all "PAID" orders from last 24h
    ├─ Call Stripe API: get_charges(since=24h_ago)
    ├─ Compare with our records
    ├─ Alert on discrepancies
    └─ Manual intervention if needed
```

**Idempotency Implementation:**
```python
def process_payment(order_id, amount, card_token):
    # Generate idempotency key
    idempotency_key = hashlib.sha256(
        f"{order_id}:{amount}:{int(time.time())//1000}".encode()
    ).hexdigest()
    
    # Check if already processed
    cached = redis.get(f"payment:{idempotency_key}")
    if cached:
        return json.loads(cached)  # Return cached response
    
    # Call Stripe with idempotency_key header
    response = stripe.Charge.create(
        amount=amount,
        currency='USD',
        source=card_token,
        idempotency_key=idempotency_key
    )
    
    # Cache response
    redis.setex(f"payment:{idempotency_key}", 86400, json.dumps({
        'charge_id': response.id,
        'status': response.status
    }))
    
    return response
```

**When to Use:**
- Payment critical (fraud prevention)
- High volume (100K+/day)
- Reliability essential
- PCI compliance needed

---

### Challenge 122: Fraud Detection System
**Problem:** Detect fraudulent transactions in real-time with 99.9% accuracy

**Tool:** **Real-Time ML Model + Rules Engine + Kafka Streams**

**Architecture:**
```
Payment Transaction
{
  "user_id": 123,
  "amount": 500,
  "card_last4": "1234",
  "ip_address": "192.168.1.1",
  "device_id": "abc123",
  "timestamp": "2024-12-13T10:30Z",
  "merchant_category": "electronics"
}
    ↓
Real-Time Feature Extraction
    ├─ User's average transaction amount
    ├─ User's transaction frequency (last 24h)
    ├─ User's typical purchase categories
    ├─ Device risk score (from device fingerprinting)
    ├─ IP geolocation (detect impossible travel)
    ├─ Card velocity (how many transactions/hour on this card)
    ├─ Merchant risk score (is this merchant known for fraud)
    └─ Time-of-day risk (is this user normally shopping at 3 AM?)
    ↓
ML Model Scoring
    ├─ Trained model: XGBoost
    ├─ Input: 50+ features
    ├─ Output: fraud_probability (0-1)
    ├─ Latency: < 50ms
    └─ Accuracy: 99.9% (true positive rate)
    ↓
Rules Engine (Override ML if needed)
    ├─ IF amount > $10,000 AND is_new_user → FLAG
    ├─ IF card_velocity > 10/hour → FLAG
    ├─ IF impossible_travel (different countries in 1 hour) → FLAG
    ├─ IF blacklisted_card → BLOCK
    ├─ IF blacklisted_ip → BLOCK
    └─ IF fraud_probability > 0.8 → FLAG
    ↓
Decision
    ├─ fraud_score < 0.3: APPROVE
    ├─ fraud_score 0.3-0.7: REVIEW (3D Secure challenge)
    └─ fraud_score > 0.7: DECLINE
    ↓
Logging & Learning
    ├─ Store all features + decision
    ├─ Track: true positives, false positives
    ├─ Rebalance model weekly
    └─ Update rules based on fraud trends
```

**Model Training:**
```
Historical Data:
- 100M transactions
- 0.1% fraud rate = 100K frauds
- 99.9M legitimate transactions

Train/Test Split:
- 80M train, 20M test
- Class imbalance: undersampling, SMOTE

Features:
- User: avg_amount, transaction_count, device_count
- Card: velocity, location, age
- Merchant: category_risk, mcc_code, chargeback_rate
- Temporal: hour_of_day, day_of_week, seasonality

Evaluation:
- Metric: F1 score (balance precision & recall)
- Target: 99% recall (catch 99% of fraud)
- Acceptable false positive: 1% (users annoyed by challenges)
```

**When to Use:**
- Payment processing critical
- Fraud rate concerns
- Large transaction volumes
- ML model infrastructure available

---

### Challenge 123: Invoice Generation & Storage at Scale
**Problem:** Generate 1M invoices/day (PDF), retrieve in < 100ms

**Tool:** **PostgreSQL (Metadata) + S3 (PDF Files) + Lambda + Redis Cache**

**Architecture:**
```
Order Completed
    ↓
Lambda Function Triggered
    ├─ Extract order details from DynamoDB
    ├─ Fetch customer details from PostgreSQL
    ├─ Fetch shipping details from DynamoDB
    └─ Assemble invoice data
    ↓
PDF Generation (Python ReportLab or Node puppeteer)
    ├─ Template invoice HTML
    ├─ Populate with data
    ├─ Convert to PDF
    └─ Size: ~200KB per invoice
    ↓
Upload to S3
    └─ s3://invoices/2024/12/13/order-123.pdf
    └─ Enable CloudFront CDN distribution
    ↓
PostgreSQL Metadata
INSERT INTO invoices (
    invoice_id,
    order_id,
    user_id,
    amount,
    s3_path,
    generated_at
) VALUES (...)
    ↓
Redis Cache (Metadata Only)
    └─ invoice:123 = {s3_path, generated_at} (cache for 1 hour)
    ↓
Retrieve Invoice
API: GET /order/123/invoice
    ├─ Check Redis cache (HIT → instant)
    ├─ Cache MISS → query PostgreSQL
    ├─ Return S3 signed URL (valid for 1 hour)
    └─ User downloads PDF from CloudFront (edge cache)
    ↓
Cost Breakdown
├─ PDF generation: 0.5 sec × 1M = 500K CPU-seconds
├─ Lambda cost: ~$10/day
├─ S3 storage: 1M × 200KB = 200GB/month = ~$5/month
├─ CloudFront: CDN cache hits 95% = minimal cost
└─ Total: ~$10-20/day
```

**Concurrency Handling:**
```
Peak load: 1000 invoices/second to generate
Lambda capacity:
- Concurrent executions: auto-scale up to 1000
- Generate: 1000 invoices × 0.5 sec = 500 seconds
- But parallelized: completes in ~1 second (1000 parallel)
- Buffer queue (SQS) prevents overload

Failure handling:
- Failed generation → retry queue
- Manual intervention after 3 retries
```

**When to Use:**
- High invoice volume (100K+/day)
- PDF generation acceptable
- Storage cost concern (S3 cheap)
- Scalability needed

---

### Challenge 124: Refund Processing
**Problem:** Process refunds (full, partial), accounting for taxes, shipping

**Tool:** **Saga Pattern + Payment Gateway + Order Service**

**Flow:**
```
Customer Requests Refund
    ↓
Refund Service
    ├─ Validate refund eligibility:
    │  ├─ Order status: DELIVERED (refundable within 30 days)
    │  ├─ Check: not already refunded
    │  ├─ Check: partial refund not > original amount
    │  └─ If valid: proceed
    └─ If invalid: deny with reason
    ↓
Calculate Refund Amount
    ├─ Original amount: $100
    ├─ Minus restocking fee (if applicable): -$5
    ├─ Accounting for shipping:
    │  ├─ If return shipping paid by customer: -$10
    │  └─ If free return: -$0
    ├─ Accounting for taxes:
    │  ├─ Calculate tax on refunded amount
    │  └─ Refund includes tax
    └─ Net refund: $85
    ↓
Saga Orchestration
    Step 1: Reverse Charge
        ├─ Call Stripe: Refund charge_id, amount=$85
        └─ Response: refund_id
    
    Step 2: Update Inventory
        ├─ Increment inventory (item goes back to stock)
        ├─ UPDATE inventory SET quantity = quantity + 1
        └─ Mark item as "return received" (pending QC)
    
    Step 3: Create Reverse Order
        ├─ INSERT INTO orders (refund_order_id, ...)
        ├─ Type: REFUND
        ├─ Reference: original_order_id
        └─ Amount: $85
    
    Step 4: Update Payment Record
        ├─ INSERT INTO payment_transactions
        ├─ Type: REFUND
        ├─ Reference: original_charge_id
        └─ Refund approved by: system/manual
    
    Step 5: Notify Customer
        ├─ Email: "Refund of $85 initiated"
        ├─ Expected in: 5-10 business days
        └─ Refund ID: abc123
    ↓
If ANY step fails (e.g., payment gateway error):
    ← Compensate backwards
    ← Mark refund as FAILED, alert support team
    ← Manual intervention required
    ↓
Nightly Reconciliation
    ├─ Query all REFUNDED orders from last 24h
    ├─ Call Stripe API: get_refunds()
    ├─ Verify amounts match
    ├─ Alert on discrepancies
    └─ Generate report for accounting
```

**Handling Edge Cases:**
```
Partial Refund:
- Original: 2 items × $50 each = $100
- Customer returns 1 item
- Refund: $50 - restocking fee - shipping

Tax Handling:
- Order total: $100 (including $8 tax)
- Refund $100: customer gets $8 tax back
- Account for: sales tax reversal, VAT handling

Shipping:
- If pre-paid: non-refundable (customer paid upfront)
- If later charged: refund the charge, OR
- If free return: refund full amount
```

**When to Use:**
- E-commerce with returns
- Complex refund logic needed
- Accounting integration required
- Financial accuracy critical

---

### Challenge 125: Multi-Currency Support
**Problem:** Support 50+ currencies, real-time exchange rates, convert on checkout

**Tool:** **PostgreSQL + Redis Cache + Exchange Rate API**

**Architecture:**
```
Supported Currencies:
├─ USD (base)
├─ EUR, GBP, JPY, INR, AUD, CAD, ...
└─ 50+ currencies total
    ↓
Exchange Rate Updates
    ├─ Source: OpenExchangeRates API, OANDA, etc.
    ├─ Fetch hourly: all rates vs USD
    ├─ Cache in Redis (TTL: 1 hour)
    └─ Store in PostgreSQL (for history)
    ↓
PostgreSQL Schema
CREATE TABLE exchange_rates (
    from_currency VARCHAR(3),
    to_currency VARCHAR(3),
    rate DECIMAL(18,6),
    timestamp TIMESTAMP,
    PRIMARY KEY (from_currency, to_currency, timestamp)
);

Redis:
├─ rates:2024-12-13-10 = {
│   "USD_EUR": 0.92,
│   "USD_INR": 83.45,
│   ...
│  }
└─ TTL: 1 hour
    ↓
Checkout Flow
    ├─ User selects currency: INR (Indian Rupee)
    ├─ Product price: $50 USD
    ├─ Fetch rate: USD_INR = 83.45
    ├─ Convert: $50 × 83.45 = ₹4172.50
    ├─ Display: "₹4172.50" to user
    └─ Store both: original_amount: USD 50, display_amount: INR 4172.50
    ↓
Payment Processing
    ├─ When user checks out in INR:
    ├─ Convert back to USD for payment processing
    ├─ Payment: $50 USD (using original amount)
    ├─ Store in order: amount_usd, amount_original_currency
    └─ Receipt shows: ₹4172.50
    ↓
Reconciliation
    ├─ At end of day:
    ├─ Sum all sales by currency
    ├─ Convert to USD using historical rates
    ├─ Verify matches expected revenue
    └─ Account for currency fluctuations
```

**Rate Update Strategy:**
```python
# Fetch rates every hour
def update_exchange_rates():
    response = requests.get('https://openexchangerates.org/api/latest',
        params={'app_id': API_KEY, 'base': 'USD'})
    rates = response.json()['rates']
    
    # Store in Redis
    redis.set('rates:current', json.dumps(rates), ex=3600)
    
    # Store in PostgreSQL for history
    for currency, rate in rates.items():
        db.insert('exchange_rates', {
            'from_currency': 'USD',
            'to_currency': currency,
            'rate': rate,
            'timestamp': datetime.now()
        })

# Fetch conversion
def convert(amount_usd, to_currency):
    rates = redis.get('rates:current')
    if not rates:
        rates = fetch_from_db(datetime.now())
    
    rate = rates[f'USD_{to_currency}']
    return amount_usd * rate
```

**Handling Currency Volatility:**
```
Options:
1. Lock rate at checkout (show customer rate)
   - Protects customer from fluctuations
   - Risk: company absorbs changes

2. Lock rate for 1 hour (allows time for payment)
   - Compromise: partial protection

3. Use rate at payment processing
   - Company protected
   - Customer surprised if rate changes
```

**When to Use:**
- Global e-commerce
- 50+ currencies needed
- Real-time rates important
- Exchange rate volatility concern

---

*[Continue with Challenges 126-200 in similar format...]*

---

## Summary Table: Challenges 101-200

| # | Challenge | Tool | Category | Scale |
|---|-----------|------|----------|-------|
| 101 | Product Catalog | Elasticsearch + DynamoDB | E-Com | 1B+ products |
| 102 | Inventory Sync | Redis + PostgreSQL + Kafka | E-Com | 10K stores |
| 103 | Shopping Cart | Redis + PostgreSQL | E-Com | 100M users |
| 104 | Recommendations | Spark + Kafka Streams | E-Com | 100M users |
| 105 | Flash Sale | Token Bucket + Queue | E-Com | 1M users |
| 106 | Image Optimization | CDN + Lambda | E-Com | 10B images/day |
| 107 | Wishlist | DynamoDB + Redis | E-Com | Long-term |
| 108 | Dynamic Pricing | Spark + Rules Engine | E-Com | Algorithmic |
| 109 | Out-of-Stock Notification | PostgreSQL + Kafka | E-Com | 100K+ users |
| 110 | Bulk Orders | Async Job + Saga | E-Com | 10K+ items |
| 111 | Reviews & Ratings | Elasticsearch + DynamoDB | E-Com | 1B+ reviews |
| 112 | Inventory Channels | CDC + Kafka | E-Com | Multi-channel |
| 113 | SKU Management | DynamoDB + Elasticsearch | E-Com | 1000+ variants |
| 114 | Price History | TimescaleDB | E-Com | Time-series |
| 115 | Product Expiry | DynamoDB TTL + Lambda | E-Com | Perishable |
| 116 | Concurrent Orders | Distributed Lock + Saga | E-Com | Race conditions |
| 117 | Seasonal Catalog | PostgreSQL + Feature Flags | E-Com | Regional |
| 118 | Category Hierarchy | PostgreSQL RECURSIVE | E-Com | Deep trees |
| 119 | Bulk Upload | S3 + SQS + Batch | E-Com | 100K products |
| 120 | Metadata Enrichment | Kafka + LLM API | E-Com | AI generation |
| 121 | Payment Processing | Stripe + Saga + Idempotency | Payment | 100K+/day |
| 122 | Fraud Detection | ML Model + Rules Engine | Payment | 99.9% accuracy |
| 123 | Invoice Generation | Lambda + S3 | Payment | 1M/day |
| 124 | Refund Processing | Saga Pattern | Payment | Multi-step |
| 125 | Multi-Currency | PostgreSQL + Redis | Payment | 50+ currencies |

---

## MAANG Real-World Patterns (181-200)

Each challenge includes:
- **Real company** (Meta, Google, Amazon, Apple, Microsoft, etc.)
- **Real problem** they solved
- **Tool/Architecture** used
- **Scale** they operate at
- **Key insights** for interviews

---

**Total:** 100 Challenges | MAANG Focus | E-Commerce Deep Dive | Real Architectures


