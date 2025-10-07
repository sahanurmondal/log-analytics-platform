# Deep Dive #20: Recommendation Engine (E-Commerce)

## 1. Problem Clarification

Design a product recommendation system combining collaborative filtering, content-based filtering, and hybrid approaches to suggest relevant products to users based on browsing history, purchase patterns, and product attributes.

**Assumptions / Scope:**
- **Scale:** 100M users, 10M products, 1B interactions (views, purchases, ratings)
- **Recommendation types:** Personalized (for you), similar items, frequently bought together, trending, new arrivals
- **Approaches:** Collaborative filtering (user-user, item-item), content-based (product attributes), hybrid (weighted combination)
- **Real-time:** Update recommendations within 5 minutes of user actions
- **Cold start:** Handle new users (no history) and new products (no interactions)
- **Performance:** Generate recommendations < 100ms p99, batch precomputation acceptable
- **Diversity:** Avoid filter bubble, balance relevance vs exploration
- **Out of scope:** Deep learning models (v2), real-time feature engineering, multi-armed bandits

**Non-Functional Goals:**
- Low latency (< 100ms for cached recommendations)
- High availability (99.9%)
- Scalable offline training (daily batch jobs)
- Explainability (why this recommendation?)
- A/B testable (multiple strategies)

---

## 2. Core Requirements

| Category | Requirement |
|----------|-------------|
| **Functional** | • Generate personalized recommendations per user<br>• Find similar products (content-based)<br>• Suggest frequently bought together items<br>• Trending/popular items for cold users<br>• New arrivals by category<br>• Explain recommendations (reason)<br>• Track click-through, conversion rates<br>• A/B test recommendation strategies<br>• Exclude already purchased/viewed items<br>• Support multiple recommendation contexts (home, PDP, cart) |
| **Non-Functional** | • Scalability: 100M users, 10M products<br>• Performance: < 100ms p99 for recommendations<br>• Freshness: Update within 5 minutes of interactions<br>• Accuracy: Click-through rate > 5%, conversion > 2%<br>• Availability: 99.9% uptime<br>• Explainability: Transparent reasons for suggestions<br>• Observability: CTR, conversion, coverage metrics |

---

## 3. Engineering Challenges

1. **Cold Start:** New users (no history) → use trending/popular; New products → leverage attributes
2. **Scalability:** Compute 100M × 10M similarity matrix (1PB) → dimensionality reduction (SVD, embeddings)
3. **Real-time vs Batch:** Balance fresh recommendations vs computational cost
4. **Sparsity:** Most users interact with < 0.01% of products → matrix factorization
5. **Diversity:** Avoid recommending only similar items → introduce randomness, serendipity
6. **Popularity Bias:** Trending items dominate → balance with long-tail products
7. **Context-aware:** Home page vs PDP recommendations differ → strategy per context
8. **Explainability:** Users want to know "why this recommendation?" → track reason
9. **A/B Testing:** Compare strategies without biasing results → deterministic user bucketing
10. **Performance:** Precompute & cache vs on-demand computation trade-off

---

## 4. Design Patterns Applied

| Concern | Pattern | Justification |
|---------|---------|---------------|
| Recommendation strategies | **Strategy** | Swap between collaborative, content-based, hybrid |
| Combining multiple signals | **Composite** | Weighted ensemble of multiple recommenders |
| Context-specific recommendations | **Strategy** | Different strategies for home, PDP, cart contexts |
| Caching recommendations | **Proxy** | Cache layer with invalidation on user actions |
| Explanation generation | **Decorator** | Add reason metadata to base recommendations |
| Model training pipeline | **Template Method** | Standard ETL → train → evaluate → deploy flow |
| Real-time updates | **Observer** | Listen to user events, invalidate cache |
| Fallback for cold start | **Chain of Responsibility** | Try personalized → similar → trending |
| Feature extraction | **Adapter** | Normalize user/product data for models |
| A/B testing assignment | **Strategy** | Bucketing users deterministically |

---

## 5. Domain Model

| Entity / Component | Responsibility |
|--------------------|----------------|
| **RecommendationService** | Orchestrate recommendation generation, caching, logging |
| **RecommendationStrategy (interface)** | Generate recommendations for user/context |
| **CollaborativeFilteringStrategy** | User-user or item-item similarity |
| **ContentBasedStrategy** | Product attribute matching |
| **HybridStrategy** | Weighted combination of multiple strategies |
| **TrendingStrategy** | Popular items (global or category-specific) |
| **FrequentlyBoughtTogetherStrategy** | Market basket analysis |
| **UserProfile (Entity)** | User preferences, browsing history, purchase history |
| **ProductVector (VO)** | Numerical representation of product attributes |
| **Interaction (Entity)** | User action (view, add-to-cart, purchase, rating) |
| **InteractionType (enum)** | VIEW, ADD_TO_CART, PURCHASE, RATING, WISHLIST |
| **Recommendation (VO)** | Product ID, score, reason |
| **RecommendationContext (enum)** | HOME, PDP, CART, SEARCH, EMAIL |
| **SimilarityIndex** | Precomputed product-product similarities |
| **ModelTrainer (Job)** | Batch job to train collaborative filtering models |
| **CacheManager** | Store precomputed recommendations with TTL |
| **MetricsCollector** | Track CTR, conversion, coverage |
| **ExplanationService** | Generate human-readable reasons |

---

## 6. UML Class Diagram (ASCII)

```
┌──────────────────────┐         ┌──────────────────────┐
│RecommendationService │────────>│RecommendationStrategy│<<interface>>
│ -strategies[]        │         │ +recommend(user,     │
│ -cache               │         │  context, n)         │
│ -metricsCollector    │         └──────────┬───────────┘
└──────────┬───────────┘                    │
           │                     ┌──────────┴──────────┐
           │                     │                     │
           v              ┌──────v────────┐    ┌──────v──────┐
┌──────────────────┐     │Collaborative  │    │ContentBased │
│ Recommendation   │     │Filtering      │    │Strategy     │
│ -productId       │     └───────────────┘    └─────────────┘
│ -score           │              │
│ -reason          │     ┌────────v─────────┐
│ -context         │     │Hybrid            │
└──────────────────┘     │Strategy          │
                         │ -strategies[]    │
                         │ -weights[]       │
                         └──────────────────┘

┌──────────────────┐         ┌──────────────────┐
│ UserProfile      │────────>│ Interaction      │
│ -userId          │         │ -userId          │
│ -preferences     │         │ -productId       │
│ -viewHistory[]   │         │ -type            │
│ -purchaseHistory[]│        │ -timestamp       │
│ -embeddings      │         │ -rating          │
└──────────────────┘         └──────────────────┘

┌──────────────────┐         ┌──────────────────┐
│ SimilarityIndex  │         │ ProductVector    │
│ -productSimilarity│────────>│ -productId       │
│  Map<String,     │         │ -features[]      │
│   List<Sim>>     │         │ -embeddings      │
└──────────────────┘         └──────────────────┘

┌──────────────────┐         ┌──────────────────┐
│ ModelTrainer     │         │ MetricsCollector │
│ +trainCF()       │         │ +trackClick()    │
│ +trainEmbeddings()│        │ +trackConversion()│
│ +evaluate()      │         │ +getCTR()        │
└──────────────────┘         └──────────────────┘
```

---

## 7. Sequence Diagram (Get Personalized Recommendations)

```
User    RecommendationService  CacheManager  Strategy  SimilarityIndex  MetricsCollector
 │              │                   │            │            │                │
 │ getRecommendations(context)      │            │            │                │
 ├─────────────>│                   │            │            │                │
 │              │ checkCache(user)  │            │            │                │
 │              ├──────────────────>│            │            │                │
 │              │<──────────────────┤            │            │                │
 │              │   cache miss      │            │            │                │
 │              │                   │            │            │                │
 │              │ recommend(user, context)       │            │                │
 │              ├────────────────────┼───────────>│            │                │
 │              │                   │            │ getSimilar(products)        │
 │              │                   │            ├───────────>│                │
 │              │                   │            │<───────────┤                │
 │              │                   │            │ similar[]  │                │
 │              │<───────────────────┼────────────┤            │                │
 │              │   recommendations  │            │            │                │
 │              │                   │            │            │                │
 │              │ putCache(user, recs, TTL=5min) │            │                │
 │              ├──────────────────>│            │            │                │
 │              │                   │            │            │                │
 │              │ addExplanations(recs)          │            │                │
 │              │────────────┐      │            │            │                │
 │              │<───────────┘      │            │            │                │
 │              │                   │            │            │                │
 │<─────────────┤                   │            │            │                │
 │ Recommendations│                 │            │            │                │
 │              │                   │            │            │                │
 │ click(productId)                 │            │            │                │
 ├─────────────>│                   │            │            │                │
 │              │ trackClick(user, product, context)          │                │
 │              ├────────────────────┼────────────┼────────────┼───────────────>│
 │              │                   │            │            │                │
```

---

## 8. Implementation (Java-like Pseudocode)

### Core Interfaces

```java
// ========== INTERVIEW-CRITICAL: Strategy pattern for pluggable algorithms ==========
interface RecommendationStrategy {
    List<Recommendation> recommend(String userId, RecommendationContext context, int topN);
    String getName();
}

// ========== INTERVIEW-CRITICAL: Collaborative filtering (item-item) ==========
class ItemItemCollaborativeFiltering implements RecommendationStrategy {
    private final SimilarityIndex similarityIndex;
    private final InteractionRepository interactionRepo;
    
    @Override
    public List<Recommendation> recommend(String userId, RecommendationContext context, int topN) {
        // Step 1: Get user's recent interactions (last 30 days)
        List<Interaction> recentInteractions = interactionRepo.findByUserSince(
            userId, 
            Instant.now().minus(Duration.ofDays(30))
        );
        
        if (recentInteractions.isEmpty()) {
            return Collections.emptyList(); // Cold start
        }
        
        // Step 2: Get similar products for each interacted item
        Map<String, Double> candidateScores = new HashMap<>();
        
        for (Interaction interaction : recentInteractions) {
            List<ProductSimilarity> similar = similarityIndex.getSimilar(
                interaction.getProductId(), 
                50
            );
            
            // Weight by interaction type (purchase > add-to-cart > view)
            double weight = getInteractionWeight(interaction.getType());
            
            for (ProductSimilarity sim : similar) {
                candidateScores.merge(
                    sim.getProductId(), 
                    sim.getSimilarity() * weight, 
                    Double::sum
                );
            }
        }
        
        // Step 3: Filter out already interacted products
        Set<String> interactedProducts = recentInteractions.stream()
            .map(Interaction::getProductId)
            .collect(Collectors.toSet());
        
        candidateScores.keySet().removeAll(interactedProducts);
        
        // Step 4: Sort by score, return top N
        return candidateScores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(topN)
            .map(e -> Recommendation.builder()
                .productId(e.getKey())
                .score(e.getValue())
                .reason("Similar to items you viewed")
                .strategy(getName())
                .build())
            .collect(Collectors.toList());
    }
    
    private double getInteractionWeight(InteractionType type) {
        return switch (type) {
            case PURCHASE -> 3.0;
            case ADD_TO_CART -> 2.0;
            case WISHLIST -> 1.5;
            case VIEW -> 1.0;
            case RATING -> 2.5;
        };
    }
}

// ========== INTERVIEW-CRITICAL: Content-based filtering ==========
class ContentBasedStrategy implements RecommendationStrategy {
    private final ProductVectorRepository vectorRepo;
    private final UserProfileRepository profileRepo;
    
    @Override
    public List<Recommendation> recommend(String userId, RecommendationContext context, int topN) {
        // Step 1: Build user preference vector from interaction history
        UserProfile profile = profileRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        double[] userVector = buildUserPreferenceVector(profile);
        
        // Step 2: Compute cosine similarity with all products
        List<ProductVector> allProducts = vectorRepo.findAll();
        List<ScoredProduct> scores = new ArrayList<>();
        
        for (ProductVector product : allProducts) {
            double similarity = cosineSimilarity(userVector, product.getFeatures());
            scores.add(new ScoredProduct(product.getProductId(), similarity));
        }
        
        // Step 3: Sort and return top N
        return scores.stream()
            .sorted(Comparator.comparingDouble(ScoredProduct::getScore).reversed())
            .limit(topN)
            .map(sp -> Recommendation.builder()
                .productId(sp.getProductId())
                .score(sp.getScore())
                .reason("Matches your interests")
                .strategy(getName())
                .build())
            .collect(Collectors.toList());
    }
    
    private double[] buildUserPreferenceVector(UserProfile profile) {
        // Average feature vectors of purchased/liked products
        List<ProductVector> interactedProducts = vectorRepo.findByIds(
            profile.getPurchaseHistory()
        );
        
        int dim = interactedProducts.get(0).getFeatures().length;
        double[] avgVector = new double[dim];
        
        for (ProductVector pv : interactedProducts) {
            for (int i = 0; i < dim; i++) {
                avgVector[i] += pv.getFeatures()[i];
            }
        }
        
        // Normalize
        for (int i = 0; i < dim; i++) {
            avgVector[i] /= interactedProducts.size();
        }
        
        return avgVector;
    }
    
    private double cosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

// ========== INTERVIEW-CRITICAL: Hybrid strategy (ensemble) ==========
class HybridStrategy implements RecommendationStrategy {
    private final List<RecommendationStrategy> strategies;
    private final Map<String, Double> weights; // strategy name -> weight
    
    @Override
    public List<Recommendation> recommend(String userId, RecommendationContext context, int topN) {
        // Step 1: Collect recommendations from all strategies
        Map<String, Double> aggregatedScores = new HashMap<>();
        
        for (RecommendationStrategy strategy : strategies) {
            List<Recommendation> recs = strategy.recommend(userId, context, topN * 2);
            double weight = weights.getOrDefault(strategy.getName(), 1.0);
            
            for (Recommendation rec : recs) {
                aggregatedScores.merge(
                    rec.getProductId(), 
                    rec.getScore() * weight, 
                    Double::sum
                );
            }
        }
        
        // Step 2: Normalize scores and sort
        double maxScore = Collections.max(aggregatedScores.values());
        
        return aggregatedScores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(topN)
            .map(e -> Recommendation.builder()
                .productId(e.getKey())
                .score(e.getValue() / maxScore) // normalize to [0,1]
                .reason("Recommended for you")
                .strategy(getName())
                .build())
            .collect(Collectors.toList());
    }
}

// ========== INTERVIEW-CRITICAL: Trending/popular fallback for cold start ==========
class TrendingStrategy implements RecommendationStrategy {
    private final ProductRepository productRepo;
    private final InteractionRepository interactionRepo;
    
    @Override
    public List<Recommendation> recommend(String userId, RecommendationContext context, int topN) {
        // Trending = most interactions in last 7 days
        Instant since = Instant.now().minus(Duration.ofDays(7));
        
        Map<String, Long> interactionCounts = interactionRepo.findSince(since).stream()
            .collect(Collectors.groupingBy(
                Interaction::getProductId, 
                Collectors.counting()
            ));
        
        return interactionCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(topN)
            .map(e -> Recommendation.builder()
                .productId(e.getKey())
                .score(e.getValue().doubleValue())
                .reason("Trending now")
                .strategy(getName())
                .build())
            .collect(Collectors.toList());
    }
}
```

### Recommendation Service

```java
// ========== INTERVIEW-CRITICAL: Facade with fallback chain ==========
@Service
class RecommendationService {
    private final Map<String, RecommendationStrategy> strategies;
    private final CacheManager cacheManager;
    private final MetricsCollector metricsCollector;
    private final ExplanationService explanationService;
    
    public List<Recommendation> getRecommendations(String userId, RecommendationContext context, int topN) {
        // Step 1: Check cache
        String cacheKey = buildCacheKey(userId, context);
        List<Recommendation> cached = cacheManager.get(cacheKey);
        if (cached != null) {
            metricsCollector.recordCacheHit(context);
            return cached;
        }
        
        metricsCollector.recordCacheMiss(context);
        
        // Step 2: Try strategies in order (fallback chain)
        List<Recommendation> recommendations = tryStrategies(userId, context, topN);
        
        // Step 3: Post-process (diversify, add explanations)
        recommendations = diversify(recommendations);
        recommendations = explanationService.addExplanations(recommendations, userId);
        
        // Step 4: Cache results
        cacheManager.put(cacheKey, recommendations, Duration.ofMinutes(5));
        
        // Step 5: Log for offline metrics
        metricsCollector.recordRecommendations(userId, context, recommendations);
        
        return recommendations;
    }
    
    private List<Recommendation> tryStrategies(String userId, RecommendationContext context, int topN) {
        // Chain of responsibility: try personalized → content-based → trending
        List<String> strategyOrder = getStrategyOrder(context);
        
        for (String strategyName : strategyOrder) {
            RecommendationStrategy strategy = strategies.get(strategyName);
            List<Recommendation> recs = strategy.recommend(userId, context, topN);
            
            if (!recs.isEmpty()) {
                return recs;
            }
        }
        
        // Final fallback: trending
        return strategies.get("trending").recommend(userId, context, topN);
    }
    
    private List<String> getStrategyOrder(RecommendationContext context) {
        return switch (context) {
            case HOME -> List.of("hybrid", "collaborative", "trending");
            case PDP -> List.of("similar", "frequentlyBoughtTogether", "collaborative");
            case CART -> List.of("frequentlyBoughtTogether", "similar");
            case SEARCH -> List.of("collaborative", "contentBased");
            case EMAIL -> List.of("hybrid", "trending");
        };
    }
    
    // ========== INTERVIEW-CRITICAL: Diversity to avoid filter bubble ==========
    private List<Recommendation> diversify(List<Recommendation> recommendations) {
        // Ensure at least 20% are from different categories than top pick
        if (recommendations.size() < 5) return recommendations;
        
        String topCategory = productRepo.findById(recommendations.get(0).getProductId())
            .map(Product::getCategory)
            .orElse(null);
        
        List<Recommendation> diverse = new ArrayList<>(recommendations.subList(0, Math.min(3, recommendations.size())));
        List<Recommendation> others = recommendations.stream()
            .skip(3)
            .filter(r -> {
                String category = productRepo.findById(r.getProductId())
                    .map(Product::getCategory)
                    .orElse(null);
                return !Objects.equals(category, topCategory);
            })
            .limit(2)
            .collect(Collectors.toList());
        
        diverse.addAll(others);
        return diverse;
    }
    
    public void trackInteraction(String userId, String productId, InteractionType type, 
                                  RecommendationContext context) {
        // Record interaction
        Interaction interaction = Interaction.builder()
            .userId(userId)
            .productId(productId)
            .type(type)
            .context(context)
            .timestamp(Instant.now())
            .build();
        
        interactionRepo.save(interaction);
        
        // Invalidate cache
        String cacheKey = buildCacheKey(userId, context);
        cacheManager.invalidate(cacheKey);
        
        // Track metrics (CTR, conversion)
        if (type == InteractionType.VIEW) {
            metricsCollector.recordClick(userId, productId, context);
        } else if (type == InteractionType.PURCHASE) {
            metricsCollector.recordConversion(userId, productId, context);
        }
    }
}
```

### Similarity Index (Offline Training)

```java
// ========== INTERVIEW-CRITICAL: Batch job for precomputing similarities ==========
@Component
class ModelTrainer {
    private final InteractionRepository interactionRepo;
    private final SimilarityIndex similarityIndex;
    
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2am
    public void trainItemItemSimilarities() {
        log.info("Starting item-item similarity training");
        
        // Step 1: Build user-item matrix
        List<Interaction> interactions = interactionRepo.findAll();
        Map<String, Map<String, Double>> userItemMatrix = buildMatrix(interactions);
        
        // Step 2: Compute item-item cosine similarities
        Set<String> allProducts = interactions.stream()
            .map(Interaction::getProductId)
            .collect(Collectors.toSet());
        
        Map<String, List<ProductSimilarity>> similarities = new ConcurrentHashMap<>();
        
        allProducts.parallelStream().forEach(productA -> {
            List<ProductSimilarity> similar = new ArrayList<>();
            
            for (String productB : allProducts) {
                if (productA.equals(productB)) continue;
                
                double similarity = computeCosineSimilarity(productA, productB, userItemMatrix);
                if (similarity > 0.3) { // Threshold to reduce storage
                    similar.add(new ProductSimilarity(productB, similarity));
                }
            }
            
            // Keep top 100 most similar
            similar.sort(Comparator.comparingDouble(ProductSimilarity::getSimilarity).reversed());
            similarities.put(productA, similar.subList(0, Math.min(100, similar.size())));
        });
        
        // Step 3: Persist to similarity index
        similarityIndex.bulkUpdate(similarities);
        
        log.info("Completed item-item similarity training: {} products", allProducts.size());
    }
    
    private Map<String, Map<String, Double>> buildMatrix(List<Interaction> interactions) {
        Map<String, Map<String, Double>> matrix = new HashMap<>();
        
        for (Interaction interaction : interactions) {
            matrix.computeIfAbsent(interaction.getUserId(), k -> new HashMap<>())
                .merge(interaction.getProductId(), getInteractionWeight(interaction.getType()), Double::sum);
        }
        
        return matrix;
    }
    
    private double computeCosineSimilarity(String productA, String productB, 
                                           Map<String, Map<String, Double>> userItemMatrix) {
        // Find users who interacted with both products
        Set<String> usersA = getUsersWhoInteracted(productA, userItemMatrix);
        Set<String> usersB = getUsersWhoInteracted(productB, userItemMatrix);
        
        Set<String> commonUsers = new HashSet<>(usersA);
        commonUsers.retainAll(usersB);
        
        if (commonUsers.isEmpty()) return 0.0;
        
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        
        for (String user : commonUsers) {
            double ratingA = userItemMatrix.get(user).getOrDefault(productA, 0.0);
            double ratingB = userItemMatrix.get(user).getOrDefault(productB, 0.0);
            dotProduct += ratingA * ratingB;
        }
        
        for (String user : usersA) {
            double rating = userItemMatrix.get(user).getOrDefault(productA, 0.0);
            normA += rating * rating;
        }
        
        for (String user : usersB) {
            double rating = userItemMatrix.get(user).getOrDefault(productB, 0.0);
            normB += rating * rating;
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
```

### Database Schema

```sql
-- ========== INTERVIEW-CRITICAL: Denormalized for fast reads ==========
CREATE TABLE user_interactions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    interaction_type VARCHAR(20) NOT NULL, -- VIEW, ADD_TO_CART, PURCHASE, RATING, WISHLIST
    context VARCHAR(20), -- HOME, PDP, CART, SEARCH, EMAIL
    rating INT,
    timestamp TIMESTAMP NOT NULL,
    
    INDEX idx_user_timestamp (user_id, timestamp DESC),
    INDEX idx_product_timestamp (product_id, timestamp DESC),
    INDEX idx_timestamp (timestamp DESC)
);

CREATE TABLE product_similarities (
    product_id VARCHAR(36) NOT NULL,
    similar_product_id VARCHAR(36) NOT NULL,
    similarity_score DECIMAL(5,4) NOT NULL,
    last_computed TIMESTAMP NOT NULL,
    
    PRIMARY KEY (product_id, similar_product_id),
    INDEX idx_similar_score (similar_product_id, similarity_score DESC)
);

CREATE TABLE user_profiles (
    user_id VARCHAR(36) PRIMARY KEY,
    preference_vector JSON, -- Serialized feature vector
    last_updated TIMESTAMP NOT NULL
);

CREATE TABLE product_vectors (
    product_id VARCHAR(36) PRIMARY KEY,
    feature_vector JSON, -- Serialized features (category, brand, price, attributes)
    embedding_vector JSON, -- Learned embeddings from model
    last_updated TIMESTAMP NOT NULL
);

CREATE TABLE recommendation_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    context VARCHAR(20) NOT NULL,
    recommended_products JSON, -- Array of product IDs
    strategy VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    
    INDEX idx_user_timestamp (user_id, timestamp DESC)
);

CREATE TABLE recommendation_metrics (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    context VARCHAR(20) NOT NULL,
    strategy VARCHAR(50) NOT NULL,
    shown_at TIMESTAMP NOT NULL,
    clicked_at TIMESTAMP,
    purchased_at TIMESTAMP,
    
    INDEX idx_product_context (product_id, context),
    INDEX idx_strategy (strategy, shown_at DESC)
);
```

---

## 9. Thread Safety Analysis

**Concurrency Model:**
- **Read-heavy:** 99% reads (get recommendations), 1% writes (track interactions)
- **Batch training:** Offline job (single-threaded per product pair)
- **Cache:** Thread-safe cache (Caffeine) with concurrent read/write
- **Similarity index:** Immutable after batch update (atomic pointer swap)
- **Metrics:** Atomic counters for CTR, conversion tracking

**Critical Sections:**
- **Batch update similarity index:** Rebuild in background, atomically swap reference
- **Cache invalidation:** Remove on interaction (race acceptable, eventual consistency)
- **Metrics aggregation:** Use atomic counters, batch flush to DB

**Race Conditions Prevented:**
- **Stale recommendations:** Accept (5-minute cache TTL acceptable)
- **Concurrent training jobs:** Exclusive lock via cron (single instance)
- **Double-counting metrics:** Idempotency key on metric records

---

## 10. Top 10 Interview Q&A

**Q1: How do you handle cold start for new users?**
**A:** Fallback hierarchy: 1) Trending/popular items (global or category), 2) New arrivals in preferred categories (if any onboarding info), 3) Diverse selection across categories. After first few interactions, switch to collaborative filtering. Hybrid approach: blend trending (80%) + random (20%) for exploration.

**Q2: How would you scale to 100M users and 10M products?**
**A:** Shard user profiles by user_id. Precompute item-item similarities offline (daily batch). Store similarities in distributed cache (Redis cluster). Use approximate nearest neighbors (ANN) for real-time content-based (FAISS, Annoy). Limit similarity index to top 100 per product. Serve recommendations from cache (5-min TTL).

**Q3: How do you balance relevance vs diversity (filter bubble)?**
**A:** Introduce serendipity: 20% random items from different categories. Use MMR (Maximal Marginal Relevance) scoring: penalize similarity to already shown items. Category diversity constraint: max 50% from same category. Temporal diversity: vary recommendations on refresh. Trade-off: slight drop in CTR but better user satisfaction long-term.

**Q4: How would you explain recommendations to users?**
**A:** Track reason at generation: "Similar to X", "Frequently bought together", "Trending in your area". Store explanation metadata with recommendation. Display inline: "Because you viewed iPhone cases". Transparency builds trust. Privacy-aware: avoid revealing sensitive inferences ("Based on your browsing" not "You seem to be pregnant").

**Q5: How do you evaluate recommendation quality?**
**A:** Offline: precision@K, recall@K, nDCG, coverage (% products recommended). Online: CTR (click-through rate), conversion rate, revenue per user. A/B test strategies: holdout group with random recommendations. Long-term: user retention, session duration, diversity of interactions. Avoid optimizing CTR alone (may hurt diversity).

**Q6: How would you implement real-time personalization?**
**A:** Stream processing: Kafka consumes interactions, updates user profile in real-time. Invalidate cache on interaction. Incremental model updates: online learning (stochastic gradient descent). Trade-off: staleness (5-min lag) vs compute cost. Critical path: cache hit = no recomputation. Background: async profile update.

**Q7: How do you handle the sparsity problem in collaborative filtering?**
**A:** Matrix factorization: decompose user-item matrix into lower-dimensional user/item factors (SVD, ALS). Embeddings: learn dense vectors (Word2Vec on interaction sequences). Implicit feedback: use views/clicks, not just ratings. Regularization: prevent overfitting on sparse data. Hybrid: blend with content-based to fill gaps.

**Q8: How would you implement frequently bought together recommendations?**
**A:** Market basket analysis: Apriori algorithm for association rules. Precompute support/confidence for product pairs. Store top 20 co-purchased products per item. Real-time: query on PDP or cart page. Filter: min support threshold (purchased together > 10 times). Update daily. Display: "Customers who bought X also bought Y".

**Q9: How do you prevent popularity bias (always recommending bestsellers)?**
**A:** Downrank popular items: apply log(popularity) instead of raw count. Boost long-tail: add bonus score for less-popular items. Contextual: trending on home page, diverse on PDP. User-specific: if user tends to explore, reduce popular weight. Evaluation: measure catalog coverage (Gini coefficient).

**Q10: How would you A/B test recommendation strategies?**
**A:** Deterministic bucketing: hash(user_id) % 100 → assign to variant. Ensure user sees same variant (stateful). Track metrics per variant: CTR, conversion, revenue. Statistical significance: t-test after min sample size (1000 impressions/variant). Duration: run for 1-2 weeks to account for day-of-week effects. Multi-armed bandits for automatic winner selection.

---

## 11. Extension Points

**Immediate Extensions:**
1. **Contextual bandits:** Learn optimal strategy per user over time
2. **Session-based:** Real-time recommendations within browsing session
3. **Cross-domain:** Use search queries to inform product recommendations
4. **Negative signals:** Downrank dismissed/hidden items
5. **Time decay:** Recent interactions weighted higher

**Advanced Features:**
1. **Deep learning:** Neural collaborative filtering, autoencoders
2. **Graph-based:** Node embeddings (Node2Vec) on user-product graph
3. **Multi-objective:** Optimize for CTR, conversion, diversity jointly
4. **Causal inference:** Estimate true causal effect of recommendations
5. **Federated learning:** Privacy-preserving recommendations

**Operational Improvements:**
1. **Feature store:** Centralized user/product features for consistency
2. **Online evaluation:** Interleaving to reduce bias in A/B tests
3. **Model monitoring:** Detect concept drift, trigger retraining
4. **Explainability:** LIME/SHAP for black-box model explanations
5. **Cost optimization:** Cache hit rate tuning, reduce similarity index size

---

## 12. Testing Strategy

**Unit Tests:**
- **Collaborative filtering:** Similarity computation correctness
- **Content-based:** Cosine similarity, vector operations
- **Hybrid:** Weighted aggregation logic
- **Diversity:** Verify category distribution in results

**Integration Tests:**
- **End-to-end:** Interaction → invalidate cache → new recommendations
- **Fallback chain:** Empty profile → trending fallback
- **A/B bucketing:** Deterministic user assignment
- **Metrics tracking:** Click → log → aggregate

**Offline Evaluation:**
- **Precision@K:** % relevant in top K (ground truth = purchases)
- **Recall@K:** % of relevant items retrieved
- **nDCG:** Discounted cumulative gain (rank-aware)
- **Coverage:** % of catalog recommended at least once

**Online Evaluation:**
- **A/B tests:** CTR, conversion, revenue per variant
- **Interleaving:** Mix results from two strategies, measure user preference
- **Counterfactual:** Estimate what would have happened with alternative strategy

**Edge Cases:**
- **New user (no interactions):** Trending fallback
- **New product (no interactions):** Content-based on attributes
- **Single interaction:** Insufficient data for collaborative → content-based
- **All recommended items out of stock:** Re-fetch with availability filter
- **User blocks certain categories:** Respect preferences

**Property-Based Tests:**
- **Score monotonicity:** Higher similarity → higher rank
- **Diversity constraint:** Max 50% same category
- **Explanation consistency:** Reason matches strategy used
- **Cache consistency:** Same request returns same results (within TTL)

---

## 13. Pitfalls & Anti-Patterns Avoided

| Anti-Pattern | How Avoided |
|--------------|-------------|
| **Computing recommendations on-demand** | Precompute similarities offline, cache results |
| **Always recommending popular items** | Diversity constraints, downrank bestsellers |
| **No cold start handling** | Fallback to trending, content-based for new users/products |
| **Ignoring context** | Context-specific strategies (home, PDP, cart) |
| **No A/B testing** | Deterministic bucketing, track metrics per variant |
| **Black box recommendations** | Add explanation metadata, transparency |
| **Optimizing CTR only** | Also track conversion, diversity, long-term engagement |
| **Stale recommendations** | Cache invalidation on interactions, 5-min TTL |
| **Unbounded similarity computation** | Limit to top 100, threshold > 0.3 |
| **No negative signals** | Track dismissals, reduce score for hidden items |

---

## 14. Complexity Analysis

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| **Get recommendations (cached)** | O(1) | O(K) | K = topN (typically 10-20) |
| **Collaborative filtering** | O(M × K) | O(K) | M = user interactions (< 100) |
| **Content-based** | O(N × D) | O(D) | N = products, D = feature dimensions (use ANN) |
| **Hybrid** | O(S × K) | O(K) | S = number of strategies (3-5) |
| **Similarity lookup** | O(log K) | O(K) | Index sorted by score |
| **Batch training (item-item)** | O(N² × U) | O(N²) | N products, U users (parallelized) |
| **Matrix factorization** | O(I × (U × K + N × K)) | O((U + N) × K) | I iterations, K factors |
| **Cache invalidation** | O(1) | O(1) | Single key removal |

**Optimizations:**
- **ANN for content-based:** FAISS index for O(log N) lookup instead of O(N)
- **Sampling:** Train on subset of interactions (stratified sampling)
- **Incremental updates:** Update user profile without full recomputation
- **Distributed training:** Spark/Dask for parallel similarity computation

---

## 15. Interview Evaluation Rubric

**Requirements Clarification (20%):**
- [ ] Identified recommendation types (personalized, similar, trending)
- [ ] Clarified scale (100M users, 10M products)
- [ ] Discussed cold start strategies
- [ ] Asked about diversity vs relevance trade-off

**System Design (30%):**
- [ ] Proposed multiple strategies (collaborative, content-based, hybrid)
- [ ] Designed fallback chain for cold start
- [ ] Offline batch training for similarities
- [ ] Context-aware recommendations (home, PDP, cart)
- [ ] Caching with invalidation strategy

**Code Quality (25%):**
- [ ] Clean strategy interfaces
- [ ] Proper use of patterns (Strategy, Composite, Chain of Responsibility)
- [ ] Efficient similarity computation (cosine, dot product)
- [ ] Diversity post-processing
- [ ] Explainability metadata

**Scalability & Performance (15%):**
- [ ] Analyzed time complexity (O(1) cached, O(M×K) collaborative)
- [ ] Precomputation strategy (offline training)
- [ ] Approximate nearest neighbors for content-based
- [ ] Distributed training (Spark)
- [ ] Cache hit rate optimization

**Edge Cases & Testing (10%):**
- [ ] Cold start (new user, new product)
- [ ] Empty interaction history
- [ ] All recommendations out of stock
- [ ] A/B testing determinism
- [ ] Comprehensive evaluation metrics (offline + online)
