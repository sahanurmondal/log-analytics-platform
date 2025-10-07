# Problem 14: Multi-Tenant SaaS Subscription Manager (Strategy + Template Method + State)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a subscription management system for multi-tenant SaaS supporting multiple plans, billing cycles, upgrades/downgrades, prorations, and usage-based billing.

**Assumptions / Scope:**
- Multiple tenants (companies) with subscriptions
- Multiple plan tiers (Free, Basic, Pro, Enterprise)
- Billing cycles (monthly, annual, custom)
- Upgrades/downgrades with proration
- Usage-based add-ons (API calls, storage)
- Trial periods with auto-conversion
- Payment failures and retry logic
- Scale: 100K tenants, 10K subscription changes/day
- Out of scope: Payment gateway integration details, tax calculation

**Non-Functional Goals:**
- Process subscription change in < 100ms
- Handle 10K billing events/day
- 100% billing accuracy (no double charge)
- Audit trail for all changes
- Support dunning management (failed payments)

### 2. Core Requirements

**Functional:**
- Create subscription with plan and billing cycle
- Support trial periods (14-day trial)
- Upgrade/downgrade plans with immediate or scheduled effect
- Calculate prorated charges for mid-cycle changes
- Track usage-based metrics (API calls, storage GB)
- Bill usage overage at cycle end
- Handle payment failures with retry
- Pause/resume subscriptions
- Cancel with immediate or end-of-cycle effect
- Generate invoices with line items
- Support custom enterprise contracts

**Non-Functional:**
- **Performance**: < 100ms subscription change
- **Accuracy**: No double billing, correct proration
- **Consistency**: ACID transactions for billing
- **Scalability**: 100K active subscriptions
- **Observability**: Track MRR, churn, upgrades

### 3. Main Engineering Challenges & Solutions

**Challenge 1: State Pattern for Subscription Lifecycle**
- **Problem**: Subscription transitions through states (Trial → Active → Paused → Cancelled)
- **Solution**: State pattern with state-specific behavior
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: State pattern for subscription lifecycle
 */
interface SubscriptionState {
    SubscriptionState activate(Subscription subscription);
    SubscriptionState pause(Subscription subscription);
    SubscriptionState cancel(Subscription subscription);
    SubscriptionState upgrade(Subscription subscription, Plan newPlan);
    SubscriptionState downgrade(Subscription subscription, Plan newPlan);
    boolean canBill(Subscription subscription);
    String getStateName();
}

/**
 * Trial state
 */
class TrialState implements SubscriptionState {
    
    @Override
    public SubscriptionState activate(Subscription subscription) {
        // Convert trial to active
        subscription.setTrialEndDate(null);
        subscription.setNextBillingDate(calculateNextBillingDate(subscription));
        
        eventPublisher.publish(new TrialConvertedEvent(subscription.getId()));
        
        return new ActiveState();
    }
    
    @Override
    public SubscriptionState pause(Subscription subscription) {
        throw new InvalidStateTransitionException("Cannot pause trial");
    }
    
    @Override
    public SubscriptionState cancel(Subscription subscription) {
        subscription.setCancelledAt(Instant.now());
        return new CancelledState();
    }
    
    @Override
    public SubscriptionState upgrade(Subscription subscription, Plan newPlan) {
        // End trial, start paid immediately
        subscription.setPlan(newPlan);
        return activate(subscription);
    }
    
    @Override
    public SubscriptionState downgrade(Subscription subscription, Plan newPlan) {
        throw new InvalidStateTransitionException("Cannot downgrade during trial");
    }
    
    @Override
    public boolean canBill(Subscription subscription) {
        // Check if trial expired
        return subscription.getTrialEndDate() != null &&
               Instant.now().isAfter(subscription.getTrialEndDate());
    }
    
    @Override
    public String getStateName() {
        return "TRIAL";
    }
    
    private Instant calculateNextBillingDate(Subscription subscription) {
        return Instant.now().plus(subscription.getBillingCycle().getDuration());
    }
}

/**
 * Active state
 */
class ActiveState implements SubscriptionState {
    
    @Override
    public SubscriptionState activate(Subscription subscription) {
        return this; // Already active
    }
    
    @Override
    public SubscriptionState pause(Subscription subscription) {
        subscription.setPausedAt(Instant.now());
        return new PausedState();
    }
    
    @Override
    public SubscriptionState cancel(Subscription subscription) {
        subscription.setCancelledAt(Instant.now());
        subscription.setEndDate(calculateEndDate(subscription));
        
        return new CancelledState();
    }
    
    /**
     * INTERVIEW CRITICAL: Upgrade with proration
     */
    @Override
    public SubscriptionState upgrade(Subscription subscription, Plan newPlan) {
        Plan oldPlan = subscription.getPlan();
        
        // Calculate proration
        Proration proration = calculateProration(
            subscription,
            oldPlan,
            newPlan,
            Instant.now()
        );
        
        // Apply immediately
        subscription.setPlan(newPlan);
        subscription.setNextBillingDate(proration.getNextBillingDate());
        
        // Create invoice for prorated amount
        createProratedInvoice(subscription, proration);
        
        eventPublisher.publish(new SubscriptionUpgradedEvent(
            subscription.getId(),
            oldPlan.getId(),
            newPlan.getId(),
            proration.getAmount()
        ));
        
        return this;
    }
    
    /**
     * INTERVIEW CRITICAL: Downgrade scheduled for end of cycle
     */
    @Override
    public SubscriptionState downgrade(Subscription subscription, Plan newPlan) {
        // Schedule downgrade for end of current billing cycle
        subscription.setScheduledPlanChange(new PlanChange(
            newPlan,
            subscription.getNextBillingDate(),
            PlanChangeType.DOWNGRADE
        ));
        
        eventPublisher.publish(new SubscriptionDowngradeScheduledEvent(
            subscription.getId(),
            newPlan.getId(),
            subscription.getNextBillingDate()
        ));
        
        return this;
    }
    
    @Override
    public boolean canBill(Subscription subscription) {
        return Instant.now().isAfter(subscription.getNextBillingDate());
    }
    
    @Override
    public String getStateName() {
        return "ACTIVE";
    }
    
    private Instant calculateEndDate(Subscription subscription) {
        // Cancel at end of current billing cycle
        return subscription.getNextBillingDate();
    }
}

/**
 * Paused state
 */
class PausedState implements SubscriptionState {
    
    @Override
    public SubscriptionState activate(Subscription subscription) {
        subscription.setPausedAt(null);
        
        // Extend next billing date by pause duration
        Duration pauseDuration = Duration.between(
            subscription.getPausedAt(),
            Instant.now()
        );
        
        subscription.setNextBillingDate(
            subscription.getNextBillingDate().plus(pauseDuration)
        );
        
        return new ActiveState();
    }
    
    @Override
    public SubscriptionState pause(Subscription subscription) {
        return this; // Already paused
    }
    
    @Override
    public SubscriptionState cancel(Subscription subscription) {
        subscription.setCancelledAt(Instant.now());
        return new CancelledState();
    }
    
    @Override
    public SubscriptionState upgrade(Subscription subscription, Plan newPlan) {
        throw new InvalidStateTransitionException("Cannot upgrade while paused");
    }
    
    @Override
    public SubscriptionState downgrade(Subscription subscription, Plan newPlan) {
        throw new InvalidStateTransitionException("Cannot downgrade while paused");
    }
    
    @Override
    public boolean canBill(Subscription subscription) {
        return false; // No billing while paused
    }
    
    @Override
    public String getStateName() {
        return "PAUSED";
    }
}

/**
 * Cancelled state
 */
class CancelledState implements SubscriptionState {
    
    @Override
    public SubscriptionState activate(Subscription subscription) {
        throw new InvalidStateTransitionException("Cannot reactivate cancelled subscription");
    }
    
    @Override
    public SubscriptionState pause(Subscription subscription) {
        throw new InvalidStateTransitionException("Cannot pause cancelled subscription");
    }
    
    @Override
    public SubscriptionState cancel(Subscription subscription) {
        return this; // Already cancelled
    }
    
    @Override
    public SubscriptionState upgrade(Subscription subscription, Plan newPlan) {
        throw new InvalidStateTransitionException("Cannot upgrade cancelled subscription");
    }
    
    @Override
    public SubscriptionState downgrade(Subscription subscription, Plan newPlan) {
        throw new InvalidStateTransitionException("Cannot downgrade cancelled subscription");
    }
    
    @Override
    public boolean canBill(Subscription subscription) {
        return false;
    }
    
    @Override
    public String getStateName() {
        return "CANCELLED";
    }
}

/**
 * Subscription entity using state
 */
@Entity
public class Subscription {
    @Id
    private UUID id;
    
    private UUID tenantId;
    
    @ManyToOne
    private Plan plan;
    
    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle; // MONTHLY, ANNUAL
    
    @Transient
    private SubscriptionState state;
    
    @Column(name = "state_name")
    private String stateName; // For persistence
    
    private Instant startDate;
    private Instant trialEndDate;
    private Instant nextBillingDate;
    private Instant pausedAt;
    private Instant cancelledAt;
    private Instant endDate;
    
    @Embedded
    private PlanChange scheduledPlanChange;
    
    /**
     * State transitions
     */
    public void activate() {
        setState(state.activate(this));
    }
    
    public void pause() {
        setState(state.pause(this));
    }
    
    public void cancel() {
        setState(state.cancel(this));
    }
    
    public void upgrade(Plan newPlan) {
        setState(state.upgrade(this, newPlan));
    }
    
    public void downgrade(Plan newPlan) {
        setState(state.downgrade(this, newPlan));
    }
    
    private void setState(SubscriptionState newState) {
        this.state = newState;
        this.stateName = newState.getStateName();
    }
    
    @PostLoad
    private void restoreState() {
        this.state = SubscriptionStateFactory.fromName(stateName);
    }
}
```

**Challenge 2: Proration Calculation Strategy**
- **Problem**: Calculate prorated charges for mid-cycle plan changes
- **Solution**: Strategy pattern for different proration methods
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Proration calculation strategies
 */
interface ProrationStrategy {
    Proration calculate(Subscription subscription, Plan oldPlan, 
                       Plan newPlan, Instant changeDate);
}

/**
 * Daily proration (most accurate)
 */
class DailyProrationStrategy implements ProrationStrategy {
    
    /**
     * Calculate proration based on days used vs days in cycle
     */
    @Override
    public Proration calculate(Subscription subscription, Plan oldPlan,
                               Plan newPlan, Instant changeDate) {
        Instant cycleStart = subscription.getLastBillingDate();
        Instant cycleEnd = subscription.getNextBillingDate();
        
        // Days in current cycle
        long totalDays = Duration.between(cycleStart, cycleEnd).toDays();
        
        // Days used of old plan
        long daysUsed = Duration.between(cycleStart, changeDate).toDays();
        
        // Days remaining in cycle
        long daysRemaining = Duration.between(changeDate, cycleEnd).toDays();
        
        // Credit for unused portion of old plan
        BigDecimal oldPlanPrice = oldPlan.getPrice();
        BigDecimal creditAmount = oldPlanPrice
            .multiply(BigDecimal.valueOf(daysRemaining))
            .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
        
        // Charge for new plan for remaining days
        BigDecimal newPlanPrice = newPlan.getPrice();
        BigDecimal chargeAmount = newPlanPrice
            .multiply(BigDecimal.valueOf(daysRemaining))
            .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
        
        // Net amount to charge
        BigDecimal netAmount = chargeAmount.subtract(creditAmount);
        
        return Proration.builder()
            .creditAmount(creditAmount)
            .chargeAmount(chargeAmount)
            .netAmount(netAmount)
            .nextBillingDate(cycleEnd) // Keep same cycle end
            .build();
    }
}

/**
 * Full cycle proration (charge full new plan price immediately)
 */
class FullCycleProrationStrategy implements ProrationStrategy {
    
    @Override
    public Proration calculate(Subscription subscription, Plan oldPlan,
                               Plan newPlan, Instant changeDate) {
        BigDecimal newPlanPrice = newPlan.getPrice();
        
        // Charge full price of new plan
        // No credit for old plan
        
        // Reset billing cycle to start from change date
        Instant newCycleEnd = changeDate.plus(
            subscription.getBillingCycle().getDuration()
        );
        
        return Proration.builder()
            .creditAmount(BigDecimal.ZERO)
            .chargeAmount(newPlanPrice)
            .netAmount(newPlanPrice)
            .nextBillingDate(newCycleEnd)
            .build();
    }
}

/**
 * No proration (just switch at next cycle)
 */
class NoProratingStrategy implements ProrationStrategy {
    
    @Override
    public Proration calculate(Subscription subscription, Plan oldPlan,
                               Plan newPlan, Instant changeDate) {
        // Schedule change for next billing date
        // No immediate charge
        
        return Proration.builder()
            .creditAmount(BigDecimal.ZERO)
            .chargeAmount(BigDecimal.ZERO)
            .netAmount(BigDecimal.ZERO)
            .nextBillingDate(subscription.getNextBillingDate())
            .scheduled(true)
            .build();
    }
}

@Value
@Builder
class Proration {
    BigDecimal creditAmount;
    BigDecimal chargeAmount;
    BigDecimal netAmount;
    Instant nextBillingDate;
    boolean scheduled;
}
```

**Challenge 3: Usage-Based Billing**
- **Problem**: Track and bill usage metrics (API calls, storage)
- **Solution**: Template Method for billing flow with usage calculation
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Template method for billing
 */
abstract class BillingProcessor {
    
    /**
     * Template method - defines billing workflow
     */
    @Transactional
    public Invoice processBilling(Subscription subscription) {
        // 1. Validate can bill
        if (!subscription.getState().canBill(subscription)) {
            throw new CannotBillException("Subscription not in billable state");
        }
        
        // 2. Calculate base subscription charge
        BigDecimal baseCharge = calculateBaseCharge(subscription);
        
        // 3. Calculate usage charges (hook method - overridden by subclasses)
        BigDecimal usageCharge = calculateUsageCharges(subscription);
        
        // 4. Apply discounts (hook method)
        BigDecimal discount = applyDiscounts(subscription, baseCharge, usageCharge);
        
        // 5. Calculate total
        BigDecimal total = baseCharge.add(usageCharge).subtract(discount);
        
        // 6. Create invoice
        Invoice invoice = createInvoice(subscription, baseCharge, usageCharge, discount, total);
        
        // 7. Attempt payment
        boolean paymentSuccess = processPayment(invoice);
        
        if (paymentSuccess) {
            // 8. Update subscription
            updateSubscriptionAfterBilling(subscription);
            
            // 9. Reset usage metrics
            resetUsageMetrics(subscription);
        } else {
            // Handle payment failure
            handlePaymentFailure(invoice);
        }
        
        return invoice;
    }
    
    /**
     * Calculate base subscription charge
     */
    private BigDecimal calculateBaseCharge(Subscription subscription) {
        Plan plan = subscription.getPlan();
        BillingCycle cycle = subscription.getBillingCycle();
        
        return plan.getPrice().multiply(cycle.getMultiplier());
    }
    
    /**
     * Hook method: Calculate usage-based charges
     */
    protected abstract BigDecimal calculateUsageCharges(Subscription subscription);
    
    /**
     * Hook method: Apply discounts
     */
    protected BigDecimal applyDiscounts(Subscription subscription,
                                        BigDecimal baseCharge,
                                        BigDecimal usageCharge) {
        return BigDecimal.ZERO; // Default: no discount
    }
    
    /**
     * Hook method: Reset usage counters
     */
    protected abstract void resetUsageMetrics(Subscription subscription);
}

/**
 * Billing processor with usage tracking
 */
class UsageBasedBillingProcessor extends BillingProcessor {
    private final UsageTrackingService usageService;
    
    /**
     * INTERVIEW CRITICAL: Calculate usage charges
     */
    @Override
    protected BigDecimal calculateUsageCharges(Subscription subscription) {
        UUID tenantId = subscription.getTenantId();
        Instant billingStart = subscription.getLastBillingDate();
        Instant billingEnd = subscription.getNextBillingDate();
        
        // Get usage metrics for billing period
        UsageMetrics metrics = usageService.getMetrics(
            tenantId,
            billingStart,
            billingEnd
        );
        
        BigDecimal totalUsageCharge = BigDecimal.ZERO;
        
        // Calculate API calls overage
        if (metrics.getApiCalls() > subscription.getPlan().getIncludedApiCalls()) {
            long overage = metrics.getApiCalls() - subscription.getPlan().getIncludedApiCalls();
            BigDecimal apiCharge = BigDecimal.valueOf(overage)
                .multiply(subscription.getPlan().getApiCallPrice());
            totalUsageCharge = totalUsageCharge.add(apiCharge);
        }
        
        // Calculate storage overage
        if (metrics.getStorageGB() > subscription.getPlan().getIncludedStorageGB()) {
            double overage = metrics.getStorageGB() - subscription.getPlan().getIncludedStorageGB();
            BigDecimal storageCharge = BigDecimal.valueOf(overage)
                .multiply(subscription.getPlan().getStorageGBPrice());
            totalUsageCharge = totalUsageCharge.add(storageCharge);
        }
        
        return totalUsageCharge;
    }
    
    @Override
    protected void resetUsageMetrics(Subscription subscription) {
        // Reset counters for new billing cycle
        usageService.resetMetrics(
            subscription.getTenantId(),
            subscription.getNextBillingDate()
        );
    }
}

/**
 * Simple billing processor (no usage tracking)
 */
class SimpleBillingProcessor extends BillingProcessor {
    
    @Override
    protected BigDecimal calculateUsageCharges(Subscription subscription) {
        return BigDecimal.ZERO; // No usage charges
    }
    
    @Override
    protected void resetUsageMetrics(Subscription subscription) {
        // Nothing to reset
    }
}
```

**Challenge 4: Payment Retry and Dunning**
- **Problem**: Handle failed payments gracefully with retry logic
- **Solution**: Exponential backoff retry with dunning process
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Payment retry with exponential backoff
 */
@Service
public class PaymentRetryService {
    private final PaymentProcessor paymentProcessor;
    private final NotificationService notificationService;
    
    private static final int MAX_RETRIES = 3;
    private static final Duration[] RETRY_DELAYS = {
        Duration.ofHours(24),  // First retry after 24 hours
        Duration.ofDays(3),    // Second retry after 3 days
        Duration.ofDays(7)     // Third retry after 7 days
    };
    
    /**
     * Handle payment failure with retry
     */
    @Transactional
    public void handlePaymentFailure(Invoice invoice) {
        Subscription subscription = invoice.getSubscription();
        
        // Increment failure count
        int failureCount = invoice.getPaymentAttempts();
        invoice.setPaymentAttempts(failureCount + 1);
        
        if (failureCount < MAX_RETRIES) {
            // Schedule retry
            Duration delay = RETRY_DELAYS[failureCount];
            Instant retryAt = Instant.now().plus(delay);
            
            invoice.setNextRetryAt(retryAt);
            invoice.setStatus(InvoiceStatus.PAYMENT_RETRY_SCHEDULED);
            
            // Notify customer
            notificationService.sendPaymentFailureNotification(
                subscription.getTenantId(),
                invoice,
                retryAt
            );
            
        } else {
            // Max retries exceeded - enter dunning
            invoice.setStatus(InvoiceStatus.PAYMENT_FAILED);
            subscription.setState(new DunningState());
            
            // Notify customer - final warning
            notificationService.sendFinalPaymentNotification(
                subscription.getTenantId(),
                invoice
            );
            
            // Schedule subscription suspension
            scheduleSubscriptionSuspension(subscription);
        }
        
        invoiceRepo.save(invoice);
        subscriptionRepo.save(subscription);
    }
    
    /**
     * Retry payment
     */
    @Scheduled(fixedDelay = 3600000) // Check every hour
    public void processRetries() {
        List<Invoice> retryable = invoiceRepo.findRetryable(Instant.now());
        
        for (Invoice invoice : retryable) {
            try {
                boolean success = paymentProcessor.processPayment(
                    invoice.getSubscription().getTenantId(),
                    invoice.getTotalAmount()
                );
                
                if (success) {
                    invoice.setStatus(InvoiceStatus.PAID);
                    invoice.setPaidAt(Instant.now());
                    
                    // Resume subscription
                    Subscription subscription = invoice.getSubscription();
                    subscription.activate();
                    
                    notificationService.sendPaymentSuccessNotification(
                        subscription.getTenantId(),
                        invoice
                    );
                    
                } else {
                    handlePaymentFailure(invoice);
                }
                
            } catch (Exception e) {
                logger.error("Payment retry failed for invoice {}", 
                           invoice.getId(), e);
                handlePaymentFailure(invoice);
            }
        }
    }
    
    private void scheduleSubscriptionSuspension(Subscription subscription) {
        // Suspend after 30 days of failed payment
        Instant suspendAt = Instant.now().plus(Duration.ofDays(30));
        
        ScheduledTask task = ScheduledTask.builder()
            .subscriptionId(subscription.getId())
            .action(TaskAction.SUSPEND_SUBSCRIPTION)
            .scheduledFor(suspendAt)
            .build();
        
        taskRepo.save(task);
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **State** | Subscription lifecycle | State-specific behavior |
| **Strategy** | Proration calculation | Swap proration methods |
| **Template Method** | Billing flow | Common workflow, variant steps |
| **Observer** | Subscription events | Decouple event handling |
| **Factory** | Create state instances | Instantiate based on name |
| **Repository** | Data access | Abstract persistence |

### 5. Domain Model & Class Structure

```
       Subscription
      - state ────────────> SubscriptionState (interface)
      - plan                      ▲
      - billingCycle              │
                        ┌─────────┼─────────┬─────────┐
                        │         │         │         │
                   TrialState ActiveState PausedState CancelledState
```

### 6. Detailed Sequence Diagrams

**Sequence: Upgrade Subscription**
```
Client  SubService  Subscription  ProrationCalc  InvoiceService
  │         │            │              │               │
  ├─upgrade─>│           │              │               │
  │         ├─upgrade────>│             │               │
  │         │            ├─calculate────>│              │
  │         │            │<─proration───┤               │
  │         │            ├─setPlan───┐   │              │
  │         │            │<──────────┘   │              │
  │         ├─createInvoice──────────────────────────>│
  │         │<─invoice──────────────────────────────────┤
  │<─result─┤            │              │               │
```

### 7. Core Implementation (Interview-Critical Code)

```java
// Plan
@Entity
public class Plan {
    @Id
    private UUID id;
    
    private String name; // Free, Basic, Pro, Enterprise
    private BigDecimal price;
    
    // Usage limits
    private long includedApiCalls;
    private double includedStorageGB;
    
    // Overage pricing
    private BigDecimal apiCallPrice; // Per 1000 calls
    private BigDecimal storageGBPrice; // Per GB
    
    @Enumerated(EnumType.STRING)
    private PlanTier tier; // FREE, BASIC, PRO, ENTERPRISE
}

// Billing cycle
public enum BillingCycle {
    MONTHLY(Duration.ofDays(30), BigDecimal.ONE),
    ANNUAL(Duration.ofDays(365), BigDecimal.valueOf(12));
    
    private final Duration duration;
    private final BigDecimal multiplier;
}

// Invoice
@Entity
public class Invoice {
    @Id
    private UUID id;
    
    @ManyToOne
    private Subscription subscription;
    
    private BigDecimal baseCharge;
    private BigDecimal usageCharge;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
    
    private int paymentAttempts;
    private Instant nextRetryAt;
    private Instant createdAt;
    private Instant paidAt;
    
    @OneToMany
    private List<InvoiceLineItem> lineItems;
}

// Usage metrics
@Entity
public class UsageMetrics {
    @Id
    private UUID id;
    
    private UUID tenantId;
    private long apiCalls;
    private double storageGB;
    
    private Instant periodStart;
    private Instant periodEnd;
}
```

### 8. Thread Safety & Concurrency

**State Transitions:**
- Transactional state changes
- Optimistic locking on subscription
- Idempotent operations

**Billing Processing:**
- Distributed lock per subscription
- Prevent duplicate billing
- Atomic invoice creation

**Usage Tracking:**
- Atomic increment operations
- Eventually consistent reads acceptable
- Periodic aggregation

### 9. Top Interview Questions & Answers

**Q1: Why State pattern for subscription?**
**A:**
```
State pattern allows:
1. State-specific behavior (e.g., Trial can't pause)
2. Clear state transitions
3. Easy to add new states
4. Prevents invalid operations

Alternative: Enum + switch
→ Violates OCP, hard to maintain
```

**Q2: How to calculate proration?**
**A:**
```java
// Daily proration formula:
Credit = OldPrice × (DaysRemaining / TotalDays)
Charge = NewPrice × (DaysRemaining / TotalDays)
NetAmount = Charge - Credit

Example:
- 30-day cycle, changed on day 20
- Old plan: $100, New plan: $150
- Days remaining: 10

Credit = $100 × (10/30) = $33.33
Charge = $150 × (10/30) = $50.00
Net = $50.00 - $33.33 = $16.67
```

**Q3: Database schema?**
**A:**
```sql
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    plan_id UUID REFERENCES plans(id),
    billing_cycle VARCHAR(20) NOT NULL,
    state_name VARCHAR(20) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    trial_end_date TIMESTAMP,
    next_billing_date TIMESTAMP,
    last_billing_date TIMESTAMP,
    paused_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    end_date TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_subscriptions_tenant 
    ON subscriptions(tenant_id);
CREATE INDEX idx_subscriptions_next_billing 
    ON subscriptions(state_name, next_billing_date) 
    WHERE state_name = 'ACTIVE';

CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    subscription_id UUID REFERENCES subscriptions(id),
    base_charge DECIMAL(10, 2),
    usage_charge DECIMAL(10, 2),
    discount DECIMAL(10, 2),
    total_amount DECIMAL(10, 2),
    status VARCHAR(20) NOT NULL,
    payment_attempts INT DEFAULT 0,
    next_retry_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    paid_at TIMESTAMP
);

CREATE INDEX idx_invoices_retry 
    ON invoices(status, next_retry_at) 
    WHERE status = 'PAYMENT_RETRY_SCHEDULED';

CREATE TABLE usage_metrics (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    api_calls BIGINT DEFAULT 0,
    storage_gb DECIMAL(10, 2) DEFAULT 0,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL
);

CREATE INDEX idx_usage_tenant_period 
    ON usage_metrics(tenant_id, period_start, period_end);
```

**Q4: How to prevent double billing?**
**A:**
```java
// Use distributed lock
@Transactional
public Invoice processBilling(Subscription subscription) {
    String lockKey = "billing:" + subscription.getId();
    
    try (DistributedLock lock = lockService.acquire(lockKey, Duration.ofMinutes(5))) {
        // Check if already billed for this cycle
        Optional<Invoice> existing = invoiceRepo.findBySubscriptionAndPeriod(
            subscription.getId(),
            subscription.getLastBillingDate(),
            subscription.getNextBillingDate()
        );
        
        if (existing.isPresent()) {
            return existing.get(); // Idempotent
        }
        
        // Proceed with billing...
    }
}
```

**Q5: How to handle downgrades?**
**A:**
```java
// Schedule downgrade for end of cycle
@Override
public SubscriptionState downgrade(Subscription subscription, Plan newPlan) {
    // Don't apply immediately (user already paid for current cycle)
    PlanChange change = PlanChange.builder()
        .newPlan(newPlan)
        .effectiveDate(subscription.getNextBillingDate())
        .type(PlanChangeType.DOWNGRADE)
        .build();
    
    subscription.setScheduledPlanChange(change);
    
    return this; // Stay in Active state
}

// Apply scheduled change during billing
@Scheduled(cron = "0 0 * * * *") // Every hour
public void applyScheduledChanges() {
    List<Subscription> subscriptions = subscriptionRepo
        .findWithScheduledChanges(Instant.now());
    
    for (Subscription sub : subscriptions) {
        PlanChange change = sub.getScheduledPlanChange();
        sub.setPlan(change.getNewPlan());
        sub.setScheduledPlanChange(null);
        subscriptionRepo.save(sub);
    }
}
```

**Q6: Performance optimization?**
**A:**
```java
// 1. Batch billing processing
@Scheduled(cron = "0 0 2 * * *") // 2 AM daily
public void processDailyBilling() {
    Instant now = Instant.now();
    
    subscriptionRepo.findBillable(now)
        .forEach(subscription -> {
            CompletableFuture.runAsync(() -> {
                try {
                    billingProcessor.processBilling(subscription);
                } catch (Exception e) {
                    logger.error("Billing failed for {}", 
                               subscription.getId(), e);
                }
            }, executorService);
        });
}

// 2. Cache plans
@Cacheable(value = "plans", key = "#id")
public Plan findPlan(UUID id) {
    return planRepo.findById(id).orElseThrow();
}

// 3. Aggregate usage metrics periodically
@Scheduled(fixedDelay = 300000) // Every 5 minutes
public void aggregateUsage() {
    // Aggregate from event stream to metrics table
}
```

**Q7: What metrics to track?**
**A:**
```
SaaS Metrics:
1. MRR (Monthly Recurring Revenue)
2. Churn rate (cancellations / total)
3. Upgrade rate (upgrades / total)
4. ARPU (Average Revenue Per User)
5. LTV (Lifetime Value)

Operational:
- Billing success rate
- Payment retry success rate
- Average time in trial
- Usage overage frequency
```

**Q8: How to handle refunds?**
**A:**
```java
@Transactional
public Refund processRefund(UUID invoiceId, BigDecimal amount, String reason) {
    Invoice invoice = invoiceRepo.findById(invoiceId).orElseThrow();
    
    if (invoice.getStatus() != InvoiceStatus.PAID) {
        throw new InvalidRefundException("Invoice not paid");
    }
    
    if (amount.compareTo(invoice.getTotalAmount()) > 0) {
        throw new InvalidRefundException("Refund exceeds invoice amount");
    }
    
    // Process refund with payment gateway
    boolean success = paymentGateway.refund(invoice, amount);
    
    if (success) {
        Refund refund = Refund.builder()
            .id(UUID.randomUUID())
            .invoiceId(invoiceId)
            .amount(amount)
            .reason(reason)
            .processedAt(Instant.now())
            .build();
        
        refundRepo.save(refund);
        
        // Update invoice
        invoice.setRefundedAmount(
            invoice.getRefundedAmount().add(amount)
        );
        invoiceRepo.save(invoice);
        
        return refund;
    }
    
    throw new RefundFailedException("Payment gateway error");
}
```

**Q9: How to test state transitions?**
**A:**
```java
@Test
public void testSubscriptionLifecycle() {
    Subscription subscription = new Subscription();
    subscription.setState(new TrialState());
    
    // Trial → Active
    subscription.activate();
    assertEquals("ACTIVE", subscription.getStateName());
    
    // Active → Paused
    subscription.pause();
    assertEquals("PAUSED", subscription.getStateName());
    
    // Paused → Active
    subscription.activate();
    assertEquals("ACTIVE", subscription.getStateName());
    
    // Active → Cancelled
    subscription.cancel();
    assertEquals("CANCELLED", subscription.getStateName());
    
    // Cannot reactivate from cancelled
    assertThrows(InvalidStateTransitionException.class, 
                () -> subscription.activate());
}
```

**Q10: How to handle enterprise contracts?**
**A:**
```java
@Entity
public class EnterpriseContract extends Subscription {
    private BigDecimal customPrice;
    private int contractTermMonths; // 12, 24, 36
    private BigDecimal commitmentAmount;
    private boolean autoRenew;
    
    @OneToMany
    private List<ContractTerm> customTerms;
    
    @Override
    protected BigDecimal calculateBaseCharge() {
        // Use custom pricing instead of plan price
        return customPrice;
    }
}

class EnterpriseProrationStrategy implements ProrationStrategy {
    @Override
    public Proration calculate(...) {
        // No proration for enterprise
        // Changes only at contract renewal
        return Proration.noProration();
    }
}
```

### 10. Extensions & Variations

1. **Seat-Based Pricing**: Charge per user seat
2. **Freemium Model**: Free tier with upgrade prompts
3. **Add-ons**: Optional features (backup, support)
4. **Volume Discounts**: Discount for high usage
5. **Partner Reseller**: White-label subscriptions

### 11. Testing Strategy

**Unit Tests:**
- State transitions
- Proration calculations
- Usage billing logic

**Integration Tests:**
- Full billing cycle
- Payment retry flow
- State persistence

**Performance Tests:**
- 10K billing operations
- Concurrent subscription changes
- Usage metric aggregation

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Enum + switch for states
✅ **Do**: State pattern

❌ **Avoid**: Immediate downgrade (user loses paid time)
✅ **Do**: Schedule for cycle end

❌ **Avoid**: No payment retry
✅ **Do**: Exponential backoff retry

❌ **Avoid**: Synchronous billing
✅ **Do**: Async batch processing

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Change plan | O(1) | O(1) | State transition |
| Calculate proration | O(1) | O(1) | Simple arithmetic |
| Process billing | O(1) | O(1) | Single subscription |
| Batch billing | O(N) | O(N) | N = subscriptions |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **State Pattern** | 30% | Lifecycle management |
| **Proration Logic** | 25% | Accurate calculations |
| **Payment Handling** | 20% | Retry, dunning |
| **Usage Billing** | 15% | Template method |
| **Real-world Awareness** | 10% | MRR, churn, refunds |

**Red Flags:**
- No state management
- Incorrect proration
- No payment retry
- Missing audit trail
- Double billing possible

---
