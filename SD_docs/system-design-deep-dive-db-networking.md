# System Design Deep Dive: Databases, Networking, Caching & Async Processing

## Table of Contents
- [1. Database Systems](#1-database-systems)
  - [1.1 Evolution of Databases](#11-evolution-of-databases)
  - [1.2 ACID Properties](#12-acid-properties)
  - [1.3 Transaction Isolation Levels](#13-transaction-isolation-levels)
  - [1.4 Data Replication Architectures](#14-data-replication-architectures)
  - [1.5 Horizontal & Vertical Scaling](#15-horizontal--vertical-scaling)
  - [1.6 Partitioning & Sharding Strategies](#16-partitioning--sharding-strategies)
  - [1.7 Database Engine Internals](#17-database-engine-internals)
  - [1.8 Indexing Deep Dive](#18-indexing-deep-dive)
  - [1.9 SQL Query Optimization](#19-sql-query-optimization)
  - [1.10 Database Selection Guide](#110-database-selection-guide)
- [2. Networking Fundamentals](#2-networking-fundamentals)
  - [2.1 Protocol Stack & OSI Model](#21-protocol-stack--osi-model)
  - [2.2 HTTP Fundamentals](#22-http-fundamentals)
  - [2.3 DNS Architecture](#23-dns-architecture)
  - [2.4 TCP vs UDP](#24-tcp-vs-udp)
  - [2.5 Load Balancers](#25-load-balancers)
  - [2.6 API Protocols Comparison](#26-api-protocols-comparison)
  - [2.7 WebSockets](#27-websockets)
  - [2.8 Long Polling vs Short Polling vs SSE vs WebSockets](#28-long-polling-vs-short-polling-vs-sse-vs-websockets)

## 1. Database Systems

### 1.1 Evolution of Databases

#### RDBMS (1970s-2000s)
Relational Database Management Systems were the dominant database paradigm for decades, based on Edgar F. Codd's relational model. They organize data into tables with predefined schemas and use SQL for querying.

**Characteristics:**
- Strong ACID properties
- Structured data with schema
- SQL-based querying
- Vertical scaling focus
- Normalized data structures
- Strong consistency

**Examples:** Oracle, MySQL, PostgreSQL, Microsoft SQL Server

**CAP Position:** Typically CA (Consistency and Availability, sacrificing Partition tolerance)

**Use Cases:** 
- Enterprise applications
- Financial systems
- Any application requiring complex transactions
- Systems with complex querying needs

#### NoSQL (2000s-2010s)
As internet-scale applications emerged, traditional RDBMSs couldn't handle the scale, leading to the rise of NoSQL ("Not Only SQL") databases designed for distributed environments, high throughput, and flexible data models.

**Characteristics:**
- BASE properties (Basically Available, Soft state, Eventually consistent)
- Schema flexibility
- Horizontal scaling
- High throughput
- Specialized query patterns

**Major Types:**
1. **Document Stores**
   - Schema-less JSON/BSON documents
   - Examples: MongoDB, CouchDB
   - Use cases: Content management, user profiles, product catalogs

2. **Key-Value Stores**
   - Simple key-value pairs for fast access
   - Examples: Redis, DynamoDB, Riak
   - Use cases: Caching, session storage, shopping carts

3. **Wide-Column Stores**
   - Sparse, distributed multi-dimensional maps
   - Examples: Cassandra, HBase
   - Use cases: Time-series data, IoT, recommendation engines

4. **Graph Databases**
   - Focus on relationships between entities
   - Examples: Neo4j, JanusGraph
   - Use cases: Social networks, fraud detection, knowledge graphs

**CAP Position:** Usually AP (Availability and Partition tolerance) or CP (Consistency and Partition tolerance)

#### NewSQL (2010s-Present)
NewSQL attempts to combine the scalability of NoSQL systems with the ACID guarantees of traditional RDBMSs.

**Characteristics:**
- Distributed SQL engines
- Horizontal scaling while maintaining ACID
- SQL compatibility
- Advanced consensus algorithms
- Designed for cloud/distributed environments

**Examples:** Google Spanner, CockroachDB, YugabyteDB, Amazon Aurora, TiDB, VoltDB

**CAP Position:** CP with optimizations for high availability

**Use Cases:**
- Globally distributed applications requiring strong consistency
- Financial systems that need scale
- Applications migrating from RDBMS that need horizontal scaling

#### CAP Theorem Visualization

```
                      C
                     /|\
                    / | \
                   /  |  \
                  /   |   \
                 /    |    \
                /     |     \
               /      |      \
              /       |       \
             /        |        \
            A --------------------- P
```

**Database CAP Positioning:**
- Traditional RDBMS (MySQL, PostgreSQL): CA (not partition tolerant)
- MongoDB (with majority reads/writes): CP
- Cassandra (with tunable consistency): Usually AP
- DynamoDB: Configurable for AP or CP
- CockroachDB/Spanner: CP with high availability

#### Timeline and Evolution

```
1970s ----------- 2000s ----------- 2010s ----------- Present
[   RDBMS Era    ][    NoSQL Rise   ][    NewSQL     ]
Oracle, DB2,      MongoDB, Cassandra, Spanner, CockroachDB,
PostgreSQL, MySQL Redis, Neo4j       YugabyteDB
```

#### Pitfalls & Best Practices

**Pitfalls:**
- Choosing a database solely based on popularity
- Assuming NoSQL means "no schema" (schema design still matters)
- Overestimating the importance of raw performance vs. operational simplicity
- Underestimating migration complexity between paradigms

**Best Practices:**
- Choose database based on data access patterns and consistency requirements
- Consider operational complexity and team expertise
- Use polyglot persistence when appropriate (different databases for different workloads)
- Thoroughly test performance with realistic workloads
- Start with simpler solutions and evolve as needed

#### Resources
- [CAP Theorem: 12 Years Later](https://www.infoq.com/articles/cap-twelve-years-later-how-the-rules-have-changed/) - Eric Brewer's reflections
- [Designing Data-Intensive Applications](https://dataintensive.net/) - Martin Kleppmann's comprehensive book
- [NoSQL Databases: A Survey and Decision Guidance](https://medium.baqend.com/nosql-databases-a-survey-and-decision-guidance-ea7823a822d)
- [The Future of SQL: NewSQL Database Systems](https://www.percona.com/blog/the-history-and-future-of-newsql/)
- [CockroachDB Architecture Guide](https://www.cockroachlabs.com/docs/stable/architecture/overview.html)

### 1.2 ACID Properties

ACID properties guarantee reliable processing of database transactions, ensuring data validity despite errors, failures, or concurrent access.

#### Atomicity

**Definition**: Transactions are "all or nothing." Either all operations within a transaction succeed, or none do.

**Real-World Example**: Bank transfer between accounts

```java
@Transactional
public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
    Account from = accountRepository.findById(fromId)
        .orElseThrow(() -> new AccountNotFoundException(fromId));
    Account to = accountRepository.findById(toId)
        .orElseThrow(() -> new AccountNotFoundException(toId));
    
    if (from.getBalance().compareTo(amount) < 0) {
        throw new InsufficientFundsException(fromId);
    }
    
    from.debit(amount);
    accountRepository.save(from);  // If system fails here...
    
    to.credit(amount);
    accountRepository.save(to);    // ...the entire transaction rolls back
}
```

**Why It Matters**: Without atomicity, a failure between the debit and credit operations could leave the system in an inconsistent state where money disappears or is duplicated.

**Implementation**: Transaction logs, write-ahead logging (WAL), two-phase commit

#### Consistency

**Definition**: Transactions transform the database from one valid state to another valid state, maintaining all defined rules and constraints.

**Real-World Example**: Order processing with inventory constraints

```java
@Transactional
public void processOrder(Long productId, int quantity, Long customerId) {
    Product product = productRepository.findById(productId).orElseThrow();
    if (product.getInventory() < quantity) {
        throw new InsufficientInventoryException(productId);
    }
    
    // Update inventory
    product.setInventory(product.getInventory() - quantity);
    productRepository.save(product);
    
    // Create order
    Order order = new Order();
    order.setCustomerId(customerId);
    order.setProductId(productId);
    order.setQuantity(quantity);
    order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
    orderRepository.save(order);
}
```

**Why It Matters**: Consistency ensures business rules are enforced (inventory can't go negative) and prevents data corruption.

**Implementation**: Constraints (CHECK, UNIQUE, FK), triggers, application-level validation

#### Isolation

**Definition**: Transactions are executed as if they were run sequentially, even when actually executed concurrently.

**Real-World Example**: Concurrent inventory updates

```java
// Transaction 1
@Transactional(isolation = Isolation.SERIALIZABLE)
public void reserveInventory(Long productId, int quantity) {
    Product product = productRepository.findById(productId).orElseThrow();
    // If current inventory is 10
    if (product.getInventory() >= quantity) {
        product.setInventory(product.getInventory() - quantity);
        productRepository.save(product);
    }
}

// Transaction 2 (running concurrently)
@Transactional(isolation = Isolation.SERIALIZABLE)
public void reserveInventory(Long productId, int quantity) {
    Product product = productRepository.findById(productId).orElseThrow();
    // Without proper isolation, both transactions might read 10,
    // both deduct their quantities, and we could oversell
    if (product.getInventory() >= quantity) {
        product.setInventory(product.getInventory() - quantity);
        productRepository.save(product);
    }
}
```

**Why It Matters**: Without isolation, concurrent transactions could see each other's intermediate states, leading to race conditions, dirty reads, and other anomalies.

**Implementation**: Locking mechanisms (shared/exclusive), Multi-Version Concurrency Control (MVCC)

#### Durability

**Definition**: Once a transaction is committed, its changes persist even in the event of system failures.

**Real-World Example**: Ensuring payment records survive crashes

```java
// Simplified pseudocode of database internals for durability
void commitTransaction() {
    // 1. Write all transaction changes to WAL (Write-Ahead Log)
    writeToWAL(transactionChanges);
    
    // 2. Force WAL to persistent storage (fsync)
    fsync(walFile);
    
    // 3. Acknowledge commit to client
    sendAckToClient();
    
    // 4. Eventually apply changes to data files (can happen later)
    applyChangesToDataFiles();
}
```

**Why It Matters**: Without durability, acknowledged transactions could be lost during failures, creating inconsistencies between what the system reported and the actual data state.

**Implementation**: Write-ahead logging, durable storage media, replication

#### ACID vs BASE

| Property | ACID | BASE |
|----------|------|------|
| Consistency | Strong | Eventual |
| Availability | May sacrifice for consistency | Prioritized |
| Transaction Support | Full | Limited |
| Scalability | Usually vertical | Horizontal |
| Recovery | Transaction rollback | Complex, compensating transactions |
| Examples | PostgreSQL, MySQL | Cassandra, DynamoDB |

#### Pitfalls & Best Practices

**Pitfalls:**
- Assuming all databases provide the same ACID guarantees
- Overusing long-running transactions, causing lock contention
- Relying on isolation levels without understanding their implications
- Confusing application-level constraints with database consistency

**Best Practices:**
- Use transactions appropriately (neither too granular nor too broad)
- Choose isolation levels based on application requirements
- Consider performance implications of higher isolation levels
- Test concurrent scenarios to verify behavior
- Understand the specific ACID guarantees of your chosen database

#### Resources
- [ACID Properties in DBMS](https://www.geeksforgeeks.org/acid-properties-in-dbms/)
- [Martin Fowler on ACID](https://martinfowler.com/bliki/ACID.html)
- [ACID vs BASE Explained](https://www.strongdm.com/blog/acid-vs-base)
- [Transaction Processing: Concepts and Techniques](https://dl.acm.org/doi/book/10.5555/573304) - Classic book by Gray & Reuter
- [PostgreSQL Transaction Documentation](https://www.postgresql.org/docs/current/tutorial-transactions.html)

### 1.3 Transaction Isolation Levels

Transaction isolation levels define how transactions interact with each other when running concurrently. Each level offers a different balance between consistency and performance.

#### Isolation Levels Overview

| Isolation Level | Dirty Read | Non-repeatable Read | Phantom Read | Performance | Implementation |
|-----------------|------------|---------------------|--------------|-------------|----------------|
| Read Uncommitted| Possible   | Possible            | Possible     | Highest     | No locks       |
| Read Committed  | Prevented  | Possible            | Possible     | High        | Short-term read locks or MVCC |
| Repeatable Read | Prevented  | Prevented           | Possible*    | Medium      | Long-term read locks or MVCC with snapshot |
| Serializable    | Prevented  | Prevented           | Prevented    | Lowest      | Full locking, 2PL or serialization graph |

*_In some MVCC implementations like MySQL InnoDB, Repeatable Read also prevents phantom reads_

#### Read Uncommitted

**What It Is**: The lowest isolation level that provides no isolation guarantees. Transactions can see uncommitted changes made by other transactions.

**What Gets Locked**: Virtually nothing - essentially no isolation

**Anomalies Allowed**:
- **Dirty Reads**: Reading uncommitted changes that might be rolled back
- **Non-repeatable Reads**: Reading different values for the same row within a transaction
- **Phantom Reads**: Seeing different sets of rows that match a condition during a transaction

**Example Problem**:
```java
// Transaction 1
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public void generateReport(Long productId) {
    Product product = productRepository.findById(productId).orElseThrow();
    // product.price = $100 originally
    
    // Meanwhile, Transaction 2 is updating the price but hasn't committed
    
    BigDecimal price = product.getPrice(); // Might read uncommitted $150
    int inventory = product.getInventory();
    BigDecimal value = price.multiply(BigDecimal.valueOf(inventory));
    System.out.println("Inventory value: " + value);
    
    // If Transaction 2 rolls back, our calculation was based on invalid data
}

// Transaction 2
@Transactional
public void updateProductPrice(Long productId, BigDecimal newPrice) {
    Product product = productRepository.findById(productId).orElseThrow();
    product.setPrice(newPrice); // Set to $150
    productRepository.save(product);
    // ...error occurs...
    throw new RuntimeException("Price update failed");
    // Transaction rolls back, but Transaction 1 already used this price
}
```

**Use Cases**: Rarely appropriate for production; might be used for reporting scenarios where approximate values are acceptable and performance is critical.

#### Read Committed

**What It Is**: Transactions can only see committed data from other transactions. However, if other transactions commit during the current transaction, their changes become visible.

**What Gets Locked**: 
- Write locks held until commit
- Read locks are brief or implemented via MVCC (reading committed versions)

**Anomalies Prevented**:
- **Dirty Reads**: Prevented

**Anomalies Allowed**:
- **Non-repeatable Reads**: Still possible
- **Phantom Reads**: Still possible

**Example Problem**:
```java
// Transaction 1
@Transactional(isolation = Isolation.READ_COMMITTED)
public void analyzeProductPrice(Long productId) {
    Product product = productRepository.findById(productId).orElseThrow();
    BigDecimal initialPrice = product.getPrice(); // $100
    
    // Do some analysis for a few seconds
    try { Thread.sleep(5000); } catch (InterruptedException e) {}
    
    // Meanwhile, Transaction 2 updates price and commits
    
    product = productRepository.findById(productId).orElseThrow();
    BigDecimal currentPrice = product.getPrice(); // Now $150
    
    // Non-repeatable read: we see different values for the same product price
    BigDecimal difference = currentPrice.subtract(initialPrice);
    System.out.println("Price changed by: " + difference); // $50
}

// Transaction 2
@Transactional
public void updateProductPrice(Long productId, BigDecimal newPrice) {
    Product product = productRepository.findById(productId).orElseThrow();
    product.setPrice(newPrice); // $150
    productRepository.save(product);
    // This transaction commits while Transaction 1 is still running
}
```

**Use Cases**: General-purpose default for many applications where dirty reads must be avoided but absolute consistency is less critical.

#### Repeatable Read

**What It Is**: Ensures that if a transaction reads a row, it will see the same data for that row throughout the transaction, regardless of changes by other transactions.

**What Gets Locked**:
- Traditional: Read and write locks held until transaction end
- MVCC: Transaction works with a consistent snapshot of committed data as of transaction start

**Anomalies Prevented**:
- **Dirty Reads**: Prevented
- **Non-repeatable Reads**: Prevented

**Anomalies Allowed**:
- **Phantom Reads**: Possible (though some implementations like MySQL InnoDB prevent them)

**Example Problem**:
```java
// Transaction 1
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void countProductsInCategory(Long categoryId) {
    long initialCount = productRepository.countByCategoryId(categoryId); // 10 products
    
    // Do some processing
    try { Thread.sleep(5000); } catch (InterruptedException e) {}
    
    // Meanwhile, Transaction 2 adds a new product to this category and commits
    
    long currentCount = productRepository.countByCategoryId(categoryId);
    
    // In standard ANSI SQL: phantom read, currentCount = 11
    // In MySQL InnoDB: currentCount = 10 (no phantom read)
    System.out.println("Product count difference: " + (currentCount - initialCount));
}

// Transaction 2
@Transactional
public void addProductToCategory(Product newProduct, Long categoryId) {
    newProduct.setCategoryId(categoryId);
    productRepository.save(newProduct);
    // This transaction commits while Transaction 1 is still running
}
```

**Use Cases**: Applications requiring consistency across multiple reads, report generation, complex calculations based on multiple queries.

#### Serializable

**What It Is**: The highest isolation level that guarantees execution as if transactions ran serially (one after another) even though they may run concurrently.

**What Gets Locked**:
- Traditional: Full predicate locks or 2PL (Two-Phase Locking)
- Modern: Serialization through conflict detection or certification

**Anomalies Prevented**:
- **Dirty Reads**: Prevented
- **Non-repeatable Reads**: Prevented
- **Phantom Reads**: Prevented

**Implementation Approaches**:
1. **Pessimistic**: Lock everything that might be accessed (read/write locks on rows, ranges)
2. **Optimistic**: Allow transactions to proceed, but validate at commit time for conflicts
3. **Serialization Graph**: Track dependencies between transactions and abort if cycles detected

**Example With Serializable**:
```java
// Transaction 1
@Transactional(isolation = Isolation.SERIALIZABLE)
public void transferBetweenAccounts(Long fromId, Long toId, BigDecimal amount) {
    try {
        Account from = accountRepository.findById(fromId).orElseThrow();
        Account to = accountRepository.findById(toId).orElseThrow();
        
        if (from.getBalance().compareTo(amount) >= 0) {
            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));
            
            accountRepository.save(from);
            accountRepository.save(to);
        }
    } catch (SerializationFailureException e) {
        // With optimistic serializable isolation, we might get conflicts
        // if another transaction modified the same accounts
        System.out.println("Transaction conflict detected, please retry");
    }
}
```

**Use Cases**: Financial transactions, inventory management, situations where data integrity is absolutely critical and performance less important.

#### Implementation Differences Across Databases

| Database | Read Uncommitted | Read Committed | Repeatable Read | Serializable |
|----------|------------------|----------------|----------------|--------------|
| PostgreSQL | MVCC | MVCC snapshot per statement | MVCC snapshot per transaction | Serialization anomaly detection (SSI) |
| MySQL (InnoDB) | Lock-free reads | MVCC snapshot per statement | MVCC snapshot + next-key locks (prevents phantoms) | Shared locks on all reads (pessimistic) |
| Oracle | Not supported | Default (MVCC) | Implemented via SELECT FOR UPDATE | Via SELECT FOR UPDATE SERIALIZABLE |
| SQL Server | Lock-free dirty reads | Row-level locks, released after operation | Range locks held for transaction | Full locks (pessimistic) |
| MongoDB | Default | "majority" read concern | "snapshot" read concern | Via transactions with "snapshot" |

#### Practical Spring Boot Example

```java
@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    
    // Default isolation (typically READ COMMITTED)
    @Transactional
    public Order createOrder(Long productId, int quantity, Long userId) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getInventory() < quantity) {
            throw new InsufficientInventoryException(productId);
        }
        
        product.setInventory(product.getInventory() - quantity);
        productRepository.save(product);
        
        Order order = new Order();
        order.setProductId(productId);
        order.setUserId(userId);
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        
        return orderRepository.save(order);
    }
    
    // Using higher isolation for critical operations
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order createOrderSerializable(Long productId, int quantity, Long userId) {
        try {
            // Same logic as above with serializable isolation
            // ...
        } catch (TransactionSystemException e) {
            if (e.getRootCause() instanceof SerializationFailureException) {
                // Handle serialization conflict
                throw new ConcurrentModificationException("Please retry order");
            }
            throw e;
        }
    }
}
```

#### Choosing The Right Isolation Level

| Business Need | Suggested Isolation Level |
|---------------|---------------------------|
| Maximum throughput, approximate reads acceptable | Read Uncommitted (rarely) |
| General OLTP workloads | Read Committed |
| Financial calculations, complex reports | Repeatable Read |
| Critical financial transactions, inventory | Serializable |

#### Pitfalls & Best Practices

**Pitfalls:**
- Using higher isolation levels than necessary, causing performance bottlenecks
- Assuming isolation levels work the same across different databases
- Neglecting to handle serialization failures in optimistic concurrency
- Long-running transactions at high isolation levels causing lock contention

**Best Practices:**
- Choose the minimum isolation level that meets your consistency requirements
- Keep transactions short, especially at higher isolation levels
- Understand your database's specific implementation of isolation levels
- Add retry logic for serialization failures with optimistic concurrency
- Test concurrent scenarios to verify behavior matches expectations
- Monitor lock contention and adjust isolation strategy as needed

#### Resources
- [Transaction Isolation Levels](https://docs.microsoft.com/en-us/sql/odbc/reference/develop-app/transaction-isolation-levels) - Microsoft's guide
- [A Critique of ANSI SQL Isolation Levels](https://www.microsoft.com/en-us/research/wp-content/uploads/2016/02/tr-95-51.pdf) - Seminal paper
- [PostgreSQL Transaction Isolation](https://www.postgresql.org/docs/current/transaction-iso.html) - PostgreSQL's implementation
- [MySQL Transaction Isolation Levels](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html) - MySQL's implementation
- [Understanding Isolation Levels](https://www.cockroachlabs.com/blog/sql-transaction-isolation-levels-explained/) - CockroachDB's explanation

### 1.4 Data Replication Architectures

Data replication copies data across multiple database nodes to improve availability, read scalability, and geographic distribution. Different replication architectures offer different trade-offs.

#### Single-Leader Replication (Master-Slave)

**Description**: One primary node (leader/master) accepts all writes; multiple replica nodes (followers/slaves) synchronize from the leader and serve reads.

**Diagram**:
```
          ┌─────────┐
          │  Leader │◄──────── Writes
          └────┬────┘
               │
   ┌───────────┼───────────┐
   ▼           ▼           ▼
┌────────┐  ┌────────┐  ┌────────┐
│Follower│  │Follower│  │Follower│◄── Reads
└────────┘  └────────┘  └────────┘
```

**Replication Methods**:
1. **Statement-based**: Leader sends SQL statements to followers
   - Pros: Compact, simple for identical operations
   - Cons: Non-deterministic functions (NOW(), RAND()) may produce different results
   
2. **Write-ahead Log (WAL) shipping**: Leader sends low-level log records
   - Pros: Exact byte-for-byte reproduction, high performance
   - Cons: Tightly coupled to storage engine, may break with version upgrades
   
3. **Logical (Row-based) replication**: Leader sends records of row changes
   - Pros: Database-agnostic, allows schema evolution
   - Cons: Higher overhead than WAL shipping

**Synchronization Modes**:
- **Synchronous**: Leader waits for follower acknowledgment before confirming write
  - Higher consistency, lower performance
  - Failure of a replica blocks writes
  
- **Asynchronous**: Leader confirms write without waiting for followers
  - Higher performance, potential data loss
  - Replica lag can cause consistency issues

**Pros**:
- Simple to understand and implement
- No write conflicts (all writes go through leader)
- Read scalability through read replicas
- Well-supported by most databases

**Cons**:
- Single point of failure (leader)
- Replication lag can cause stale reads
- Write scalability limited by leader capacity

**Real-World Implementation Example (PostgreSQL)**:
```bash
# On primary server
# postgresql.conf changes
wal_level = replica
max_wal_senders = 10
max_replication_slots = 10

# pg_hba.conf entry
host replication replica_user 10.0.0.0/24 md5

# On replica
# Create replica
pg_basebackup -h primary_host -D /var/lib/postgresql/data -U replica_user -P -R
```

**Spring Boot Configuration Example**:
```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @ConfigurationProperties("app.datasource.primary")
    @Primary
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @ConfigurationProperties("app.datasource.replica")
    public DataSource replicaDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    public DataSource routingDataSource(
            @Qualifier("primaryDataSource") DataSource primaryDataSource,
            @Qualifier("replicaDataSource") DataSource replicaDataSource) {
        
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.PRIMARY, primaryDataSource);
        targetDataSources.put(DataSourceType.REPLICA, replicaDataSource);
        
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);
        
        return routingDataSource;
    }
    
    // Custom routing DataSource that decides which physical DataSource to use
    public class RoutingDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            return TransactionSynchronizationManager.isCurrentTransactionReadOnly() 
                ? DataSourceType.REPLICA 
                : DataSourceType.PRIMARY;
        }
    }
    
    // DataSource type enum
    public enum DataSourceType {
        PRIMARY, REPLICA
    }
}

// Usage in services
@Service
public class ProductService {
    @Transactional(readOnly = true) // This will use replica
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    @Transactional // This will use primary
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
}
```

#### Multi-Leader Replication (Multi-Master)

**Description**: Multiple nodes can accept writes and replicate to all other nodes.

**Diagram**:
```
    Region A                Region B
   ┌─────────┐            ┌─────────┐
   │ Leader A │◄─────────►│ Leader B │
   └────┬────┘            └────┬────┘
        │                      │
        ▼                      ▼
   ┌─────────┐            ┌─────────┐
   │FollowersA│            │FollowersB│
   └─────────┘            └─────────┘
```

**Topologies**:
1. **Full mesh**: Each leader connects to every other leader
   - Simple for small clusters, N² connections become problematic at scale
   
2. **Circular**: Leaders form a ring, changes pass around the circle
   - Single point of failure, delays in propagation
   
3. **Star**: Central leader fans out to others
   - Central hub becomes bottleneck

**Pros**:
- Write availability in multiple locations
- Lower write latency for geographically distributed users
- Resilience to regional failures

**Cons**:
- Complex conflict resolution required
- Eventually consistent by nature
- More complex replication topology

**Conflict Types & Resolution Strategies**:

1. **Last-Write-Wins (LWW)**
   - Based on timestamps or logical clocks
   - Simple but can lose data
   
2. **Version Vectors/Vector Clocks**
   - Track causality between versions
   - Can detect concurrent modifications
   
3. **Custom Merge Functions**
   - Application-specific logic to merge conflicting changes
   - Example: Shopping cart merge (union of items)
   
4. **Conflict-free Replicated Data Types (CRDTs)**
   - Data structures with mathematical properties ensuring convergence
   - Examples: counters, sets, maps with defined merge semantics

**Example Implementation (MySQL Group Replication)**:
```sql
-- On each node
SET GLOBAL group_replication_bootstrap_group=ON;
START GROUP_REPLICATION;
SET GLOBAL group_replication_bootstrap_group=OFF;

-- Check group membership
SELECT * FROM performance_schema.replication_group_members;
```

**Java CRDT Example (G-Counter)**:
```java
// Grow-only Counter CRDT
public class GCounter {
    private final Map<String, Integer> counts;
    private final String nodeId;
    
    public GCounter(String nodeId) {
        this.counts = new ConcurrentHashMap<>();
        this.nodeId = nodeId;
        this.counts.put(nodeId, 0);
    }
    
    // Only increment our own counter
    public void increment() {
        this.counts.compute(nodeId, (k, v) -> (v == null) ? 1 : v + 1);
    }
    
    // Merge by taking the max count for each node
    public void merge(GCounter other) {
        other.counts.forEach((node, count) -> {
            this.counts.merge(node, count, Integer::max);
        });
    }
    
    // Total is the sum of all counters
    public int value() {
        return this.counts.values().stream().mapToInt(Integer::intValue).sum();
    }
}
```

#### Leaderless Replication (Dynamo-style)

**Description**: Any node can accept writes; clients often write to multiple nodes and read from multiple nodes.

**Diagram**:
```
   ┌───────┐     ┌───────┐     ┌───────┐
   │ Node1 │◄───►│ Node2 │◄───►│ Node3 │
   └───────┘     └───────┘     └───────┘
       ▲             ▲             ▲
       │             │             │
       └─────────────┴─────────────┘
             Reads & Writes
```

**Quorum Consensus**:
- N = Total number of replicas
- W = Write quorum (minimum nodes for successful write)
- R = Read quorum (minimum nodes for successful read)
- If W + R > N, readers should see the latest write (consistency)
- Common settings:
  - N=3, W=2, R=2: Balanced approach
  - N=3, W=3, R=1: Optimize for read performance
  - N=3, W=1, R=3: Optimize for write performance

**Read Repair & Anti-Entropy**:
1. **Read repair**: Fix inconsistencies when detected during reads
2. **Anti-entropy process**: Background process comparing and reconciling replicas
3. **Hinted handoff**: Temporarily store writes for unavailable nodes

**Pros**:
- No single point of failure
- Linear write scalability
- Tunable consistency levels
- Lower latency by writing to local node

**Cons**:
- Complex read/write coordination
- Eventual consistency by default
- Read repairs or anti-entropy processes needed
- More complex application logic

**Example Implementation (Cassandra)**:
```java
// Cassandra configuration
Cluster cluster = Cluster.builder()
    .addContactPoints("node1", "node2", "node3")
    .build();

Session session = cluster.connect("mykeyspace");

// Setting consistency levels
Statement statement = new SimpleStatement("SELECT * FROM users WHERE id = ?");
statement.setConsistencyLevel(ConsistencyLevel.QUORUM);

// Spring Data Cassandra example
@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {
    @Consistency(ConsistencyLevel.QUORUM)
    Optional<User> findById(UUID id);
    
    @Consistency(ConsistencyLevel.ALL)
    User save(User user);
}
```

#### Comparison of Replication Models

| Feature | Single-Leader | Multi-Leader | Leaderless |
|---------|--------------|--------------|------------|
| Write Scalability | Limited by leader | Good | Excellent |
| Read Scalability | Excellent | Excellent | Good |
| Latency | Higher for geo-distributed | Low in each region | Low for local nodes |
| Consistency | Strong possible | Eventually consistent | Tunable (quorum) |
| Complexity | Low | High | Medium |
| Conflict Handling | None needed | Required | Required |
| Failure Tolerance | Leader is SPOF | Highly tolerant | Highly tolerant |
| Use Cases | Most traditional DB workloads | Multi-region applications | High availability, massive scale |

#### Replication Lag & Consistency Guarantees

When using asynchronous replication, several consistency problems can arise due to replication lag:

1. **Reading your own writes**: Users expect to see their own updates immediately
   - Solution: Read from leader for user's own data
   
2. **Monotonic reads**: Users shouldn't see data moving backward in time
   - Solution: Read from same replica or replica with minimum lag
   
3. **Consistent prefix reads**: Users should see causally related events in correct order
   - Solution: Write related events to same partition or use causal timestamps

**Code Example (Read-your-writes consistency)**:
```java
@Service
public class UserProfileService {
    private final UserRepository userRepository;
    private final RoutingDataSource routingDataSource;
    
    public User updateProfile(Long userId, ProfileUpdate update) {
        User user = userRepository.findById(userId).orElseThrow();
        user.applyUpdate(update);
        
        // Write to primary
        User savedUser = userRepository.save(user);
        
        // Force primary read for this user temporarily
        routingDataSource.setPrimaryPreferredForUser(userId, Duration.ofMinutes(1));
        
        return savedUser;
    }
    
    public User getProfile(Long userId) {
        // Will use primary if this user recently made updates
        return userRepository.findById(userId).orElseThrow();
    }
}
```

#### Pitfalls & Best Practices

**Pitfalls:**
- Underestimating the complexity of conflict resolution in multi-leader/leaderless
- Assuming replication is instant in asynchronous systems
- Neglecting to monitor replication lag
- Inappropriate consistency level for the use case
- Using eventual consistency for critical operations

**Best Practices:**
- Choose replication strategy based on geographic distribution and consistency needs
- Monitor replication lag and alert on excessive values
- Implement application-level solutions for consistency issues (read-your-writes, etc.)
- Use synchronous replication for critical data, async for less critical
- Test system behavior during network partitions and node failures
- Document and train team on eventual consistency implications

#### Resources
- [Amazon Dynamo Paper](https://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf) - Foundational paper for leaderless replication
- [CRDT Research Paper](https://hal.inria.fr/inria-00555588/document) - Shapiro et al.'s seminal paper on CRDTs
- [Jepsen Analyses](https://jepsen.io/analyses) - Real-world distributed database consistency testing
- [Cassandra: The Definitive Guide](https://www.oreilly.com/library/view/cassandra-the-definitive/9781491933657/) - Practical Cassandra coverage
- [MySQL Group Replication](https://dev.mysql.com/doc/refman/8.0/en/group-replication.html) - Multi-master MySQL
- [PostgreSQL Replication](https://www.postgresql.org/docs/current/high-availability.html) - PostgreSQL replication options
- [CockroachDB Multi-Region Architecture](https://www.cockroachlabs.com/docs/stable/multiregion-overview.html) - Modern multi-region SQL

### 1.5 Horizontal & Vertical Scaling

Scaling strategies allow databases to handle growing workloads. Vertical scaling adds more resources to existing machines, while horizontal scaling adds more machines to the system.

#### Vertical Scaling (Scaling Up)

**Description**: Adding more power (CPU, memory, disk, network) to existing database servers.

**Approaches**:
1. **Hardware Upgrades**
   - Adding CPU cores, RAM, faster disks
   - Moving to faster networking (10Gbps → 25/40/100Gbps)
   
2. **Instance Type Upgrades** (Cloud)
   - Changing instance size (e.g., AWS: r5.xlarge → r5.4xlarge)
   - Storage optimizations (IOPS provisioning, NVMe)
   
3. **Specialized Hardware**
   - Adding GPUs for analytical workloads
   - Using FPGA accelerators
   - Employing custom ASICs for specific workloads

**Pros**:
- Simple to implement (often no application changes)
- Maintains existing architecture and operational model
- Avoids distributed system complexity
- Often better for workloads that can't be easily partitioned

**Cons**:
- Physical/provider-imposed limits on maximum size
- Cost typically increases non-linearly with capacity
- Single point of failure remains
- Downtime often required for upgrades (unless using advanced clustering)

**Example Upgrade** (AWS RDS):
```bash
# Using AWS CLI to upgrade a database instance
aws rds modify-db-instance \
    --db-instance-identifier myproduction-db \
    --db-instance-class db.r5.4xlarge \
    --allocated-storage 1000 \
    --iops 10000 \
    --apply-immediately

# Using terraform
resource "aws_db_instance" "production_db" {
  instance_class       = "db.r5.4xlarge"
  allocated_storage    = 1000
  iops                 = 10000
  # other parameters...
}
```

#### Horizontal Scaling (Scaling Out)

**Description**: Distributing data and load across multiple database servers.

**Approaches**:

1. **Read Replicas**
   - Add read-only copies of the database
   - Distribute read queries across replicas
   - Primary still handles all writes
   
2. **Sharding/Partitioning**
   - Split data across multiple independent database servers
   - Each shard handles its own reads and writes
   - No single server contains the complete dataset
   
3. **Distributed Databases**
   - Purpose-built for horizontal scaling
   - Data automatically distributed across nodes
   - Coordination for queries that span multiple nodes

**Pros**:
- Near-linear scalability
- Improved availability and fault tolerance
- Often more cost-effective (commodity hardware)
- Can scale incrementally as needed

**Cons**:
- Increased application complexity
- Potential for distributed system problems
  - Partial failures
  - Network partitions
  - Consistency challenges
- More complex operational model

**Spring Boot Example** (Read-write splitting):
```java
@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }
    
    @Bean
    @ConfigurationProperties("spring.datasource.replica")
    public DataSource replicaDataSource() {
        return replicaDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }
    
    @Bean
    @Primary
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }
    
    @Bean
    public DataSource replicaDataSource() {
        return replicaDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }
    
    @Bean
    public DataSource routingDataSource() {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();
        
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("primary", primaryDataSource());
        dataSourceMap.put("replica", replicaDataSource());
        
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource());
        
        return routingDataSource;
    }
    
    public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            return isReadOnly ? "replica" : "primary";
        }
    }
}

// Usage
@Service
public class ProductService {
    private final ProductRepository repository;
    
    @Transactional(readOnly = true) // Will use replica
    public List<Product> getAllProducts() {
        return repository.findAll();
    }
    
    @Transactional // Will use primary
    public Product createProduct(Product product) {
        return repository.save(product);
    }
}
```

**Sharding Example** (Spring with custom routing):
```java
@Configuration
public class ShardingConfig {
    @Bean
    public DataSource shardedDataSource() {
        Map<Object, Object> shards = new HashMap<>();
        // Configure multiple shards
        shards.put(0, createDataSource("jdbc:mysql://shard0:3306/mydb"));
        shards.put(1, createDataSource("jdbc:mysql://shard1:3306/mydb"));
        shards.put(2, createDataSource("jdbc:mysql://shard2:3306/mydb"));
        shards.put(3, createDataSource("jdbc:mysql://shard3:3306/mydb"));
        
        ShardingDataSource shardingDataSource = new ShardingDataSource();
        shardingDataSource.setTargetDataSources(shards);
        shardingDataSource.setDefaultTargetDataSource(shards.get(0));
        
        return shardingDataSource;
    }
    
    private DataSource createDataSource(String url) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername("user");
        config.setPassword("password");
        config.setMaximumPoolSize(20);
        return new HikariDataSource(config);
    }
    
    public class ShardingDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            // Get user ID from security context or thread local
            Long userId = CurrentUser.getId();
            // Simple hash-based sharding
            return Math.abs(userId.hashCode() % 4);
        }
    }
}

// ShardingJdbcTemplate for operations that need to query all shards
@Component
public class ShardingJdbcTemplate {
    private final Map<Integer, JdbcTemplate> shardTemplates;
    
    public ShardingJdbcTemplate(DataSource shardedDataSource) {
        ShardingDataSource sds = (ShardingDataSource) shardedDataSource;
        Map<Object, Object> dataSources = sds.getResolvedDataSources();
        
        this.shardTemplates = dataSources.entrySet().stream()
            .collect(Collectors.toMap(
                e -> (Integer) e.getKey(), 
                e -> new JdbcTemplate((DataSource) e.getValue())
            ));
    }
    
    // Query across all shards and combine results
    public <T> List<T> queryAcrossShards(String sql, RowMapper<T> rowMapper, Object... args) {
        List<T> results = new ArrayList<>();
        
        for (JdbcTemplate template : shardTemplates.values()) {
            List<T> shardResults = template.query(sql, rowMapper, args);
            results.addAll(shardResults);
        }
        
        return results;
    }
}
```

#### Performance Benchmarks

| Scaling Approach | Database Type | Before | After | Improvement Factor | Cost Change |
|------------------|---------------|--------|-------|-------------------|-------------|
| Vertical (2x CPU/RAM) | PostgreSQL | 5,000 QPS | 8,000 QPS | 1.6x | +100% |
| Vertical (4x CPU/RAM) | PostgreSQL | 5,000 QPS | 12,000 QPS | 2.4x | +300% |
| Horizontal (2 shards) | PostgreSQL | 5,000 QPS | 9,500 QPS | 1.9x | +120% |
| Horizontal (4 shards) | PostgreSQL | 5,000 QPS | 18,000 QPS | 3.6x | +300% |
| Read replicas (3) | MySQL | 3,000 read QPS | 8,500 read QPS | 2.8x | +200% |
| Horizontal (3 nodes) | MongoDB | 12,000 QPS | 32,000 QPS | 2.7x | +200% |
| Horizontal (4 nodes) | Cassandra | 20,000 QPS | 75,000 QPS | 3.75x | +300% |

*Note: These are example figures and actual performance will vary based on workload, configuration, hardware, and many other factors.*

#### When To Choose Each Approach

**Vertical Scaling Is Better When:**
- Your workload isn't easily partitioned
- You need strong transactional guarantees
- You want operational simplicity
- Your scale needs are modest
- Cost isn't the primary concern

**Horizontal Scaling Is Better When:**
- You need massive scale (>100K QPS)
- You need high availability
- Cost efficiency is important
- Your data model allows natural partitioning
- You're building a cloud-native system

**Combined Approach:**
Many systems use a hybrid approach:
- Scale vertically until practical limits
- Add read replicas for read scaling
- Implement sharding when necessary
- Use appropriate hardware for each tier

#### Database Specific Scaling Approaches

| Database | Vertical Scaling | Horizontal Scaling |
|----------|------------------|-------------------|
| PostgreSQL | Excellent (up to very large instances) | Good (logical replication, external solutions like Citus) |
| MySQL | Good | Good (built-in sharding with NDB, proxies) |
| MongoDB | Good | Excellent (auto-sharding, replica sets) |
| Cassandra | Limited benefit | Excellent (ring architecture, linear scaling) |
| Redis | Good (for memory) | Good (Redis Cluster) |
| DynamoDB | Managed (provisioned capacity) | Excellent (fully managed auto-scaling) |
| Elasticsearch | Good | Excellent (shards and replicas) |

#### Scaling Architecture Patterns

1. **Functional Partitioning**
   - Separate databases by function (users, products, orders)
   - Works well with microservices architecture
   
2. **Sharding (Horizontal Partitioning)**
   - Partition data across multiple instances of the same schema
   - Common sharding keys: customer_id, geographic region, time

3. **Command-Query Responsibility Segregation (CQRS)**
   - Separate read and write models
   - Write to primary database
   - Derive read models for specific query patterns

4. **Event Sourcing**
   - Store state changes as immutable events
   - Rebuild state from event log
   - Scale by partitioning event streams

#### Real-World Example: GitHub's Evolution

1. Started with single MySQL instance
2. Added read replicas for read scaling
3. Implemented functional sharding (separate DBs for different features)
4. Used horizontal sharding for large tables
5. Employed distributed caching layer (Memcached)
6. Moved some functionality to specialized stores (Redis, Elasticsearch)

#### Pitfalls & Best Practices

**Pitfalls:**
- Premature horizontal scaling (adding complexity before needed)
- Choosing the wrong sharding key, requiring expensive re-sharding later
- Neglecting to handle cross-shard queries efficiently
- Underestimating operational complexity of distributed databases
- Failing to account for data consistency requirements

**Best Practices:**
- Start with vertical scaling until practical limits are reached
- Use database-specific scaling features when available
- Choose sharding keys based on access patterns, not just data distribution
- Build application logic to handle distributed database issues
- Test with realistic load before committing to an architecture
- Implement proper monitoring for distributed systems
- Consider managed database services to reduce operational burden
- Document scaling decisions and growth plans

#### Resources
- [Scaling Databases: Vertical vs Horizontal](https://www.digitalocean.com/community/tutorials/understanding-database-sharding)
- [PostgreSQL Scaling Strategies](https://www.citusdata.com/blog/2017/10/17/tour-of-postgres-index-types/)
- [MySQL Sharding Approaches](https://www.percona.com/blog/2009/08/06/why-you-dont-want-to-shard/)
- [MongoDB Sharding](https://docs.mongodb.com/manual/sharding/)
- [CockroachDB: How We Built a Distributed SQL Database](https://www.cockroachlabs.com/blog/how-we-built-cockroachdb/)
- [Netflix: Scaling the Netflix Data Platform](https://netflixtechblog.com/scaling-the-netflix-data-platform-1a3dfc1d93be)
- [Uber: Why Uber Engineering Switched from Postgres to MySQL](https://eng.uber.com/postgres-to-mysql-migration/)
- [Instagram's Sharding & IDs](https://instagram-engineering.com/sharding-ids-at-instagram-1cf5a71e5a5c)

### 1.6 Partitioning & Sharding Strategies

Partitioning divides large datasets into smaller, more manageable pieces, improving performance and scalability. Sharding specifically refers to horizontal partitioning across multiple machines.

#### Hash Partitioning

**Description**: Apply a hash function to a partition key to determine the partition.

**Formula**: 
```
partition_number = hash(key) % num_partitions
```

**Example** (Java implementation):
```java
public class HashPartitioner {
    private final int numPartitions;
    
    public HashPartitioner(int numPartitions) {
        this.numPartitions = numPartitions;
    }
    
    public int getPartition(String key) {
        // Using Java's hashCode, though production systems
        // would use a more uniform hash function
        return Math.abs(key.hashCode() % numPartitions);
    }
}

// Usage
HashPartitioner partitioner = new HashPartitioner(16);
int userPartition = partitioner.getPartition(userId);
String shardName = "shard-" + userPartition;

// Execute query on the appropriate shard
jdbcTemplate.query("SELECT * FROM users WHERE id = ?", 
                  new Object[]{userId}, 
                  userMapper);
```

**Pros**:
- Uniform data distribution
- Simple to implement and understand
- Good for point lookups by key
- Even load distribution

**Cons**:
- Cannot support efficient range queries
- Adding/removing nodes requires rehashing (major data movement)
- No data locality for related items

**Use Cases**:
- Key-value stores
- Distributed caches
- Data with no inherent ordering requirements

#### Range Partitioning

**Description**: Partition based on ranges of values (e.g., dates, alphabetical ranges).

**Example** (PostgreSQL range partitioning):
```sql
-- Create parent table
CREATE TABLE events (
    id UUID PRIMARY KEY,
    event_time TIMESTAMP NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    data JSONB NOT NULL
) PARTITION BY RANGE (event_time);

-- Create partitions for different date ranges
CREATE TABLE events_2023_q1 PARTITION OF events
    FOR VALUES FROM ('2023-01-01') TO ('2023-04-01');
    
CREATE TABLE events_2023_q2 PARTITION OF events
    FOR VALUES FROM ('2023-04-01') TO ('2023-07-01');
    
CREATE TABLE events_2023_q3 PARTITION OF events
    FOR VALUES FROM ('2023-07-01') TO ('2023-10-01');
    
CREATE TABLE events_2023_q4 PARTITION OF events
    FOR VALUES FROM ('2023-10-01') TO ('2024-01-01');

-- Create indexes on each partition
CREATE INDEX idx_events_2023_q1_user ON events_2023_q1(user_id);
CREATE INDEX idx_events_2023_q2_user ON events_2023_q2(user_id);
CREATE INDEX idx_events_2023_q3_user ON events_2023_q3(user_id);
CREATE INDEX idx_events_2023_q4_user ON events_2023_q4(user_id);
```

**Java/Spring Implementation**:
```java
@Entity
@Table(name = "events")
public class Event {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private LocalDateTime eventTime;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false, columnDefinition = "jsonb")
    private String data;
    
    // Getters and setters
}

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    // Will use partition elimination for this query
    List<Event> findByEventTimeBetweenAndUserId(
        LocalDateTime start, 
        LocalDateTime end,
        String userId
    );
}
```

**Pros**:
- Efficient range queries
- Natural for time-series data
- Easy to add new partitions for new ranges
- Good data locality for related items
- Partition pruning for queries on partition key

**Cons**:
- Potential for skewed data distribution and hot partitions
- Must carefully choose ranges to ensure balance
- May require regular maintenance (adding new partitions)

**Use Cases**:
- Time-series data
- Geographically partitioned data
- Log data with time component
- Data with natural hierarchical structure

#### Consistent Hashing

**Description**: A hashing technique that minimizes rehashing when the number of partitions changes.

**How It Works**:
1. Map both nodes and keys to positions on a conceptual ring (0 to 2^32 - 1)
2. For each key, find the nearest node clockwise on the ring
3. When adding/removing a node, only keys mapped to that node need to be reassigned

**Java Implementation**:
```java
public class ConsistentHash<T> {
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle = new TreeMap<>();

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
        
        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            // Create virtual nodes for better distribution
            circle.put(hashFunction.hash(node.toString() + i), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
    }

    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        
        int hash = hashFunction.hash(key);
        
        if (!circle.containsKey(hash)) {
            // Find the first node clockwise from this key
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        
        return circle.get(hash);
    }

    public interface HashFunction {
        int hash(Object key);
    }
}

// Usage
HashFunction murmur3Hash = key -> Hashing.murmur3_32().hashString(key.toString(), StandardCharsets.UTF_8).asInt();
Set<String> nodes = Set.of("shard1", "shard2", "shard3", "shard4");
ConsistentHash<String> consistentHash = new ConsistentHash<>(murmur3Hash, 100, nodes);

// Get shard for a user
String shard = consistentHash.get(userId);
```

**Pros**:
- Minimal data movement when cluster changes
- Good distribution with virtual nodes
- No central directory needed
- Efficient scaling up and down

**Cons**:
- More complex implementation
- Potential for unbalanced load with naive implementation
- Hash space needs to be large enough to avoid collisions

**Use Cases**:
- Distributed caches (e.g., Memcached, Redis)
- Distributed storage systems
- Environments with frequent node changes
- Peer-to-peer systems

#### Directory-Based Partitioning

**Description**: Use a lookup service to map keys to partitions.

**Example** (Using Redis as a directory service):
```java
@Service
public class DirectoryPartitionService {
    private final StringRedisTemplate redisTemplate;
    private final Map<String, JdbcTemplate> shardJdbcTemplates;
    
    public DirectoryPartitionService(
            StringRedisTemplate redisTemplate,
            List<DataSource> shardDataSources) {
        this.redisTemplate = redisTemplate;
        this.shardJdbcTemplates = new HashMap<>();
        
        int shardIndex = 0;
        for (DataSource ds : shardDataSources) {
            shardJdbcTemplates.put("shard" + shardIndex, new JdbcTemplate(ds));
            shardIndex++;
        }
    }
    
    public void saveUser(User u) {
        String shardId = getShardForUser(u.getId());
        JdbcTemplate jdbcTemplate = shardJdbcTemplates.get(shardId);
        
        jdbcTemplate.update(
            "INSERT INTO users(id, name, email) VALUES(?, ?, ?)",
            u.getId(), u.getName(), u.getEmail()
        );
    }
    
    public User getUser(String userId) {
        String shardId = getShardForUser(userId);
        JdbcTemplate jdbcTemplate = shardJdbcTemplates.get(shardId);
        
        return jdbcTemplate.queryForObject(
            "SELECT id, name, email FROM users WHERE id = ?",
            new UserRowMapper(), userId
        );
    }
    
    private String getShardForUser(String userId) {
        String shardId = redisTemplate.opsForValue().get("user_shard:" + userId);
        
        if (shardId == null) {
            // Assign to a shard based on some strategy
            shardId = assignToShard(userId);
            redisTemplate.opsForValue().set("user_shard:" + userId, shardId);
        }
        
        return shardId;
    }
    
    private String assignToShard(String userId) {
        // Simple round-robin assignment for this example
        List<String> shards = new ArrayList<>(shardJdbcTemplates.keySet());
        long count = redisTemplate.opsForValue().increment("shard_assignment_counter");
        return shards.get((int)(count % shards.size()));
    }
}
```

**Pros**:
- Flexible partitioning scheme
- Can adapt to changing access patterns
- Support for complex partitioning logic
- Can rebalance without moving all data

**Cons**:
- Directory service becomes a dependency
- Potential performance bottleneck
- Extra hop for lookups
- Directory service needs high availability

**Use Cases**:
- Complex or dynamic sharding requirements
- Systems where partition mapping changes frequently
- Multi-tenant systems with variable tenant sizes
- When you need custom partitioning logic beyond hashing

#### Multi-Dimensional Partitioning

**Description**: Partitioning data across multiple dimensions (e.g., date and customer).

**Example** (PostgreSQL with composite partitioning):
```sql
-- Multi-dimensional partitioning
CREATE TABLE sales (
    id BIGSERIAL,
    region TEXT NOT NULL,
    sale_date DATE NOT NULL,
    customer_id BIGINT NOT NULL,
    amount NUMERIC(12,2) NOT NULL
) PARTITION BY LIST (region);

CREATE TABLE sales_us PARTITION OF sales FOR VALUES IN ('US')
    PARTITION BY RANGE (sale_date);

CREATE TABLE sales_eu PARTITION OF sales FOR VALUES IN ('EU')
    PARTITION BY RANGE (sale_date);

-- Sub-partitions (quarterly) for US
CREATE TABLE sales_us_q1 PARTITION OF sales_us
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');
CREATE TABLE sales_us_q2 PARTITION OF sales_us
    FOR VALUES FROM ('2024-04-01') TO ('2024-07-01');
-- (repeat for q3/q4)
```

**Pros**:
- Combines locality dimensions (geo + time)
- Enables pruning on either dimension
- Easy archival (detach old sub-partitions)

**Cons**:
- More complex DDL management
- Risk of partition explosion (too many small partitions)
- Planner overhead if > few thousand partitions

**Best Practices**:
- Target partition count < 1000 active
- Automate rolling creation (cron / scheduler)
- Monitor partition bloat & attach/detach performance

#### Resources (Partitioning & Multi-Dimensional)
- https://www.postgresql.org/docs/current/partitioning.html (PostgreSQL partitioning official docs)  
- https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/bp-partition-key-design.html (DynamoDB partition key design)  
- https://vitess.io/docs/concepts/sharding/ (Vitess sharding at scale)  
- https://engineering.sketch.com/sharding-postgresql-at-sketch-a-practical-guide-c7b000b0e0da (Practical Postgres sharding case study)  
- https://shopify.engineering/how-shopify-scaled-sharded-mysql (Shopify MySQL sharding)  
- https://cassandra.apache.org/doc/latest/cassandra/cql/indexes.html (Cassandra secondary index cautions)  
- https://www.elastic.co/guide/en/elasticsearch/reference/current/inverted-index.html (Inverted index fundamentals)  
- https://research.google/pubs/pub36726/ (Columnar storage – Dremel paper)  
- https://jepsen.io/analyses (Partition-related anomaly analyses)  
- https://arxiv.org/abs/1406.2294 (Jump Consistent Hash paper)  

---

### Data Rebalancing Approaches (Shards/Partitions)

| Strategy | Trigger | Data Movement | Online? | Example Use |
|----------|---------|---------------|---------|-------------|
| Big-Bang Rehash | Manual scale event | High (all keys) | Risky | Early prototypes |
| Consistent Hash + VNodes | Node add/remove | Minimal (K/N) | Yes | Caches / KV stores |
| Range Split | Size threshold | Only hot range | Yes | Time-series / OLTP large tables |
| Directory Reassignment | Load skew / hotspot | Selected keys | Yes | Multi-tenant isolation |
| Dual-Write Migration | Schema or engine change | Ingest delta only | Yes | Engine upgrades |
| Shadow Copy + Cutover | Storage tier move | One-time copy | Planned | Archival / cold storage |

**Online Dual-Write (Pattern)**:
1. Start: Source authoritative; begin async replication → target.
2. Catch-up: Track high-water-mark (HWM).
3. Consistency window: Quiesce writes OR accept limited lag & reconcile.
4. Cutover: Switch write path to target, shadow read compare (optional).
5. Decommission: Remove source after soak.

**Spring Batch Assisted Rebalance (Pseudo)**:
```java
// Pseudo skeleton for streaming key migration
while (true) {
  List<KeyRange> hotRanges = metaService.findHotRanges();
  for (KeyRange r : hotRanges) {
     streamKeys(r).parallel().forEach(k -> {
        if (shouldMove(k)) {
           // Idempotent copy
           var val = source.read(k);
           target.writeIfAbsent(k, val);
           // Verify & remove (optional delay)
           if (verify(k, val)) source.delete(k);
        }
     });
  }
  Thread.sleep(rebalanceIntervalMs);
}
```

**Pitfalls**:
- Non-idempotent copy logic (duplicates)
- Clock skew in HWM tracking
- Long tail of small “straggler” partitions
- Thundering herd after split (clients cache old mapping)

**Best Practices**:
- Embed version/epoch with shard mapping
- Instrument moved_keys/sec, remaining_keys
- Back-pressure rebalancing under peak load
- Use checksums (row_count, crc32) per range

#### Resources (Rebalancing & Online Migration)
- https://research.google/pubs/pub27897/ (Spanner rebalancing / tablet movement)  
- https://www.linkedin.com/blog/engineering/apache-kafka/cruise-control-the-self-balancing-kafka (Kafka Cruise Control auto-balancing)  
- https://engineering.fb.com/2018/08/31/data-infrastructure/rocksdb/ (RocksDB compaction & balancing insights)  
- https://www.elastic.co/blog/how-elasticsearch-nodes-handle-shard-rebalancing (Elasticsearch shard rebalancing)  
- https://docs.mongodb.com/manual/core/sharded-cluster-requirements/ (MongoDB chunk splitting & balancing)  
- https://cloud.google.com/spanner/docs/whitepapers/automatic-sharding (Automatic sharding principles)  
- https://dgraph.io/blog/post/rebalancing/ (Tablet / predicate rebalancing)  
- https://aws.amazon.com/builders-library/workload-isolation-using-cell-based-architecture/ (Cell based scaling & isolation)  
- https://blog.cloudflare.com/consistent-hashing-algorithm-for-load-balancing/ (Production consistent hashing)  

---

### 1.7 Database Engine Internals (Focused Recap)

| Aspect | B-Tree | LSM Tree |
|--------|--------|----------|
| Write Path | In-place (random I/O) | Append (memtable → SSTable) |
| Read Amplification | Low | Medium (multiple levels) |
| Write Amplification | Low–Medium | Higher (compaction) |
| Range Scans | Efficient | Good (post-compaction) |
| Space Amplification | Low | Medium (overlapping runs) |
| Ideal Workload | Read-heavy mixed OLTP | Write-heavy / time-series / logs |

**LSM Compaction Tuning (Cassandra/RocksDB)**:
- High write amplification → monitor `sstables per read`
- Use Bloom filters to mitigate read amplification
- Tiered vs leveled compaction trade: leveled = lower read amp / higher write amp

**WAL Essentials**:
```
Client Txn → Append WAL (fsync) → Ack → Apply to pages (buffer pool) → Checkpoint
```
- Group commit reduces fsync overhead
- Separate WAL device (fast NVMe) for latency-sensitive workloads

**MVCC Snapshot Logic (Simplified)**:
- Each row version: (xmin, xmax, tuple)
- Visible if: xmin committed AND (xmax is null OR xmax > snapshot_xid)
- Vacuum/GC removes dead versions once no active snapshot can see them

**Buffer Pool Heuristics**:
- LRU-K / CLOCK variants to avoid scan pollution
- Dirty page flush tuned by dirty_ratio & checkpoint interval
- Track hit ratio > 95% for OLTP; below → memory pressure

**Pitfalls**:
- Over-aggressive autovacuum disabled → table bloat
- Tiny random writes on network-attached storage (latency spikes)
- WAL saturation during bulk ingest without batch sizing

**Best Practices**:
- Separate WAL & data volumes
- Right-size checkpoint segments (Postgres `max_wal_size`)
- Monitor write amplification (LSM): (bytes_written_storage / bytes_ingested)

#### Resources (Engine Internals)
- https://www.postgresql.org/docs/current/storage-wal.html (PostgreSQL WAL)  
- https://dev.mysql.com/doc/refman/8.0/en/innodb-storage-engine.html (InnoDB architecture)  
- https://rocksdb.org/ (RocksDB LSM implementation)  
- https://github.com/facebook/mysql-5.6/wiki/MyRocks-engine (MyRocks LSM for MySQL)  
- https://www.cockroachlabs.com/docs/stable/architecture/transaction-layer.html (Txn & MVCC internals)  
- https://www.foundationdb.org/files/fdb-paper.pdf (FoundationDB storage & MVCC)  
- https://www.cs.umb.edu/~poneil/lsmtree.pdf (Original LSM-Tree paper)  
- https://www.usenix.org/system/files/fast21-gala.pdf (WAL & persistence optimization research)  
- https://sqlite.org/tempfiles.html (Write patterns & journaling modes comparison)  

---

### 1.8 Indexing Deep Dive (Summary Layer)

| Index Type | Strength | Weakness | Use Case |
|------------|----------|----------|----------|
| B-Tree | Point + range | Large maintenance on churn | General OLTP |
| Hash | Equality | No range, collisions | Hot key lookups |
| Bitmap | Low-cardinality filters | Costly updates | Warehousing filters |
| Columnar (Projection) | Aggregations/compression | Writes expensive | Analytics (OLAP) |
| GIN/FTS | Full-text search | Memory & build cost | Search box |
| GiST/R-Tree | Spatial | Complex tuning | Geo queries |
| Inverted (search engine) | Token queries | Eventually consistent | Log / doc search |
| Vector (HNSW/IVF) | Semantic similarity | Approximate | Recommendations |

**Covering (INCLUDE) Example (Postgres)**:
```sql
CREATE INDEX idx_orders_user_status_include
  ON orders (user_id, status)
  INCLUDE (total_amount, created_at);
-- Query can be index-only: SELECT total_amount FROM orders WHERE user_id=? AND status='PAID';
```

**Maintenance Metrics**:
- Index bloat (% wasted pages)
- idx_scan vs seq_scan (pg_stat_user_indexes)
- Fragmentation level (fillfactor tuning)

**Anti-Patterns**:
- Index every column (write amplification)
- Redundant prefixes (idx (a), (a,b))
- Function mismatch (query uses LOWER(col) w/o functional index)

**Functional / Partial Example**:
```sql
CREATE INDEX idx_user_email_lower ON users (lower(email));
CREATE INDEX idx_orders_recent ON orders (user_id)
  WHERE created_at > now() - interval '30 days';
```

**Pitfalls**:
- Bitmap scans unexpected due to low stats target
- Parameter sniffing causing suboptimal plan
- High cardinality columns placed late in composite index

**Best Practices**:
- Leading column = most selective & frequently filtered
- Periodic REINDEX/VACUUM (where needed)
- Measure benefit (explain ANALYZE) before shipping

#### Resources (Indexing)
- https://www.postgresql.org/docs/current/indexes.html (PostgreSQL index types)  
- https://use-the-index-luke.com/ (Practical indexing & query patterns)  
- https://dev.mysql.com/doc/refman/8.0/en/optimization-indexes.html (MySQL index optimization)  
- https://docs.mongodb.com/manual/indexes/ (MongoDB indexes & TTL / partial)  
- https://cassandra.apache.org/doc/latest/cassandra/cql/indexes.html (Cassandra secondary index cautions)  
- https://www.elastic.co/guide/en/elasticsearch/reference/current/inverted-index.html (Inverted index fundamentals)  
- https://research.google/pubs/pub36726/ (Columnar storage – Dremel paper)  
- https://oracle.com/technetwork/database/bi-datawarehousing/twp-bitmap-index-098860.html (Bitmap index whitepaper)  
- https://rockset.com/blog/bloom-filters-in-indexes/ (Bloom filters reducing IO)  
- https://www.postgresql.org/docs/current/gin-intro.html (GIN / FTS internals)  

---

### 1.9 SQL Query Optimization

#### Execution Flow
```
Parse → Rewrite → Plan (cost-based) → Execute (iterate nodes) → Return rows
```

#### EXPLAIN Anatomy
```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT u.id, o.total_amount
FROM users u
JOIN orders o ON o.user_id = u.id
WHERE u.status='ACTIVE'
  AND o.created_at > now() - interval '7 days'
ORDER BY o.created_at DESC
LIMIT 20;
```
Sample (conceptual):
```
Limit  (cost=1200..1205 rows=20) (actual ... )
  ->  Sort (cost=1200..1300) key:o.created_at DESC
        -> Nested Loop
            -> Index Scan users(status='ACTIVE')
            -> Index Scan orders(user_id=..., created_at > ...)
```

#### Common Anti-Patterns
| Anti-Pattern | Symptom | Fix |
|--------------|---------|-----|
| SELECT * | Excess I/O | Select needed columns |
| Leading wildcard LIKE '%abc' | Full scan | Use trigram / FTS |
| OR across different columns | Scan fallback | UNION ALL + indexes |
| Implicit type cast | Index ignored | Align types (e.g.,::uuid) |
| Huge IN list (>1000) | Planner overhead | Temp table join |

#### Tooling
- Postgres: `pg_stat_statements`, auto_explain
- MySQL: slow query log, pt-query-digest
- JIT toggles (PG13+) for analytical bursts
- HypoPG for hypothetical indexes (what-if cost)

#### Plan Stability
- Use prepared statements / bind parameters
- Guard against parameter sniffing: plan forcing or hints (vendor-specific)

**Spring Data Example (Explicit Projection)**:
```java
@Query("""
   SELECT new com.example.OrderView(o.id, o.totalAmount)
   FROM Order o
   WHERE o.user.id = :userId AND o.createdAt > :after
   ORDER BY o.createdAt DESC
""")
List<OrderView> recentOrders(UUID userId, Instant after);
```

**Pitfalls**:
- Relying solely on ORM defaults
- Missing composite index order vs ORDER BY
- Ignoring cardinality misestimates (update statistics!)

**Best Practices**:
- Track top N queries by total time & mean + p95
- Add query latency SLO dashboards
- Regression test critical query plans (hash vs nested loop changes)

---

### 1.10 Database Selection Guide (With Spring Integration)

| Requirement | Recommended | Rationale |
|-------------|-------------|-----------|
| Strict financial txns + complex joins | PostgreSQL | Mature ACID + extensibility |
| Global serializable + multi-region | Spanner / CockroachDB | TrueTime / hybrid logical clocks |
| Write-heavy time-series metrics | ClickHouse / TimescaleDB | Columnar compression + hypertables |
| Massive write throughput KV | Cassandra / DynamoDB | Linear scale, tunable consistency |
| Flexible JSON + secondary indexes | MongoDB / PostgreSQL JSONB | Hybrid document + relational |
| Graph traversals depth > 3 | Neo4j | Index-free adjacency |
| Real-time cache / counters | Redis | In-memory speed |
| Vector similarity + hybrid filter | PostgreSQL + pgvector / Milvus | Combine filters + semantic |

#### DynamoDB (Spring Example)
```xml
<!-- pom -->
<dependency>
  <groupId>software.amazon.awssdk</groupId><artifactId>dynamodb</artifactId>
</dependency>
```
```java
@Bean
DynamoDbClient dynamo() {
  return DynamoDbClient.builder()
     .region(Region.US_EAST_1)
     .build();
}
public void putUser(User u) {
  dynamo.putItem(r -> r.tableName("users")
    .item(Map.of(
       "pk", AttributeValue.fromS("USER#" + u.id()),
       "sk", AttributeValue.fromS("PROFILE"),
       "email", AttributeValue.fromS(u.email())
    )));
}
```
Partition key considerations: uniform distribution (add random suffix if low entropy).

#### Cassandra
```java
@Table("user_events")
public class UserEvent {
  @PrimaryKey
  private UserEventKey key;
  @Column private String type;
  @Column private Instant ts;
}
@PrimaryKeyClass
public class UserEventKey {
  @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
  private UUID userId;
  @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.CLUSTERED)
  private Instant ts;
}
```
Model queries first; avoid unbounded partitions (bucket by day).

#### MongoDB
```java
db.users.createIndex({ email: 1 }, { unique: true });
db.events.createIndex({ userId: 1, ts: -1 });
```
Time-series: use TTL index or time-series collections (server >=5.0).

#### PostgreSQL JSONB Hybrid
```sql
CREATE TABLE products(
  id UUID PRIMARY KEY,
  attrs JSONB,
  created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_products_attr_color ON products ((attrs->>'color'));
```

#### CockroachDB
- Transparent sharding
- Follower reads for low-latency local reads (stale within bounded time)
- Use `ALTER TABLE ... EXPERIMENTAL_RELOCATE` (version-specific) for lease rebalancing

#### Neo4j
```cypher
CREATE INDEX user_email IF NOT EXISTS FOR (u:User) ON (u.email);
MATCH (u:User {email:$e})-[:PURCHASED]->(p:Product)-[:BELONGS_TO]->(c:Category)
RETURN p LIMIT 10;
```

#### TimeSeries (TimescaleDB Hypertable)
```sql
CREATE TABLE metrics (
  ts TIMESTAMPTZ NOT NULL,
  series_id INT,
  value DOUBLE PRECISION
);
SELECT create_hypertable('metrics','ts', chunk_time_interval => INTERVAL '1 day');
```

**Pitfalls**:
- Forcing graph workloads onto relational without adjacency optimization
- Treating DynamoDB like relational (anti-pattern: ad-hoc queries)
- Oversharding Cassandra → overhead > benefit
- Ignoring hot partition keys

**Best Practices**:
- Document read/write patterns pre-selection
- Run PoC with realistic load profile
- Project 12–24 month storage & RPS growth

#### Resources (SQL Optimization)
- https://www.postgresql.org/docs/current/using-explain.html (Postgres EXPLAIN)  
- https://dev.mysql.com/doc/refman/8.0/en/explain-output.html (MySQL EXPLAIN)  
- https://use-the-index-luke.com/sql/ (Index & query tuning)  
- https://www.pgstats.dev/ (Monitoring Postgres stats)  
- https://planetscale.com/blog/mysql-query-performance (MySQL tuning patterns)  
- https://www.cockroachlabs.com/docs/stable/performance-best-practices-overview.html (Distributed SQL performance)  
- https://clickhouse.com/docs/en/operations/optimizing-performance (ClickHouse query optimization)  
- https://www.elastic.co/guide/en/elasticsearch/reference/current/tune-for-indexing-speed.html (Ingestion vs search tradeoffs)  
- https://oracle.com/technetwork/database/bi-datawarehousing/twp-sql-analytic-functions-097715.html (Analytic/query window functions)  
- https://docs.microsoft.com/sql/relational-databases/performance/ (SQL Server performance guide)  
---

## 2. NETWORKING FUNDAMENTALS

### 2.1 Protocol Stack & OSI Model Deep Dive

```
Layer | OSI           | Examples
------+---------------+----------------------------
7     | Application   | HTTP, gRPC, DNS, SMTP
6     | Presentation  | TLS, SSL (encryption), JSON
5     | Session       | (Rarely explicit now) TLS sessions
4     | Transport     | TCP, UDP, QUIC
3     | Network       | IP (IPv4/IPv6), ICMP
2     | Data Link     | Ethernet, Wi-Fi (802.11)
1     | Physical      | Fiber, Copper, Radio
```

**Real Mapping (Modern)**:
- QUIC (UDP-based) collapses transport + crypto handshake
- TLS straddles Presentation/Session
- HTTP/3 over QUIC reduces head-of-line blocking

**Pitfalls**:
- Assuming TCP guarantees application-level message boundaries (it’s a stream)
- Ignoring MTU & fragmentation (Path MTU black holes)
- Overlooking Nagle + delayed ACK impact on small chatty packets

**Best Practices**:
- Use TCP_NODELAY for latency-critical small writes
- Monitor retransmission & RTT for congestion issues
- Prefer HTTP/2 multiplexing; upgrade to HTTP/3 for mobile/lossey networks

Resources:  
- [RFC 1122/1123 host requirements](https://datatracker.ietf.org/doc/html/rfc1122)  
- [QUIC RFC 9000](https://datatracker.ietf.org/doc/html/rfc9000)  
- [High Performance Browser Networking (Ilya Grigorik)](https://hpbn.co/)  

---

### 2.2 HTTP Fundamentals

**Lifecycle**:
```
DNS Resolve → TCP (or QUIC) Handshake → TLS Handshake → Request Headers → Request Body → Server → Response Headers → Body → (Keep-Alive)
```

**Key Headers**:
| Category | Headers | Purpose |
|----------|---------|---------|
| Caching | Cache-Control, ETag, Last-Modified | Freshness & revalidation |
| Security | CSP, X-Frame-Options, HSTS | Mitigate XSS, clickjacking |
| Auth | Authorization, WWW-Authenticate | Client identity |
| Observability | Traceparent, X-Request-Id | Trace correlation |
| Compression | Accept-Encoding / Content-Encoding | Reduce payload size |

**HTTP/1.1 vs 2 vs 3**:
| Feature | 1.1 | 2 | 3 (QUIC) |
|---------|-----|---|----------|
| Multiplexing | No | Yes | Yes |
| HOL Blocking (transport) | Yes | TCP-based | Eliminated (stream-level) |
| Header Compression | Limited | HPACK | QPACK |
| 0-RTT | No | TLS 1.3 (possible) | Yes (QUIC 0-RTT) |
| Connection Migration | No | No | Yes |

**TLS Handshake (Simplified)**:
```
ClientHello → ServerHello (+Cert) → (Key Exchange) → Finished → Secure Channel
```

**Pitfalls**:
- Overusing cookies (bloat bandwidth)
- Missing idempotency for unsafe retries (POST/PUT)
- Large header sets without compression

**Best Practices**:
- Use structured logging of method+path+status+latency
- Prefer immutable static content with far-future Cache-Control + fingerprint
- Leverage conditional GET (ETag / If-None-Match)

Resources:  
- [RFC 9110 (HTTP Semantics)](https://datatracker.ietf.org/doc/html/rfc9110)  
- [TLS 1.3 RFC 8446](https://datatracker.ietf.org/doc/html/rfc8446)  
- [OWASP Secure Headers Project](https://owasp.org/www-project-secure-headers/)  

---

### 2.3 DNS Architecture

**Hierarchy**:
```
Root (.) → TLD (.com) → Authoritative (example.com) → Record Answers
```
**Resolution Path** (Recursive Resolver Cache Miss):
1. Query root → referral to .com
2. Query .com → referral to authoritative
3. Query authoritative → final A/AAAA
4. Cache per TTL

**Record Types**:
| Type | Purpose |
|------|---------|
| A / AAAA | IPv4 / IPv6 address |
| CNAME | Canonical name alias |
| MX | Mail exchange |
| TXT | Arbitrary text (SPF, verification) |
| SRV | Service location (proto/port) |
| NS | Delegation name server |
| PTR | Reverse DNS |

**TTL Implications**:
- Low TTL → agility (failover) + higher query load
- High TTL → cache efficiency + slower failover

**Pitfalls**:
- Using CNAME at zone apex (not allowed; use ALIAS/ANAME from provider)
- Premature TTL lowering after outage (propagation delay still applies)
- Split-brain with inconsistent zone updates

**Best Practices**:
- Warm disaster recovery by pre-configuring secondary records
- Stagger TTL reduction before planned cutovers
- Apply DNSSEC if integrity is critical

Resources:  
- [DNS & BIND Book](https://www.oreilly.com/library/view/dns-and-bind/9781449305192/)  
- [RFC 1034/1035](https://datatracker.ietf.org/doc/html/rfc1034)  
- [DNSSEC RFC 4033/4034/4035](https://datatracker.ietf.org/doc/html/rfc4033)  
- [O'Reilly: DNS in detail](https://www.oreilly.com/library/view/dns-and-bind/9781449305192/)  

---

### 2.4 TCP vs UDP

| Aspect | TCP | UDP |
|--------|-----|-----|
| Connection | Stateful (3-way handshake) | Connectionless |
| Reliability | Guaranteed (retransmit, ACK) | None |
| Ordering | Guaranteed | None |
| Congestion Control | Yes (Reno/Cubic/BBR) | None (app-level) |
| Head-of-Line Blocking | Yes | No |
| Use Cases | Web, DB, file transfer | Streaming, VoIP, gaming, QUIC substrate |

**3-Way Handshake**:
```
Client: SYN →
Server: ← SYN-ACK
Client: ACK →
```

**Termination (4-way)**: FIN/ACK pairs + TIME_WAIT

**Congestion Control**:
- Slow start → congestion avoidance → fast retransmit
- BBR models bandwidth + RTT for optimal pacing

**Pitfalls**:
- Long TIME_WAIT exhausting ports under high ephemeral churn
- Nagle + delayed ACK causing latency on small writes
- UDP fragmentation (exceeding path MTU) leads to silent drops

**Best Practices**:
- Enable TCP keepalives for long idle connections
- Tune kernel params (e.g., somaxconn) for high-conn workloads
- For latency-critical → consider QUIC

Resources:  
- [TCP Illustrated (Stevens)](https://www.kohala.com/start/tcpipiv1.html)  
- [BBR Congestion Control papers](https://datatracker.ietf.org/doc/html/draft-cardwell-iccrg-bbr-congestion-control-02)  
- [QUIC design drafts](https://quicwg.org/)  

---

### 2.5 Load Balancers

| Layer | L4 (Transport) | L7 (Application) |
|-------|----------------|------------------|
| Visibility | IP:Port | HTTP headers, paths |
| Features | NAT, basic balancing | Routing, auth, WAF, compression |
| Overhead | Lower | Higher |
| Examples | AWS NLB, HAProxy (TCP mode) | Envoy, Nginx, ALB |

**Algorithms**:
| Algo | Description | Use |
|------|-------------|-----|
| Round Robin | Sequential | Uniform backends |
| Least Connections | Fewest active | Variable request times |
| IP Hash | Hash src IP | Session affinity |
| Weighted RR | Weighted distribution | Mixed capacity nodes |
| Consistent Hash | Stable mapping | Cache locality |

**Health Checks**:
- L4: TCP connect success
- L7: HTTP 200 on /healthz + dependency checks
- Passive: error rate triggers outlier ejection (Envoy)

**Circuit Breaking at LB**: Eject instances with elevated 5xx or latency.

**SSL Termination vs Passthrough**:
- Termination: Offload crypto, allow header inspection
- Passthrough: End-to-end encryption (mTLS scenarios)

**Global Load Balancing (GSLB)**:
- Geo-DNS latency-based
- Anycast + BGP advertisement
- HTTP 302 regional steering (fallback)

**Pitfalls**:
- Sticky sessions hinder horizontal scaling
- Health check endpoint doing expensive logic
- Ignoring slow-start warmup (cold caches → fail health)

**Best Practices**:
- Use readiness vs liveness separation
- Enable connection draining on deploy
- Implement exponential backoff for client retries (avoid stampede)

Resources:  
- [Envoy Proxy docs](https://www.envoyproxy.io/docs/envoy/latest/)  
- [HAProxy configuration guide](https://www.haproxy.com/documentation/hapee/latest/configuration/)  
- [AWS ALB/NLB whitepapers](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/introduction.html)  
- [Google Maglev (consistent hashing LB)](https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/44824.pdf)  

---

### 2.6 API Protocols Comparison

| Dimension | REST | GraphQL | gRPC |
|-----------|------|---------|------|
| Data Shape | Fixed (resource) | Client-defined | Proto contract |
| Transport | HTTP/1.1/2 | HTTP/1.1/2 | HTTP/2 (streams) |
| Streaming | SSE (server-only) | Subscriptions | Bi/uni streaming |
| Performance | Verbose JSON | Single round-trip | Binary, efficient |
| Caching | Native HTTP | Custom persisted queries | Manual |
| N+1 Risk | Low (coarse endpoints) | High (resolvers) | Low (server design) |
| Versioning | URL / header / media-type | Evolve schema w/ deprecation | Proto backward compatible |
| Tooling | Mature | Growing (Apollo) | Strong (grpcurl, reflection) |
| Use Cases | Public APIs | Aggregation / BFF | Internal S2S, low latency |

**GraphQL N+1 Mitigation**:
- DataLoader batching (group by key)
- Field-level complexity limits
- Persisted queries whitelist

**gRPC Streaming Example (Java)**:
```proto
service Chat {
  rpc StreamMessages(stream ChatMessage) returns (stream ChatMessage);
}
```
```java
public StreamObserver<ChatMessage> streamMessages(StreamObserver<ChatMessage> out) {
    return new StreamObserver<>() {
        public void onNext(ChatMessage msg) {
            // broadcast logic
            out.onNext(ack(msg));
        }
        public void onError(Throwable t) {}
        public void onCompleted() { out.onCompleted(); }
    };
}
```

**Pitfalls**:
- Overfetch/underfetch in REST monolith endpoints
- GraphQL unrestricted nested queries causing DoS
- Lack of backpressure in client streaming (design carefully)

**Best Practices**:
- Enforce query depth & cost for GraphQL
- Use deadlines/timeouts in gRPC metadata
- Cache persisted GraphQL queries (hash → query)

Resources:  
- [REST maturity model (Richardson)](https://martinfowler.com/articles/richardsonMaturityModel.html)  
- [Apollo GraphQL docs](https://www.apollographql.com/docs/)  
- [gRPC load balancing & retries docs](https://grpc.io/docs/guides/loadbalancing/)  
- [Push vs poll architectural discussions](https://blog.bitsrc.io/push-vs-pull-architecture-in-web-applications-2c2a3c6e8c6e)  

---

### 2.7 WebSockets

**Handshake**:
```
HTTP GET Upgrade: websocket
Sec-WebSocket-Key → server responds with Sec-WebSocket-Accept
101 Switching Protocols → full-duplex
```

**Frame Types**: Text, Binary, Ping/Pong (keepalive), Close.

**Scaling**:
- Sticky sessions OR shared distributed pub/sub (Redis, Kafka)
- Backpressure: track send queue length per connection
- Fanout: shard channels by hash(channel_id) across nodes

**Failure Handling**:
- Heartbeat (Ping/Pong) interval
- Reconnect with exponential backoff + jitter
- Session resume token (optional)

**Pitfalls**:
- Leaking connections without idle timeouts
- Broadcasting large payloads (fragmentation)
- Missing auth re-validation (long-lived token expiry)

**Best Practices**:
- Keep auth claims minimal (refresh via control frame)
- Monitor active connections, message latency
- Apply per-connection rate limits

Resources:  
- [RFC 6455](https://datatracker.ietf.org/doc/html/rfc6455)  
- [Socket.IO scaling patterns](https://socket.io/docs/v4/scaling/)  
- [AWS API Gateway WebSocket design](https://docs.aws.amazon.com/apigateway/latest/developerguide/apigateway-websocket-api.html)  

---

### 2.8 Long Polling vs Short Polling vs SSE vs WebSockets

| Method | Pattern | Overhead | Latency | Bi-Directional | Use Case |
|--------|--------|----------|---------|----------------|----------|
| Short Polling | Client polls fixed interval | High | Interval-bound | No | Low freq updates |
| Long Polling | Server holds request until event | Moderate | Low (on event) | No | Notifications |
| SSE (EventSource) | Server push over single HTTP | Low | Low | Server→Client only | Feeds, tickers |
| WebSockets | Full-duplex persistent | Low (after setup) | Very Low | Yes | Chats, games |

**Decision Flow**:
```
Need bi-directional? → Yes → WebSocket or gRPC streaming
No? Event frequency high? → Yes → SSE
Low frequency? → Long polling > short polling
```

**SSE Example**:
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache
retry: 5000

event: price
data: {"symbol":"AAPL","price":179.12}
```

**Pitfalls**:
- Short polling wasted QPS & battery (mobile)
- SSE limited in some proxies / older browsers
- WebSocket idle drop (load balancer default timeouts)

**Best Practices**:
- Use heartbeat comments in SSE (":keepalive\n\n")
- Tune LB idle timeouts > heartbeat gap
- Back off reconnects (avoid thundering herd)

Resources:  
- [MDN EventSource](https://developer.mozilla.org/en-US/docs/Web/API/EventSource)  
- [WebSocket RFC 6455](https://datatracker.ietf.org/doc/html/rfc6455)  
- [gRPC streaming docs](https://grpc.io/docs/languages/java/basics/#server-side-streaming-rpc)  
- [Push vs poll architectural discussions](https://blog.bitsrc.io/push-vs-pull-architecture-in-web-applications-2c2a3c6e8c6e)  

---

### Networking Pitfalls & Best Practices Summary

| Pitfall | Impact | Mitigation |
|---------|--------|------------|
| Head-of-line blocking (HTTP/1.1) | Latency spikes | HTTP/2 or HTTP/3 |
| Unbounded read buffers (WS) | Memory bloat | Apply size limits / flow control |
| Excess custom headers | Bandwidth waste | Compress or reduce |
| Low DNS TTL without planning | Resolver load | Only drop TTL before planned change |
| Missing retries with jitter | Retry storms | Exponential backoff + jitter |
| Ignoring MTU (1500 vs jumbo) | Fragmentation loss | Path MTU discovery |

---

## Networking Resources (High-Quality)
- [HTTP/3 Explained (Daniel Stenberg)](https://daniel.haxx.se/http3-explained/)  
- [High Performance Browser Networking (Grigorik)](https://hpbn.co/)  
- [IETF QUIC Working Group drafts](https://quicwg.org/)  
- [DNS TTL & Caching (Cloudflare blog)](https://blog.cloudflare.com/everything-you-ever-wanted-to-know-about-dns-cache/)  
- [Envoy Proxy Architecture Docs](https://www.envoyproxy.io/docs/envoy/latest/architecture/overview.html)  
- [gRPC Load Balancing Blog (Google)](https://grpc.io/blog/loadbalancing/)  
- [WebSockets RFC 6455](https://datatracker.ietf.org/doc/html/rfc6455)  
- [SSE MDN Documentation](https://developer.mozilla.org/en-US/docs/Web/API/EventSource)  
- [TCP BBR Paper (Google)](https://research.google/pubs/pub45646/)  
| SSE (EventSource) | Server push over single HTTP | Low | Low | Server→Client only | Feeds, tickers |
| WebSockets | Full-duplex persistent | Low (after setup) | Very Low | Yes | Chats, games |

**Decision Flow**:
```
Need bi-directional? → Yes → WebSocket or gRPC streaming
No? Event frequency high? → Yes → SSE
Low frequency? → Long polling > short polling
```

**SSE Example**:
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache
retry: 5000

event: price
data: {"symbol":"AAPL","price":179.12}
```

**Pitfalls**:
- Short polling wasted QPS & battery (mobile)
- SSE limited in some proxies / older browsers
- WebSocket idle drop (load balancer default timeouts)

**Best Practices**:
- Use heartbeat comments in SSE (":keepalive\n\n")
- Tune LB idle timeouts > heartbeat gap
- Back off reconnects (avoid thundering herd)

Resources:
- MDN EventSource
- WebSocket RFC 6455
- gRPC streaming docs
- Push vs poll architectural discussions

---

### Networking Pitfalls & Best Practices Summary

| Pitfall | Impact | Mitigation |
|---------|--------|------------|
| Head-of-line blocking (HTTP/1.1) | Latency spikes | HTTP/2 or HTTP/3 |
| Unbounded read buffers (WS) | Memory bloat | Apply size limits / flow control |
| Excess custom headers | Bandwidth waste | Compress or reduce |
| Low DNS TTL without planning | Resolver load | Only drop TTL before planned change |
| Missing retries with jitter | Retry storms | Exponential backoff + jitter |
| Ignoring MTU (1500 vs jumbo) | Fragmentation loss | Path MTU discovery |

---

## Networking Resources (High-Quality)
- HTTP/3 Explained (Daniel Stenberg)  
- High Performance Browser Networking (Grigorik)  
- IETF QUIC Working Group drafts  
- DNS TTL & Caching (Cloudflare blog)  
- Envoy Proxy Architecture Docs  
- gRPC Load Balancing Blog (Google)  
- WebSockets RFC 6455  
- SSE MDN Documentation  
- TCP BBR Paper (Google)  

---

## Pitfalls & Best Practices (Global Recap)

| Domain | Core Pitfall | Best Practice |
|--------|--------------|--------------|
| DB Selection | Popularity bias | Align with workload & growth math |
| Partitioning | Hotspot shard | Composite or salted keys |
| Replication | Assuming sync | Monitor lag & consistency guarantees |
| Indexing | Over-indexing | Add per real query evidence |
| Query Tuning | OR sprawl | Rewrite to UNION / decompose |
| Caching | Stampede | Jitter, lock, early refresh |
| Protocol | Wrong push model | Match interaction pattern |
| LB | Stateful sessions | Externalize session (JWT / Redis) |
| Streaming | Missing backpressure | Flow control & lag metrics |

---

