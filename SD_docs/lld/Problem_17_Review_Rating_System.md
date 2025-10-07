# Deep Dive #17: Review & Rating System

## 1. Problem Clarification

Design a scalable review and rating system supporting text reviews, star ratings, upvote/downvote helpfulness, media attachments, moderation workflow, and aggregated metrics for products/services.

**Assumptions / Scope:**
- **Scale:** 100M reviews, 1M new reviews/day, 10K reads/sec
- **Features:** Star rating (1-5), text review (optional), photos/videos, verified purchase badge, helpful votes, report abuse, moderation queue, seller responses
- **Aggregation:** Average rating, rating distribution histogram, filtered stats (verified only, time-based)
- **Display:** Paginated reviews with sort (most helpful, newest, highest/lowest rating), filter (verified, rating, keyword)
- **Moderation:** Auto-flagging (profanity, spam patterns), manual review queue, approval/rejection workflow
- **Performance:** Write latency < 200ms, read latency p99 < 100ms, eventual consistency acceptable for aggregates
- **Out of scope:** ML-based fake review detection (v2), sentiment analysis, image recognition for media

**Non-Functional Goals:**
- High availability (99.9%)
- Partition tolerance (AP system, eventual consistency)
- Audit trail for moderation actions
- Rate limiting to prevent spam
- GDPR compliance (user data deletion)

---

## 2. Core Requirements

| Category | Requirement |
|----------|-------------|
| **Functional** | • Submit review (rating + text + media)<br>• Mark review as helpful/not helpful<br>• Report review for abuse<br>• Seller response to reviews<br>• Moderation workflow (auto-flag, manual review)<br>• Aggregate ratings (average, histogram)<br>• Filter & sort reviews<br>• Edit/delete own reviews<br>• Verified purchase badge |
| **Non-Functional** | • Scalability: 100M reviews, 1M writes/day<br>• Performance: Write < 200ms, Read p99 < 100ms<br>• Consistency: Eventual for aggregates, strong for writes<br>• Availability: 99.9% uptime<br>• Durability: Reviews persisted reliably<br>• Observability: Metrics on review velocity, moderation queue depth |

---

## 3. Engineering Challenges

1. **Aggregate Computation:** Real-time vs batch update of average rating (consistency vs performance)
2. **Helpful Vote Scaling:** Handle 10K+ votes per popular review without hotspots
3. **Spam Prevention:** Rate limiting, duplicate detection, bot mitigation
4. **Moderation Efficiency:** Auto-flagging rules, prioritize high-traffic products
5. **Media Storage:** Handle images/videos (CDN, encoding, thumbnails)
6. **Pagination Consistency:** Stable ordering despite new reviews arriving
7. **Filtering Performance:** Efficiently query by verified status, rating, date range
8. **Write Amplification:** Update product aggregate on every review (optimization needed)
9. **Concurrent Votes:** Prevent double-voting, handle race conditions on vote count
10. **Data Privacy:** Support user data deletion (GDPR right to erasure)

---

## 4. Design Patterns Applied

| Concern | Pattern | Justification |
|---------|---------|---------------|
| Review submission validation | **Template Method** | Standard flow with extension points for custom validation |
| Moderation rules | **Chain of Responsibility** | Apply multiple filters (profanity, spam, length) sequentially |
| Aggregation strategy | **Strategy** | Swap between real-time, micro-batch, batch aggregation |
| Review lifecycle | **State** | Draft → Pending → Approved → Rejected transitions |
| Event notification | **Observer** | Notify on review submission (email, analytics, search index) |
| Media handling | **Adapter** | Unify S3, Cloudinary, local storage behind interface |
| Vote counting | **Command** | Encapsulate vote action for undo/redo, idempotency |
| Aggregate computation | **Saga** | Orchestrate review save → aggregate update → index update |
| Cache invalidation | **Proxy** | Transparent caching layer around review service |
| Access control | **Decorator** | Add authorization checks around core service methods |

---

## 5. Domain Model

| Entity / Component | Responsibility |
|--------------------|----------------|
| **ReviewService** | Orchestrate review submission, retrieval, aggregation |
| **Review (Entity)** | Text, rating, author, product, timestamps, status, media URLs |
| **Rating (VO)** | Star value (1-5) with validation |
| **HelpfulVote (Entity)** | User ID, review ID, vote type (helpful/not) |
| **ModerationRule (interface)** | Evaluate if review needs manual review |
| **ProfanityFilter** | Detect banned words, offensive language |
| **SpamDetector** | Identify duplicate content, bot patterns |
| **ReviewState (enum)** | DRAFT, PENDING, APPROVED, REJECTED, DELETED |
| **ModerationAction (VO)** | Moderator ID, action type, reason, timestamp |
| **ProductRatingAggregate (Entity)** | Product ID, avg rating, count, histogram, last updated |
| **AggregationStrategy (interface)** | Compute aggregate stats from reviews |
| **RealTimeAggregator** | Update on every review write (strong consistency) |
| **BatchAggregator** | Periodic recomputation (eventual consistency) |
| **MediaStorageAdapter (interface)** | Upload/retrieve media assets |
| **EventPublisher** | Emit domain events (ReviewSubmitted, ReviewApproved) |
| **ReviewRepository** | Persist reviews with indexing on product, user, status |
| **VoteRepository** | Track helpful votes with unique constraint (user, review) |
| **RateLimiter** | Prevent review spam per user/IP |

---

## 6. UML Class Diagram (ASCII)

```
┌──────────────────┐         ┌──────────────────┐
│ ReviewService    │────────>│ ModerationRule   │<<interface>>
│ -repo            │         │ +evaluate(review)│
│ -aggregator      │         └────────┬─────────┘
│ -mediator        │                  │
│ -ratelimiter     │         ┌────────┴─────────┐
└────────┬─────────┘         │                  │
         │              ┌────v──────┐    ┌──────v────┐
         │              │Profanity  │    │SpamDetector│
         │              │Filter     │    │           │
         │              └───────────┘    └───────────┘
         │
         v
┌──────────────────┐         ┌──────────────────┐
│ Review (Entity)  │────────>│ ReviewState      │<<enum>>
│ -id              │         │ DRAFT, PENDING,  │
│ -productId       │         │ APPROVED, etc    │
│ -userId          │         └──────────────────┘
│ -rating          │
│ -text            │         ┌──────────────────┐
│ -mediaUrls       │────────>│ MediaStorage     │<<interface>>
│ -helpfulCount    │         │ +upload(file)    │
│ -state           │         └──────────────────┘
│ -verifiedPurchase│
└────────┬─────────┘
         │
         v
┌──────────────────┐         ┌──────────────────┐
│ HelpfulVote      │         │ AggregationStrategy│<<interface>>
│ -userId          │         │ +compute(reviews)│
│ -reviewId        │         └────────┬─────────┘
│ -voteType        │                  │
└──────────────────┘         ┌────────┴─────────┐
                             │                  │
                      ┌──────v───────┐  ┌───────v──────┐
                      │RealTime      │  │Batch         │
                      │Aggregator    │  │Aggregator    │
                      └──────────────┘  └──────────────┘
                             │
                             v
                      ┌──────────────────┐
                      │ProductRating     │
                      │Aggregate (Entity)│
                      │ -avgRating       │
                      │ -reviewCount     │
                      │ -histogram[5]    │
                      └──────────────────┘
```

---

## 7. Sequence Diagram (Submit Review)

```
User        ReviewService    RateLimiter    ModerationRule    Repository    EventPublisher    Aggregator
  │               │               │                │              │                │              │
  │ submitReview  │               │                │              │                │              │
  ├──────────────>│               │                │              │                │              │
  │               │ checkLimit    │                │              │                │              │
  │               ├──────────────>│                │              │                │              │
  │               │<──────────────┤                │              │                │              │
  │               │   allowed     │                │              │                │              │
  │               │               │                │              │                │              │
  │               │ validate & evaluate            │              │                │              │
  │               ├────────────────┼───────────────>│              │                │              │
  │               │                │   needsReview? │              │                │              │
  │               │<───────────────┼────────────────┤              │                │              │
  │               │                │   true/false   │              │                │              │
  │               │                │                │              │                │              │
  │               │ save(review, state=PENDING/APPROVED)           │                │              │
  │               ├────────────────┼────────────────┼─────────────>│                │              │
  │               │                │                │   saved      │                │              │
  │               │<───────────────┼────────────────┼──────────────┤                │              │
  │               │                │                │              │                │              │
  │               │ publish(ReviewSubmitted)        │              │                │              │
  │               ├────────────────┼────────────────┼──────────────┼───────────────>│              │
  │               │                │                │              │                │              │
  │               │ if APPROVED: updateAggregate(productId)        │                │              │
  │               ├────────────────┼────────────────┼──────────────┼────────────────┼─────────────>│
  │               │                │                │              │                │   updated    │
  │               │<───────────────┼────────────────┼──────────────┼────────────────┼──────────────┤
  │               │                │                │              │                │              │
  │<──────────────┤                │                │              │                │              │
  │  Review       │                │                │              │                │              │
```

---

## 8. Implementation (Java-like Pseudocode)

### Core Interfaces

```java
// ========== INTERVIEW-CRITICAL: Chain of Responsibility for moderation ==========
interface ModerationRule {
    ModerationResult evaluate(Review review);
    ModerationRule setNext(ModerationRule next);
}

class ModerationResult {
    private final boolean needsManualReview;
    private final List<String> reasons;
    
    public static ModerationResult approved() {
        return new ModerationResult(false, List.of());
    }
    
    public static ModerationResult flagged(String... reasons) {
        return new ModerationResult(true, List.of(reasons));
    }
}

class ProfanityFilter implements ModerationRule {
    private ModerationRule next;
    private final Set<String> bannedWords;
    
    @Override
    public ModerationResult evaluate(Review review) {
        String text = review.getText().toLowerCase();
        for (String banned : bannedWords) {
            if (text.contains(banned)) {
                return ModerationResult.flagged("Contains profanity: " + banned);
            }
        }
        return next != null ? next.evaluate(review) : ModerationResult.approved();
    }
    
    @Override
    public ModerationRule setNext(ModerationRule next) {
        this.next = next;
        return next;
    }
}

class SpamDetector implements ModerationRule {
    private ModerationRule next;
    private final ReviewRepository repo;
    
    @Override
    public ModerationResult evaluate(Review review) {
        // Check for duplicate content from same user
        List<Review> recent = repo.findByUserSince(
            review.getUserId(), 
            Instant.now().minus(Duration.ofHours(1))
        );
        
        for (Review r : recent) {
            double similarity = calculateSimilarity(review.getText(), r.getText());
            if (similarity > 0.9) {
                return ModerationResult.flagged("Duplicate content detected");
            }
        }
        
        // Check for excessive caps
        long capsCount = review.getText().chars().filter(Character::isUpperCase).count();
        if (capsCount > review.getText().length() * 0.5) {
            return ModerationResult.flagged("Excessive capital letters");
        }
        
        return next != null ? next.evaluate(review) : ModerationResult.approved();
    }
}

// ========== INTERVIEW-CRITICAL: Strategy pattern for aggregation ==========
interface AggregationStrategy {
    ProductRatingAggregate compute(String productId);
}

class RealTimeAggregator implements AggregationStrategy {
    private final ReviewRepository reviewRepo;
    private final AggregateRepository aggregateRepo;
    
    @Override
    public ProductRatingAggregate compute(String productId) {
        List<Review> approved = reviewRepo.findByProductAndStatus(
            productId, 
            ReviewState.APPROVED
        );
        
        double avgRating = approved.stream()
            .mapToDouble(r -> r.getRating().getValue())
            .average()
            .orElse(0.0);
        
        int[] histogram = new int[5];
        for (Review r : approved) {
            histogram[r.getRating().getValue() - 1]++;
        }
        
        return ProductRatingAggregate.builder()
            .productId(productId)
            .averageRating(avgRating)
            .reviewCount(approved.size())
            .ratingHistogram(histogram)
            .lastUpdated(Instant.now())
            .build();
    }
}

class BatchAggregator implements AggregationStrategy {
    private final ReviewRepository reviewRepo;
    
    @Override
    public ProductRatingAggregate compute(String productId) {
        // Use pre-computed aggregates updated by batch job
        // Query aggregate table directly (eventually consistent)
        return aggregateRepo.findByProductId(productId)
            .orElse(ProductRatingAggregate.empty(productId));
    }
}
```

### Review Service

```java
// ========== INTERVIEW-CRITICAL: Template Method for review submission flow ==========
@Service
class ReviewService {
    private final ReviewRepository reviewRepo;
    private final VoteRepository voteRepo;
    private final AggregationStrategy aggregator;
    private final ModerationRule moderationChain;
    private final MediaStorageAdapter mediaStorage;
    private final EventPublisher eventPublisher;
    private final RateLimiter rateLimiter;
    
    public Review submitReview(ReviewSubmission submission) {
        // Step 1: Rate limiting
        if (!rateLimiter.allowRequest(submission.getUserId(), "review", 5, Duration.ofHours(24))) {
            throw new RateLimitExceededException("Maximum 5 reviews per 24 hours");
        }
        
        // Step 2: Validation
        validateSubmission(submission);
        
        // Step 3: Upload media
        List<String> mediaUrls = uploadMedia(submission.getMediaFiles());
        
        // Step 4: Create review entity
        Review review = Review.builder()
            .id(UUID.randomUUID().toString())
            .productId(submission.getProductId())
            .userId(submission.getUserId())
            .rating(new Rating(submission.getRating()))
            .text(submission.getText())
            .mediaUrls(mediaUrls)
            .verifiedPurchase(checkVerifiedPurchase(submission.getUserId(), submission.getProductId()))
            .helpfulCount(0)
            .notHelpfulCount(0)
            .createdAt(Instant.now())
            .build();
        
        // Step 5: Moderation
        ModerationResult modResult = moderationChain.evaluate(review);
        review.setState(modResult.needsManualReview() ? ReviewState.PENDING : ReviewState.APPROVED);
        if (modResult.needsManualReview()) {
            review.setModerationReasons(modResult.getReasons());
        }
        
        // Step 6: Persist
        reviewRepo.save(review);
        
        // Step 7: Update aggregate if approved
        if (review.getState() == ReviewState.APPROVED) {
            updateProductAggregate(review.getProductId());
        }
        
        // Step 8: Publish event
        eventPublisher.publish(new ReviewSubmittedEvent(review));
        
        return review;
    }
    
    // ========== INTERVIEW-CRITICAL: Command pattern for vote with idempotency ==========
    public void voteHelpful(String reviewId, String userId, VoteType voteType) {
        // Idempotent: check if vote exists
        Optional<HelpfulVote> existing = voteRepo.findByReviewAndUser(reviewId, userId);
        
        if (existing.isPresent()) {
            HelpfulVote vote = existing.get();
            if (vote.getVoteType() == voteType) {
                return; // Already voted this way
            }
            // Change vote: decrement old, increment new
            updateVoteCount(reviewId, vote.getVoteType(), -1);
            updateVoteCount(reviewId, voteType, 1);
            vote.setVoteType(voteType);
            voteRepo.save(vote);
        } else {
            // New vote
            HelpfulVote vote = new HelpfulVote(reviewId, userId, voteType);
            voteRepo.save(vote);
            updateVoteCount(reviewId, voteType, 1);
        }
    }
    
    @Transactional
    private void updateVoteCount(String reviewId, VoteType voteType, int delta) {
        Review review = reviewRepo.findByIdForUpdate(reviewId) // SELECT FOR UPDATE
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        
        if (voteType == VoteType.HELPFUL) {
            review.setHelpfulCount(review.getHelpfulCount() + delta);
        } else {
            review.setNotHelpfulCount(review.getNotHelpfulCount() + delta);
        }
        
        reviewRepo.save(review);
    }
    
    public PagedResult<Review> getReviews(String productId, ReviewFilter filter, Sort sort, int page, int size) {
        // Step 1: Apply filters
        Specification<Review> spec = buildSpecification(productId, filter);
        
        // Step 2: Apply sort
        PageRequest pageRequest = PageRequest.of(page, size, convertSort(sort));
        
        // Step 3: Query with pagination
        Page<Review> results = reviewRepo.findAll(spec, pageRequest);
        
        return PagedResult.of(results.getContent(), results.getTotalElements(), page, size);
    }
    
    public ProductRatingAggregate getProductRating(String productId) {
        return aggregator.compute(productId);
    }
    
    private void updateProductAggregate(String productId) {
        ProductRatingAggregate aggregate = aggregator.compute(productId);
        aggregateRepo.save(aggregate);
        eventPublisher.publish(new ProductRatingUpdatedEvent(productId, aggregate));
    }
}
```

### Review Entity

```java
// ========== INTERVIEW-CRITICAL: State pattern for review lifecycle ==========
@Entity
class Review {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String productId;
    
    @Column(nullable = false)
    private String userId;
    
    @Embedded
    private Rating rating;
    
    @Column(length = 5000)
    private String text;
    
    @ElementCollection
    private List<String> mediaUrls;
    
    @Enumerated(EnumType.STRING)
    private ReviewState state;
    
    private boolean verifiedPurchase;
    
    private int helpfulCount;
    private int notHelpfulCount;
    
    @ElementCollection
    private List<String> moderationReasons;
    
    @Embedded
    private ModerationAction lastModerationAction;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    @Version
    private Long version; // Optimistic locking
    
    // State transition methods
    public void approve(String moderatorId, String reason) {
        if (state != ReviewState.PENDING) {
            throw new IllegalStateException("Can only approve pending reviews");
        }
        this.state = ReviewState.APPROVED;
        this.lastModerationAction = new ModerationAction(moderatorId, "APPROVE", reason);
    }
    
    public void reject(String moderatorId, String reason) {
        if (state != ReviewState.PENDING) {
            throw new IllegalStateException("Can only reject pending reviews");
        }
        this.state = ReviewState.REJECTED;
        this.lastModerationAction = new ModerationAction(moderatorId, "REJECT", reason);
    }
}

@Embeddable
class Rating {
    @Column(nullable = false)
    private int value; // 1-5
    
    public Rating(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.value = value;
    }
}

enum ReviewState {
    DRAFT,      // Not yet submitted
    PENDING,    // Awaiting moderation
    APPROVED,   // Visible to public
    REJECTED,   // Rejected by moderator
    DELETED     // Soft deleted by user/admin
}
```

### Database Schema

```sql
-- ========== INTERVIEW-CRITICAL: Indexing strategy for performance ==========
CREATE TABLE reviews (
    id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    rating_value INT NOT NULL CHECK (rating_value BETWEEN 1 AND 5),
    text TEXT,
    state VARCHAR(20) NOT NULL,
    verified_purchase BOOLEAN NOT NULL DEFAULT FALSE,
    helpful_count INT NOT NULL DEFAULT 0,
    not_helpful_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    
    INDEX idx_product_state (product_id, state),
    INDEX idx_product_rating (product_id, rating_value),
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_state_created (state, created_at) -- for moderation queue
);

CREATE TABLE helpful_votes (
    review_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    vote_type VARCHAR(10) NOT NULL, -- HELPFUL, NOT_HELPFUL
    created_at TIMESTAMP NOT NULL,
    
    PRIMARY KEY (review_id, user_id),
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE
);

CREATE TABLE product_rating_aggregates (
    product_id VARCHAR(36) PRIMARY KEY,
    average_rating DECIMAL(3,2) NOT NULL,
    review_count INT NOT NULL,
    rating_histogram_1 INT NOT NULL, -- count of 1-star
    rating_histogram_2 INT NOT NULL,
    rating_histogram_3 INT NOT NULL,
    rating_histogram_4 INT NOT NULL,
    rating_histogram_5 INT NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE review_media (
    id VARCHAR(36) PRIMARY KEY,
    review_id VARCHAR(36) NOT NULL,
    media_url VARCHAR(500) NOT NULL,
    media_type VARCHAR(20) NOT NULL, -- IMAGE, VIDEO
    thumbnail_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE
);
```

---

## 9. Thread Safety Analysis

**Concurrency Model:**
- **Write conflicts:** Optimistic locking (@Version) on Review entity
- **Vote counting:** Pessimistic lock (SELECT FOR UPDATE) to prevent lost updates
- **Aggregate computation:** Lock-free reads (eventual consistency acceptable)
- **Rate limiting:** Atomic counters in Redis (INCR command)
- **Duplicate detection:** Unique constraint (review_id, user_id) in votes table

**Critical Sections:**
1. **Vote update:** SELECT FOR UPDATE on review row ensures atomic increment
2. **Aggregate recomputation:** Can run concurrently (last-write-wins acceptable)
3. **Moderation state transition:** Optimistic locking prevents conflicting approvals/rejections

**Race Conditions Prevented:**
- **Double voting:** Unique constraint + upsert logic
- **Concurrent helpful count updates:** Pessimistic lock serializes updates
- **Stale aggregate reads:** Accept eventual consistency (5-minute lag acceptable)

---

## 10. Top 10 Interview Q&A

**Q1: How do you prevent users from submitting duplicate reviews?**
**A:** Unique constraint on (user_id, product_id). At application level, check before submission. For spam, use Bloom filter for fast negative lookup. Track review fingerprint (hash of text) to detect slight variations.

**Q2: How do you scale helpful vote counting to millions of votes?**
**A:** Micro-batch updates: buffer votes in Redis, flush to DB every 10 seconds. Use optimistic locking with retry. For reads, cache vote counts (5-minute TTL). Hot reviews: shard by review_id to distribute load.

**Q3: How would you compute real-time aggregates without recalculating on every review?**
**A:** Incremental updates: old_avg = (old_sum / old_count), new_sum = old_sum + new_rating, new_avg = new_sum / (old_count + 1). Store sum + count. Update histogram array by incrementing appropriate index. Use atomic operations (Redis HINCRBY).

**Q4: How do you handle fake reviews at scale?**
**A:** Multi-layered approach: 1) Verified purchase badge (trusted source), 2) Review velocity anomaly detection (sudden spike), 3) Text similarity clustering (coordinated attacks), 4) User behavior patterns (new accounts, burst posting), 5) ML model scoring (requires labeled data).

**Q5: How would you implement pagination with stable ordering?**
**A:** Cursor-based pagination: ORDER BY helpful_count DESC, created_at DESC, id ASC. Return cursor (last helpful_count, created_at, id). Next page: WHERE (helpful_count < cursor_helpful OR (helpful_count = cursor_helpful AND created_at < cursor_created) OR ...). Index on (helpful_count, created_at, id).

**Q6: How do you moderate reviews efficiently at scale?**
**A:** Priority queue: flag high-traffic products first. Auto-approval rules: verified + high user reputation + clean history. Batch moderation UI (50 reviews/page). ML pre-scoring to rank by spam likelihood. Crowdsource flagging (user reports trigger review).

**Q7: How would you handle seller responses to reviews?**
**A:** One-to-one relationship: review → seller_response. Separate table with review_id FK. Display inline with review. Notify review author. Allow single response (or versioning). Index on review_id for fast lookup.

**Q8: How do you ensure GDPR compliance for user data deletion?**
**A:** Soft delete: mark review as DELETED, anonymize user_id (replace with ANON_<hash>), keep text for aggregate stats. Hard delete option: cascade delete votes, responses, media. Batch job to purge after retention period. Audit log of deletions.

**Q9: How would you implement review filtering (verified, rating, keyword)?**
**A:** JPA Specification pattern or query builder. Combine filters with AND logic. Index strategy: composite index on (product_id, verified_purchase, rating_value). Full-text search: Elasticsearch for keyword filtering. Cache popular filter combinations.

**Q10: How do you prevent vote manipulation (vote brigading)?**
**A:** Rate limiting: 10 votes/hour per user. Velocity checks: flag reviews with sudden vote surge. Graph analysis: detect coordinated voting rings (same IPs, temporal clustering). Weighted votes: verified purchasers count more. Exponential decay: recent votes weighted higher.

---

## 11. Extension Points

**Immediate Extensions:**
1. **Seller responses:** One response per review, notify review author
2. **Media moderation:** Image recognition for inappropriate content (Rekognition)
3. **Review editing:** Allow edit within 24 hours, track edit history
4. **Sentiment analysis:** Auto-tag reviews as positive/negative/neutral
5. **Review questions:** Q&A section per product, separate from reviews

**Advanced Features:**
1. **Personalized ranking:** Show most relevant reviews to user (collaborative filtering)
2. **Review summarization:** AI-generated summary of common themes
3. **Verified reviewer badge:** Multiple purchases, long account history
4. **Review incentives:** Reward high-quality reviews (reputation points)
5. **Comparative reviews:** "Compared to product X..." feature

**Operational Improvements:**
1. **A/B testing:** Test moderation rules, ranking algorithms
2. **Anomaly detection:** Auto-flag review velocity spikes, coordinated attacks
3. **Metrics dashboard:** Review submission rate, approval rate, avg helpful votes
4. **Audit trail:** Track all moderation actions for compliance
5. **Federated moderation:** Community moderators with limited permissions

---

## 12. Testing Strategy

**Unit Tests:**
- **ModerationRule:** Profanity detection, spam patterns, boundary cases
- **AggregationStrategy:** Average calculation, histogram correctness, edge cases (0 reviews)
- **Rating validation:** Invalid values (0, 6, negative)
- **Vote logic:** Idempotency, vote change, count updates

**Integration Tests:**
- **Review submission:** End-to-end flow with moderation, aggregation, events
- **Concurrent votes:** Multiple users voting simultaneously
- **Pagination:** Stable ordering, cursor correctness
- **Filtering:** Verified, rating, date range combinations

**Performance Tests:**
- **Write throughput:** 1M reviews/day sustained
- **Read latency:** p99 < 100ms for paginated queries
- **Vote scaling:** 10K votes/sec on hot review
- **Aggregate computation:** < 1 second for 10K reviews

**Edge Cases:**
- **Duplicate submissions:** Same user, product, text
- **Concurrent approvals:** Two moderators approving simultaneously
- **Vote flood:** Single user voting rapidly
- **Empty aggregates:** Product with 0 reviews
- **Media upload failures:** Retry, rollback review

**Property-Based Tests:**
- **Aggregate consistency:** Sum of histogram == review count
- **Vote count invariant:** helpful + not_helpful == unique voters
- **Average monotonicity:** Adding rating ≤ avg decreases avg
- **Pagination completeness:** Union of all pages == full result set

---

## 13. Pitfalls & Anti-Patterns Avoided

| Anti-Pattern | How Avoided |
|--------------|-------------|
| **Recomputing aggregates from scratch** | Incremental updates, batch jobs for eventually consistent reads |
| **Lost vote updates** | Pessimistic locking (SELECT FOR UPDATE) on vote count updates |
| **Unbounded moderation queue** | Auto-approval rules, priority sorting, SLA alerts |
| **No spam prevention** | Rate limiting, duplicate detection, moderation chain |
| **Stale cache after updates** | Event-driven invalidation, TTL expiration |
| **Inconsistent pagination** | Cursor-based with tie-breaker (id) for deterministic ordering |
| **No audit trail** | ModerationAction embedded in Review, separate audit log table |
| **Hardcoded moderation rules** | Chain of Responsibility allows runtime rule composition |
| **Missing verified badge** | Check purchase history, mark at review creation |
| **No GDPR compliance** | Soft delete with anonymization, data retention policies |

---

## 14. Complexity Analysis

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| **Submit review** | O(M + R) | O(1) | M moderation rules, R media uploads |
| **Vote on review** | O(1) | O(1) | Single row update with lock |
| **Get reviews (paginated)** | O(log N + K) | O(K) | Index seek + fetch K rows |
| **Compute aggregate (batch)** | O(N) | O(1) | Scan all N approved reviews for product |
| **Compute aggregate (incremental)** | O(1) | O(1) | Update running sum/count |
| **Filter reviews** | O(log N + K) | O(K) | Index on filter columns |
| **Moderation queue** | O(log N) | O(K) | Index on (state, created_at) |
| **Delete review** | O(1) | O(1) | Soft delete, mark state=DELETED |
| **Search by keyword** | O(log N + K) | O(K) | Full-text index (Elasticsearch) |

**Optimizations:**
- **Vote buffering:** Batch updates in Redis, flush periodically
- **Aggregate caching:** Cache product rating for 5 minutes
- **Covering index:** (product_id, state, rating_value, created_at) for filtered queries
- **Denormalization:** Store review count, avg rating on product table for fast access

---

## 15. Interview Evaluation Rubric

**Requirements Clarification (20%):**
- [ ] Identified key features (ratings, votes, moderation, aggregates)
- [ ] Clarified scale (100M reviews, 1M/day writes)
- [ ] Discussed consistency trade-offs (eventual for aggregates)
- [ ] Asked about moderation workflow and spam prevention

**System Design (30%):**
- [ ] Designed review entity with proper state lifecycle
- [ ] Proposed moderation chain (Chain of Responsibility)
- [ ] Chose aggregation strategy (real-time vs batch)
- [ ] Handled concurrent vote updates (locking strategy)
- [ ] Media storage abstraction

**Code Quality (25%):**
- [ ] Clean domain model (Review, Vote, Aggregate entities)
- [ ] Proper use of patterns (Chain, Strategy, State, Command)
- [ ] Thread-safe vote counting (SELECT FOR UPDATE)
- [ ] Idempotent vote operations
- [ ] Validation and error handling

**Scalability & Performance (15%):**
- [ ] Analyzed time complexity (O(log N) for indexed queries)
- [ ] Identified bottlenecks (aggregate computation, vote hotspots)
- [ ] Proposed sharding strategy (by product_id for reviews, review_id for votes)
- [ ] Caching strategy (aggregates, popular reviews)
- [ ] Batch processing for aggregates

**Edge Cases & Testing (10%):**
- [ ] Handled duplicate reviews, concurrent votes
- [ ] GDPR compliance (data deletion)
- [ ] Spam prevention (rate limiting, similarity detection)
- [ ] Pagination consistency (cursor-based)
- [ ] Comprehensive test strategy
