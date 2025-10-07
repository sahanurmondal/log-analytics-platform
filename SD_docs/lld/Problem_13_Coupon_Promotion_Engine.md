# Problem 13: Coupon/Promotion Engine (Specification Pattern + Strategy + Observer)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a flexible coupon and promotion system supporting combinable rules, stacking constraints, user eligibility, and automatic application at checkout.

**Assumptions / Scope:**
- Multiple coupon types (percentage, fixed, free shipping, BOGO)
- Stackability rules (can combine certain coupons, not others)
- User-specific coupons (unique codes, targeted offers)
- Automatic application of best available promotions
- Usage limits (per user, total redemptions)
- Time-bound promotions
- Scale: 100K active coupons, 10K redemptions/sec
- Out of scope: Referral programs, loyalty points integration

**Non-Functional Goals:**
- Validate coupon in < 10ms
- Apply promotions in < 50ms
- Support 10K concurrent redemptions
- 100% audit trail (who used what when)
- Prevent double redemption
- High availability (99.9%)

### 2. Core Requirements

**Functional:**
- Create coupons with codes (SAVE20, FREESHIP)
- Define eligibility rules (min cart value, customer segment, product category)
- Set usage limits (1 per user, 1000 total)
- Support stackability (can combine with other coupons)
- Validate coupon at checkout
- Apply best available promotions automatically
- Track redemptions with user/order mapping
- Prevent duplicate redemption
- Generate unique single-use codes
- Support scheduled promotions (Black Friday sale)
- Expire coupons after date/usage limit

**Non-Functional:**
- **Performance**: < 10ms validation, < 50ms application
- **Consistency**: No double redemption (atomic updates)
- **Scalability**: 100K active coupons, 10K redemptions/sec
- **Auditability**: Full redemption history
- **Observability**: Track redemption rates, popular coupons

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Specification Pattern for Eligibility Rules**
- **Problem**: Complex, composable eligibility criteria
- **Solution**: Specification pattern with AND/OR/NOT composition
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Specification pattern for coupon eligibility
 */
interface CouponSpecification {
    boolean isSatisfiedBy(CouponContext context);
    CouponSpecification and(CouponSpecification other);
    CouponSpecification or(CouponSpecification other);
    CouponSpecification not();
}

/**
 * Base specification with composition methods
 */
abstract class BaseCouponSpecification implements CouponSpecification {
    
    @Override
    public CouponSpecification and(CouponSpecification other) {
        return new AndSpecification(this, other);
    }
    
    @Override
    public CouponSpecification or(CouponSpecification other) {
        return new OrSpecification(this, other);
    }
    
    @Override
    public CouponSpecification not() {
        return new NotSpecification(this);
    }
}

/**
 * Composite specifications
 */
class AndSpecification extends BaseCouponSpecification {
    private final CouponSpecification left;
    private final CouponSpecification right;
    
    public AndSpecification(CouponSpecification left, CouponSpecification right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        return left.isSatisfiedBy(context) && right.isSatisfiedBy(context);
    }
}

class OrSpecification extends BaseCouponSpecification {
    private final CouponSpecification left;
    private final CouponSpecification right;
    
    public OrSpecification(CouponSpecification left, CouponSpecification right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        return left.isSatisfiedBy(context) || right.isSatisfiedBy(context);
    }
}

class NotSpecification extends BaseCouponSpecification {
    private final CouponSpecification specification;
    
    public NotSpecification(CouponSpecification specification) {
        this.specification = specification;
    }
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        return !specification.isSatisfiedBy(context);
    }
}

/**
 * Concrete specifications
 */
class MinCartValueSpecification extends BaseCouponSpecification {
    private final BigDecimal minValue;
    
    public MinCartValueSpecification(BigDecimal minValue) {
        this.minValue = minValue;
    }
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        return context.getCartTotal().compareTo(minValue) >= 0;
    }
}

class CustomerSegmentSpecification extends BaseCouponSpecification {
    private final Set<CustomerSegment> allowedSegments;
    
    public CustomerSegmentSpecification(Set<CustomerSegment> allowedSegments) {
        this.allowedSegments = allowedSegments;
    }
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        return allowedSegments.contains(context.getCustomerSegment());
    }
}

class ProductCategorySpecification extends BaseCouponSpecification {
    private final Set<String> allowedCategories;
    
    public ProductCategorySpecification(Set<String> allowedCategories) {
        this.allowedCategories = allowedCategories;
    }
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        return context.getCartItems().stream()
            .anyMatch(item -> allowedCategories.contains(item.getCategoryId()));
    }
}

class FirstOrderSpecification extends BaseCouponSpecification {
    private final OrderHistoryService orderHistoryService;
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        return orderHistoryService.getOrderCount(context.getCustomerId()) == 0;
    }
}

class TimeRangeSpecification extends BaseCouponSpecification {
    private final Instant startTime;
    private final Instant endTime;
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        Instant now = context.getCurrentTime();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }
}

class UsageLimitSpecification extends BaseCouponSpecification {
    private final CouponUsageRepository usageRepo;
    private final int maxUsagePerUser;
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        int usageCount = usageRepo.countByCustomerAndCoupon(
            context.getCustomerId(),
            context.getCouponCode()
        );
        
        return usageCount < maxUsagePerUser;
    }
}

// Usage example
CouponSpecification eligibility = new MinCartValueSpecification(new BigDecimal("50"))
    .and(new CustomerSegmentSpecification(Set.of(CustomerSegment.VIP, CustomerSegment.REGULAR)))
    .and(new TimeRangeSpecification(startDate, endDate))
    .and(new UsageLimitSpecification(usageRepo, 1));

if (eligibility.isSatisfiedBy(context)) {
    // Apply coupon
}
```

**Challenge 2: Strategy Pattern for Discount Calculation**
- **Problem**: Different discount types require different calculation logic
- **Solution**: Strategy pattern for discount calculation
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Strategy pattern for discount calculation
 */
interface DiscountStrategy {
    DiscountResult calculate(CouponContext context);
    boolean isApplicable(CouponContext context);
}

/**
 * Percentage discount strategy
 */
class PercentageDiscountStrategy implements DiscountStrategy {
    private final double percentage; // 0.20 = 20%
    private final BigDecimal maxDiscount; // Cap discount amount
    
    public PercentageDiscountStrategy(double percentage, BigDecimal maxDiscount) {
        this.percentage = percentage;
        this.maxDiscount = maxDiscount;
    }
    
    /**
     * INTERVIEW CRITICAL: Calculate percentage discount with cap
     */
    @Override
    public DiscountResult calculate(CouponContext context) {
        BigDecimal cartTotal = context.getCartTotal();
        BigDecimal discount = cartTotal.multiply(BigDecimal.valueOf(percentage));
        
        // Apply cap if set
        if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
            discount = maxDiscount;
        }
        
        return DiscountResult.builder()
            .discountAmount(discount)
            .finalAmount(cartTotal.subtract(discount))
            .description(String.format("%.0f%% off", percentage * 100))
            .build();
    }
    
    @Override
    public boolean isApplicable(CouponContext context) {
        return context.getCartTotal().compareTo(BigDecimal.ZERO) > 0;
    }
}

/**
 * Fixed amount discount strategy
 */
class FixedDiscountStrategy implements DiscountStrategy {
    private final BigDecimal discountAmount;
    
    public FixedDiscountStrategy(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    @Override
    public DiscountResult calculate(CouponContext context) {
        BigDecimal cartTotal = context.getCartTotal();
        BigDecimal actualDiscount = discountAmount;
        
        // Can't discount more than cart total
        if (actualDiscount.compareTo(cartTotal) > 0) {
            actualDiscount = cartTotal;
        }
        
        return DiscountResult.builder()
            .discountAmount(actualDiscount)
            .finalAmount(cartTotal.subtract(actualDiscount))
            .description("$" + actualDiscount + " off")
            .build();
    }
    
    @Override
    public boolean isApplicable(CouponContext context) {
        return context.getCartTotal().compareTo(discountAmount) >= 0;
    }
}

/**
 * Free shipping strategy
 */
class FreeShippingStrategy implements DiscountStrategy {
    
    @Override
    public DiscountResult calculate(CouponContext context) {
        BigDecimal shippingCost = context.getShippingCost();
        
        return DiscountResult.builder()
            .discountAmount(shippingCost)
            .finalAmount(context.getCartTotal()) // Shipping separate
            .description("Free shipping")
            .freeShipping(true)
            .build();
    }
    
    @Override
    public boolean isApplicable(CouponContext context) {
        return context.getShippingCost() != null && 
               context.getShippingCost().compareTo(BigDecimal.ZERO) > 0;
    }
}

/**
 * Buy X Get Y free strategy
 */
class BuyXGetYFreeStrategy implements DiscountStrategy {
    private final int buyQuantity;
    private final int freeQuantity;
    private final String applicableProductId; // null = any product
    
    public BuyXGetYFreeStrategy(int buyQuantity, int freeQuantity, String productId) {
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
        this.applicableProductId = productId;
    }
    
    /**
     * INTERVIEW CRITICAL: Calculate BOGO discount
     */
    @Override
    public DiscountResult calculate(CouponContext context) {
        List<CartItem> applicableItems = context.getCartItems().stream()
            .filter(item -> applicableProductId == null || 
                          item.getProductId().equals(applicableProductId))
            .collect(Collectors.toList());
        
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (CartItem item : applicableItems) {
            int quantity = item.getQuantity();
            int sets = quantity / (buyQuantity + freeQuantity);
            
            if (sets > 0) {
                BigDecimal itemPrice = item.getPrice();
                BigDecimal discount = itemPrice.multiply(
                    BigDecimal.valueOf(sets * freeQuantity)
                );
                totalDiscount = totalDiscount.add(discount);
            }
        }
        
        return DiscountResult.builder()
            .discountAmount(totalDiscount)
            .finalAmount(context.getCartTotal().subtract(totalDiscount))
            .description(String.format("Buy %d Get %d Free", buyQuantity, freeQuantity))
            .build();
    }
    
    @Override
    public boolean isApplicable(CouponContext context) {
        return context.getCartItems().stream()
            .filter(item -> applicableProductId == null || 
                          item.getProductId().equals(applicableProductId))
            .anyMatch(item -> item.getQuantity() >= (buyQuantity + freeQuantity));
    }
}

/**
 * Tiered discount strategy (spend more, save more)
 */
class TieredDiscountStrategy implements DiscountStrategy {
    private final List<DiscountTier> tiers;
    
    @Value
    static class DiscountTier {
        BigDecimal minSpend;
        double discountPercentage;
    }
    
    @Override
    public DiscountResult calculate(CouponContext context) {
        BigDecimal cartTotal = context.getCartTotal();
        
        // Find applicable tier (highest tier that meets min spend)
        DiscountTier applicableTier = tiers.stream()
            .filter(tier -> cartTotal.compareTo(tier.getMinSpend()) >= 0)
            .max(Comparator.comparing(DiscountTier::getMinSpend))
            .orElseThrow();
        
        BigDecimal discount = cartTotal.multiply(
            BigDecimal.valueOf(applicableTier.getDiscountPercentage())
        );
        
        return DiscountResult.builder()
            .discountAmount(discount)
            .finalAmount(cartTotal.subtract(discount))
            .description(String.format("%.0f%% off (Spend $%.0f+)", 
                                     applicableTier.getDiscountPercentage() * 100,
                                     applicableTier.getMinSpend()))
            .build();
    }
    
    @Override
    public boolean isApplicable(CouponContext context) {
        BigDecimal cartTotal = context.getCartTotal();
        return tiers.stream()
            .anyMatch(tier -> cartTotal.compareTo(tier.getMinSpend()) >= 0);
    }
}
```

**Challenge 3: Atomic Coupon Redemption (Prevent Double Use)**
- **Problem**: Prevent same coupon from being used twice concurrently
- **Solution**: Optimistic locking with version field
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Atomic coupon redemption
 */
@Service
public class CouponRedemptionService {
    private final CouponRepository couponRepo;
    private final CouponUsageRepository usageRepo;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Redeem coupon with optimistic locking
     */
    @Transactional
    public CouponRedemption redeemCoupon(String couponCode, String customerId, 
                                         String orderId, CouponContext context) {
        // 1. Fetch coupon with lock
        Coupon coupon = couponRepo.findByCode(couponCode)
            .orElseThrow(() -> new CouponNotFoundException(couponCode));
        
        // 2. Validate eligibility
        ValidationResult validation = validateCoupon(coupon, context);
        if (!validation.isValid()) {
            throw new CouponNotApplicableException(validation.getReason());
        }
        
        // 3. Check usage limit
        if (coupon.getTotalRedemptions() >= coupon.getMaxRedemptions()) {
            throw new CouponUsageLimitExceededException("Coupon fully redeemed");
        }
        
        // 4. Check per-user limit
        int userUsageCount = usageRepo.countByCustomerAndCoupon(customerId, couponCode);
        if (userUsageCount >= coupon.getMaxUsagePerUser()) {
            throw new CouponUsageLimitExceededException("User limit exceeded");
        }
        
        // 5. Calculate discount
        DiscountResult discount = coupon.getDiscountStrategy().calculate(context);
        
        // 6. Increment redemption count (with optimistic lock)
        try {
            coupon.incrementRedemptions();
            couponRepo.save(coupon); // Will fail if version changed
        } catch (OptimisticLockException e) {
            throw new ConcurrentRedemptionException("Coupon modified, retry");
        }
        
        // 7. Record usage
        CouponUsage usage = CouponUsage.builder()
            .id(UUID.randomUUID())
            .couponCode(couponCode)
            .customerId(customerId)
            .orderId(orderId)
            .discountAmount(discount.getDiscountAmount())
            .usedAt(Instant.now())
            .build();
        
        usageRepo.save(usage);
        
        // 8. Publish event
        eventPublisher.publishEvent(new CouponRedeemedEvent(
            couponCode, customerId, orderId, discount.getDiscountAmount()
        ));
        
        // 9. Return redemption result
        return CouponRedemption.builder()
            .couponCode(couponCode)
            .discount(discount)
            .usageId(usage.getId())
            .build();
    }
    
    /**
     * Validate coupon against all specifications
     */
    private ValidationResult validateCoupon(Coupon coupon, CouponContext context) {
        // Check active
        if (!coupon.isActive()) {
            return ValidationResult.invalid("Coupon is inactive");
        }
        
        // Check time range
        Instant now = context.getCurrentTime();
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            return ValidationResult.invalid("Coupon expired or not yet active");
        }
        
        // Check eligibility specification
        if (!coupon.getEligibilitySpec().isSatisfiedBy(context)) {
            return ValidationResult.invalid("Eligibility criteria not met");
        }
        
        // Check discount applicability
        if (!coupon.getDiscountStrategy().isApplicable(context)) {
            return ValidationResult.invalid("Discount not applicable to cart");
        }
        
        return ValidationResult.valid();
    }
}

/**
 * Coupon entity with optimistic locking
 */
@Entity
public class Coupon {
    @Id
    private UUID id;
    
    @Column(unique = true)
    private String code;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private CouponType type; // PERCENTAGE, FIXED, FREE_SHIPPING, BOGO
    
    private boolean active;
    
    private Instant startDate;
    private Instant endDate;
    
    private int maxRedemptions;
    private int totalRedemptions;
    private int maxUsagePerUser;
    
    @Column(name = "stackable")
    private boolean stackable; // Can combine with other coupons
    
    @Transient
    private CouponSpecification eligibilitySpec;
    
    @Transient
    private DiscountStrategy discountStrategy;
    
    @Version
    private long version; // Optimistic locking
    
    /**
     * Increment redemptions atomically
     */
    public void incrementRedemptions() {
        if (totalRedemptions >= maxRedemptions) {
            throw new CouponUsageLimitExceededException();
        }
        this.totalRedemptions++;
    }
}
```

**Challenge 4: Auto-Apply Best Promotions**
- **Problem**: Automatically find and apply best available coupons
- **Solution**: Greedy algorithm to maximize discount
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Auto-apply best promotions
 */
@Service
public class PromotionOptimizerService {
    private final CouponRepository couponRepo;
    private final CouponRedemptionService redemptionService;
    
    /**
     * Find and apply best combination of coupons
     */
    public PromotionResult applyBestPromotions(CouponContext context) {
        // 1. Fetch all applicable coupons
        List<Coupon> applicableCoupons = findApplicableCoupons(context);
        
        if (applicableCoupons.isEmpty()) {
            return PromotionResult.noPromotions();
        }
        
        // 2. Find best combination
        List<Coupon> bestCombination = findBestCombination(applicableCoupons, context);
        
        // 3. Apply coupons
        List<CouponRedemption> redemptions = new ArrayList<>();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (Coupon coupon : bestCombination) {
            try {
                CouponRedemption redemption = redemptionService.redeemCoupon(
                    coupon.getCode(),
                    context.getCustomerId(),
                    context.getOrderId(),
                    context
                );
                
                redemptions.add(redemption);
                totalDiscount = totalDiscount.add(
                    redemption.getDiscount().getDiscountAmount()
                );
                
                // Update context with new total
                context = context.withCartTotal(
                    context.getCartTotal().subtract(
                        redemption.getDiscount().getDiscountAmount()
                    )
                );
                
            } catch (Exception e) {
                // Skip this coupon, continue with others
                logger.warn("Failed to apply coupon {}: {}", 
                          coupon.getCode(), e.getMessage());
            }
        }
        
        return PromotionResult.builder()
            .redemptions(redemptions)
            .totalDiscount(totalDiscount)
            .finalAmount(context.getCartTotal())
            .build();
    }
    
    /**
     * Find all applicable coupons for context
     */
    private List<Coupon> findApplicableCoupons(CouponContext context) {
        List<Coupon> allCoupons = couponRepo.findActive();
        
        return allCoupons.stream()
            .filter(coupon -> coupon.getEligibilitySpec().isSatisfiedBy(context))
            .filter(coupon -> coupon.getDiscountStrategy().isApplicable(context))
            .collect(Collectors.toList());
    }
    
    /**
     * Find best combination using greedy algorithm
     */
    private List<Coupon> findBestCombination(List<Coupon> coupons, 
                                             CouponContext context) {
        // Sort by discount amount (descending)
        List<Coupon> sorted = coupons.stream()
            .sorted(Comparator.comparing(coupon -> 
                calculateDiscount(coupon, context).getDiscountAmount(),
                Comparator.reverseOrder()
            ))
            .collect(Collectors.toList());
        
        List<Coupon> selected = new ArrayList<>();
        CouponContext currentContext = context;
        
        for (Coupon coupon : sorted) {
            // Check if can be added
            if (canCombine(coupon, selected)) {
                DiscountResult discount = coupon.getDiscountStrategy()
                    .calculate(currentContext);
                
                if (discount.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                    selected.add(coupon);
                    
                    // Update context
                    currentContext = currentContext.withCartTotal(
                        currentContext.getCartTotal()
                            .subtract(discount.getDiscountAmount())
                    );
                }
            }
        }
        
        return selected;
    }
    
    /**
     * Check if coupon can be combined with already selected
     */
    private boolean canCombine(Coupon coupon, List<Coupon> selected) {
        // Non-stackable coupons cannot combine
        if (!coupon.isStackable()) {
            return selected.isEmpty();
        }
        
        // Cannot combine with non-stackable coupons
        boolean hasNonStackable = selected.stream()
            .anyMatch(c -> !c.isStackable());
        
        if (hasNonStackable) {
            return false;
        }
        
        // Check custom combination rules
        for (Coupon existing : selected) {
            if (!areCombinationCompatible(coupon, existing)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check custom combination rules
     */
    private boolean areCombinationCompatible(Coupon c1, Coupon c2) {
        // Example: Cannot combine two percentage discounts
        if (c1.getType() == CouponType.PERCENTAGE && 
            c2.getType() == CouponType.PERCENTAGE) {
            return false;
        }
        
        // Example: Can combine free shipping with any other
        if (c1.getType() == CouponType.FREE_SHIPPING || 
            c2.getType() == CouponType.FREE_SHIPPING) {
            return true;
        }
        
        return true;
    }
    
    /**
     * Calculate discount for sorting
     */
    private DiscountResult calculateDiscount(Coupon coupon, CouponContext context) {
        return coupon.getDiscountStrategy().calculate(context);
    }
}
```

**Challenge 5: Observer Pattern for Coupon Events**
- **Problem**: Notify systems when coupons are redeemed (analytics, notifications)
- **Solution**: Observer pattern with event publishing
- **Algorithm**:
```java
/**
 * Coupon events
 */
public class CouponRedeemedEvent {
    private final String couponCode;
    private final String customerId;
    private final String orderId;
    private final BigDecimal discountAmount;
    private final Instant timestamp;
}

public class CouponExpiredEvent {
    private final String couponCode;
    private final Instant timestamp;
}

/**
 * Event listeners
 */
@Component
public class CouponAnalyticsListener {
    
    @EventListener
    public void onCouponRedeemed(CouponRedeemedEvent event) {
        // Track metrics
        metricsService.recordCouponUsage(
            event.getCouponCode(),
            event.getDiscountAmount()
        );
        
        // Update dashboard
        analyticsService.incrementRedemptionCount(event.getCouponCode());
    }
}

@Component
public class CouponNotificationListener {
    
    @EventListener
    @Async
    public void onCouponRedeemed(CouponRedeemedEvent event) {
        // Send confirmation email
        emailService.sendCouponConfirmation(
            event.getCustomerId(),
            event.getCouponCode(),
            event.getDiscountAmount()
        );
    }
}

@Component
public class CouponExpirationListener {
    
    @EventListener
    public void onCouponExpired(CouponExpiredEvent event) {
        // Archive expired coupon
        archiveService.archiveCoupon(event.getCouponCode());
        
        // Notify admin
        adminNotificationService.notifyCouponExpired(event.getCouponCode());
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Specification** | Eligibility rules | Composable, testable criteria |
| **Strategy** | Discount calculation | Swap discount algorithms |
| **Observer** | Coupon events | Decouple event handling |
| **Template Method** | Validation flow | Common steps, variant logic |
| **Factory** | Create coupons from config | Instantiate based on type |
| **Repository** | Data access | Abstract persistence |
| **Optimistic Locking** | Concurrent redemption | Prevent double use |

### 5. Domain Model & Class Structure

```
         Coupon
      - code
      - eligibilitySpec ──────> CouponSpecification
      - discountStrategy ─────> DiscountStrategy
      - version (locking)
            │
            │ uses
            ▼
      CouponUsage
      - customerId
      - orderId
      - usedAt
```

### 6. Detailed Sequence Diagrams

**Sequence: Redeem Coupon**
```
Client  RedemptionSvc  CouponRepo  UsageRepo  EventPublisher
  │          │            │            │            │
  ├─redeem───>│           │            │            │
  │          ├─findByCode─>│           │            │
  │          │<─coupon────┤            │            │
  │          ├─validate────┐            │            │
  │          │<────────────┘            │            │
  │          ├─calculate───┐            │            │
  │          │<────────────┘            │            │
  │          ├─increment───>│           │            │
  │          │<─saved───────┤            │            │
  │          ├─save────────────────────>│            │
  │          │<─usage──────────────────┤            │
  │          ├─publish──────────────────────────────>│
  │<─result──┤            │            │            │
```

### 7. Core Implementation (Interview-Critical Code)

```java
// Context for coupon evaluation
@Value
@Builder(toBuilder = true)
public class CouponContext {
    private String customerId;
    private String orderId;
    private CustomerSegment customerSegment;
    private BigDecimal cartTotal;
    private List<CartItem> cartItems;
    private BigDecimal shippingCost;
    private Instant currentTime;
    private String couponCode;
    
    public CouponContext withCartTotal(BigDecimal newTotal) {
        return this.toBuilder()
                   .cartTotal(newTotal)
                   .build();
    }
}

// Cart item
@Value
public class CartItem {
    private String productId;
    private String categoryId;
    private int quantity;
    private BigDecimal price;
}

// Discount result
@Value
@Builder
public class DiscountResult {
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String description;
    private boolean freeShipping;
}

// Validation result
@Value
public class ValidationResult {
    private boolean valid;
    private String reason;
    
    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }
    
    public static ValidationResult invalid(String reason) {
        return new ValidationResult(false, reason);
    }
}

// Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCode(String code);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true " +
           "AND c.startDate <= :now AND c.endDate >= :now")
    List<Coupon> findActive(@Param("now") Instant now);
}

public interface CouponUsageRepository extends JpaRepository<CouponUsage, UUID> {
    @Query("SELECT COUNT(*) FROM CouponUsage " +
           "WHERE customerId = :customerId AND couponCode = :couponCode")
    int countByCustomerAndCoupon(
        @Param("customerId") String customerId,
        @Param("couponCode") String couponCode
    );
}
```

### 8. Thread Safety & Concurrency

**Optimistic Locking:**
- Version field on Coupon entity
- Retry on OptimisticLockException
- Prevents double redemption

**Concurrent Redemptions:**
- Each redemption in separate transaction
- No shared mutable state
- Atomic increment of redemption count

**Event Publishing:**
- Async listeners (@Async)
- Non-blocking notification
- Eventual consistency acceptable

### 9. Top Interview Questions & Answers

**Q1: Why Specification pattern?**
**A:**
```
Specification allows:
1. Composable rules (AND, OR, NOT)
2. Testable in isolation
3. Reusable across coupons
4. Easy to persist (serialize to JSON)

Example:
MinCartValue(50) AND (VIP OR FirstOrder) AND TimeRange(...)
```

**Q2: How to prevent double redemption?**
**A:**
```java
// Optimistic locking with version
@Entity
public class Coupon {
    @Version
    private long version;
    
    private int totalRedemptions;
    
    public void incrementRedemptions() {
        this.totalRedemptions++;
    }
}

// JPA will throw OptimisticLockException if version changed
try {
    coupon.incrementRedemptions();
    couponRepo.save(coupon);
} catch (OptimisticLockException e) {
    throw new ConcurrentRedemptionException("Retry");
}
```

**Q3: Database schema?**
**A:**
```sql
CREATE TABLE coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    max_redemptions INT DEFAULT 999999,
    total_redemptions INT DEFAULT 0,
    max_usage_per_user INT DEFAULT 1,
    stackable BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0, -- Optimistic locking
    eligibility_spec_json JSON,
    discount_strategy_json JSON,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_active ON coupons(active, start_date, end_date);

CREATE TABLE coupon_usages (
    id UUID PRIMARY KEY,
    coupon_code VARCHAR(50) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    order_id VARCHAR(100) NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL,
    used_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_usage_customer_coupon 
    ON coupon_usages(customer_id, coupon_code);
CREATE INDEX idx_usage_order 
    ON coupon_usages(order_id);
```

**Q4: How to generate unique codes?**
**A:**
```java
public class CouponCodeGenerator {
    
    /**
     * Generate unique alphanumeric code
     */
    public String generateCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }
    
    /**
     * Generate batch of unique codes
     */
    public Set<String> generateBatch(int count, int codeLength) {
        Set<String> codes = new HashSet<>();
        
        while (codes.size() < count) {
            String code = generateCode(codeLength);
            
            // Check uniqueness
            if (!couponRepo.existsByCode(code)) {
                codes.add(code);
            }
        }
        
        return codes;
    }
}
```

**Q5: How to handle stackability?**
**A:**
```java
private boolean canCombine(Coupon newCoupon, List<Coupon> applied) {
    // If new coupon is non-stackable, must be first
    if (!newCoupon.isStackable()) {
        return applied.isEmpty();
    }
    
    // Cannot add if already have non-stackable
    if (applied.stream().anyMatch(c -> !c.isStackable())) {
        return false;
    }
    
    // Check custom rules (stored in DB)
    return checkCombinationRules(newCoupon, applied);
}
```

**Q6: Performance optimization?**
**A:**
```java
// 1. Cache active coupons
@Cacheable(value = "active-coupons", ttl = 300)
public List<Coupon> getActiveCoupons() {
    return couponRepo.findActive(Instant.now());
}

// 2. Lazy evaluation of specifications
class LazySpecification extends BaseCouponSpecification {
    private Supplier<Boolean> evaluator;
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        return evaluator.get();
    }
}

// 3. Batch load coupon usages
Map<String, Integer> usageCounts = usageRepo
    .findByCustomer(customerId)
    .stream()
    .collect(Collectors.groupingBy(
        CouponUsage::getCouponCode,
        Collectors.counting()
    ));
```

**Q7: How to test combinations?**
**A:**
```java
@Test
public void testCouponCombination() {
    Coupon stackable1 = createPercentageCoupon("SAVE10", 0.10, true);
    Coupon stackable2 = createFixedCoupon("OFF5", new BigDecimal("5"), true);
    Coupon exclusive = createPercentageCoupon("VIP20", 0.20, false);
    
    CouponContext context = CouponContext.builder()
        .cartTotal(new BigDecimal("100"))
        .build();
    
    // Can combine stackable coupons
    assertTrue(canCombine(stackable2, List.of(stackable1)));
    
    // Cannot add non-stackable if others present
    assertFalse(canCombine(exclusive, List.of(stackable1)));
    
    // Can use exclusive alone
    assertTrue(canCombine(exclusive, List.of()));
}
```

**Q8: How to schedule expiration?**
**A:**
```java
@Scheduled(cron = "0 0 * * * *") // Every hour
public void expireCoupons() {
    Instant now = Instant.now();
    
    List<Coupon> expired = couponRepo.findExpired(now);
    
    for (Coupon coupon : expired) {
        coupon.setActive(false);
        couponRepo.save(coupon);
        
        eventPublisher.publishEvent(
            new CouponExpiredEvent(coupon.getCode(), now)
        );
    }
}
```

**Q9: What metrics to track?**
**A:**
```
KPIs:
1. Redemption rate (redemptions / impressions)
2. Average discount per order
3. Coupon ROI (revenue / discount given)
4. Popular coupons (most redeemed)
5. Validation latency

Alerts:
- Coupon near usage limit (> 90%)
- Validation latency > 20ms
- High rejection rate (> 50%)
```

**Q10: How to A/B test coupons?**
**A:**
```java
class ABTestCouponSpecification extends BaseCouponSpecification {
    private final CouponSpecification baseSpec;
    private final double testPercentage; // 0.10 = 10% see test
    
    @Override
    public boolean isSatisfiedBy(CouponContext context) {
        // Check base eligibility first
        if (!baseSpec.isSatisfiedBy(context)) {
            return false;
        }
        
        // Assign to test group based on hash
        int hash = context.getCustomerId().hashCode();
        return (Math.abs(hash) % 100) < (testPercentage * 100);
    }
}
```

### 10. Extensions & Variations

1. **Referral Coupons**: Give coupon to referrer and referee
2. **Loyalty Integration**: Convert points to coupons
3. **Dynamic Discounts**: Adjust based on inventory/demand
4. **Geo-Targeted**: Coupons for specific locations
5. **Personalized**: ML-based coupon recommendations

### 11. Testing Strategy

**Unit Tests:**
- Specification composition
- Strategy calculations
- Combination logic

**Integration Tests:**
- Full redemption flow
- Concurrent redemptions
- Event publishing

**Performance Tests:**
- 10K redemptions/sec
- 10ms validation latency
- Cache effectiveness

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: No version field (double redemption)
✅ **Do**: Optimistic locking

❌ **Avoid**: Hardcoded eligibility rules
✅ **Do**: Specification pattern

❌ **Avoid**: Single strategy for all discounts
✅ **Do**: Strategy pattern per type

❌ **Avoid**: Synchronous event handling
✅ **Do**: Async observers

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Validate coupon | O(S) | O(1) | S = specifications |
| Calculate discount | O(I) | O(1) | I = cart items |
| Find best combination | O(C²) | O(C) | C = applicable coupons |
| Redeem coupon | O(1) | O(1) | Single DB write |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Specification Pattern** | 30% | Composable eligibility rules |
| **Strategy Pattern** | 25% | Different discount types |
| **Atomicity** | 20% | Optimistic locking, no double use |
| **Combination Logic** | 15% | Stackability, best selection |
| **Real-world Awareness** | 10% | Events, metrics, A/B testing |

**Red Flags:**
- No double-redemption prevention
- Hardcoded business rules
- No audit trail
- Ignoring concurrent access
- Missing stackability logic

---
