# Problem 7: E-commerce Cart & Checkout (Strategy + Command + Money Pattern)

## System Design Perspective (High-Level)

### Architecture Overview
```
┌──────────────┐         ┌─────────────┐         ┌──────────────┐
│   Customer   │────────>│     API     │────────>│    Cart      │
│     Web/App  │<────────│   Gateway   │<────────│   Service    │
└──────────────┘         └─────────────┘         └──────┬───────┘
                                                         │
                        ┌────────────────────────────────┼─────────────┐
                        │                                │             │
                        ▼                                ▼             ▼
                ┌───────────────┐              ┌─────────────┐  ┌──────────┐
                │   Product     │              │  Pricing    │  │Inventory │
                │   Catalog     │              │  Engine     │  │ Service  │
                └───────────────┘              └─────────────┘  └────┬─────┘
                                                                      │
                        ┌─────────────────────────────────────────────┘
                        ▼
                ┌───────────────┐              ┌─────────────┐
                │   Checkout    │─────────────>│  Payment    │
                │   Service     │              │  Service    │
                └───────┬───────┘              └─────────────┘
                        │
                        ▼
                ┌───────────────┐              ┌─────────────┐
                │     Order     │              │   Event     │
                │   Service     │─────────────>│    Bus      │
                └───────────────┘              └─────────────┘
```

### Key System Design Decisions
1. **Money Pattern**: Immutable value objects for precise currency calculations
2. **Optimistic Locking**: Version-based concurrency control for cart updates
3. **Idempotent APIs**: Prevent duplicate orders from retries
4. **Inventory Reservation**: Two-phase commit (reserve → confirm/release)
5. **CQRS**: Separate cart writes from product catalog reads

### Scalability Considerations
- **Read-heavy**: 1000:1 read-to-write ratio on product catalog
- **Cart Session**: Redis for ephemeral cart storage (TTL: 30 days)
- **Hot Products**: Cache popular items with CDN
- **Peak Load**: Black Friday - 100K checkouts/min
- **Data Volume**: 10M active carts, 100M products

---

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design an e-commerce cart and checkout system handling cart operations (add, update, remove items), price calculations with promotions, inventory validation, and order placement.

**Assumptions / Scope:**
- Cart operations: Add/remove/update quantity, apply coupons
- Pricing: Base price, quantity discounts, coupons, taxes, shipping
- Inventory: Real-time availability check, temporary reservation during checkout
- Checkout flow: Validate cart → Reserve inventory → Process payment → Create order
- Persistence: Cart in session/cache, orders in database
- Concurrency: Multiple users, same user multiple tabs
- Scale: 1M active carts, 50K checkouts/hour
- Out of scope: Product catalog management, recommendation engine

**Non-Functional Goals:**
- Precise money calculations (no floating-point errors)
- Consistent cart state under concurrent updates
- Inventory reservation holds for 10 minutes
- Sub-500ms checkout latency (excluding payment)
- Idempotent order creation
- Audit trail for price changes

### 2. Core Requirements

**Functional:**
- Add/remove/update items in cart with quantity
- Calculate cart total: subtotal, discounts, taxes, shipping
- Apply and validate coupon codes
- Check inventory availability in real-time
- Reserve inventory during checkout (temporary hold)
- Process payment and create order atomically
- Handle cart abandonment (cleanup expired items)
- Support guest and registered user carts

**Non-Functional:**
- **Accuracy**: Exact decimal arithmetic for money (no rounding errors)
- **Consistency**: Strong consistency for cart operations (user sees own writes)
- **Concurrency**: Handle concurrent cart updates (optimistic locking)
- **Performance**: Cart operations < 100ms, checkout < 500ms
- **Idempotency**: Duplicate checkout requests return same order
- **Observability**: Track cart conversion rate, abandonment metrics

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Precise Money Calculations**
- **Problem**: Floating-point arithmetic causes rounding errors ($0.1 + $0.2 = $0.30000000000000004)
- **Solution**: Money value object with integer cents + currency
- **Algorithm**:
```
class Money {
    private final long amountInCents;
    private final Currency currency;
    
    Money add(Money other) {
        validateSameCurrency(other);
        return new Money(
            this.amountInCents + other.amountInCents,
            this.currency
        );
    }
    
    Money multiply(BigDecimal multiplier) {
        // Use BigDecimal for intermediate calculations
        BigDecimal cents = BigDecimal.valueOf(amountInCents);
        BigDecimal result = cents.multiply(multiplier);
        
        // Round to nearest cent using HALF_UP
        long newCents = result.setScale(0, RoundingMode.HALF_UP).longValue();
        
        return new Money(newCents, currency);
    }
    
    Money applyPercentageDiscount(BigDecimal percentage) {
        BigDecimal multiplier = BigDecimal.ONE.subtract(
            percentage.divide(BigDecimal.valueOf(100))
        );
        return multiply(multiplier);
    }
}
```

**Challenge 2: Concurrent Cart Updates (Optimistic Locking)**
- **Problem**: User opens cart in two tabs, updates in both → lost update
- **Solution**: Version-based optimistic locking
- **Algorithm**:
```
class Cart {
    private UUID id;
    private long version;  // Incremented on each update
    private List<CartItem> items;
    
    @Version  // JPA annotation for optimistic locking
    public long getVersion() { return version; }
}

CartService:
    updateCart(cartId, updates, expectedVersion):
        cart = cartRepo.findById(cartId)
        
        // Check version
        if cart.version != expectedVersion:
            throw OptimisticLockException("Cart modified by another request")
        
        // Apply updates
        cart.applyUpdates(updates)
        cart.version++
        
        // Save with version check (database constraint)
        try:
            cartRepo.save(cart)
        catch ConstraintViolationException:
            throw OptimisticLockException("Concurrent modification detected")
        
        return cart

// Client retry logic
retryOnOptimisticLock:
    maxAttempts = 3
    attempt = 0
    
    while attempt < maxAttempts:
        try:
            cart = fetchLatestCart()
            result = updateCart(cartId, updates, cart.version)
            return result
        catch OptimisticLockException:
            attempt++
            sleep(exponentialBackoff(attempt))
    
    throw CartUpdateFailedException()
```

**Challenge 3: Inventory Reservation During Checkout**
- **Problem**: Check availability → user pays → inventory gone (race condition)
- **Solution**: Two-phase reservation with timeout
- **Algorithm**:
```
CheckoutService:
    checkout(cartId):
        cart = cartRepo.findById(cartId)
        
        // Phase 1: Reserve inventory (pessimistic lock)
        reservationId = UUID.randomUUID()
        
        for item in cart.items:
            reservation = inventoryService.reserve(
                productId = item.productId,
                quantity = item.quantity,
                reservationId = reservationId,
                expiresAt = now() + 10 minutes
            )
            
            if !reservation.success:
                // Rollback previous reservations
                inventoryService.releaseReservation(reservationId)
                throw InsufficientInventoryException(item.productId)
        
        try:
            // Phase 2: Process payment
            payment = paymentService.charge(cart.total, paymentMethod)
            
            if payment.status == SUCCESS:
                // Phase 3: Confirm reservation & create order
                inventoryService.confirmReservation(reservationId)
                order = createOrder(cart, payment)
                clearCart(cartId)
                return order
            else:
                // Payment failed, release reservation
                inventoryService.releaseReservation(reservationId)
                throw PaymentFailedException()
        
        catch Exception e:
            // Any error, release reservation
            inventoryService.releaseReservation(reservationId)
            throw CheckoutFailedException(e)

InventoryService:
    reserve(productId, quantity, reservationId, expiresAt):
        // Atomic operation with database lock
        product = productRepo.lockById(productId)  // SELECT FOR UPDATE
        
        if product.availableStock < quantity:
            return ReservationResult.INSUFFICIENT_STOCK
        
        // Deduct from available stock
        product.availableStock -= quantity
        product.reservedStock += quantity
        productRepo.update(product)
        
        // Record reservation
        reservation = new Reservation(
            id = reservationId,
            productId = productId,
            quantity = quantity,
            expiresAt = expiresAt,
            status = PENDING
        )
        reservationRepo.save(reservation)
        
        return ReservationResult.SUCCESS
    
    confirmReservation(reservationId):
        reservations = reservationRepo.findByReservationId(reservationId)
        
        for res in reservations:
            product = productRepo.findById(res.productId)
            product.reservedStock -= res.quantity
            product.soldStock += res.quantity
            productRepo.update(product)
            
            res.status = CONFIRMED
            reservationRepo.update(res)
    
    releaseReservation(reservationId):
        reservations = reservationRepo.findByReservationId(reservationId)
        
        for res in reservations:
            product = productRepo.findById(res.productId)
            product.reservedStock -= res.quantity
            product.availableStock += res.quantity
            productRepo.update(product)
            
            res.status = CANCELLED
            reservationRepo.update(res)

// Background job: Clean up expired reservations
ReservationCleanupJob:
    @Scheduled(fixedDelay = 60000)  // Every minute
    cleanupExpiredReservations():
        expiredReservations = reservationRepo.findExpired(now())
        
        for res in expiredReservations:
            if res.status == PENDING:
                releaseReservation(res.reservationId)
```

**Challenge 4: Price Consistency (Prevent Price Arbitrage)**
- **Problem**: User adds item at $100 → price changes to $150 → user checks out at $100
- **Solution**: Lock prices at cart addition time, detect changes at checkout
- **Algorithm**:
```
class CartItem {
    private Product product;
    private int quantity;
    private Money unitPriceWhenAdded;  // Snapshot
    private Instant addedAt;
}

CartService:
    addItem(cartId, productId, quantity):
        product = productCatalog.getProduct(productId)
        
        // Snapshot current price
        item = new CartItem(
            product = product,
            quantity = quantity,
            unitPriceWhenAdded = product.currentPrice,
            addedAt = now()
        )
        
        cart.items.add(item)
        return cart

CheckoutService:
    validatePrices(cart):
        priceChanges = []
        
        for item in cart.items:
            currentProduct = productCatalog.getProduct(item.productId)
            
            if currentProduct.currentPrice != item.unitPriceWhenAdded:
                priceChanges.add(
                    PriceChange(
                        productId = item.productId,
                        oldPrice = item.unitPriceWhenAdded,
                        newPrice = currentProduct.currentPrice
                    )
                )
        
        if !priceChanges.isEmpty():
            // Option 1: Reject checkout, ask user to review
            throw PriceChangedException(priceChanges)
            
            // Option 2: Auto-update to current prices
            // (requires user notification and consent)
        
        return true
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Money Pattern** | Price, discount, total calculations | Prevents floating-point errors, encapsulates currency logic |
| **Strategy** | Pricing rules, discount strategies | Easily swap promotional pricing algorithms |
| **Command** | Cart operations (Add, Remove, Update) | Encapsulate operations for undo/redo, audit trail |
| **Composite** | Pricing rules (stacking discounts) | Tree of pricing rules applied hierarchically |
| **Factory** | Create different cart types (guest vs registered) | Encapsulate instantiation logic |
| **Builder** | Construct complex orders | Step-by-step order creation with validation |
| **Repository** | Cart, order, product persistence | Abstract data access layer |
| **Saga** | Checkout flow (reserve → pay → confirm) | Handle distributed transaction across services |
| **Specification** | Coupon eligibility, promotion rules | Compose complex business rules |
| **Value Object** | Money, CartItem | Immutability, equality by value |

### 5. Domain Model & Class Structure

```
┌────────────────────┐
│   CartService      │ (Application Service)
│  - cartRepo        │
│  - pricingEngine   │
│  - inventorySvc    │
│  - checkoutSvc     │
└─────────┬──────────┘
          │ manages
          │
    ┌─────┴──────────────┬─────────────────┐
    ▼                    ▼                 ▼
┌─────────┐      ┌──────────────┐   ┌─────────────┐
│  Cart   │      │   Product    │   │   Coupon    │
│(Aggregate)     │   (Entity)   │   │  (Entity)   │
└─────────┘      └──────────────┘   └─────────────┘
    │
    │ contains
    ▼
┌──────────────┐       ┌───────────┐
│  CartItem    │──────>│   Money   │
│  (Entity)    │ has   │   (VO)    │
└──────────────┘       └───────────┘

Strategies:
┌───────────────────────┐
│  PricingStrategy      │
│  + calculatePrice()   │
└───────────────────────┘
        ▲
        │implements
┌───────┴────────────────────────────┐
│  BasePricingStrategy               │
│  QuantityDiscountStrategy          │
│  BulkPricingStrategy               │
│  TieredPricingStrategy             │
└────────────────────────────────────┘

Commands:
┌───────────────────────┐
│   CartCommand         │ (Abstract)
│  + execute()          │
│  + undo()             │
└────────┬──────────────┘
         │
    ┌────┴──────────────────────┐
┌───▼──────────┐    ┌──────────▼──────┐
│AddItemCmd    │    │RemoveItemCmd    │
│UpdateQtyCmd  │    │ApplyCouponCmd   │
└──────────────┘    └─────────────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Add Item to Cart**
```
User      CartService   PricingEngine   ProductCatalog   CartRepo
  │            │              │               │             │
  ├─addItem───>│              │               │             │
  │ (prodId,   │              │               │             │
  │  qty)      │              │               │             │
  │            ├─getProduct───────────────────>│             │
  │            │<─product─────────────────────┤             │
  │            ├─calculatePrice──>│            │             │
  │            │  (basePrice)     │            │             │
  │            │<─finalPrice──────┤            │             │
  │            ├─addItem(cart)────────────────────────────>│
  │            │<─updatedCart─────────────────────────────┤
  │<─cartDTO───┤              │               │             │
```

**Sequence: Checkout with Reservation**
```
User    CheckoutSvc  InventorySvc  PaymentSvc  OrderSvc  CartRepo
  │          │            │             │          │         │
  ├─checkout>│            │             │          │         │
  │          ├─getCart────────────────────────────────────>│
  │          │<─cart──────────────────────────────────────┤
  │          ├─reserve────>│             │          │         │
  │          │ (items)     │             │          │         │
  │          │<─reserveId──┤             │          │         │
  │          ├─charge──────────────────>│          │         │
  │          │            │            │          │         │
  │          │<─paymentOk────────────────┤          │         │
  │          ├─confirm────>│             │          │         │
  │          │ (reserveId) │             │          │         │
  │          ├─createOrder────────────────────────>│         │
  │          │            │             │         │         │
  │          ├─clearCart──────────────────────────────────>│
  │<─order───┤            │             │          │         │
```

### 7. Core Implementation (Java-esque Pseudocode)

```java
// Money Value Object (Immutable)
public final class Money {
    private final long amountInCents;
    private final Currency currency;
    
    private Money(long amountInCents, Currency currency) {
        if (amountInCents < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
        this.amountInCents = amountInCents;
        this.currency = Objects.requireNonNull(currency);
    }
    
    public static Money of(BigDecimal amount, Currency currency) {
        long cents = amount.multiply(BigDecimal.valueOf(100))
            .setScale(0, RoundingMode.HALF_UP)
            .longValue();
        return new Money(cents, currency);
    }
    
    public static Money dollars(double amount) {
        return of(BigDecimal.valueOf(amount), Currency.USD);
    }
    
    public Money add(Money other) {
        validateCurrency(other);
        return new Money(this.amountInCents + other.amountInCents, this.currency);
    }
    
    public Money subtract(Money other) {
        validateCurrency(other);
        if (this.amountInCents < other.amountInCents) {
            throw new IllegalArgumentException("Result would be negative");
        }
        return new Money(this.amountInCents - other.amountInCents, this.currency);
    }
    
    public Money multiply(int quantity) {
        return new Money(this.amountInCents * quantity, this.currency);
    }
    
    public Money multiply(BigDecimal multiplier) {
        BigDecimal cents = BigDecimal.valueOf(amountInCents);
        long newCents = cents.multiply(multiplier)
            .setScale(0, RoundingMode.HALF_UP)
            .longValue();
        return new Money(newCents, this.currency);
    }
    
    public Money applyPercentageDiscount(BigDecimal percentage) {
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
            percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );
        return multiply(discountMultiplier);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Money)) return false;
        Money other = (Money) obj;
        return this.amountInCents == other.amountInCents &&
               this.currency.equals(other.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amountInCents, currency);
    }
}

// Cart Aggregate
public class Cart {
    private final UUID id;
    private final UUID userId;
    private final List<CartItem> items;
    private Coupon appliedCoupon;
    
    @Version
    private long version;
    
    private Instant createdAt;
    private Instant updatedAt;
    
    public void addItem(Product product, int quantity) {
        // Check if item already exists
        Optional<CartItem> existing = findItem(product.getId());
        
        if (existing.isPresent()) {
            existing.get().increaseQuantity(quantity);
        } else {
            CartItem newItem = CartItem.create(
                product,
                quantity,
                product.getCurrentPrice()
            );
            items.add(newItem);
        }
        
        this.updatedAt = Instant.now();
    }
    
    public void removeItem(UUID productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        this.updatedAt = Instant.now();
    }
    
    public void updateQuantity(UUID productId, int newQuantity) {
        CartItem item = findItem(productId)
            .orElseThrow(() -> new ItemNotFoundException(productId));
        
        if (newQuantity <= 0) {
            removeItem(productId);
        } else {
            item.setQuantity(newQuantity);
            this.updatedAt = Instant.now();
        }
    }
    
    public void applyCoupon(Coupon coupon) {
        if (!coupon.isValid()) {
            throw new InvalidCouponException("Coupon expired or inactive");
        }
        
        if (!coupon.meetsMinimumRequirement(getSubtotal())) {
            throw new InvalidCouponException("Minimum purchase requirement not met");
        }
        
        this.appliedCoupon = coupon;
        this.updatedAt = Instant.now();
    }
    
    public Money getSubtotal() {
        return items.stream()
            .map(CartItem::getLineTotal)
            .reduce(Money.ZERO, Money::add);
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    private Optional<CartItem> findItem(UUID productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();
    }
}

// Cart Item Entity
public class CartItem {
    private UUID id;
    private UUID productId;
    private String productName;
    private int quantity;
    private Money unitPriceWhenAdded;
    private Instant addedAt;
    
    public static CartItem create(Product product, int quantity, Money price) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        CartItem item = new CartItem();
        item.id = UUID.randomUUID();
        item.productId = product.getId();
        item.productName = product.getName();
        item.quantity = quantity;
        item.unitPriceWhenAdded = price;
        item.addedAt = Instant.now();
        return item;
    }
    
    public Money getLineTotal() {
        return unitPriceWhenAdded.multiply(quantity);
    }
    
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
    }
    
    public void setQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = newQuantity;
    }
}

// Cart Service
public class CartService {
    private final CartRepository cartRepo;
    private final ProductCatalog productCatalog;
    private final PricingEngine pricingEngine;
    private final EventPublisher eventPublisher;
    
    @Transactional
    public Cart addItem(UUID cartId, UUID productId, int quantity) {
        // Fetch cart with lock
        Cart cart = cartRepo.findByIdWithLock(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));
        
        // Get product details
        Product product = productCatalog.getProduct(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
        
        // Check availability
        if (product.getAvailableStock() < quantity) {
            throw new InsufficientStockException(productId, quantity);
        }
        
        // Add to cart
        cart.addItem(product, quantity);
        
        // Save
        Cart updated = cartRepo.save(cart);
        
        // Publish event
        eventPublisher.publish(new ItemAddedToCartEvent(cartId, productId, quantity));
        
        return updated;
    }
    
    @Transactional
    public Cart updateQuantity(UUID cartId, UUID productId, int newQuantity, long expectedVersion) {
        Cart cart = cartRepo.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));
        
        // Optimistic lock check
        if (cart.getVersion() != expectedVersion) {
            throw new OptimisticLockException("Cart was modified");
        }
        
        cart.updateQuantity(productId, newQuantity);
        
        return cartRepo.save(cart);
    }
    
    public CartSummary getCartSummary(UUID cartId) {
        Cart cart = cartRepo.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));
        
        return pricingEngine.calculateSummary(cart);
    }
}

// Pricing Engine
public class PricingEngine {
    private final List<PricingRule> rules;
    private final TaxCalculator taxCalculator;
    private final ShippingCalculator shippingCalculator;
    
    public CartSummary calculateSummary(Cart cart) {
        Money subtotal = cart.getSubtotal();
        
        // Apply discounts
        Money discount = Money.ZERO;
        if (cart.getAppliedCoupon() != null) {
            discount = cart.getAppliedCoupon().calculateDiscount(subtotal);
        }
        
        // Apply pricing rules (quantity discounts, etc.)
        for (PricingRule rule : rules) {
            if (rule.appliesTo(cart)) {
                discount = discount.add(rule.calculateDiscount(cart));
            }
        }
        
        Money subtotalAfterDiscount = subtotal.subtract(discount);
        
        // Calculate tax
        Money tax = taxCalculator.calculate(subtotalAfterDiscount, cart.getShippingAddress());
        
        // Calculate shipping
        Money shipping = shippingCalculator.calculate(cart);
        
        Money total = subtotalAfterDiscount.add(tax).add(shipping);
        
        return CartSummary.builder()
            .subtotal(subtotal)
            .discount(discount)
            .subtotalAfterDiscount(subtotalAfterDiscount)
            .tax(tax)
            .shipping(shipping)
            .total(total)
            .build();
    }
}

// Checkout Service with Saga
public class CheckoutService {
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final EventPublisher eventPublisher;
    
    @Transactional
    public Order checkout(UUID cartId, PaymentMethod paymentMethod, Address shippingAddress) {
        // Step 1: Get cart
        Cart cart = cartService.getCart(cartId);
        
        if (cart.isEmpty()) {
            throw new EmptyCartException();
        }
        
        // Step 2: Validate prices (detect changes)
        validatePrices(cart);
        
        // Step 3: Calculate final total
        CartSummary summary = pricingEngine.calculateSummary(cart);
        
        // Step 4: Reserve inventory
        UUID reservationId = UUID.randomUUID();
        
        try {
            for (CartItem item : cart.getItems()) {
                ReservationResult result = inventoryService.reserve(
                    item.getProductId(),
                    item.getQuantity(),
                    reservationId,
                    Instant.now().plus(Duration.ofMinutes(10))
                );
                
                if (!result.isSuccess()) {
                    throw new InsufficientInventoryException(item.getProductId());
                }
            }
            
            // Step 5: Process payment
            Payment payment = paymentService.processPayment(
                summary.getTotal(),
                paymentMethod,
                generateIdempotencyKey(cartId)
            );
            
            if (!payment.isSuccessful()) {
                inventoryService.releaseReservation(reservationId);
                throw new PaymentFailedException(payment.getFailureReason());
            }
            
            // Step 6: Confirm reservation
            inventoryService.confirmReservation(reservationId);
            
            // Step 7: Create order
            Order order = orderService.createOrder(cart, payment, shippingAddress);
            
            // Step 8: Clear cart
            cartService.clearCart(cartId);
            
            // Step 9: Publish event
            eventPublisher.publish(new OrderPlacedEvent(order.getId(), cart.getUserId()));
            
            return order;
            
        } catch (Exception e) {
            // Compensate: Release reservation
            inventoryService.releaseReservation(reservationId);
            throw new CheckoutFailedException(e);
        }
    }
    
    private void validatePrices(Cart cart) {
        List<PriceChange> changes = new ArrayList<>();
        
        for (CartItem item : cart.getItems()) {
            Product currentProduct = productCatalog.getProduct(item.getProductId());
            
            if (!currentProduct.getCurrentPrice().equals(item.getUnitPriceWhenAdded())) {
                changes.add(new PriceChange(
                    item.getProductId(),
                    item.getUnitPriceWhenAdded(),
                    currentProduct.getCurrentPrice()
                ));
            }
        }
        
        if (!changes.isEmpty()) {
            throw new PriceChangedException(changes);
        }
    }
    
    private String generateIdempotencyKey(UUID cartId) {
        return DigestUtils.sha256Hex(cartId.toString() + Instant.now().truncatedTo(ChronoUnit.MINUTES));
    }
}

// Pricing Strategy Interface
public interface PricingStrategy {
    Money calculatePrice(Product product, int quantity);
    boolean appliesTo(Product product, int quantity);
}

// Quantity Discount Strategy
public class QuantityDiscountStrategy implements PricingStrategy {
    private final Map<Integer, BigDecimal> discountTiers;
    
    public QuantityDiscountStrategy() {
        // Buy 10+: 10% off, Buy 50+: 20% off, Buy 100+: 30% off
        discountTiers = Map.of(
            10, BigDecimal.valueOf(0.10),
            50, BigDecimal.valueOf(0.20),
            100, BigDecimal.valueOf(0.30)
        );
    }
    
    @Override
    public Money calculatePrice(Product product, int quantity) {
        Money basePrice = product.getBasePrice().multiply(quantity);
        
        BigDecimal discountPercentage = discountTiers.entrySet().stream()
            .filter(entry -> quantity >= entry.getKey())
            .max(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .orElse(BigDecimal.ZERO);
        
        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            return basePrice.applyPercentageDiscount(
                discountPercentage.multiply(BigDecimal.valueOf(100))
            );
        }
        
        return basePrice;
    }
    
    @Override
    public boolean appliesTo(Product product, int quantity) {
        return product.isEligibleForQuantityDiscount() && quantity >= 10;
    }
}

// Command Pattern for Cart Operations
public interface CartCommand {
    void execute(Cart cart);
    void undo(Cart cart);
}

public class AddItemCommand implements CartCommand {
    private final Product product;
    private final int quantity;
    private UUID addedItemId;
    
    @Override
    public void execute(Cart cart) {
        cart.addItem(product, quantity);
        // Store the item ID for undo
        addedItemId = cart.findItem(product.getId()).map(CartItem::getId).orElse(null);
    }
    
    @Override
    public void undo(Cart cart) {
        if (addedItemId != null) {
            cart.removeItem(product.getId());
        }
    }
}
```

### 8. Thread Safety & Concurrency

**Optimistic Locking:**
- Version field on Cart entity
- Database constraint prevents concurrent updates
- Client retries with exponential backoff

**Inventory Reservation:**
- Pessimistic locking (SELECT FOR UPDATE) on product stock
- Atomic decrement of available stock
- Background job releases expired reservations

**Money Immutability:**
- All Money operations return new instances
- Thread-safe by design (no mutable state)

### 9. Top Interview Questions & Answers

**Q1: Why use Money pattern instead of BigDecimal directly?**
**A:**
- **Type Safety**: Prevents mixing currencies ($100 + €50)
- **Encapsulation**: Arithmetic operations in one place
- **Domain Language**: Code reads like business logic
- **Immutability**: Value object properties
```java
// Without Money pattern (error-prone)
BigDecimal price = new BigDecimal("10.99");
BigDecimal qty = new BigDecimal("3");
BigDecimal total = price.multiply(qty); // OK
BigDecimal eurTotal = euroPrice.multiply(qty); // Can't catch currency mismatch!

// With Money pattern (safe)
Money price = Money.dollars(10.99);
Money eurPrice = Money.euros(10.99);
Money total = price.multiply(3); // OK
Money mixed = price.add(eurPrice); // Throws CurrencyMismatchException!
```

**Q2: How do you handle concurrent cart updates from multiple tabs?**
**A:**
- **Optimistic Locking**: Version number on cart
- **Last-Write-Wins**: Update with version check
- **Retry Logic**: Exponential backoff on conflict
```java
// Client-side retry
int attempt = 0;
while (attempt < 3) {
    try {
        Cart latest = fetchCart(cartId);
        updateCart(cartId, changes, latest.version);
        break;
    } catch (OptimisticLockException e) {
        attempt++;
        sleep(100 * Math.pow(2, attempt));
    }
}
```

**Q3: What if product price changes while item is in cart?**
**A:**
- **Snapshot Price**: Store price at add-time
- **Validation at Checkout**: Compare current vs snapshot
- **Options**:
  1. Reject checkout, show price changes
  2. Auto-update to current prices (with notification)
  3. Honor old price (time-limited, e.g., 24 hours)

**Q4: How do you prevent overselling (inventory goes negative)?**
**A:**
- **Two-Phase Commit**: Reserve → Confirm/Release
- **Pessimistic Lock**: SELECT FOR UPDATE during reserve
- **Timeout**: Auto-release after 10 minutes
- **Background Job**: Clean up expired reservations

**Q5: How do you ensure idempotent checkout?**
**A:**
```java
String idempotencyKey = sha256(cartId + timestamp.truncated(minutes));

Order checkout(UUID cartId, PaymentMethod pm) {
    // Check if order already exists for this key
    Optional<Order> existing = orderRepo.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent()) {
        return existing.get(); // Return cached result
    }
    
    // Process checkout...
    Order order = createOrder(cart, payment);
    order.setIdempotencyKey(idempotencyKey);
    orderRepo.save(order);
    
    return order;
}
```

**Q6: How do you test Money calculations?**
**A:**
```java
@Test
public void testMoneyArithmetic() {
    Money price = Money.dollars(10.99);
    Money total = price.multiply(3);
    
    assertEquals(Money.dollars(32.97), total);
    
    // Test rounding
    Money discount = total.applyPercentageDiscount(BigDecimal.valueOf(15));
    assertEquals(Money.dollars(28.02), discount); // 32.97 * 0.85 = 28.0245 → 28.02
}

@Test
public void testCurrencyMismatch() {
    Money usd = Money.dollars(100);
    Money eur = Money.euros(100);
    
    assertThrows(CurrencyMismatchException.class, () -> {
        usd.add(eur);
    });
}
```

**Q7: How would you implement cart abandonment recovery?**
**A:**
```java
@Scheduled(cron = "0 0 * * * *") // Every hour
public void processAbandonedCarts() {
    Instant cutoff = Instant.now().minus(Duration.ofHours(24));
    
    List<Cart> abandoned = cartRepo.findByUpdatedAtBeforeAndNotEmpty(cutoff);
    
    for (Cart cart : abandoned) {
        // Check if user has completed purchase
        if (!orderRepo.existsByCartId(cart.getId())) {
            // Send reminder email with cart link
            emailService.sendAbandonedCartReminder(
                cart.getUserId(),
                cart.getId(),
                generateCartRecoveryLink(cart.getId())
            );
            
            // Track metric
            metrics.incrementCounter("cart.abandoned", 
                "value", cart.getSubtotal().toString());
        }
    }
}
```

**Q8: How do you handle flash sales with high concurrency?**
**A:**
- **Queue-Based**: Accept orders to queue, process async
- **Optimistic Oversell**: Allow slight oversell, compensate later
- **Token Bucket**: Rate limit checkout requests
- **Cache Warming**: Preload hot products
```java
@RateLimiter(maxRequests = 1000, perSeconds = 1)
public Order checkout(UUID cartId) {
    // Rate-limited checkout
    // Queue if over limit
}
```

**Q9: What metrics would you track?**
**A:**
```java
Metrics:
1. Cart Conversion Rate: % carts that become orders
2. Average Cart Value: Mean cart subtotal
3. Cart Abandonment Rate: % carts not checked out in 24h
4. Items Per Cart: Average item count
5. Checkout Duration: P50/P95/P99 time to complete
6. Price Change Rejections: % checkouts failed due to price changes
7. Inventory Reserve Time: Duration items held in reservation
8. Coupon Usage Rate: % carts with coupons applied
9. Payment Success Rate: % successful payments
10. Concurrent Cart Operations: Active updates/second
```

**Q10: How would you implement a "Buy X Get Y" promotion?**
**A:**
```java
public class BuyXGetYPromotion implements PricingRule {
    private final UUID targetProductId;
    private final int buyQuantity;
    private final UUID freeProductId;
    private final int freeQuantity;
    
    @Override
    public Money calculateDiscount(Cart cart) {
        int targetQty = cart.getItemQuantity(targetProductId);
        
        if (targetQty >= buyQuantity) {
            int eligibleSets = targetQty / buyQuantity;
            int freeItems = eligibleSets * freeQuantity;
            
            Money freeProductPrice = cart.getItemUnitPrice(freeProductId);
            return freeProductPrice.multiply(freeItems);
        }
        
        return Money.ZERO;
    }
    
    @Override
    public boolean appliesTo(Cart cart) {
        return cart.containsProduct(targetProductId) &&
               cart.getItemQuantity(targetProductId) >= buyQuantity;
    }
}
```

### 10. Extensions & Variations

1. **Wishlist**: Separate from cart, move items between
2. **Save for Later**: Temporary hold without inventory reservation
3. **Gift Wrapping**: Additional services per item
4. **Subscriptions**: Recurring cart checkout
5. **Split Payment**: Multiple payment methods
6. **Price Alerts**: Notify when cart total drops below threshold
7. **Social Cart**: Share cart with friends for group purchase

### 11. Testing Strategy

**Unit Tests:**
- Money arithmetic accuracy
- Cart operations (add, remove, update)
- Pricing engine calculations
- Coupon validation logic

**Integration Tests:**
- Full checkout flow (reserve → pay → order)
- Optimistic locking with concurrent updates
- Price change detection
- Inventory reservation timeout

**Property-Based Tests:**
```java
@Property
public void moneyAdditionIsCommutative(@ForAll Money a, @ForAll Money b) {
    assertEquals(a.add(b), b.add(a));
}

@Property
public void cartTotalEqualsItemSum(@ForAll List<CartItem> items) {
    Cart cart = new Cart();
    items.forEach(cart::addItem);
    
    Money sumOfItems = items.stream()
        .map(CartItem::getLineTotal)
        .reduce(Money.ZERO, Money::add);
    
    assertEquals(sumOfItems, cart.getSubtotal());
}
```

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Using double/float for money
✅ **Do**: Money value object with integer cents

❌ **Avoid**: No concurrency control (lost updates)
✅ **Do**: Optimistic locking with version

❌ **Avoid**: Check inventory only at checkout
✅ **Do**: Real-time validation + reservation

❌ **Avoid**: Mutable cart state shared across threads
✅ **Do**: Immutable cart items, atomic operations

❌ **Avoid**: Hardcoded discount logic
✅ **Do**: Strategy pattern for pricing rules

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Add Item | O(n) | O(1) | n = items in cart (check duplicates) |
| Remove Item | O(n) | O(1) | Linear search by product ID |
| Calculate Total | O(n) | O(1) | Sum all item totals |
| Apply Coupon | O(1) | O(1) | Direct assignment |
| Checkout | O(n) | O(n) | n = items (reserve each) |
| Price Validation | O(n) | O(n) | Query catalog per item |

**Space Complexity:**
- O(C) for C active carts
- O(I) for I items across all carts
- O(R) for R active reservations

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Money Pattern** | 25% | Correct use of integer cents, immutability, currency safety |
| **Concurrency** | 20% | Optimistic locking, inventory reservation, race condition awareness |
| **Business Logic** | 20% | Price calculation accuracy, coupon validation, discount stacking |
| **Error Handling** | 15% | Price changes, stock issues, payment failures with compensation |
| **Extensibility** | 10% | Strategy for pricing, command for operations |
| **Real-world Awareness** | 10% | Cart abandonment, flash sales, idempotency |

**Red Flags:**
- Using float/double for money
- No concurrency control
- Direct inventory decrement without reservation
- Mutable cart state without protection
- No price validation at checkout

---
