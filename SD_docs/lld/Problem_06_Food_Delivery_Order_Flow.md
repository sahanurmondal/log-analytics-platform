# Problem 6: Food Delivery Order Flow (Observer + State + Saga)

## System Design Perspective (High-Level)

### Architecture Overview
```
┌──────────────┐         ┌─────────────┐         ┌──────────────┐
│   Customer   │────────>│     API     │────────>│    Order     │
│     App      │<────────│   Gateway   │<────────│   Service    │
└──────────────┘         └─────────────┘         └──────┬───────┘
                                                         │
                        ┌────────────────────────────────┼─────────────┐
                        │                                │             │
                        ▼                                ▼             ▼
                ┌───────────────┐              ┌─────────────┐  ┌──────────┐
                │  Restaurant   │              │   Courier   │  │ Payment  │
                │    Service    │              │   Service   │  │ Service  │
                └───────┬───────┘              └──────┬──────┘  └────┬─────┘
                        │                             │              │
                        └─────────────┬───────────────┴──────────────┘
                                      ▼
                              ┌───────────────┐
                              │   Event Bus   │
                              │  (Kafka/RMQ)  │
                              └───────┬───────┘
                                      │
                        ┌─────────────┼─────────────┐
                        ▼             ▼             ▼
                ┌──────────┐   ┌──────────┐  ┌──────────┐
                │Notifica  │   │Analytics │  │ Tracking │
                │  tion    │   │ Service  │  │ Service  │
                └──────────┘   └──────────┘  └──────────┘
```

### Key System Design Decisions
1. **Event-Driven Architecture**: Asynchronous communication via message broker
2. **Saga Pattern**: Distributed transaction management (order → payment → restaurant → courier)
3. **CQRS**: Separate write (order placement) from read (order tracking)
4. **Eventual Consistency**: Accept temporary inconsistency for availability
5. **Outbox Pattern**: Reliable event publishing with transactional guarantees

### Scalability Considerations
- **Peak Load**: 50K orders/min during lunch/dinner rush
- **Restaurant Capacity**: 10K active restaurants, 100K menu items
- **Courier Fleet**: 20K active couriers per city
- **Latency Target**: <200ms order placement, <5s courier assignment

---

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a food delivery order management system handling order placement, restaurant acceptance, courier assignment, real-time status tracking, and delivery completion.

**Assumptions / Scope:**
- Order types: Immediate delivery, scheduled delivery
- Payment: Pre-payment required, multiple payment methods
- Restaurant workflow: Receive → Accept/Reject → Prepare → Ready for pickup
- Courier assignment: Automatic dispatch based on proximity and availability
- Real-time tracking: GPS updates every 10-30 seconds
- Cancellations: Customer (before restaurant accepts), Restaurant (capacity issues)
- Scale: 1M orders/day per city
- Out of scope: Menu management, reviews, recommendations

**Non-Functional Goals:**
- 99.9% order placement success rate
- No payment double-charge under failures
- Real-time status updates (<5 second latency)
- Audit trail for disputes
- Graceful degradation (continue operation if courier service down)

### 2. Core Requirements

**Functional:**
- Place order with cart items, delivery address, payment method
- Validate restaurant open hours, menu availability, delivery zone
- Process payment and handle failures
- Notify restaurant and await acceptance (with timeout)
- Assign courier automatically or manually
- Track order state: Placed → Confirmed → Preparing → Ready → PickedUp → Delivering → Delivered
- Support cancellations with refund logic
- Real-time ETA updates based on courier location

**Non-Functional:**
- **Consistency**: Strong for payment, eventual for status updates
- **Availability**: 99.9% uptime (degrade gracefully)
- **Performance**: Order placement < 200ms
- **Reliability**: Exactly-once payment processing
- **Observability**: End-to-end tracing, metrics per state transition

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Distributed Transaction Coordination (Saga Pattern)**
- **Problem**: Order involves multiple services (Payment → Restaurant → Courier), any can fail
- **Solution**: Choreography-based Saga with compensating transactions
- **Algorithm**:
```
OrderSaga:
  Event: OrderPlaced
    → Trigger: ProcessPayment
  
  Event: PaymentSucceeded
    → Trigger: NotifyRestaurant
  
  Event: PaymentFailed
    → Compensate: CancelOrder, RefundCustomer
  
  Event: RestaurantAccepted
    → Trigger: AssignCourier
  
  Event: RestaurantRejected
    → Compensate: RefundPayment, NotifyCustomer
  
  Event: CourierAssigned
    → Update: OrderState = Confirmed
  
  Event: CourierAssignmentFailed
    → Compensate: RetryAssignment (3 attempts)
    → If all fail: RefundPayment, NotifyCustomer

// Compensation Flow Example
compensateOrderPlacement():
  if paymentCompleted:
    initiateRefund(orderId, fullAmount)
  
  if restaurantNotified:
    sendCancellation(restaurantId, orderId)
  
  if courierAssigned:
    releaseAssignment(courierId, orderId)
  
  order.state = CANCELLED
  eventBus.publish(OrderCancelled(orderId, reason))
```

**Challenge 2: Exactly-Once Payment Processing**
- **Problem**: Network failures can cause duplicate payment attempts
- **Solution**: Idempotency key + Outbox pattern
- **Algorithm**:
```
PaymentProcessor:
  processPayment(orderId, amount, paymentMethod):
    idempotencyKey = generateKey(orderId, amount)
    
    // Check if already processed
    existing = paymentRepo.findByIdempotencyKey(idempotencyKey)
    if existing != null:
      return existing // Return cached result
    
    // Atomic DB transaction
    transaction.begin()
    try:
      payment = createPayment(orderId, amount, status=PENDING)
      outboxEvent = createOutboxEvent(PaymentInitiated, payment)
      
      paymentRepo.save(payment)
      outboxRepo.save(outboxEvent)
      
      transaction.commit()
    catch:
      transaction.rollback()
      throw PaymentException()
    
    // Async: Process with external gateway (retry on failure)
    paymentGateway.charge(payment, idempotencyKey)
    
    return payment

// Outbox Publisher (separate background process)
OutboxPublisher:
  poll every 100ms:
    unpublishedEvents = outboxRepo.findPending(limit=100)
    
    for event in unpublishedEvents:
      try:
        eventBus.publish(event)
        outboxRepo.markPublished(event.id)
      catch:
        // Retry with exponential backoff
        event.retryCount++
        event.nextRetry = now() + exponentialDelay(event.retryCount)
        outboxRepo.update(event)
```

**Challenge 3: Real-Time Order Status Tracking**
- **Problem**: Multiple services update order state, need consistent view
- **Solution**: Event Sourcing + Materialized View (CQRS)
- **Algorithm**:
```
OrderEventStore:
  events[] // Immutable append-only log
  
  appendEvent(event):
    event.sequenceNumber = events.length + 1
    event.timestamp = now()
    events.append(event)
    
    // Publish to event bus
    eventBus.publish(event)
  
  replayEvents(orderId):
    orderEvents = events.filter(e => e.orderId == orderId)
    order = new Order(orderId)
    
    for event in orderEvents:
      order.apply(event) // Rebuild state
    
    return order

// Read Model Projector
OrderProjector:
  onEvent(event):
    switch event.type:
      case OrderPlaced:
        createOrderView(event.orderId, event.details)
      
      case PaymentSucceeded:
        updateOrderView(event.orderId, paymentStatus=PAID)
      
      case RestaurantAccepted:
        updateOrderView(event.orderId, 
          restaurantStatus=ACCEPTED,
          estimatedPrepTime=event.prepTime)
      
      case CourierAssigned:
        updateOrderView(event.orderId,
          courierId=event.courierId,
          estimatedDelivery=event.eta)
      
      // ... handle all events

// Query side (fast reads from materialized view)
getOrderStatus(orderId):
  return orderViewRepo.findById(orderId) // O(1) lookup
```

**Challenge 4: Courier Assignment with Fallback**
- **Problem**: Optimal courier may decline, need efficient retry
- **Solution**: Priority queue + timeout-based reassignment
- **Algorithm**:
```
CourierAssignmentService:
  assignCourier(order):
    candidates = findCandidates(
      order.restaurantLocation,
      order.deliveryAddress
    )
    
    if candidates.isEmpty():
      // Fallback: Notify manual dispatch
      manualDispatchQueue.add(order)
      return null
    
    for attempt in 1..MAX_ATTEMPTS:
      courier = candidates.poll() // Get best candidate
      
      assignment = createAssignment(order, courier, 
        expiresAt=now() + 30 seconds)
      
      assignmentRepo.save(assignment)
      notificationService.notifyCourier(courier, assignment)
      
      // Wait for response
      response = awaitResponse(assignment, timeout=30s)
      
      if response == ACCEPTED:
        assignment.status = CONFIRMED
        order.state = CONFIRMED
        return assignment
      
      if response == DECLINED || response == TIMEOUT:
        assignment.status = DECLINED
        continue // Try next candidate
    
    // All declined, escalate
    manualDispatchQueue.add(order)
    eventBus.publish(CourierAssignmentFailed(order.id))
    return null

findCandidates(restaurantLoc, deliveryLoc):
  // Step 1: Find couriers near restaurant
  nearbyCouriers = courierIndex.findWithinRadius(
    restaurantLoc,
    radius=5km,
    status=AVAILABLE
  )
  
  // Step 2: Calculate score for each
  scored = []
  for courier in nearbyCouriers:
    distance = haversineDistance(courier.location, restaurantLoc)
    eta = calculateETA(courier.location, restaurantLoc)
    
    score = calculateScore(
      distance,
      eta,
      courier.rating,
      courier.acceptanceRate,
      courier.onTimeDeliveryRate
    )
    
    scored.add((courier, score))
  
  // Step 3: Return top N candidates
  return scored.sortByScore().take(5)
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Observer** | Order state change notifications | Decouple state machine from notification logic (SMS, push, email) |
| **State** | Order lifecycle management | Clear state transitions with validation |
| **Saga** | Distributed transaction coordination | Handle failures across Payment → Restaurant → Courier |
| **Command** | Order operations as objects | Queuing, retry, audit trail |
| **Strategy** | Courier assignment algorithms | Swap between nearest-first, balanced, AI-based |
| **Factory** | Create different order types | Immediate vs Scheduled vs Subscription orders |
| **CQRS** | Separate read/write models | Optimize writes for consistency, reads for speed |
| **Outbox** | Reliable event publishing | Atomic write + publish with transactional guarantees |
| **Circuit Breaker** | External service calls | Fail fast when payment gateway or SMS service down |

### 5. Domain Model & Class Structure

```
┌────────────────────┐
│   OrderService     │ (Application Service)
│  - orderRepo       │
│  - eventStore      │
│  - paymentSvc      │
│  - courierSvc      │
│  - eventBus        │
└─────────┬──────────┘
          │ orchestrates
          │
    ┌─────┴──────────────┬─────────────────┐
    ▼                    ▼                 ▼
┌─────────┐      ┌──────────────┐   ┌─────────────┐
│  Order  │      │   Payment    │   │  Assignment │
│(Aggregate)     │  (Entity)    │   │  (Entity)   │
└─────────┘      └──────────────┘   └─────────────┘
    │
    │ contains
    ▼
┌──────────────┐
│  OrderItem   │
│  (Entity)    │
└──────────────┘

State Machine:
┌──────────────┐
│ OrderState   │
│  (Enum)      │
│ PLACED       │
│ PAYMENT_PEND │
│ CONFIRMED    │
│ PREPARING    │
│ READY        │
│ PICKED_UP    │
│ DELIVERING   │
│ DELIVERED    │
│ CANCELLED    │
└──────────────┘

Events:
┌──────────────────┐
│   OrderEvent     │ (Abstract)
│  - orderId       │
│  - timestamp     │
│  - sequenceNum   │
└────────┬─────────┘
         │
    ┌────┴──────────────────────┐
    │                           │
┌───▼──────────┐    ┌──────────▼──────┐
│OrderPlaced   │    │PaymentSucceeded │
│OrderCancelled│    │RestaurantAccept │
│CourierAssign │    │DeliveryComplete │
└──────────────┘    └─────────────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Happy Path Order Flow**
```
Customer  OrderSvc  PaymentSvc  EventBus  RestSvc  CourierSvc  Notif
  │          │          │          │         │         │         │
  ├─place───>│          │          │         │         │         │
  │  order   │          │          │         │         │         │
  │          ├─process─>│          │         │         │         │
  │          │ payment  │          │         │         │         │
  │          │<─success─┤          │         │         │         │
  │          ├─publish(PaymentOk)─>│         │         │         │
  │          │          │          ├────────>│         │         │
  │          │          │          │ notify  │         │         │
  │          │          │          │<─accept─┤         │         │
  │          │<─────────────────────┤         │         │         │
  │          ├─publish(RestaurantOk)>        │         │         │
  │          ├─assign──────────────────────────────>│         │
  │          │          │          │         │       │         │
  │          │<────────courierAssigned───────────────┤         │
  │          ├─publish(CourierAssigned)─────>│       │         │
  │          │          │          │         │       │         │
  │          ├─notifyAll──────────────────────────────────────>│
  │<─confirmation────────┤          │         │       │         │
```

**Sequence: Compensation Flow (Restaurant Rejects)**
```
Customer  OrderSvc  PaymentSvc  EventBus  RestSvc  Notif
  │          │          │          │         │       │
  │          │          │          ├────────>│       │
  │          │          │          │ notify  │       │
  │          │          │          │<─reject─┤       │
  │          │<─────────────────────┤         │       │
  │          ├─compensate───────────>         │       │
  │          │          │          │         │       │
  │          ├─refund──>│          │         │       │
  │          │          ├─process  │         │       │
  │          │<─refundOk┤          │         │       │
  │          ├─publish(OrderCancelled)>      │       │
  │          ├─notify──────────────────────────────>│
  │<─cancellation────────┤          │         │       │
```

### 7. Core Implementation (Java-esque Pseudocode)

```java
// Main Order Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final OrderEventStore eventStore;
    private final PaymentService paymentService;
    private final CourierAssignmentService courierService;
    private final RestaurantService restaurantService;
    private final EventBus eventBus;
    
    @Transactional
    public Order placeOrder(OrderRequest request) {
        // Step 1: Validate
        validateOrder(request);
        
        // Step 2: Create order aggregate
        Order order = Order.builder()
            .id(UUID.randomUUID())
            .customerId(request.getCustomerId())
            .restaurantId(request.getRestaurantId())
            .items(request.getItems())
            .deliveryAddress(request.getDeliveryAddress())
            .totalAmount(calculateTotal(request.getItems()))
            .state(OrderState.PLACED)
            .createdAt(Instant.now())
            .build();
        
        // Step 3: Save order
        orderRepo.save(order);
        
        // Step 4: Record event
        OrderPlacedEvent event = new OrderPlacedEvent(order);
        eventStore.append(event);
        
        // Step 5: Publish event (triggers saga)
        eventBus.publish(event);
        
        return order;
    }
    
    // Event Handlers (Saga Orchestration)
    
    @EventHandler
    public void onOrderPlaced(OrderPlacedEvent event) {
        Order order = orderRepo.findById(event.getOrderId());
        
        try {
            // Initiate payment
            Payment payment = paymentService.processPayment(
                order.getId(),
                order.getTotalAmount(),
                order.getPaymentMethod()
            );
            
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                eventBus.publish(new PaymentSucceededEvent(order.getId(), payment));
            } else {
                eventBus.publish(new PaymentFailedEvent(order.getId(), payment));
            }
        } catch (PaymentException e) {
            eventBus.publish(new PaymentFailedEvent(order.getId(), e.getMessage()));
        }
    }
    
    @EventHandler
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        Order order = orderRepo.findById(event.getOrderId());
        order.setState(OrderState.PAYMENT_CONFIRMED);
        orderRepo.update(order);
        
        // Notify restaurant
        restaurantService.notifyNewOrder(order);
        
        // Set timeout for restaurant acceptance
        scheduleTimeout(order.getId(), Duration.ofMinutes(5), () -> {
            Order current = orderRepo.findById(order.getId());
            if (current.getState() == OrderState.PAYMENT_CONFIRMED) {
                // Restaurant didn't respond
                compensateOrder(order.getId(), "Restaurant timeout");
            }
        });
    }
    
    @EventHandler
    public void onRestaurantAccepted(RestaurantAcceptedEvent event) {
        Order order = orderRepo.findById(event.getOrderId());
        order.setState(OrderState.CONFIRMED);
        order.setEstimatedPrepTime(event.getPrepTime());
        orderRepo.update(order);
        
        // Assign courier
        courierService.assignCourier(order);
    }
    
    @EventHandler
    public void onRestaurantRejected(RestaurantRejectedEvent event) {
        compensateOrder(event.getOrderId(), event.getReason());
    }
    
    @EventHandler
    public void onCourierAssigned(CourierAssignedEvent event) {
        Order order = orderRepo.findById(event.getOrderId());
        order.setCourierId(event.getCourierId());
        order.setEstimatedDeliveryTime(event.getEta());
        orderRepo.update(order);
        
        // Notify all parties
        notificationService.notifyOrderConfirmed(order);
    }
    
    @EventHandler
    public void onCourierAssignmentFailed(CourierAssignmentFailedEvent event) {
        compensateOrder(event.getOrderId(), "No courier available");
    }
    
    private void compensateOrder(UUID orderId, String reason) {
        Order order = orderRepo.findById(orderId);
        
        // Step 1: Initiate refund
        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            paymentService.refund(order.getId(), order.getTotalAmount());
        }
        
        // Step 2: Cancel restaurant notification
        restaurantService.cancelOrder(order.getRestaurantId(), order.getId());
        
        // Step 3: Release courier if assigned
        if (order.getCourierId() != null) {
            courierService.releaseAssignment(order.getCourierId(), order.getId());
        }
        
        // Step 4: Update order state
        order.setState(OrderState.CANCELLED);
        order.setCancellationReason(reason);
        orderRepo.update(order);
        
        // Step 5: Publish event
        eventBus.publish(new OrderCancelledEvent(orderId, reason));
        
        // Step 6: Notify customer
        notificationService.notifyOrderCancelled(order, reason);
    }
}

// Payment Service with Idempotency
public class PaymentService {
    private final PaymentRepository paymentRepo;
    private final OutboxRepository outboxRepo;
    private final PaymentGateway paymentGateway;
    
    @Transactional
    public Payment processPayment(
        UUID orderId,
        Money amount,
        PaymentMethod method
    ) throws PaymentException {
        // Generate idempotency key
        String idempotencyKey = generateKey(orderId, amount);
        
        // Check if already processed (idempotent)
        Optional<Payment> existing = paymentRepo.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Create payment record
        Payment payment = Payment.builder()
            .id(UUID.randomUUID())
            .orderId(orderId)
            .amount(amount)
            .method(method)
            .status(PaymentStatus.PENDING)
            .idempotencyKey(idempotencyKey)
            .createdAt(Instant.now())
            .build();
        
        // Atomic: Save payment + outbox event
        paymentRepo.save(payment);
        
        OutboxEvent outboxEvent = new OutboxEvent(
            UUID.randomUUID(),
            "PaymentInitiated",
            payment.toJson(),
            OutboxStatus.PENDING
        );
        outboxRepo.save(outboxEvent);
        
        // Async: Call external gateway
        CompletableFuture.runAsync(() -> {
            try {
                PaymentResult result = paymentGateway.charge(
                    payment.getAmount(),
                    payment.getMethod(),
                    idempotencyKey
                );
                
                payment.setStatus(
                    result.isSuccess() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED
                );
                payment.setGatewayTransactionId(result.getTransactionId());
                paymentRepo.update(payment);
                
            } catch (Exception e) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason(e.getMessage());
                paymentRepo.update(payment);
            }
        });
        
        return payment;
    }
    
    private String generateKey(UUID orderId, Money amount) {
        return DigestUtils.sha256Hex(orderId + ":" + amount.toString());
    }
}

// Order Aggregate with State Machine
public class Order {
    private final UUID id;
    private final UUID customerId;
    private final UUID restaurantId;
    private final List<OrderItem> items;
    private final Address deliveryAddress;
    private final Money totalAmount;
    
    private OrderState state;
    private UUID courierId;
    private Instant estimatedDeliveryTime;
    private String cancellationReason;
    
    public void setState(OrderState newState) {
        if (!this.state.canTransitionTo(newState)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.state, newState)
            );
        }
        this.state = newState;
    }
    
    public boolean canCancel() {
        return state.isCancellable();
    }
}

public enum OrderState {
    PLACED,
    PAYMENT_CONFIRMED,
    CONFIRMED,
    PREPARING,
    READY,
    PICKED_UP,
    DELIVERING,
    DELIVERED,
    CANCELLED;
    
    private static final Map<OrderState, Set<OrderState>> TRANSITIONS = Map.of(
        PLACED, Set.of(PAYMENT_CONFIRMED, CANCELLED),
        PAYMENT_CONFIRMED, Set.of(CONFIRMED, CANCELLED),
        CONFIRMED, Set.of(PREPARING, CANCELLED),
        PREPARING, Set.of(READY, CANCELLED),
        READY, Set.of(PICKED_UP, CANCELLED),
        PICKED_UP, Set.of(DELIVERING),
        DELIVERING, Set.of(DELIVERED),
        DELIVERED, Set.of(),
        CANCELLED, Set.of()
    );
    
    public boolean canTransitionTo(OrderState target) {
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
    
    public boolean isCancellable() {
        return this == PLACED || 
               this == PAYMENT_CONFIRMED || 
               this == CONFIRMED || 
               this == PREPARING || 
               this == READY;
    }
}

// Event Store (Append-Only Log)
public class OrderEventStore {
    private final EventRepository eventRepo;
    private final EventBus eventBus;
    
    public void append(OrderEvent event) {
        // Assign sequence number
        long sequenceNum = eventRepo.getNextSequence(event.getOrderId());
        event.setSequenceNumber(sequenceNum);
        event.setTimestamp(Instant.now());
        
        // Save event (immutable)
        eventRepo.save(event);
        
        // Publish asynchronously
        CompletableFuture.runAsync(() -> {
            eventBus.publish(event);
        });
    }
    
    public List<OrderEvent> getEvents(UUID orderId) {
        return eventRepo.findByOrderId(orderId)
            .sorted(Comparator.comparingLong(OrderEvent::getSequenceNumber))
            .collect(Collectors.toList());
    }
    
    public Order replayEvents(UUID orderId) {
        List<OrderEvent> events = getEvents(orderId);
        
        if (events.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }
        
        Order order = null;
        for (OrderEvent event : events) {
            if (event instanceof OrderPlacedEvent) {
                order = ((OrderPlacedEvent) event).toOrder();
            } else {
                order.apply(event);
            }
        }
        
        return order;
    }
}

// CQRS Read Model Projector
public class OrderProjector {
    private final OrderViewRepository viewRepo;
    
    @EventHandler
    public void onOrderPlaced(OrderPlacedEvent event) {
        OrderView view = OrderView.builder()
            .orderId(event.getOrderId())
            .customerId(event.getCustomerId())
            .restaurantId(event.getRestaurantId())
            .status(OrderStatus.PLACED)
            .totalAmount(event.getTotalAmount())
            .createdAt(event.getTimestamp())
            .build();
        
        viewRepo.save(view);
    }
    
    @EventHandler
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        OrderView view = viewRepo.findById(event.getOrderId());
        view.setStatus(OrderStatus.PAYMENT_CONFIRMED);
        view.setPaymentId(event.getPaymentId());
        viewRepo.update(view);
    }
    
    @EventHandler
    public void onCourierAssigned(CourierAssignedEvent event) {
        OrderView view = viewRepo.findById(event.getOrderId());
        view.setStatus(OrderStatus.CONFIRMED);
        view.setCourierId(event.getCourierId());
        view.setEstimatedDelivery(event.getEta());
        viewRepo.update(view);
    }
    
    // ... handle all events
}
```

### 8. Thread Safety & Concurrency

**Saga Concurrency:**
- Each order has independent saga execution
- No shared mutable state between orders
- Event handlers are idempotent (can replay safely)

**Outbox Pattern:**
- Atomic write to DB + outbox table in single transaction
- Background poller publishes events (at-least-once delivery)
- Event consumers must be idempotent

**Payment Idempotency:**
```java
// Idempotency key prevents duplicate charges
String key = sha256(orderId + amount + timestamp)
Payment payment = paymentGateway.charge(amount, key)

// Gateway guarantees: same key → same result
```

### 9. Top Interview Questions & Answers

**Q1: Why use Saga pattern instead of distributed transactions (2PC)?**
**A:**
- **Scalability**: 2PC requires locking across services (blocks throughput)
- **Availability**: 2PC coordinator is single point of failure
- **Autonomy**: Services can evolve independently
- **Long-lived**: Order saga spans minutes/hours (2PC not suitable)
- **Trade-off**: Accept eventual consistency for better availability

**Q2: How do you handle duplicate events in event-driven architecture?**
**A:**
```java
// Make event handlers idempotent
@EventHandler
public void onPaymentSucceeded(PaymentSucceededEvent event) {
    Order order = orderRepo.findById(event.getOrderId());
    
    // Check if already processed
    if (order.getPaymentStatus() == PaymentStatus.CONFIRMED) {
        return; // Already handled, skip
    }
    
    order.setPaymentStatus(PaymentStatus.CONFIRMED);
    orderRepo.update(order);
}

// Alternative: Track processed events
Set<UUID> processedEventIds = new ConcurrentHashSet<>();

if (processedEventIds.contains(event.getId())) {
    return; // Duplicate, skip
}
processedEventIds.add(event.getId());
```

**Q3: What happens if the payment succeeds but the notification fails?**
**A:**
- **Outbox Pattern** ensures at-least-once delivery
- Event sits in outbox table until successfully published
- Background poller retries failed publications
- Even if app crashes, events are persisted
```java
// Outbox guarantees delivery
@Transactional
public void processPayment() {
    // Atomic: Payment + Outbox event
    payment.save();
    outbox.save(new PaymentSucceededEvent());
    commit();
}

// Separate poller publishes from outbox
OutboxPoller:
  every 100ms:
    events = outbox.findUnpublished()
    for event in events:
      eventBus.publish(event)
      outbox.markPublished(event.id)
```

**Q4: How do you test the compensation flow?**
**A:**
```java
@Test
public void testRestaurantRejectionTriggersRefund() {
    // Setup
    Order order = placeOrder(validRequest);
    Payment payment = paymentService.processPayment(order);
    
    // Trigger compensation
    eventBus.publish(new RestaurantRejectedEvent(
        order.getId(),
        "Out of ingredients"
    ));
    
    // Wait for async processing
    await().atMost(5, SECONDS).until(() -> {
        Order updated = orderRepo.findById(order.getId());
        return updated.getState() == OrderState.CANCELLED;
    });
    
    // Verify refund initiated
    Payment refund = paymentRepo.findByOrderId(order.getId());
    assertEquals(PaymentStatus.REFUNDED, refund.getStatus());
    
    // Verify customer notified
    verify(notificationService).notifyOrderCancelled(order);
}
```

**Q5: How do you handle out-of-order events?**
**A:**
- **Sequence Numbers**: Each event has sequential number
- **Causality**: Events reference parent event IDs
- **Buffering**: Queue out-of-order events until gap filled
```java
class OrderEventBuffer {
    Map<UUID, SortedMap<Long, OrderEvent>> buffers = new HashMap<>();
    Map<UUID, Long> nextExpectedSeq = new HashMap<>();
    
    void onEvent(OrderEvent event) {
        UUID orderId = event.getOrderId();
        long seq = event.getSequenceNumber();
        long expected = nextExpectedSeq.getOrDefault(orderId, 1L);
        
        if (seq == expected) {
            // In order, process immediately
            processEvent(event);
            nextExpectedSeq.put(orderId, seq + 1);
            
            // Check buffer for next events
            while (buffers.get(orderId).containsKey(expected + 1)) {
                OrderEvent buffered = buffers.get(orderId).remove(expected + 1);
                processEvent(buffered);
                expected++;
            }
        } else if (seq > expected) {
            // Future event, buffer it
            buffers.computeIfAbsent(orderId, k -> new TreeMap<>())
                .put(seq, event);
        }
        // else: seq < expected, duplicate, ignore
    }
}
```

**Q6: How would you implement scheduled orders (deliver at specific time)?**
**A:**
```java
class ScheduledOrderService {
    private final ScheduledExecutorService scheduler;
    
    public Order placeScheduledOrder(OrderRequest request, Instant deliveryTime) {
        // Create order with scheduled state
        Order order = createOrder(request);
        order.setScheduledDeliveryTime(deliveryTime);
        order.setState(OrderState.SCHEDULED);
        orderRepo.save(order);
        
        // Calculate when to start processing
        Instant processingStartTime = deliveryTime.minus(
            calculatePrepAndDeliveryTime(order),
            ChronoUnit.MINUTES
        );
        
        // Schedule activation
        long delayMinutes = Duration.between(
            Instant.now(),
            processingStartTime
        ).toMinutes();
        
        scheduler.schedule(() -> {
            activateScheduledOrder(order.getId());
        }, delayMinutes, TimeUnit.MINUTES);
        
        return order;
    }
    
    private void activateScheduledOrder(UUID orderId) {
        Order order = orderRepo.findById(orderId);
        order.setState(OrderState.PLACED);
        orderRepo.update(order);
        
        // Trigger normal order flow
        eventBus.publish(new OrderPlacedEvent(order));
    }
}
```

**Q7: How do you handle courier location updates efficiently?**
**A:**
```java
// Batch updates to reduce DB writes
class CourierLocationService {
    private final Map<UUID, Location> locationBuffer = new ConcurrentHashMap<>();
    
    public void updateLocation(UUID courierId, Location location) {
        locationBuffer.put(courierId, location);
    }
    
    @Scheduled(fixedDelay = 10000) // Every 10 seconds
    public void flushLocations() {
        Map<UUID, Location> snapshot = new HashMap<>(locationBuffer);
        locationBuffer.clear();
        
        // Batch update DB
        courierRepo.batchUpdateLocations(snapshot);
        
        // Update geospatial index
        for (Map.Entry<UUID, Location> entry : snapshot.entrySet()) {
            geoIndex.updateLocation(entry.getKey(), entry.getValue());
        }
        
        // Publish events only for active deliveries
        List<Assignment> active = assignmentRepo.findActiveForCouriers(
            snapshot.keySet()
        );
        
        for (Assignment assignment : active) {
            Location courierLoc = snapshot.get(assignment.getCourierId());
            Duration eta = etaCalculator.calculateETA(
                courierLoc,
                assignment.getDeliveryAddress()
            );
            
            eventBus.publish(new ETAUpdatedEvent(
                assignment.getOrderId(),
                eta
            ));
        }
    }
}
```

**Q8: What metrics would you track?**
**A:**
```java
Metrics to track:
1. Order Success Rate: % of orders that reach DELIVERED state
2. Time to Confirm: Duration from PLACED to CONFIRMED
3. Restaurant Acceptance Rate: % accepted vs rejected
4. Courier Assignment Time: Duration to find courier
5. Courier Acceptance Rate: % couriers who accept first offer
6. Delivery Time Accuracy: Estimated vs actual delivery time
7. Payment Success Rate: % successful payment attempts
8. Refund Rate: % orders refunded
9. Saga Completion Time: End-to-end order flow duration
10. Event Processing Lag: Delay between event publish and consume

// Implementation
@Timed(value = "order.placement.duration")
@Counted("order.placement.total")
public Order placeOrder(OrderRequest request) { /* ... */ }

@Gauge(name = "order.active.count", description = "Active orders")
public long getActiveOrderCount() {
    return orderRepo.countByState(OrderState.IN_PROGRESS);
}
```

**Q9: How do you handle peak load (lunch rush)?**
**A:**
- **Auto-scaling**: Scale order service based on CPU/memory
- **Queue-based Load Leveling**: Accept orders to queue, process async
- **Degraded Mode**: Skip non-critical steps (analytics, recommendations)
- **Circuit Breaker**: Fail fast if downstream services overloaded
```java
// Queue-based processing
@RateLimiter(maxRequests = 1000, perSeconds = 1)
public OrderReceipt placeOrderAsync(OrderRequest request) {
    // Accept order quickly
    UUID orderId = UUID.randomUUID();
    
    // Queue for processing
    orderQueue.add(new QueuedOrder(orderId, request));
    
    // Return immediately
    return new OrderReceipt(
        orderId,
        "Order received, processing in progress"
    );
}

// Background worker processes queue
@Consumer(queue = "order-queue", concurrency = 50)
public void processQueuedOrder(QueuedOrder queued) {
    processOrder(queued.getRequest());
}
```

**Q10: How would you implement order modification (add/remove items before confirmation)?**
**A:**
```java
public Order modifyOrder(UUID orderId, OrderModification modification) {
    Order order = orderRepo.findById(orderId);
    
    // Can only modify before restaurant starts preparing
    if (!order.getState().isModifiable()) {
        throw new OrderNotModifiableException(
            "Cannot modify order in state: " + order.getState()
        );
    }
    
    // Apply modification
    Order modified = order.modify(modification);
    
    // Recalculate total
    Money newTotal = calculateTotal(modified.getItems());
    Money priceDifference = newTotal.subtract(order.getTotalAmount());
    
    if (priceDifference.isPositive()) {
        // Additional payment needed
        Payment additionalPayment = paymentService.processPayment(
            orderId,
            priceDifference,
            order.getPaymentMethod()
        );
        
        if (additionalPayment.getStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentFailedException("Additional payment failed");
        }
    } else if (priceDifference.isNegative()) {
        // Partial refund
        paymentService.refund(orderId, priceDifference.abs());
    }
    
    // Update order
    modified.setTotalAmount(newTotal);
    orderRepo.update(modified);
    
    // Publish event
    eventBus.publish(new OrderModifiedEvent(orderId, modification));
    
    // Notify restaurant
    restaurantService.notifyOrderModified(modified);
    
    return modified;
}
```

### 10. Extensions & Variations

1. **Group Orders**: Multiple customers split one order
2. **Subscription Meals**: Recurring daily/weekly deliveries
3. **Multi-restaurant Orders**: Order from multiple restaurants
4. **Live Order Tracking**: Real-time map view with courier location
5. **Smart Routing**: Optimize courier route for multiple pickups/drops
6. **Pre-order**: Place order hours in advance
7. **Contactless Delivery**: OTP-based verification

### 11. Testing Strategy

**Unit Tests:**
- State machine transitions validation
- Saga compensation logic
- Payment idempotency with duplicate keys
- Event handler idempotency

**Integration Tests:**
- End-to-end order flow (happy path)
- Compensation flow (restaurant rejection)
- Payment failure handling
- Outbox pattern reliability

**Chaos Testing:**
```java
@Test
public void testSagaResilienceWithRandomFailures() {
    ChaosMonkey chaos = new ChaosMonkey()
        .failPaymentService(probability = 0.2)
        .delayRestaurantService(probability = 0.3, delay = 10s)
        .killCourierService(probability = 0.1);
    
    // Place 100 orders
    for (int i = 0; i < 100; i++) {
        Order order = placeOrder(validRequest);
        
        // Wait for saga completion or compensation
        await().atMost(60, SECONDS).until(() -> {
            Order result = orderRepo.findById(order.getId());
            return result.getState().isTerminal(); // DELIVERED or CANCELLED
        });
        
        // Verify consistency: either fully completed or fully compensated
        Order final = orderRepo.findById(order.getId());
        if (final.getState() == OrderState.DELIVERED) {
            assertPaymentSucceeded(final);
            assertCourierAssigned(final);
        } else if (final.getState() == OrderState.CANCELLED) {
            assertRefundIssued(final);
            assertRestaurantNotified(final);
        }
    }
}
```

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Distributed transactions (2PC) - blocks availability
✅ **Do**: Saga pattern with eventual consistency

❌ **Avoid**: Synchronous service calls in order flow
✅ **Do**: Event-driven async communication

❌ **Avoid**: Direct event publishing without transactional guarantees
✅ **Do**: Outbox pattern for reliable event delivery

❌ **Avoid**: Non-idempotent event handlers
✅ **Do**: Design handlers to safely handle duplicate events

❌ **Avoid**: Tight coupling between order and payment services
✅ **Do**: Communicate via events, services remain independent

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Place Order | O(1) | O(1) | Just persist + queue event |
| Process Payment | O(1) | O(1) | Idempotency check + gateway call |
| Assign Courier | O(log n + k) | O(k) | Geo query + ranking |
| Update Status | O(1) | O(1) | Event append + projection update |
| Query Order | O(1) | - | Direct lookup from read model |
| Replay Events | O(E) | O(E) | E = event count per order |

**Space Complexity:**
- O(O) for O active orders
- O(E) for event store (append-only, grows forever)
- O(V) for read model views (one per order)

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Saga Understanding** | 25% | Correctly implements compensation, handles failures |
| **Event-Driven Design** | 20% | Proper event modeling, async communication |
| **Consistency Handling** | 20% | Idempotency, outbox pattern, duplicate handling |
| **State Management** | 15% | Clear state machine, valid transitions |
| **Scalability** | 10% | Discusses sharding, caching, async processing |
| **Real-world Awareness** | 10% | Considers timeouts, retries, monitoring |

**Red Flags:**
- Uses distributed transactions (2PC)
- No compensation logic for failures
- Non-idempotent event handlers
- Synchronous blocking service calls
- No consideration for duplicate events

---
