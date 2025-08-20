package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Design Distributed Service Discovery
 * 
 * Description:
 * Design a distributed service discovery system that allows services to
 * register
 * themselves and discover other services in a distributed environment. The
 * system
 * should handle service health monitoring, load balancing, and fault tolerance.
 * 
 * Requirements:
 * - Service registration and deregistration
 * - Health checking and monitoring
 * - Service discovery with load balancing
 * - Distributed consensus for consistency
 * - Failure detection and recovery
 * - Configuration management
 * - Multi-datacenter support
 * 
 * Key Features:
 * - Dynamic service registration
 * - Health monitoring
 * - Load balancing strategies
 * - Service metadata management
 * - Event-driven updates
 * - Distributed caching
 * 
 * Company Tags: Consul, Eureka, Zookeeper, etcd, Amazon
 * Difficulty: Hard
 */
public class DesignDistributedServiceDiscovery {

    enum ServiceStatus {
        HEALTHY, UNHEALTHY, UNKNOWN, MAINTENANCE
    }

    enum LoadBalancingStrategy {
        ROUND_ROBIN, LEAST_CONNECTIONS, RANDOM, WEIGHTED_ROUND_ROBIN
    }

    static class ServiceInstance {
        final String instanceId;
        final String serviceId;
        final String host;
        final int port;
        final Map<String, String> metadata;
        final long registrationTime;
        volatile ServiceStatus status;
        volatile long lastHeartbeat;
        volatile int currentConnections;
        final int weight;

        public ServiceInstance(String serviceId, String host, int port, int weight) {
            this.instanceId = UUID.randomUUID().toString();
            this.serviceId = serviceId;
            this.host = host;
            this.port = port;
            this.weight = weight;
            this.metadata = new ConcurrentHashMap<>();
            this.registrationTime = System.currentTimeMillis();
            this.status = ServiceStatus.UNKNOWN;
            this.lastHeartbeat = System.currentTimeMillis();
            this.currentConnections = 0;
        }

        public String getAddress() {
            return host + ":" + port;
        }

        public void addMetadata(String key, String value) {
            metadata.put(key, value);
        }

        public void updateHeartbeat() {
            lastHeartbeat = System.currentTimeMillis();
            if (status == ServiceStatus.UNKNOWN) {
                status = ServiceStatus.HEALTHY;
            }
        }

        public void incrementConnections() {
            currentConnections++;
        }

        public void decrementConnections() {
            if (currentConnections > 0) {
                currentConnections--;
            }
        }

        @Override
        public String toString() {
            return String.format("%s@%s [%s] (connections: %d, weight: %d)",
                    serviceId, getAddress(), status, currentConnections, weight);
        }
    }

    static class ServiceRegistry {
        private final Map<String, Set<ServiceInstance>> serviceInstances;
        private final Map<String, ServiceInstance> instanceById;
        private final Map<String, AtomicInteger> roundRobinCounters;

        public ServiceRegistry() {
            this.serviceInstances = new ConcurrentHashMap<>();
            this.instanceById = new ConcurrentHashMap<>();
            this.roundRobinCounters = new ConcurrentHashMap<>();
        }

        public synchronized String registerService(String serviceId, String host, int port, int weight) {
            ServiceInstance instance = new ServiceInstance(serviceId, host, port, weight);

            serviceInstances.computeIfAbsent(serviceId, k -> ConcurrentHashMap.newKeySet()).add(instance);
            instanceById.put(instance.instanceId, instance);

            return instance.instanceId;
        }

        public synchronized boolean deregisterService(String instanceId) {
            ServiceInstance instance = instanceById.remove(instanceId);
            if (instance != null) {
                Set<ServiceInstance> instances = serviceInstances.get(instance.serviceId);
                if (instances != null) {
                    instances.remove(instance);
                    if (instances.isEmpty()) {
                        serviceInstances.remove(instance.serviceId);
                    }
                }
                return true;
            }
            return false;
        }

        public List<ServiceInstance> discoverService(String serviceId) {
            Set<ServiceInstance> instances = serviceInstances.get(serviceId);
            if (instances == null) {
                return new ArrayList<>();
            }

            return instances.stream()
                    .filter(instance -> instance.status == ServiceStatus.HEALTHY)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        public ServiceInstance selectInstance(String serviceId, LoadBalancingStrategy strategy) {
            List<ServiceInstance> healthyInstances = discoverService(serviceId);
            if (healthyInstances.isEmpty()) {
                return null;
            }

            switch (strategy) {
                case ROUND_ROBIN:
                    return selectRoundRobin(serviceId, healthyInstances);
                case LEAST_CONNECTIONS:
                    return selectLeastConnections(healthyInstances);
                case WEIGHTED_ROUND_ROBIN:
                    return selectWeightedRoundRobin(serviceId, healthyInstances);
                case RANDOM:
                default:
                    return healthyInstances.get(new Random().nextInt(healthyInstances.size()));
            }
        }

        private ServiceInstance selectRoundRobin(String serviceId, List<ServiceInstance> instances) {
            AtomicInteger counter = roundRobinCounters.computeIfAbsent(serviceId, k -> new AtomicInteger(0));
            int index = counter.getAndIncrement() % instances.size();
            return instances.get(index);
        }

        private ServiceInstance selectLeastConnections(List<ServiceInstance> instances) {
            return instances.stream()
                    .min(Comparator.comparingInt(instance -> instance.currentConnections))
                    .orElse(null);
        }

        private ServiceInstance selectWeightedRoundRobin(String serviceId, List<ServiceInstance> instances) {
            // Simple weighted selection - in production, this would be more sophisticated
            int totalWeight = instances.stream().mapToInt(instance -> instance.weight).sum();
            if (totalWeight == 0) {
                return selectRoundRobin(serviceId, instances);
            }

            int randomWeight = new Random().nextInt(totalWeight);
            int currentWeight = 0;

            for (ServiceInstance instance : instances) {
                currentWeight += instance.weight;
                if (randomWeight < currentWeight) {
                    return instance;
                }
            }

            return instances.get(0); // Fallback
        }

        public Set<String> getAllServices() {
            return new HashSet<>(serviceInstances.keySet());
        }

        public ServiceInstance getInstance(String instanceId) {
            return instanceById.get(instanceId);
        }

        public Map<String, List<ServiceInstance>> getAllServiceInstances() {
            Map<String, List<ServiceInstance>> result = new HashMap<>();
            for (Map.Entry<String, Set<ServiceInstance>> entry : serviceInstances.entrySet()) {
                result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            return result;
        }
    }

    static class HealthChecker {
        private final ServiceRegistry registry;
        private final ScheduledExecutorService scheduler;
        private final long healthCheckIntervalMs;
        private final long heartbeatTimeoutMs;

        public HealthChecker(ServiceRegistry registry, long healthCheckIntervalMs, long heartbeatTimeoutMs) {
            this.registry = registry;
            this.healthCheckIntervalMs = healthCheckIntervalMs;
            this.heartbeatTimeoutMs = heartbeatTimeoutMs;
            this.scheduler = Executors.newScheduledThreadPool(2);

            startHealthChecking();
        }

        private void startHealthChecking() {
            scheduler.scheduleAtFixedRate(this::performHealthChecks,
                    healthCheckIntervalMs, healthCheckIntervalMs, TimeUnit.MILLISECONDS);
        }

        private void performHealthChecks() {
            long currentTime = System.currentTimeMillis();

            for (Map.Entry<String, List<ServiceInstance>> entry : registry.getAllServiceInstances().entrySet()) {
                for (ServiceInstance instance : entry.getValue()) {
                    if (currentTime - instance.lastHeartbeat > heartbeatTimeoutMs) {
                        if (instance.status == ServiceStatus.HEALTHY) {
                            instance.status = ServiceStatus.UNHEALTHY;
                            System.out.println("⚠️  Instance became unhealthy: " + instance);
                        }
                    }

                    // Simulate health check (in real implementation, this would ping the service)
                    if (instance.status == ServiceStatus.UNHEALTHY && shouldRecover(instance)) {
                        instance.status = ServiceStatus.HEALTHY;
                        instance.updateHeartbeat();
                        System.out.println("✅ Instance recovered: " + instance);
                    }
                }
            }
        }

        private boolean shouldRecover(ServiceInstance instance) {
            // Simulate 30% chance of recovery
            return Math.random() < 0.3;
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
    }

    // Main Service Discovery System
    private final ServiceRegistry registry;
    private final HealthChecker healthChecker;
    private final Map<String, Object> configuration;
    private final List<ServiceDiscoveryListener> listeners;

    public interface ServiceDiscoveryListener {
        void onServiceRegistered(String serviceId, ServiceInstance instance);

        void onServiceDeregistered(String serviceId, String instanceId);

        void onServiceHealthChanged(ServiceInstance instance, ServiceStatus oldStatus, ServiceStatus newStatus);
    }

    public DesignDistributedServiceDiscovery() {
        this.registry = new ServiceRegistry();
        this.healthChecker = new HealthChecker(registry, 5000, 15000); // 5s check, 15s timeout
        this.configuration = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();

        setupDefaultConfiguration();
    }

    private void setupDefaultConfiguration() {
        configuration.put("default.loadBalancing", LoadBalancingStrategy.ROUND_ROBIN);
        configuration.put("default.healthCheck.interval", 5000L);
        configuration.put("default.heartbeat.timeout", 15000L);
        configuration.put("default.instance.weight", 1);
    }

    public String registerService(String serviceId, String host, int port) {
        return registerService(serviceId, host, port, (Integer) configuration.get("default.instance.weight"));
    }

    public String registerService(String serviceId, String host, int port, int weight) {
        String instanceId = registry.registerService(serviceId, host, port, weight);
        ServiceInstance instance = registry.getInstance(instanceId);

        // Notify listeners
        for (ServiceDiscoveryListener listener : listeners) {
            listener.onServiceRegistered(serviceId, instance);
        }

        return instanceId;
    }

    public boolean deregisterService(String instanceId) {
        ServiceInstance instance = registry.getInstance(instanceId);
        if (instance != null) {
            String serviceId = instance.serviceId;
            boolean success = registry.deregisterService(instanceId);

            if (success) {
                // Notify listeners
                for (ServiceDiscoveryListener listener : listeners) {
                    listener.onServiceDeregistered(serviceId, instanceId);
                }
            }

            return success;
        }
        return false;
    }

    public List<ServiceInstance> discoverService(String serviceId) {
        return registry.discoverService(serviceId);
    }

    public ServiceInstance selectServiceInstance(String serviceId) {
        LoadBalancingStrategy strategy = (LoadBalancingStrategy) configuration.get("default.loadBalancing");
        return registry.selectInstance(serviceId, strategy);
    }

    public ServiceInstance selectServiceInstance(String serviceId, LoadBalancingStrategy strategy) {
        return registry.selectInstance(serviceId, strategy);
    }

    public void heartbeat(String instanceId) {
        ServiceInstance instance = registry.getInstance(instanceId);
        if (instance != null) {
            ServiceStatus oldStatus = instance.status;
            instance.updateHeartbeat();

            if (oldStatus != instance.status) {
                // Notify listeners of status change
                for (ServiceDiscoveryListener listener : listeners) {
                    listener.onServiceHealthChanged(instance, oldStatus, instance.status);
                }
            }
        }
    }

    public void addServiceInstance(String instanceId, String key, String value) {
        ServiceInstance instance = registry.getInstance(instanceId);
        if (instance != null) {
            instance.addMetadata(key, value);
        }
    }

    public void addListener(ServiceDiscoveryListener listener) {
        listeners.add(listener);
    }

    public Set<String> getAllServices() {
        return registry.getAllServices();
    }

    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalServices", registry.getAllServices().size());

        Map<String, Integer> serviceCounts = new HashMap<>();
        Map<String, Integer> healthyServiceCounts = new HashMap<>();

        for (Map.Entry<String, List<ServiceInstance>> entry : registry.getAllServiceInstances().entrySet()) {
            String serviceId = entry.getKey();
            List<ServiceInstance> instances = entry.getValue();

            serviceCounts.put(serviceId, instances.size());

            long healthyCount = instances.stream()
                    .filter(instance -> instance.status == ServiceStatus.HEALTHY)
                    .count();
            healthyServiceCounts.put(serviceId, (int) healthyCount);
        }

        status.put("serviceCounts", serviceCounts);
        status.put("healthyServiceCounts", healthyServiceCounts);
        status.put("configuration", new HashMap<>(configuration));

        return status;
    }

    public void shutdown() {
        healthChecker.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedServiceDiscovery discovery = new DesignDistributedServiceDiscovery();

        // Add listener for events
        discovery.addListener(new ServiceDiscoveryListener() {
            @Override
            public void onServiceRegistered(String serviceId, ServiceInstance instance) {
                System.out.println("🔌 Service registered: " + serviceId + " -> " + instance.getAddress());
            }

            @Override
            public void onServiceDeregistered(String serviceId, String instanceId) {
                System.out.println("🔌 Service deregistered: " + serviceId + " (" + instanceId + ")");
            }

            @Override
            public void onServiceHealthChanged(ServiceInstance instance, ServiceStatus oldStatus,
                    ServiceStatus newStatus) {
                System.out
                        .println("💓 Health changed: " + instance.getAddress() + " " + oldStatus + " -> " + newStatus);
            }
        });

        System.out.println("=== Registering Services ===");

        // Register multiple instances of different services
        String userService1 = discovery.registerService("user-service", "192.168.1.10", 8080, 2);
        String userService2 = discovery.registerService("user-service", "192.168.1.11", 8080, 1);
        String userService3 = discovery.registerService("user-service", "192.168.1.12", 8080, 3);

        String paymentService1 = discovery.registerService("payment-service", "192.168.1.20", 9090);
        String paymentService2 = discovery.registerService("payment-service", "192.168.1.21", 9090);

        String orderService1 = discovery.registerService("order-service", "192.168.1.30", 7070);

        // Add metadata
        discovery.addServiceInstance(userService1, "region", "us-west");
        discovery.addServiceInstance(userService1, "version", "1.2.0");

        Thread.sleep(1000);

        System.out.println("\n=== Service Discovery ===");

        // Discover services
        List<ServiceInstance> userServices = discovery.discoverService("user-service");
        System.out.println("User service instances: " + userServices.size());
        for (ServiceInstance instance : userServices) {
            System.out.println("  " + instance);
        }

        System.out.println("\n=== Load Balancing Tests ===");

        // Test different load balancing strategies
        System.out.println("Round Robin:");
        for (int i = 0; i < 6; i++) {
            ServiceInstance selected = discovery.selectServiceInstance("user-service",
                    LoadBalancingStrategy.ROUND_ROBIN);
            if (selected != null) {
                selected.incrementConnections();
                System.out.println("  Selected: " + selected.getAddress());
            }
        }

        System.out.println("\nLeast Connections:");
        for (int i = 0; i < 4; i++) {
            ServiceInstance selected = discovery.selectServiceInstance("user-service",
                    LoadBalancingStrategy.LEAST_CONNECTIONS);
            if (selected != null) {
                selected.incrementConnections();
                System.out.println(
                        "  Selected: " + selected.getAddress() + " (connections: " + selected.currentConnections + ")");
            }
        }

        System.out.println("\nWeighted Round Robin:");
        for (int i = 0; i < 10; i++) {
            ServiceInstance selected = discovery.selectServiceInstance("user-service",
                    LoadBalancingStrategy.WEIGHTED_ROUND_ROBIN);
            if (selected != null) {
                System.out.println("  Selected: " + selected.getAddress() + " (weight: " + selected.weight + ")");
            }
        }

        // Simulate heartbeats
        System.out.println("\n=== Heartbeat Simulation ===");
        for (int i = 0; i < 3; i++) {
            discovery.heartbeat(userService1);
            discovery.heartbeat(paymentService1);
            Thread.sleep(2000);
        }

        System.out.println("\n=== System Status ===");
        Map<String, Object> status = discovery.getSystemStatus();
        status.forEach((key, value) -> System.out.println(key + ": " + value));

        // Wait for health checks to potentially mark services as unhealthy
        System.out.println("\n=== Waiting for Health Checks (20 seconds) ===");
        Thread.sleep(20000);

        System.out.println("\n=== Final System Status ===");
        status = discovery.getSystemStatus();
        status.forEach((key, value) -> System.out.println(key + ": " + value));

        // Deregister some services
        System.out.println("\n=== Deregistering Services ===");
        discovery.deregisterService(userService2);
        discovery.deregisterService(paymentService1);

        Thread.sleep(1000);

        System.out.println("\n=== Final Service List ===");
        for (String serviceId : discovery.getAllServices()) {
            List<ServiceInstance> instances = discovery.discoverService(serviceId);
            System.out.println(serviceId + ": " + instances.size() + " healthy instances");
            for (ServiceInstance instance : instances) {
                System.out.println("  " + instance);
            }
        }

        discovery.shutdown();
    }
}
