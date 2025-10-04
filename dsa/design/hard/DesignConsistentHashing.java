package design.hard;

import java.util.*;

/**
 * Variation: Design Consistent Hashing
 *
 * Description:
 * Implement consistent hashing for distributed systems.
 *
 * Constraints:
 * - At most 10^5 operations.
 *
 * Follow-up:
 * - Can you optimize for virtual nodes?
 * - Can you support dynamic node addition/removal?
 * 
 * Time Complexity: O(log n) for getNode, O(log n) for addNode/removeNode
 * Space Complexity: O(n * virtual_nodes)
 * 
 * Company Tags: Google, Amazon, Facebook
 */
public class DesignConsistentHashing {

    private TreeMap<Integer, Integer> ring; // hash -> nodeId
    private Set<Integer> nodes;
    private int ringSize;
    private int virtualNodes;

    public DesignConsistentHashing(int ringSize) {
        this.ringSize = ringSize;
        this.virtualNodes = 100; // Default virtual nodes per physical node
        this.ring = new TreeMap<>();
        this.nodes = new HashSet<>();
    }

    public DesignConsistentHashing(int ringSize, int virtualNodes) {
        this.ringSize = ringSize;
        this.virtualNodes = virtualNodes;
        this.ring = new TreeMap<>();
        this.nodes = new HashSet<>();
    }

    private int hash(String input) {
        return Math.abs(input.hashCode() % ringSize);
    }

    public int getNode(String key) {
        if (ring.isEmpty()) {
            return -1;
        }

        int hash = hash(key);

        // Find the first node clockwise from the hash
        Map.Entry<Integer, Integer> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            // Wrap around to the first node
            entry = ring.firstEntry();
        }

        return entry.getValue();
    }

    public void addNode(int nodeId) {
        if (nodes.contains(nodeId)) {
            return; // Node already exists
        }

        nodes.add(nodeId);

        // Add virtual nodes for this physical node
        for (int i = 0; i < virtualNodes; i++) {
            String virtualKey = nodeId + ":" + i;
            int hash = hash(virtualKey);
            ring.put(hash, nodeId);
        }
    }

    public void removeNode(int nodeId) {
        if (!nodes.contains(nodeId)) {
            return; // Node doesn't exist
        }

        nodes.remove(nodeId);

        // Remove all virtual nodes for this physical node
        Iterator<Map.Entry<Integer, Integer>> iterator = ring.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            if (entry.getValue() == nodeId) {
                iterator.remove();
            }
        }
    }

    // Get all nodes in the ring (for debugging)
    public List<Integer> getAllNodes() {
        return new ArrayList<>(nodes);
    }

    // Get ring distribution (for debugging)
    public Map<Integer, List<Integer>> getRingDistribution() {
        Map<Integer, List<Integer>> distribution = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : ring.entrySet()) {
            distribution.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                    .add(entry.getKey());
        }
        return distribution;
    }

    public static void main(String[] args) {
        DesignConsistentHashing hashing = new DesignConsistentHashing(100);
        hashing.addNode(1);
        hashing.addNode(2);
        System.out.println(hashing.getNode("key1")); // nodeId
        hashing.removeNode(1);
        System.out.println(hashing.getNode("key1")); // nodeId
        // Edge Case: Remove non-existent node
        hashing.removeNode(99);
        // Edge Case: Add duplicate node
        hashing.addNode(2);

        // Test with multiple keys
        hashing.addNode(3);
        System.out.println("key1 -> node " + hashing.getNode("key1"));
        System.out.println("key2 -> node " + hashing.getNode("key2"));
        System.out.println("key3 -> node " + hashing.getNode("key3"));
    }
}
