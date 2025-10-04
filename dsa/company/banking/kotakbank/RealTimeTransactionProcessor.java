package company.banking.kotakbank;

import java.util.*;

/**
 * Kotak Bank SDE3 Interview Question #4 (Bar Raiser)
 * 
 * Problem: Real-time Transaction Processing & Risk Assessment System
 * LeetCode Equivalent: Sliding Window Maximum + LRU Cache + Design patterns
 * 
 * Banking Context:
 * Design and implement a real-time transaction processing system that:
 * 1. Processes transactions in sliding time windows
 * 2. Detects anomalous spending patterns
 * 3. Maintains customer transaction history efficiently
 * 4. Provides risk scores for transactions
 * 
 * This is a comprehensive system design question that tests multiple concepts.
 * 
 * Interview Focus:
 * - Sliding window algorithms for time-series data
 * - Cache design for frequently accessed data
 * - Real-time anomaly detection
 * - System design principles for banking
 * - Scalability and performance optimization
 * 
 * Difficulty: Hard (Bar Raiser)
 * Expected Time: 45-60 minutes
 * 
 * Follow-up Questions:
 * 1. How would you scale this to handle 100M transactions per day?
 * 2. How would you ensure 99.99% uptime for this critical system?
 * 3. How would you handle data consistency across multiple data centers?
 * 4. How would you implement machine learning for better risk assessment?
 */
public class RealTimeTransactionProcessor {

    /**
     * Enhanced Transaction class with risk assessment data
     */
    public static class Transaction {
        String transactionId;
        String accountId;
        String merchantId;
        int amount;
        String category;
        long timestamp;
        String location;
        double riskScore;

        public Transaction(String accountId, String merchantId, int amount,
                String category, String location) {
            this.transactionId = "TXN" + System.nanoTime();
            this.accountId = accountId;
            this.merchantId = merchantId;
            this.amount = amount;
            this.category = category;
            this.timestamp = System.currentTimeMillis();
            this.location = location;
            this.riskScore = 0.0;
        }

        @Override
        public String toString() {
            return String.format("%s: $%d at %s (%s) - Risk: %.2f",
                    transactionId, amount, merchantId, category, riskScore);
        }
    }

    /**
     * Customer spending pattern for anomaly detection
     */
    public static class SpendingPattern {
        Map<String, List<Integer>> categorySpending;
        Map<String, Integer> merchantFrequency;
        List<Integer> recentAmounts;
        double avgDailySpending;
        double maxSingleTransaction;

        public SpendingPattern() {
            this.categorySpending = new HashMap<>();
            this.merchantFrequency = new HashMap<>();
            this.recentAmounts = new ArrayList<>();
            this.avgDailySpending = 0.0;
            this.maxSingleTransaction = 0.0;
        }
    }

    /**
     * LRU Cache for frequently accessed customer data
     * Time Complexity: O(1) for get/put operations
     * Space Complexity: O(capacity)
     */
    public static class CustomerDataCache {
        private final int capacity;
        private final Map<String, CacheNode> cache;
        private final CacheNode head;
        private final CacheNode tail;

        private static class CacheNode {
            String accountId;
            SpendingPattern pattern;
            CacheNode prev;
            CacheNode next;

            CacheNode(String accountId, SpendingPattern pattern) {
                this.accountId = accountId;
                this.pattern = pattern;
            }
        }

        public CustomerDataCache(int capacity) {
            this.capacity = capacity;
            this.cache = new HashMap<>();
            this.head = new CacheNode("", null);
            this.tail = new CacheNode("", null);
            head.next = tail;
            tail.prev = head;
        }

        public SpendingPattern getCustomerPattern(String accountId) {
            CacheNode node = cache.get(accountId);
            if (node != null) {
                moveToHead(node);
                return node.pattern;
            }
            return null;
        }

        public void putCustomerPattern(String accountId, SpendingPattern pattern) {
            CacheNode existing = cache.get(accountId);
            if (existing != null) {
                existing.pattern = pattern;
                moveToHead(existing);
            } else {
                CacheNode newNode = new CacheNode(accountId, pattern);
                cache.put(accountId, newNode);
                addToHead(newNode);

                if (cache.size() > capacity) {
                    CacheNode removed = removeTail();
                    cache.remove(removed.accountId);
                }
            }
        }

        private void addToHead(CacheNode node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void removeNode(CacheNode node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void moveToHead(CacheNode node) {
            removeNode(node);
            addToHead(node);
        }

        private CacheNode removeTail() {
            CacheNode lastNode = tail.prev;
            removeNode(lastNode);
            return lastNode;
        }

        public int size() {
            return cache.size();
        }

        public void printCacheStats() {
            System.out.println("📊 Cache Statistics:");
            System.out.println("   Size: " + cache.size() + "/" + capacity);
            System.out.println("   Utilization: " + String.format("%.1f%%",
                    (cache.size() * 100.0) / capacity));
        }
    }

    /**
     * Sliding Window for time-based transaction analysis
     * Maintains transactions within a specific time window
     */
    public static class TransactionWindow {
        private final long windowSizeMs;
        private final Deque<Transaction> window;

        public TransactionWindow(long windowSizeMs) {
            this.windowSizeMs = windowSizeMs;
            this.window = new ArrayDeque<>();
        }

        public void addTransaction(Transaction transaction) {
            // Remove expired transactions
            long cutoffTime = transaction.timestamp - windowSizeMs;
            while (!window.isEmpty() && window.peekFirst().timestamp < cutoffTime) {
                window.pollFirst();
            }

            window.addLast(transaction);
        }

        public List<Transaction> getActiveTransactions() {
            return new ArrayList<>(window);
        }

        public int getTotalAmount() {
            return window.stream().mapToInt(t -> t.amount).sum();
        }

        public int getTransactionCount() {
            return window.size();
        }

        public double getAverageAmount() {
            if (window.isEmpty())
                return 0.0;
            return getTotalAmount() / (double) window.size();
        }
    }

    /**
     * Main Transaction Processor with risk assessment
     */
    private final CustomerDataCache customerCache;
    private final Map<String, TransactionWindow> customerWindows;
    private final long riskWindowMs;
    private final RiskAssessmentEngine riskEngine;

    public RealTimeTransactionProcessor(int cacheSize, long riskWindowMs) {
        this.customerCache = new CustomerDataCache(cacheSize);
        this.customerWindows = new HashMap<>();
        this.riskWindowMs = riskWindowMs;
        this.riskEngine = new RiskAssessmentEngine();
    }

    /**
     * Process a new transaction with real-time risk assessment
     */
    public boolean processTransaction(Transaction transaction) {
        System.out.println("🔄 Processing: " + transaction);

        // Update sliding window for the customer
        if (!customerWindows.containsKey(transaction.accountId)) {
            customerWindows.put(transaction.accountId, new TransactionWindow(riskWindowMs));
        }
        TransactionWindow window = customerWindows.get(transaction.accountId);
        window.addTransaction(transaction);

        // Get or create customer spending pattern
        SpendingPattern pattern = customerCache.getCustomerPattern(transaction.accountId);
        if (pattern == null) {
            pattern = new SpendingPattern();
        }

        // Calculate risk score
        double riskScore = riskEngine.calculateRiskScore(transaction, pattern, window);
        transaction.riskScore = riskScore;

        // Update customer pattern
        updateSpendingPattern(pattern, transaction);
        customerCache.putCustomerPattern(transaction.accountId, pattern);

        // Make approval decision
        boolean approved = riskScore < 0.7; // Risk threshold

        if (approved) {
            System.out.println("✅ Transaction APPROVED (Risk: " +
                    String.format("%.2f", riskScore) + ")");
        } else {
            System.out.println("❌ Transaction REJECTED (Risk: " +
                    String.format("%.2f", riskScore) + ")");
        }

        return approved;
    }

    /**
     * Update customer spending pattern with new transaction
     */
    private void updateSpendingPattern(SpendingPattern pattern, Transaction transaction) {
        // Update category spending
        if (!pattern.categorySpending.containsKey(transaction.category)) {
            pattern.categorySpending.put(transaction.category, new ArrayList<>());
        }
        pattern.categorySpending.get(transaction.category).add(transaction.amount);

        // Update merchant frequency
        pattern.merchantFrequency.put(transaction.merchantId,
                pattern.merchantFrequency.getOrDefault(transaction.merchantId, 0) + 1);

        // Update recent amounts (keep last 10)
        pattern.recentAmounts.add(transaction.amount);
        if (pattern.recentAmounts.size() > 10) {
            pattern.recentAmounts.remove(0);
        }

        // Update statistics
        pattern.maxSingleTransaction = Math.max(pattern.maxSingleTransaction, transaction.amount);
        pattern.avgDailySpending = pattern.recentAmounts.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Risk Assessment Engine
     */
    public static class RiskAssessmentEngine {

        public double calculateRiskScore(Transaction transaction, SpendingPattern pattern,
                TransactionWindow window) {
            double riskScore = 0.0;

            // Factor 1: Amount anomaly (30% weight)
            riskScore += calculateAmountRisk(transaction, pattern) * 0.3;

            // Factor 2: Frequency anomaly (25% weight)
            riskScore += calculateFrequencyRisk(window) * 0.25;

            // Factor 3: Merchant risk (20% weight)
            riskScore += calculateMerchantRisk(transaction, pattern) * 0.2;

            // Factor 4: Location risk (15% weight)
            riskScore += calculateLocationRisk(transaction) * 0.15;

            // Factor 5: Time pattern risk (10% weight)
            riskScore += calculateTimeRisk(transaction) * 0.1;

            return Math.min(riskScore, 1.0); // Cap at 1.0
        }

        private double calculateAmountRisk(Transaction transaction, SpendingPattern pattern) {
            if (pattern.avgDailySpending == 0)
                return 0.2; // New customer

            double ratio = transaction.amount / pattern.avgDailySpending;
            if (ratio > 10)
                return 1.0; // 10x average spending
            if (ratio > 5)
                return 0.8; // 5x average spending
            if (ratio > 3)
                return 0.5; // 3x average spending
            return 0.0;
        }

        private double calculateFrequencyRisk(TransactionWindow window) {
            int transactionCount = window.getTransactionCount();
            if (transactionCount > 20)
                return 1.0; // Too many transactions
            if (transactionCount > 10)
                return 0.6;
            if (transactionCount > 5)
                return 0.3;
            return 0.0;
        }

        private double calculateMerchantRisk(Transaction transaction, SpendingPattern pattern) {
            String merchantId = transaction.merchantId;
            int frequency = pattern.merchantFrequency.getOrDefault(merchantId, 0);

            if (frequency == 0)
                return 0.4; // New merchant
            if (frequency < 3)
                return 0.2; // Rarely used merchant
            return 0.0; // Familiar merchant
        }

        private double calculateLocationRisk(Transaction transaction) {
            // Simplified location risk based on high-risk patterns
            String location = transaction.location.toLowerCase();
            if (location.contains("unknown") || location.contains("foreign")) {
                return 0.8;
            }
            return 0.0;
        }

        private double calculateTimeRisk(Transaction transaction) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(transaction.timestamp);
            int hour = cal.get(Calendar.HOUR_OF_DAY);

            // Higher risk for transactions between 11 PM and 5 AM
            if (hour >= 23 || hour <= 5) {
                return 0.6;
            }
            return 0.0;
        }
    }

    /**
     * System monitoring and analytics
     */
    public void printSystemStats() {
        System.out.println("\n📊 SYSTEM STATISTICS");
        System.out.println("=".repeat(50));

        customerCache.printCacheStats();

        System.out.println("\n🔍 Active Windows: " + customerWindows.size());

        int totalActiveTransactions = customerWindows.values().stream()
                .mapToInt(TransactionWindow::getTransactionCount)
                .sum();
        System.out.println("📈 Total Active Transactions: " + totalActiveTransactions);

        double avgRiskWindowSize = customerWindows.values().stream()
                .mapToDouble(TransactionWindow::getAverageAmount)
                .average()
                .orElse(0.0);
        System.out.println("💰 Average Transaction Amount: $" + String.format("%.2f", avgRiskWindowSize));
    }

    /**
     * Test the complete system
     */
    public void testWithLeetCodeAPI() {
        System.out.println("🏦 Testing Real-Time Transaction Processor");
        System.out.println("=".repeat(50));

        // Simulate various transaction scenarios
        Transaction[] testTransactions = {
                // Normal transactions
                new Transaction("ACC001", "AMAZON", 50, "SHOPPING", "Mumbai"),
                new Transaction("ACC001", "STARBUCKS", 15, "FOOD", "Mumbai"),
                new Transaction("ACC001", "AMAZON", 75, "SHOPPING", "Mumbai"),

                // Suspicious patterns
                new Transaction("ACC001", "UNKNOWN_MERCHANT", 5000, "SHOPPING", "Foreign"), // High amount + new
                                                                                            // merchant
                new Transaction("ACC002", "ATM_CASH", 10000, "CASH", "Unknown"), // High amount + location risk

                // Frequent transactions (potential fraud)
                new Transaction("ACC003", "ONLINE_STORE", 100, "SHOPPING", "Delhi"),
                new Transaction("ACC003", "ONLINE_STORE", 150, "SHOPPING", "Delhi"),
                new Transaction("ACC003", "ONLINE_STORE", 200, "SHOPPING", "Delhi"),
                new Transaction("ACC003", "ONLINE_STORE", 250, "SHOPPING", "Delhi"),
                new Transaction("ACC003", "ONLINE_STORE", 300, "SHOPPING", "Delhi"),
        };

        System.out.println("🔄 Processing " + testTransactions.length + " transactions...\n");

        int approvedCount = 0;
        int rejectedCount = 0;

        for (Transaction transaction : testTransactions) {
            boolean approved = processTransaction(transaction);
            if (approved)
                approvedCount++;
            else
                rejectedCount++;
            System.out.println();

            // Simulate processing delay
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("📊 PROCESSING SUMMARY:");
        System.out.println("✅ Approved: " + approvedCount);
        System.out.println("❌ Rejected: " + rejectedCount);
        System.out.println("📈 Approval Rate: " + String.format("%.1f%%",
                (approvedCount * 100.0) / testTransactions.length));

        printSystemStats();
    }

    /**
     * Discuss system design and scalability
     */
    public void discussSystemDesign() {
        System.out.println("\n🏗️  SYSTEM DESIGN ANALYSIS");
        System.out.println("=".repeat(50));

        System.out.println("1. ARCHITECTURE COMPONENTS:");
        System.out.println("   - Real-time transaction processing pipeline");
        System.out.println("   - LRU cache for customer data (O(1) access)");
        System.out.println("   - Sliding window for time-based analysis");
        System.out.println("   - Multi-factor risk assessment engine");

        System.out.println("\n2. SCALABILITY STRATEGIES:");
        System.out.println("   - Horizontal sharding by customer ID");
        System.out.println("   - Distributed caching (Redis cluster)");
        System.out.println("   - Event-driven architecture with Kafka");
        System.out.println("   - Microservices for different risk factors");

        System.out.println("\n3. PERFORMANCE OPTIMIZATIONS:");
        System.out.println("   - In-memory processing for real-time decisions");
        System.out.println("   - Asynchronous pattern updates");
        System.out.println("   - Batch processing for non-critical analytics");
        System.out.println("   - Database read replicas for historical data");

        System.out.println("\n4. RELIABILITY & MONITORING:");
        System.out.println("   - Circuit breakers for external services");
        System.out.println("   - Health checks and automated failover");
        System.out.println("   - Real-time alerting for system anomalies");
        System.out.println("   - Comprehensive audit logging");

        System.out.println("\n5. SECURITY & COMPLIANCE:");
        System.out.println("   - End-to-end encryption for transaction data");
        System.out.println("   - PCI DSS compliance for payment processing");
        System.out.println("   - Role-based access control");
        System.out.println("   - Regular security audits and penetration testing");
    }

    public static void main(String[] args) {
        System.out.println("🏦 KOTAK BANK SDE3 BAR RAISER QUESTION");
        System.out.println("📋 Problem: Real-time Transaction Processing System");
        System.out.println("🔗 LeetCode Concepts: Sliding Window + LRU Cache + System Design");
        System.out.println();

        // Initialize system with 1000 customer cache, 5-minute risk window
        RealTimeTransactionProcessor processor = new RealTimeTransactionProcessor(1000, 5 * 60 * 1000);

        processor.testWithLeetCodeAPI();
        processor.discussSystemDesign();

        System.out.println("\n💡 INTERVIEW TIPS FOR BAR RAISER:");
        System.out.println("1. Start with high-level design, then dive into implementation");
        System.out.println("2. Discuss trade-offs between accuracy and performance");
        System.out.println("3. Consider both functional and non-functional requirements");
        System.out.println("4. Address scalability, reliability, and security concerns");
        System.out.println("5. Demonstrate knowledge of banking domain and regulations");
        System.out.println("6. Show understanding of real-time vs batch processing");
        System.out.println("7. Discuss monitoring, alerting, and operational concerns");
    }
}
