package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Design Distributed Circuit Breaker
 * 
 * Description:
 * Design a distributed circuit breaker system that prevents cascading failures
 * across microservices. The circuit breaker monitors remote service calls and
 * switches between CLOSED, OPEN, and HALF_OPEN states based on failure rates.
 * 
 * Requirements:
 * - Monitor failure rates across multiple service instances
 * - Implement three states: CLOSED, OPEN, HALF_OPEN
 * - Automatic recovery mechanism with exponential backoff
 * - Distributed state synchronization across nodes
 * - Health check integration
 * - Fallback mechanism support
 * 
 * Key Features:
 * - Failure threshold configuration
 * - Timeout configuration
 * - Health monitoring
 * - Metrics collection
 * - Distributed coordination
 * 
 * Company Tags: Netflix, Amazon, Google, Uber, Airbnb
 * Difficulty: Hard
 */
public class DesignDistributedCircuitBreaker {

    enum CircuitState {
        CLOSED, // Normal operation
        OPEN, // Circuit is open, failing fast
        HALF_OPEN // Testing if service is recovered
    }

    static class ServiceMetrics {
        private final AtomicLong totalRequests = new AtomicLong(0);
        private final AtomicLong failedRequests = new AtomicLong(0);
        private final AtomicLong successRequests = new AtomicLong(0);
        private final long windowStart;

        public ServiceMetrics() {
            this.windowStart = System.currentTimeMillis();
        }

        public double getFailureRate() {
            long total = totalRequests.get();
            return total == 0 ? 0.0 : (double) failedRequests.get() / total;
        }

        public void recordSuccess() {
            totalRequests.incrementAndGet();
            successRequests.incrementAndGet();
        }

        public void recordFailure() {
            totalRequests.incrementAndGet();
            failedRequests.incrementAndGet();
        }

        public long getTotalRequests() {
            return totalRequests.get();
        }

        public long getFailedRequests() {
            return failedRequests.get();
        }

        public long getSuccessRequests() {
            return successRequests.get();
        }
    }

    static class CircuitBreakerConfig {
        final double failureThreshold;
        final int minimumRequests;
        final long timeoutMs;
        final long healthCheckIntervalMs;
        final int maxTestRequests;

        public CircuitBreakerConfig(double failureThreshold, int minimumRequests,
                long timeoutMs, long healthCheckIntervalMs, int maxTestRequests) {
            this.failureThreshold = failureThreshold;
            this.minimumRequests = minimumRequests;
            this.timeoutMs = timeoutMs;
            this.healthCheckIntervalMs = healthCheckIntervalMs;
            this.maxTestRequests = maxTestRequests;
        }
    }

    static class ServiceCircuitBreaker {
        private final String serviceId;
        private final CircuitBreakerConfig config;
        private volatile CircuitState state;
        private volatile ServiceMetrics currentMetrics;
        private volatile long lastStateChangeTime;
        private final AtomicInteger testRequestCount = new AtomicInteger(0);

        public ServiceCircuitBreaker(String serviceId, CircuitBreakerConfig config) {
            this.serviceId = serviceId;
            this.config = config;
            this.state = CircuitState.CLOSED;
            this.currentMetrics = new ServiceMetrics();
            this.lastStateChangeTime = System.currentTimeMillis();
        }

        public synchronized boolean canExecute() {
            switch (state) {
                case CLOSED:
                    return true;
                case OPEN:
                    if (System.currentTimeMillis() - lastStateChangeTime >= config.timeoutMs) {
                        transitionToHalfOpen();
                        return true;
                    }
                    return false;
                case HALF_OPEN:
                    return testRequestCount.get() < config.maxTestRequests;
                default:
                    return false;
            }
        }

        public synchronized void recordSuccess() {
            currentMetrics.recordSuccess();

            if (state == CircuitState.HALF_OPEN) {
                if (testRequestCount.incrementAndGet() >= config.maxTestRequests) {
                    transitionToClosed();
                }
            }
        }

        public synchronized void recordFailure() {
            currentMetrics.recordFailure();

            if (state == CircuitState.HALF_OPEN) {
                transitionToOpen();
            } else if (state == CircuitState.CLOSED) {
                checkIfShouldOpen();
            }
        }

        private void checkIfShouldOpen() {
            if (currentMetrics.getTotalRequests() >= config.minimumRequests &&
                    currentMetrics.getFailureRate() >= config.failureThreshold) {
                transitionToOpen();
            }
        }

        private void transitionToOpen() {
            state = CircuitState.OPEN;
            lastStateChangeTime = System.currentTimeMillis();
            resetMetrics();
        }

        private void transitionToHalfOpen() {
            state = CircuitState.HALF_OPEN;
            lastStateChangeTime = System.currentTimeMillis();
            testRequestCount.set(0);
        }

        private void transitionToClosed() {
            state = CircuitState.CLOSED;
            lastStateChangeTime = System.currentTimeMillis();
            resetMetrics();
        }

        private void resetMetrics() {
            currentMetrics = new ServiceMetrics();
        }

        public CircuitState getState() {
            return state;
        }

        public ServiceMetrics getMetrics() {
            return currentMetrics;
        }

        public String getServiceId() {
            return serviceId;
        }
    }

    // Main Circuit Breaker Manager
    private final Map<String, ServiceCircuitBreaker> circuitBreakers;
    private final Map<String, CircuitBreakerConfig> serviceConfigs;
    private final ScheduledExecutorService scheduler;
    private final Map<String, Long> lastHealthCheckTime;

    public DesignDistributedCircuitBreaker() {
        this.circuitBreakers = new ConcurrentHashMap<>();
        this.serviceConfigs = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.lastHealthCheckTime = new ConcurrentHashMap<>();

        // Start health check scheduler
        startHealthCheckScheduler();
    }

    public void registerService(String serviceId, CircuitBreakerConfig config) {
        serviceConfigs.put(serviceId, config);
        circuitBreakers.put(serviceId, new ServiceCircuitBreaker(serviceId, config));
        lastHealthCheckTime.put(serviceId, System.currentTimeMillis());
    }

    public boolean executeWithCircuitBreaker(String serviceId, Callable<Boolean> serviceCall,
            Callable<Boolean> fallback) {
        ServiceCircuitBreaker circuitBreaker = circuitBreakers.get(serviceId);
        if (circuitBreaker == null) {
            throw new IllegalArgumentException("Service not registered: " + serviceId);
        }

        if (!circuitBreaker.canExecute()) {
            // Circuit is open, execute fallback if available
            if (fallback != null) {
                try {
                    return fallback.call();
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }

        try {
            boolean result = serviceCall.call();
            if (result) {
                circuitBreaker.recordSuccess();
            } else {
                circuitBreaker.recordFailure();
            }
            return result;
        } catch (Exception e) {
            circuitBreaker.recordFailure();
            if (fallback != null) {
                try {
                    return fallback.call();
                } catch (Exception fallbackException) {
                    return false;
                }
            }
            return false;
        }
    }

    public Map<String, Object> getServiceStatus(String serviceId) {
        ServiceCircuitBreaker circuitBreaker = circuitBreakers.get(serviceId);
        if (circuitBreaker == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> status = new HashMap<>();
        status.put("serviceId", serviceId);
        status.put("state", circuitBreaker.getState().name());
        status.put("totalRequests", circuitBreaker.getMetrics().getTotalRequests());
        status.put("failedRequests", circuitBreaker.getMetrics().getFailedRequests());
        status.put("successRequests", circuitBreaker.getMetrics().getSuccessRequests());
        status.put("failureRate", circuitBreaker.getMetrics().getFailureRate());

        return status;
    }

    public Map<String, Map<String, Object>> getAllServicesStatus() {
        Map<String, Map<String, Object>> allStatus = new HashMap<>();
        for (String serviceId : circuitBreakers.keySet()) {
            allStatus.put(serviceId, getServiceStatus(serviceId));
        }
        return allStatus;
    }

    private void startHealthCheckScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<String, ServiceCircuitBreaker> entry : circuitBreakers.entrySet()) {
                String serviceId = entry.getKey();
                ServiceCircuitBreaker circuitBreaker = entry.getValue();

                if (circuitBreaker.getState() == CircuitState.OPEN) {
                    // Perform health check
                    performHealthCheck(serviceId);
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void performHealthCheck(String serviceId) {
        // Simulate health check - in real implementation, this would ping the actual
        // service
        boolean isHealthy = Math.random() > 0.3; // 70% chance of being healthy

        if (isHealthy) {
            ServiceCircuitBreaker circuitBreaker = circuitBreakers.get(serviceId);
            if (circuitBreaker.getState() == CircuitState.OPEN) {
                // Don't automatically transition, wait for timeout
                lastHealthCheckTime.put(serviceId, System.currentTimeMillis());
            }
        }
    }

    public void updateServiceConfig(String serviceId, CircuitBreakerConfig newConfig) {
        serviceConfigs.put(serviceId, newConfig);
        // Create new circuit breaker with updated config
        circuitBreakers.put(serviceId, new ServiceCircuitBreaker(serviceId, newConfig));
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedCircuitBreaker circuitBreaker = new DesignDistributedCircuitBreaker();

        // Configure services
        CircuitBreakerConfig config = new CircuitBreakerConfig(0.5, 5, 10000, 5000, 3);
        circuitBreaker.registerService("user-service", config);
        circuitBreaker.registerService("payment-service",
                new CircuitBreakerConfig(0.3, 3, 15000, 3000, 2));

        // Simulate service calls
        System.out.println("=== Testing Circuit Breaker ===");

        // Test user-service with failures
        for (int i = 0; i < 10; i++) {
            final int callId = i;
            boolean result = circuitBreaker.executeWithCircuitBreaker("user-service",
                    () -> {
                        // Simulate 60% failure rate
                        boolean success = Math.random() > 0.6;
                        System.out.println("User service call " + callId + ": " +
                                (success ? "SUCCESS" : "FAILURE"));
                        return success;
                    },
                    () -> {
                        System.out.println("Fallback executed for user service call " + callId);
                        return true;
                    });

            System.out.println("Call result: " + result);
            System.out.println("Service status: " + circuitBreaker.getServiceStatus("user-service"));
            System.out.println();

            Thread.sleep(500);
        }

        System.out.println("=== Final Status ===");
        System.out.println(circuitBreaker.getAllServicesStatus());

        // Wait for potential recovery
        Thread.sleep(12000);

        System.out.println("=== After Recovery Period ===");
        // Try a few more calls
        for (int i = 0; i < 3; i++) {
            final int callId = i + 10;
            boolean result = circuitBreaker.executeWithCircuitBreaker("user-service",
                    () -> {
                        System.out.println("Recovery test call " + callId + ": SUCCESS");
                        return true; // Simulate recovery
                    }, null);
            System.out.println("Recovery call result: " + result);
            System.out.println("Service status: " + circuitBreaker.getServiceStatus("user-service"));
            Thread.sleep(1000);
        }

        circuitBreaker.shutdown();
    }
}
