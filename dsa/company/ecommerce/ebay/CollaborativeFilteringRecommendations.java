package company.ecommerce.ebay;

import java.util.*;

/**
 * Collaborative Filtering Recommendations
 *
 * Problem: Recommend products based on what similar users bought
 * Used by: Personalized product suggestions, "Users who bought X also bought Y"
 *
 * Approach:
 * 1. Build user-item interaction matrix (purchases, views, ratings)
 * 2. Calculate user similarity using cosine similarity
 * 3. Find similar users
 * 4. Recommend items they bought that target user hasn't
 * 5. Rank by user similarity and item popularity
 *
 * Algorithm: User-based Collaborative Filtering
 * Time Complexity: O(m*n) for matrix building, O(k log n) for recommendations
 * Space Complexity: O(m*n) for interaction matrix
 */
public class CollaborativeFilteringRecommendations {

    static class User {
        int userId;
        String username;
        Map<Integer, Integer> purchases; // productId -> count
        Map<Object, Integer> ratings; // productId -> rating (1-5)
        Set<Integer> viewedProducts;

        public User(int id, String name) {
            this.userId = id;
            this.username = name;
            this.purchases = new HashMap<>();
            this.ratings = new HashMap<Object, Integer>();
            this.viewedProducts = new HashSet<>();
        }
    }

    static class Recommendation {
        int productId;
        int recommendedByUserId;
        String recommendationReason;
        double score;

        public Recommendation(int productId, int byUser, double score, String reason) {
            this.productId = productId;
            this.recommendedByUserId = byUser;
            this.score = Math.round(score * 100.0) / 100.0;
            this.recommendationReason = reason;
        }

        @Override
        public String toString() {
            return String.format("Product %d | Score: %.2f | Via User %d | %s",
                productId, score, recommendedByUserId, recommendationReason);
        }
    }

    private Map<Integer, User> users;
    private Map<Integer, Integer> productPopularity; // productId -> purchase count

    public CollaborativeFilteringRecommendations() {
        this.users = new HashMap<>();
        this.productPopularity = new HashMap<>();
    }

    /**
     * Register user
     * Time: O(1)
     */
    public void registerUser(int userId, String username) {
        users.put(userId, new User(userId, username));
    }

    /**
     * Record user purchase
     * Time: O(1)
     */
    public void recordPurchase(int userId, int productId) {
        if (!users.containsKey(userId)) {
            return;
        }

        User user = users.get(userId);
        user.purchases.put(productId, user.purchases.getOrDefault(productId, 0) + 1);
        productPopularity.put(productId, productPopularity.getOrDefault(productId, 0) + 1);
    }

    /**
     * Record user rating
     * Time: O(1)
     */
    public void recordRating(int userId, int productId, int rating) {
        if (!users.containsKey(userId) || rating < 1 || rating > 5) {
            return;
        }

        users.get(userId).ratings.put(productId, rating);
    }

    /**
     * Record product view
     * Time: O(1)
     */
    public void recordView(int userId, int productId) {
        if (users.containsKey(userId)) {
            users.get(userId).viewedProducts.add(productId);
        }
    }

    /**
     * Calculate similarity between two users
     * Time: O(k) where k = products in union of purchases
     */
    public double calculateUserSimilarity(int userId1, int userId2) {
        if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
            return 0;
        }

        User user1 = users.get(userId1);
        User user2 = users.get(userId2);

        // Get all products both users have interacted with
        Set<Integer> commonProducts = new HashSet<>(user1.purchases.keySet());
        commonProducts.retainAll(user2.purchases.keySet());

        if (commonProducts.isEmpty()) {
            return 0;
        }

        // Calculate cosine similarity based on ratings
        double dotProduct = 0;
        double magnitude1 = 0;
        double magnitude2 = 0;

        for (int productId : commonProducts) {
            double rating1 = user1.ratings.getOrDefault(productId, 3); // Default middle rating
            double rating2 = user2.ratings.getOrDefault(productId, 3);

            dotProduct += rating1 * rating2;
            magnitude1 += rating1 * rating1;
            magnitude2 += rating2 * rating2;
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0;
        }

        return dotProduct / (magnitude1 * magnitude2);
    }

    /**
     * Find K most similar users
     * Time: O(n log k)
     */
    public List<Integer> findSimilarUsers(int userId, int k) {
        if (!users.containsKey(userId)) {
            return new ArrayList<>();
        }

        // Min heap for top k
        PriorityQueue<Map.Entry<Integer, Double>> heap =
            new PriorityQueue<>((a, b) -> Double.compare(a.getValue(), b.getValue()));

        for (int otherId : users.keySet()) {
            if (otherId == userId) continue;

            double similarity = calculateUserSimilarity(userId, otherId);
            if (similarity > 0) {
                heap.offer(new AbstractMap.SimpleEntry<>(otherId, similarity));
                if (heap.size() > k) {
                    heap.poll();
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(heap.poll().getKey());
        }
        Collections.reverse(result);
        return result;
    }

    /**
     * Get collaborative filtering recommendations
     * Time: O(k * m log n) where k = similar users, m = products, n = results
     */
    public List<Recommendation> getCollaborativeRecommendations(int userId, int numSimilarUsers, int numRecommendations) {
        if (!users.containsKey(userId)) {
            return new ArrayList<>();
        }

        User targetUser = users.get(userId);
        List<Integer> similarUsers = findSimilarUsers(userId, numSimilarUsers);

        // Collect products recommended by similar users
        Map<Integer, Double> productScores = new HashMap<>();

        for (int similarUserId : similarUsers) {
            double similarity = calculateUserSimilarity(userId, similarUserId);
            User similarUser = users.get(similarUserId);

            // Recommend products this user bought but target user hasn't
            for (int productId : similarUser.purchases.keySet()) {
                if (!targetUser.purchases.containsKey(productId) &&
                    !targetUser.viewedProducts.contains(productId)) {

                    double rating = similarUser.ratings.getOrDefault(productId, 3);
                    double score = similarity * rating; // Weighted by similarity and rating

                    double currentScore = productScores.getOrDefault(productId, 0.0);
                    productScores.put(productId, currentScore + score);
                }
            }
        }

        // Convert to recommendation list and sort by score
        return productScores.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(numRecommendations)
            .map(e -> new Recommendation(e.getKey(), -1, e.getValue() / numSimilarUsers,
                "Recommended by " + numSimilarUsers + " similar users"))
            .toList();
    }

    /**
     * Item-Item recommendations: "Users who bought X also bought Y"
     * Time: O(m log n)
     */
    public List<Integer> getItemBasedRecommendations(int productId, int k) {
        Map<Integer, Integer> cooccurrence = new HashMap<>();

        // Find products frequently bought together
        for (User user : users.values()) {
            if (user.purchases.containsKey(productId)) {
                for (int otherProduct : user.purchases.keySet()) {
                    if (otherProduct != productId) {
                        cooccurrence.put(otherProduct, cooccurrence.getOrDefault(otherProduct, 0) + 1);
                    }
                }
            }
        }

        // Get top k
        return cooccurrence.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(k)
            .map(Map.Entry::getKey)
            .toList();
    }

    public static void main(String[] args) {
        CollaborativeFilteringRecommendations system = new CollaborativeFilteringRecommendations();

        System.out.println("=== Registering Users ===");
        system.registerUser(1, "Alice");
        system.registerUser(2, "Bob");
        system.registerUser(3, "Charlie");
        system.registerUser(4, "Diana");

        System.out.println("=== Recording Purchases ===");
        // Alice's purchases
        system.recordPurchase(1, 101); system.recordRating(1, 101, 5); // iPhone
        system.recordPurchase(1, 102); system.recordRating(1, 102, 4); // Case
        system.recordPurchase(1, 103); system.recordRating(1, 103, 5); // Charger

        // Bob's purchases (similar to Alice)
        system.recordPurchase(2, 101); system.recordRating(2, 101, 5);
        system.recordPurchase(2, 102); system.recordRating(2, 102, 4);
        system.recordPurchase(2, 104); system.recordRating(2, 104, 4); // Screen Protector

        // Charlie's purchases (different)
        system.recordPurchase(3, 201); system.recordRating(3, 201, 5); // Laptop
        system.recordPurchase(3, 202); system.recordRating(3, 202, 4);

        // Diana's purchases
        system.recordPurchase(4, 101); system.recordRating(4, 101, 4);
        system.recordPurchase(4, 104); system.recordRating(4, 104, 5);

        System.out.println("Purchases recorded\n");

        System.out.println("=== User Similarity ===");
        System.out.println("Alice vs Bob: " + String.format("%.2f", system.calculateUserSimilarity(1, 2)));
        System.out.println("Alice vs Charlie: " + String.format("%.2f", system.calculateUserSimilarity(1, 3)));
        System.out.println();

        System.out.println("=== Similar Users to Alice ===");
        system.findSimilarUsers(1, 3).forEach(id -> System.out.println("User " + id));

        System.out.println("\n=== Collaborative Recommendations for Alice ===");
        system.getCollaborativeRecommendations(1, 2, 3).forEach(System.out::println);

        System.out.println("\n=== Item-Based: Users Who Bought iPhone Also Bought ===");
        system.getItemBasedRecommendations(101, 3).forEach(id -> System.out.println("Product " + id));
    }
}

