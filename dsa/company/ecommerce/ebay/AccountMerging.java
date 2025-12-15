package company.ecommerce.ebay;

import java.util.*;

/**
 * Account Merging with Transaction History Update
 *
 * Problem: Merge two accounts while maintaining transaction integrity
 *
 * Features:
 * - Create accounts with balances
 * - Transfer money between accounts
 * - Merge accounts with balance consolidation
 * - Update all transaction references
 * - Prevent operations on merged accounts
 *
 * Merge Logic:
 * 1. Combine balances: primary.balance += secondary.balance
 * 2. Update transactions: All references to secondary -> primary
 * 3. Mark secondary as merged/inactive
 * 4. Block future operations on secondary
 *
 * Time Complexity:
 * - createAccount: O(1)
 * - deposit: O(1)
 * - transfer: O(1)
 * - mergeAccounts: O(t) where t = transactions in secondary account
 * - getStatement: O(t) where t = account transactions
 *
 * Space Complexity: O(n + t) where n = accounts, t = total transactions
 */
public class AccountMerging {

    static class Transaction {
        String transactionId;
        String fromId;
        String toId;
        int amount;
        long timestamp;
        String description;
        boolean isReversed; // For tracking if transaction was updated during merge

        public Transaction(String id, String from, String to, int amount, String desc) {
            this.transactionId = id;
            this.fromId = from;
            this.toId = to;
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
            this.description = desc;
            this.isReversed = false;
        }

        /**
         * Update transaction reference (during merge)
         */
        public void updateReference(String oldId, String newId) {
            if (this.fromId.equals(oldId)) {
                this.fromId = newId;
            }
            if (this.toId.equals(oldId)) {
                this.toId = newId;
            }
        }

        @Override
        public String toString() {
            return String.format("%s: %s -> %s: %d (%s)",
                transactionId, fromId, toId, amount, description);
        }
    }

    static class Account {
        String accountId;
        int balance;
        long createdAt;
        List<Transaction> transactions;
        boolean merged;
        String mergedIntoAccount; // If merged, which account it was merged into

        public Account(String id, int initialBalance) {
            this.accountId = id;
            this.balance = initialBalance;
            this.createdAt = System.currentTimeMillis();
            this.transactions = new ArrayList<>();
            this.merged = false;
            this.mergedIntoAccount = null;
        }

        public void addTransaction(Transaction txn) {
            transactions.add(txn);
        }

        @Override
        public String toString() {
            String status = merged ? " [MERGED into " + mergedIntoAccount + "]" : "";
            return String.format("%s: Balance=%d, Transactions=%d%s",
                accountId, balance, transactions.size(), status);
        }
    }

    private Map<String, Account> accounts;
    private Map<String, Transaction> allTransactions;
    private int transactionCounter;

    public AccountMerging() {
        this.accounts = new HashMap<>();
        this.allTransactions = new HashMap<>();
        this.transactionCounter = 0;
    }

    /**
     * Create a new account
     * Time: O(1)
     */
    public void createAccount(String accountId, int initialBalance) {
        if (accounts.containsKey(accountId)) {
            System.out.println("❌ Account already exists: " + accountId);
            return;
        }

        accounts.put(accountId, new Account(accountId, initialBalance));
        System.out.println("✓ Account created: " + accountId + " with balance " + initialBalance);
    }

    /**
     * Deposit money to account
     * Time: O(1)
     */
    public void deposit(String accountId, int amount) {
        if (!accounts.containsKey(accountId)) {
            System.out.println("❌ Account not found: " + accountId);
            return;
        }

        Account account = accounts.get(accountId);

        if (account.merged) {
            System.out.println("❌ Cannot deposit to merged account: " + accountId);
            return;
        }

        if (amount <= 0) {
            System.out.println("❌ Amount must be positive");
            return;
        }

        account.balance += amount;

        String txnId = "TXN" + (++transactionCounter);
        Transaction txn = new Transaction(txnId, "SYSTEM", accountId, amount, "Deposit");
        account.addTransaction(txn);
        allTransactions.put(txnId, txn);

        System.out.println("✓ Deposit: " + amount + " to " + accountId);
    }

    /**
     * Transfer money between accounts
     * Time: O(1)
     */
    public void transfer(String fromId, String toId, int amount) {
        if (!accounts.containsKey(fromId) || !accounts.containsKey(toId)) {
            System.out.println("❌ Account not found");
            return;
        }

        Account fromAccount = accounts.get(fromId);
        Account toAccount = accounts.get(toId);

        // Check if accounts are merged
        if (fromAccount.merged) {
            System.out.println("❌ Cannot transfer from merged account: " + fromId);
            return;
        }

        if (toAccount.merged) {
            System.out.println("❌ Cannot transfer to merged account: " + toId);
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

        if (fromAccount.balance < amount) {
            System.out.println("❌ Insufficient balance in " + fromId);
            return;
        }

        // Perform transfer
        fromAccount.balance -= amount;
        toAccount.balance += amount;

        // Record transaction
        String txnId = "TXN" + (++transactionCounter);
        Transaction txn = new Transaction(txnId, fromId, toId, amount, "Transfer");
        fromAccount.addTransaction(txn);
        toAccount.addTransaction(txn);
        allTransactions.put(txnId, txn);

        System.out.println("✓ Transfer: " + fromId + " -> " + toId + ": " + amount);
    }

    /**
     * Merge two accounts
     * Time: O(t) where t = transactions in secondary account
     *
     * Process:
     * 1. Verify both accounts exist and are distinct
     * 2. Combine balances
     * 3. Update all transaction references
     * 4. Mark secondary as merged
     */
    public void mergeAccounts(String primaryId, String secondaryId) {
        if (!accounts.containsKey(primaryId) || !accounts.containsKey(secondaryId)) {
            System.out.println("❌ Account not found");
            return;
        }

        if (primaryId.equals(secondaryId)) {
            System.out.println("❌ Cannot merge account with itself");
            return;
        }

        Account primary = accounts.get(primaryId);
        Account secondary = accounts.get(secondaryId);

        if (primary.merged) {
            System.out.println("❌ Primary account is already merged");
            return;
        }

        if (secondary.merged) {
            System.out.println("❌ Secondary account is already merged");
            return;
        }

        // Step 1: Combine balances
        int combinedBalance = primary.balance + secondary.balance;
        primary.balance = combinedBalance;

        System.out.println("✓ Step 1: Combined balances: " + secondary.balance + " + " +
                         (combinedBalance - secondary.balance) + " = " + combinedBalance);

        // Step 2: Update transaction references
        System.out.println("✓ Step 2: Updating transaction references...");

        int updatedCount = 0;

        // Update all transactions involving secondary account
        for (Transaction txn : secondary.transactions) {
            String originalFromId = txn.fromId;
            String originalToId = txn.toId;

            txn.updateReference(secondaryId, primaryId);

            // Also add this transaction to primary account if not already there
            boolean alreadyInPrimary = primary.transactions.stream()
                .anyMatch(t -> t.transactionId.equals(txn.transactionId));

            if (!alreadyInPrimary) {
                primary.transactions.add(txn);
                updatedCount++;
            }

            // Log the update
            if (!originalFromId.equals(txn.fromId) || !originalToId.equals(txn.toId)) {
                System.out.println("  " + txn.transactionId + ": " + originalFromId + "->" +
                                 txn.fromId + " (from), " + originalToId + "->" + txn.toId + " (to)");
            }
        }

        System.out.println("✓ Updated " + updatedCount + " transaction references");

        // Step 3: Mark secondary as merged
        secondary.merged = true;
        secondary.mergedIntoAccount = primaryId;

        System.out.println("✓ Step 3: Secondary account marked as merged");
        System.out.println("✓ Merge complete: " + secondaryId + " -> " + primaryId);
    }

    /**
     * Get account statement
     * Time: O(t) where t = transactions
     */
    public void getStatement(String accountId) {
        if (!accounts.containsKey(accountId)) {
            System.out.println("❌ Account not found: " + accountId);
            return;
        }

        Account account = accounts.get(accountId);

        System.out.println("\n========== STATEMENT FOR " + accountId + " ==========");
        System.out.println("Balance: " + account.balance);
        System.out.println("Status: " + (account.merged ? "MERGED into " + account.mergedIntoAccount : "ACTIVE"));
        System.out.println("Transactions (" + account.transactions.size() + "):");

        if (account.transactions.isEmpty()) {
            System.out.println("  No transactions");
        } else {
            for (int i = 0; i < account.transactions.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + account.transactions.get(i));
            }
        }
        System.out.println("==========================================\n");
    }

    /**
     * Display all accounts
     */
    public void displayAccounts() {
        System.out.println("\n--- All Accounts ---");
        for (Account account : accounts.values()) {
            System.out.println("  " + account);
        }
    }

    /**
     * Display all transactions
     */
    public void displayAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        for (Transaction txn : allTransactions.values()) {
            System.out.println("  " + txn);
        }
    }

    // ==================== DEMO ====================

    public static void main(String[] args) {
        AccountMerging system = new AccountMerging();

        System.out.println("\n========== ACCOUNT MERGING DEMO ==========\n");

        // Create accounts
        System.out.println("--- Creating Accounts ---");
        system.createAccount("ACC001", 500);
        system.createAccount("ACC002", 300);
        system.createAccount("ACC003", 200);
        system.createAccount("ACC004", 150);
        system.createAccount("ACC005", 250);

        // Perform transactions
        System.out.println("\n--- Transactions ---");
        system.transfer("ACC001", "ACC003", 100);  // T1: ACC001 -> ACC003
        system.transfer("ACC004", "ACC002", 50);   // T2: ACC004 -> ACC002
        system.transfer("ACC002", "ACC005", 20);   // T3: ACC002 -> ACC005
        system.transfer("ACC001", "ACC002", 75);   // T4: ACC001 -> ACC002

        // Display before merge
        System.out.println("\n--- Before Merge ---");
        system.displayAccounts();
        system.getStatement("ACC001");
        system.getStatement("ACC002");
        system.getStatement("ACC003");

        // Merge ACC002 into ACC001
        System.out.println("\n--- Merging ACC002 into ACC001 ---");
        system.mergeAccounts("ACC001", "ACC002");

        // Display after merge
        System.out.println("\n--- After Merge ---");
        system.displayAccounts();

        // Show detailed statements
        System.out.println("\n--- Statements After Merge ---");
        system.getStatement("ACC001");
        system.getStatement("ACC002");

        // Try operations on merged account
        System.out.println("--- Attempting Operations on Merged Account ---");
        system.transfer("ACC002", "ACC003", 50);
        system.transfer("ACC001", "ACC002", 100);
        system.deposit("ACC002", 100);

        // Test case 2: Multiple merges
        System.out.println("\n========== COMPLEX MERGE TEST ==========\n");

        AccountMerging system2 = new AccountMerging();

        System.out.println("--- Setup ---");
        system2.createAccount("MAIN", 1000);
        system2.createAccount("SUB1", 200);
        system2.createAccount("SUB2", 150);

        system2.transfer("MAIN", "SUB1", 100);
        system2.transfer("SUB1", "MAIN", 50);
        system2.transfer("SUB2", "MAIN", 75);
        system2.transfer("MAIN", "SUB2", 25);

        System.out.println("\n--- Before Merges ---");
        system2.displayAccounts();

        System.out.println("\n--- Merge SUB1 into MAIN ---");
        system2.mergeAccounts("MAIN", "SUB1");
        system2.displayAccounts();

        System.out.println("\n--- Merge SUB2 into MAIN ---");
        system2.mergeAccounts("MAIN", "SUB2");
        system2.displayAccounts();

        System.out.println("\n--- Final Statements ---");
        system2.getStatement("MAIN");
        system2.getStatement("SUB1");
        system2.getStatement("SUB2");
    }
}

