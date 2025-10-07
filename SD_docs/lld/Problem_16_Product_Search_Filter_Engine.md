# Deep Dive #16: Product Search with Filters

## 1. Problem Clarification

Design a flexible product search engine supporting full-text search, faceted filtering (category, brand, price range, ratings, attributes), relevance scoring, and result pagination for an e-commerce platform.

**Assumptions / Scope:**
- **Scale:** 1M+ products, 10K concurrent searches/sec peak
- **Search types:** Text query (title, description, tags), faceted filters (multi-select categories, brands, price ranges, ratings, custom attributes), sorting (relevance, price, rating, newest)
- **Data:** Products have structured attributes (category tree, brand, price, ratings, stock status) + unstructured (text title/description)
- **Indexing:** In-memory inverted index for prototype; abstraction for Elasticsearch/Solr integration
- **Performance:** p99 < 100ms for typical queries; incremental filter application
- **Relevance:** TF-IDF baseline with boost factors (exact match, popularity, recency)
- **Concurrency:** Read-heavy (searches); occasional writes (product updates, stock changes)
- **Out of scope:** ML-based ranking (initial), personalization, spell-correction (v2), synonym expansion

**Non-Functional Goals:**
- O(log N) search with indexed lookups
- Thread-safe concurrent reads
- Extensible filter criteria and scoring strategies
- Cache-friendly result sets
- Graceful degradation under load

---

## 2. Core Requirements

| Category | Requirement |
|----------|-------------|
| **Functional** | • Full-text search across product fields<br>• Apply multiple filters (AND/OR combinations)<br>• Facet counts for filter options<br>• Sort by relevance, price, rating, date<br>• Paginate results with stable ordering<br>• Real-time stock filtering<br>• Support dynamic attribute filters |
| **Non-Functional** | • Performance: Search latency p99 < 100ms<br>• Consistency: Read-your-writes for critical updates<br>• Scalability: Handle 1M products, 10K QPS<br>• Extensibility: Pluggable scoring, filter strategies<br>• Observability: Query metrics, slow query logs<br>• Resilience: Fallback to cached results on failures |

---

## 3. Engineering Challenges

1. **Inverted Index Efficiency:** Balance memory vs disk, update latency vs query speed
2. **Facet Computation:** Calculate filter counts without scanning entire result set
3. **Relevance Scoring:** Combine text relevance with business signals (popularity, margin)
4. **Filter Coordination:** Handle complex filter logic (nested categories, range intersections)
5. **Concurrency:** Lock-free reads while allowing incremental index updates
6. **Cache Invalidation:** Selective invalidation on product updates without full flush
7. **Query Parsing:** Tokenization, stop words, phrase queries with performance
8. **Pagination Consistency:** Stable sort across pages despite concurrent updates

---

## 4. Design Patterns Applied

| Concern | Pattern | Justification |
|---------|---------|---------------|
| Query composition | **Composite** | Combine filters (AND/OR/NOT) into tree structure |
| Filter evaluation | **Specification** | Reusable, combinable filter predicates |
| Scoring strategies | **Strategy** | Swap relevance algorithms (TF-IDF, BM25, custom) |
| Index access abstraction | **Adapter** | Unify in-memory, Elasticsearch, Solr behind interface |
| Result transformation | **Decorator** | Add facets, highlights, suggestions to base results |
| Query parsing | **Interpreter** | Parse text query into AST for execution |
| Cache management | **Proxy** | Transparent caching layer around search service |
| Index updates | **Observer** | Notify index on product changes via events |
| Multi-stage filtering | **Chain of Responsibility** | Pipeline text → filters → scoring → pagination |
| Result aggregation | **Facade** | Simplify client interaction with complex search API |

---

## 5. Domain Model

| Entity / Component | Responsibility |
|--------------------|----------------|
| **SearchService** | Orchestrate query execution, coordinate components |
| **QueryParser** | Parse text query into structured SearchQuery |
| **SearchQuery (VO)** | Immutable query representation (terms, filters, sort, page) |
| **InvertedIndex** | Map terms → document IDs with positions, frequencies |
| **FilterCriteria (interface)** | Evaluate if product matches filter condition |
| **CompositeFilter** | AND/OR/NOT combinations of filters |
| **PriceRangeFilter** | Min/max price bounds |
| **CategoryFilter** | Match category subtree |
| **AttributeFilter** | Generic key-value attribute matching |
| **ScoringStrategy (interface)** | Compute relevance score for document |
| **TfIdfScorer** | TF-IDF scoring implementation |
| **BoostingScorer** | Apply boost factors (popularity, margin, recency) |
| **FacetAggregator** | Compute facet counts for filter options |
| **SearchResult (VO)** | Paginated results + facets + metadata |
| **ProductDocument** | Indexed product representation (fields + attributes) |
| **IndexWriter** | Update inverted index (add/update/delete documents) |
| **QueryCache** | Cache search results by query fingerprint |
| **EventListener** | React to product updates and invalidate cache/index |

---

## 6. UML Class Diagram (ASCII)

```
┌─────────────────┐         ┌──────────────────┐
│ SearchService   │────────>│ QueryParser      │
│ -index          │         │ +parse(text)     │
│ -scorer         │         └──────────────────┘
│ -facetAggregator│                │
│ -cache          │                v
└────────┬────────┘         ┌──────────────────┐
         │                  │ SearchQuery (VO) │
         │                  │ -terms           │
         │                  │ -filters         │
         │                  │ -sort            │
         │                  │ -page/size       │
         │                  └──────────────────┘
         │                           │
         v                           v
┌─────────────────┐         ┌──────────────────┐
│ InvertedIndex   │         │ FilterCriteria   │<<interface>>
│ -termMap        │         ├──────────────────┤
│ +search(terms)  │         │ +matches(doc)    │
│ +update(doc)    │         └─────────┬────────┘
└─────────────────┘                   │
         │                            │
         │                   ┌────────┴────────┐
         │                   │                 │
         │           ┌───────v──────┐  ┌──────v──────┐
         │           │CompositeFilter│  │PriceRange   │
         │           │-children      │  │-min/max     │
         │           │-operator(AND) │  └─────────────┘
         │           └──────────────┘
         v
┌─────────────────┐         ┌──────────────────┐
│ ScoringStrategy │<<interface>>│ FacetAggregator│
├─────────────────┤         │ +compute(results)│
│ +score(doc)     │         └──────────────────┘
└────────┬────────┘                   │
         │                            v
    ┌────┴─────┐              ┌──────────────────┐
    │          │              │ SearchResult (VO)│
┌───v────┐ ┌──v──────┐       │ -hits            │
│TfIdf   │ │Boosting │       │ -facets          │
│Scorer  │ │Scorer   │       │ -totalCount      │
└────────┘ └─────────┘       └──────────────────┘
```

---

## 7. Sequence Diagram (Search Flow)

```
Client          SearchService    QueryParser    InvertedIndex    FilterCriteria    ScoringStrategy    FacetAggregator
  │                   │               │                │                │                  │                  │
  │ search(text,      │               │                │                │                  │                  │
  │ filters, page)    │               │                │                │                  │                  │
  ├──────────────────>│               │                │                │                  │                  │
  │                   │ parse(text)   │                │                │                  │                  │
  │                   ├──────────────>│                │                │                  │                  │
  │                   │<──────────────┤                │                │                  │                  │
  │                   │   SearchQuery │                │                │                  │                  │
  │                   │               │                │                │                  │                  │
  │                   │ search(terms) │                │                │                  │                  │
  │                   ├───────────────┼───────────────>│                │                  │                  │
  │                   │               │   candidateIDs │                │                  │                  │
  │                   │<──────────────┼────────────────┤                │                  │                  │
  │                   │               │                │                │                  │                  │
  │                   │   for each candidate           │                │                  │                  │
  │                   │ ─────────────────────────────> │ matches(doc)   │                  │                  │
  │                   │               │                ├───────────────>│                  │                  │
  │                   │               │                │<───────────────┤                  │                  │
  │                   │               │                │   boolean      │                  │                  │
  │                   │               │                │                │                  │                  │
  │                   │   for each matched doc         │                │                  │                  │
  │                   │ ──────────────────────────────────────────────────> score(doc)     │                  │
  │                   │               │                │                │  <───────────────┤                  │
  │                   │               │                │                │      score       │                  │
  │                   │               │                │                │                  │                  │
  │                   │   sort, paginate               │                │                  │                  │
  │                   │               │                │                │                  │                  │
  │                   │ compute(results, query)        │                │                  │                  │
  │                   ├───────────────┼────────────────┼────────────────┼──────────────────┼─────────────────>│
  │                   │               │                │                │                  │   facets Map     │
  │                   │<──────────────┼────────────────┼────────────────┼──────────────────┼──────────────────┤
  │                   │               │                │                │                  │                  │
  │<──────────────────┤               │                │                │                  │                  │
  │  SearchResult     │               │                │                │                  │                  │
```

---

## 8. Implementation (Java-like Pseudocode)

### Core Interfaces

```java
// ========== INTERVIEW-CRITICAL: Composite pattern for flexible filter composition ==========
interface FilterCriteria {
    boolean matches(ProductDocument doc);
    FilterCriteria and(FilterCriteria other);
    FilterCriteria or(FilterCriteria other);
    FilterCriteria not();
}

class CompositeFilter implements FilterCriteria {
    private final List<FilterCriteria> children;
    private final Operator op; // AND, OR
    
    @Override
    public boolean matches(ProductDocument doc) {
        return switch (op) {
            case AND -> children.stream().allMatch(f -> f.matches(doc));
            case OR -> children.stream().anyMatch(f -> f.matches(doc));
        };
    }
    
    @Override
    public FilterCriteria and(FilterCriteria other) {
        return new CompositeFilter(List.of(this, other), Operator.AND);
    }
}

// ========== INTERVIEW-CRITICAL: Strategy pattern for pluggable scoring ==========
interface ScoringStrategy {
    double score(ProductDocument doc, SearchQuery query);
}

class TfIdfScorer implements ScoringStrategy {
    private final InvertedIndex index;
    
    @Override
    public double score(ProductDocument doc, SearchQuery query) {
        double score = 0.0;
        for (String term : query.getTerms()) {
            int tf = doc.getTermFrequency(term);
            double idf = index.getIdf(term);
            score += tf * idf;
        }
        return score;
    }
}

class BoostingScorer implements ScoringStrategy {
    private final ScoringStrategy baseScorer;
    private final double popularityBoost = 0.2;
    private final double marginBoost = 0.1;
    
    @Override
    public double score(ProductDocument doc, SearchQuery query) {
        double base = baseScorer.score(doc, query);
        double boost = 1.0 + 
            (doc.getPopularityScore() * popularityBoost) +
            (doc.getMarginScore() * marginBoost);
        return base * boost;
    }
}
```

### Search Service

```java
// ========== INTERVIEW-CRITICAL: Facade orchestrating search pipeline ==========
class SearchService {
    private final InvertedIndex index;
    private final ScoringStrategy scorer;
    private final FacetAggregator facetAggregator;
    private final QueryCache cache;
    private final Executor executor; // for parallel facet computation
    
    public SearchResult search(String queryText, FilterCriteria filters, 
                               SortOption sort, int page, int size) {
        // Step 1: Parse query
        SearchQuery query = QueryParser.parse(queryText);
        
        // Step 2: Check cache
        String cacheKey = buildCacheKey(query, filters, sort, page, size);
        SearchResult cached = cache.get(cacheKey);
        if (cached != null) return cached;
        
        // Step 3: Get candidate documents from inverted index
        Set<String> candidateIds = index.search(query.getTerms());
        
        // Step 4: Apply filters in parallel (partition candidates)
        List<ProductDocument> filtered = candidateIds.parallelStream()
            .map(index::getDocument)
            .filter(filters::matches)
            .collect(Collectors.toList());
        
        // Step 5: Score documents
        List<ScoredDocument> scored = filtered.stream()
            .map(doc -> new ScoredDocument(doc, scorer.score(doc, query)))
            .collect(Collectors.toList());
        
        // Step 6: Sort and paginate
        Comparator<ScoredDocument> comparator = buildComparator(sort);
        List<ScoredDocument> page = scored.stream()
            .sorted(comparator)
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
        
        // Step 7: Compute facets (async for non-blocking)
        CompletableFuture<Map<String, Facet>> facetsFuture = 
            CompletableFuture.supplyAsync(
                () -> facetAggregator.compute(filtered, query), 
                executor
            );
        
        // Step 8: Build result
        SearchResult result = SearchResult.builder()
            .hits(page)
            .totalCount(scored.size())
            .facets(facetsFuture.join()) // block on facets
            .page(page)
            .size(size)
            .build();
        
        // Step 9: Cache result
        cache.put(cacheKey, result, Duration.ofMinutes(5));
        
        return result;
    }
}
```

### Inverted Index

```java
// ========== INTERVIEW-CRITICAL: Inverted index for O(log N) term lookup ==========
class InvertedIndex {
    // term -> [docId -> TermInfo(frequency, positions)]
    private final ConcurrentMap<String, Map<String, TermInfo>> index;
    private final ConcurrentMap<String, ProductDocument> documents;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public Set<String> search(List<String> terms) {
        lock.readLock().lock();
        try {
            if (terms.isEmpty()) return documents.keySet();
            
            // Intersect posting lists for all terms (AND semantics)
            Set<String> result = new HashSet<>(index.getOrDefault(terms.get(0), Map.of()).keySet());
            for (int i = 1; i < terms.size(); i++) {
                result.retainAll(index.getOrDefault(terms.get(i), Map.of()).keySet());
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void addDocument(ProductDocument doc) {
        lock.writeLock().lock();
        try {
            documents.put(doc.getId(), doc);
            
            // Tokenize and build inverted index entries
            Map<String, Integer> termFreqs = tokenize(doc);
            for (Map.Entry<String, Integer> entry : termFreqs.entrySet()) {
                index.computeIfAbsent(entry.getKey(), k -> new ConcurrentHashMap<>())
                     .put(doc.getId(), new TermInfo(entry.getValue()));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public double getIdf(String term) {
        int docFreq = index.getOrDefault(term, Map.of()).size();
        return Math.log((double) documents.size() / (docFreq + 1));
    }
    
    private Map<String, Integer> tokenize(ProductDocument doc) {
        // Tokenize title + description, count term frequencies
        // Normalize: lowercase, remove stop words, stem
        Map<String, Integer> freqs = new HashMap<>();
        String[] tokens = (doc.getTitle() + " " + doc.getDescription())
            .toLowerCase()
            .split("\\s+");
        for (String token : tokens) {
            if (!isStopWord(token)) {
                freqs.merge(token, 1, Integer::sum);
            }
        }
        return freqs;
    }
}
```

### Facet Aggregation

```java
// ========== INTERVIEW-CRITICAL: Efficient facet counting without re-scanning ==========
class FacetAggregator {
    public Map<String, Facet> compute(List<ProductDocument> filteredDocs, SearchQuery query) {
        Map<String, Facet> facets = new HashMap<>();
        
        // Category facet (tree structure)
        Map<String, Long> categoryCounts = filteredDocs.stream()
            .map(ProductDocument::getCategory)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        facets.put("category", new Facet("category", categoryCounts));
        
        // Brand facet
        Map<String, Long> brandCounts = filteredDocs.stream()
            .map(ProductDocument::getBrand)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        facets.put("brand", new Facet("brand", brandCounts));
        
        // Price range facet (bucketed)
        Map<String, Long> priceCounts = filteredDocs.stream()
            .collect(Collectors.groupingBy(
                doc -> getPriceBucket(doc.getPrice()),
                Collectors.counting()
            ));
        facets.put("price", new Facet("price", priceCounts));
        
        // Rating facet
        Map<String, Long> ratingCounts = filteredDocs.stream()
            .collect(Collectors.groupingBy(
                doc -> String.valueOf((int) doc.getRating()),
                Collectors.counting()
            ));
        facets.put("rating", new Facet("rating", ratingCounts));
        
        return facets;
    }
    
    private String getPriceBucket(BigDecimal price) {
        // Bucket: 0-25, 25-50, 50-100, 100-200, 200+
        if (price.compareTo(new BigDecimal("25")) < 0) return "0-25";
        if (price.compareTo(new BigDecimal("50")) < 0) return "25-50";
        if (price.compareTo(new BigDecimal("100")) < 0) return "50-100";
        if (price.compareTo(new BigDecimal("200")) < 0) return "100-200";
        return "200+";
    }
}
```

### Filter Implementations

```java
// ========== INTERVIEW-CRITICAL: Specification pattern for reusable filters ==========
class PriceRangeFilter implements FilterCriteria {
    private final BigDecimal min;
    private final BigDecimal max;
    
    @Override
    public boolean matches(ProductDocument doc) {
        BigDecimal price = doc.getPrice();
        return (min == null || price.compareTo(min) >= 0) &&
               (max == null || price.compareTo(max) <= 0);
    }
}

class CategoryFilter implements FilterCriteria {
    private final String categoryPath; // e.g., "/Electronics/Laptops"
    
    @Override
    public boolean matches(ProductDocument doc) {
        // Match category subtree
        return doc.getCategory().startsWith(categoryPath);
    }
}

class AttributeFilter implements FilterCriteria {
    private final String key;
    private final Set<String> values;
    
    @Override
    public boolean matches(ProductDocument doc) {
        String attrValue = doc.getAttribute(key);
        return attrValue != null && values.contains(attrValue);
    }
}

class InStockFilter implements FilterCriteria {
    @Override
    public boolean matches(ProductDocument doc) {
        return doc.getStockQuantity() > 0;
    }
}
```

### Cache Layer

```java
// ========== INTERVIEW-CRITICAL: Proxy pattern for transparent caching ==========
class QueryCache {
    private final Cache<String, SearchResult> cache;
    
    public QueryCache() {
        this.cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .recordStats()
            .build();
    }
    
    public SearchResult get(String key) {
        return cache.getIfPresent(key);
    }
    
    public void put(String key, SearchResult result, Duration ttl) {
        cache.put(key, result);
    }
    
    public void invalidate(String productId) {
        // Invalidate all cached entries containing this product
        // Simple approach: clear all (trade-off for simplicity)
        cache.invalidateAll();
    }
}
```

### Event Listener

```java
// ========== INTERVIEW-CRITICAL: Observer pattern for index updates ==========
@Component
class ProductEventListener {
    private final InvertedIndex index;
    private final QueryCache cache;
    
    @EventListener
    public void onProductUpdated(ProductUpdatedEvent event) {
        ProductDocument doc = event.getDocument();
        
        // Update index
        index.updateDocument(doc);
        
        // Invalidate cache entries for this product
        cache.invalidate(doc.getId());
        
        log.info("Updated index for product: {}", doc.getId());
    }
    
    @EventListener
    public void onProductDeleted(ProductDeletedEvent event) {
        index.deleteDocument(event.getProductId());
        cache.invalidate(event.getProductId());
    }
}
```

---

## 9. Thread Safety Analysis

**Concurrency Model:**
- **Read-heavy workload:** Search queries (99%) vs index updates (1%)
- **InvertedIndex:** ReadWriteLock allows concurrent reads, exclusive writes
- **Immutable SearchQuery & SearchResult:** Thread-safe by design
- **Concurrent collections:** ConcurrentHashMap for term maps
- **Parallel filtering:** Stream parallelism on independent documents
- **Cache:** Caffeine library provides thread-safe operations

**Critical Sections:**
1. **Index updates:** WriteLock ensures consistency during add/update/delete
2. **Facet aggregation:** Lock-free (operates on snapshot of filtered results)
3. **Cache invalidation:** Atomic per-key operations
4. **Scoring:** Stateless strategies, no shared mutable state

**Race Conditions Prevented:**
- **Double-add prevention:** Unique document ID enforced
- **Stale reads:** ReadWriteLock guarantees happens-before for writes
- **Cache inconsistency:** Invalidate before returning from update

---

## 10. Top 10 Interview Q&A

**Q1: How do you handle updates to products without blocking searches?**
**A:** Use ReadWriteLock on InvertedIndex. Reads acquire shared lock (concurrent), writes exclusive. Updates are batched (micro-batch every 100ms) to minimize lock contention. For zero-downtime, use double-buffering: maintain two index versions, flip pointer atomically.

**Q2: How would you scale to 100M products?**
**A:** Shard inverted index by term hash (consistent hashing). Query fan-out to shards, merge results at coordinator. Use Elasticsearch/Solr for distributed indexing. Cache hot queries at edge (CDN). Partition by geography/category for locality.

**Q3: Explain TF-IDF scoring.**
**A:** TF (term frequency) = occurrences of term in document. IDF (inverse document frequency) = log(totalDocs / docsWithTerm). Rewards rare terms more than common ones. Combined: TF * IDF. High score = term is frequent in doc but rare globally.

**Q4: How do you compute facets efficiently?**
**A:** Aggregate filtered results (post-filter) using grouping. Optimization: pre-compute facet counts per filter combination (exponential space, impractical). Use approximate counting (HyperLogLog) for large cardinality. Elasticsearch uses doc_values for fast faceting.

**Q5: How would you handle typos/misspellings?**
**A:** Fuzzy matching: Levenshtein distance ≤2. Use n-grams (trigrams) for indexing. Build autocomplete trie with edit distance tolerance. Phonetic algorithms (Soundex, Metaphone). Return suggestions: "Did you mean X?"

**Q6: How to implement pagination with stable ordering?**
**A:** Include tie-breaker (document ID) in sort to ensure deterministic order. Problem: deep pagination (page 1000) requires computing all preceding pages. Solution: Cursor-based pagination (keyset) using last seen ID + score. Elasticsearch: search_after parameter.

**Q7: How do you invalidate cache on product updates?**
**A:** Selective invalidation: track productId → cacheKeys mapping (reverse index). On update, invalidate affected keys. Trade-off: overhead of tracking. Simple approach: TTL-based expiration (5 min) + invalidate all on critical updates (price change). For consistency, use versioned cache keys.

**Q8: How would you add personalization?**
**A:** Inject user context into scoring: recent views, purchase history, location. Use two-phase: retrieval (text match) → re-ranking (personalized). Model: embedding-based (user vector · product vector). Trade-off: cache effectiveness drops (per-user results).

**Q9: Explain AND vs OR filter semantics.**
**A:** AND: intersection of posting lists (retainAll). OR: union (addAll). Implementation: CompositeFilter with operator enum. Optimization: process smallest posting list first (reduce intersection set early). Use skip lists for efficient merging.

**Q10: How to handle synonym expansion?**
**A:** Build synonym map (laptop → [notebook, computer]). At query time, expand terms: "laptop" → "laptop OR notebook OR computer". Index time: index both term and synonyms (increases index size). Trade-off: false positives vs recall. Use synonym_graph token filter in Elasticsearch.

---

## 11. Extension Points

**Immediate Extensions:**
1. **Autocomplete:** Build prefix trie, return top-K completions with popularity weighting
2. **Spell correction:** Fuzzy matching, edit distance, phonetic algorithms
3. **Synonyms:** Synonym graph expansion at query/index time
4. **Highlighting:** Return matched text snippets with <mark> tags
5. **More filters:** Date ranges, multi-value attributes, geo-proximity

**Advanced Features:**
1. **ML-based ranking:** Learning-to-rank (GBDT, neural nets) with features (CTR, conversion rate)
2. **Personalization:** User embeddings, collaborative filtering, session-based
3. **A/B testing:** Variant scoring strategies, track metrics per variant
4. **Query understanding:** Intent detection (navigational vs transactional), entity extraction
5. **Federated search:** Combine product, content, help docs results

**Operational Improvements:**
1. **Index sharding:** Distribute across nodes for horizontal scaling
2. **Async indexing:** Event-driven updates via message queue (Kafka)
3. **Metrics:** Query latency, cache hit rate, index size, slow query log
4. **Relevance tuning:** A/B test boost factors, crowdsource relevance judgments

---

## 12. Testing Strategy

**Unit Tests:**
- **FilterCriteria:** Verify AND/OR/NOT logic, edge cases (empty set, single item)
- **ScoringStrategy:** TF-IDF calculation correctness, boost factor application
- **InvertedIndex:** Term lookup, document add/update/delete, concurrent access
- **QueryParser:** Tokenization, stop word removal, phrase query parsing
- **FacetAggregator:** Count accuracy, bucket boundaries

**Integration Tests:**
- **End-to-end search:** Query → filter → score → paginate → facets
- **Cache behavior:** Hit/miss, invalidation on updates
- **Concurrent searches:** Multiple threads querying simultaneously
- **Index updates:** Add/update/delete while searches in flight

**Performance Tests:**
- **Latency:** p50, p99, p99.9 under load (10K QPS)
- **Throughput:** Max QPS before degradation
- **Index size:** Memory footprint vs document count
- **Facet computation:** Cost vs filtered result set size

**Edge Cases:**
- **Empty query:** Return popular products or error
- **No results:** Suggest related queries, relax filters
- **Deep pagination:** Page 10,000 (cursor-based pagination)
- **Special characters:** Quotes, operators in query text
- **Very long queries:** 100+ terms (truncate or error)

**Property-Based Tests:**
- **Filter commutativity:** (A AND B) == (B AND A)
- **Score monotonicity:** More term matches → higher score
- **Facet consistency:** Sum of facet counts == total filtered count
- **Pagination completeness:** Union of all pages == full result set

---

## 13. Pitfalls & Anti-Patterns Avoided

| Anti-Pattern | How Avoided |
|--------------|-------------|
| **Scanning full dataset** | Inverted index provides O(log N) term lookup |
| **Blocking writes** | ReadWriteLock allows concurrent reads during updates |
| **Unbounded cache growth** | Caffeine with size limit + TTL expiration |
| **Inconsistent scoring** | Immutable SearchQuery, stateless strategies |
| **Deep pagination inefficiency** | Document cursor-based pagination for deep pages |
| **Missing cache invalidation** | Event listener invalidates on product updates |
| **Unindexed filters** | All filter fields indexed for fast access |
| **Synchronous facet computation** | Async computation with CompletableFuture |
| **Hardcoded ranking** | Strategy pattern for pluggable scoring |
| **No query validation** | Parser validates syntax, rejects malformed queries |

---

## 14. Complexity Analysis

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| **Index term lookup** | O(log T) | O(T × D) | T terms, D docs; map lookup |
| **Filter evaluation** | O(N × F) | O(1) | N candidates, F filters |
| **Scoring** | O(N × Q) | O(1) | N docs, Q query terms |
| **Sort** | O(N log N) | O(N) | Quick sort of scored docs |
| **Facet aggregation** | O(N × A) | O(A × V) | N docs, A attributes, V values |
| **Pagination** | O(1) | O(P × S) | Skip + limit on sorted list |
| **Cache lookup** | O(1) | O(C) | Hash map, C cached entries |
| **Index update** | O(T) | O(T) | T unique terms in document |
| **Overall search** | O(log T + N log N) | O(N) | Dominated by sort for large N |

**Optimizations:**
- **Early termination:** Stop scoring after top-K (heap-based top-K)
- **Skip lists:** Fast posting list intersection for AND queries
- **Bloom filters:** Quick negative lookup (term not in index)
- **Compressed posting lists:** Varint encoding reduces memory

---

## 15. Interview Evaluation Rubric

**Requirements Clarification (20%):**
- [ ] Identified search types (text, facets, filters, sort)
- [ ] Clarified scale (1M products, 10K QPS)
- [ ] Discussed relevance scoring requirements
- [ ] Asked about consistency vs availability trade-offs

**System Design (30%):**
- [ ] Proposed inverted index for text search
- [ ] Designed composite filter structure (Specification pattern)
- [ ] Explained scoring strategies (Strategy pattern)
- [ ] Modeled facet aggregation approach
- [ ] Cache layer for performance

**Code Quality (25%):**
- [ ] Clean interface boundaries (FilterCriteria, ScoringStrategy)
- [ ] Immutable value objects (SearchQuery, SearchResult)
- [ ] Thread-safe index operations (ReadWriteLock)
- [ ] Proper separation of concerns (parser, index, scorer, aggregator)
- [ ] SOLID principles applied

**Scalability & Performance (15%):**
- [ ] Analyzed time complexity (O(log T + N log N))
- [ ] Identified bottlenecks (scoring, facet computation)
- [ ] Proposed sharding strategy for scale
- [ ] Cache effectiveness discussion
- [ ] Pagination optimization (cursor-based)

**Edge Cases & Testing (10%):**
- [ ] Handled empty queries, no results scenarios
- [ ] Concurrent update + search race conditions
- [ ] Cache invalidation correctness
- [ ] Deep pagination challenges
- [ ] Comprehensive test strategy
