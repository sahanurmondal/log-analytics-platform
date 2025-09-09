package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

/**
 * Design Distributed Search Index
 *
 * Description: Design a distributed search index that supports:
 * - Document indexing across multiple shards
 * - Full-text search with ranking
 * - Real-time indexing and updates
 * - Fault tolerance and replication
 * 
 * Constraints:
 * - Support billions of documents
 * - Sub-second search response times
 * - Handle high indexing throughput
 *
 * Follow-up:
 * - How to handle index optimization?
 * - Cross-shard result aggregation?
 * 
 * Time Complexity: O(log n) for search, O(1) for indexing
 * Space Complexity: O(documents * terms)
 * 
 * Company Tags: Elasticsearch, Solr, Google Search
 */
public class DesignDistributedSearchIndex {

    class Document {
        String docId;
        String title;
        String content;
        Map<String, Object> fields;
        long timestamp;
        double score;

        Document(String docId, String title, String content) {
            this.docId = docId;
            this.title = title;
            this.content = content;
            this.fields = new HashMap<>();
            this.timestamp = System.currentTimeMillis();
            this.score = 0.0;
        }
    }

    class Term {
        String term;
        Map<String, TermStats> documentStats; // docId -> stats
        int documentFrequency; // Number of docs containing this term

        Term(String term) {
            this.term = term;
            this.documentStats = new ConcurrentHashMap<>();
            this.documentFrequency = 0;
        }

        void addDocument(String docId, int frequency, int docLength) {
            if (!documentStats.containsKey(docId)) {
                documentFrequency++;
            }
            documentStats.put(docId, new TermStats(frequency, docLength));
        }

        void removeDocument(String docId) {
            if (documentStats.remove(docId) != null) {
                documentFrequency--;
            }
        }

        double calculateTfIdf(String docId, int totalDocs) {
            TermStats stats = documentStats.get(docId);
            if (stats == null)
                return 0.0;

            double tf = 1.0 + Math.log(stats.frequency);
            double idf = Math.log((double) totalDocs / documentFrequency);

            return tf * idf;
        }
    }

    class TermStats {
        int frequency;
        int documentLength;

        TermStats(int frequency, int documentLength) {
            this.frequency = frequency;
            this.documentLength = documentLength;
        }
    }

    class SearchResult {
        String docId;
        double score;
        Map<String, Object> highlights;
        Document document;

        SearchResult(String docId, double score, Document document) {
            this.docId = docId;
            this.score = score;
            this.document = document;
            this.highlights = new HashMap<>();
        }
    }

    class IndexShard {
        String shardId;
        Map<String, Document> documents;
        Map<String, Term> invertedIndex;
        int totalDocuments;
        ReentrantReadWriteLock lock;

        IndexShard(String shardId) {
            this.shardId = shardId;
            this.documents = new ConcurrentHashMap<>();
            this.invertedIndex = new ConcurrentHashMap<>();
            this.totalDocuments = 0;
            this.lock = new ReentrantReadWriteLock();
        }

        void indexDocument(Document doc) {
            lock.writeLock().lock();
            try {
                // Remove old document if exists
                if (documents.containsKey(doc.docId)) {
                    removeDocument(doc.docId);
                }

                documents.put(doc.docId, doc);

                // Tokenize and index
                List<String> tokens = tokenize(doc.title + " " + doc.content);
                Map<String, Integer> termFrequencies = calculateTermFrequencies(tokens);

                for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
                    String term = entry.getKey();
                    int frequency = entry.getValue();

                    Term termInfo = invertedIndex.computeIfAbsent(term, Term::new);
                    termInfo.addDocument(doc.docId, frequency, tokens.size());
                }

                totalDocuments++;
            } finally {
                lock.writeLock().unlock();
            }
        }

        void removeDocument(String docId) {
            lock.writeLock().lock();
            try {
                Document doc = documents.remove(docId);
                if (doc == null)
                    return;

                // Remove from inverted index
                for (Term term : invertedIndex.values()) {
                    term.removeDocument(docId);
                }

                // Remove empty terms
                invertedIndex.entrySet().removeIf(entry -> entry.getValue().documentFrequency == 0);

                totalDocuments--;
            } finally {
                lock.writeLock().unlock();
            }
        }

        List<SearchResult> search(String query, int limit) {
            lock.readLock().lock();
            try {
                List<String> queryTerms = tokenize(query);
                Map<String, Double> docScores = new HashMap<>();

                // Calculate scores for each document
                for (String term : queryTerms) {
                    Term termInfo = invertedIndex.get(term.toLowerCase());
                    if (termInfo != null) {
                        for (String docId : termInfo.documentStats.keySet()) {
                            double tfIdf = termInfo.calculateTfIdf(docId, totalDocuments);
                            docScores.put(docId, docScores.getOrDefault(docId, 0.0) + tfIdf);
                        }
                    }
                }

                // Sort by score and return top results
                return docScores.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .limit(limit)
                        .map(entry -> new SearchResult(entry.getKey(), entry.getValue(),
                                documents.get(entry.getKey())))
                        .collect(Collectors.toList());

            } finally {
                lock.readLock().unlock();
            }
        }

        private List<String> tokenize(String text) {
            return Arrays.stream(text.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .split("\\s+"))
                    .filter(token -> !token.isEmpty() && !isStopWord(token))
                    .collect(Collectors.toList());
        }

        private Map<String, Integer> calculateTermFrequencies(List<String> tokens) {
            Map<String, Integer> frequencies = new HashMap<>();
            for (String token : tokens) {
                frequencies.put(token, frequencies.getOrDefault(token, 0) + 1);
            }
            return frequencies;
        }

        private boolean isStopWord(String word) {
            Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of",
                    "with", "by", "is", "are", "was", "were");
            return stopWords.contains(word);
        }

        Map<String, Object> getShardStats() {
            lock.readLock().lock();
            try {
                Map<String, Object> stats = new HashMap<>();
                stats.put("shardId", shardId);
                stats.put("totalDocuments", totalDocuments);
                stats.put("totalTerms", invertedIndex.size());
                stats.put("averageDocLength", documents.values().stream()
                        .mapToInt(doc -> tokenize(doc.title + " " + doc.content).size())
                        .average().orElse(0.0));
                return stats;
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    class ShardManager {
        private List<IndexShard> shards;
        private ConsistentHashRing hashRing;
        private int replicationFactor;

        ShardManager(int numShards, int replicationFactor) {
            this.shards = new ArrayList<>();
            this.hashRing = new ConsistentHashRing();
            this.replicationFactor = replicationFactor;

            // Initialize shards
            for (int i = 0; i < numShards; i++) {
                String shardId = "shard-" + i;
                IndexShard shard = new IndexShard(shardId);
                shards.add(shard);
                hashRing.addShard(shardId, shard);
            }
        }

        List<IndexShard> getShardsForDocument(String docId) {
            return hashRing.getShardsForKey(docId, replicationFactor);
        }

        List<IndexShard> getAllShards() {
            return new ArrayList<>(shards);
        }
    }

    class ConsistentHashRing {
        private TreeMap<Integer, IndexShard> ring;
        private Map<String, IndexShard> shardMap;
        private int virtualNodes;

        ConsistentHashRing() {
            this.ring = new TreeMap<>();
            this.shardMap = new HashMap<>();
            this.virtualNodes = 150;
        }

        void addShard(String shardId, IndexShard shard) {
            shardMap.put(shardId, shard);

            for (int i = 0; i < virtualNodes; i++) {
                String virtualShardId = shardId + ":" + i;
                int hash = virtualShardId.hashCode();
                ring.put(hash, shard);
            }
        }

        List<IndexShard> getShardsForKey(String key, int count) {
            if (ring.isEmpty())
                return new ArrayList<>();

            int hash = key.hashCode();
            Set<IndexShard> selectedShards = new LinkedHashSet<>();

            // Find clockwise shards
            NavigableMap<Integer, IndexShard> tailMap = ring.tailMap(hash, true);
            NavigableMap<Integer, IndexShard> headMap = ring.headMap(hash, false);

            // Add shards from tail map first
            for (IndexShard shard : tailMap.values()) {
                if (selectedShards.size() >= count)
                    break;
                selectedShards.add(shard);
            }

            // Add from head map if needed
            for (IndexShard shard : headMap.values()) {
                if (selectedShards.size() >= count)
                    break;
                selectedShards.add(shard);
            }

            return new ArrayList<>(selectedShards);
        }
    }

    private ShardManager shardManager;
    private ExecutorService indexingExecutor;
    private ExecutorService searchExecutor;

    public DesignDistributedSearchIndex(int numShards, int replicationFactor) {
        this.shardManager = new ShardManager(numShards, replicationFactor);
        this.indexingExecutor = Executors.newFixedThreadPool(10);
        this.searchExecutor = Executors.newFixedThreadPool(20);
    }

    public void indexDocument(String docId, String title, String content) {
        if (docId == null || docId.trim().isEmpty()) {
            throw new IllegalArgumentException("Document ID cannot be null or empty");
        }

        final String finalTitle = (title == null) ? "" : title;
        final String finalContent = (content == null) ? "" : content;

        indexingExecutor.submit(() -> {
            try {
                Document doc = new Document(docId, finalTitle, finalContent);
                List<IndexShard> shards = shardManager.getShardsForDocument(docId);

                for (IndexShard shard : shards) {
                    shard.indexDocument(doc);
                }
            } catch (Exception e) {
                System.err.println("Error indexing document " + docId + ": " + e.getMessage());
            }
        });
    }

    public void removeDocument(String docId) {
        if (docId == null || docId.trim().isEmpty()) {
            return; // Silently ignore invalid docId for removal
        }

        indexingExecutor.submit(() -> {
            try {
                List<IndexShard> shards = shardManager.getShardsForDocument(docId);

                for (IndexShard shard : shards) {
                    shard.removeDocument(docId);
                }
            } catch (Exception e) {
                System.err.println("Error removing document " + docId + ": " + e.getMessage());
            }
        });
    }

    public CompletableFuture<List<SearchResult>> search(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        if (limit <= 0) {
            limit = 10; // Default limit
        }

        final int finalLimit = limit;
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<CompletableFuture<List<SearchResult>>> futures = new ArrayList<>();

                // Search all shards in parallel
                for (IndexShard shard : shardManager.getAllShards()) {
                    futures.add(CompletableFuture.supplyAsync(() -> {
                        try {
                            return shard.search(query, finalLimit);
                        } catch (Exception e) {
                            System.err.println("Error searching shard " + shard.shardId + ": " + e.getMessage());
                            return new ArrayList<SearchResult>();
                        }
                    }, searchExecutor));
                }

                // Combine results from all shards with timeout
                List<SearchResult> allResults = new ArrayList<>();
                for (CompletableFuture<List<SearchResult>> future : futures) {
                    try {
                        List<SearchResult> shardResults = future.get(5, TimeUnit.SECONDS);
                        allResults.addAll(shardResults);
                    } catch (TimeoutException e) {
                        System.err.println("Shard search timed out");
                    } catch (Exception e) {
                        System.err.println("Error getting shard results: " + e.getMessage());
                    }
                }

                // Merge and deduplicate results
                Map<String, SearchResult> mergedResults = new HashMap<>();
                for (SearchResult result : allResults) {
                    if (result != null && result.docId != null) {
                        SearchResult existing = mergedResults.get(result.docId);
                        if (existing == null || result.score > existing.score) {
                            mergedResults.put(result.docId, result);
                        }
                    }
                }

                // Sort by score and return top results
                return mergedResults.values().stream()
                        .filter(result -> result != null && result.document != null)
                        .sorted((a, b) -> Double.compare(b.score, a.score))
                        .limit(finalLimit)
                        .collect(Collectors.toList());

            } catch (Exception e) {
                System.err.println("Error during search: " + e.getMessage());
                return new ArrayList<>();
            }
        }, searchExecutor);
    }

    public Map<String, Object> getIndexStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            List<IndexShard> allShards = shardManager.getAllShards();

            int totalDocs = allShards.stream()
                    .mapToInt(shard -> shard.totalDocuments)
                    .sum();

            int totalTerms = allShards.stream()
                    .mapToInt(shard -> shard.invertedIndex.size())
                    .sum();

            stats.put("totalShards", allShards.size());
            stats.put("totalDocuments", totalDocs);
            stats.put("totalTerms", totalTerms);
            stats.put("replicationFactor", shardManager.replicationFactor);

            // Per-shard stats with error handling
            List<Map<String, Object>> shardStats = new ArrayList<>();
            for (IndexShard shard : allShards) {
                try {
                    shardStats.add(shard.getShardStats());
                } catch (Exception e) {
                    System.err.println("Error getting stats for shard " + shard.shardId + ": " + e.getMessage());
                    Map<String, Object> errorStats = new HashMap<>();
                    errorStats.put("shardId", shard.shardId);
                    errorStats.put("error", "Stats unavailable");
                    shardStats.add(errorStats);
                }
            }
            stats.put("shardStats", shardStats);

            return stats;
        } catch (Exception e) {
            System.err.println("Error getting index stats: " + e.getMessage());
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "Stats unavailable");
            return errorStats;
        }
    }

    public boolean isHealthy() {
        try {
            return !indexingExecutor.isShutdown() &&
                    !searchExecutor.isShutdown() &&
                    shardManager.getAllShards().stream().allMatch(shard -> shard != null);
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForIndexing() throws InterruptedException {
        // Wait for pending indexing tasks to complete
        try {
            indexingExecutor.submit(() -> {
                // Empty task to wait for completion
            }).get();
        } catch (ExecutionException e) {
            System.err.println("Error waiting for indexing completion: " + e.getMessage());
        }
    }

    public void shutdown() {
        try {
            indexingExecutor.shutdown();
            searchExecutor.shutdown();

            try {
                if (!indexingExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Indexing executor did not terminate gracefully, forcing shutdown");
                    indexingExecutor.shutdownNow();
                }
                if (!searchExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Search executor did not terminate gracefully, forcing shutdown");
                    searchExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.println("Shutdown interrupted");
                indexingExecutor.shutdownNow();
                searchExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        DesignDistributedSearchIndex searchIndex = new DesignDistributedSearchIndex(3, 2);

        try {
            // Index some documents
            searchIndex.indexDocument("doc1", "Java Programming",
                    "Java is a popular programming language used for enterprise applications");
            searchIndex.indexDocument("doc2", "Python Guide",
                    "Python is a versatile programming language perfect for beginners");
            searchIndex.indexDocument("doc3", "Web Development",
                    "JavaScript is essential for modern web development and frontend programming");
            searchIndex.indexDocument("doc4", "Machine Learning",
                    "Python and R are popular languages for machine learning and data science");
            searchIndex.indexDocument("doc5", "Database Design",
                    "SQL databases are fundamental for storing and querying structured data");

            // Wait for indexing to complete
            Thread.sleep(2000);

            System.out.println("Index stats after indexing:");
            System.out.println(searchIndex.getIndexStats());
            System.out.println("Index healthy: " + searchIndex.isHealthy());

            // Perform searches
            System.out.println("\nSearch results for 'programming language':");
            CompletableFuture<List<SearchResult>> future1 = searchIndex.search("programming language", 5);
            List<SearchResult> results1 = future1.get();

            for (SearchResult result : results1) {
                System.out.println("Doc: " + result.docId + ", Score: " +
                        String.format("%.3f", result.score) + ", Title: " + result.document.title);
            }

            System.out.println("\nSearch results for 'python machine learning':");
            CompletableFuture<List<SearchResult>> future2 = searchIndex.search("python machine learning", 3);
            List<SearchResult> results2 = future2.get();

            for (SearchResult result : results2) {
                System.out.println("Doc: " + result.docId + ", Score: " +
                        String.format("%.3f", result.score) + ", Title: " + result.document.title);
            }

            System.out.println("\nSearch results for 'web development':");
            CompletableFuture<List<SearchResult>> future3 = searchIndex.search("web development", 3);
            List<SearchResult> results3 = future3.get();

            for (SearchResult result : results3) {
                System.out.println("Doc: " + result.docId + ", Score: " +
                        String.format("%.3f", result.score) + ", Title: " + result.document.title);
            }

            // Test edge cases
            System.out.println("\nTesting edge cases:");
            CompletableFuture<List<SearchResult>> emptySearch = searchIndex.search("", 5);
            System.out.println("Empty query results: " + emptySearch.get().size());

            CompletableFuture<List<SearchResult>> nullSearch = searchIndex.search(null, 5);
            System.out.println("Null query results: " + nullSearch.get().size());

            // Test document removal
            searchIndex.removeDocument("doc2");
            Thread.sleep(1000);

            System.out.println("\nAfter removing doc2:");
            CompletableFuture<List<SearchResult>> future4 = searchIndex.search("python", 5);
            List<SearchResult> results4 = future4.get();

            for (SearchResult result : results4) {
                System.out.println("Doc: " + result.docId + ", Score: " +
                        String.format("%.3f", result.score) + ", Title: " + result.document.title);
            }

            System.out.println("\nFinal index stats:");
            System.out.println(searchIndex.getIndexStats());

        } finally {
            searchIndex.shutdown();
        }
    }
}
