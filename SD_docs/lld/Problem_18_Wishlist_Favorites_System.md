# Deep Dive #18: Wishlist & Favorites System

## 1. Problem Clarification

Design a user wishlist/favorites system supporting multiple lists, real-time sync across devices, privacy controls, social sharing, price drop alerts, and stock availability notifications.

**Assumptions / Scope:**
- **Scale:** 500M users, 10M concurrent sessions, 5B wishlist items total
- **Features:** Create/manage multiple lists, add/remove items, reorder items, privacy settings (public/private/shared), collaborative lists, price tracking, stock alerts
- **Real-time:** Sync across user's devices (web, mobile) within 1 second
- **Social:** Share lists via link, follow other users' public lists, activity feed
- **Notifications:** Price drops, back-in-stock alerts, low-stock warnings
- **Performance:** Read latency p99 < 50ms, write < 100ms, eventual consistency acceptable
- **Out of scope:** Recommendation based on wishlist (v2), gift registry features, wish-list-to-cart conversion analytics (basic tracking only)

**Non-Functional Goals:**
- High availability (99.95%)
- Horizontal scalability (partition by user_id)
- Offline support (local-first with sync)
- Data privacy (GDPR compliant)
- Low network overhead (delta sync)

---

## 2. Core Requirements

| Category | Requirement |
|----------|-------------|
| **Functional** | • Create/update/delete wishlists<br>• Add/remove/reorder items within list<br>• Set privacy level (private, public, shared)<br>• Share list via link or email<br>• Collaborate on shared lists<br>• Real-time sync across devices<br>• Price drop alerts<br>• Stock availability notifications<br>• View activity feed (friends' wishlist updates)<br>• Follow/unfollow public lists |
| **Non-Functional** | • Scalability: 500M users, 5B items<br>• Performance: Read < 50ms, Write < 100ms<br>• Consistency: Eventual (multi-device sync)<br>• Availability: 99.95% uptime<br>• Real-time: < 1s sync latency<br>• Privacy: User-controlled sharing<br>• Observability: Sync lag, alert delivery rate |

---

## 3. Engineering Challenges

1. **Conflict Resolution:** Concurrent edits from multiple devices (CRDTs, operational transform, or last-write-wins)
2. **Real-time Sync:** Push updates to all user sessions (WebSocket, SSE, long-polling trade-offs)
3. **Privacy Enforcement:** Efficient access control checks on shared lists
4. **Price Tracking:** Poll product pricing service, detect changes, avoid notification spam
5. **Collaborative Editing:** Multiple users modifying same list simultaneously
6. **Offline Support:** Queue local changes, sync on reconnect, handle conflicts
7. **Data Sharding:** Partition wishlists by user_id while supporting cross-user queries (shared lists)
8. **Notification Scaling:** Millions of price/stock checks, fanout to interested users
9. **Reordering UX:** Maintain stable positions despite concurrent updates
10. **Access Link Security:** Revocable share links, expiration, permission levels

---

## 4. Design Patterns Applied

| Concern | Pattern | Justification |
|---------|---------|---------------|
| Multi-device sync | **Observer** | Notify all sessions on wishlist change |
| Conflict resolution | **Command** | Record operations for operational transform / CRDT |
| Privacy checks | **Specification** | Compose access rules (owner, shared, public) |
| Notification dispatch | **Strategy** | Different channels (email, push, in-app) |
| Price tracking | **Observer** | Subscribe to product price changes |
| Offline queue | **Command** | Queue local mutations, replay on sync |
| List operations | **Memento** | Snapshot list state for undo/redo |
| Access control | **Proxy** | Intercept list access, enforce permissions |
| Delta sync | **Adapter** | Translate between local and server models |
| Event propagation | **Mediator** | Centralize real-time message routing |

---

## 5. Domain Model

| Entity / Component | Responsibility |
|--------------------|----------------|
| **WishlistService** | Orchestrate CRUD, sync, notifications |
| **Wishlist (Entity)** | List metadata (name, owner, privacy, collaborators) |
| **WishlistItem (Entity)** | Product reference, position, added_at, notes |
| **PrivacyLevel (enum)** | PRIVATE, PUBLIC, SHARED_LINK, COLLABORATIVE |
| **WishlistMember (Entity)** | User permission on shared list (owner, editor, viewer) |
| **ShareLink (VO)** | Unique token, expiration, permission level |
| **OperationLog (Entity)** | Mutation record for sync (add, remove, reorder) |
| **PriceAlertSubscription (Entity)** | User, product, threshold price |
| **StockAlertSubscription (Entity)** | User, product, notify when available |
| **SyncService** | Coordinate real-time updates via WebSocket |
| **ConflictResolver (interface)** | Resolve concurrent edits (LWW, OT, CRDT) |
| **LastWriteWinsResolver** | Simple timestamp-based resolution |
| **CRDTResolver** | Conflict-free replicated data type approach |
| **NotificationService** | Dispatch alerts (price, stock) |
| **EventPublisher** | Emit domain events (ItemAdded, ListShared) |
| **AccessControlService** | Evaluate if user can access list |
| **ActivityFeedService** | Aggregate friend/followed list updates |

---

## 6. UML Class Diagram (ASCII)

```
┌──────────────────┐         ┌──────────────────┐
│ WishlistService  │────────>│ SyncService      │
│ -repo            │         │ +broadcast(event)│
│ -syncService     │         └──────────────────┘
│ -notificationSvc │
│ -accessControl   │         ┌──────────────────┐
└────────┬─────────┘         │ Wishlist (Entity)│
         │                   │ -id              │
         │                   │ -ownerId         │
         v                   │ -name            │
┌──────────────────┐         │ -privacy         │
│ Wishlist         │<────────│ -items[]         │
│ +addItem()       │         │ -members[]       │
│ +removeItem()    │         │ -shareLink       │
│ +reorder()       │         └────────┬─────────┘
└────────┬─────────┘                  │
         │                            │
         v                   ┌────────v─────────┐
┌──────────────────┐         │ WishlistItem     │
│ PrivacyLevel     │<<enum>> │ -productId       │
│ PRIVATE          │         │ -position        │
│ PUBLIC           │         │ -addedAt         │
│ SHARED_LINK      │         │ -notes           │
│ COLLABORATIVE    │         └──────────────────┘
└──────────────────┘
                             ┌──────────────────┐
┌──────────────────┐         │ WishlistMember   │
│ ConflictResolver │<<interface>>│ -userId      │
├──────────────────┤         │ -role            │
│ +resolve(ops)    │         │ -addedAt         │
└────────┬─────────┘         └──────────────────┘
         │
    ┌────┴─────┐
    │          │
┌───v────┐ ┌──v──────┐       ┌──────────────────┐
│LWW     │ │CRDT     │       │ OperationLog     │
│Resolver│ │Resolver │       │ -operation       │
└────────┘ └─────────┘       │ -timestamp       │
                             │ -deviceId        │
                             │ -vector_clock    │
                             └──────────────────┘

┌──────────────────┐         ┌──────────────────┐
│ PriceAlert       │         │ NotificationService│
│Subscription      │────────>│ +send(alert)     │
│ -productId       │         └──────────────────┘
│ -thresholdPrice  │
│ -userId          │
└──────────────────┘
```

---

## 7. Sequence Diagram (Add Item with Real-time Sync)

```
User(Device1)  WishlistService  Repository  SyncService  User(Device2)  NotificationSvc
     │               │              │            │              │              │
     │ addItem(list, │              │            │              │              │
     │ productId)    │              │            │              │              │
     ├──────────────>│              │            │              │              │
     │               │ save item    │            │              │              │
     │               ├─────────────>│            │              │              │
     │               │<─────────────┤            │              │              │
     │               │   saved      │            │              │              │
     │               │              │            │              │              │
     │               │ broadcast(   │            │              │              │
     │               │ ItemAdded)   │            │              │              │
     │               ├──────────────┼───────────>│              │              │
     │               │              │            │ push to      │              │
     │               │              │            │ device2      │              │
     │               │              │            ├─────────────>│              │
     │               │              │            │              │              │
     │               │ subscribe price alert     │              │              │
     │               ├──────────────┼────────────┼──────────────┼─────────────>│
     │               │              │            │              │              │
     │<──────────────┤              │            │              │              │
     │   ItemAdded   │              │            │              │              │
```

---

## 8. Implementation (Java-like Pseudocode)

### Core Interfaces

```java
// ========== INTERVIEW-CRITICAL: Command pattern for conflict-free sync ==========
interface WishlistOperation {
    OperationType getType(); // ADD, REMOVE, REORDER, UPDATE_NOTES
    String getOperationId(); // UUID for idempotency
    Instant getTimestamp();
    String getDeviceId();
    void apply(Wishlist wishlist);
}

class AddItemOperation implements WishlistOperation {
    private final String operationId;
    private final String productId;
    private final int position;
    private final Instant timestamp;
    private final String deviceId;
    
    @Override
    public void apply(Wishlist wishlist) {
        // Check if already applied (idempotent)
        if (wishlist.hasItem(productId)) {
            return;
        }
        WishlistItem item = new WishlistItem(productId, position, timestamp);
        wishlist.addItem(item);
    }
}

class RemoveItemOperation implements WishlistOperation {
    private final String operationId;
    private final String productId;
    private final Instant timestamp;
    private final String deviceId;
    
    @Override
    public void apply(Wishlist wishlist) {
        wishlist.removeItem(productId); // idempotent
    }
}

// ========== INTERVIEW-CRITICAL: Strategy pattern for conflict resolution ==========
interface ConflictResolver {
    List<WishlistOperation> resolve(List<WishlistOperation> local, 
                                     List<WishlistOperation> remote);
}

class LastWriteWinsResolver implements ConflictResolver {
    @Override
    public List<WishlistOperation> resolve(List<WishlistOperation> local, 
                                           List<WishlistOperation> remote) {
        // Merge operations, prefer later timestamp
        Map<String, WishlistOperation> merged = new HashMap<>();
        
        for (WishlistOperation op : local) {
            merged.put(op.getOperationId(), op);
        }
        
        for (WishlistOperation op : remote) {
            WishlistOperation existing = merged.get(op.getOperationId());
            if (existing == null || op.getTimestamp().isAfter(existing.getTimestamp())) {
                merged.put(op.getOperationId(), op);
            }
        }
        
        return new ArrayList<>(merged.values());
    }
}

class CRDTResolver implements ConflictResolver {
    @Override
    public List<WishlistOperation> resolve(List<WishlistOperation> local, 
                                           List<WishlistOperation> remote) {
        // Use vector clocks for happens-before relation
        // Remove wins over Add (deletion semantics)
        // Reorder operations ordered by timestamp + tie-breaker (device_id)
        
        Set<String> removedProducts = new HashSet<>();
        List<WishlistOperation> resolved = new ArrayList<>();
        
        // Collect all removes
        Stream.concat(local.stream(), remote.stream())
            .filter(op -> op.getType() == OperationType.REMOVE)
            .forEach(op -> removedProducts.add(((RemoveItemOperation) op).getProductId()));
        
        // Apply adds that haven't been removed
        Stream.concat(local.stream(), remote.stream())
            .filter(op -> op.getType() == OperationType.ADD)
            .map(op -> (AddItemOperation) op)
            .filter(op -> !removedProducts.contains(op.getProductId()))
            .forEach(resolved::add);
        
        // Add all removes
        Stream.concat(local.stream(), remote.stream())
            .filter(op -> op.getType() == OperationType.REMOVE)
            .forEach(resolved::add);
        
        return resolved;
    }
}
```

### Wishlist Service

```java
// ========== INTERVIEW-CRITICAL: Facade with real-time sync orchestration ==========
@Service
class WishlistService {
    private final WishlistRepository wishlistRepo;
    private final OperationLogRepository opLogRepo;
    private final SyncService syncService;
    private final ConflictResolver conflictResolver;
    private final AccessControlService accessControl;
    private final NotificationService notificationService;
    private final EventPublisher eventPublisher;
    
    public Wishlist addItem(String wishlistId, String userId, String productId, String deviceId) {
        // Step 1: Load wishlist & check permissions
        Wishlist wishlist = wishlistRepo.findById(wishlistId)
            .orElseThrow(() -> new WishlistNotFoundException(wishlistId));
        
        accessControl.requirePermission(wishlist, userId, Permission.EDIT);
        
        // Step 2: Create operation
        AddItemOperation op = AddItemOperation.builder()
            .operationId(UUID.randomUUID().toString())
            .productId(productId)
            .position(wishlist.getItems().size()) // append
            .timestamp(Instant.now())
            .deviceId(deviceId)
            .build();
        
        // Step 3: Apply operation
        op.apply(wishlist);
        
        // Step 4: Persist
        wishlistRepo.save(wishlist);
        opLogRepo.save(op);
        
        // Step 5: Broadcast to all user sessions (real-time sync)
        syncService.broadcast(userId, new ItemAddedEvent(wishlistId, productId, op));
        
        // Step 6: Subscribe to price alerts if configured
        subscribeToPriceAlerts(userId, productId);
        
        // Step 7: Publish event
        eventPublisher.publish(new WishlistItemAddedEvent(wishlist, productId));
        
        return wishlist;
    }
    
    public SyncResult sync(String wishlistId, String userId, String deviceId, 
                           List<WishlistOperation> localOps, long lastSyncVersion) {
        // Step 1: Check permissions
        Wishlist wishlist = wishlistRepo.findById(wishlistId)
            .orElseThrow(() -> new WishlistNotFoundException(wishlistId));
        
        accessControl.requirePermission(wishlist, userId, Permission.VIEW);
        
        // Step 2: Fetch remote operations since last sync
        List<WishlistOperation> remoteOps = opLogRepo.findByWishlistAndVersionGreaterThan(
            wishlistId, lastSyncVersion
        );
        
        // Step 3: Resolve conflicts
        List<WishlistOperation> resolved = conflictResolver.resolve(localOps, remoteOps);
        
        // Step 4: Apply resolved operations
        Wishlist synced = wishlist.copy();
        for (WishlistOperation op : resolved) {
            op.apply(synced);
        }
        
        // Step 5: Persist if local operations exist
        if (!localOps.isEmpty()) {
            wishlistRepo.save(synced);
            opLogRepo.saveAll(localOps);
        }
        
        // Step 6: Return delta
        return SyncResult.builder()
            .wishlist(synced)
            .operations(remoteOps)
            .currentVersion(wishlist.getVersion())
            .conflictsResolved(!localOps.isEmpty() && !remoteOps.isEmpty())
            .build();
    }
    
    public Wishlist shareViaLink(String wishlistId, String userId, Permission permission, 
                                 Duration expiration) {
        Wishlist wishlist = wishlistRepo.findById(wishlistId)
            .orElseThrow(() -> new WishlistNotFoundException(wishlistId));
        
        accessControl.requirePermission(wishlist, userId, Permission.OWNER);
        
        // Generate unique share token
        String token = generateSecureToken();
        ShareLink shareLink = ShareLink.builder()
            .token(token)
            .permission(permission)
            .expiresAt(Instant.now().plus(expiration))
            .createdBy(userId)
            .build();
        
        wishlist.setShareLink(shareLink);
        wishlistRepo.save(wishlist);
        
        eventPublisher.publish(new WishlistSharedEvent(wishlist, shareLink));
        
        return wishlist;
    }
    
    public void addCollaborator(String wishlistId, String ownerId, String collaboratorId, 
                                WishlistRole role) {
        Wishlist wishlist = wishlistRepo.findById(wishlistId)
            .orElseThrow(() -> new WishlistNotFoundException(wishlistId));
        
        accessControl.requirePermission(wishlist, ownerId, Permission.OWNER);
        
        WishlistMember member = WishlistMember.builder()
            .userId(collaboratorId)
            .role(role)
            .addedAt(Instant.now())
            .addedBy(ownerId)
            .build();
        
        wishlist.addMember(member);
        wishlistRepo.save(wishlist);
        
        // Notify collaborator
        notificationService.send(collaboratorId, new CollaborationInviteNotification(wishlist));
    }
}
```

### Real-time Sync Service

```java
// ========== INTERVIEW-CRITICAL: Observer pattern for multi-device sync ==========
@Service
class SyncService {
    private final ConcurrentMap<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    
    @OnWebSocketConnect
    public void onConnect(String userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("User {} connected from device {}", userId, session.getId());
    }
    
    @OnWebSocketDisconnect
    public void onDisconnect(String userId, WebSocketSession session) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }
    
    public void broadcast(String userId, DomainEvent event) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return; // User offline, will sync on reconnect
        }
        
        String message = serializeEvent(event);
        sessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("Failed to send message to session {}", session.getId(), e);
            }
        });
    }
    
    public void broadcastToCollaborators(Wishlist wishlist, DomainEvent event) {
        wishlist.getMembers().stream()
            .map(WishlistMember::getUserId)
            .forEach(userId -> broadcast(userId, event));
    }
}
```

### Access Control

```java
// ========== INTERVIEW-CRITICAL: Specification pattern for flexible access rules ==========
@Service
class AccessControlService {
    public void requirePermission(Wishlist wishlist, String userId, Permission required) {
        if (!hasPermission(wishlist, userId, required)) {
            throw new AccessDeniedException("User " + userId + " lacks permission " + required);
        }
    }
    
    public boolean hasPermission(Wishlist wishlist, String userId, Permission required) {
        // Owner has all permissions
        if (wishlist.getOwnerId().equals(userId)) {
            return true;
        }
        
        // Check member permissions
        Optional<WishlistMember> member = wishlist.getMembers().stream()
            .filter(m -> m.getUserId().equals(userId))
            .findFirst();
        
        if (member.isPresent()) {
            return hasRole(member.get().getRole(), required);
        }
        
        // Check public access
        if (wishlist.getPrivacy() == PrivacyLevel.PUBLIC) {
            return required == Permission.VIEW;
        }
        
        return false;
    }
    
    public boolean canAccessViaShareLink(Wishlist wishlist, String token) {
        ShareLink shareLink = wishlist.getShareLink();
        if (shareLink == null || !shareLink.getToken().equals(token)) {
            return false;
        }
        
        // Check expiration
        if (shareLink.getExpiresAt() != null && 
            shareLink.getExpiresAt().isBefore(Instant.now())) {
            return false;
        }
        
        return true;
    }
    
    private boolean hasRole(WishlistRole role, Permission permission) {
        return switch (permission) {
            case VIEW -> true; // All roles can view
            case EDIT -> role == WishlistRole.EDITOR || role == WishlistRole.OWNER;
            case OWNER -> role == WishlistRole.OWNER;
        };
    }
}
```

### Price Alert System

```java
// ========== INTERVIEW-CRITICAL: Observer pattern for price tracking ==========
@Service
class PriceAlertService {
    private final PriceAlertRepository alertRepo;
    private final ProductPricingService pricingService;
    private final NotificationService notificationService;
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkPriceDrops() {
        List<PriceAlertSubscription> alerts = alertRepo.findActiveAlerts();
        
        // Batch fetch current prices
        Set<String> productIds = alerts.stream()
            .map(PriceAlertSubscription::getProductId)
            .collect(Collectors.toSet());
        
        Map<String, BigDecimal> currentPrices = pricingService.getPrices(productIds);
        
        // Check each alert
        for (PriceAlertSubscription alert : alerts) {
            BigDecimal currentPrice = currentPrices.get(alert.getProductId());
            if (currentPrice == null) continue;
            
            if (currentPrice.compareTo(alert.getThresholdPrice()) <= 0) {
                // Price drop detected
                notificationService.send(
                    alert.getUserId(),
                    new PriceDropNotification(alert.getProductId(), currentPrice)
                );
                
                // Update last notified to avoid spam
                alert.setLastNotifiedAt(Instant.now());
                alertRepo.save(alert);
            }
        }
    }
    
    public void subscribe(String userId, String productId, BigDecimal thresholdPrice) {
        // Check if already subscribed
        if (alertRepo.existsByUserAndProduct(userId, productId)) {
            return; // Idempotent
        }
        
        PriceAlertSubscription alert = PriceAlertSubscription.builder()
            .userId(userId)
            .productId(productId)
            .thresholdPrice(thresholdPrice)
            .createdAt(Instant.now())
            .active(true)
            .build();
        
        alertRepo.save(alert);
    }
}
```

### Database Schema

```sql
-- ========== INTERVIEW-CRITICAL: Sharding by user_id for scalability ==========
CREATE TABLE wishlists (
    id VARCHAR(36) PRIMARY KEY,
    owner_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    privacy VARCHAR(20) NOT NULL, -- PRIVATE, PUBLIC, SHARED_LINK, COLLABORATIVE
    share_token VARCHAR(64),
    share_expires_at TIMESTAMP,
    share_permission VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    
    INDEX idx_owner (owner_id),
    INDEX idx_privacy (privacy),
    UNIQUE INDEX idx_share_token (share_token)
);

CREATE TABLE wishlist_items (
    id VARCHAR(36) PRIMARY KEY,
    wishlist_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    position INT NOT NULL,
    notes TEXT,
    added_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
    INDEX idx_wishlist (wishlist_id),
    INDEX idx_product (product_id),
    UNIQUE INDEX idx_wishlist_product (wishlist_id, product_id)
);

CREATE TABLE wishlist_members (
    wishlist_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL, -- OWNER, EDITOR, VIEWER
    added_at TIMESTAMP NOT NULL,
    added_by VARCHAR(36),
    
    PRIMARY KEY (wishlist_id, user_id),
    FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
    INDEX idx_user (user_id)
);

CREATE TABLE wishlist_operation_log (
    id VARCHAR(36) PRIMARY KEY,
    wishlist_id VARCHAR(36) NOT NULL,
    operation_type VARCHAR(20) NOT NULL, -- ADD, REMOVE, REORDER, UPDATE_NOTES
    product_id VARCHAR(36),
    position INT,
    timestamp TIMESTAMP NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    version BIGINT NOT NULL,
    
    FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
    INDEX idx_wishlist_version (wishlist_id, version),
    INDEX idx_timestamp (timestamp)
);

CREATE TABLE price_alert_subscriptions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    threshold_price DECIMAL(10,2) NOT NULL,
    last_notified_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    
    INDEX idx_user_product (user_id, product_id),
    INDEX idx_product (product_id),
    INDEX idx_active (active, last_notified_at)
);
```

---

## 9. Thread Safety Analysis

**Concurrency Model:**
- **Read-heavy:** 95% reads (view wishlist), 5% writes (add/remove items)
- **Conflict resolution:** CRDTs or Last-Write-Wins based on use case
- **Optimistic locking:** @Version on Wishlist entity for concurrent updates
- **Operation log:** Append-only (no conflicts), ordered by version
- **Real-time sync:** Lock-free broadcast via concurrent map of sessions

**Critical Sections:**
1. **Add item:** Check duplicate + insert (unique constraint handles race)
2. **Remove item:** Idempotent delete
3. **Reorder:** Apply operations in timestamp order (deterministic)
4. **Sync:** Fetch remote ops + resolve + apply atomically

**Race Conditions Prevented:**
- **Double-add:** Unique constraint (wishlist_id, product_id)
- **Concurrent reorder:** Operations applied in deterministic order (timestamp + device_id tie-breaker)
- **Stale reads:** Acceptable (eventual consistency)

---

## 10. Top 10 Interview Q&A

**Q1: How do you handle offline edits and sync conflicts?**
**A:** Queue operations locally (IndexedDB/local storage). On reconnect, send operations to server. Server resolves conflicts using CRDT (removal wins) or LWW. Operations have unique IDs for idempotency. Sync protocol: send last_sync_version, receive delta since then.

**Q2: How would you scale to 500M users?**
**A:** Shard by user_id (consistent hashing). Wishlists stored in user's shard. Shared lists: denormalize membership to all shards (eventual consistency). Cache hot wishlists (Redis). Use CDN for static content. Separate read replicas for analytics.

**Q3: How do you implement real-time sync across devices?**
**A:** WebSocket per device session. On mutation, broadcast event to all user's sessions via concurrent map lookup. Message format: {type: "ITEM_ADDED", wishlist_id, product_id, operation}. Client applies operation locally. Fallback: polling every 30s if WebSocket unavailable.

**Q4: How would you handle a user with 10,000 items in a wishlist?**
**A:** Pagination: fetch 50 items per page. Index on (wishlist_id, position). Virtual scrolling on client. Denormalize item count on wishlist table. Background job to warn users > 1000 items (UX issue). Consider list size limits (5000 items).

**Q5: How do you prevent abuse of shared links?**
**A:** Rate limit access per token (100 views/hour). Expiration (default 30 days). Revocable (owner can regenerate token). Permission levels (view-only vs edit). Track access logs. CAPTCHA for public links with high traffic.

**Q6: How would you implement price drop alerts at scale?**
**A:** Batch job every 5 minutes: fetch all active alerts, group by product_id. Query pricing service (batch API). Compare current vs threshold. Send notifications (batch enqueue to SQS/Kafka). Throttle: max 1 alert per product per 24 hours per user. Unsubscribe after 90 days of no price drop.

**Q7: How do you handle collaborative editing conflicts?**
**A:** Operational Transform (OT) or CRDTs. Simple approach: Add wins (merge), Remove wins (tombstone). Reorder: apply all operations in deterministic order (timestamp, device_id). Display "User X added Y" notifications to collaborators. Show last edited by/when for conflict awareness.

**Q8: How would you implement activity feeds (friends' wishlist updates)?**
**A:** Follow relationship table. On wishlist update, fanout event to followers (write amplification). Store in followers' timelines (Redis sorted set, score=timestamp). Pull: fetch top 50 events from timeline. Push: real-time notification via WebSocket. Privacy: filter by wishlist.privacy.

**Q9: How do you handle GDPR data deletion?**
**A:** Soft delete: anonymize user_id in wishlists/items. Hard delete option: cascade delete wishlists, items, operation logs, alerts. Batch job to purge after retention period (30 days). Audit log of deletions. Notify collaborators of shared list deletions.

**Q10: How would you optimize for low network overhead?**
**A:** Delta sync: send only operations since last_sync_version (not full list). Compress JSON (gzip). Batch operations (debounce 500ms). Use WebSocket binary frames (protobuf). Cache full wishlist locally, apply operations incrementally. Offline-first: sync in background.

---

## 11. Extension Points

**Immediate Extensions:**
1. **Gift registry:** Mark items as purchased, hide from recipient
2. **Price history:** Track price over time, show graph
3. **Related products:** Suggest similar items to add
4. **Wishlist templates:** Create from popular lists (wedding, baby, etc.)
5. **Import/export:** Support CSV, JSON export

**Advanced Features:**
1. **Recommendations:** ML-based suggestions from wishlist patterns
2. **Social features:** Like, comment on public lists
3. **Conversion tracking:** Wishlist-to-cart analytics
4. **Smart grouping:** Auto-categorize items (electronics, clothing)
5. **Merge duplicates:** Detect same product across lists

**Operational Improvements:**
1. **Analytics:** Wishlist size distribution, popular products, conversion funnel
2. **A/B testing:** Test notification frequency, UI layouts
3. **Anomaly detection:** Flag suspicious activity (rapid adds, scraping)
4. **Performance monitoring:** Sync lag, operation log growth, alert delivery rate
5. **Cost optimization:** Archive old operation logs (S3), compress media

---

## 12. Testing Strategy

**Unit Tests:**
- **ConflictResolver:** Various conflict scenarios (concurrent add/remove, reorder)
- **AccessControl:** Permission checks for owner, editor, viewer, public
- **Operation.apply():** Idempotency, state transitions
- **ShareLink:** Expiration, revocation

**Integration Tests:**
- **End-to-end sync:** Add item on device1, verify appears on device2
- **Collaborative editing:** Two users modifying same list
- **Price alerts:** Mock price change, verify notification sent
- **Share link access:** Valid/expired/invalid tokens

**Performance Tests:**
- **Sync latency:** Measure device-to-device propagation time
- **Large lists:** 10K items, pagination performance
- **Concurrent users:** 100 users editing shared list
- **Price alert scale:** 1M subscriptions, batch job duration

**Edge Cases:**
- **Offline queue:** 100+ operations, sync on reconnect
- **Duplicate adds:** Same item from multiple devices simultaneously
- **Expired share link:** Access denied
- **Deleted product:** Wishlist item with missing product
- **Collaborator removes item while owner editing**

**Property-Based Tests:**
- **Eventual consistency:** All devices converge to same state
- **Idempotency:** Replaying operations yields same result
- **Commutativity:** Reorder operations independently applied
- **CRDT invariants:** Remove > Add (tombstone semantics)

---

## 13. Pitfalls & Anti-Patterns Avoided

| Anti-Pattern | How Avoided |
|--------------|-------------|
| **Full list sync on every change** | Delta sync with operation log, send only changes since last version |
| **Unbounded operation log growth** | Compact log periodically (snapshot + truncate old ops) |
| **Lost updates from concurrent edits** | Optimistic locking + conflict resolution (CRDT/LWW) |
| **Notification spam** | Throttle alerts (1 per product per 24h), batch notifications |
| **No offline support** | Queue operations locally, sync on reconnect |
| **Stale WebSocket sessions** | Heartbeat ping/pong, reconnect on timeout |
| **Missing access control** | Specification pattern for permission checks, enforced at service layer |
| **No revocation for share links** | Token regeneration invalidates old links |
| **Unbounded wishlist size** | Pagination, soft limit warnings, hard limits (5000 items) |
| **No audit trail** | Operation log tracks all mutations with user/device/timestamp |

---

## 14. Complexity Analysis

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| **Add item** | O(1) | O(1) | Insert + broadcast |
| **Remove item** | O(1) | O(1) | Delete + broadcast |
| **Reorder** | O(1) | O(1) | Update position |
| **Sync (delta)** | O(K) | O(K) | K operations since last sync |
| **Full list fetch** | O(N) | O(N) | N items in list, use pagination |
| **Check access** | O(M) | O(1) | M members in list (typically small) |
| **Price alert batch** | O(P × A) | O(P) | P products, A alerts per product |
| **Conflict resolution** | O(K log K) | O(K) | Sort K operations by timestamp |
| **Broadcast to sessions** | O(S) | O(S) | S active sessions for user |

**Optimizations:**
- **Operation log compaction:** Snapshot list state, delete old operations
- **Cache hot wishlists:** Redis with 5-minute TTL
- **Batch price checks:** Group alerts by product_id, single API call
- **Denormalize member count:** Fast access control checks

---

## 15. Interview Evaluation Rubric

**Requirements Clarification (20%):**
- [ ] Identified multi-device sync, offline support, real-time updates
- [ ] Clarified scale (500M users, 5B items)
- [ ] Discussed conflict resolution strategies
- [ ] Asked about privacy controls and sharing

**System Design (30%):**
- [ ] Designed operation log for sync (Command pattern)
- [ ] Proposed conflict resolution (CRDT/LWW)
- [ ] Real-time sync architecture (WebSocket, Observer)
- [ ] Access control with share links (Specification, Proxy)
- [ ] Price alert subscription system

**Code Quality (25%):**
- [ ] Clean domain model (Wishlist, WishlistItem, OperationLog)
- [ ] Proper use of patterns (Command, Observer, Strategy, Specification)
- [ ] Idempotent operations
- [ ] Thread-safe concurrent structures
- [ ] Privacy enforcement

**Scalability & Performance (15%):**
- [ ] Analyzed time complexity (O(1) for core ops, O(K) for sync)
- [ ] Sharding strategy (by user_id)
- [ ] Delta sync optimization (bandwidth reduction)
- [ ] Pagination for large lists
- [ ] Batch processing for alerts

**Edge Cases & Testing (10%):**
- [ ] Offline edits, sync conflicts
- [ ] Expired share links, revocation
- [ ] Concurrent collaborative edits
- [ ] Large list handling (10K+ items)
- [ ] Comprehensive test strategy
