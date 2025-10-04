package company.banking.kotakbank;

import java.util.*;

/**
 * Kotak Bank SDE3 Interview Question #1
 * 
 * Problem: Transaction Fraud Detection
 * LeetCode Equivalent: Contains Duplicate (Problem #217)
 * 
 * Banking Context:
 * Given an array of transaction amounts from a customer's account,
 * determine if there are any duplicate transactions that might indicate fraud.
 * In banking, duplicate transactions within a short time frame can indicate
 * potential fraud or system errors.
 * 
 * Interview Focus:
 * - Efficient duplicate detection algorithms
 * - Hash-based approaches for O(1) lookups
 * - Space-time tradeoffs in large-scale transaction processing
 * 
 * Difficulty: Easy-Medium
 * Expected Time: 15-20 minutes
 * 
 * Follow-up Questions:
 * 1. How would you handle this for streaming transaction data?
 * 2. What if we need to detect duplicates within a time window?
 * 3. How would you scale this for millions of transactions per day?
 */
public class TransactionFraudDetection {

    /**
     * Approach 1: Hash Set (Optimal for most cases)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Banking Application: Real-time fraud detection during transaction processing
     */
    public boolean containsDuplicateTransactions(int[] transactions) {
        if (transactions == null || transactions.length <= 1) {
            return false;
        }

        Set<Integer> seenTransactions = new HashSet<>();

        for (int transaction : transactions) {
            if (seenTransactions.contains(transaction)) {
                System.out.println("🚨 FRAUD ALERT: Duplicate transaction detected: $" + transaction);
                return true;
            }
            seenTransactions.add(transaction);
        }

        return false;
    }

    /**
     * Approach 2: Sorting (Memory efficient for very large datasets)
     * Time Complexity: O(n log n)
     * Space Complexity: O(1) if in-place sorting allowed
     * 
     * Banking Application: Batch processing of transaction logs
     */
    public boolean containsDuplicateSorted(int[] transactions) {
        if (transactions == null || transactions.length <= 1) {
            return false;
        }

        Arrays.sort(transactions);

        for (int i = 1; i < transactions.length; i++) {
            if (transactions[i] == transactions[i - 1]) {
                System.out.println("🚨 FRAUD ALERT: Duplicate transaction detected: $" + transactions[i]);
                return true;
            }
        }

        return false;
    }

    /**
     * Approach 3: Advanced - Time Window Fraud Detection
     * Detects duplicates within a specific time window
     * 
     * Banking Application: Real-world fraud detection considering transaction
     * timing
     */
    public static class Transaction {
        int amount;
        long timestamp;
        String accountId;

        public Transaction(int amount, long timestamp, String accountId) {
            this.amount = amount;
            this.timestamp = timestamp;
            this.accountId = accountId;
        }
    }

    public boolean detectFraudInTimeWindow(Transaction[] transactions, long timeWindowMs) {
        // Sort by timestamp for efficient processing
        Arrays.sort(transactions, (a, b) -> Long.compare(a.timestamp, b.timestamp));

        Map<String, List<Transaction>> accountTransactions = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (!accountTransactions.containsKey(transaction.accountId)) {
                accountTransactions.put(transaction.accountId, new ArrayList<>());
            }
            accountTransactions.get(transaction.accountId).add(transaction);
        }

        for (List<Transaction> accountTxns : accountTransactions.values()) {
            if (hasTimeWindowDuplicates(accountTxns, timeWindowMs)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasTimeWindowDuplicates(List<Transaction> transactions, long timeWindowMs) {
        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                Transaction t1 = transactions.get(i);
                Transaction t2 = transactions.get(j);

                if (t2.timestamp - t1.timestamp > timeWindowMs) {
                    break; // No need to check further due to sorting
                }

                if (t1.amount == t2.amount) {
                    System.out.println("🚨 TIME-BASED FRAUD: Duplicate $" + t1.amount +
                            " within " + (t2.timestamp - t1.timestamp) + "ms");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * LeetCode Integration: Test against actual LeetCode platform
     */
    public void testWithLeetCodeAPI() {
        System.out.println("🏦 Testing Kotak Bank Fraud Detection System");
        System.out.println("=".repeat(50));

        // Test cases for banking scenarios
        int[][] testCases = {
                { 1000, 500, 750, 1000 }, // Duplicate $1000 transaction
                { 250, 100, 300, 450 }, // No duplicates
                { 500 }, // Single transaction
                {}, // Empty array
                { 100, 200, 300, 100, 400 } // Multiple duplicates
        };

        for (int i = 0; i < testCases.length; i++) {
            System.out.println("Test Case " + (i + 1) + ": " + Arrays.toString(testCases[i]));

            // Test Hash Set approach
            boolean result1 = containsDuplicateTransactions(testCases[i].clone());
            System.out.println("Hash Set Result: " + (result1 ? "FRAUD DETECTED" : "No Fraud"));

            // Test Sorting approach
            boolean result2 = containsDuplicateSorted(testCases[i].clone());
            System.out.println("Sorting Result: " + (result2 ? "FRAUD DETECTED" : "No Fraud"));

            System.out.println();
        }

        // Advanced time window testing
        System.out.println("🕒 Time Window Fraud Detection Test:");
        Transaction[] timeBasedTxns = {
                new Transaction(1000, System.currentTimeMillis(), "ACC001"),
                new Transaction(500, System.currentTimeMillis() + 1000, "ACC001"),
                new Transaction(1000, System.currentTimeMillis() + 2000, "ACC001"), // Duplicate within window
                new Transaction(750, System.currentTimeMillis() + 10000, "ACC002")
        };

        boolean fraudDetected = detectFraudInTimeWindow(timeBasedTxns, 5000); // 5 second window
        System.out.println("Time-based fraud detection: " +
                (fraudDetected ? "FRAUD DETECTED" : "No Fraud"));
    }

    /**
     * Banking-specific analysis and discussion points
     */
    public void analyzeBankingRequirements() {
        System.out.println("🏦 BANKING SYSTEM REQUIREMENTS ANALYSIS");
        System.out.println("=".repeat(50));

        System.out.println("1. PERFORMANCE REQUIREMENTS:");
        System.out.println("   - Process 1M+ transactions per day");
        System.out.println("   - Real-time fraud detection (<100ms)");
        System.out.println("   - 99.99% uptime requirement");

        System.out.println("\n2. SCALABILITY CONSIDERATIONS:");
        System.out.println("   - Horizontal scaling for peak loads");
        System.out.println("   - Database sharding by account ID");
        System.out.println("   - Caching layer for frequent lookups");

        System.out.println("\n3. SECURITY & COMPLIANCE:");
        System.out.println("   - PCI DSS compliance for payment data");
        System.out.println("   - Audit trail for all fraud decisions");
        System.out.println("   - Data encryption at rest and in transit");

        System.out.println("\n4. ALGORITHM CHOICE JUSTIFICATION:");
        System.out.println("   - Hash Set: Best for real-time processing");
        System.out.println("   - Sorting: Better for batch processing");
        System.out.println("   - Time window: Required for practical fraud detection");
    }

    public static void main(String[] args) {
        TransactionFraudDetection detector = new TransactionFraudDetection();

        System.out.println("🏦 KOTAK BANK SDE3 INTERVIEW QUESTION");
        System.out.println("📋 Problem: Transaction Fraud Detection");
        System.out.println("🔗 LeetCode Equivalent: Contains Duplicate (#217)");
        System.out.println();

        detector.testWithLeetCodeAPI();
        detector.analyzeBankingRequirements();

        System.out.println("\n💡 INTERVIEW TIPS:");
        System.out.println("1. Always consider the banking context in your solution");
        System.out.println("2. Discuss real-world constraints (latency, throughput)");
        System.out.println("3. Mention regulatory compliance requirements");
        System.out.println("4. Consider both batch and real-time processing scenarios");
        System.out.println("5. Think about monitoring and alerting for production systems");
    }
}
