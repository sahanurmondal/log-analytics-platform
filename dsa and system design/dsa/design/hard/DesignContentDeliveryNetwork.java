package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Content Delivery Network (CDN)
 *
 * Description: Design a CDN system that supports:
 * - Global edge server distribution
 * - Content caching and invalidation
 * - Load balancing and failover
 * - Analytics and monitoring
 * 
 * Constraints:
 * - Support millions of requests per second
 * - Minimize latency globally
 * - Handle cache miss scenarios
 *
 * Follow-up:
 * - How to handle cache coherency?
 * - Dynamic content delivery?
 * 
 * Time Complexity: O(1) for cache hits, O(log n) for routing
 * Space Complexity: O(content_size * edge_servers)
 * 
 * Company Tags: CloudFlare, Akamai, Amazon CloudFront
 */
public class DesignContentDeliveryNetwork {

    enum ContentType {
        STATIC, DYNAMIC, STREAMING
    }

    enum CachePolicy {
        LRU, LFU, TTL_BASED
    }

    class Content {
        String contentId;
        String url;
        ContentType type;
        byte[] data;
        Map<String, String> headers;
        long createdTime;
        long lastModified;
        long ttl;
        String etag;

        Content(String contentId, String url, ContentType type, byte[] data, long ttl) {
            this.contentId = contentId;
            this.url = url;
            this.type = type;
            this.data = data != null ? Arrays.copyOf(data, data.length) : new byte[0];
            this.headers = new HashMap<>();
            this.createdTime = System.currentTimeMillis();
            this.lastModified = createdTime;
            this.ttl = ttl;
            this.etag = generateETag();
        }

        private String generateETag() {
            return "\"" + Integer.toHexString(Arrays.hashCode(data)) + "\"";
        }

        boolean isExpired() {
            return ttl > 0 && System.currentTimeMillis() - createdTime > ttl;
        }

        void updateContent(byte[] newData) {
            this.data = Arrays.copyOf(newData, newData.length);
            this.lastModified = System.currentTimeMillis();
            this.etag = generateETag();
        }
    }

    class EdgeServer {
        String serverId;
        String location;
        double latitude;
        double longitude;
        Map<String, Content> cache;
        CachePolicy cachePolicy;
        long maxCacheSize;
        long currentCacheSize;
        Map<String, Long> accessTimes; // For LRU
        Map<String, Integer> accessCounts; // For LFU
        int requestCount;
        double averageResponseTime;

        EdgeServer(String serverId, String location, double lat, double lng, long maxCacheSize) {
            this.serverId = serverId;
            this.location = location;
            this.latitude = lat;
            this.longitude = lng;
            this.cache = new ConcurrentHashMap<>();
            this.cachePolicy = CachePolicy.LRU;
            this.maxCacheSize = maxCacheSize;
            this.currentCacheSize = 0;
            this.accessTimes = new ConcurrentHashMap<>();
            this.accessCounts = new ConcurrentHashMap<>();
            this.requestCount = 0;
            this.averageResponseTime = 0.0;
        }

        Content getContent(String contentId) {
            Content content = cache.get(contentId);
            if (content != null && !content.isExpired()) {
                updateAccessStats(contentId);
                return content;
            } else if (content != null && content.isExpired()) {
                evictContent(contentId);
            }
            return null;
        }

        boolean cacheContent(Content content) {
            if (currentCacheSize + content.data.length > maxCacheSize) {
                if (!makeSpace(content.data.length)) {
                    return false; // Cannot make enough space
                }
            }

            cache.put(content.contentId, content);
            currentCacheSize += content.data.length;
            updateAccessStats(content.contentId);

            return true;
        }

        private boolean makeSpace(long requiredSpace) {
            List<String> candidates = getCandidatesForEviction();
            long freedSpace = 0;

            for (String contentId : candidates) {
                if (freedSpace >= requiredSpace)
                    break;

                Content content = cache.get(contentId);
                if (content != null) {
                    evictContent(contentId);
                    freedSpace += content.data.length;
                }
            }

            return freedSpace >= requiredSpace;
        }

        private List<String> getCandidatesForEviction() {
            List<String> candidates = new ArrayList<>(cache.keySet());

            switch (cachePolicy) {
                case LRU:
                    candidates.sort((a, b) -> Long.compare(
                            accessTimes.getOrDefault(a, 0L),
                            accessTimes.getOrDefault(b, 0L)));
                    break;
                case LFU:
                    candidates.sort((a, b) -> Integer.compare(
                            accessCounts.getOrDefault(a, 0),
                            accessCounts.getOrDefault(b, 0)));
                    break;
                case TTL_BASED:
                    candidates.sort((a, b) -> {
                        Content contentA = cache.get(a);
                        Content contentB = cache.get(b);
                        if (contentA == null || contentB == null)
                            return 0;
                        return Long.compare(contentA.createdTime, contentB.createdTime);
                    });
                    break;
            }

            return candidates;
        }

        private void evictContent(String contentId) {
            Content content = cache.remove(contentId);
            if (content != null) {
                currentCacheSize -= content.data.length;
                accessTimes.remove(contentId);
                accessCounts.remove(contentId);
            }
        }

        private void updateAccessStats(String contentId) {
            accessTimes.put(contentId, System.currentTimeMillis());
            accessCounts.put(contentId, accessCounts.getOrDefault(contentId, 0) + 1);
        }

        void invalidateContent(String contentId) {
            evictContent(contentId);
        }

        double calculateLatency(double clientLat, double clientLng) {
            // Simplified latency calculation based on distance
            double distance = calculateDistance(latitude, longitude, clientLat, clientLng);
            return Math.max(10, distance / 100); // Min 10ms, +1ms per 100km
        }

        private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
            double earthRadius = 6371; // km
            double dLat = Math.toRadians(lat2 - lat1);
            double dLng = Math.toRadians(lng2 - lng1);

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return earthRadius * c;
        }
    }

    class OriginServer {
        String serverId;
        Map<String, Content> contents;

        OriginServer(String serverId) {
            this.serverId = serverId;
            this.contents = new ConcurrentHashMap<>();
        }

        void storeContent(Content content) {
            contents.put(content.contentId, content);
        }

        Content fetchContent(String contentId) {
            return contents.get(contentId);
        }

        void updateContent(String contentId, byte[] newData) {
            Content content = contents.get(contentId);
            if (content != null) {
                content.updateContent(newData);
            }
        }
    }

    private List<EdgeServer> edgeServers;
    private OriginServer originServer;
    private Map<String, Set<String>> contentToServers; // Track which servers have which content
    private ScheduledExecutorService scheduler;

    public DesignContentDeliveryNetwork() {
        edgeServers = new ArrayList<>();
        originServer = new OriginServer("origin-server");
        contentToServers = new ConcurrentHashMap<>();
        scheduler = Executors.newScheduledThreadPool(2);

        initializeEdgeServers();
        startCacheManagement();
    }

    private void initializeEdgeServers() {
        // Initialize edge servers in major locations
        edgeServers.add(new EdgeServer("edge-us-east", "Virginia", 39.0458, -77.4413, 1024 * 1024 * 100)); // 100MB
        edgeServers.add(new EdgeServer("edge-us-west", "California", 37.7749, -122.4194, 1024 * 1024 * 100));
        edgeServers.add(new EdgeServer("edge-eu", "Frankfurt", 50.1109, 8.6821, 1024 * 1024 * 100));
        edgeServers.add(new EdgeServer("edge-asia", "Tokyo", 35.6762, 139.6503, 1024 * 1024 * 100));
        edgeServers.add(new EdgeServer("edge-au", "Sydney", -33.8688, 151.2093, 1024 * 1024 * 100));
    }

    public void storeContent(String contentId, String url, ContentType type, byte[] data, long ttl) {
        Content content = new Content(contentId, url, type, data, ttl);
        originServer.storeContent(content);
    }

    public Content requestContent(String contentId, double clientLat, double clientLng) {
        // Find best edge server for client
        EdgeServer bestServer = findBestEdgeServer(clientLat, clientLng);

        // Try to get content from edge server
        Content content = bestServer.getContent(contentId);

        if (content == null) {
            // Cache miss - fetch from origin
            content = originServer.fetchContent(contentId);
            if (content != null) {
                // Cache content on edge server
                if (bestServer.cacheContent(content)) {
                    contentToServers.computeIfAbsent(contentId, k -> ConcurrentHashMap.newKeySet())
                            .add(bestServer.serverId);
                }
            }
        }

        // Update server stats
        if (bestServer != null) {
            bestServer.requestCount++;
            double responseTime = bestServer.calculateLatency(clientLat, clientLng);
            bestServer.averageResponseTime = (bestServer.averageResponseTime * 0.9) + (responseTime * 0.1);
        }

        return content;
    }

    private EdgeServer findBestEdgeServer(double clientLat, double clientLng) {
        return edgeServers.stream()
                .min(Comparator.comparingDouble(server -> server.calculateLatency(clientLat, clientLng)))
                .orElse(edgeServers.get(0));
    }

    public void invalidateContent(String contentId) {
        // Invalidate on all edge servers that have this content
        Set<String> serverIds = contentToServers.get(contentId);
        if (serverIds != null) {
            for (String serverId : serverIds) {
                EdgeServer server = edgeServers.stream()
                        .filter(s -> s.serverId.equals(serverId))
                        .findFirst()
                        .orElse(null);

                if (server != null) {
                    server.invalidateContent(contentId);
                }
            }
            contentToServers.remove(contentId);
        }
    }

    public void updateContent(String contentId, byte[] newData) {
        // Update content on origin server
        originServer.updateContent(contentId, newData);

        // Invalidate cached copies
        invalidateContent(contentId);
    }

    private void startCacheManagement() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (EdgeServer server : edgeServers) {
                // Clean expired content
                cleanExpiredContent(server);
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    private void cleanExpiredContent(EdgeServer server) {
        List<String> expiredContent = new ArrayList<>();

        for (Map.Entry<String, Content> entry : server.cache.entrySet()) {
            if (entry.getValue().isExpired()) {
                expiredContent.add(entry.getKey());
            }
        }

        for (String contentId : expiredContent) {
            server.invalidateContent(contentId);

            Set<String> serverIds = contentToServers.get(contentId);
            if (serverIds != null) {
                serverIds.remove(server.serverId);
                if (serverIds.isEmpty()) {
                    contentToServers.remove(contentId);
                }
            }
        }
    }

    public Map<String, Object> getCDNStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalCachedItems = edgeServers.stream()
                .mapToInt(server -> server.cache.size())
                .sum();

        long totalCacheSize = edgeServers.stream()
                .mapToLong(server -> server.currentCacheSize)
                .sum();

        int totalRequests = edgeServers.stream()
                .mapToInt(server -> server.requestCount)
                .sum();

        double avgResponseTime = edgeServers.stream()
                .mapToDouble(server -> server.averageResponseTime)
                .average()
                .orElse(0.0);

        stats.put("totalEdgeServers", edgeServers.size());
        stats.put("totalCachedItems", totalCachedItems);
        stats.put("totalCacheSize", totalCacheSize);
        stats.put("totalRequests", totalRequests);
        stats.put("averageResponseTime", avgResponseTime);
        stats.put("originContentCount", originServer.contents.size());

        return stats;
    }

    public List<Map<String, Object>> getEdgeServerStats() {
        List<Map<String, Object>> serverStats = new ArrayList<>();

        for (EdgeServer server : edgeServers) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("serverId", server.serverId);
            stats.put("location", server.location);
            stats.put("cachedItems", server.cache.size());
            stats.put("cacheSize", server.currentCacheSize);
            stats.put("maxCacheSize", server.maxCacheSize);
            stats.put("cacheUtilization", (double) server.currentCacheSize / server.maxCacheSize * 100);
            stats.put("requestCount", server.requestCount);
            stats.put("averageResponseTime", server.averageResponseTime);

            serverStats.add(stats);
        }

        return serverStats;
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignContentDeliveryNetwork cdn = new DesignContentDeliveryNetwork();

        // Store some content
        String htmlContent = "<html><body><h1>Hello CDN!</h1></body></html>";
        cdn.storeContent("page1", "/index.html", ContentType.STATIC, htmlContent.getBytes(), 3600000);

        String imageContent = "fake-image-data-" + "x".repeat(1000);
        cdn.storeContent("img1", "/logo.png", ContentType.STATIC, imageContent.getBytes(), 7200000);

        String apiContent = "{\"message\":\"Hello from API\"}";
        cdn.storeContent("api1", "/api/hello", ContentType.DYNAMIC, apiContent.getBytes(), 300000);

        System.out.println("Initial CDN stats: " + cdn.getCDNStats());

        // Simulate requests from different locations
        // US East Coast client
        Content content1 = cdn.requestContent("page1", 40.7128, -74.0060); // NYC
        System.out.println("NYC client got content: " + (content1 != null));

        // EU client
        Content content2 = cdn.requestContent("page1", 51.5074, -0.1278); // London
        System.out.println("London client got content: " + (content2 != null));

        // Asia client
        Content content3 = cdn.requestContent("img1", 35.6762, 139.6503); // Tokyo
        System.out.println("Tokyo client got content: " + (content3 != null));

        // Multiple requests from same location (should hit cache)
        for (int i = 0; i < 5; i++) {
            cdn.requestContent("page1", 40.7128, -74.0060);
            cdn.requestContent("img1", 40.7128, -74.0060);
        }

        System.out.println("\nAfter requests CDN stats: " + cdn.getCDNStats());

        // Show edge server stats
        System.out.println("\nEdge server statistics:");
        for (Map<String, Object> serverStats : cdn.getEdgeServerStats()) {
            System.out.println(serverStats);
        }

        // Test content invalidation
        cdn.updateContent("page1", "<html><body><h1>Updated CDN Content!</h1></body></html>".getBytes());
        System.out.println("\nContent updated and invalidated");

        // Request updated content
        Content updatedContent = cdn.requestContent("page1", 40.7128, -74.0060);
        System.out.println("Updated content retrieved: " + (updatedContent != null));

        System.out.println("\nFinal CDN stats: " + cdn.getCDNStats());

        cdn.shutdown();
    }
}
