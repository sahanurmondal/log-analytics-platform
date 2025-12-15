package company.ecommerce.ebay;

import java.util.*;

/**
 * Fraud Detection System
 *
 * Problem: Identify fraudulent transactions in real-time
 * Used by: Risk management, payment security, seller verification
 *
 * Detection Methods:
 * 1. Velocity checks (high frequency transactions)
 * 2. Pattern matching (suspicious behavior)
 * 3. Amount anomalies (unusual transaction size)
 * 4. Geographic inconsistencies (impossible travel)
 * 5. Device fingerprinting (multiple accounts from same device)
 * 6. Seller behavior analysis (rapid price changes, refunds)
 *
 * Algorithm: Rules Engine + ML features
 * Time Complexity: O(1) per transaction
 * Space Complexity: O(n) for history tracking
 */
public class FraudDetectionSystem {

    static class Transaction {
        String transactionId;
        int buyerId;
        int sellerId;
        double amount;
        String status; // "PENDING", "APPROVED", "FLAGGED", "BLOCKED"
        long timestamp;
        String deviceId;
        String ipAddress;
        String location;
        int riskScore;
        List<String> flagReasons;

        public Transaction(String id, int buyer, int seller, double amount, String device, String ip, String location) {
            this.transactionId = id;
            this.buyerId = buyer;
            this.sellerId = seller;
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
            this.deviceId = device;
            this.ipAddress = ip;
            this.location = location;
            this.status = "PENDING";
            this.riskScore = 0;
            this.flagReasons = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("Transaction %s | Buyer: %d, Seller: %d | Amount: $%.2f | Risk: %d | Status: %s | Reasons: %s",
                transactionId, buyerId, sellerId, amount, riskScore, status, flagReasons);
        }
    }

    // Transaction history per user
    private Map<Integer, List<Transaction>> buyerHistory;
    private Map<Integer, List<Transaction>> sellerHistory;
    private Map<String, List<Integer>> deviceUserMap;

    // Thresholds
    private static final int HIGH_VELOCITY_THRESHOLD = 5; // 5 transactions in 10 minutes
    private static final long VELOCITY_WINDOW = 10 * 60 * 1000; // 10 minutes
    private static final double HIGH_AMOUNT_MULTIPLIER = 3.0; // 3x average
    private static final int RISK_SCORE_THRESHOLD = 50;

    public FraudDetectionSystem() {
        this.buyerHistory = new HashMap<>();
        this.sellerHistory = new HashMap<>();
        this.deviceUserMap = new HashMap<>();
    }

    /**
     * Analyze transaction for fraud
     * Time: O(k) where k is recent transaction count
     */
    public Transaction analyzeTransaction(String transactionId, int buyerId, int sellerId,
                                         double amount, String deviceId, String ipAddress, String location) {
        Transaction txn = new Transaction(transactionId, buyerId, sellerId, amount, deviceId, ipAddress, location);

        // Run fraud checks
        checkVelocity(txn, buyerId);
        checkAmountAnomaly(txn, buyerId);
        checkDeviceConsistency(txn, buyerId);
        checkSellerBehavior(txn, sellerId);
        checkGeographicInconsistency(txn, buyerId);
        checkRepeatOffender(txn, buyerId, sellerId);

        // Determine final status based on risk score
        if (txn.riskScore >= RISK_SCORE_THRESHOLD) {
            txn.status = "BLOCKED";
        } else if (txn.riskScore >= 30) {
            txn.status = "FLAGGED";
        } else {
            txn.status = "APPROVED";
        }

        // Store transaction
        buyerHistory.computeIfAbsent(buyerId, k -> new ArrayList<>()).add(txn);
        sellerHistory.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(txn);
        deviceUserMap.computeIfAbsent(deviceId, k -> new ArrayList<>()).add(buyerId);

        return txn;
    }

    /**
     * Check for high velocity (many transactions in short time)
     * Time: O(k)
     */
    private void checkVelocity(Transaction txn, int buyerId) {
        List<Transaction> history = buyerHistory.getOrDefault(buyerId, new ArrayList<>());

        long recentCount = history.stream()
            .filter(t -> System.currentTimeMillis() - t.timestamp < VELOCITY_WINDOW)
            .count();

        if (recentCount >= HIGH_VELOCITY_THRESHOLD) {
            txn.riskScore += 25;
            txn.flagReasons.add("High velocity (" + recentCount + " txns in 10 min)");
        }
    }

    /**
     * Check for amount anomalies
     * Time: O(k)
     */
    private void checkAmountAnomaly(Transaction txn, int buyerId) {
        List<Transaction> history = buyerHistory.getOrDefault(buyerId, new ArrayList<>());

        if (history.isEmpty()) {
            return; // New user, no baseline
        }

        double avgAmount = history.stream()
            .mapToDouble(t -> t.amount)
            .average()
            .orElse(0);

        if (txn.amount > avgAmount * HIGH_AMOUNT_MULTIPLIER) {
            txn.riskScore += 20;
            txn.flagReasons.add("Unusual amount ($" + txn.amount + " vs avg $" + String.format("%.2f", avgAmount) + ")");
        }
    }

    /**
     * Check device consistency
     * Time: O(1)
     */
    private void checkDeviceConsistency(Transaction txn, int buyerId) {
        List<Integer> usersOnDevice = deviceUserMap.getOrDefault(txn.deviceId, new ArrayList<>());

        // Multiple users on same device is suspicious
        if (usersOnDevice.size() > 5) {
            txn.riskScore += 15;
            txn.flagReasons.add("Device shared by multiple users (" + usersOnDevice.size() + " users)");
        }
    }

    /**
     * Check seller behavior
     * Time: O(k)
     */
    private void checkSellerBehavior(Transaction txn, int sellerId) {
        List<Transaction> history = sellerHistory.getOrDefault(sellerId, new ArrayList<>());

        // Check for rapid refunds
        long recentRefunds = history.stream()
            .filter(t -> System.currentTimeMillis() - t.timestamp < 24 * 60 * 60 * 1000)
            .filter(t -> t.status.equals("REFUNDED"))
            .count();

        if (recentRefunds > 10) {
            txn.riskScore += 15;
            txn.flagReasons.add("Seller has high refund rate (" + recentRefunds + " refunds in 24h)");
        }
    }

    /**
     * Check for impossible geographic travel
     * Time: O(k)
     */
    private void checkGeographicInconsistency(Transaction txn, int buyerId) {
        List<Transaction> history = buyerHistory.getOrDefault(buyerId, new ArrayList<>());

        if (history.isEmpty()) {
            return;
        }

        Transaction lastTxn = history.get(history.size() - 1);

        // Simplified: if location changed in < 1 hour, flag
        if (!lastTxn.location.equals(txn.location) &&
            (System.currentTimeMillis() - lastTxn.timestamp) < 60 * 60 * 1000) {
            txn.riskScore += 20;
            txn.flagReasons.add("Impossible travel (from " + lastTxn.location + " to " + txn.location + " in < 1 hour)");
        }
    }

    /**
     * Check for repeat offenders
     * Time: O(1)
     */
    private void checkRepeatOffender(Transaction txn, int buyerId, int sellerId) {
        List<Transaction> buyerTxns = buyerHistory.getOrDefault(buyerId, new ArrayList<>());
        List<Transaction> sellerTxns = sellerHistory.getOrDefault(sellerId, new ArrayList<>());

        // Check if this buyer was flagged with this seller before
        long priorFlaggedTxns = buyerTxns.stream()
            .filter(t -> t.sellerId == sellerId && (t.status.equals("FLAGGED") || t.status.equals("BLOCKED")))
            .count();

        if (priorFlaggedTxns > 0) {
            txn.riskScore += 25;
            txn.flagReasons.add("Repeat pattern with this seller (" + priorFlaggedTxns + " prior issues)");
        }
    }

    /**
     * Get transactions by status
     * Time: O(n)
     */
    public List<Transaction> getTransactionsByStatus(String status) {
        List<Transaction> result = new ArrayList<>();

        for (List<Transaction> history : buyerHistory.values()) {
            history.stream()
                .filter(t -> t.status.equals(status))
                .forEach(result::add);
        }

        return result;
    }

    /**
     * Manual review for flagged transaction
     * Time: O(1)
     */
    public void approveTransaction(String transactionId) {
        for (List<Transaction> history : buyerHistory.values()) {
            for (Transaction txn : history) {
                if (txn.transactionId.equals(transactionId)) {
                    txn.status = "APPROVED";
                    txn.flagReasons.add("Manual approval");
                }
            }
        }
    }

    /**
     * Report fraudulent transaction
     * Time: O(1)
     */
    public void reportFraud(String transactionId) {
        for (List<Transaction> history : buyerHistory.values()) {
            for (Transaction txn : history) {
                if (txn.transactionId.equals(transactionId)) {
                    txn.status = "BLOCKED";
                    txn.flagReasons.add("Confirmed fraud");
                }
            }
        }
    }

    public static void main(String[] args) {
        FraudDetectionSystem system = new FraudDetectionSystem();

        System.out.println("=== Transaction Analysis ===\n");

        // Normal transaction
        Transaction t1 = system.analyzeTransaction("TXN001", 1001, 2001, 50.0, "DEV001", "192.168.1.1", "New York");
        System.out.println(t1);

        // High velocity transaction
        Transaction t2 = system.analyzeTransaction("TXN002", 1002, 2002, 100.0, "DEV002", "192.168.1.2", "Los Angeles");
        Transaction t3 = system.analyzeTransaction("TXN003", 1002, 2003, 100.0, "DEV002", "192.168.1.2", "Los Angeles");
        Transaction t4 = system.analyzeTransaction("TXN004", 1002, 2004, 100.0, "DEV002", "192.168.1.2", "Los Angeles");
        Transaction t5 = system.analyzeTransaction("TXN005", 1002, 2005, 100.0, "DEV002", "192.168.1.2", "Los Angeles");
        System.out.println("\n" + t5);

        // Unusual amount
        Transaction t6 = system.analyzeTransaction("TXN006", 1001, 2001, 500.0, "DEV001", "192.168.1.1", "New York");
        System.out.println("\n" + t6);

        // Impossible travel
        Transaction t7 = system.analyzeTransaction("TXN007", 1001, 2001, 75.0, "DEV001", "192.168.1.1", "Tokyo");
        System.out.println("\n" + t7);

        System.out.println("\n=== Flagged Transactions ===");
        system.getTransactionsByStatus("FLAGGED").forEach(System.out::println);

        System.out.println("\n=== Blocked Transactions ===");
        system.getTransactionsByStatus("BLOCKED").forEach(System.out::println);
    }
}

