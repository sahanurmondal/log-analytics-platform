package miscellaneous.medium;

import java.util.*;

/**
 * LeetCode 535: Encode and Decode URLs
 * https://leetcode.com/problems/encode-and-decode-urls/
 * 
 * Companies: Meta, Amazon, Google, Microsoft, Uber, Twitter
 * Frequency: High (Asked in 400+ interviews)
 *
 * Description:
 * Design a system to encode and decode URLs.
 * 
 * Note: This is how TinyURL works: you enter a URL and it returns a shortened
 * URL.
 * Requirements:
 * 1. encode(longUrl) -> shortUrl
 * 2. decode(shortUrl) -> longUrl
 * 
 * Follow-up Questions:
 * 1. How would you design this for a distributed system?
 * 2. What about collision handling?
 * 3. How to implement expiration?
 * 4. Can you track analytics?
 * 5. How to ensure security?
 */
public class EncodeAndDecodeURLs {

    // Approach 1: Simple counter-based encoding
    public static class Codec1 {
        private Map<String, String> shortToLong = new HashMap<>();
        private Map<String, String> longToShort = new HashMap<>();
        private int counter = 0;
        private static final String BASE_URL = "http://tinyurl.com/";

        // Encodes a URL to a shortened URL.
        public String encode(String longUrl) {
            if (longToShort.containsKey(longUrl)) {
                return longToShort.get(longUrl);
            }

            String shortUrl = BASE_URL + counter++;
            shortToLong.put(shortUrl, longUrl);
            longToShort.put(longUrl, shortUrl);
            return shortUrl;
        }

        // Decodes a shortened URL to its original URL.
        public String decode(String shortUrl) {
            return shortToLong.get(shortUrl);
        }
    }

    // Approach 2: Random string-based encoding
    public static class Codec2 {
        private Map<String, String> shortToLong = new HashMap<>();
        private Map<String, String> longToShort = new HashMap<>();
        private static final String BASE_URL = "http://tinyurl.com/";
        private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private static final int URL_LENGTH = 6;
        private Random random = new Random();

        public String encode(String longUrl) {
            if (longToShort.containsKey(longUrl)) {
                return longToShort.get(longUrl);
            }

            String shortCode;
            do {
                shortCode = generateRandomString();
            } while (shortToLong.containsKey(BASE_URL + shortCode));

            String shortUrl = BASE_URL + shortCode;
            shortToLong.put(shortUrl, longUrl);
            longToShort.put(longUrl, shortUrl);
            return shortUrl;
        }

        public String decode(String shortUrl) {
            return shortToLong.get(shortUrl);
        }

        private String generateRandomString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < URL_LENGTH; i++) {
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            return sb.toString();
        }
    }

    // Approach 3: Hash-based encoding
    public static class Codec3 {
        private Map<String, String> shortToLong = new HashMap<>();
        private Map<String, String> longToShort = new HashMap<>();
        private static final String BASE_URL = "http://tinyurl.com/";

        public String encode(String longUrl) {
            if (longToShort.containsKey(longUrl)) {
                return longToShort.get(longUrl);
            }

            String shortCode = Integer.toHexString(longUrl.hashCode());

            // Handle collisions
            while (shortToLong.containsKey(BASE_URL + shortCode)) {
                shortCode = shortCode + "a"; // Simple collision resolution
            }

            String shortUrl = BASE_URL + shortCode;
            shortToLong.put(shortUrl, longUrl);
            longToShort.put(longUrl, shortUrl);
            return shortUrl;
        }

        public String decode(String shortUrl) {
            return shortToLong.get(shortUrl);
        }
    }

    // Approach 4: Base62 encoding
    public static class Codec4 {
        private Map<String, String> shortToLong = new HashMap<>();
        private Map<String, String> longToShort = new HashMap<>();
        private static final String BASE_URL = "http://tinyurl.com/";
        private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private int counter = 0;

        public String encode(String longUrl) {
            if (longToShort.containsKey(longUrl)) {
                return longToShort.get(longUrl);
            }

            String shortCode = toBase62(counter++);
            String shortUrl = BASE_URL + shortCode;
            shortToLong.put(shortUrl, longUrl);
            longToShort.put(longUrl, shortUrl);
            return shortUrl;
        }

        public String decode(String shortUrl) {
            return shortToLong.get(shortUrl);
        }

        private String toBase62(int num) {
            if (num == 0)
                return "a";

            StringBuilder sb = new StringBuilder();
            while (num > 0) {
                sb.append(BASE62.charAt(num % 62));
                num /= 62;
            }
            return sb.reverse().toString();
        }
    }

    // Follow-up 1: Distributed system design with database
    public static class DistributedCodec {
        private Map<String, URLData> shortToData = new HashMap<>(); // Simulating database
        private Map<String, String> longToShort = new HashMap<>();
        private static final String BASE_URL = "http://tinyurl.com/";
        private AtomicLongGenerator idGenerator;

        public DistributedCodec(int nodeId) {
            this.idGenerator = new AtomicLongGenerator(nodeId);
        }

        public String encode(String longUrl) {
            if (longToShort.containsKey(longUrl)) {
                String shortUrl = longToShort.get(longUrl);
                URLData data = shortToData.get(shortUrl);
                if (data != null && !data.isExpired()) {
                    return shortUrl;
                }
            }

            long id = idGenerator.nextId();
            String shortCode = toBase62(id);
            String shortUrl = BASE_URL + shortCode;

            URLData data = new URLData(longUrl, System.currentTimeMillis(),
                    System.currentTimeMillis() + 86400000); // 24 hours expiry

            shortToData.put(shortUrl, data);
            longToShort.put(longUrl, shortUrl);
            return shortUrl;
        }

        public String decode(String shortUrl) {
            URLData data = shortToData.get(shortUrl);
            if (data != null && !data.isExpired()) {
                data.incrementAccessCount();
                return data.longUrl;
            }
            return null;
        }

        public URLStats getStats(String shortUrl) {
            URLData data = shortToData.get(shortUrl);
            return data != null ? new URLStats(data) : null;
        }

        private String toBase62(long num) {
            if (num == 0)
                return "a";

            String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();

            while (num > 0) {
                sb.append(base62.charAt((int) (num % 62)));
                num /= 62;
            }
            return sb.reverse().toString();
        }
    }

    // Follow-up 2: Advanced codec with collision detection
    public static class AdvancedCodec {
        private Map<String, String> shortToLong = new HashMap<>();
        private Map<String, String> longToShort = new HashMap<>();
        private Set<String> usedCodes = new HashSet<>();
        private static final String BASE_URL = "http://tinyurl.com/";
        private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private Random random = new Random();
        private int maxRetries = 100;

        public String encode(String longUrl) {
            if (longToShort.containsKey(longUrl)) {
                return longToShort.get(longUrl);
            }

            String shortCode = generateUniqueCode();
            if (shortCode == null) {
                throw new RuntimeException("Unable to generate unique code after " + maxRetries + " attempts");
            }

            String shortUrl = BASE_URL + shortCode;
            shortToLong.put(shortUrl, longUrl);
            longToShort.put(longUrl, shortUrl);
            usedCodes.add(shortCode);
            return shortUrl;
        }

        public String decode(String shortUrl) {
            return shortToLong.get(shortUrl);
        }

        private String generateUniqueCode() {
            for (int attempt = 0; attempt < maxRetries; attempt++) {
                String code = generateRandomString(6);
                if (!usedCodes.contains(code)) {
                    return code;
                }
            }
            return null;
        }

        private String generateRandomString(int length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            return sb.toString();
        }
    }

    // Follow-up 3: Codec with expiration
    public static class ExpiringCodec {
        private Map<String, ExpiringURLData> shortToData = new HashMap<>();
        private Map<String, String> longToShort = new HashMap<>();
        private static final String BASE_URL = "http://tinyurl.com/";
        private static final long DEFAULT_TTL = 86400000; // 24 hours
        private int counter = 0;

        public String encode(String longUrl) {
            return encode(longUrl, DEFAULT_TTL);
        }

        public String encode(String longUrl, long ttlMillis) {
            // Clean expired URLs
            cleanExpired();

            if (longToShort.containsKey(longUrl)) {
                String shortUrl = longToShort.get(longUrl);
                ExpiringURLData data = shortToData.get(shortUrl);
                if (data != null && !data.isExpired()) {
                    return shortUrl;
                }
            }

            String shortCode = toBase62(counter++);
            String shortUrl = BASE_URL + shortCode;

            long expiryTime = System.currentTimeMillis() + ttlMillis;
            ExpiringURLData data = new ExpiringURLData(longUrl, expiryTime);

            shortToData.put(shortUrl, data);
            longToShort.put(longUrl, shortUrl);
            return shortUrl;
        }

        public String decode(String shortUrl) {
            ExpiringURLData data = shortToData.get(shortUrl);
            if (data != null && !data.isExpired()) {
                return data.longUrl;
            }

            // Clean up expired entry
            if (data != null) {
                shortToData.remove(shortUrl);
                longToShort.remove(data.longUrl);
            }

            return null;
        }

        private void cleanExpired() {
            Iterator<Map.Entry<String, ExpiringURLData>> iterator = shortToData.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ExpiringURLData> entry = iterator.next();
                if (entry.getValue().isExpired()) {
                    longToShort.remove(entry.getValue().longUrl);
                    iterator.remove();
                }
            }
        }

        private String toBase62(int num) {
            if (num == 0)
                return "a";

            String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();

            while (num > 0) {
                sb.append(base62.charAt(num % 62));
                num /= 62;
            }
            return sb.reverse().toString();
        }
    }

    // Follow-up 4: Analytics-enabled codec
    public static class AnalyticsCodec {
        private Map<String, AnalyticsURLData> shortToData = new HashMap<>();
        private Map<String, String> longToShort = new HashMap<>();
        private static final String BASE_URL = "http://tinyurl.com/";
        private int counter = 0;

        public String encode(String longUrl) {
            if (longToShort.containsKey(longUrl)) {
                return longToShort.get(longUrl);
            }

            String shortCode = toBase62(counter++);
            String shortUrl = BASE_URL + shortCode;

            AnalyticsURLData data = new AnalyticsURLData(longUrl);
            shortToData.put(shortUrl, data);
            longToShort.put(longUrl, shortUrl);
            return shortUrl;
        }

        public String decode(String shortUrl) {
            return decode(shortUrl, null);
        }

        public String decode(String shortUrl, String userAgent) {
            AnalyticsURLData data = shortToData.get(shortUrl);
            if (data != null) {
                data.recordAccess(userAgent);
                return data.longUrl;
            }
            return null;
        }

        public AnalyticsData getAnalytics(String shortUrl) {
            AnalyticsURLData data = shortToData.get(shortUrl);
            return data != null ? data.getAnalytics() : null;
        }

        private String toBase62(int num) {
            if (num == 0)
                return "a";

            String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();

            while (num > 0) {
                sb.append(base62.charAt(num % 62));
                num /= 62;
            }
            return sb.reverse().toString();
        }
    }

    // Follow-up 5: Secure codec with authentication
    public static class SecureCodec {
        private Map<String, SecureURLData> shortToData = new HashMap<>();
        private Map<String, String> longToShort = new HashMap<>();
        private static final String BASE_URL = "http://tinyurl.com/";
        private int counter = 0;

        public String encode(String longUrl, String userId) {
            String key = longUrl + ":" + userId;
            if (longToShort.containsKey(key)) {
                return longToShort.get(key);
            }

            String shortCode = toBase62(counter++);
            String shortUrl = BASE_URL + shortCode;

            SecureURLData data = new SecureURLData(longUrl, userId);
            shortToData.put(shortUrl, data);
            longToShort.put(key, shortUrl);
            return shortUrl;
        }

        public String decode(String shortUrl, String userId) {
            SecureURLData data = shortToData.get(shortUrl);
            if (data != null && data.hasAccess(userId)) {
                return data.longUrl;
            }
            return null; // Unauthorized access
        }

        public boolean setPublic(String shortUrl, String userId) {
            SecureURLData data = shortToData.get(shortUrl);
            if (data != null && data.isOwner(userId)) {
                data.setPublic(true);
                return true;
            }
            return false;
        }

        private String toBase62(int num) {
            if (num == 0)
                return "a";

            String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();

            while (num > 0) {
                sb.append(base62.charAt(num % 62));
                num /= 62;
            }
            return sb.reverse().toString();
        }
    }

    // Supporting classes
    private static class URLData {
        String longUrl;
        long createdAt;
        long expiresAt;
        int accessCount;

        URLData(String longUrl, long createdAt, long expiresAt) {
            this.longUrl = longUrl;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.accessCount = 0;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }

        void incrementAccessCount() {
            accessCount++;
        }
    }

    private static class ExpiringURLData {
        String longUrl;
        long expiresAt;

        ExpiringURLData(String longUrl, long expiresAt) {
            this.longUrl = longUrl;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }

    private static class AnalyticsURLData {
        String longUrl;
        long createdAt;
        List<AccessRecord> accessRecords;

        AnalyticsURLData(String longUrl) {
            this.longUrl = longUrl;
            this.createdAt = System.currentTimeMillis();
            this.accessRecords = new ArrayList<>();
        }

        void recordAccess(String userAgent) {
            accessRecords.add(new AccessRecord(System.currentTimeMillis(), userAgent));
        }

        AnalyticsData getAnalytics() {
            return new AnalyticsData(this);
        }
    }

    private static class SecureURLData {
        String longUrl;
        String ownerId;
        boolean isPublic;

        SecureURLData(String longUrl, String ownerId) {
            this.longUrl = longUrl;
            this.ownerId = ownerId;
            this.isPublic = false;
        }

        boolean hasAccess(String userId) {
            return isPublic || ownerId.equals(userId);
        }

        boolean isOwner(String userId) {
            return ownerId.equals(userId);
        }

        void setPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }
    }

    private static class AccessRecord {
        long timestamp;
        String userAgent;

        AccessRecord(long timestamp, String userAgent) {
            this.timestamp = timestamp;
            this.userAgent = userAgent;
        }
    }

    private static class AnalyticsData {
        long createdAt;
        int totalAccesses;
        Map<String, Integer> userAgentCounts;

        AnalyticsData(AnalyticsURLData data) {
            this.createdAt = data.createdAt;
            this.totalAccesses = data.accessRecords.size();
            this.userAgentCounts = new HashMap<>();

            for (AccessRecord record : data.accessRecords) {
                if (record.userAgent != null) {
                    userAgentCounts.put(record.userAgent,
                            userAgentCounts.getOrDefault(record.userAgent, 0) + 1);
                }
            }
        }
    }

    private static class URLStats {
        String longUrl;
        long createdAt;
        long expiresAt;
        int accessCount;

        URLStats(URLData data) {
            this.longUrl = data.longUrl;
            this.createdAt = data.createdAt;
            this.expiresAt = data.expiresAt;
            this.accessCount = data.accessCount;
        }
    }

    // Distributed ID generator
    private static class AtomicLongGenerator {
        private java.util.concurrent.atomic.AtomicLong counter;
        private final int nodeId;

        AtomicLongGenerator(int nodeId) {
            this.nodeId = nodeId;
            this.counter = new java.util.concurrent.atomic.AtomicLong(0);
        }

        long nextId() {
            long id = counter.incrementAndGet();
            return (id << 10) | nodeId; // Combine counter with node ID
        }
    }

    // Performance testing
    public static void performanceTest() {
        System.out.println("=== Performance Comparison ===");

        String[] urls = {
                "https://leetcode.com/problems/design-tinyurl",
                "https://www.google.com/search?q=java+programming",
                "https://github.com/username/repository/issues/123",
                "https://stackoverflow.com/questions/123456/how-to-code",
                "https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html"
        };

        // Test different codecs
        Codec1 codec1 = new Codec1();
        Codec2 codec2 = new Codec2();
        Codec3 codec3 = new Codec3();
        Codec4 codec4 = new Codec4();

        // Encoding performance
        long start = System.nanoTime();
        for (String url : urls) {
            codec1.encode(url);
        }
        long codec1Time = System.nanoTime() - start;

        start = System.nanoTime();
        for (String url : urls) {
            codec2.encode(url);
        }
        long codec2Time = System.nanoTime() - start;

        start = System.nanoTime();
        for (String url : urls) {
            codec3.encode(url);
        }
        long codec3Time = System.nanoTime() - start;

        start = System.nanoTime();
        for (String url : urls) {
            codec4.encode(url);
        }
        long codec4Time = System.nanoTime() - start;

        System.out.println("Encoding Performance:");
        System.out.println("Counter-based: " + codec1Time / 1000.0 + " microseconds");
        System.out.println("Random-based: " + codec2Time / 1000.0 + " microseconds");
        System.out.println("Hash-based: " + codec3Time / 1000.0 + " microseconds");
        System.out.println("Base62-based: " + codec4Time / 1000.0 + " microseconds");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        Codec1 codec = new Codec1();
        String longUrl = "https://leetcode.com/problems/design-tinyurl";

        String shortUrl = codec.encode(longUrl);
        String decoded = codec.decode(shortUrl);

        System.out.println("Original: " + longUrl);
        System.out.println("Encoded: " + shortUrl);
        System.out.println("Decoded: " + decoded);
        System.out.println("Success: " + longUrl.equals(decoded));

        // Test Case 2: Different approaches
        System.out.println("\n=== Test Case 2: Different Approaches ===");

        Codec2 randomCodec = new Codec2();
        Codec3 hashCodec = new Codec3();
        Codec4 base62Codec = new Codec4();

        String testUrl = "https://www.example.com/very/long/path/to/resource";

        System.out.println("Original URL: " + testUrl);
        System.out.println("Random encoding: " + randomCodec.encode(testUrl));
        System.out.println("Hash encoding: " + hashCodec.encode(testUrl));
        System.out.println("Base62 encoding: " + base62Codec.encode(testUrl));

        // Test Case 3: Distributed system
        System.out.println("\n=== Test Case 3: Distributed System ===");

        DistributedCodec distributedCodec = new DistributedCodec(1);
        String distributedUrl = "https://distributed.example.com";

        String shortDistributed = distributedCodec.encode(distributedUrl);
        String decodedDistributed = distributedCodec.decode(shortDistributed);

        System.out.println("Distributed encoded: " + shortDistributed);
        System.out.println("Distributed decoded: " + decodedDistributed);

        URLStats stats = distributedCodec.getStats(shortDistributed);
        if (stats != null) {
            System.out.println("Access count: " + stats.accessCount);
        }

        // Test Case 4: Expiring URLs
        System.out.println("\n=== Test Case 4: Expiring URLs ===");

        ExpiringCodec expiringCodec = new ExpiringCodec();
        String expiringUrl = "https://temporary.example.com";

        String shortExpiring = expiringCodec.encode(expiringUrl, 1000); // 1 second TTL
        System.out.println("Expiring encoded: " + shortExpiring);
        System.out.println("Immediate decode: " + expiringCodec.decode(shortExpiring));

        // Wait and try again
        try {
            Thread.sleep(1100); // Wait for expiration
            System.out.println("After expiration: " + expiringCodec.decode(shortExpiring));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Test Case 5: Analytics
        System.out.println("\n=== Test Case 5: Analytics ===");

        AnalyticsCodec analyticsCodec = new AnalyticsCodec();
        String analyticsUrl = "https://analytics.example.com";

        String shortAnalytics = analyticsCodec.encode(analyticsUrl);
        System.out.println("Analytics encoded: " + shortAnalytics);

        // Simulate multiple accesses
        analyticsCodec.decode(shortAnalytics, "Mozilla/5.0 Chrome");
        analyticsCodec.decode(shortAnalytics, "Mozilla/5.0 Firefox");
        analyticsCodec.decode(shortAnalytics, "Mozilla/5.0 Chrome");

        AnalyticsData analytics = analyticsCodec.getAnalytics(shortAnalytics);
        if (analytics != null) {
            System.out.println("Total accesses: " + analytics.totalAccesses);
            System.out.println("User agents: " + analytics.userAgentCounts);
        }

        // Test Case 6: Security
        System.out.println("\n=== Test Case 6: Security ===");

        SecureCodec secureCodec = new SecureCodec();
        String secureUrl = "https://private.example.com";

        String shortSecure = secureCodec.encode(secureUrl, "user123");
        System.out.println("Secure encoded: " + shortSecure);

        System.out.println("Owner access: " + secureCodec.decode(shortSecure, "user123"));
        System.out.println("Unauthorized access: " + secureCodec.decode(shortSecure, "user456"));

        secureCodec.setPublic(shortSecure, "user123");
        System.out.println("After making public: " + secureCodec.decode(shortSecure, "user456"));

        // Test Case 7: Collision handling
        System.out.println("\n=== Test Case 7: Collision Handling ===");

        AdvancedCodec advancedCodec = new AdvancedCodec();

        // Encode multiple URLs to test collision handling
        for (int i = 0; i < 10; i++) {
            String url = "https://example" + i + ".com";
            String shortUrl2 = advancedCodec.encode(url);
            System.out.println("URL " + i + ": " + shortUrl2);
        }

        // Test Case 8: Duplicate URL handling
        System.out.println("\n=== Test Case 8: Duplicate URL Handling ===");

        Codec1 duplicateCodec = new Codec1();
        String duplicateUrl = "https://duplicate.example.com";

        String short1 = duplicateCodec.encode(duplicateUrl);
        String short2 = duplicateCodec.encode(duplicateUrl); // Same URL again

        System.out.println("First encoding: " + short1);
        System.out.println("Second encoding: " + short2);
        System.out.println("Same result: " + short1.equals(short2));

        // Test Case 9: Edge cases
        System.out.println("\n=== Test Case 9: Edge Cases ===");

        Codec1 edgeCodec = new Codec1();

        // Test various URL formats
        String[] edgeUrls = {
                "",
                "http://a.com",
                "https://very-long-domain-name-that-exceeds-normal-length.example.org/path/to/resource",
                "ftp://files.example.com/file.txt",
                "https://example.com?param1=value1&param2=value2#section"
        };

        for (String url : edgeUrls) {
            if (!url.isEmpty()) {
                String encoded = edgeCodec.encode(url);
                String decodedUrl = edgeCodec.decode(encoded);
                System.out.println("URL: " + url);
                System.out.println("Encoded: " + encoded);
                System.out.println("Success: " + url.equals(decodedUrl));
                System.out.println();
            }
        }

        // Performance testing
        performanceTest();

        // Test Case 10: Load testing
        System.out.println("\n=== Test Case 10: Load Testing ===");

        Codec4 loadCodec = new Codec4();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            String url = "https://load-test-" + i + ".example.com";
            loadCodec.encode(url);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Encoded 1000 URLs in " + (endTime - startTime) + " ms");

        System.out.println("\nEncode and Decode URLs testing completed successfully!");
    }
}
