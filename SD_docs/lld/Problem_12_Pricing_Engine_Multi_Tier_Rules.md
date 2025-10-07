# Problem 12: Pricing Engine with Multi-Tier Rules (Chain of Responsibility + Strategy)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a flexible pricing engine that applies multiple pricing rules in sequence (base price → discounts → taxes → shipping), supporting rule priority, combinability, and audit trail.

**Assumptions / Scope:**
- Multiple pricing rule types (percentage discount, fixed discount, BOGO, tiered pricing)
- Rules have priority and can be chained
- Support rule combinability constraints (can't stack certain discounts)
- Apply in sequence: Base → Discounts → Loyalty → Taxes → Shipping
- Track calculation breakdown for transparency
- Scale: 10K rules, 5K price calculations/sec
- Out of scope: Dynamic pricing based on demand, competitor pricing

**Non-Functional Goals:**
- Calculate price in < 20ms
- Support 5K concurrent calculations
- 100% audit trail (how price was calculated)
- Extensible (add new rule types easily)
- Deterministic (same input → same output)

### 2. Core Requirements

**Functional:**
- Define pricing rules with conditions (cart value, customer segment, product category)
- Chain rules in priority order
- Apply percentage/fixed discounts
- Support tiered pricing (bulk discounts)
- Apply Buy-One-Get-One (BOGO) offers
- Add taxes (sales tax, VAT) based on location
- Calculate shipping cost based on weight/distance
- Track price breakdown (base, discounts applied, taxes, shipping)
- Prevent invalid rule combinations (e.g., two "exclusive" discounts)
- Support A/B testing (10% see Rule A, 90% see Rule B)

**Non-Functional:**
- **Performance**: < 20ms calculation, 5K req/sec
- **Auditability**: Full price calculation trail
- **Extensibility**: Add new rule types without changing core
- **Consistency**: Same input → same price
- **Observability**: Track rule application rates

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Chain of Responsibility for Rule Application**
- **Problem**: Apply rules in sequence, each potentially modifying price
- **Solution**: Chain of Responsibility with immutable PriceContext
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Chain of Responsibility for pricing rules
 */
abstract class PricingRule {
    protected PricingRule next;
    protected int priority; // Lower = earlier
    protected String ruleId;
    
    public void setNext(PricingRule next) {
        this.next = next;
    }
    
    /**
     * Process price and pass to next rule
     */
    public PriceContext handle(PriceContext context) {
        // Check if rule applies
        if (!isApplicable(context)) {
            return passToNext(context);
        }
        
        // Apply rule
        PriceContext updatedContext = apply(context);
        
        // Check combinability
        if (!isCombinableWith(updatedContext)) {
            // Skip remaining rules
            return updatedContext;
        }
        
        // Pass to next rule
        return passToNext(updatedContext);
    }
    
    /**
     * Check if rule applies to this context
     */
    protected abstract boolean isApplicable(PriceContext context);
    
    /**
     * Apply pricing logic
     */
    protected abstract PriceContext apply(PriceContext context);
    
    /**
     * Check if can be combined with already applied rules
     */
    protected boolean isCombinableWith(PriceContext context) {
        // Check for exclusive rules
        for (AppliedRule appliedRule : context.getAppliedRules()) {
            if (appliedRule.isExclusive() || this.isExclusive()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Pass to next rule in chain
     */
    protected PriceContext passToNext(PriceContext context) {
        if (next != null) {
            return next.handle(context);
        }
        return context;
    }
    
    protected abstract boolean isExclusive();
}

/**
 * Percentage discount rule
 */
class PercentageDiscountRule extends PricingRule {
    private final double percentage; // 0.10 = 10%
    private final RuleCondition condition;
    private final boolean exclusive;
    
    public PercentageDiscountRule(String ruleId, double percentage, 
                                  RuleCondition condition, boolean exclusive) {
        this.ruleId = ruleId;
        this.percentage = percentage;
        this.condition = condition;
        this.exclusive = exclusive;
    }
    
    @Override
    protected boolean isApplicable(PriceContext context) {
        return condition.evaluate(context);
    }
    
    /**
     * INTERVIEW CRITICAL: Apply percentage discount
     */
    @Override
    protected PriceContext apply(PriceContext context) {
        BigDecimal currentPrice = context.getCurrentPrice();
        BigDecimal discount = currentPrice.multiply(BigDecimal.valueOf(percentage));
        BigDecimal newPrice = currentPrice.subtract(discount);
        
        // Record rule application
        AppliedRule appliedRule = AppliedRule.builder()
            .ruleId(ruleId)
            .ruleType("PERCENTAGE_DISCOUNT")
            .description(String.format("%.0f%% discount", percentage * 100))
            .originalAmount(currentPrice)
            .discountAmount(discount)
            .finalAmount(newPrice)
            .exclusive(exclusive)
            .build();
        
        return context.withPrice(newPrice)
                     .addAppliedRule(appliedRule);
    }
    
    @Override
    protected boolean isExclusive() {
        return exclusive;
    }
}

/**
 * Fixed discount rule
 */
class FixedDiscountRule extends PricingRule {
    private final BigDecimal discountAmount;
    private final RuleCondition condition;
    private final boolean exclusive;
    
    @Override
    protected boolean isApplicable(PriceContext context) {
        return condition.evaluate(context) && 
               context.getCurrentPrice().compareTo(discountAmount) > 0;
    }
    
    @Override
    protected PriceContext apply(PriceContext context) {
        BigDecimal currentPrice = context.getCurrentPrice();
        BigDecimal newPrice = currentPrice.subtract(discountAmount);
        
        // Ensure price doesn't go negative
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            newPrice = BigDecimal.ZERO;
        }
        
        AppliedRule appliedRule = AppliedRule.builder()
            .ruleId(ruleId)
            .ruleType("FIXED_DISCOUNT")
            .description("$" + discountAmount + " off")
            .originalAmount(currentPrice)
            .discountAmount(currentPrice.subtract(newPrice))
            .finalAmount(newPrice)
            .exclusive(exclusive)
            .build();
        
        return context.withPrice(newPrice)
                     .addAppliedRule(appliedRule);
    }
    
    @Override
    protected boolean isExclusive() {
        return exclusive;
    }
}

/**
 * Tiered pricing rule (bulk discounts)
 */
class TieredPricingRule extends PricingRule {
    private final List<PriceTier> tiers;
    private final RuleCondition condition;
    
    /**
     * INTERVIEW CRITICAL: Apply tiered pricing
     */
    @Override
    protected PriceContext apply(PriceContext context) {
        int quantity = context.getQuantity();
        
        // Find applicable tier
        PriceTier applicableTier = tiers.stream()
            .filter(tier -> quantity >= tier.getMinQuantity() && 
                          quantity <= tier.getMaxQuantity())
            .findFirst()
            .orElseThrow();
        
        BigDecimal unitPrice = applicableTier.getUnitPrice();
        BigDecimal newPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        AppliedRule appliedRule = AppliedRule.builder()
            .ruleId(ruleId)
            .ruleType("TIERED_PRICING")
            .description(String.format("Tier: %d-%d units @ $%.2f/unit",
                                     applicableTier.getMinQuantity(),
                                     applicableTier.getMaxQuantity(),
                                     unitPrice))
            .originalAmount(context.getCurrentPrice())
            .discountAmount(context.getCurrentPrice().subtract(newPrice))
            .finalAmount(newPrice)
            .build();
        
        return context.withPrice(newPrice)
                     .addAppliedRule(appliedRule);
    }
    
    @Override
    protected boolean isApplicable(PriceContext context) {
        return condition.evaluate(context);
    }
    
    @Override
    protected boolean isExclusive() {
        return false;
    }
}

/**
 * Buy One Get One (BOGO) rule
 */
class BuyOneGetOneRule extends PricingRule {
    private final int buyQuantity;
    private final int getQuantity;
    private final double getDiscount; // 1.0 = free, 0.5 = 50% off
    private final RuleCondition condition;
    
    /**
     * INTERVIEW CRITICAL: Apply BOGO logic
     */
    @Override
    protected PriceContext apply(PriceContext context) {
        int totalQuantity = context.getQuantity();
        BigDecimal unitPrice = context.getBasePrice().divide(
            BigDecimal.valueOf(totalQuantity), 
            RoundingMode.HALF_UP
        );
        
        // Calculate how many "sets" of BOGO apply
        int sets = totalQuantity / (buyQuantity + getQuantity);
        int remainingItems = totalQuantity % (buyQuantity + getQuantity);
        
        // Calculate discount
        BigDecimal fullPriceItems = BigDecimal.valueOf(sets * buyQuantity + remainingItems);
        BigDecimal discountedItems = BigDecimal.valueOf(sets * getQuantity);
        BigDecimal discountPerItem = unitPrice.multiply(BigDecimal.valueOf(getDiscount));
        
        BigDecimal totalDiscount = discountedItems.multiply(discountPerItem);
        BigDecimal newPrice = context.getCurrentPrice().subtract(totalDiscount);
        
        AppliedRule appliedRule = AppliedRule.builder()
            .ruleId(ruleId)
            .ruleType("BOGO")
            .description(String.format("Buy %d Get %d @ %.0f%% off",
                                     buyQuantity, getQuantity, getDiscount * 100))
            .originalAmount(context.getCurrentPrice())
            .discountAmount(totalDiscount)
            .finalAmount(newPrice)
            .build();
        
        return context.withPrice(newPrice)
                     .addAppliedRule(appliedRule);
    }
    
    @Override
    protected boolean isApplicable(PriceContext context) {
        return context.getQuantity() >= (buyQuantity + getQuantity) &&
               condition.evaluate(context);
    }
    
    @Override
    protected boolean isExclusive() {
        return true; // BOGO typically exclusive
    }
}

/**
 * Tax rule
 */
class TaxRule extends PricingRule {
    private final TaxCalculator taxCalculator;
    
    /**
     * INTERVIEW CRITICAL: Calculate tax based on location
     */
    @Override
    protected PriceContext apply(PriceContext context) {
        String zipCode = context.getShippingAddress().getZipCode();
        BigDecimal taxableAmount = context.getCurrentPrice();
        
        TaxRate taxRate = taxCalculator.getTaxRate(zipCode);
        BigDecimal taxAmount = taxableAmount.multiply(taxRate.getRate());
        BigDecimal newPrice = taxableAmount.add(taxAmount);
        
        AppliedRule appliedRule = AppliedRule.builder()
            .ruleId(ruleId)
            .ruleType("TAX")
            .description(String.format("Sales Tax (%.2f%%)", taxRate.getRate().multiply(BigDecimal.valueOf(100))))
            .originalAmount(taxableAmount)
            .discountAmount(BigDecimal.ZERO)
            .finalAmount(newPrice)
            .additionalInfo(Map.of("taxAmount", taxAmount))
            .build();
        
        return context.withPrice(newPrice)
                     .addAppliedRule(appliedRule);
    }
    
    @Override
    protected boolean isApplicable(PriceContext context) {
        return context.getShippingAddress() != null;
    }
    
    @Override
    protected boolean isExclusive() {
        return false;
    }
}

/**
 * Shipping cost rule
 */
class ShippingCostRule extends PricingRule {
    private final ShippingCalculator shippingCalculator;
    
    @Override
    protected PriceContext apply(PriceContext context) {
        double weight = context.getTotalWeight();
        String zipCode = context.getShippingAddress().getZipCode();
        
        BigDecimal shippingCost = shippingCalculator.calculate(weight, zipCode);
        BigDecimal newPrice = context.getCurrentPrice().add(shippingCost);
        
        AppliedRule appliedRule = AppliedRule.builder()
            .ruleId(ruleId)
            .ruleType("SHIPPING")
            .description(String.format("Shipping (%.1f kg to %s)", weight, zipCode))
            .originalAmount(context.getCurrentPrice())
            .discountAmount(BigDecimal.ZERO)
            .finalAmount(newPrice)
            .additionalInfo(Map.of("shippingCost", shippingCost))
            .build();
        
        return context.withPrice(newPrice)
                     .addAppliedRule(appliedRule);
    }
    
    @Override
    protected boolean isApplicable(PriceContext context) {
        return context.getShippingAddress() != null;
    }
    
    @Override
    protected boolean isExclusive() {
        return false;
    }
}
```

**Challenge 2: Immutable Price Context**
- **Problem**: Thread-safe price calculation with audit trail
- **Solution**: Immutable context object
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Immutable price context
 */
@Value
@Builder(toBuilder = true)
public class PriceContext {
    private final UUID contextId;
    private final BigDecimal basePrice;
    private final BigDecimal currentPrice;
    private final int quantity;
    private final String productId;
    private final String customerId;
    private final CustomerSegment customerSegment; // VIP, REGULAR, NEW
    private final Address shippingAddress;
    private final double totalWeight; // kg
    private final Instant calculationTime;
    private final List<AppliedRule> appliedRules;
    
    /**
     * Create new context with updated price
     */
    public PriceContext withPrice(BigDecimal newPrice) {
        return this.toBuilder()
                   .currentPrice(newPrice)
                   .build();
    }
    
    /**
     * Add applied rule to audit trail
     */
    public PriceContext addAppliedRule(AppliedRule rule) {
        List<AppliedRule> updatedRules = new ArrayList<>(this.appliedRules);
        updatedRules.add(rule);
        
        return this.toBuilder()
                   .appliedRules(updatedRules)
                   .build();
    }
    
    /**
     * Get total discount applied
     */
    public BigDecimal getTotalDiscount() {
        return basePrice.subtract(currentPrice);
    }
    
    /**
     * Get price breakdown for display
     */
    public PriceBreakdown getBreakdown() {
        return PriceBreakdown.builder()
            .basePrice(basePrice)
            .discounts(appliedRules.stream()
                .filter(r -> r.getRuleType().contains("DISCOUNT") || 
                           r.getRuleType().equals("BOGO"))
                .collect(Collectors.toList()))
            .taxes(appliedRules.stream()
                .filter(r -> r.getRuleType().equals("TAX"))
                .collect(Collectors.toList()))
            .shipping(appliedRules.stream()
                .filter(r -> r.getRuleType().equals("SHIPPING"))
                .findFirst()
                .orElse(null))
            .finalPrice(currentPrice)
            .build();
    }
}

/**
 * Applied rule for audit trail
 */
@Value
@Builder
public class AppliedRule {
    private String ruleId;
    private String ruleType;
    private String description;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private boolean exclusive;
    private Map<String, Object> additionalInfo;
    private Instant appliedAt;
}
```

**Challenge 3: Rule Condition Specification Pattern**
- **Problem**: Flexible rule conditions (cart > $100, customer = VIP, product = Electronics)
- **Solution**: Specification pattern for composable conditions
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Specification pattern for rule conditions
 */
interface RuleCondition {
    boolean evaluate(PriceContext context);
    RuleCondition and(RuleCondition other);
    RuleCondition or(RuleCondition other);
    RuleCondition not();
}

/**
 * Base specification
 */
abstract class BaseCondition implements RuleCondition {
    
    @Override
    public RuleCondition and(RuleCondition other) {
        return new AndCondition(this, other);
    }
    
    @Override
    public RuleCondition or(RuleCondition other) {
        return new OrCondition(this, other);
    }
    
    @Override
    public RuleCondition not() {
        return new NotCondition(this);
    }
}

/**
 * Composite conditions
 */
class AndCondition extends BaseCondition {
    private final RuleCondition left;
    private final RuleCondition right;
    
    public AndCondition(RuleCondition left, RuleCondition right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean evaluate(PriceContext context) {
        return left.evaluate(context) && right.evaluate(context);
    }
}

class OrCondition extends BaseCondition {
    private final RuleCondition left;
    private final RuleCondition right;
    
    public OrCondition(RuleCondition left, RuleCondition right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean evaluate(PriceContext context) {
        return left.evaluate(context) || right.evaluate(context);
    }
}

class NotCondition extends BaseCondition {
    private final RuleCondition condition;
    
    public NotCondition(RuleCondition condition) {
        this.condition = condition;
    }
    
    @Override
    public boolean evaluate(PriceContext context) {
        return !condition.evaluate(context);
    }
}

/**
 * Concrete conditions
 */
class MinCartValueCondition extends BaseCondition {
    private final BigDecimal minValue;
    
    @Override
    public boolean evaluate(PriceContext context) {
        return context.getBasePrice().compareTo(minValue) >= 0;
    }
}

class CustomerSegmentCondition extends BaseCondition {
    private final CustomerSegment requiredSegment;
    
    @Override
    public boolean evaluate(PriceContext context) {
        return context.getCustomerSegment() == requiredSegment;
    }
}

class ProductCategoryCondition extends BaseCondition {
    private final String categoryId;
    private final ProductRepository productRepo;
    
    @Override
    public boolean evaluate(PriceContext context) {
        Product product = productRepo.findById(context.getProductId()).orElseThrow();
        return product.getCategoryId().equals(categoryId);
    }
}

class TimeRangeCondition extends BaseCondition {
    private final LocalTime startTime;
    private final LocalTime endTime;
    
    @Override
    public boolean evaluate(PriceContext context) {
        LocalTime now = context.getCalculationTime().atZone(ZoneId.systemDefault()).toLocalTime();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }
}

// Usage example
RuleCondition condition = new MinCartValueCondition(new BigDecimal("100"))
    .and(new CustomerSegmentCondition(CustomerSegment.VIP))
    .or(new ProductCategoryCondition("electronics-uuid"));

if (condition.evaluate(context)) {
    // Apply rule
}
```

**Challenge 4: Rule Priority and Builder**
- **Problem**: Build complex rule chains with correct priority
- **Solution**: Builder pattern with priority sorting
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Build pricing rule chain
 */
class PricingRuleChainBuilder {
    private final List<PricingRule> rules = new ArrayList<>();
    
    public PricingRuleChainBuilder addRule(PricingRule rule) {
        rules.add(rule);
        return this;
    }
    
    /**
     * Build chain sorted by priority
     */
    public PricingRule build() {
        if (rules.isEmpty()) {
            throw new IllegalStateException("No rules added");
        }
        
        // Sort by priority (lower = earlier)
        rules.sort(Comparator.comparingInt(r -> r.priority));
        
        // Chain rules
        for (int i = 0; i < rules.size() - 1; i++) {
            rules.get(i).setNext(rules.get(i + 1));
        }
        
        return rules.get(0); // Return first rule in chain
    }
}

// Usage
PricingRule chain = new PricingRuleChainBuilder()
    // Discounts (priority 1-10)
    .addRule(new PercentageDiscountRule("vip-discount", 0.15, 
             new CustomerSegmentCondition(VIP), true)
             .setPriority(1))
    .addRule(new FixedDiscountRule("cart-discount", new BigDecimal("10"),
             new MinCartValueCondition(new BigDecimal("100")), false)
             .setPriority(2))
    .addRule(new TieredPricingRule("bulk-discount", bulkTiers, alwaysTrue())
             .setPriority(3))
    // Tax (priority 20)
    .addRule(new TaxRule(taxCalculator).setPriority(20))
    // Shipping (priority 30)
    .addRule(new ShippingCostRule(shippingCalculator).setPriority(30))
    .build();

// Calculate price
PriceContext result = chain.handle(initialContext);
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Chain of Responsibility** | Rule application sequence | Pass context through rules |
| **Strategy** | Different rule types (%, fixed, BOGO) | Swap calculation logic |
| **Specification** | Rule conditions | Composable criteria |
| **Builder** | Construct rule chain | Complex object creation |
| **Immutable Object** | PriceContext | Thread-safe, audit trail |
| **Template Method** | Base PricingRule class | Common flow, variant steps |
| **Factory** | Create rules from configuration | Instantiate based on type |

### 5. Domain Model & Class Structure

```
           PricingRule (Abstract)
                   ▲
                   │
      ┌────────────┼────────────┬──────────────┐
      │            │            │              │
PercentageRule  FixedRule  TieredRule     BOGORule
      │            │            │              │
      └────────────┴────────────┴──────────────┘
                   │
              uses │
                   ▼
            RuleCondition (Interface)
                   ▲
                   │
      ┌────────────┼────────────┬──────────────┐
      │            │            │              │
 MinCartValue CustomerSegment ProductCategory TimeRange
```

### 6. Detailed Sequence Diagrams

**Sequence: Calculate Price**
```
Client  PricingService  RuleChain  PercentageRule  TaxRule  ShippingRule
  │          │             │            │            │          │
  ├─calculate─>│           │            │            │          │
  │          ├─build chain─>│            │            │          │
  │          ├─handle──────>│            │            │          │
  │          │             ├─handle─────>│            │          │
  │          │             │<─context────┤            │          │
  │          │             ├─handle──────────────────>│          │
  │          │             │<─context──────────────────┤          │
  │          │             ├─handle──────────────────────────────>│
  │          │             │<─context───────────────────────────┤
  │          │<─result─────┤            │            │          │
  │<─breakdown─┤           │            │            │          │
```

### 7. Core Implementation (Interview-Critical Code)

```java
// Main pricing service
@Service
public class PricingService {
    private final RuleRepository ruleRepo;
    private final PricingRuleChainBuilder chainBuilder;
    
    /**
     * INTERVIEW CRITICAL: Calculate final price
     */
    public PriceBreakdown calculatePrice(PriceRequest request) {
        // Build initial context
        PriceContext initialContext = PriceContext.builder()
            .contextId(UUID.randomUUID())
            .basePrice(request.getBasePrice())
            .currentPrice(request.getBasePrice())
            .quantity(request.getQuantity())
            .productId(request.getProductId())
            .customerId(request.getCustomerId())
            .customerSegment(getCustomerSegment(request.getCustomerId()))
            .shippingAddress(request.getShippingAddress())
            .totalWeight(request.getTotalWeight())
            .calculationTime(Instant.now())
            .appliedRules(new ArrayList<>())
            .build();
        
        // Load applicable rules
        List<PricingRule> rules = loadApplicableRules(request);
        
        // Build chain
        PricingRule chain = chainBuilder
            .addAll(rules)
            .build();
        
        // Execute chain
        PriceContext finalContext = chain.handle(initialContext);
        
        // Persist calculation
        persistCalculation(finalContext);
        
        return finalContext.getBreakdown();
    }
    
    /**
     * Load rules applicable to this request
     */
    private List<PricingRule> loadApplicableRules(PriceRequest request) {
        // Load from database or cache
        return ruleRepo.findActive().stream()
            .map(this::toRuleInstance)
            .collect(Collectors.toList());
    }
}

// Price tier for bulk discounts
@Value
public class PriceTier {
    private int minQuantity;
    private int maxQuantity;
    private BigDecimal unitPrice;
}

// Tax calculator
public interface TaxCalculator {
    TaxRate getTaxRate(String zipCode);
}

@Value
public class TaxRate {
    private BigDecimal rate; // 0.08 = 8%
    private String jurisdiction;
}

// Shipping calculator
public interface ShippingCalculator {
    BigDecimal calculate(double weight, String zipCode);
}
```

### 8. Thread Safety & Concurrency

**Immutable Context:**
- No shared mutable state
- Each calculation independent
- Thread-safe by design

**Rule Caching:**
- Cache active rules
- Invalidate on update
- ReadWriteLock for rule cache

**Concurrent Calculations:**
- Stateless service
- No blocking operations
- Horizontal scaling

### 9. Top Interview Questions & Answers

**Q1: Why Chain of Responsibility?**
**A:**
```
CoR allows:
1. Dynamic rule composition
2. Early termination (exclusive rules)
3. Audit trail (see which rules applied)
4. Easy to add/remove rules

Alternative (rejected): Strategy pattern
→ Can't easily chain multiple strategies
```

**Q2: How to handle exclusive rules?**
**A:**
```java
@Override
protected boolean isCombinableWith(PriceContext context) {
    // Check if any already-applied rule is exclusive
    boolean hasExclusive = context.getAppliedRules().stream()
        .anyMatch(AppliedRule::isExclusive);
    
    if (hasExclusive || this.isExclusive()) {
        return false; // Stop chain
    }
    
    return true;
}
```

**Q3: Database schema?**
**A:**
```sql
CREATE TABLE pricing_rules (
    id UUID PRIMARY KEY,
    rule_type VARCHAR(50) NOT NULL, -- PERCENTAGE, FIXED, BOGO, TAX
    priority INT NOT NULL,
    exclusive BOOLEAN DEFAULT FALSE,
    condition_json JSON NOT NULL, -- Serialized RuleCondition
    parameters_json JSON NOT NULL, -- Rule-specific params
    active BOOLEAN DEFAULT TRUE,
    start_date TIMESTAMP,
    end_date TIMESTAMP
);

CREATE INDEX idx_rules_active_priority 
    ON pricing_rules(active, priority) 
    WHERE active = TRUE;

CREATE TABLE price_calculations (
    id UUID PRIMARY KEY,
    context_id UUID NOT NULL,
    customer_id VARCHAR(100),
    product_id VARCHAR(100),
    base_price DECIMAL(10, 2),
    final_price DECIMAL(10, 2),
    applied_rules JSON, -- Audit trail
    calculated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_calculations_customer 
    ON price_calculations(customer_id, calculated_at);
```

**Q4: How to test rule chain?**
**A:**
```java
@Test
public void testRuleChain() {
    // Setup
    PriceContext context = PriceContext.builder()
        .basePrice(new BigDecimal("100"))
        .currentPrice(new BigDecimal("100"))
        .customerSegment(CustomerSegment.VIP)
        .build();
    
    // Build chain
    PricingRule chain = new PricingRuleChainBuilder()
        .addRule(new PercentageDiscountRule("vip", 0.10, 
                 new CustomerSegmentCondition(VIP), false))
        .addRule(new TaxRule(taxCalc))
        .build();
    
    // Execute
    PriceContext result = chain.handle(context);
    
    // Verify
    assertEquals(new BigDecimal("90"), result.getCurrentPriceBeforeTax());
    assertEquals(2, result.getAppliedRules().size());
    assertTrue(result.getAppliedRules().get(0).getRuleType().equals("PERCENTAGE_DISCOUNT"));
}
```

**Q5: How to A/B test rules?**
**A:**
```java
class ABTestRule extends PricingRule {
    private final PricingRule ruleA;
    private final PricingRule ruleB;
    private final double aPercentage; // 0.10 = 10% see A
    
    @Override
    protected PriceContext apply(PriceContext context) {
        // Hash customer ID to deterministically assign variant
        int hash = context.getCustomerId().hashCode();
        boolean showA = (hash % 100) < (aPercentage * 100);
        
        PricingRule selectedRule = showA ? ruleA : ruleB;
        return selectedRule.handle(context);
    }
}
```

**Q6: Performance optimization?**
**A:**
```java
// 1. Cache active rules
@Cacheable(value = "pricing-rules")
public List<PricingRule> getActiveRules() {
    return ruleRepo.findActive();
}

// 2. Pre-compile conditions
// Instead of parsing JSON every time, compile once
Map<String, RuleCondition> compiledConditions = new ConcurrentHashMap<>();

// 3. Async audit logging
CompletableFuture.runAsync(() -> 
    auditRepo.save(priceCalculation)
);

// 4. Connection pooling for tax/shipping lookups
HikariDataSource dataSource = new HikariDataSource();
```

**Q7: How to handle rule conflicts?**
**A:**
```java
// Define priority explicitly
public enum RulePriority {
    TIERED_PRICING(1),     // First
    PERCENTAGE_DISCOUNT(2),
    FIXED_DISCOUNT(3),
    BOGO(4),
    TAX(20),               // Late
    SHIPPING(30);          // Last
}

// Detect conflicts
if (hasConflict(newRule, existingRules)) {
    throw new RuleConflictException("Rule conflicts with: " + conflicting);
}
```

**Q8: How to explain price to customer?**
**A:**
```java
public String formatBreakdown(PriceBreakdown breakdown) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("Base Price: $%.2f\n", breakdown.getBasePrice()));
    
    for (AppliedRule discount : breakdown.getDiscounts()) {
        sb.append(String.format("  - %s: -$%.2f\n", 
                               discount.getDescription(),
                               discount.getDiscountAmount()));
    }
    
    sb.append(String.format("Subtotal: $%.2f\n", breakdown.getSubtotal()));
    
    for (AppliedRule tax : breakdown.getTaxes()) {
        BigDecimal taxAmount = (BigDecimal) tax.getAdditionalInfo().get("taxAmount");
        sb.append(String.format("  + %s: +$%.2f\n", 
                               tax.getDescription(),
                               taxAmount));
    }
    
    if (breakdown.getShipping() != null) {
        BigDecimal shippingCost = (BigDecimal) breakdown.getShipping()
            .getAdditionalInfo().get("shippingCost");
        sb.append(String.format("  + Shipping: +$%.2f\n", shippingCost));
    }
    
    sb.append(String.format("Total: $%.2f", breakdown.getFinalPrice()));
    
    return sb.toString();
}
```

**Q9: What metrics to track?**
**A:**
```
KPIs:
1. Calculation latency (p50, p95, p99)
2. Rule application rate (which rules used most)
3. Average discount per order
4. Exclusive rule hit rate
5. A/B test conversion rates

Alerts:
- Calculation latency > 50ms
- Rule conflict detected
- Negative price after discount
```

**Q10: How to version rules?**
**A:**
```java
@Entity
public class PricingRuleVersion {
    @Id
    private UUID id;
    
    private UUID ruleId; // Logical rule ID
    private int version;
    private RuleType type;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String conditionJson;
    private String parametersJson;
}

// Load rule for specific time
public PricingRule getRuleAt(UUID ruleId, Instant time) {
    PricingRuleVersion version = ruleRepo.findByRuleIdAndTime(ruleId, time);
    return parseRule(version);
}
```

### 10. Extensions & Variations

1. **Dynamic Pricing**: Adjust based on demand/inventory
2. **ML-Based Rules**: Personalized discounts based on propensity model
3. **Multi-Currency**: Support price calculation in different currencies
4. **Subscription Pricing**: Recurring discounts for subscribers
5. **Bundle Pricing**: Discount for product combinations

### 11. Testing Strategy

**Unit Tests:**
- Each rule type in isolation
- Condition specification composition
- Immutable context operations

**Integration Tests:**
- Full chain execution
- Rule combinability
- Audit trail correctness

**Performance Tests:**
- 5K calculations/sec
- 20ms latency p95
- Cache effectiveness

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Mutable context (thread-unsafe)
✅ **Do**: Immutable context with new instances

❌ **Avoid**: Hardcoded rule order
✅ **Do**: Priority-based sorting

❌ **Avoid**: Complex nested if-else for rules
✅ **Do**: Chain of Responsibility

❌ **Avoid**: Synchronous audit logging
✅ **Do**: Async event-driven logging

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Calculate price | O(R) | O(R) | R = number of rules |
| Build chain | O(R log R) | O(R) | Sort by priority |
| Evaluate condition | O(1) | O(1) | Simple comparisons |
| Format breakdown | O(R) | O(R) | Iterate applied rules |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Chain of Responsibility** | 30% | Correct pattern usage, passing context |
| **Immutability** | 25% | Thread-safe context design |
| **Specification Pattern** | 20% | Composable conditions |
| **Audit Trail** | 15% | Track all rule applications |
| **Real-world Awareness** | 10% | Exclusive rules, priority, A/B testing |

**Red Flags:**
- Mutable shared state
- No audit trail
- Hardcoded rule logic
- No exclusive rule handling
- Ignoring rule priority

---
