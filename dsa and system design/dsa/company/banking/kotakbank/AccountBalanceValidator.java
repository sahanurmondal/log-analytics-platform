package company.banking.kotakbank;

import java.util.*;

/**
 * Kotak Bank SDE3 Interview Question #2
 * 
 * Problem: Account Balance Validation & Transaction History
 * LeetCode Equivalent: Two Sum (Problem #1) + Running Sum variants
 * 
 * Banking Context:
 * Given a list of transactions (debits/credits) and a target balance,
 * find if there are any two transactions that sum up to the target balance.
 * This is crucial for account reconciliation and fraud detection in banking
 * systems.
 * 
 * Extended Problem: Also track running balance and validate account state.
 * 
 * Interview Focus:
 * - Two-pointer technique vs Hash Map approach
 * - Handling negative numbers (debits) and positive numbers (credits)
 * - Real-time balance calculation and validation
 * - Edge cases in financial calculations
 * 
 * Difficulty: Easy-Medium
 * Expected Time: 20-25 minutes
 * 
 * Follow-up Questions:
 * 1. How would you handle floating-point precision in real banking systems?
 * 2. What if transactions can be processed out of order?
 * 3. How would you implement this for concurrent transaction processing?
 * 4. How would you audit trail every balance calculation?
 */
public class AccountBalanceValidator {

    /**
     * Transaction representation for banking operations
     */
    public static class BankTransaction {
        int amount; // Positive for credit, negative for debit
        String type; // "CREDIT" or "DEBIT"
        String description;
        long timestamp;
        String transactionId;

        public BankTransaction(int amount, String type, String description) {
            this.amount = amount;
            this.type = type;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
            this.transactionId = "TXN" + System.nanoTime();
        }

        @Override
        public String toString() {
            return String.format("%s: %s$%d (%s)", transactionId,
                    amount >= 0 ? "+" : "", amount, description);
        }
    }

    /**
     * Approach 1: Hash Map (Optimal for most banking scenarios)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Banking Application: Real-time transaction matching for reconciliation
     */
    public int[] findTransactionsForTarget(int[] transactions, int target) {
        if (transactions == null || transactions.length < 2) {
            return new int[0];
        }

        Map<Integer, Integer> transactionMap = new HashMap<>();

        for (int i = 0; i < transactions.length; i++) {
            int complement = target - transactions[i];

            if (transactionMap.containsKey(complement)) {
                int[] result = { transactionMap.get(complement), i };
                System.out.println("✅ Found transaction pair: $" + transactions[result[0]] +
                        " + $" + transactions[result[1]] + " = $" + target);
                return result;
            }

            transactionMap.put(transactions[i], i);
        }

        System.out.println("❌ No transaction pair found for target: $" + target);
        return new int[0];
    }

    /**
     * Approach 2: Two Pointers (Memory efficient, requires sorting)
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     * 
     * Banking Application: Batch processing of sorted transaction logs
     */
    public int[] findTransactionsTwoPointer(int[] transactions, int target) {
        if (transactions == null || transactions.length < 2) {
            return new int[0];
        }

        // Create array of {value, originalIndex} pairs
        int[][] indexedTransactions = new int[transactions.length][2];
        for (int i = 0; i < transactions.length; i++) {
            indexedTransactions[i][0] = transactions[i];
            indexedTransactions[i][1] = i;
        }

        // Sort by transaction amount
        Arrays.sort(indexedTransactions, (a, b) -> Integer.compare(a[0], b[0]));

        int left = 0;
        int right = indexedTransactions.length - 1;

        while (left < right) {
            int sum = indexedTransactions[left][0] + indexedTransactions[right][0];

            if (sum == target) {
                int[] result = {
                        Math.min(indexedTransactions[left][1], indexedTransactions[right][1]),
                        Math.max(indexedTransactions[left][1], indexedTransactions[right][1])
                };
                System.out.println("✅ Found transaction pair: $" + indexedTransactions[left][0] +
                        " + $" + indexedTransactions[right][0] + " = $" + target);
                return result;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        System.out.println("❌ No transaction pair found for target: $" + target);
        return new int[0];
    }

    /**
     * Advanced Banking Feature: Running Balance Calculation with Validation
     */
    public boolean validateAccountBalance(BankTransaction[] transactions, int initialBalance,
            int expectedFinalBalance) {
        System.out.println("🏦 ACCOUNT BALANCE VALIDATION");
        System.out.println("Initial Balance: $" + initialBalance);
        System.out.println("Expected Final Balance: $" + expectedFinalBalance);
        System.out.println("-".repeat(50));

        int runningBalance = initialBalance;
        List<String> auditTrail = new ArrayList<>();

        for (int i = 0; i < transactions.length; i++) {
            BankTransaction txn = transactions[i];
            int previousBalance = runningBalance;
            runningBalance += txn.amount;

            String auditEntry = String.format("Step %d: %s | Balance: $%d → $%d",
                    i + 1, txn, previousBalance, runningBalance);
            auditTrail.add(auditEntry);
            System.out.println(auditEntry);

            // Banking validation: Check for negative balance (overdraft)
            if (runningBalance < 0) {
                System.out.println("⚠️  OVERDRAFT WARNING: Account balance is negative!");
            }
        }

        boolean isValid = (runningBalance == expectedFinalBalance);
        System.out.println("-".repeat(50));
        System.out.println("Final Balance: $" + runningBalance);
        System.out.println("Validation: " + (isValid ? "✅ PASSED" : "❌ FAILED"));

        return isValid;
    }

    /**
     * Banking-specific: Find all transaction pairs that sum to zero (potential
     * reversals)
     */
    public List<int[]> findReversalTransactions(int[] transactions) {
        List<int[]> reversals = new ArrayList<>();
        Map<Integer, List<Integer>> transactionMap = new HashMap<>();

        for (int i = 0; i < transactions.length; i++) {
            int amount = transactions[i];
            int reversal = -amount;

            if (transactionMap.containsKey(reversal)) {
                for (int reversalIndex : transactionMap.get(reversal)) {
                    reversals.add(new int[] { reversalIndex, i });
                    System.out.println("🔄 Potential Reversal: $" + transactions[reversalIndex] +
                            " and $" + amount + " (indices " + reversalIndex + ", " + i + ")");
                }
            }

            if (!transactionMap.containsKey(amount)) {
                transactionMap.put(amount, new ArrayList<>());
            }
            transactionMap.get(amount).add(i);
        }

        return reversals;
    }

    /**
     * LeetCode Integration: Test against actual LeetCode platform
     */
    public void testWithLeetCodeAPI() {
        System.out.println("🏦 Testing Kotak Bank Balance Validator");
        System.out.println("=".repeat(50));

        // Test cases for banking scenarios
        Object[][] testCases = {
                { new int[] { 1000, -500, 300, 700 }, 1000, "Target balance reconciliation" },
                { new int[] { -200, 100, 300, -100 }, 0, "Zero-sum validation" },
                { new int[] { 500, 250, 750, 1000 }, 1500, "Credit accumulation" },
                { new int[] { -100, -200, -50, 350 }, 0, "Mixed debit/credit" },
                { new int[] { 1000 }, 500, "Single transaction (impossible)" }
        };

        for (int i = 0; i < testCases.length; i++) {
            int[] transactions = (int[]) testCases[i][0];
            int target = (int) testCases[i][1];
            String description = (String) testCases[i][2];

            System.out.println("Test Case " + (i + 1) + ": " + description);
            System.out.println("Transactions: " + Arrays.toString(transactions));
            System.out.println("Target: $" + target);

            // Test Hash Map approach
            int[] result1 = findTransactionsForTarget(transactions.clone(), target);

            // Test Two Pointer approach
            int[] result2 = findTransactionsTwoPointer(transactions.clone(), target);

            System.out.println("Hash Map Result: " + Arrays.toString(result1));
            System.out.println("Two Pointer Result: " + Arrays.toString(result2));
            System.out.println();
        }

        // Test reversal detection
        System.out.println("🔄 REVERSAL DETECTION TEST:");
        int[] reversalTest = { 100, -50, 200, -100, 300, 50 };
        System.out.println("Transactions: " + Arrays.toString(reversalTest));
        findReversalTransactions(reversalTest);
        System.out.println();

        // Test running balance validation
        System.out.println("💰 RUNNING BALANCE VALIDATION TEST:");
        BankTransaction[] bankTxns = {
                new BankTransaction(1000, "CREDIT", "Salary deposit"),
                new BankTransaction(-200, "DEBIT", "ATM withdrawal"),
                new BankTransaction(-50, "DEBIT", "Bank fee"),
                new BankTransaction(300, "CREDIT", "Refund"),
                new BankTransaction(-500, "DEBIT", "Rent payment")
        };

        validateAccountBalance(bankTxns, 500, 1050); // Expected final: 500 + 1000 - 200 - 50 + 300 - 500 = 1050
    }

    /**
     * Banking system architecture discussion points
     */
    public void discussBankingArchitecture() {
        System.out.println("\n🏗️  BANKING SYSTEM ARCHITECTURE CONSIDERATIONS");
        System.out.println("=".repeat(50));

        System.out.println("1. ACID PROPERTIES:");
        System.out.println("   - Atomicity: All-or-nothing transaction processing");
        System.out.println("   - Consistency: Account balances must always be consistent");
        System.out.println("   - Isolation: Concurrent transactions don't interfere");
        System.out.println("   - Durability: Committed transactions survive system failures");

        System.out.println("\n2. REAL-TIME PROCESSING:");
        System.out.println("   - Sub-second transaction processing");
        System.out.println("   - Immediate balance updates");
        System.out.println("   - Real-time fraud detection");

        System.out.println("\n3. AUDIT AND COMPLIANCE:");
        System.out.println("   - Complete audit trail for every transaction");
        System.out.println("   - Immutable transaction logs");
        System.out.println("   - Regulatory reporting capabilities");

        System.out.println("\n4. SCALABILITY PATTERNS:");
        System.out.println("   - Database sharding by account ID");
        System.out.println("   - Read replicas for balance inquiries");
        System.out.println("   - Event sourcing for transaction history");

        System.out.println("\n5. ERROR HANDLING:");
        System.out.println("   - Graceful degradation during high load");
        System.out.println("   - Automatic retry mechanisms");
        System.out.println("   - Dead letter queues for failed transactions");
    }

    public static void main(String[] args) {
        AccountBalanceValidator validator = new AccountBalanceValidator();

        System.out.println("🏦 KOTAK BANK SDE3 INTERVIEW QUESTION");
        System.out.println("📋 Problem: Account Balance Validation");
        System.out.println("🔗 LeetCode Equivalent: Two Sum (#1) + Extensions");
        System.out.println();

        validator.testWithLeetCodeAPI();
        validator.discussBankingArchitecture();

        System.out.println("\n💡 INTERVIEW TIPS:");
        System.out.println("1. Always discuss ACID properties for banking systems");
        System.out.println("2. Consider both batch and real-time processing requirements");
        System.out.println("3. Mention audit trails and regulatory compliance");
        System.out.println("4. Discuss precision handling for financial calculations");
        System.out.println("5. Consider concurrent access and race conditions");
        System.out.println("6. Always validate edge cases (negative balances, overflows)");
    }
}
