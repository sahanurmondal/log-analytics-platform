package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Design Stream Processing Engine
 *
 * Description: Design a real-time stream processing engine:
 * - Event ingestion and partitioning
 * - Stream transformations and aggregations
 * - Windowing and watermarks
 * - Fault tolerance and state management
 * 
 * Constraints:
 * - Handle millions of events per second
 * - Provide exactly-once semantics
 * - Support complex event processing
 *
 * Follow-up:
 * - How to handle late-arriving events?
 * - Backpressure management?
 * 
 * Time Complexity: O(1) for event processing, O(log n) for windowing
 * Space Complexity: O(windows * events)
 * 
 * Company Tags: Apache Kafka, Apache Flink, Apache Storm
 */
public class DesignStreamProcessingEngine {

    enum EventType {
        DATA, WATERMARK, CHECKPOINT
    }

    enum WindowType {
        TUMBLING, SLIDING, SESSION
    }

    class StreamEvent {
        String eventId;
        EventType type;
        String source;
        Object payload;
        long timestamp;
        long ingestionTime;
        Map<String, Object> metadata;

        StreamEvent(String eventId, EventType type, String source, Object payload, long timestamp) {
            this.eventId = eventId != null ? eventId : UUID.randomUUID().toString();
            this.type = type != null ? type : EventType.DATA;
            this.source = source != null ? source : "unknown";
            this.payload = payload;
            this.timestamp = timestamp;
            this.ingestionTime = System.currentTimeMillis();
            this.metadata = new HashMap<>();
        }
    }

    class WindowedEvent {
        long windowStart;
        long windowEnd;
        List<StreamEvent> events;
        boolean isComplete;

        WindowedEvent(long windowStart, long windowEnd) {
            this.windowStart = windowStart;
            this.windowEnd = windowEnd;
            this.events = new ArrayList<>();
            this.isComplete = false;
        }

        void addEvent(StreamEvent event) {
            events.add(event);
        }

        boolean belongsToWindow(long timestamp) {
            return timestamp >= windowStart && timestamp < windowEnd;
        }
    }

    class StreamProcessor {
        String processorId;
        Function<StreamEvent, StreamEvent> transformer;
        Function<List<StreamEvent>, Object> aggregator;
        Map<String, Object> state;

        StreamProcessor(String processorId) {
            this.processorId = processorId;
            this.state = new ConcurrentHashMap<>();
        }

        StreamEvent transform(StreamEvent event) {
            return transformer != null ? transformer.apply(event) : event;
        }

        Object aggregate(List<StreamEvent> events) {
            return aggregator != null ? aggregator.apply(events) : events.size();
        }
    }

    class StreamPartition {
        int partitionId;
        String topicName;
        Queue<StreamEvent> eventQueue;
        long lastProcessedOffset;
        long watermark;
        Map<String, WindowedEvent> activeWindows;
        StreamProcessor processor;

        StreamPartition(int partitionId, String topicName) {
            this.partitionId = partitionId;
            this.topicName = topicName;
            this.eventQueue = new ConcurrentLinkedQueue<>();
            this.lastProcessedOffset = 0;
            this.watermark = 0;
            this.activeWindows = new ConcurrentHashMap<>();
            this.processor = new StreamProcessor("processor-" + partitionId);
        }

        void addEvent(StreamEvent event) {
            eventQueue.offer(event);
        }

        StreamEvent pollEvent() {
            return eventQueue.poll();
        }

        boolean hasEvents() {
            return !eventQueue.isEmpty();
        }

        void assignToWindows(StreamEvent event) {
            long eventTime = event.timestamp;

            // Create windows based on window type
            switch (windowType) {
                case TUMBLING:
                    assignToTumblingWindow(event, eventTime);
                    break;
                case SLIDING:
                    assignToSlidingWindows(event, eventTime);
                    break;
                case SESSION:
                    assignToSessionWindow(event, eventTime);
                    break;
            }
        }

        private void assignToTumblingWindow(StreamEvent event, long eventTime) {
            long windowStart = (eventTime / windowSize) * windowSize;
            long windowEnd = windowStart + windowSize;
            String windowKey = windowStart + "-" + windowEnd;

            WindowedEvent window = activeWindows.get(windowKey);
            if (window == null) {
                window = new WindowedEvent(windowStart, windowEnd);
                activeWindows.put(windowKey, window);
            }
            window.addEvent(event);
        }

        private void assignToSlidingWindows(StreamEvent event, long eventTime) {
            // Multiple overlapping windows for sliding windows
            long firstWindowStart = eventTime - windowSize + slideSize;
            firstWindowStart = (firstWindowStart / slideSize) * slideSize;

            for (long start = firstWindowStart; start <= eventTime; start += slideSize) {
                if (start + windowSize > eventTime) {
                    final long windowStart = start;
                    long windowEnd = windowStart + windowSize;
                    String windowKey = windowStart + "-" + windowEnd;

                    WindowedEvent window = activeWindows.get(windowKey);
                    if (window == null) {
                        window = new WindowedEvent(windowStart, windowEnd);
                        activeWindows.put(windowKey, window);
                    }
                    window.addEvent(event);
                }
            }
        }

        private void assignToSessionWindow(StreamEvent event, long eventTime) {
            // Find existing session window or create new one
            String sessionKey = findOrCreateSessionWindow(event, eventTime);
            WindowedEvent window = activeWindows.get(sessionKey);
            if (window != null) {
                window.addEvent(event);
                // Extend window end time for session windows
                window.windowEnd = Math.max(window.windowEnd, eventTime + sessionTimeout);
            }
        }

        private String findOrCreateSessionWindow(StreamEvent event, long eventTime) {
            // Simple session window logic - group events from same source
            String source = event.source;
            for (Map.Entry<String, WindowedEvent> entry : activeWindows.entrySet()) {
                WindowedEvent window = entry.getValue();
                if (window.events.stream().anyMatch(e -> e.source.equals(source) &&
                        Math.abs(e.timestamp - eventTime) <= sessionTimeout)) {
                    return entry.getKey();
                }
            }

            // Create new session window
            String windowKey = source + "-" + eventTime;
            activeWindows.put(windowKey, new WindowedEvent(eventTime, eventTime + sessionTimeout));
            return windowKey;
        }
    }

    private Map<String, StreamPartition> partitions;
    private ScheduledExecutorService scheduler;
    private final int windowSize;
    private final int slideSize;
    private final WindowType windowType;
    private final long sessionTimeout;
    private final Map<String, Object> checkpointState;

    public DesignStreamProcessingEngine(int partitionCount, int windowSize, int slideSize) {
        this(partitionCount, windowSize, slideSize, WindowType.TUMBLING, 30000);
    }

    public DesignStreamProcessingEngine(int partitionCount, int windowSize, int slideSize,
            WindowType windowType, long sessionTimeout) {
        this.partitions = new ConcurrentHashMap<>();
        this.windowSize = windowSize;
        this.slideSize = slideSize;
        this.windowType = windowType;
        this.sessionTimeout = sessionTimeout;
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.checkpointState = new ConcurrentHashMap<>();

        // Initialize partitions
        for (int i = 0; i < partitionCount; i++) {
            String topicName = "topic-" + i;
            partitions.put(topicName, new StreamPartition(i, topicName));
        }

        // Start background tasks
        startEventProcessing();
        startWatermarking();
        startWindowProcessing();
        startCheckpointing();
    }

    public void ingestEvent(String topicName, String eventId, EventType type, Object payload) {
        ingestEvent(topicName, eventId, type, payload, System.currentTimeMillis());
    }

    public void ingestEvent(String topicName, String eventId, EventType type, Object payload, long timestamp) {
        if (topicName == null || topicName.isEmpty()) {
            System.err.println("Warning: Invalid topic name provided for event ingestion");
            return;
        }

        StreamPartition partition = partitions.get(topicName);
        if (partition != null) {
            StreamEvent event = new StreamEvent(eventId, type, topicName, payload, timestamp);
            partition.addEvent(event);
        } else {
            System.err.println("Warning: Partition not found for topic: " + topicName);
        }
    }

    public void setTransformer(String topicName, Function<StreamEvent, StreamEvent> transformer) {
        if (topicName == null || transformer == null) {
            System.err.println("Warning: Invalid parameters for setTransformer");
            return;
        }

        StreamPartition partition = partitions.get(topicName);
        if (partition != null) {
            partition.processor.transformer = transformer;
        } else {
            System.err.println("Warning: Partition not found for topic: " + topicName);
        }
    }

    public void setAggregator(String topicName, Function<List<StreamEvent>, Object> aggregator) {
        if (topicName == null || aggregator == null) {
            System.err.println("Warning: Invalid parameters for setAggregator");
            return;
        }

        StreamPartition partition = partitions.get(topicName);
        if (partition != null) {
            partition.processor.aggregator = aggregator;
        } else {
            System.err.println("Warning: Partition not found for topic: " + topicName);
        }
    }

    private void startEventProcessing() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (StreamPartition partition : partitions.values()) {
                processEventsInPartition(partition);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void processEventsInPartition(StreamPartition partition) {
        int processed = 0;
        while (partition.hasEvents() && processed < 1000) { // Batch processing
            try {
                StreamEvent event = partition.pollEvent();
                if (event == null) {
                    break;
                }

                if (event.type == EventType.DATA) {
                    // Transform event
                    StreamEvent transformedEvent = partition.processor.transform(event);

                    // Assign to windows
                    partition.assignToWindows(transformedEvent);

                    // Update watermark if this is a watermark event
                    if (transformedEvent.timestamp > partition.watermark) {
                        partition.watermark = transformedEvent.timestamp;
                    }
                }

                partition.lastProcessedOffset++;
                processed++;
            } catch (Exception e) {
                System.err
                        .println("Error processing event in partition " + partition.topicName + ": " + e.getMessage());
                // Continue processing other events
            }
        }
    }

    private void startWindowProcessing() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (StreamPartition partition : partitions.values()) {
                processWindows(partition);
            }
        }, windowSize, slideSize, TimeUnit.MILLISECONDS);
    }

    private void processWindows(StreamPartition partition) {
        Iterator<Map.Entry<String, WindowedEvent>> iterator = partition.activeWindows.entrySet().iterator();

        while (iterator.hasNext()) {
            try {
                Map.Entry<String, WindowedEvent> entry = iterator.next();
                WindowedEvent window = entry.getValue();

                // Check if window should be triggered
                if (shouldTriggerWindow(window, partition.watermark)) {
                    // Aggregate events in window
                    Object result = partition.processor.aggregate(window.events);

                    // Emit result
                    emitWindowResult(partition.topicName, window, result);

                    // Mark as complete and remove
                    window.isComplete = true;
                    iterator.remove();
                }
            } catch (Exception e) {
                System.err
                        .println("Error processing window in partition " + partition.topicName + ": " + e.getMessage());
                // Remove problematic window to prevent stuck processing
                iterator.remove();
            }
        }
    }

    private boolean shouldTriggerWindow(WindowedEvent window, long watermark) {
        // Trigger if watermark passed window end time
        return watermark >= window.windowEnd;
    }

    private void emitWindowResult(String topicName, WindowedEvent window, Object result) {
        System.out.println("Window result for " + topicName +
                " [" + window.windowStart + "-" + window.windowEnd + "]: " + result);
    }

    private void startWatermarking() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (StreamPartition partition : partitions.values()) {
                updateWatermark(partition);
            }
        }, 5000, 1000, TimeUnit.MILLISECONDS);
    }

    private void updateWatermark(StreamPartition partition) {
        // Simple watermark strategy: current time - max out of order delay
        long newWatermark = System.currentTimeMillis() - 5000; // 5 second delay tolerance

        if (newWatermark > partition.watermark) {
            partition.watermark = newWatermark;

            // Create watermark event
            StreamEvent watermarkEvent = new StreamEvent(
                    "watermark-" + UUID.randomUUID().toString(),
                    EventType.WATERMARK,
                    partition.topicName,
                    newWatermark,
                    newWatermark);

            partition.addEvent(watermarkEvent);
        }
    }

    private void startCheckpointing() {
        scheduler.scheduleWithFixedDelay(() -> {
            performCheckpoint();
        }, 30000, 30000, TimeUnit.MILLISECONDS);
    }

    private void performCheckpoint() {
        Map<String, Object> checkpoint = new HashMap<>();

        for (StreamPartition partition : partitions.values()) {
            Map<String, Object> partitionState = new HashMap<>();
            partitionState.put("lastProcessedOffset", partition.lastProcessedOffset);
            partitionState.put("watermark", partition.watermark);
            partitionState.put("activeWindowCount", partition.activeWindows.size());
            partitionState.put("processorState", partition.processor.state);

            checkpoint.put(partition.topicName, partitionState);
        }

        checkpointState.clear();
        checkpointState.putAll(checkpoint);

        System.out.println("Checkpoint completed at " + System.currentTimeMillis());
    }

    public Map<String, Object> getEngineStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalEvents = partitions.values().stream()
                .mapToInt(p -> (int) p.lastProcessedOffset)
                .sum();

        int totalActiveWindows = partitions.values().stream()
                .mapToInt(p -> p.activeWindows.size())
                .sum();

        stats.put("totalPartitions", partitions.size());
        stats.put("totalProcessedEvents", totalEvents);
        stats.put("totalActiveWindows", totalActiveWindows);
        stats.put("windowType", windowType);
        stats.put("windowSize", windowSize);
        stats.put("slideSize", slideSize);

        return stats;
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DesignStreamProcessingEngine streamEngine = new DesignStreamProcessingEngine(
                3, 10000, 5000, WindowType.SLIDING, 30000);

        // Set up transformers and aggregators
        streamEngine.setTransformer("topic-0", event -> {
            // Simple transformation - add processed flag
            event.metadata.put("processed", true);
            return event;
        });

        streamEngine.setAggregator("topic-0", events -> {
            // Count aggregation
            return "Count: " + events.size() + ", Sources: " +
                    events.stream().map(e -> e.source).distinct().count();
        });

        // Ingest sample events with different timestamps
        long baseTime = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            streamEngine.ingestEvent("topic-0", "event-" + i, EventType.DATA,
                    "payload-" + i, baseTime + i * 1000);

            if (i % 5 == 0) {
                streamEngine.ingestEvent("topic-1", "event-" + i, EventType.DATA,
                        "payload-" + i, baseTime + i * 1000);
            }
        }

        System.out.println("Initial stats: " + streamEngine.getEngineStats());

        // Wait for processing
        Thread.sleep(25000);

        System.out.println("Final stats: " + streamEngine.getEngineStats());

        streamEngine.shutdown();
    }
}