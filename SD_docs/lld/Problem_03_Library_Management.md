# Problem 3: Library Management System (Repository + Domain Events + Fine Calculation)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a library management system that handles book cataloging, lending, returns, holds/reservations, and fine calculations with support for multiple branches and member types.

**Assumptions / Scope:**
- Support multiple library branches
- Multiple copies of same book across branches
- Member types: Regular, Premium, Student (different lending limits)
- Item types: Books, Magazines, DVDs (different lending periods)
- Hold/reservation system with priority queue
- Automated fine calculation for overdue items
- Scale: 100K books, 50K members, 10K active loans
- Out of scope: Inter-library loans, digital content, staff management

**Non-Functional Goals:**
- Real-time availability updates
- Sub-200ms search response time
- Consistent state across branches
- Automated overdue notifications
- Audit trail for all transactions

### 2. Core Requirements

**Functional:**
- Catalog management (add/update/remove items)
- Search by title, author, ISBN, category
- Check out and return items
- Hold/reserve items when unavailable
- Calculate and collect fines for overdue items
- Renew items (if no holds pending)
- Track item history (who borrowed when)
- Member registration and profile management
- Generate reports (popular books, overdue list)

**Non-Functional:**
- **Consistency**: No double-checkout of same copy
- **Performance**: Search < 200ms, checkout < 500ms
- **Scalability**: Handle 1000 concurrent operations
- **Reliability**: 99.9% uptime
- **Observability**: Track lending patterns, popular items

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Preventing Double Checkout**
- **Problem**: Race condition when multiple members try to check out last available copy
- **Solution**: Optimistic locking with version control
- **Algorithm**:
```java
/**
 * Atomic checkout with version-based optimistic locking
 */
class CheckoutService {
    
    @Transactional
    public CheckoutResult checkoutItem(UUID memberId, UUID itemCopyId) {
        // Lock the item copy row
        ItemCopy copy = itemCopyRepo.findByIdWithLock(itemCopyId)
            .orElseThrow(() -> new ItemCopyNotFoundException(itemCopyId));
        
        // Check availability
        if (copy.getStatus() != ItemStatus.AVAILABLE) {
            return CheckoutResult.failure(
                CheckoutFailureReason.ITEM_NOT_AVAILABLE,
                "Item is " + copy.getStatus()
            );
        }
        
        // Check member eligibility
        Member member = memberRepo.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));
        
        if (!member.canCheckout()) {
            return CheckoutResult.failure(
                CheckoutFailureReason.MEMBER_INELIGIBLE,
                "Member has unpaid fines or max items checked out"
            );
        }
        
        // Calculate due date based on item type and member type
        LocalDate dueDate = calculateDueDate(copy.getItem(), member);
        
        // Create loan record
        Loan loan = Loan.builder()
            .id(UUID.randomUUID())
            .memberId(memberId)
            .itemCopyId(itemCopyId)
            .branchId(copy.getBranchId())
            .checkoutDate(LocalDate.now())
            .dueDate(dueDate)
            .status(LoanStatus.ACTIVE)
            .build();
        
        loanRepo.save(loan);
        
        // Update item status
        copy.setStatus(ItemStatus.CHECKED_OUT);
        copy.setVersion(copy.getVersion() + 1);
        itemCopyRepo.save(copy);
        
        // Update member stats
        member.incrementActiveLoans();
        memberRepo.save(member);
        
        // Publish event
        eventPublisher.publish(new ItemCheckedOutEvent(loan.getId(), memberId, itemCopyId));
        
        return CheckoutResult.success(loan);
    }
    
    /**
     * Calculate due date based on item type and member privileges
     */
    private LocalDate calculateDueDate(Item item, Member member) {
        int baseDays = switch (item.getType()) {
            case BOOK -> 14; // 2 weeks
            case MAGAZINE -> 7; // 1 week
            case DVD -> 3; // 3 days
        };
        
        // Premium members get extended periods
        if (member.getType() == MemberType.PREMIUM) {
            baseDays = (int) (baseDays * 1.5);
        }
        
        return LocalDate.now().plusDays(baseDays);
    }
}
```

**Challenge 2: Hold/Reservation Priority Queue**
- **Problem**: When item returned, who gets it next? FIFO or priority-based?
- **Solution**: Priority queue with configurable strategy
- **Algorithm**:
```java
/**
 * Hold management with priority queue
 */
class HoldService {
    
    @Transactional
    public HoldResult placeHold(UUID memberId, UUID itemId) {
        Member member = memberRepo.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));
        
        Item item = itemRepo.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException(itemId));
        
        // Check if member already has active hold
        if (holdRepo.existsActiveHold(memberId, itemId)) {
            return HoldResult.failure("Member already has hold on this item");
        }
        
        // Check if item is available now
        Optional<ItemCopy> availableCopy = itemCopyRepo.findAvailableCopy(itemId);
        if (availableCopy.isPresent()) {
            return HoldResult.failure("Item is available for immediate checkout");
        }
        
        // Calculate priority
        int priority = calculateHoldPriority(member, item);
        
        // Create hold
        Hold hold = Hold.builder()
            .id(UUID.randomUUID())
            .memberId(memberId)
            .itemId(itemId)
            .placedDate(LocalDate.now())
            .expiresDate(LocalDate.now().plusDays(7)) // Hold valid for 7 days
            .priority(priority)
            .status(HoldStatus.PENDING)
            .build();
        
        holdRepo.save(hold);
        
        eventPublisher.publish(new HoldPlacedEvent(hold.getId(), memberId, itemId));
        
        return HoldResult.success(hold);
    }
    
    /**
     * INTERVIEW CRITICAL: Process return and assign to next hold
     */
    @Transactional
    public void processReturn(UUID loanId) {
        Loan loan = loanRepo.findById(loanId)
            .orElseThrow(() -> new LoanNotFoundException(loanId));
        
        ItemCopy copy = itemCopyRepo.findById(loan.getItemCopyId())
            .orElseThrow(() -> new ItemCopyNotFoundException(loan.getItemCopyId()));
        
        // Check for pending holds
        Optional<Hold> nextHold = holdRepo.findNextPendingHold(copy.getItemId());
        
        if (nextHold.isPresent()) {
            Hold hold = nextHold.get();
            
            // Reserve for hold
            copy.setStatus(ItemStatus.ON_HOLD);
            copy.setReservedForMemberId(hold.getMemberId());
            copy.setReservationExpiresAt(LocalDate.now().plusDays(2)); // 2 days to pick up
            
            // Update hold status
            hold.setStatus(HoldStatus.READY_FOR_PICKUP);
            hold.setNotifiedDate(LocalDate.now());
            holdRepo.save(hold);
            
            // Notify member
            notificationService.notifyHoldReady(hold);
            
        } else {
            // No holds, make available
            copy.setStatus(ItemStatus.AVAILABLE);
            copy.setReservedForMemberId(null);
        }
        
        itemCopyRepo.save(copy);
        
        // Update loan
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        loanRepo.save(loan);
        
        eventPublisher.publish(new ItemReturnedEvent(loan.getId(), copy.getId()));
    }
    
    /**
     * Calculate hold priority (higher = more urgent)
     */
    private int calculateHoldPriority(Member member, Item item) {
        int priority = 0;
        
        // Premium members get higher priority
        if (member.getType() == MemberType.PREMIUM) {
            priority += 100;
        }
        
        // Students get slight boost for academic items
        if (member.getType() == MemberType.STUDENT && 
            item.getCategory().isAcademic()) {
            priority += 50;
        }
        
        // Long-standing members get slight boost
        int membershipYears = member.getMembershipYears();
        priority += Math.min(membershipYears * 5, 50); // Max 50 points
        
        return priority;
    }
}
```

**Challenge 3: Fine Calculation & Auto-Processing**
- **Problem**: Calculate fines for overdue items with grace periods and caps
- **Solution**: Background job with flexible fine calculation policy
- **Algorithm**:
```java
/**
 * Fine calculation with configurable policies
 */
class FineCalculationService {
    
    private static final BigDecimal BOOK_FINE_PER_DAY = new BigDecimal("0.50");
    private static final BigDecimal DVD_FINE_PER_DAY = new BigDecimal("2.00");
    private static final BigDecimal MAX_FINE_PER_ITEM = new BigDecimal("50.00");
    private static final int GRACE_PERIOD_DAYS = 2;
    
    /**
     * INTERVIEW CRITICAL: Calculate fine for overdue loan
     */
    public BigDecimal calculateFine(Loan loan, LocalDate returnDate) {
        if (returnDate.isBefore(loan.getDueDate()) || 
            returnDate.equals(loan.getDueDate())) {
            return BigDecimal.ZERO;
        }
        
        // Calculate overdue days (excluding grace period)
        long totalOverdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
        long chargeableDays = Math.max(0, totalOverdueDays - GRACE_PERIOD_DAYS);
        
        if (chargeableDays == 0) {
            return BigDecimal.ZERO;
        }
        
        // Get item type to determine fine rate
        ItemCopy copy = itemCopyRepo.findById(loan.getItemCopyId())
            .orElseThrow();
        Item item = itemRepo.findById(copy.getItemId())
            .orElseThrow();
        
        BigDecimal finePerDay = switch (item.getType()) {
            case BOOK -> BOOK_FINE_PER_DAY;
            case MAGAZINE -> BOOK_FINE_PER_DAY;
            case DVD -> DVD_FINE_PER_DAY;
        };
        
        // Calculate fine
        BigDecimal calculatedFine = finePerDay.multiply(
            new BigDecimal(chargeableDays)
        );
        
        // Apply cap
        BigDecimal finalFine = calculatedFine.min(MAX_FINE_PER_ITEM);
        
        return finalFine;
    }
    
    /**
     * INTERVIEW CRITICAL: Background job to process overdue loans
     */
    @Scheduled(cron = "0 0 2 * * *") // Run daily at 2 AM
    @Transactional
    public void processOverdueLoans() {
        LocalDate today = LocalDate.now();
        
        // Find all active overdue loans
        List<Loan> overdueLoans = loanRepo.findOverdueLoans(today);
        
        for (Loan loan : overdueLoans) {
            try {
                // Calculate current fine
                BigDecimal fine = calculateFine(loan, today);
                
                if (fine.compareTo(BigDecimal.ZERO) > 0) {
                    // Create or update fine record
                    Fine existingFine = fineRepo.findByLoanId(loan.getId())
                        .orElse(null);
                    
                    if (existingFine == null) {
                        Fine newFine = Fine.builder()
                            .id(UUID.randomUUID())
                            .loanId(loan.getId())
                            .memberId(loan.getMemberId())
                            .amount(fine)
                            .status(FineStatus.UNPAID)
                            .createdDate(today)
                            .build();
                        
                        fineRepo.save(newFine);
                        
                        // Notify member
                        notificationService.notifyOverdue(loan, fine);
                        
                    } else {
                        // Update existing fine
                        existingFine.setAmount(fine);
                        existingFine.setLastUpdated(today);
                        fineRepo.save(existingFine);
                    }
                }
                
                // Check if severely overdue (> 30 days)
                long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                if (overdueDays > 30) {
                    markItemAsLost(loan);
                }
                
            } catch (Exception e) {
                // Log error but continue processing others
                logger.error("Failed to process overdue loan: {}", loan.getId(), e);
            }
        }
    }
    
    private void markItemAsLost(Loan loan) {
        ItemCopy copy = itemCopyRepo.findById(loan.getItemCopyId())
            .orElse(null);
        
        if (copy != null) {
            copy.setStatus(ItemStatus.LOST);
            itemCopyRepo.save(copy);
            
            // Create replacement cost fine
            Item item = itemRepo.findById(copy.getItemId()).orElse(null);
            if (item != null) {
                Fine replacementFine = Fine.builder()
                    .id(UUID.randomUUID())
                    .loanId(loan.getId())
                    .memberId(loan.getMemberId())
                    .amount(item.getReplacementCost())
                    .status(FineStatus.UNPAID)
                    .type(FineType.REPLACEMENT)
                    .createdDate(LocalDate.now())
                    .build();
                
                fineRepo.save(replacementFine);
                
                notificationService.notifyItemLost(loan, item.getReplacementCost());
            }
        }
    }
}
```

**Challenge 4: Search with Multiple Criteria**
- **Problem**: Efficient search across title, author, ISBN with relevance ranking
- **Solution**: Inverted index with composite scoring
- **Algorithm**:
```java
/**
 * Search service with relevance ranking
 */
class LibrarySearchService {
    
    /**
     * INTERVIEW CRITICAL: Search with multiple criteria and ranking
     */
    public SearchResult search(SearchQuery query) {
        List<Item> results = new ArrayList<>();
        
        // Build query based on criteria
        if (query.hasIsbn()) {
            // ISBN is unique, direct lookup
            Optional<Item> item = itemRepo.findByIsbn(query.getIsbn());
            return SearchResult.of(item.map(List::of).orElse(List.of()));
        }
        
        // Full-text search with scoring
        List<ScoredItem> scoredItems = performFullTextSearch(query);
        
        // Apply filters
        List<ScoredItem> filtered = applyFilters(scoredItems, query);
        
        // Sort by relevance score
        filtered.sort(Comparator.comparingDouble(ScoredItem::getScore).reversed());
        
        // Extract items
        results = filtered.stream()
            .map(ScoredItem::getItem)
            .limit(query.getLimit())
            .collect(Collectors.toList());
        
        return SearchResult.of(results);
    }
    
    private List<ScoredItem> performFullTextSearch(SearchQuery query) {
        List<ScoredItem> scored = new ArrayList<>();
        
        // Search by title
        if (query.hasTitle()) {
            List<Item> titleMatches = itemRepo.searchByTitle(query.getTitle());
            for (Item item : titleMatches) {
                double score = calculateTitleScore(item.getTitle(), query.getTitle());
                scored.add(new ScoredItem(item, score * 2.0)); // Title weighted 2x
            }
        }
        
        // Search by author
        if (query.hasAuthor()) {
            List<Item> authorMatches = itemRepo.searchByAuthor(query.getAuthor());
            for (Item item : authorMatches) {
                double score = calculateAuthorScore(item.getAuthor(), query.getAuthor());
                
                // Check if already in results
                Optional<ScoredItem> existing = scored.stream()
                    .filter(si -> si.getItem().getId().equals(item.getId()))
                    .findFirst();
                
                if (existing.isPresent()) {
                    // Boost existing score
                    existing.get().addScore(score * 1.5);
                } else {
                    scored.add(new ScoredItem(item, score * 1.5)); // Author weighted 1.5x
                }
            }
        }
        
        // Search by category
        if (query.hasCategory()) {
            List<Item> categoryMatches = itemRepo.findByCategory(query.getCategory());
            for (Item item : categoryMatches) {
                Optional<ScoredItem> existing = scored.stream()
                    .filter(si -> si.getItem().getId().equals(item.getId()))
                    .findFirst();
                
                if (existing.isPresent()) {
                    existing.get().addScore(0.5); // Small boost for category match
                } else {
                    scored.add(new ScoredItem(item, 0.5));
                }
            }
        }
        
        return scored;
    }
    
    private double calculateTitleScore(String itemTitle, String queryTitle) {
        String lowerItemTitle = itemTitle.toLowerCase();
        String lowerQuery = queryTitle.toLowerCase();
        
        // Exact match
        if (lowerItemTitle.equals(lowerQuery)) {
            return 10.0;
        }
        
        // Starts with
        if (lowerItemTitle.startsWith(lowerQuery)) {
            return 8.0;
        }
        
        // Contains
        if (lowerItemTitle.contains(lowerQuery)) {
            return 5.0;
        }
        
        // Word overlap (Jaccard similarity)
        Set<String> itemWords = new HashSet<>(Arrays.asList(lowerItemTitle.split("\\s+")));
        Set<String> queryWords = new HashSet<>(Arrays.asList(lowerQuery.split("\\s+")));
        
        Set<String> intersection = new HashSet<>(itemWords);
        intersection.retainAll(queryWords);
        
        Set<String> union = new HashSet<>(itemWords);
        union.addAll(queryWords);
        
        double jaccard = (double) intersection.size() / union.size();
        return jaccard * 5.0;
    }
    
    private List<ScoredItem> applyFilters(List<ScoredItem> items, SearchQuery query) {
        return items.stream()
            .filter(si -> {
                Item item = si.getItem();
                
                // Filter by availability
                if (query.isAvailableOnly()) {
                    long availableCount = itemCopyRepo.countAvailableCopies(item.getId());
                    if (availableCount == 0) return false;
                }
                
                // Filter by publication year
                if (query.hasYearRange()) {
                    int year = item.getPublicationYear();
                    if (year < query.getMinYear() || year > query.getMaxYear()) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
}

class ScoredItem {
    private final Item item;
    private double score;
    
    public ScoredItem(Item item, double score) {
        this.item = item;
        this.score = score;
    }
    
    public void addScore(double delta) {
        this.score += delta;
    }
    
    public Item getItem() { return item; }
    public double getScore() { return score; }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Repository** | Data access for Item, Member, Loan, etc. | Abstract persistence, enable testing |
| **Domain Events** | CheckoutEvent, ReturnEvent, HoldReadyEvent | Decouple business logic from side effects |
| **Strategy** | Fine calculation, hold priority, search ranking | Swap algorithms based on policy |
| **Factory** | Create different item types (Book, DVD, Magazine) | Encapsulate creation logic |
| **Specification** | Search filters, eligibility checks | Composable business rules |
| **Value Object** | ISBN, MembershipId, Money | Immutable, validated domain concepts |
| **Aggregate** | Item + ItemCopies | Consistency boundary |

### 5. Domain Model & Class Structure

```
┌──────────────────────┐
│  LibraryService      │ (Application Service)
│   - checkoutService  │
│   - holdService      │
│   - fineService      │
│   - searchService    │
└────────┬─────────────┘
         │ coordinates
         │
    ┌────┴──────────┬────────────┬──────────┐
    ▼               ▼            ▼          ▼
┌────────┐    ┌────────┐   ┌──────┐   ┌──────┐
│  Item  │    │ Member │   │ Loan │   │ Hold │
│(Aggr.) │    │(Entity)│   │(Ent.)│   │(Ent.)│
└───┬────┘    └────────┘   └──────┘   └──────┘
    │ has
    ▼
┌──────────┐
│ItemCopy  │ (Entity within Item aggregate)
└──────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Checkout Flow**
```
Member   UI   CheckoutSvc   ItemCopyRepo   LoanRepo   EventBus
  │       │         │              │           │          │
  ├─scan─>│         │              │           │          │
  │       ├─checkout─────>│        │           │          │
  │       │         ├─findWithLock─>│           │          │
  │       │         │<─copy(avail)──┤           │          │
  │       │         ├─checkEligibility          │          │
  │       │         ├─calcDueDate              │          │
  │       │         ├─createLoan────────────────>│          │
  │       │         ├─updateStatus─>│           │          │
  │       │         ├─publish──────────────────────────────>│
  │       │<─success──────────┤     │           │          │
  │<─rcpt─┤         │              │           │          │
```

**Sequence: Return with Hold**
```
Staff  ReturnSvc  LoanRepo  ItemCopyRepo  HoldRepo  NotifSvc
  │        │         │           │            │         │
  ├─return─>│        │           │            │         │
  │        ├─findLoan─>│          │            │         │
  │        ├─findNextHold────────────────────>│         │
  │        │<─hold(priority)──────────────────┤         │
  │        ├─reserveForHold──────>│            │         │
  │        ├─updateHoldStatus──────────────────>│         │
  │        ├─notify────────────────────────────────────>│
  │        ├─updateLoan─>│        │            │         │
  │<─done──┤        │           │            │         │
```

### 7. Core Implementation (Interview-Critical Methods)

```java
// ============================================
// DOMAIN ENTITIES
// ============================================

public class Item {
    private UUID id;
    private String isbn;
    private String title;
    private String author;
    private ItemType type;
    private Category category;
    private int publicationYear;
    private BigDecimal replacementCost;
    
    // Copies across all branches
    private List<ItemCopy> copies = new ArrayList<>();
    
    public long getAvailableCount() {
        return copies.stream()
            .filter(c -> c.getStatus() == ItemStatus.AVAILABLE)
            .count();
    }
    
    public Optional<ItemCopy> getAvailableCopy(UUID branchId) {
        return copies.stream()
            .filter(c -> c.getStatus() == ItemStatus.AVAILABLE)
            .filter(c -> branchId == null || c.getBranchId().equals(branchId))
            .findFirst();
    }
}

public class ItemCopy {
    private UUID id;
    private UUID itemId;
    private UUID branchId;
    private String barcode;
    private ItemStatus status;
    private UUID reservedForMemberId; // For holds
    private LocalDate reservationExpiresAt;
    
    @Version
    private long version;
}

public enum ItemType {
    BOOK(14),      // 14 day lending period
    MAGAZINE(7),   // 7 days
    DVD(3);        // 3 days
    
    private final int defaultLendingDays;
    
    ItemType(int days) {
        this.defaultLendingDays = days;
    }
    
    public int getDefaultLendingDays() {
        return defaultLendingDays;
    }
}

public enum ItemStatus {
    AVAILABLE,
    CHECKED_OUT,
    ON_HOLD,      // Reserved for specific member
    IN_TRANSIT,   // Being moved between branches
    LOST,
    DAMAGED
}

public class Member {
    private UUID id;
    private String membershipId;
    private String name;
    private String email;
    private MemberType type;
    private LocalDate joinDate;
    private MemberStatus status;
    private int activeLoansCount;
    private BigDecimal totalFines;
    
    public boolean canCheckout() {
        // Business rules
        if (status != MemberStatus.ACTIVE) {
            return false;
        }
        
        // Check loan limit
        int maxLoans = type.getMaxLoanLimit();
        if (activeLoansCount >= maxLoans) {
            return false;
        }
        
        // Check unpaid fines
        if (totalFines.compareTo(new BigDecimal("10.00")) > 0) {
            return false;
        }
        
        return true;
    }
    
    public int getMembershipYears() {
        return (int) ChronoUnit.YEARS.between(joinDate, LocalDate.now());
    }
    
    public void incrementActiveLoans() {
        this.activeLoansCount++;
    }
    
    public void decrementActiveLoans() {
        this.activeLoansCount = Math.max(0, this.activeLoansCount - 1);
    }
}

public enum MemberType {
    REGULAR(5),    // Max 5 books
    STUDENT(10),   // Max 10 books
    PREMIUM(20);   // Max 20 books, extended periods
    
    private final int maxLoanLimit;
    
    MemberType(int limit) {
        this.maxLoanLimit = limit;
    }
    
    public int getMaxLoanLimit() {
        return maxLoanLimit;
    }
}

public class Loan {
    private UUID id;
    private UUID memberId;
    private UUID itemCopyId;
    private UUID branchId;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private int renewalCount;
    
    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && 
               LocalDate.now().isAfter(dueDate);
    }
    
    public long getOverdueDays() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
}

public enum LoanStatus {
    ACTIVE,
    RETURNED,
    LOST
}

public class Hold {
    private UUID id;
    private UUID memberId;
    private UUID itemId; // Not specific copy
    private LocalDate placedDate;
    private LocalDate expiresDate;
    private LocalDate notifiedDate;
    private HoldStatus status;
    private int priority; // Higher = more urgent
}

public enum HoldStatus {
    PENDING,           // Waiting for copy
    READY_FOR_PICKUP, // Copy available, member notified
    FULFILLED,         // Member checked out
    EXPIRED,          // Member didn't pick up
    CANCELLED
}

public class Fine {
    private UUID id;
    private UUID loanId;
    private UUID memberId;
    private BigDecimal amount;
    private FineStatus status;
    private FineType type;
    private LocalDate createdDate;
    private LocalDate paidDate;
}

public enum FineStatus {
    UNPAID,
    PAID,
    WAIVED
}

public enum FineType {
    OVERDUE,
    REPLACEMENT,
    DAMAGE
}

// ============================================
// CHECKOUT SERVICE
// ============================================

public class CheckoutService {
    private final ItemCopyRepository itemCopyRepo;
    private final MemberRepository memberRepo;
    private final LoanRepository loanRepo;
    private final EventPublisher eventPublisher;
    
    /**
     * INTERVIEW CRITICAL: Atomic checkout operation
     */
    @Transactional
    public CheckoutResult checkout(UUID memberId, UUID itemCopyId) {
        // Lock item copy
        ItemCopy copy = itemCopyRepo.findByIdWithLock(itemCopyId)
            .orElseThrow(() -> new ItemCopyNotFoundException(itemCopyId));
        
        // Validate availability
        if (copy.getStatus() != ItemStatus.AVAILABLE) {
            // Check if reserved for this member
            if (copy.getStatus() == ItemStatus.ON_HOLD &&
                copy.getReservedForMemberId() != null &&
                copy.getReservedForMemberId().equals(memberId)) {
                // Allow checkout
            } else {
                return CheckoutResult.failure(
                    CheckoutFailureReason.ITEM_NOT_AVAILABLE,
                    "Item status: " + copy.getStatus()
                );
            }
        }
        
        // Validate member
        Member member = memberRepo.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));
        
        if (!member.canCheckout()) {
            return CheckoutResult.failure(
                CheckoutFailureReason.MEMBER_INELIGIBLE,
                "Member cannot checkout: unpaid fines or max loans reached"
            );
        }
        
        // Get item details for due date calculation
        Item item = itemRepo.findById(copy.getItemId())
            .orElseThrow();
        
        // Calculate due date
        int lendingDays = item.getType().getDefaultLendingDays();
        if (member.getType() == MemberType.PREMIUM) {
            lendingDays = (int) (lendingDays * 1.5);
        }
        LocalDate dueDate = LocalDate.now().plusDays(lendingDays);
        
        // Create loan
        Loan loan = Loan.builder()
            .id(UUID.randomUUID())
            .memberId(memberId)
            .itemCopyId(itemCopyId)
            .branchId(copy.getBranchId())
            .checkoutDate(LocalDate.now())
            .dueDate(dueDate)
            .status(LoanStatus.ACTIVE)
            .renewalCount(0)
            .build();
        
        loanRepo.save(loan);
        
        // Update item copy
        copy.setStatus(ItemStatus.CHECKED_OUT);
        copy.setReservedForMemberId(null); // Clear hold reservation
        itemCopyRepo.save(copy);
        
        // Update member
        member.incrementActiveLoans();
        memberRepo.save(member);
        
        // Publish event
        eventPublisher.publish(new ItemCheckedOutEvent(
            loan.getId(), memberId, itemCopyId, dueDate
        ));
        
        return CheckoutResult.success(loan);
    }
    
    /**
     * INTERVIEW CRITICAL: Renew loan if eligible
     */
    @Transactional
    public RenewalResult renew(UUID loanId) {
        Loan loan = loanRepo.findById(loanId)
            .orElseThrow(() -> new LoanNotFoundException(loanId));
        
        // Check if active
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            return RenewalResult.failure("Loan is not active");
        }
        
        // Check renewal limit
        if (loan.getRenewalCount() >= 2) {
            return RenewalResult.failure("Max renewals reached (2)");
        }
        
        // Check for pending holds
        ItemCopy copy = itemCopyRepo.findById(loan.getItemCopyId())
            .orElseThrow();
        boolean hasPendingHolds = holdRepo.existsPendingHolds(copy.getItemId());
        
        if (hasPendingHolds) {
            return RenewalResult.failure("Item has pending holds");
        }
        
        // Check member eligibility
        Member member = memberRepo.findById(loan.getMemberId())
            .orElseThrow();
        
        if (member.getTotalFines().compareTo(new BigDecimal("10.00")) > 0) {
            return RenewalResult.failure("Member has excessive fines");
        }
        
        // Extend due date
        Item item = itemRepo.findById(copy.getItemId()).orElseThrow();
        int extensionDays = item.getType().getDefaultLendingDays();
        LocalDate newDueDate = loan.getDueDate().plusDays(extensionDays);
        
        loan.setDueDate(newDueDate);
        loan.setRenewalCount(loan.getRenewalCount() + 1);
        loanRepo.save(loan);
        
        eventPublisher.publish(new LoanRenewedEvent(loanId, newDueDate));
        
        return RenewalResult.success(loan);
    }
}

// ============================================
// RETURN SERVICE
// ============================================

public class ReturnService {
    private final LoanRepository loanRepo;
    private final ItemCopyRepository itemCopyRepo;
    private final HoldRepository holdRepo;
    private final FineCalculationService fineService;
    private final NotificationService notificationService;
    private final EventPublisher eventPublisher;
    
    /**
     * INTERVIEW CRITICAL: Process return with fine calculation and hold assignment
     */
    @Transactional
    public ReturnResult processReturn(UUID loanId) {
        Loan loan = loanRepo.findById(loanId)
            .orElseThrow(() -> new LoanNotFoundException(loanId));
        
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            return ReturnResult.failure("Loan is not active");
        }
        
        LocalDate returnDate = LocalDate.now();
        ItemCopy copy = itemCopyRepo.findById(loan.getItemCopyId())
            .orElseThrow();
        
        // Calculate fine if overdue
        BigDecimal fine = BigDecimal.ZERO;
        if (returnDate.isAfter(loan.getDueDate())) {
            fine = fineService.calculateFine(loan, returnDate);
            
            if (fine.compareTo(BigDecimal.ZERO) > 0) {
                Fine fineRecord = Fine.builder()
                    .id(UUID.randomUUID())
                    .loanId(loanId)
                    .memberId(loan.getMemberId())
                    .amount(fine)
                    .status(FineStatus.UNPAID)
                    .type(FineType.OVERDUE)
                    .createdDate(returnDate)
                    .build();
                
                fineRepo.save(fineRecord);
                
                // Update member's total fines
                Member member = memberRepo.findById(loan.getMemberId())
                    .orElseThrow();
                member.setTotalFines(member.getTotalFines().add(fine));
                memberRepo.save(member);
            }
        }
        
        // Check for pending holds
        Optional<Hold> nextHold = holdRepo.findNextPendingHold(copy.getItemId());
        
        if (nextHold.isPresent()) {
            Hold hold = nextHold.get();
            
            // Reserve for hold
            copy.setStatus(ItemStatus.ON_HOLD);
            copy.setReservedForMemberId(hold.getMemberId());
            copy.setReservationExpiresAt(LocalDate.now().plusDays(2));
            
            // Update hold
            hold.setStatus(HoldStatus.READY_FOR_PICKUP);
            hold.setNotifiedDate(LocalDate.now());
            holdRepo.save(hold);
            
            // Notify member
            notificationService.notifyHoldReady(hold);
            
        } else {
            // Make available
            copy.setStatus(ItemStatus.AVAILABLE);
            copy.setReservedForMemberId(null);
        }
        
        itemCopyRepo.save(copy);
        
        // Update loan
        loan.setReturnDate(returnDate);
        loan.setStatus(LoanStatus.RETURNED);
        loanRepo.save(loan);
        
        // Update member active loans count
        Member member = memberRepo.findById(loan.getMemberId())
            .orElseThrow();
        member.decrementActiveLoans();
        memberRepo.save(member);
        
        // Publish event
        eventPublisher.publish(new ItemReturnedEvent(loanId, copy.getId(), fine));
        
        return ReturnResult.success(loan, fine);
    }
}

// ============================================
// HOLD SERVICE
// ============================================

public class HoldService {
    private final HoldRepository holdRepo;
    private final ItemCopyRepository itemCopyRepo;
    private final MemberRepository memberRepo;
    private final EventPublisher eventPublisher;
    
    /**
     * INTERVIEW CRITICAL: Place hold with priority queue
     */
    @Transactional
    public HoldResult placeHold(UUID memberId, UUID itemId) {
        // Validate member
        Member member = memberRepo.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));
        
        // Check for existing hold
        if (holdRepo.existsActiveHold(memberId, itemId)) {
            return HoldResult.failure("Member already has active hold on this item");
        }
        
        // Check if available now
        long availableCount = itemCopyRepo.countAvailableCopies(itemId);
        if (availableCount > 0) {
            return HoldResult.failure("Item is available for immediate checkout");
        }
        
        // Calculate priority
        int priority = calculatePriority(member);
        
        // Create hold
        Hold hold = Hold.builder()
            .id(UUID.randomUUID())
            .memberId(memberId)
            .itemId(itemId)
            .placedDate(LocalDate.now())
            .expiresDate(LocalDate.now().plusDays(90)) // Hold valid for 90 days
            .status(HoldStatus.PENDING)
            .priority(priority)
            .build();
        
        holdRepo.save(hold);
        
        eventPublisher.publish(new HoldPlacedEvent(hold.getId(), memberId, itemId));
        
        return HoldResult.success(hold);
    }
    
    private int calculatePriority(Member member) {
        int priority = 0;
        
        // Premium members
        if (member.getType() == MemberType.PREMIUM) {
            priority += 100;
        }
        
        // Long-standing members
        int years = member.getMembershipYears();
        priority += Math.min(years * 5, 50);
        
        return priority;
    }
    
    /**
     * Background job to expire old holds
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expireOldHolds() {
        LocalDate now = LocalDate.now();
        
        List<Hold> expiredHolds = holdRepo.findExpiredHolds(now);
        
        for (Hold hold : expiredHolds) {
            hold.setStatus(HoldStatus.EXPIRED);
            holdRepo.save(hold);
        }
    }
}

// ============================================
// REPOSITORY INTERFACES
// ============================================

public interface LoanRepository {
    Optional<Loan> findById(UUID id);
    Loan save(Loan loan);
    
    @Query("SELECT * FROM loans WHERE status = 'ACTIVE' AND due_date < :date")
    List<Loan> findOverdueLoans(@Param("date") LocalDate date);
    
    @Query("SELECT * FROM loans WHERE member_id = :memberId AND status = 'ACTIVE'")
    List<Loan> findActiveLoansByMember(@Param("memberId") UUID memberId);
}

public interface HoldRepository {
    Optional<Hold> findById(UUID id);
    Hold save(Hold hold);
    
    @Query("SELECT * FROM holds WHERE item_id = :itemId AND status = 'PENDING' " +
           "ORDER BY priority DESC, placed_date ASC LIMIT 1")
    Optional<Hold> findNextPendingHold(@Param("itemId") UUID itemId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM holds WHERE member_id = :memberId " +
           "AND item_id = :itemId AND status IN ('PENDING', 'READY_FOR_PICKUP'))")
    boolean existsActiveHold(@Param("memberId") UUID memberId, 
                             @Param("itemId") UUID itemId);
    
    @Query("SELECT * FROM holds WHERE status = 'READY_FOR_PICKUP' " +
           "AND reservation_expires_at < :date")
    List<Hold> findExpiredHolds(@Param("date") LocalDate date);
}

// Result DTOs omitted for brevity
```

### 8. Thread Safety & Concurrency

**Checkout Concurrency:**
- Pessimistic lock on ItemCopy (SELECT FOR UPDATE)
- Transaction isolation prevents double-checkout
- Version field for optimistic locking alternative

**Hold Queue:**
- Priority-based ordering (priority DESC, placed_date ASC)
- Atomic assignment on return
- No race condition (single transaction)

**Fine Calculation:**
- Background job (no user-facing contention)
- Batch processing with error isolation
- Idempotent (recalculates if exists)

### 9. Top Interview Questions & Answers

**Q1: How do you prevent two people from checking out the last copy?**
**A:**
```java
// Use database row-level lock
@Query("SELECT * FROM item_copies WHERE id = :id FOR UPDATE")
Optional<ItemCopy> findByIdWithLock(@Param("id") UUID id);

// Within transaction:
1. Lock row
2. Check availability
3. Create loan
4. Update status
5. Commit (releases lock)

// Alternative: Optimistic locking with version field
```

**Q2: How do you decide which hold gets the returned item?**
**A:**
Priority queue ordering:
```sql
ORDER BY priority DESC, placed_date ASC

Priority calculation:
- Premium members: +100
- Membership years: +5 per year (max 50)
- Student + academic book: +50

Tie-breaker: Earliest placed_date (FIFO)
```

**Q3: What if member doesn't pick up held item?**
**A:**
```java
// Set reservation expiry (2 days from notification)
copy.setReservationExpiresAt(LocalDate.now().plusDays(2));

// Background job checks expired reservations
@Scheduled(cron = "0 0 4 * * *")
void expireReservations() {
    List<ItemCopy> expired = findExpiredReservations();
    for (ItemCopy copy : expired) {
        // Mark hold as expired
        // Make item available
        // Assign to next hold if exists
    }
}
```

**Q4: How do you calculate fines for different item types?**
**A:**
```java
Fine calculation formula:
1. Base rate by type (Book: $0.50/day, DVD: $2.00/day)
2. Grace period (2 days, no charge)
3. Chargeable days = max(0, overdue_days - grace_period)
4. Calculated fine = rate * chargeable_days
5. Apply cap (max $50 per item)

Premium members: No grace period discount
```

**Q5: What metrics to track?**
**A:**
```java
Metrics:
1. Checkout rate per hour/day
2. Average loan duration by item type
3. Overdue rate (% of loans overdue)
4. Hold fulfillment time (placed → ready)
5. Popular items (most borrowed)
6. Member activity (checkouts per member)
7. Fine collection rate

Alerts:
- Overdue rate > 15% → Review lending periods
- Hold fulfillment > 30 days → Purchase more copies
- Single item > 10 holds → High demand
```

**Q6: How to handle lost items?**
**A:**
```java
// After 30 days overdue:
1. Mark loan status as LOST
2. Mark item copy as LOST
3. Create replacement cost fine
4. Suspend member until fine paid
5. Order replacement copy (if popular)

// If member returns later:
- Waive replacement fine
- Charge overdue fine only (with cap)
- Restore item copy
```

**Q7: What's the database schema?**
**A:**
```sql
CREATE TABLE items (
    id UUID PRIMARY KEY,
    isbn VARCHAR(13) UNIQUE,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(200),
    type VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    publication_year INT,
    replacement_cost DECIMAL(10,2)
);

CREATE TABLE item_copies (
    id UUID PRIMARY KEY,
    item_id UUID NOT NULL REFERENCES items(id),
    branch_id UUID NOT NULL,
    barcode VARCHAR(50) UNIQUE,
    status VARCHAR(20) NOT NULL,
    reserved_for_member_id UUID,
    reservation_expires_at DATE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_copies_item_status 
    ON item_copies(item_id, status);

CREATE TABLE loans (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL,
    item_copy_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    checkout_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL,
    renewal_count INT DEFAULT 0
);

CREATE INDEX idx_loans_member_status 
    ON loans(member_id, status);
CREATE INDEX idx_loans_overdue 
    ON loans(status, due_date) 
    WHERE status = 'ACTIVE';

CREATE TABLE holds (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL,
    item_id UUID NOT NULL,
    placed_date DATE NOT NULL,
    expires_date DATE NOT NULL,
    notified_date DATE,
    status VARCHAR(20) NOT NULL,
    priority INT NOT NULL
);

CREATE INDEX idx_holds_item_priority 
    ON holds(item_id, status, priority DESC, placed_date ASC);

CREATE TABLE fines (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    member_id UUID NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_date DATE NOT NULL,
    paid_date DATE
);

CREATE INDEX idx_fines_member_status 
    ON fines(member_id, status);
```

**Q8: How to search efficiently across large catalog?**
**A:**
```java
// Full-text search index
CREATE INDEX idx_items_title_gin 
    ON items USING gin(to_tsvector('english', title));

CREATE INDEX idx_items_author_gin 
    ON items USING gin(to_tsvector('english', author));

// Query:
SELECT * FROM items 
WHERE to_tsvector('english', title) @@ to_tsquery('english', 'java programming')
ORDER BY ts_rank(to_tsvector('english', title), to_tsquery('english', 'java programming')) DESC;

// Alternative: Elasticsearch for advanced search
```

**Q9: How to handle renewals?**
**A:**
```java
Renewal rules:
1. Max 2 renewals per loan
2. No pending holds on item
3. Member in good standing (fines < $10)
4. Item not overdue by > 7 days

Extension period:
- Same as original lending period
- Due date = current_due_date + lending_days
```

**Q10: How to scale to multiple branches?**
**A:**
```
Partitioning strategy:
1. Item catalog: Shared across all branches
2. Item copies: Tagged with branch_id
3. Loans: Belong to checkout branch
4. Holds: System-wide (any branch can fulfill)

Inter-branch transfers:
- Member can return to any branch
- Item status: IN_TRANSIT
- Background job syncs inventory
- Transfer request system for high-demand items
```

### 10. Extensions & Variations

1. **Digital Content**: E-books, audiobooks with concurrent license limits
2. **Waitlist Notifications**: SMS/email when position in queue improves
3. **Reading History**: Privacy-aware tracking of past borrowings
4. **Recommendations**: Suggest similar books based on history
5. **Mobile App**: Barcode scanning, digital library card

### 11. Testing Strategy

**Unit Tests:**
- Fine calculation logic
- Hold priority calculation
- Member eligibility rules
- Search scoring algorithm

**Integration Tests:**
- Checkout flow with locking
- Return with hold assignment
- Concurrent checkouts (race condition)
- Overdue processing job

**Load Tests:**
- 1000 concurrent checkouts
- Large catalog search (1M items)
- Peak hour simulation

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Check availability → Create loan (race condition)
✅ **Do**: Lock item copy within transaction

❌ **Avoid**: Complex hold assignment logic in application
✅ **Do**: Database ORDER BY with priority

❌ **Avoid**: Synchronous fine calculation on return
✅ **Do**: Background job for bulk processing

❌ **Avoid**: No expiry on holds/reservations
✅ **Do**: Automatic expiration with cleanup job

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Checkout | O(1) | O(1) | Single copy lock |
| Return | O(log H) | O(1) | H = holds on item (priority queue) |
| Search | O(log N) | O(R) | N = items, R = results (indexed) |
| Place hold | O(1) | O(1) | Insert with priority |
| Calculate fine | O(1) | O(1) | Simple arithmetic |
| Process overdues | O(N) | O(N) | N = overdue loans (batch) |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Concurrency** | 30% | Prevents double-checkout, proper locking |
| **Hold Management** | 25% | Priority queue, fair assignment |
| **Fine Calculation** | 20% | Flexible policy, grace period, cap |
| **Search** | 15% | Efficient indexing, relevance ranking |
| **Domain Modeling** | 10% | Clear entities, aggregates, value objects |

**Red Flags:**
- No locking strategy for checkout
- FIFO-only holds (no priority)
- Hardcoded fine calculation
- No expiry mechanism for holds
- Missing audit trail

---
