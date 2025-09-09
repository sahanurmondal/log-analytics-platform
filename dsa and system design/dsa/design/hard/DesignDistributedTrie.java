package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Variation: Design Distributed Trie
 *
 * Description:
 * Design a distributed trie supporting insert, search, and delete operations.
 *
 * Constraints:
 * - At most 10^6 operations.
 *
 * Follow-up:
 * - Can you optimize for prefix search?
 * - Can you support wildcard search?
 * 
 * Time Complexity: O(m) where m is word length, distributed across nodes
 * Space Complexity: O(ALPHABET_SIZE * N * m) distributed across nodes
 * 
 * Company Tags: System Design, Distributed Systems, Trie, Search
 */
public class DesignDistributedTrie {

    private static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;
        String word; // Store complete word for easy retrieval

        TrieNode() {
            this.children = new ConcurrentHashMap<>();
            this.isEndOfWord = false;
            this.word = null;
        }
    }

    private static class DistributedTrieNode {
        String nodeId;
        TrieNode root;
        final ReentrantReadWriteLock lock;
        boolean isHealthy;
        int wordCount;
        Set<String> prefixCache; // Cache for common prefixes

        DistributedTrieNode(String nodeId) {
            this.nodeId = nodeId;
            this.root = new TrieNode();
            this.lock = new ReentrantReadWriteLock();
            this.isHealthy = true;
            this.wordCount = 0;
            this.prefixCache = ConcurrentHashMap.newKeySet();
        }

        void insert(String word) {
            if (word == null || word.isEmpty()) {
                return;
            }

            lock.writeLock().lock();
            try {
                TrieNode current = root;
                for (char c : word.toCharArray()) {
                    current.children.putIfAbsent(c, new TrieNode());
                    current = current.children.get(c);
                }

                if (!current.isEndOfWord) {
                    current.isEndOfWord = true;
                    current.word = word;
                    wordCount++;
                    updatePrefixCache(word);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        boolean search(String word) {
            if (word == null || word.isEmpty()) {
                return false;
            }

            lock.readLock().lock();
            try {
                TrieNode current = root;
                for (char c : word.toCharArray()) {
                    current = current.children.get(c);
                    if (current == null) {
                        return false;
                    }
                }
                return current.isEndOfWord;
            } finally {
                lock.readLock().unlock();
            }
        }

        boolean delete(String word) {
            if (word == null || word.isEmpty()) {
                return false;
            }

            lock.writeLock().lock();
            try {
                if (deleteHelper(root, word, 0)) {
                    wordCount--;
                    removePrefixFromCache(word);
                    return true;
                }
                return false;
            } finally {
                lock.writeLock().unlock();
            }
        }

        private boolean deleteHelper(TrieNode node, String word, int index) {
            if (index == word.length()) {
                if (!node.isEndOfWord) {
                    return false; // Word doesn't exist
                }
                node.isEndOfWord = false;
                node.word = null;
                // Return true if current has no children (can be deleted)
                return node.children.isEmpty();
            }

            char c = word.charAt(index);
            TrieNode child = node.children.get(c);
            if (child == null) {
                return false; // Word doesn't exist
            }

            boolean shouldDeleteChild = deleteHelper(child, word, index + 1);

            if (shouldDeleteChild) {
                node.children.remove(c);
                // Return true if current has no children and is not end of another word
                return !node.isEndOfWord && node.children.isEmpty();
            }

            return false;
        }

        List<String> searchWithPrefix(String prefix) {
            if (prefix == null) {
                prefix = "";
            }

            lock.readLock().lock();
            try {
                List<String> result = new ArrayList<>();
                TrieNode current = root;

                // Navigate to prefix
                for (char c : prefix.toCharArray()) {
                    current = current.children.get(c);
                    if (current == null) {
                        return result; // Empty list
                    }
                }

                // DFS to find all words with this prefix
                dfsCollectWords(current, prefix, result);
                return result;
            } finally {
                lock.readLock().unlock();
            }
        }

        private void dfsCollectWords(TrieNode node, String prefix, List<String> result) {
            if (node.isEndOfWord) {
                result.add(node.word);
            }

            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                dfsCollectWords(entry.getValue(), prefix + entry.getKey(), result);
            }
        }

        List<String> searchWithWildcard(String pattern) {
            lock.readLock().lock();
            try {
                List<String> result = new ArrayList<>();
                dfsWildcardSearch(root, pattern, 0, "", result);
                return result;
            } finally {
                lock.readLock().unlock();
            }
        }

        private void dfsWildcardSearch(TrieNode node, String pattern, int index,
                String current, List<String> result) {
            if (index == pattern.length()) {
                if (node.isEndOfWord) {
                    result.add(node.word);
                }
                return;
            }

            char c = pattern.charAt(index);
            if (c == '.') {
                // Wildcard - try all possible characters
                for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                    dfsWildcardSearch(entry.getValue(), pattern, index + 1,
                            current + entry.getKey(), result);
                }
            } else {
                // Regular character
                TrieNode child = node.children.get(c);
                if (child != null) {
                    dfsWildcardSearch(child, pattern, index + 1, current + c, result);
                }
            }
        }

        Set<String> getAllWords() {
            lock.readLock().lock();
            try {
                Set<String> words = new HashSet<>();
                getAllWordsHelper(root, words);
                return words;
            } finally {
                lock.readLock().unlock();
            }
        }

        private void getAllWordsHelper(TrieNode node, Set<String> words) {
            if (node.isEndOfWord) {
                words.add(node.word);
            }

            for (TrieNode child : node.children.values()) {
                getAllWordsHelper(child, words);
            }
        }

        private void updatePrefixCache(String word) {
            for (int i = 1; i <= word.length(); i++) {
                prefixCache.add(word.substring(0, i));
            }
        }

        private void removePrefixFromCache(String word) {
            // Remove prefixes only if no other words use them
            for (int i = 1; i <= word.length(); i++) {
                String prefix = word.substring(0, i);
                if (searchWithPrefix(prefix).isEmpty()) {
                    prefixCache.remove(prefix);
                }
            }
        }
    }

    private final List<DistributedTrieNode> nodes;
    private final ConsistentHashing hashRing;
    private final ExecutorService executorService;
    private final ScheduledExecutorService maintenanceService;
    private final ReentrantReadWriteLock globalLock;

    public DesignDistributedTrie(int nodeCount) {
        if (nodeCount <= 0) {
            throw new IllegalArgumentException("Node count must be positive");
        }

        this.nodes = new ArrayList<>();
        this.hashRing = new ConsistentHashing();
        this.executorService = Executors.newCachedThreadPool();
        this.maintenanceService = Executors.newScheduledThreadPool(2);
        this.globalLock = new ReentrantReadWriteLock();

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            String nodeId = "trie-node-" + i;
            DistributedTrieNode node = new DistributedTrieNode(nodeId);
            nodes.add(node);
            hashRing.addNode(nodeId);
        }

        startMaintenance();

        System.out.println("Initialized Distributed Trie with " + nodeCount + " nodes");
    }

    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("Word cannot be null or empty");
        }

        globalLock.readLock().lock();
        try {
            String targetNodeId = hashRing.getNode(word);
            DistributedTrieNode targetNode = findNodeById(targetNodeId);

            if (targetNode != null && targetNode.isHealthy) {
                targetNode.insert(word);
                // System.out.println("Inserted '" + word + "' into " + targetNodeId);
            } else {
                throw new RuntimeException("Target node " + targetNodeId + " is not available");
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public boolean search(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        globalLock.readLock().lock();
        try {
            String targetNodeId = hashRing.getNode(word);
            DistributedTrieNode targetNode = findNodeById(targetNodeId);

            if (targetNode != null && targetNode.isHealthy) {
                return targetNode.search(word);
            }
            return false;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public void delete(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        globalLock.readLock().lock();
        try {
            String targetNodeId = hashRing.getNode(word);
            DistributedTrieNode targetNode = findNodeById(targetNodeId);

            if (targetNode != null && targetNode.isHealthy) {
                boolean deleted = targetNode.delete(word);
                if (deleted) {
                    System.out.println("Deleted '" + word + "' from " + targetNodeId);
                }
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    // Additional advanced operations

    public List<String> searchWithPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        globalLock.readLock().lock();
        try {
            List<String> allResults = new ArrayList<>();

            // Search across all nodes as prefix results might span multiple nodes
            for (DistributedTrieNode node : nodes) {
                if (node.isHealthy) {
                    allResults.addAll(node.searchWithPrefix(prefix));
                }
            }

            return allResults.stream()
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public List<String> searchWithWildcard(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new ArrayList<>();
        }

        globalLock.readLock().lock();
        try {
            List<String> allResults = new ArrayList<>();

            // Search across all nodes for wildcard patterns
            for (DistributedTrieNode node : nodes) {
                if (node.isHealthy) {
                    allResults.addAll(node.searchWithWildcard(pattern));
                }
            }

            return allResults.stream()
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public int getTotalWordCount() {
        return nodes.stream()
                .filter(node -> node.isHealthy)
                .mapToInt(node -> node.wordCount)
                .sum();
    }

    public Set<String> getAllWords() {
        Set<String> allWords = new HashSet<>();
        for (DistributedTrieNode node : nodes) {
            if (node.isHealthy) {
                allWords.addAll(node.getAllWords());
            }
        }
        return allWords;
    }

    public Map<String, Integer> getNodeWordCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (DistributedTrieNode node : nodes) {
            counts.put(node.nodeId, node.wordCount);
        }
        return counts;
    }

    // Helper methods

    private DistributedTrieNode findNodeById(String nodeId) {
        return nodes.stream()
                .filter(node -> node.nodeId.equals(nodeId))
                .findFirst()
                .orElse(null);
    }

    private void startMaintenance() {
        // Health monitoring
        maintenanceService.scheduleWithFixedDelay(() -> {
            for (DistributedTrieNode node : nodes) {
                // Simulate occasional failures (very low chance)
                if (Math.random() < 0.001) {
                    node.isHealthy = false;
                    System.out.println("Node " + node.nodeId + " marked as unhealthy");
                } else if (!node.isHealthy && Math.random() < 0.1) {
                    node.isHealthy = true;
                    System.out.println("Node " + node.nodeId + " recovered");
                }
            }
        }, 10, 10, TimeUnit.SECONDS);

        // Cache cleanup
        maintenanceService.scheduleWithFixedDelay(() -> {
            for (DistributedTrieNode node : nodes) {
                if (node.isHealthy && node.prefixCache.size() > 10000) {
                    // Clean up cache if it gets too large
                    node.prefixCache.clear();
                    Set<String> words = node.getAllWords();
                    for (String word : words) {
                        node.updatePrefixCache(word);
                    }
                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    // Simple consistent hashing implementation
    private static class ConsistentHashing {
        private final SortedMap<Long, String> ring = new TreeMap<>();
        private final int virtualNodes = 150;

        void addNode(String nodeId) {
            for (int i = 0; i < virtualNodes; i++) {
                long hash = hash(nodeId + ":" + i);
                ring.put(hash, nodeId);
            }
        }

        String getNode(String key) {
            if (ring.isEmpty()) {
                return null;
            }

            long hash = hash(key);
            SortedMap<Long, String> tailMap = ring.tailMap(hash);
            Long nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
            return ring.get(nodeHash);
        }

        private long hash(String key) {
            return key.hashCode() & 0x7FFFFFFFL; // Ensure positive
        }
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNodes", nodes.size());
        stats.put("healthyNodes", nodes.stream().mapToInt(n -> n.isHealthy ? 1 : 0).sum());
        stats.put("totalWords", getTotalWordCount());
        stats.put("nodeWordCounts", getNodeWordCounts());
        return stats;
    }

    public void shutdown() {
        maintenanceService.shutdown();
        executorService.shutdown();
        try {
            if (!maintenanceService.awaitTermination(5, TimeUnit.SECONDS)) {
                maintenanceService.shutdownNow();
            }
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            maintenanceService.shutdownNow();
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Distributed Trie Tests ===");

        DesignDistributedTrie trie = new DesignDistributedTrie(3);

        System.out.println("\n--- Basic Operations Test ---");
        trie.insert("apple");
        System.out.println("Search apple: " + trie.search("apple")); // true
        trie.delete("apple");
        System.out.println("Search apple after delete: " + trie.search("apple")); // false

        // Edge Case: Search non-existent word
        System.out.println("Search banana: " + trie.search("banana")); // false

        System.out.println("\n--- Multiple Words Test ---");
        String[] words = { "apple", "app", "application", "apply", "banana", "band", "bandana" };

        for (String word : words) {
            trie.insert(word);
        }

        System.out.println("Total words inserted: " + trie.getTotalWordCount());
        System.out.println("Node word distribution: " + trie.getNodeWordCounts());

        for (String word : words) {
            System.out.println("Search " + word + ": " + trie.search(word));
        }

        System.out.println("\n--- Prefix Search Test ---");
        List<String> applePrefix = trie.searchWithPrefix("app");
        System.out.println("Words with prefix 'app': " + applePrefix);

        List<String> banPrefix = trie.searchWithPrefix("ban");
        System.out.println("Words with prefix 'ban': " + banPrefix);

        List<String> emptyPrefix = trie.searchWithPrefix("");
        System.out.println("All words (empty prefix): " + emptyPrefix);

        System.out.println("\n--- Wildcard Search Test ---");
        trie.insert("cat");
        trie.insert("car");
        trie.insert("card");
        trie.insert("care");
        trie.insert("careful");

        List<String> wildcard1 = trie.searchWithWildcard("ca.");
        System.out.println("Words matching 'ca.': " + wildcard1);

        List<String> wildcard2 = trie.searchWithWildcard("car.");
        System.out.println("Words matching 'car.': " + wildcard2);

        List<String> wildcard3 = trie.searchWithWildcard("c...");
        System.out.println("Words matching 'c...': " + wildcard3);

        System.out.println("\n--- Delete Operations Test ---");
        trie.delete("app");
        System.out.println("After deleting 'app':");
        System.out.println("Search app: " + trie.search("app")); // false
        System.out.println("Search apple: " + trie.search("apple")); // true
        System.out.println("Search application: " + trie.search("application")); // true

        List<String> appPrefixAfterDelete = trie.searchWithPrefix("app");
        System.out.println("Words with prefix 'app' after deletion: " + appPrefixAfterDelete);

        System.out.println("\n--- Concurrent Operations Test ---");
        DesignDistributedTrie concurrentTrie = new DesignDistributedTrie(2);

        Thread inserter1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                concurrentTrie.insert("word" + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread inserter2 = new Thread(() -> {
            for (int i = 50; i < 100; i++) {
                concurrentTrie.insert("word" + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread searcher = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(20);
                    int totalWords = concurrentTrie.getTotalWordCount();
                    System.out.println("Current word count: " + totalWords);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        inserter1.start();
        inserter2.start();
        searcher.start();

        try {
            inserter1.join();
            inserter2.join();
            searcher.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final concurrent trie word count: " + concurrentTrie.getTotalWordCount());

        System.out.println("\n--- Edge Cases Test ---");
        DesignDistributedTrie edgeTrie = new DesignDistributedTrie(1);

        // Empty string handling
        try {
            edgeTrie.insert("");
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly handled empty string insertion: " + e.getMessage());
        }

        // Null handling
        System.out.println("Search null: " + edgeTrie.search(null)); // false

        // Single character words
        edgeTrie.insert("a");
        edgeTrie.insert("b");
        System.out.println("Search 'a': " + edgeTrie.search("a")); // true
        System.out.println("Search 'ab': " + edgeTrie.search("ab")); // false

        // Overlapping words
        edgeTrie.insert("test");
        edgeTrie.insert("testing");
        edgeTrie.insert("tester");

        List<String> testPrefix = edgeTrie.searchWithPrefix("test");
        System.out.println("Words with prefix 'test': " + testPrefix);

        edgeTrie.delete("test");
        System.out.println("After deleting 'test':");
        System.out.println("Search test: " + edgeTrie.search("test")); // false
        System.out.println("Search testing: " + edgeTrie.search("testing")); // true
        System.out.println("Search tester: " + edgeTrie.search("tester")); // true

        System.out.println("\n--- System Statistics ---");
        Map<String, Object> stats = trie.getSystemStats();
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        trie.shutdown();
        concurrentTrie.shutdown();
        edgeTrie.shutdown();

        System.out.println("Distributed Trie tests completed.");
    }
}
