package design.hard;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Variation: Design Distributed Min Stack
 *
 * Description:
 * Design a distributed stack that supports push, pop, top, and retrieving the
 * minimum element in constant time.
 *
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 10^6 calls will be made to push, pop, top, and getMin.
 *
 * Follow-up:
 * - Can you optimize for thread safety?
 * - Can you generalize for max stack and replication?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n) where n is number of elements
 * 
 * Company Tags: System Design, Distributed Systems
 */
public class DesignDistributedStackWithMin {

    class StackNode {
        int nodeId;
        Stack<Integer> stack;
        Stack<Integer> minStack;
        ReentrantReadWriteLock lock;

        StackNode(int nodeId) {
            this.nodeId = nodeId;
            this.stack = new Stack<>();
            this.minStack = new Stack<>();
            this.lock = new ReentrantReadWriteLock();
        }

        void push(int val) {
            lock.writeLock().lock();
            try {
                stack.push(val);
                if (minStack.isEmpty() || val <= minStack.peek()) {
                    minStack.push(val);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        int pop() {
            lock.writeLock().lock();
            try {
                if (stack.isEmpty()) {
                    throw new RuntimeException("Stack is empty");
                }

                int val = stack.pop();
                if (!minStack.isEmpty() && val == minStack.peek()) {
                    minStack.pop();
                }
                return val;
            } finally {
                lock.writeLock().unlock();
            }
        }

        int top() {
            lock.readLock().lock();
            try {
                if (stack.isEmpty()) {
                    throw new RuntimeException("Stack is empty");
                }
                return stack.peek();
            } finally {
                lock.readLock().unlock();
            }
        }

        int getMin() {
            lock.readLock().lock();
            try {
                if (minStack.isEmpty()) {
                    throw new RuntimeException("Stack is empty");
                }
                return minStack.peek();
            } finally {
                lock.readLock().unlock();
            }
        }

        boolean isEmpty() {
            lock.readLock().lock();
            try {
                return stack.isEmpty();
            } finally {
                lock.readLock().unlock();
            }
        }

        int size() {
            lock.readLock().lock();
            try {
                return stack.size();
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    private List<StackNode> nodes;
    private int currentNode;
    private int maxNodeSize;
    private ReentrantReadWriteLock globalLock;

    public DesignDistributedStackWithMin(int nodeCount) {
        nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            nodes.add(new StackNode(i));
        }
        currentNode = 0;
        maxNodeSize = 1000; // Max elements per node
        globalLock = new ReentrantReadWriteLock();
    }

    public DesignDistributedStackWithMin(int nodeCount, int maxNodeSize) {
        this(nodeCount);
        this.maxNodeSize = maxNodeSize;
    }

    public void push(int val) {
        globalLock.writeLock().lock();
        try {
            // Find a node that can accept new elements
            StackNode targetNode = findAvailableNode();
            targetNode.push(val);
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    private StackNode findAvailableNode() {
        // Try current node first
        if (nodes.get(currentNode).size() < maxNodeSize) {
            return nodes.get(currentNode);
        }

        // Find next available node
        for (int i = 0; i < nodes.size(); i++) {
            int nodeIndex = (currentNode + i) % nodes.size();
            if (nodes.get(nodeIndex).size() < maxNodeSize) {
                currentNode = nodeIndex;
                return nodes.get(nodeIndex);
            }
        }

        // If all nodes are at capacity, use round-robin
        currentNode = (currentNode + 1) % nodes.size();
        return nodes.get(currentNode);
    }

    private StackNode findTopNode() {
        // Find the node with the most recent element (highest index with data)
        for (int i = nodes.size() - 1; i >= 0; i--) {
            int nodeIndex = (currentNode + i) % nodes.size();
            if (!nodes.get(nodeIndex).isEmpty()) {
                return nodes.get(nodeIndex);
            }
        }

        // Check from current node backwards
        for (int i = currentNode; i >= 0; i--) {
            if (!nodes.get(i).isEmpty()) {
                return nodes.get(i);
            }
        }

        // Check forward from current node
        for (int i = currentNode + 1; i < nodes.size(); i++) {
            if (!nodes.get(i).isEmpty()) {
                return nodes.get(i);
            }
        }

        return null; // All nodes are empty
    }

    public int pop() {
        globalLock.writeLock().lock();
        try {
            StackNode topNode = findTopNode();
            if (topNode == null) {
                throw new RuntimeException("Stack is empty");
            }
            return topNode.pop();
        } catch (RuntimeException e) {
            return Integer.MIN_VALUE; // Handle gracefully as per main method
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    public int top() {
        globalLock.readLock().lock();
        try {
            StackNode topNode = findTopNode();
            if (topNode == null) {
                return -1;
            }
            return topNode.top();
        } catch (RuntimeException e) {
            return -1;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public int getMin() {
        globalLock.readLock().lock();
        try {
            int globalMin = Integer.MAX_VALUE;
            boolean hasElements = false;

            for (StackNode node : nodes) {
                if (!node.isEmpty()) {
                    hasElements = true;
                    int nodeMin = node.getMin();
                    globalMin = Math.min(globalMin, nodeMin);
                }
            }

            return hasElements ? globalMin : Integer.MAX_VALUE;
        } catch (RuntimeException e) {
            return Integer.MAX_VALUE; // Handle gracefully
        } finally {
            globalLock.readLock().unlock();
        }
    }

    // Additional utility methods
    public boolean isEmpty() {
        globalLock.readLock().lock();
        try {
            return nodes.stream().allMatch(StackNode::isEmpty);
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public int size() {
        globalLock.readLock().lock();
        try {
            return nodes.stream().mapToInt(StackNode::size).sum();
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public Map<String, Object> getStats() {
        globalLock.readLock().lock();
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalNodes", nodes.size());
            stats.put("totalElements", size());
            stats.put("isEmpty", isEmpty());
            stats.put("currentNode", currentNode);

            List<Integer> nodeSizes = new ArrayList<>();
            for (StackNode node : nodes) {
                nodeSizes.add(node.size());
            }
            stats.put("nodeSizes", nodeSizes);

            return stats;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public static void main(String[] args) {
        DesignDistributedStackWithMin stack = new DesignDistributedStackWithMin(3);
        stack.push(-2);
        stack.push(0);
        stack.push(-3);
        System.out.println(stack.getMin()); // -3
        stack.pop();
        System.out.println(stack.top()); // 0
        System.out.println(stack.getMin()); // -2
        // Edge Case: Pop from empty stack
        stack.pop();
        stack.pop();
        System.out.println(stack.pop()); // Should handle gracefully
        // Edge Case: Get min from empty stack
        System.out.println(stack.getMin()); // Should handle gracefully
        // Edge Case: Push duplicate min
        stack.push(-2);
        stack.push(-2);
        System.out.println(stack.getMin()); // -2
        stack.pop();
        System.out.println(stack.getMin()); // -2

        System.out.println("\nStats: " + stack.getStats());
    }
}
