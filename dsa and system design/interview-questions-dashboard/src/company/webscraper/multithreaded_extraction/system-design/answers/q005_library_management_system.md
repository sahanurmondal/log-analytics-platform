# Library Management System - Book Lending & Waitlist

## 📋 **Navigation**
- **Previous Question**: [Q4: Stock Price System Design](./q004_stock_price_system_design.md)
- **Next Question**: [Q6: Optimal Storage Strategy](./q006_optimal_storage_strategy.md)
- **Main Menu**: [System Design Questions](../README.md)

---

## 📝 **Problem Statement**

**Company**: Salesforce  
**Difficulty**: Medium  
**Question**: High-Level Design: Library Management System | Book Lending | Library Book Checkout and Waitlist System

Design a software system for a physical book lending library where users can browse/search catalogs, check out books, join waitlists when unavailable, and receive notifications when books become available. The system must handle concurrent users and prevent double checkouts.

---

## 1. 🎯 **PROBLEM UNDERSTANDING & REQUIREMENTS GATHERING**

### Problem Restatement
Design a comprehensive library management system that enables users to discover books, manage checkouts/returns, handle waitlists for popular titles, and coordinate with librarians for physical book management. The system must prevent race conditions in concurrent checkout scenarios and maintain accurate inventory tracking.

### Clarifying Questions

**Scale & Usage:**
- How many library branches? (Estimated: 50 branches, 1 main system)
- How many registered users? (100K users, 10K active daily)
- How many books in catalog? (1M titles, 5M physical copies)
- Peak concurrent users during busy periods? (1K concurrent users)

**Operational Requirements:**
- Maximum checkout duration? (3 weeks with renewal options)
- Waitlist size limits? (100 users per book)
- How do librarians process checkouts? (Barcode scanning + system confirmation)
- Inter-branch transfers allowed? (Yes, with 2-day delivery)

**Business Rules:**
- Maximum books per user? (10 books simultaneously)
- Overdue fine structure? ($0.25/day with caps)
- Reservation hold time? (3 days after notification)
- User categories with different privileges? (Student, Adult, Senior, Staff)

### Functional Requirements

**Core Features:**
- User registration and profile management
- Book catalog browsing with search and filters
- Real-time availability checking across branches
- Secure checkout process with librarian confirmation
- Automated waitlist management with FIFO ordering
- Return processing with condition assessment
- Notification system for availability and overdue items
- Fine calculation and payment processing

**Advanced Features:**
- Inter-branch book transfer requests
- Book recommendation system based on reading history
- Digital resource management (e-books, audiobooks)
- Reading challenge and community features
- Analytics dashboard for librarians

### Non-Functional Requirements

**Performance:**
- Support 1K concurrent users during peak hours
- Search response time < 500ms
- Checkout process completion < 30 seconds
- 99.9% uptime during library operating hours

**Reliability:**
- Zero lost checkout transactions
- Accurate inventory tracking at all times
- Consistent waitlist ordering across concurrent operations
- Automated backup and recovery procedures

**Security:**
- User data protection (GDPR compliance)
- Librarian authentication and role-based access
- Audit trail for all book movements
- Secure payment processing for fines

### Success Metrics
- **User Satisfaction**: <2 minute average checkout time
- **System Accuracy**: 99.99% inventory accuracy
- **Operational Efficiency**: 90% reduction in manual record keeping
- **User Engagement**: 30% increase in book circulation

### Constraints & Assumptions
- Integration with existing library card systems
- Physical barcode scanning infrastructure
- Limited IT budget for public library system
- Staff training requirements for new system

---

## 2. 📊 **CAPACITY PLANNING & SCALE ESTIMATION**

### Back-of-envelope Calculations

**User Activity:**
- 100K registered users, 10% active daily = 10K DAU
- Average 2 books checked out per active user = 20K checkouts/day
- Peak hours (5-8 PM): 50% of daily activity = 10K checkouts in 3 hours
- Peak checkout rate: ~1 checkout/second

**Data Storage:**
- User profiles: 100K users × 2KB = 200MB
- Book catalog: 1M titles × 5KB metadata = 5GB
- Physical copies: 5M copies × 1KB status = 5GB
- Transaction history: 20K checkouts/day × 365 days × 5 years × 2KB = 73GB
- Total primary storage: ~85GB (with indexes: ~200GB)

**Search Requirements:**
- Catalog searches: 50K/day = 0.6 searches/second
- Search index size: 1M books × 10KB = 10GB
- Cache hit ratio target: 80% for popular searches

**Notification Load:**
- Email notifications: 5K/day (overdue, availability)
- SMS notifications: 2K/day (urgent alerts)
- Push notifications: 8K/day (mobile app users)

### Peak Load Scenarios
- **Back-to-school period**: 3x normal checkout volume
- **New popular release**: 100+ waitlist sign-ups in first hour
- **System maintenance**: Batch processing during off-hours
- **Holiday periods**: Extended checkout periods affect return processing

---

## 3. 🏗️ **HIGH-LEVEL SYSTEM ARCHITECTURE**

```
                Library Management System Architecture
    
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Portal    │───▶│   CloudFront    │───▶│  Load Balancer  │
│  Mobile Apps    │    │     (CDN)       │    │    (ALB)        │
│ Librarian UI    │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                       ┌────────────────────────────────┼────────────────────────────────┐
                       ▼                                ▼                                ▼
           ┌─────────────────┐            ┌─────────────────┐            ┌─────────────────┐
           │  API Gateway    │            │  Auth Service   │            │  Rate Limiter   │
           │    (Kong)       │            │  (OAuth 2.0)    │            │    (Redis)      │
           └─────────────────┘            └─────────────────┘            └─────────────────┘
                       │
        ┌──────────────┼──────────────┬──────────────┬──────────────┬──────────────┐
        ▼              ▼              ▼              ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Catalog    │ │   Checkout   │ │   Waitlist   │ │   User Mgmt  │ │Notification  │ │   Reports    │
│   Service    │ │   Service    │ │   Service    │ │   Service    │ │   Service    │ │   Service    │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
        │              │              │              │              │              │
        ▼              ▼              ▼              ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ PostgreSQL   │ │ PostgreSQL   │ │    Redis     │ │ PostgreSQL   │ │     SQS      │ │ PostgreSQL   │
│ (Catalog)    │ │(Transactions)│ │ (Waitlists)  │ │   (Users)    │ │(Async Tasks) │ │ (Analytics)  │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘

                                         Supporting Infrastructure
                                              
                    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
                    │ Elasticsearch   │    │      S3         │    │    Lambda       │
                    │   (Search)      │    │ (Images/Docs)   │    │ (Notifications) │
                    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Component Responsibilities

**Catalog Service:**
- Book metadata management and search
- Availability tracking across branches
- Category and classification management
- Integration with external book databases (ISBN lookup)

**Checkout Service:**
- Transactional checkout/return processing
- Inventory locking and race condition prevention
- Due date calculation and renewal handling
- Fine calculation and payment integration

**Waitlist Service:**
- FIFO queue management for each book
- Automatic notification triggering
- Reservation timeout handling
- Priority queue for special user categories

**User Management Service:**
- Authentication and profile management
- Reading history and preferences
- Fine balance and payment history
- Librarian role and permission management

---

## 4. 🔧 **DETAILED COMPONENT DESIGN**

### 4.1 API Design

```yaml
# Book Catalog API
GET    /api/v1/books?query={search}&genre={category}&available=true
GET    /api/v1/books/{isbn}
GET    /api/v1/books/{isbn}/availability
POST   /api/v1/books/{isbn}/reserve

# Checkout Management API  
POST   /api/v1/checkouts
PUT    /api/v1/checkouts/{checkout_id}/return
GET    /api/v1/users/{user_id}/checkouts
POST   /api/v1/checkouts/{checkout_id}/renew

# Waitlist API
POST   /api/v1/books/{isbn}/waitlist
DELETE /api/v1/books/{isbn}/waitlist/{user_id}
GET    /api/v1/users/{user_id}/waitlists

# User Management API
GET    /api/v1/users/{user_id}/profile
GET    /api/v1/users/{user_id}/history
GET    /api/v1/users/{user_id}/fines
POST   /api/v1/users/{user_id}/fines/pay
```

### 4.2 Database Design

**Core Entity Schemas:**
```sql
-- Books catalog with physical copies
CREATE TABLE books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(200) NOT NULL,
    publisher VARCHAR(200),
    publication_year INT,
    genre VARCHAR(100),
    description TEXT,
    total_pages INT,
    language VARCHAR(50) DEFAULT 'English',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_books_isbn (isbn),
    INDEX idx_books_title (title),
    INDEX idx_books_author (author),
    FULLTEXT idx_books_search (title, author, description)
);

-- Physical book copies across branches
CREATE TABLE book_copies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    copy_number VARCHAR(20) NOT NULL,
    branch_id BIGINT NOT NULL,
    condition_status ENUM('excellent', 'good', 'fair', 'poor') DEFAULT 'good',
    availability_status ENUM('available', 'checked_out', 'reserved', 'maintenance') DEFAULT 'available',
    location_code VARCHAR(20), -- shelf location
    acquisition_date DATE,
    last_checkout TIMESTAMP,
    
    UNIQUE KEY uk_copy_branch (copy_number, branch_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    INDEX idx_copies_availability (book_id, availability_status),
    INDEX idx_copies_branch (branch_id)
);

-- Checkout transactions with concurrency control
CREATE TABLE checkouts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    copy_id BIGINT NOT NULL,
    librarian_id BIGINT NOT NULL,
    checkout_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP NULL,
    return_condition ENUM('excellent', 'good', 'fair', 'poor', 'damaged') NULL,
    fine_amount DECIMAL(6,2) DEFAULT 0.00,
    fine_paid BOOLEAN DEFAULT FALSE,
    renewal_count INT DEFAULT 0,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (copy_id) REFERENCES book_copies(id),
    INDEX idx_checkouts_user (user_id),
    INDEX idx_checkouts_due_date (due_date),
    INDEX idx_checkouts_active (return_date) -- NULL for active checkouts
);

-- Waitlist with position tracking
CREATE TABLE waitlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    position_number INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notified_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL, -- reservation expiry
    status ENUM('waiting', 'notified', 'expired', 'fulfilled') DEFAULT 'waiting',
    
    UNIQUE KEY uk_waitlist_user_book (user_id, book_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_waitlist_book_position (book_id, position_number),
    INDEX idx_waitlist_notifications (status, notified_at)
);
```

### 4.3 Concurrency Control & Race Condition Prevention

**Checkout Process with Locking:**
```conceptual
Atomic Checkout Flow:
1. BEGIN TRANSACTION
2. SELECT copy FOR UPDATE WHERE available 
3. Validate user eligibility (max books, fines)
4. UPDATE copy SET status='checked_out', user_id=X
5. INSERT checkout record
6. COMMIT TRANSACTION

If step 2 finds no available copies:
- Check waitlist for this book
- Add user to waitlist if not already present
- Return waitlist position
```

**Waitlist Management:**
```conceptual
FIFO Queue Implementation:
- Redis sorted set with timestamp scores
- Atomic operations for position management
- Background job processes notifications
- Reservation timeout cleanup

Key Operations:
ZADD waitlist:{book_id} {timestamp} {user_id}
ZRANGE waitlist:{book_id} 0 0  # Get next user
ZREM waitlist:{book_id} {user_id}  # Remove user
```

### 4.4 Search & Recommendation System

**Search Implementation:**
- Elasticsearch for full-text search across title, author, description
- Faceted search with filters (genre, availability, branch)
- Auto-complete suggestions with typo tolerance
- Search result ranking based on popularity and relevance

**Recommendation Engine:**
- Collaborative filtering based on checkout history
- Content-based recommendations using book metadata
- Popular books in user's preferred genres
- "Readers who enjoyed X also liked Y" patterns

---

## 5. ⚡ **ADVANCED SCALABILITY PATTERNS**

### 5.1 Caching Strategy

**Multi-Level Caching:**
```
L1: Application Cache (In-memory)
├── Popular book metadata: 1 hour TTL
├── User session data: 30 minutes TTL
└── Search results: 15 minutes TTL

L2: Redis Cluster
├── Book availability status: 5 minutes TTL
├── Waitlist positions: Real-time updates
├── User reading history: 24 hours TTL
└── Search autocomplete: 6 hours TTL

L3: Database Query Cache
├── Category browsing: 2 hours TTL
├── Branch information: 24 hours TTL
└── Static book data: 12 hours TTL
```

### 5.2 Read Replica Strategy

**Database Read Scaling:**
- Master database for all write operations
- Read replicas for search queries and reports
- Read/write split at application level
- Lag monitoring and fallback to master when needed

---

## 6. 🛡️ **RELIABILITY & FAULT TOLERANCE**

### 6.1 Data Consistency

**Transaction Boundaries:**
- Checkout operations use ACID transactions
- Waitlist updates with optimistic locking
- Eventual consistency acceptable for search indexes
- Compensation transactions for failed operations

**Backup & Recovery:**
- Automated daily database backups
- Point-in-time recovery capability
- Cross-region backup replication
- Regular disaster recovery testing

### 6.2 Error Handling

**Graceful Degradation:**
- Search falls back to basic queries if Elasticsearch down
- Checkout continues with simplified validation if recommendation service unavailable  
- Notifications queue for retry if email service temporary failure
- Manual librarian override for system failures

---

## 7. 🔒 **SECURITY & COMPLIANCE**

### 7.1 Data Protection

**User Privacy:**
- GDPR compliance for EU users
- Reading history encryption at rest
- Personal data anonymization options
- Data retention policies (7 years for financial records)

**Access Control:**
- Role-based permissions (Librarian, Admin, User)
- Multi-factor authentication for staff accounts
- API key management for external integrations
- Audit logging for all administrative actions

### 7.2 Payment Security

**Fine Payment Processing:**
- PCI DSS compliant payment handling
- Integration with secure payment processors
- Tokenized credit card storage
- Fraud detection for unusual payment patterns

---

## 8. 📊 **MONITORING & OBSERVABILITY**

### 8.1 Key Metrics

**Business Metrics:**
- Books checked out per day
- Average checkout duration
- Waitlist fulfillment time
- User engagement and retention rates

**Technical Metrics:**
- API response times (p95 < 500ms)
- Database connection pool utilization
- Cache hit ratios across all levels
- Background job processing delays

### 8.2 Alerting Strategy

**Critical Alerts:**
- Checkout transaction failures
- Database connectivity issues
- Payment processing errors
- Excessive API error rates (>1%)

**Monitoring Dashboard:**
- Real-time checkout activity
- Popular books and trends
- System performance metrics
- Staff workload distribution

---

## 9. ⚖️ **TRADE-OFFS ANALYSIS**

### 9.1 Consistency vs Performance

**Strong Consistency (Chosen for):**
- Book checkout transactions (prevent double-booking)
- Fine calculations and payments
- Librarian inventory management

**Eventual Consistency (Acceptable for):**
- Search index updates
- Reading recommendations
- Usage analytics and reporting

### 9.2 Real-time vs Batch Processing

**Real-time Processing:**
- Checkout/return transactions
- Waitlist notifications
- Availability status updates

**Batch Processing:**
- Overdue notifications (daily)
- Usage reports generation
- Recommendation model training

---

## 10. 🎨 **DESIGN PATTERNS & CONCEPTS**

### Applied Patterns

**Event-Driven Architecture:**
- Domain events for book status changes
- Asynchronous notification processing
- Loose coupling between services

**Saga Pattern:**
- Multi-step checkout process with compensation
- Inter-branch transfer workflows
- Payment processing with rollback capability

**Repository Pattern:**
- Abstracted data access layer
- Easy testing with mock repositories
- Database technology independence

---

## 11. 🛠️ **TECHNOLOGY STACK**

### Core Components
- **Backend**: Java Spring Boot (enterprise library requirements)
- **Database**: PostgreSQL (ACID compliance, complex queries)
- **Cache**: Redis Cluster (waitlist management, sessions)
- **Search**: Elasticsearch (book discovery, full-text search)
- **Message Queue**: AWS SQS (reliable notification delivery)

### Infrastructure
- **Cloud Provider**: AWS (government/public sector preference)
- **Container Platform**: ECS (simpler than Kubernetes for small team)
- **CI/CD**: GitHub Actions (cost-effective, simple workflows)
- **Monitoring**: CloudWatch + custom dashboards

---

## 12. 🤔 **FOLLOW-UP QUESTIONS & ANSWERS**

### Q1: How do you prevent double checkout of the same book copy?

**Answer:**
Implement pessimistic locking with database-level constraints:
1. Use `SELECT FOR UPDATE` to lock the book copy record during checkout
2. Verify availability within the transaction
3. Update status atomically before committing
4. Add unique constraint on (copy_id, return_date=NULL) to prevent database-level double booking
5. Application-level validation as additional safety layer

For high concurrency, consider optimistic locking with version numbers and retry logic.

### Q2: Design the waitlist notification system for fairness

**Answer:**
FIFO waitlist with automatic notification pipeline:
1. **Queue Structure**: Redis sorted set with join timestamp as score
2. **Position Tracking**: Atomic operations maintain accurate positions
3. **Notification Trigger**: Book return event triggers background job
4. **Reservation Window**: 72-hour hold period after notification
5. **Timeout Handling**: Automatic removal and next-user notification
6. **Fairness Guarantee**: Strict timestamp ordering prevents queue jumping

Special cases: Staff privileges, urgent academic needs handled via priority queues.

### Q3: Handle inter-branch book transfers efficiently

**Answer:**
Implement distributed inventory with transfer workflow:
1. **Transfer Request**: User requests book from different branch
2. **Availability Check**: Real-time query across all branch inventories
3. **Transfer Initiation**: Create transfer record with tracking
4. **Physical Movement**: 2-day delivery with status updates
5. **Arrival Processing**: Destination branch confirms receipt
6. **User Notification**: Automatic notification when available for pickup

Optimization: Predictive transfer based on user location and book popularity patterns.

### Q4: Design fine calculation system with complex rules

**Answer:**
Rule-based fine calculation engine:
1. **Base Rules**: $0.25/day for overdue books
2. **User Categories**: Different rates for students, adults, seniors
3. **Book Types**: Higher fines for new releases, reference materials
4. **Grace Periods**: 2-day grace for first-time users
5. **Caps**: Maximum fine per book ($10), per user ($50)
6. **Holidays**: No fines during library closure periods

Implementation: Strategy pattern for different fine calculation rules, with audit trail for all calculations.

### Q5: Implement mobile app synchronization for offline usage

**Answer:**
Offline-first mobile architecture:
1. **Local SQLite**: Cache user's checked-out books and history
2. **Sync Strategy**: Background sync when network available
3. **Conflict Resolution**: Server timestamp wins for conflicting updates
4. **Essential Offline Features**: View checked-out books, due dates, reading history
5. **Online-Only Features**: New checkouts, waitlist management, payments
6. **Data Validation**: Re-validate offline actions when coming online

Critical: Prevent offline checkout initiation to avoid inventory conflicts.

---

## 13. 🚀 **IMPLEMENTATION ROADMAP**

### Phase 1: Core Library Operations (0-4 months)
- User authentication and book catalog
- Basic checkout/return functionality  
- Simple waitlist management
- Librarian dashboard for inventory management

### Phase 2: Advanced Features (4-8 months)
- Full-text search with recommendations
- Mobile application development
- Fine calculation and payment processing
- Inter-branch transfer system

### Phase 3: Analytics & Optimization (8-12 months)
- Usage analytics and reporting
- Predictive inventory management
- Advanced recommendation algorithms
- Integration with external library systems

---

## 14. 🔄 **ALTERNATIVE APPROACHES**

### Approach 1: Microservices with Event Sourcing
**Pros**: Full audit trail, temporal queries, high scalability
**Cons**: Complex for small library system, over-engineering
**Use Case**: Large library consortium with complex requirements

### Approach 2: Monolithic Application with Modular Design
**Pros**: Simpler deployment, easier debugging, lower operational overhead
**Cons**: Scaling limitations, technology lock-in
**Use Case**: Single-branch libraries or small library systems

### Approach 3: SaaS Library Management Solution
**Pros**: Lower development cost, professional support, regular updates
**Cons**: Limited customization, recurring costs, vendor lock-in
**Use Case**: Libraries with limited IT resources and standard requirements

---

## 15. 📚 **LEARNING RESOURCES**

### Library Science & Management
- **"Library Systems & Services" by Charles R. McClure** - Library operations fundamentals
- **"Integrated Library Systems" by Marshall Breeding** - Technology solutions for libraries
- **Dublin Core Metadata Standards** - Book cataloging and classification
- **MARC Records Documentation** - Standard library data formats

### Inventory & Queue Management
- **"Queuing Theory Fundamentals" by William Stewart** - Mathematical foundations
- **"Inventory Management Principles" by Tony Wild** - Stock control strategies
- **Redis Documentation: Sorted Sets** - Efficient queue implementations
- **"Database Concurrency Control" by Bernstein & Newcomer** - Transaction management

### Public Sector Software Development
- **"Government Technology Architecture" by NIST** - Security and compliance guidelines
- **"Accessibility Guidelines (WCAG 2.1)"** - Public access requirements
- **"GDPR Implementation for Public Services"** - Data protection compliance
- **"Open Source in Government" by Red Hat** - Cost-effective technology choices

---

## 🎯 **Key Takeaways**

This library management system design balances simplicity with robustness, suitable for public library environments with limited IT resources but critical operational requirements.

**Critical Success Factors:**
1. **Concurrency Control**: Prevent double-checkout through database-level locking
2. **User Experience**: Fast search and simple checkout process for non-technical users
3. **Reliability**: Zero-downtime during library hours with graceful degradation
4. **Compliance**: Data protection and accessibility requirements for public services
5. **Cost Efficiency**: Open-source technologies with predictable operational costs

**Interview Performance Tips:**
- Emphasize practical constraints of public sector projects
- Discuss trade-offs between features and operational complexity
- Show understanding of librarian workflows and user needs
- Demonstrate knowledge of concurrency control in inventory systems