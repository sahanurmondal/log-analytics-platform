package design.hard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Design Distributed Bloom Filter
 *
 * Description: Design a distributed bloom filter that supports add and contains
 * operations across multiple nodes. A Bloom filter is a space-efficient
 * probabilistic data structure for testing set membership.
 *
 * Constraints:
 * - At most 10^6 operations
 * - Support multiple hash functions
 * - Minimize false positive rate
 * - Handle node failures gracefully
 *
 * Follow-up:
 * - Can you optimize for false positive rate?
 * - Can you support deletion (Counting Bloom Filter)?
 * - How to handle node failures and replication?
 * 
 * Time Complexity: O(k) for add/contains where k is number of hash functions
 * Space Complexity: O(m/n) where m is total bits, n is number of nodes
 * 
 * Company Tags: Google, Facebook, Amazon, Netflix
 */
public class DesignDistributedBloomFilter {

    class BloomNode {
        private final BitSet bitSet;
        private final int size;
        private final String nodeId;

        public BloomNode(String nodeId, int size) {
            this.nodeId = nodeId;
            this.size = size;
            this.bitSet = new BitSet(size);
        }

        public void setBit(int index) {
            bitSet.set(index % size);
        }

        public boolean getBit(int index) {
            return bitSet.get(index % size);
        }

        public String getNodeId() {
            return nodeId;
        }
    }

    private final List<BloomNode> nodes;
    private final int nodeCount;
    private final int bitsPerNode;
    private final int hashFunctions;
    private final Map<String, BloomNode> nodeMap;

    public DesignDistributedBloomFilter(int nodeCount, int totalSize) {
        this.nodeCount = nodeCount;
        this.bitsPerNode = totalSize / nodeCount;
        this.hashFunctions = 3; // Optimal for most use cases
        this.nodes = new ArrayList<>();
        this.nodeMap = new ConcurrentHashMap<>();

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            String nodeId = "node-" + i;
            BloomNode node = new BloomNode(nodeId, bitsPerNode);
            nodes.add(node);
            nodeMap.put(nodeId, node);
        }
    }

    public void add(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        // Generate multiple hash values
        int[] hashes = generateHashes(value);

        // Set bits in appropriate nodes
        for (int hash : hashes) {
            int nodeIndex = Math.abs(hash) % nodeCount;
            int bitIndex = Math.abs(hash) % bitsPerNode;
            nodes.get(nodeIndex).setBit(bitIndex);
        }
    }

    public boolean contains(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        // Generate same hash values
        int[] hashes = generateHashes(value);

        // Check all bits
        for (int hash : hashes) {
            int nodeIndex = Math.abs(hash) % nodeCount;
            int bitIndex = Math.abs(hash) % bitsPerNode;

            if (!nodes.get(nodeIndex).getBit(bitIndex)) {
                return false; // Definitely not in set
            }
        }

        return true; // Possibly in set (may be false positive)
    }

    private int[] generateHashes(String value) {
        int[] hashes = new int[hashFunctions];

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            byte[] md5Hash = md5.digest(value.getBytes(StandardCharsets.UTF_8));
            byte[] sha1Hash = sha1.digest(value.getBytes(StandardCharsets.UTF_8));

            // Generate multiple hash values using different combinations
            hashes[0] = bytesToInt(md5Hash, 0);
            hashes[1] = bytesToInt(sha1Hash, 0);
            hashes[2] = bytesToInt(md5Hash, 4) ^ bytesToInt(sha1Hash, 4);

        } catch (Exception e) {
            // Fallback to simple hash functions
            int hash1 = value.hashCode();
            int hash2 = hash1 * 31;
            int hash3 = hash2 * 17;

            hashes[0] = hash1;
            hashes[1] = hash2;
            hashes[2] = hash3;
        }

        return hashes;
    }

    private int bytesToInt(byte[] bytes, int offset) {
        if (offset + 4 > bytes.length) {
            offset = 0;
        }
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[offset + 1] & 0xFF) << 16) |
                ((bytes[offset + 2] & 0xFF) << 8) |
                (bytes[offset + 3] & 0xFF);
    }

    public double getFalsePositiveRate() {
        // Theoretical false positive rate: (1 - e^(-kn/m))^k
        // k = number of hash functions, n = number of elements, m = bit array size
        return Math.pow(1 - Math.exp(-hashFunctions * 1000.0 / (nodeCount * bitsPerNode)), hashFunctions);
    }

    public void addNode(String nodeId) {
        if (!nodeMap.containsKey(nodeId)) {
            BloomNode newNode = new BloomNode(nodeId, bitsPerNode);
            nodes.add(newNode);
            nodeMap.put(nodeId, newNode);
        }
    }

    public boolean removeNode(String nodeId) {
        BloomNode node = nodeMap.remove(nodeId);
        return node != null && nodes.remove(node);
    }

    public int getActiveNodes() {
        return nodes.size();
    }

    public static void main(String[] args) {
        System.out.println("=== Distributed Bloom Filter Test ===");

        DesignDistributedBloomFilter filter = new DesignDistributedBloomFilter(3, 1000);

        // Test 1: Basic functionality
        filter.add("apple");
        filter.add("banana");
        filter.add("cherry");

        System.out.println("Contains 'apple': " + filter.contains("apple")); // true
        System.out.println("Contains 'banana': " + filter.contains("banana")); // true
        System.out.println("Contains 'cherry': " + filter.contains("cherry")); // true
        System.out.println("Contains 'grape': " + filter.contains("grape")); // false (likely)

        // Test 2: False positive demonstration
        System.out.println("\n=== False Positive Test ===");
        String[] testWords = { "dog", "cat", "mouse", "elephant", "tiger" };

        for (String word : testWords) {
            boolean result = filter.contains(word);
            System.out.println("Contains '" + word + "': " + result +
                    (result ? " (FALSE POSITIVE)" : " (CORRECT)"));
        }

        // Test 3: Add many elements
        System.out.println("\n=== Bulk Add Test ===");
        for (int i = 0; i < 100; i++) {
            filter.add("item" + i);
        }

        int falsePositives = 0;
        int totalTests = 100;

        for (int i = 100; i < 100 + totalTests; i++) {
            if (filter.contains("item" + i)) {
                falsePositives++;
            }
        }

        double actualFalsePositiveRate = (double) falsePositives / totalTests;
        double theoreticalRate = filter.getFalsePositiveRate();

        System.out.println("False positives: " + falsePositives + "/" + totalTests);
        System.out.println("Actual FP rate: " + String.format("%.4f", actualFalsePositiveRate));
        System.out.println("Theoretical FP rate: " + String.format("%.4f", theoreticalRate));

        // Test 4: Node management
        System.out.println("\n=== Node Management Test ===");
        System.out.println("Active nodes: " + filter.getActiveNodes());

        filter.addNode("backup-node");
        System.out.println("After adding node: " + filter.getActiveNodes());

        filter.removeNode("node-0");
        System.out.println("After removing node: " + filter.getActiveNodes());

        // Edge Case: Add duplicate
        filter.add("apple");
        System.out.println("Contains 'apple' after duplicate add: " + filter.contains("apple")); // true
    }
}
