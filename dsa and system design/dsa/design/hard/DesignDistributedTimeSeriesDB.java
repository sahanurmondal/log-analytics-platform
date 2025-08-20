package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Distributed Time Series Database
 *
 * Description: Design a distributed time series database that supports:
 * - Time series data ingestion and storage
 * - Range queries with aggregation
 * - Data partitioning and replication
 * - Downsampling and compression
 *
 * Constraints:
 * - Support millions of data points per second
 * - Handle time-based queries efficiently
 * - Provide horizontal scalability
 *
 * Follow-up:
 * - Can you optimize for query performance?
 * - Can you support aggregation and downsampling?
 * 
 * Time Complexity: O(log n) for insert/query, O(n) for range queries
 * Space Complexity: O(data_points * replication_factor)
 * 
 * Company Tags: InfluxDB, TimescaleDB, Prometheus
 */
public class DesignDistributedTimeSeriesDB {

    enum AggregationType {
        SUM, AVG, MIN, MAX, COUNT, FIRST, LAST
    }

    class DataPoint {
        long timestamp;
        double value;
        Map<String, String> tags;

        DataPoint(long timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
            this.tags = new HashMap<>();
        }

        DataPoint(long timestamp, double value, Map<String, String> tags) {
            this.timestamp = timestamp;
            this.value = value;
            this.tags = tags != null ? new HashMap<>(tags) : new HashMap<>();
        }
    }

    class TimeSeries {
        String metric;
        TreeMap<Long, DataPoint> data; // timestamp -> DataPoint
        Map<String, String> metadata;
        long retentionMs;

        TimeSeries(String metric) {
            this.metric = metric;
            this.data = new TreeMap<>();
            this.metadata = new HashMap<>();
            this.retentionMs = 7 * 24 * 3600 * 1000L; // 7 days default
        }

        synchronized void insert(long timestamp, double value, Map<String, String> tags) {
            DataPoint point = new DataPoint(timestamp, value, tags);
            data.put(timestamp, point);

            // Clean up old data based on retention policy
            cleanup();
        }

        synchronized void delete(long timestamp) {
            data.remove(timestamp);
        }

        synchronized List<DataPoint> query(long start, long end) {
            return data.subMap(start, true, end, true).values()
                    .stream()
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        synchronized double aggregate(long start, long end, AggregationType type) {
            List<DataPoint> points = query(start, end);
            if (points.isEmpty())
                return 0.0;

            switch (type) {
                case SUM:
                    return points.stream().mapToDouble(p -> p.value).sum();
                case AVG:
                    return points.stream().mapToDouble(p -> p.value).average().orElse(0.0);
                case MIN:
                    return points.stream().mapToDouble(p -> p.value).min().orElse(0.0);
                case MAX:
                    return points.stream().mapToDouble(p -> p.value).max().orElse(0.0);
                case COUNT:
                    return points.size();
                case FIRST:
                    return points.get(0).value;
                case LAST:
                    return points.get(points.size() - 1).value;
                default:
                    return 0.0;
            }
        }

        private void cleanup() {
            if (retentionMs <= 0)
                return;

            long cutoff = System.currentTimeMillis() - retentionMs;
            data.headMap(cutoff).clear();
        }

        synchronized int size() {
            return data.size();
        }
    }

    class ShardNode {
        String nodeId;
        Map<String, TimeSeries> series;
        Map<String, Object> nodeStats;
        boolean isHealthy;
        long lastHeartbeat;

        ShardNode(String nodeId) {
            this.nodeId = nodeId;
            this.series = new ConcurrentHashMap<>();
            this.nodeStats = new ConcurrentHashMap<>();
            this.isHealthy = true;
            this.lastHeartbeat = System.currentTimeMillis();
        }

        void insert(String metric, long timestamp, double value, Map<String, String> tags) {
            TimeSeries ts = series.computeIfAbsent(metric, TimeSeries::new);
            ts.insert(timestamp, value, tags);

            updateStats("inserts", 1);
        }

        void delete(String metric, long timestamp) {
            TimeSeries ts = series.get(metric);
            if (ts != null) {
                ts.delete(timestamp);
                updateStats("deletes", 1);
            }
        }

        List<DataPoint> query(String metric, long start, long end) {
            TimeSeries ts = series.get(metric);
            updateStats("queries", 1);

            return ts != null ? ts.query(start, end) : new ArrayList<>();
        }

        double aggregate(String metric, long start, long end, AggregationType type) {
            TimeSeries ts = series.get(metric);
            return ts != null ? ts.aggregate(start, end, type) : 0.0;
        }

        List<DataPoint> downsample(String metric, long start, long end,
                long bucketSize, AggregationType type) {
            TimeSeries ts = series.get(metric);
            if (ts == null)
                return new ArrayList<>();

            List<DataPoint> rawData = ts.query(start, end);
            List<DataPoint> downsampled = new ArrayList<>();

            if (rawData.isEmpty())
                return downsampled;

            long currentBucket = (start / bucketSize) * bucketSize;
            List<DataPoint> bucketData = new ArrayList<>();

            for (DataPoint point : rawData) {
                long pointBucket = (point.timestamp / bucketSize) * bucketSize;

                if (pointBucket != currentBucket) {
                    if (!bucketData.isEmpty()) {
                        double aggregatedValue = aggregateBucket(bucketData, type);
                        downsampled.add(new DataPoint(currentBucket, aggregatedValue));
                    }
                    currentBucket = pointBucket;
                    bucketData.clear();
                }

                bucketData.add(point);
            }

            // Process last bucket
            if (!bucketData.isEmpty()) {
                double aggregatedValue = aggregateBucket(bucketData, type);
                downsampled.add(new DataPoint(currentBucket, aggregatedValue));
            }

            return downsampled;
        }

        private double aggregateBucket(List<DataPoint> bucketData, AggregationType type) {
            switch (type) {
                case SUM:
                    return bucketData.stream().mapToDouble(p -> p.value).sum();
                case AVG:
                    return bucketData.stream().mapToDouble(p -> p.value).average().orElse(0.0);
                case MIN:
                    return bucketData.stream().mapToDouble(p -> p.value).min().orElse(0.0);
                case MAX:
                    return bucketData.stream().mapToDouble(p -> p.value).max().orElse(0.0);
                case COUNT:
                    return bucketData.size();
                case FIRST:
                    return bucketData.get(0).value;
                case LAST:
                    return bucketData.get(bucketData.size() - 1).value;
                default:
                    return 0.0;
            }
        }

        private void updateStats(String key, int increment) {
            nodeStats.put(key, (Integer) nodeStats.getOrDefault(key, 0) + increment);
        }

        Map<String, Object> getStats() {
            Map<String, Object> stats = new HashMap<>(nodeStats);
            stats.put("nodeId", nodeId);
            stats.put("seriesCount", series.size());
            stats.put("totalDataPoints", series.values().stream().mapToInt(TimeSeries::size).sum());
            stats.put("isHealthy", isHealthy);
            stats.put("lastHeartbeat", lastHeartbeat);
            return stats;
        }
    }

    class ConsistentHashRing {
        private TreeMap<Integer, String> ring;
        private Map<String, ShardNode> nodes;
        private int virtualNodes;

        ConsistentHashRing() {
            this.ring = new TreeMap<>();
            this.nodes = new HashMap<>();
            this.virtualNodes = 150;
        }

        void addNode(ShardNode node) {
            nodes.put(node.nodeId, node);

            for (int i = 0; i < virtualNodes; i++) {
                String virtualNodeId = node.nodeId + ":" + i;
                int hash = virtualNodeId.hashCode();
                ring.put(hash, node.nodeId);
            }
        }

        void removeNode(String nodeId) {
            nodes.remove(nodeId);

            for (int i = 0; i < virtualNodes; i++) {
                String virtualNodeId = nodeId + ":" + i;
                int hash = virtualNodeId.hashCode();
                ring.remove(hash);
            }
        }

        List<ShardNode> getNodes(String key, int count) {
            if (ring.isEmpty())
                return new ArrayList<>();

            int hash = key.hashCode();
            Set<String> selectedNodeIds = new LinkedHashSet<>();

            // Find clockwise nodes
            NavigableMap<Integer, String> tailMap = ring.tailMap(hash, true);
            NavigableMap<Integer, String> headMap = ring.headMap(hash, false);

            // Add nodes from tail map first
            for (String nodeId : tailMap.values()) {
                if (selectedNodeIds.size() >= count)
                    break;
                selectedNodeIds.add(nodeId);
            }

            // Add from head map if needed
            for (String nodeId : headMap.values()) {
                if (selectedNodeIds.size() >= count)
                    break;
                selectedNodeIds.add(nodeId);
            }

            return selectedNodeIds.stream()
                    .map(nodes::get)
                    .filter(Objects::nonNull)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
    }

    private ConsistentHashRing hashRing;
    private int replicationFactor;
    private ScheduledExecutorService scheduler;

    public DesignDistributedTimeSeriesDB(int nodeCount) {
        this(nodeCount, 2); // Default replication factor of 2
    }

    public DesignDistributedTimeSeriesDB(int nodeCount, int replicationFactor) {
        this.hashRing = new ConsistentHashRing();
        this.replicationFactor = replicationFactor;
        this.scheduler = Executors.newScheduledThreadPool(2);

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            String nodeId = "tsdb-node-" + i;
            ShardNode node = new ShardNode(nodeId);
            hashRing.addNode(node);
        }

        startHealthChecks();
    }

    public void insert(String metric, long timestamp, double value) {
        insert(metric, timestamp, value, null);
    }

    public void insert(String metric, long timestamp, double value, Map<String, String> tags) {
        if (metric == null || metric.trim().isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty");
        }

        List<ShardNode> nodes = hashRing.getNodes(metric, replicationFactor);

        for (ShardNode node : nodes) {
            if (node.isHealthy) {
                node.insert(metric, timestamp, value, tags);
            }
        }
    }

    public double query(String metric, long start, long end) {
        return query(metric, start, end, AggregationType.SUM);
    }

    public double query(String metric, long start, long end, AggregationType aggregationType) {
        if (metric == null || metric.trim().isEmpty()) {
            return 0.0;
        }

        List<ShardNode> nodes = hashRing.getNodes(metric, replicationFactor);

        // Query from first healthy node (due to replication, all should have same data)
        for (ShardNode node : nodes) {
            if (node.isHealthy) {
                return node.aggregate(metric, start, end, aggregationType);
            }
        }

        return 0.0;
    }

    public List<DataPoint> queryRaw(String metric, long start, long end) {
        if (metric == null || metric.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<ShardNode> nodes = hashRing.getNodes(metric, replicationFactor);

        // Query from first healthy node
        for (ShardNode node : nodes) {
            if (node.isHealthy) {
                return node.query(metric, start, end);
            }
        }

        return new ArrayList<>();
    }

    public List<DataPoint> downsample(String metric, long start, long end,
            long bucketSizeMs, AggregationType aggregationType) {
        if (metric == null || metric.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<ShardNode> nodes = hashRing.getNodes(metric, replicationFactor);

        // Query from first healthy node
        for (ShardNode node : nodes) {
            if (node.isHealthy) {
                return node.downsample(metric, start, end, bucketSizeMs, aggregationType);
            }
        }

        return new ArrayList<>();
    }

    public void delete(String metric, long timestamp) {
        if (metric == null || metric.trim().isEmpty()) {
            return;
        }

        List<ShardNode> nodes = hashRing.getNodes(metric, replicationFactor);

        for (ShardNode node : nodes) {
            if (node.isHealthy) {
                node.delete(metric, timestamp);
            }
        }
    }

    private void startHealthChecks() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (ShardNode node : hashRing.nodes.values()) {
                // Simulate health check (98% uptime)
                boolean healthy = Math.random() > 0.02;

                if (!healthy && node.isHealthy) {
                    node.isHealthy = false;
                    System.out.println("Node " + node.nodeId + " became unhealthy");
                } else if (healthy && !node.isHealthy) {
                    node.isHealthy = true;
                    System.out.println("Node " + node.nodeId + " recovered");
                }

                node.lastHeartbeat = System.currentTimeMillis();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public Map<String, Object> getClusterStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalNodes = hashRing.nodes.size();
        int healthyNodes = (int) hashRing.nodes.values().stream()
                .filter(node -> node.isHealthy)
                .count();

        int totalSeries = hashRing.nodes.values().stream()
                .mapToInt(node -> node.series.size())
                .sum();

        int totalDataPoints = hashRing.nodes.values().stream()
                .mapToInt(node -> node.series.values().stream().mapToInt(TimeSeries::size).sum())
                .sum();

        stats.put("totalNodes", totalNodes);
        stats.put("healthyNodes", healthyNodes);
        stats.put("totalSeries", totalSeries);
        stats.put("totalDataPoints", totalDataPoints);
        stats.put("replicationFactor", replicationFactor);

        return stats;
    }

    public List<Map<String, Object>> getNodeStats() {
        return hashRing.nodes.values().stream()
                .map(ShardNode::getStats)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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
        DesignDistributedTimeSeriesDB db = new DesignDistributedTimeSeriesDB(3);

        System.out.println("Initial cluster stats: " + db.getClusterStats());

        // Basic operations
        db.insert("cpu", 1, 0.5);
        db.insert("cpu", 2, 0.6);
        System.out.println(db.query("cpu", 1, 2)); // 1.1
        db.delete("cpu", 1);
        System.out.println(db.query("cpu", 1, 2)); // 0.6

        // Edge Case: Query non-existent metric
        System.out.println(db.query("mem", 1, 2)); // 0.0

        // Advanced operations with tags
        Map<String, String> tags = Map.of("host", "server1", "region", "us-east");
        db.insert("memory", 1000, 85.5, tags);
        db.insert("memory", 2000, 87.2, tags);
        db.insert("memory", 3000, 82.1, tags);

        // Different aggregation types
        System.out.println("\nMemory metrics aggregation:");
        System.out.println("SUM: " + db.query("memory", 1000, 3000, AggregationType.SUM));
        System.out.println("AVG: " + db.query("memory", 1000, 3000, AggregationType.AVG));
        System.out.println("MIN: " + db.query("memory", 1000, 3000, AggregationType.MIN));
        System.out.println("MAX: " + db.query("memory", 1000, 3000, AggregationType.MAX));
        System.out.println("COUNT: " + db.query("memory", 1000, 3000, AggregationType.COUNT));

        // Raw data query
        System.out.println("\nRaw data points:");
        List<DataPoint> rawData = db.queryRaw("memory", 1000, 3000);
        for (DataPoint point : rawData) {
            System.out.println("Timestamp: " + point.timestamp + ", Value: " + point.value);
        }

        // Downsampling example
        System.out.println("\nDownsampled data (1000ms buckets, AVG):");
        List<DataPoint> downsampled = db.downsample("memory", 1000, 3000, 1000, AggregationType.AVG);
        for (DataPoint point : downsampled) {
            System.out.println("Bucket: " + point.timestamp + ", Value: " + point.value);
        }

        // Insert more data for performance testing
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            db.insert("performance_test", startTime + i * 1000, Math.random() * 100);
        }

        System.out.println("\nAfter performance test:");
        System.out.println("Cluster stats: " + db.getClusterStats());

        // Show node statistics
        System.out.println("\nNode statistics:");
        for (Map<String, Object> nodeStats : db.getNodeStats()) {
            System.out.println(nodeStats);
        }

        // Wait for potential health check events
        Thread.sleep(2000);

        db.shutdown();
    }
}
