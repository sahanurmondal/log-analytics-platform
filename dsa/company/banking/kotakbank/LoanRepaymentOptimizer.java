package company.banking.kotakbank;

import java.util.*;

/**
 * Kotak Bank SDE3 Interview Question #3
 * 
 * Problem: Loan Repayment Schedule Optimization
 * LeetCode Equivalent: Coin Change (Problem #322) + Dynamic Programming
 * variants
 * 
 * Banking Context:
 * Given different denominations of payments (EMI amounts) and a target loan
 * amount,
 * find the minimum number of payments needed to fully repay the loan.
 * This is crucial for loan restructuring and payment optimization in banking.
 * 
 * Extended Problem: Handle partial payments, interest calculations, and payment
 * scheduling.
 * 
 * Interview Focus:
 * - Dynamic programming approach for optimization problems
 * - Greedy vs DP trade-offs in financial scenarios
 * - Handling edge cases in loan calculations
 * - Memory optimization for large loan amounts
 * 
 * Difficulty: Medium-Hard
 * Expected Time: 25-30 minutes
 * 
 * Follow-up Questions:
 * 1. How would you handle variable interest rates?
 * 2. What if payments can be made partially?
 * 3. How would you optimize for minimum interest paid rather than minimum
 * payments?
 * 4. How would you handle payment scheduling and due dates?
 */
public class LoanRepaymentOptimizer {

    /**
     * Payment option representation for loan repayment
     */
    public static class PaymentOption {
        int amount;
        String type;
        double interestRate;
        int maxUsage; // -1 for unlimited

        public PaymentOption(int amount, String type, double interestRate) {
            this.amount = amount;
            this.type = type;
            this.interestRate = interestRate;
            this.maxUsage = -1; // unlimited by default
        }

        public PaymentOption(int amount, String type, double interestRate, int maxUsage) {
            this.amount = amount;
            this.type = type;
            this.interestRate = interestRate;
            this.maxUsage = maxUsage;
        }

        @Override
        public String toString() {
            return String.format("$%d %s (%.2f%% interest)", amount, type, interestRate * 100);
        }
    }

    /**
     * Approach 1: Dynamic Programming (Bottom-up)
     * Time Complexity: O(amount * n) where n is number of payment options
     * Space Complexity: O(amount)
     * 
     * Banking Application: Optimal loan repayment strategy calculation
     */
    public int minPaymentsToRepayLoan(int[] paymentAmounts, int loanAmount) {
        if (loanAmount == 0)
            return 0;
        if (paymentAmounts == null || paymentAmounts.length == 0)
            return -1;

        // DP array where dp[i] represents minimum payments needed for amount i
        int[] dp = new int[loanAmount + 1];
        Arrays.fill(dp, loanAmount + 1); // Initialize with impossible value
        dp[0] = 0; // Base case: 0 payments needed for 0 amount

        System.out.println("🏦 Calculating optimal repayment strategy for loan amount: $" + loanAmount);
        System.out.println("Available payment options: " + Arrays.toString(paymentAmounts));

        for (int amount = 1; amount <= loanAmount; amount++) {
            for (int paymentAmount : paymentAmounts) {
                if (paymentAmount <= amount) {
                    dp[amount] = Math.min(dp[amount], dp[amount - paymentAmount] + 1);
                }
            }
        }

        int result = dp[loanAmount] > loanAmount ? -1 : dp[loanAmount];

        if (result != -1) {
            System.out.println("✅ Minimum payments required: " + result);
            reconstructPaymentPlan(paymentAmounts, loanAmount, dp);
        } else {
            System.out.println("❌ Loan amount cannot be repaid with given payment options");
        }

        return result;
    }

    /**
     * Reconstruct the actual payment plan from DP solution
     */
    private void reconstructPaymentPlan(int[] paymentAmounts, int loanAmount, int[] dp) {
        List<Integer> paymentPlan = new ArrayList<>();
        int remainingAmount = loanAmount;

        while (remainingAmount > 0) {
            for (int paymentAmount : paymentAmounts) {
                if (paymentAmount <= remainingAmount &&
                        dp[remainingAmount - paymentAmount] == dp[remainingAmount] - 1) {
                    paymentPlan.add(paymentAmount);
                    remainingAmount -= paymentAmount;
                    break;
                }
            }
        }

        System.out.println("📋 Optimal Payment Plan: " + paymentPlan);

        // Calculate payment frequency analysis
        Map<Integer, Integer> paymentFrequency = new HashMap<>();
        for (int payment : paymentPlan) {
            paymentFrequency.put(payment, paymentFrequency.getOrDefault(payment, 0) + 1);
        }

        System.out.println("📊 Payment Breakdown:");
        for (Map.Entry<Integer, Integer> entry : paymentFrequency.entrySet()) {
            System.out.println("   $" + entry.getKey() + " × " + entry.getValue() + " = $" +
                    (entry.getKey() * entry.getValue()));
        }
    }

    /**
     * Approach 2: Advanced - Minimum Interest Calculation
     * Optimizes for minimum total interest paid rather than minimum number of
     * payments
     */
    public double minInterestLoanRepayment(PaymentOption[] options, int loanAmount) {
        if (loanAmount == 0)
            return 0.0;
        if (options == null || options.length == 0)
            return -1.0;

        // DP array where dp[i] represents minimum interest for amount i
        double[] dp = new double[loanAmount + 1];
        Arrays.fill(dp, Double.MAX_VALUE);
        dp[0] = 0.0;

        // Track payment choices for reconstruction
        int[] paymentChoice = new int[loanAmount + 1];
        Arrays.fill(paymentChoice, -1);

        System.out.println("💰 Calculating minimum interest repayment strategy");
        System.out.println("Loan Amount: $" + loanAmount);

        for (int amount = 1; amount <= loanAmount; amount++) {
            for (int i = 0; i < options.length; i++) {
                PaymentOption option = options[i];
                if (option.amount <= amount) {
                    double interestCost = option.amount * option.interestRate;
                    double totalCost = dp[amount - option.amount] + interestCost;

                    if (totalCost < dp[amount]) {
                        dp[amount] = totalCost;
                        paymentChoice[amount] = i;
                    }
                }
            }
        }

        double result = dp[loanAmount] == Double.MAX_VALUE ? -1.0 : dp[loanAmount];

        if (result != -1.0) {
            System.out.println("✅ Minimum total interest: $" + String.format("%.2f", result));
            reconstructInterestOptimalPlan(options, loanAmount, paymentChoice);
        } else {
            System.out.println("❌ Loan cannot be repaid with given options");
        }

        return result;
    }

    /**
     * Reconstruct payment plan optimized for minimum interest
     */
    private void reconstructInterestOptimalPlan(PaymentOption[] options, int loanAmount, int[] paymentChoice) {
        List<PaymentOption> plan = new ArrayList<>();
        int remainingAmount = loanAmount;
        double totalInterest = 0.0;

        while (remainingAmount > 0 && paymentChoice[remainingAmount] != -1) {
            PaymentOption chosen = options[paymentChoice[remainingAmount]];
            plan.add(chosen);
            totalInterest += chosen.amount * chosen.interestRate;
            remainingAmount -= chosen.amount;
        }

        System.out.println("📋 Interest-Optimal Payment Plan:");
        Map<String, Integer> planSummary = new HashMap<>();
        for (PaymentOption payment : plan) {
            String key = payment.toString();
            planSummary.put(key, planSummary.getOrDefault(key, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : planSummary.entrySet()) {
            System.out.println("   " + entry.getKey() + " × " + entry.getValue());
        }

        System.out.println("💵 Total Interest Paid: $" + String.format("%.2f", totalInterest));
    }

    /**
     * Banking-specific: Loan restructuring with payment constraints
     */
    public boolean canRestructureLoan(int[] originalEMIs, int newLoanAmount, int maxPayments) {
        int minPayments = minPaymentsToRepayLoan(originalEMIs, newLoanAmount);

        if (minPayments == -1) {
            System.out.println("❌ Loan restructuring not possible with current EMI options");
            return false;
        }

        if (minPayments <= maxPayments) {
            System.out.println("✅ Loan restructuring possible!");
            System.out.println("   Required payments: " + minPayments);
            System.out.println("   Maximum allowed: " + maxPayments);
            System.out.println("   Buffer: " + (maxPayments - minPayments) + " payments");
            return true;
        } else {
            System.out.println("❌ Loan restructuring requires too many payments");
            System.out.println("   Required: " + minPayments + ", Allowed: " + maxPayments);
            return false;
        }
    }

    /**
     * Advanced: Calculate EMI for given loan parameters
     */
    public double calculateEMI(double principal, double annualRate, int tenureMonths) {
        if (annualRate == 0) {
            return principal / tenureMonths;
        }

        double monthlyRate = annualRate / 12 / 100;
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) /
                (Math.pow(1 + monthlyRate, tenureMonths) - 1);

        System.out.println("📊 EMI Calculation:");
        System.out.println("   Principal: $" + String.format("%.2f", principal));
        System.out.println("   Annual Rate: " + annualRate + "%");
        System.out.println("   Tenure: " + tenureMonths + " months");
        System.out.println("   EMI: $" + String.format("%.2f", emi));

        return emi;
    }

    /**
     * LeetCode Integration: Test against actual LeetCode platform
     */
    public void testWithLeetCodeAPI() {
        System.out.println("🏦 Testing Kotak Bank Loan Optimizer");
        System.out.println("=".repeat(50));

        // Test cases for loan repayment scenarios
        Object[][] testCases = {
                { new int[] { 1000, 2000, 5000 }, 11000, "Standard EMI options" },
                { new int[] { 500, 1000, 1500 }, 3000, "Small loan repayment" },
                { new int[] { 3000, 5000 }, 9999, "Impossible repayment" },
                { new int[] { 1 }, 100000, "Micro-payment scenario" },
                { new int[] { 2500, 5000, 10000 }, 15000, "Flexible payment options" }
        };

        for (int i = 0; i < testCases.length; i++) {
            int[] payments = (int[]) testCases[i][0];
            int loanAmount = (int) testCases[i][1];
            String description = (String) testCases[i][2];

            System.out.println("\nTest Case " + (i + 1) + ": " + description);
            System.out.println("Payment options: " + Arrays.toString(payments));
            System.out.println("Loan amount: $" + loanAmount);
            System.out.println("-".repeat(30));

            minPaymentsToRepayLoan(payments, loanAmount);
            System.out.println();
        }

        // Test interest optimization
        System.out.println("\n💰 INTEREST OPTIMIZATION TEST:");
        PaymentOption[] interestOptions = {
                new PaymentOption(1000, "Regular EMI", 0.12), // 12% annual
                new PaymentOption(2500, "Bulk Payment", 0.08), // 8% annual
                new PaymentOption(5000, "Large Payment", 0.05) // 5% annual
        };

        minInterestLoanRepayment(interestOptions, 12000);

        // Test loan restructuring
        System.out.println("\n🔄 LOAN RESTRUCTURING TEST:");
        int[] currentEMIs = { 2000, 3000, 5000 };
        canRestructureLoan(currentEMIs, 25000, 8);

        // Test EMI calculation
        System.out.println("\n📊 EMI CALCULATION TEST:");
        calculateEMI(500000, 8.5, 240); // 5L loan, 8.5% rate, 20 years
    }

    /**
     * Banking domain analysis
     */
    public void analyzeLoanOptimization() {
        System.out.println("\n🏦 LOAN OPTIMIZATION ANALYSIS");
        System.out.println("=".repeat(50));

        System.out.println("1. OPTIMIZATION CRITERIA:");
        System.out.println("   - Minimize number of payments (cash flow)");
        System.out.println("   - Minimize total interest (cost optimization)");
        System.out.println("   - Maximize payment flexibility");

        System.out.println("\n2. BANKING CONSTRAINTS:");
        System.out.println("   - Regulatory capital requirements");
        System.out.println("   - Risk assessment and credit scoring");
        System.out.println("   - Liquidity management");

        System.out.println("\n3. ALGORITHM COMPLEXITY:");
        System.out.println("   - DP Time: O(amount × payment_options)");
        System.out.println("   - Space: O(amount) - can be optimized further");
        System.out.println("   - Real-world: Handle up to ₹10 crore loans");

        System.out.println("\n4. PRODUCTION CONSIDERATIONS:");
        System.out.println("   - Batch processing for loan portfolio analysis");
        System.out.println("   - Real-time API for customer loan calculators");
        System.out.println("   - Integration with credit bureau data");

        System.out.println("\n5. RISK MANAGEMENT:");
        System.out.println("   - Default probability calculation");
        System.out.println("   - Stress testing for market conditions");
        System.out.println("   - Regulatory compliance (Basel III)");
    }

    public static void main(String[] args) {
        LoanRepaymentOptimizer optimizer = new LoanRepaymentOptimizer();

        System.out.println("🏦 KOTAK BANK SDE3 INTERVIEW QUESTION");
        System.out.println("📋 Problem: Loan Repayment Optimization");
        System.out.println("🔗 LeetCode Equivalent: Coin Change (#322) + Extensions");
        System.out.println();

        optimizer.testWithLeetCodeAPI();
        optimizer.analyzeLoanOptimization();

        System.out.println("\n💡 INTERVIEW TIPS:");
        System.out.println("1. Start with basic DP solution, then optimize");
        System.out.println("2. Discuss both time and space complexity trade-offs");
        System.out.println("3. Consider real-world banking constraints");
        System.out.println("4. Mention regulatory and risk management aspects");
        System.out.println("5. Think about scalability for large loan portfolios");
        System.out.println("6. Consider edge cases: negative payments, interest rate changes");
    }
}
