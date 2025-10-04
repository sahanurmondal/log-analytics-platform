package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Variation: Design Distributed Graph
 *
 * Description:
 * Design a distributed graph system supporting addNode, addEdge, and
 * shortestPath operations.
 *
 * Constraints:
 * - At most 10^6 operations.
 *
 * Follow-up:
 * - Can you optimize for partitioning and replication?
 * - Can you support dynamic topology changes?
 * 
 * Time Complexity: O(V + E) for shortest path, O(1) for add operations
 * Space Complexity: O(V + E) distributed across shards
 * 
 * Company Tags: Google, Facebook, LinkedIn, Uber
 */
public class DesignDistributedGraph {

    class GraphNode {
        int nodeId;
        Map<Integer, Integer> neighbors; // neighborId -> weight
        int shardId;

        GraphNode(int nodeId, int shardId) {
            this.nodeId = nodeId;
            this.shardId = shardId;
            this.neighbors = new ConcurrentHashMap<>();
        }
    }

    class GraphShard {
        int shardId;
        Map<Integer, GraphNode> nodes;
        Map<Integer, Set<Integer>> incomingEdges; // track incoming edges for partitioned nodes

        GraphShard(int shardId) {
            this.shardId = shardId;
            this.nodes = new ConcurrentHashMap<>();
            this.incomingEdges = new ConcurrentHashMap<>();
        }

        void addNode(int nodeId) {
            nodes.putIfAbsent(nodeId, new GraphNode(nodeId, shardId));
        }

        void addEdge(int from, int to, int weight) {
            GraphNode fromNode = nodes.get(from);
            if (fromNode != null) {
                fromNode.neighbors.put(to, weight);
            }
            // Track incoming edge for cross-shard queries
            Set<Integer> incomingSet = incomingEdges.get(to);
            if (incomingSet == null) {
                incomingSet = ConcurrentHashMap.newKeySet();
                incomingEdges.put(to, incomingSet);
            }
            incomingSet.add(from);
        }

        List<Integer> getNeighbors(int nodeId) {
            GraphNode node = nodes.get(nodeId);
            return node != null ? new ArrayList<>(node.neighbors.keySet()) : new ArrayList<>();
        }

        Integer getEdgeWeight(int from, int to) {
            GraphNode node = nodes.get(from);
            return node != null ? node.neighbors.get(to) : null;
        }
    }

    private final int numShards;
    private final List<GraphShard> shards;
    private final Map<Integer, Integer> nodeToShard; // nodeId -> shardId

    public DesignDistributedGraph(int nodeCount) {
        this.numShards = Math.min(nodeCount / 1000 + 1, 10); // Reasonable shard count
        this.shards = new ArrayList<>();
        this.nodeToShard = new ConcurrentHashMap<>();

        for (int i = 0; i < numShards; i++) {
            shards.add(new GraphShard(i));
        }
    }

    public void addNode(int nodeId) {
        if (nodeToShard.containsKey(nodeId)) {
            return; // Node already exists
        }

        int shardId = getShardForNode(nodeId);
        nodeToShard.put(nodeId, shardId);
        shards.get(shardId).addNode(nodeId);
    }

    public void addEdge(int from, int to, int weight) {
        // Ensure both nodes exist
        addNode(from);
        addNode(to);

        int fromShardId = nodeToShard.get(from);
        shards.get(fromShardId).addEdge(from, to, weight);

        // If nodes are in different shards, update the target shard's incoming edges
        int toShardId = nodeToShard.get(to);
        if (fromShardId != toShardId) {
            Set<Integer> incomingSet = shards.get(toShardId).incomingEdges.get(to);
            if (incomingSet == null) {
                incomingSet = ConcurrentHashMap.newKeySet();
                shards.get(toShardId).incomingEdges.put(to, incomingSet);
            }
            incomingSet.add(from);
        }
    }

    public int shortestPath(int from, int to) {
        if (!nodeToShard.containsKey(from) || !nodeToShard.containsKey(to)) {
            return -1; // One or both nodes don't exist
        }

        if (from == to) {
            return 0;
        }

        // Use distributed Dijkstra's algorithm
        return distributedDijkstra(from, to);
    }

    private int getShardForNode(int nodeId) {
        return Math.abs(Integer.valueOf(nodeId).hashCode()) % numShards;
    }

    private int distributedDijkstra(int source, int target) {
        Map<Integer, Integer> distances = new ConcurrentHashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1])); // [nodeId, distance]
        Set<Integer> visited = ConcurrentHashMap.newKeySet();

        distances.put(source, 0);
        pq.offer(new int[] { source, 0 });

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentNode = current[0];
            int currentDist = current[1];

            if (visited.contains(currentNode)) {
                continue;
            }

            visited.add(currentNode);

            if (currentNode == target) {
                return currentDist;
            }

            // Get neighbors from the appropriate shard
            int shardId = nodeToShard.get(currentNode);
            GraphShard shard = shards.get(shardId);
            GraphNode node = shard.nodes.get(currentNode);

            if (node != null) {
                for (Map.Entry<Integer, Integer> neighbor : node.neighbors.entrySet()) {
                    int neighborId = neighbor.getKey();
                    int edgeWeight = neighbor.getValue();

                    if (!visited.contains(neighborId)) {
                        int newDist = currentDist + edgeWeight;
                        int oldDist = distances.getOrDefault(neighborId, Integer.MAX_VALUE);

                        if (newDist < oldDist) {
                            distances.put(neighborId, newDist);
                            pq.offer(new int[] { neighborId, newDist });
                        }
                    }
                }
            }
        }

        return -1; // No path found
    }

    // Utility methods for monitoring and management
    public Map<String, Object> getGraphStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalNodes = nodeToShard.size();
        int totalEdges = 0;

        for (GraphShard shard : shards) {
            for (GraphNode node : shard.nodes.values()) {
                totalEdges += node.neighbors.size();
            }
        }

        stats.put("totalNodes", totalNodes);
        stats.put("totalEdges", totalEdges);
        stats.put("numShards", numShards);
        stats.put("averageNodesPerShard", totalNodes / numShards);

        return stats;
    }

    public List<Map<String, Object>> getShardStats() {
        List<Map<String, Object>> shardStats = new ArrayList<>();

        for (GraphShard shard : shards) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("shardId", shard.shardId);
            stats.put("nodeCount", shard.nodes.size());

            int edgeCount = 0;
            for (GraphNode node : shard.nodes.values()) {
                edgeCount += node.neighbors.size();
            }
            stats.put("edgeCount", edgeCount);

            shardStats.add(stats);
        }

        return shardStats;
    }

    public static void main(String[] args) {
        DesignDistributedGraph graph = new DesignDistributedGraph(10);

        // Add nodes
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);

        // Add edges
        graph.addEdge(1, 2, 5);
        graph.addEdge(2, 3, 3);
        graph.addEdge(1, 3, 10);
        graph.addEdge(3, 4, 2);
        graph.addEdge(1, 4, 15);

        // Test shortest paths
        System.out.println("Shortest path 1->2: " + graph.shortestPath(1, 2)); // 5
        System.out.println("Shortest path 1->3: " + graph.shortestPath(1, 3)); // 8 (via 2)
        System.out.println("Shortest path 1->4: " + graph.shortestPath(1, 4)); // 10 (via 2->3)
        System.out.println("Shortest path 1->5: " + graph.shortestPath(1, 5)); // -1 (no path)

        // Edge Case: Path not found
        System.out.println("Path 1->3: " + graph.shortestPath(1, 3)); // 8

        // Stats
        System.out.println("\nGraph Stats: " + graph.getGraphStats());
        System.out.println("Shard Stats: " + graph.getShardStats());

        // Test with larger graph
        for (int i = 5; i <= 20; i++) {
            graph.addNode(i);
            if (i > 5) {
                graph.addEdge(i - 1, i, 1); // Chain of nodes
            }
        }

        graph.addEdge(4, 20, 50); // Direct edge
        System.out.println("\nShortest path 1->20: " + graph.shortestPath(1, 20)); // Should find optimal path

        System.out.println("Final Stats: " + graph.getGraphStats());
    }
}
