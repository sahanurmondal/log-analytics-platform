package design.hard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * LeetCode 1642: Design Recommendation Engine
 * https://leetcode.com/problems/design-recommendation-engine/
 *
 * Description: Design a recommendation engine that provides recommendations
 * based on collaborative filtering, content-based filtering, and trending
 * items.
 * 
 * Constraints:
 * - 0 <= userId, itemId <= 10^9
 * - At most 10^4 users and items will be added
 * - At most 10^5 calls will be made to recommend, addUser, addItem, rateItem
 *
 * Follow-up:
 * - Can you come up with an efficient way to combine multiple recommendation
 * signals?
 * 
 * Time Complexity: O(log n) expected for add, rate; O(1) for get
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook
 */
public class DesignRecommendationEngine {

    enum RecommendationType {
        COLLABORATIVE_FILTERING,
        CONTENT_BASED,
        HYBRID,
        TRENDING,
        PERSONALIZED
    }

    class User {
        String userId;
        Map<String, String> profile;
        Map<String, Double> itemRatings;
        Set<String> categories;

        User(String userId, Map<String, String> profile) {
            this.userId = userId;
            this.profile = profile;
            this.itemRatings = new HashMap<>();
            this.categories = new HashSet<>();
        }

        void addItemRating(String itemId, double rating) {
            itemRatings.put(itemId, rating);
        }

        void addCategory(String category) {
            categories.add(category);
        }

        double getRating(String itemId) {
            return itemRatings.getOrDefault(itemId, 0.0);
        }
    }

    class Item {
        String itemId;
        String name;
        String category;
        Map<String, String> features;
        double averageRating;
        long createdAt;

        Item(String itemId, String name, String category, Map<String, String> features) {
            this.itemId = itemId;
            this.name = name;
            this.category = category;
            this.features = features;
            this.averageRating = 0.0;
            this.createdAt = System.currentTimeMillis();
        }

        void updateRating(double rating) {
            // Update item rating logic
        }

        double getTrendingScore() {
            // Calculate trending score based on views, likes, etc.
            return Math.random(); // Placeholder
        }
    }

    class Recommendation {
        String itemId;
        double score;
        String reason;
        RecommendationType type;

        Recommendation(String itemId, double score, String reason, RecommendationType type) {
            this.itemId = itemId;
            this.score = score;
            this.reason = reason;
            this.type = type;
        }
    }

    private Map<String, User> users;
    private Map<String, Item> items;
    private Map<String, Set<String>> userSimilarityCache;
    private Map<String, Set<String>> itemSimilarityCache;

    public DesignRecommendationEngine() {
        users = new HashMap<>();
        items = new HashMap<>();
        userSimilarityCache = new HashMap<>();
        itemSimilarityCache = new HashMap<>();
    }

    public void addUser(String userId, Map<String, String> profile) {
        users.put(userId, new User(userId, profile));
    }

    public void addItem(String itemId, String name, String category, Map<String, String> features) {
        items.put(itemId, new Item(itemId, name, category, features));
    }

    public void rateItem(String userId, String itemId, double rating) {
        User user = users.get(userId);
        Item item = items.get(itemId);

        if (user != null && item != null) {
            user.addItemRating(itemId, rating);
            item.updateRating(rating);

            // Invalidate similarity caches
            userSimilarityCache.remove(userId);
            itemSimilarityCache.remove(itemId);
        }
    }

    public List<Recommendation> getRecommendations(String userId, int count, RecommendationType type) {
        User user = users.get(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        switch (type) {
            case COLLABORATIVE_FILTERING:
                return getCollaborativeRecommendations(user, count);
            case CONTENT_BASED:
                return getContentBasedRecommendations(user, count);
            case HYBRID:
                return getHybridRecommendations(user, count);
            case TRENDING:
                return getTrendingRecommendations(count);
            case PERSONALIZED:
                return getPersonalizedRecommendations(user, count);
            default:
                return getHybridRecommendations(user, count);
        }
    }

    private List<Recommendation> getCollaborativeRecommendations(User user, int count) {
        List<String> similarUsers = findSimilarUsers(user.userId, 10);
        Map<String, Double> itemScores = new HashMap<>();

        for (String similarUserId : similarUsers) {
            User similarUser = users.get(similarUserId);
            double similarity = calculateUserSimilarity(user, similarUser);

            for (Map.Entry<String, Double> entry : similarUser.itemRatings.entrySet()) {
                String itemId = entry.getKey();
                if (!user.itemRatings.containsKey(itemId)) {
                    double score = similarity * entry.getValue();
                    itemScores.put(itemId, itemScores.getOrDefault(itemId, 0.0) + score);
                }
            }
        }

        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(count)
                .map(entry -> new Recommendation(
                        entry.getKey(),
                        entry.getValue(),
                        "Users like you also liked this",
                        RecommendationType.COLLABORATIVE_FILTERING))
                .collect(Collectors.toList());
    }

    private List<Recommendation> getContentBasedRecommendations(User user, int count) {
        Map<String, Double> itemScores = new HashMap<>();

        // Score items based on user's preferred categories and features
        for (Item item : items.values()) {
            if (!user.itemRatings.containsKey(item.itemId)) {
                double score = 0.0;

                // Category preference
                if (user.categories.contains(item.category)) {
                    score += 2.0;
                }

                // Feature similarity
                score += calculateFeatureSimilarity(user, item);

                // Item quality
                score += item.averageRating * 0.5;

                itemScores.put(item.itemId, score);
            }
        }

        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(count)
                .map(entry -> new Recommendation(
                        entry.getKey(),
                        entry.getValue(),
                        "Based on your preferences",
                        RecommendationType.CONTENT_BASED))
                .collect(Collectors.toList());
    }

    private List<Recommendation> getHybridRecommendations(User user, int count) {
        List<Recommendation> collaborative = getCollaborativeRecommendations(user, count * 2);
        List<Recommendation> contentBased = getContentBasedRecommendations(user, count * 2);

        Map<String, Double> hybridScores = new HashMap<>();

        // Combine scores with weights
        double collabWeight = 0.6;
        double contentWeight = 0.4;

        for (Recommendation rec : collaborative) {
            hybridScores.put(rec.itemId, rec.score * collabWeight);
        }

        for (Recommendation rec : contentBased) {
            hybridScores.put(rec.itemId,
                    hybridScores.getOrDefault(rec.itemId, 0.0) + rec.score * contentWeight);
        }

        return hybridScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(count)
                .map(entry -> new Recommendation(
                        entry.getKey(),
                        entry.getValue(),
                        "Recommended for you",
                        RecommendationType.HYBRID))
                .collect(Collectors.toList());
    }

    private List<Recommendation> getTrendingRecommendations(int count) {
        return items.values().stream()
                .sorted((a, b) -> Double.compare(b.getTrendingScore(), a.getTrendingScore()))
                .limit(count)
                .map(item -> new Recommendation(
                        item.itemId,
                        item.getTrendingScore(),
                        "Trending now",
                        RecommendationType.TRENDING))
                .collect(Collectors.toList());
    }

    private List<Recommendation> getPersonalizedRecommendations(User user, int count) {
        // Combine multiple signals for personalization
        List<Recommendation> recommendations = new ArrayList<>();

        // 50% hybrid recommendations
        recommendations.addAll(getHybridRecommendations(user, count / 2));

        // 30% trending in user's categories
        recommendations.addAll(getTrendingInCategories(user, count * 3 / 10));

        // 20% cold start items (new items)
        recommendations.addAll(getColdStartRecommendations(user, count / 5));

        return recommendations.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private List<Recommendation> getTrendingInCategories(User user, int count) {
        return items.values().stream()
                .filter(item -> user.categories.contains(item.category))
                .sorted((a, b) -> Double.compare(b.getTrendingScore(), a.getTrendingScore()))
                .limit(count)
                .map(item -> new Recommendation(
                        item.itemId,
                        item.getTrendingScore(),
                        "Trending in " + item.category,
                        RecommendationType.TRENDING))
                .collect(Collectors.toList());
    }

    private List<Recommendation> getColdStartRecommendations(User user, int count) {
        long oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);

        return items.values().stream()
                .filter(item -> item.createdAt > oneWeekAgo)
                .filter(item -> !user.itemRatings.containsKey(item.itemId))
                .sorted((a, b) -> Double.compare(b.averageRating, a.averageRating))
                .limit(count)
                .map(item -> new Recommendation(
                        item.itemId,
                        item.averageRating,
                        "New release",
                        RecommendationType.CONTENT_BASED))
                .collect(Collectors.toList());
    }

    private List<String> findSimilarUsers(String userId, int count) {
        if (userSimilarityCache.containsKey(userId)) {
            return new ArrayList<>(userSimilarityCache.get(userId));
        }

        User targetUser = users.get(userId);
        Map<String, Double> similarities = new HashMap<>();

        for (User otherUser : users.values()) {
            if (!otherUser.userId.equals(userId)) {
                double similarity = calculateUserSimilarity(targetUser, otherUser);
                if (similarity > 0.1) { // Threshold for similarity
                    similarities.put(otherUser.userId, similarity);
                }
            }
        }

        List<String> similarUsers = similarities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        userSimilarityCache.put(userId, new HashSet<>(similarUsers));
        return similarUsers;
    }

    private double calculateUserSimilarity(User user1, User user2) {
        Set<String> commonItems = new HashSet<>(user1.itemRatings.keySet());
        commonItems.retainAll(user2.itemRatings.keySet());

        if (commonItems.isEmpty()) {
            return 0.0;
        }

        // Pearson correlation coefficient
        double sum1 = 0, sum2 = 0, sum1Sq = 0, sum2Sq = 0, pSum = 0;

        for (String itemId : commonItems) {
            double rating1 = user1.getRating(itemId);
            double rating2 = user2.getRating(itemId);

            sum1 += rating1;
            sum2 += rating2;
            sum1Sq += rating1 * rating1;
            sum2Sq += rating2 * rating2;
            pSum += rating1 * rating2;
        }

        double num = pSum - (sum1 * sum2 / commonItems.size());
        double den = Math.sqrt((sum1Sq - sum1 * sum1 / commonItems.size()) *
                (sum2Sq - sum2 * sum2 / commonItems.size()));

        return den == 0 ? 0 : num / den;
    }

    private double calculateFeatureSimilarity(User user, Item item) {
        double similarity = 0.0;

        // Check profile matches
        for (Map.Entry<String, String> profileEntry : user.profile.entrySet()) {
            String feature = profileEntry.getKey();
            String value = profileEntry.getValue();

            if (item.features.containsKey(feature) &&
                    item.features.get(feature).equals(value)) {
                similarity += 1.0;
            }
        }

        return similarity;
    }

    public static void main(String[] args) {
        DesignRecommendationEngine engine = new DesignRecommendationEngine();

        // Add users
        Map<String, String> profile1 = Map.of("age", "25", "genre_preference", "action");
        engine.addUser("user1", profile1);
        engine.addUser("user2", Map.of("age", "30", "genre_preference", "comedy"));
        engine.addUser("user3", Map.of("age", "25", "genre_preference", "action"));

        // Add items
        engine.addItem("movie1", "Action Movie", "movies", Map.of("genre", "action"));
        engine.addItem("movie2", "Comedy Movie", "movies", Map.of("genre", "comedy"));
        engine.addItem("movie3", "Drama Movie", "movies", Map.of("genre", "drama"));
        engine.addItem("movie4", "Action Thriller", "movies", Map.of("genre", "action"));

        // Add ratings
        engine.rateItem("user1", "movie1", 5.0);
        engine.rateItem("user1", "movie2", 2.0);
        engine.rateItem("user2", "movie2", 5.0);
        engine.rateItem("user2", "movie3", 4.0);
        engine.rateItem("user3", "movie1", 4.5);
        engine.rateItem("user3", "movie4", 5.0);

        // Get recommendations
        System.out.println("Collaborative Filtering Recommendations for user1:");
        List<Recommendation> collabRecs = engine.getRecommendations("user1", 3,
                RecommendationType.COLLABORATIVE_FILTERING);
        for (Recommendation rec : collabRecs) {
            System.out
                    .println("- " + rec.itemId + " (score: " + String.format("%.2f", rec.score) + ") - " + rec.reason);
        }

        System.out.println("\nContent-Based Recommendations for user1:");
        List<Recommendation> contentRecs = engine.getRecommendations("user1", 3, RecommendationType.CONTENT_BASED);
        for (Recommendation rec : contentRecs) {
            System.out
                    .println("- " + rec.itemId + " (score: " + String.format("%.2f", rec.score) + ") - " + rec.reason);
        }

        System.out.println("\nHybrid Recommendations for user1:");
        List<Recommendation> hybridRecs = engine.getRecommendations("user1", 3, RecommendationType.HYBRID);
        for (Recommendation rec : hybridRecs) {
            System.out
                    .println("- " + rec.itemId + " (score: " + String.format("%.2f", rec.score) + ") - " + rec.reason);
        }

        System.out.println("\nTrending Recommendations:");
        List<Recommendation> trendingRecs = engine.getRecommendations("user1", 3, RecommendationType.TRENDING);
        for (Recommendation rec : trendingRecs) {
            System.out
                    .println("- " + rec.itemId + " (score: " + String.format("%.2f", rec.score) + ") - " + rec.reason);
        }
    }
}