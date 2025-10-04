package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Container Orchestrator (Kubernetes-like)
 *
 * Description: Design a container orchestrator that supports:
 * - Pod scheduling and lifecycle management
 * - Resource allocation and constraints
 * - Service discovery and load balancing
 * - Auto-scaling and health checks
 * 
 * Constraints:
 * - Support thousands of containers
 * - Handle node failures gracefully
 * - Efficient resource utilization
 *
 * Follow-up:
 * - How to handle persistent storage?
 * - Multi-cluster management?
 * 
 * Time Complexity: O(log n) for scheduling, O(1) for most operations
 * Space Complexity: O(nodes * pods)
 * 
 * Company Tags: Kubernetes, Docker Swarm, Nomad
 */
public class DesignContainerOrchestrator {

    enum PodPhase {
        PENDING, RUNNING, SUCCEEDED, FAILED, UNKNOWN
    }

    enum NodeCondition {
        READY, NOT_READY, UNKNOWN
    }

    class ResourceRequirements {
        double cpuRequest;
        double memoryRequest;
        double cpuLimit;
        double memoryLimit;

        ResourceRequirements(double cpuRequest, double memoryRequest,
                double cpuLimit, double memoryLimit) {
            this.cpuRequest = cpuRequest;
            this.memoryRequest = memoryRequest;
            this.cpuLimit = cpuLimit;
            this.memoryLimit = memoryLimit;
        }
    }

    class Container {
        String name;
        String image;
        ResourceRequirements resources;
        Map<String, String> env;
        List<String> command;

        Container(String name, String image, ResourceRequirements resources) {
            this.name = name;
            this.image = image;
            this.resources = resources;
            this.env = new HashMap<>();
            this.command = new ArrayList<>();
        }
    }

    class Pod {
        String podId;
        String name;
        String namespace;
        List<Container> containers;
        PodPhase phase;
        String nodeId;
        Map<String, String> labels;
        Map<String, String> annotations;
        long createdTime;
        long startTime;
        int restartCount;

        Pod(String podId, String name, String namespace) {
            this.podId = podId;
            this.name = name;
            this.namespace = namespace;
            this.containers = new ArrayList<>();
            this.phase = PodPhase.PENDING;
            this.labels = new HashMap<>();
            this.annotations = new HashMap<>();
            this.createdTime = System.currentTimeMillis();
            this.restartCount = 0;
        }

        ResourceRequirements getTotalResourceRequirements() {
            double totalCpuRequest = containers.stream()
                    .mapToDouble(c -> c.resources.cpuRequest)
                    .sum();
            double totalMemoryRequest = containers.stream()
                    .mapToDouble(c -> c.resources.memoryRequest)
                    .sum();
            double totalCpuLimit = containers.stream()
                    .mapToDouble(c -> c.resources.cpuLimit)
                    .sum();
            double totalMemoryLimit = containers.stream()
                    .mapToDouble(c -> c.resources.memoryLimit)
                    .sum();

            return new ResourceRequirements(totalCpuRequest, totalMemoryRequest,
                    totalCpuLimit, totalMemoryLimit);
        }
    }

    class Node {
        String nodeId;
        String name;
        NodeCondition condition;
        double totalCpu;
        double totalMemory;
        double allocatedCpu;
        double allocatedMemory;
        Set<String> pods;
        Map<String, String> labels;
        long lastHeartbeat;

        Node(String nodeId, String name, double totalCpu, double totalMemory) {
            this.nodeId = nodeId;
            this.name = name;
            this.condition = NodeCondition.READY;
            this.totalCpu = totalCpu;
            this.totalMemory = totalMemory;
            this.allocatedCpu = 0.0;
            this.allocatedMemory = 0.0;
            this.pods = new HashSet<>();
            this.labels = new HashMap<>();
            this.lastHeartbeat = System.currentTimeMillis();
        }

        boolean canSchedulePod(Pod pod) {
            if (condition != NodeCondition.READY)
                return false;

            ResourceRequirements requirements = pod.getTotalResourceRequirements();

            return (allocatedCpu + requirements.cpuRequest <= totalCpu) &&
                    (allocatedMemory + requirements.memoryRequest <= totalMemory);
        }

        boolean schedulePod(Pod pod) {
            if (!canSchedulePod(pod))
                return false;

            ResourceRequirements requirements = pod.getTotalResourceRequirements();
            allocatedCpu += requirements.cpuRequest;
            allocatedMemory += requirements.memoryRequest;
            pods.add(pod.podId);

            return true;
        }

        void unschedulePod(Pod pod) {
            if (pods.remove(pod.podId)) {
                ResourceRequirements requirements = pod.getTotalResourceRequirements();
                allocatedCpu -= requirements.cpuRequest;
                allocatedMemory -= requirements.memoryRequest;
            }
        }

        double getCpuUtilization() {
            return totalCpu > 0 ? allocatedCpu / totalCpu : 0.0;
        }

        double getMemoryUtilization() {
            return totalMemory > 0 ? allocatedMemory / totalMemory : 0.0;
        }
    }

    class Scheduler {
        String schedulerName;

        Scheduler(String schedulerName) {
            this.schedulerName = schedulerName;
        }

        Node selectNode(Pod pod, List<Node> availableNodes) {
            // Filter nodes that can schedule the pod
            List<Node> candidateNodes = availableNodes.stream()
                    .filter(node -> node.canSchedulePod(pod))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            if (candidateNodes.isEmpty())
                return null;

            // Score nodes (lower score is better)
            return candidateNodes.stream()
                    .min(Comparator.comparingDouble(this::scoreNode))
                    .orElse(null);
        }

        private double scoreNode(Node node) {
            // Scoring based on resource utilization
            double cpuScore = node.getCpuUtilization();
            double memoryScore = node.getMemoryUtilization();

            // Prefer balanced utilization
            return Math.abs(cpuScore - memoryScore) + Math.max(cpuScore, memoryScore);
        }
    }

    class Service {
        String serviceId;
        String name;
        String namespace;
        Map<String, String> selector;
        List<String> targetPods;
        String clusterIP;
        int port;
        int targetPort;

        Service(String serviceId, String name, String namespace, int port, int targetPort) {
            this.serviceId = serviceId;
            this.name = name;
            this.namespace = namespace;
            this.selector = new HashMap<>();
            this.targetPods = new ArrayList<>();
            this.port = port;
            this.targetPort = targetPort;
            this.clusterIP = generateClusterIP();
        }

        private String generateClusterIP() {
            return "10.0." + (int) (Math.random() * 255) + "." + (int) (Math.random() * 255);
        }

        void updateTargetPods(List<Pod> allPods) {
            targetPods.clear();

            for (Pod pod : allPods) {
                if (pod.phase == PodPhase.RUNNING && matchesSelector(pod)) {
                    targetPods.add(pod.podId);
                }
            }
        }

        private boolean matchesSelector(Pod pod) {
            for (Map.Entry<String, String> entry : selector.entrySet()) {
                String podLabelValue = pod.labels.get(entry.getKey());
                if (!entry.getValue().equals(podLabelValue)) {
                    return false;
                }
            }
            return true;
        }
    }

    private Map<String, Pod> pods;
    private Map<String, Node> nodes;
    private Map<String, Service> services;
    private Scheduler scheduler;
    private ScheduledExecutorService executorService;

    public DesignContainerOrchestrator() {
        pods = new ConcurrentHashMap<>();
        nodes = new ConcurrentHashMap<>();
        services = new ConcurrentHashMap<>();
        scheduler = new Scheduler("default-scheduler");
        executorService = Executors.newScheduledThreadPool(5);

        startControllerLoops();
    }

    public void addNode(String nodeId, String name, double totalCpu, double totalMemory,
            Map<String, String> labels) {
        Node node = new Node(nodeId, name, totalCpu, totalMemory);
        if (labels != null) {
            node.labels.putAll(labels);
        }

        nodes.put(nodeId, node);
        System.out.println("Added node: " + name);
    }

    public String createPod(String name, String namespace, Map<String, String> labels) {
        String podId = UUID.randomUUID().toString();
        Pod pod = new Pod(podId, name, namespace);

        if (labels != null) {
            pod.labels.putAll(labels);
        }

        pods.put(podId, pod);
        System.out.println("Created pod: " + name);

        return podId;
    }

    public void addContainer(String podId, String containerName, String image,
            double cpuRequest, double memoryRequest,
            double cpuLimit, double memoryLimit) {
        Pod pod = pods.get(podId);
        if (pod != null) {
            ResourceRequirements resources = new ResourceRequirements(
                    cpuRequest, memoryRequest, cpuLimit, memoryLimit);
            Container container = new Container(containerName, image, resources);
            pod.containers.add(container);
        }
    }

    public String createService(String name, String namespace, int port, int targetPort,
            Map<String, String> selector) {
        String serviceId = UUID.randomUUID().toString();
        Service service = new Service(serviceId, name, namespace, port, targetPort);

        if (selector != null) {
            service.selector.putAll(selector);
        }

        services.put(serviceId, service);
        System.out.println("Created service: " + name);

        return serviceId;
    }

    private void startControllerLoops() {
        // Pod scheduling loop
        executorService.scheduleWithFixedDelay(this::schedulePods, 1, 2, TimeUnit.SECONDS);

        // Health check loop
        executorService.scheduleWithFixedDelay(this::performHealthChecks, 10, 10, TimeUnit.SECONDS);

        // Service endpoint update loop
        executorService.scheduleWithFixedDelay(this::updateServiceEndpoints, 5, 5, TimeUnit.SECONDS);
    }

    private void schedulePods() {
        List<Pod> pendingPods = pods.values().stream()
                .filter(pod -> pod.phase == PodPhase.PENDING)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        List<Node> availableNodes = nodes.values().stream()
                .filter(node -> node.condition == NodeCondition.READY)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        for (Pod pod : pendingPods) {
            Node selectedNode = scheduler.selectNode(pod, availableNodes);

            if (selectedNode != null) {
                if (selectedNode.schedulePod(pod)) {
                    pod.nodeId = selectedNode.nodeId;
                    pod.phase = PodPhase.RUNNING;
                    pod.startTime = System.currentTimeMillis();

                    System.out.println("Scheduled pod " + pod.name + " on node " + selectedNode.name);
                }
            }
        }
    }

    private void performHealthChecks() {
        long now = System.currentTimeMillis();

        // Check node health
        for (Node node : nodes.values()) {
            // Simulate heartbeat
            node.lastHeartbeat = now;

            // Simulate node failure (1% chance)
            if (Math.random() < 0.01) {
                node.condition = NodeCondition.NOT_READY;
                System.out.println("Node " + node.name + " became unhealthy");

                // Evict pods from unhealthy node
                evictPodsFromNode(node);
            } else if (node.condition == NodeCondition.NOT_READY && Math.random() < 0.5) {
                node.condition = NodeCondition.READY;
                System.out.println("Node " + node.name + " recovered");
            }
        }

        // Check pod health
        for (Pod pod : pods.values()) {
            if (pod.phase == PodPhase.RUNNING) {
                // Simulate pod failure (0.5% chance)
                if (Math.random() < 0.005) {
                    pod.phase = PodPhase.FAILED;
                    pod.restartCount++;

                    Node node = nodes.get(pod.nodeId);
                    if (node != null) {
                        node.unschedulePod(pod);
                    }

                    System.out.println("Pod " + pod.name + " failed");

                    // Restart pod
                    restartPod(pod);
                }
            }
        }
    }

    private void evictPodsFromNode(Node node) {
        List<String> podsToEvict = new ArrayList<>(node.pods);

        for (String podId : podsToEvict) {
            Pod pod = pods.get(podId);
            if (pod != null) {
                pod.phase = PodPhase.PENDING;
                pod.nodeId = null;
                node.unschedulePod(pod);
                System.out.println("Evicted pod " + pod.name + " from node " + node.name);
            }
        }
    }

    private void restartPod(Pod pod) {
        // Reset pod to pending state for rescheduling
        pod.phase = PodPhase.PENDING;
        pod.nodeId = null;
        System.out.println("Restarting pod " + pod.name);
    }

    private void updateServiceEndpoints() {
        for (Service service : services.values()) {
            service.updateTargetPods(new ArrayList<>(pods.values()));
        }
    }

    public Map<String, Object> getClusterStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalNodes = nodes.size();
        int readyNodes = (int) nodes.values().stream()
                .filter(node -> node.condition == NodeCondition.READY)
                .count();

        int totalPods = pods.size();
        int runningPods = (int) pods.values().stream()
                .filter(pod -> pod.phase == PodPhase.RUNNING)
                .count();

        double totalCpu = nodes.values().stream()
                .mapToDouble(node -> node.totalCpu)
                .sum();

        double allocatedCpu = nodes.values().stream()
                .mapToDouble(node -> node.allocatedCpu)
                .sum();

        stats.put("totalNodes", totalNodes);
        stats.put("readyNodes", readyNodes);
        stats.put("totalPods", totalPods);
        stats.put("runningPods", runningPods);
        stats.put("cpuUtilization", totalCpu > 0 ? allocatedCpu / totalCpu * 100 : 0);
        stats.put("totalServices", services.size());

        return stats;
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignContainerOrchestrator orchestrator = new DesignContainerOrchestrator();

        // Add nodes
        orchestrator.addNode("node1", "worker-1", 4.0, 8.0, Map.of("zone", "us-east-1a"));
        orchestrator.addNode("node2", "worker-2", 4.0, 8.0, Map.of("zone", "us-east-1b"));
        orchestrator.addNode("node3", "worker-3", 8.0, 16.0, Map.of("zone", "us-east-1c"));

        // Create pods
        for (int i = 0; i < 5; i++) {
            String podId = orchestrator.createPod("web-pod-" + i, "default",
                    Map.of("app", "web", "version", "v1"));
            orchestrator.addContainer(podId, "web-container", "nginx:latest", 0.5, 1.0, 1.0, 2.0);
        }

        for (int i = 0; i < 3; i++) {
            String podId = orchestrator.createPod("api-pod-" + i, "default",
                    Map.of("app", "api", "version", "v1"));
            orchestrator.addContainer(podId, "api-container", "app:latest", 1.0, 2.0, 2.0, 4.0);
        }

        // Create services
        orchestrator.createService("web-service", "default", 80, 8080, Map.of("app", "web"));
        orchestrator.createService("api-service", "default", 8000, 8000, Map.of("app", "api"));

        System.out.println("Initial cluster stats: " + orchestrator.getClusterStats());

        // Wait for scheduling and health checks
        Thread.sleep(15000);

        System.out.println("After scheduling: " + orchestrator.getClusterStats());

        // Create more pods to test scaling
        for (int i = 5; i < 10; i++) {
            String podId = orchestrator.createPod("web-pod-" + i, "default",
                    Map.of("app", "web", "version", "v2"));
            orchestrator.addContainer(podId, "web-container", "nginx:v2", 0.5, 1.0, 1.0, 2.0);
        }

        Thread.sleep(10000);

        System.out.println("Final cluster stats: " + orchestrator.getClusterStats());

        orchestrator.shutdown();
    }
}
