package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Service Mesh System
 *
 * Description: Design a service mesh that supports:
 * - Traffic management and routing
 * - Security policies and mTLS
 * - Observability and monitoring
 * - Circuit breaking and retries
 * 
 * Constraints:
 * - Support thousands of microservices
 * - Low latency overhead
 * - High availability and fault tolerance
 *
 * Follow-up:
 * - How to handle service discovery?
 * - Cross-cluster communication?
 * 
 * Time Complexity: O(1) for routing, O(log n) for policy evaluation
 * Space Complexity: O(services * policies)
 * 
 * Company Tags: Istio, Linkerd, Consul Connect
 */
public class DesignServiceMesh {

    enum TrafficPolicy {
        ROUND_ROBIN, WEIGHTED, LEAST_CONN, RANDOM
    }

    enum SecurityMode {
        PERMISSIVE, STRICT, DISABLED
    }

    class ServiceProxy {
        String serviceId;
        String proxyId;
        Map<String, String> labels;
        List<String> upstreamServices;
        TrafficPolicy trafficPolicy;
        SecurityMode securityMode;
        Map<String, Integer> weights;
        CircuitBreakerConfig circuitBreaker;

        ServiceProxy(String serviceId, String proxyId) {
            this.serviceId = serviceId;
            this.proxyId = proxyId;
            this.labels = new HashMap<>();
            this.upstreamServices = new ArrayList<>();
            this.trafficPolicy = TrafficPolicy.ROUND_ROBIN;
            this.securityMode = SecurityMode.PERMISSIVE;
            this.weights = new HashMap<>();
            this.circuitBreaker = new CircuitBreakerConfig();
        }
    }

    class CircuitBreakerConfig {
        int maxFailures;
        long timeoutMs;
        long recoveryTimeMs;
        int currentFailures;
        long lastFailureTime;
        boolean isOpen;

        CircuitBreakerConfig() {
            this.maxFailures = 5;
            this.timeoutMs = 30000;
            this.recoveryTimeMs = 60000;
            this.currentFailures = 0;
            this.isOpen = false;
        }

        boolean allowRequest() {
            if (!isOpen)
                return true;

            if (System.currentTimeMillis() - lastFailureTime > recoveryTimeMs) {
                isOpen = false;
                currentFailures = 0;
                return true;
            }

            return false;
        }

        void recordSuccess() {
            currentFailures = 0;
            isOpen = false;
        }

        void recordFailure() {
            currentFailures++;
            lastFailureTime = System.currentTimeMillis();

            if (currentFailures >= maxFailures) {
                isOpen = true;
            }
        }
    }

    class TrafficRule {
        String ruleId;
        String sourceService;
        String destinationService;
        Map<String, String> matchLabels;
        TrafficPolicy policy;
        Map<String, Integer> weights;
        int priority;

        TrafficRule(String ruleId, String sourceService, String destinationService) {
            this.ruleId = ruleId;
            this.sourceService = sourceService;
            this.destinationService = destinationService;
            this.matchLabels = new HashMap<>();
            this.policy = TrafficPolicy.ROUND_ROBIN;
            this.weights = new HashMap<>();
            this.priority = 0;
        }

        boolean matches(String source, String destination, Map<String, String> labels) {
            if (!sourceService.equals("*") && !sourceService.equals(source)) {
                return false;
            }

            if (!destinationService.equals("*") && !destinationService.equals(destination)) {
                return false;
            }

            for (Map.Entry<String, String> entry : matchLabels.entrySet()) {
                String labelValue = labels.get(entry.getKey());
                if (!entry.getValue().equals(labelValue)) {
                    return false;
                }
            }

            return true;
        }
    }

    class SecurityPolicy {
        String policyId;
        String sourceService;
        String destinationService;
        SecurityMode mode;
        Set<String> allowedMethods;
        Map<String, String> requiredHeaders;

        SecurityPolicy(String policyId, String sourceService, String destinationService) {
            this.policyId = policyId;
            this.sourceService = sourceService;
            this.destinationService = destinationService;
            this.mode = SecurityMode.PERMISSIVE;
            this.allowedMethods = new HashSet<>();
            this.requiredHeaders = new HashMap<>();
        }

        boolean isAllowed(String source, String destination, String method, Map<String, String> headers) {
            if (mode == SecurityMode.DISABLED)
                return true;

            if (!sourceService.equals("*") && !sourceService.equals(source)) {
                return false;
            }

            if (!destinationService.equals("*") && !destinationService.equals(destination)) {
                return false;
            }

            if (!allowedMethods.isEmpty() && !allowedMethods.contains(method)) {
                return false;
            }

            for (Map.Entry<String, String> entry : requiredHeaders.entrySet()) {
                String headerValue = headers.get(entry.getKey());
                if (!entry.getValue().equals(headerValue)) {
                    return false;
                }
            }

            return true;
        }
    }

    class RequestMetrics {
        String serviceId;
        long requestCount;
        long successCount;
        long errorCount;
        double averageLatency;
        long lastRequestTime;

        RequestMetrics(String serviceId) {
            this.serviceId = serviceId;
            this.requestCount = 0;
            this.successCount = 0;
            this.errorCount = 0;
            this.averageLatency = 0.0;
            this.lastRequestTime = 0;
        }

        void recordRequest(boolean success, double latency) {
            requestCount++;
            lastRequestTime = System.currentTimeMillis();

            if (success) {
                successCount++;
            } else {
                errorCount++;
            }

            averageLatency = (averageLatency * (requestCount - 1) + latency) / requestCount;
        }

        double getSuccessRate() {
            return requestCount > 0 ? (double) successCount / requestCount : 0.0;
        }
    }

    private Map<String, ServiceProxy> proxies;
    private List<TrafficRule> trafficRules;
    private List<SecurityPolicy> securityPolicies;
    private Map<String, RequestMetrics> metrics;
    private ScheduledExecutorService scheduler;

    public DesignServiceMesh() {
        proxies = new ConcurrentHashMap<>();
        trafficRules = new ArrayList<>();
        securityPolicies = new ArrayList<>();
        metrics = new ConcurrentHashMap<>();
        scheduler = Executors.newScheduledThreadPool(2);

        startMetricsCollection();
    }

    public void registerService(String serviceId, String proxyId, Map<String, String> labels) {
        ServiceProxy proxy = new ServiceProxy(serviceId, proxyId);
        if (labels != null) {
            proxy.labels.putAll(labels);
        }

        proxies.put(serviceId, proxy);
        metrics.put(serviceId, new RequestMetrics(serviceId));

        System.out.println("Registered service: " + serviceId + " with proxy: " + proxyId);
    }

    public void addTrafficRule(String ruleId, String sourceService, String destinationService,
            TrafficPolicy policy, Map<String, Integer> weights) {
        TrafficRule rule = new TrafficRule(ruleId, sourceService, destinationService);
        rule.policy = policy;
        if (weights != null) {
            rule.weights.putAll(weights);
        }

        trafficRules.add(rule);
        trafficRules.sort((a, b) -> Integer.compare(b.priority, a.priority));
    }

    public void addSecurityPolicy(String policyId, String sourceService, String destinationService,
            SecurityMode mode, Set<String> allowedMethods) {
        SecurityPolicy policy = new SecurityPolicy(policyId, sourceService, destinationService);
        policy.mode = mode;
        if (allowedMethods != null) {
            policy.allowedMethods.addAll(allowedMethods);
        }

        securityPolicies.add(policy);
    }

    public String routeRequest(String sourceService, String destinationService, String method,
            Map<String, String> headers, Map<String, String> labels) {

        // Check security policies
        boolean allowed = securityPolicies.stream()
                .filter(policy -> policy.sourceService.equals("*") || policy.sourceService.equals(sourceService))
                .filter(policy -> policy.destinationService.equals("*")
                        || policy.destinationService.equals(destinationService))
                .findFirst()
                .map(policy -> policy.isAllowed(sourceService, destinationService, method, headers))
                .orElse(true);

        if (!allowed) {
            System.out.println("Request denied by security policy");
            return null;
        }

        // Find applicable traffic rule
        TrafficRule applicableRule = trafficRules.stream()
                .filter(rule -> rule.matches(sourceService, destinationService, labels))
                .findFirst()
                .orElse(null);

        // Check circuit breaker
        ServiceProxy proxy = proxies.get(destinationService);
        if (proxy != null && !proxy.circuitBreaker.allowRequest()) {
            System.out.println("Request blocked by circuit breaker for " + destinationService);
            return null;
        }

        // Route based on policy
        String targetInstance = selectTargetInstance(destinationService, applicableRule);

        // Simulate request
        boolean success = simulateRequest(targetInstance);
        double latency = 50 + Math.random() * 100; // 50-150ms

        // Record metrics
        RequestMetrics requestMetrics = metrics.get(destinationService);
        if (requestMetrics != null) {
            requestMetrics.recordRequest(success, latency);
        }

        // Update circuit breaker
        if (proxy != null) {
            if (success) {
                proxy.circuitBreaker.recordSuccess();
            } else {
                proxy.circuitBreaker.recordFailure();
            }
        }

        return success ? targetInstance : null;
    }

    private String selectTargetInstance(String serviceId, TrafficRule rule) {
        ServiceProxy proxy = proxies.get(serviceId);
        if (proxy == null)
            return serviceId + "-instance-default";

        TrafficPolicy policy = rule != null ? rule.policy : proxy.trafficPolicy;

        switch (policy) {
            case ROUND_ROBIN:
                return serviceId + "-instance-" + (System.currentTimeMillis() % 3);
            case WEIGHTED:
                return selectWeightedInstance(serviceId, rule);
            case LEAST_CONN:
                return selectLeastConnectedInstance(serviceId);
            case RANDOM:
                return serviceId + "-instance-" + new Random().nextInt(3);
            default:
                return serviceId + "-instance-0";
        }
    }

    private String selectWeightedInstance(String serviceId, TrafficRule rule) {
        Map<String, Integer> weights = rule != null ? rule.weights
                : Map.of("instance-0", 50, "instance-1", 30, "instance-2", 20);

        int totalWeight = weights.values().stream().mapToInt(Integer::intValue).sum();
        int random = new Random().nextInt(totalWeight);

        int currentWeight = 0;
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            currentWeight += entry.getValue();
            if (random < currentWeight) {
                return serviceId + "-" + entry.getKey();
            }
        }

        return serviceId + "-instance-0";
    }

    private String selectLeastConnectedInstance(String serviceId) {
        // Simulate selecting instance with least connections
        RequestMetrics metrics = this.metrics.get(serviceId);
        if (metrics != null && metrics.requestCount % 3 == 0) {
            return serviceId + "-instance-0"; // Least loaded
        } else if (metrics != null && metrics.requestCount % 3 == 1) {
            return serviceId + "-instance-1";
        } else {
            return serviceId + "-instance-2";
        }
    }

    private boolean simulateRequest(String targetInstance) {
        // Simulate request with 95% success rate
        return Math.random() > 0.05;
    }

    private void startMetricsCollection() {
        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("=== Service Mesh Metrics ===");
            for (RequestMetrics metric : metrics.values()) {
                if (metric.requestCount > 0) {
                    System.out.println(String.format(
                            "Service: %s, Requests: %d, Success Rate: %.2f%%, Avg Latency: %.2fms",
                            metric.serviceId, metric.requestCount,
                            metric.getSuccessRate() * 100, metric.averageLatency));
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    public Map<String, Object> getMeshStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalServices", proxies.size());
        stats.put("trafficRules", trafficRules.size());
        stats.put("securityPolicies", securityPolicies.size());

        long totalRequests = metrics.values().stream()
                .mapToLong(m -> m.requestCount)
                .sum();

        double avgSuccessRate = metrics.values().stream()
                .filter(m -> m.requestCount > 0)
                .mapToDouble(RequestMetrics::getSuccessRate)
                .average()
                .orElse(0.0);

        stats.put("totalRequests", totalRequests);
        stats.put("averageSuccessRate", avgSuccessRate * 100);

        return stats;
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignServiceMesh serviceMesh = new DesignServiceMesh();

        // Register services
        serviceMesh.registerService("user-service", "user-proxy",
                Map.of("version", "v1", "tier", "backend"));
        serviceMesh.registerService("order-service", "order-proxy",
                Map.of("version", "v2", "tier", "backend"));
        serviceMesh.registerService("payment-service", "payment-proxy",
                Map.of("version", "v1", "tier", "critical"));

        // Add traffic rules
        serviceMesh.addTrafficRule("rule-1", "user-service", "order-service",
                TrafficPolicy.WEIGHTED, Map.of("instance-0", 70, "instance-1", 30));

        serviceMesh.addTrafficRule("rule-2", "*", "payment-service",
                TrafficPolicy.LEAST_CONN, null);

        // Add security policies
        serviceMesh.addSecurityPolicy("policy-1", "*", "payment-service",
                SecurityMode.STRICT, Set.of("POST", "GET"));

        System.out.println("Initial mesh stats: " + serviceMesh.getMeshStats());

        // Simulate traffic
        Map<String, String> headers = Map.of("Authorization", "Bearer token");
        Map<String, String> labels = Map.of("environment", "prod");

        for (int i = 0; i < 50; i++) {
            // User service calling order service
            String result1 = serviceMesh.routeRequest("user-service", "order-service",
                    "GET", headers, labels);

            // Order service calling payment service
            String result2 = serviceMesh.routeRequest("order-service", "payment-service",
                    "POST", headers, labels);

            // Direct call to user service
            String result3 = serviceMesh.routeRequest("frontend", "user-service",
                    "GET", headers, labels);

            if (i % 10 == 0) {
                System.out.println("Batch " + (i / 10) + " completed");
            }

            Thread.sleep(100);
        }

        System.out.println("\nFinal mesh stats: " + serviceMesh.getMeshStats());

        serviceMesh.shutdown();
    }
}
