package company.ecommerce.ebay;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Digital Wallet / Bank Account Application
 *
 * Features:
 * 1. Create wallets with initial balance
 * 2. Transfer money between accounts
 * 3. View transaction statements
 * 4. View account overview
 * 5. Offer 1: Equal balance reward (₹10 each)
 * 6. Offer 2: Top 3 spenders reward (₹10, ₹5, ₹2)
 * 7. Fixed Deposit with interest tracking
 *
 * Constraints:
 * - Minimum transfer: ₹0.0001
 * - Thread-safe operations
 * - All in-memory storage
 *
 * Time Complexity:
 * - CreateWallet: O(1)
 * - TransferMoney: O(1)
 * - Statement: O(t) where t = transactions
 * - Overview: O(n) where n = accounts
 * - Offer2: O(n log n)
 * - FixedDeposit: O(1)
 */
public class DigitalWallet {

    static class Transaction {
        String type; // "TRANSFER", "REWARD", "FD_INTEREST"
        String fromAccount;
        String toAccount;
        double amount;
        long timestamp;
        String description;

        public Transaction(String type, String from, String to, double amount, String desc) {
            this.type = type;
            this.fromAccount = from;
            this.toAccount = to;
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
            this.description = desc;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s -> %s: ₹%.4f (%s)",
                type, fromAccount, toAccount, amount, description);
        }
    }

    static class FixedDeposit {
        double amount;
        int remainingTransactions;
        boolean active;

        public FixedDeposit(double amount) {
            this.amount = amount;
            this.remainingTransactions = 5;
            this.active = true;
        }

        @Override
        public String toString() {
            return String.format("FD: ₹%.4f (Remaining txns: %d)", amount, remainingTransactions);
        }
    }

    static class Account {
        String accountId;
        String accountHolder;
        double balance;
        long createdAt;
        List<Transaction> transactions;
        FixedDeposit fixedDeposit;
        int transactionCount;
        boolean merged;

        public Account(String id, String holder, double initial) {
            this.accountId = id;
            this.accountHolder = holder;
            this.balance = initial;
            this.createdAt = System.currentTimeMillis();
            this.transactions = new ArrayList<>();
            this.fixedDeposit = null;
            this.transactionCount = 0;
            this.merged = false;
        }

        public void addTransaction(Transaction txn) {
            this.transactions.add(txn);
            this.transactionCount++;
        }

        @Override
        public String toString() {
            String fdInfo = fixedDeposit != null && fixedDeposit.active
                ? " [" + fixedDeposit.toString() + "]"
                : "";
            return String.format("%s %.4f%s", accountId, balance, fdInfo);
        }
    }

    private Map<String, Account> accounts;
    private final double MIN_TRANSFER = 0.0001;

    public DigitalWallet() {
        this.accounts = new HashMap<>();
    }

    /**
     * Create a new wallet
     * Time: O(1)
     */
    public void createWallet(String accountHolder, double initialBalance) {
        if (accounts.containsKey(accountHolder)) {
            System.out.println("❌ Account already exists: " + accountHolder);
            return;
        }

        Account account = new Account(accountHolder, accountHolder, initialBalance);
        accounts.put(accountHolder, account);
        System.out.println("✓ Wallet created for " + accountHolder + " with balance ₹" + initialBalance);
    }

    /**
     * Transfer money between accounts
     * Time: O(1)
     */
    public void transferMoney(String from, String to, double amount) {
        if (!accounts.containsKey(from) || !accounts.containsKey(to)) {
            System.out.println("❌ Account not found");
            return;
        }

        if (from.equals(to)) {
            System.out.println("❌ Cannot transfer to same account");
            return;
        }

        if (amount < MIN_TRANSFER) {
            System.out.println("❌ Minimum transfer amount is ₹" + MIN_TRANSFER);
            return;
        }

        Account fromAcc = accounts.get(from);
        Account toAcc = accounts.get(to);

        if (fromAcc.merged || toAcc.merged) {
            System.out.println("❌ Cannot transfer: account is merged");
            return;
        }

        if (fromAcc.balance < amount) {
            System.out.println("❌ Insufficient balance in " + from);
            return;
        }

        // Perform transfer
        fromAcc.balance -= amount;
        toAcc.balance += amount;

        // Record transaction
        Transaction txn = new Transaction("TRANSFER", from, to, amount, "Transfer");
        fromAcc.addTransaction(txn);
        toAcc.addTransaction(txn);

        System.out.printf("✓ Transfer: %s -> %s: ₹%.4f%n", from, to, amount);

        // Check for offer 1: equal balance after transaction
        if (Math.abs(fromAcc.balance - toAcc.balance) < 0.0001) {
            applyOffer1Reward(from, to);
        }

        // Check FD condition
        checkFixedDepositBalance(from);
    }

    /**
     * Apply Offer 1 reward when both accounts have equal balance
     */
    private void applyOffer1Reward(String acc1, String acc2) {
        Account account1 = accounts.get(acc1);
        Account account2 = accounts.get(acc2);

        double reward = 10.0;
        account1.balance += reward;
        account2.balance += reward;

        Transaction txn1 = new Transaction("REWARD", "SYSTEM", acc1, reward, "Offer1: Equal Balance Reward");
        Transaction txn2 = new Transaction("REWARD", "SYSTEM", acc2, reward, "Offer1: Equal Balance Reward");

        account1.addTransaction(txn1);
        account2.addTransaction(txn2);

        System.out.println("🎉 Offer 1 Applied: " + acc1 + " and " + acc2 + " received ₹" + reward + " each!");
    }

    /**
     * Display account statement
     * Time: O(t) where t = transactions
     */
    public void statement(String accountId) {
        if (!accounts.containsKey(accountId)) {
            System.out.println("❌ Account not found: " + accountId);
            return;
        }

        Account account = accounts.get(accountId);

        System.out.println("\n========== STATEMENT FOR " + accountId + " ==========");
        System.out.println("Current Balance: ₹" + String.format("%.4f", account.balance));

        if (account.fixedDeposit != null && account.fixedDeposit.active) {
            System.out.println("Fixed Deposit: ₹" + String.format("%.4f", account.fixedDeposit.amount));
            System.out.println("Remaining FD Transactions: " + account.fixedDeposit.remainingTransactions);
        }

        System.out.println("\nTransactions:");
        if (account.transactions.isEmpty()) {
            System.out.println("  No transactions");
        } else {
            for (int i = 0; i < account.transactions.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + account.transactions.get(i));
            }
        }
        System.out.println("=====================================\n");
    }

    /**
     * Display overview of all accounts
     * Time: O(n) where n = accounts
     */
    public void overview() {
        System.out.println("\n========== ACCOUNT OVERVIEW ==========");

        if (accounts.isEmpty()) {
            System.out.println("No accounts created");
        } else {
            for (Account account : accounts.values()) {
                if (!account.merged) {
                    System.out.println(account);
                }
            }
        }

        System.out.println("=====================================\n");
    }

    /**
     * Apply Offer 2: Top 3 spenders get ₹10, ₹5, ₹2
     * Time: O(n log n)
     */
    public void applyOffer2() {
        List<Account> topSpenders = accounts.values().stream()
            .filter(acc -> !acc.merged)
            .sorted((a, b) -> {
                // Sort by transaction count (descending)
                int countCmp = Integer.compare(b.transactionCount, a.transactionCount);
                if (countCmp != 0) return countCmp;

                // Then by balance (descending)
                int balanceCmp = Double.compare(b.balance, a.balance);
                if (balanceCmp != 0) return balanceCmp;

                // Then by creation time (ascending)
                return Long.compare(a.createdAt, b.createdAt);
            })
            .limit(3)
            .collect(Collectors.toList());

        double[] rewards = {10.0, 5.0, 2.0};

        for (int i = 0; i < topSpenders.size(); i++) {
            Account account = topSpenders.get(i);
            double reward = rewards[i];

            account.balance += reward;
            Transaction txn = new Transaction("REWARD", "SYSTEM", account.accountId, reward,
                "Offer2: Top " + (i + 1) + " Spender Reward");
            account.addTransaction(txn);

            System.out.println("🎉 Offer 2 Applied: " + account.accountId + " (Rank #" + (i + 1) +
                             ") received ₹" + reward);
        }
    }

    /**
     * Create fixed deposit for account
     * Time: O(1)
     */
    public void fixedDeposit(String accountId, double fdAmount) {
        if (!accounts.containsKey(accountId)) {
            System.out.println("❌ Account not found: " + accountId);
            return;
        }

        Account account = accounts.get(accountId);

        if (account.balance < fdAmount) {
            System.out.println("❌ Insufficient balance for FD");
            return;
        }

        account.fixedDeposit = new FixedDeposit(fdAmount);
        System.out.println("✓ Fixed Deposit created: ₹" + fdAmount + " for " + accountId);
    }

    /**
     * Check if FD should be dissolved or apply interest
     */
    private void checkFixedDepositBalance(String accountId) {
        Account account = accounts.get(accountId);

        if (account.fixedDeposit == null || !account.fixedDeposit.active) {
            return;
        }

        // Decrease remaining transactions
        account.fixedDeposit.remainingTransactions--;

        // If balance drops below FD amount, dissolve
        if (account.balance < account.fixedDeposit.amount) {
            System.out.println("⚠️ Fixed Deposit dissolved: balance dropped below FD amount");
            account.fixedDeposit.active = false;
            return;
        }

        // If 5 transactions completed, apply interest and close FD
        if (account.fixedDeposit.remainingTransactions <= 0) {
            double interest = 10.0;
            account.balance += interest;
            account.fixedDeposit.active = false;

            Transaction txn = new Transaction("REWARD", "SYSTEM", accountId, interest,
                "Fixed Deposit Interest");
            account.addTransaction(txn);

            System.out.println("✓ Fixed Deposit Interest Applied: ₹" + interest + " to " + accountId);
        }
    }

    /**
     * Merge two accounts
     * Time: O(t) where t = transactions
     */
    public void mergeAccounts(String primary, String secondary) {
        if (!accounts.containsKey(primary) || !accounts.containsKey(secondary)) {
            System.out.println("❌ Account not found");
            return;
        }

        if (primary.equals(secondary)) {
            System.out.println("❌ Cannot merge account with itself");
            return;
        }

        Account primaryAcc = accounts.get(primary);
        Account secondaryAcc = accounts.get(secondary);

        // Merge balances
        primaryAcc.balance += secondaryAcc.balance;

        // Merge transactions and update references
        for (Transaction txn : secondaryAcc.transactions) {
            if (txn.fromAccount.equals(secondary)) {
                txn.fromAccount = primary;
            }
            if (txn.toAccount.equals(secondary)) {
                txn.toAccount = primary;
            }
            primaryAcc.addTransaction(txn);
        }

        // Mark secondary as merged
        secondaryAcc.merged = true;

        System.out.println("✓ Accounts merged: " + secondary + " -> " + primary);
    }

    // ==================== DEMO ====================

    public static void main(String[] args) {
        DigitalWallet wallet = new DigitalWallet();

        System.out.println("\n========== DIGITAL WALLET DEMO ==========\n");

        // Create wallets
        System.out.println("--- Creating Wallets ---");
        wallet.createWallet("Harry", 100);
        wallet.createWallet("Ron", 95.7);
        wallet.createWallet("Hermione", 104);
        wallet.createWallet("Albus", 200);
        wallet.createWallet("Draco", 500);

        // Overview
        System.out.println("\n--- Initial Overview ---");
        wallet.overview();

        // Transfers
        System.out.println("--- Transfers ---");
        wallet.transferMoney("Harry", "Ron", 4.3); // Harry: 95.7, Ron: 100 -> Offer 1!
        wallet.transferMoney("Hermione", "Albus", 50);
        wallet.transferMoney("Draco", "Harry", 50);
        wallet.transferMoney("Ron", "Hermione", 20);

        // Overview after transfers
        System.out.println("--- Overview After Transfers ---");
        wallet.overview();

        // Statements
        System.out.println("--- Statements ---");
        wallet.statement("Harry");
        wallet.statement("Ron");

        // Fixed Deposit
        System.out.println("--- Fixed Deposit ---");
        wallet.fixedDeposit("Albus", 100);
        wallet.statement("Albus");

        // Offer 2
        System.out.println("--- Applying Offer 2 ---");
        wallet.applyOffer2();
        wallet.overview();

        // Merge accounts
        System.out.println("--- Merging Accounts ---");
        wallet.mergeAccounts("Harry", "Ron");
        wallet.statement("Harry");
        wallet.overview();
    }
}

