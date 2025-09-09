package design.hard;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Design Load Balancer
 *
 * Description: Design a load balancer that supports:
 * - Multiple load balancing algorithms
 * - Server health checking
 * - Dynamic server addition/removal
 * - Request routing with session affinity
 * 
 * Constraints:
 * - Support up to 1000 servers
 * - Handle high request rates
 * - Implement common algorithms: Round Robin, Weighted, Least Connections
 *
 * Follow-up:
 * - How to handle server failures?
 * - Distributed load balancing?
 * 
 * Time Complexity: O(1) for most algorithms, O(log n) for weighted
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Amazon, Facebook
 */
public class DesignLoadBalancer {

    enum LoadBalancingAlgorithm {
        ROUND_ROBIN, WEIGHTED_ROUND_ROBIN, LEAST_CONNECTIONS,
        RANDOM, CONSISTENT_HASH, IP_HASH
    }

    enum ServerStatus {
        HEALTHY, UNHEALTHY, MAINTENANCE
    }

    class Server {
        String serverId;
        String ipAddress;
        int port;
        int weight;
        ServerStatus status;
        AtomicInteger activeConnections;
        long lastHealthCheck;
        double responseTime; // Average response time in ms

        Server(String serverId, String ipAddress, int port, int weight) {
            this.serverId = serverId;
            this.ipAddress = ipAddress;
            this.port = port;
            this.weight = weight;
            this.status = ServerStatus.HEALTHY;
            this.activeConnections = new AtomicInteger(0);
            this.lastHealthCheck = System.currentTimeMillis();
            this.responseTime = 0.0;
        }

        @Override
        public String toString() {
            return serverId + "(" + ipAddress + ":" + port + ")";
        }
    }

    interface LoadBalancingStrategy {
        Server selectServer(String clientId, List<Server> healthyServers);

        void onRequestComplete(Server server, double responseTime);
    }

    // Round Robin Strategy
    class RoundRobinStrategy implements LoadBalancingStrategy {
        private AtomicInteger currentIndex = new AtomicInteger(0);

        @Override
        public Server selectServer(String clientId, List<Server> healthyServers) {
            if (healthyServers.isEmpty())
                return null;

            int index = currentIndex.getAndIncrement() % healthyServers.size();
            return healthyServers.get(index);
        }

        @Override
        public void onRequestComplete(Server server, double responseTime) {
            // No special handling needed for round robin
        }
    }

    // Weighted Round Robin Strategy
    class WeightedRoundRobinStrategy implements LoadBalancingStrategy {
        private Map<String, Integer> serverWeights = new HashMap<>();
        private Map<String, Integer> currentWeights = new HashMap<>();

        @Override
        public Server selectServer(String clientId, List<Server> healthyServers) {
            if (healthyServers.isEmpty())
                return null;

            Server selected = null;
            int totalWeight = 0;

            for (Server server : healthyServers) {
                serverWeights.putIfAbsent(server.serverId, server.weight);
                currentWeights.putIfAbsent(server.serverId, 0);

                totalWeight += server.weight;
                int currentWeight = currentWeights.get(server.serverId) + server.weight;
                currentWeights.put(server.serverId, currentWeight);

                if (selected == null || currentWeight > currentWeights.get(selected.serverId)) {
                    selected = server;
                }
            }

            if (selected != null) {
                currentWeights.put(selected.serverId,
                        currentWeights.get(selected.serverId) - totalWeight);
            }

            return selected;
        }

        @Override
        public void onRequestComplete(Server server, double responseTime) {
            // Could adjust weights based on performance
        }
    }

    // Least Connections Strategy
    class LeastConnectionsStrategy implements LoadBalancingStrategy {
        @Override
        public Server selectServer(String clientId, List<Server> healthyServers) {
            if (healthyServers.isEmpty())
                return null;

            return healthyServers.stream()
                    .min(Comparator.comparingInt(s -> s.activeConnections.get()))
                    .orElse(null);
        }

        @Override
        public void onRequestComplete(Server server, double responseTime) {
            server.activeConnections.decrementAndGet();

            // Update average response time
            server.responseTime = (server.responseTime * 0.9) + (responseTime * 0.1);
        }
    }

    // Consistent Hash Strategy
    class ConsistentHashStrategy implements LoadBalancingStrategy {
        private TreeMap<Integer, Server> ring = new TreeMap<>();
        private final int virtualNodes = 150;

        public void updateRing(List<Server> servers) {
            ring.clear();
            for (Server server : servers) {
                for (int i = 0; i < virtualNodes; i++) {
                    String virtualNodeName = server.serverId + ":" + i;
                    int hash = virtualNodeName.hashCode();
                    ring.put(hash, server);
                }
            }
        }

        @Override
        public Server selectServer(String clientId, List<Server> healthyServers) {
            if (healthyServers.isEmpty())
                return null;

            if (ring.isEmpty()) {
                updateRing(healthyServers);
            }

            int hash = clientId.hashCode();
            Map.Entry<Integer, Server> entry = ring.ceilingEntry(hash);
            if (entry == null) {
                entry = ring.firstEntry();
            }

            return entry.getValue();
        }

        @Override
        public void onRequestComplete(Server server, double responseTime) {
            // No special handling needed
        }
    }

    private List<Server> servers;
    private LoadBalancingStrategy strategy;
    private Map<String, String> sessionAffinity; // sessionId -> serverId

    public DesignLoadBalancer(LoadBalancingAlgorithm algorithm) {
        servers = new ArrayList<>();
        sessionAffinity = new HashMap<>();
        setLoadBalancingAlgorithm(algorithm);
    }

    public void setLoadBalancingAlgorithm(LoadBalancingAlgorithm algorithm) {
        switch (algorithm) {
            case ROUND_ROBIN:
                strategy = new RoundRobinStrategy();
                break;
            case WEIGHTED_ROUND_ROBIN:
                strategy = new WeightedRoundRobinStrategy();
                break;
            case LEAST_CONNECTIONS:
                strategy = new LeastConnectionsStrategy();
                break;
            case CONSISTENT_HASH:
                strategy = new ConsistentHashStrategy();
                break;
            default:
                strategy = new RoundRobinStrategy();
        }
    }

    public void addServer(String serverId, String ipAddress, int port, int weight) {
        Server server = new Server(serverId, ipAddress, port, weight);
        servers.add(server);

        // Update consistent hash ring if using that strategy
        if (strategy instanceof ConsistentHashStrategy) {
            ((ConsistentHashStrategy) strategy).updateRing(getHealthyServers());
        }
    }

    public boolean removeServer(String serverId) {
        boolean removed = servers.removeIf(s -> s.serverId.equals(serverId));

        if (removed && strategy instanceof ConsistentHashStrategy) {
            ((ConsistentHashStrategy) strategy).updateRing(getHealthyServers());
        }

        return removed;
    }

    public void setServerStatus(String serverId, ServerStatus status) {
        servers.stream()
                .filter(s -> s.serverId.equals(serverId))
                .findFirst()
                .ifPresent(s -> {
                    s.status = status;
                    if (strategy instanceof ConsistentHashStrategy) {
                        ((ConsistentHashStrategy) strategy).updateRing(getHealthyServers());
                    }
                });
    }

    public Server routeRequest(String clientId, String sessionId) {
        // Check session affinity first
        if (sessionId != null && sessionAffinity.containsKey(sessionId)) {
            String serverId = sessionAffinity.get(sessionId);
            Server server = servers.stream()
                    .filter(s -> s.serverId.equals(serverId) && s.status == ServerStatus.HEALTHY)
                    .findFirst()
                    .orElse(null);

            if (server != null) {
                server.activeConnections.incrementAndGet();
                return server;
            } else {
                // Remove invalid session affinity
                sessionAffinity.remove(sessionId);
            }
        }

        // Use load balancing strategy
        List<Server> healthyServers = getHealthyServers();
        Server selectedServer = strategy.selectServer(clientId, healthyServers);

        if (selectedServer != null) {
            selectedServer.activeConnections.incrementAndGet();

            // Create session affinity if sessionId provided
            if (sessionId != null) {
                sessionAffinity.put(sessionId, selectedServer.serverId);
            }
        }

        return selectedServer;
    }

    public void completeRequest(Server server, double responseTime) {
        strategy.onRequestComplete(server, responseTime);
    }

    private List<Server> getHealthyServers() {
        return servers.stream()
                .filter(s -> s.status == ServerStatus.HEALTHY)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void performHealthCheck() {
        long now = System.currentTimeMillis();

        for (Server server : servers) {
            // Simulate health check
            boolean healthy = simulateHealthCheck(server);

            if (healthy && server.status == ServerStatus.UNHEALTHY) {
                server.status = ServerStatus.HEALTHY;
                System.out.println("Server " + server.serverId + " is back online");
            } else if (!healthy && server.status == ServerStatus.HEALTHY) {
                server.status = ServerStatus.UNHEALTHY;
                System.out.println("Server " + server.serverId + " is unhealthy");
            }

            server.lastHealthCheck = now;
        }

        // Update consistent hash ring if needed
        if (strategy instanceof ConsistentHashStrategy) {
            ((ConsistentHashStrategy) strategy).updateRing(getHealthyServers());
        }
    }

    private boolean simulateHealthCheck(Server server) {
        // Simulate health check - in real implementation, this would be an HTTP/TCP
        // check
        return Math.random() > 0.1; // 90% chance of being healthy
    }

    public Map<String, Object> getLoadBalancerStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Server> healthy = getHealthyServers();
        List<Server> unhealthy = servers.stream()
                .filter(s -> s.status != ServerStatus.HEALTHY)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        stats.put("totalServers", servers.size());
        stats.put("healthyServers", healthy.size());
        stats.put("unhealthyServers", unhealthy.size());
        stats.put("activeSessions", sessionAffinity.size());

        int totalConnections = servers.stream()
                .mapToInt(s -> s.activeConnections.get())
                .sum();
        stats.put("totalActiveConnections", totalConnections);

        return stats;
    }

    public List<Map<String, Object>> getServerStats() {
        List<Map<String, Object>> serverStats = new ArrayList<>();

        for (Server server : servers) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("serverId", server.serverId);
            stats.put("address", server.ipAddress + ":" + server.port);
            stats.put("status", server.status);
            stats.put("weight", server.weight);
            stats.put("activeConnections", server.activeConnections.get());
            stats.put("averageResponseTime", server.responseTime);
            stats.put("lastHealthCheck", server.lastHealthCheck);

            serverStats.add(stats);
        }

        return serverStats;
    }

    public static void main(String[] args) throws InterruptedException {
        // Test Load Balancer
        DesignLoadBalancer lb = new DesignLoadBalancer(LoadBalancingAlgorithm.ROUND_ROBIN);

        // Add servers
        lb.addServer("server1", "192.168.1.1", 8080, 1);
        lb.addServer("server2", "192.168.1.2", 8080, 2);
        lb.addServer("server3", "192.168.1.3", 8080, 1);

        System.out.println("Testing Round Robin:");
        for (int i = 0; i < 6; i++) {
            Server server = lb.routeRequest("client" + i, null);
            System.out.println("Request " + i + " -> " + server);
            if (server != null) {
                lb.completeRequest(server, 100 + Math.random() * 50);
            }
        }

        // Test with session affinity
        System.out.println("\nTesting Session Affinity:");
        Server server1 = lb.routeRequest("client1", "session123");
        Server server2 = lb.routeRequest("client1", "session123");
        System.out.println("First request with session -> " + server1);
        System.out.println("Second request with session -> " + server2);
        System.out.println("Same server: " + (server1 == server2));

        // Test weighted round robin
        System.out.println("\nTesting Weighted Round Robin:");
        lb.setLoadBalancingAlgorithm(LoadBalancingAlgorithm.WEIGHTED_ROUND_ROBIN);

        Map<String, Integer> serverCounts = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            Server server = lb.routeRequest("client" + i, null);
            if (server != null) {
                serverCounts.put(server.serverId, serverCounts.getOrDefault(server.serverId, 0) + 1);
                lb.completeRequest(server, 100);
            }
        }
        System.out.println("Request distribution: " + serverCounts);

        // Show stats
        System.out.println("\nLoad Balancer Stats: " + lb.getLoadBalancerStats());

        // Perform health check
        System.out.println("\nPerforming health check...");
        lb.performHealthCheck();

        System.out.println("Updated Stats: " + lb.getLoadBalancerStats());
    }
}
