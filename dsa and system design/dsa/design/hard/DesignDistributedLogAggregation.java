package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Design Distributed Log Aggregation System
 * 
 * Description:
 * Design a distributed log aggregation system that collects, processes, and
 * indexes logs from multiple distributed services. The system should support
 * real-time log streaming, filtering, searching, and alerting.
 * 
 * Requirements:
 * - Log collection from multiple sources
 * - Real-time log processing and filtering
 * - Distributed storage and indexing
 * - Log search and querying capabilities
 * - Alert generation based on log patterns
 * - Log retention and archival policies
 * - Fault tolerance and data durability
 * 
 * Key Features:
 * - Multi-source log ingestion
 * - Stream processing pipeline
 * - Distributed indexing
 * - Query optimization
 * - Real-time alerting
 * - Data partitioning and sharding
 * 
 * Company Tags: Elasticsearch, Splunk, Datadog, New Relic, Amazon
 * Difficulty: Hard
 */
public class DesignDistributedLogAggregation {

    enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR, FATAL
    }

    static class LogEntry {
        final String logId;
        final String serviceId;
        final String message;
        final LogLevel level;
        final long timestamp;
        final Map<String, String> metadata;
        final String source;

        public LogEntry(String serviceId, String message, LogLevel level, String source) {
            this.logId = UUID.randomUUID().toString();
            this.serviceId = serviceId;
            this.message = message;
            this.level = level;
            this.timestamp = System.currentTimeMillis();
            this.metadata = new HashMap<>();
            this.source = source;
        }

        public LogEntry addMetadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s [%s] %s: %s",
                    new Date(timestamp), level, serviceId, source, message);
        }
    }

    static class LogFilter {
        private final Set<LogLevel> allowedLevels;
        private final Set<String> allowedServices;
        private final List<String> messagePatterns;
        private final Map<String, String> requiredMetadata;

        public LogFilter() {
            this.allowedLevels = new HashSet<>();
            this.allowedServices = new HashSet<>();
            this.messagePatterns = new ArrayList<>();
            this.requiredMetadata = new HashMap<>();
        }

        public LogFilter addLevel(LogLevel level) {
            allowedLevels.add(level);
            return this;
        }

        public LogFilter addService(String serviceId) {
            allowedServices.add(serviceId);
            return this;
        }

        public LogFilter addMessagePattern(String pattern) {
            messagePatterns.add(pattern);
            return this;
        }

        public LogFilter addMetadata(String key, String value) {
            requiredMetadata.put(key, value);
            return this;
        }

        public boolean matches(LogEntry entry) {
            // Check log level
            if (!allowedLevels.isEmpty() && !allowedLevels.contains(entry.level)) {
                return false;
            }

            // Check service
            if (!allowedServices.isEmpty() && !allowedServices.contains(entry.serviceId)) {
                return false;
            }

            // Check message patterns
            if (!messagePatterns.isEmpty()) {
                boolean matchesPattern = messagePatterns.stream()
                        .anyMatch(pattern -> entry.message.toLowerCase().contains(pattern.toLowerCase()));
                if (!matchesPattern)
                    return false;
            }

            // Check metadata
            for (Map.Entry<String, String> requirement : requiredMetadata.entrySet()) {
                String actualValue = entry.metadata.get(requirement.getKey());
                if (!requirement.getValue().equals(actualValue)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class LogIndex {
        private final Map<String, Set<String>> serviceIndex;
        private final Map<LogLevel, Set<String>> levelIndex;
        private final Map<String, Set<String>> messageIndex;
        private final Map<Long, Set<String>> timeIndex;
        private final Map<String, LogEntry> logStorage;

        public LogIndex() {
            this.serviceIndex = new ConcurrentHashMap<>();
            this.levelIndex = new ConcurrentHashMap<>();
            this.messageIndex = new ConcurrentHashMap<>();
            this.timeIndex = new ConcurrentHashMap<>();
            this.logStorage = new ConcurrentHashMap<>();
        }

        public void indexLog(LogEntry entry) {
            String logId = entry.logId;
            logStorage.put(logId, entry);

            // Index by service
            serviceIndex.computeIfAbsent(entry.serviceId, k -> ConcurrentHashMap.newKeySet()).add(logId);

            // Index by level
            levelIndex.computeIfAbsent(entry.level, k -> ConcurrentHashMap.newKeySet()).add(logId);

            // Index by message keywords
            String[] words = entry.message.toLowerCase().split("\\s+");
            for (String word : words) {
                if (word.length() > 2) { // Ignore short words
                    messageIndex.computeIfAbsent(word, k -> ConcurrentHashMap.newKeySet()).add(logId);
                }
            }

            // Index by time bucket (hour-based)
            long hourBucket = entry.timestamp / (1000 * 60 * 60);
            timeIndex.computeIfAbsent(hourBucket, k -> ConcurrentHashMap.newKeySet()).add(logId);
        }

        public List<LogEntry> search(LogFilter filter, long startTime, long endTime, int limit) {
            Set<String> candidateLogIds = null;

            // Start with time range
            long startHour = startTime / (1000 * 60 * 60);
            long endHour = endTime / (1000 * 60 * 60);

            for (long hour = startHour; hour <= endHour; hour++) {
                Set<String> hourLogs = timeIndex.get(hour);
                if (hourLogs != null) {
                    if (candidateLogIds == null) {
                        candidateLogIds = new HashSet<>(hourLogs);
                    } else {
                        candidateLogIds.addAll(hourLogs);
                    }
                }
            }

            if (candidateLogIds == null) {
                return new ArrayList<>();
            }

            // Apply filter and collect results
            List<LogEntry> results = new ArrayList<>();
            for (String logId : candidateLogIds) {
                LogEntry entry = logStorage.get(logId);
                if (entry != null &&
                        entry.timestamp >= startTime &&
                        entry.timestamp <= endTime &&
                        filter.matches(entry)) {
                    results.add(entry);
                    if (results.size() >= limit) {
                        break;
                    }
                }
            }

            // Sort by timestamp (newest first)
            results.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
            return results;
        }

        public Map<String, Long> getServiceStats() {
            Map<String, Long> stats = new HashMap<>();
            for (Map.Entry<String, Set<String>> entry : serviceIndex.entrySet()) {
                stats.put(entry.getKey(), (long) entry.getValue().size());
            }
            return stats;
        }

        public Map<LogLevel, Long> getLevelStats() {
            Map<LogLevel, Long> stats = new HashMap<>();
            for (Map.Entry<LogLevel, Set<String>> entry : levelIndex.entrySet()) {
                stats.put(entry.getKey(), (long) entry.getValue().size());
            }
            return stats;
        }
    }

    static class AlertRule {
        final String ruleId;
        final String name;
        final LogFilter filter;
        final int threshold;
        final long timeWindowMs;
        final String alertMessage;
        private volatile long lastAlertTime;
        private final AtomicInteger matchCount = new AtomicInteger(0);

        public AlertRule(String name, LogFilter filter, int threshold, long timeWindowMs, String alertMessage) {
            this.ruleId = UUID.randomUUID().toString();
            this.name = name;
            this.filter = filter;
            this.threshold = threshold;
            this.timeWindowMs = timeWindowMs;
            this.alertMessage = alertMessage;
            this.lastAlertTime = 0;
        }

        public boolean shouldTriggerAlert(long currentTime) {
            if (currentTime - lastAlertTime < timeWindowMs) {
                return false; // Cool-down period
            }

            return matchCount.get() >= threshold;
        }

        public void recordMatch() {
            matchCount.incrementAndGet();
        }

        public void resetCounts() {
            matchCount.set(0);
        }

        public void recordAlert(long timestamp) {
            lastAlertTime = timestamp;
            resetCounts();
        }
    }

    // Main Log Aggregation System
    private final LogIndex logIndex;
    private final List<AlertRule> alertRules;
    private final BlockingQueue<LogEntry> logQueue;
    private final ExecutorService logProcessors;
    private final ScheduledExecutorService alertScheduler;
    private final AtomicLong totalLogsProcessed = new AtomicLong(0);
    private volatile boolean running = true;

    public DesignDistributedLogAggregation(int processorThreads) {
        this.logIndex = new LogIndex();
        this.alertRules = new ArrayList<>();
        this.logQueue = new LinkedBlockingQueue<>();
        this.logProcessors = Executors.newFixedThreadPool(processorThreads);
        this.alertScheduler = Executors.newScheduledThreadPool(2);

        // Start log processing workers
        startLogProcessors(processorThreads);

        // Start alert checking
        startAlertChecker();
    }

    public void ingestLog(LogEntry logEntry) {
        if (!running)
            return;

        try {
            logQueue.offer(logEntry, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void addAlertRule(AlertRule rule) {
        synchronized (alertRules) {
            alertRules.add(rule);
        }
    }

    public List<LogEntry> searchLogs(LogFilter filter, long startTime, long endTime, int limit) {
        return logIndex.search(filter, startTime, endTime, limit);
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLogsProcessed", totalLogsProcessed.get());
        stats.put("queueSize", logQueue.size());
        stats.put("serviceStats", logIndex.getServiceStats());
        stats.put("levelStats", logIndex.getLevelStats());
        stats.put("alertRulesCount", alertRules.size());
        return stats;
    }

    private void startLogProcessors(int threadCount) {
        for (int i = 0; i < threadCount; i++) {
            logProcessors.submit(() -> {
                while (running || !logQueue.isEmpty()) {
                    try {
                        LogEntry entry = logQueue.poll(1, TimeUnit.SECONDS);
                        if (entry != null) {
                            processLogEntry(entry);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    private void processLogEntry(LogEntry entry) {
        // Index the log entry
        logIndex.indexLog(entry);
        totalLogsProcessed.incrementAndGet();

        // Check against alert rules
        synchronized (alertRules) {
            for (AlertRule rule : alertRules) {
                if (rule.filter.matches(entry)) {
                    rule.recordMatch();
                }
            }
        }
    }

    private void startAlertChecker() {
        alertScheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();

            synchronized (alertRules) {
                for (AlertRule rule : alertRules) {
                    if (rule.shouldTriggerAlert(currentTime)) {
                        triggerAlert(rule, currentTime);
                    }
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void triggerAlert(AlertRule rule, long timestamp) {
        System.out.println("🚨 ALERT TRIGGERED 🚨");
        System.out.println("Rule: " + rule.name);
        System.out.println("Message: " + rule.alertMessage);
        System.out.println("Matches: " + rule.matchCount.get());
        System.out.println("Timestamp: " + new Date(timestamp));
        System.out.println("---");

        rule.recordAlert(timestamp);
    }

    public void shutdown() {
        running = false;

        logProcessors.shutdown();
        alertScheduler.shutdown();

        try {
            if (!logProcessors.awaitTermination(5, TimeUnit.SECONDS)) {
                logProcessors.shutdownNow();
            }
            if (!alertScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                alertScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            logProcessors.shutdownNow();
            alertScheduler.shutdownNow();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedLogAggregation logSystem = new DesignDistributedLogAggregation(4);

        // Set up alert rules
        LogFilter errorFilter = new LogFilter()
                .addLevel(LogLevel.ERROR)
                .addLevel(LogLevel.FATAL);

        AlertRule errorAlert = new AlertRule(
                "High Error Rate",
                errorFilter,
                3, // threshold
                30000, // 30 seconds window
                "High number of errors detected in the system");

        LogFilter paymentFailureFilter = new LogFilter()
                .addService("payment-service")
                .addMessagePattern("failed");

        AlertRule paymentAlert = new AlertRule(
                "Payment Failures",
                paymentFailureFilter,
                2,
                20000,
                "Payment service is experiencing failures");

        logSystem.addAlertRule(errorAlert);
        logSystem.addAlertRule(paymentAlert);

        // Simulate log ingestion
        System.out.println("=== Starting Log Ingestion ===");

        String[] services = { "user-service", "payment-service", "order-service", "notification-service" };
        LogLevel[] levels = { LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR, LogLevel.DEBUG };

        // Generate logs
        for (int i = 0; i < 50; i++) {
            String service = services[i % services.length];
            LogLevel level = levels[i % levels.length];

            String message;
            if (i % 10 == 0) {
                message = "Transaction failed with error code 500";
            } else if (i % 7 == 0) {
                message = "Payment processing failed for user " + (i % 100);
            } else {
                message = "Processing request " + i + " successfully";
            }

            LogEntry entry = new LogEntry(service, message, level, "app-server-" + (i % 3))
                    .addMetadata("requestId", "req-" + i)
                    .addMetadata("userId", "user-" + (i % 20));

            logSystem.ingestLog(entry);

            if (i % 10 == 0) {
                Thread.sleep(100); // Pause occasionally
            }
        }

        // Wait for processing
        Thread.sleep(5000);

        System.out.println("=== System Statistics ===");
        Map<String, Object> stats = logSystem.getSystemStats();
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        // Search for error logs
        System.out.println("\n=== Error Log Search ===");
        LogFilter searchFilter = new LogFilter().addLevel(LogLevel.ERROR);
        long now = System.currentTimeMillis();
        List<LogEntry> errorLogs = logSystem.searchLogs(searchFilter, now - 60000, now, 10);

        System.out.println("Found " + errorLogs.size() + " error logs:");
        for (LogEntry log : errorLogs) {
            System.out.println(log);
        }

        // Search for payment service logs
        System.out.println("\n=== Payment Service Logs ===");
        LogFilter paymentFilter = new LogFilter().addService("payment-service");
        List<LogEntry> paymentLogs = logSystem.searchLogs(paymentFilter, now - 60000, now, 5);

        System.out.println("Found " + paymentLogs.size() + " payment service logs:");
        for (LogEntry log : paymentLogs) {
            System.out.println(log);
        }

        // Wait for potential alerts
        Thread.sleep(35000);

        logSystem.shutdown();
    }
}
