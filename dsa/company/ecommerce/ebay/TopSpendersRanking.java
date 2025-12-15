package company.ecommerce.ebay;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Top Spenders Ranking for Banking System
 *
 * Problem: Identify and rank top N accounts by outgoing transaction amounts
 *
 * Features:
 * - Create accounts with deposits
 * - Transfer money between accounts
 * - Get top N spenders at any timestamp
 * - Secondary sorting: account ID (ascending)
 *
 * Ranking Criteria:
 * 1. Total outgoing amount (descending)
 * 2. Account ID (ascending) if amounts equal
 *
 * Time Complexity:
 * - createAccount: O(1)
 * - deposit: O(1)
 * - transfer: O(1)
 * - topSpenders: O(n log n) where n = accounts
 *
 * Space Complexity: O(n*t) where n = accounts, t = transactions
 */
public class TopSpendersRanking {

    static class Transaction {
        String fromId;
        String toId;
        int amount;
        int timestamp;
        String type; // "TRANSFER", "DEPOSIT"

        public Transaction(String from, String to, int amount, int ts, String type) {
            this.fromId = from;
            this.toId = to;
            this.amount = amount;
            this.timestamp = ts;
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("%s: %s -> %s: %d @ %d", type, fromId, toId, amount, timestamp);
        }
    }

    static class Account {
        String accountId;
        int balance;
        int createdAt;
        List<Transaction> transactions;

        public Account(String id, int createdAt) {
            this.accountId = id;
            this.balance = 0;
            this.createdAt = createdAt;
            this.transactions = new ArrayList<>();
        }

        /**
         * Calculate total outgoing amount up to timestamp
         */
        public int getTotalOutgoing(int timestamp) {
            return (int) transactions.stream()
                .filter(t -> t.fromId.equals(accountId) && t.timestamp <= timestamp && t.type.equals("TRANSFER"))
                .mapToInt(t -> t.amount)
                .sum();
        }

        @Override
        public String toString() {
            return String.format("%s (Balance: %d)", accountId, balance);
        }
    }

    private Map<String, Account> accounts;
    private List<Transaction> allTransactions;

    public TopSpendersRanking() {
        this.accounts = new HashMap<>();
        this.allTransactions = new ArrayList<>();
    }

    /**
     * Create a new account
     * Time: O(1)
     */
    public void createAccount(String accountId, int timestamp) {
        if (accounts.containsKey(accountId)) {
            System.out.println("❌ Account already exists: " + accountId);
            return;
        }

        accounts.put(accountId, new Account(accountId, timestamp));
        System.out.println("✓ Account created: " + accountId + " at timestamp " + timestamp);
    }

    /**
     * Deposit money into account
     * Time: O(1)
     */
    public void deposit(String accountId, int timestamp, int amount) {
        if (!accounts.containsKey(accountId)) {
            System.out.println("❌ Account not found: " + accountId);
            return;
        }

        if (amount <= 0) {
            System.out.println("❌ Amount must be positive");
            return;
        }

        Account account = accounts.get(accountId);
        account.balance += amount;

        Transaction txn = new Transaction(accountId, accountId, amount, timestamp, "DEPOSIT");
        account.transactions.add(txn);
        allTransactions.add(txn);

        System.out.println("✓ Deposited " + amount + " to " + accountId);
    }

    /**
     * Transfer money from one account to another
     * Time: O(1)
     */
    public void transfer(String fromId, String toId, int timestamp, int amount) {
        if (!accounts.containsKey(fromId) || !accounts.containsKey(toId)) {
            System.out.println("❌ Account not found");
            return;
        }

        if (fromId.equals(toId)) {
            System.out.println("❌ Cannot transfer to same account");
            return;
        }

        if (amount <= 0) {
            System.out.println("❌ Amount must be positive");
            return;
        }

        Account fromAccount = accounts.get(fromId);
        Account toAccount = accounts.get(toId);

        if (fromAccount.balance < amount) {
            System.out.println("❌ Insufficient balance");
            return;
        }

        // Perform transfer
        fromAccount.balance -= amount;
        toAccount.balance += amount;

        // Record transaction
        Transaction txn = new Transaction(fromId, toId, amount, timestamp, "TRANSFER");
        fromAccount.transactions.add(txn);
        toAccount.transactions.add(txn);
        allTransactions.add(txn);

        System.out.println("✓ Transfer: " + fromId + " -> " + toId + ": " + amount);
    }

    /**
     * Get top N spenders at given timestamp
     * Time: O(n log n) where n = accounts
     *
     * Sorting:
     * 1. Total outgoing amount (descending)
     * 2. Account ID (ascending)
     */
    public List<String> topSpenders(int timestamp, int n) {
        List<String> result = accounts.values().stream()
            .sorted((a, b) -> {
                // Sort by total outgoing (descending)
                int outgoingA = a.getTotalOutgoing(timestamp);
                int outgoingB = b.getTotalOutgoing(timestamp);

                int outgoingCmp = Integer.compare(outgoingB, outgoingA); // Descending
                if (outgoingCmp != 0) {
                    return outgoingCmp;
                }

                // Sort by account ID (ascending)
                return a.accountId.compareTo(b.accountId);
            })
            .limit(n)
            .map(acc -> acc.accountId)
            .collect(Collectors.toList());

        return result;
    }

    /**
     * Display accounts with their outgoing totals
     */
    public void displayAccountsWithOutgoing(int timestamp) {
        System.out.println("\n--- Accounts with Outgoing Totals at Timestamp " + timestamp + " ---");

        accounts.values().stream()
            .sorted((a, b) -> {
                int outA = a.getTotalOutgoing(timestamp);
                int outB = b.getTotalOutgoing(timestamp);
                int cmp = Integer.compare(outB, outA);
                return cmp != 0 ? cmp : a.accountId.compareTo(b.accountId);
            })
            .forEach(acc -> {
                int outgoing = acc.getTotalOutgoing(timestamp);
                System.out.println("  " + acc.accountId + ": Total Outgoing = " + outgoing +
                                 " (Balance: " + acc.balance + ")");
            });
    }

    /**
     * Display transaction history
     */
    public void displayTransactions() {
        System.out.println("\n--- All Transactions ---");
        for (int i = 0; i < allTransactions.size(); i++) {
            System.out.println("  T" + (i + 1) + ": " + allTransactions.get(i));
        }
    }

    // ==================== DEMO ====================

    public static void main(String[] args) {
        TopSpendersRanking system = new TopSpendersRanking();

        System.out.println("\n========== TOP SPENDERS RANKING DEMO ==========\n");

        // Create accounts
        System.out.println("--- Creating Accounts ---");
        system.createAccount("ACC001", 1);
        system.createAccount("ACC002", 3);
        system.createAccount("ACC003", 5);

        // Deposits
        System.out.println("\n--- Deposits ---");
        system.deposit("ACC001", 2, 1000);
        system.deposit("ACC002", 4, 500);
        system.deposit("ACC003", 6, 700);

        // Transfers
        System.out.println("\n--- Transfers ---");
        system.transfer("ACC001", "ACC002", 7, 200);  // ACC001 spends 200
        system.transfer("ACC001", "ACC003", 8, 300);  // ACC001 spends 300 (total 500)
        system.transfer("ACC002", "ACC003", 9, 150);  // ACC002 spends 150
        system.transfer("ACC003", "ACC001", 10, 50);  // ACC003 spends 50

        // Display transactions
        system.displayTransactions();

        // Display outgoing totals
        system.displayAccountsWithOutgoing(11);

        // Top spenders
        System.out.println("\n--- Top 2 Spenders at Timestamp 11 ---");
        List<String> top2 = system.topSpenders(11, 2);
        System.out.println("Result: " + top2);
        System.out.println("Expected: [ACC001, ACC002]");

        System.out.println("\n--- Top 3 Spenders at Timestamp 11 ---");
        List<String> top3 = system.topSpenders(11, 3);
        System.out.println("Result: " + top3);
        System.out.println("Expected: [ACC001, ACC002, ACC003]");

        // More test cases
        System.out.println("\n========== ADDITIONAL TEST CASES ==========\n");

        TopSpendersRanking system2 = new TopSpendersRanking();

        System.out.println("--- Test Case 2: Tie Breaking ---");
        system2.createAccount("ACC_A", 1);
        system2.createAccount("ACC_B", 1);
        system2.createAccount("ACC_C", 1);

        system2.deposit("ACC_A", 2, 1000);
        system2.deposit("ACC_B", 2, 1000);
        system2.deposit("ACC_C", 2, 1000);

        // All transfer same amount
        system2.transfer("ACC_A", "ACC_C", 5, 100);
        system2.transfer("ACC_B", "ACC_C", 5, 100);
        system2.transfer("ACC_C", "ACC_A", 5, 100);

        system2.displayAccountsWithOutgoing(10);

        System.out.println("\n--- Top 2 (should use ID for tie breaking) ---");
        List<String> tieTop2 = system2.topSpenders(10, 2);
        System.out.println("Result: " + tieTop2);
        System.out.println("Expected: [ACC_A, ACC_B] (both have 100 outgoing, sorted by ID)");
    }
}

