package company.ecommerce.ebay;

import java.util.*;

/**
 * Scheduled Payments with Cashback in Banking System
 *
 * Problem: Implement scheduled payments with automatic cashback mechanism
 *
 * Features:
 * - Schedule future payments with cashback
 * - Check payment status
 * - Process scheduled payments at specific timestamps
 * - Apply cashback on successful transfers
 * - Handle insufficient balance (payment fails)
 *
 * Payment States:
 * - SCHEDULED: Payment created, waiting for processing
 * - PROCESSED: Successfully transferred + cashback applied
 * - FAILED: Insufficient balance or error
 * - CANCELLED: User cancelled the payment
 *
 * Time Complexity:
 * - schedulePayment: O(1)
 * - getPaymentStatus: O(1)
 * - processScheduledPayments: O(p) where p = pending payments
 *
 * Space Complexity: O(p) for pending payments
 */
public class ScheduledPayments {

    static class Payment {
        String paymentId;
        String fromAccountId;
        String toAccountId;
        int amount;
        double cashbackPercentage;
        int scheduledTimestamp;
        String status; // SCHEDULED, PROCESSED, FAILED, CANCELLED

        public Payment(String id, String from, String to, int amount,
                      double cashback, int timestamp) {
            this.paymentId = id;
            this.fromAccountId = from;
            this.toAccountId = to;
            this.amount = amount;
            this.cashbackPercentage = cashback;
            this.scheduledTimestamp = timestamp;
            this.status = "SCHEDULED";
        }

        public int getCashbackAmount() {
            return (int) (amount * cashbackPercentage);
        }

        @Override
        public String toString() {
            return String.format("Payment %s: %s -> %s: %d (cashback: %.1f%%) at %d [%s]",
                paymentId, fromAccountId, toAccountId, amount,
                cashbackPercentage * 100, scheduledTimestamp, status);
        }
    }

    static class Account {
        String accountId;
        int balance;

        public Account(String id, int initialBalance) {
            this.accountId = id;
            this.balance = initialBalance;
        }

        @Override
        public String toString() {
            return String.format("%s: %d", accountId, balance);
        }
    }

    private Map<String, Account> accounts;
    private Map<String, Payment> payments;
    private Queue<Payment> pendingPayments; // Min-heap by scheduled timestamp
    private int paymentIdCounter;

    public ScheduledPayments() {
        this.accounts = new HashMap<>();
        this.payments = new HashMap<>();
        this.pendingPayments = new PriorityQueue<>(
            Comparator.comparingInt(p -> p.scheduledTimestamp)
        );
        this.paymentIdCounter = 0;
    }

    /**
     * Create an account
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
     * Schedule a payment for future execution
     * Time: O(log p) where p = pending payments
     */
    public String schedulePayment(String fromAccountId, String toAccountId,
                                  int timestamp, int amount, double cashbackPercentage) {
        if (!accounts.containsKey(fromAccountId) || !accounts.containsKey(toAccountId)) {
            System.out.println("❌ Account not found");
            return null;
        }

        if (fromAccountId.equals(toAccountId)) {
            System.out.println("❌ Cannot schedule payment to same account");
            return null;
        }

        if (amount <= 0) {
            System.out.println("❌ Amount must be positive");
            return null;
        }

        if (cashbackPercentage < 0 || cashbackPercentage > 1) {
            System.out.println("❌ Cashback percentage must be between 0 and 1");
            return null;
        }

        // Generate payment ID
        String paymentId = "P" + (++paymentIdCounter);

        // Create payment
        Payment payment = new Payment(paymentId, fromAccountId, toAccountId, amount,
                                     cashbackPercentage, timestamp);

        payments.put(paymentId, payment);
        pendingPayments.offer(payment);

        System.out.println("✓ Payment scheduled: " + paymentId);
        return paymentId;
    }

    /**
     * Get status of a payment
     * Time: O(1)
     */
    public String getPaymentStatus(String accountId, int timestamp, String paymentId) {
        if (!payments.containsKey(paymentId)) {
            System.out.println("❌ Payment not found: " + paymentId);
            return null;
        }

        Payment payment = payments.get(paymentId);

        // Verify account ownership (from account)
        if (!payment.fromAccountId.equals(accountId)) {
            System.out.println("❌ Not authorized to check this payment");
            return null;
        }

        return payment.status;
    }

    /**
     * Process all scheduled payments at current timestamp
     * Time: O(p log p) where p = payments to process
     */
    public void processScheduledPayments(int currentTimestamp) {
        System.out.println("\n--- Processing Payments at Timestamp " + currentTimestamp + " ---");

        List<Payment> processedList = new ArrayList<>();

        while (!pendingPayments.isEmpty()) {
            Payment payment = pendingPayments.peek();

            // If payment is scheduled after current timestamp, stop
            if (payment.scheduledTimestamp > currentTimestamp) {
                break;
            }

            pendingPayments.poll();

            // Skip if already processed or failed
            if (!payment.status.equals("SCHEDULED")) {
                continue;
            }

            // Check if from account has sufficient balance
            Account fromAccount = accounts.get(payment.fromAccountId);

            if (fromAccount.balance >= payment.amount) {
                // Process payment
                Account toAccount = accounts.get(payment.toAccountId);

                fromAccount.balance -= payment.amount;
                toAccount.balance += payment.amount;

                // Apply cashback
                int cashback = payment.getCashbackAmount();
                fromAccount.balance += cashback;

                payment.status = "PROCESSED";

                System.out.println("✓ " + payment.paymentId + ": Processed " + payment.amount +
                                 " (Cashback: " + cashback + ")");
            } else {
                // Payment failed - insufficient balance
                payment.status = "FAILED";

                System.out.println("❌ " + payment.paymentId + ": Failed - Insufficient balance");
            }

            processedList.add(payment);
        }

        if (processedList.isEmpty()) {
            System.out.println("No payments to process");
        }
    }

    /**
     * Cancel a scheduled payment
     * Time: O(1) logically, O(p) if searching in queue
     */
    public void cancelPayment(String paymentId) {
        if (!payments.containsKey(paymentId)) {
            System.out.println("❌ Payment not found: " + paymentId);
            return;
        }

        Payment payment = payments.get(paymentId);

        if (!payment.status.equals("SCHEDULED")) {
            System.out.println("❌ Cannot cancel payment with status: " + payment.status);
            return;
        }

        payment.status = "CANCELLED";
        System.out.println("✓ Payment cancelled: " + paymentId);
    }

    /**
     * Display account balances
     */
    public void displayAccounts() {
        System.out.println("\n--- Accounts ---");
        for (Account account : accounts.values()) {
            System.out.println("  " + account);
        }
    }

    /**
     * Display payment details
     */
    public void displayPayments() {
        System.out.println("\n--- All Payments ---");
        for (Payment payment : payments.values()) {
            System.out.println("  " + payment);
        }
    }

    // ==================== DEMO ====================

    public static void main(String[] args) {
        ScheduledPayments system = new ScheduledPayments();

        System.out.println("\n========== SCHEDULED PAYMENTS WITH CASHBACK DEMO ==========\n");

        // Create accounts
        System.out.println("--- Creating Accounts ---");
        system.createAccount("ACC001", 1000);
        system.createAccount("ACC002", 500);
        system.createAccount("ACC003", 750);
        system.displayAccounts();

        // Schedule payments
        System.out.println("\n--- Scheduling Payments ---");
        String p1 = system.schedulePayment("ACC001", "ACC002", 10, 100, 0.05);
        String p2 = system.schedulePayment("ACC001", "ACC003", 15, 200, 0.1);
        String p3 = system.schedulePayment("ACC002", "ACC001", 20, 600, 0.02); // Will fail

        // Check status before processing
        System.out.println("\n--- Payment Status at Timestamp 5 ---");
        System.out.println("P1 Status: " + system.getPaymentStatus("ACC001", 5, p1));
        System.out.println("P2 Status: " + system.getPaymentStatus("ACC001", 5, p2));
        System.out.println("P3 Status: " + system.getPaymentStatus("ACC002", 5, p3));

        // Process payments at different timestamps
        System.out.println("\n--- Processing Payments at Timestamp 10 ---");
        system.processScheduledPayments(10);
        system.displayAccounts();

        System.out.println("\n--- Payment Status at Timestamp 11 ---");
        System.out.println("P1 Status: " + system.getPaymentStatus("ACC001", 11, p1));

        System.out.println("\n--- Processing Payments at Timestamp 15 ---");
        system.processScheduledPayments(15);
        system.displayAccounts();

        System.out.println("\n--- Processing Payments at Timestamp 20 ---");
        system.processScheduledPayments(20);
        system.displayAccounts();

        System.out.println("\n--- Final Payment Status ---");
        System.out.println("P1 Status: " + system.getPaymentStatus("ACC001", 25, p1));
        System.out.println("P2 Status: " + system.getPaymentStatus("ACC001", 25, p2));
        System.out.println("P3 Status: " + system.getPaymentStatus("ACC002", 25, p3));

        // Display all payments
        system.displayPayments();

        // Test case 2: Cancellation
        System.out.println("\n========== CANCELLATION TEST ==========\n");

        ScheduledPayments system2 = new ScheduledPayments();
        system2.createAccount("A", 1000);
        system2.createAccount("B", 500);

        String p4 = system2.schedulePayment("A", "B", 50, 100, 0.05);
        System.out.println("Scheduled payment: " + p4);

        system2.cancelPayment(p4);
        System.out.println("After cancel - Status: " + system2.getPaymentStatus("A", 45, p4));

        system2.processScheduledPayments(50);
        System.out.println("After processing - Status: " + system2.getPaymentStatus("A", 55, p4));
        system2.displayAccounts();
    }
}

