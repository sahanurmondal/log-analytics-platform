package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Distributed Tracing System
 *
 * Description: Design a distributed tracing system that supports:
 * - Request tracing across microservices
 * - Span collection and correlation
 * - Performance monitoring and analysis
 * - Error tracking and debugging
 * 
 * Constraints:
 * - Handle millions of traces per second
 * - Low overhead on applications
 * - Real-time trace visualization
 *
 * Follow-up:
 * - How to handle trace sampling?
 * - Cross-datacenter tracing?
 * 
 * Time Complexity: O(1) for span creation, O(log n) for trace queries
 * Space Complexity: O(traces * spans)
 * 
 * Company Tags: Jaeger, Zipkin, AWS X-Ray
 */
public class DesignDistributedTracing {

    enum SpanKind {
        CLIENT, SERVER, PRODUCER, CONSUMER, INTERNAL
    }

    enum SpanStatus {
        OK, ERROR, TIMEOUT, CANCELLED
    }

    class Span {
        String spanId;
        String traceId;
        String parentSpanId;
        String operationName;
        String serviceName;
        SpanKind kind;
        SpanStatus status;
        long startTime;
        long endTime;
        Map<String, Object> tags;
        List<LogEntry> logs;
        Map<String, String> baggage;

        Span(String spanId, String traceId, String parentSpanId, String operationName, String serviceName) {
            this.spanId = spanId;
            this.traceId = traceId;
            this.parentSpanId = parentSpanId;
            this.operationName = operationName;
            this.serviceName = serviceName;
            this.kind = SpanKind.INTERNAL;
            this.status = SpanStatus.OK;
            this.startTime = System.currentTimeMillis();
            this.tags = new ConcurrentHashMap<>();
            this.logs = new ArrayList<>();
            this.baggage = new ConcurrentHashMap<>();
        }

        void finish() {
            this.endTime = System.currentTimeMillis();
        }

        void setTag(String key, Object value) {
            tags.put(key, value);
        }

        void log(String message) {
            logs.add(new LogEntry(System.currentTimeMillis(), message, new HashMap<>()));
        }

        void log(String message, Map<String, Object> fields) {
            logs.add(new LogEntry(System.currentTimeMillis(), message, fields));
        }

        long getDuration() {
            return endTime > 0 ? endTime - startTime : System.currentTimeMillis() - startTime;
        }
    }

    class LogEntry {
        long timestamp;
        String message;
        Map<String, Object> fields;

        LogEntry(long timestamp, String message, Map<String, Object> fields) {
            this.timestamp = timestamp;
            this.message = message;
            this.fields = new HashMap<>(fields);
        }
    }

    class Trace {
        String traceId;
        Map<String, Span> spans;
        String rootSpanId;
        long startTime;
        long endTime;
        Set<String> services;

        Trace(String traceId) {
            this.traceId = traceId;
            this.spans = new ConcurrentHashMap<>();
            this.services = ConcurrentHashMap.newKeySet();
            this.startTime = Long.MAX_VALUE;
            this.endTime = 0;
        }

        void addSpan(Span span) {
            spans.put(span.spanId, span);
            services.add(span.serviceName);

            if (span.parentSpanId == null) {
                rootSpanId = span.spanId;
            }

            startTime = Math.min(startTime, span.startTime);
            if (span.endTime > 0) {
                endTime = Math.max(endTime, span.endTime);
            }
        }

        long getDuration() {
            return endTime > startTime ? endTime - startTime : System.currentTimeMillis() - startTime;
        }

        int getSpanCount() {
            return spans.size();
        }

        boolean hasErrors() {
            return spans.values().stream().anyMatch(span -> span.status == SpanStatus.ERROR);
        }

        List<String> getCriticalPath() {
            if (rootSpanId == null)
                return new ArrayList<>();

            List<String> path = new ArrayList<>();
            findCriticalPath(rootSpanId, path);
            return path;
        }

        private void findCriticalPath(String spanId, List<String> path) {
            path.add(spanId);
            Span span = spans.get(spanId);
            if (span == null)
                return;

            // Find child span with longest duration
            String longestChild = null;
            long maxDuration = 0;

            for (Span childSpan : spans.values()) {
                if (spanId.equals(childSpan.parentSpanId)) {
                    long duration = childSpan.getDuration();
                    if (duration > maxDuration) {
                        maxDuration = duration;
                        longestChild = childSpan.spanId;
                    }
                }
            }

            if (longestChild != null) {
                findCriticalPath(longestChild, path);
            }
        }
    }

    class TraceCollector {
        String collectorId;
        BlockingQueue<Span> spanQueue;
        Map<String, Trace> activeTraces;
        Map<String, Trace> completedTraces;
        ExecutorService processingExecutor;
        ScheduledExecutorService cleanupExecutor;

        TraceCollector(String collectorId) {
            this.collectorId = collectorId;
            this.spanQueue = new LinkedBlockingQueue<>(10000);
            this.activeTraces = new ConcurrentHashMap<>();
            this.completedTraces = new ConcurrentLinkedHashMap<String, Trace>(1000) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Trace> eldest) {
                    return size() > 1000; // Keep only recent 1000 traces
                }
            };
            this.processingExecutor = Executors.newFixedThreadPool(5);
            this.cleanupExecutor = Executors.newScheduledThreadPool(1);

            startProcessing();
            startCleanup();
        }

        void collectSpan(Span span) {
            if (span == null) {
                System.err.println("Warning: Attempted to collect null span");
                return;
            }

            try {
                if (!spanQueue.offer(span, 1, TimeUnit.SECONDS)) {
                    System.err.println("Warning: Span queue is full, dropping span: " + span.spanId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Warning: Interrupted while collecting span: " + span.spanId);
            }
        }

        private void startProcessing() {
            for (int i = 0; i < 5; i++) {
                processingExecutor.submit(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Span span = spanQueue.take();
                            processSpan(span);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
            }
        }

        private void processSpan(Span span) {
            Trace trace = activeTraces.computeIfAbsent(span.traceId, Trace::new);
            trace.addSpan(span);

            // Check if trace is complete (all spans finished)
            if (isTraceComplete(trace)) {
                activeTraces.remove(span.traceId);
                completedTraces.put(span.traceId, trace);
            }
        }

        private boolean isTraceComplete(Trace trace) {
            // Simple heuristic: trace is complete if no new spans for 10 seconds
            long now = System.currentTimeMillis();
            return trace.spans.values().stream()
                    .allMatch(span -> (span.endTime > 0 && (now - span.endTime > 10000)) ||
                            (span.endTime == 0 && now - span.startTime > 60000));
        }

        private void startCleanup() {
            cleanupExecutor.scheduleWithFixedDelay(() -> {
                long cutoff = System.currentTimeMillis() - 300000; // 5 minutes

                Iterator<Map.Entry<String, Trace>> iterator = activeTraces.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Trace> entry = iterator.next();
                    Trace trace = entry.getValue();

                    if (trace.startTime < cutoff) {
                        iterator.remove();
                        completedTraces.put(entry.getKey(), trace);
                    }
                }
            }, 60, 60, TimeUnit.SECONDS);
        }

        void shutdown() {
            processingExecutor.shutdown();
            cleanupExecutor.shutdown();

            try {
                if (!processingExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    processingExecutor.shutdownNow();
                }
                if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    cleanupExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                processingExecutor.shutdownNow();
                cleanupExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    class ConcurrentLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;
        private final Object lock = new Object();

        ConcurrentLinkedHashMap(int maxSize) {
            super(16, 0.75f, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxSize;
        }

        @Override
        public V put(K key, V value) {
            synchronized (lock) {
                return super.put(key, value);
            }
        }

        @Override
        public V get(Object key) {
            synchronized (lock) {
                return super.get(key);
            }
        }

        @Override
        public V remove(Object key) {
            synchronized (lock) {
                return super.remove(key);
            }
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            synchronized (lock) {
                return new HashSet<Map.Entry<K, V>>(super.entrySet());
            }
        }

        @Override
        public Collection<V> values() {
            synchronized (lock) {
                return new ArrayList<>(super.values());
            }
        }
    }

    class TracingContext {
        private static final ThreadLocal<Span> activeSpan = new ThreadLocal<>();

        static void setActiveSpan(Span span) {
            activeSpan.set(span);
        }

        static Span getActiveSpan() {
            return activeSpan.get();
        }

        static void clearActiveSpan() {
            activeSpan.remove();
        }
    }

    private TraceCollector collector;
    private Map<String, Double> samplingRates;
    private Random random;

    public DesignDistributedTracing() {
        collector = new TraceCollector("main-collector");
        samplingRates = new ConcurrentHashMap<>();
        random = new Random();

        // Default sampling rates
        samplingRates.put("default", 0.1); // 10% sampling
        samplingRates.put("critical", 1.0); // 100% sampling for critical services
        samplingRates.put("test", 0.01); // 1% sampling for test services
    }

    public Span startSpan(String operationName, String serviceName) {
        return startSpan(operationName, serviceName, null);
    }

    public Span startSpan(String operationName, String serviceName, Span parent) {
        if (operationName == null || operationName.isEmpty()) {
            System.err.println("Warning: Invalid operation name for span creation");
            return null;
        }

        if (serviceName == null || serviceName.isEmpty()) {
            System.err.println("Warning: Invalid service name for span creation");
            return null;
        }

        String traceId;
        String parentSpanId = null;

        if (parent != null) {
            traceId = parent.traceId;
            parentSpanId = parent.spanId;
        } else {
            traceId = UUID.randomUUID().toString();
        }

        // Apply sampling
        double samplingRate = samplingRates.getOrDefault(serviceName, samplingRates.get("default"));
        if (random.nextDouble() > samplingRate) {
            return null; // Not sampled
        }

        String spanId = UUID.randomUUID().toString();
        Span span = new Span(spanId, traceId, parentSpanId, operationName, serviceName);

        TracingContext.setActiveSpan(span);
        return span;
    }

    public void finishSpan(Span span) {
        if (span == null)
            return;

        span.finish();
        collector.collectSpan(span);

        // Clear from context if it's the active span
        if (TracingContext.getActiveSpan() == span) {
            TracingContext.clearActiveSpan();
        }
    }

    public Trace getTrace(String traceId) {
        Trace trace = collector.activeTraces.get(traceId);
        if (trace == null) {
            trace = collector.completedTraces.get(traceId);
        }
        return trace;
    }

    public List<Trace> searchTraces(String serviceName, String operationName,
            long startTime, long endTime, boolean errorsOnly) {
        List<Trace> results = new ArrayList<>();

        for (Trace trace : collector.completedTraces.values()) {
            if (trace.startTime < startTime || trace.startTime > endTime) {
                continue;
            }

            if (serviceName != null && !trace.services.contains(serviceName)) {
                continue;
            }

            if (operationName != null) {
                boolean hasOperation = trace.spans.values().stream()
                        .anyMatch(span -> operationName.equals(span.operationName));
                if (!hasOperation)
                    continue;
            }

            if (errorsOnly && !trace.hasErrors()) {
                continue;
            }

            results.add(trace);
        }

        return results.stream()
                .sorted((a, b) -> Long.compare(b.startTime, a.startTime))
                .limit(100)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void setSamplingRate(String serviceName, double rate) {
        if (serviceName == null || serviceName.isEmpty()) {
            System.err.println("Warning: Invalid service name for sampling rate configuration");
            return;
        }

        if (rate < 0.0 || rate > 1.0) {
            System.err.println("Warning: Sampling rate must be between 0.0 and 1.0, got: " + rate);
            return;
        }

        samplingRates.put(serviceName, rate);
    }

    public Map<String, Object> getTracingStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("activeTraces", collector.activeTraces.size());
        stats.put("completedTraces", collector.completedTraces.size());
        stats.put("pendingSpans", collector.spanQueue.size());
        stats.put("samplingRates", new HashMap<>(samplingRates));

        // Service statistics
        Map<String, Integer> serviceSpanCounts = new HashMap<>();
        Map<String, Long> serviceErrorCounts = new HashMap<>();

        for (Trace trace : collector.completedTraces.values()) {
            for (Span span : trace.spans.values()) {
                serviceSpanCounts.put(span.serviceName,
                        serviceSpanCounts.getOrDefault(span.serviceName, 0) + 1);

                if (span.status == SpanStatus.ERROR) {
                    serviceErrorCounts.put(span.serviceName,
                            serviceErrorCounts.getOrDefault(span.serviceName, 0L) + 1);
                }
            }
        }

        stats.put("serviceSpanCounts", serviceSpanCounts);
        stats.put("serviceErrorCounts", serviceErrorCounts);

        return stats;
    }

    public void shutdown() {
        collector.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedTracing tracing = new DesignDistributedTracing();

        // Set sampling rates
        tracing.setSamplingRate("user-service", 1.0); // 100% for demo
        tracing.setSamplingRate("order-service", 1.0);
        tracing.setSamplingRate("payment-service", 1.0);

        // Simulate distributed request trace
        simulateDistributedRequest(tracing);

        Thread.sleep(2000); // Wait for processing

        System.out.println("Tracing stats: " + tracing.getTracingStats());

        // Search for traces
        List<Trace> traces = tracing.searchTraces("user-service", null,
                System.currentTimeMillis() - 60000, System.currentTimeMillis(), false);

        System.out.println("\nFound " + traces.size() + " traces:");
        for (Trace trace : traces) {
            System.out.println("Trace " + trace.traceId +
                    ": " + trace.getSpanCount() + " spans, " +
                    trace.getDuration() + "ms duration, " +
                    "services: " + trace.services +
                    ", errors: " + trace.hasErrors());

            System.out.println("Critical path: " + trace.getCriticalPath());
        }

        tracing.shutdown();
    }

    private static void simulateDistributedRequest(DesignDistributedTracing tracing) {
        // Simulate: Frontend -> User Service -> Order Service -> Payment Service

        Span frontendSpan = tracing.startSpan("handle_request", "frontend");
        if (frontendSpan != null) {
            frontendSpan.setTag("http.method", "POST");
            frontendSpan.setTag("http.url", "/api/orders");

            try {
                Thread.sleep(10);

                // Call user service
                Span userSpan = tracing.startSpan("get_user", "user-service", frontendSpan);
                if (userSpan != null) {
                    userSpan.setTag("user.id", "12345");
                    userSpan.log("Fetching user data");

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        userSpan.status = SpanStatus.ERROR;
                        userSpan.log("Thread interrupted", Map.of("error", e.getMessage()));
                    } finally {
                        tracing.finishSpan(userSpan);
                    }
                }

                // Call order service
                Span orderSpan = tracing.startSpan("create_order", "order-service", frontendSpan);
                if (orderSpan != null) {
                    orderSpan.setTag("order.id", "67890");
                    orderSpan.log("Creating new order");

                    try {
                        Thread.sleep(100);

                        // Call payment service
                        Span paymentSpan = tracing.startSpan("process_payment", "payment-service", orderSpan);
                        if (paymentSpan != null) {
                            paymentSpan.setTag("payment.amount", 99.99);
                            paymentSpan.log("Processing payment");

                            try {
                                Thread.sleep(200);

                                // Simulate occasional error
                                if (Math.random() < 0.1) {
                                    paymentSpan.status = SpanStatus.ERROR;
                                    paymentSpan.log("Payment failed", Map.of("error", "insufficient_funds"));
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                paymentSpan.status = SpanStatus.ERROR;
                                paymentSpan.log("Thread interrupted", Map.of("error", e.getMessage()));
                            } finally {
                                tracing.finishSpan(paymentSpan);
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        orderSpan.status = SpanStatus.ERROR;
                        orderSpan.log("Thread interrupted", Map.of("error", e.getMessage()));
                    } finally {
                        tracing.finishSpan(orderSpan);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                frontendSpan.status = SpanStatus.ERROR;
                frontendSpan.log("Thread interrupted", Map.of("error", e.getMessage()));
            } finally {
                tracing.finishSpan(frontendSpan);
            }
        }
    }
}
