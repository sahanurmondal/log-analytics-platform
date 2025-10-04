package design.hard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Design Distributed Min Deque
 *
 * Description: Design a distributed double-ended queue (deque) that supports
 * push/pop operations from both ends and retrieving the minimum element
 * in constant time across multiple nodes.
 *
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 10^6 calls will be made to push, pop, and getMin
 * - Support distributed operations with replication
 * - Handle node failures gracefully
 *
 * Follow-up:
 * - Can you optimize for thread safety?
 * - Can you generalize for max deque and replication?
 * - How to handle consensus for distributed min tracking?
 * 
 * Time Complexity: O(1) for all operations amortized
 * Space Complexity: O(n * replication_factor)
 * 
 * Company Tags: Google, Amazon, Facebook, Uber
 */
public class DesignDistributedDequeWithMin {

    class DequeNode {
        private final String nodeId;
        private final LinkedList<Integer> deque;
        private final LinkedList<Integer> minStack; // Track minimums
        private final ReadWriteLock lock;
        private final Map<Integer, Integer> elementCount;

        public DequeNode(String nodeId) {
            this.nodeId = nodeId;
            this.deque = new LinkedList<>();
            this.minStack = new LinkedList<>();
            this.lock = new ReentrantReadWriteLock();
            this.elementCount = new HashMap<>();
        }

        public void pushFront(int val) {
            lock.writeLock().lock();
            try {
                deque.addFirst(val);
                updateMinOnAdd(val);
                elementCount.merge(val, 1, Integer::sum);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void pushBack(int val) {
            lock.writeLock().lock();
            try {
                deque.addLast(val);
                updateMinOnAdd(val);
                elementCount.merge(val, 1, Integer::sum);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public Integer popFront() {
            lock.writeLock().lock();
            try {
                if (deque.isEmpty()) {
                    return null;
                }
                int val = deque.removeFirst();
                updateMinOnRemove(val);
                decrementCount(val);
                return val;
            } finally {
                lock.writeLock().unlock();
            }
        }

        public Integer popBack() {
            lock.writeLock().lock();
            try {
                if (deque.isEmpty()) {
                    return null;
                }
                int val = deque.removeLast();
                updateMinOnRemove(val);
                decrementCount(val);
                return val;
            } finally {
                lock.writeLock().unlock();
            }
        }

        public Integer getMin() {
            lock.readLock().lock();
            try {
                return minStack.isEmpty() ? null : minStack.peekLast();
            } finally {
                lock.readLock().unlock();
            }
        }

        public int size() {
            lock.readLock().lock();
            try {
                return deque.size();
            } finally {
                lock.readLock().unlock();
            }
        }

        public boolean isEmpty() {
            lock.readLock().lock();
            try {
                return deque.isEmpty();
            } finally {
                lock.readLock().unlock();
            }
        }

        private void updateMinOnAdd(int val) {
            if (minStack.isEmpty() || val <= minStack.peekLast()) {
                minStack.addLast(val);
            }
        }

        private void updateMinOnRemove(int val) {
            if (!minStack.isEmpty() && val == minStack.peekLast()) {
                minStack.removeLast();
            }
        }

        private void decrementCount(int val) {
            elementCount.merge(val, -1, Integer::sum);
            if (elementCount.get(val) <= 0) {
                elementCount.remove(val);
            }
        }

        public String getNodeId() {
            return nodeId;
        }

        public List<Integer> getSnapshot() {
            lock.readLock().lock();
            try {
                return new ArrayList<>(deque);
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    private final List<DequeNode> nodes;
    private final Map<String, DequeNode> nodeMap;
    private final int replicationFactor;
    private int currentNodeIndex = 0;

    public DesignDistributedDequeWithMin(int nodeCount) {
        this(nodeCount, Math.min(2, nodeCount)); // Default replication factor
    }

    public DesignDistributedDequeWithMin(int nodeCount, int replicationFactor) {
        this.nodes = new ArrayList<>();
        this.nodeMap = new ConcurrentHashMap<>();
        this.replicationFactor = Math.min(replicationFactor, nodeCount);

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            String nodeId = "deque-node-" + i;
            DequeNode node = new DequeNode(nodeId);
            nodes.add(node);
            nodeMap.put(nodeId, node);
        }
    }

    public void pushFront(int val) {
        List<DequeNode> targetNodes = getNodesForOperation();

        for (DequeNode node : targetNodes) {
            node.pushFront(val);
        }
    }

    public void pushBack(int val) {
        List<DequeNode> targetNodes = getNodesForOperation();

        for (DequeNode node : targetNodes) {
            node.pushBack(val);
        }
    }

    public Integer popFront() {
        List<DequeNode> targetNodes = getNodesForOperation();

        // Try to pop from the first non-empty node
        for (DequeNode node : targetNodes) {
            Integer result = node.popFront();
            if (result != null) {
                // Remove from replicas as well
                for (DequeNode replica : targetNodes) {
                    if (replica != node && !replica.isEmpty()) {
                        replica.popFront();
                    }
                }
                return result;
            }
        }

        return null;
    }

    public Integer popBack() {
        List<DequeNode> targetNodes = getNodesForOperation();

        // Try to pop from the first non-empty node
        for (DequeNode node : targetNodes) {
            Integer result = node.popBack();
            if (result != null) {
                // Remove from replicas as well
                for (DequeNode replica : targetNodes) {
                    if (replica != node && !replica.isEmpty()) {
                        replica.popBack();
                    }
                }
                return result;
            }
        }

        return null;
    }

    private List<DequeNode> getNodesForOperation() {
        List<DequeNode> targetNodes = new ArrayList<>();

        // Round-robin with replication
        targetNodes.add(nodes.get(currentNodeIndex % nodes.size()));

        // Add replica nodes
        for (int i = 1; i < replicationFactor; i++) {
            int replicaIndex = (currentNodeIndex + i) % nodes.size();
            targetNodes.add(nodes.get(replicaIndex));
        }

        currentNodeIndex = (currentNodeIndex + 1) % nodes.size();
        return targetNodes;
    }

    public Integer getMin() {
        // Query all nodes and return the global minimum
        Integer globalMin = null;

        for (DequeNode node : nodes) {
            Integer nodeMin = node.getMin();
            if (nodeMin != null) {
                if (globalMin == null || nodeMin < globalMin) {
                    globalMin = nodeMin;
                }
            }
        }

        return globalMin;
    }

    public static void main(String[] args) {
        System.out.println("=== Distributed Min Deque Test ===");

        DesignDistributedDequeWithMin deque = new DesignDistributedDequeWithMin(3);

        // Basic operations
        deque.pushFront(3);
        deque.pushBack(2);
        deque.pushFront(1);
        System.out.println("Min after pushes: " + deque.getMin()); // 1

        deque.popFront();
        System.out.println("Min after pop front: " + deque.getMin()); // 2

        deque.popBack();
        System.out.println("Min after pop back: " + deque.getMin()); // 3

        // Edge cases
        System.out.println("Pop from empty: " + deque.popFront());
        System.out.println("Min of empty: " + deque.getMin());
    }
}
