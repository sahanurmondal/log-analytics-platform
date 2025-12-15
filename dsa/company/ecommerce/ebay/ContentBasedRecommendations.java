package company.ecommerce.ebay;

import java.util.*;

/**
 * Content-Based Recommendations
 *
 * Problem: Recommend products similar to items user viewed/purchased
 * Used by: "You may also like" section, product discovery, cross-selling
 *
 * Approach:
 * 1. Extract product features (category, price, tags, specs)
 * 2. Calculate similarity between products using cosine similarity
 * 3. Rank recommendations by similarity score
 * 4. Apply diversity to avoid redundant suggestions
 *
 * Algorithm: TF-IDF + Cosine Similarity
 * Time Complexity: O(k * log n) where k = features, n = products
 * Space Complexity: O(n * k) for feature vectors
 */
public class ContentBasedRecommendations {

    static class Product {
        int productId;
        String name;
        String category;
        double price;
        Set<String> tags;
        Map<String, Double> features;
        List<String> keywords;

        public Product(int id, String name, String category, double price) {
            this.productId = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.tags = new HashSet<>();
            this.features = new HashMap<>();
            this.keywords = new ArrayList<>();
        }
    }

    static class Recommendation {
        int productId;
        String productName;
        double similarityScore; // 0-100
        String reason;

        public Recommendation(int id, String name, double score, String reason) {
            this.productId = id;
            this.productName = name;
            this.similarityScore = Math.round(score * 100.0) / 100.0;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return String.format("Product %d: %s | Similarity: %.2f | Reason: %s",
                productId, productName, similarityScore, reason);
        }
    }

    private Map<Integer, Product> productCatalog;
    private Map<String, Integer> tagFrequency;

    public ContentBasedRecommendations() {
        this.productCatalog = new HashMap<>();
        this.tagFrequency = new HashMap<>();
    }

    /**
     * Add product to catalog
     * Time: O(k) where k = number of tags
     */
    public void addProduct(int productId, String name, String category, double price,
                           List<String> tags, List<String> keywords) {
        Product product = new Product(productId, name, category, price);
        product.tags.addAll(tags);
        product.keywords.addAll(keywords);

        // Extract features
        extractFeatures(product);

        productCatalog.put(productId, product);

        // Update tag frequencies
        for (String tag : tags) {
            tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
        }
    }

    /**
     * Extract and normalize features from product
     * Time: O(k)
     */
    private void extractFeatures(Product product) {
        // Feature 1: Category (1 if match, 0 if not)
        product.features.put("category_" + product.category, 1.0);

        // Feature 2: Price range (normalized)
        product.features.put("price_range", normalizePriceRange(product.price));

        // Feature 3: Tags (TF-IDF weighting)
        for (String tag : product.tags) {
            double tfidf = calculateTFIDF(tag);
            product.features.put("tag_" + tag, tfidf);
        }

        // Feature 4: Keywords
        for (String keyword : product.keywords) {
            product.features.put("keyword_" + keyword, 1.0);
        }
    }

    /**
     * Normalize price into 0-1 range
     */
    private double normalizePriceRange(double price) {
        // Assume prices range from $10 to $1000
        return Math.min(1.0, price / 1000.0);
    }

    /**
     * Calculate TF-IDF score for a tag
     * Time: O(1)
     */
    private double calculateTFIDF(String tag) {
        int frequency = tagFrequency.getOrDefault(tag, 1);
        int totalProducts = productCatalog.size();

        // IDF = log(total / frequency)
        double idf = Math.log((double) totalProducts / Math.max(1, frequency));

        // TF = 1 (tag present)
        return idf / 10.0; // Normalize
    }

    /**
     * Calculate cosine similarity between two products
     * Time: O(k) where k = number of features
     */
    public double calculateSimilarity(int productId1, int productId2) {
        if (!productCatalog.containsKey(productId1) || !productCatalog.containsKey(productId2)) {
            return 0;
        }

        Product p1 = productCatalog.get(productId1);
        Product p2 = productCatalog.get(productId2);

        // Get all unique features
        Set<String> allFeatures = new HashSet<>();
        allFeatures.addAll(p1.features.keySet());
        allFeatures.addAll(p2.features.keySet());

        double dotProduct = 0;
        double magnitude1 = 0;
        double magnitude2 = 0;

        for (String feature : allFeatures) {
            double val1 = p1.features.getOrDefault(feature, 0.0);
            double val2 = p2.features.getOrDefault(feature, 0.0);

            dotProduct += val1 * val2;
            magnitude1 += val1 * val1;
            magnitude2 += val2 * val2;
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0;
        }

        return dotProduct / (magnitude1 * magnitude2);
    }

    /**
     * Get K similar products for a given product
     * Time: O(n log k)
     */
    public List<Recommendation> getSimilarProducts(int productId, int k) {
        if (!productCatalog.containsKey(productId)) {
            return new ArrayList<>();
        }

        Product targetProduct = productCatalog.get(productId);

        // Min heap for top k
        PriorityQueue<Map.Entry<Integer, Double>> heap =
            new PriorityQueue<>((a, b) -> Double.compare(a.getValue(), b.getValue()));

        for (Map.Entry<Integer, Product> entry : productCatalog.entrySet()) {
            if (entry.getKey() == productId) continue; // Skip self

            double similarity = calculateSimilarity(productId, entry.getKey());

            heap.offer(new AbstractMap.SimpleEntry<>(entry.getKey(), similarity));
            if (heap.size() > k) {
                heap.poll();
            }
        }

        // Convert to sorted list
        List<Recommendation> recommendations = new ArrayList<>();
        while (!heap.isEmpty()) {
            Map.Entry<Integer, Double> entry = heap.poll();
            int simProductId = entry.getKey();
            double similarity = entry.getValue();
            Product simProduct = productCatalog.get(simProductId);

            String reason = generateReason(targetProduct, simProduct, similarity);
            recommendations.add(new Recommendation(simProductId, simProduct.name, similarity, reason));
        }

        Collections.reverse(recommendations);
        return recommendations;
    }

    /**
     * Generate reason for recommendation
     */
    private String generateReason(Product source, Product target, double similarity) {
        if (source.category.equals(target.category)) {
            return "Same category (" + source.category + ")";
        }

        // Find common tags
        Set<String> commonTags = new HashSet<>(source.tags);
        commonTags.retainAll(target.tags);

        if (!commonTags.isEmpty()) {
            return "Shared tags: " + commonTags.iterator().next();
        }

        double priceDiff = Math.abs(source.price - target.price);
        if (priceDiff < source.price * 0.1) {
            return "Similar price point";
        }

        return "Content similarity";
    }

    /**
     * Get personalized recommendations based on user history
     * Time: O(n log k)
     */
    public List<Recommendation> getPersonalizedRecommendations(List<Integer> userHistory, int k) {
        Map<Integer, Double> productScores = new HashMap<>();

        // For each product in user history
        for (int viewedProductId : userHistory) {
            List<Recommendation> similar = getSimilarProducts(viewedProductId, 10);

            for (Recommendation rec : similar) {
                double currentScore = productScores.getOrDefault(rec.productId, 0.0);
                productScores.put(rec.productId, currentScore + rec.similarityScore);
            }
        }

        // Sort by score and get top k
        return productScores.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(k)
            .map(e -> {
                Product p = productCatalog.get(e.getKey());
                return new Recommendation(e.getKey(), p.name, e.getValue() / userHistory.size(),
                    "Personalized based on history");
            })
            .toList();
    }

    public static void main(String[] args) {
        ContentBasedRecommendations recommender = new ContentBasedRecommendations();

        // Add products
        System.out.println("=== Adding Products ===");
        recommender.addProduct(1, "iPhone 14 Pro", "Electronics", 999,
            Arrays.asList("smartphone", "apple", "premium"),
            Arrays.asList("phone", "camera", "5g"));

        recommender.addProduct(2, "iPhone 14", "Electronics", 799,
            Arrays.asList("smartphone", "apple"),
            Arrays.asList("phone", "camera"));

        recommender.addProduct(3, "Samsung Galaxy S23", "Electronics", 899,
            Arrays.asList("smartphone", "android", "premium"),
            Arrays.asList("phone", "camera", "5g"));

        recommender.addProduct(4, "Google Pixel 7", "Electronics", 599,
            Arrays.asList("smartphone", "android"),
            Arrays.asList("phone", "camera"));

        recommender.addProduct(5, "iPad Air", "Electronics", 599,
            Arrays.asList("tablet", "apple"),
            Arrays.asList("tablet", "display"));

        System.out.println("Products added\n");

        System.out.println("=== Similar Products for iPhone 14 Pro ===");
        recommender.getSimilarProducts(1, 4).forEach(System.out::println);

        System.out.println("\n=== Similar Products for Google Pixel 7 ===");
        recommender.getSimilarProducts(4, 3).forEach(System.out::println);

        System.out.println("\n=== Personalized Recommendations ===");
        List<Integer> userHistory = Arrays.asList(1, 4); // Viewed iPhone 14 Pro and Pixel 7
        recommender.getPersonalizedRecommendations(userHistory, 3).forEach(System.out::println);
    }
}

