package design.hard;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

/**
 * Design API Gateway System
 *
 * Description: Design an API gateway that supports:
 * - Request routing and load balancing
 * - Authentication and authorization
 * - Rate limiting and throttling
 * - Request/response transformation
 * 
 * Constraints:
 * - Handle millions of requests per second
 * - Sub-millisecond routing decisions
 * - Extensible plugin architecture
 *
 * Follow-up:
 * - How to handle circuit breaking?
 * - Multi-region deployment?
 * 
 * Time Complexity: O(1) for routing, O(log n) for rate limiting
 * Space Complexity: O(routes + rate_limits)
 * 
 * Company Tags: Kong, AWS API Gateway, Zuul
 */
public class DesignAPIGateway {

    enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
    }

    enum AuthType {
        NONE, API_KEY, JWT, OAUTH2, BASIC_AUTH
    }

    class Route {
        String routeId;
        String path;
        HttpMethod method;
        String upstream;
        int priority;
        Map<String, String> headers;
        List<String> plugins;
        AuthType authType;
        boolean enabled;

        Route(String routeId, String path, HttpMethod method, String upstream) {
            this.routeId = routeId;
            this.path = path;
            this.method = method;
            this.upstream = upstream;
            this.priority = 0;
            this.headers = new HashMap<>();
            this.plugins = new ArrayList<>();
            this.authType = AuthType.NONE;
            this.enabled = true;
        }

        boolean matches(String requestPath, HttpMethod requestMethod) {
            if (!enabled)
                return false;
            if (method != null && method != requestMethod)
                return false;

            return matchesPath(requestPath, path);
        }

        private boolean matchesPath(String requestPath, String routePath) {
            if (routePath.equals("*"))
                return true;
            if (routePath.equals(requestPath))
                return true;

            // Handle wildcards
            if (routePath.endsWith("/*")) {
                String prefix = routePath.substring(0, routePath.length() - 2);
                return requestPath.startsWith(prefix);
            }

            // Handle path parameters
            String[] routeParts = routePath.split("/");
            String[] requestParts = requestPath.split("/");

            if (routeParts.length != requestParts.length)
                return false;

            for (int i = 0; i < routeParts.length; i++) {
                String routePart = routeParts[i];
                String requestPart = requestParts[i];

                if (!routePart.startsWith("{") && !routePart.equals(requestPart)) {
                    return false;
                }
            }

            return true;
        }
    }

    class RateLimitRule {
        String ruleId;
        String identifier; // IP, user, API key
        int requestsPerWindow;
        long windowSizeMs;
        Map<String, RateLimitCounter> counters;

        RateLimitRule(String ruleId, String identifier, int requestsPerWindow, long windowSizeMs) {
            this.ruleId = ruleId;
            this.identifier = identifier;
            this.requestsPerWindow = requestsPerWindow;
            this.windowSizeMs = windowSizeMs;
            this.counters = new ConcurrentHashMap<>();
        }

        boolean isAllowed(String key) {
            RateLimitCounter counter = counters.computeIfAbsent(key,
                    k -> new RateLimitCounter(windowSizeMs));

            return counter.isAllowed(requestsPerWindow);
        }
    }

    class RateLimitCounter {
        private long windowSizeMs;
        private long windowStart;
        private int count;

        RateLimitCounter(long windowSizeMs) {
            this.windowSizeMs = windowSizeMs;
            this.windowStart = System.currentTimeMillis();
            this.count = 0;
        }

        synchronized boolean isAllowed(int limit) {
            long now = System.currentTimeMillis();

            // Reset window if expired
            if (now - windowStart >= windowSizeMs) {
                windowStart = now;
                count = 0;
            }

            if (count < limit) {
                count++;
                return true;
            }

            return false;
        }
    }

    static class APIRequest {
        String requestId;
        String path;
        HttpMethod method;
        Map<String, String> headers;
        Map<String, String> queryParams;
        String body;
        String clientIP;
        long timestamp;

        APIRequest(String path, HttpMethod method, String clientIP) {
            this.requestId = UUID.randomUUID().toString();
            this.path = path;
            this.method = method;
            this.clientIP = clientIP;
            this.headers = new HashMap<>();
            this.queryParams = new HashMap<>();
            this.timestamp = System.currentTimeMillis();
        }
    }

    class APIResponse {
        String requestId;
        int statusCode;
        Map<String, String> headers;
        String body;
        long processingTime;

        APIResponse(String requestId, int statusCode) {
            this.requestId = requestId;
            this.statusCode = statusCode;
            this.headers = new HashMap<>();
            this.processingTime = 0;
        }
    }

    interface Plugin {
        String getName();

        APIResponse process(APIRequest request, APIResponse response);

        int getPriority();
    }

    class AuthenticationPlugin implements Plugin {
        private Map<String, String> apiKeys;

        AuthenticationPlugin() {
            apiKeys = new ConcurrentHashMap<>();
            apiKeys.put("key123", "user1");
            apiKeys.put("key456", "user2");
        }

        @Override
        public String getName() {
            return "authentication";
        }

        @Override
        public APIResponse process(APIRequest request, APIResponse response) {
            String apiKey = request.headers.get("X-API-Key");

            if (apiKey == null || !apiKeys.containsKey(apiKey)) {
                return new APIResponse(request.requestId, 401);
            }

            // Add user context
            request.headers.put("X-User-ID", apiKeys.get(apiKey));
            return response;
        }

        @Override
        public int getPriority() {
            return 1; // High priority
        }
    }

    class LoggingPlugin implements Plugin {
        @Override
        public String getName() {
            return "logging";
        }

        @Override
        public APIResponse process(APIRequest request, APIResponse response) {
            System.out.println("Request: " + request.method + " " + request.path +
                    " from " + request.clientIP);
            return response;
        }

        @Override
        public int getPriority() {
            return 10; // Low priority
        }
    }

    class CORSPlugin implements Plugin {
        @Override
        public String getName() {
            return "cors";
        }

        @Override
        public APIResponse process(APIRequest request, APIResponse response) {
            response.headers.put("Access-Control-Allow-Origin", "*");
            response.headers.put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.headers.put("Access-Control-Allow-Headers", "Content-Type, Authorization");
            return response;
        }

        @Override
        public int getPriority() {
            return 5;
        }
    }

    private List<Route> routes;
    private List<RateLimitRule> rateLimitRules;
    private Map<String, Plugin> plugins;
    private ExecutorService requestProcessor;
    private Map<String, String> upstreams;

    public DesignAPIGateway() {
        routes = new ArrayList<>();
        rateLimitRules = new ArrayList<>();
        plugins = new ConcurrentHashMap<>();
        requestProcessor = Executors.newFixedThreadPool(20);
        upstreams = new ConcurrentHashMap<>();

        // Register default plugins
        registerPlugin(new AuthenticationPlugin());
        registerPlugin(new LoggingPlugin());
        registerPlugin(new CORSPlugin());

        // Default upstreams
        upstreams.put("user-service", "http://localhost:8081");
        upstreams.put("order-service", "http://localhost:8082");
        upstreams.put("payment-service", "http://localhost:8083");
    }

    public void addRoute(String routeId, String path, HttpMethod method, String upstream,
            int priority, AuthType authType) {
        Route route = new Route(routeId, path, method, upstream);
        route.priority = priority;
        route.authType = authType;

        routes.add(route);
        routes.sort((a, b) -> Integer.compare(b.priority, a.priority));
    }

    public void addRateLimitRule(String ruleId, String identifier, int requestsPerWindow, long windowSizeMs) {
        RateLimitRule rule = new RateLimitRule(ruleId, identifier, requestsPerWindow, windowSizeMs);
        rateLimitRules.add(rule);
    }

    public void registerPlugin(Plugin plugin) {
        plugins.put(plugin.getName(), plugin);
    }

    public CompletableFuture<APIResponse> processRequest(APIRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();

            try {
                // 1. Find matching route
                Route matchedRoute = findMatchingRoute(request);
                if (matchedRoute == null) {
                    APIResponse response = new APIResponse(request.requestId, 404);
                    response.body = "Route not found";
                    return response;
                }

                // 2. Apply rate limiting
                if (!checkRateLimit(request)) {
                    APIResponse response = new APIResponse(request.requestId, 429);
                    response.body = "Rate limit exceeded";
                    return response;
                }

                // 3. Authentication (if required)
                if (matchedRoute.authType != AuthType.NONE && !authenticate(request, matchedRoute)) {
                    APIResponse response = new APIResponse(request.requestId, 401);
                    response.body = "Authentication failed";
                    return response;
                }

                // 4. Apply plugins
                APIResponse response = new APIResponse(request.requestId, 200);

                List<Plugin> sortedPlugins = matchedRoute.plugins.stream()
                        .map(plugins::get)
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparingInt(Plugin::getPriority))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                for (Plugin plugin : sortedPlugins) {
                    response = plugin.process(request, response);
                    if (response.statusCode != 200) {
                        return response;
                    }
                }

                // 5. Forward to upstream service
                response = forwardToUpstream(request, matchedRoute);

                response.processingTime = System.currentTimeMillis() - startTime;
                return response;

            } catch (Exception e) {
                APIResponse response = new APIResponse(request.requestId, 500);
                response.body = "Internal server error: " + e.getMessage();
                response.processingTime = System.currentTimeMillis() - startTime;
                return response;
            }
        }, requestProcessor);
    }

    private Route findMatchingRoute(APIRequest request) {
        return routes.stream()
                .filter(route -> route.matches(request.path, request.method))
                .findFirst()
                .orElse(null);
    }

    private boolean checkRateLimit(APIRequest request) {
        for (RateLimitRule rule : rateLimitRules) {
            String key = getKeyForRule(request, rule);
            if (key != null && !rule.isAllowed(key)) {
                return false;
            }
        }
        return true;
    }

    private String getKeyForRule(APIRequest request, RateLimitRule rule) {
        switch (rule.identifier) {
            case "ip":
                return request.clientIP;
            case "api_key":
                return request.headers.get("X-API-Key");
            case "user":
                return request.headers.get("X-User-ID");
            default:
                return null;
        }
    }

    private boolean authenticate(APIRequest request, Route route) {
        switch (route.authType) {
            case API_KEY:
                return request.headers.containsKey("X-API-Key");
            case JWT:
                return validateJWT(request.headers.get("Authorization"));
            case BASIC_AUTH:
                return validateBasicAuth(request.headers.get("Authorization"));
            default:
                return true;
        }
    }

    private boolean validateJWT(String authHeader) {
        // Simplified JWT validation
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private boolean validateBasicAuth(String authHeader) {
        // Simplified Basic Auth validation
        return authHeader != null && authHeader.startsWith("Basic ");
    }

    private APIResponse forwardToUpstream(APIRequest request, Route route) {
        String upstreamUrl = upstreams.get(route.upstream);
        if (upstreamUrl == null) {
            APIResponse response = new APIResponse(request.requestId, 502);
            response.body = "Upstream not found";
            return response;
        }

        // Simulate upstream call
        try {
            Thread.sleep(50 + (int) (Math.random() * 100)); // 50-150ms latency

            // Simulate occasional upstream errors
            if (Math.random() < 0.05) {
                APIResponse response = new APIResponse(request.requestId, 503);
                response.body = "Upstream service unavailable";
                return response;
            }

            APIResponse response = new APIResponse(request.requestId, 200);
            response.body = "{\"message\":\"Success from " + route.upstream + "\"}";
            response.headers.put("Content-Type", "application/json");
            response.headers.put("X-Upstream", route.upstream);

            return response;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            APIResponse response = new APIResponse(request.requestId, 500);
            response.body = "Request interrupted";
            return response;
        }
    }

    public Map<String, Object> getGatewayStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalRoutes", routes.size());
        stats.put("enabledRoutes", routes.stream().mapToInt(r -> r.enabled ? 1 : 0).sum());
        stats.put("rateLimitRules", rateLimitRules.size());
        stats.put("registeredPlugins", plugins.size());
        stats.put("upstreams", upstreams.size());

        // Route stats by upstream
        Map<String, Long> routesByUpstream = routes.stream()
                .collect(Collectors.groupingBy(r -> r.upstream, Collectors.counting()));
        stats.put("routesByUpstream", routesByUpstream);

        return stats;
    }

    public void shutdown() {
        requestProcessor.shutdown();
        try {
            if (!requestProcessor.awaitTermination(5, TimeUnit.SECONDS)) {
                requestProcessor.shutdownNow();
            }
        } catch (InterruptedException e) {
            requestProcessor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        DesignAPIGateway gateway = new DesignAPIGateway();

        // Configure routes
        gateway.addRoute("users-get", "/api/users/*", HttpMethod.GET, "user-service", 10, AuthType.API_KEY);
        gateway.addRoute("users-post", "/api/users", HttpMethod.POST, "user-service", 10, AuthType.API_KEY);
        gateway.addRoute("orders-all", "/api/orders/*", HttpMethod.GET, "order-service", 5, AuthType.JWT);
        gateway.addRoute("payments", "/api/payments/*", HttpMethod.POST, "payment-service", 1, AuthType.JWT);
        gateway.addRoute("health", "/health", HttpMethod.GET, "user-service", 100, AuthType.NONE);

        // Configure rate limiting
        gateway.addRateLimitRule("ip-limit", "ip", 100, 60000); // 100 requests per minute per IP
        gateway.addRateLimitRule("api-key-limit", "api_key", 1000, 60000); // 1000 requests per minute per API key

        System.out.println("Gateway stats: " + gateway.getGatewayStats());

        // Simulate requests
        List<CompletableFuture<APIResponse>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            // Create test requests
            APIRequest request1 = new APIRequest("/api/users/123", HttpMethod.GET, "192.168.1." + (i % 10));
            request1.headers.put("X-API-Key", "key123");

            APIRequest request2 = new APIRequest("/api/orders/456", HttpMethod.GET, "192.168.1." + (i % 5));
            request2.headers.put("Authorization", "Bearer jwt-token");

            APIRequest request3 = new APIRequest("/health", HttpMethod.GET, "192.168.1.100");

            // Process requests
            futures.add(gateway.processRequest(request1));
            futures.add(gateway.processRequest(request2));
            futures.add(gateway.processRequest(request3));
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Display results
        System.out.println("\nSample request results:");
        for (int i = 0; i < Math.min(10, futures.size()); i++) {
            APIResponse response = futures.get(i).get();
            System.out.println("Request " + i + ": Status " + response.statusCode +
                    ", Processing time: " + response.processingTime + "ms");
        }

        System.out.println("\nFinal gateway stats: " + gateway.getGatewayStats());

        gateway.shutdown();
    }
}
