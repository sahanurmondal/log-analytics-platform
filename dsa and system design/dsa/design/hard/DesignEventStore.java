package design.hard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Design Event Store System
 *
 * Description: Design an event store that supports:
 * - Store events with timestamps
 * - Query events by time range
 * - Subscribe to event streams
 * - Event aggregation and projections
 * - Replay events from specific points
 * 
 * Constraints:
 * - Events are immutable once stored
 * - Support millions of events
 * - Low latency queries
 *
 * Follow-up:
 * - How to handle event versioning?
 * - Distributed event store?
 * 
 * Time Complexity: O(log n) for queries, O(1) for append
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Amazon, Microsoft
 */
public class DesignEventStore {

    class Event {
        String eventId;
        String streamId;
        String eventType;
        String data;
        Map<String, String> metadata;
        long timestamp;
        long version;

        Event(String streamId, String eventType, String data, Map<String, String> metadata) {
            this.eventId = UUID.randomUUID().toString();
            this.streamId = streamId;
            this.eventType = eventType;
            this.data = data;
            this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
            this.timestamp = System.currentTimeMillis();
        }
    }

    interface EventSubscriber {
        void onEvent(Event event);
    }

    class EventStream {
        String streamId;
        List<Event> events;
        AtomicLong version;
        Map<String, List<EventSubscriber>> subscribers; // eventType -> subscribers

        EventStream(String streamId) {
            this.streamId = streamId;
            this.events = new ArrayList<>();
            this.version = new AtomicLong(0);
            this.subscribers = new ConcurrentHashMap<>();
        }

        synchronized long appendEvent(Event event) {
            event.version = version.incrementAndGet();
            events.add(event);

            // Notify subscribers
            notifySubscribers(event);

            return event.version;
        }

        private void notifySubscribers(Event event) {
            // Notify specific event type subscribers
            List<EventSubscriber> typeSubscribers = subscribers.get(event.eventType);
            if (typeSubscribers != null) {
                for (EventSubscriber subscriber : typeSubscribers) {
                    try {
                        subscriber.onEvent(event);
                    } catch (Exception e) {
                        System.err.println("Subscriber error: " + e.getMessage());
                    }
                }
            }

            // Notify all event subscribers
            List<EventSubscriber> allSubscribers = subscribers.get("*");
            if (allSubscribers != null) {
                for (EventSubscriber subscriber : allSubscribers) {
                    try {
                        subscriber.onEvent(event);
                    } catch (Exception e) {
                        System.err.println("Subscriber error: " + e.getMessage());
                    }
                }
            }
        }

        List<Event> getEvents(long fromVersion, int limit) {
            List<Event> result = new ArrayList<>();
            int start = (int) Math.max(0, fromVersion - 1);
            int end = Math.min(events.size(), start + limit);

            for (int i = start; i < end; i++) {
                result.add(events.get(i));
            }

            return result;
        }

        List<Event> getEventsByTimeRange(long startTime, long endTime) {
            List<Event> result = new ArrayList<>();

            for (Event event : events) {
                if (event.timestamp >= startTime && event.timestamp <= endTime) {
                    result.add(event);
                }
            }

            return result;
        }
    }

    private Map<String, EventStream> streams;
    private TreeMap<Long, List<Event>> timeIndex;
    private Map<String, List<Event>> typeIndex;

    public DesignEventStore() {
        streams = new ConcurrentHashMap<>();
        timeIndex = new TreeMap<>();
        typeIndex = new ConcurrentHashMap<>();
    }

    public long appendEvent(String streamId, String eventType, String data, Map<String, String> metadata) {
        EventStream stream = streams.computeIfAbsent(streamId, EventStream::new);
        Event event = new Event(streamId, eventType, data, metadata);

        long version = stream.appendEvent(event);

        // Update indexes
        synchronized (timeIndex) {
            timeIndex.computeIfAbsent(event.timestamp, k -> new ArrayList<>()).add(event);
        }

        synchronized (typeIndex) {
            typeIndex.computeIfAbsent(eventType, k -> new ArrayList<>()).add(event);
        }

        return version;
    }

    public List<Event> getStreamEvents(String streamId, long fromVersion, int limit) {
        EventStream stream = streams.get(streamId);
        if (stream == null) {
            return new ArrayList<>();
        }

        return stream.getEvents(fromVersion, limit);
    }

    public List<Event> getStreamEventsByTime(String streamId, long startTime, long endTime) {
        EventStream stream = streams.get(streamId);
        if (stream == null) {
            return new ArrayList<>();
        }

        return stream.getEventsByTimeRange(startTime, endTime);
    }

    public List<Event> getEventsByTimeRange(long startTime, long endTime) {
        List<Event> result = new ArrayList<>();

        synchronized (timeIndex) {
            NavigableMap<Long, List<Event>> subMap = timeIndex.subMap(startTime, true, endTime, true);
            for (List<Event> events : subMap.values()) {
                result.addAll(events);
            }
        }

        return result;
    }

    public List<Event> getEventsByType(String eventType, int limit) {
        List<Event> events = typeIndex.get(eventType);
        if (events == null) {
            return new ArrayList<>();
        }

        synchronized (events) {
            int start = Math.max(0, events.size() - limit);
            return new ArrayList<>(events.subList(start, events.size()));
        }
    }

    public void subscribe(String streamId, String eventType, EventSubscriber subscriber) {
        EventStream stream = streams.computeIfAbsent(streamId, EventStream::new);
        stream.subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    public void subscribeToAll(String streamId, EventSubscriber subscriber) {
        subscribe(streamId, "*", subscriber);
    }

    public Map<String, Object> getStreamInfo(String streamId) {
        EventStream stream = streams.get(streamId);
        if (stream == null) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();
        info.put("streamId", streamId);
        info.put("version", stream.version.get());
        info.put("eventCount", stream.events.size());
        info.put("subscriberCount", stream.subscribers.values().stream().mapToInt(List::size).sum());

        if (!stream.events.isEmpty()) {
            info.put("firstEventTime", stream.events.get(0).timestamp);
            info.put("lastEventTime", stream.events.get(stream.events.size() - 1).timestamp);
        }

        return info;
    }

    public List<Event> replayEvents(String streamId, long fromVersion, EventSubscriber subscriber) {
        List<Event> events = getStreamEvents(streamId, fromVersion, Integer.MAX_VALUE);

        for (Event event : events) {
            try {
                subscriber.onEvent(event);
            } catch (Exception e) {
                System.err.println("Replay error: " + e.getMessage());
            }
        }

        return events;
    }

    public Map<String, Integer> getEventTypeStatistics() {
        Map<String, Integer> stats = new HashMap<>();

        for (List<Event> events : typeIndex.values()) {
            for (Event event : events) {
                stats.put(event.eventType, stats.getOrDefault(event.eventType, 0) + 1);
            }
        }

        return stats;
    }

    public static void main(String[] args) {
        DesignEventStore eventStore = new DesignEventStore();

        // Create a subscriber
        EventSubscriber userSubscriber = new EventSubscriber() {
            @Override
            public void onEvent(Event event) {
                System.out.println("Received event: " + event.eventType + " - " + event.data);
            }
        };

        // Subscribe to user events
        eventStore.subscribe("user-123", "UserCreated", userSubscriber);
        eventStore.subscribe("user-123", "UserUpdated", userSubscriber);

        // Append events
        Map<String, String> metadata = new HashMap<>();
        metadata.put("source", "web-app");
        metadata.put("userId", "123");

        long v1 = eventStore.appendEvent("user-123", "UserCreated",
                "{\"name\":\"John\",\"email\":\"john@example.com\"}", metadata);

        long v2 = eventStore.appendEvent("user-123", "UserUpdated",
                "{\"name\":\"John Doe\",\"email\":\"john@example.com\"}", metadata);

        long v3 = eventStore.appendEvent("user-123", "UserLoggedIn",
                "{\"timestamp\":\"2024-01-01T10:00:00Z\"}", metadata);

        System.out.println("Events appended with versions: " + v1 + ", " + v2 + ", " + v3);

        // Query events
        List<Event> streamEvents = eventStore.getStreamEvents("user-123", 1, 10);
        System.out.println("\nStream events count: " + streamEvents.size());

        // Get events by type
        List<Event> userCreatedEvents = eventStore.getEventsByType("UserCreated", 10);
        System.out.println("UserCreated events count: " + userCreatedEvents.size());

        // Get stream info
        System.out.println("\nStream info: " + eventStore.getStreamInfo("user-123"));

        // Get event type statistics
        System.out.println("Event type statistics: " + eventStore.getEventTypeStatistics());

        // Replay events
        System.out.println("\nReplaying events from version 2:");
        eventStore.replayEvents("user-123", 2, event -> {
            System.out.println("Replayed: " + event.eventType + " v" + event.version);
        });
    }
}
