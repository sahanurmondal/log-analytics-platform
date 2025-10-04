package company.banking.kotakbank.missing;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Banking Transaction Categorization System
 * 
 * Problem: Given a transaction description, categorize it into predefined
 * categories
 * to help customers and banks track spending patterns and generate insights.
 * 
 * Categories:
 * - FOOD_DINING: Restaurants, cafes, food delivery
 * - SHOPPING: E-commerce, retail stores, shopping
 * - TRANSPORT: Taxi, metro, fuel, parking
 * - ENTERTAINMENT: Movies, games, subscriptions
 * - HEALTHCARE: Hospitals, pharmacies, medical
 * - UTILITIES: Electricity, water, gas, internet
 * - INCOME: Salary, bonus, interest credit
 * - TRANSFER: Account transfers, UPI, NEFT
 * - ATM_CASH: ATM withdrawals, cash deposits
 * - INVESTMENT: Mutual funds, stocks, FD
 * - UNKNOWN: Unrecognized transactions
 * 
 * Input: String description - Transaction description from bank statement
 * Output: String category - Predicted category
 * 
 * Example:
 * Input: "SWIGGY FOOD DELIVERY BANGALORE"
 * Output: "FOOD_DINING"
 * 
 * Input: "AMAZON PAY SHOPPING ELECTRONICS"
 * Output: "SHOPPING"
 */
public class TransactionCategorization {

    // Category constants
    public static final String FOOD_DINING = "FOOD_DINING";
    public static final String SHOPPING = "SHOPPING";
    public static final String TRANSPORT = "TRANSPORT";
    public static final String ENTERTAINMENT = "ENTERTAINMENT";
    public static final String HEALTHCARE = "HEALTHCARE";
    public static final String UTILITIES = "UTILITIES";
    public static final String INCOME = "INCOME";
    public static final String TRANSFER = "TRANSFER";
    public static final String ATM_CASH = "ATM_CASH";
    public static final String INVESTMENT = "INVESTMENT";
    public static final String UNKNOWN = "UNKNOWN";

    // Pattern-based categorization maps
    private static final Map<String, List<Pattern>> CATEGORY_PATTERNS = new HashMap<>();

    static {
        // Food & Dining patterns
        CATEGORY_PATTERNS.put(FOOD_DINING, Arrays.asList(
                Pattern.compile(
                        ".*\\b(swiggy|zomato|uber\\s*eats|dominos|mcdonald|kfc|restaurant|cafe|food|dining|pizza|burger)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(starbucks|subway|dunkin|baskin|hotel|dhaba|canteen)\\b.*",
                        Pattern.CASE_INSENSITIVE)));

        // Shopping patterns
        CATEGORY_PATTERNS.put(SHOPPING, Arrays.asList(
                Pattern.compile(".*\\b(amazon|flipkart|myntra|ajio|shopping|retail|mall|store|market)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(grocery|supermarket|bigbasket|grofers|reliance|dmart|clothing|electronics)\\b.*",
                        Pattern.CASE_INSENSITIVE)));

        // Transport patterns
        CATEGORY_PATTERNS.put(TRANSPORT, Arrays.asList(
                Pattern.compile(".*\\b(uber|ola|taxi|metro|bus|auto|rickshaw|fuel|petrol|diesel|parking)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(transport|travel|railway|irctc|airline|flight|booking)\\b.*",
                        Pattern.CASE_INSENSITIVE)));

        // Entertainment patterns
        CATEGORY_PATTERNS.put(ENTERTAINMENT, Arrays.asList(
                Pattern.compile(
                        ".*\\b(netflix|amazon\\s*prime|hotstar|spotify|youtube|entertainment|movie|cinema)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(gaming|games|subscription|streaming|music|video|pvr|inox)\\b.*",
                        Pattern.CASE_INSENSITIVE)));

        // Healthcare patterns
        CATEGORY_PATTERNS.put(HEALTHCARE, Arrays.asList(
                Pattern.compile(".*\\b(hospital|clinic|pharmacy|medical|doctor|medicine|health|apollo|fortis)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(diagnostic|lab|insurance|mediclaim|treatment|consultation)\\b.*",
                        Pattern.CASE_INSENSITIVE)));

        // Utilities patterns
        CATEGORY_PATTERNS.put(UTILITIES, Arrays.asList(
                Pattern.compile(".*\\b(electricity|water|gas|internet|broadband|wifi|telecom|mobile|phone)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(utility|bill|payment|recharge|prepaid|postpaid|airtel|jio|vi)\\b.*",
                        Pattern.CASE_INSENSITIVE)));

        // Income patterns
        CATEGORY_PATTERNS.put(INCOME, Arrays.asList(
                Pattern.compile(".*\\b(salary|wage|bonus|incentive|commission|interest|dividend|refund)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(credit|deposit|earning|income|cash\\s*back|reward|cashback)\\b.*",
                        Pattern.CASE_INSENSITIVE)));

        // Transfer patterns
        CATEGORY_PATTERNS.put(TRANSFER, Arrays.asList(
                Pattern.compile(".*\\b(transfer|upi|neft|rtgs|imps|paytm|phonepe|gpay|bhim)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(wallet|payment|send|receive|p2p|peer)\\b.*", Pattern.CASE_INSENSITIVE)));

        // ATM/Cash patterns
        CATEGORY_PATTERNS.put(ATM_CASH, Arrays.asList(
                Pattern.compile(".*\\b(atm|cash|withdrawal|deposit|branch|counter)\\b.*", Pattern.CASE_INSENSITIVE)));

        // Investment patterns
        CATEGORY_PATTERNS.put(INVESTMENT, Arrays.asList(
                Pattern.compile(".*\\b(mutual\\s*fund|sip|investment|equity|debt|fd|fixed\\s*deposit|rd)\\b.*",
                        Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(stock|share|trading|portfolio|zerodha|upstox|angel|groww)\\b.*",
                        Pattern.CASE_INSENSITIVE)));
    }

    /**
     * Basic categorization using rule-based pattern matching
     */
    public static String categorize(String description) {
        if (description == null || description.trim().isEmpty()) {
            return UNKNOWN;
        }

        String cleanDescription = description.trim();

        // Check each category's patterns
        for (Map.Entry<String, List<Pattern>> entry : CATEGORY_PATTERNS.entrySet()) {
            for (Pattern pattern : entry.getValue()) {
                if (pattern.matcher(cleanDescription).matches()) {
                    return entry.getKey();
                }
            }
        }

        return UNKNOWN;
    }

    /**
     * Enhanced categorization with confidence scoring
     */
    public static CategoryResult categorizeWithConfidence(String description) {
        if (description == null || description.trim().isEmpty()) {
            return new CategoryResult(UNKNOWN, 0.0);
        }

        String cleanDescription = description.trim().toLowerCase();
        Map<String, Double> categoryScores = new HashMap<>();

        // Calculate scores for each category
        for (Map.Entry<String, List<Pattern>> entry : CATEGORY_PATTERNS.entrySet()) {
            String category = entry.getKey();
            double score = 0.0;

            for (Pattern pattern : entry.getValue()) {
                if (pattern.matcher(description).matches()) {
                    score += 1.0;
                }
            }

            // Bonus for exact keyword matches
            score += countKeywordMatches(cleanDescription, category) * 0.5;

            if (score > 0) {
                categoryScores.put(category, score);
            }
        }

        if (categoryScores.isEmpty()) {
            return new CategoryResult(UNKNOWN, 0.0);
        }

        // Find category with highest score
        String bestCategory = categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(UNKNOWN);

        double confidence = Math.min(1.0, categoryScores.get(bestCategory) / 3.0);
        return new CategoryResult(bestCategory, confidence);
    }

    private static int countKeywordMatches(String description, String category) {
        String[] keywords = getKeywordsForCategory(category);
        int count = 0;
        for (String keyword : keywords) {
            if (description.contains(keyword.toLowerCase())) {
                count++;
            }
        }
        return count;
    }

    private static String[] getKeywordsForCategory(String category) {
        switch (category) {
            case FOOD_DINING:
                return new String[] { "food", "eat", "meal", "drink", "restaurant" };
            case SHOPPING:
                return new String[] { "buy", "purchase", "order", "shop", "product" };
            case TRANSPORT:
                return new String[] { "ride", "travel", "trip", "journey", "commute" };
            case ENTERTAINMENT:
                return new String[] { "watch", "play", "fun", "enjoy", "leisure" };
            case HEALTHCARE:
                return new String[] { "health", "medical", "cure", "treatment", "wellness" };
            case UTILITIES:
                return new String[] { "bill", "service", "utility", "connection", "monthly" };
            case INCOME:
                return new String[] { "earn", "receive", "get", "gain", "profit" };
            case TRANSFER:
                return new String[] { "send", "transfer", "pay", "remit", "move" };
            case ATM_CASH:
                return new String[] { "cash", "withdraw", "deposit", "money", "atm" };
            case INVESTMENT:
                return new String[] { "invest", "save", "grow", "fund", "return" };
            default:
                return new String[] {};
        }
    }

    /**
     * Follow-up 1: Batch categorization for multiple transactions
     */
    public static Map<String, String> batchCategorize(List<String> descriptions) {
        Map<String, String> results = new HashMap<>();
        for (String desc : descriptions) {
            results.put(desc, categorize(desc));
        }
        return results;
    }

    /**
     * Follow-up 2: Category statistics and insights
     */
    public static Map<String, Integer> getCategoryStatistics(List<String> descriptions) {
        Map<String, Integer> stats = new HashMap<>();
        for (String desc : descriptions) {
            String category = categorize(desc);
            stats.put(category, stats.getOrDefault(category, 0) + 1);
        }
        return stats;
    }

    /**
     * Follow-up 3: Learning from user corrections
     */
    public static class CategoryLearner {
        private Map<String, String> userCorrections = new HashMap<>();

        public void addCorrection(String description, String correctCategory) {
            userCorrections.put(description.toLowerCase().trim(), correctCategory);
        }

        public String categorizeWithLearning(String description) {
            String key = description.toLowerCase().trim();
            if (userCorrections.containsKey(key)) {
                return userCorrections.get(key);
            }
            return categorize(description);
        }

        public Set<String> getLearnedPatterns() {
            return userCorrections.keySet();
        }
    }

    // Result class for enhanced categorization
    public static class CategoryResult {
        public final String category;
        public final double confidence;

        public CategoryResult(String category, double confidence) {
            this.category = category;
            this.confidence = confidence;
        }

        @Override
        public String toString() {
            return String.format("%s (%.2f)", category, confidence);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Banking Transaction Categorization System ===\n");

        // Test Case 1: Basic categorization
        System.out.println("1. Basic Categorization Tests:");
        String[] testTransactions = {
                "SWIGGY FOOD DELIVERY BANGALORE",
                "AMAZON PAY SHOPPING ELECTRONICS",
                "UBER RIDE FROM HOME TO OFFICE",
                "NETFLIX MONTHLY SUBSCRIPTION",
                "APOLLO HOSPITAL CONSULTATION",
                "BSES ELECTRICITY BILL PAYMENT",
                "SALARY CREDIT FROM KOTAK BANK",
                "UPI TRANSFER TO JOHN DOE",
                "ATM CASH WITHDRAWAL HDFC",
                "ZERODHA EQUITY TRADING ACCOUNT"
        };

        for (String transaction : testTransactions) {
            System.out.printf("%-40s -> %s%n", transaction, categorize(transaction));
        }

        // Test Case 2: Enhanced categorization with confidence
        System.out.println("\n2. Enhanced Categorization with Confidence:");
        String[] confidenceTests = {
                "RESTAURANT BILL PAYMENT",
                "UNKNOWN MERCHANT XYZ123",
                "FLIPKART ONLINE SHOPPING CLOTHES"
        };

        for (String transaction : confidenceTests) {
            CategoryResult result = categorizeWithConfidence(transaction);
            System.out.printf("%-40s -> %s%n", transaction, result);
        }

        // Test Case 3: Batch categorization
        System.out.println("\n3. Batch Categorization:");
        List<String> batchTransactions = Arrays.asList(
                "DOMINOS PIZZA ORDER",
                "METRO CARD RECHARGE",
                "MUTUAL FUND SIP PAYMENT");

        Map<String, String> batchResults = batchCategorize(batchTransactions);
        batchResults.forEach((desc, category) -> System.out.printf("%-30s -> %s%n", desc, category));

        // Test Case 4: Category statistics
        System.out.println("\n4. Category Statistics:");
        List<String> monthlyTransactions = Arrays.asList(
                "SWIGGY ORDER", "AMAZON PURCHASE", "UBER RIDE",
                "SWIGGY ORDER", "NETFLIX PAYMENT", "SALARY CREDIT",
                "AMAZON PURCHASE", "UBER RIDE", "ELECTRICITY BILL");

        Map<String, Integer> stats = getCategoryStatistics(monthlyTransactions);
        stats.forEach((category, count) -> System.out.printf("%-15s: %d transactions%n", category, count));

        // Test Case 5: Learning system
        System.out.println("\n5. Learning System Demo:");
        CategoryLearner learner = new CategoryLearner();
        learner.addCorrection("CUSTOM MERCHANT ABC", SHOPPING);
        learner.addCorrection("UNKNOWN VENDOR XYZ", FOOD_DINING);

        System.out.println("Before learning: " + categorize("CUSTOM MERCHANT ABC"));
        System.out.println("After learning:  " + learner.categorizeWithLearning("CUSTOM MERCHANT ABC"));
        System.out.println("Learned patterns: " + learner.getLearnedPatterns().size());

        // Test Case 6: Edge cases
        System.out.println("\n6. Edge Cases:");
        String[] edgeCases = {
                null,
                "",
                "   ",
                "1234567890",
                "MIXED AMAZON UBER SWIGGY DESCRIPTION",
                "VERY LONG TRANSACTION DESCRIPTION WITH MULTIPLE KEYWORDS FOOD SHOPPING TRANSPORT ENTERTAINMENT"
        };

        for (String edge : edgeCases) {
            System.out.printf("%-70s -> %s%n",
                    edge == null ? "null" : "\"" + edge + "\"",
                    categorize(edge));
        }

        System.out.println("\n=== Test Completed Successfully ===");
    }
}
