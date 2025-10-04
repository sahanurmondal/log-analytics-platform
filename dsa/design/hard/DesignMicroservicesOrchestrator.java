package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Microservices Orchestrator
 *
 * Description: Design a system that orchestrates microservices:
 * - Service discovery and registration
 * - Load balancing and circuit breakers
 * - Request routing and API gateway
 * - Health monitoring and auto-scaling
 * 
 * Constraints:
 * - Support hundreds of microservices
 * - Handle service failures gracefully
 * - Provide real-time monitoring
 *
 * Follow-up:
 * - How to implement service mesh?
 * - Distributed tracing support?
 * 
 * Time Complexity: O(1) for service calls, O(log n) for discovery
 * Space Complexity: O(services * instances)
 * 
 * Company Tags: Kubernetes, Istio, Netflix Eureka
 */
public class DesignMicroservicesOrchestrator {

    enum ServiceStatus {
        HEALTHY, UNHEALTHY, STARTING, STOPPING, UNKNOWN
    }

    enum CircuitBreakerState {
        CLOSED, OPEN, HALF_OPEN
    }

    class ServiceInstance {
        String instanceId;
        String serviceName;
        String host;
        int port;
        ServiceStatus status;
        Map<String, String> metadata;
        long lastHealthCheck;
        long registrationTime;
        int currentLoad;
        double responseTimeMs;

        ServiceInstance(String instanceId, String serviceName, String host, int port) {
            this.instanceId = instanceId;
            this.serviceName = serviceName;
            this.host = host;
            this.port = port;
            this.status = ServiceStatus.STARTING;
            this.metadata = new HashMap<>();
            this.lastHealthCheck = System.currentTimeMillis();
            this.registrationTime = System.currentTimeMillis();
            this.currentLoad = 0;
            this.responseTimeMs = 0.0;
        }

        String getEndpoint() {
            return host + ":" + port;
        }

        boolean isHealthy() {
            return status == ServiceStatus.HEALTHY &&
                    System.currentTimeMillis() - lastHealthCheck < 30000; // 30s timeout
        }
    }

    class ServiceDefinition {
        String serviceName;
        String version;
        Set<ServiceInstance> instances;
        Map<String, String> configuration;
        int minInstances;
        int maxInstances;
        double cpuThreshold;
        double memoryThreshold;

        ServiceDefinition(String serviceName, String version) {
            this.serviceName = serviceName;
            this.version = version;
            this.instances = ConcurrentHashMap.newKeySet();
            this.configuration = new ConcurrentHashMap<>();
            this.minInstances = 1;
            this.maxInstances = 10;
            this.cpuThreshold = 0.8;
            this.memoryThreshold = 0.8;
        }

        List<ServiceInstance> getHealthyInstances() {
            return instances.stream()
                    .filter(ServiceInstance::isHealthy)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
    }

    class CircuitBreaker {
        String serviceName;
        CircuitBreakerState state;
        int failureCount;
        int failureThreshold;
        long lastFailureTime;
        long timeout;
        int requestCount;
        int successCount;

        CircuitBreaker(String serviceName) {
            this.serviceName = serviceName;
            this.state = CircuitBreakerState.CLOSED;
            this.failureCount = 0;
            this.failureThreshold = 5;
            this.timeout = 60000; // 1 minute
            this.requestCount = 0;
            this.successCount = 0;
        }

        boolean allowRequest() {
            switch (state) {
                case CLOSED:
                    return true;
                case OPEN:
                    if (System.currentTimeMillis() - lastFailureTime > timeout) {
                        state = CircuitBreakerState.HALF_OPEN;
                        return true;
                    }
                    return false;
                case HALF_OPEN:
                    return true;
                default:
                    return false;
            }
        }

        void recordSuccess() {
            successCount++;
            requestCount++;

            if (state == CircuitBreakerState.HALF_OPEN) {
                if (successCount >= 3) {
                    state = CircuitBreakerState.CLOSED;
                    failureCount = 0;
                    resetCounters();
                }
            }
        }

        void recordFailure() {
            failureCount++;
            requestCount++;
            lastFailureTime = System.currentTimeMillis();

            if (state == CircuitBreakerState.CLOSED && failureCount >= failureThreshold) {
                state = CircuitBreakerState.OPEN;
            } else if (state == CircuitBreakerState.HALF_OPEN) {
                state = CircuitBreakerState.OPEN;
            }
        }

        private void resetCounters() {
            requestCount = 0;
            successCount = 0;
        }
    }

    class LoadBalancer {
        enum Strategy {
            ROUND_ROBIN, LEAST_CONNECTIONS, WEIGHTED_RESPONSE_TIME, RANDOM
        }

        private Strategy strategy;
        private Map<String, Integer> roundRobinCounters;

        LoadBalancer(Strategy strategy) {
            this.strategy = strategy;
            this.roundRobinCounters = new ConcurrentHashMap<>();
        }

        ServiceInstance selectInstance(List<ServiceInstance> instances) {
            if (instances.isEmpty())
                return null;

            switch (strategy) {
                case ROUND_ROBIN:
                    return selectRoundRobin(instances);
                case LEAST_CONNECTIONS:
                    return selectLeastConnections(instances);
                case WEIGHTED_RESPONSE_TIME:
                    return selectWeightedResponseTime(instances);
                case RANDOM:
                    return instances.get(new Random().nextInt(instances.size()));
                default:
                    return instances.get(0);
            }
        }

        private ServiceInstance selectRoundRobin(List<ServiceInstance> instances) {
            String serviceKey = instances.get(0).serviceName;
            int counter = roundRobinCounters.getOrDefault(serviceKey, 0);
            ServiceInstance selected = instances.get(counter % instances.size());
            roundRobinCounters.put(serviceKey, counter + 1);
            return selected;
        }

        private ServiceInstance selectLeastConnections(List<ServiceInstance> instances) {
            return instances.stream()
                    .min(Comparator.comparingInt(instance -> instance.currentLoad))
                    .orElse(instances.get(0));
        }

        private ServiceInstance selectWeightedResponseTime(List<ServiceInstance> instances) {
            return instances.stream()
                    .min(Comparator.comparingDouble(instance -> instance.responseTimeMs))
                    .orElse(instances.get(0));
        }
    }

    private Map<String, ServiceDefinition> services;
    private Map<String, CircuitBreaker> circuitBreakers;
    private LoadBalancer loadBalancer;
    private ScheduledExecutorService scheduler;
    private final Object registrationLock = new Object();

    public DesignMicroservicesOrchestrator() {
        services = new ConcurrentHashMap<>();
        circuitBreakers = new ConcurrentHashMap<>();
        loadBalancer = new LoadBalancer(LoadBalancer.Strategy.ROUND_ROBIN);
        scheduler = Executors.newScheduledThreadPool(3);

        startHealthCheckScheduler();
        startAutoScalingScheduler();
    }

    public boolean registerService(String serviceName, String version, String instanceId,
            String host, int port, Map<String, String> metadata) {
        synchronized (registrationLock) {
            ServiceDefinition service = services.computeIfAbsent(serviceName,
                    k -> new ServiceDefinition(serviceName, version));

            ServiceInstance instance = new ServiceInstance(instanceId, serviceName, host, port);
            if (metadata != null) {
                instance.metadata.putAll(metadata);
            }

            service.instances.add(instance);

            // Initialize circuit breaker
            circuitBreakers.computeIfAbsent(serviceName, CircuitBreaker::new);

            System.out.println("Registered service instance: " + serviceName + "/" + instanceId);
            return true;
        }
    }

    public boolean deregisterService(String serviceName, String instanceId) {
        ServiceDefinition service = services.get(serviceName);
        if (service != null) {
            boolean removed = service.instances.removeIf(instance -> instance.instanceId.equals(instanceId));

            if (removed) {
                System.out.println("Deregistered service instance: " + serviceName + "/" + instanceId);
            }
            return removed;
        }
        return false;
    }

    public ServiceInstance discoverService(String serviceName) {
        ServiceDefinition service = services.get(serviceName);
        if (service == null)
            return null;

        CircuitBreaker circuitBreaker = circuitBreakers.get(serviceName);
        if (circuitBreaker != null && !circuitBreaker.allowRequest()) {
            System.out.println("Circuit breaker OPEN for service: " + serviceName);
            return null;
        }

        List<ServiceInstance> healthyInstances = service.getHealthyInstances();
        return loadBalancer.selectInstance(healthyInstances);
    }

    public boolean callService(String serviceName, String endpoint, Object request) {
        ServiceInstance instance = discoverService(serviceName);
        if (instance == null) {
            return false;
        }

        CircuitBreaker circuitBreaker = circuitBreakers.get(serviceName);
        instance.currentLoad++;

        try {
            // Simulate service call
            long startTime = System.currentTimeMillis();
            boolean success = simulateServiceCall(instance, endpoint, request);
            long responseTime = System.currentTimeMillis() - startTime;

            // Update metrics
            instance.responseTimeMs = (instance.responseTimeMs * 0.9) + (responseTime * 0.1);

            if (success) {
                if (circuitBreaker != null) {
                    circuitBreaker.recordSuccess();
                }
                return true;
            } else {
                if (circuitBreaker != null) {
                    circuitBreaker.recordFailure();
                }
                return false;
            }
        } finally {
            instance.currentLoad--;
        }
    }

    private boolean simulateServiceCall(ServiceInstance instance, String endpoint, Object request) {
        // Simulate network call with 95% success rate
        try {
            Thread.sleep(50 + new Random().nextInt(100)); // 50-150ms response time
            return Math.random() > 0.05;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private void startHealthCheckScheduler() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (ServiceDefinition service : services.values()) {
                for (ServiceInstance instance : service.instances) {
                    boolean healthy = performHealthCheck(instance);
                    instance.status = healthy ? ServiceStatus.HEALTHY : ServiceStatus.UNHEALTHY;
                    instance.lastHealthCheck = System.currentTimeMillis();
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private boolean performHealthCheck(ServiceInstance instance) {
        // Simulate health check with 98% success rate
        return Math.random() > 0.02;
    }

    private void startAutoScalingScheduler() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (ServiceDefinition service : services.values()) {
                checkAutoScaling(service);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void checkAutoScaling(ServiceDefinition service) {
        List<ServiceInstance> healthyInstances = service.getHealthyInstances();

        if (healthyInstances.isEmpty())
            return;

        double avgLoad = healthyInstances.stream()
                .mapToInt(instance -> instance.currentLoad)
                .average()
                .orElse(0.0);

        double avgResponseTime = healthyInstances.stream()
                .mapToDouble(instance -> instance.responseTimeMs)
                .average()
                .orElse(0.0);

        // Scale up if high load or response time
        if ((avgLoad > 5 || avgResponseTime > 200) &&
                healthyInstances.size() < service.maxInstances) {

            scaleUp(service);
        }
        // Scale down if low load
        else if (avgLoad < 1 && avgResponseTime < 50 &&
                healthyInstances.size() > service.minInstances) {

            scaleDown(service);
        }
    }

    private void scaleUp(ServiceDefinition service) {
        String newInstanceId = service.serviceName + "-" + UUID.randomUUID().toString().substring(0, 8);
        String host = "auto-" + service.instances.size();
        int port = 8080 + service.instances.size();

        registerService(service.serviceName, service.version, newInstanceId, host, port, null);
        System.out.println("Auto-scaled UP: " + service.serviceName + " -> " + newInstanceId);
    }

    private void scaleDown(ServiceDefinition service) {
        List<ServiceInstance> instances = new ArrayList<>(service.instances);
        if (!instances.isEmpty()) {
            ServiceInstance toRemove = instances.get(instances.size() - 1);
            deregisterService(service.serviceName, toRemove.instanceId);
            System.out.println("Auto-scaled DOWN: " + service.serviceName + " -> " + toRemove.instanceId);
        }
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalServices = services.size();
        int totalInstances = services.values().stream()
                .mapToInt(service -> service.instances.size())
                .sum();

        int healthyInstances = services.values().stream()
                .mapToInt(service -> service.getHealthyInstances().size())
                .sum();

        stats.put("totalServices", totalServices);
        stats.put("totalInstances", totalInstances);
        stats.put("healthyInstances", healthyInstances);
        stats.put("circuitBreakers", circuitBreakers.size());

        // Circuit breaker states
        Map<String, String> cbStates = new HashMap<>();
        circuitBreakers.forEach((service, cb) -> cbStates.put(service, cb.state.name()));
        stats.put("circuitBreakerStates", cbStates);

        return stats;
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignMicroservicesOrchestrator orchestrator = new DesignMicroservicesOrchestrator();

        // Register services
        orchestrator.registerService("user-service", "v1", "user-1", "host1", 8081, null);
        orchestrator.registerService("user-service", "v1", "user-2", "host2", 8081, null);
        orchestrator.registerService("order-service", "v1", "order-1", "host3", 8082, null);
        orchestrator.registerService("payment-service", "v1", "payment-1", "host4", 8083, null);

        System.out.println("Initial stats: " + orchestrator.getSystemStats());

        // Simulate service calls
        for (int i = 0; i < 20; i++) {
            boolean userCall = orchestrator.callService("user-service", "/users/123", null);
            boolean orderCall = orchestrator.callService("order-service", "/orders", null);
            boolean paymentCall = orchestrator.callService("payment-service", "/payments", null);

            System.out.println("Calls - User: " + userCall + ", Order: " + orderCall + ", Payment: " + paymentCall);
            Thread.sleep(100);
        }

        // Wait for health checks and auto-scaling
        Thread.sleep(15000);

        System.out.println("After simulation stats: " + orchestrator.getSystemStats());

        orchestrator.shutdown();
    }
}
