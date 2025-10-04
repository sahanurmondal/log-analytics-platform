package miscellaneous.stripe;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design Stripe Payment Processing System
 * 
 * Description:
 * Design a payment processing system that supports:
 * - Payment processing and validation
 * - Multiple payment methods
 * - Fraud detection
 * - Subscription billing
 * - Refunds and chargebacks
 * 
 * Company: Stripe
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignPaymentSystem {

    enum PaymentStatus {
        PENDING, SUCCEEDED, FAILED, REFUNDED, DISPUTED
    }

    enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, DIGITAL_WALLET
    }

    class Payment {
        String id;
        String customerId;
        String merchantId;
        double amount;
        String currency;
        PaymentMethod method;
        PaymentStatus status;
        long timestamp;
        String description;
        Map<String, String> metadata;

        Payment(String customerId, String merchantId, double amount, String currency, PaymentMethod method) {
            this.id = UUID.randomUUID().toString();
            this.customerId = customerId;
            this.merchantId = merchantId;
            this.amount = amount;
            this.currency = currency;
            this.method = method;
            this.status = PaymentStatus.PENDING;
            this.timestamp = System.currentTimeMillis();
            this.metadata = new HashMap<>();
        }
    }

    class FraudDetector {
        public boolean isFraudulent(Payment payment) {
            // Check for suspicious patterns
            if (payment.amount > 10000) {
                return true; // Large amount
            }

            // Check velocity - too many payments in short time
            long recentPayments = payments.values().stream()
                    .filter(p -> p.customerId.equals(payment.customerId))
                    .filter(p -> System.currentTimeMillis() - p.timestamp < 3600000) // 1 hour
                    .count();

            if (recentPayments > 5) {
                return true;
            }

            // Check for unusual location patterns
            return false;
        }
    }

    class PaymentProcessor {
        public boolean processPayment(Payment payment) {
            // Validate payment
            if (!validatePayment(payment)) {
                payment.status = PaymentStatus.FAILED;
                return false;
            }

            // Check for fraud
            if (fraudDetector.isFraudulent(payment)) {
                payment.status = PaymentStatus.FAILED;
                return false;
            }

            // Process with external payment gateway
            boolean success = processWithGateway(payment);
            payment.status = success ? PaymentStatus.SUCCEEDED : PaymentStatus.FAILED;

            return success;
        }

        private boolean validatePayment(Payment payment) {
            return payment.amount > 0 &&
                    payment.customerId != null &&
                    payment.merchantId != null;
        }

        private boolean processWithGateway(Payment payment) {
            // Simulate external payment processing
            return Math.random() > 0.1; // 90% success rate
        }
    }

    class SubscriptionManager {
        class Subscription {
            String id;
            String customerId;
            String planId;
            double amount;
            int intervalDays;
            long nextBillingDate;
            boolean active;

            Subscription(String customerId, String planId, double amount, int intervalDays) {
                this.id = UUID.randomUUID().toString();
                this.customerId = customerId;
                this.planId = planId;
                this.amount = amount;
                this.intervalDays = intervalDays;
                this.nextBillingDate = System.currentTimeMillis() + (intervalDays * 24 * 60 * 60 * 1000L);
                this.active = true;
            }
        }

        private Map<String, Subscription> subscriptions = new HashMap<>();

        public String createSubscription(String customerId, String planId, double amount, int intervalDays) {
            Subscription subscription = new Subscription(customerId, planId, amount, intervalDays);
            subscriptions.put(subscription.id, subscription);
            return subscription.id;
        }

        public void processBilling() {
            long currentTime = System.currentTimeMillis();

            for (Subscription subscription : subscriptions.values()) {
                if (subscription.active && subscription.nextBillingDate <= currentTime) {
                    // Create recurring payment
                    Payment payment = new Payment(
                            subscription.customerId,
                            "merchant_subscription",
                            subscription.amount,
                            "USD",
                            PaymentMethod.CREDIT_CARD);

                    if (paymentProcessor.processPayment(payment)) {
                        subscription.nextBillingDate = currentTime + (subscription.intervalDays * 24 * 60 * 60 * 1000L);
                    } else {
                        // Handle failed payment
                        handleFailedSubscriptionPayment(subscription);
                    }
                }
            }
        }

        private void handleFailedSubscriptionPayment(Subscription subscription) {
            // Could implement retry logic, email notifications, etc.
            System.out.println("Failed subscription payment for: " + subscription.id);
        }
    }

    class RefundManager {
        public String processRefund(String paymentId, double amount) {
            Payment originalPayment = payments.get(paymentId);
            if (originalPayment == null || originalPayment.status != PaymentStatus.SUCCEEDED) {
                return null;
            }

            if (amount > originalPayment.amount) {
                return null; // Cannot refund more than original amount
            }

            // Create refund payment
            Payment refund = new Payment(
                    originalPayment.customerId,
                    originalPayment.merchantId,
                    -amount, // Negative amount for refund
                    originalPayment.currency,
                    originalPayment.method);

            refund.status = PaymentStatus.REFUNDED;
            payments.put(refund.id, refund);

            // Update original payment if full refund
            if (amount == originalPayment.amount) {
                originalPayment.status = PaymentStatus.REFUNDED;
            }

            return refund.id;
        }
    }

    private Map<String, Payment> payments = new HashMap<>();
    private FraudDetector fraudDetector = new FraudDetector();
    private PaymentProcessor paymentProcessor = new PaymentProcessor();
    private SubscriptionManager subscriptionManager = new SubscriptionManager();
    private RefundManager refundManager = new RefundManager();

    public String createPayment(String customerId, String merchantId, double amount,
            String currency, PaymentMethod method) {
        Payment payment = new Payment(customerId, merchantId, amount, currency, method);
        payments.put(payment.id, payment);

        // Process payment
        paymentProcessor.processPayment(payment);

        return payment.id;
    }

    public Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    public String createSubscription(String customerId, String planId, double amount, int intervalDays) {
        return subscriptionManager.createSubscription(customerId, planId, amount, intervalDays);
    }

    public String refundPayment(String paymentId, double amount) {
        return refundManager.processRefund(paymentId, amount);
    }

    public static void main(String[] args) {
        DesignPaymentSystem stripe = new DesignPaymentSystem();

        // Create payment
        String paymentId = stripe.createPayment("customer1", "merchant1", 100.0, "USD", PaymentMethod.CREDIT_CARD);
        System.out.println("Payment created: " + paymentId);

        // Check payment status
        Payment payment = stripe.getPayment(paymentId);
        System.out.println("Payment status: " + payment.status);

        // Create subscription
        String subscriptionId = stripe.createSubscription("customer1", "plan1", 29.99, 30);
        System.out.println("Subscription created: " + subscriptionId);

        // Process refund
        String refundId = stripe.refundPayment(paymentId, 50.0);
        System.out.println("Refund processed: " + refundId);
    }
}
