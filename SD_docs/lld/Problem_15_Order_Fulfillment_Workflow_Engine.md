# Problem 15: Order Fulfillment Workflow Engine (State Machine + Command + Saga)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design an order fulfillment workflow that orchestrates inventory reservation, payment processing, shipping, and delivery tracking with rollback on failures.

**Assumptions / Scope:**
- Order flows through states: Placed → Validated → Reserved → Paid → Shipped → Delivered
- Each transition may fail (inventory unavailable, payment declined, shipping issues)
- Need rollback/compensation on failure
- Support split shipments (order from multiple warehouses)
- Track order history (who did what when)
- Scale: 10K orders/hour, 100K concurrent orders
- Out of scope: Returns/exchanges, real-time GPS tracking

**Non-Functional Goals:**
- Process order in < 500ms
- 99.9% fulfillment success rate
- 100% audit trail
- Idempotent operations (safe to retry)
- Support distributed transactions

### 2. Core Requirements

**Functional:**
- Create order with line items
- Validate order (stock availability, pricing, customer credit)
- Reserve inventory across warehouses
- Process payment with authorization/capture
- Create shipment(s) with tracking numbers
- Track delivery status
- Handle failures with compensation (release inventory, refund payment)
- Support order cancellation at various stages
- Generate order status timeline
- Notify customer at each stage

**Non-Functional:**
- **Performance**: < 500ms order processing
- **Reliability**: 99.9% success rate
- **Consistency**: Distributed transaction handling
- **Scalability**: 10K orders/hour
- **Observability**: Track each transition

### 3. Main Engineering Challenges & Solutions

**Challenge 1: State Machine for Order Workflow**
- **Problem**: Order transitions through complex states with validation
- **Solution**: Explicit state machine with transitions
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: State machine for order fulfillment
 */
enum OrderState {
    PLACED,
    VALIDATING,
    VALIDATED,
    RESERVING_INVENTORY,
    INVENTORY_RESERVED,
    PROCESSING_PAYMENT,
    PAYMENT_AUTHORIZED,
    CREATING_SHIPMENT,
    SHIPPED,
    IN_TRANSIT,
    DELIVERED,
    FAILED,
    CANCELLED;
    
    /**
     * Check if transition is valid
     */
    public boolean canTransitionTo(OrderState newState) {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(newState);
    }
    
    /**
     * Define valid state transitions
     */
    private static final Map<OrderState, Set<OrderState>> VALID_TRANSITIONS = Map.ofEntries(
        entry(PLACED, Set.of(VALIDATING, CANCELLED)),
        entry(VALIDATING, Set.of(VALIDATED, FAILED)),
        entry(VALIDATED, Set.of(RESERVING_INVENTORY, FAILED)),
        entry(RESERVING_INVENTORY, Set.of(INVENTORY_RESERVED, FAILED)),
        entry(INVENTORY_RESERVED, Set.of(PROCESSING_PAYMENT, CANCELLED)),
        entry(PROCESSING_PAYMENT, Set.of(PAYMENT_AUTHORIZED, FAILED)),
        entry(PAYMENT_AUTHORIZED, Set.of(CREATING_SHIPMENT, CANCELLED)),
        entry(CREATING_SHIPMENT, Set.of(SHIPPED, FAILED)),
        entry(SHIPPED, Set.of(IN_TRANSIT)),
        entry(IN_TRANSIT, Set.of(DELIVERED)),
        entry(DELIVERED, Set.of()),
        entry(FAILED, Set.of(VALIDATING)), // Can retry
        entry(CANCELLED, Set.of())
    );
}

/**
 * Order entity with state machine
 */
@Entity
public class Order {
    @Id
    private UUID id;
    
    private String orderId; // Human-readable
    
    @Enumerated(EnumType.STRING)
    private OrderState state;
    
    private UUID customerId;
    
    @OneToMany
    private List<OrderLineItem> lineItems;
    
    private BigDecimal totalAmount;
    
    @Embedded
    private ShippingAddress shippingAddress;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderStateTransition> stateHistory;
    
    private Instant placedAt;
    private Instant deliveredAt;
    
    @Version
    private long version; // Optimistic locking
    
    /**
     * INTERVIEW CRITICAL: Transition to new state
     */
    public void transitionTo(OrderState newState, String reason, String actorId) {
        if (!state.canTransitionTo(newState)) {
            throw new InvalidStateTransitionException(
                String.format("Cannot transition from %s to %s", state, newState)
            );
        }
        
        OrderState oldState = this.state;
        this.state = newState;
        
        // Record transition in history
        OrderStateTransition transition = OrderStateTransition.builder()
            .id(UUID.randomUUID())
            .fromState(oldState)
            .toState(newState)
            .reason(reason)
            .actorId(actorId)
            .timestamp(Instant.now())
            .build();
        
        stateHistory.add(transition);
    }
    
    /**
     * Get timeline of state changes
     */
    public List<OrderStateTransition> getTimeline() {
        return new ArrayList<>(stateHistory);
    }
}

/**
 * State transition history
 */
@Entity
@Value
@Builder
public class OrderStateTransition {
    @Id
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    private OrderState fromState;
    
    @Enumerated(EnumType.STRING)
    private OrderState toState;
    
    private String reason;
    private String actorId;
    private Instant timestamp;
    
    @Column(columnDefinition = "json")
    private String metadata; // Additional context
}
```

**Challenge 2: Command Pattern for Workflow Steps**
- **Problem**: Each workflow step (validate, reserve, pay) needs to be executable and reversible
- **Solution**: Command pattern with execute/compensate methods
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Command pattern for workflow steps
 */
interface WorkflowCommand {
    /**
     * Execute the command
     */
    CommandResult execute(Order order);
    
    /**
     * Compensate (rollback) the command
     */
    void compensate(Order order);
    
    /**
     * Check if command is idempotent
     */
    default boolean isIdempotent() {
        return true;
    }
}

/**
 * Validate order command
 */
class ValidateOrderCommand implements WorkflowCommand {
    private final InventoryService inventoryService;
    private final PricingService pricingService;
    private final CustomerService customerService;
    
    /**
     * INTERVIEW CRITICAL: Validate order
     */
    @Override
    public CommandResult execute(Order order) {
        // 1. Validate stock availability
        for (OrderLineItem item : order.getLineItems()) {
            int available = inventoryService.getAvailableQuantity(item.getSku());
            if (available < item.getQuantity()) {
                return CommandResult.failure(
                    "Insufficient stock for " + item.getSku()
                );
            }
        }
        
        // 2. Validate pricing
        BigDecimal calculatedTotal = pricingService.calculateTotal(order.getLineItems());
        if (!calculatedTotal.equals(order.getTotalAmount())) {
            return CommandResult.failure("Price mismatch");
        }
        
        // 3. Validate customer (credit limit, blocked status)
        if (!customerService.canPlaceOrder(order.getCustomerId())) {
            return CommandResult.failure("Customer cannot place order");
        }
        
        return CommandResult.success("Order validated");
    }
    
    @Override
    public void compensate(Order order) {
        // Validation is read-only, nothing to compensate
    }
}

/**
 * Reserve inventory command
 */
class ReserveInventoryCommand implements WorkflowCommand {
    private final InventoryService inventoryService;
    private final AllocationService allocationService;
    
    /**
     * INTERVIEW CRITICAL: Reserve inventory
     */
    @Override
    public CommandResult execute(Order order) {
        try {
            // Allocate inventory across warehouses
            AllocationResult allocation = allocationService.allocateOrder(order);
            
            if (!allocation.isFullyAllocated()) {
                return CommandResult.failure("Partial allocation not allowed");
            }
            
            // Reserve allocated inventory
            List<Reservation> reservations = new ArrayList<>();
            
            for (Allocation alloc : allocation.getAllocations()) {
                Reservation reservation = inventoryService.reserve(
                    alloc.getWarehouseId(),
                    alloc.getSku(),
                    alloc.getQuantity(),
                    order.getId()
                );
                
                reservations.add(reservation);
            }
            
            // Store reservations on order
            order.setInventoryReservations(reservations);
            
            return CommandResult.success("Inventory reserved")
                .withData("reservations", reservations);
            
        } catch (Exception e) {
            return CommandResult.failure("Reservation failed: " + e.getMessage());
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Compensate by releasing reservations
     */
    @Override
    public void compensate(Order order) {
        List<Reservation> reservations = order.getInventoryReservations();
        
        if (reservations != null) {
            for (Reservation reservation : reservations) {
                try {
                    inventoryService.releaseReservation(reservation.getId());
                } catch (Exception e) {
                    logger.error("Failed to release reservation {}", 
                               reservation.getId(), e);
                }
            }
        }
    }
}

/**
 * Process payment command
 */
class ProcessPaymentCommand implements WorkflowCommand {
    private final PaymentGateway paymentGateway;
    
    /**
     * INTERVIEW CRITICAL: Authorize payment
     */
    @Override
    public CommandResult execute(Order order) {
        try {
            // Authorize payment (doesn't capture yet)
            PaymentResult result = paymentGateway.authorize(
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getId()
            );
            
            if (result.isSuccess()) {
                order.setPaymentAuthorizationId(result.getAuthorizationId());
                
                return CommandResult.success("Payment authorized")
                    .withData("authorizationId", result.getAuthorizationId());
            } else {
                return CommandResult.failure(
                    "Payment declined: " + result.getDeclineReason()
                );
            }
            
        } catch (Exception e) {
            return CommandResult.failure("Payment error: " + e.getMessage());
        }
    }
    
    /**
     * Compensate by voiding authorization
     */
    @Override
    public void compensate(Order order) {
        String authId = order.getPaymentAuthorizationId();
        
        if (authId != null) {
            try {
                paymentGateway.voidAuthorization(authId);
            } catch (Exception e) {
                logger.error("Failed to void authorization {}", authId, e);
            }
        }
    }
}

/**
 * Create shipment command
 */
class CreateShipmentCommand implements WorkflowCommand {
    private final ShippingService shippingService;
    private final PaymentGateway paymentGateway;
    
    @Override
    public CommandResult execute(Order order) {
        try {
            // Capture payment (now that we're shipping)
            paymentGateway.capture(order.getPaymentAuthorizationId());
            
            // Create shipment(s)
            List<Shipment> shipments = shippingService.createShipments(order);
            
            order.setShipments(shipments);
            
            return CommandResult.success("Shipment created")
                .withData("shipments", shipments);
            
        } catch (Exception e) {
            return CommandResult.failure("Shipment creation failed: " + e.getMessage());
        }
    }
    
    @Override
    public void compensate(Order order) {
        // Refund captured payment
        String authId = order.getPaymentAuthorizationId();
        
        if (authId != null) {
            try {
                paymentGateway.refund(authId, order.getTotalAmount());
            } catch (Exception e) {
                logger.error("Failed to refund payment {}", authId, e);
            }
        }
    }
}

/**
 * Command result
 */
@Value
@Builder
public class CommandResult {
    private boolean success;
    private String message;
    private Map<String, Object> data;
    
    public static CommandResult success(String message) {
        return CommandResult.builder()
            .success(true)
            .message(message)
            .data(new HashMap<>())
            .build();
    }
    
    public static CommandResult failure(String message) {
        return CommandResult.builder()
            .success(false)
            .message(message)
            .data(new HashMap<>())
            .build();
    }
    
    public CommandResult withData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
```

**Challenge 3: Saga Pattern for Distributed Transaction**
- **Problem**: Coordinate multiple services (inventory, payment, shipping) with compensation
- **Solution**: Saga orchestrator with compensation on failure
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Saga orchestrator for order fulfillment
 */
@Service
public class OrderFulfillmentSaga {
    private final OrderRepository orderRepo;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Define workflow steps
     */
    private final List<WorkflowStep> steps = List.of(
        new WorkflowStep("validate", new ValidateOrderCommand(), 
                        OrderState.VALIDATING, OrderState.VALIDATED),
        new WorkflowStep("reserve", new ReserveInventoryCommand(),
                        OrderState.RESERVING_INVENTORY, OrderState.INVENTORY_RESERVED),
        new WorkflowStep("payment", new ProcessPaymentCommand(),
                        OrderState.PROCESSING_PAYMENT, OrderState.PAYMENT_AUTHORIZED),
        new WorkflowStep("shipment", new CreateShipmentCommand(),
                        OrderState.CREATING_SHIPMENT, OrderState.SHIPPED)
    );
    
    /**
     * Execute saga workflow
     */
    @Transactional
    public void executeFulfillment(UUID orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        
        List<WorkflowStep> executedSteps = new ArrayList<>();
        
        try {
            for (WorkflowStep step : steps) {
                // Transition to in-progress state
                order.transitionTo(step.getInProgressState(), 
                                 "Starting " + step.getName(), 
                                 "SYSTEM");
                orderRepo.save(order);
                
                // Execute command
                CommandResult result = step.getCommand().execute(order);
                
                if (result.isSuccess()) {
                    // Transition to success state
                    order.transitionTo(step.getSuccessState(),
                                     result.getMessage(),
                                     "SYSTEM");
                    orderRepo.save(order);
                    
                    executedSteps.add(step);
                    
                    // Publish event
                    publishStepCompleted(order, step, result);
                    
                } else {
                    // Step failed - start compensation
                    throw new WorkflowException(result.getMessage());
                }
            }
            
            // All steps succeeded
            order.transitionTo(OrderState.SHIPPED, "Order fulfilled", "SYSTEM");
            orderRepo.save(order);
            
            eventPublisher.publishEvent(new OrderFulfilledEvent(orderId));
            
        } catch (Exception e) {
            // Compensate all executed steps in reverse order
            compensate(order, executedSteps);
            
            order.transitionTo(OrderState.FAILED, 
                             "Fulfillment failed: " + e.getMessage(),
                             "SYSTEM");
            orderRepo.save(order);
            
            eventPublisher.publishEvent(new OrderFailedEvent(orderId, e.getMessage()));
            
            throw e;
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Compensate executed steps
     */
    private void compensate(Order order, List<WorkflowStep> executedSteps) {
        // Compensate in reverse order
        ListIterator<WorkflowStep> iterator = 
            executedSteps.listIterator(executedSteps.size());
        
        while (iterator.hasPrevious()) {
            WorkflowStep step = iterator.previous();
            
            try {
                logger.info("Compensating step: {}", step.getName());
                step.getCommand().compensate(order);
                
                order.transitionTo(OrderState.FAILED,
                                 "Compensated " + step.getName(),
                                 "SYSTEM");
                
            } catch (Exception e) {
                logger.error("Compensation failed for step {}", 
                           step.getName(), e);
                // Continue compensating other steps
            }
        }
    }
    
    private void publishStepCompleted(Order order, WorkflowStep step, 
                                     CommandResult result) {
        eventPublisher.publishEvent(new WorkflowStepCompletedEvent(
            order.getId(),
            step.getName(),
            step.getSuccessState(),
            result.getData()
        ));
    }
}

/**
 * Workflow step definition
 */
@Value
class WorkflowStep {
    String name;
    WorkflowCommand command;
    OrderState inProgressState;
    OrderState successState;
}
```

**Challenge 4: Idempotent Workflow Execution**
- **Problem**: Handle duplicate execution requests (retries, network issues)
- **Solution**: Idempotency keys and state checking
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Idempotent workflow execution
 */
@Service
public class IdempotentWorkflowService {
    private final OrderRepository orderRepo;
    private final OrderFulfillmentSaga saga;
    private final IdempotencyKeyRepository idempotencyRepo;
    
    /**
     * Execute workflow idempotently
     */
    @Transactional
    public WorkflowResult executeIdempotently(UUID orderId, String idempotencyKey) {
        // Check if already processed
        Optional<IdempotencyRecord> existing = 
            idempotencyRepo.findByKey(idempotencyKey);
        
        if (existing.isPresent()) {
            // Return cached result
            return existing.get().getResult();
        }
        
        // Check current order state
        Order order = orderRepo.findById(orderId).orElseThrow();
        
        // If already in terminal state, return success
        if (order.getState() == OrderState.SHIPPED || 
            order.getState() == OrderState.DELIVERED) {
            return WorkflowResult.success("Already fulfilled");
        }
        
        // If in failed/cancelled state, can retry
        if (order.getState() == OrderState.FAILED) {
            // Reset to initial state
            order.transitionTo(OrderState.VALIDATING, "Retrying", "SYSTEM");
            orderRepo.save(order);
        }
        
        try {
            // Execute saga
            saga.executeFulfillment(orderId);
            
            WorkflowResult result = WorkflowResult.success("Fulfilled");
            
            // Store idempotency record
            IdempotencyRecord record = IdempotencyRecord.builder()
                .key(idempotencyKey)
                .orderId(orderId)
                .result(result)
                .createdAt(Instant.now())
                .build();
            
            idempotencyRepo.save(record);
            
            return result;
            
        } catch (Exception e) {
            WorkflowResult result = WorkflowResult.failure(e.getMessage());
            
            // Store failure for idempotency
            IdempotencyRecord record = IdempotencyRecord.builder()
                .key(idempotencyKey)
                .orderId(orderId)
                .result(result)
                .createdAt(Instant.now())
                .build();
            
            idempotencyRepo.save(record);
            
            return result;
        }
    }
}

/**
 * Idempotency record
 */
@Entity
public class IdempotencyRecord {
    @Id
    private UUID id;
    
    @Column(unique = true)
    private String key;
    
    private UUID orderId;
    
    @Column(columnDefinition = "json")
    private WorkflowResult result;
    
    private Instant createdAt;
    
    @Column(name = "expires_at")
    private Instant expiresAt; // Clean up old records
}
```

**Challenge 5: Async Workflow with Dead Letter Queue**
- **Problem**: Handle long-running operations without blocking
- **Solution**: Message queue with retry and DLQ
- **Algorithm**:
```java
/**
 * Async workflow processor
 */
@Service
public class AsyncWorkflowProcessor {
    
    @RabbitListener(queues = "order-fulfillment-queue")
    public void processOrder(OrderFulfillmentMessage message) {
        try {
            idempotentWorkflowService.executeIdempotently(
                message.getOrderId(),
                message.getIdempotencyKey()
            );
            
        } catch (Exception e) {
            // Retry with exponential backoff
            if (message.getRetryCount() < MAX_RETRIES) {
                // Requeue with delay
                message.incrementRetryCount();
                retryTemplate.execute(context -> {
                    rabbitTemplate.send("order-fulfillment-queue", message);
                    return null;
                });
                
            } else {
                // Move to dead letter queue
                rabbitTemplate.send("order-fulfillment-dlq", message);
                
                // Alert operations team
                alertService.sendDLQAlert(message);
            }
        }
    }
    
    /**
     * Process dead letter queue manually
     */
    @RabbitListener(queues = "order-fulfillment-dlq")
    public void processDLQ(OrderFulfillmentMessage message) {
        logger.error("Order in DLQ: {}", message.getOrderId());
        
        // Manual intervention required
        // Store for admin dashboard
        dlqRecordRepo.save(new DLQRecord(message));
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **State Machine** | Order lifecycle | Enforce valid transitions |
| **Command** | Workflow steps | Encapsulate execute/compensate |
| **Saga** | Distributed transaction | Coordinate with compensation |
| **Observer** | State change events | Decouple notifications |
| **Template Method** | Workflow execution | Common flow, variant steps |
| **Strategy** | Different fulfillment strategies | Swap based on order type |

### 5. Domain Model & Class Structure

```
           Order
         - state ──────────> OrderState (enum)
         - stateHistory      
              │
              │ executes
              ▼
      OrderFulfillmentSaga
              │
              │ uses
              ▼
       WorkflowCommand (interface)
              ▲
              │
    ┌─────────┼─────────┬──────────┐
    │         │         │          │
 Validate  Reserve  Payment  Shipment
```

### 6. Detailed Sequence Diagrams

**Sequence: Order Fulfillment with Failure**
```
Client  Saga  ValidateCmd  ReserveCmd  PaymentCmd  Compensate
  │      │         │            │           │           │
  ├─fulfill>│      │            │           │           │
  │      ├─execute─>│           │           │           │
  │      │<─success─┤           │           │           │
  │      ├─execute──────────────>│          │           │
  │      │<─success──────────────┤           │           │
  │      ├─execute────────────────────────>│           │
  │      │<─failure──────────────────────────┤           │
  │      ├─compensate────────────>│          │           │
  │      ├─compensate─>│           │          │           │
  │<─failed┤         │            │           │           │
```

### 7. Core Implementation (Interview-Critical Code)

```java
// Order line item
@Entity
public class OrderLineItem {
    @Id
    private UUID id;
    
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}

// Shipping address
@Embeddable
public class ShippingAddress {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}

// Reservation
@Entity
public class Reservation {
    @Id
    private UUID id;
    
    private UUID orderId;
    private UUID warehouseId;
    private String sku;
    private int quantity;
    private Instant expiresAt;
}

// Shipment
@Entity
public class Shipment {
    @Id
    private UUID id;
    
    private UUID orderId;
    private String trackingNumber;
    private String carrier;
    
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;
    
    private Instant shippedAt;
    private Instant estimatedDelivery;
    private Instant actualDelivery;
}
```

### 8. Thread Safety & Concurrency

**State Transitions:**
- Optimistic locking (@Version)
- Validate transition before applying
- Transactional boundaries

**Saga Execution:**
- Single thread per order
- Message queue ensures sequential processing
- Idempotency prevents duplicate execution

**Compensation:**
- Best-effort compensation
- Log compensation failures
- Manual intervention for critical failures

### 9. Top Interview Questions & Answers

**Q1: Why Saga pattern?**
**A:**
```
Saga vs 2PC:
- Saga: Eventually consistent, compensating transactions
- 2PC: Strongly consistent, but blocks resources

Saga advantages:
1. No distributed locks
2. Better availability
3. Can span long-running operations
4. Compensating transactions more flexible

Use Saga when:
- Cross-service transactions
- Long-running workflows
- Availability > consistency
```

**Q2: How to handle compensation failures?**
**A:**
```java
private void compensate(Order order, List<WorkflowStep> steps) {
    List<CompensationFailure> failures = new ArrayList<>();
    
    for (WorkflowStep step : reverse(steps)) {
        try {
            step.getCommand().compensate(order);
        } catch (Exception e) {
            // Log but continue
            failures.add(new CompensationFailure(step.getName(), e));
        }
    }
    
    if (!failures.isEmpty()) {
        // Alert operations team
        alertService.sendCompensationAlert(order.getId(), failures);
        
        // Store for manual resolution
        compensationFailureRepo.saveAll(failures);
    }
}
```

**Q3: Database schema?**
**A:**
```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_id VARCHAR(50) UNIQUE NOT NULL,
    state VARCHAR(30) NOT NULL,
    customer_id UUID NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_authorization_id VARCHAR(100),
    placed_at TIMESTAMP NOT NULL,
    delivered_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_orders_customer 
    ON orders(customer_id);
CREATE INDEX idx_orders_state 
    ON orders(state, placed_at);

CREATE TABLE order_state_transitions (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    from_state VARCHAR(30) NOT NULL,
    to_state VARCHAR(30) NOT NULL,
    reason TEXT,
    actor_id VARCHAR(100),
    timestamp TIMESTAMP NOT NULL,
    metadata JSON
);

CREATE INDEX idx_transitions_order 
    ON order_state_transitions(order_id, timestamp);

CREATE TABLE idempotency_records (
    id UUID PRIMARY KEY,
    key VARCHAR(255) UNIQUE NOT NULL,
    order_id UUID REFERENCES orders(id),
    result JSON NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_idempotency_expires 
    ON idempotency_records(expires_at);
```

**Q4: How to test saga compensation?**
**A:**
```java
@Test
public void testCompensation() {
    // Mock payment command to fail
    when(paymentCommand.execute(any()))
        .thenReturn(CommandResult.failure("Payment declined"));
    
    // Execute saga
    assertThrows(WorkflowException.class, () -> {
        saga.executeFulfillment(orderId);
    });
    
    // Verify compensation called
    verify(reserveCommand).compensate(any());
    verify(validateCommand, never()).compensate(any()); // Read-only
    
    // Verify order state
    Order order = orderRepo.findById(orderId).orElseThrow();
    assertEquals(OrderState.FAILED, order.getState());
    
    // Verify inventory released
    verify(inventoryService).releaseReservation(any());
}
```

**Q5: How to handle split shipments?**
**A:**
```java
class CreateShipmentCommand implements WorkflowCommand {
    @Override
    public CommandResult execute(Order order) {
        List<Reservation> reservations = order.getInventoryReservations();
        
        // Group by warehouse
        Map<UUID, List<Reservation>> byWarehouse = reservations.stream()
            .collect(Collectors.groupingBy(Reservation::getWarehouseId));
        
        List<Shipment> shipments = new ArrayList<>();
        
        for (Map.Entry<UUID, List<Reservation>> entry : byWarehouse.entrySet()) {
            UUID warehouseId = entry.getKey();
            List<Reservation> warehouseReservations = entry.getValue();
            
            // Create shipment for this warehouse
            Shipment shipment = shippingService.createShipment(
                order.getId(),
                warehouseId,
                warehouseReservations
            );
            
            shipments.add(shipment);
        }
        
        order.setShipments(shipments);
        
        return CommandResult.success("Created " + shipments.size() + " shipments");
    }
}
```

**Q6: Performance optimization?**
**A:**
```java
// 1. Async saga execution
@Async
public CompletableFuture<WorkflowResult> executeFulfillmentAsync(UUID orderId) {
    return CompletableFuture.supplyAsync(() -> {
        return idempotentWorkflowService.executeIdempotently(
            orderId,
            UUID.randomUUID().toString()
        );
    });
}

// 2. Parallel validation
@Override
public CommandResult execute(Order order) {
    CompletableFuture<Boolean> stockCheck = 
        CompletableFuture.supplyAsync(() -> validateStock(order));
    
    CompletableFuture<Boolean> pricingCheck = 
        CompletableFuture.supplyAsync(() -> validatePricing(order));
    
    CompletableFuture<Boolean> customerCheck = 
        CompletableFuture.supplyAsync(() -> validateCustomer(order));
    
    // Wait for all
    CompletableFuture.allOf(stockCheck, pricingCheck, customerCheck).join();
    
    // Check results...
}

// 3. Cache frequently accessed data
@Cacheable(value = "customer-status", key = "#customerId")
public boolean canPlaceOrder(UUID customerId) {
    return customerRepo.findById(customerId)
        .map(Customer::isActive)
        .orElse(false);
}
```

**Q7: What metrics to track?**
**A:**
```
Operational Metrics:
1. Order fulfillment rate (% successful)
2. Average fulfillment time
3. Compensation rate (% orders requiring rollback)
4. Step failure rates (which step fails most)
5. DLQ size

Business Metrics:
- Order volume (orders/hour)
- Revenue per order
- Cancellation rate
- Time to ship (placed → shipped)
```

**Q8: How to handle order cancellation?**
**A:**
```java
@Transactional
public void cancelOrder(UUID orderId, String reason) {
    Order order = orderRepo.findById(orderId).orElseThrow();
    
    // Can only cancel before shipped
    if (order.getState().ordinal() >= OrderState.SHIPPED.ordinal()) {
        throw new CannotCancelException("Order already shipped");
    }
    
    // Compensate based on current state
    switch (order.getState()) {
        case PAYMENT_AUTHORIZED:
            // Void payment authorization
            paymentGateway.voidAuthorization(
                order.getPaymentAuthorizationId()
            );
            // Fall through
            
        case INVENTORY_RESERVED:
            // Release inventory
            for (Reservation res : order.getInventoryReservations()) {
                inventoryService.releaseReservation(res.getId());
            }
            break;
    }
    
    // Transition to cancelled
    order.transitionTo(OrderState.CANCELLED, reason, "CUSTOMER");
    orderRepo.save(order);
    
    eventPublisher.publishEvent(new OrderCancelledEvent(orderId));
}
```

**Q9: How to implement timeout handling?**
**A:**
```java
@Service
public class OrderTimeoutService {
    
    @Scheduled(fixedDelay = 60000) // Every minute
    public void checkTimeouts() {
        Instant timeout = Instant.now().minus(Duration.ofMinutes(30));
        
        // Find orders stuck in intermediate states
        List<Order> stuckOrders = orderRepo.findByStateInAndUpdatedAtBefore(
            List.of(
                OrderState.VALIDATING,
                OrderState.RESERVING_INVENTORY,
                OrderState.PROCESSING_PAYMENT
            ),
            timeout
        );
        
        for (Order order : stuckOrders) {
            logger.warn("Order {} stuck in state {}", 
                       order.getId(), order.getState());
            
            // Retry or fail
            try {
                saga.executeFulfillment(order.getId());
            } catch (Exception e) {
                order.transitionTo(OrderState.FAILED, 
                                 "Timeout: " + e.getMessage(),
                                 "SYSTEM");
                orderRepo.save(order);
            }
        }
    }
}
```

**Q10: How to generate order timeline?**
**A:**
```java
public OrderTimeline getTimeline(UUID orderId) {
    Order order = orderRepo.findById(orderId).orElseThrow();
    
    List<TimelineEntry> entries = order.getStateHistory().stream()
        .map(transition -> TimelineEntry.builder()
            .timestamp(transition.getTimestamp())
            .state(transition.getToState())
            .description(formatDescription(transition))
            .actor(transition.getActorId())
            .build())
        .collect(Collectors.toList());
    
    return OrderTimeline.builder()
        .orderId(orderId)
        .entries(entries)
        .build();
}

private String formatDescription(OrderStateTransition transition) {
    return String.format("%s → %s: %s",
        transition.getFromState(),
        transition.getToState(),
        transition.getReason()
    );
}
```

### 10. Extensions & Variations

1. **Pre-order Handling**: Reserve future inventory
2. **Subscription Orders**: Recurring fulfillment
3. **Gift Orders**: Special packaging, gift messages
4. **International Orders**: Customs, duties
5. **Real-time Tracking**: GPS updates from carriers

### 11. Testing Strategy

**Unit Tests:**
- State machine transitions
- Command execute/compensate
- Idempotency logic

**Integration Tests:**
- Full saga execution
- Compensation flow
- Message queue processing

**Performance Tests:**
- 10K orders/hour
- 500ms fulfillment latency
- Concurrent order processing

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: 2PC distributed transactions
✅ **Do**: Saga with compensation

❌ **Avoid**: Blocking synchronous workflow
✅ **Do**: Async message-driven

❌ **Avoid**: No idempotency
✅ **Do**: Idempotency keys

❌ **Avoid**: Ignoring compensation failures
✅ **Do**: Alert and manual resolution

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Execute saga | O(N) | O(N) | N = workflow steps |
| Compensate | O(N) | O(N) | Reverse order |
| State transition | O(1) | O(1) | Simple update |
| Get timeline | O(T) | O(T) | T = transitions |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **State Machine** | 30% | Valid transitions, enforcement |
| **Saga Pattern** | 25% | Compensation logic |
| **Command Pattern** | 20% | Execute/compensate separation |
| **Idempotency** | 15% | Duplicate handling |
| **Real-world Awareness** | 10% | Timeouts, DLQ, metrics |

**Red Flags:**
- No state validation
- Missing compensation
- Ignoring idempotency
- Blocking operations
- No audit trail

---
