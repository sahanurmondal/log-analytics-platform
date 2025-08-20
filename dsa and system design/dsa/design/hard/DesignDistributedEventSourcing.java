package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Design Distributed Event Sourcing System
 * 
 * Description:
 * Design a distributed event sourcing system that captures all changes to
 * application
 * state as a sequence of events. The system should support event storage,
 * replay,
 * projections, and snapshots across multiple nodes with eventual consistency.
 * 
 * Requirements:
 * - Event capture and storage
 * - Event replay and state reconstruction
 * - Distributed event log with partitioning
 * - Event projections and views
 * - Snapshot management
 * - Eventual consistency guarantees
 * - Conflict resolution
 * - Event versioning and schema evolution
 * 
 * Key Features:
 * - Immutable event log
 * - Event sourcing patterns
 * - CQRS (Command Query Responsibility Segregation)
 * - Distributed consensus for ordering
 * - Projection materialization
 * - Event replay capabilities
 * 
 * Company Tags: Event Store, Apache Kafka, Amazon, Microsoft, Google
 * Difficulty: Hard
 */
public class DesignDistributedEventSourcing {

    static class Event {
        final String eventId;
        final String aggregateId;
        final String eventType;
        final Map<String, Object> eventData;
        final long timestamp;
        final long version;
        final String causationId;
        final String correlationId;
        final Map<String, String> metadata;

        public Event(String aggregateId, String eventType, Map<String, Object> eventData, long version) {
            this.eventId = UUID.randomUUID().toString();
            this.aggregateId = aggregateId;
            this.eventType = eventType;
            this.eventData = new HashMap<>(eventData);
            this.timestamp = System.currentTimeMillis();
            this.version = version;
            this.causationId = null;
            this.correlationId = UUID.randomUUID().toString();
            this.metadata = new HashMap<>();
        }

        public Event(String aggregateId, String eventType, Map<String, Object> eventData,
                long version, String causationId, String correlationId) {
            this.eventId = UUID.randomUUID().toString();
            this.aggregateId = aggregateId;
            this.eventType = eventType;
            this.eventData = new HashMap<>(eventData);
            this.timestamp = System.currentTimeMillis();
            this.version = version;
            this.causationId = causationId;
            this.correlationId = correlationId;
            this.metadata = new HashMap<>();
        }

        public Event addMetadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        @Override
        public String toString() {
            return String.format("Event{id=%s, aggregate=%s, type=%s, version=%d, timestamp=%d}",
                    eventId, aggregateId, eventType, version, timestamp);
        }
    }

    static class EventStream {
        private final String aggregateId;
        private final List<Event> events;
        private volatile long currentVersion;

        public EventStream(String aggregateId) {
            this.aggregateId = aggregateId;
            this.events = new ArrayList<>();
            this.currentVersion = 0;
        }

        public synchronized void appendEvent(Event event) {
            if (event.version != currentVersion + 1) {
                throw new IllegalStateException("Version mismatch. Expected: " + (currentVersion + 1) +
                        ", got: " + event.version);
            }

            events.add(event);
            currentVersion = event.version;
        }

        public synchronized List<Event> getEvents() {
            return new ArrayList<>(events);
        }

        public synchronized List<Event> getEventsFromVersion(long fromVersion) {
            return events.stream()
                    .filter(event -> event.version >= fromVersion)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        public long getCurrentVersion() {
            return currentVersion;
        }

        public String getAggregateId() {
            return aggregateId;
        }
    }

    static class Snapshot {
        final String snapshotId;
        final String aggregateId;
        final long version;
        final Map<String, Object> state;
        final long timestamp;

        public Snapshot(String aggregateId, long version, Map<String, Object> state) {
            this.snapshotId = UUID.randomUUID().toString();
            this.aggregateId = aggregateId;
            this.version = version;
            this.state = new HashMap<>(state);
            this.timestamp = System.currentTimeMillis();
        }
    }

    interface EventProjection {
        void apply(Event event);

        Map<String, Object> getState();

        void reset();

        String getProjectionName();
    }

    static class UserAccountProjection implements EventProjection {
        private final Map<String, Map<String, Object>> userAccounts = new ConcurrentHashMap<>();

        @Override
        public void apply(Event event) {
            switch (event.eventType) {
                case "UserCreated":
                    handleUserCreated(event);
                    break;
                case "UserUpdated":
                    handleUserUpdated(event);
                    break;
                case "UserDeleted":
                    handleUserDeleted(event);
                    break;
                case "BalanceChanged":
                    handleBalanceChanged(event);
                    break;
                default:
                    // Ignore unknown events
                    break;
            }
        }

        private void handleUserCreated(Event event) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", event.aggregateId);
            userData.put("email", event.eventData.get("email"));
            userData.put("name", event.eventData.get("name"));
            userData.put("balance", 0.0);
            userData.put("createdAt", event.timestamp);
            userData.put("version", event.version);

            userAccounts.put(event.aggregateId, userData);
        }

        private void handleUserUpdated(Event event) {
            Map<String, Object> userData = userAccounts.get(event.aggregateId);
            if (userData != null) {
                userData.putAll(event.eventData);
                userData.put("version", event.version);
                userData.put("updatedAt", event.timestamp);
            }
        }

        private void handleUserDeleted(Event event) {
            userAccounts.remove(event.aggregateId);
        }

        private void handleBalanceChanged(Event event) {
            Map<String, Object> userData = userAccounts.get(event.aggregateId);
            if (userData != null) {
                Double currentBalance = (Double) userData.get("balance");
                Double change = (Double) event.eventData.get("amount");
                userData.put("balance", currentBalance + change);
                userData.put("version", event.version);
                userData.put("lastBalanceChange", event.timestamp);
            }
        }

        @Override
        public Map<String, Object> getState() {
            return new HashMap<String, Object>(userAccounts);
        }

        @Override
        public void reset() {
            userAccounts.clear();
        }

        @Override
        public String getProjectionName() {
            return "UserAccountProjection";
        }
    }

    // Main Event Store
    static class EventStore {
        private final Map<String, EventStream> eventStreams;
        private final Map<String, Snapshot> snapshots;
        private final List<EventProjection> projections;
        private final ExecutorService projectionExecutor;
        private final AtomicLong globalEventCounter;

        public EventStore() {
            this.eventStreams = new ConcurrentHashMap<>();
            this.snapshots = new ConcurrentHashMap<>();
            this.projections = new ArrayList<>();
            this.projectionExecutor = Executors.newFixedThreadPool(4);
            this.globalEventCounter = new AtomicLong(0);
        }

        public void appendEvent(Event event) {
            EventStream stream = eventStreams.computeIfAbsent(event.aggregateId,
                    id -> new EventStream(id));

            try {
                stream.appendEvent(event);
                globalEventCounter.incrementAndGet();

                // Update projections asynchronously
                updateProjections(event);

            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException("Optimistic concurrency conflict: " + e.getMessage());
            }
        }

        public List<Event> getEvents(String aggregateId) {
            EventStream stream = eventStreams.get(aggregateId);
            return stream != null ? stream.getEvents() : new ArrayList<>();
        }

        public List<Event> getEventsFromVersion(String aggregateId, long fromVersion) {
            EventStream stream = eventStreams.get(aggregateId);
            return stream != null ? stream.getEventsFromVersion(fromVersion) : new ArrayList<>();
        }

        public long getCurrentVersion(String aggregateId) {
            EventStream stream = eventStreams.get(aggregateId);
            return stream != null ? stream.getCurrentVersion() : 0;
        }

        public void saveSnapshot(String aggregateId, Map<String, Object> state) {
            long version = getCurrentVersion(aggregateId);
            Snapshot snapshot = new Snapshot(aggregateId, version, state);
            snapshots.put(aggregateId, snapshot);
        }

        public Snapshot getSnapshot(String aggregateId) {
            return snapshots.get(aggregateId);
        }

        public void addProjection(EventProjection projection) {
            projections.add(projection);

            // Replay all existing events for new projection
            projectionExecutor.submit(() -> replayEventsForProjection(projection));
        }

        private void updateProjections(Event event) {
            for (EventProjection projection : projections) {
                projectionExecutor.submit(() -> {
                    try {
                        projection.apply(event);
                    } catch (Exception e) {
                        System.err.println("Error updating projection " + projection.getProjectionName() +
                                ": " + e.getMessage());
                    }
                });
            }
        }

        private void replayEventsForProjection(EventProjection projection) {
            projection.reset();

            for (EventStream stream : eventStreams.values()) {
                for (Event event : stream.getEvents()) {
                    try {
                        projection.apply(event);
                    } catch (Exception e) {
                        System.err.println("Error replaying event for projection " +
                                projection.getProjectionName() + ": " + e.getMessage());
                    }
                }
            }
        }

        public Map<String, Object> getProjectionState(String projectionName) {
            return projections.stream()
                    .filter(p -> p.getProjectionName().equals(projectionName))
                    .findFirst()
                    .map(EventProjection::getState)
                    .orElse(new HashMap<>());
        }

        public Map<String, Object> getEventStoreStats() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStreams", eventStreams.size());
            stats.put("totalEvents", globalEventCounter.get());
            stats.put("totalSnapshots", snapshots.size());
            stats.put("projectionsCount", projections.size());

            Map<String, Long> streamVersions = new HashMap<>();
            for (Map.Entry<String, EventStream> entry : eventStreams.entrySet()) {
                streamVersions.put(entry.getKey(), entry.getValue().getCurrentVersion());
            }
            stats.put("streamVersions", streamVersions);

            return stats;
        }

        public void shutdown() {
            projectionExecutor.shutdown();
            try {
                if (!projectionExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    projectionExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                projectionExecutor.shutdownNow();
            }
        }
    }

    // Aggregate Root base class
    static abstract class AggregateRoot {
        protected String id;
        protected long version;
        protected List<Event> uncommittedEvents;

        public AggregateRoot(String id) {
            this.id = id;
            this.version = 0;
            this.uncommittedEvents = new ArrayList<>();
        }

        protected void applyEvent(Event event) {
            uncommittedEvents.add(event);
            version = event.version;
            handleEvent(event);
        }

        protected abstract void handleEvent(Event event);

        public List<Event> getUncommittedEvents() {
            return new ArrayList<>(uncommittedEvents);
        }

        public void markEventsAsCommitted() {
            uncommittedEvents.clear();
        }

        public void loadFromHistory(List<Event> history) {
            for (Event event : history) {
                handleEvent(event);
                version = event.version;
            }
        }

        public String getId() {
            return id;
        }

        public long getVersion() {
            return version;
        }
    }

    // Example User Aggregate
    static class UserAggregate extends AggregateRoot {
        private String email;
        private String name;
        private double balance;
        private boolean isActive;

        public UserAggregate(String id) {
            super(id);
            this.isActive = false;
            this.balance = 0.0;
        }

        public void createUser(String email, String name) {
            if (isActive) {
                throw new IllegalStateException("User already exists");
            }

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("email", email);
            eventData.put("name", name);

            Event event = new Event(id, "UserCreated", eventData, version + 1);
            applyEvent(event);
        }

        public void updateUser(String newEmail, String newName) {
            if (!isActive) {
                throw new IllegalStateException("User does not exist");
            }

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("email", newEmail);
            eventData.put("name", newName);

            Event event = new Event(id, "UserUpdated", eventData, version + 1);
            applyEvent(event);
        }

        public void changeBalance(double amount) {
            if (!isActive) {
                throw new IllegalStateException("User does not exist");
            }

            if (balance + amount < 0) {
                throw new IllegalStateException("Insufficient balance");
            }

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("amount", amount);
            eventData.put("oldBalance", balance);
            eventData.put("newBalance", balance + amount);

            Event event = new Event(id, "BalanceChanged", eventData, version + 1);
            applyEvent(event);
        }

        public void deleteUser() {
            if (!isActive) {
                throw new IllegalStateException("User does not exist");
            }

            Event event = new Event(id, "UserDeleted", new HashMap<>(), version + 1);
            applyEvent(event);
        }

        @Override
        protected void handleEvent(Event event) {
            switch (event.eventType) {
                case "UserCreated":
                    this.email = (String) event.eventData.get("email");
                    this.name = (String) event.eventData.get("name");
                    this.isActive = true;
                    break;
                case "UserUpdated":
                    this.email = (String) event.eventData.get("email");
                    this.name = (String) event.eventData.get("name");
                    break;
                case "BalanceChanged":
                    Double amount = (Double) event.eventData.get("amount");
                    this.balance += amount;
                    break;
                case "UserDeleted":
                    this.isActive = false;
                    break;
                default:
                    // Ignore unknown events for forward compatibility
                    break;
            }
        }

        // Getters
        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public double getBalance() {
            return balance;
        }

        public boolean isActive() {
            return isActive;
        }

        @Override
        public String toString() {
            return String.format("User{id=%s, email=%s, name=%s, balance=%.2f, active=%s, version=%d}",
                    id, email, name, balance, isActive, version);
        }
    }

    // Repository for managing aggregates
    static class UserRepository {
        private final EventStore eventStore;

        public UserRepository(EventStore eventStore) {
            this.eventStore = eventStore;
        }

        public void save(UserAggregate user) {
            List<Event> uncommittedEvents = user.getUncommittedEvents();

            for (Event event : uncommittedEvents) {
                eventStore.appendEvent(event);
            }

            user.markEventsAsCommitted();
        }

        public UserAggregate load(String userId) {
            UserAggregate user = new UserAggregate(userId);

            // Try to load from snapshot first
            Snapshot snapshot = eventStore.getSnapshot(userId);
            long fromVersion = 0;

            if (snapshot != null) {
                // Load state from snapshot
                fromVersion = snapshot.version + 1;
                // In a real implementation, you'd restore aggregate state from snapshot
            }

            // Load events since snapshot
            List<Event> events = eventStore.getEventsFromVersion(userId, fromVersion);
            if (!events.isEmpty()) {
                user.loadFromHistory(events);
            }

            return user;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        EventStore eventStore = new EventStore();
        UserRepository userRepository = new UserRepository(eventStore);

        // Add projection
        UserAccountProjection accountProjection = new UserAccountProjection();
        eventStore.addProjection(accountProjection);

        System.out.println("=== Event Sourcing Demo ===");

        // Create users
        UserAggregate user1 = new UserAggregate("user-001");
        user1.createUser("alice@example.com", "Alice Smith");
        userRepository.save(user1);

        UserAggregate user2 = new UserAggregate("user-002");
        user2.createUser("bob@example.com", "Bob Johnson");
        userRepository.save(user2);

        System.out.println("Created users:");
        System.out.println("User 1: " + user1);
        System.out.println("User 2: " + user2);

        // Modify users
        user1.changeBalance(100.0);
        user1.updateUser("alice.smith@example.com", "Alice Johnson");
        userRepository.save(user1);

        user2.changeBalance(50.0);
        user2.changeBalance(-20.0);
        userRepository.save(user2);

        System.out.println("\nAfter modifications:");
        System.out.println("User 1: " + user1);
        System.out.println("User 2: " + user2);

        // Wait for projections to update
        Thread.sleep(1000);

        // Show event history
        System.out.println("\n=== Event History ===");
        List<Event> user1Events = eventStore.getEvents("user-001");
        System.out.println("User 1 events (" + user1Events.size() + "):");
        for (Event event : user1Events) {
            System.out.println("  " + event);
        }

        List<Event> user2Events = eventStore.getEvents("user-002");
        System.out.println("\nUser 2 events (" + user2Events.size() + "):");
        for (Event event : user2Events) {
            System.out.println("  " + event);
        }

        // Show projection state
        System.out.println("\n=== Projection State ===");
        Map<String, Object> projectionState = eventStore.getProjectionState("UserAccountProjection");
        System.out.println("User Account Projection:");
        for (Map.Entry<String, Object> entry : projectionState.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        // Test aggregate reconstruction
        System.out.println("\n=== Aggregate Reconstruction ===");
        UserAggregate reconstructedUser1 = userRepository.load("user-001");
        System.out.println("Reconstructed User 1: " + reconstructedUser1);

        UserAggregate reconstructedUser2 = userRepository.load("user-002");
        System.out.println("Reconstructed User 2: " + reconstructedUser2);

        // Create snapshot
        Map<String, Object> user1SnapshotState = new HashMap<>();
        user1SnapshotState.put("email", reconstructedUser1.getEmail());
        user1SnapshotState.put("name", reconstructedUser1.getName());
        user1SnapshotState.put("balance", reconstructedUser1.getBalance());
        user1SnapshotState.put("isActive", reconstructedUser1.isActive());

        eventStore.saveSnapshot("user-001", user1SnapshotState);
        System.out.println("\nSnapshot saved for user-001");

        // Show statistics
        System.out.println("\n=== Event Store Statistics ===");
        Map<String, Object> stats = eventStore.getEventStoreStats();
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        eventStore.shutdown();
    }
}
