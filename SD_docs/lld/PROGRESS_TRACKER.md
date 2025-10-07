# LLD 100 Problems - Progress Tracker

## Batch 1: Core Domain / Everyday Systems (Problems 1-10)

### ✅ Completed Problems

| # | Problem | File | Status | Key Patterns |
|---|---------|------|--------|--------------|
| 1 | Parking Lot System | Problem_01_Parking_Lot_System.md | ✅ Existing | Strategy, State, Factory |
| 2 | **Elevator Control System** | Problem_02_Elevator_Control_System.md | ✅ NEW | State Machine, SCAN Algorithm, Strategy |
| 3 | **Library Management** | Problem_03_Library_Management.md | ✅ NEW | Repository, Domain Events, Optimistic Locking |
| 4 | **Hotel Booking Engine** | Problem_04_Hotel_Booking_Engine.md | ✅ NEW | CQRS, Overbooking Strategy, Dynamic Pricing |
| 5 | Ride Sharing Matching | Problem_05_Ride_Sharing_Matching.md | ✅ Existing | Geospatial, Matching Algorithm |
| 6 | Food Delivery Order Flow | Problem_06_Food_Delivery_Order_Flow.md | ✅ Existing | Saga, Event Sourcing, State Machine |
| 7 | E-commerce Cart & Checkout | Problem_07_Ecommerce_Cart_Checkout.md | ✅ Existing | Money Pattern, Idempotency |
| 8 | Inventory Reservation | Problem_08_Inventory_Reservation_Service.md | ✅ Existing | 2PC, Pessimistic/Optimistic Locking |

### ✅ Recently Completed

| # | Problem | File | Status | Key Patterns |
|---|---------|------|--------|--------------|
| 9 | **Inventory Reconciliation Differ** | Problem_09_Inventory_Reconciliation_Differ.md | ✅ NEW | Merkle Trees, Conflict Resolution, Idempotency |
| 10 | **Inventory Batching Allocator** | Problem_10_Inventory_Batching_Allocator.md | ✅ NEW | Bin Packing, Greedy Heuristics, Load Balancing |

**Batch 1 Status: 100% Complete (10/10) ✅**

---

## Batch 2: E-Commerce & Marketplaces (Problems 11-20)

### ✅ Completed Problems

| # | Problem | File | Status | Key Patterns |
|---|---------|------|--------|--------------|
| 11 | **Shopping Catalog Hierarchy** | Problem_11_Shopping_Catalog_Hierarchy.md | ✅ NEW | Composite, Path Materialization, Visitor |
| 12 | **Pricing Engine Multi-Tier Rules** | Problem_12_Pricing_Engine_Multi_Tier_Rules.md | ✅ NEW | Chain of Responsibility, Strategy, Immutable Context |
| 13 | **Coupon/Promotion Engine** | Problem_13_Coupon_Promotion_Engine.md | ✅ NEW | Specification, Strategy, Observer, Optimistic Locking |
| 14 | **Multi-Tenant Subscription Manager** | Problem_14_Multi_Tenant_Subscription_Manager.md | ✅ NEW | State Pattern, Template Method, Strategy (Proration) |
| 15 | **Order Fulfillment Workflow** | Problem_15_Order_Fulfillment_Workflow_Engine.md | ✅ NEW | State Machine, Command, Saga, Idempotency |

### 📋 Remaining in Batch 2 (Problems 16-20)

| # | Problem | Status | Next Step |
|---|---------|--------|-----------|
| 16 | Product Search with Filters | ⏳ Pending | Create design doc |
| 17 | Review & Rating System | ⏳ Pending | Create design doc |
| 18 | Wishlist & Favorites | ⏳ Pending | Create design doc |
| 19 | Flash Sale / Limited Inventory | ⏳ Pending | Create design doc |
| 20 | Recommendation Engine | ⏳ Pending | Create design doc |

**Batch 2 Status: 25% Complete (5/20)**

---

## Key Patterns Covered So Far

### Concurrency Patterns
- **Pessimistic Locking** (Problem 8): SELECT FOR UPDATE, row-level locks
- **Optimistic Locking** (Problems 3, 8): Version-based CAS
- **Two-Phase Commit** (Problem 8): Distributed transaction with compensation
- **State Machine** (Problems 2, 6): Explicit state transitions

### Domain Design Patterns
- **Repository** (Problem 3): Data access abstraction
- **Domain Events** (Problems 3, 6, 8): Decouple side effects
- **Aggregate** (Problem 3): Consistency boundary
- **Value Object** (Problem 3): ISBN, Money
- **CQRS** (Problem 4): Separate read/write models

### Scheduling & Algorithms
- **SCAN Algorithm** (Problem 2): Elevator scheduling
- **Priority Queue** (Problem 3): Hold management
- **Cost Function** (Problem 2): Multi-factor optimization
- **Dynamic Pricing** (Problem 4): Demand-based pricing

### Real-World Challenges
- **Double Booking Prevention** (Problems 3, 4): Race condition handling
- **Overbooking** (Problem 4): Statistical risk management
- **Starvation Prevention** (Problem 2): Priority aging
- **Fine Calculation** (Problem 3): Policy-based computation

---

## Next Batch 2 Preview (Problems 11-15)

11. Shopping Catalog (Composite)
12. Pricing Engine (Rules)
13. Coupon / Promotion Engine
14. Order State Machine
15. Subscription / Billing Lifecycle

---

---

## Batch 8: Game Engines & Interactive Systems (Problems 76-85)

### ✅ Completed Problems

| # | Problem | File | Status | Key Patterns |
|---|---------|------|--------|--------------|
| 76 | **Chess Engine Skeleton** | Problem_76_Chess_Engine_Skeleton.md / ChessEngine.java | ✅ NEW | Strategy, Factory, Command |
| 77 | **TicTacToe Engine** | TicTacToeEngine.java | ✅ NEW | State, Strategy, Template Method |
| 78 | **Snake Game Engine** | SnakeGameEngine.java | ✅ NEW | State, Command, Observer |
| 79 | **Minesweeper Engine** | MinesweeperEngine.java | ✅ NEW | State, Strategy, Observer, Flyweight |
| 80 | **Quiz/Exam Engine** | QuizEngine.java | ✅ NEW | Strategy, Factory, State, Template Method |
| 81 | **Quiz Adaptive Difficulty** | QuizEngine.java | ✅ NEW | Strategy (Adaptive), Observer |
| 82 | **Text Editor Undo/Redo** | TextEditorUndoRedo.java | ✅ NEW | Command, Memento, Composite |
| 83 | **Game State Persistence** | GameStatePersistence.java | ✅ NEW | Memento, Strategy, Template Method, Prototype |
| 84 | **Media Streaming Buffer** | MediaStreamingBufferController.java | ✅ NEW | Strategy, Observer, State |
| 85 | **Game Session Manager** | GameSessionStateManager.java | ✅ NEW | State, Strategy, Observer, Singleton |

**Batch 8 Status: 100% Complete (10/10) ✅**

### Key Algorithms & Techniques

#### Game Logic
- **Move Generation**: O(N) piece-based strategies
- **Win Detection**: O(N) for board scanning
- **Collision Detection**: O(N) self-collision, O(1) boundary
- **Cascade Reveal**: BFS/DFS for Minesweeper

#### State Management
- **Undo/Redo**: Command pattern with memento snapshots
- **Session Lifecycle**: Timeout detection, pause/resume
- **Persistence**: Serialization strategies, compression

#### Adaptive Systems
- **Difficulty Adjustment**: Sliding window performance analysis
- **Bitrate Adaptation**: Network-aware quality selection
- **Buffer Management**: Dynamic target sizing

#### Real-Time Systems
- **Media Buffering**: Adaptive streaming (ABR)
- **Timeout Management**: Scheduled background checks
- **Event Notifications**: Observer pattern for loose coupling

---

## Summary Statistics

**Total Problems Completed:** 25/100 (25%)
- Batch 1: 10/10 ✅
- Batch 2: 5/20 (partial)
- Batch 8: 10/10 ✅

**Last Updated:** October 7, 2025
