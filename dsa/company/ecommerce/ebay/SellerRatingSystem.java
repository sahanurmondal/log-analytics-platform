package company.ecommerce.ebay;

import java.util.*;

/**
 * Seller Rating System
 *
 * Problem: Find top K sellers based on ratings and provide ranking
 * Used by: Buyer recommendations, seller leaderboard, quality metrics
 *
 * Features:
 * - Rating calculation (weighted average)
 * - Top K sellers retrieval
 * - Category-wise ranking
 * - Rating history
 *
 * Algorithm: Min Heap (Priority Queue) + HashMap
 * Time Complexity: O(n log k) for top K, O(log n) for insert
 * Space Complexity: O(n)
 */
public class SellerRatingSystem {

    static class SellerRating {
        int sellerId;
        String sellerName;
        double averageRating; // 0-5 stars
        int totalReviews;
        int totalSales;
        String category;
        List<Integer> ratings;
        long lastUpdated;

        public SellerRating(int id, String name, String category) {
            this.sellerId = id;
            this.sellerName = name;
            this.category = category;
            this.averageRating = 0;
            this.totalReviews = 0;
            this.totalSales = 0;
            this.ratings = new ArrayList<>();
            this.lastUpdated = System.currentTimeMillis();
        }

        public void addRating(int rating) {
            if (rating < 1 || rating > 5) return;
            ratings.add(rating);
            totalReviews++;
            recalculateAverage();
            lastUpdated = System.currentTimeMillis();
        }

        private void recalculateAverage() {
            if (ratings.isEmpty()) {
                averageRating = 0;
                return;
            }
            double sum = ratings.stream().mapToDouble(Integer::doubleValue).sum();
            averageRating = Math.round((sum / ratings.size()) * 100.0) / 100.0;
        }

        @Override
        public String toString() {
            return String.format("Seller: %s (ID: %d) | Rating: %.2f/5.0 | Reviews: %d | Sales: %d | Category: %s",
                sellerName, sellerId, averageRating, totalReviews, totalSales, category);
        }
    }

    static class SellerRanking {
        int rank;
        int sellerId;
        String sellerName;
        double rating;
        int reviewCount;
        String badge; // GOLD, SILVER, BRONZE, STANDARD

        public SellerRanking(int rank, int id, String name, double rating, int reviews) {
            this.rank = rank;
            this.sellerId = id;
            this.sellerName = name;
            this.rating = rating;
            this.reviewCount = reviews;
            this.badge = calculateBadge(rating, reviews);
        }

        private String calculateBadge(double rating, int reviews) {
            if (rating >= 4.8 && reviews >= 100) return "GOLD";
            if (rating >= 4.5 && reviews >= 50) return "SILVER";
            if (rating >= 4.0 && reviews >= 20) return "BRONZE";
            return "STANDARD";
        }

        @Override
        public String toString() {
            return String.format("#%d | %s (%d) | Rating: %.2f | Reviews: %d | Badge: %s",
                rank, sellerName, sellerId, rating, reviewCount, badge);
        }
    }

    private Map<Integer, SellerRating> sellers;
    private Map<String, List<Integer>> categorySellerMap;

    public SellerRatingSystem() {
        this.sellers = new HashMap<>();
        this.categorySellerMap = new HashMap<>();
    }

    /**
     * Register seller
     * Time: O(1)
     */
    public void registerSeller(int sellerId, String sellerName, String category) {
        if (sellers.containsKey(sellerId)) {
            return;
        }

        SellerRating seller = new SellerRating(sellerId, sellerName, category);
        sellers.put(sellerId, seller);

        categorySellerMap.computeIfAbsent(category, k -> new ArrayList<>()).add(sellerId);
    }

    /**
     * Add rating for a seller
     * Time: O(1) amortized
     */
    public void addRating(int sellerId, int rating) {
        if (sellers.containsKey(sellerId)) {
            sellers.get(sellerId).addRating(rating);
        }
    }

    /**
     * Update sales count
     * Time: O(1)
     */
    public void recordSale(int sellerId) {
        if (sellers.containsKey(sellerId)) {
            sellers.get(sellerId).totalSales++;
        }
    }

    /**
     * Get top K sellers globally
     * Time: O(n log k)
     */
    public List<SellerRanking> getTopKSellers(int k) {
        // Min heap based on rating
        PriorityQueue<Map.Entry<Integer, SellerRating>> heap =
            new PriorityQueue<>((a, b) -> Double.compare(a.getValue().averageRating, b.getValue().averageRating));

        for (Map.Entry<Integer, SellerRating> entry : sellers.entrySet()) {
            if (entry.getValue().totalReviews > 0) { // Only include sellers with reviews
                heap.offer(entry);
                if (heap.size() > k) {
                    heap.poll();
                }
            }
        }

        List<SellerRanking> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            Map.Entry<Integer, SellerRating> entry = heap.poll();
            SellerRating seller = entry.getValue();
            result.add(new SellerRanking(0, seller.sellerId, seller.sellerName,
                seller.averageRating, seller.totalReviews));
        }

        // Reverse to get descending order and assign ranks
        Collections.reverse(result);
        for (int i = 0; i < result.size(); i++) {
            result.get(i).rank = i + 1;
        }

        return result;
    }

    /**
     * Get top K sellers in category
     * Time: O(m log k) where m is sellers in category
     */
    public List<SellerRanking> getTopKSellersByCategory(String category, int k) {
        List<Integer> categorySellerIds = categorySellerMap.getOrDefault(category, new ArrayList<>());

        PriorityQueue<Map.Entry<Integer, SellerRating>> heap =
            new PriorityQueue<>((a, b) -> Double.compare(a.getValue().averageRating, b.getValue().averageRating));

        for (int sellerId : categorySellerIds) {
            SellerRating seller = sellers.get(sellerId);
            if (seller.totalReviews > 0) {
                heap.offer(new AbstractMap.SimpleEntry<>(sellerId, seller));
                if (heap.size() > k) {
                    heap.poll();
                }
            }
        }

        List<SellerRanking> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            Map.Entry<Integer, SellerRating> entry = heap.poll();
            SellerRating seller = entry.getValue();
            result.add(new SellerRanking(0, seller.sellerId, seller.sellerName,
                seller.averageRating, seller.totalReviews));
        }

        Collections.reverse(result);
        for (int i = 0; i < result.size(); i++) {
            result.get(i).rank = i + 1;
        }

        return result;
    }

    /**
     * Get seller rating
     * Time: O(1)
     */
    public double getSellerRating(int sellerId) {
        if (sellers.containsKey(sellerId)) {
            return sellers.get(sellerId).averageRating;
        }
        return 0;
    }

    /**
     * Get seller details
     * Time: O(1)
     */
    public String getSellerDetails(int sellerId) {
        if (sellers.containsKey(sellerId)) {
            return sellers.get(sellerId).toString();
        }
        return "Seller not found";
    }

    /**
     * Get all sellers sorted by rating
     * Time: O(n log n)
     */
    public List<SellerRating> getAllSellersSorted() {
        return sellers.values().stream()
            .filter(s -> s.totalReviews > 0)
            .sorted((a, b) -> Double.compare(b.averageRating, a.averageRating))
            .toList();
    }

    public static void main(String[] args) {
        SellerRatingSystem system = new SellerRatingSystem();

        // Register sellers
        System.out.println("=== Registering Sellers ===");
        system.registerSeller(101, "TechStore", "Electronics");
        system.registerSeller(102, "ElectroHub", "Electronics");
        system.registerSeller(103, "GadgetWorld", "Electronics");
        system.registerSeller(201, "BookNook", "Books");
        system.registerSeller(202, "PageTurner", "Books");
        system.registerSeller(301, "FashionGear", "Clothing");

        System.out.println("Sellers registered\n");

        // Add ratings
        System.out.println("=== Adding Ratings ===");
        for (int i = 0; i < 50; i++) {
            system.addRating(101, (i % 5) + 1);
            system.addRating(102, ((i + 2) % 5) + 1);
            system.addRating(103, ((i + 1) % 5) + 1);
            system.addRating(201, ((i + 3) % 5) + 1);
            system.addRating(202, ((i + 4) % 5) + 1);
        }

        // Record sales
        for (int i = 0; i < 150; i++) {
            system.recordSale(101);
            system.recordSale(102);
        }

        System.out.println("Ratings and sales recorded\n");

        System.out.println("=== Seller Details ===");
        System.out.println(system.getSellerDetails(101));
        System.out.println(system.getSellerDetails(102));
        System.out.println();

        System.out.println("=== Top 3 Sellers (Global) ===");
        system.getTopKSellers(3).forEach(System.out::println);

        System.out.println("\n=== Top 2 Electronics Sellers ===");
        system.getTopKSellersByCategory("Electronics", 2).forEach(System.out::println);

        System.out.println("\n=== All Sellers (Sorted) ===");
        system.getAllSellersSorted().forEach(System.out::println);
    }
}

